CREATE DATABASE IF NOT EXISTS disease_analyzer;

USE disease_analyzer;

CREATE TABLE IF NOT EXISTS news (
    title STRING,
    topic STRING,
    link STRING,
    news_date TIMESTAMP
);