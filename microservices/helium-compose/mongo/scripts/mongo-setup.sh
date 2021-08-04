#!/bin/bash
sleep 15

mongodb1=$(getent hosts mongo1 | awk '{ print $1 }')
mongodb2=$(getent hosts mongo2 | awk '{ print $1 }')
mongodb3=$(getent hosts mongo3 | awk '{ print $1 }')

port=${PORT:-27017}

echo "Waiting for startup.."
until curl http://${mongodb1}:27017/serverStatus\?text\=1 2>&1| grep uptime | head -1; do
  printf '.'
  sleep 1
done


echo "Started.."
echo "setup.sh; time now: $(date +"%T")"


mongo --host ${mongodb1}:${port} <<EOF
   var cfg = {
        "_id": "rs0",
        "members": [
            {
                "_id": 0,
                "host": "${mongodb1}:${port}",
                "priority":1
            },
            {
                "_id": 1,
                "host": "${mongodb2}:${port}",
                "priority":0.5
            },
            {
                "_id": 2,
                "host": "${mongodb3}:${port}",
                "priority":0.5
            }
        ],settings: {chainingAllowed: true}
    };
    rs.initiate(cfg, { force: true });
    rs.reconfig(cfg, { force: true });
    rs.status();
EOF

