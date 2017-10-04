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

/**
 * Simple 'logger' class that outputs messages to the standard output or standard error.
 * The verbosity of the output is configurable.
 */
public class Log {

    private static boolean isVerbose = false;

    public static void setVerbose(boolean isVerbose) {
        Log.isVerbose = isVerbose;
    }

    public static void config(String msg) {
        config(msg, false);
    }

    public static void config(String msg, boolean isDefault) {
        if (isDefault) {
            if (isVerbose) {
                System.out.println("Config: [DEFAULT] " + msg);
            }
        }
        else {
            System.out.println("Config: [LOADED ] " + msg);
        }
    }

    public static void verbose(String msg) {
        if (!isVerbose) {
            return;
        }
        System.out.println("Verbose: " + msg);
    }

    public static void info(String msg) {
        System.out.println(msg);
    }

    public static void warning(String msg) {
        System.out.println("Warning: " + msg);
    }

    public static void error(String msg, Throwable e) {
        System.err.println("Error: " + msg);
        e.printStackTrace();
    }

}
