package se.kth.iv1351.bankjdbc.model;

import se.kth.iv1351.bankjdbc.integration.TeachingActivityDAO;
import se.kth.iv1351.bankjdbc.integration.TeachingActivityDBException;
import se.kth.iv1351.bankjdbc.model.DTO.AdminExamHoursDTO;
import se.kth.iv1351.bankjdbc.model.DTO.SalaryDTO;
import se.kth.iv1351.bankjdbc.model.DTO.CourseInstanceDTO;
import se.kth.iv1351.bankjdbc.model.DTO.PlannedActivityDTO;
import se.kth.iv1351.bankjdbc.model.DTO.TeachingCostDTO;

import java.util.ArrayList;

public class TeachingCostCalculator {
    
    TeachingActivityDAO dao;

    public TeachingCostCalculator(TeachingActivityDAO dao){
        this.dao = dao;
    }

    public ArrayList<TeachingCostDTO> calculateTeachingCostsForCourse(int courseId, String year){

        double plannedCost = 0;
        double actualCost = 0;

        try {
            CourseInstanceDTO courseInstance = dao.fetchCourseInstance(courseId, year);
            ArrayList<PlannedActivityDTO> plannedActivities = dao.fetchPlannedActivities(courseId);
            AdminExamHoursDTO adminExamHours = dao.fetchAdminExamHoursForCourse(courseId);

            for (PlannedActivityDTO act : plannedActivities) {

                double avgSalary = calcAvgSalary(dao.fetchSalaryEmployee(act.getEmpId()));

                plannedCost += (act.getPlannedHours() + adminExamHours.getAdminHours() + adminExamHours.getExamHours())
                    * avgSalary;
                
                actualCost += (act.getAllocatedHours() + adminExamHours.getAdminHours() + adminExamHours.getExamHours())
                    * avgSalary;
            }

            ArrayList<TeachingCostDTO> result = dao.showTeachingCostsForCourse(plannedCost, actualCost, courseInstance.getId());

            dao.commit();

            return result;
        } catch (TeachingActivityDBException tae) {
            dao.rollback();
            System.out.println("Error when calculating teaching costs: " + tae.getMessage());
        }
        return null;
    }

    private double calcAvgSalary(ArrayList<SalaryDTO> salaries){
        double total = 0;
        int n = 0;

        for (SalaryDTO sal : salaries){
            total += sal.getSalary();
            n ++;
        }

        return total/n;
    }
}
