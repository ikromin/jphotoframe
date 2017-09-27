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

    public DataUpdateThread(Controller controller, ConfigOptions config, ModelData data, int sleepTime) {
        this.controller = controller;
        this.config = config;
        this.data = data;
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        Log.verbose("Starting data update thread");

        while (!controller.isStopping()) {
            controller.waitIfPaused();
            doUpdate();

            try {
                sleep(sleepTime);
            } catch (InterruptedException e) {
                Log.warning("Couldn't put thread to sleep");
            }
        }

        Log.verbose("Exiting data update thread");
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

    public abstract void doUpdate();

}
