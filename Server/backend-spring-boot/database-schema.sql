-- Create Database
CREATE DATABASE IF NOT EXISTS docdispatch;
USE docdispatch;

-- Create queries table
CREATE TABLE IF NOT EXISTS queries (
    qid INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    contact VARCHAR(15) NOT NULL,
    name VARCHAR(255) NOT NULL,
    age TINYINT UNSIGNED NOT NULL,
    gender VARCHAR(6) NOT NULL,
    temperature TINYINT NOT NULL,
    days TINYINT NOT NULL,
    contagious VARCHAR(3) NOT NULL,
    treatment VARCHAR(100) NULL,
    disease VARCHAR(20) NULL,
    attended TINYINT(1) NOT NULL DEFAULT 0,
    received_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create attended table
CREATE TABLE IF NOT EXISTS attended (
    qid INT NOT NULL PRIMARY KEY,
    contact VARCHAR(15) NOT NULL,
    doctor VARCHAR(255) NOT NULL,
    treatment VARCHAR(255) NOT NULL,
    remarks VARCHAR(255) NULL,
    attended_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
