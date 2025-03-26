package com.anesu.project.managerservice.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.anesu.project.managerservice.entity.vacation.VacationRequest;
import com.anesu.project.managerservice.entity.vacation.VacationRequestStatus;
import com.anesu.project.managerservice.model.ScheduleService;
import com.anesu.project.managerservice.model.repository.VacationRequestRepository;
import com.anesu.project.managerservice.service.exception.InvalidVacationRequestException;
import com.anesu.project.managerservice.service.exception.VacationRequestNotFoundException;
import com.anesu.project.managerservice.service.util.VacationRequestValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VacationRequestServiceImplTest {

  @Mock private VacationRequestRepository vacationRequestRepositoryMock;
  @Mock private VacationRequestValidator vacationRequestValidatorMock;
  @Mock private ScheduleService scheduleServiceMock;

  private VacationRequestServiceImpl cut;

  @BeforeEach
  void setUp() {
    cut =
        new VacationRequestServiceImpl(
            vacationRequestRepositoryMock, vacationRequestValidatorMock, scheduleServiceMock);
  }

  @Test
  void approveVacationRequest_AndChangeStatusToApproved_AfterValidationHasPassed() {

    // Given
    Long vacationRequestId = 1L;
    VacationRequest vacationRequest = new VacationRequest();
    vacationRequest.setId(vacationRequestId);
    VacationRequestStatus status = VacationRequestStatus.PENDING;
    vacationRequest.setStartDate(LocalDateTime.now());
    vacationRequest.setEndDate(LocalDateTime.now().plusDays(10));
    vacationRequest.setOfficeLocationId(vacationRequest.getOfficeLocationId());

    when(vacationRequestRepositoryMock.findByIdAndStatus(vacationRequestId, status))
        .thenReturn(Optional.of(vacationRequest));
    when(vacationRequestRepositoryMock.save(any(VacationRequest.class)))
        .thenReturn(vacationRequest);

    // When
    VacationRequest approvedVacationRequest =
        cut.approveVacationRequest(vacationRequest.getId(), VacationRequestStatus.APPROVED);

    // Then
    verify(vacationRequestRepositoryMock, times(1)).save(vacationRequest);
    verify(scheduleServiceMock)
        .addApprovedVacationRequestToSchedule(
            vacationRequest.getEmployeeId(), approvedVacationRequest);
  }

  @Test
  void declineVacationRequest_ChangeStatusToDeclined() {

    // Given
    Long vacationRequestId = 10L;
    String rejectionReason = "Required";
    VacationRequest vacationRequest = new VacationRequest();
    vacationRequest.setId(vacationRequestId);
    vacationRequest.setStatus(VacationRequestStatus.PENDING);
    when(vacationRequestRepositoryMock.findByIdAndStatus(
            vacationRequestId, vacationRequest.getStatus()))
        .thenReturn(Optional.of(vacationRequest));
    when(vacationRequestRepositoryMock.save(any(VacationRequest.class)))
        .thenReturn(vacationRequest);

    // When
    VacationRequest rejectedVacationRequest =
        cut.declineVacationRequest(vacationRequestId, rejectionReason);

    // Then
    assertEquals(VacationRequestStatus.REJECTED, rejectedVacationRequest.getStatus());

    verify(vacationRequestRepositoryMock, times(1)).save(vacationRequest);
  }

  @Test
  void approveVacationRequest_ShouldThrowExceptionWhenVacationRequestIdIsNotFound() {

    // Given
    Long vacationRequestId = 10L;
    VacationRequestStatus status = VacationRequestStatus.PENDING;

    when(vacationRequestRepositoryMock.findByIdAndStatus(vacationRequestId, status))
        .thenReturn(Optional.empty());

    // When & Then
    assertThrows(
        VacationRequestNotFoundException.class,
        () -> cut.approveVacationRequest(vacationRequestId, status));

    verify(vacationRequestRepositoryMock, times(1)).findByIdAndStatus(vacationRequestId, status);
    verifyNoMoreInteractions(vacationRequestRepositoryMock);
  }

  @Test
  void shouldRetrieveListOfVacationRequestsThroughTheEmployeeId() {
    // Given

    Long employeeId = 1L;
    VacationRequest vacationRequest = new VacationRequest();
    when(vacationRequestRepositoryMock.findByEmployeeId(employeeId))
        .thenReturn(List.of(vacationRequest));

    // When
    List<VacationRequest> retrievedVacationRequest =
        cut.getVacationRequestsByEmployeeId(employeeId);

    // Then
    assertNotNull(retrievedVacationRequest);
  }

  @Test
  void shouldThrowExceptionWhenVacationRequestIsNotFoundByIdAndStatus() {

    // Given
    Long vacationRequestId = 10L;
    VacationRequest vacationRequest = new VacationRequest();
    vacationRequest.setId(vacationRequestId);
    VacationRequestStatus status = VacationRequestStatus.PENDING;
    when(vacationRequestRepositoryMock.findByIdAndStatus(vacationRequestId, status))
        .thenReturn(Optional.empty());

    // When
    assertThrows(
        VacationRequestNotFoundException.class,
        () -> cut.getVacationRequestByIdAndStatus(vacationRequest.getId(), status));

    // Then
    verify(vacationRequestRepositoryMock, times(1)).findByIdAndStatus(vacationRequestId, status);
    verifyNoMoreInteractions(vacationRequestRepositoryMock);
  }
}
