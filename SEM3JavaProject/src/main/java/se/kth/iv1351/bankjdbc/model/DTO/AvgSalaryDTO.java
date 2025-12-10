package se.kth.iv1351.bankjdbc.model.DTO;

public class AvgSalaryDTO {
    
    private final int id;
    private final double avgSalary;

    public AvgSalaryDTO(int id, double avgSalary){
        this.id = id;
        this.avgSalary = avgSalary;
    }

    public int getId(){return this.id;}
    public double getAvgSalary(){return this.avgSalary;}
}
