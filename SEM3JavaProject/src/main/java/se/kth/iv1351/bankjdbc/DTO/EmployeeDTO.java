package se.kth.iv1351.bankjdbc.DTO;

public class EmployeeDTO {
    private int id;
    private int person_id;
    private int job_title_id;
    private int manager;
    private int department_id;

    public EmployeeDTO(int id, int person, int job_title, int manager, int dep){
        this.id = id;
        this.person_id = person;
        this.job_title_id = job_title;
        this.manager = manager;
        this.department_id = dep;
    }

    public int getId(){return this.id;}
    public int getPersonId(){return this.person_id;}
    public int getJobTitleId(){return this.job_title_id;}
    public int getManager(){return this.manager;}
    public int getDepartmentId(){return this.department_id;}
}