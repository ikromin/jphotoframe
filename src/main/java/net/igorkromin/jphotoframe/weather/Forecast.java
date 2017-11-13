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

package net.igorkromin.jphotoframe.weather;

import java.util.Date;

/**
 * Data class to record forecast information for a single day.
 */
public class Forecast {

    private Date date;
    private String day;
    private int low = Integer.MAX_VALUE;
    private int high = Integer.MIN_VALUE;
    private int code = WeatherConditionCodes.NOT_AVAILABLE.code;

    public Date getDate() {
        return date;
    }

    public String getDay() {
        return day;
    }

    public int getLow() {
        return low;
    }

    public int getHigh() {
        return high;
    }

    public int getCode() {
        return code;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public void setCode(WeatherConditionCodes code) {
        this.code = code.code;
    }

    public void setConditions(int code) {
        this.code = code;
    }
}
