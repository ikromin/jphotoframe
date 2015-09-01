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

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * Created by ikromin on 30/08/2015.
 */
public class Controller implements KeyListener {

    private static final int FIRST_TICK_TIMEOUT = 1000;

    GraphicsDevice device;
    View view;
    ImageDirectory imageDirectory;
    Thread photoChangeThread;
    Thread timeChangeThread;
    boolean stopping = false;
    int timerInterval;
    boolean firstTick = true;
    boolean fastTick = false;

    public Controller(ConfigOptions config, View view) throws IOException {
        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[config.getGfxDeviceNum()];
        imageDirectory = new ImageDirectory(config);

        this.view = view;
        view.addKeyListener(this);

        timerInterval = config.getImageTimeout();
        photoChangeThread = new Thread() {
            @Override
            public void run() {
                while (!stopping) {
                    updatePhoto();
                }
            }
        };

        timeChangeThread = new Thread() {
            @Override
            public void run() {
                while (!stopping) {
                    while (!stopping) {
                        updateTime();
                    }
                }
            }
        };
    }

    public void start() {
        System.out.println("Starting photo frame");

        try {
            device.setFullScreenWindow(view);
            view.setReady(true);

            // stupid workaround for OS X losing focus
            view.setVisible(false);
            view.setVisible(true);

            imageDirectory.startWatching();
            imageDirectory.sync();

            photoChangeThread.start();
            timeChangeThread.start();
        }
        catch (IOException e) {
            System.out.println("Error starting: " + e.getMessage());
            stop();
        }
    }

    public synchronized void stop() {
        if (stopping) {
            return;
        }

        System.out.println("Stopping photo frame");
        stopping = true;

        view.setReady(false);

        photoChangeThread.interrupt();
        timeChangeThread.interrupt();

        imageDirectory.stopWatching();
        device.setFullScreenWindow(null);
        view.dispose();
    }

    private void updatePhoto() {
        try {
            int sleepTime = timerInterval;
            if (firstTick) {
                sleepTime = FIRST_TICK_TIMEOUT;
                firstTick = false;
            }
            if (fastTick) {
                sleepTime = 100;
                fastTick = false;
            }
            Thread.sleep(sleepTime);

            File f = imageDirectory.nextFile();
            File c = imageDirectory.getCachedFile(f);

            // display cached file if it exists
            if (c != null && c.exists() && c.canRead()) {
                System.out.println("Using cached image: " + c.getAbsolutePath());
                view.displayImage(c);
            }
            else {
                BufferedImage img = view.displayImage(f);
                if (img != null) {
                    imageDirectory.cacheImage(f, img);
                }
                else {
                    fastTick = true;
                }
            }
        }
        catch (InterruptedException e) {
            // ignore interruption exception, just exit the method
        }
    }

    private void updateTime() {
        try {
            Thread.sleep(1000);
            view.repaint();
        }
        catch (InterruptedException e) {
            // ignore interruption exception, just exit the method
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Exit if any key is pressed
        stop();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Exit if any key is pressed
        stop();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // not implemented
    }

}
