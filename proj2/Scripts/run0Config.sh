PW=ultimaceia
/usr/bin/expect -c 'spawn ssh sd0102@l040101-ws1.clients.ua.pt; expect "sd0102" { send "'`echo $PW`'\r" }; expect "sd0102" { send "java -cp SD_T1_P2_G2.jar Proj2.Config" }; interact'

