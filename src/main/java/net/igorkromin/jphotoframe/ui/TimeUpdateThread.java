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

import net.igorkromin.jphotoframe.ConfigOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUpdateThread extends DataUpdateThread {

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    public TimeUpdateThread(Controller controller, ConfigOptions config, ModelData data, int sleepTime) {
        super(controller, config, data, sleepTime);

        dateFormat = new SimpleDateFormat(config.getDateFormat());
        timeFormat = new SimpleDateFormat(config.getTimeFormat());
    }

    @Override
    public void doUpdate() {
        Date now = new Date();
        ModelData data = getData();

        String date = dateFormat.format(now);
        String time = timeFormat.format(now);

        // only update the date/time if it's actually changed
        if (!date.equals(data.getDateString()) || !time.equals(data.getTimeString())) {
            data.setDateTime(date, time);
            getController().requestUpdate();
        }
    }

}
