#!/bin/bash

layoutPath=$(pwd)'/app/src/main/res/layout/'
layout='app/src/main/res/layout/'

for file in `ls $layoutPath`
do
  echo $file
done

# sed -i.bak 's|#ffffff|@color\/colorPrimaryLight|g' activity_itinerary.xml