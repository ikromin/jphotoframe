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

import net.aksingh.owmjapis.DailyForecast;
import net.aksingh.owmjapis.OpenWeatherMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * Created by ikromin on 30/08/2015.
 */
public class Controller implements KeyListener, MouseListener {

    private static final int FIRST_TICK_TIMEOUT = 2000;

    GraphicsDevice device;
    View view;
    ImageDirectory imageDirectory;
    Thread photoChangeThread;
    Thread timeChangeThread;
    Thread weatherChangeThread;
    boolean stopping = false;
    boolean firstTick = true;
    boolean fastTick = false;
    ConfigOptions config;

    public Controller(ConfigOptions config, View view) throws IOException {
        this.config = config;
        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[config.getGfxDeviceNum()];
        imageDirectory = new ImageDirectory(config);

        this.view = view;
        view.addKeyListener(this);
        view.addMouseListener(this);

        photoChangeThread = new Thread() {
            @Override
            public void run() {
                while (!stopping) {
                    imageDirectory.waitIfPaused();
                    updatePhoto();
                }
            }
        };

        timeChangeThread = new Thread() {
            @Override
            public void run() {
                while (!stopping) {
                    imageDirectory.waitIfPaused();
                    updateTime();
                }
            }
        };

        weatherChangeThread = new Thread() {
            @Override
            public void run() {
                while (!stopping) {
                    imageDirectory.waitIfPaused();
                    updateWeather();
                }
            }
        };

    }

    public void start() {
        System.out.println("Starting photo frame");

        try {
            // get the initial list of files to display
            imageDirectory.sync();

            if (config.isFullScreenWindow()) {
                device.setFullScreenWindow(view);
            }
            else {
                view.setExtendedState(view.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            }

            // stupid workaround for OS X losing focus
            view.setVisible(false);
            view.setVisible(true);

            // start all the support threads
            imageDirectory.startWatching();
            photoChangeThread.start();
            timeChangeThread.start();
            weatherChangeThread.start();

            view.setReady(true);
        }
        catch (Exception e) {
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
        weatherChangeThread.interrupt();

        imageDirectory.stopWatching();
        device.setFullScreenWindow(null);
        view.dispose();
    }

    private void updatePhoto() {
        try {
            int sleepTime = config.getImageTimeout();
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
                view.displayImage(f);
                BufferedImage img = view.getCurrentImage();
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

    private void updateWeather() {
        // get the forecast if configured
        if (config.isShowWeather()) {
            OpenWeatherMap.Units units = (config.getWeatherUnits().equals(ConfigDefaults.DEFAULT_WEATHER_UNITS) == true)
                    ?  OpenWeatherMap.Units.METRIC
                    : OpenWeatherMap.Units.IMPERIAL;
            OpenWeatherMap owm = new OpenWeatherMap(units, config.getWeatherApiKey());

            try {
                System.out.println("Getting weather data");

                DailyForecast forecast = owm.dailyForecastByCityName(config.getWeatherCity(), (byte) config.getWeatherForecastDays());

                if (forecast.hasCityInstance() && forecast.hasForecastCount()) {
                    view.setWeather(new Weather(forecast));
                    view.repaint();
                }
            }
            catch (Exception e) {
                view.setWeather(Weather.getNoConnectionDummyForecast());
                view.repaint();
                System.out.println("Could not fetch weather forecast, error: " + e.getMessage());
            }

            try {
                Thread.sleep(config.getWeatherUpdateTime());
            }
            catch (InterruptedException e) {
                // ignore interruption exception, just exit the method
            }
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

    @Override
    public void mouseClicked(MouseEvent e) {
        // Exit on mouse click
        stop();

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Exit on mouse press
        stop();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // not implemented
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // not implemented
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // not implemented
    }
}
