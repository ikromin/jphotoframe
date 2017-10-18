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

package net.igorkromin.jphotoframe.ui.widgets;

import org.json.JSONObject;

import java.awt.*;

/**
 * Base class for all widgets.
 * Provides the following properties:
 *  - enabled : whether this widget will be added to the draw list or not (default to true)
 */
public abstract class Widget {

    public static final String KEY_TYPE = "type";

    private static final String KEY_ENABLED = "enabled";

    private boolean isEnabled = true;

    public Widget(JSONObject json) {
        // - enabled
        if (json.has(KEY_ENABLED)) {
            isEnabled = Boolean.parseBoolean(json.getString(KEY_ENABLED));
        }
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public abstract void draw(Graphics2D graphics, Rectangle bounds);

}
