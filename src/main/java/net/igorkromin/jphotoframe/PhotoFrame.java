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

import net.igorkromin.jphotoframe.ui.Controller;
import net.igorkromin.jphotoframe.ui.View;

/**
 * Entry point to the JPhotoFrame App. This class is mostly responsible for loading the config and then creating
 * either the rotation utility class or the controller class for the photo frame.
 */
public class PhotoFrame {

    private static final int RET_STATUS_START_ERR = 1;

    public static void main(String args[]) {

        try {
            ArgsParser argsParser = new ArgsParser(args);

            // set log verbosity if required
            if (argsParser.verboseLog()) {
                Log.setVerbose(true);
            }

            // pick between the rotation utility or the normal photo frame operation
            if (argsParser.fixRotation()) {
                RotationFixer rotationFixer = new RotationFixer(argsParser.configPath());
                rotationFixer.start();
            }
            else {
                ConfigOptions config = new ConfigOptions((argsParser.configPath() != null) ? argsParser.configPath() : null);

                View frame = new View(config);
                Controller controller = new Controller(config, frame);
                controller.start();
            }
        }
        catch (Exception e) {
            Log.error("Could not run application due to error: " + e.getMessage(), e);
            System.exit(RET_STATUS_START_ERR);
        }
    }

}
