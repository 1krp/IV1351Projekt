package se.kth.iv1351.bankjdbc.DTO;

public class TeachingCostDTO {
    private String course_code;
    private int course_instance;
    private String study_period;
    private double planned_cost;
    private double actual_cost;

    public TeachingCostDTO(String code, int instance_id, String period, double planned, double actual){
        this.course_code = code;
        this.course_instance = instance_id;
        this.study_period = period;
        this.planned_cost = planned;
        this.actual_cost = actual;
    }

    public String getCourseCode(){return this.course_code;}
    public int getCourseInstance(){return this.course_instance;}
    public String getStudyPeriod(){return this.study_period;}
    public double getPlannedCost(){return this.planned_cost;}
    public double getActualCost(){return this.actual_cost;}
}