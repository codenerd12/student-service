drop table IF EXISTS students;

CREATE TABLE students(
id int NOT NULL AUTO_INCREMENT, 
name varchar(50), 
department varchar(50), 
PRIMARY KEY(id));

INSERT INTO students (id, name, department) VALUES (101, 'Manish', 'MCA');
INSERT INTO students (id, name, department) VALUES (102, 'Akki', 'MCA');
INSERT INTO students (id, name, department) VALUES (103, 'Sandeep', 'B-tech');
INSERT INTO students (id, name, department) VALUES (104, 'Alok', 'MBA');
INSERT INTO students (id, name, department) VALUES (105, 'Neelam', 'MBA');