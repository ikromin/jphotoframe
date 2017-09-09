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

package net.igorkromin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collections;
import java.util.Vector;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created by ikromin on 30/08/2015.
 */
public class ImageDirectory {

    WatchService dirWatcher;
    Path imageDirPath;
    File imageDirFile;
    File cacheDirFile;
    Vector<File> imageFiles;
    int imageIndex;
    Object lock = new Object();
    boolean foundPauseFile = false;

    public ImageDirectory(ConfigOptions config)
            throws IOException
    {
        String dir = config.getImageDirectory();

        cacheDirFile = new File(config.getCacheDirectory());
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

        imageFiles = new Vector<>();
    }

    public void startWatching()
            throws IOException
    {
        (new WatcherThread(dirWatcher)).start();
        imageDirPath.register(dirWatcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
    }

    public void stopWatching() {
        try {
            dirWatcher.close();
        }
        catch (IOException e) {
            // ignore the exception on close
        }
    }

    public void sync() {
        System.out.println("Synchronising directory contents");

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
                    System.out.println("Pausing");
                }
            }
        }

        Collections.shuffle(imageFiles);
        System.out.println("Found " + imageFiles.size() + " files");

        // notify all waiting threads that they can resume
        if (wasPaused && foundPauseFile == false) {
            System.out.println("Resuming");
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

    public File getCachedFile(File file) {
        File cachedFile = getCachedFileName(file);

        if (cachedFile.exists() && cachedFile.canRead()) {
            return cachedFile;
        }

        return null;
    }

    public void cacheImage(final File file, final BufferedImage bufferedImage)
    {
        System.out.println("Caching image: " + file.getAbsolutePath());
        File cachedFile = getCachedFileName(file);

        try {
            ImageIO.write(bufferedImage, "JPG", cachedFile);
        }
        catch (Exception e) {
            System.out.println("Failed to cache image: " + file.getAbsolutePath() + " cause: " + e.getMessage());
        }
    }

    private File getCachedFileName(File file) {
        Path newPath = FileSystems.getDefault().getPath(cacheDirFile.getAbsolutePath(), "X" + file.hashCode());
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
            System.out.println("Started watching directory");

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
                System.out.println("Stopped watching directory");
            }
        }
    }

}
