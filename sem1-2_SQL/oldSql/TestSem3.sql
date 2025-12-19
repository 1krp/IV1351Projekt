SELECT sp.period_name
FROM course_instance ci
JOIN course_instance_study_period cisp ON ci.id = cisp.course_instance_id
JOIN study_period sp ON cisp.study_period_id = sp.id
WHERE ci.id=1;


--PAs for emp
SELECT 
    pa.id paid,
    ci.id ciid,
    sp.period_name
FROM planned_activity pa 
JOIN course_instance ci ON ci.id=pa.course_instance_id
JOIN course_instance_study_period cisp ON ci.id=cisp.course_instance_id
JOIN study_period sp ON cisp.study_period_id=sp.id
WHERE pa.employee_id=1;

SELECT * FROM employment_constants

-- ci per peridod
SELECT 
    ci.id ciid,
    sp.period_name

FROM course_instance ci
JOIN course_instance_study_period cisp ON ci.id=cisp.course_instance_id
JOIN study_period sp ON cisp.study_period_id=sp.id

-- ciids for emp
SELECT 
    DISTINCT ci.id ciid_per_emp,
    sp.period_name

FROM course_instance ci
JOIN planned_activity pa On pa.course_instance_id=ci.id
JOIN course_instance_study_period cisp ON ci.id = cisp.course_instance_id
JOIN study_period sp ON cisp.study_period_id = sp.id
WHERE pa.employee_id = 1;

            -- the query for course allocation
            SELECT
                COUNT(DISTINCT ci.id) AS num_courses,
                sp.period_name
            FROM
                planned_activity pa 
               JOIN course_instance ci ON pa.course_instance_id = ci.id AND ci.study_year = '2025'
                JOIN course_instance_study_period cisp ON ci.id = cisp.course_instance_id
                JOIN study_period sp ON cisp.study_period_id = sp.id
            WHERE pa.employee_id = 1
            GROUP BY sp.period_name


            SELECT
                ci.id,
                sp.period_name,
                pa.activity_id
            FROM
                planned_activity pa 
               JOIN course_instance ci ON pa.course_instance_id = ci.id AND ci.study_year = '2025'
                JOIN course_instance_study_period cisp ON ci.id = cisp.course_instance_id
                JOIN study_period sp ON cisp.study_period_id = sp.id
            WHERE pa.employee_id = 1




SELECT * FROM course_instance




SELECT
    ci.id AS ciid,
    sp.period_name
FROM
    planned_activity pa 
    JOIN course_instance ci ON pa.course_instance_id = ci.id AND ci.study_year = '2025'
    JOIN course_instance_study_period cisp ON ci.id = cisp.course_instance_id
    JOIN study_period sp ON cisp.study_period_id = sp.id
WHERE pa.employee_id = 1

--- emp 3
SELECT 
    DISTINCT ci.id ciid_per_emp,
    sp.period_name

FROM course_instance ci
JOIN planned_activity pa On pa.course_instance_id=ci.id
JOIN course_instance_study_period cisp ON ci.id = cisp.course_instance_id
JOIN study_period sp ON cisp.study_period_id = sp.id
WHERE pa.employee_id = 3;