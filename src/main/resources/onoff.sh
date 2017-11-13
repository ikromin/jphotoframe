#!/bin/bash

# set the following variables as needed

JPHOTOFRAME_DIR=/home/pi/jphotoframe
PAUSE_FILE=/photos/pause.txt
DISP=:0

# do not edit below

cd $JPHOTOFRAME_DIR

mon=`/usr/bin/xset q -display :0|grep "Monitor is On"|wc -l`
moff=`/usr/bin/xset q -display :0|grep "Monitor is Off"|wc -l`

mhour=`date +"%H"`
mdo=`grep $mhour onoff_times.txt|awk -F"\t" '{print $2}'`

if [ $mdo == "on" ] && [ $mon == "0" ]; then
	echo Turning monitor ON
	/usr/bin/xset dpms force on -display $DISP
	rm /photos/pause.txt
fi

if [ $mdo == "off" ] && [ $moff == "0" ]; then
	echo Turning monitor OFF
	/usr/bin/xset dpms force off -display $DISP
	echo > $PAUSE_FILE
fi