package net.igorkromin.jphotoframe.ui;

import net.igorkromin.jphotoframe.ConfigOptions;
import net.igorkromin.jphotoframe.ImageUtil;
import net.igorkromin.jphotoframe.weather.Weather;

import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Holds the runtime data for the app 'model'. The View class uses this data to update what's displayed on screen.
 */
public class ModelData {

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private Weather weather;
    private BufferedImage currentImage;

    public ModelData(ConfigOptions config) {
        dateFormat = new SimpleDateFormat(config.getDateFormat());
        timeFormat = new SimpleDateFormat(config.getTimeFormat());
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public void setCurrentImage(BufferedImage image) {
        currentImage = image;
    }


    public BufferedImage getCurrentImage() {
        if (currentImage == null) {
            currentImage = ImageUtil.getDefaultImage();
        }

        return currentImage;
    }

    public String getDateString() {
        Date dateTime = new Date();
        return dateFormat.format(dateTime);
    }

    public String getTimeString() {
        Date dateTime = new Date();
        return timeFormat.format(dateTime);
    }

    public Weather getWeather() {
        return weather;
    }
}
