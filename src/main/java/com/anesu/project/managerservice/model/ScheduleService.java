package com.anesu.project.managerservice.model;

import com.anesu.project.managerservice.entity.ScheduleStatus;
import com.anesu.project.managerservice.entity.schedule.Schedule;
import com.anesu.project.managerservice.entity.shift.ShiftRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {

  /**
   * Approves or rejects a schedule update requested by an employee.
   *
   * @param scheduleId the ID of the {@link Schedule} to approve or reject
   * @param status the approval status (approved or rejected)
   * @return the updated {@link Schedule} after approval or rejection
   */
  Schedule approveOrRejectSchedule(Long scheduleId, ScheduleStatus status);

  /**
   * Updates a specific employee's schedule based on the manager's decision.
   *
   * @param employeeId the ID of the employee whose schedule needs to be updated
   * @param updatedSchedule the updated schedule information
   * @return the updated {@link Schedule}
   */
  Schedule updateEmployeeSchedule(Long employeeId, Schedule updatedSchedule);

  /**
   * Adds a new shift to an employee's schedule after approving a {@link ShiftRequest}.
   *
   * @param employeeId the ID of the employee to update
   * @param approvedShiftRequest the approved {@link ShiftRequest}
   * @return the updated {@link Schedule}
   */
  Schedule addShiftToSchedule(Long employeeId, ShiftRequest approvedShiftRequest);

  /**
   * Retrieves a schedule for a specific employee by ID.
   *
   * @param scheduleId the ID of the {@link Schedule} to retrieve
   * @return the found {@link Schedule}, or {@code null} if not found
   */
  Optional<Schedule> getScheduleById(Long scheduleId);

  /**
   * Retrieves a list of schedules for all employees within a date range.
   *
   * @param startDate the start of the {@link LocalDateTime} range
   * @param endDate the end of the {@link LocalDateTime} range
   * @return a list of {@link Schedule}s for all employees within the date range
   */
  Optional<List<Schedule>> getAllSchedulesInDateRange(
      LocalDateTime startDate, LocalDateTime endDate);

  /**
   * Deletes a specific employee's schedule by ID (only if applicable to manager's permissions).
   *
   * @param scheduleId the ID of the {@link Schedule} to delete
   */
  void deleteSchedule(Long scheduleId);
}
