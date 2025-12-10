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