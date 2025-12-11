package se.kth.iv1351.bankjdbc.model;

import java.util.ArrayList;

import se.kth.iv1351.bankjdbc.integration.TeachingActivityDAO;
import se.kth.iv1351.bankjdbc.integration.TeachingActivityDBException;
import se.kth.iv1351.bankjdbc.model.DTO.PlannedActivityDTO;
import se.kth.iv1351.bankjdbc.model.DTO.TeacherAllocationDTO;

public class TeacherAllocatePA {

    TeachingActivityDAO dao;


    public TeacherAllocatePA(TeachingActivityDAO dao){
        this.dao = dao;
    }


    public void allocatePlannedActivity(int employeeId, int courseInstanceId, int plannedHours, int activityID, int allocatedHours, String year) throws TeachingActivityDBException {

        PlannedActivityDTO plannedActivityDTO = new PlannedActivityDTO(0, employeeId, courseInstanceId, plannedHours, allocatedHours, activityID, 0);
        String courseInstancePeriod = dao.findPeriodForCourseInstance(courseInstanceId);
        ArrayList<TeacherAllocationDTO> teacherAllocations = dao.findTeacherAllocationPeriod(year, employeeId);
        int maxCourses = dao.findMaxCoursesPerTeacher();

        for (TeacherAllocationDTO allocation : teacherAllocations) {
            
            if ( (courseInstancePeriod.equals(allocation.getPeriod()) && (allocation.getNumCourses() < maxCourses))) {    
                dao.createPlannedActivity(plannedActivityDTO);
                return;
            }
        }
        throw new AllocationLimitExceededException("Could not allocate planned activity for employee: " + employeeId + " in period: " + courseInstancePeriod);
    }
} 
