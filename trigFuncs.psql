-- TRIGGER FUNCTION 1 (used when new activity_employee relation is added) --

CREATE OR REPLACE FUNCTION prevent_study_period_modification()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'You can not insert, update or delete rows in study_period.';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER prevent_update
BEFORE INSERT OR UPDATE OR DELETE ON study_period
FOR EACH ROW
EXECUTE FUNCTION prevent_study_period_modification();

CREATE FUNCTION check_employee_study_period_load()
RETURNS TRIGGER AS $$
DECLARE
    sp_id INTEGER;     -- study period (P1, P2, P3, P4)
    cnt INTEGER;    -- how many course instances this employee has in this period
BEGIN
    -- 1. For the NEW course_instance_id, fetch its study period(s)
    FOR sp_id IN
        SELECT study_period_id
        FROM course_instance_study_period
        WHERE course_instance_id = NEW.course_instance_id
    LOOP

        -- 2. Count how many course instances this employee has in this study period
        SELECT COUNT(*) INTO cnt
        FROM activity_employee ae
        JOIN course_instance_study_period cisp
            ON ae.course_instance_id = cisp.course_instance_id
        WHERE ae.employee_id = NEW.employee_id
          AND cisp.study_period_id = sp_id;

        -- 3. If the employee already has 4, adding this one would exceed the limit
        IF cnt >= (SELECT max_courses FROM constants WHERE id = 1) THEN
            RAISE EXCEPTION
                'Employee % already has % course instances in study period % (max = 4)',
                NEW.employee_id, cnt, sp_id;
        END IF;

    END LOOP;

    RETURN NEW;  -- Everything OK, allow the insert/update
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_employee_study_period_load
BEFORE INSERT OR UPDATE ON activity_employee
FOR EACH ROW
EXECUTE FUNCTION check_employee_study_period_load();

-- TRIGGER FUNCTION 2 (used when new study_period is added to a course_instance) --

CREATE FUNCTION check_period_addition_load()
RETURNS TRIGGER AS $$
DECLARE
    emp_id INTEGER;
    cnt INTEGER;
BEGIN
    -- Loop over all employees assigned to this course instance
    FOR emp_id IN
        SELECT employee_id
        FROM activity_employee
        WHERE course_instance_id = NEW.course_instance_id
    LOOP
        
        -- Count how many course instances this employee already has in NEW.study_period_id
        SELECT COUNT(*) INTO cnt
        FROM activity_employee ae
        JOIN course_instance_study_period cisp
            ON ae.course_instance_id = cisp.course_instance_id
        WHERE ae.employee_id = emp_id
          AND cisp.study_period_id = NEW.study_period_id;

        -- If >= 4 then adding this study period makes it 5
        IF cnt >= (SELECT max_courses FROM constants WHERE id = 1) THEN
            RAISE EXCEPTION
                'Employee % already has % course instances in study period % (max = 4)',
                emp_id, cnt, NEW.study_period_id;
        END IF;

    END LOOP;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_period_addition_load
BEFORE INSERT OR UPDATE ON course_instance_study_period
FOR EACH ROW
EXECUTE FUNCTION check_period_addition_load();

-- TRIGGER FUNCTION 3 (used to store current hp from a course_layout to a new course_instance) --

CREATE FUNCTION add_current_hp_to_instance()
RETURNS TRIGGER AS $$
BEGIN
    SELECT hp INTO NEW.hp_when_created
    FROM course_layout
    WHERE id = NEW.course_layout_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_add_current_hp_to_instance
BEFORE INSERT ON course_instance
FOR EACH ROW
EXECUTE FUNCTION add_current_hp_to_instance();