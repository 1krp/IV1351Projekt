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
import se.kth.iv1351.bankjdbc.model.TADTO;

/**
 * This data access object (DAO) encapsulates all database calls in the bank
 * application. No code outside this class shall have any knowledge about the
 * database.
 */
public class TeachingActivityDAO {
    private Connection connection;

    private static final String EC_C_TABLE_NAME = "employment_constants";
    private static final String EC_C_COLUMN_NAME = "max_courses";
    private static final String EC_C_PK_COLUMN_NAME = "id";
    private static final String TEACHING_ACTIVITY_TABLE_NAME = "teaching_activity";
    private static final String TEACHING_ACTIVITY_PK_COLUMN_NAME = "id";
    private static final String TEACHING_ACTIVITY_COLUMN_ACTIVITY_NAME = "activity_name";
    private static final String TEACHING_ACTIVITY_COLUMN_FACTOR = "factor";
    private static final String PLANNED_ACTIVITY_TABLE_NAME = "planned_activity";
    private static final String PLANNED_ACTIVITY_COLUMN_ACTIVITY_ID = "activity_id";

    private PreparedStatement updateTeacherAllocationLimitStmt;

    private PreparedStatement createTAStmt;
    private PreparedStatement createTAFactorStmt;
    private PreparedStatement createTAPAconnectionStmt;
    private PreparedStatement findTAStmt;

    /**
     * Constructs a new DAO object connected to the bank database.
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
                "postgres", "asd123");
        connection.setAutoCommit(false);
    }

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
    }
    public void createTeachingActivity(TADTO TA) throws TeachingActivityDBException {
        String failureMsg = "Could not create teaching activity: " + TA.getTAName();
        int updatedRows = 0;
        try {
            int holderPK = findTAByName(TA.getTAName());
            if (holderPK == 0) {
                createTAStmt.setString(1, TA.getHolderName());
                updatedRows = createHolderStmt.executeUpdate();
                if (updatedRows != 1) {
                    handleException(failureMsg, null);
                }
                holderPK = findHolderPKByName(TA.getHolderName());
            }

            createAccountStmt.setInt(1, createAccountNo());
            createAccountStmt.setInt(2, account.getBalance());
            createAccountStmt.setInt(3, holderPK);
            updatedRows = createAccountStmt.executeUpdate();
            if (updatedRows != 1) {
                handleException(failureMsg, null);
            }

            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
    }
    private int findTAByName(String activityName) throws SQLException {
        ResultSet result = null;
        findTAStmt.setString(1, activityName);//kan vara fel kolumn
        result = findTAStmt.executeQuery();
        if (result.next()) {
            return result.getInt(TEACHING_ACTIVITY_COLUMN_ACTIVITY_NAME);
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
