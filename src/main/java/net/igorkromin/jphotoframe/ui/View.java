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

package net.igorkromin.jphotoframe.ui;

import net.igorkromin.jphotoframe.*;
import net.igorkromin.jphotoframe.weather.Forecast;
import net.igorkromin.jphotoframe.weather.WeatherConditionCodes;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by ikromin on 29/08/2015.
 */
public class View extends JFrame {

    private static final String WEATHER_ICONS_FONT_FILE = "/weathericons-regular-webfont.ttf";

    Image backgroundImage;
    Font dateFont;
    Font timeFont;
    Font conditionFont;
    Font forecastFont;
    Font locationFont;
    boolean ready = false;
    Color textColor;
    Color textOutlineColor;
    BasicStroke outlineStroke;
    AlphaComposite bgComposite;
    AffineTransform tx;

    ConfigOptions config;
    ModelData data;

    public View(ConfigOptions config, ModelData data)
            throws IOException
    {
        if (GraphicsEnvironment.isHeadless()) {
            throw new RuntimeException("Cannot run in headless mode");
        }

        this.config = config;
        this.data = data;

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        setBackground(Color.black);

        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setCursor(null);
        contentPane.setOpaque(false);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawImage((Graphics2D) g);
            }
        };
        panel.setBackground(Color.black);
        panel.setOpaque(true);
        contentPane.add(panel);

        tx = new AffineTransform();

        dateFont = new Font(config.getFontName(), Font.BOLD, config.getFontSizeDate());
        timeFont = new Font(config.getFontName(), Font.BOLD, config.getFontSizeTime());

        try {
            conditionFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream(WEATHER_ICONS_FONT_FILE))
                    .deriveFont(config.getFontSizeWeatherCondition());
        }
        catch (FontFormatException e) {
            throw new RuntimeException(e.getMessage());
        }

        forecastFont = new Font(config.getFontName(), Font.BOLD, config.getFontSizeWeatherForecast());
        locationFont = new Font(config.getFontName(), Font.BOLD, config.getFontSizeLocation());

        Log.verbose("Font set to " + dateFont.getFontName());

        int[] rgb1 = config.getTextColor();
        int[] rgb2 = config.getTextOutlineColor();
        textColor = new Color(rgb1[0], rgb1[1], rgb1[2]);
        textOutlineColor = new Color(rgb2[0], rgb2[1], rgb2[2]);

        outlineStroke = new BasicStroke(config.getTextOutlineWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        bgComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, config.getBackgroundOpacity());
    }

    private void drawImage(Graphics2D g) {
        if (!ready) {
            return;
        }

        BufferedImage image = data.getCurrentImage();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Rectangle rect = getBounds();
        int width = image.getWidth();
        int height = image.getHeight();

        Composite c = g.getComposite();

        if (backgroundImage != null) {
            g.setComposite(bgComposite);
            g.drawImage(backgroundImage, 0, 0, rect.width, rect.height, null);
        }

        g.setComposite(c);
        g.drawImage(image, (int) (rect.getWidth() - width) / 2, (int) (rect.getHeight() - height) / 2, null);

        drawDateTime(g, rect, dateFont, data.getDateString(), config.getDateOffsetX(), config.getDateOffsetY());
        drawDateTime(g, rect, timeFont, data.getTimeString(), config.getTimeOffsetX(), config.getTimeOffsetY());

        int position = 0;
        if (data.getWeather() != null) {
            for (Forecast f : data.getWeather().getForecast()) {
                if (position < config.getWeatherForecastDays()) {
                    drawForecast(g, rect, f, position);
                }
                else {
                    break;
                }
                position++;
            }
        }
    }

    private void drawForecast(Graphics2D g, Rectangle rect, Forecast forecast, int position) {
        int offsetX = config.getWeatherOffsetX();
        int offsetY1 = config.getWeatherConditionOffsetY();
        int offsetY2 = config.getWeatherForecastDayTempOffsetY();
        int offsetY3 = config.getWeatherForecastConditionOffsetY();
        int offsetY4 = config.getWeatherCityOffsetY();
        int positionWidth = config.getWeatherDayWidth();

        String forecastText = forecast.getDay() + " " + forecast.getLow() + "-" + forecast.getHigh();
        WeatherConditionCodes conditionEnum = WeatherConditionCodes.fromInt(forecast.getCode());
        String conditionIcon = conditionEnum.toString();

        TextLayout text;
        Rectangle textBounds;
        FontRenderContext fontRenderContext = g.getFontRenderContext();

        // conditionIcon 'icon'
        text = new TextLayout(conditionIcon, conditionFont, fontRenderContext);
        int conditionWidth = (int) text.getBounds().getWidth();
        int nudgeX = (positionWidth > conditionWidth) ? (positionWidth - conditionWidth) / 2 : 0; // center in allocated space
        tx.setToTranslation(nudgeX + offsetX + (position * positionWidth), rect.height - offsetY1);
        drawText(g, conditionFont, text);

        // forecast day+temp text
        text = new TextLayout(forecastText, forecastFont, fontRenderContext);
        textBounds = text.getBounds().getBounds();
        tx.setToTranslation(offsetX + (position * positionWidth), rect.height - textBounds.height - offsetY2);
        drawText(g, forecastFont, text);

        // forecast condition text
        text = new TextLayout(conditionEnum.getInfoText(), forecastFont, fontRenderContext);
        textBounds = text.getBounds().getBounds();
        tx.setToTranslation(offsetX + (position * positionWidth), rect.height - textBounds.height - offsetY3);
        drawText(g, forecastFont, text);

        // forecast location
        if (position == 0) {
            String locationText = data.getWeather().getCity() + ", " + data.getWeather().getCountry();
            text = new TextLayout(locationText, locationFont, fontRenderContext);
            textBounds = text.getBounds().getBounds();
            tx.setToTranslation(offsetX + (position * positionWidth), rect.height - textBounds.height - offsetY4);
            drawText(g, locationFont, text);
        }
    }

    private void drawDateTime(Graphics2D g, Rectangle rect, Font font, String dispString, int offsetX, int offsetY) {
        FontRenderContext fontRenderContext = g.getFontRenderContext();
        TextLayout text = new TextLayout(dispString, font, fontRenderContext);
        Rectangle textBounds = text.getBounds().getBounds();
        tx.setToTranslation(rect.width - textBounds.width - offsetX, offsetY + textBounds.height);
        drawText(g, font, text);
    }

    private void drawText(Graphics2D g, Font font, TextLayout text) {
        Shape shape = text.getOutline(null);

        g.setFont(font);
        g.setStroke(outlineStroke);
        g.setTransform(tx);
        g.setColor(textOutlineColor);
        g.draw(shape);
        g.setColor(textColor);

        text.draw(g, 0, 0);
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void displayImage(File file) {
        if (!ready) {
            return;
        }

        try {
            // load the image
            BufferedImage img = ImageUtil.readImage(file);
            data.setCurrentImage(img);

            int[] dimensions = ImageUtil.getAspectDimensions(img, getBounds());
            int newWidth = dimensions[0];
            int newHeight = dimensions[1];

            // create and draw the image scaled to the device we're displaying on
            BufferedImage image = getGraphicsConfiguration().createCompatibleImage(newWidth, newHeight);
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(img, 0, 0, newWidth, newHeight, null);
            g.dispose();

            // create background image
            int bgWidth = (int) (newWidth * config.getBackgroundPercent());
            int bgHeight = (int) (newHeight * config.getBackgroundPercent());
            backgroundImage = image.getScaledInstance(bgWidth, bgHeight, Image.SCALE_FAST); // TODO: changes this to use scaled drawImage() instead

            data.setCurrentImage(image);
        }
        catch (Exception e) {
            Log.warning("Could not display image due to error: " + e.getMessage());
            data.setCurrentImage(null);
            backgroundImage = null;
        }

        repaint();
    }

}