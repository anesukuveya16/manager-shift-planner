package com.anesu.project.managerservice.service.util;

import com.anesu.project.managerservice.entity.vacation.VacationRequest;
import com.anesu.project.managerservice.model.repository.VacationRequestRepository;
import org.springframework.stereotype.Component;

@Component
public class VacationRequestValidator {

  private static final int MAX_VACATION_DAYS_EACH_YEAR = 30;
  private static final String OVERLAPPING_VACATION_REQUEST_ERROR =
      "Request could not be fulfilled because there is already an approved vacation request for this period for employee: ";
  private static final String INVALID_VACATION_STATUS_ERROR =
      "Only pending requests can be withdrawn.";
  private static final String INVALID_VACATION_OWNER_ERROR =
      "You can only withdraw your own requests.";

  public void validateVacationRequest(
      VacationRequest vacationRequest, VacationRequestRepository repository) {

    VacationRequest isOverlappingWithExistingRequests;
  }



}
