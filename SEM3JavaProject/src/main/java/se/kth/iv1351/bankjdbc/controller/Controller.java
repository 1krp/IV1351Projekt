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

package se.kth.iv1351.bankjdbc.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import se.kth.iv1351.bankjdbc.integration.TeachingActivityDAO;
import se.kth.iv1351.bankjdbc.integration.TeachingActivityDBException;
import se.kth.iv1351.bankjdbc.model.RejectedException;
import se.kth.iv1351.bankjdbc.model.TeachingCostCalculator;
import se.kth.iv1351.bankjdbc.model.DTO.PAjoinTADTO;
import se.kth.iv1351.bankjdbc.model.DTO.TeachingCostDTO;

/**
 * This is the application's only controller, all calls to the model pass here.
 * The controller is also responsible for calling the DAO. Typically, the
 * controller first calls the DAO to retrieve data (if needed), then operates on
 * the data, and finally tells the DAO to store the updated data (if any).
 */
public class Controller {
    private final TeachingActivityDAO TeachingActivityDb;
    private final TeachingCostCalculator tcCalculator;

    /**
     * Creates a new instance, and retrieves a connection to the database.
     * 
     * @throws TeachingActivityDbException If unable to connect to the database.
     */
    public Controller() throws TeachingActivityDBException {
        TeachingActivityDb = new TeachingActivityDAO();
        tcCalculator = new TeachingCostCalculator(TeachingActivityDb);
    }

    public ArrayList<TeachingCostDTO> fetchTeachingCostsForCourse(int cid, String year){

        ArrayList<TeachingCostDTO> courseTeachingCosts = new ArrayList<>();
        courseTeachingCosts = tcCalculator.calculateTeachingCostsForCourse(cid, year);
        return courseTeachingCosts;
    }

    public void updateTeacherAllocationLimit(int newLimit) throws RejectedException {
        String failureMsg = "Could not update teacher allocation limit to: " + newLimit 
                + ". Limit must be zero or higher.";

        if (newLimit < 0) {
            throw new RejectedException(failureMsg);
        }

        try {
            TeachingActivityDb.updateTeacherAllocationLimit(newLimit);
        } catch (TeachingActivityDBException tadbe) {
            throw new RejectedException(failureMsg, tadbe);
        } catch (Exception e) {
            throw new RejectedException(failureMsg, e);
        }
    }


    public void insertNewActivityWithAssociations(String activityName, double factor, int employee_id, int course_instance_id, int planned_hours, int allocated_hours) throws RejectedException{
        String failureMsg = "Could not insert "+ activityName +" into planned activity";
        try{
            TeachingActivityDb.createTAInPA(activityName, factor, employee_id, course_instance_id, planned_hours, allocated_hours);
        }
        catch(TeachingActivityDBException tadbe){
            throw new RejectedException(failureMsg, tadbe);
        }catch (Exception e) {
            commitOngoingTransaction(failureMsg);
            throw new RejectedException(failureMsg, e);
        }
    }
    
    public ArrayList<PAjoinTADTO> showTeachingActivity(String activityName) throws TeachingActivityDBException{
        return TeachingActivityDb.showTAs(activityName);
    }
    public void modifyNumStudendsInCourseInstance(int courseInstanceId, int numStudents) throws RejectedException {
        String failureMsg = "Could not update num_students to " + numStudents;
        
        if (courseInstanceId < 0 || numStudents < 0) {
            throw new RejectedException(failureMsg);
        }

        try {
            TeachingActivityDb.updateNumStudendsInCourseInstance(courseInstanceId, numStudents);
        } catch (TeachingActivityDBException tadbe) {
            throw new RejectedException(failureMsg, tadbe);
        } catch (Exception e) {
            commitOngoingTransaction(failureMsg);
            throw new RejectedException(failureMsg, e);
        }
    } 
    
     
    private void commitOngoingTransaction(String failureMsg) throws RejectedException {
        try {
            TeachingActivityDb.commit();
        } catch (TeachingActivityDBException tadbe) {
            throw new RejectedException(failureMsg, tadbe);
        }
    }
    
}