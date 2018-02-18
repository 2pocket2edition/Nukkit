#!/bin/bash

while true
do
    echo "Press Ctrl+C to stop"
    sleep 3
    mvn clean package
    mv target/*.jar .
    mvn clean compile exec:exec -Dexec.executable="java" -Dexec.args="-classpath %classpath -Xmx6G -Xms6G cn.nukkit.Nukkit"
done
