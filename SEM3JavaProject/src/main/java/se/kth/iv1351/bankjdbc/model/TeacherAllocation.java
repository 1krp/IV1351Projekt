package se.kth.iv1351.bankjdbc.model;
import se.kth.iv1351.bankjdbc.model.DTO.TeacherAllocationDTO;


public class TeacherAllocation implements TeacherAllocationDTO {
    private int numCourses;
    private String period;


    public TeacherAllocation(int numCourses, String period){
        this.numCourses = numCourses;
        this.period = period;

    }

    @Override
    public int getNumCourses(){
        return numCourses;
    }

    @Override
    public String getPeriod(){
        return period;
    }


}
 
