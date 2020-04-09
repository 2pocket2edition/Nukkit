#!/bin/bash

while true
do
    echo "Press Ctrl+C to stop"
    sleep 3
    git pull
    rm nukkit-1.0-SNAPSHOT.jar*
    bash launch.sh
done
