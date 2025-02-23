package com.anesu.project.managerservice.model;

import com.anesu.project.managerservice.entity.shift.ShiftRequest;
import com.anesu.project.managerservice.entity.shift.ShiftRequestStatus;
import com.anesu.project.managerservice.service.exception.ShiftRequestNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

public interface ShiftRequestService {

  // TODO: To be implemented later:
  // ShiftRequest createAndSendShiftRequestToEmployee(ShiftRequest shiftRequest);

  /**
   * Approves a shift request submitted by an employee.
   *
   * @param employeeId the ID of the employee who submitted the shift request
   * @param shiftRequestId the ID of the shift request to approve
   * @return the approved {@link ShiftRequest} with updated status
   */
  ShiftRequest approveShiftRequest(Long employeeId, Long shiftRequestId)
      throws ShiftRequestNotFoundException;

  /**
   * Declines a shift request submitted by an employee, providing a reason.
   *
   * @param employeeId the ID of the employee who submitted the shift request
   * @param rejectionReason the reason for rejecting the shift request
   * @return the declined {@link ShiftRequest} with updated status and reason
   */
  ShiftRequest declineShiftRequest(Long employeeId, String rejectionReason)
      throws ShiftRequestNotFoundException;

  /**
   * Retrieves a shift request for a specific employee.
   *
   * @param employeeId the ID of the employee whose shift request is being retrieved
   * @return the corresponding {@link ShiftRequest}, or {@code null} if not found
   */
  ShiftRequest getShiftRequestByEmployeeId(Long employeeId);

  /**
   * Retrieves a list of shift requests for an employee based on their status.
   *
   * <p>//* @param employeeId the ID of the employee whose shift requests are being retrieved
   *
   * @param status the {@link ShiftRequestStatus} to filter requests
   * @return a list of {@link ShiftRequest} matching the given status, or an empty list if none
   *     found
   */
  ShiftRequest getShiftRequestByIdAndStatus(Long shiftRequestId, ShiftRequestStatus status)
      throws ShiftRequestNotFoundException;

  /**
   * Retrieves shift requests within a specific date range.
   *
   * @param startDate the start of the date range
   * @param endDate the end of the date range
   * @return a list of {@link ShiftRequest} within the specified date range, or an empty list if
   *     none found
   */
  List<ShiftRequest> getShiftRequestByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
