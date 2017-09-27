package net.igorkromin.jphotoframe.ui;

import net.igorkromin.jphotoframe.ImageUtil;
import net.igorkromin.jphotoframe.weather.Weather;

import java.awt.image.BufferedImage;

/**
 * Holds the runtime data for the app 'model'. The View class uses this data to update what's displayed on screen.
 */
public class ModelData {

    private String date;
    private String time;
    private Weather weather;
    private BufferedImage currentImage;

    private boolean changed = true; // TODO: change to false once the View class is fully updated

    public boolean hasChanged() {
        return changed;
    }

    public void resetChange() {
        changed = false;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
        changed = true;
    }

    public void setCurrentImage(BufferedImage image) {
        currentImage = image;
        changed = true;
    }

    public void setDateTime(String date, String time) {
        this.date = date;
        this.time = time;
        changed = true;
    }

    public BufferedImage getCurrentImage() {
        if (currentImage == null) {
            currentImage = ImageUtil.getDefaultImage();
        }

        return currentImage;
    }

    public String getDateString() {
        return date;
    }

    public String getTimeString() {
        return time;
    }

    public Weather getWeather() {
        return weather;
    }
}
