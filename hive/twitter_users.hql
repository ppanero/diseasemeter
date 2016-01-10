CREATE DATABASE IF NOT EXISTS disease_analyzer;

USE disease_analyzer;

CREATE TABLE IF NOT EXISTS twitter_users (
    creation_time TIMESTAMP,
    creation_country STRING,
    creation_place_name STRING,
    creation_place_type STRING,
    content STRING,
    user_default_location STRING,
    username STRING,
    disease STRING,
    status STRING,
);