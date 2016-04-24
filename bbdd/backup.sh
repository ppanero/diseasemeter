#!/bin/bash

DATE=`date +%d%m%Y`

mysqldump -u diseasemeter -p diseasemeter > $1/disease_cdc_news_twitterstreaming$DATE.sql
mongoexport --db diseasemeter --collection centers > $1/centers_ts$DATE.json
mongoexport --db diseasemeter --collection heatpoints > $1/heatpoints_ts$DATE.json
