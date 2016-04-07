package net.igorkromin;

import org.bitpipeline.lib.owm.WeatherData;

import java.util.HashMap;
import java.util.List;

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

    public void setCode(WeatherConditionCodes code) {
        this.code = code.code;
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
