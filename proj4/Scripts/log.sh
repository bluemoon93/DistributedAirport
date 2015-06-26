PW=ultimaceia
/usr/bin/expect -c 'spawn scp sd0102@l040101-ws5.clients.ua.pt:./log.txt log.txt; expect "sd0102" { send "'`echo $PW`'\r" }; interact'
