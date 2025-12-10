/*
 * The MIT License
 *
 * Copyright 2017 Leif Lindbäck <leifl@kth.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package se.kth.iv1351.bankjdbc.view;

/**
 * Defines all commands that can be performed by a user of the chat application.
 */
public enum Command {
    /**
     * Compute the teaching cost (as planned and actually allocated) of a particular course instance given in the current year.
     */
    COST,
    /**
     *  Modify the number of students in a given course instance.
     */
    MODIFY_COURSE_INSTANCE,
    /**
     * Allocate teaching activities for various course instances for teachers. (not more the max_courses).
     */
    ALLOCATE_PLANNED_ACTIVITY,
    /**
     * Deallocate teaching activities for various course instances for teachers.
     */
    DEALLOCATE_PLANNED_ACTIVITY,
    /**
     * Update the num of courses a teacher can have per period.
     */
    UPDATE_MAX_TEACHER_ALLOCATION,
    /**
     * Adds a new activity type for a specific course instance and teacher. Displays the change. 
     */
    ADD_NEW_TEACHING_ACTIVITY,
    /**
     * Shows all rows of a given teaching activity in planned activity table
     */
    REMOVE_ACTIVITY,
    /**
     * Removes a activity from activity table given an activity name
     */
    SHOW_TEACHING_ACTIVITY,
    /**
     * Lists all commands.
     */
    HELP,
    /**
     * Leave the chat application.
     */
    QUIT,
    /**
     * None of the valid commands above was specified.
     */
    ILLEGAL_COMMAND
}
