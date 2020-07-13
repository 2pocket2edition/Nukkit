#!/bin/bash

wget -O nukkit-1.0-SNAPSHOT.jar --no-check-certificate --header="Host: jenkins.daporkchop.net" https://10.0.0.20/job/Minecraft/job/2p2e/job/NukkitX/job/master/lastSuccessfulBuild/artifact/target/nukkit-1.0-SNAPSHOT.jar

java -Xmx1500M -Xms1G \
-XX:+UseG1GC \
-Dsun.rmi.dgc.server.gcInterval=2147483646 \
-XX:+UnlockExperimentalVMOptions \
-XX:MaxGCPauseMillis=50 \
-XX:G1HeapRegionSize=16M \
-XX:+AggressiveOpts \
-XX:+AlwaysPreTouch \
-XX:+UseLargePagesInMetaspace \
-XX:ConcGCThreads=3 \
-jar nukkit-1.0-SNAPSHOT.jar
