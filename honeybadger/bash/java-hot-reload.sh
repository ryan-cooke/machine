#!/bin/bash
DIR=/home/pi/intellij/out/production/Forge
DEBUG_ARGS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

cd $DIR
sudo killall java
while true; do
	# Run the commands
	sudo java -classpath .:classes:/opt/pi4j/lib/'*' -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 Machine.rpi.MainPi &
	JAVA_PID=$!
	inotifywait -e modify -e close_write $DIR/Machine/rpi/MainPi.class &
	INOTIFY_PID=$!

	# Wait for one of them to finish
	wait -n
	sleep 2

	# Cleanup!
	sudo kill $INOTIFY_PID
	sudo kill -9 $JAVA_PID
	sudo killall java
done
