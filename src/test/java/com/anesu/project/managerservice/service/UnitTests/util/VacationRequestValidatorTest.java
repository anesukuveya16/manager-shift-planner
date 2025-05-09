package com.anesu.project.managerservice.service.UnitTests.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.anesu.project.managerservice.entity.vacation.VacationRequest;
import com.anesu.project.managerservice.entity.vacation.VacationRequestStatus;
import com.anesu.project.managerservice.model.repository.VacationRequestRepository;
import com.anesu.project.managerservice.service.exception.InvalidVacationRequestException;
import com.anesu.project.managerservice.service.util.VacationRequestValidator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VacationRequestValidatorTest {

  @Mock private VacationRequestRepository vacationRequestRepositoryMock;

  private VacationRequestValidator cut;

  @BeforeEach
  void setUp() {
    cut = new VacationRequestValidator();
  }

  @Test
  void validateVacationRequest_NotThrowExceptionIfAnnualLeaveDaysHaveNotBeenExceeded() {
    // Given
    LocalDateTime startDate = LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0);
    List<VacationRequest> usedVacationDays =
        Arrays.stream(new int[] {4, 5, 5})
            .mapToObj(
                duration -> {
                  LocalDateTime endDate = startDate.plusDays(duration - 1);
                  VacationRequest vacationRequest = new VacationRequest();
                  vacationRequest.setEmployeeId(2L);
                  vacationRequest.setStartDate(startDate);
                  vacationRequest.setEndDate(endDate);
                  vacationRequest.setStatus(VacationRequestStatus.APPROVED);
                  return vacationRequest;
                })
            .toList();

    VacationRequest givenVacationRequest = newlyCreatedPendingVacationRequest();

    when(vacationRequestRepositoryMock.findByEmployeeIdAndOverlappingIntoNewYear(
            anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(usedVacationDays);

    // When & Then
    cut.validateVacationRequest(givenVacationRequest, vacationRequestRepositoryMock);
  }

  @Test
  void validateVacationRequest_ShouldThrowExceptionWhenAnnualVacationDaysHaveBeenExceeded() {
    // Given

    LocalDateTime startDate = LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0);
    List<VacationRequest> usedVacationDays =
        Arrays.stream(new int[] {5, 7, 5})
            .mapToObj(
                duration -> {
                  LocalDateTime endDate = startDate.plusDays(duration - 1);
                  VacationRequest vacationRequest = new VacationRequest();
                  vacationRequest.setEmployeeId(305L);
                  vacationRequest.setStartDate(startDate);
                  vacationRequest.setEndDate(endDate);
                  vacationRequest.setStatus(VacationRequestStatus.APPROVED);
                  return vacationRequest;
                })
            .toList();

    VacationRequest givenVacationRequest = newlyCreatedPendingVacationRequest();

    when(vacationRequestRepositoryMock.findByEmployeeIdAndOverlappingIntoNewYear(
            anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(usedVacationDays);

    // When & Then

    assertThrows(
        InvalidVacationRequestException.class,
        () -> cut.validateVacationRequest(givenVacationRequest, vacationRequestRepositoryMock));
  }

  @Test
  void shouldThrowException_WhenThereIsAnOverlapBetweenVacationRequests() {
    // Given
    Long employeeId = 2L;

    List<VacationRequest> usedVacationRequests =
        pastApprovedRealisticVacationRequestScenarioInCurrentYear();

    VacationRequest givenVacationRequest =
        createNewVacationRequestWithDuration(
            2L,
            LocalDateTime.of(LocalDateTime.now().getYear(), 05, 4, 0, 0),
            3,
            VacationRequestStatus.PENDING);

    when(vacationRequestRepositoryMock.findByEmployeeIdAndDateRange(anyLong(), any(), any()))
        .thenReturn(usedVacationRequests);

    // When
    InvalidVacationRequestException invalidVacationRequestException =
        assertThrows(
            InvalidVacationRequestException.class,
            () -> cut.validateVacationRequest(givenVacationRequest, vacationRequestRepositoryMock));

    // Then

    assertEquals(
        invalidVacationRequestException.getMessage(),
        "Request could not be fulfilled because there is already an approved vacation request for this period for employee: "
            + employeeId);

    verifyNoMoreInteractions(vacationRequestRepositoryMock);
  }

  @Test
  void
      shouldNotThrowExceptionWhenRemainingVacationDaysSpanningIntoNewYear_DoNotExceedPreviousAnnualLimit() {
    // Given

    List<VacationRequest> usedVacationRequests =
        pastApprovedRealisticVacationRequestScenarioInCurrentYear();

    VacationRequest newVacationRequest =
        createNewVacationRequestWithDuration(
            2L,
            LocalDateTime.of(LocalDate.now().getYear(), 12, 30, 0, 0),
            4,
            VacationRequestStatus.PENDING);

    when(vacationRequestRepositoryMock.findByEmployeeIdAndOverlappingIntoNewYear(
            anyLong(), any(), any()))
        .thenReturn(usedVacationRequests);

    // When & Then

    cut.validateVacationRequest(newVacationRequest, vacationRequestRepositoryMock);
  }

  @Test
  void
      shouldThrowException_WhenRemainingVacationDaysSpanningIntoNewYear_AreNotWithinPreviousAnnualLimit() {
    // Given
    List<VacationRequest> usedVacationRequests =
        pastApprovedRealisticVacationRequestScenarioInCurrentYear();

    VacationRequest newVacationRequest =
        createNewVacationRequestWithDuration(
            2L,
            LocalDateTime.of(LocalDateTime.now().getYear(), 12, 30, 00, 00),
            6,
            VacationRequestStatus.PENDING);

    when(vacationRequestRepositoryMock.findByEmployeeIdAndOverlappingIntoNewYear(
            anyLong(), any(), any()))
        .thenReturn(usedVacationRequests);

    // When
    assertThrows(
        InvalidVacationRequestException.class,
        () -> cut.validateVacationRequest(newVacationRequest, vacationRequestRepositoryMock));

    // Then
    verify(vacationRequestRepositoryMock, never()).save(any());
  }

  // Helper methods
  private VacationRequest newlyCreatedPendingVacationRequest() {
    VacationRequest givenVacationRequest = new VacationRequest();
    givenVacationRequest.setEmployeeId(305L);
    givenVacationRequest.setStartDate(LocalDateTime.now().plusDays(10));
    givenVacationRequest.setEndDate(LocalDateTime.now().plusDays(25));
    givenVacationRequest.setStatus(VacationRequestStatus.PENDING);
    return givenVacationRequest;
  }

  private List<VacationRequest> pastApprovedRealisticVacationRequestScenarioInCurrentYear() {
    return List.of(
        createNewVacationRequestWithDuration(
            2L,
            LocalDateTime.of(LocalDate.now().getYear(), 01, 27, 0, 0),
            15,
            VacationRequestStatus.APPROVED),
        createNewVacationRequestWithDuration(
            2L,
            LocalDateTime.of(LocalDate.now().getYear(), 03, 28, 0, 0),
            11,
            VacationRequestStatus.APPROVED));
  }

  private VacationRequest createNewVacationRequestWithDuration(
      Long employeeId, LocalDateTime startDate, int durationInDays, VacationRequestStatus status) {
    VacationRequest newVacationRequest = new VacationRequest();
    newVacationRequest.setEmployeeId(employeeId);
    newVacationRequest.setStartDate(startDate);
    newVacationRequest.setEndDate(startDate.plusDays(durationInDays - 1));
    newVacationRequest.setStatus(status);
    return newVacationRequest;
  }
}
