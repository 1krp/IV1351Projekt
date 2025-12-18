package se.kth.iv1351.bankjdbc.model;

import java.util.ArrayList;

import se.kth.iv1351.bankjdbc.integration.TeachingActivityDAO;
import se.kth.iv1351.bankjdbc.integration.TeachingActivityDBException;
import se.kth.iv1351.bankjdbc.model.DTO.PlannedActivityDTO;
import se.kth.iv1351.bankjdbc.model.DTO.AllocatedActivityDTO;

public class TeacherAllocatePA {

    TeachingActivityDAO dao;

    public TeacherAllocatePA(TeachingActivityDAO dao){
        this.dao = dao;
    }

    public void allocatePlannedActivity(int employeeId, int courseInstanceId, 
        String periodName, int plannedHours, int activityID, int allocatedHours, String year) 
            throws TeachingActivityDBException {

        try {
            int maxCourses = dao.findMaxCoursesPerTeacher();
            ArrayList<AllocatedActivityDTO> teacherAllocations = 
                dao.fetchTeacherAllocationPeriod(year, employeeId, periodName);

            int countedInstances = countInstancesInPeriod(teacherAllocations);

            if (countedInstances < maxCourses) {

                PlannedActivityDTO plannedActivityDTO = 
                    new PlannedActivityDTO(0, employeeId, courseInstanceId, periodName, plannedHours, 
                        allocatedHours, activityID, 0);
                
                dao.createPlannedActivity(plannedActivityDTO);
                return;
            } else {
                throw new AllocationLimitExceededException(
                    "Activity dismissed! Employee " + employeeId + 
                    " already have " + maxCourses + " allocated courses for period " + periodName + ".");  
            }
        } catch (TeachingActivityDBException tae) {
            dao.rollback();
            System.out.println("Error when allocating new planned activity: " + tae.getMessage());
        } 
    }

    private int countInstancesInPeriod(ArrayList<AllocatedActivityDTO> activities){

        ArrayList<Integer> countedInstances = new ArrayList<>();
    
        for (AllocatedActivityDTO dto : activities){

            if (!countedInstances.contains(dto.getCiid())) {
                countedInstances.add(dto.getCiid());
            }
        }

        System.out.println(countedInstances.size());
    
        return countedInstances.size();
    }
}
