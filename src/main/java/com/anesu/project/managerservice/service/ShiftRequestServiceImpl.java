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
  public ShiftRequest approveShiftRequest(Long employeeId, Long shiftRequestId)
      throws ShiftRequestNotFoundException {

    ShiftRequest shiftRequest =
        getShiftRequestByIdAndStatus(shiftRequestId, ShiftRequestStatus.PENDING);

    shiftRequestValidator.validateShiftRequest(shiftRequest, shiftRequestRepository);

    shiftRequest.setStatus(ShiftRequestStatus.APPROVED);
    ShiftRequest approvedShiftRequest = shiftRequestRepository.save(shiftRequest);

    scheduleService.addShiftToSchedule(employeeId, approvedShiftRequest);

    return approvedShiftRequest;
  }

  @Override
  public ShiftRequest declineShiftRequest(Long shiftRequestId, String rejectionReason)
      throws ShiftRequestNotFoundException {

    ShiftRequest shiftRequest =
        getShiftRequestByIdAndStatus(shiftRequestId, ShiftRequestStatus.REJECTED);

    shiftRequestValidator.validateShiftRequest(shiftRequest, shiftRequestRepository);

    shiftRequest.setStatus(ShiftRequestStatus.REJECTED);
    shiftRequest.setRejectionReason(shiftRequest.getRejectionReason());

    return shiftRequestRepository.save(shiftRequest);
  }

  @Override
  public ShiftRequest getShiftRequestByEmployeeId(Long employeeId) {
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
                    "Could not find pending shift request with ID: " + shiftRequestId));
  }

  @Override
  public List<ShiftRequest> getShiftRequestByDateRange(
      LocalDateTime startDate, LocalDateTime endDate) {
    return shiftRequestRepository.findByDateRange(startDate, endDate);
  }
}
