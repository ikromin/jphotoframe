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

package net.igorkromin;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ikromin on 29/08/2015.
 */
public class View extends JFrame {

    private static final String WEATHER_ICONS_FONT_FILE = "/net/igorkromin/weathericons-regular-webfont.ttf";
    private static final String DEFAULT_IMAGE_FILE = "net/igorkromin/archetype.png";

    BufferedImage defaultImage;
    BufferedImage currentImage;
    Image backgroundImage;
    Font dateFont;
    Font timeFont;
    Font conditionFont;
    Font forecastFont;
    Font locationFont;
    boolean ready = false;
    SimpleDateFormat dateFormat;
    SimpleDateFormat timeFormat;
    ConfigOptions config;
    Color textColor;
    Color textOutlineColor;
    BasicStroke outlineStroke;
    AlphaComposite bgComposite;
    AffineTransform tx;
    Weather weather;

    public View(ConfigOptions config)
            throws IOException, FontFormatException
    {
        this.config = config;

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

        defaultImage = ImageIO.read(ClassLoader.getSystemResource(DEFAULT_IMAGE_FILE));

        dateFont = new Font(config.getFontName(), Font.BOLD, config.getFontSizeDate());
        timeFont = new Font(config.getFontName(), Font.BOLD, config.getFontSizeTime());
        conditionFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream(WEATHER_ICONS_FONT_FILE))
                .deriveFont(config.getFontSizeWeatherCondition());
        forecastFont = new Font(config.getFontName(), Font.BOLD, config.getFontSizeWeatherForecast());
        locationFont = new Font(config.getFontName(), Font.BOLD, config.getFontSizeLocation());

        dateFormat = new SimpleDateFormat(config.getDateFormat());
        timeFormat = new SimpleDateFormat(config.getTimeFormat());

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

        if (currentImage == null) {
            currentImage = defaultImage;
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Rectangle rect = getBounds();
        int width = currentImage.getWidth();
        int height = currentImage.getHeight();

        Composite c = g.getComposite();

        if (backgroundImage != null) {
            g.setComposite(bgComposite);
            g.drawImage(backgroundImage, 0, 0, rect.width, rect.height, null);
        }

        g.setComposite(c);
        g.drawImage(currentImage, (int) (rect.getWidth() - width) / 2, (int) (rect.getHeight() - height) / 2, null);

        Date dateTime = new Date();
        drawDateTime(g, rect, dateFont, dateFormat.format(dateTime), config.getDateOffsetX(), config.getDateOffsetY());
        drawDateTime(g, rect, timeFont, timeFormat.format(dateTime), config.getTimeOffsetX(), config.getTimeOffsetY());

        int position = 0;
        if (weather != null) {
            for (Forecast f : weather.getForecast()) {
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
            String locationText = weather.getCity() + ", " + weather.getCountry();
            text = new TextLayout(locationText, locationFont, fontRenderContext);
            textBounds = text.getBounds().getBounds();
            tx.setToTranslation(offsetX + (position * positionWidth), rect.height - textBounds.height - offsetY2 - 180);
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

        // load the image
        BufferedImage img;
        try {
            img = ImageIO.read(file);
        }
        catch (IOException|OutOfMemoryError e) {
            System.out.println("Could not load file: " + file.getAbsolutePath() + " cause: " + e.getMessage());
            return;
        }

        if (img == null) {
            System.out.println("Could not load file: " + file.getAbsolutePath());
            return;
        }

        Rectangle bounds = getBounds();
        int width = img.getWidth();
        int height = img.getHeight();
        int newWidth, newHeight;

        if (width > height) {
            newWidth = bounds.width;
            newHeight = (newWidth * height) / width;
        }
        else {
            newHeight = bounds.height;
            newWidth = (newHeight * width) / height;
        }

        try {
            // create and draw the image scaled to the device we're displaying on
            currentImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g = currentImage.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(img, 0, 0, newWidth, newHeight, null);
            g.dispose();

            // create background image
            int bgWidth = (int) (newWidth * config.getBackgroundPercent());
            int bgHeight = (int) (newHeight * config.getBackgroundPercent());
            backgroundImage = currentImage.getScaledInstance(bgWidth, bgHeight, Image.SCALE_FAST);
        }
        catch (Exception e) {
            System.out.println("Could not create memory image: " + e.getMessage());
            currentImage = null;
            backgroundImage = null;
        }

        repaint();
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public BufferedImage getCurrentImage() {
        return currentImage;
    }

}
