package net.igorkromin.jphotoframe.ui.widgets;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;

/**
 * Widget tha supports transformations. Used as a base for other widgets that can be transformed.
 *
 * Provides the following properties:
 *  - origin : two-integer array specifying the transformation origin, similar to Anchor 'anchor'
 *  - offset : two-integer array specifying the offset in pixels relative to the origin
 *  - rotate : degrees rotation around the origin point
 *  - showBounds : whether to show the drawing boundary box or not
 */
public abstract class Transformable extends Widget {

    public static final String KEY_TRANSFORM = "transform";

    private static final String KEY_ORIGIN = "origin";
    private static final String KEY_OFFSET = "offset";
    private static final String KEY_ROTATE = "rotate";
    private static final String KEY_SHOW_BOUNDS = "showBounds";

    private static final int ORIGIN_WIDTH = 8;
    private static final int ORIGIN_DISPLACE = ORIGIN_WIDTH / 2;

    private int originX = 0;
    private int originY = 0;
    private int offsetX = 0;
    private int offsetY = 0;
    private boolean showBounds = false;
    private Rectangle drawBounds;
    private double radsRotation = 0.0;

    public Transformable(JSONObject json, Rectangle drawAreaBounds) {
        super(json, drawAreaBounds);

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

        // - rotate
        if (json.has(KEY_ROTATE)) {
            radsRotation = Math.toRadians(json.getInt(KEY_ROTATE));
        }

        // - showBounds
        if (json.has(KEY_SHOW_BOUNDS)) {
            showBounds = Boolean.parseBoolean(json.getString(KEY_SHOW_BOUNDS));
        }
    }

    public void draw(Graphics2D graphics) {
        // copy the Graphics2D object to avoid incompatible state changes
        Graphics2D graphics2 = (Graphics2D) graphics.create();

        drawBounds = syncModelToBounds(graphics2);

        if (drawBounds != null) {
            // translation
            int x = (originX == 0) ? offsetX : -drawBounds.width + offsetX;
            int y = (originY == 0) ? offsetY : -drawBounds.height + offsetY;
            graphics2.translate(x, y);

            // rotation
            int rx = ((originX == 0) ? 0 : drawBounds.width);
            int ry = ((originY == 0) ? 0 : drawBounds.height);
            graphics2.rotate(radsRotation, rx, ry);

            if (showBounds) {
                graphics2.setColor(Color.yellow);
                graphics2.drawRect(0, 0, drawBounds.width, drawBounds.height);

                int ox = ((originX == 0) ? 0 : drawBounds.width) - ORIGIN_DISPLACE;
                int oy = ((originY == 0) ? 0 : drawBounds.height) - ORIGIN_DISPLACE;

                graphics2.fillOval(ox, oy, ORIGIN_WIDTH, ORIGIN_WIDTH);
            }

            drawTransformed(graphics2);
        }

        graphics2.dispose();
    }

    public abstract Rectangle syncModelToBounds(Graphics2D graphics);

    public abstract void drawTransformed(Graphics2D graphics);

}
