CREATE INDEX idx_course_layout_id ON course_version(course_layout_id);

CREATE INDEX idx_course_instance_course_version_id ON course_instance(course_version_id);
CREATE INDEX idx_course_instance_study_year ON course_instance(study_year);

CREATE INDEX idx_cisp_study_period_id ON course_instance_study_period(study_period_id);
CREATE INDEX idx_cisp_course_instance_id ON course_instance_study_period(course_instance_id);

CREATE INDEX idx_planned_activity_employee_id ON planned_activity(employee_id);
CREATE INDEX idx_planned_activity_course_instance_id ON planned_activity(course_instance_id);
CREATE INDEX idx_planned_activity_activity_id ON planned_activity(activity_id);

CREATE INDEX idx_employee_person_id ON employee(person_id);
CREATE INDEX idx_employee_job_title_id ON employee(job_title_id);
CREATE INDEX idx_employee_department_id ON employee(department_id);

CREATE INDEX idx_employee_salary_employee_id ON employee_salary(employee_id);

CREATE INDEX idx_employee_skill_employee_id ON employee_skill(employee_id);
CREATE INDEX idx_employee_skill_skill_id ON employee_skill(skill_id);

CREATE INDEX idx_teaching_activity_activity_name ON teaching_activity(activity_name);