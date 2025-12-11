SELECT sp.period_name
FROM course_instance ci
JOIN course_instance_study_period cisp ON ci.id = cisp.course_instance_id
JOIN study_period sp ON cisp.study_period_id = sp.id
WHERE ci.id=1;

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

            SELECT
                COUNT(ci.id) AS num_courses,
                sp.period_name
            FROM
                planned_activity pa 
               JOIN course_instance ci ON pa.course_instance_id = ci.id AND ci.study_year = '2025'
                JOIN course_instance_study_period cisp ON ci.id = cisp.course_instance_id
                JOIN study_period sp ON cisp.study_period_id = sp.id
            WHERE pa.employee_id = 1
            GROUP BY sp.period_name



SELECT * FROM course_instance