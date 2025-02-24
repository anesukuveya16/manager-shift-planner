package com.anesu.project.managerservice.service;

import com.anesu.project.managerservice.entity.vacation.VacationRequest;
import com.anesu.project.managerservice.entity.vacation.VacationRequestStatus;
import com.anesu.project.managerservice.model.ScheduleService;
import com.anesu.project.managerservice.model.VacationRequestService;
import com.anesu.project.managerservice.model.repository.VacationRequestRepository;
import com.anesu.project.managerservice.service.util.VacationRequestNotFoundException;
import com.anesu.project.managerservice.service.util.VacationRequestValidator;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class VacationRequestServiceImpl implements VacationRequestService {

  private final VacationRequestRepository vacationRequestRepository;
  private final VacationRequestValidator vacationRequestValidator;
  private final ScheduleService scheduleService;

  public VacationRequestServiceImpl(
      VacationRequestRepository vacationRequestRepository,
      VacationRequestValidator vacationRequestValidator,
      ScheduleService scheduleService) {
    this.vacationRequestRepository = vacationRequestRepository;
    this.vacationRequestValidator = vacationRequestValidator;
    this.scheduleService = scheduleService;
  }

  @Override
  public VacationRequest approveVacationRequest(
      Long vacationRequestId, VacationRequestStatus status) {
    return null;
  }

  @Override
  public VacationRequest declineVacationRequest(Long vacationRequestId, String rejectionReason) {
    return null;
  }

  @Override
  public List<VacationRequest> getVacationRequestsByEmployeeId(Long employeeId) {
    return vacationRequestRepository.findByEmployeeId(employeeId);
  }

  @Override
  public List<VacationRequest> getVacationByIdAndDateRange(
      Long employeeId, LocalDateTime startDate, LocalDateTime endDate) {
    return vacationRequestRepository.findByEmployeeIdAndDateRange(employeeId, startDate, endDate);
  }

  @Override
  public List<VacationRequest> getTeamCalendar(
      Long officeLocationId, LocalDateTime startDate, LocalDateTime endDate) {
    return null;
  }

  @Override
  public VacationRequest getVacationRequestByIdAndStatus(
      Long vacationRequestId, VacationRequestStatus status)
      throws VacationRequestNotFoundException {
    return vacationRequestRepository
        .findByIdAndStatus(vacationRequestId, status)
        .orElseThrow(
            () ->
                new VacationRequestNotFoundException(
                    "Vacation request not found with ID: " + vacationRequestId));
  }
}
