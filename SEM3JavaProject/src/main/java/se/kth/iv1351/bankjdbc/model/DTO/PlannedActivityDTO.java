package se.kth.iv1351.bankjdbc.model.DTO;

public class PlannedActivityDTO {

    private final int id;
    private final int employee_id;
    private final int course_instance_id;
    private final int planned_hours;
    private final int allocated_hours;
    private final int tActivity_id;
    private final double factor;

    public PlannedActivityDTO(int id, int employee_id, int course_instance_id, int planned_hours, 
        int allocated_hours, int tActivity_id, double factor){
        
        this.id = id;
        this.employee_id = employee_id;
        this.course_instance_id = course_instance_id;
        this.planned_hours = planned_hours;
        this.allocated_hours = allocated_hours;
        this.tActivity_id = tActivity_id;
        this.factor = factor;
    }

    public int getId(){return this.id;}
    public int getEmpId(){return this.employee_id;}
    public int getCourseId(){return this.course_instance_id;}
    public int getPlannedHours(){return this.planned_hours;}
    public int getAllocatedHours(){return this.allocated_hours;}
    public int getTActivity(){return this.tActivity_id;}
    public double getFactor(){return this.factor;}
}
