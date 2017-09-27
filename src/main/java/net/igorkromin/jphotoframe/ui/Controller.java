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

package net.igorkromin.jphotoframe.ui;

import net.aksingh.owmjapis.DailyForecast;
import net.aksingh.owmjapis.OpenWeatherMap;
import net.igorkromin.jphotoframe.*;
import net.igorkromin.jphotoframe.weather.Weather;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * Created by ikromin on 30/08/2015.
 */
public class Controller extends EventController {

    private static final int FIRST_TICK_TIMEOUT = 2000;

    GraphicsDevice device;
    Thread photoChangeThread;
    Thread timeChangeThread;
    Thread weatherChangeThread;
    boolean stopping = false;
    boolean firstTick = true;
    boolean fastTick = false;

    ConfigOptions config;
    ImageDirectory imageDirectory;
    ModelData data;

    public Controller(ConfigOptions config, View view, ModelData data) throws IOException {
        super(view);

        this.config = config;
        this.data = data;

        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[config.getGfxDeviceNum()];
        imageDirectory = new ImageDirectory(config.getImageDirectory(), config.getCacheDirectory(), this);

        photoChangeThread = new Thread() {
            @Override
            public void run() {
                while (!stopping) {
                    waitIfPaused();
                    updatePhoto();
                }
            }
        };

        timeChangeThread = new TimeUpdateThread(this, config, data, 1000);

        weatherChangeThread = new Thread() {
            @Override
            public void run() {
                while (!stopping) {
                    waitIfPaused();
                    updateWeather();
                }
            }
        };

    }

    public void start() {
        Log.info("Starting photo frame");

        try {
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
            Log.error("Error starting: " + e.getMessage(), e);
            stop();
        }
    }

    public synchronized void stop() {
        if (stopping) {
            return;
        }

        Log.info("Stopping photo frame");
        stopping = true;

        view.setReady(false); // TODO: can just use isStopping() ?

        Log.verbose("Stopping threads");
        photoChangeThread.interrupt();
        timeChangeThread.interrupt();
        weatherChangeThread.interrupt();

        Log.verbose("Stopping directory watcher");
        imageDirectory.stopWatching();

        device.setFullScreenWindow(null);
        view.dispose();

        Log.verbose("Stopped");
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
            File c = null; // TODO: temporarily disabled imageDirectory.getCachedImageFile(f);

            // display cached file if it exists
            if (imageDirectory.fileExists(c)) {
                Log.verbose("Using cached image: " + c.getAbsolutePath());
                view.displayImage(c);
            }
            // fall back to full res image + create a cached copy
            else {
                if (f != null) {
                    view.displayImage(f);
                    BufferedImage img = data.getCurrentImage();
                    if (img != null) {
                        // TODO: temporarily disabled ImageUtil.writeImage(c, img); // TODO: This should be done in a separate thread
                    } else {
                        fastTick = true;
                    }
                }
            }
        }
        catch (InterruptedException e) {
            // ignore interruption exception, just exit the method
        }
    }

    private void updateWeather() {
        // get the forecast if configured
        if (config.isShowWeather()) {
            Log.verbose("Getting weather data");

            OpenWeatherMap.Units units = (config.getWeatherUnits().equals(ConfigDefaults.DEFAULT_WEATHER_UNITS) == true)
                    ?  OpenWeatherMap.Units.METRIC
                    : OpenWeatherMap.Units.IMPERIAL;
            OpenWeatherMap owm = new OpenWeatherMap(units, config.getWeatherApiKey());

            try {
                DailyForecast forecast = owm.dailyForecastByCityName(config.getWeatherCity(), (byte) config.getWeatherForecastDays());

                if (forecast.hasCityInstance() && forecast.hasForecastCount()) {
                    data.setWeather(new Weather(forecast));
                    view.repaint();
                }
            }
            catch (Exception e) {
                data.setWeather(Weather.getNoConnectionDummyForecast());
                view.repaint();
                Log.error("Could not fetch weather forecast, error: " + e.getMessage(), e);
            }
        }

        try {
            Thread.sleep(config.getWeatherUpdateTime());
        }
        catch (InterruptedException e) {
            // ignore interruption exception, just exit the method
        }
    }

    public boolean isStopping() {
        return stopping;
    }

    public void requestUpdate() {
        synchronized (data) {
            if (data.hasChanged()) {
                view.repaint();
            }
        }
    }
}
