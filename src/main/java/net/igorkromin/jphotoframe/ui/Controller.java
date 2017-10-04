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


/**
 * Created by ikromin on 30/08/2015.
 */
public class Controller extends EventController {

    PhotoUpdateThread photoChangeThread;
    DataUpdateThread timeChangeThread;
    DataUpdateThread weatherChangeThread;
    boolean stopping = false;

    ConfigOptions config;
    ModelData data;

    public Controller(ConfigOptions config, View view, ModelData data) throws IOException {
        super(view);

        this.config = config;
        this.data = data;

        timeChangeThread = new TimeUpdateThread(this, config, data, 1000);
        weatherChangeThread = new WeatherUpdateThread(this, config, data, config.getWeatherUpdateTime());
        photoChangeThread = new PhotoUpdateThread(this, config, data, config.getImageTimeout(), view.getImageBuffer());
    }

    public void start() {
        Log.info("Starting photo frame");

        try {
            // start all the support threads
            photoChangeThread.start();
            timeChangeThread.start();
            weatherChangeThread.start();
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
        photoChangeThread.interrupt();
        timeChangeThread.interrupt();
        weatherChangeThread.interrupt();

        view.dispose();

        Log.verbose("Stopped");
    }

    public boolean isStopping() {
        return stopping;
    }

    public synchronized void requestUpdate() {
        if (data.hasChanged()) {
            view.repaint();
        }
        else {
            Log.verbose("Data not changed");
        }
    }
}
