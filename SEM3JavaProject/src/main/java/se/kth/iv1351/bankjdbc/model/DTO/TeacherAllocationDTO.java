package se.kth.iv1351.bankjdbc.model.DTO;

public class TeacherAllocationDTO {

    private final int numCourses;
    private final String period;

    public TeacherAllocationDTO(int numCourses, String period){
        
        this.numCourses = numCourses;
        this.period = period;

    }

    public int getNumCourses(){return this.numCourses;}
    public String getPeriod(){return this.period;}
    
}
