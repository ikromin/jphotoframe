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
import net.igorkromin.jphotoframe.weather.Weather;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;

import static net.igorkromin.jphotoframe.ui.widgets.Factory.*;

/**
 * Text object bound to a model data source. Supports the following data sources:
 *  - date
 *  - time
 *
 * Provides the following properties:
 *  - data : model data source to display
 *  - format : text format string
 *  - font : name of the font to use to render the text
 *  - size : font size
 *  - colour : colour of the text
 *  - outlineColour : colour of the outline drawn around the text
 *  - outlineWidth : width of the outline, values larger than 1 will typically exceed draw bounds
 *  - transform : transformation to apply to this text
 */
public class Text extends Transformable {

    private static final String DATA_SRC_TIME = "$time";
    private static final String DATA_SRC_DATE = "$date";
    private static final String DATA_SRC_WEATHER_GEO = "$weather.geo";

    private ModelData data;
    private String text = null;

    private Rectangle textBounds = null;
    private Shape shape = null;

    String dataSource = null;
    String textFormat = DEFAULT_FORMAT;
    Color colour = DEFAULT_COLOUR;
    Color outlineColour = DEFAULT_OUTLINE_COLOUR;
    Stroke outlineStroke = null;
    Font font;

    private Text(JSONObject transform, Rectangle drawAreaBounds) {
        super(transform, drawAreaBounds);
    }

    public Text(JSONObject json, ModelData data, Rectangle drawAreaBounds) {
        this(safeGet(json, KEY_TRANSFORM), drawAreaBounds);

        this.data = data;

        int width = DEFAULT_OUTLINE_WIDTH;
        String fontName = DEFAULT_FONT_NAME;
        int fontSize = DEFAULT_FONT_SIZE;

        // - text
        if (json.has(KEY_TEXT)) {
            JSONObject text = json.getJSONObject(KEY_TEXT);

            // - data source
            if (text.has(KEY_DATA)) {
                dataSource = text.getString(KEY_DATA);
            }

            // - text format
            if (text.has(KEY_FORMAT)) {
                textFormat = text.getString(KEY_FORMAT);
            }

            // - font name
            if (text.has(KEY_FONT)) {
                fontName = text.getString(KEY_FONT);
            }

            // - font size
            if (text.has(KEY_DATA)) {
                fontSize = text.getInt(KEY_SIZE);
            }

            // - colour
            if (text.has(KEY_COLOUR)) {
                JSONArray arr = text.getJSONArray(KEY_COLOUR);
                if (arr.length() == 3) {
                    colour = new Color(arr.getInt(0), arr.getInt(1), arr.getInt(2));
                }
            }

            // - outlineColour
            if (text.has(KEY_OUTLINE_COLOUR)) {
                JSONArray arr = text.getJSONArray(KEY_OUTLINE_COLOUR);
                if (arr.length() == 3) {
                    outlineColour = new Color(arr.getInt(0), arr.getInt(1), arr.getInt(2));
                }
            }

            // - outlineWidth
            if (text.has(KEY_OUTLINE_WIDTH)) {
                width = text.getInt(KEY_OUTLINE_WIDTH);
            }
        }

        outlineStroke = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        font = FontManager.getFont(fontName, Font.BOLD, fontSize);
    }

    @Override
    public Rectangle syncModelToBounds(Graphics2D graphics) {
        String newText = getModelData();

        if (newText != null) {
            if (newText.equals(text)) {
                return textBounds;
            }

            FontRenderContext frc = graphics.getFontRenderContext();
            GlyphVector gv = font.createGlyphVector(frc, newText);

            shape = gv.getOutline();
            textBounds = shape.getBounds();
            text = newText;

            return textBounds;
        }

        return null;
    }

    private String getModelData() {
        String newText = null;

        try {
            if (DATA_SRC_DATE.equals(dataSource)) {
                newText = String.format(textFormat, data.getDateString());
            }
            else if (DATA_SRC_TIME.equals(dataSource)) {
                newText = String.format(textFormat, data.getTimeString());
            }
            else if (DATA_SRC_WEATHER_GEO.equals(dataSource)) {
                Weather w = data.getWeather();
                if (w != null) {
                    newText = String.format(textFormat, w.getCountry(), w.getCity());
                }
            }
            else if (dataSource != null && !"".equals(dataSource)) {
                newText = String.format(textFormat, dataSource);
            }
        }
        catch (Exception e) {
            Log.error("Error during text formatting", e);
            return "ERROR! - " + e.getMessage();
        }

        return newText;
    }

    @Override
    public void drawTransformed(Graphics2D graphics) {
        if (text == null) {
            return;
        }

        graphics.setColor(colour);

        graphics.translate(-textBounds.x, -textBounds.y);
        graphics.fill(shape);

        Stroke originalStroke = graphics.getStroke();

        graphics.setStroke(outlineStroke);
        graphics.setColor(outlineColour);
        graphics.draw(shape);

        graphics.setStroke(originalStroke);
    }

    public void overwriteDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public Rectangle getTextBounds() {
        return textBounds;
    }
}
