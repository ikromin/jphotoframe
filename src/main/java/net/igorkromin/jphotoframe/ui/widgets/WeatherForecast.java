package net.igorkromin.jphotoframe.ui.widgets;

import net.igorkromin.jphotoframe.ui.ModelData;
import net.igorkromin.jphotoframe.weather.Forecast;
import net.igorkromin.jphotoframe.weather.Weather;
import net.igorkromin.jphotoframe.weather.WeatherConditionCodes;
import org.json.JSONObject;

import java.awt.*;

import java.util.ArrayList;
import java.util.List;

import static net.igorkromin.jphotoframe.ui.widgets.Factory.*;

public class WeatherForecast extends Transformable {

    private static final String DATA_SRC_TEMPERATURE = "$temperature";
    private static final String DATA_SRC_CONDITION = "$condition";
    private static final String DATA_SRC_CONDITION2 = "$condition2";
    private static final String DATA_SRC_GLYPH = "$glyph";
    private static final String DATA_SRC_DAY = "$day";
    private static final String DATA_SRC_DATE = "$date";

    private static final int ORIENT_HORZ = 0;
    private static final int ORIENT_VERT = 2;

    private static final String GAP_POS_LEADING = "leading";
    private static final String GAP_POS_TRAILING = "trailing";

    private static final String ORIENTATION_HORZ = "horizontal";
    private static final String ORIENTATION_VERT = "vertical";

    private ModelData data;

    private Rectangle bounds = new Rectangle(0, 0);
    private JSONObject textConfig;
    private List<Text> textWidgets = new ArrayList<>();
    private List<Text> drawList = new ArrayList<>();

    String dataSource = null;
    String textFormat = DEFAULT_FORMAT;
    private int itemGap = DEFAULT_GAP_SIZE;
    private int itemBoundScalar = DEFAULT_GAP_SCALAR;
    private int orientation = DEFAULT_ORIENTATION;
    private boolean reverse = DEFAULT_REVERSE;

    public WeatherForecast(JSONObject json, ModelData data, Rectangle drawAreaBounds) {
        super(json.getJSONObject(KEY_TRANSFORM), drawAreaBounds);

        this.data = data;

        // - text
        JSONObject text =  (json.has(KEY_TEXT)) ? json.getJSONObject(KEY_TEXT) : new JSONObject();
        textConfig = new JSONObject();
        textConfig.put(KEY_TEXT, text);

        // - data source
        if (text.has(KEY_DATA)) {
            dataSource = text.getString(KEY_DATA);
        }

        // - text format
        if (text.has(KEY_FORMAT)) {
            textFormat = text.getString(KEY_FORMAT);
        }
        text.remove(KEY_FORMAT);

        // - items
        if (json.has(KEY_ITEMS)) {
            JSONObject items = json.getJSONObject(KEY_ITEMS);

            // - gap
            if (items.has(KEY_ITEM_GAP)) {
                itemGap = items.getInt(KEY_ITEM_GAP);

                if (itemGap < 0) {
                    throw new RuntimeException("Item gap has to be a positive number");
                }
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
                    this.orientation = ORIENT_HORZ;
                }
                else if (ORIENTATION_VERT.equals(orientation)) {
                    this.orientation = ORIENT_VERT;
                }
            }

            // - reverse
            if (items.has(KEY_REVERSE)) {
                reverse = Boolean.parseBoolean(items.getString(KEY_REVERSE));
            }
        }
    }

    @Override
    public Rectangle syncModelToBounds(Graphics2D graphics) {

        Weather weather = data.getWeather();
        if (weather != null && weather.getForecast() != null) {
            List<Forecast> forecastList = weather.getForecast();
            int forecasts = forecastList.size();

            int width = 0;
            int height = 0;

            adjustWidgetList(forecasts);
            drawList.clear();

            for (int i = 0; i < forecasts; i++) {
                Forecast forecast = forecastList.get((reverse) ? forecasts - i - 1 : i);
                Text text = textWidgets.get(i);

                text.overwriteDataSource(getModelData(forecast));
                Rectangle bounds = text.syncModelToBounds(graphics);

                if (bounds != null) {
                    switch (orientation) {
                        case ORIENT_HORZ:
                            width += (bounds.width * ((i < forecasts - 1) ? itemBoundScalar : 1)) +
                                    ((i < forecasts - 1) ? itemGap : 0);
                            height = (bounds.height > height) ? bounds.height : height;

                            break;

                        case ORIENT_VERT:
                            height += (bounds.height * ((i < forecasts - 1) ? itemBoundScalar : 1)) +
                                    ((i < forecasts - 1) ? itemGap : 0);
                            width = (bounds.width > width) ? bounds.width : width;

                            break;
                    }

                    drawList.add(text);
                }
            }

            bounds.setBounds(0,0, width, height);
            return bounds;
        }

        return null;
    }

    private String getModelData(Forecast forecast) {
        String newText = null;

        if (DATA_SRC_TEMPERATURE.equals(dataSource)) {
            newText = String.format(textFormat, forecast.getLow(), forecast.getHigh());
        }
        else if (DATA_SRC_DAY.equals(dataSource)) {
            newText = String.format(textFormat, forecast.getDay());
        }
        else if (DATA_SRC_DATE.equals(dataSource)) {
            newText = String.format(textFormat, forecast.getDate());
        }
        else if (DATA_SRC_CONDITION.equals(dataSource)) {
            WeatherConditionCodes code = WeatherConditionCodes.fromInt(forecast.getCode());
            newText = String.format(textFormat, code.getInfoText());
        }
        else if (DATA_SRC_CONDITION2.equals(dataSource)) {
            WeatherConditionCodes code = WeatherConditionCodes.fromInt(forecast.getCode());
            newText = String.format(textFormat, code.getDetailedInfo());
        }
        else if (DATA_SRC_GLYPH.equals(dataSource)) {
            WeatherConditionCodes code = WeatherConditionCodes.fromInt(forecast.getCode());
            newText = String.format(textFormat, code.toString());
        }

        return newText;
    }

    /**
     * Adjusts the text widget list to have one widget per forecast day.
     * @param newSize
     */
    private void adjustWidgetList(int newSize) {
        int size = textWidgets.size();

        if (newSize == size) {
            return;
        }

        // grow the widget list
        if (size < newSize) {
            while (size < newSize) {
                textWidgets.add(new Text(textConfig, data, getDrawAreaBounds()));
                size++;
            }
        }
        // shrink the widget list
        else {
            while (size > newSize) {
                textWidgets.remove(size - 1);
                size--;
            }
        }
    }

    @Override
    public void drawTransformed(Graphics2D graphics) {
        int tx = 0;
        int ty = 0;

        for (Text text : drawList) {
            // copy the Graphics2D object to avoid incompatible state changes
            Graphics2D graphics2 = (Graphics2D) graphics.create();

            graphics2.translate(tx, ty);
            text.drawTransformed(graphics2);

            switch (orientation) {
                case ORIENT_HORZ:
                    tx += (text.getTextBounds().width * itemBoundScalar) + itemGap;
                    break;

                case ORIENT_VERT:
                    ty += (text.getTextBounds().height * itemBoundScalar) + itemGap;
                    break;
            }
        }
    }

}
