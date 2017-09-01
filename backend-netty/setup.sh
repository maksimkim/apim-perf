#!/bin/bash

mvn clean compile assembly:single

cd target

sysctl -w net.ipv4.tcp_max_syn_backlog=65535
sysctl -w net.core.somaxconn=65535
sysctl net.ipv4.tcp_tw_reuse=1
sysctl net.ipv4.tcp_tw_recycle=1
sysctl -w kernel.shmmax=134217728
sysctl -w kernel.shmall=2097152


openssl req -extensions v3_ca -new -x509 -days 30 -nodes -subj "/CN=NettyTestRoot" -newkey rsa:2048 -sha512 -out server_ca.pem -keyout server_ca.key
openssl req -new -keyout server.key -nodes -newkey rsa:2048 -subj "/CN=apim-httpperf.westus.cloudapp.azure.com" | openssl x509 -req -CAkey server_ca.key -CA server_ca.pem -days 36500 -set_serial $RANDOM -sha512 -out server.pem

java -server -XX:+UseNUMA -XX:+UseParallelGC -XX:+AggressiveOpts -jar backend-netty-0.1-jar-with-dependencies.jar &
