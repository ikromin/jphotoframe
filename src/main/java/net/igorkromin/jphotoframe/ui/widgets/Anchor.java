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
import java.util.ArrayList;
import java.util.List;

import static net.igorkromin.jphotoframe.ui.widgets.Factory.*;

/**
 * Bounds anchoring widget. Sets the draw translation to one of the bounding sides along the X and Y axes with respect
 * to the entire drawing area as specified by the Rectangle object.
 *
 * Provides the following properties:
 *  - anchor : two-integer array specifying which side to anchor to along an axis. 0 is the min-side, 1 is the max-side.
 *  - children : list of child widgets
 */
public class Anchor extends Widget {

    private int anchorX = 0;
    private int anchorY = 0;
    private List<Transformable> children = new ArrayList<>();

    public Anchor(JSONObject json, ModelData data, Rectangle drawAreaBounds) {
        super(json, drawAreaBounds);

        // - anchor
        if (json.has(KEY_ANCHOR)) {
            JSONArray anchors = json.getJSONArray(KEY_ANCHOR);
            if (anchors.length() == 2) {
                int x = anchors.getInt(0);
                int y = anchors.getInt(1);

                if (x < 0 || y < 0 || x > 1 || y > 1) {
                    throw new RuntimeException("Anchor values can only be 0 or 1");
                }

                anchorX = (x == 0) ? 0 : drawAreaBounds.width;
                anchorY = (y == 0) ? 0 : drawAreaBounds.height;
            }
        }

        // - children
        if (json.has(KEY_CHILDREN)) {
            JSONArray arr = json.getJSONArray(KEY_CHILDREN);

            Log.layout("Adding child widgets to anchor");
            for (int i = 0; i < arr.length(); i++) {
                Transformable t = (Transformable) Factory.makeWidget(arr.getJSONObject(i), data, drawAreaBounds);

                if (t != null) {
                    children.add(t);
                }
            }
            Log.layout("Added " + children.size() + " child widgets");
        }
    }

    @Override
    public void draw(Graphics2D graphics) {

        // copy the Graphics2D object to avoid incompatible state changes
        Graphics2D graphics2 = (Graphics2D) graphics.create();

        graphics2.translate(anchorX, anchorY);

        for (Transformable t : children) {
            t.draw(graphics2);
        }

        graphics2.dispose();
    }

}
