/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  To Mamiarilaza
 * Created: 8 nov. 2023
 */

CREATE DATABASE generalisation;

\c generalisation;

CREATE TABLE sexe (
    id_sexe SERIAL PRIMARY KEY,
    sexe VARCHAR(30)
);

INSERT INTO sexe (sexe) VALUES 
('Homme'),
('Femme');

CREATE SEQUENCE seq_person START WITH 3;
CREATE TABLE person (
    id_person VARCHAR(7) PRIMARY KEY,
    name VARCHAR(40),
    firstname VARCHAR(40),
    id_sexe INTEGER,
    date_naissance DATE,
    FOREIGN KEY(id_sexe) REFERENCES sexe(id_sexe)
);
INSERT INTO person (id_person, name, firstname, id_sexe, date_naissance) VALUES
('PER0001', 'MAMIARILAZA', 'To', 1, '2004-07-07'),
('PER0002', 'ANDRIANARIVELO', 'Sahaza', 2, '2005-04-17');

CREATE TABLE chien (
    id_chien SERIAL PRIMARY KEY,
    id_person VARCHAR(7),
    name VARCHAR(30),
    price DOUBLE PRECISION,
    FOREIGN KEY(id_person) REFERENCES person(id_person)
);
    
INSERT INTO chien (id_person, name, price) VALUES
('PER0001', 'Rockly', 120000),
('PER0002', 'Bouba', 300000),
('PER0001', 'Black', 200000);

-- Reinitialisation
ALTER SEQUENCE seq_chien RESTART WITH 1;
DELETE FROM chien;

ALTER SEQUENCE seq_person RESTART WITH 1;
DELETE FROM person;

ALTER SEQUENCE seq_sexe RESTART WITH 1;
DELETE FROM sexe;