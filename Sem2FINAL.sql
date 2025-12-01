\c postgres
DROP DATABASE iv_db;
CREATE DATABASE iv_db;
\c iv_db;

CREATE TABLE activity_constants (
 id SERIAL PRIMARY KEY,
 activity_name VARCHAR(100) NOT NULL,
 fixed_hours INT NOT NULL,
 hp_factor DOUBLE PRECISION NOT NULL,
 students_factor DOUBLE PRECISION NOT NULL
);


CREATE TABLE course_layout (
 id SERIAL PRIMARY KEY,
 course_code VARCHAR(500) NOT NULL,
 course_name VARCHAR(100) NOT NULL
);


CREATE TABLE course_version (
 id SERIAL PRIMARY KEY,
 course_layout_id INT NOT NULL,
 max_students INT NOT NULL,
 min_students INT NOT NULL,
 hp DOUBLE PRECISION NOT NULL
);


CREATE TABLE department (
 id SERIAL PRIMARY KEY,
 department_name VARCHAR(500) NOT NULL,
 manager INT,
 street VARCHAR(100) NOT NULL,
 zip VARCHAR(10) NOT NULL,
 city VARCHAR(100) NOT NULL
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
 street VARCHAR(500) NOT NULL,
 zip VARCHAR(50) NOT NULL,
 city VARCHAR(50) NOT NULL
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
 name VARCHAR(500) NOT NULL,
 description VARCHAR(500) NOT NULL
);


CREATE TABLE study_period (
 id SERIAL PRIMARY KEY,
 period_name VARCHAR(2) NOT NULL
);


CREATE TABLE teaching_activity (
 id SERIAL PRIMARY KEY,
 activity_name VARCHAR(100) NOT NULL,
 factor DOUBLE PRECISION NOT NULL
);


CREATE TABLE course_instance (
 id SERIAL PRIMARY KEY,
 num_students INT NOT NULL,
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
 id SERIAL PRIMARY KEY,
 employee_id INT NOT NULL,
 course_instance_id INT NOT NULL,
 planned_hours INT NOT NULL,
 allocated_hours INT NOT NULL,
 activity_id INT NOT NULL
);


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


ALTER TABLE employee_skill ADD CONSTRAINT FK_employee_skill_0 FOREIGN KEY (skill_id) REFERENCES skill (id) ON DELETE SET NULL;
ALTER TABLE employee_skill ADD CONSTRAINT FK_employee_skill_1 FOREIGN KEY (employee_id) REFERENCES employee (id) ON DELETE SET NULL;


ALTER TABLE person_phone ADD CONSTRAINT FK_person_phone_0 FOREIGN KEY (person_id) REFERENCES person (id) ON DELETE CASCADE;
ALTER TABLE person_phone ADD CONSTRAINT FK_person_phone_1 FOREIGN KEY (phone_number) REFERENCES phone (phone_number) ON DELETE CASCADE;


ALTER TABLE planned_activity ADD CONSTRAINT FK_planned_activity_0 FOREIGN KEY (employee_id) REFERENCES employee (id);
ALTER TABLE planned_activity ADD CONSTRAINT FK_planned_activity_1 FOREIGN KEY (course_instance_id) REFERENCES course_instance (id);
ALTER TABLE planned_activity ADD CONSTRAINT FK_planned_activity_2 FOREIGN KEY (activity_id) REFERENCES teaching_activity (id);


CREATE MATERIALIZED VIEW admin_and_exam_hours_per_employee_and_course AS
    SELECT
        ci.id ciid,
        emp.num_employees,
        ROUND(
            (
                (SELECT ac.fixed_hours + ac.students_factor * ci.num_students
                 FROM activity_constants ac
                 WHERE ac.activity_name = 'ADMIN')
            / emp.num_employees)::numeric, 2
        ) AS admin_hours_per_employee,
        ROUND(
            (
                (SELECT ac.fixed_hours + ac.students_factor * ci.num_students
                 FROM activity_constants ac
                 WHERE ac.activity_name = 'EXAM')
            / emp.num_employees)::numeric, 2
        ) AS exam_hours_per_employee
    FROM course_instance ci
    JOIN course_version cv ON ci.course_version_id=cv.id
    JOIN (
        SELECT
            ci.id ciid,
            COUNT(DISTINCT employee_id) as num_employees
        FROM planned_activity pa
        JOIN course_instance ci ON ci.id=pa.course_instance_id
        GROUP BY ci.id
        ORDER BY ci.id
    ) emp
        ON ci.id = emp.ciid;

CREATE MATERIALIZED VIEW courses_per_employee AS
    SELECT 
        e.id eid, 
        p.first_name, 
        sp.period_name,
        COUNT(DISTINCT ci.id) num_courses
    FROM employee e
    JOIN person p ON p.id=e.person_id
    JOIN planned_activity pa ON pa.employee_id=e.id
    JOIN course_instance ci ON pa.course_instance_id=ci.id
    JOIN course_instance_study_period cisp ON ci.id=cisp.course_instance_id
    JOIN study_period sp ON sp.id=cisp.study_period_id AND sp.period_name = 'P1' -- Period selection
    GROUP BY e.id, sp.period_name, p.first_name
    HAVING COUNT(DISTINCT ci.id) > (SELECT max_courses FROM employment_constants ec WHERE ec.id=1) -- (Checks if num_courses > 4)
    ORDER BY sp.period_name;

CREATE MATERIALIZED VIEW teacher_allocated_hours_summary AS 
    WITH paJta AS(
        SELECT course_instance_id, 
            SUM(CASE WHEN activity_name = 'Lecture' THEN allocated_hours * factor ELSE 0 END) AS lecture_hours,
            SUM(CASE WHEN activity_name = 'Seminar' THEN allocated_hours * factor ELSE 0 END) AS seminar_hours,
            SUM(CASE WHEN activity_name = 'Lab' THEN allocated_hours * factor ELSE 0 END) AS lab_hours,
            SUM(CASE WHEN activity_name = 'Tutorial' THEN allocated_hours * factor ELSE 0 END) AS tutorial_hours,
            SUM(CASE WHEN activity_name = 'Other' THEN allocated_hours * factor ELSE 0 END) AS other_hours
        FROM planned_activity pa
        JOIN teaching_activity ta ON pa.activity_id=ta.id
        GROUP BY course_instance_id
    ), ci_num_e AS(
        SELECT
            pa.course_instance_id,
            COUNT(DISTINCT employee_id) AS num_employees
        FROM planned_activity pa
        GROUP BY pa.course_instance_id
    ), examAdminHrs AS(
        SELECT ci.id ciid,  
            ROUND((SUM(CASE WHEN activity_name = 'EXAM' THEN (fixed_hours + hp_factor*hp + num_students*students_factor)/ne.num_employees ELSE 0 END))::numeric, 2) exam_hours,
            ROUND((SUM(CASE WHEN activity_name = 'ADMIN' THEN (fixed_hours + hp_factor*hp + num_students*students_factor)/ne.num_employees ELSE 0 END))::numeric, 2) admin_hours
        FROM course_instance ci
        JOIN course_version cv ON ci.course_version_id=cv.id
        JOIN ci_num_e ne ON ne.course_instance_id=ci.id 
        CROSS JOIN activity_constants ac
        GROUP BY ci.id
    ), task2view AS(
        SELECT DISTINCT
            cl.course_code, 
            ci.id ciid, 
            cv.hp,
            p.first_name,
            p.last_name,
            jt.job_title designation,
            paJta.lecture_hours, 
            paJta.tutorial_hours,
            paJta.lab_hours,
            paJta.seminar_hours,
            paJta.other_hours,
            eah.exam_hours,
            eah.admin_hours,
            (eah.exam_hours + eah.admin_hours + paJta.lecture_hours + paJta.seminar_hours + paJta.lab_hours + tutorial_hours + paJta.other_hours) total_hours
        FROM course_instance ci 
        JOIN course_version cv ON ci.course_version_id=cv.id AND ci.study_year = '2025'
        JOIN course_layout cl ON cl.id=cv.course_layout_id
        JOIN paJta ON paJta.course_instance_id=ci.id
        JOIN examAdminHrs eah ON eah.ciid=ci.id
        JOIN planned_activity pa ON ci.id=pa.course_instance_id
        JOIN employee e ON pa.employee_id=e.id
        JOIN person p ON e.person_id=p.id
        JOIN job_title jt ON jt.id=e.job_title_id
        --GROUP BY course_code, ci.id, hp, first_name, last_name, designation, lecture_hours, seminar_hours, lab_hours, tutorial_hours, other_hours, exam, admin, total_hours 
    )
    SELECT * 
    FROM task2view ORDER BY ciid;