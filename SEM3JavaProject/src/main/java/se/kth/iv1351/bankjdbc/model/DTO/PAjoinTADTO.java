package se.kth.iv1351.bankjdbc.model.DTO;

public class PAjoinTADTO {
    private int employee_id;
    private int course_instance_id;
    private int planned_hours;
    private int allocated_hours;
    private String activityName;

    public PAjoinTADTO(int employee_id, int course_instance_id, int planned_hours, int allocated_hours, String activityName){
        this.employee_id = employee_id;
        this.course_instance_id = course_instance_id;
        this.planned_hours = planned_hours;
        this.allocated_hours = allocated_hours;
        this.activityName = activityName;
    }
    public int getEmployeeId(){return this.employee_id;}
    public int getCourseInstanceId(){return this.course_instance_id;}
    public int getPlannedHours(){return this.planned_hours;}
    public int getAllocatedHours(){return this.allocated_hours;}
    public String getActivityName(){return this.activityName;}



}
