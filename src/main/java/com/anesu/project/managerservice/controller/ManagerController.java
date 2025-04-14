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
@RequestMapping("/api/manager")
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
  @PostMapping(ManagerServiceRestEndpoints.CREATE_SCHEDULE)
  public Schedule createSchedule(@RequestBody Schedule schedule) {
    return scheduleService.createSchedule(schedule);
  }

  @PutMapping(ManagerServiceRestEndpoints.UPDATE_SCHEDULE)
  public Schedule updateEmployeeSchedule(
      @PathVariable Long scheduleId, @RequestBody Schedule updatedSchedule) {
    return scheduleService.updateEmployeeSchedule(scheduleId, updatedSchedule);
  }

  @GetMapping(ManagerServiceRestEndpoints.GET_SCHEDULE_BY_ID)
  public Optional<Schedule> getScheduleById(@PathVariable Long scheduleId) {
    return scheduleService.getScheduleById(scheduleId);
  }

  @GetMapping(ManagerServiceRestEndpoints.GET_SCHEDULES_IN_RANGE)
  public Optional<List<Schedule>> getAllEmployeeSchedulesWithinGivenDateRange(
      @PathVariable Long scheduleId,
      @RequestParam LocalDateTime startDate,
      @RequestParam LocalDateTime endDate) {
    return scheduleService.getAllEmployeeSchedulesWithinGivenDateRange(
        scheduleId, startDate, endDate);
  }

  @DeleteMapping(ManagerServiceRestEndpoints.DELETE_SCHEDULE)
  public void deleteSchedule(@PathVariable Long employeeId) {
    scheduleService.deleteSchedule(employeeId);
  }

  // Shift Request Endpoints
  @PostMapping(ManagerServiceRestEndpoints.CREATE_SHIFT_REQUEST)
  public ShiftRequest sendShiftRequestToEmployee(
      @PathVariable Long employeeId, @RequestBody ShiftRequest shiftRequest) {
    return shiftRequestService.sendShiftRequestToEmployee(employeeId, shiftRequest);
  }

  @PutMapping(ManagerServiceRestEndpoints.APPROVE_SHIFT_REQUEST)
  public ShiftRequest approveShiftRequest(
      @PathVariable Long employeeId,
      @PathVariable Long shiftRequestId,
      @RequestBody ShiftRequest approvedShiftRequest) {
    return shiftRequestService.approveShiftRequest(employeeId, shiftRequestId);
  }

  @PutMapping(ManagerServiceRestEndpoints.DECLINE_SHIFT_REQUEST)
  public ShiftRequest declineShiftRequest(
      @PathVariable Long shiftRequestId, @RequestBody String rejectionReason) {
    return shiftRequestService.declineShiftRequest(shiftRequestId, rejectionReason);
  }

  @GetMapping(ManagerServiceRestEndpoints.GET_SHIFT_REQUEST_BY_EMPLOYEE_ID)
  public Optional<ShiftRequest> getShiftRequestByEmployeeId(@PathVariable Long employeeId) {
    return shiftRequestService.getShiftRequestByEmployeeId(employeeId);
  }

  @GetMapping(ManagerServiceRestEndpoints.GET_SHIFT_REQUESTS_IN_RANGE)
  public List<ShiftRequest> getShiftRequestByDateRange(
      @RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
    return shiftRequestService.getShiftRequestByDateRange(startDate, endDate);
  }

  // Vacation Request Endpoints
  @PutMapping(ManagerServiceRestEndpoints.APPROVE_VACATION_REQUEST)
  public VacationRequest approveVacationRequest(
      @PathVariable Long vacationRequestId, @RequestParam VacationRequestStatus status) {
    return vacationRequestService.approveVacationRequest(vacationRequestId, status);
  }

  @PutMapping(ManagerServiceRestEndpoints.DECLINE_VACATION_REQUEST)
  public VacationRequest declineVacationRequest(
      @PathVariable Long vacationRequestId, @RequestParam String rejectionReason) {
    return vacationRequestService.declineVacationRequest(vacationRequestId, rejectionReason);
  }

  @GetMapping(ManagerServiceRestEndpoints.GET_VACATIONS_BY_EMPLOYEE_ID)
  public List<VacationRequest> getVacationRequestsByEmployeeId(@PathVariable Long employeeId) {
    return vacationRequestService.getVacationRequestsByEmployeeId(employeeId);
  }

  @GetMapping(ManagerServiceRestEndpoints.GET_EMPLOYEE_VACATIONS_IN_RANGE)
  public List<VacationRequest> getVacationByIdAndDateRange(
      @PathVariable Long employeeId,
      @RequestParam LocalDateTime startDate,
      @RequestParam LocalDateTime endDate) {
    return vacationRequestService.getVacationByIdAndDateRange(employeeId, startDate, endDate);
  }

  @GetMapping(ManagerServiceRestEndpoints.GET_TEAM_CALENDAR)
  public List<VacationRequest> getTeamCalendar(
      @PathVariable Long officeLocationId,
      @RequestParam LocalDateTime startDate,
      @RequestParam LocalDateTime endDate) {
    return vacationRequestService.getTeamCalendar(officeLocationId, startDate, endDate);
  }
}
