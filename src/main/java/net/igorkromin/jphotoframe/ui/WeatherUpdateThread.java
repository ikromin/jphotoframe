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

package net.igorkromin.jphotoframe.ui;

import net.aksingh.owmjapis.DailyForecast;
import net.aksingh.owmjapis.OpenWeatherMap;
import net.igorkromin.jphotoframe.ConfigOptions;
import net.igorkromin.jphotoframe.Log;
import net.igorkromin.jphotoframe.weather.Weather;

public class WeatherUpdateThread extends DataUpdateThread {

    public WeatherUpdateThread(Controller controller, ConfigOptions config, ModelData data, int sleepTime) {
        super(controller, config, data, sleepTime);
    }

    @Override
    public void doUpdate() {
        Log.verbose("Getting weather data");

        ConfigOptions config = getConfig();
        ModelData data = getData();

        OpenWeatherMap.Units units = (config.getWeatherUnits().equals(ConfigOptions.DEFAULT_WEATHER_UNITS) == true)
                ?  OpenWeatherMap.Units.METRIC
                : OpenWeatherMap.Units.IMPERIAL;
        OpenWeatherMap owm = new OpenWeatherMap(units, config.getWeatherApiKey());

        try {
            DailyForecast forecast = owm.dailyForecastByCityName(config.getWeatherCity(), (byte) config.getWeatherForecastDays());

            if (forecast.hasCityInstance() && forecast.hasForecastCount()) {
                data.setWeather(new Weather(forecast));
                Log.verbose("Got updated weather data");
            }
        }
        catch (Exception e) {
            data.setWeather(Weather.getNoConnectionDummyForecast());
            Log.error("Could not fetch weather forecast, error: " + e.getMessage(), e);
        }
        finally {
            getController().requestUpdate();
        }
    }

}
