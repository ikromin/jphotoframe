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

import net.igorkromin.jphotoframe.ConfigOptions;
import net.igorkromin.jphotoframe.ImageUtil;
import net.igorkromin.jphotoframe.ui.widgets.Factory;
import net.igorkromin.jphotoframe.ui.widgets.Widget;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * Created by ikromin on 29/08/2015.
 */
public class View extends JFrame {

    GraphicsDevice device;

    ConfigOptions config;
    ModelData data;
    List<Widget> widgets;

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
                drawScreen((Graphics2D) g);
            }
        };
        panel.setBackground(Color.black);
        panel.setOpaque(true);
        contentPane.add(panel);

        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[config.getGfxDeviceNum()];
        if (config.isFullScreenWindow()) {
            device.setFullScreenWindow(this);
        }
        else {
            setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        }

        // stupid workaround for OS X losing focus
        setVisible(false);
        setVisible(true);
    }



    @Override
    public void dispose() {
        if (config.isFullScreenWindow()) {
            device.setFullScreenWindow(null);
        }

        super.dispose();
    }

    private void drawScreen(Graphics2D g) {
        BufferedImage image = data.getCurrentImage();
        Rectangle rect = getBounds();

        // if there is no image loaded, show the logo image instead (centered on screen)
        if (image == null) {
            // erase the screen first
            g.setColor(Color.black);
            g.fillRect(0, 0, rect.width, rect.height);

            image = ImageUtil.getDefaultImage();
            int x = (rect.width - image.getWidth()) / 2;
            int y = (rect.height - image.getHeight()) / 2;
            g.drawImage(image, x, y, null);
        }
        // have image so draw it 'as is' the photo update thread takes care of centering, background, etc
        else {
            g.drawImage(image, 0, 0, null);
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw all of the screen widgets
        if (widgets != null) {
            for (Widget w : widgets) {
                w.draw(g);
            }
        }

        data.resetChange();
    }

    public BufferedImage getImageBuffer() {
        Rectangle rect = getBounds();
        return getGraphicsConfiguration().createCompatibleImage(rect.width, rect.height);
    }

    public void loadWidgets() {
        widgets = Factory.makeWidgetsFromLayout(config.getLayoutFile(), data, getBounds());
    }

}
