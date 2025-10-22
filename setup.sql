DROP DATABASE IF EXISTS circuitdb;

CREATE DATABASE circuitdb;

USE circuitdb;

CREATE TABLE accounts (
    id INT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255),
    password VARCHAR(255),
    PRIMARY KEY (id)
);