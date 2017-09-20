package net.igorkromin.jphotoframe;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Utility methods for image handling.
 */
public class ImageUtil {

    private static final String DEFAULT_IMAGE_FILE = "archetype.png";

    /**
     * Writes a buffered image to a file. All exceptions are caught and logged but not rethrown.
     * @param imageFile File to write the image to
     * @param bufferedImage Source buffered image object
     */
    public static void writeImage(final File imageFile, final BufferedImage bufferedImage)
    {
        Log.verbose("Writing image: " + imageFile.getAbsolutePath());

        try {
            ImageIO.write(bufferedImage, "JPG", imageFile);
        }
        catch (Exception e) {
            Log.error("Failed to cache image: " + imageFile.getAbsolutePath() + " cause: " + e.getMessage(), e);
        }
    }

    /**
     * Loads the default logo image from the classpath and returns it as a buffered image object.
     * @return
     * @throws IOException
     */
    public static BufferedImage getDefaultImage() throws IOException {
        return ImageIO.read(ClassLoader.getSystemResource(DEFAULT_IMAGE_FILE));
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
     * Calculates image dimensions correct to the aspect ratio of the passed in image that would fit within the
     * given rectangle boundary.
     * @param img
     * @param bounds
     * @return Two-element array with the width at index 0 and height at index 1
     */
    public static int[] getAspectDimensions(BufferedImage img, Rectangle bounds) {
        int[] dimensions =  new int[2];

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

        dimensions[0] = newWidth;
        dimensions[1] = newHeight;

        return dimensions;
    }
}
