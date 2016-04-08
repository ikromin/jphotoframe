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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by ikromin on 29/08/2015.
 */
public class ConfigOptions {

    private static final String PROP_DEVICE_NUM = "screenNumber";
    private static final String PROP_IMG_TIME = "imageTimeout";
    private static final String PROP_IMG_DIRECTORY = "imageDirectory";
    private static final String PROP_CACHE_DIRECTORY = "cacheDirectory";
    private static final String PROP_FONT_NAME = "fontName";
    private static final String PROP_FONT_SIZE_DATE = "fontSizeDate";
    private static final String PROP_FONT_SIZE_TIME = "fontSizeTime";
    private static final String PROP_FONT_SIZE_WEATHER_CONDITION = "fontSizeWeatherCondition";
    private static final String PROP_FONT_SIZE_WEATHER_FORECAST = "fontSizeWeatherForecast";
    private static final String PROP_FONT_SIZE_LOCATION = "fontSizeLocation";
    private static final String PROP_FORMAT_TIME = "timeFormat";
    private static final String PROP_FORMAT_DATE = "dateFormat";
    private static final String PROP_DATE_OFFSET_X = "dateOffsetX";
    private static final String PROP_DATE_OFFSET_Y = "dateOffsetY";
    private static final String PROP_TIME_OFFSET_X = "timeOffsetX";
    private static final String PROP_TIME_OFFSET_Y = "timeOffsetY";
    private static final String PROP_WEATHER_OFFSET_X = "weatherOffsetX";
    private static final String PROP_WEATHER_CITY_OFFSET_Y = "weatherCityOffsetY";
    private static final String PROP_WEATHER_CONDITION_OFFSET_Y = "weatherConditionOffsetY";
    private static final String PROP_WEATHER_FORECAST_DAYTEMP_OFFSET_Y = "weatherForecastDayTempOffsetY";
    private static final String PROP_WEATHER_FORECAST_CONDITION_OFFSET_Y = "weatherForecastConditionOffsetY";
    private static final String PROP_WEATHER_DAY_WIDTH = "weatherDayWidth";
    private static final String PROP_WEATHER_FORECAST_DAYS = "weatherForecastDays";
    private static final String PROP_TEXT_COLOR = "textColor";
    private static final String PROP_TEXT_OUTLINE_COLOR = "textOutlineColor";
    private static final String PROP_TEXT_OUTLINE_WIDTH = "textOutlineWidth";
    private static final String PROP_BG_PERCENT = "backgroundSourcePercent";
    private static final String PROP_BG_OPACITY = "backgroundOpacity";
    private static final String PROP_SHOW_WEATHER = "showWeather";
    private static final String PROP_WEATHER_CITY = "weatherCity";
    private static final String PROP_FULL_SCREEN_WINDOW = "fullScreenWindow";
    private static final String PROP_WEATHER_UPDATE_TIME = "weatherUpdateTime";

    private int gfxDeviceNum;
    private int imageTimeout;
    private int fontSizeDate;
    private int fontSizeTime;
    private int dateOffsetX;
    private int dateOffsetY;
    private int timeOffsetX;
    private int textOutlineWidth;
    private int timeOffsetY;
    private String weatherCity;
    private int[] textColor;
    private int[] textOutlineColor;
    private String imageDirectory;
    private String cacheDirectory;
    private String fontName;
    private String dateFormat;
    private String timeFormat;
    private float bgPercent;
    private float bgOpacity;
    private boolean showWeather;
    private boolean fullScreenWindow;
    private float fontSizeWeatherCondition;
    private int fontSizeWeatherForecast;
    private int fontSizeLocation;
    private int weatherOffsetX;
    private int weatherCityOffsetY;
    private int weatherConditionOffsetY;
    private int weatherForecastDayTempOffsetY;
    private int weatherForecastConditionOffsetY;
    private int weatherDayWidth;
    private int weatherForecastDays;
    private int weatherUpdateTime;


    public ConfigOptions(File configFile)
    {
        System.out.println("Loading configuration properties");

        try {
            Properties props = new Properties();
            props.load(new FileInputStream(configFile));

            gfxDeviceNum = Integer.parseInt(getValue(props, PROP_DEVICE_NUM, ConfigDefaults.DEFAULT_DEVICE_NUM));
            imageTimeout = Integer.parseInt(getValue(props, PROP_IMG_TIME, ConfigDefaults.DEFAULT_IMG_TIME));
            fontSizeDate = Integer.parseInt(getValue(props, PROP_FONT_SIZE_DATE, ConfigDefaults.DEFAULT_FONT_SIZE_DATE));
            fontSizeTime = Integer.parseInt(getValue(props, PROP_FONT_SIZE_TIME, ConfigDefaults.DEFAULT_FONT_SIZE_TIME));
            dateOffsetX = Integer.parseInt(getValue(props, PROP_DATE_OFFSET_X, ConfigDefaults.DEFAULT_DATE_OFFSET_X));
            dateOffsetY = Integer.parseInt(getValue(props, PROP_DATE_OFFSET_Y, ConfigDefaults.DEFAULT_DATE_OFFSET_Y));
            timeOffsetX = Integer.parseInt(getValue(props, PROP_TIME_OFFSET_X, ConfigDefaults.DEFAULT_TIME_OFFSET_X));
            timeOffsetY = Integer.parseInt(getValue(props, PROP_TIME_OFFSET_Y, ConfigDefaults.DEFAULT_TIME_OFFSET_Y));
            textOutlineWidth = Integer.parseInt(getValue(props, PROP_TEXT_OUTLINE_WIDTH, ConfigDefaults.DEFAULT_TEXT_OUTLINE_WIDTH));
            weatherCity = getValue(props, PROP_WEATHER_CITY, ConfigDefaults.DEFAULT_WEATHER_CITY);
            imageDirectory = getValue(props, PROP_IMG_DIRECTORY, null);
            cacheDirectory = getValue(props, PROP_CACHE_DIRECTORY, null);
            fontName = getValue(props, PROP_FONT_NAME, ConfigDefaults.DEFAULT_FONT_NAME);
            dateFormat = getValue(props, PROP_FORMAT_DATE, ConfigDefaults.DEFAULT_FORMAT_DATE);
            timeFormat = getValue(props, PROP_FORMAT_TIME, ConfigDefaults.DEFAULT_FORMAT_TIME);
            textColor = getRgb(getValue(props, PROP_TEXT_COLOR, ConfigDefaults.DEFAULT_TEXT_COLOR));
            textOutlineColor = getRgb(getValue(props, PROP_TEXT_OUTLINE_COLOR, ConfigDefaults.DEFAULT_TEXT_OUTLINE_COLOR));
            bgPercent = Float.parseFloat(getValue(props, PROP_BG_PERCENT, ConfigDefaults.DEFAULT_BG_PERCENT));
            bgOpacity = Float.parseFloat(getValue(props, PROP_BG_OPACITY, ConfigDefaults.DEFAULT_BG_OPACITY));
            showWeather = Boolean.parseBoolean(getValue(props, PROP_SHOW_WEATHER, ConfigDefaults.DEFAULT_SHOW_WEATHER));
            fullScreenWindow = Boolean.parseBoolean(getValue(props, PROP_FULL_SCREEN_WINDOW, ConfigDefaults.DEFAULT_FULL_SCREEN_WINDOW));
            fontSizeWeatherCondition = Float.parseFloat(getValue(props, PROP_FONT_SIZE_WEATHER_CONDITION, ConfigDefaults.DEFAULT_FONT_SIZE_WEATHER_CONDITION));
            fontSizeWeatherForecast = Integer.parseInt(getValue(props, PROP_FONT_SIZE_WEATHER_FORECAST, ConfigDefaults.DEFAULT_FONT_SIZE_WEATHER_FORECAST));
            fontSizeLocation = Integer.parseInt(getValue(props, PROP_FONT_SIZE_LOCATION, ConfigDefaults.DEFAULT_FONT_SIZE_LOCATION));
            weatherOffsetX = Integer.parseInt(getValue(props, PROP_WEATHER_OFFSET_X, ConfigDefaults.DEFAULT_WEATHER_OFFSET_X));
            weatherCityOffsetY = Integer.parseInt(getValue(props, PROP_WEATHER_CITY_OFFSET_Y, ConfigDefaults.DEFAULT_WEATHER_CITY_OFFSET_Y));
            weatherConditionOffsetY = Integer.parseInt(getValue(props, PROP_WEATHER_CONDITION_OFFSET_Y, ConfigDefaults.DEFAULT_WEATHER_CONDITION_OFFSET_Y));
            weatherForecastDayTempOffsetY = Integer.parseInt(getValue(props, PROP_WEATHER_FORECAST_DAYTEMP_OFFSET_Y, ConfigDefaults.DEFAULT_WEATHER_FORECAST_DAYTEMP_OFFSET_Y));
            weatherForecastConditionOffsetY = Integer.parseInt(getValue(props, PROP_WEATHER_FORECAST_CONDITION_OFFSET_Y, ConfigDefaults.DEFAULT_WEATHER_FORECAST_CONDITION_OFFSET_Y));
            weatherDayWidth = Integer.parseInt(getValue(props, PROP_WEATHER_DAY_WIDTH, ConfigDefaults.DEFAULT_WEATHER_DAY_WIDTH));
            weatherForecastDays = Integer.parseInt(getValue(props, PROP_WEATHER_FORECAST_DAYS, ConfigDefaults.DEFAULT_WEATHER_FORECAST_DAYS));
            weatherUpdateTime = Integer.parseInt(getValue(props, PROP_WEATHER_UPDATE_TIME, ConfigDefaults.DEFAULT_WEATHER_UPDATE_TIME));

            int dwut = Integer.parseInt(ConfigDefaults.DEFAULT_WEATHER_UPDATE_TIME);
            if (weatherUpdateTime < dwut) {
                System.out.println("Weather update time less than 10 mins, forcing to 10 mins");
                weatherUpdateTime = dwut;
            }

            int dwfd = Integer.parseInt(ConfigDefaults.DEFAULT_WEATHER_FORECAST_DAYS);
            if (weatherForecastDays > dwfd) {
                System.out.println("Weather forecast days exceeded, forcing to " + dwfd + " days");
                weatherForecastDays = dwfd;
            }

        }
        catch (IOException e) {
            throw new RuntimeException("Cannot read configuration file: " + configFile.getAbsolutePath());
        }
        catch (Exception e) {
            throw new RuntimeException("Invalid configuration detected: " + e.getMessage());
        }
    }

    public int getFontSizeLocation() {
        return fontSizeLocation;
    }

    public float getFontSizeWeatherCondition() {
        return fontSizeWeatherCondition;
    }

    public int getFontSizeWeatherForecast() {
        return fontSizeWeatherForecast;
    }

    public int getWeatherOffsetX() {
        return weatherOffsetX;
    }

    public int getWeatherCityOffsetY() {
        return weatherCityOffsetY;
    }

    public int getWeatherConditionOffsetY() {
        return weatherConditionOffsetY;
    }

    public int getWeatherForecastDayTempOffsetY() {
        return weatherForecastDayTempOffsetY;
    }

    public int getWeatherForecastConditionOffsetY() {
        return weatherForecastConditionOffsetY;
    }

    public int getWeatherDayWidth() {
        return weatherDayWidth;
    }

    public int getWeatherForecastDays() {
        return weatherForecastDays;
    }

    private int[] getRgb(String value) {
        String[] strArr = value.split(",");

        if (strArr.length != 3) {
            throw new RuntimeException("Expected 3 comma separated numbers, for input: " + value);
        }

        int[] rgb = new int[3];
        rgb[0] = Integer.parseInt(strArr[0]);
        rgb[1] = Integer.parseInt(strArr[1]);
        rgb[2] = Integer.parseInt(strArr[2]);

        return rgb;
    }

    private String getValue(Properties props, String key, String defaultValue) {
        if (props.containsKey(key)) {
            String value = (String) props.get(key);
            System.out.println("[CONFIG]  " + key + " = " + value);

            return value;
        }
        else {
            if (defaultValue != null) {
                System.out.println("[DEFAULT] " + key + " = " + defaultValue);
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

    public boolean isShowWeather() {
        return showWeather;
    }

    public boolean isFullScreenWindow() {
        return fullScreenWindow;
    }

    public int getTextOutlineWidth() {
        return textOutlineWidth;
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

    public int getFontSizeDate() {
        return fontSizeDate;
    }

    public int getFontSizeTime() {
        return fontSizeTime;
    }

    public String getFontName() {
        return fontName;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public int getDateOffsetX() {
        return dateOffsetX;
    }

    public int getDateOffsetY() {
        return dateOffsetY;
    }

    public int getTimeOffsetX() {
        return timeOffsetX;
    }

    public int getTimeOffsetY() {
        return timeOffsetY;
    }

    public int[] getTextColor() {
        return textColor;
    }

    public int[] getTextOutlineColor() {
        return textOutlineColor;
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
}
