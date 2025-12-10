package se.kth.iv1351.bankjdbc.model;

public class PlannedActivityDTO {
    private int employee_id;
    private int course_instance_id;
    private int planned_hours;
    private int allocated_hours;
    private int activity_id;

    public PlannedActivityDTO(int employee_id, int instance_id, int planned, int allocated, int activity_id){
        this.employee_id = employee_id;
        this.course_instance_id = instance_id;
        this.planned_hours = planned;
        this.allocated_hours = allocated;
        this.activity_id = activity_id;
    }

    public int getEmployeeId(){return this.employee_id;}
    public int getCourseInstance(){return this.course_instance_id;}
    public int getPlannedHours(){return this.planned_hours;}
    public int getAllocatedHours(){return this.allocated_hours;}
    public int getActivityId(){return this.activity_id;}
}