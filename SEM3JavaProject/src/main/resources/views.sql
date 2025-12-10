CREATE VIEW admin_and_exam_hours_per_employee_and_course AS
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


CREATE VIEW courses_per_employee AS
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

CREATE MATERIALIZED VIEW teacher_allocated_hours_summary AS -- task 2
    WITH paJta AS(
        SELECT course_instance_id, p.first_name, p.last_name, jt.job_title,
            SUM(CASE WHEN activity_name = 'Lecture' THEN pa.allocated_hours * factor ELSE 0 END) AS lecture_hours,
            SUM(CASE WHEN activity_name = 'Seminar' THEN pa.allocated_hours * factor ELSE 0 END) AS seminar_hours,
            SUM(CASE WHEN activity_name = 'Lab' THEN pa.allocated_hours * factor ELSE 0 END) AS lab_hours,
            SUM(CASE WHEN activity_name = 'Tutorial' THEN pa.allocated_hours * factor ELSE 0 END) AS tutorial_hours,
            SUM(CASE WHEN activity_name = 'Other' THEN pa.allocated_hours * factor ELSE 0 END) AS other_hours
        FROM planned_activity pa
        JOIN teaching_activity ta ON pa.activity_id=ta.id
        JOIN employee e ON e.id=pa.employee_id
        JOIN person p ON p.id=e.person_id
        JOIN job_title jt ON jt.id=e.job_title_id
        GROUP BY course_instance_id, p.first_name, p.last_name, jt.job_title
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
    )
    SELECT DISTINCT
        cl.course_code, 
        ci.id ciid, 
        cv.hp,
        paJta.job_title designation,
        paJta.first_name,
        paJta.last_name,
        paJta.lecture_hours, 
        paJta.tutorial_hours,
        paJta.lab_hours,
        paJta.seminar_hours,
        paJta.other_hours,
        eah.exam_hours,
        eah.admin_hours,
        (eah.exam_hours + eah.admin_hours + paJta.lecture_hours + paJta.seminar_hours + paJta.lab_hours + pajta.tutorial_hours + paJta.other_hours) total_hours
    FROM course_instance ci 
    JOIN course_version cv ON ci.course_version_id=cv.id AND ci.study_year = '2025'
    JOIN course_layout cl ON cl.id=cv.course_layout_id
    JOIN paJta ON paJta.course_instance_id=ci.id
    JOIN examAdminHrs eah ON eah.ciid=ci.id
    JOIN planned_activity pa ON ci.id=pa.course_instance_id
    ORDER BY ciid


SELECT 
    cl.course_code, 
    ci.id AS course_instance, 
    sp.period_name AS study_period
FROM course_instance ci JOIN course_version cv ON ci.course_version_id = cv.id
    JOIN course_layout cl ON cv.course_layout_id = cl.id
    JOIN course_instance_study_period cisp ON ci.id = cisp.course_instance_id
    JOIN study_period sp ON cisp.study_period_id = sp.id
WHERE ci.id = 3;