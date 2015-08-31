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

package net.igorkromin;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by ikromin on 29/08/2015.
 */
public class PhotoFrame {

    private static final String DEFAULT_CONFIG_FILE = "config.properties";
    private static final int RET_STATUS_HEADLESS_ERR = 1;
    private static final int RET_STATUS_CONFIG_ERR = 2;

    public static void main(String args[]) throws IOException {

        File configFile;
        ConfigOptions config = null;

        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Cannot run in headless mode");
            System.exit(RET_STATUS_HEADLESS_ERR);
        }

        if (args.length > 0) {
            configFile = new File(args[0]);
        }
        else {
            configFile = new File(DEFAULT_CONFIG_FILE);
        }

        try {
            config = new ConfigOptions(configFile);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(RET_STATUS_CONFIG_ERR);
        }

        View frame = new View(config);
        Controller controller = new Controller(config, frame);

        controller.start();
    }

}
