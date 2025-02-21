package com.anesu.project.managerservice.service;

import com.anesu.project.managerservice.entity.ScheduleStatus;
import com.anesu.project.managerservice.entity.schedule.Schedule;
import com.anesu.project.managerservice.entity.shift.ShiftRequest;
import com.anesu.project.managerservice.model.ScheduleService;
import com.anesu.project.managerservice.model.repository.ScheduleRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ScheduleServiceImpl implements ScheduleService {

  private final ScheduleRepository scheduleRepository;

  public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
    this.scheduleRepository = scheduleRepository;
  }

  @Override
  public Schedule approveOrRejectSchedule(Long scheduleId, ScheduleStatus status) {
    return null;
  }

  @Override
  public Schedule updateEmployeeSchedule(Long employeeId, Schedule updatedSchedule) {
    return null;
  }

  @Override
  public Schedule addShiftToSchedule(Long employeeId, ShiftRequest approvedShiftRequest) {
    return null;
  }

  @Override
  public Optional<Schedule> getScheduleById(Long scheduleId) {
    return Optional.empty();
  }

  @Override
  public Optional<List<Schedule>> getAllSchedulesInDateRange(
      LocalDateTime startDate, LocalDateTime endDate) {
    return Optional.empty();
  }

  @Override
  public void deleteSchedule(Long scheduleId) {}
}
