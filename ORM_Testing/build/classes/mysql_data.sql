/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  To Mamiarilaza
 * Created: 8 nov. 2023
 */

CREATE DATABASE generalisation;

USE generalisation;

CREATE TABLE seq_sexe ( sequence_value INT );
CREATE TABLE sexe (
    id_sexe INT AUTO_INCREMENT PRIMARY KEY,
    sexe VARCHAR(30)
);

INSERT INTO sexe (sexe) VALUES 
('Homme'),
('Femme');

CREATE TABLE person (
    id_person VARCHAR(7) PRIMARY KEY,
    name VARCHAR(40),
    firstname VARCHAR(40),
    id_sexe INT,
    date_naissance DATE,
    FOREIGN KEY(id_sexe) REFERENCES sexe(id_sexe)
);

INSERT INTO person (id_person, name, firstname, id_sexe, date_naissance) VALUES
('PER0001', 'MAMIARILAZA', 'To', 1, '2004-07-07'),
('PER0002', 'ANDRIANARIVELO', 'Sahaza', 2, '2005-04-17');

CREATE TABLE chien (
    id_chien VARCHAR(7),
    id_person VARCHAR(7),
    name VARCHAR(30),
    price DOUBLE,
    FOREIGN KEY(id_person) REFERENCES person(id_person)
);
    
INSERT INTO chien(id_chien, id_person, name, price) VALUES
('CHE0001', 'PER0001', 'Rockly', 120000),
('CHE0002', 'PER0002', 'Bouba', 300000),
('CHE0003', 'PER0001', 'Black', 200000);

-- Reinitialization
ALTER TABLE chien AUTO_INCREMENT = 1;
TRUNCATE TABLE chien;

ALTER TABLE person AUTO_INCREMENT = 1;
TRUNCATE TABLE person;

ALTER TABLE sexe AUTO_INCREMENT = 1;
TRUNCATE TABLE sexe;
