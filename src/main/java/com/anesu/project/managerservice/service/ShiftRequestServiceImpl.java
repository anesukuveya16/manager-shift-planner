package com.anesu.project.managerservice.service;

import com.anesu.project.managerservice.entity.shift.ShiftRequest;
import com.anesu.project.managerservice.entity.shift.ShiftRequestStatus;
import com.anesu.project.managerservice.model.ScheduleService;
import com.anesu.project.managerservice.model.ShiftRequestService;
import com.anesu.project.managerservice.model.repository.ShiftRequestRepository;
import com.anesu.project.managerservice.service.exception.ShiftRequestNotFoundException;
import com.anesu.project.managerservice.service.util.ShiftRequestValidator;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ShiftRequestServiceImpl implements ShiftRequestService {

  private final ShiftRequestRepository shiftRequestRepository;
  private final ShiftRequestValidator shiftRequestValidator;
  private final ScheduleService scheduleService;

  public ShiftRequestServiceImpl(
      ShiftRequestRepository shiftRequestRepository,
      ShiftRequestValidator shiftRequestValidator,
      ScheduleService scheduleService) {
    this.shiftRequestRepository = shiftRequestRepository;
    this.shiftRequestValidator = shiftRequestValidator;
    this.scheduleService = scheduleService;
  }

  @Override
  public ShiftRequest sendShiftRequestToEmployee(Long employeeId, ShiftRequest shiftRequest) {

    shiftRequestValidator.validateShiftRequest(shiftRequest, shiftRequestRepository);

    shiftRequest.setEmployeeId(employeeId);
    shiftRequest.setStatus(ShiftRequestStatus.PENDING);

    return shiftRequestRepository.save(shiftRequest);
  }

  @Override
  public ShiftRequest approveShiftRequest(Long employeeId, Long shiftRequestId) {

    ShiftRequest shiftRequest =
        getShiftRequestByIdAndStatus(shiftRequestId, ShiftRequestStatus.PENDING);

    shiftRequestValidator.validateShiftRequest(shiftRequest, shiftRequestRepository);

    shiftRequest.setStatus(ShiftRequestStatus.APPROVED);
    ShiftRequest approvedShiftRequest = shiftRequestRepository.save(shiftRequest);

    scheduleService.addShiftToSchedule(employeeId, approvedShiftRequest);

    return approvedShiftRequest;
  }

  @Override
  public ShiftRequest declineShiftRequest(Long shiftRequestId, String rejectionReason) {

    ShiftRequest shiftRequest =
        getShiftRequestByIdAndStatus(shiftRequestId, ShiftRequestStatus.PENDING);

    shiftRequest.setStatus(ShiftRequestStatus.REJECTED);
    shiftRequest.setRejectionReason(rejectionReason);

    return shiftRequestRepository.save(shiftRequest);
  }

  @Override
  public List<ShiftRequest> getShiftRequestByEmployeeId(Long employeeId) {
    return shiftRequestRepository.findByEmployeeId(employeeId);
  }

  @Override
  public ShiftRequest getShiftRequestByIdAndStatus(Long shiftRequestId, ShiftRequestStatus status)
      throws ShiftRequestNotFoundException {
    return shiftRequestRepository
        .findByIdAndStatus(shiftRequestId, status)
        .orElseThrow(
            () ->
                new ShiftRequestNotFoundException(
                    "Could not find shift with status []"
                        + status
                        + " and ID [] "
                        + shiftRequestId
                        + "to approve."));
  }

  @Override
  public List<ShiftRequest> getShiftRequestByDateRange(
      LocalDateTime startDate, LocalDateTime endDate) {
    return shiftRequestRepository.findByShiftDateBetween(startDate, endDate);
  }
}
