package net.igorkromin;

import net.aksingh.owmjapis.AbstractForecast;
import net.aksingh.owmjapis.DailyForecast;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ikromin on 28/03/2016.
 */
public class Weather {

    private static Calendar cal = Calendar.getInstance();
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    private static HashMap<Integer, String> dayOfWeek = new HashMap<>();
    private static Weather noConnectionForecast = null;

    static {
        dayOfWeek.put(Calendar.MONDAY, "MON");
        dayOfWeek.put(Calendar.TUESDAY, "TUE");
        dayOfWeek.put(Calendar.WEDNESDAY, "WED");
        dayOfWeek.put(Calendar.THURSDAY, "THU");
        dayOfWeek.put(Calendar.FRIDAY, "FRI");
        dayOfWeek.put(Calendar.SATURDAY, "SAT");
        dayOfWeek.put(Calendar.SUNDAY, "SUN");
    }

    private String city;
    private String country;
    private List<Forecast> forecasts;

    private Weather() {
        // private constructor for no-connection forecast static
    }

    public Weather(DailyForecast forecast) {
        if (forecast.hasCityInstance()) {
            AbstractForecast.City city = forecast.getCityInstance();

            if (city.hasCityName()) {
                this.city = city.getCityName();
            }

            if (city.hasCountryCode()) {
                this.country = city.getCountryCode();
            }
        }

        if (forecast.hasForecastCount()) {
            //List<ForecastWeatherData> forecasts = forecast.getForecasts();

            this.forecasts = new ArrayList<>();
            HashMap<String, Forecast> forecastMap = new HashMap<>();

            for (int i = 0; i < forecast.getForecastCount(); i++) {
            //for (ForecastWeatherData fwd : forecasts) {
                DailyForecast.Forecast fwd = forecast.getForecastInstance(i);

                Date dt = fwd.getDateTime();
                String date = sdf.format(dt);

                Forecast f;
                if (!forecastMap.containsKey(date)) {
                    f = new Forecast();
                    forecastMap.put(date, f);
                    this.forecasts.add(f);

                    // Set the forecast day of week e.g. MON
                    cal.setTime(dt);
                    f.setDay(dayOfWeek.get(cal.get(Calendar.DAY_OF_WEEK)));
                }
                else {
                    f = forecastMap.get(date);
                }

                DailyForecast.Forecast.Temperature temperature = fwd.getTemperatureInstance();

                //int temp = (int) fwd.getTemperatureInstance().get getTemp();
                //if (temp < f.getLow()) {
                //    f.setLow(temp);
                //}
                f.setLow((int) temperature.getMinimumTemperature());

                //if (temp > f.getHigh()) {
                //    f.setHigh(temp);
                //}
                f.setHigh((int) temperature.getMaximumTemperature());

                // TODO: get back to multiple weather instances
                f.setConditions(fwd.getWeatherInstance(0).getWeatherCode());
            }
        }

        //for (Forecast f : this.forecasts) {
        //    f.calcCondition();
        //}
    }

    public List<Forecast> getForecast() {
        return forecasts;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    /**
     * Creates a dummy forecast that shows that there is no Internet connection.
     * @return
     */
    public static Weather getNoConnectionDummyForecast() {
        if (noConnectionForecast == null) {
            Forecast ncForecast = new Forecast();
            ncForecast.setDay("Now");
            ncForecast.setHigh(0);
            ncForecast.setLow(0);
            ncForecast.setCode(WeatherConditionCodes.NO_CONNECTION);

            ArrayList<Forecast> ncForecasts = new ArrayList<>(1);
            ncForecasts.add(ncForecast);

            noConnectionForecast = new Weather();
            noConnectionForecast.city = "Error";
            noConnectionForecast.country = "No Connection";
            noConnectionForecast.forecasts = ncForecasts;
        }

        return noConnectionForecast;
    }

}
