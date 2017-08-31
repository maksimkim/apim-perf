sysctl -w net.ipv4.tcp_max_syn_backlog=65535
sysctl -w net.core.somaxconn=65535
ulimit -n 65535
sysctl net.ipv4.tcp_tw_reuse=1
sysctl net.ipv4.tcp_tw_recycle=1
sysctl -w kernel.shmmax=2147483648
sysctl -w kernel.shmall=2097152
