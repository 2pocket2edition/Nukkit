#!/bin/bash

# this just allows me to change vm args through git

#-XX:+UseG1GC \

java \
-XX:+UseConcMarkSweepGC \
-XX:+UseParNewGC \
-XX:+UseNUMA \
-XX:+CMSParallelRemarkEnabled \
-XX:MaxTenuringThreshold=15 \
-XX:MaxGCPauseMillis=50 \
-XX:GCPauseIntervalMillis=150 \
-XX:+UseAdaptiveGCBoundary \
-XX:-UseGCOverheadLimit \
-XX:+UseBiasedLocking \
-XX:SurvivorRatio=8 \
-XX:TargetSurvivorRatio=90 \
-XX:MaxTenuringThreshold=15 \
-XX:+UseFastAccessorMethods \
-XX:+UseCompressedOops \
-XX:+OptimizeStringConcat \
-XX:+AggressiveOpts \
-XX:ReservedCodeCacheSize=512M \
-XX:+UseCodeCacheFlushing \
-XX:SoftRefLRUPolicyMSPerMB=10000 \
-XX:ParallelGCThreads=3 \
-Xmx3G \
-Xms256M \
-jar nukkit-1.0-SNAPSHOT.jar
