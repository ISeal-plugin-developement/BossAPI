#!/bin/bash

rm -r -f ~/Desktop/servers/paper1201/plugins/BossAPI-*.jar
mv ~/IdeaProjects/BossAPI/target/output/* ~/Desktop/servers/paper1201/plugins/
echo "BossAPI artifact moved to server plugins folder"
echo "Here be dragons!"
