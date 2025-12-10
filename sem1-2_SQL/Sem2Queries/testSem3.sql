SELECT  
COUNT(DISTINCT ci.id) AS num_courses
FROM
    planned_activity pa 
    JOIN course_instance ci ON pa.course_instance_id = ci.id AND ci.study_year = '2025'
    JOIN course_instance_study_period cisp ON ci.id = cisp.course_instance_id
    JOIN study_period sp ON cisp.study_period_id = sp.id
WHERE pa.employee_id = 1 

SELECT  
    ci.id ciid,
    sp.period_name
FROM
    planned_activity pa 
    JOIN course_instance ci ON pa.course_instance_id = ci.id AND ci.study_year = '2025'
    JOIN course_instance_study_period cisp ON ci.id = cisp.course_instance_id
    JOIN study_period sp ON cisp.study_period_id = sp.id
WHERE pa.employee_id = 1 
GROUP BY ci.id, sp.period_name