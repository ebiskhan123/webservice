sudo systemctl stop mywebservice.service
sudo systemd-resolve --flush-caches
sudo systemctl daemon-reload
sudo systemctl start mywebservice.service
