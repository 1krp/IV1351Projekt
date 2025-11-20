-- ADD ENUM VALUES FOR study_period AND teaching_activity --

INSERT INTO study_period (period_name) VALUES ('P1'),('P2'),('P3'),('P4');
INSERT INTO teaching_activity (activity, factor) VALUES ('Lecture',3.6),('Lab',2.4),('Tutorial',2.4),('Seminar',1.8);
INSERT INTO activity_constants (activity_name, fixed_hours, hp_factor, students_factor) VALUES ('EXAM',32,0,0.725),('ADMIN',28,2,0.2);
INSERT INTO employment_constants (max_courses) VALUES (4); -- ALLOWED MAX COURSES FOR A TEACHER DURING A STUDY PERIOD --

-- DUMMY DATA --

INSERT INTO course_layout (course_code, course_name) VALUES ('IV1351','Data Storage Paradigms'),('IV1350','Objekt Orienterad Design'),('IX1500','Diskret Matematik'),('IS1500','Datorteknik');
INSERT INTO course_version (course_layout_id, max_students, min_students, hp) VALUES (2,300,100,7.5),(3,310,90,7.5),(4,200,75,7.5),(5,380,100,9.0);
INSERT INTO person (person_number, first_name, last_name, street, zip, city) VALUES ('5112079999','Anders','Hansen','Gatan 1','123','Staden'),('7112059999','Peter','Hansen','Gatan 2','123','Staden'), ('0412059999','Johan','Hansen','Gatan 3','123','Staden'), ('9912059999','Findus','Hansen','Gatan 4','123','Staden');
INSERT INTO job_title (job_title) VALUES ('Lecturer'), ('Janitor'), ('Professor'), ('Teacher-assistant'), ('Department-manager');
INSERT INTO department (department_name, manager, street, zip, city) VALUES ('EECS',null,'Brinellvägen 8','114 28','Stockholm');
INSERT INTO employee (salary, person_id, job_title_id, manager, department_id) VALUES (30000,2,2,null,2);
INSERT INTO course_instance (num_students, study_year, course_version_id) VALUES (111,'2025',2),(260,'2025',3),(170,'2025',4),(300,'2025',5);
INSERT INTO course_instance_study_period (course_instance_id, study_period_id) VALUES (4,2),(4,3),(2,2),(3,2);
INSERT INTO planned_activity (course_instance_id, planned_hours, activity_id) VALUES (2, 10, 2), (3, 10, 2), (4, 10, 2), (5, 10, 2);
INSERT INTO activity_employee (course_instance_id, employee_id) VALUES (2,2), (3,2), (4,2), (5,2);