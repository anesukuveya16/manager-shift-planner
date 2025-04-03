package com.anesu.project.managerservice.controller;

import com.anesu.project.managerservice.entity.schedule.Schedule;
import com.anesu.project.managerservice.entity.shift.ShiftRequest;
import com.anesu.project.managerservice.entity.vacation.VacationRequest;
import com.anesu.project.managerservice.entity.vacation.VacationRequestStatus;
import com.anesu.project.managerservice.service.ScheduleServiceImpl;
import com.anesu.project.managerservice.service.ShiftRequestServiceImpl;
import com.anesu.project.managerservice.service.VacationRequestServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manager")
public class ManagerController {

  private final ScheduleServiceImpl scheduleService;
  private final ShiftRequestServiceImpl shiftRequestService;
  private final VacationRequestServiceImpl vacationRequestService;

  public ManagerController(
      ScheduleServiceImpl scheduleService,
      ShiftRequestServiceImpl shiftRequestService,
      VacationRequestServiceImpl vacationRequestService) {
    this.scheduleService = scheduleService;
    this.shiftRequestService = shiftRequestService;
    this.vacationRequestService = vacationRequestService;
  }

  // Schedule Endpoints
  @PutMapping("/schedules/{scheduleId}")
  public Schedule updateEmployeeSchedule(
      @PathVariable Long scheduleId, @RequestBody Schedule updatedSchedule) {
    return scheduleService.updateEmployeeSchedule(scheduleId, updatedSchedule);
  }

  @GetMapping("/schedules/{scheduleId}")
  public Optional<Schedule> getScheduleById(@PathVariable Long scheduleId) {
    return scheduleService.getScheduleById(scheduleId);
  }

  @GetMapping("/schedules/{scheduleId}/range")
  public Optional<List<Schedule>> getAllEmployeeSchedulesWithinGivenDateRange(
      @PathVariable Long scheduleId,
      @RequestParam LocalDateTime startDate,
      @RequestParam LocalDateTime endDate) {
    return scheduleService.getAllEmployeeSchedulesWithinGivenDateRange(
        scheduleId, startDate, endDate);
  }

  @DeleteMapping("/schedules/{employeeId}")
  public void deleteSchedule(@PathVariable Long employeeId) {
    scheduleService.deleteSchedule(employeeId);
  }

  // Shift Request Endpoints
  @PostMapping("/employees/{employeeId}/shifts")
  public ShiftRequest sendShiftRequestToEmployee(
      @PathVariable Long employeeId, @RequestBody ShiftRequest shiftRequest) {
    return shiftRequestService.sendShiftRequestToEmployee(employeeId, shiftRequest);
  }

  @PutMapping("/employees/{employeeId}/shifts/{shiftRequestId}/approve")
  public ShiftRequest approveShiftRequest(
      @PathVariable Long employeeId, @PathVariable Long shiftRequestId) {
    return shiftRequestService.approveShiftRequest(employeeId, shiftRequestId);
  }

  @PutMapping("/shifts/{shiftRequestId}/decline")
  public ShiftRequest declineShiftRequest(
      @PathVariable Long shiftRequestId, @RequestParam String rejectionReason) {
    return shiftRequestService.declineShiftRequest(shiftRequestId, rejectionReason);
  }

  @GetMapping("/employees/{employeeId}/shifts")
  public Optional<ShiftRequest> getShiftRequestByEmployeeId(@PathVariable Long employeeId) {
    return shiftRequestService.getShiftRequestByEmployeeId(employeeId);
  }

  @GetMapping("/shifts/range")
  public List<ShiftRequest> getShiftRequestByDateRange(
      @RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
    return shiftRequestService.getShiftRequestByDateRange(startDate, endDate);
  }

  // Vacation Request Endpoints
  @PutMapping("/vacations/{vacationRequestId}/approve")
  public VacationRequest approveVacationRequest(
      @PathVariable Long vacationRequestId, @RequestParam VacationRequestStatus status) {
    return vacationRequestService.approveVacationRequest(vacationRequestId, status);
  }

  @PutMapping("/vacations/{vacationRequestId}/decline")
  public VacationRequest declineVacationRequest(
      @PathVariable Long vacationRequestId, @RequestParam String rejectionReason) {
    return vacationRequestService.declineVacationRequest(vacationRequestId, rejectionReason);
  }

  @GetMapping("/employees/{employeeId}/vacations")
  public List<VacationRequest> getVacationRequestsByEmployeeId(@PathVariable Long employeeId) {
    return vacationRequestService.getVacationRequestsByEmployeeId(employeeId);
  }

  @GetMapping("/employees/{employeeId}/vacations/range")
  public List<VacationRequest> getVacationByIdAndDateRange(
      @PathVariable Long employeeId,
      @RequestParam LocalDateTime startDate,
      @RequestParam LocalDateTime endDate) {
    return vacationRequestService.getVacationByIdAndDateRange(employeeId, startDate, endDate);
  }

  @GetMapping("/offices/{officeLocationId}/vacations")
  public List<VacationRequest> getTeamCalendar(
      @PathVariable Long officeLocationId,
      @RequestParam LocalDateTime startDate,
      @RequestParam LocalDateTime endDate) {
    return vacationRequestService.getTeamCalendar(officeLocationId, startDate, endDate);
  }
}
