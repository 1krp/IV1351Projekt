-- TASK A --

-- 1. --
SELECT
    cl.course_code,
    ci.id cid,
    sp.period_name,
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
    JOIN course_instance ci ON pa.course_instance_id = ci.id AND ci.study_year = '2025'
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
        GROUP BY
            es.employee_id,
            e.id
    ) eJs ON eJs.id = pa.employee_id
GROUP BY
    ci.id,
    cl.course_code,
    sp.period_name
ORDER BY cid;
