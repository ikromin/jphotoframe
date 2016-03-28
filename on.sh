#!/bin/bash
mon=`/usr/bin/xset q -display :0|grep "Monitor is On"|wc -l`
if [ $mon == "0" ]; then
	echo Turning monitor ON
	/usr/bin/xset dpms force on -display :0
	rm /photos/pause.txt
fi
