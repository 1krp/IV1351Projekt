/*
 * The MIT License (MIT)
 * Copyright (c) 2020 Leif Lindbäck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction,including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so,subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package se.kth.iv1351.bankjdbc.integration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import se.kth.iv1351.bankjdbc.model.DTO.*;

/**
 * This data access object (DAO) encapsulates all database calls in the bank
 * application. No code outside this class shall have any knowledge about the
 * database.
 */
public class TeachingActivityDAO {
    private static final String TEACHING_ACTIVITY_TABLE_NAME = "teaching_activity";
    private static final String TEACHING_ACTIVITY_TABLE_PK = "id";
    private static final String TEACHING_ACTIVITY_COLUMN_ACTIVITY_NAME = "activity_name";
    private static final String TEACHING_ACTIVITY_COLUMN_FACTOR = "factor";

    private static final String PLANNED_ACTIVITY_COLUMN_PLANNED_HOURS = "planned_hours";
    private static final String PLANNED_ACTIVITY_COLUMN_ALLOCATED_HOURS = "allocated_hours"; 
    private static final String PLANNED_ACTIVITY_TABLE_NAME = "planned_activity";
    private static final String PLANNED_ACTIVITY_COLUMN_ACTIVITY_ID = "activity_id";
    private static final String PLANNED_ACTIVITY_COLUMN_EMPLOYEE_ID = "employee_id";
    private static final String PLANNED_ACTIVITY_COLUMN_COURSE_INSTANCE_ID = "course_instance_id";
    private static final String PLANNED_ACTIVITY_PK_ID = "id";
    

    private static final String EC_C_TABLE_NAME = "employment_constants";
    private static final String EC_C_COLUMN_NAME = "max_courses";
    private static final String EC_C_PK_COLUMN_NAME = "id";
    private static final String CI_TABLE_NAME = "course_instance";
    private static final String CI_COLUMN_NAME = "num_students";
    private static final String CI_PK_COLUMN_NAME = "id";
    private static final String CV_TABLE_NAME = "course_version";
    private static final String CV_PK_COLUMN_NAME = "id";
    private static final String CL_TABLE_NAME = "course_layout";
    private static final String CL_PK_COLUMN_NAME = "id";

    private Connection connection;

    private PreparedStatement updateTeacherAllocationLimitStmt;
    private PreparedStatement updateNumStudendsInCIStmt;
    private PreparedStatement createTAStmt;
    private PreparedStatement showTARowsStmt;
    private PreparedStatement createTAFactorStmt;
    private PreparedStatement findTAStmt;
    private PreparedStatement deletePlannedActivityStmt;
    private PreparedStatement deleteActivityStmt;
    private PreparedStatement insertNewActivityStmt;
    private PreparedStatement displayTAStmt;
    private PreparedStatement fetchCourseInstanceStmt;
    private PreparedStatement fetchPlannedActivityStmt;
    private PreparedStatement fetchAdminExamHoursForCourseStmt;
    private PreparedStatement fetchSalaryEmployeeStmt;
    private PreparedStatement showTeachingCostsStmt;
    private PreparedStatement deallocatePAStmt;
    private PreparedStatement findPAsForTeacherStmt;
    private PreparedStatement findPeriodForCoursinstanceStmt;
    private PreparedStatement findMaxCoursesPerTeacherStmt;
    private PreparedStatement createPlannedActivityStmt;
    

    /**
     * Constructs a new DAO object connected to the database.
     */
    public TeachingActivityDAO() throws TeachingActivityDBException {
        try {
            connectToDB();
            prepareStatements();
        } catch (ClassNotFoundException | SQLException exception) {
            throw new TeachingActivityDBException("Could not connect to datasource.", exception);
        }
    }



    /**
     * Updates the max_courses limit in the employment_constants table
     * 
     * @param newLimit
     * @throws TeachingActivityDBException
     */

    public void updateTeacherAllocationLimit(int newLimit) throws TeachingActivityDBException {
        String failureMsg = "Could not update max_courses to: " + newLimit;
        try{
            updateTeacherAllocationLimitStmt.setInt(1, newLimit);
            updateTeacherAllocationLimitStmt.setInt(2, 1);

            int updatedRows = updateTeacherAllocationLimitStmt.executeUpdate();
            if (updatedRows != 1) {
                handleException(failureMsg, null);
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
    }


    public void updateNumStudendsInCourseInstance(int courseInstanceId, int numStudents) throws TeachingActivityDBException {
        String failureMsg = "Could not update num_students to: " + numStudents;
        try{
            updateNumStudendsInCIStmt.setInt(1, numStudents);
            updateNumStudendsInCIStmt.setInt(2, courseInstanceId);

            int updatedRows = updateNumStudendsInCIStmt.executeUpdate();
            if (updatedRows != 1) {
                handleException(failureMsg, null);
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
    }

    public void deallocatePlannedActivity(int plannedActivityId) throws TeachingActivityDBException {
        String failureMsg = "Could not deallocate the planned activity with id: " + plannedActivityId;
        try{
            deallocatePAStmt.setInt(1, plannedActivityId);

            int updatedRows = deallocatePAStmt.executeUpdate();
            if (updatedRows != 1) {
                handleException(failureMsg, null);
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
    }

    public ArrayList<TeacherAllocationDTO> findTeacherAllocationPeriod(String year, int employeeId) throws TeachingActivityDBException {
        String failureMsg = "Could not search for teacher allocation pressure";

        ArrayList<TeacherAllocationDTO> allocations = new ArrayList<>();
        try {

            findPAsForTeacherStmt.setString(1, year);
            findPAsForTeacherStmt.setInt(2, employeeId);

            ResultSet result = findPAsForTeacherStmt.executeQuery();
            while (result.next()) {
                TeacherAllocationDTO allocationDTO = new TeacherAllocationDTO(
                    result.getInt("num_courses"),
                    result.getString("period_name")
                );

                allocations.add(allocationDTO);
            }
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        } 

        return allocations;
    }

    public String findPeriodForCoursinstance(int courseInstanceId) throws TeachingActivityDBException {
        String failureMsg = "Could not find period for given course instance";

        try {
            findPeriodForCoursinstanceStmt.setInt(1, courseInstanceId);

            try (ResultSet result = findPeriodForCoursinstanceStmt.executeQuery()) {
                if (result.next()) {
                    return result.getString(1);
                } else {
                    throw new TeachingActivityDBException("No max courses value found"); 
                }
            }

        } catch (SQLException e) {
            throw new TeachingActivityDBException(failureMsg, e);
        }
    }

    public int findMaxCoursesPerTeacher() throws TeachingActivityDBException {
        String failureMsg = "Could not search max courses per teacher";

        try (ResultSet result = findMaxCoursesPerTeacherStmt.executeQuery()) {
            if (result.next()) {
                return result.getInt(1);
            } else {
                return 0; 
            }
        } catch (SQLException e) {
            throw new TeachingActivityDBException(failureMsg, e);
        }
    }

    public void createPlannedActivity(PlannedActivityDTO plannedDTO) throws TeachingActivityDBException {
        String failureMsg = "Could not allocate activity";
        int updatedRows = 0;
        try {
            createPlannedActivityStmt.setInt(1, plannedDTO.getEmpId());
            createPlannedActivityStmt.setInt(2, plannedDTO.getCourseId());
            createPlannedActivityStmt.setInt(3, plannedDTO.getPlannedHours());
            createPlannedActivityStmt.setInt(4, plannedDTO.getAllocatedHours());
            createPlannedActivityStmt.setInt(5, plannedDTO.getTActivity());

            updatedRows = createPlannedActivityStmt.executeUpdate();
            if (updatedRows != 1) {
                handleException(failureMsg, null);
            }

            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
    }

    /**
     * Commits the current transaction.
     * 
     * @throws TeachingActivityDBException If unable to commit the current transaction.
     */
    public void commit() throws TeachingActivityDBException {
        try {
            connection.commit();
        } catch (SQLException e) {
            handleException("Failed to commit", e);
        }
    }

    /**
     * Rollbacks the current transaction.
     * 
     * @throws TeachingActivityDBException If unable to commit the current transaction.
     */
    public void rollback(){
        try {
            connection.rollback();
        } catch (SQLException e) {
            System.out.println("Problem when rollback: " + e.getMessage());
        }
    }

    private void connectToDB() throws ClassNotFoundException, SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
                "Sparfbag", "Sagastass20!");
        connection.setAutoCommit(false);
    }

    /**
     * Prepared statements 
     * 
     * @throws SQLException If unable to excecute SQL-statement
     */
    private void prepareStatements() throws SQLException {
        updateTeacherAllocationLimitStmt = connection.prepareStatement("UPDATE " + EC_C_TABLE_NAME 
                + " SET " + EC_C_COLUMN_NAME + " = ? WHERE " + EC_C_PK_COLUMN_NAME + " = ?");

        createTAStmt = connection.prepareStatement("INSERT INTO " + TEACHING_ACTIVITY_TABLE_NAME 
                + "(" + TEACHING_ACTIVITY_COLUMN_ACTIVITY_NAME + ", "+ TEACHING_ACTIVITY_COLUMN_FACTOR +") VALUES (?,?)");//Exercise

        findTAStmt = connection.prepareStatement("SELECT " + TEACHING_ACTIVITY_TABLE_PK
                + " FROM " + TEACHING_ACTIVITY_TABLE_NAME + " WHERE " + TEACHING_ACTIVITY_COLUMN_ACTIVITY_NAME + " = ?"); //Om TA redan finns
        
        deletePlannedActivityStmt = connection.prepareStatement("DELETE FROM " + PLANNED_ACTIVITY_TABLE_NAME + 
        " WHERE " + PLANNED_ACTIVITY_COLUMN_ACTIVITY_ID + " =?");

        deleteActivityStmt = connection.prepareStatement("DELETE FROM " + TEACHING_ACTIVITY_TABLE_NAME + 
        " WHERE " + TEACHING_ACTIVITY_COLUMN_ACTIVITY_NAME + " =?");

        insertNewActivityStmt = connection.prepareStatement("INSERT INTO "+ PLANNED_ACTIVITY_TABLE_NAME +"(" 
        + PLANNED_ACTIVITY_COLUMN_EMPLOYEE_ID + ", " + PLANNED_ACTIVITY_COLUMN_COURSE_INSTANCE_ID + ", " +
        PLANNED_ACTIVITY_COLUMN_PLANNED_HOURS + ", "+ PLANNED_ACTIVITY_COLUMN_ALLOCATED_HOURS + ", "
         + PLANNED_ACTIVITY_COLUMN_ACTIVITY_ID + ") VALUES (?, ?, ?, ?, ?)" );      
         
        updateNumStudendsInCIStmt = connection.prepareStatement("UPDATE " + CI_TABLE_NAME 
                + " SET " + CI_COLUMN_NAME + " = ? WHERE " + CI_PK_COLUMN_NAME + " = ?");
                
        displayTAStmt = connection.prepareStatement("SELECT planned_activity.employee_id, planned_activity.course_instance_id, planned_activity.planned_hours, planned_activity.allocated_hours, teaching_activity.activity_name\r\n" + //
                        "FROM planned_activity\r\n" + //
                        "INNER JOIN teaching_activity ON planned_activity.activity_id = teaching_activity.id WHERE teaching_activity.activity_name = ?");

        fetchCourseInstanceStmt = connection.prepareStatement(
            "SELECT ci." + CI_PK_COLUMN_NAME + ", ci.num_students, ci.study_year, cv.hp, cl.course_code \n" + //
            "FROM " + CI_TABLE_NAME + " ci \n" + //
            "JOIN " + CV_TABLE_NAME + " cv ON ci.course_version_id = cv." + CV_PK_COLUMN_NAME + " \n" + //
            "JOIN " + CL_TABLE_NAME + " cl ON cv.course_layout_id = cl." + CL_PK_COLUMN_NAME + "\n" + //
            "WHERE ci." + CI_PK_COLUMN_NAME + " = ? AND ci.study_year = ? FOR UPDATE"
        );

        fetchPlannedActivityStmt = connection.prepareStatement(
            "SELECT pa.*, ta.factor\n" + //
            "FROM planned_activity pa JOIN teaching_activity ta ON pa.activity_id = ta.id\n" + //
            "JOIN " + CI_TABLE_NAME + " ci ON pa.course_instance_id = ci." + CI_PK_COLUMN_NAME + " AND ci." + CI_PK_COLUMN_NAME + " = ? \n" + //
            "JOIN " + CV_TABLE_NAME + " cv ON ci.course_version_id = cv." + CV_PK_COLUMN_NAME + " \n" + //
            "JOIN " + CL_TABLE_NAME + " cl ON cv.course_layout_id = cl." + CL_PK_COLUMN_NAME + " FOR UPDATE"
        );

        fetchAdminExamHoursForCourseStmt = connection.prepareStatement(
            "SELECT ci.id, aaeh.admin_hours_per_employee, aaeh.exam_hours_per_employee \n" + //
            "FROM " + CI_TABLE_NAME + " ci JOIN admin_and_exam_hours_per_employee_and_course aaeh \n" + //
            "ON ci." + CI_PK_COLUMN_NAME + " = aaeh.ciid \n" + //
            "WHERE ci." + CI_PK_COLUMN_NAME + " = ?"
        );

        fetchSalaryEmployeeStmt = connection.prepareStatement(
            "SELECT es.employee_id, es.salary_per_hour \n" + //
            "FROM employee_salary es WHERE es.employee_id = ? FOR UPDATE");

        showTeachingCostsStmt = connection.prepareStatement(
            "SELECT cl.course_code, ci." + CI_PK_COLUMN_NAME  + " AS course_instance,\n" +
            "sp.period_name AS study_period, (?) AS planned_cost, (?) AS actual_cost \n" +
            "FROM " + CI_TABLE_NAME + " ci \n" +
            "JOIN " + CV_TABLE_NAME + " cv ON ci.course_version_id = cv." + CV_PK_COLUMN_NAME + " \n" +
            "JOIN " + CL_TABLE_NAME + " cl ON cv.course_layout_id = cl." + CL_PK_COLUMN_NAME + " \n" +
            "JOIN course_instance_study_period cisp ON ci." + CI_PK_COLUMN_NAME + " = cisp.course_instance_id\n" +
            "JOIN study_period sp ON cisp.study_period_id = sp.id \n" +
            "WHERE ci." + CI_PK_COLUMN_NAME + " = ?"
        );
        showTARowsStmt = connection.prepareStatement("SELECT * FROM "+ TEACHING_ACTIVITY_TABLE_NAME);

        deallocatePAStmt = connection.prepareStatement("DELETE FROM " + PLANNED_ACTIVITY_TABLE_NAME 
                + " WHERE " + PLANNED_ACTIVITY_PK_ID + " = ?");

        findPAsForTeacherStmt = connection.prepareStatement(
            "SELECT  \n" +
            "    COUNT(ci." + CI_PK_COLUMN_NAME + ") AS num_courses,\n" +
            "    sp.period_name\n" +
            "FROM\n" +
            "    planned_activity pa \n" +
            "    JOIN " + CI_TABLE_NAME + " ci ON pa.course_instance_id = ci." + CI_PK_COLUMN_NAME + " AND ci.study_year = ? \n" +
            "    JOIN course_instance_study_period cisp ON ci.id = cisp.course_instance_id \n" +
            "    JOIN study_period sp ON cisp.study_period_id = sp.id \n" +
            "WHERE pa.employee_id = ? \n" +
            "GROUP BY sp.period_name"
        );

        findPeriodForCoursinstanceStmt = connection.prepareStatement(
                "SELECT sp.period_name\n" + //
                    "FROM " + CI_TABLE_NAME + " ci\n" + //
                    "JOIN course_instance_study_period cisp ON ci." + CI_PK_COLUMN_NAME + " = cisp.course_instance_id\n" + //
                    "JOIN study_period sp ON cisp.study_period_id = sp.id\n" + //
                    "WHERE ci." + CI_PK_COLUMN_NAME + " = ?");
        
        findMaxCoursesPerTeacherStmt = connection.prepareStatement("SELECT " + EC_C_COLUMN_NAME
                + " FROM " + EC_C_TABLE_NAME + " WHERE " + EC_C_PK_COLUMN_NAME + "=1" );

        createPlannedActivityStmt = connection.prepareStatement("INSERT INTO " + PLANNED_ACTIVITY_TABLE_NAME
                + "(employee_id, course_instance_id, planned_hours, allocated_hours, activity_id) VALUES (?, ?, ?, ?, ?)");
    }
        
    /**
     * For task A1 - fetches a row that includes the following course intance data:
     * course instance id, number of students, study year, hp points, course code
     * 
     * @param cid course_instance_id
     * @param year study year
     * @return CourseInstanceDTO if execution is successful, else null
     * @throws TeachingActivityDBException
     */
    public CourseInstanceDTO fetchCourseInstance(int cid, String year) 
        throws TeachingActivityDBException {
            
        CourseInstanceDTO courseInst = null;
        
        try {
            fetchCourseInstanceStmt.setInt(1, cid);
            fetchCourseInstanceStmt.setString(2, year);
            ResultSet rs = fetchCourseInstanceStmt.executeQuery();
        
            while (rs.next()){
                courseInst = new CourseInstanceDTO(
                    rs.getInt("id"),
                    rs.getInt("num_students"),
                    rs.getString("study_year"),
                    rs.getDouble("hp"),
                    rs.getString("course_code")
                );
            }
                    
        } catch (SQLException se){
            String erMsg = "Error when trying to fetch course instance.";
            handleException(erMsg, se);
        }
        return courseInst;
    }

    /**
     * For task A1 - fetches a row that includes the following planned activity data:
     * planned activity id, employee id, course instance id, planned hours, allocated hours,
     * activity id and multiplication factor.
     * 
     * @param courseId course_instance_id
     * @return A list with PlannedActivityDTO if execution is successful, else null
     * @throws TeachingActivityDBException
     */
    public ArrayList<PlannedActivityDTO> fetchPlannedActivities(int courseId) 
        throws TeachingActivityDBException {
            
        ArrayList<PlannedActivityDTO> allPlannedActivities = new ArrayList<>();
        
        try {
            fetchPlannedActivityStmt.setInt(1, courseId);
            ResultSet rs = fetchPlannedActivityStmt.executeQuery();
        
            while (rs.next()){
                PlannedActivityDTO plannedActivity = new PlannedActivityDTO(
                    rs.getInt("id"),
                    rs.getInt("employee_id"),
                    rs.getInt("course_instance_id"),
                    rs.getInt("planned_hours"),
                    rs.getInt("allocated_hours"),
                    rs.getInt("activity_id"),
                    rs.getDouble("factor")
                );

                allPlannedActivities.add(plannedActivity);
            }
                    
        } catch (SQLException se){
            String erMsg = "Error when trying to fetch planned activity.";
            handleException(erMsg, se);
        }
        return allPlannedActivities;
    }

    /**
     * For task A1 - fetches admin and exam hours per employee for a course
     * 
     * @param courseId course_instance_id
     * @return AdminExamHoursDTODTO if execution is successful, else null
     * @throws TeachingActivityDBException
     */
    public AdminExamHoursDTO fetchAdminExamHoursForCourse(int courseId) 
        throws TeachingActivityDBException {
            
        AdminExamHoursDTO adminExamHours = null;
        
        try {
            fetchAdminExamHoursForCourseStmt.setInt(1, courseId);
            ResultSet rs = fetchAdminExamHoursForCourseStmt.executeQuery();
        
            while (rs.next()){
                adminExamHours = new AdminExamHoursDTO(
                    rs.getInt("id"),
                    rs.getInt("admin_hours_per_employee"),
                    rs.getInt("exam_hours_per_employee")
                );
            }
                    
        } catch (SQLException se){
            String erMsg = "Error when trying to fetch admin/exam hours.";
            handleException(erMsg, se);
        }
        return adminExamHours;
    }

    /**
     * For task A1 - fetches all registered salary per hour for an employee.
     * Used to calculate the mean salary for an employee.
     * 
     * @param empId employee_id
     * @return A list with SalaryDTO if execution is successful, else null
     * @throws TeachingActivityDBException
     */
    public ArrayList<SalaryDTO> fetchSalaryEmployee(int empId) 
        throws TeachingActivityDBException {
            
        ArrayList<SalaryDTO> salaries = new ArrayList<>();
        
        try {
            fetchSalaryEmployeeStmt.setInt(1, empId);
            ResultSet rs = fetchSalaryEmployeeStmt.executeQuery();
        
            while (rs.next()){
                SalaryDTO salary = new SalaryDTO(
                    rs.getInt("employee_id"),
                    rs.getDouble("salary_per_hour")
                );
                salaries.add(salary);
            }

        } catch (SQLException se){
            String erMsg = "Error when trying to fetch avg employee salary";
            handleException(erMsg, se);
        }
        
        return salaries;
    }

    /**
     * For task A1 - fetches rows with desired outout data:
     * Course code, course instance id, study period, planned cost, actual cost.
     * 
     * @param plannedCost Calculated planned cost
     * @param actCost Calculated actual cost
     * @param courseId Course instance id
     * @return A list with TeachingCostDTO for all study periods the course is planned for
     * @throws TeachingActivityDBException
     */
    public ArrayList<TeachingCostDTO> createTeachingCostsForCourseView(double plannedCost, double actCost, int courseId) 
        throws TeachingActivityDBException {
            
        ArrayList<TeachingCostDTO> allTeachingCosts = new ArrayList<>();
        
        try {
            showTeachingCostsStmt.setDouble(1, plannedCost);
            showTeachingCostsStmt.setDouble(2, actCost);
            showTeachingCostsStmt.setInt(3, courseId);
            ResultSet rs = showTeachingCostsStmt.executeQuery();
        
            while (rs.next()){
                TeachingCostDTO teachingCosts = new TeachingCostDTO(
                    rs.getString("course_code"), 
                    rs.getInt("course_instance"), 
                    rs.getString("study_period"),
                    rs.getDouble("planned_cost"),
                    rs.getDouble("actual_cost")
                );

                allTeachingCosts.add(teachingCosts);
            }        
        } catch (SQLException se){
            String erMsg = "Error when trying to fetch rows for teaching costs.";
            handleException(erMsg, se);
        }
        return allTeachingCosts;
    }


    public void createTAInPA(String activityName, double factor, int employee_id, int course_instance_id, int planned_hours, int allocated_hours)
     throws TeachingActivityDBException {
        int activityId = createTeachingActivity(activityName,factor);
        String failureMsgId = activityName+" Already exists"; ;
        String failureMsg = "Could not create the planned activity with activity id: " + activityId;
        String failureMsgSQL = "SQL error when adding: " + activityId;
        if(activityId==0 || activityId==-1){
            handleException(failureMsgId, null);
            return;
        }
        int updatedRows = 0;
        try {
            insertNewActivityStmt.setInt(1, employee_id);
            insertNewActivityStmt.setInt(2, course_instance_id);
            insertNewActivityStmt.setInt(3, planned_hours);
            insertNewActivityStmt.setInt(4, allocated_hours);
            insertNewActivityStmt.setInt(5, activityId);
        updatedRows = insertNewActivityStmt.executeUpdate(); 
        if (updatedRows != 1) {
                handleException(failureMsg, null);
            }
        connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsgSQL, sqle);
        }    
    }
    public ArrayList<PAjoinTADTO> showTAs(String activityName) throws TeachingActivityDBException{
        String failureMsg = "Error";
        ArrayList<PAjoinTADTO> joinedTable = new ArrayList<>();;
        try{
            displayTAStmt.setString(1, activityName);
            ResultSet rs = displayTAStmt.executeQuery();
        
            while (rs.next()){
                joinedTable.add( new PAjoinTADTO(
                        rs.getInt("employee_id"), 
                        rs.getInt("course_instance_id"), 
                        rs.getInt("planned_hours"),
                        rs.getInt("allocated_hours"),
                        rs.getString("activity_name")
                ));
            }
        }catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
        return joinedTable;
    }

    private int createTeachingActivity(String activityName, double factor) throws TeachingActivityDBException {
        String failureMsgUpdate = "Could not add teaching activity: " + activityName;
        String failureMsgSQL = "SQL error for: " + activityName;
        String failureMsgSQLKeys = "SQL error for generated keys: " + activityName;
        String failureMsgDontAdd = activityName+" Already exists";
        int updatedRows = 0;
        int activityId = 0;
        try {
            activityId = doesTAAlreadyExist(activityName);
            if (activityId == 0) {
    
                createTAStmt.setString(1, activityName);
                createTAStmt.setDouble(2, factor);
                updatedRows = createTAStmt.executeUpdate();
                if (updatedRows != 1) {
                    handleException(failureMsgUpdate, null);
                }
                ResultSet generatedKeys = createTAStmt.getGeneratedKeys();
                if (generatedKeys.next()){
                    activityId = generatedKeys.getInt(1);
                }
            }else{
                handleException(failureMsgDontAdd, null);
            } 

        } catch (SQLException sqle) {
             handleException(failureMsgSQL, sqle);
        }
        
        return activityId;
    }
   
    private int doesTAAlreadyExist(String activityName) throws SQLException {

        ResultSet result = null;
        findTAStmt.setString(1, activityName);
        result = findTAStmt.executeQuery();
        if (result.next()) {
            return -1;
        }
        return 0;
    }

    private void handleException(String failureMsg, Exception cause) throws TeachingActivityDBException {
        String completeFailureMsg = failureMsg;
        try {
            connection.rollback();
        } catch (SQLException rollbackExc) {
            completeFailureMsg = completeFailureMsg +
                    ". Also failed to rollback transaction because of: " + rollbackExc.getMessage();
        }

        if (cause != null) {
            throw new TeachingActivityDBException(completeFailureMsg, cause);
        }
    }

    private void closeResultSet(String failureMsg, ResultSet result) throws TeachingActivityDBException {
        try {
            result.close();
        } catch (Exception e) {
            throw new TeachingActivityDBException(failureMsg + " Could not close result set.", e);
        }
    }

}
