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
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * Background image filler that uses the source image as input and generates
 * the background by resizing the source image by 'backgroundSourcePercent'
 * and drawing it with opacity of 'backgroundOpacity' as set up in ConfigOptions.
 *
 * This results in a stretched, pixelated version of the input image being used
 * to fill the background. The background image does not retain the source image
 * aspect ratio.
 */
public class StretchScaleFiller implements BackgroundFiller {

    private BufferedImage buffer;
    AffineTransform txFill = new AffineTransform();
    AffineTransformOp backgroundOp;
    private int bufferWidth;
    private int bufferHeight;
    private float bgScalar;
    private float bgOpacity;

    @Override
    public void initialise(BufferedImage buffer, ConfigOptions config) {
        this.buffer = buffer;

        bufferWidth = buffer.getWidth();
        bufferHeight = buffer.getHeight();

        bgScalar = config.getBackgroundPercent();
        bgOpacity = config.getBackgroundOpacity();

        AffineTransform txB = new AffineTransform();
        txB.scale(bgScalar, bgScalar);
        backgroundOp = new AffineTransformOp(txB, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    }

    @Override
    public void fillBackground(BufferedImage srcImage) {
        BufferedImage background = createScaledTranslucentImage(srcImage);

        // transform op to stretch the background image to the buffer dimensions
        txFill.setToScale((double) bufferWidth / background.getWidth(), (double) bufferHeight / background.getHeight());
        AffineTransformOp opB2 = new AffineTransformOp(txFill, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        Graphics2D g = buffer.createGraphics();
        g.drawImage(background, opB2, 0, 0);
        g.dispose();
    }

    /**
     * Creates an image that is a copy of the passed in srcImage but with scaling and opacity applied as loaded in
     * the config object for the background percentage and opacity values.
     * @param srcImage
     * @return
     */
    private BufferedImage createScaledTranslucentImage(BufferedImage srcImage) {
        BufferedImage background = new BufferedImage(
                (int) (srcImage.getWidth() * bgScalar), (int) (srcImage.getHeight() * bgScalar), srcImage.getType());
        Graphics2D gb = background.createGraphics();

        // erase the buffer first
        gb.setColor(Color.black);
        gb.fillRect(0, 0, background.getWidth(), background.getHeight());

        // used to blend the image to the black background so only some of the source image luminosity shows
        Composite bgComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, bgOpacity);

        gb.setComposite(bgComposite);
        gb.drawImage(srcImage, backgroundOp, 0, 0);
        gb.dispose();

        return background;
    }

}
