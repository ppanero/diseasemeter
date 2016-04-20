-- MySQL dump 10.13  Distrib 5.6.26, for osx10.8 (x86_64)
--
-- Host: localhost    Database: diseasemeter
-- ------------------------------------------------------
-- Server version	5.6.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cdc_data`
--

DROP TABLE IF EXISTS `cdc_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cdc_data` (
  `_disease` varchar(45) NOT NULL,
  `_location` varchar(45) NOT NULL,
  `_date` varchar(45) NOT NULL,
  `disease_extra` varchar(45) DEFAULT NULL,
  `location_extra` varchar(45) DEFAULT NULL,
  `level` int(11) NOT NULL,
  `weight` int(11) NOT NULL,
  PRIMARY KEY (`_disease`,`_location`,`_date`),
  KEY `fk_cdc_data_disease_idx` (`_disease`,`_location`),
  CONSTRAINT `fk_cdc_data_disease_idx` FOREIGN KEY (`_disease`, `_location`) REFERENCES `disease` (`_name`, `_location`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cdc_data`
--

LOCK TABLES `cdc_data` WRITE;
/*!40000 ALTER TABLE `cdc_data` DISABLE KEYS */;
INSERT INTO `cdc_data` VALUES ('Chikungunya','Central America','19/11/2015',NULL,NULL,1,100),('Chikungunya','Mexico','19/11/2015',NULL,NULL,1,100),('Chikungunya','Senegal','18/09/2015',NULL,NULL,1,100),('Chikungunya','South America','15/03/2016',NULL,NULL,1,100),('Chikungunya','the Caribbean','14/05/2015',NULL,NULL,1,100),('Chikungunya','the Pacific Islands','06/07/2015',NULL,NULL,1,100),('Cholera','Tanzania','30/10/2015',NULL,NULL,1,100),('Lassa Fever','Benin','14/04/2016',NULL,NULL,1,100),('Lassa Fever','Nigeria','14/04/2016',NULL,NULL,1,100),('MERS','the Arabian Peninsula','19/11/2015',NULL,NULL,2,250),('Polio','Burma','21/12/2015',NULL,'Myanmar',2,250),('Polio','Guinea','03/11/2015',NULL,NULL,2,250),('Polio','Laos','03/11/2015',NULL,NULL,2,250),('Polio','Madagascar','03/11/2015',NULL,NULL,2,250),('Polio','Nigeria','16/11/2015',NULL,NULL,2,250),('Polio','Ukraine','03/11/2015',NULL,NULL,2,250),('Yellow Fever','Angola','15/04/2016',NULL,NULL,2,250),('Zika Virus','American Samoa','11/03/2016',NULL,NULL,2,250),('Zika Virus','Aruba','11/03/2016',NULL,NULL,2,250),('Zika Virus','Barbados','11/03/2016',NULL,NULL,2,250),('Zika Virus','Belize','18/04/2016',NULL,NULL,2,250),('Zika Virus','Bolivia','11/03/2016',NULL,NULL,2,250),('Zika Virus','Bonaire','11/03/2016',NULL,NULL,2,250),('Zika Virus','Brazil','11/03/2016',NULL,NULL,2,250),('Zika Virus','Cape Verde','11/03/2016',NULL,NULL,2,250),('Zika Virus','Colombia','11/03/2016',NULL,NULL,2,250),('Zika Virus','Costa Rica','11/03/2016',NULL,NULL,2,250),('Zika Virus','Cuba','19/03/2016',NULL,NULL,2,250),('Zika Virus','Curaçao','11/03/2016',NULL,NULL,2,250),('Zika Virus','Dominica','22/03/2016',NULL,NULL,2,250),('Zika Virus','Ecuador','11/03/2016',NULL,NULL,2,250),('Zika Virus','El Salvador','11/03/2016',NULL,NULL,2,250),('Zika Virus','Fiji','04/04/2016',NULL,NULL,2,250),('Zika Virus','French Guiana','11/03/2016',NULL,NULL,2,250),('Zika Virus','Guadeloupe','11/03/2016',NULL,NULL,2,250),('Zika Virus','Guatemala','11/03/2016',NULL,NULL,2,250),('Zika Virus','Guyana','11/03/2016',NULL,NULL,2,250),('Zika Virus','Haiti','11/03/2016',NULL,NULL,2,250),('Zika Virus','Honduras','11/03/2016',NULL,NULL,2,250),('Zika Virus','Jamaica','11/03/2016',NULL,NULL,2,250),('Zika Virus','Kosrae','01/04/2016',NULL,'Federated States of Micronesia',2,250),('Zika Virus','Marshall Islands','11/03/2016',NULL,NULL,2,250),('Zika Virus','Martinique','11/03/2016',NULL,NULL,2,250),('Zika Virus','Mexico','11/03/2016',NULL,NULL,2,250),('Zika Virus','New Caledonia','11/03/2016',NULL,NULL,2,250),('Zika Virus','Nicaragua','11/03/2016',NULL,NULL,2,250),('Zika Virus','Panama','11/03/2016',NULL,NULL,2,250),('Zika Virus','Paraguay','11/03/2016',NULL,NULL,2,250),('Zika Virus','Puerto Rico','29/03/2016',NULL,NULL,2,250),('Zika Virus','Saint Lucia','13/04/2016',NULL,NULL,2,250),('Zika Virus','Saint Martin','11/03/2016',NULL,NULL,2,250),('Zika Virus','Saint Vincent and the Grenadines','11/03/2016',NULL,NULL,2,250),('Zika Virus','Samoa','11/03/2016',NULL,NULL,2,250),('Zika Virus','Sint Maarten','11/03/2016',NULL,NULL,2,250),('Zika Virus','Suriname','11/03/2016',NULL,NULL,2,250),('Zika Virus','the Dominican Republic','11/03/2016',NULL,NULL,2,250),('Zika Virus','Tonga','11/03/2016',NULL,NULL,2,250),('Zika Virus','Trinidad and Tobago','11/03/2016',NULL,NULL,2,250),('Zika Virus','US Virgin Islands','11/03/2016',NULL,NULL,2,250),('Zika Virus','Venezuela','11/03/2016',NULL,NULL,2,250);
/*!40000 ALTER TABLE `cdc_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `disease`
--

DROP TABLE IF EXISTS `disease`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `disease` (
  `_name` varchar(45) NOT NULL,
  `_location` varchar(45) NOT NULL,
  `level` int(11) NOT NULL,
  `weight` int(11) NOT NULL,
  `twitter_count` int(11) NOT NULL,
  `news_count` int(11) NOT NULL,
  `cdc_count` int(11) NOT NULL,
  `last_update` varchar(10) NOT NULL,
  `initial_date` varchar(10) NOT NULL,
  `active` tinyint(1) NOT NULL,
  PRIMARY KEY (`_name`,`_location`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `disease`
--

LOCK TABLES `disease` WRITE;
/*!40000 ALTER TABLE `disease` DISABLE KEYS */;
INSERT INTO `disease` VALUES ('Chikungunya','Central America',1,100,0,0,2,'20/04/2016','19/11/2015',1),('Chikungunya','Mexico',1,100,0,0,2,'20/04/2016','19/11/2015',1),('Chikungunya','Senegal',1,100,0,0,2,'20/04/2016','18/09/2015',1),('Chikungunya','South America',1,100,0,0,2,'20/04/2016','15/03/2016',1),('Chikungunya','the Caribbean',1,100,0,0,2,'20/04/2016','14/05/2015',1),('Chikungunya','the Pacific Islands',1,100,0,0,2,'20/04/2016','06/07/2015',1),('Cholera','Tanzania',1,100,0,0,2,'20/04/2016','30/10/2015',1),('Lassa Fever','Benin',1,100,0,0,2,'20/04/2016','14/04/2016',1),('Lassa Fever','Nigeria',1,100,0,0,2,'20/04/2016','14/04/2016',1),('MERS','the Arabian Peninsula',2,250,0,0,2,'20/04/2016','19/11/2015',1),('Polio','Burma',2,250,0,0,2,'20/04/2016','21/12/2015',1),('Polio','Guinea',2,250,0,0,2,'20/04/2016','03/11/2015',1),('Polio','Laos',2,250,0,0,2,'20/04/2016','03/11/2015',1),('Polio','Madagascar',2,250,0,0,2,'20/04/2016','03/11/2015',1),('Polio','Nigeria',2,250,0,0,2,'20/04/2016','16/11/2015',1),('Polio','Ukraine',2,250,0,0,2,'20/04/2016','03/11/2015',1),('Yellow Fever','Angola',2,250,0,0,2,'20/04/2016','15/04/2016',1),('Zika Virus','American Samoa',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Aruba',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Barbados',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Belize',2,250,0,0,2,'20/04/2016','18/04/2016',1),('Zika Virus','Bolivia',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Bonaire',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Brazil',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Cape Verde',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Colombia',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Costa Rica',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Cuba',2,250,0,0,2,'20/04/2016','19/03/2016',1),('Zika Virus','Curaçao',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Dominica',2,250,0,0,2,'20/04/2016','22/03/2016',1),('Zika Virus','Ecuador',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','El Salvador',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Fiji',3,500,0,0,2,'20/04/2016','01/04/2016',1),('Zika Virus','French Guiana',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Guadeloupe',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Guatemala',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Guyana',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Haiti',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Honduras',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Jamaica',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Kosrae',2,250,0,0,2,'20/04/2016','01/04/2016',1),('Zika Virus','Marshall Islands',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Martinique',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Mexico',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','New Caledonia',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Nicaragua',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Panama',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Paraguay',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Puerto Rico',2,250,0,0,2,'20/04/2016','29/03/2016',1),('Zika Virus','Saint Lucia',2,250,0,0,2,'20/04/2016','13/04/2016',1),('Zika Virus','Saint Martin',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Saint Vincent and the Grenadines',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Samoa',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Sint Maarten',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Suriname',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','the Dominican Republic',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Tonga',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Trinidad and Tobago',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','US Virgin Islands',2,250,0,0,2,'20/04/2016','11/03/2016',1),('Zika Virus','Venezuela',2,250,0,0,2,'20/04/2016','11/03/2016',1);
/*!40000 ALTER TABLE `disease` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-04-20 11:32:42
