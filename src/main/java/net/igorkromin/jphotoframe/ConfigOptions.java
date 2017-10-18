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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static net.igorkromin.jphotoframe.ConfigDefaults.*;

/**
 * Creates the runtime configuration model either from file or based on default values in 
 */
public class ConfigOptions {

    private static final String PROP_DEVICE_NUM = "screenNumber";
    private static final String PROP_IMG_TIME = "imageTimeout";
    private static final String PROP_IMG_DIRECTORY = "imageDirectory";
    private static final String PROP_CACHE_DIRECTORY = "cacheDirectory";
    private static final String PROP_FORMAT_TIME = "timeFormat";
    private static final String PROP_FORMAT_DATE = "dateFormat";
    private static final String PROP_WEATHER_FORECAST_DAYS = "weatherForecastDays";
    private static final String PROP_BG_PERCENT = "backgroundSourcePercent";
    private static final String PROP_BG_OPACITY = "backgroundOpacity";
    private static final String PROP_SHOW_WEATHER = "showWeather";
    private static final String PROP_WEATHER_CITY = "weatherCity";
    private static final String PROP_FULL_SCREEN_WINDOW = "fullScreenWindow";
    private static final String PROP_WEATHER_UPDATE_TIME = "weatherUpdateTime";
    private static final String PROP_WEATHER_API_KEY = "owmApiKey";
    private static final String PROP_WEATHER_UNITS = "weatherUnits";
    private static final String PROP_LAYOUT_FILE = "layout";

    private boolean showWeather, fullScreenWindow;
    private int gfxDeviceNum, imageTimeout;
    private int weatherForecastDays, weatherUpdateTime;
    private float bgPercent, bgOpacity;
    private String weatherCity, imageDirectory, cacheDirectory, dateFormat;
    private String timeFormat, weatherApiKey, weatherUnits, layoutFile;


    public ConfigOptions(String configFileName)
    {
        Properties props = new Properties();

        try {

            if (configFileName == null) {
                Log.info("Using default configuration");
            }
            else {
                Log.info("Loading configuration properties from file: " + configFileName);
                File configFile = new File(configFileName);
                props.load(new FileInputStream(configFile));
            }

            showWeather = Boolean.parseBoolean(getValue(props, PROP_SHOW_WEATHER, DEFAULT_SHOW_WEATHER));
            fullScreenWindow = Boolean.parseBoolean(getValue(props, PROP_FULL_SCREEN_WINDOW, DEFAULT_FULL_SCREEN_WINDOW));

            gfxDeviceNum = Integer.parseInt(getValue(props, PROP_DEVICE_NUM, DEFAULT_DEVICE_NUM));
            imageTimeout = Integer.parseInt(getValue(props, PROP_IMG_TIME, DEFAULT_IMG_TIME));
            weatherForecastDays = Integer.parseInt(getValue(props, PROP_WEATHER_FORECAST_DAYS, DEFAULT_WEATHER_FORECAST_DAYS));
            weatherUpdateTime = Integer.parseInt(getValue(props, PROP_WEATHER_UPDATE_TIME, DEFAULT_WEATHER_UPDATE_TIME));

            bgPercent = Float.parseFloat(getValue(props, PROP_BG_PERCENT, DEFAULT_BG_PERCENT));
            bgOpacity = Float.parseFloat(getValue(props, PROP_BG_OPACITY, DEFAULT_BG_OPACITY));

            weatherCity = getValue(props, PROP_WEATHER_CITY, DEFAULT_WEATHER_CITY);
            imageDirectory = getValue(props, PROP_IMG_DIRECTORY, DEFAULT_IMG_DIRECTORY);
            cacheDirectory = getValue(props, PROP_CACHE_DIRECTORY, DEFAULT_CACHE_DIRECTORY);
            dateFormat = getValue(props, PROP_FORMAT_DATE, DEFAULT_FORMAT_DATE);
            timeFormat = getValue(props, PROP_FORMAT_TIME, DEFAULT_FORMAT_TIME);
            weatherApiKey = getValue(props, PROP_WEATHER_API_KEY, DEFAULT_WEATHER_API_KEY);
            weatherUnits = getValue(props, PROP_WEATHER_UNITS, DEFAULT_WEATHER_UNITS);
            layoutFile = getValue(props, PROP_LAYOUT_FILE, DEFAULT_LAYOUT_FILE);

            int dwut = Integer.parseInt(DEFAULT_WEATHER_UPDATE_TIME);
            if (weatherUpdateTime < dwut) {
                Log.warning("Weather update time less than 10 mins, forcing to 10 mins");
                weatherUpdateTime = dwut;
            }

            int dwfd = Integer.parseInt(DEFAULT_WEATHER_FORECAST_DAYS);
            if (weatherForecastDays > dwfd) {
                Log.warning("Weather forecast days exceeded, forcing to " + dwfd + " days");
                weatherForecastDays = dwfd;
            }

            if (showWeather && weatherApiKey.equals(DEFAULT_WEATHER_API_KEY)) {
                Log.warning("Weather API key is not set, weather will be disabled");
                showWeather = false;
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Cannot read configuration file: " + configFileName);
        }
        catch (Exception e) {
            throw new RuntimeException("Invalid configuration detected: " + e.getMessage());
        }
    }

    public int getWeatherForecastDays() {
        return weatherForecastDays;
    }

    private String getValue(Properties props, String key, String defaultValue) {
        if (props.containsKey(key)) {
            String value = (String) props.get(key);
            Log.config(key + " = " + value);

            return value;
        }
        else {
            if (defaultValue != null) {
                Log.config(key + " = " + defaultValue, true);
                return defaultValue;
            }
            else {
                throw new RuntimeException("Required property not found: " + key);
            }
        }
    }

    public String getWeatherCity() {
        return weatherCity;
    }

    public boolean isFullScreenWindow() {
        return fullScreenWindow;
    }

    public int getGfxDeviceNum() {
        return gfxDeviceNum;
    }

    public String getImageDirectory() {
        return imageDirectory;
    }

    public int getImageTimeout() {
        return imageTimeout;
    }

    public String getCacheDirectory() {
        return cacheDirectory;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public float getBackgroundPercent() {
        return bgPercent;
    }

    public float getBackgroundOpacity() {
        return bgOpacity;
    }

    public int getWeatherUpdateTime() {
        return weatherUpdateTime;
    }

    public String getWeatherApiKey() {
        return weatherApiKey;
    }

    public String getWeatherUnits() {
        return weatherUnits;
    }

    public String getLayoutFile() {
        return layoutFile;
    }

}
