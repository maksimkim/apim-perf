#!/bin/bash

sysctl -w net.ipv4.tcp_max_syn_backlog=65535
sysctl -w net.core.somaxconn=65535
sysctl net.ipv4.tcp_tw_reuse=1
sysctl net.ipv4.tcp_tw_recycle=1
sysctl -w kernel.shmmax=134217728
sysctl -w kernel.shmall=2097152

mvn clean compile assembly:single

cd target
java -server -XX:+UseNUMA -XX:+UseParallelGC -XX:+AggressiveOpts -jar backend-netty-0.1-jar-with-dependencies.jar &
