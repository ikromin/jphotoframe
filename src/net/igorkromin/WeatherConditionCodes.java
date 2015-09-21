package net.igorkromin;

/**
 * Maps the Yahoo! Weather codes to the weather icons font characters
 * https://developer.yahoo.com/weather/documentation.html#codes
 */
public enum WeatherConditionCodes {

    TORNADO                 (0,    0xf056, "Tornado"),
    TROPICAL_STORM          (1,    0xf01d, "Storms"),
    HURRICANE               (2,    0xf073, "Hurricane"),
    SEVERE_THUNDERSTOMS     (3,    0xf01e, "Thunder"),
    THUNDERSTOMS            (4,    0xf01e, "Thunders"),
    MIXED_RAIN_AND_SNOW     (5,    0xf017, "Rain+Snow"),
    MIXED_RAIN_AND_SLEET    (6,    0xf0b5, "Rain+Sleet"),
    MIXED_SNOW_AND_SLEET    (7,    0xf0b5, "Snow+Sleet"),
    FREEZING_DRIZZLE        (8,    0xf04e, "Drizzle"),
    DRIZZLE                 (9,    0xf04e, "Drizzle"),
    FREEZING_RAIN           (10,   0xf019, "Rain"),
    SHOWERS1                (11,   0xf01a, "Showers"),
    SHOWERS2                (12,   0xf01a, "Showers"),
    SNOW_FLURRIES           (13,   0xf01b, "Snow"),
    LIGHT_SNOW_SHOWERS      (14,   0xf01b, "Snow"),
    BLOWING_SNOW            (15,   0xf064, "Snow"),
    SNOW                    (16,   0xf01b, "Snow"),
    HAIL                    (17,   0xf015, "Hail"),
    SLEET                   (18,   0xf0b5, "Sleet"),
    DUST                    (19,   0xf063, "Dust"),
    FOGGY                   (20,   0xf014, "Foggy"),
    HAZE                    (21,   0xf0b6, "Haze"),
    SMOKY                   (22,   0xf062, "Smoky"),
    BLUSTERY                (23,   0xf050, "Blustery"),
    WINDY                   (24,   0xf021, "Windy"),
    COLD                    (25,   0xf076, "Cold"),
    CLOUDY                  (26,   0xf013, "Cloudy"),
    MOSTLY_CLOUDY_NIGHT     (27,   0xf086, "Cloudy"),
    MOSTLY_CLOUDY_DAY       (28,   0xf002, "Cloudy"),
    PARTLY_CLOUDY_NIGHT     (29,   0xf083, "Cloudy"),
    PARTLY_CLOUDY_DAY       (30,   0xf00c, "Cloudy"),
    CLEAR_NIGHT             (31,   0xf02e, "Clear"),
    SUNNY                   (32,   0xf00d, "Sunny"),
    FAIR_NIGHT              (33,   0xf02e, "Fair"),
    FAIR_DAY                (34,   0xf00d, "Fair"),
    MIXED_RAIN_AND_HAIL     (35,   0xf015, "Rain+Hail"),
    HOT                     (36,   0xf072, "Hot"),
    ISOLATED_THUNDERSTORMS  (37,   0xf01e, "Thunder"),
    SCATTERED_THUNDERSTORMS1(38,   0xf01e, "Thunder"),
    SCATTERED_THUNDERSTORMS2(39,   0xf01e, "Thunder"),
    SCATTERED_SHOWERS       (40,   0xf01a, "Showers"),
    HEAVY_SNOW1             (41,   0xf01b, "Snow"),
    SCATTERD_SNOW_SHOWERS   (42,   0xf01b, "Snow"),
    HEAVY_SNOW2             (43,   0xf01b, "Snow"),
    PARTLY_CLOUDY           (44,   0xf013, "Cloudy"),
    THUNDERSHOWERS          (45,   0xf01e, "Showers"),
    SNOW_SHOWERS            (46,   0xf01b, "Snow"),
    ISOLATED_THUNDERSHOWERS (47,   0xf01d, "Thunder"),
    NOT_AVAILABLE           (3200, 0xf075, "N/A")
    ;

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

        return null;
    }

    public String getInfoText() {
        return info;
    }

    public String toString() {
        return Character.toString((char) character);
    }

}
