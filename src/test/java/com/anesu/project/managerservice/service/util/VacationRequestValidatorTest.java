package com.anesu.project.managerservice.service.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.anesu.project.managerservice.entity.vacation.VacationRequest;
import com.anesu.project.managerservice.entity.vacation.VacationRequestStatus;
import com.anesu.project.managerservice.model.repository.VacationRequestRepository;
import com.anesu.project.managerservice.service.exception.InvalidVacationRequestException;
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

    private static final int MAX_VACATION_DAYS_EACH_YEAR = 30;
    @Mock private VacationRequestRepository vacationRequestRepositoryMock;

  private VacationRequestValidator cut;

  @BeforeEach
  void setUp() {

    VacationRequest vacationRequest = new VacationRequest();
    vacationRequest.setEmployeeId(1L);
    vacationRequest.setStartDate(LocalDateTime.now().plusDays(10));
    vacationRequest.setEndDate(LocalDateTime.now().plusDays(15)); // 6 days
    vacationRequest.setStatus(VacationRequestStatus.PENDING);
    cut = new VacationRequestValidator();
  }

  @Test
  void validateVacationRequest_NotThrowExceptionIfAnnualLeaveDaysHaveNotBeenExceeded() {
    // Given
    LocalDateTime startDate = LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0);
    List<VacationRequest> usedVacationDays =
        Arrays.stream(new int[] {10, 5, 5})
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

    VacationRequest givenVacationRequest = new VacationRequest();
    givenVacationRequest.setEmployeeId(2L);
    givenVacationRequest.setStartDate(LocalDateTime.now().plusDays(10));
    givenVacationRequest.setEndDate(LocalDateTime.now().plusDays(15));
    givenVacationRequest.setStatus(VacationRequestStatus.PENDING);

    when(vacationRequestRepositoryMock.findByEmployeeIdAndOverlappingVacationDays(
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
                  vacationRequest.setStatus(VacationRequestStatus.PENDING);
                  return vacationRequest;
                })
            .toList();

    VacationRequest givenVacationRequest = newlyCreatedPendingVacationRequest();

    when(vacationRequestRepositoryMock.findByEmployeeIdAndOverlappingVacationDays(
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

    List<VacationRequest> usedVacationRequests =
        List.of(
            new VacationRequest() {
              {
                setEmployeeId(305L);
                setStartDate(LocalDateTime.of(2025, 01, 27, 0, 0));
                setEndDate(LocalDateTime.of(2025, 02, 10, 0, 0));
                setStatus(VacationRequestStatus.APPROVED);
              }
            },
            new VacationRequest() {
              {
                setEmployeeId(305L);
                setStartDate(LocalDateTime.of(2025, 03, 28, 0, 0));
                setEndDate(LocalDateTime.of(2025, 04, 07, 0, 0));
                setStatus(VacationRequestStatus.APPROVED);
              }
            });

    VacationRequest givenVacationRequest = newlyCreatedPendingVacationRequest();

    when(vacationRequestRepositoryMock.findByEmployeeIdAndOverlappingVacationDays(
            anyLong(), any(), any()))
        .thenReturn(usedVacationRequests);

    // When & Then
        assertThrows(
            InvalidVacationRequestException.class,
            () -> cut.validateVacationRequest(givenVacationRequest, vacationRequestRepositoryMock));

  }

  private static VacationRequest newlyCreatedPendingVacationRequest() {
    VacationRequest givenVacationRequest = new VacationRequest();
    givenVacationRequest.setEmployeeId(305L);
    givenVacationRequest.setStartDate(LocalDateTime.now().plusDays(10));
    givenVacationRequest.setEndDate(LocalDateTime.now().plusDays(25));
    givenVacationRequest.setStatus(VacationRequestStatus.PENDING);
    return givenVacationRequest;
  }
}
