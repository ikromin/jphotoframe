package net.igorkromin;

/**
 * Maps the Yahoo! Weather codes to the weather icons font characters
 * https://developer.yahoo.com/weather/documentation.html#codes
 */
public enum WeatherConditionCodes {

    TORNADO(0, 0xf056),
    TROPICAL_STORM(1, 0xf01d),
    HURRICANE(2, 0xf073),
    SEVERE_THUNDERSTOMS(3, 0xf01e),
    THUNDERSTOMS(4, 0xf01e),
    MIXED_RAIN_AND_SNOW(5, 0xf017),
    MIXED_RAIN_AND_SLEET(6, 0xf0b5),
    MIXED_SNOW_AND_SLEET(7, 0xf0b5),
    FREEZING_DRIZZLE(8, 0xf04e),
    DRIZZLE(9, 0xf04e),
    FREEZING_RAIN(10, 0xf019),
    SHOWERS1(11, 0xf01a),
    SHOWERS2(12, 0xf01a),
    SNOW_FLURRIES(13, 0xf01b),
    LIGHT_SNOW_SHOWERS(14, 0xf01b),
    BLOWING_SNOW(15, 0xf064),
    SNOW(16, 0xf01b),
    HAIL(17, 0xf015),
    SLEET(18, 0xf0b5),
    DUST(19, 0xf063),
    FOGGY(20, 0xf014),
    HAZE(21, 0xf0b6),
    SMOKY(22, 0xf062),
    BLUSTERY(23, 0xf050),
    WINDY(24, 0xf021),
    COLD(25, 0xf076),
    CLOUDY(26, 0xf013),
    MOSTLY_CLOUDY_NIGHT(27, 0xf086),
    MOSTLY_CLOUDY_DAY(28, 0xf002),
    PARTLY_CLOUDY_NIGHT(29, 0xf083),
    PARTLY_CLOUDY_DAY(30, 0xf00c),
    CLEAR_NIGHT(31, 0xf02e),
    SUNNY(32, 0xf00d),
    FAIR_NIGHT(33, 0xf02e),
    FAIR_DAY(34, 0xf00d),
    MIXED_RAIN_AND_HAIL(35, 0xf015),
    HOT(36, 0xf072),
    ISOLATED_THUNDERSTORMS(37, 0xf01e),
    SCATTERED_THUNDERSTORMS1(38, 0xf01e),
    SCATTERED_THUNDERSTORMS2(39, 0xf01e),
    SCATTERED_SHOWERS(40, 0xf01a),
    HEAVY_SNOW1(41, 0xf01b),
    SCATTERD_SNOW_SHOWERS(42, 0xf01b),
    HEAVY_SNOW2(43, 0xf01b),
    PARTLY_CLOUDY(44, 0xf013),
    THUNDERSHOWERS(45, 0xf01e),
    SNOW_SHOWERS(46, 0xf01b),
    ISOLATED_THUNDERSHOWERS(47, 0xf01d),
    NOT_AVAILABLE(3200, 0xf075)
    ;

    int code;
    int character;

    WeatherConditionCodes(int code, int character) {
        this.code = code;
        this.character = character;
    }

    public static WeatherConditionCodes fromInt(int code) {

        for (WeatherConditionCodes w : WeatherConditionCodes.values()) {
            if (w.code == code) {
                return w;
            }
        }

        return null;
    }

    public String toString() {
        return Character.toString((char) character);
    }

}
