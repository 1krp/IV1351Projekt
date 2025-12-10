SELECT sp.period_name
FROM course_instance ci
JOIN course_instance_study_period cisp ON ci.id = cisp.course_instance_id
JOIN study_period sp ON cisp.study_period_id = sp.id
WHERE ci.id=1;