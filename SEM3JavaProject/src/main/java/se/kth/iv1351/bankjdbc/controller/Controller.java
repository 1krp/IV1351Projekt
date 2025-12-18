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

import java.util.ArrayList;

import se.kth.iv1351.bankjdbc.integration.TeachingActivityDAO;
import se.kth.iv1351.bankjdbc.integration.TeachingActivityDBException;
import se.kth.iv1351.bankjdbc.model.RejectedException;
import se.kth.iv1351.bankjdbc.model.TeachingCostCalculator;
import se.kth.iv1351.bankjdbc.model.DTO.PAjoinTADTO;
import se.kth.iv1351.bankjdbc.model.TeacherAllocatePA;
import se.kth.iv1351.bankjdbc.model.DTO.TeachingCostDTO;

/**
 * This is the application's only controller, all calls to the model pass here.
 * The controller is also responsible for calling the DAO. Typically, the
 * controller first calls the DAO to retrieve data (if needed), then operates on
 * the data, and finally tells the DAO to store the updated data (if any).
 */
public class Controller {
    private final TeachingActivityDAO teachingActivityDAO;
    private final TeachingCostCalculator tcCalculator;
    private final TeacherAllocatePA tAllocatePA;

    /**
     * Creates a new instance, and retrieves a connection to the database.
     * 
     * @throws TeachingActivityDbException If unable to connect to the database.
     */
    public Controller() throws TeachingActivityDBException {
        teachingActivityDAO = new TeachingActivityDAO();
        tcCalculator = new TeachingCostCalculator(teachingActivityDAO);
        tAllocatePA = new TeacherAllocatePA(teachingActivityDAO);
    }

    /**
     * Task A1 - Fetches the teaching costs for a given course and year
     * Communicates with database through the model layer
     * 
     * @param courseId   course instance id of course to calculate teaching costs for
     * @param year  studdy year to calculate teaching costs for
     * @return      a list with the teaching costs for a course for each study period of a given year
     */
    public ArrayList<TeachingCostDTO> fetchTeachingCostsForCourse(int courseId, String year){
        return tcCalculator.calculateTeachingCostsForCourse(courseId, year);
    }

    /**
     * Task A2 - Modifies number of students for a course instance
     * 
     * @param courseInstanceId
     * @param numStudents
     * @throws RejectedException
     */
    public void modifyNumStudendsInCourseInstance(int courseInstanceId, int numStudents) 
        throws RejectedException {

        String failureMsg = "Could not update num_students to " + numStudents;
        
        if (courseInstanceId < 0 || numStudents < 0) {
            throw new RejectedException(failureMsg);
        }

        try {
            teachingActivityDAO.updateNumStudendsInCourseInstance(courseInstanceId, numStudents);
        } catch (TeachingActivityDBException tadbe) {
            throw new RejectedException(failureMsg, tadbe);
        } catch (Exception e) {
            commitOngoingTransaction(failureMsg);
            throw new RejectedException(failureMsg, e);
        }
    }

    /**
     * 
     * @param newLimit
     * @throws RejectedException
     */
    public void updateTeacherAllocationLimit(int newLimit) throws RejectedException {
        String failureMsg = "Could not update teacher allocation limit to: " + newLimit 
                + ". Limit must be zero or higher.";

        if (newLimit < 0) {
            throw new RejectedException(failureMsg);
        }

        try {
            teachingActivityDAO.updateTeacherAllocationLimit(newLimit);
        } catch (TeachingActivityDBException tadbe) {
            throw new RejectedException(failureMsg, tadbe);
        } catch (Exception e) {
            throw new RejectedException(failureMsg, e);
        }
    }

    /**
     * Task A4 - Add a new teaching activity
     * 
     * @param activityName
     * @param factor
     * @param employee_id
     * @param course_instance_id
     * @param planned_hours
     * @param allocated_hours
     * @throws RejectedException
     */
    public void insertNewActivityWithAssociations(String activityName, double factor, int employee_id, 
        int course_instance_id, int planned_hours, int allocated_hours) throws RejectedException{

        String failureMsg = "Could not insert "+ activityName +" into planned activity";

        try {
            teachingActivityDAO.createTAInPA(activityName, factor, employee_id, course_instance_id, 
                planned_hours, allocated_hours);
        } catch(TeachingActivityDBException tadbe){
            throw new RejectedException(failureMsg, tadbe);
        } catch (Exception e) {
            commitOngoingTransaction(failureMsg);
            throw new RejectedException(failureMsg, e);
        }
    }

    /**
     * 
     * @param activityName
     * @return
     * @throws TeachingActivityDBException
     */
    public ArrayList<PAjoinTADTO> showTeachingActivity(String activityName) 
        throws TeachingActivityDBException{

        return teachingActivityDAO.showTAs(activityName);
    }

    /**
     * 
     * @param plannedActivityId
     * @throws RejectedException
     */
    public void deallocatePlannedActivity(int plannedActivityId) throws RejectedException {

        String failureMsg = "Could not deallocate the planned activity with id: " + plannedActivityId;

        if (plannedActivityId < 1) {
            throw new RejectedException(failureMsg);
        }

        try {
            teachingActivityDAO.deallocatePlannedActivity(plannedActivityId); 
        } catch (Exception e) {
            commitOngoingTransaction(failureMsg);
            throw new RejectedException(failureMsg, e);
        }
    }

    /**
     * 
     * @param employeeId
     * @param courseInstanceId
     * @param plannedHours
     * @param activityID
     * @param allocatedHours
     * @param year
     * @throws RejectedException
     */
    public void allocatePlannedActivity(int employeeId, int courseInstanceId, String periodName, int plannedHours, 
        int activityID, int allocatedHours, String year) throws RejectedException {

        String failureMsg = "could not allocate activity";

        try {
            tAllocatePA.allocatePlannedActivity(employeeId, courseInstanceId, periodName, plannedHours, 
                activityID, allocatedHours, year);
        } catch(TeachingActivityDBException tadbe){
            throw new RejectedException(failureMsg, tadbe);
        } catch (Exception e) {
            commitOngoingTransaction(failureMsg);
            throw new RejectedException(failureMsg, e);
        }
    }
    
    /**
     * 
     * @param failureMsg
     * @throws RejectedException
     */
    private void commitOngoingTransaction(String failureMsg) throws RejectedException {
        try {
            teachingActivityDAO.commit();
        } catch (TeachingActivityDBException tadbe) {
            throw new RejectedException(failureMsg, tadbe);
        }
    }
    
}