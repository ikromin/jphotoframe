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

    public static final String DATA_SRC_TIME = "$time";
    public static final String DATA_SRC_DATE = "$date";
    public static final String DATA_SRC_WEATHER_GEO = "$weather.geo";

    private static final String KEY_WIDGETS = "widgets";

    private static final String TYPE_ANCHOR = "anchor";
    private static final String TYPE_TEXT = "text";

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
        String type = object.getString(Widget.KEY_TYPE);
        Widget widget = null;

        Log.layout("Creating widget of type: " + type);

        if (TYPE_ANCHOR.equals(type)) {
            widget = new Anchor(object, data, bounds);
        }
        else if (TYPE_TEXT.equals(type)) {
            widget = new Text(object, data, bounds);
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
