package se.kth.iv1351.bankjdbc.model.DTO;

public class SalaryDTO {
    
    private final int employee_id;
    private final double salary_per_hour;

    public SalaryDTO(int id, double salary){
        this.employee_id = id;
        this.salary_per_hour = salary;
    }

    public int getId(){return this.employee_id;}
    public double getSalary(){return this.salary_per_hour;}
}
