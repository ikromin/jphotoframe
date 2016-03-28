#!/bin/sh
java -Xms128m -Xmx128m -jar -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.NoOpLog jphotoframe.jar >>jphotoframe.log 2>&1 &