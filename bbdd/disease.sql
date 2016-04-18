-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `diseasemeter` DEFAULT CHARACTER SET utf8 ;
USE `diseasemeter` ;

-- -----------------------------------------------------
-- Table `disease`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `disease` (
  `_name` VARCHAR(45) NOT NULL,
  `_location` VARCHAR(45) NOT NULL,
  `level` INT NOT NULL,
  `twitter_count` INT NOT NULL,
  `news_count` INT NOT NULL,
  `cdc_count` INT NOT NULL,
  `last_update` VARCHAR(10) NOT NULL,
  `initial_date` VARCHAR(10) NOT NULL,
  `active` TINYINT(1) NOT NULL,
  PRIMARY KEY (`_name`, `_location`))

-- -----------------------------------------------------
-- Table `cdc_data`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cdc_data` (
  `_disease` VARCHAR(45) NOT NULL,
  `_location` VARCHAR(45) NOT NULL,
  `_date` VARCHAR(45) NOT NULL,
  `disease_extra` VARCHAR(45),
  `location_extra` VARCHAR(45),
  `level` INT NOT NULL,
  `weight` INT NOT NULL,
  PRIMARY KEY (`_disease`, `_location`, `_date`),
  INDEX `fk_cdc_data_disease_idx` (`_disease`, `_location`),
  CONSTRAINT `fk_cdc_data_disease_idx`
    FOREIGN KEY (`_disease`, `_location`)
    REFERENCES `disease` (`_name`, `_location`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)

-- -----------------------------------------------------
-- Table `social_data`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `social_data` (
  `_id` INT NOT NULL AUTO_INCREMENT,
  `disease_id` INT NOT NULL,
  `location` VARCHAR(45) NOT NULL,
  `date` VARCHAR(10) NOT NULL,
  `source` INT NOT NULL,
  `content` VARCHAR(200) NOT NULL,
  `language` INT NOT NULL,
  `sentiment` INT NOT NULL,
  PRIMARY KEY (`_id`),
  INDEX `fk_social_data_disease_idx` (`disease_id` ASC),
  CONSTRAINT `fk_social_data_disease`
    FOREIGN KEY (`disease_id`)
    REFERENCES `disease` (`_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
