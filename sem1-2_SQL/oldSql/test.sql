SELECT course_instance_id, employee_id,
        SUM(CASE WHEN activity_name = 'Lecture' THEN pa.allocated_hours * factor ELSE 0 END) AS lecture_hours,
        SUM(CASE WHEN activity_name = 'Seminar' THEN pa.allocated_hours * factor ELSE 0 END) AS seminar_hours,
        SUM(CASE WHEN activity_name = 'Lab' THEN pa.allocated_hours * factor ELSE 0 END) AS lab_hours,
        SUM(CASE WHEN activity_name = 'Tutorial' THEN pa.allocated_hours * factor ELSE 0 END) AS tutorial_hours,
        SUM(CASE WHEN activity_name = 'Other' THEN pa.allocated_hours * factor ELSE 0 END) AS other_hours
    FROM planned_activity pa
    JOIN teaching_activity ta ON pa.activity_id=ta.id
    GROUP BY course_instance_id, pa.employee_id
    ORDER BY course_instance_id



SELECT * FROM planned_activity pa
    JOIN teaching_activity ta ON pa.activity_id=ta.id





    SELECT course_instance_id, p.first_name, p.last_name,
        SUM(CASE WHEN activity_name = 'Lecture' THEN pa.allocated_hours * factor ELSE 0 END) AS lecture_hours,
        SUM(CASE WHEN activity_name = 'Seminar' THEN pa.allocated_hours * factor ELSE 0 END) AS seminar_hours,
        SUM(CASE WHEN activity_name = 'Lab' THEN pa.allocated_hours * factor ELSE 0 END) AS lab_hours,
        SUM(CASE WHEN activity_name = 'Tutorial' THEN pa.allocated_hours * factor ELSE 0 END) AS tutorial_hours,
        SUM(CASE WHEN activity_name = 'Other' THEN pa.allocated_hours * factor ELSE 0 END) AS other_hours
    FROM planned_activity pa
    JOIN teaching_activity ta ON pa.activity_id=ta.id
    JOIN employee e ON e.id=pa.employee_id
    JOIN person p ON p.id=e.person_id
    GROUP BY course_instance_id, p.first_name, p.last_name
    ORDER BY course_instance_id


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
      ORDER BY course_instance_id