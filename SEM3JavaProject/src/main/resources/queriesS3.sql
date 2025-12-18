-- TASK A --

INSERT INTO employee_salary (employee_id, salary_enforcement_date, salary_per_hour) VALUES
(1,'2025-01-01',250.0),
(2,'2025-01-01',240.0),
(3,'2025-01-01',380.0),
(4,'2025-01-01',270.0),
(5,'2025-01-01',250.0),
(6,'2025-01-01',200.0),
(7,'2025-01-01',300.0),
(8,'2025-01-01',250.0),
(9,'2025-01-01',400.0),
(10,'2025-01-01',200.0);

-- 1. --

SELECT
    cl.course_code,
    ci.id AS course_instance,
    sp.period_name AS study_period,
    SUM(
        eJs.avgS * pa.planned_hours + 
        eJs.avgS * aaeh.admin_hours_per_employee +
        eJs.avgS * aaeh.exam_hours_per_employee
    ) planned_cost,
    SUM(
        eJS.avgS * pa.allocated_hours +
        eJs.avgS * aaeh.admin_hours_per_employee +
        eJs.avgS * aaeh.exam_hours_per_employee
    ) actual_cost
FROM
    planned_activity pa
    JOIN course_instance ci ON pa.course_instance_id = ci.id AND ci.id = ? AND ci.study_year = ?
    JOIN course_version cv ON ci.course_version_id = cv.id
    JOIN course_layout cl ON cv.course_layout_id = cl.id
    JOIN course_instance_study_period cisp ON ci.id = cisp.course_instance_id
    JOIN study_period sp ON cisp.study_period_id = sp.id
    JOIN admin_and_exam_hours_per_employee_and_course aaeh ON ci.id = aaeh.ciid
    JOIN (
        SELECT
            e.id,
            AVG(es.salary_per_hour) avgS
        FROM
            employee e
        JOIN employee_salary es ON e.id = es.employee_id
        FOR UPDATE
        GROUP BY
            es.employee_id,
            e.id
        ) eJs ON eJs.id = pa.employee_id
GROUP BY
    cl.course_code,
    ci.id,
    sp.period_name
ORDER BY course_instance;