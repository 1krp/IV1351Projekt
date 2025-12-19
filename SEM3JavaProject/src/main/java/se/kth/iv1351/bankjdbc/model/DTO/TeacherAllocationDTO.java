package se.kth.iv1351.bankjdbc.model.DTO;

public class TeacherAllocationDTO {

    private final int courseId;
    private final String period;

    public TeacherAllocationDTO(int courseId, String period){
        
        this.courseId = courseId;
        this.period = period;

    }

    public int getCourseId(){return this.courseId;}
    public String getPeriod(){return this.period;}
    
}
