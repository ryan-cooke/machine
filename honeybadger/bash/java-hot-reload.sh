#!/bin/bash
DIR=/home/pi/intellij/out/production/Forge
cd $DIR
sudo killall java
while true; do
	sudo java -classpath .:classes:/opt/pi4j/lib/'*' Machine.rpi.MainPi &
	JAVA_PID=$!
	inotifywait -e modify -e close_write $DIR/Machine/rpi/MainPi.class
	sleep 2
	sudo kill -9 $JAVA_PID
	sudo killall java
done
