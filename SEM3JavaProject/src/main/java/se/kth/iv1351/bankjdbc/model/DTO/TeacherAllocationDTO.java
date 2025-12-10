package se.kth.iv1351.bankjdbc.model.DTO;

/**
 * Specifies a read-only view of a teacher's allocation.
 */
public interface TeacherAllocationDTO {
    /**
     * @return Number of unique courses allocated to the teacher during the period.
     */
    int getNumCourses();

    /**
     * @return Name of the study period.
     */
    String getPeriod();
}
