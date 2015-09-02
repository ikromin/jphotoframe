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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ikromin on 29/08/2015.
 */
public class View extends JFrame {

    BufferedImage defaultImage;
    BufferedImage currentImage;
    Font dateFont;
    Font timeFont;
    boolean ready = false;
    SimpleDateFormat dateFormat;
    SimpleDateFormat timeFormat;
    ConfigOptions config;
    Color textColor;
    Color textOutlineColor;

    public View(ConfigOptions config)
            throws IOException
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

        defaultImage = ImageIO.read(ClassLoader.getSystemResource("net/igorkromin/archetype.png"));

        dateFont = new Font(config.getFontName(), Font.BOLD, config.getFontSizeDate());
        timeFont = new Font(config.getFontName(), Font.BOLD, config.getFontSizeTime());

        dateFormat = new SimpleDateFormat(config.getDateFormat());
        timeFormat = new SimpleDateFormat(config.getTimeFormat());

        int[] rgb1 = config.getTextColor();
        int[] rgb2 = config.getTextOutlineColor();
        textColor = new Color(rgb1[0], rgb1[1], rgb1[2]);
        textOutlineColor = new Color(rgb2[0], rgb2[1], rgb2[2]);
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

        g.drawImage(currentImage, (int) (rect.getWidth() - width) / 2, (int) (rect.getHeight() - height) / 2, null);

        Date dateTime = new Date();
        drawText(g, rect, dateFont, dateFormat.format(dateTime), config.getDateOffsetX(), config.getDateOffsetY());
        drawText(g, rect, timeFont, timeFormat.format(dateTime), config.getTimeOffsetX(), config.getTimeOffsetY());
    }

    private void drawText(Graphics2D g, Rectangle rect, Font font, String dispString, int offsetX, int offsetY) {
        g.setFont(font);
        Rectangle2D strBounds = g.getFontMetrics().getStringBounds(dispString, g);

        int sy = (int) strBounds.getHeight() + offsetY;
        int sx = rect.width - (int) strBounds.getWidth() - offsetX;

        int w = config.getTextOutlineOffset();

        // draw outline
        g.setColor(textOutlineColor);
        g.drawString(dispString, sx + w, sy);
        g.drawString(dispString, sx - w, sy);
        g.drawString(dispString, sx, sy + w);
        g.drawString(dispString, sx, sy - w);

        // if smooth text outline is set, draw the diagonally offset text too
        if (config.isTextSmoothOutline()) {
            g.drawString(dispString, sx + w, sy + w);
            g.drawString(dispString, sx - w, sy - w);
            g.drawString(dispString, sx + w, sy - w);
            g.drawString(dispString, sx - w, sy + w);
        }

        g.setColor(textColor);
        g.drawString(dispString, sx, sy);
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public BufferedImage displayImage(File file) {
        if (!ready) {
            return null;
        }

        // load the image
        BufferedImage img;
        try {
            img = ImageIO.read(file);
        }
        catch (IOException e) {
            System.out.println("Could not load file: " + file.getAbsolutePath() + " cause: " + e.getMessage());
            return null;
        }

        if (img == null) {
            System.out.println("Could not load file: " + file.getAbsolutePath());
            return null;
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

        // create and draw the image scaled to the device we're displaying on
        currentImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = currentImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(img, 0, 0, newWidth, newHeight, null);
        g.dispose();

        repaint();
        return currentImage;
    }

}
