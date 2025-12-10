package se.kth.iv1351.bankjdbc.model;

import java.util.ArrayList;
import java.util.List;

import se.kth.iv1351.bankjdbc.model.DTO.TeacherAllocationDTO;

/**
 * Domain logic for checking if a teacher allocation exceeds the configured limit.
 */
public final class TeacherAllocationLimit {
    private TeacherAllocationLimit() {}

    /**
     * @param allocation A teacher allocation for a study period.
     * @param maxCoursesPerPeriod Maximum number of courses allowed per teacher in one period.
     * @return {@code true} if the allocation exceeds the allowed maximum, otherwise {@code false}.
     */
    public static boolean exceedsLimit(TeacherAllocationDTO allocation, int maxCoursesPerPeriod) {
        return allocation.getNumCourses() > maxCoursesPerPeriod;
    }

    /**
     * Filters all allocations that exceed the specified per-period limit.
     *
     * @param allocations             Allocations for a teacher.
     * @param maxCoursesPerPeriod     Maximum courses allowed per period.
     * @return Allocations where {@link #exceedsLimit(TeacherAllocationDTO, int)} is {@code true}.
     */
    public static List<TeacherAllocationDTO> exceedingAllocations(List<TeacherAllocationDTO> allocations,
            int maxCoursesPerPeriod) {
        List<TeacherAllocationDTO> exceeding = new ArrayList<>();

        for (TeacherAllocationDTO allocation : allocations) {
            if (exceedsLimit(allocation, maxCoursesPerPeriod)) {
                exceeding.add(allocation);
            }
        }

        return exceeding;
    }
    
}