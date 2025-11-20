\c postgres
DROP DATABASE iv_db;
CREATE DATABASE iv_db;
\c iv_db;

CREATE TABLE activity_constants (
 id SERIAL PRIMARY KEY,
 activity_name VARCHAR(100),
 fixed_hours INT,
 hp_factor DOUBLE PRECISION,
 students_factor DOUBLE PRECISION
);


CREATE TABLE course_layout (
 id SERIAL PRIMARY KEY,
 course_code VARCHAR(500) NOT NULL,
 course_name VARCHAR(100) NOT NULL
);


CREATE TABLE course_version (
 id SERIAL PRIMARY KEY,
 course_layout_id INT NOT NULL,
 max_students INT,
 min_students INT,
 hp DOUBLE PRECISION
);


CREATE TABLE department (
 id SERIAL PRIMARY KEY,
 department_name VARCHAR(500) NOT NULL,
 manager INT,
 street VARCHAR(100),
 zip VARCHAR(10),
 city VARCHAR(100)
);


CREATE TABLE email (
 email VARCHAR(500) NOT NULL
);

ALTER TABLE email ADD CONSTRAINT PK_email PRIMARY KEY (email);


CREATE TABLE employee (
 id SERIAL PRIMARY KEY,
 person_id INT NOT NULL,
 job_title_id INT NOT NULL,
 manager INT,
 department_id INT NOT NULL
);


CREATE TABLE employee_salary (
 id SERIAL PRIMARY KEY,
 employee_id INT NOT NULL,
 salary_enforcement_date DATE NOT NULL,
 salary_per_hour DOUBLE PRECISION NOT NULL
);


CREATE TABLE employment_constants (
 id SERIAL PRIMARY KEY,
 max_courses INT DEFAULT 4 NOT NULL
);


CREATE TABLE job_title (
 id SERIAL PRIMARY KEY,
 job_title VARCHAR(500) NOT NULL
);


CREATE TABLE person (
 id SERIAL PRIMARY KEY,
 person_number VARCHAR(12) NOT NULL,
 first_name VARCHAR(500) NOT NULL,
 last_name VARCHAR(500) NOT NULL,
 street VARCHAR(500),
 zip VARCHAR(50),
 city VARCHAR(50)
);


CREATE TABLE person_email (
 person_id INT NOT NULL,
 email VARCHAR(500) NOT NULL
);

ALTER TABLE person_email ADD CONSTRAINT PK_person_email PRIMARY KEY (person_id,email);


CREATE TABLE phone (
 phone_number VARCHAR(500) NOT NULL
);

ALTER TABLE phone ADD CONSTRAINT PK_phone PRIMARY KEY (phone_number);


CREATE TABLE skill (
 id SERIAL PRIMARY KEY,
 name VARCHAR(500),
 description VARCHAR(500)
);


CREATE TABLE study_period (
 id SERIAL PRIMARY KEY,
 period_name VARCHAR(2) NOT NULL
);


CREATE TABLE teaching_activity (
 id SERIAL PRIMARY KEY,
 activity VARCHAR(100),
 factor DOUBLE PRECISION
);


CREATE TABLE course_instance (
 id SERIAL PRIMARY KEY,
 num_students INT,
 study_year VARCHAR(4) NOT NULL,
 course_version_id INT NOT NULL
);


CREATE TABLE course_instance_study_period (
 study_period_id INT NOT NULL,
 course_instance_id INT NOT NULL
);

ALTER TABLE course_instance_study_period ADD CONSTRAINT PK_course_instance_study_period PRIMARY KEY (study_period_id,course_instance_id);


CREATE TABLE employee_skill (
 skill_id INT NOT NULL,
 employee_id INT NOT NULL
);

ALTER TABLE employee_skill ADD CONSTRAINT PK_employee_skill PRIMARY KEY (skill_id,employee_id);


CREATE TABLE person_phone (
 person_id INT NOT NULL,
 phone_number VARCHAR(500) NOT NULL
);

ALTER TABLE person_phone ADD CONSTRAINT PK_person_phone PRIMARY KEY (person_id,phone_number);


CREATE TABLE planned_activity (
 course_instance_id INT NOT NULL,
 planned_hours INT NOT NULL,
 activity_id INT NOT NULL
);

ALTER TABLE planned_activity ADD CONSTRAINT PK_planned_activity PRIMARY KEY (course_instance_id);


CREATE TABLE activity_employee (
 course_instance_id INT NOT NULL,
 employee_id INT NOT NULL
);

ALTER TABLE activity_employee ADD CONSTRAINT PK_activity_employee PRIMARY KEY (course_instance_id,employee_id);


ALTER TABLE course_version ADD CONSTRAINT FK_course_version_0 FOREIGN KEY (course_layout_id) REFERENCES course_layout (id);


ALTER TABLE department ADD CONSTRAINT FK_department_0 FOREIGN KEY (manager) REFERENCES employee (id);


ALTER TABLE employee ADD CONSTRAINT FK_employee_0 FOREIGN KEY (person_id) REFERENCES person (id);
ALTER TABLE employee ADD CONSTRAINT FK_employee_1 FOREIGN KEY (job_title_id) REFERENCES job_title (id);
ALTER TABLE employee ADD CONSTRAINT FK_employee_2 FOREIGN KEY (department_id) REFERENCES department (id);


ALTER TABLE employee_salary ADD CONSTRAINT FK_employee_salary_0 FOREIGN KEY (employee_id) REFERENCES employee (id);


ALTER TABLE person_email ADD CONSTRAINT FK_person_email_0 FOREIGN KEY (person_id) REFERENCES person (id) ON DELETE CASCADE;
ALTER TABLE person_email ADD CONSTRAINT FK_person_email_1 FOREIGN KEY (email) REFERENCES email (email) ON DELETE CASCADE;


ALTER TABLE course_instance ADD CONSTRAINT FK_course_instance_0 FOREIGN KEY (course_version_id) REFERENCES course_version (id);


ALTER TABLE course_instance_study_period ADD CONSTRAINT FK_course_instance_study_period_0 FOREIGN KEY (study_period_id) REFERENCES study_period (id);
ALTER TABLE course_instance_study_period ADD CONSTRAINT FK_course_instance_study_period_1 FOREIGN KEY (course_instance_id) REFERENCES course_instance (id);


ALTER TABLE employee_skill ADD CONSTRAINT FK_employee_skill_0 FOREIGN KEY (employee_id) REFERENCES employee (id) ON DELETE SET NULL;
ALTER TABLE employee_skill ADD CONSTRAINT FK_employee_skill_1 FOREIGN KEY (skill_id) REFERENCES skill (id) ON DELETE SET NULL;


ALTER TABLE person_phone ADD CONSTRAINT FK_person_phone_0 FOREIGN KEY (person_id) REFERENCES person (id) ON DELETE CASCADE;
ALTER TABLE person_phone ADD CONSTRAINT FK_person_phone_1 FOREIGN KEY (phone_number) REFERENCES phone (phone_number) ON DELETE CASCADE;


ALTER TABLE planned_activity ADD CONSTRAINT FK_planned_activity_0 FOREIGN KEY (course_instance_id) REFERENCES course_instance (id);
ALTER TABLE planned_activity ADD CONSTRAINT FK_planned_activity_1 FOREIGN KEY (activity_id) REFERENCES teaching_activity (id);


ALTER TABLE activity_employee ADD CONSTRAINT FK_activity_employee_0 FOREIGN KEY (employee_id) REFERENCES employee (id) ON DELETE SET NULL;
ALTER TABLE activity_employee ADD CONSTRAINT FK_activity_employee_1 FOREIGN KEY (course_instance_id) REFERENCES planned_activity (course_instance_id) ON DELETE SET NULL;


-- SET UP SERIAL ID GENERATORS --

ALTER TABLE employee_salary
ALTER COLUMN id
SET DEFAULT nextval('employee_salary_id_seq');
SELECT setval('employee_salary_id_seq', COALESCE((SELECT MAX(id) FROM employee_salary), 1));

ALTER TABLE activity_constants
ALTER COLUMN id
SET DEFAULT nextval('activity_constants_id_seq');
SELECT setval('activity_constants_id_seq', COALESCE((SELECT MAX(id) FROM activity_constants), 1));

ALTER TABLE employment_constants
ALTER COLUMN id
SET DEFAULT nextval('employment_constants_id_seq');
SELECT setval('employment_constants_id_seq', COALESCE((SELECT MAX(id) FROM employment_constants), 1));

ALTER TABLE person
ALTER COLUMN id
SET DEFAULT nextval('person_id_seq');
SELECT setval('person_id_seq', COALESCE((SELECT MAX(id) FROM person), 1));

ALTER TABLE department
ALTER COLUMN id
SET DEFAULT nextval('department_id_seq');
SELECT setval('department_id_seq', COALESCE((SELECT MAX(id) FROM department), 1));

ALTER TABLE employee
ALTER COLUMN id
SET DEFAULT nextval('employee_id_seq');
SELECT setval('employee_id_seq', COALESCE((SELECT MAX(id) FROM employee), 1));

ALTER TABLE course_layout
ALTER COLUMN id
SET DEFAULT nextval('course_layout_id_seq');
SELECT setval('course_layout_id_seq', COALESCE((SELECT MAX(id) FROM course_layout), 1));

ALTER TABLE course_version
ALTER COLUMN id
SET DEFAULT nextval('course_version_id_seq');
SELECT setval('course_version_id_seq', COALESCE((SELECT MAX(id) FROM course_version), 1));

ALTER TABLE course_instance
ALTER COLUMN id
SET DEFAULT nextval('course_instance_id_seq');
SELECT setval('course_instance_id_seq', COALESCE((SELECT MAX(id) FROM course_instance), 1));

ALTER TABLE study_period
ALTER COLUMN id
SET DEFAULT nextval('study_period_id_seq');
SELECT setval('study_period_id_seq', COALESCE((SELECT MAX(id) FROM study_period), 1));

ALTER TABLE teaching_activity
ALTER COLUMN id
SET DEFAULT nextval('teaching_activity_id_seq');
SELECT setval('teaching_activity_id_seq', COALESCE((SELECT MAX(id) FROM teaching_activity), 1));

ALTER TABLE job_title
ALTER COLUMN id
SET DEFAULT nextval('job_title_id_seq');
SELECT setval('job_title_id_seq', COALESCE((SELECT MAX(id) FROM job_title), 1));

ALTER TABLE skill
ALTER COLUMN id
SET DEFAULT nextval('skill_id_seq');
SELECT setval('skill_id_seq', COALESCE((SELECT MAX(id) FROM skill), 1));
