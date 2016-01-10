CREATE DATABASE IF NOT EXISTS disease_analyzer;

USE disease_analyzer;

CREATE EXTERNAL TABLE IF NOT EXISTS twitter_streaming (
    creation_time TIMESTAMP,
    creation_country STRING,
    creation_place_name STRING,
    creation_place_type STRING,
    content STRING,
    user_default_location STRING,
    username STRING,
    tag STRING
)
    ROW FORMAT DELIMITED
    FIELDS TERMINATED BY ‘\t’
    LINES TERMINATED BY ‘\n’
    STORED AS TEXTFILE
    LOCATION "/user/devel/data/twitter_data_proc_files/";