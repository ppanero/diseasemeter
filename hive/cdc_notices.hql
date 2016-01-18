CREATE DATABASE IF NOT EXISTS disease_analyzer;

USE disease_analyzer;

CREATE TABLE IF NOT EXISTS cdc_notices (
    alert_level STRING,
    alert_date TIMESTAMP,
    disease STRING,
    disease_extra STRING,
    area STRING,
    area_extra STRING
);