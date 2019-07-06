#!/bin/bash

while true
do
    echo "Press Ctrl+C to stop"
    sleep 3
    git pull
    cd src/main/java/net/twoptwoe/mobplugin/
    git pull origin MobPlugin
    cd ../../../../../..
    ./mvnw compile exec:exec -Dexec.executable="java" -Dexec.args="-classpath %classpath -XX:+UseG1GC -Xmx4G -Xms256M cn.nukkit.Nukkit"
done
