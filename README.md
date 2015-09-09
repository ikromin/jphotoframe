# JPhotoFrame v0.2
JPhotoFrame is a simple Java application for displaying a collection of photos in a full-screen slideshow. It is meant to be used when creating a DIY photo frame.

## Features

* Lightweight and easily configurable
* Generates cached (resampled) images automatically
* Current date/time display with custom date/time formats
* Adjustable text font sizes
* Fills in the background on vertical photos using the photo as the source

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
```
### Configuration Options

These are the available configuration options. All apart frmo the directory settings are optional and have default values that will be used if nothing is provided for them.

**screenNumber** - *integer* - The monitor/screen to use for full screen display. The default value of 0 should work in most cases

**imageDirectory** - *string* - Path to the directory where photos will be fetched from. Child directories will be ignored.

**cacheDirectory** - *string* - Path to store the cached files, should not be the same location as the imageDirectory.

**imageTimeout** - *integer* - How long each photo is displayed, in milliseconds.

**fontName** - *string* - Name of the font to use when displaying all text.

**fontSizeDate** - *integer* - Size of the font used to display the date.

**fontSizeTime** - *integer* - Size of the font used to display the time.

**dateFormat** - *string* - Date format string as per the SimpleDateFormat Java class.

**timeFormat** - *string* - Time format string as per teh SimpleDateFormat Java class.

**dateOffsetX** - *integer* - Horizontal offset from the right side of the screen when displaying the date.

**dateOffsetY** - *integer* - Vertical offset from the top of the screen when displaying the date.

**timeOffsetX** - *integer* - Horizontal offset from the right side of the screen when displaying the time.

**timeOffsetY** - *integer* - Vertical offset from the top of the screen when displaying the time.

**textColor** - *rgb* - Color to use for all text as RGB (red,green,blue) value.

**textOutlineColor** - *rgb* - Color to use for the text outline. Should typically be set to 0,0,0 for best results

**textOutlineOffset** - *integer* - Width of the text outline.

**backgroundSourcePercent** - *float* - Percentage of the photo to use to generate the background.

**backgroundOpacity** - *float* - Background opacity.

## Running

To run, launch the jar file like so:

`java -Xms32m -Xmx32m -jar jphotoframe.jar&`

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
