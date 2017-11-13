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

/**
 * Image processor Factory. Creates image scaler and background filler instances.
 */
public class Factory {

    public static final String SCALER_COVER_ASPECT = "CoverAspect";
    public static final String SCALER_CONTAIN_ASPECT = "ContainAspect";

    public static final String FILLER_STRETCH_SCALE = "StretchScale";
    public static final String FILLER_BLACK = "Black";


    /**
     * Creates a scaler as defined in configuration.
     * @param config
     * @return
     */
    public static ImageScaler getScaler(ConfigOptions config) {
        ImageScaler scaler = null;
        String configScaler = config.getImgScaler();

        Log.verbose("Scaler requested: " + configScaler);

        if (SCALER_COVER_ASPECT.equals(configScaler)) {
            scaler = new CoverAspectScaler();
        }
        else if (SCALER_CONTAIN_ASPECT.equals(configScaler)) {
            scaler = new ContainAspectScaler();
        }

        return scaler;
    }

    /**
     * Creates a filler as defined in configuration.
     * @param config
     * @return
     */
    public static BackgroundFiller getFiller(ConfigOptions config) {
        BackgroundFiller filler = null;
        String configFiller = config.getBackgroundFiller();

        Log.verbose("Filler requested: " + configFiller);

        if (FILLER_BLACK.equals(configFiller)) {
            filler = new BlackFiller();
        }
        else if (FILLER_STRETCH_SCALE.equals(configFiller)) {
            filler = new StretchScaleFiller();
        }

        return filler;
    }

}
