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
        
        int periodAllocation = 0;

        String courseInstancePeriod = dao.findPeriodForCourseInstance(courseInstanceId);
        PlannedActivityDTO plannedActivityDTO = new PlannedActivityDTO(0, employeeId, courseInstanceId, courseInstancePeriod, plannedHours, allocatedHours, activityID, 0);
        ArrayList<TeacherAllocationDTO> teacherAllocations = dao.findTeacherAllocationPeriod(year, employeeId);
        int maxCourses = dao.findMaxCoursesPerTeacher();

        for (TeacherAllocationDTO allocation : teacherAllocations) {
            // if course already exists in the teachers allocations
            if ((allocation.getCourseId() == courseInstanceId) && (courseInstancePeriod.equals(allocation.getPeriod()))) {
                dao.createPlannedActivity(plannedActivityDTO);
                System.out.println("added PA since course instance already exists in pa for that period");
                return;
            }
            // find out the allocation of unique course instances for the current period 
            if (courseInstancePeriod.equals(allocation.getPeriod())){
                periodAllocation++;
            }
        }
        if (periodAllocation < maxCourses) {
            dao.createPlannedActivity(plannedActivityDTO);
            return;
        }
        throw new AllocationLimitExceededException("Could not allocate planned activity for employee: " + employeeId + " in period: " + courseInstancePeriod);
    }
} 
