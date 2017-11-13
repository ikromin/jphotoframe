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
import net.igorkromin.jphotoframe.img.BackgroundFiller;
import net.igorkromin.jphotoframe.img.Factory;
import net.igorkromin.jphotoframe.img.ImageScaler;
import net.igorkromin.jphotoframe.img.ImageUtil;
import net.igorkromin.jphotoframe.Log;

import java.awt.image.BufferedImage;
import java.io.File;

public class PhotoUpdateThread extends DataUpdateThread {

    private ImageDirectory imageDirectory;
    private BufferedImage buffer;
    private BackgroundFiller filler;
    private ImageScaler scaler;
    private boolean doNotCache = false;

    /**
     * Rectangle is used to sets the image drawing dimensions. All loaded images will be made to fit into this rectangle.
     * All cached images will be sized to this rectangle.
     */
    public PhotoUpdateThread(Controller controller, ConfigOptions config, ModelData data, int sleepTime, BufferedImage buffer) {
        super(controller, config, data, sleepTime);
        this.buffer = buffer;

        if (config.isDisableCaching()) {
            Log.info("Image caching is disabled");
            doNotCache = true;
        }

        filler = Factory.getFiller(config);
        scaler = Factory.getScaler(config);

        if (filler == null || scaler == null) {
            Log.warning("Image scaler or background filler could not be created");
            doNotRun();
        }
        else {
            filler.initialise(buffer, config);
            scaler.initialise(buffer, config);

            try {
                imageDirectory = new ImageDirectory(config.getImageDirectory(), config.getCacheDirectory(), controller);
                imageDirectory.startWatching();
            } catch (Exception e) {
                Log.error("Could not start watching photo directory", e);
                doNotRun();
            }
        }
    }

    @Override
    public void doUpdate() {

        File f = imageDirectory.nextFile();
        File c = imageDirectory.getCachedImageFile(f);

        // display cached file if it exists
        if (!doNotCache && imageDirectory.fileExists(c)) {
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
                filler.fillBackground(image);
                scaler.drawScaledImage(image);

                getData().setCurrentImage(buffer);
                getController().requestUpdate();

                // write cached image
                if (!doNotCache && c != null) {
                    ImageUtil.writeImage(c, buffer);
                }
            }
        }
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
