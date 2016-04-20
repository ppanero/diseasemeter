#!/bin/bash

DATE=`date +%d%m%Y`

mysqldump -u diseasemeter -p diseasemeter > $1/disease_cdc_$DATE.sql
mongoexport --db diseasemeter --collection centers > $1/centers_$DATE.json
mongoexport --db diseasemeter --collection heatpoints > $1/heatpoints_$DATE.json
