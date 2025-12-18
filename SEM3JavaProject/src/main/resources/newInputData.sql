-- ADD VALUES FOR study_period AND teaching_activity -------------------------

INSERT INTO study_period (period_name) VALUES
('P1'),
('P2'),
('P3'),
('P4');

INSERT INTO teaching_activity (activity_name, factor) VALUES
('Lecture', 3.6),
('Lab', 2.4),
('Tutorial', 2.4),
('Seminar', 1.8),
('Other', 1.0);

INSERT INTO activity_constants (activity_name, fixed_hours, hp_factor, students_factor) VALUES
('EXAM', 32, 0, 0.725),
('ADMIN', 28, 2, 0.2);

INSERT INTO employment_constants (max_courses) VALUES (4); -- allowed max courses per study period


-- BASE STRUCTURE NEEDED FOR FKs -------------------------------------------

-- Job titles
INSERT INTO job_title (job_title) VALUES
('Lecturer'),
('Janitor'),
('Professor'),
('Teacher-assistant'),
('Department-manager');

-- One department
INSERT INTO department (department_name, manager, street, zip, city) VALUES
('EECS', NULL, 'Brinellvägen 8', '114 28', 'Stockholm');

-- Courses and versions
INSERT INTO course_layout (course_code, course_name) VALUES
('IV1351','Data Storage Paradigms'),
('IV1350','Objekt Orienterad Design'),
('IX1500','Diskret Matematik'),
('IS1500','Datorteknik'),
('DD1321','Programmeringsteknik'),
('SF1624','Algebra och Geometri');

INSERT INTO course_version (course_layout_id, max_students, min_students, hp) VALUES
(1,300,100,7.5),
(2,310,90,7.5),
(3,200,75,7.5),
(4,380,100,9.0),
(5,250,80,7.5),
(6,220,70,7.5);


-- PERSONS ------------------------------------------------------------------

INSERT INTO person (person_number, first_name, last_name, street, zip, city) VALUES
('7001021234','Martin','Holm','Gatan 9','123','Staden'),
('9408113344','Sara','Lund','Gatan 10','123','Staden'),
('8810225566','Jenny','Ek','Gatan 11','123','Staden'),
('7503156677','Patrik','Sandberg','Gatan 12','123','Staden'),
('9201018899','Oscar','Berg','Gatan 13','123','Staden'),
('8305227788','Filippa','Nyström','Gatan 14','123','Staden'),
('9603104455','David','Sjögren','Gatan 15','123','Staden'),
('9901026677','Linnea','Björk','Gatan 16','123','Staden'),
('8104129988','Tobias','Wester','Gatan 17','123','Staden'),
('8208225544','Emelie','Nord','Gatan 18','123','Staden');


-- EMPLOYEES (10 employees, ids will be 1..10) ------------------------------

INSERT INTO employee (person_id, job_title_id, manager, department_id) VALUES
(1, 1, NULL, 1),  -- id = 1, manager
(2, 4, 1,    1),  -- id = 2
(3, 3, 1,    1),  -- id = 3
(4, 1, 1,    1),  -- id = 4
(5, 4, 1,    1),  -- id = 5
(6, 2, 1,    1),  -- id = 6
(7, 1, 1,    1),  -- id = 7
(8, 4, 1,    1),  -- id = 8
(9, 3, 1,    1),  -- id = 9
(10,2, 1,    1);  -- id = 10


-- SALARIES FOR EMPLOYEES 1..10 --------------------------------------------

INSERT INTO employee_salary (employee_id, salary_enforcement_date, salary_per_hour) VALUES
(1,'2025-01-01',250.0),
(1,'2025-06-01',300.0),
(2,'2025-01-01',240.0),
(2,'2025-06-01',260.0),
(3,'2025-01-01',380.0),
(3,'2025-06-01',420.0),
(4,'2025-01-01',270.0),
(4,'2025-06-01',295.0),
(5,'2025-01-01',250.0),
(5,'2025-06-01',265.0),
(6,'2025-01-01',200.0),
(6,'2025-06-01',230.0),
(7,'2025-01-01',300.0),
(7,'2025-06-01',315.0),
(8,'2025-01-01',250.0),
(8,'2025-06-01',255.0),
(9,'2025-01-01',400.0),
(9,'2025-06-01',450.0),
(10,'2025-01-01',200.0),
(10,'2025-06-01',205.0);


-- COURSE INSTANCES (ALL 2025, ids 1..15) ----------------------------------

INSERT INTO course_instance (num_students, study_year, course_version_id) VALUES
(200,'2025',1), -- id 1
(111,'2025',1), -- id 2
(260,'2025',2), -- id 3
(170,'2025',3), -- id 4
(300,'2025',4), -- id 5
(190,'2025',2), -- id 6
(150,'2025',3), -- id 7
(220,'2025',5), -- id 8
(180,'2025',6), -- id 9
(210,'2025',1), -- id 10
(160,'2025',5), -- id 11
(180,'2025',4), -- id 12
(140,'2025',2), -- id 13
(175,'2025',3), -- id 14
(195,'2025',6); -- id 15


-- STUDY PERIOD MAPPINGS ----------------------------------------------------

INSERT INTO course_instance_study_period (course_instance_id, study_period_id) VALUES
(3,1),
(3,2),
(1,1),
(2,1),
(4,2),
(5,2),
(6,1),
(7,1),
(8,2),
(9,1),
(10,2),
(11,1),
(12,2),
(13,1),
(14,2),
(15,1);


-- PLANNED ACTIVITIES -------------------------------------------------------
-- activity_id refers to teaching_activity.id (1..5)

INSERT INTO planned_activity (employee_id, course_instance_id, planned_hours, allocated_hours, activity_id) VALUES
-- Employee 1
(1, 1, 20, 15, 1),
(1, 1, 40, 40, 2),
(1, 1, 80, 70, 3),
(1, 1, 80, 60, 4),
(1, 1,100, 90, 5),
(1, 3, 10, 10, 1),
(1, 4, 10, 10, 1),
(1, 4, 10,  8, 1),

-- Employee 2
(2, 1, 20, 18, 1),
(2, 1, 40, 20, 2),
(2, 1, 80, 70, 3),
(2, 1, 80,120, 4),
(2, 1,100, 90, 5),
(2, 3, 10, 15, 1),
(2, 4, 10,  8, 1),
(2, 4, 10,  6, 1),

-- Employee 3
(3, 1, 20, 20, 1),
(3, 3, 30, 25, 2),
(3, 5, 40, 50, 3),
(3, 6, 20, 10, 4),

-- Employee 4
(4, 2, 15, 18, 1),
(4, 7, 25, 20, 2),
(4, 8, 10, 15, 3),
(4, 9, 30, 25, 4),
(4,10, 20, 10, 5),

-- Employee 5
(5, 5, 30, 20, 1),
(5, 9, 40, 50, 2),
(5,11, 35, 30, 3),
(5,12, 25, 40, 4),
(5,13, 60, 45, 5),

-- Employee 6
(6, 6, 35, 30, 1),
(6,10, 25, 20, 2),
(6,12, 45, 55, 3),
(6,14, 70, 50, 4),

-- Employee 7
(7, 8, 20, 30, 1),
(7,11, 30, 25, 2),
(7,13, 50, 35, 3),
(7,15, 80, 60, 4),

-- Employee 8
(8, 7, 10, 12, 1),
(8, 9, 15, 10, 2),
(8,14, 25, 30, 3),
(8,15, 45, 55, 4),

-- Employee 9
(9, 6, 40, 30, 1),
(9, 8, 60, 70, 2),
(9,12, 20, 25, 3),
(9,13, 80, 60, 4),
(9,15, 50, 30, 5),

-- Employee 10
(10, 9, 15, 10, 1),
(10,10, 35, 45, 2),
(10,11, 25, 20, 3),
(10,14, 10, 18, 1),
(10,15, 60, 30, 4),

-- Extra extremes for ph/ah ratio testing
(6,15,120, 60, 4),  -- > 2.0
(7,14, 20, 40, 5),  -- < 0.50
(8,13,150, 90, 5),  -- > 1.66
(9,11, 20, 35, 4),  -- < 0.57
(4,10, 90, 60, 3),  -- > 1.50
(5, 7, 30, 55, 2);  -- < 0.55
