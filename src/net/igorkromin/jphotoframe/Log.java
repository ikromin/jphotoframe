package net.igorkromin.jphotoframe;

/**
 * Simple 'logger' class that outputs messages to the standard output or standard error.
 * The verbosity of the output is configurable.
 */
public class Log {

    private static boolean isVerbose = true;

    public static void setVerbose(boolean isVerbose) {
        Log.isVerbose = isVerbose;
    }

    public static void config(String msg) {
        config(msg, false);
    }

    public static void config(String msg, boolean isDefault) {
        if (isDefault) {
            System.out.println("Config: [DEFAULT] " + msg);
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

    public static void error(String msg) {
        System.err.println("Error: " + msg);
    }

}
