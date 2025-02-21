package com.anesu.project.managerservice.service;

import com.anesu.project.managerservice.entity.ScheduleStatus;
import com.anesu.project.managerservice.entity.schedule.Schedule;
import com.anesu.project.managerservice.entity.shift.ShiftRequest;
import com.anesu.project.managerservice.model.ScheduleService;
import com.anesu.project.managerservice.model.repository.ScheduleRepository;
import com.anesu.project.managerservice.service.exception.InvalidScheduleException;
import com.anesu.project.managerservice.service.exception.ScheduleNotFoundException;
import com.anesu.project.managerservice.service.util.ScheduleValidator;
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
      ScheduleRepository scheduleRepository,
      ScheduleValidator scheduleValidator,
      ScheduleValidator scheduleValidator1) {
    this.scheduleRepository = scheduleRepository;
    this.scheduleValidator = scheduleValidator1;
  }

  @Override
  public Schedule approveOrRejectSchedule(Long scheduleId, ScheduleStatus status) {
    Schedule schedule =
        scheduleRepository
            .findById(scheduleId)
            .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found."));
    try {
      scheduleValidator.validateSchedule(schedule);
      schedule.setStatus(ScheduleStatus.APPROVED);
      System.out.println("Schedule approved!");
    } catch (InvalidScheduleException exception) {
      schedule.setStatus(ScheduleStatus.APPROVED);
      System.out.println("Schedule rejected due to validation failure.");
    }

    return scheduleRepository.save(schedule);
  }

  @Override
  public Schedule updateEmployeeSchedule(Long scheduleId, Schedule updatedSchedule) {
    Schedule existingEmployeeScheduleToUpdate =
        scheduleRepository
            .findById(scheduleId)
            .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found."));

    updatedExistingEmployeeSchedule(updatedSchedule, existingEmployeeScheduleToUpdate);

    scheduleValidator.validateSchedule(updatedSchedule);

    return scheduleRepository.save(updatedSchedule);
  }

  @Override
  public Schedule addShiftToSchedule(Long employeeId, ShiftRequest approvedShiftRequest) {
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
