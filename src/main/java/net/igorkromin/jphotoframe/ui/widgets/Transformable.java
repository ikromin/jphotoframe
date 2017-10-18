package net.igorkromin.jphotoframe.ui.widgets;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Widget tha supports transformations. Used as a base for other widgets that can be transformed.
 *
 * Provides the following properties:
 *  - origin : two-integer array specifying the transformation origin, similar to Anchor 'anchor'
 *  - offset : two-integer array specifying the offset in pixels relative to the origin
 *  - showBounds : whether to show the drawing boundary box or not
 */
public abstract class Transformable extends Widget {

    public static final String KEY_TRANSFORM = "transform";

    private static final String KEY_ORIGIN = "origin";
    private static final String KEY_OFFSET = "offset";
    private static final String KEY_SHOW_BOUNDS = "showBounds";

    private int originX = 0;
    private int originY = 0;
    private int offsetX = 0;
    private int offsetY = 0;
    private boolean showBounds = false;
    private AffineTransform originalTx;
    private AffineTransform transform;
    private Rectangle drawBounds;

    public Transformable(JSONObject json) {
        super(json);

        // - origin
        if (json.has(KEY_ORIGIN)) {
            JSONArray origins = json.getJSONArray(KEY_ORIGIN);
            if (origins.length() == 2) {
                originX = origins.getInt(0);
                originY = origins.getInt(1);

                if (originX < 0 || originY < 0 || originX > 1 || originY > 1) {
                    throw new RuntimeException("Origin values can only be 0 or 1");
                }
            }
        }

        // - offsets
        if (json.has(KEY_OFFSET)) {
            JSONArray offsets = json.getJSONArray(KEY_OFFSET);
            if (offsets.length() == 2) {
                offsetX = offsets.getInt(0);
                offsetY = offsets.getInt(1);
            }
        }

        // - showBounds
        if (json.has(KEY_SHOW_BOUNDS)) {
            showBounds = Boolean.parseBoolean(json.getString(KEY_SHOW_BOUNDS));
        }

        transform = new AffineTransform();
    }

    /**
     * Saves the transformation state of the Graphics2D object.
     * @param graphics
     */
    public void preDraw(Graphics2D graphics) {
        drawBounds = syncModelToBounds(graphics);

        if (drawBounds != null) {
            originalTx = graphics.getTransform();

            int x = (originX == 0) ? offsetX : -drawBounds.width + offsetX;
            int y = (originY == 0) ? offsetY : -drawBounds.height + offsetY;

            transform.setToTranslation(x, y);
            graphics.transform(transform);

            if (showBounds) {
                graphics.setColor(Color.yellow);
                graphics.drawRect(0, 0, drawBounds.width, drawBounds.height);
            }
        }
    }

    /**
     * Restores the previously saved transformation state to the Graphics2D object.
     * @param graphics
     */
    public void postDraw(Graphics2D graphics) {
        if (drawBounds != null && originalTx != null) {
            graphics.setTransform(originalTx);
        }
    }

    public abstract Rectangle syncModelToBounds(Graphics2D graphics);

}
