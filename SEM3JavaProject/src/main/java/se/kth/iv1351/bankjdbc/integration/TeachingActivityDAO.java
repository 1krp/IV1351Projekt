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
import java.util.List;

import se.kth.iv1351.bankjdbc.model.TeachingActivity;
import se.kth.iv1351.bankjdbc.model.DTO.TADTO;
import se.kth.iv1351.bankjdbc.model.DTO.TeachingCostDTO;
import se.kth.iv1351.bankjdbc.model.DTO.AdminExamHoursDTO;
import se.kth.iv1351.bankjdbc.model.DTO.PlannedActivityDTO;
import se.kth.iv1351.bankjdbc.model.DTO.StudyPeriodDTO;
import se.kth.iv1351.bankjdbc.model.DTO.AvgSalaryDTO;
import se.kth.iv1351.bankjdbc.model.DTO.CourseInstanceDTO;

/**
 * This data access object (DAO) encapsulates all database calls in the bank
 * application. No code outside this class shall have any knowledge about the
 * database.
 */
public class TeachingActivityDAO {
    private static final String TEACHING_ACTIVITY_TABLE_NAME = "teaching_activity";
    private static final String TEACHING_ACTIVITY_PK_COLUMN_NAME = "id";
    private static final String TEACHING_ACTIVITY_COLUMN_ACTIVITY_NAME = "activity_name";
    private static final String TEACHING_ACTIVITY_COLUMN_FACTOR = "factor";
    private static final String PLANNED_ACTIVITY_TABLE_NAME = "planned_activity";
    private static final String PLANNED_ACTIVITY_COLUMN_ACTIVITY_ID = "activity_id";
    private static final String EC_C_TABLE_NAME = "employment_constants";
    private static final String EC_C_COLUMN_NAME = "max_courses";
    private static final String EC_C_PK_COLUMN_NAME = "id";
    private static final String CI_TABLE_NAME = "course_instance";
    private static final String CI_COLUMN_NAME = "num_students";
    private static final String CI_PK_COLUMN_NAME = "id";

    private Connection connection;

    private PreparedStatement fetchCourseInstanceStmt;
    private PreparedStatement fetchPlannedActivityStmt;
    private PreparedStatement fetchAdminExamHoursForCourseStmt;
    private PreparedStatement fetchStudyPeriodsStmt;

    private PreparedStatement fetchCourseHoursStmt;
    private PreparedStatement fetchAvgSalaryEmployeeStmt;
    private PreparedStatement showTeachingCostsStmt;
    private PreparedStatement computeTeachingCostStmt;
    private PreparedStatement updateTeacherAllocationLimitStmt;
    private PreparedStatement updateNumStudendsInCIStmt;
    private PreparedStatement createTAStmt;
    private PreparedStatement createTAFactorStmt;
    private PreparedStatement createTAPAconnectionStmt;
    private PreparedStatement findTAStmt;
    

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

    private void connectToDB() throws ClassNotFoundException, SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/iv_db",
                "postgres", "cbmmlp");
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
                + "(" + TEACHING_ACTIVITY_COLUMN_ACTIVITY_NAME + ") VALUES (?)");//Exercise

        createTAFactorStmt = connection.prepareStatement("INSERT INTO " + TEACHING_ACTIVITY_TABLE_NAME 
                + "(" + TEACHING_ACTIVITY_COLUMN_FACTOR + ") VALUES (?)");

        createTAPAconnectionStmt = connection.prepareStatement("INSERT INTO " + PLANNED_ACTIVITY_TABLE_NAME + 
        "(" + PLANNED_ACTIVITY_COLUMN_ACTIVITY_ID + ") VALUES ("+ TEACHING_ACTIVITY_PK_COLUMN_NAME +")");
        
        findTAStmt = connection.prepareStatement("SELECT " + TEACHING_ACTIVITY_COLUMN_ACTIVITY_NAME
                + " FROM " + TEACHING_ACTIVITY_TABLE_NAME + " WHERE " + TEACHING_ACTIVITY_COLUMN_ACTIVITY_NAME + " = ?"); //Om TA redan finns
         
        updateNumStudendsInCIStmt = connection.prepareStatement("UPDATE " + CI_TABLE_NAME 
                + " SET " + CI_COLUMN_NAME + " = ? WHERE " + CI_PK_COLUMN_NAME + " = ?");

        fetchCourseInstanceStmt = connection.prepareStatement(
            "SELECT ci.id, ci.num_students, ci.study_year, cv.hp, cl.course_code \n" + //
            "FROM course_instance ci \n" + //
            "JOIN course_version cv ON ci.course_version_id = cv.id \n" + //
            "JOIN course_layout cl ON cv.course_layout_id = cl.id \n" + //
            "WHERE ci.id = ? AND ci.study_year = ?"
        );

        fetchPlannedActivityStmt = connection.prepareStatement(
            "SELECT pa.*, ta.factor\n" + //
            "FROM planned_activity pa JOIN teaching_activity ta ON pa.activity_id = ta.id\n" + //
            "JOIN course_instance ci ON ci.id = pa.course_instance_id AND ci.id = ? \n" + //
            "JOIN course_version cv ON ci.course_version_id = cv.id\n" + //
            "JOIN course_layout cl ON cv.course_layout_id = cl.id"
        );

        fetchAdminExamHoursForCourseStmt = connection.prepareStatement(
            "SELECT ci.id, aaeh.admin_hours_per_employee, aaeh.exam_hours_per_employee \n" + //
            "FROM course_instance ci JOIN admin_and_exam_hours_per_employee_and_course aaeh \n" + //
            "ON ci." + CI_PK_COLUMN_NAME + " = aaeh.ciid \n" + //
            "WHERE ci." + CI_PK_COLUMN_NAME + " = ?"
        );

        fetchAvgSalaryEmployeeStmt = connection.prepareStatement(
            "SELECT e.id, AVG(es.salary_per_hour) average_salary \n" + //
            "FROM employee e JOIN employee_salary es ON e.id = es.employee_id \n" + //
            "WHERE e.id = ? \n" + //
            "GROUP BY e.id");

        showTeachingCostsStmt = connection.prepareStatement(
            "SELECT cl.course_code, ci." + CI_PK_COLUMN_NAME  + " AS course_instance,\n" + 
            "sp.period_name AS study_period, (?) AS planned_cost, (?) AS actual_cost \n" + //
            "FROM course_instance ci JOIN course_version cv ON ci.course_version_id = cv.id \n" + //
            "JOIN course_layout cl ON cv.course_layout_id = cl.id \n" + //
            "JOIN course_instance_study_period cisp ON ci.id = cisp.course_instance_id\n" + //
            "JOIN study_period sp ON cisp.study_period_id = sp.id \n" + //
            "WHERE ci.id = ?"
        );


                
        computeTeachingCostStmt = connection.prepareStatement(
                "SELECT\n" + //
                "    cl.course_code,\n" + //
                "    ci." + CI_PK_COLUMN_NAME  + " AS course_instance,\n" + //
                "    sp.period_name AS study_period,\n" + //
                "    SUM(\n" + //
                "        eJs.avgS * pa.planned_hours + \n" + //
                "        eJs.avgS * aaeh.admin_hours_per_employee +\n" + //
                "        eJs.avgS * aaeh.exam_hours_per_employee\n" + //
                "        ) planned_cost,\n" + //
                "    SUM(\n" + //
                "        eJS.avgS * pa.allocated_hours + \n" + //
                "        eJs.avgS * aaeh.admin_hours_per_employee +\n" + //
                "        eJs.avgS * aaeh.exam_hours_per_employee\n" + //
                "        ) actual_cost \n" + //
                "FROM\n" + //
                     PLANNED_ACTIVITY_TABLE_NAME + " pa \n" + //
                "    JOIN " + CI_TABLE_NAME + " ci ON pa.course_instance_id = ci." + CI_PK_COLUMN_NAME 
                        + " AND ci." + CI_PK_COLUMN_NAME + " = ? AND ci.study_year = ? \n" + //
                "    JOIN course_version cv ON ci.course_version_id = cv.id\n" + //
                "    JOIN course_layout cl ON cv.course_layout_id = cl.id\n" + //
                "    JOIN course_instance_study_period cisp ON ci.id = cisp.course_instance_id\n" + //
                "    JOIN study_period sp ON cisp.study_period_id = sp.id\n" + //
                "    JOIN admin_and_exam_hours_per_employee_and_course aaeh ON ci." + CI_PK_COLUMN_NAME 
                        + " = aaeh.ciid\n" + //
                "    JOIN (\n" + //
                "        SELECT\n" + //
                "            e.id,\n" + //
                "            AVG(es.salary_per_hour) avgS\n" + //
                "        FROM\n" + //
                "            employee e \n" + //
                "            JOIN employee_salary es ON e.id = es.employee_id\n" + //
                "            FOR UPDATE \n" + //
                "        GROUP BY\n" + //
                "            es.employee_id,\n" + //
                "            e.id\n" + //
                "    ) eJs ON eJs.id = pa.employee_id \n" + //
                "GROUP BY\n" + //
                "    cl.course_code,\n" + //
                "    ci." + CI_PK_COLUMN_NAME  + ",\n" + //
                "    sp.period_name \n" + //
                "ORDER BY course_instance;"
            );
    }

    public CourseInstanceDTO fetchCourseInstance(int cid, String year) throws SQLException {
            
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
            System.out.println("2pp" + se);
        }
        return courseInst;
    }

    public ArrayList<PlannedActivityDTO> fetchPlannedActivities(int courseId) throws SQLException {
            
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
                    rs.getInt("factor")
                );

                allPlannedActivities.add(plannedActivity);
            }
                    
        } catch (SQLException se){
            System.out.println("3pp" + se);
        }
        return allPlannedActivities;
    }

    public AdminExamHoursDTO fetchAdminExamHoursForCourse(int cid) throws SQLException {
            
        AdminExamHoursDTO adminExamHours = null;
        
        try {
            fetchAdminExamHoursForCourseStmt.setInt(1, cid);
            ResultSet rs = fetchAdminExamHoursForCourseStmt.executeQuery();
        
            while (rs.next()){
                adminExamHours = new AdminExamHoursDTO(
                    rs.getInt("id"),
                    rs.getInt("admin_hours_per_employee"),
                    rs.getInt("exam_hours_per_employee")
                );
            }
                    
        } catch (SQLException se){
            System.out.println("4pp" + se);
        }
        return adminExamHours;
    }

    public AvgSalaryDTO fetchAvgSalaryEmployee(int eid) throws SQLException {
            
        AvgSalaryDTO avgSalary = null;
        
        try {
            fetchAvgSalaryEmployeeStmt.setInt(1, eid);
            ResultSet rs = fetchAvgSalaryEmployeeStmt.executeQuery();
        
            while (rs.next()){
                avgSalary = new AvgSalaryDTO(
                    rs.getInt("id"),
                    rs.getDouble("average_salary")
                );
            }
                    
        } catch (SQLException se){
            System.out.println("5pp"+se);
        }
        return avgSalary;
    }

    public ArrayList<TeachingCostDTO> showTeachingCostsForCourse(double plannedCost, double actCost, int courseId) 
        throws SQLException {
            
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
            System.out.println("7pp" + se);
        }
        return allTeachingCosts;
    }
        
    /**
     * Task A1
     * 
     * @param cid course_instance_id
     * @return TeachingCostDTO that contains wanted output row if execution is successful, else null
     * @throws SQLException if query can not be executed
     */
    public TeachingCostDTO calculateTeachingCosts(int cid) throws SQLException {
            
        TeachingCostDTO teachingCosts = null;
        
        try {
            computeTeachingCostStmt.setInt(1, cid);
            ResultSet rs = computeTeachingCostStmt.executeQuery();
        
            while (rs.next()){
                teachingCosts = new TeachingCostDTO(
                    rs.getString("course_code"), 
                    rs.getInt("course_instance"), 
                    rs.getString("study_period"),
                    rs.getDouble("planned_cost"),
                    rs.getDouble("actual_cost")
                );
            }
                    
        } catch (SQLException se){
            System.out.println("1pp" + se);
        }
        return teachingCosts;
    }

    public void createTeachingActivity(TADTO TA) throws TeachingActivityDBException {
        String failureMsg = "Could not create teaching activity: " + TA.getTAName();
        boolean updatedRows = false;
        try {
            String activityName = findTAByName(TA.getTAName());
            if (activityName == null) {
                createTAStmt.setString(1, TA.getTAName());
                createTAFactorStmt.setDouble(2, TA.getFactor());
                updatedRows = createTAStmt.executeUpdate()==1 && createTAFactorStmt.executeUpdate()==1;
                if (updatedRows==false) {
                    handleException(failureMsg, null);
                }
            }
        connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
    }
    private String findTAByName(String activityName) throws SQLException {
        ResultSet result = null;
        findTAStmt.setString(1, activityName);//kan vara fel kolumn
        result = findTAStmt.executeQuery();
        if (result.next()) {
            return result.getString(TEACHING_ACTIVITY_COLUMN_ACTIVITY_NAME);
        }
        return null;
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
            throw new TeachingActivityDBException(failureMsg, cause);
        } else {
            throw new TeachingActivityDBException(failureMsg);
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
