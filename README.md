# JPhotoFrame v0.2

JPhotoFrame is a simple Java application for displaying a collection of photos in a full-screen slideshow. It is meant to be used when creating a DIY photo frame.

## Features

* Lightweight and easily configurable
* Generates cached (resampled) images automatically
* Current date/time display with custom date/time formats
* Adjustable text font sizes
* Fills in the background on vertical photos using the photo as the source
* Weather forecast using Yahoo! Weather

## Screenshots

![](https://github.com/ikromin/jphotoframe/blob/master/screenshots/v0.2_with_weather.png)

## Building

Use Ant to build. Edit the *jphotoframe.properties* to point to your JDK location prior to building.

``ant``

## Configuration

Configuration is simple. Add a file called *config.properties* into the same directory as the jar file. Below is a sample configuration file.

```
screenNumber=0
imageDirectory=/photos
cacheDirectory=/photos/cache
imageTimeout=300000
fontName=Verdana
fontSizeDate=20
fontSizeTime=50
dateFormat=MMM d yyyy
timeFormat=H:mm
dateOffsetX=15
dateOffsetY=10
timeOffsetX=10
timeOffsetY=30
textColor=0,0,0
textOutlineColor=255,0,0
textOutlineOffset=2
backgroundSourcePercent=0.01
backgroundOpacity=0.2
showWeather=true
weatherWoeid=1100661
```
### Configuration Options

These are the available configuration options. All apart frmo the directory settings are optional and have default values that will be used if nothing is provided for them.

#### Boolean Values

|Configuration Option    |Description
|------------------------|------------------------------------------------------------------------------------------
|showWeather             |Whether the weather forecast should be fetched and displayed.

#### Decimal/Floating Point Values

|Configuration Option    |Description
|------------------------|------------------------------------------------------------------------------------------
|backgroundOpacity       |Background opacity.
|backgroundSourcePercent |Percentage of the photo to use to generate the background.
|fontSizeWeatherCondition|Size of the font used to display the weather condition icon.

#### Integer Values

|Configuration Option    |Description
|------------------------|------------------------------------------------------------------------------------------
|dateOffsetX             |Horizontal offset from the right side of the screen when displaying the date.
|dateOffsetY             |Vertical offset from the top of the screen when displaying the date.
|fontSizeDate            |Size of the font used to display the date.
|fontSizeWeatherForecast |Size of the font used to display the weather forecast (day low/high)
|fontSizeTime            |Size of the font used to display the time.
|imageTimeout            |How long each photo is displayed, in milliseconds.
|screenNumber            |The monitor/screen to use for full screen display. The default value of 0 should work in most cases.
|textOutlineOffset       |Width of the text outline.
|timeOffsetX             |Horizontal offset from the right side of the screen when displaying the time.
|timeOffsetY             |Vertical offset from the top of the screen when displaying the time.
|weatherConditionOffsetY |Vertical offset from the bottom of the screen when displaying the weather condition icon.
|weatherDayWidth         |Amout of pixels that each forecast day should take on screen, includes condition icon and forecast text.
|weatherForecastDays     |Maximum number of days to display the the forecast. Values larger than 5 will have no effect.
|weatherForecastOffsetY  |Vertical offset from the bottom of the screen when displaying the forecast (day min/max).
|weatherOffsetX          |Horizontal offset from the left side of the screen when displaying the weather icons.
|weatherWoeid            |The WOEID of the location to get the weather forecast for. These can be looked up here: http://woeid.rosselliot.co.nz

#### String/Text Values

|Configuration Option    |Description
|------------------------|------------------------------------------------------------------------------------------
|cacheDirectory          |Path to store the cached files, should not be the same location as the imageDirectory.
|dateFormat              |Date format string as per the SimpleDateFormat Java class.
|fontName                |Name of the font to use when displaying all text.
|imageDirectory          |Path to the directory where photos will be fetched from. Child directories will be ignored.
|timeFormat              |Time format string as per teh SimpleDateFormat Java class.

### Color/RGB Values

|Configuration Option    |Description
|------------------------|------------------------------------------------------------------------------------------
|textColor               |Color to use for all text as RGB (red,green,blue) value.
|textOutlineColor        |Color to use for the text outline. Should typically be set to 0,0,0 for best results

## Running

To run, launch the jar file like so:

`java -Xms32m -Xmx32m -jar jphotoframe.jar&`

Make sure that there is a *lib* directory with the *slf4j-api-1.7.2.jar* and *yahoo-weather-java-api-1.2.0.jar* files
inside it. The directory structure should look like this:

```
+-- lib/
|   +-- slf4j-api-1.7.2.jar
|   +-- yahoo-weather-java-api-1.2.0.jar
+-- jphotoframe.jar
```

The jar files from the lib directory are included in the jphotoframe.jar file manifest and should be picked up
automatically.

## Weather Forecasts

Weather is retrieved from Yahoo! Weather using this library: https://github.com/fedy2/yahoo-weather-java-api.

A WOEID must be configured for your location to retrieve forecasts. These can be looked up here:
http://woeid.rosselliot.co.nz.

All weather conditions are displayed using Weather Icons (http://erikflowers.github.io/weather-icons/).

Below are the icons, conditions and condition codes used:

![](https://github.com/ikromin/jphotoframe/blob/master/conditions.png)

## License

JPhotoFrame - a simple Java application for displaying a collection of photos in a full-screen slideshow.
Copyright (C) 2015  Igor Kromin

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; version 2 of the License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

You can find this and my other open source projects here - http://github.com/ikromin
