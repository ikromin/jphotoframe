#!/bin/sh
java -Xms128m -Xmx256m -jar jphotoframe.jar "$1" >>jphotoframe.log 2>&1 &