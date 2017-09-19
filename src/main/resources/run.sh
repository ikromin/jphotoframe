#!/bin/sh
if [ -z "$1" ];
then
    java -Xms128m -Xmx256m -jar jphotoframe.jar >>jphotoframe.log 2>&1 &
else
	java -Xms128m -Xmx256m -jar jphotoframe.jar "$1" >>jphotoframe.log 2>&1 &
fi
