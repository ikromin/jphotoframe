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

package net.igorkromin.jphotoframe;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Utility methods for image handling.
 */
public class ImageUtil {

    private static final String DEFAULT_IMAGE_FILE = "archetype.png";

    private static BufferedImage defaultImage = null;

    /**
     * Writes a buffered image to a file. Images always written as JPG. All exceptions are caught and logged but not
     * rethrown.
     * @param imageFile File to write the image to
     * @param bufferedImage Source buffered image object
     */
    public static void writeImage(final File imageFile, final BufferedImage bufferedImage)
    {
        Log.verbose("Writing image: " + imageFile.getAbsolutePath());

        try {
            JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
            jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpegParams.setCompressionQuality(0.9f); // TODO: allow config options to set quality here

            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            writer.setOutput(new FileImageOutputStream(imageFile));

            writer.write(null, new IIOImage(bufferedImage, null, null), jpegParams);
        }
        catch (Exception e) {
            Log.error("Failed to cache image: " + imageFile.getAbsolutePath() + " cause: " + e.getMessage(), e);
        }
    }

    /**
     * Loads the default logo image from the classpath and returns it as a buffered image object. The image is cached
     * after the initial load.
     * @return null if the default image can't be loaded otherwise the loaded BufferedImage object
     */
    public static BufferedImage getDefaultImage() {
        try {
            if (defaultImage == null) {
                defaultImage = ImageIO.read(ClassLoader.getSystemResource(DEFAULT_IMAGE_FILE));
            }
            return defaultImage;
        }
        catch (IOException e) {
            return null;
        }
    }

    /**
     * Lodads an image from the provided image file. If the passed in file is null a RuntimeException is thrown. If the
     * image file cannot be read, a RuntimeException is also thrown.
     * @param imageFile
     * @return
     */
    public static BufferedImage readImage(final File imageFile) {

        if (imageFile == null) {
            throw new RuntimeException("Image file is null");
        }

        try {
            BufferedImage img = ImageIO.read(imageFile);

            if (img == null) {
                throw new RuntimeException("Could not load image file: " + imageFile);
            }

            return img;
        }
        catch (IOException|OutOfMemoryError e) {
            Log.error("Could not load file: " + imageFile.getAbsolutePath(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Creates an image that is a copy of the passed in srcImage but with scaling and opacity applied as loaded in
     * the config object for the background percentage and opacity values.
     * @param srcImage
     * @param config
     * @return
     */
    public static BufferedImage createScaledTranslucentImage(BufferedImage srcImage, ConfigOptions config) {
        float bgScalar = config.getBackgroundPercent();

        BufferedImage background = new BufferedImage(
                (int) (srcImage.getWidth() * bgScalar), (int) (srcImage.getHeight() * bgScalar), srcImage.getType());
        Graphics2D gb = background.createGraphics();

        AffineTransform txB = new AffineTransform();
        txB.scale(bgScalar, bgScalar);
        AffineTransformOp opB = new AffineTransformOp(txB, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        // erase the buffer first
        gb.setColor(Color.black);
        gb.fillRect(0, 0, background.getWidth(), background.getHeight());

        // used to blend the image to the black background so only some of the source image luminosity shows
        Composite bgComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, config.getBackgroundOpacity());

        gb.setComposite(bgComposite);
        gb.drawImage(srcImage, opB, 0, 0);
        gb.dispose();

        return background;
    }

}
