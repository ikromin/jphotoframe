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

import java.awt.*;
import java.util.HashMap;

/**
 * Caches fonts used by Widgets
 */
public class FontManager {

    private static final String WEATHER_ICONS_FONT_FILE = "/weathericons-regular-webfont.ttf";

    private static HashMap<String, Font> fonts = new HashMap<>();
    private static Font weatherFont = null;

    public static Font getFont(String name, int style, int size) {

        String key = name + style + size;
        Font font;

        if (fonts.containsKey(key)) {
            font = fonts.get(key);
        }
        else {
            font = new Font(name, style, size);
            fonts.put(key, font);
        }

        return font;
    }

    public static Font getWeatherFont(int size) {
        if (weatherFont != null) {
            return  weatherFont;
        }

        try {
            weatherFont = Font.createFont(Font.TRUETYPE_FONT,
                    FontManager.class.getResourceAsStream(WEATHER_ICONS_FONT_FILE))
                    .deriveFont((float) size);

            return weatherFont;
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
