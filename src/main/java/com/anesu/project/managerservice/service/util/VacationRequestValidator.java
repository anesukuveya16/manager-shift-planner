package com.anesu.project.managerservice.service.util;

import com.anesu.project.managerservice.entity.vacation.VacationRequest;
import com.anesu.project.managerservice.model.repository.VacationRequestRepository;
import com.anesu.project.managerservice.service.exception.InvalidVacationRequestException;
import org.springframework.stereotype.Component;

@Component
public class VacationRequestValidator {

  private static final int MAX_VACATION_DAYS_EACH_YEAR = 30;
  private static final String OVERLAPPING_VACATION_REQUEST_ERROR =
      "Request could not be fulfilled because there is already an approved vacation request for this period for employee: ";
  private static final String INVALID_VACATION_STATUS_ERROR =
      "Only pending requests can be withdrawn.";

  public void validateVacationRequest(
      VacationRequest vacationRequest, VacationRequestRepository repository) {
    validateAnyOverlappingVacationRequests(vacationRequest, repository);
    validateTheRemainingVacationDays(vacationRequest, repository);
  }

  private void validateAnyOverlappingVacationRequests(
      VacationRequest vacationRequest, VacationRequestRepository repository) {
    if (isOverlappingWithExistingRequest(vacationRequest, repository)) {
      throw new InvalidVacationRequestException(
          INVALID_VACATION_STATUS_ERROR + vacationRequest.getEmployeeId());
    }
  }

  private boolean isOverlappingWithExistingRequest(
      VacationRequest vacationRequest, VacationRequestRepository repository) {
    Long employeeId = vacationRequest.getEmployeeId();
    return repository.findByEmployeeIdAndDateRange(employeeId, vacationRequest.getStartDate(), vacationRequest.getEndDate())
            .stream()

  }

  private void validateTheRemainingVacationDays(
      VacationRequest vacationRequest, VacationRequestRepository repository) {}
}
