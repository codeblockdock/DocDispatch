-- Create Database
CREATE DATABASE IF NOT EXISTS docdispatch;
USE docdispatch;

-- Create attended table
CREATE TABLE IF NOT EXISTS attended (
    qid INT NOT NULL PRIMARY KEY,
    doctor VARCHAR(255) NOT NULL,
    hospital VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    diagnosis VARCHAR(255) NOT NULL,
    treatment VARCHAR(255) NOT NULL,
    advice VARCHAR(255),
    appointment VARCHAR(50) NOT NULL,
    attended_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create patient_location table
CREATE TABLE IF NOT EXISTS patient_location (
    qid INT NOT NULL PRIMARY KEY,
    pincode VARCHAR(10) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL
);

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
    symptoms JSON NOT NULL,
    disease VARCHAR(30) NULL,
    attended TINYINT(1) NOT NULL DEFAULT 0,
    received_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create predicted_disease table
CREATE TABLE IF NOT EXISTS predicted_disease (
    qid INT NOT NULL PRIMARY KEY,
    symptoms JSON NOT NULL,
    disease VARCHAR(255) NOT NULL
);
