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

import net.igorkromin.jphotoframe.Log;
import net.igorkromin.jphotoframe.ui.ModelData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Widwet Object Factory. Creates single Widget type objects or an entire list of objects from a layout file.
 */
public class Factory {

    public static final String KEY_WIDGETS = "widgets";
    public static final String KEY_TYPE = "type";
    public static final String KEY_ENABLED = "enabled";
    public static final String KEY_TRANSFORM = "transform";
    public static final String KEY_ORIGIN = "origin";
    public static final String KEY_OFFSET = "offset";
    public static final String KEY_ROTATE = "rotate";
    public static final String KEY_SHOW_BOUNDS = "showBounds";
    public static final String KEY_TEXT = "text";
    public static final String KEY_DATA = "data";
    public static final String KEY_FORMAT = "format";
    public static final String KEY_FONT = "font";
    public static final String KEY_SIZE = "size";
    public static final String KEY_COLOUR = "colour";
    public static final String KEY_OUTLINE_COLOUR = "outlineColour";
    public static final String KEY_OUTLINE_WIDTH = "outlineWidth";
    public static final String KEY_ITEMS = "items";
    public static final String KEY_ITEM_GAP = "gap";
    public static final String KEY_GAP_POSITION = "gapPosition";
    public static final String KEY_ORIENTATION = "orientation";
    public static final String KEY_REVERSE = "reverse";

    private static final String TYPE_ANCHOR = "anchor";
    private static final String TYPE_TEXT = "text";
    private static final String TYPE_WEATHER = "weather";

    public static final int DEFAULT_COORD_COMPONENT = 0;
    public static final double DEFAULT_ROTATION = 0.0;
    public static final boolean DEFAULT_SHOW_BOUNDS = false;
    public static final int DEFAULT_OUTLINE_WIDTH = 1;
    public static final int DEFAULT_FONT_SIZE = 10;
    public static final String DEFAULT_FONT_NAME = "Verdana";
    public static final String DEFAULT_FORMAT = "%s";
    public static final Color DEFAULT_COLOUR = Color.white;
    public static final Color DEFAULT_OUTLINE_COLOUR = Color.black;
    public static final int DEFAULT_GAP_SIZE = 150;
    public static final int DEFAULT_GAP_SCALAR = 0;
    public static final int DEFAULT_ORIENTATION = 1;
    public static final boolean DEFAULT_REVERSE = false;


    /**
     * Creates a list of Widget type objects from a layout file.
     * @param layoutFileName
     * @param bounds
     * @return
     */
    public static List<Widget> makeWidgetsFromLayout(String layoutFileName, ModelData data, Rectangle bounds) {

        List<Widget> widgets = new ArrayList<>();

        if (layoutFileName == null) {
            Log.warning("No layout configuration specified");
        }
        else {
            Log.info("Loading layout configuration from file: " + layoutFileName);

            try {
                byte[] bytes = Files.readAllBytes(Paths.get(layoutFileName));
                JSONObject layout = new JSONObject(new String(bytes));

                if (layout.has("widgets")) {
                    JSONArray arr = layout.getJSONArray(KEY_WIDGETS);

                    for (int i = 0; i < arr.length(); i++) {
                        Widget widget = makeWidget(arr.getJSONObject(i), data, bounds);
                        if (widget != null) {
                            widgets.add(widget);
                        }
                    }
                }
                else {
                    Log.warning("Widgets not found layout file");
                }
            }
            catch (IOException e) {
                Log.error("Could not load layout configuration from file: " + layoutFileName, e);
            }
        }

        return widgets;
    }

    /**
     * Creates a single Widget type object from a JSON object. If the widget is not enabled, a null is returned.
     * Unrecognised widget types will return null also.
     * @param object
     * @param bounds
     * @return
     */
    public static Widget makeWidget(JSONObject object, ModelData data, Rectangle bounds) {
        String type = object.getString(KEY_TYPE);
        Widget widget = null;

        Log.layout("Creating widget of type: " + type);

        if (TYPE_ANCHOR.equals(type)) {
            widget = new Anchor(object, data, bounds);
        }
        else if (TYPE_TEXT.equals(type)) {
            widget = new Text(object, data, bounds);
        }
        else if (TYPE_WEATHER.equals(type)) {
            widget = new WeatherForecast(object, data, bounds);
        }
        else {
            Log.warning("Unrecognised widget type: " + type);
        }

        if (!widget.isEnabled()) {
            Log.layout("Widget is not enabled, will skip");
            widget = null;
        }

        return widget;
    }

}
