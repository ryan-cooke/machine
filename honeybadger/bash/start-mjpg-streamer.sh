#!/bin/bash

# Launch the default cam
sudo mjpg_streamer -i "/usr/lib/input_uvc.so -d /dev/video0 -y -n -r 640x480 -f 10" -o "/usr/lib/output_http.so -p 8090 -w /var/www/mjpg_streamer"

# If a second cam exists, launch that
sudo mjpg_streamer -i "/usr/lib/input_uvc.so -d /dev/video1 -y -n -r 640x480 -f 10" -o "/usr/lib/output_http.so -p 8080 -w /var/www/mjpg_streamer"
