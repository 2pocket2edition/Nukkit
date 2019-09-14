#!/bin/bash

while true
do
    echo "Press Ctrl+C to stop"
    sleep 3
    git pull
    wget https://jenkins.daporkchop.net/job/2p2e/job/Nukkit/lastSuccessfulBuild/artifact/target/nukkit-1.0-SNAPSHOT.jar
    java -XX:+UseG1GC -Xmx3G -Xms256M -jar nukkit-1.0-SNAPSHOT.jar
done
