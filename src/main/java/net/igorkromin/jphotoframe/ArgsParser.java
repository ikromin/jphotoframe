package net.igorkromin.jphotoframe;

/**
 * This class checks command line arguments and sets appropriate internal properties.
 */
public class ArgsParser {

    private static final String ARG_VAL_FIX_ROTATION = "-fixrotation";
    private static final String ARG_VAL_VERBOSE = "-verbose";

    private boolean fixRotation = false;
    private boolean verboseLog = false;
    private String configPath = null;

    public ArgsParser(String[] args) {

        for (String arg : args) {
            if (ARG_VAL_FIX_ROTATION.equals(arg)) {
                fixRotation = true;
            }
            else if (ARG_VAL_VERBOSE.equals(arg)) {
                verboseLog = true;
            }
            else {
                configPath = arg;
            }
        }

        if (fixRotation && configPath == null) {
            throw new RuntimeException("Please specify a photos directory for the rotation fix utility");
        }
    }

    public boolean fixRotation() {
        return fixRotation;
    }

    public boolean verboseLog() {
        return verboseLog;
    }

    public String configPath() {
        return configPath;
    }

}
