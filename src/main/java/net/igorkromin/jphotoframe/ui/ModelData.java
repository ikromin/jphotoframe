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

import net.igorkromin.jphotoframe.weather.Weather;

import java.awt.image.BufferedImage;

/**
 * Holds the runtime data for the app 'model'. The View class uses this data to update what's displayed on screen.
 */
public class ModelData {

    private String date;
    private String time;
    private Weather weather;
    private BufferedImage currentImage;

    private boolean changed = false;

    public boolean hasChanged() {
        return changed;
    }

    public void resetChange() {
        changed = false;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
        changed = true;
    }

    public void setCurrentImage(BufferedImage image) {
        currentImage = image;
        changed = true;
    }

    public void setDateTime(String date, String time) {
        this.date = date;
        this.time = time;
        changed = true;
    }

    public BufferedImage getCurrentImage() {
        return currentImage;
    }

    public String getDateString() {
        return date;
    }

    public String getTimeString() {
        return time;
    }

    public Weather getWeather() {
        return weather;
    }
}
