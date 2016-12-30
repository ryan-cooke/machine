#!/bin/bash

while true; do
	java Machine.rpi.MainPi &
	JAVA_PID=$!
	inotifywait -e modify -e close_write Machine/rpi/MainPi.class
	sleep 5
	kill -9 $JAVA_PID
done
