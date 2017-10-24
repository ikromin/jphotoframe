package net.igorkromin.jphotoframe.ui.widgets;

import net.igorkromin.jphotoframe.ui.ModelData;
import org.json.JSONObject;

import java.awt.*;

public class WeatherForecast extends Transformable {

    private static final String DATA_SRC_TEMPERATURE = "$temperature";

    private static final String KEY_ITEMS = "items";
    private static final String KEY_ITEM_GAP = "gap";
    private static final String KEY_GAP_POSITION = "gapPosition";
    private static final String KEY_ORIENTATION = "orientation";

    private static final String GAP_POS_LEADING = "leading";
    private static final String GAP_POS_TRAILING = "trailing";

    private static final String ORIENTATION_HORZ = "horizontal";
    private static final String ORIENTATION_VERT = "vertical";

    private static final int DEFAULT_GAP_SIZE = 150;
    private static final int DEFAULT_GAP_SCALAR = 0;
    private static final int DEFAULT_ORIENTATION_SX = 1;
    private static final int DEFAULT_ORIENTATION_SY = 0;

    private ModelData data;

    private int itemGap = DEFAULT_GAP_SIZE;
    private int itemBoundScalar = DEFAULT_GAP_SCALAR;
    private int orientationSX = DEFAULT_ORIENTATION_SX;
    private int orientationSY = DEFAULT_ORIENTATION_SY;

    public WeatherForecast(JSONObject json, ModelData data, Rectangle drawAreaBounds) {
        super(json.getJSONObject(Transformable.KEY_TRANSFORM), drawAreaBounds);

        this.data = data;

        // - items
        if (json.has(KEY_ITEMS)) {
            JSONObject items = json.getJSONObject(KEY_ITEMS);

            // - gap
            if (items.has(KEY_ITEM_GAP)) {
                itemGap = items.getInt(KEY_ITEM_GAP);
            }

            // - gapPosition
            if (items.has(KEY_GAP_POSITION)) {
                String pos = items.getString(KEY_GAP_POSITION);

                if (GAP_POS_LEADING.equals(pos)) {
                    itemBoundScalar = 0;
                }
                else if (GAP_POS_TRAILING.equals(pos)) {
                    itemBoundScalar = 1;
                }
            }

            // - orientation
            if (items.has(KEY_ORIENTATION)) {
                String orientation = items.getString(KEY_ORIENTATION);

                if (ORIENTATION_HORZ.equals(orientation)) {
                    orientationSX = 1;
                    orientationSY = 0;
                }
                else if (ORIENTATION_VERT.equals(orientation)) {
                    orientationSX = 0;
                    orientationSY = 1;
                }
            }
        }
    }

    @Override
    public Rectangle syncModelToBounds(Graphics2D graphics) {
        return new Rectangle(150, 150);
    }

    @Override
    public void drawTransformed(Graphics2D graphics) {

    }

}
