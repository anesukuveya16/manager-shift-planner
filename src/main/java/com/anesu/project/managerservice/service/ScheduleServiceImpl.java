package com.anesu.project.managerservice.service;

import com.anesu.project.managerservice.entity.schedule.Schedule;
import com.anesu.project.managerservice.entity.shift.ShiftEntry;
import com.anesu.project.managerservice.entity.shift.ShiftRequest;
import com.anesu.project.managerservice.entity.shift.ShiftRequestStatus;
import com.anesu.project.managerservice.entity.vacation.VacationEntry;
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

    validateApprovedShiftRequest(approvedShiftRequest);

    Optional<Schedule> scheduleInApprovedShiftCalenderWeek =
        getScheduleForApprovedShiftCalendarWeek(employeeId, approvedShiftRequest);

    if (scheduleInApprovedShiftCalenderWeek.isPresent()) {

      return addNewShiftEntryToExistingSchedule(
          approvedShiftRequest, scheduleInApprovedShiftCalenderWeek);

    } else {
      Schedule schedule =
          createNewScheduleForApprovedVacationCalendarWeek(employeeId, approvedShiftRequest);

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

    validateApprovedVacationRequest(approvedVacationRequest);

    Optional<Schedule> scheduleInApprovedVacationCalenderWeek =
        getScheduleForApprovedVacationCalendarWeek(employeeId, approvedVacationRequest);

    if (scheduleInApprovedVacationCalenderWeek.isPresent()) {

      return addNewVacationEntryToExistingSchedule(
          approvedVacationRequest, scheduleInApprovedVacationCalenderWeek);

    } else {
      Schedule schedule =
          createNewScheduleForApprovedVacationCalendarWeek(employeeId, approvedVacationRequest);

      return scheduleRepository.save(schedule);
    }
  }

  @Override
  public void deleteSchedule(Long employeeId) {
    if (!scheduleRepository.existsById(employeeId)) {
      throw new ScheduleNotFoundException(SCHEDULE_NOT_FOUND_EXCEPTION + employeeId);
    }
    scheduleRepository.deleteById(employeeId);
  }

  private void updatedExistingEmployeeSchedule(
      Schedule updatedSchedule, Schedule existingSchedule) {
    existingSchedule.setStartDate(updatedSchedule.getStartDate());
    existingSchedule.setEndDate(updatedSchedule.getEndDate());
    existingSchedule.setShifts(updatedSchedule.getShifts());
    existingSchedule.setVacations(updatedSchedule.getVacations());
    existingSchedule.setTotalWorkingHours(updatedSchedule.getTotalWorkingHours());
  }

  private LocalDateTime determineShiftEndDate(ShiftRequest approvedShiftRequest) {
    return approvedShiftRequest
        .getShiftDate()
        .plusHours(approvedShiftRequest.getShiftLengthInHours());
  }

  private void validateApprovedVacationRequest(VacationRequest approvedVacationRequest) {
    if (!VacationRequestStatus.APPROVED.equals(approvedVacationRequest.getStatus())) {
      throw new InvalidScheduleException(
          "Invalid schedule operation. Only approved vacation requests can be added to the schedule.");
    }
  }

  private Schedule addNewVacationEntryToExistingSchedule(
      VacationRequest approvedVacationRequest,
      Optional<Schedule> scheduleInApprovedVacationCalenderWeek) {

    if (scheduleInApprovedVacationCalenderWeek.isPresent()) {
      Schedule schedule = scheduleInApprovedVacationCalenderWeek.get();
      schedule
          .getVacations()
          .add(VacationEntry.fromApprovedVacationRequest(approvedVacationRequest));
      return scheduleRepository.save(schedule);
    } else {
      throw new IllegalArgumentException(
          "No schedule found for the approved vacation calendar week");
    }
  }

  private Schedule createNewScheduleForApprovedVacationCalendarWeek(
      Long employeeId, VacationRequest approvedVacationRequest) {

    List<VacationEntry> vacationEntries = new ArrayList<>();

    VacationEntry vacationEntry =
        VacationEntry.fromApprovedVacationRequest(approvedVacationRequest);
    vacationEntries.add(vacationEntry);

    return Schedule.builder()
        .employeeId(employeeId)
        .startDate(approvedVacationRequest.getStartDate())
        .endDate(approvedVacationRequest.getEndDate())
        .vacations(vacationEntries)
        .build();
  }

  private Optional<Schedule> getScheduleForApprovedVacationCalendarWeek(
      Long employeeId, VacationRequest approvedVacationRequest) {

    LocalDateTime startOfVacationCalendarWeek =
        approvedVacationRequest.getStartDate().with(DayOfWeek.MONDAY);
    LocalDateTime endOfVacationCalendarWeek =
        approvedVacationRequest.getEndDate().with(DayOfWeek.SUNDAY);

    return scheduleRepository.findByEmployeeIdAndCalendarWeek(
        employeeId, startOfVacationCalendarWeek, endOfVacationCalendarWeek);
  }

  private static void validateApprovedShiftRequest(ShiftRequest approvedShiftRequest) {
    if (!ShiftRequestStatus.APPROVED.equals(approvedShiftRequest.getStatus())) {
      throw new InvalidScheduleException(
          "Invalid schedule operation. Only approved shifts can be added to the schedule.");
    }
  }

  private Schedule addNewShiftEntryToExistingSchedule(
      ShiftRequest approvedShiftRequest, Optional<Schedule> scheduleInApprovedShiftCalenderWeek) {

    if (scheduleInApprovedShiftCalenderWeek.isPresent()) {
      Schedule schedule = scheduleInApprovedShiftCalenderWeek.get();
      schedule.getShifts().add(ShiftEntry.fromApprovedShiftEntry(approvedShiftRequest));
      return scheduleRepository.save(schedule);
    } else {
      throw new IllegalArgumentException("No schedule found for the approved shift calendar week");
    }
  }

  private Optional<Schedule> getScheduleForApprovedShiftCalendarWeek(
      Long employeeId, ShiftRequest approvedShiftRequest) {
    LocalDateTime startOfShiftCalendarWeek =
        approvedShiftRequest.getShiftDate().with(DayOfWeek.MONDAY);
    LocalDateTime endOfShiftCalendarWeek =
        approvedShiftRequest.getShiftDate().with(DayOfWeek.SUNDAY);

    return scheduleRepository.findByEmployeeIdAndCalendarWeek(
        employeeId, startOfShiftCalendarWeek, endOfShiftCalendarWeek);
  }

  private Schedule createNewScheduleForApprovedVacationCalendarWeek(
      Long employeeId, ShiftRequest approvedShiftRequest) {
    List<ShiftEntry> shiftEntries = new ArrayList<>();

    ShiftEntry shiftEntry = ShiftEntry.fromApprovedShiftEntry(approvedShiftRequest);
    shiftEntries.add(shiftEntry);

    return Schedule.builder()
        .employeeId(employeeId)
        .startDate(approvedShiftRequest.getShiftDate())
        .endDate(determineShiftEndDate(approvedShiftRequest))
        .totalWorkingHours(approvedShiftRequest.getShiftLengthInHours())
        .shifts(shiftEntries)
        .build();
  }
}
