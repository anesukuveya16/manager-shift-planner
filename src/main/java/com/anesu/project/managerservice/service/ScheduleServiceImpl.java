package com.anesu.project.managerservice.service;

import com.anesu.project.managerservice.entity.schedule.Schedule;
import com.anesu.project.managerservice.entity.shift.ShiftEntry;
import com.anesu.project.managerservice.entity.shift.ShiftRequest;
import com.anesu.project.managerservice.entity.shift.ShiftRequestStatus;
import com.anesu.project.managerservice.entity.vacation.VacationRequest;
import com.anesu.project.managerservice.entity.vacation.VacationRequestStatus;
import com.anesu.project.managerservice.model.ScheduleService;
import com.anesu.project.managerservice.model.repository.ScheduleRepository;
import com.anesu.project.managerservice.service.exception.InvalidScheduleException;
import com.anesu.project.managerservice.service.exception.ScheduleNotFoundException;
import com.anesu.project.managerservice.service.util.ScheduleValidator;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    if (!ShiftRequestStatus.APPROVED.equals(approvedShiftRequest.getStatus())) {
      throw new InvalidScheduleException(
          "Invalid schedule operation. Only approved shifts can be added to the schedule.");
    }
    // checking the weekly range of the schedule

    LocalDateTime startOfShiftCalendarWeek =
        approvedShiftRequest.getShiftDate().with(DayOfWeek.MONDAY);
    LocalDateTime endOfShiftCalendarWeek =
        approvedShiftRequest.getShiftDate().with(DayOfWeek.SUNDAY);

    // Find existing schedule for that employee in the given week
    Optional<Schedule> scheduleInApprovedShiftCalenderWeek =
        scheduleRepository.findByEmployeeIdAndCalendarWeek(
            employeeId, startOfShiftCalendarWeek, endOfShiftCalendarWeek);

    ShiftEntry shiftEntry = ShiftEntry.from(approvedShiftRequest);

    if (scheduleInApprovedShiftCalenderWeek.isPresent()) {

      Schedule schedule = scheduleInApprovedShiftCalenderWeek.get();
      schedule.getShifts().add(shiftEntry);
      return scheduleRepository.save(schedule);

    } else {
      List<ShiftEntry> shiftEntries = new ArrayList<>();
      shiftEntries.add(shiftEntry);

      Schedule schedule =
          Schedule.builder()
              .employeeId(employeeId)
              .startDate(approvedShiftRequest.getShiftDate())
              .endDate(determineShiftEndDate(approvedShiftRequest))
              .totalWorkingHours(approvedShiftRequest.getShiftLengthInHours())
              .shifts(shiftEntries)
              .build();

      return scheduleRepository.save(schedule);
    }
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
  public Schedule addApprovedVacationRequestToSchedule(
      Long employeeId, VacationRequest approvedVacationRequest) {
    if (!VacationRequestStatus.APPROVED.equals(approvedVacationRequest.getStatus())) {
      throw new InvalidScheduleException(
              "Invalid schedule operation. Only approved vacation requests can be added to the schedule.");
    }
    // checking the weekly range of the schedule

    LocalDateTime startOfVacationCalendarWeek =
            approvedVacationRequest.getStartDate().with(DayOfWeek.MONDAY);
    LocalDateTime endOfVacationCalendarWeek =
            approvedVacationRequest.getEndDate().with(DayOfWeek.SUNDAY);

    // Find existing schedule for that employee in the given week
    Optional<Schedule> scheduleInApprovedVacationCalenderWeek =
            scheduleRepository.findByEmployeeIdAndCalendarWeek(
                    employeeId, startOfVacationCalendarWeek, endOfVacationCalendarWeek);

    ShiftEntry shiftEntry = ShiftEntry.from(approvedVacationRequest);

    if (scheduleInApprovedVacationCalenderWeek.isPresent()) {

      Schedule schedule = scheduleInApprovedVacationCalenderWeek.get();
      schedule.getShifts().add(shiftEntry);
      return scheduleRepository.save(schedule);

    } else {
      List<ShiftEntry> shiftEntries = new ArrayList<>();
      shiftEntries.add(shiftEntry);

      List<VacationRequest> vacationRequests = new ArrayList<>();
      vacationRequests.add(approvedVacationRequest);

      Schedule schedule =
              Schedule.builder()
                      .employeeId(employeeId)
                      .startDate(approvedVacationRequest.getStartDate())
                      .endDate(approvedVacationRequest.getEndDate())
                      .approvedByManager(approvedVacationRequest.getApprovedByManager())
                      .build();

      return scheduleRepository.save(schedule);
    }

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

  private LocalDateTime determineShiftEndDate(ShiftRequest approvedShiftRequest) {
    return approvedShiftRequest
        .getShiftDate()
        .plusHours(approvedShiftRequest.getShiftLengthInHours());
  }
}
