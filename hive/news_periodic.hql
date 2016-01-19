CREATE DATABASE IF NOT EXISTS disease_analyzer;

USE disease_analyzer;

CREATE EXTERNAL TABLE IF NOT EXISTS news_periodic (
    title STRING,
    topic STRING,
    link STRING,
    news_date TIMESTAMP
)
    ROW FORMAT DELIMITED
    FIELDS TERMINATED BY '\t'
    LINES TERMINATED BY '\n'
    STORED AS TEXTFILE
    LOCATION "/user/devel/data/news_data_proc_files/";