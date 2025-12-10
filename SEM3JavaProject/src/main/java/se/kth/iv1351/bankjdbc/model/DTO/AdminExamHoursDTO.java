package se.kth.iv1351.bankjdbc.model.DTO;

public class AdminExamHoursDTO {

    private final int id;
    private final int admin_hours_per_employee;
    private final int exam_hours_per_employee;

    public AdminExamHoursDTO(int id, int admin_hours, int exam_hours){
        
        this.id = id;
        this.admin_hours_per_employee = admin_hours;
        this.exam_hours_per_employee = exam_hours;
    }

    public int getId(){return this.id;}
    public int getAdminHours(){return this.admin_hours_per_employee;}
    public int getExamHours(){return this.exam_hours_per_employee;}
}
