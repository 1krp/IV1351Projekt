package se.kth.iv1351.bankjdbc.model.DTO;

import java.sql.Date;

public class SalaryDTO {
    
    private final int employee_id;
    private final Date salary_enforcement_date;
    private final double salary_per_hour;

    public SalaryDTO(int id, Date startDate, double salary){
        this.employee_id = id;
        this.salary_enforcement_date = startDate;
        this.salary_per_hour = salary;
    }

    public int getId(){return this.employee_id;}
    public Date getEnforcementDate(){return salary_enforcement_date;}
    public double getSalary(){return this.salary_per_hour;}
}
