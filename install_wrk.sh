sudo apt-get install build-essential libssl-dev git curl -y 
git clone https://github.com/wg/wrk.git wrk 
cd wrk 
sudo make # move the executable to somewhere in your PATH, ex: 
sudo cp wrk /usr/local/bin
