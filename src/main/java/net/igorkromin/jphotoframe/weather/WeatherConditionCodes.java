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

package net.igorkromin.jphotoframe.weather;

/**
 * OpenWeatherMap conditions and icons.
 * https://openweathermap.org/weather-conditions
 */
public enum WeatherConditionCodes {

    THUNDERSTORM_WITH_LIGHT_RAIN     (200,    0xf01e, "Thunder", "Thunder storm with light rain"),
    THUNDERSTORM_WITH_RAIN           (201,    0xf01e, "Thunder", "Thunder storm with rain"),
    THUNDERSTORM_WITH_HEAVY_RAIN     (202,    0xf01e, "Thunder", "Thunder storm with heavy rain"),
    LIGHT_THUNDERSTORM               (210,    0xf01e, "Thunder", "Light thunder storm"),
    THUNDERSTORM                     (211,    0xf01e, "Thunder", "Thunder storm"),
    HEAVY_THUNDERSTORM               (212,    0xf01e, "Thunder", "Heavy thunder storm"),
    RAGGED_THUNDERSTORM              (221,    0xf01e, "Thunder", "Ragged thunder storm"),
    THUNDERSTORM_WITH_LIGHT_DRIZZLE  (230,    0xf01e, "Thunder", "Thunder storm with light drizzle"),
    THUNDERSTORM_WITH_DRIZZLE        (231,    0xf01e, "Thunder", "Thunder storm with drizzle"),
    THUNDERSTORM_WITH_HEAVY_DRIZZLE  (232,    0xf01e, "Thunder", "Thunder storm with heavy drizzle"),

    LIGHT_INTENSITY_DRIZZLE          (300,    0xf04e, "Drizzle", "Light drizzle"),
    DRIZZLE                          (301,    0xf04e, "Drizzle", "Drizzle"),
    HEAVY_INTENSITY_DRIZZLE          (302,    0xf04e, "Drizzle", "Heavy drizzle"),
    LIGHT_INTENSITY_DRIZZLE_RAIN     (310,    0xf04e, "Drizzle", "Ligh drizzle and rain"),
    DRIZZLE_RAIN                     (311,    0xf04e, "Drizzle", "Drizzle and rain"),
    HEAVY_INTENSITY_DRIZZLE_RAIN     (312,    0xf04e, "Drizzle", "Heavy drizzle and rain"),
    SHOWER_RAIN_DRIZZLE              (313,    0xf04e, "Drizzle", "Shower, rain and drizzle"),
    HEAVY_SHOWER_RAIN_DRIZZLE        (314,    0xf04e, "Drizzle", "Heavy shower, rain and drizzle"),
    SHOWER_DRIZZLE                   (321,    0xf04e, "Drizzle", "Shower and drizzle"),

    LIGHT_RAIN                       (500,    0xf019, "Rain", "Light rain"),
    MODERATE_RAIN                    (501,    0xf019, "Rain", "Moderate rain"),
    HEAVY_INTENSITY_RAIN             (502,    0xf019, "Rain", "Heavy rain"),
    VERY_HEAVY_RAIN                  (503,    0xf019, "Rain", "Very heavy rain"),
    EXTREME_RAIN                     (504,    0xf019, "Rain", "Extreme rain"),
    FREEZING_RAIN                    (511,    0xf019, "Rain", "Freezing rain"),
    LIGHT_INTENSITY_SHOWER_RAIN      (520,    0xf019, "Rain", "Light shower rain"),
    SHOWER_RAIN                      (521,    0xf019, "Rain", "Shower rain"),
    HEAVY_INTENSITY_SHOWER_RAIN      (522,    0xf019, "Rain", "Heavy shower rain"),
    RAGGED_SHOWER_RAIN               (531,    0xf019, "Rain", "Ragged shower rain"),

    LIGHT_SNOW                       (600,    0xf01b, "Snow", "Light snow"),
    SNOW                             (601,    0xf01b, "Snow", "Snow"),
    HEAVY_SNOW                       (602,    0xf01b, "Snow", "Heavy snow"),
    SLEET                            (611,    0xf01b, "Snow", "Sleet"),
    SHOWER_SLEET                     (612,    0xf01b, "Snow", "Shower and sleet"),
    LIGHT_RAIN_SNOW                  (615,    0xf01b, "Snow", "Light rain and snow"),
    RAIN_SNOW                        (616,    0xf01b, "Snow", "Rain and snow"),
    LIGHT_SHOWER_SNOW                (620,    0xf01b, "Snow", "Light shower and snow"),
    SHOWER_SNOW                      (621,    0xf01b, "Snow", "Shower and snow"),
    HEAVY_SHOWER_SNOW                (622,    0xf01b, "Snow", "Heavy shower and snow"),

    MIST                             (701,    0xf014, "Mist", "Mist"),
    SMOKE                            (711,    0xf062, "Smoke", "Smoke"),
    HAZE                             (721,    0xf0b6, "Haze", "Haze"),
    SAND_OR_DUST_WHIRLS              (731,    0xf063, "Dust", "Sand or dust whirls"),
    FOG                              (741,    0xf014, "Fog", "Fog"),
    SAND                             (751,    0xf063, "Sand", "Sand"),
    DUST                             (761,    0xf063, "Dust", "Dust"),
    VOLCANIC_ASH                     (762,    0xf063, "Ash", "Volcanic ash"),
    TORNADO1                         (781,    0xf056, "Tornado", "Tornado"),

    SKY_IS_CLEAR                     (800,    0xf00d, "Clear", "Clear sky"),
    FEW_CLOUDS                       (801,    0xf013, "Clouds", "Few clouds"),
    SCATTERED_CLOUDS                 (802,    0xf013, "Clouds", "Scattered clouds"),
    BROKEN_CLOUDS                    (803,    0xf013, "Clouds", "Broken clouds"),
    OVERCAST_CLOUDS                  (804,    0xf013, "Clouds", "Overcast clouds"),

    TORNADO2                         (900,    0xf056, "Tornado", "Tornado"),
    TROPICAL_STORM                   (901,    0xf01d, "Storm", "Tropical storm"),
    HURRICANE                        (902,    0xf073, "Hurricane", "Hurricane"),
    COLD                             (903,    0xf076, "Cold", "Extreme cold"),
    HOT                              (904,    0xf072, "Hot", "Extreme heat"),
    WINDY                            (905,    0xf021, "Wind", "Extreme wind"),
    HAIL                             (906,    0xf015, "Hail", "Extreme hail"),

    NOT_AVAILABLE                    (3200, 0xf075, "N/A", "N/A"),

    NO_CONNECTION                    (-1, 0xf0c6, "No Connection", "No Connection");

    int code;
    int character;
    String info;
    String detailedInfo;

    WeatherConditionCodes(int code, int character, String info, String detailedInfo) {
        this.code = code;
        this.character = character;
        this.info = info;
        this.detailedInfo = detailedInfo;
    }

    public static WeatherConditionCodes fromInt(int code) {

        for (WeatherConditionCodes w : WeatherConditionCodes.values()) {
            if (w.code == code) {
                return w;
            }
        }

        return NOT_AVAILABLE;
    }

    public String getInfoText() {
        return info;
    }

    public String getDetailedInfo() {
        return detailedInfo;
    }

    public String toString() {
        return Character.toString((char) character);
    }

}
