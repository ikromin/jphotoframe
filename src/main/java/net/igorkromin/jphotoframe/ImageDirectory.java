/**
 * JPhotoFrame - a simple Java application for displaying a collection of photos in a full-screen slideshow.
 * Copyright (C) 2015  Igor Kromin
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * You can find this and my other open source projects here - http://github.com/ikromin
 */

package net.igorkromin.jphotoframe;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collections;
import java.util.Vector;

import static java.nio.file.StandardWatchEventKinds.*;
import static net.igorkromin.jphotoframe.ConfigDefaults.DEFAULT_CACHE_DIRECTORY;
import static net.igorkromin.jphotoframe.ConfigDefaults.DEFAULT_IMG_DIRECTORY;

/**
 * Monitor for the image directory. Also provides various utility methods around cached file names.
 */
public class ImageDirectory {

    WatchService dirWatcher;
    Path imageDirPath;
    File imageDirFile;
    File cacheDirFile;
    Vector<File> imageFiles = new Vector<>();
    int imageIndex;
    Object lock = new Object();
    boolean foundPauseFile = false;
    boolean isWatching = false;

    public ImageDirectory(ConfigOptions config)
            throws IOException
    {
        String dir = config.getImageDirectory();
        String cacheDir = config.getCacheDirectory();

        if (DEFAULT_IMG_DIRECTORY.equals(dir) || DEFAULT_CACHE_DIRECTORY.equals(cacheDir)) {
            Log.warning("No valid image/cache directories specified, will not watch directories");
            return;
        }

        cacheDirFile = new File(cacheDir);
        if (!(cacheDirFile.isDirectory() && cacheDirFile.canRead())) {
            throw new RuntimeException("Cache directory not available: " + cacheDirFile.getAbsolutePath());
        }

        // set up a watcher for the images directory
        imageDirPath = FileSystems.getDefault().getPath(dir);
        imageDirFile = imageDirPath.toFile();
        if (imageDirFile.exists() && imageDirFile.isDirectory() && imageDirFile.canRead()) {
            dirWatcher = FileSystems.getDefault().newWatchService();
        }
        else {
            throw new RuntimeException("Image directory not available: " + imageDirFile.getAbsolutePath());
        }

        isWatching = true;
    }

    public void startWatching()
            throws IOException
    {
        if (!isWatching) {
            return;
        }

        (new WatcherThread(dirWatcher)).start();
        imageDirPath.register(dirWatcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
    }

    public void stopWatching() {
        if (!isWatching) {
            return;
        }

        try {
            dirWatcher.close();
        }
        catch (IOException e) {
            // ignore the exception on close
        }
    }

    public void sync() {
        if (!isWatching) {
            return;
        }

        Log.info("Synchronising directory contents");

        imageFiles.clear();
        imageIndex = 0;

        boolean wasPaused = (foundPauseFile == true);

        foundPauseFile = false;
        File[] files = imageDirFile.listFiles();
        for (File f : files) {
            if (f.isFile() && f.canRead() && !f.isHidden()) {
                imageFiles.add(f);

                // check if we should pause processing and wait
                if (f.getName().equals("pause.txt")) {
                    foundPauseFile = true;
                    Log.info("Pausing");
                }
            }
        }

        Collections.shuffle(imageFiles);
        Log.info("Found " + imageFiles.size() + " files");

        // notify all waiting threads that they can resume
        if (wasPaused && foundPauseFile == false) {
            Log.info("Resuming");
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }

    public File nextFile() {
        if (imageFiles.size() > 0) {
            File f = imageFiles.get(imageIndex);

            // advance and cycle the index if necessary
            imageIndex++;
            if (imageIndex == imageFiles.size()) {
                imageIndex = 0;
            }

            return f;
        }
        else {
            return null;
        }
    }

    /**
     * Checks if a file exists and is readable.
     * @param imageFile
     * @return
     */
    public boolean fileExists(File imageFile) {
        if (imageFile == null) {
            return false;
        }

        if (imageFile != null && imageFile.exists() && imageFile.canRead()) {
            return true;
        }
        return false;
    }

    /**
     * Returns the cached image file for a (non-cached) image file. No checks are done on whether the passed in
     * file exists.
     * @param imageFile
     * @return
     */
    public File getCachedImageFile(File imageFile) {
        if (imageFile == null || cacheDirFile == null) {
            return null;
        }

        Path newPath = FileSystems.getDefault().getPath(cacheDirFile.getAbsolutePath(), "X" + imageFile.hashCode());
        File newFile = newPath.toFile();

        return newFile;
    }

    public  void waitIfPaused() {
        if (foundPauseFile) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }

    private class WatcherThread extends Thread {
        WatchService dirWatcher;

        public WatcherThread(WatchService dirWatcher) {
            this.dirWatcher = dirWatcher;
        }

        @Override
        public void run() {
            Log.verbose("Started watching directory");

            try {
                for (;;) {
                    WatchKey key = dirWatcher.take();
                    key.pollEvents();

                    if (key.isValid()) {
                        sync();
                    }

                    key.reset();
                }
            }
            catch (Exception e) {
                Log.verbose("Stopped watching directory");
            }
        }
    }

}
