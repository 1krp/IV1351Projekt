package se.kth.iv1351.bankjdbc.model;

import se.kth.iv1351.bankjdbc.integration.TeachingActivityDAO;
import se.kth.iv1351.bankjdbc.integration.TeachingActivityDBException;
import se.kth.iv1351.bankjdbc.model.DTO.AdminExamHoursDTO;
import se.kth.iv1351.bankjdbc.model.DTO.SalaryDTO;
import se.kth.iv1351.bankjdbc.model.DTO.CourseInstanceDTO;
import se.kth.iv1351.bankjdbc.model.DTO.PlannedActivityDTO;
import se.kth.iv1351.bankjdbc.model.DTO.TeachingCostDTO;

import java.util.ArrayList;
import java.sql.Date;

public class TeachingCostCalculator {
    
    TeachingActivityDAO dao;

    public TeachingCostCalculator(TeachingActivityDAO dao){
        this.dao = dao;
    }

    /**
     * Task A1 - Calculate teaching costs for a given course and year
     * Communicates directly with database
     * Transaction is commited after last query
     * 
     * @param courseId  course_instance_id for calculated course
     * @param year      study year to calculate for
     * @return          a list with the teaching costs for a course for each study period of a given year, or null
     */
    public ArrayList<TeachingCostDTO> calculateTeachingCostsForCourse(int courseId, String year){

        double plannedCost = 0;
        double actualCost = 0;

        try {
            CourseInstanceDTO courseInstance = dao.fetchCourseInstance(courseId, year);
            ArrayList<PlannedActivityDTO> plannedActivities = dao.fetchPlannedActivities(courseId);
            AdminExamHoursDTO adminExamHours = dao.fetchAdminExamHoursForCourse(courseId);

            System.out.println(plannedActivities.get(0).getStudyPeriod());

            for (PlannedActivityDTO act : plannedActivities) {

                ArrayList<SalaryDTO> salaries = dao.fetchSalaryEmployee(act.getEmpId());

                // Average salary for planned vost
                double avgSalary = calcAvgSalary(dao.fetchSalaryEmployee(act.getEmpId()));

                // Actual salary by the start of study period for actual cost
                double actualSalary = getEmployeePeriodSalary(year, act, salaries);

                plannedCost += (act.getPlannedHours() + adminExamHours.getAdminHours() + adminExamHours.getExamHours())
                    * avgSalary;
                
                actualCost += (act.getAllocatedHours() + adminExamHours.getAdminHours() + adminExamHours.getExamHours())
                    * actualSalary;
            }

            ArrayList<TeachingCostDTO> result = dao.createTeachingCostsForCourseView(
                    plannedCost, actualCost, courseInstance.getId());

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

    private double getEmployeePeriodSalary(String year, PlannedActivityDTO paDTO, ArrayList<SalaryDTO> salDTOs){
        
        Date startDate = getPeriodStartDate(year, paDTO.getStudyPeriod());
        SalaryDTO currentSalary = null;

        for (SalaryDTO sal : salDTOs) {
            Date enfData = sal.getEnforcementDate();

            if (!enfData.after(startDate)) {
                if (currentSalary == null || enfData.after(currentSalary.getEnforcementDate())) {
                    currentSalary = sal;
                }
            }
        }

        return currentSalary != null ? currentSalary.getSalary() : 0.0;

    }

    private Date getPeriodStartDate(String year, String studyPeriod){

        Date date;

        switch (studyPeriod) {
            case "P1":
                date = Date.valueOf(year + "-01-01");
                break;
            case "P2":
                date = Date.valueOf(year + "-04-01");
                break;
            case "P3":
                date = Date.valueOf(year + "-07-01");
                break;
            case "P4":
                date = Date.valueOf(year + "-10-01");
                break;
            default:
                throw new IllegalArgumentException("Unknown study period: " + studyPeriod);
        }

        return date;
    }
}
