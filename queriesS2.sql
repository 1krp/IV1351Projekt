

--Task 1 --

WITH paJta AS(
    SELECT course_instance_id, 
            SUM(CASE WHEN activity_name = 'Lecture' THEN planned_hours * factor ELSE 0 END) AS lecture_hours,
            SUM(CASE WHEN activity_name = 'Seminar' THEN planned_hours * factor ELSE 0 END) AS seminar_hours,
            SUM(CASE WHEN activity_name = 'Lab' THEN planned_hours * factor ELSE 0 END) AS lab_hours,
            SUM(CASE WHEN activity_name = 'Tutorial' THEN planned_hours * factor ELSE 0 END) AS tutorial_hours,
            SUM(CASE WHEN activity_name = 'Other' THEN planned_hours * factor ELSE 0 END) AS other_hours
        FROM planned_activity pa
        JOIN teaching_activity ta ON pa.activity_id=ta.id
        GROUP BY course_instance_id
),  examAdminHrs AS(
    SELECT ci.id ciid,  
        SUM(CASE WHEN activity_name = 'EXAM' THEN fixed_hours + hp_factor*hp + num_students*students_factor ELSE 0 END) exam,
        SUM(CASE WHEN activity_name = 'ADMIN' THEN fixed_hours + hp_factor*hp + num_students*students_factor ELSE 0 END) AS admin
    From course_instance ci
    JOIN course_version cv ON ci.course_version_id=cv.id
    JOIN activity_constants ac ON true
    GROUP BY ci.id
), task1view AS(
    SELECT  
        cl.course_code, 
        ci.id ciid, 
        cv.hp, 
        ci.num_students, 
        sp.period_name, 
        paJta.lecture_hours, 
        paJta.tutorial_hours,
        paJta.lab_hours, 
        paJta.seminar_hours,
        paJta.other_hours,
        eah.exam,
        eah.admin,
        (eah.exam + eah.admin + paJta.lecture_hours + paJta.seminar_hours + paJta.lab_hours + pajta.tutorial_hours + paJta.other_hours) total_hours
    FROM course_version cv
    JOIN course_layout cl ON cl.id=cv.course_layout_id
    JOIN course_instance ci ON ci.course_version_id=cv.id AND ci.study_year = '2025'
    JOIN course_instance_study_period cisp ON cisp.course_instance_id=ci.id
    JOIN study_period sp ON cisp.study_period_id=sp.id 
    JOIN paJta ON paJta.course_instance_id=ci.id     
    JOIN examAdminHrs eah ON eah.ciid=ci.id         
)
    SELECT *
    FROM task1view ORDER BY ciid, period_name;


--Task 2--

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
        (eah.exam_hours + eah.admin_hours + paJta.lecture_hours + paJta.seminar_hours + paJta.lab_hours + pajta.tutorial_hours + paJta.other_hours) total_hours
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

--Task 3--

WITH paJta AS(
    SELECT course_instance_id, 
        SUM(CASE WHEN activity_name = 'Lecture' THEN planned_hours*factor ELSE 0 END) AS lecture_hours,
        SUM(CASE WHEN activity_name = 'Seminar' THEN planned_hours*factor ELSE 0 END) AS seminar_hours,
        SUM(CASE WHEN activity_name = 'Lab' THEN planned_hours*factor ELSE 0 END) AS lab_hours,
        SUM(CASE WHEN activity_name = 'Tutorial' THEN planned_hours*factor ELSE 0 END) AS tutorial_hours,
        SUM(CASE WHEN activity_name = 'Other' THEN planned_hours*factor ELSE 0 END) AS other_hours
    FROM planned_activity pa
    JOIN teaching_activity ta ON pa.activity_id=ta.id
    GROUP BY course_instance_id
), ci_num_e AS(
    SELECT
        pa.course_instance_id, sp.period_name,
        COUNT(DISTINCT employee_id) AS num_employees
    FROM planned_activity pa
    JOIN teaching_activity ta ON pa.activity_id=ta.id
    JOIN employee e ON e.id=pa.employee_id 
    JOIN course_instance ci ON ci.id=pa.course_instance_id
    JOIN course_instance_study_period cisp ON cisp.course_instance_id=ci.id
    JOIN study_period sp ON cisp.study_period_id=sp.id
    GROUP BY pa.course_instance_id, sp.period_name
), examAdminHrs AS(
    SELECT ci.id ciid,  
        ROUND((SUM(CASE WHEN activity_name = 'EXAM' THEN (fixed_hours + hp_factor*hp + num_students*students_factor)/ne.num_employees ELSE 0 END))::numeric, 2) exam_hours,
        ROUND((SUM(CASE WHEN activity_name = 'ADMIN' THEN (fixed_hours + hp_factor*hp + num_students*students_factor)/ne.num_employees ELSE 0 END))::numeric, 2) admin_hours
    From course_instance ci
    JOIN course_version cv ON ci.course_version_id=cv.id
    JOIN ci_num_e ne ON ne.course_instance_id=ci.id 
    CROSS JOIN activity_constants ac
    GROUP BY ci.id
), task3view AS(
    SELECT DISTINCT
        cl.course_code, 
        ci.id ciid, 
        cv.hp,
        sp.period_name,
        p.first_name,
        p.last_name,
        paJta.lecture_hours,
        paJta.tutorial_hours,
        paJta.lab_hours,
        paJta.seminar_hours,    
        paJta.other_hours,
        eah.admin_hours,
        eah.exam_hours,
        (eah.exam_hours + eah.admin_hours + paJta.lecture_hours + paJta.seminar_hours + paJta.lab_hours + pajta.tutorial_hours + paJta.other_hours) total_hours
    FROM course_instance ci 
    JOIN course_version cv ON ci.course_version_id=cv.id AND ci.study_year='2025'
    JOIN course_layout cl ON cl.id=cv.course_layout_id 
    LEFT JOIN (
        course_instance_study_period cisp
        JOIN study_period sp ON cisp.study_period_id=sp.id) 
        ON cisp.course_instance_id=ci.id
    JOIN planned_activity pa ON ci.id=pa.course_instance_id
    JOIN employee e ON pa.employee_id=e.id AND pa.employee_id = 1 -- what teacher to look at
    JOIN person p ON e.person_id=p.id
    JOIN paJta ON paJta.course_instance_id=ci.id
    JOIN examAdminHrs eah ON eah.ciid=ci.id
    ORDER BY p.first_name
)
SELECT * 
FROM task3view ORDER BY ciid;


-- With exam admin hours view --

WITH paJta AS(
    SELECT course_instance_id, 
        SUM(CASE WHEN activity_name = 'Lecture' THEN planned_hours ELSE 0 END) AS lecture_hours,
        SUM(CASE WHEN activity_name = 'Seminar' THEN planned_hours ELSE 0 END) AS seminar_hours,
        SUM(CASE WHEN activity_name = 'Lab' THEN planned_hours ELSE 0 END) AS lab_hours,
        SUM(CASE WHEN activity_name = 'Tutorial' THEN planned_hours ELSE 0 END) AS tutorial_hours,
        SUM(CASE WHEN activity_name = 'Other' THEN planned_hours ELSE 0 END) AS other_hours
    FROM planned_activity pa
    JOIN teaching_activity ta ON pa.activity_id=ta.id
    GROUP BY course_instance_id
), task3view AS(
    SELECT DISTINCT
        cl.course_code, 
        ci.id ciid, 
        cv.hp,
        p.first_name,
        p.last_name,
        jt.job_title designation,
        paJta.lecture_hours, 
        paJta.seminar_hours, 
        paJta.lab_hours, 
        paJta.tutorial_hours, 
        paJta.other_hours,
        eah.exam_hours_per_employee,
        eah.admin_hours_per_employee,
        (eah.exam_hours_per_employee + eah.admin_hours_per_employee + paJta.lecture_hours + paJta.seminar_hours + paJta.lab_hours + pajta.tutorial_hours + paJta.other_hours) total_hours
    FROM course_version cv
    JOIN course_layout cl ON cl.id=cv.course_layout_id
    JOIN course_instance ci ON ci.course_version_id=cv.id AND ci.study_year = '2025'
    JOIN paJta ON paJta.course_instance_id=ci.id
    JOIN admin_and_exam_hours_per_employee_and_course eah ON eah.ciid=ci.id
    JOIN planned_activity pa ON ci.id=pa.course_instance_id
    JOIN employee e ON pa.employee_id=e.id
    JOIN person p ON e.person_id=p.id
    JOIN job_title jt ON jt.id=e.job_title_id
)
SELECT * 
FROM task3view


--Task 4--

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
ORDER BY sp.period_name
