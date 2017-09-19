package net.igorkromin.jphotoframe.weather;

/**
 * Maps the Yahoo! Weather codes to the weather icons font characters
 * https://developer.yahoo.com/weather/documentation.html#codes
 */
public enum WeatherConditionCodes {

    THUNDERSTORM_WITH_LIGHT_RAIN     (200,    0xf01e, "Thunder"),
    THUNDERSTORM_WITH_RAIN           (201,    0xf01e, "Thunder"),
    THUNDERSTORM_WITH_HEAVY_RAIN     (202,    0xf01e, "Thunder"),
    LIGHT_THUNDERSTORM               (210,    0xf01e, "Thunder"),
    THUNDERSTORM                     (211,    0xf01e, "Thunder"),
    HEAVY_THUNDERSTORM               (212,    0xf01e, "Thunder"),
    RAGGED_THUNDERSTORM              (221,    0xf01e, "Thunder"),
    THUNDERSTORM_WITH_LIGHT_DRIZZLE  (230,    0xf01e, "Thunder"),
    THUNDERSTORM_WITH_DRIZZLE        (231,    0xf01e, "Thunder"),
    THUNDERSTORM_WITH_HEAVY_DRIZZLE  (232,    0xf01e, "Thunder"),

    LIGHT_INTENSITY_DRIZZLE          (300,    0xf04e, "Drizzle"),
    DRIZZLE                          (301,    0xf04e, "Drizzle"),
    HEAVY_INTENSITY_DRIZZLE          (302,    0xf04e, "Drizzle"),
    LIGHT_INTENSITY_DRIZZLE_RAIN     (310,    0xf04e, "Drizzle"),
    DRIZZLE_RAIN                     (311,    0xf04e, "Drizzle"),
    HEAVY_INTENSITY_DRIZZLE_RAIN     (312,    0xf04e, "Drizzle"),
    SHOWER_RAIN_DRIZZLE              (313,    0xf04e, "Drizzle"),
    HEAVY_SHOWER_RAIN_DRIZZLE        (314,    0xf04e, "Drizzle"),
    SHOWER_DRIZZLE                   (321,    0xf04e, "Drizzle"),

    LIGHT_RAIN                       (500,    0xf019, "Rain"),
    MODERATE_RAIN                    (501,    0xf019, "Rain"),
    HEAVY_INTENSITY_RAIN             (502,    0xf019, "Rain"),
    VERY_HEAVY_RAIN                  (503,    0xf019, "Rain"),
    EXTREME_RAIN                     (504,    0xf019, "Rain"),
    FREEZING_RAIN                    (511,    0xf019, "Rain"),
    LIGHT_INTENSITY_SHOWER_RAIN      (520,    0xf019, "Rain"),
    SHOWER_RAIN                      (521,    0xf019, "Rain"),
    HEAVY_INTENSITY_SHOWER_RAIN      (522,    0xf019, "Rain"),
    RAGGED_SHOWER_RAIN               (531,    0xf019, "Rain"),

    LIGHT_SNOW                       (600,    0xf01b, "Snow"),
    SNOW                             (601,    0xf01b, "Snow"),
    HEAVY_SNOW                       (602,    0xf01b, "Snow"),
    SLEET                            (611,    0xf01b, "Snow"),
    SHOWER_SLEET                     (612,    0xf01b, "Snow"),
    LIGHT_RAIN_SNOW                  (615,    0xf01b, "Snow"),
    RAIN_SNOW                        (616,    0xf01b, "Snow"),
    LIGHT_SHOWER_SNOW                (620,    0xf01b, "Snow"),
    SHOWER_SNOW                      (621,    0xf01b, "Snow"),
    HEAVY_SHOWER_SNOW                (622,    0xf01b, "Snow"),

    MIST                             (701,    0xf014, "Mist"),
    SMOKE                            (711,    0xf062, "Smoke"),
    HAZE                             (721,    0xf0b6, "Haze"),
    SAND_OR_DUST_WHIRLS              (731,    0xf063, "Dust"),
    FOG                              (741,    0xf014, "Fog"),
    SAND                             (751,    0xf063, "Sand"),
    DUST                             (761,    0xf063, "Dust"),
    VOLCANIC_ASH                     (762,    0xf063, "Ash"),
    TORNADO1                         (751,    0xf056, "Tornado"),

    SKY_IS_CLEAR                     (800,    0xf00d, "Clear"),
    FEW_CLOUDS                       (801,    0xf013, "Clouds"),
    SCATTERED_CLOUDS                 (802,    0xf013, "Clouds"),
    BROKEN_CLOUDS                    (803,    0xf013, "Clouds"),
    OVERCAST_CLOUDS                  (804,    0xf013, "Clouds"),

    TORNADO2                         (900,    0xf056, "Tornado"),
    TROPICAL_STORM                   (901,    0xf01d, "Storm"),
    HURRICANE                        (902,    0xf073, "Hurricane"),
    COLD                             (903,    0xf076, "Cold"),
    HOT                              (904,    0xf072, "Hot"),
    WINDY                            (905,    0xf021, "Wind"),
    HAIL                             (906,    0xf015, "Hail"),

    NOT_AVAILABLE                    (3200, 0xf075, "N/A"),

    NO_CONNECTION                    (-1, 0xf0c6, "No Connection");

    int code;
    int character;
    String info;

    WeatherConditionCodes(int code, int character, String info) {
        this.code = code;
        this.character = character;
        this.info = info;
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

    public String toString() {
        return Character.toString((char) character);
    }

}
