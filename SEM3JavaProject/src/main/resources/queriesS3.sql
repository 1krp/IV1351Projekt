-- TASK A --

-- 1. --

SELECT * FROM planned_activity;


WITH eJs AS (
    SELECT
        e.id,
        AVG(es.salary_per_hour) avgS
    FROM
        employee e 
        JOIN employee_salary es ON e.id = es.employee_id
    GROUP BY
        es.employee_id,
        e.id
),
paJta AS (
    SELECT
        cl.course_code,
        ci.id cid,
        pa.employee_id eid,
        pa.planned_hours,
        pa.allocated_hours,
        sp.period_name
    FROM
        planned_activity pa 
        JOIN course_instance ci ON pa.course_instance_id = ci.id AND ci.study_year = '2025'
        JOIN course_version cv ON ci.course_version_id = cv.id
        JOIN course_layout cl ON cv.course_layout_id = cl.id
        JOIN course_instance_study_period cisp ON ci.id = cisp.course_instance_id
        JOIN study_period sp ON cisp.study_period_id = sp.id
),
taskAView AS (
    SELECT
        paJta.course_code,
        paJta.cid,
        paJta.period_name,
        (SUM(eJs.avgS * paJta.planned_hours) + SUM(eJS.avgS * paJta.allocated_hours)) cost
    FROM
        paJta
        JOIN eJs ON paJta.eid = eJs.id
    GROUP BY
        paJta.cid,
        paJta.course_code,
        paJta.cid,
        paJta.period_name
) SELECT * FROM taskAView;
