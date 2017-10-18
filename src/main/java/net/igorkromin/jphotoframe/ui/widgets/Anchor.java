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
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

/**
 * Bounds anchoring widget. Sets the draw translation to one of the bounding sides along the X and Y axes with respect
 * to the entire drawing area as specified by the Rectangle object.
 *
 * Provides the following properties:
 *  - anchor : two-integer array specifying which side to anchor to along an axis. 0 is the min-side, 1 is the max-side.
 *  - children : list of child widgets
 */
public class Anchor extends Widget {

    private static final String KEY_ANCHOR = "anchor";
    private static final String KEY_CHILDREN = "children";

    private int anchorX = 0;
    private int anchorY = 0;
    private List<Transformable> children = new ArrayList<>();
    private AffineTransform transform;

    public Anchor(JSONObject json, ModelData data) {
        super(json);

        // - anchor
        if (json.has(KEY_ANCHOR)) {
            JSONArray anchors = json.getJSONArray(KEY_ANCHOR);
            if (anchors.length() == 2) {
                anchorX = anchors.getInt(0);
                anchorY = anchors.getInt(1);

                if (anchorX < 0 || anchorY < 0 || anchorX > 1 || anchorY > 1) {
                    throw new RuntimeException("Anchor values can only be 0 or 1");
                }
            }
        }

        // - children
        if (json.has(KEY_CHILDREN)) {
            JSONArray arr = json.getJSONArray(KEY_CHILDREN);

            Log.layout("Adding child widgets to anchor");
            for (int i = 0; i < arr.length(); i++) {
                Transformable t = (Transformable) Factory.makeWidget(arr.getJSONObject(i), data);

                if (t != null) {
                    children.add(t);
                }
            }
            Log.layout("Added " + children.size() + " child widgets");
        }

        transform = new AffineTransform();
    }

    @Override
    public void draw(Graphics2D graphics, Rectangle bounds) {

        int x = (anchorX == 0) ? 0 : bounds.width;
        int y = (anchorY == 0) ? 0 : bounds.height;

        transform.setToTranslation(x, y);

        graphics.setTransform(transform);

        for (Transformable t : children) {
            t.preDraw(graphics);
            t.draw(graphics, bounds);
            t.postDraw(graphics);
        }
    }

}
