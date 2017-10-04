package net.igorkromin.jphotoframe.ui;

import net.igorkromin.jphotoframe.ConfigOptions;
import net.igorkromin.jphotoframe.Log;

/**
 * Abstracts the data update/sleep cycle into its own class and provides the necessary accessors for the data model.
 */
public abstract class DataUpdateThread extends Thread {

    private Controller controller;
    private ConfigOptions config;
    private ModelData data;
    private int sleepTime;
    private boolean doNotRun = false;

    public DataUpdateThread(Controller controller, ConfigOptions config, ModelData data, int sleepTime) {
        this.controller = controller;
        this.config = config;
        this.data = data;
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        // check if the 'do not run' flag is set and exit if it is
        if (doNotRun) {
            Log.verbose("Early thread exit");
            return;
        }

        Log.verbose("Starting data update thread");

        while (!controller.isStopping()) {
            controller.waitIfPaused();
            long startTime = System.currentTimeMillis();

            try {
                doUpdate();
            }
            catch (Exception e) {
                Log.error("Error in data update thread", e);
            }

            long elapsedTime = System.currentTimeMillis() - startTime;
            long actualSleep = sleepTime - elapsedTime;

            if (actualSleep > 0) {
                trySleep(actualSleep);
            }
            else {
                Log.warning("Data update thread took longer to update than sleep interval");
            }
        }

        try {
            beforeExit();
        }
        catch (Exception e) {
            Log.error("Error before thread exit", e);
        }

        Log.verbose("Exiting data update thread");
    }

    /**
     * Puts the thread to sleep for a specified period. Any exceptions are caught and ignored.
     * @param millis
     */
    private void trySleep(long millis) {
        try {
            sleep(millis);
        } catch (InterruptedException e) {
            if (!controller.isStopping()) {
                Log.warning("Couldn't put thread to sleep");
            }
        }
    }

    protected Controller getController() {
        return controller;
    }

    protected ConfigOptions getConfig() {
        return config;
    }

    protected ModelData getData() {
        return data;
    }

    protected void doNotRun() {
        doNotRun = true;
    }

    public abstract void doUpdate();

    /**
     * Called before the thread terminates. This implementation doesn't do anything so concrete implementing classes
     * need to override this method.
     */
    public void beforeExit() {}

}
