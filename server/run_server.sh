#!/bin/bash

# Backs up all databases
#cd ../../
#./Linux_Backup_Databases.sh
#cd Game/server

# Kills all java processes - needed for server auto restart process.
pkill -f 'java -jar'

echo ""
echo "Launching the game server in a new screen."
echo ""
echo "Type 'screen -r' to access the game server screen."
echo "Use CTRL + A + D to detach the live server screen so it runs in the background."
echo ""
echo ""
screen -dmS name ./ant_launcher.sh
