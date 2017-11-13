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

import net.igorkromin.jphotoframe.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Creates all of the supporting data update threads and manages the start, stop operations as well as the view
 * update cycle.
 */
public class Controller extends EventController {

    List<DataUpdateThread> dataThreads;
    boolean stopping = false;

    ConfigOptions config;
    ModelData data;

    public Controller(ConfigOptions config, View view, ModelData data) throws IOException {
        super(view);

        this.config = config;
        this.data = data;

        dataThreads = new ArrayList<>(3);

        dataThreads.add(new TimeUpdateThread(this, config, data, 1000));
        dataThreads.add(new PhotoUpdateThread(this, config, data, config.getImageTimeout(), view.getImageBuffer()));

        if (config.isShowWeather()) {
            dataThreads.add(new WeatherUpdateThread(this, config, data, config.getWeatherUpdateTime()));
        }
        else {
            Log.info("Weather fetch is disabled");
        }

        view.loadWidgets();
    }

    public void start() {
        Log.info("Starting photo frame");

        try {
            // start all the support threads
            for (DataUpdateThread t : dataThreads) {
                t.start();
            }
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

        Log.verbose("Stopping threads");
        for (DataUpdateThread t : dataThreads) {
            t.interrupt();
        }

        view.dispose();

        Log.verbose("Stopped");
    }

    public boolean isStopping() {
        return stopping;
    }

    /**
     * Updates the view if the model data has changed since the last time the view was updated.
     */
    public synchronized void requestUpdate() {
        if (data.hasChanged()) {
            view.repaint();
        }
        else {
            Log.verbose("Data not changed");
        }
    }
}
