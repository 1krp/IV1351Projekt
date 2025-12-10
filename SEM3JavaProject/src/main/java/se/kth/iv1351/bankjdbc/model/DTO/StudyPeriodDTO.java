package se.kth.iv1351.bankjdbc.model.DTO;

public class StudyPeriodDTO {
    private final int course_instance_id;
    private final String study_period;

    public StudyPeriodDTO(int id, String period){
        this.course_instance_id = id;
        this.study_period = period;
    }

    public int getId(){return this.course_instance_id;}
    public String getStudyPeriod(){return this.study_period;}
}
