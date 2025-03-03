package com.anesu.project.managerservice.service.util;

import com.anesu.project.managerservice.entity.vacation.VacationRequest;
import com.anesu.project.managerservice.entity.vacation.VacationRequestStatus;
import com.anesu.project.managerservice.model.repository.VacationRequestRepository;
import com.anesu.project.managerservice.service.exception.InvalidVacationRequestException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
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

    List<VacationRequest> existingRequests =
        repository.findByEmployeeIdAndDateRange(
            employeeId, vacationRequest.getStartDate(), vacationRequest.getEndDate());

    for (VacationRequest existingRequest : existingRequests) {
      if (existingRequest.getStatus().equals(VacationRequestStatus.APPROVED)
          || existingRequest.getStatus().equals(VacationRequestStatus.PENDING)) {
        return true;
      }
    }
    return false;
  }

  private void validateTheRemainingVacationDays(
      VacationRequest vacationRequest, VacationRequestRepository repository) {

    // TODO: get this list of all the vacation days

    long existingUsedVacationDays = calculatedTotalOfUsedVacationDays();
    int newVacationRequestDays = calculateNewRequestedVacationRequest(vacationRequest);
    long totalVacationDays = existingUsedVacationDays + newVacationRequestDays;

    if (totalVacationDays > MAX_VACATION_DAYS_EACH_YEAR) {
      throw new InvalidVacationRequestException(
          "Vacation request exceeds yearly limit. Employee ID: "
              + vacationRequest.getEmployeeId()
              + " already has "
              + existingUsedVacationDays
              + " days. New request adds "
              + newVacationRequestDays
              + " days, exceeding the maximum of "
              + MAX_VACATION_DAYS_EACH_YEAR
              + " days.");
    }
  }

  private long calculateDaysInRange(LocalDateTime startDate, LocalDateTime endDate) {
    LocalDate firstDayOfYear = LocalDate.now().withDayOfYear(1);
    LocalDate lastDayOfYear = LocalDate.now().withMonth(12).withDayOfMonth(31);

    LocalDate adjustedStartDate =
        startDate.toLocalDate().isBefore(firstDayOfYear) ? firstDayOfYear : startDate.toLocalDate();

    LocalDate adjustedEndDate =
        endDate.toLocalDate().isAfter(lastDayOfYear) ? lastDayOfYear : endDate.toLocalDate();

    return ChronoUnit.DAYS.between(adjustedStartDate, adjustedEndDate) + 1;
  }

  private long calculatedTotalOfUsedVacationDays(List<VacationRequest> vacationRequests) {
    return vacationRequests.stream()
        .mapToLong(vacation -> calculateDaysInRange(vacation.getStartDate(), vacation.getEndDate()))
        .sum();
  }

  private int calculateNewRequestedVacationRequest(VacationRequest vacationRequest) {
    return (int)
            ChronoUnit.DAYS.between(vacationRequest.getStartDate(), vacationRequest.getEndDate())
        + 1;
  }
}
