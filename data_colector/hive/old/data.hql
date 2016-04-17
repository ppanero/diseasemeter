CREATE DATABASE IF NOT EXISTS disease_analyzer;

USE disease_analyzer;

CREATE TABLE IF NOT EXISTS data (
    content STRING,
    date TIMESTAMP,
    disease STRING,
    _location STRING,
    source INTEGER,
    language INTEGER,
    status INTEGER,
    weight INTEGER,
    extra STRING
);