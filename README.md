# JPhotoFrame v0.4

JPhotoFrame is a simple Java application for displaying a collection of photos in a full-screen slideshow. It is meant to be used when creating a DIY photo frame.

## Features

* Lightweight and easily configurable
* Generates cached (resampled) images automatically
* Current date/time display with custom date/time formats
* Fills in the background on vertical photos using the photo as the source
* Weather forecast using OpenWeatherMap
* Pausable operation i.e. during photo frame offline hours
* Image rotation correction utility
* Configurable widget layout for date, time, weather, etc

## Issues Fixed

* Bug #1 OutOfMemoryError when loading image stops images from cycling
* OWM weather information not shown due to 2.1 API removal
* System.out usage moved to the Log class paving a way to better logging
* True separation of the model/view/controller objects
* Thread sleep times are aligned to timeout schedules instead of sleeping the same time every time i.e. if time update is 1000ms and it took 200ms to draw the screen, the thread will sleep only 800ms
* Layout engine is easier to use and customise

## Screenshots

TODO

## Building

Use Maven to build.

``mvn clean package``

## Configuration

Configuration is simple. Add a file called *config.properties* into the same directory as the jar file. Below is a sample configuration file (note that you will need a valid OWM API key to get weather data).

```
screenNumber=0
imageDirectory=/photos
cacheDirectory=/photos/cache
imageTimeout=300000
dateFormat=MMM d yyyy
timeFormat=H:mm
weatherCity=Brisbane,AU
owmApiKey=xxxxxxxxxxxxxxxxxxxxxxx
```

You can specify a custom configuration file to load by using a command line argument, simply give the location of the custom config file to the *run.sh* script or when running the *java* command. See how to run below.

### Configuration Options

These are the available configuration options. All apart frmo the directory settings are optional and have default values that will be used if nothing is provided for them.

#### Decimal/Floating Point Values

|Configuration Option    |Description
|------------------------|------------------------------------------------------------------------------------------
|backgroundOpacity       |Filler background opacity.
|backgroundSourcePercent |Percentage of the photo to use to generate the filler background.

#### Integer Values

|Configuration Option    |Description
|------------------------|------------------------------------------------------------------------------------------
|imageTimeout            |How long each photo is displayed, in milliseconds.
|screenNumber            |The monitor/screen to use for full screen display. The default value of 0 should work in most cases.
|weatherForecastDays     |Maximum number of days to display the the forecast. Values larger than 5 will be set to 5.
|weatherUpdateTime       |Time to wait between fetching weather data, in milliseconds. Values lower than 600000 will be set to 600000 i.e. 10 minutes.

#### String/Text Values

|Configuration Option    |Description
|------------------------|------------------------------------------------------------------------------------------
|cacheDirectory          |Path to store the cached files, should not be the same location as the imageDirectory.
|dateFormat              |Date format string as per the SimpleDateFormat Java class.
|imageDirectory          |Path to the directory where photos will be fetched from. Child directories will be ignored.
|layout                  |File to use for widget layout, default is layout.json
|owmApiKey               |API Key used to get weather data, from http://openweathermap.org/appid
|timeFormat              |Time format string as per teh SimpleDateFormat Java class.
|weatherCity             |The city to get weather forecast for. Format is City,Country.
|weatherUnits            |Metric/Imperial units to use for weather, valid values: *metric* or *imperial*. Defaults to metric.

## Widgets and Layout

Widgets are laid out as per the JSON layout file (default is *layout.json*). The file contains a single JSON object with a 'widgets' property, which is an array of widgets. Any number of widgets can be added to the 'widgets' array.

Example -

```
{
  "widgets": []
}
```
  
Available widgets:

* **anchor** - anchors other widgets to a certain part of the screen
* **text** - displays text either from variables bound to the data model or as free form strings
* **weather** - displays forecast data bound to a specific part of the weather data model

### Anchor Widget

Properties:

- **anchor** : two-integer array specifying which side to anchor along an axis (x,y). 0 is the min-side, 1 is the max-side e.g. [0,0] will anchor to the top left side of the screen, [1,1] will anchor to the bottom right
- **children** : list of child widgets

Example -

```
{
    "type": "anchor",
    "anchor": [1, 0],
    "children: []
}
```

### Text Widget

Properties:

- **data** : model data source to display
- **format** : text format string as per the [Java Formatter javadoc](https://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html)
- **font** : name of the font to use to render the text
- **size** : font size
- **colour** : colour of the text
- **outlineColour** : colour of the outline drawn around the text
- **outlineWidth** : width of the outline, values larger than 1 will typically exceed draw bounds
- **useInternalWeatherFont** : whether to use the internal weather font to rendering this widget
- **transform** : transformation to apply to this text, see below for more details

Allowed values for the **data** attribute:

- **$time** : binds to the model time value
- **$date** : binds to the model date value
- **$weather.geo** : binds to the weather model geographic area - contains two values (country and city)

In addition any freeform text string is allowed to be specified in the **data** attribute.


Example 1 -

```
{
    "type": "text",
    "transform": {
        "origin": [1, 0],
        "offset": [-10, 10],
        "showBounds": "false"
    },
    "text" : {
        "data": "$time",
        "size": 120,
        "outlineWidth": 12
    }
}
```

Example 2 -

```
{
    "type": "text",
    "text": {
        "data": "$weather.geo",
        "format": "%2$s, %1$s",
        "size": 26,
        "outlineWidth": 8
    }
}
```

### Weather Widget

Properties:

- **gap** : size of the gap between forecast items
- **gapPosition** : whether the gap is calculated from the 'leading' or 'trailing' end of the bounding box
- **orientation** : either 'vertical' or 'horizontal' layout
- **reverse** : whether forecast items should be shown in reverse i.e fri - mon instead of mon - fri
- **text** : text node properties
- **transform** : transformation to apply

Allowed values for the **data** attribute on the **text** property:

- **$temperature** - binds to the weather data forecast temperature value
- **$condition** - binds to the weather data forecast condition value
- **$condition2** - binds to the weather data forecast detailed condition value
- **$glyph** - binds to the weather data forecast condition code value, should be used along with the **useInternalWeatherFont** value set to **true**
- **$day** - binds to the weather data forecast condition day of week value
- **$date** - binds to the weather data forecast condition date object, can be formatted as a standard java *Date* object

Example -

```
{
    "type": "weather",
    "transform": {
        "origin": [0, 1],
        "offset": [75, -20],
        "showBounds": "false"
    },
    "text" : {
        "data": "$glyph",
        "size": 50,
        "outlineWidth": 8,
        "useInternalWeatherFont": "true"
    },
    "weather" : {
        "gap": 140,
        "gapPosition": "leading",
        "orientation": "horizontal"
    }
}
```

### Widget Transformation

A number of widgets suport the **transform** property which allows the widget to be translated or rotated on screen.

Properties:

- **origin** : two-integer array specifying the transformation origin (x,y), similar to Anchor 'anchor'
- **offset** : two-integer array specifying the offset in pixels relative to the origin (x, y)
- **rotate** : degrees rotation around the origin point
- **showBounds** : whether to show the drawing boundary box or not

Example -

```
"transform": {
    "origin": [0, 0],
    "offset": [10, -45],
    "rotate": 270,
    "showBounds": "false"
}
```

## Running

To run, launch the jar file like so:

`java -Xms32m -Xmx32m -jar jphotoframe.jar`

Alternatively copy the *jphotoframe.jar* file to the same directory as the *run.sh* (after building using ant) and execute the *run.sh* script instead.

Make sure that there is a *lib* directory with the *slf4j-api-1.7.2.jar* and *yahoo-weather-java-api-1.2.0.jar* files
inside it. The directory structure should look like this:

```
+-- lib/
|   +-- json-20170516.jar
|   +-- owm-japis-2.5.0.5.jar
|   +-- mediautil-1.0-withfixes.jar
+-- jphotoframe.jar
```

The jar files from the lib directory are included in the jphotoframe.jar file manifest and should be picked up
automatically.

## Weather Forecasts

Weather is retrieved from OpenWeatherMap using this library: https://github.com/akapribot/OWM-JAPIs

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

## Image Rotation Correction Utility

Sometimes JPEG files have rotation information saved into them. JPhotoFrame uses the standard Java JPEG loader and doesn't try to auto-orient images after loading. This can result in incorrectly rotated images.

A utility is provided to pre-process all of your photos and re-orinent them. This is done with the help of the mediautil library (http://mediachest.sourceforge.net/mediautil/).

To use this utility, run the *fixrotation.sh* script or pass the *-fixrotation* parameter when running JPhotoFrame manually. This utility expect a command line argument specifying the photo directory name.

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
