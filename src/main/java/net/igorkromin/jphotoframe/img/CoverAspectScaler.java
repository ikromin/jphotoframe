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
import net.igorkromin.jphotoframe.Log;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * Image scaler that resizes the source image based on its aspect ratio. The
 * image is then scaled by the significant dimension. Aspect ratio less than
 * one uses height. Aspect ration 1.0 or more uses width.
 *
 * This scaler does not guarantee that an image will completely fit within the
 * image draw buffer.
 */
public class CoverAspectScaler implements ImageScaler {

    private BufferedImage buffer;
    private int bufferWidth;
    private int bufferHeight;
    private float bufferAspect;

    @Override
    public void initialise(BufferedImage buffer, ConfigOptions config) {
        this.buffer = buffer;

        bufferWidth = buffer.getWidth();
        bufferHeight = buffer.getHeight();
        bufferAspect = (float) bufferWidth / bufferHeight;

        Log.verbose("Image buffer dimensions=" + bufferWidth + "x" + bufferHeight + " aspect=" + bufferAspect);
    }

    @Override
    public void drawScaledImage(BufferedImage srcImage) {
        // determine the size and aspect ratio of the loaded image
        int imageWidth = srcImage.getWidth();
        int imageHeight = srcImage.getHeight();
        float imageAspect = (float) imageWidth / imageHeight;

        Log.verbose("Image dimensions=" + imageWidth + "x" + imageHeight + " aspect=" + imageAspect);

        // adjust the size to the current buffer based on aspect ratio
        float scalar;
        if (imageAspect < 1.f) { // portrait
            scalar = (float) bufferHeight / imageHeight;
        }
        else { // landscape
            scalar = (float) bufferWidth / imageWidth;
        }

        Log.verbose("Image dimension scalar=" + scalar);
        drawToBuffer(srcImage, scalar);
    }

    /**
     * Draws an image to the buffer using scaled image dimensions and updates the view of any necessary updates.
     * @param image
     */
    private void drawToBuffer(BufferedImage image, float scalar) {
        if (image == null) {
            return;
        }

        Graphics2D g = buffer.createGraphics();

        // transform op to fit the image to the buffer
        AffineTransform tx = new AffineTransform();
        tx.scale(scalar, scalar);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BICUBIC);

        // get centered coordinates based on the scaled image size
        int x = (bufferWidth - (int) (image.getWidth() * scalar)) / 2;
        int y = (bufferHeight - (int) (image.getHeight() * scalar)) / 2;

        g.drawImage(image, op, x, y);
        g.dispose();
    }

}
