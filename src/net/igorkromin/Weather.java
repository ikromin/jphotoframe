package net.igorkromin;

import org.bitpipeline.lib.owm.ForecastWeatherData;
import org.bitpipeline.lib.owm.WeatherData;
import org.bitpipeline.lib.owm.WeatherForecastResponse;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ikromin on 28/03/2016.
 */
public class Weather {

    private static Calendar cal = Calendar.getInstance();
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    private static HashMap<Integer, String> dayOfWeek = new HashMap<>();

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

    public Weather(WeatherForecastResponse forecast) {
        if (forecast.hasCity()) {
            WeatherForecastResponse.City city = forecast.getCity();

            if (city.hasName()) {
                this.city = city.getName();
            }

            if (city.hasCountryCode()) {
                this.country = city.getCountryCode();
            }
        }

        if (forecast.hasForecasts()) {
            List<ForecastWeatherData> forecasts = forecast.getForecasts();

            this.forecasts = new ArrayList<>();
            HashMap<String, Forecast> forecastMap = new HashMap<>();

            for (ForecastWeatherData fwd : forecasts) {
                Date dt = new Date(fwd.getDateTime() * 1000);
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

                int temp = (int) fwd.getTemp();
                if (temp < f.getLow()) {
                    f.setLow(temp);
                }

                if (temp > f.getHigh()) {
                    f.setHigh(temp);
                }

                f.setConditions(fwd.getWeatherConditions());
            }
        }

        for (Forecast f : this.forecasts) {
            f.calcCondition();
        }
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
     * Data class to record forecast information for a single day.
     */
    public class Forecast {

        private String day;
        private int low = Integer.MAX_VALUE;
        private int high = Integer.MIN_VALUE;
        private int code = WeatherConditionCodes.NOT_AVAILABLE.code;
        private HashMap<WeatherData.WeatherCondition.ConditionCode, Integer> conditions = new HashMap<>();

        public String getDay() {
            return day;
        }

        public int getLow() {
            return low;
        }

        public int getHigh() {
            return high;
        }

        public int getCode() {
            return code;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public void setLow(int low) {
            this.low = low;
        }

        public void setHigh(int high) {
            this.high = high;
        }

        public void setConditions(List<WeatherData.WeatherCondition> weatherConditions) {
            if (weatherConditions == null || weatherConditions.size() < 1) {
                return;
            }

            WeatherData.WeatherCondition wc = weatherConditions.get(0);
            WeatherData.WeatherCondition.ConditionCode wcc = wc.getCode();

            int count = 0;
            if (conditions.containsKey(wcc)) {
                count = conditions.get(wcc);
            }

            count++;
            conditions.put(wcc, count);
        }

        /**
         * Determines the most frequently occurring weather condition code for this forecast day
         */
        public void calcCondition() {
            int maxCount = 0;
            int code = WeatherConditionCodes.NOT_AVAILABLE.code;

            for (WeatherData.WeatherCondition.ConditionCode wcc : conditions.keySet()) {
                int count = conditions.get(wcc);
                if (count > maxCount) {
                    maxCount = count;
                    code = wcc.getId();
                }
            }

            this.code = code;
        }
    }

}
