CREATE DATABASE IF NOT EXISTS disease_analyzer;

USE disease_analyzer;

CREATE EXTERNAL TABLE IF NOT EXISTS cdc_periodic (
    alert_level STRING,
    alert_date TIMESTAMP,
    disease STRING,
    disease_extra STRING,
    area STRING,
    area_extra STRING
)
    ROW FORMAT DELIMITED
    FIELDS TERMINATED BY '\t'
    LINES TERMINATED BY '\n'
    STORED AS TEXTFILE
    LOCATION "/user/devel/data/cdc_data_proc_files/";