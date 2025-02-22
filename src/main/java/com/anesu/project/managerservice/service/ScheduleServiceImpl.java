package com.anesu.project.managerservice.service;

import com.anesu.project.managerservice.entity.ScheduleStatus;
import com.anesu.project.managerservice.entity.schedule.Schedule;
import com.anesu.project.managerservice.entity.shift.ShiftRequest;
import com.anesu.project.managerservice.entity.shift.ShiftRequestStatus;
import com.anesu.project.managerservice.model.ScheduleService;
import com.anesu.project.managerservice.model.repository.ScheduleRepository;
import com.anesu.project.managerservice.service.exception.InvalidScheduleException;
import com.anesu.project.managerservice.service.exception.ScheduleNotFoundException;
import com.anesu.project.managerservice.service.util.ScheduleValidator;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ScheduleServiceImpl implements ScheduleService {

  private static final String SCHEDULE_NOT_FOUND_EXCEPTION = "Schedule not found with id ";
  private final ScheduleRepository scheduleRepository;
  private final ScheduleValidator scheduleValidator;

  public ScheduleServiceImpl(
      ScheduleRepository scheduleRepository, ScheduleValidator scheduleValidator) {
    this.scheduleRepository = scheduleRepository;
    this.scheduleValidator = scheduleValidator;
  }

  @Override
  public Schedule approveOrRejectSchedule(Long scheduleId, ScheduleStatus status) {
    Schedule schedule =
        scheduleRepository
            .findById(scheduleId)
            .orElseThrow(
                () -> new ScheduleNotFoundException(SCHEDULE_NOT_FOUND_EXCEPTION + scheduleId));
    try {
      scheduleValidator.validateSchedule(schedule);
      schedule.setStatus(ScheduleStatus.APPROVED);
      System.out.println("Schedule approved!");
    } catch (InvalidScheduleException exception) {
      schedule.setStatus(ScheduleStatus.REJECTED);
      System.out.println("Schedule rejected due to validation failure.");
    }

    return scheduleRepository.save(schedule);
  }

  @Override
  public Schedule updateEmployeeSchedule(Long scheduleId, Schedule updatedSchedule) {
    Schedule existingEmployeeScheduleToUpdate =
        scheduleRepository
            .findById(scheduleId)
            .orElseThrow(
                () -> new ScheduleNotFoundException(SCHEDULE_NOT_FOUND_EXCEPTION + scheduleId));

    updatedExistingEmployeeSchedule(updatedSchedule, existingEmployeeScheduleToUpdate);

    scheduleValidator.validateSchedule(updatedSchedule);

    return scheduleRepository.save(updatedSchedule);
  }

  @Override
  public Schedule addShiftToSchedule(Long employeeId, ShiftRequest approvedShiftRequest) {
    // TODO: validate or not validate "approvedShiftRequest" even though it has been approved by the
    // manager?

    if (!ShiftRequestStatus.APPROVED.equals(approvedShiftRequest.getStatus())) {
      throw new InvalidScheduleException(
          "Invalid schedule operation. Only approved shifts can be added to the schedule.");
    }

    // Checking if there is an existing Schedule for that calendar week: (extract method)
    LocalDateTime startOfShiftCalendarWeek = approvedShiftRequest.getShiftDate().with(DayOfWeek.MONDAY);
    LocalDateTime endOfShiftCalendarWeek = approvedShiftRequest.getShiftDate().with(DayOfWeek.SUNDAY);

    Optional<Schedule> scheduleInApprovedShiftCalendarWeek =
            scheduleRepository.findByEmployeeIdAndCalendarWeek(
                    employeeId, startOfShiftCalendarWeek, endOfShiftCalendarWeek);

    // Add the new shift to schedule or create a new one (extract method)




    return null;
  }

  @Override
  public Optional<Schedule> getScheduleById(Long scheduleId) {
    return scheduleRepository.findById(scheduleId);
  }

  @Override
  public Optional<List<Schedule>> getAllEmployeeSchedulesWithinGivenDateRange(
      Long scheduleId, LocalDateTime startDate, LocalDateTime endDate) {
    return scheduleRepository.findByAllEmployeeIdAndGivenDateRange(scheduleId, startDate, endDate);
  }

  @Override
  public void deleteSchedule(Long scheduleId) {
    if (!scheduleRepository.existsById(scheduleId)) {
      throw new ScheduleNotFoundException(SCHEDULE_NOT_FOUND_EXCEPTION + scheduleId);
    }
    scheduleRepository.deleteById(scheduleId);
  }

  private Schedule updatedExistingEmployeeSchedule(
      Schedule updatedSchedule, Schedule existingSchedule) {
    existingSchedule.setStartDate(updatedSchedule.getStartDate());
    existingSchedule.setEndDate(updatedSchedule.getEndDate());
    existingSchedule.setShifts(updatedSchedule.getShifts());
    existingSchedule.setVacations(updatedSchedule.getVacations());
    existingSchedule.setTotalWorkingHours(updatedSchedule.getTotalWorkingHours());

    return existingSchedule;
  }
}
