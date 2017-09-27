package net.igorkromin.jphotoframe.ui;

import net.aksingh.owmjapis.DailyForecast;
import net.aksingh.owmjapis.OpenWeatherMap;
import net.igorkromin.jphotoframe.ConfigDefaults;
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

        OpenWeatherMap.Units units = (config.getWeatherUnits().equals(ConfigDefaults.DEFAULT_WEATHER_UNITS) == true)
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
