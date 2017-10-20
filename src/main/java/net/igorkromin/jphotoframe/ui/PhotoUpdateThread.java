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
import net.igorkromin.jphotoframe.ImageDirectory;
import net.igorkromin.jphotoframe.ImageUtil;
import net.igorkromin.jphotoframe.Log;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PhotoUpdateThread extends DataUpdateThread {

    private ImageDirectory imageDirectory;
    private BufferedImage buffer;
    private int bufferWidth;
    private int bufferHeight;
    private float bufferAspect;

    /**
     * Rectangle is used to sets the image drawing dimensions. All loaded images will be made to fit into this rectangle.
     * All cached images will be sized to this rectangle.
     */
    public PhotoUpdateThread(Controller controller, ConfigOptions config, ModelData data, int sleepTime, BufferedImage buffer) {
        super(controller, config, data, sleepTime);
        this.buffer = buffer;

        bufferWidth = buffer.getWidth();
        bufferHeight = buffer.getHeight();
        bufferAspect = (float) bufferWidth / bufferHeight;

        Log.verbose("Image buffer dimensions=" + bufferWidth + "x" + bufferHeight + " aspect=" + bufferAspect);

        try {
            imageDirectory = new ImageDirectory(config.getImageDirectory(), config.getCacheDirectory(), controller);
            imageDirectory.startWatching();
        }
        catch (Exception e) {
            Log.error("Could not start watching photo directory", e);
            doNotRun();
        }
    }

    @Override
    public void doUpdate() {

        File f = imageDirectory.nextFile();
        File c = imageDirectory.getCachedImageFile(f);

        // display cached file if it exists
        // TODO: allow caching to be disabled in configuration
        if (imageDirectory.fileExists(c)) {
            Log.verbose("Using cached image: " + c.getAbsolutePath());

            // cached images are not rescaled, just set to the data model directly
            BufferedImage cachedImage = silentLoad(c);
            if (cachedImage != null) {
                getData().setCurrentImage(cachedImage);
                getController().requestUpdate();
            }
        }
        // fall back to full res image + create a cached copy
        else if (imageDirectory.fileExists(f)) {
            BufferedImage image = silentLoad(f);

            if (image != null) {
                // determine the size and aspect ratio of the loaded image
                int imageWidth = image.getWidth();
                int imageHeight = image.getHeight();
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
                drawToBuffer(image, scalar);

                // write cached image
                if (c != null) {
                    ImageUtil.writeImage(c, buffer);
                }
            }
        }
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

        BufferedImage background = ImageUtil.createScaledTranslucentImage(image, getConfig());

        // transform op to stretch the background image to the buffer dimensions
        AffineTransform txB2 = new AffineTransform();
        txB2.scale((double) bufferWidth / background.getWidth(), (double) bufferHeight / background.getHeight());
        AffineTransformOp opB2 = new AffineTransformOp(txB2, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        g.drawImage(background, opB2, 0, 0);

        // transform op to fit the image to the buffer
        AffineTransform tx = new AffineTransform();
        tx.scale(scalar, scalar);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BICUBIC);

        // get centered coordinates based on the scaled image size
        int x = (bufferWidth - (int) (image.getWidth() * scalar)) / 2;
        int y = (bufferHeight - (int) (image.getHeight() * scalar)) / 2;

        g.drawImage(image, op, x, y);
        g.dispose();

        getData().setCurrentImage(buffer);
        getController().requestUpdate();
    }

    private BufferedImage silentLoad(File file) {
        BufferedImage img = null;
        try {
            img = ImageUtil.readImage(file);
        }
        catch (Exception e) {
            // ignore any load exceptions, they would be logged in the ImageUtil class
        }
        return img;
    }

    @Override
    public void beforeExit() {
        Log.verbose("Stopping directory watcher");
        imageDirectory.stopWatching();
    }

}
