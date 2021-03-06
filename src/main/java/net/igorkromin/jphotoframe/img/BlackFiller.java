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
package net.igorkromin.jphotoframe.img;

import net.igorkromin.jphotoframe.ConfigOptions;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Background image filler that draws a black rectangle covering the entire
 * image draw buffer. Essentially erases any previously drawn image.
 */
public class BlackFiller implements BackgroundFiller {

    private BufferedImage buffer;

    @Override
    public void initialise(BufferedImage buffer, ConfigOptions config) {
        this.buffer = buffer;
    }

    @Override
    public void fillBackground(BufferedImage srcImage) {
        Graphics2D g = buffer.createGraphics();

        g.setColor(Color.black);
        g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());

        g.dispose();
    }

}
