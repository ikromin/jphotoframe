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
import static net.igorkromin.jphotoframe.ConfigOptions.DEFAULT_CACHE_DIRECTORY;
import static net.igorkromin.jphotoframe.ConfigOptions.DEFAULT_IMG_DIRECTORY;

/**
 * Monitor for the image directory. Requires a listener to be provided to handle pause/resume events.
 */
public class ImageDirectory {

    private final static String PAUSE_FILE = "pause.txt";

    PauseListener pauseListener;
    WatchService dirWatcher;
    Path imageDirPath;
    File imageDirFile;
    File cacheDirFile;
    Vector<File> imageFiles = new Vector<>();
    int imageIndex;
    boolean isWatching = false;
    boolean paused = false;

    public ImageDirectory(String dir, String cacheDir, PauseListener pauseListener)
            throws IOException
    {
        if (pauseListener == null) {
            throw new RuntimeException("Can't have a null pause listener");
        }
        this.pauseListener = pauseListener;

        if (dir == null || cacheDir == null || DEFAULT_IMG_DIRECTORY.equals(dir) || DEFAULT_CACHE_DIRECTORY.equals(cacheDir)) {
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

        // get the initial list of files in the directory
        sync();
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

    /**
     * Gets the file object for the next image to be shown.
     * @return
     */
    public File nextFile() {
        if (imageFiles.size() > 0) {
            File f = imageFiles.get(imageIndex);

            // advance and cycle the index if necessary
            imageIndex++;
            if (imageIndex == imageFiles.size()) {
                imageIndex = 0;

                // shuffle on cycle (maybe this should be configurable?)
                Collections.shuffle(imageFiles);
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

    private void sync() {
        if (!isWatching) {
            return;
        }

        Log.info("Synchronising directory contents");

        imageFiles.clear();
        imageIndex = 0;

        boolean foundPauseFile = false;
        File[] files = imageDirFile.listFiles();

        for (File f : files) {
            if (f.isFile() && f.canRead() && !f.isHidden()) {
                imageFiles.add(f);

                // check if we should pause processing and fire the pause event
                if (f.getName().equals(PAUSE_FILE)) {
                    Log.verbose("Found pause file on sync");
                    if (!paused) {
                        paused = true;
                        pauseListener.pauseEvent();
                    }
                    foundPauseFile = true;
                }
            }
        }

        Collections.shuffle(imageFiles);
        Log.info("Found " + imageFiles.size() + " files");

        // fire the resume event if the pause file is removed
        if (paused && foundPauseFile == false) {
            paused = false;
            pauseListener.resumeEvent();
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
