# JPhotoFrame v0.3

JPhotoFrame is a simple Java application for displaying a collection of photos in a full-screen slideshow. It is meant to be used when creating a DIY photo frame.

## Features

* Lightweight and easily configurable
* Generates cached (resampled) images automatically
* Current date/time display with custom date/time formats
* Adjustable text font sizes
* Fills in the background on vertical photos using the photo as the source
* Weather forecast using OpenWeatherMap
* Pausable operation i.e. during photo frame offline hours

## Issues Fixed

Bug #1 OutOfMemoryError when loading image stops images from cycling

## Screenshots

Version 0.2, standard layout.

![](https://github.com/ikromin/jphotoframe/blob/master/screenshots/v0.2_with_weather.png)

Version 0.3, alternative layout (using config_altweather.properties)

![](https://github.com/ikromin/jphotoframe/blob/0.3/screenshots/v0.3_alt_weather.png)

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
weatherCity=Brisbane,AU
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
|textOutlineWidth        |Width of the text outline.
|timeOffsetX             |Horizontal offset from the right side of the screen when displaying the time.
|timeOffsetY             |Vertical offset from the top of the screen when displaying the time.
|weatherConditionOffsetY |Vertical offset from the bottom of the screen when displaying the weather condition icon.
|weatherDayWidth         |Amout of pixels that each forecast day should take on screen, includes condition icon and forecast text.
|weatherForecastCityOffsetY |Vertical offset from the bottom of the screen when displaying the forecast (location city, country).
|weatherForecastConditionOffsetY |Vertical offset from the bottom of the screen when displaying the forecast (condition).
|weatherForecastDays     |Maximum number of days to display the the forecast. Values larger than 5 will be set to 5.
|weatherForecastDayTempOffsetY   |Vertical offset from the bottom of the screen when displaying the forecast (day and temp min-max).
|weatherOffsetX          |Horizontal offset from the left side of the screen when displaying the weather icons.
|weatherUpdateTime       |Time to wait between fetching weather data, in milliseconds. Values lower than 600000 will be set to 600000 i.e. 10 minutes.

#### String/Text Values

|Configuration Option    |Description
|------------------------|------------------------------------------------------------------------------------------
|cacheDirectory          |Path to store the cached files, should not be the same location as the imageDirectory.
|dateFormat              |Date format string as per the SimpleDateFormat Java class.
|fontName                |Name of the font to use when displaying all text.
|imageDirectory          |Path to the directory where photos will be fetched from. Child directories will be ignored.
|timeFormat              |Time format string as per teh SimpleDateFormat Java class.
|weatherCity             |The city to get weather forecast for. Format is City,Country.


### Color/RGB Values

|Configuration Option    |Description
|------------------------|------------------------------------------------------------------------------------------
|textColor               |Color to use for all text as RGB (red,green,blue) value.
|textOutlineColor        |Color to use for the text outline. Should typically be set to 0,0,0 for best results

## Running

To run, launch the jar file like so:

`java -Xms32m -Xmx32m -jar jphotoframe.jar&`

Alternatively copy the *jphotoframe.jar* file to the same directory as the *run.sh* (after building using ant) and execute the *run.sh* script instead.

Make sure that there is a *lib* directory with the *slf4j-api-1.7.2.jar* and *yahoo-weather-java-api-1.2.0.jar* files
inside it. The directory structure should look like this:

```
+-- lib/
|   +-- commons-logging-1.2.jar
|   +-- httpclient-4.2.3.jar
|   +-- httpcore-4.2.2.jar
|   +-- json-20070829.jar
|   +-- owm-lib-2.1.2.jar
+-- jphotoframe.jar
```

The jar files from the lib directory are included in the jphotoframe.jar file manifest and should be picked up
automatically.

## Weather Forecasts

Weather is retrieved from OpenWeatherMap using this library: https://github.com/migtavares/owmClient.

All weather conditions are displayed using Weather Icons (http://erikflowers.github.io/weather-icons/).

## Pausable Operation

The time and photo updates can be paused by creating a file called *pause.txt* in the photos directory. If this file is found, the photo frame will become idle until that file is removed again.
This is useful to suspend photo updates during photo frame offline hours i.e. during the night.

## Scheduled Tasks

There is a script provided along with the source code called *onoff.sh* that can be used to control when the screen is turned on and off using *xset*. This script also controls whether the pausible operation is enabled or not.

To use make sure the *onoff_times.txt* file sets relevant times for when your screen should be on or off. The format of the file is the hour (24 hour format) followed by a tab, then either the word 'on' or 'off'.

The default file is as follows:

```
00	off
01	off
02	off
03	off
04	off
05	off
06	on
07	on
08	on
09	on
10	on
11	on
12	on
13	on
14	on
15	on
16	on
17	on
18	on
19	on
20	on
21	on
22	off
23	off
```

Update the *onoff.sh* script to set the correct directories and display number.

Set your crontab to below (update paths as required):

```
0 * * * * bash /home/pi/jphotoframe/onoff.sh
```

## License

JPhotoFrame - a simple Java application for displaying a collection of photos in a full-screen slideshow.
Copyright (C) 2015-2016  Igor Kromin

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
