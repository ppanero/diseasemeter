# Disease Meter

Disease Analyzer is a system that aims to infer the aweareness of the citizens about active diseases around the world. The system consists of 4 modules the database, the data collector, the RESTful API and the Android application.

License GPLv3

## Database

The system uses two databases a relational one, in MySQL, used for storing profiling data of a disease (amount of references and content in newspapers, twitter and CDC travel webpage, countries where it is present, etc.) and a second NoSQL database, in MongoDB, for storign the geographical points where a disease was mentioned and the focuses of them.

In order to persist and query the data both the data collector and the RESTful API use Spring Data for Mongo DB and Hibernate for MySQL, in both cases the abstract mapper patter has been implemented.

The database can be found in the bbdd folder. In that forlder there is also a script to create backups of the databases.

## Data Collector (DC)

The DC is the part of the system which gathers data from Twitter, Newspaper webapages and CDC travel alerts. For now there are only three newspapers "El Mundo", "El Publico" and "20 Minutos". Although more online newspapers can be added easily by extendind the parent class and implementing the parsing method.

The DC has a streaming listener for tweets from selected official accounts and another streaming listener for general tweets mentioning diseases (either in english or in spanish). Apart form those there are 4 processes that are run periodically (the time windows is up to the user and has to be set when setting the cronjob) in order to gather information from the newspapers and CDC.

For the purpouse of anylizing and evaluation the wieght of an element (news, tweet, etc.) the DC makes use of several APIs available online. For the translation it uses the Microsoft Translation API. For keyword extraction, langauge detection and sentiment analysis it uses monkeylearn API which allows to edit the decission trees and element to look for and or evaluate. For the geolocation of an element the DC makes use of the geocoding and reverse gocoding capabilities of Google's API.

## RESTful API

The API exposes two resources to query the list of diseases currently active and gives the possibility to filter by geographic zone and date, and also the geographic points and gives the possibility to filter by disease.

## Android Application

The Android application is a simple app that allows users to query the system (using the previous mentioned API) from their phone. It has two tabs, one for the geographic information and another one for listing the diseases.

## Installation manual

Apart from following the instruction to set up the system in your server, there is a need to register in Twitter API, MonkeyLearn, Microsoft Translate, and Google API and get the corresponding API KEYs and added to the android manifest, and data collector configurations.

//TODO


