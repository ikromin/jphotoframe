#!/bin/bash
mon=`/usr/bin/xset q -display :0|grep "Monitor is Off"|wc -l`
if [ $mon == "0" ]; then
	echo Turning monitor OFF
	/usr/bin/xset dpms force off -display :0
	echo > /photos/pause.txt
fi
