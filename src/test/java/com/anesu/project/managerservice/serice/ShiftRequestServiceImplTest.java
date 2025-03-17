package com.anesu.project.managerservice.serice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.anesu.project.managerservice.entity.shift.ShiftRequest;
import com.anesu.project.managerservice.entity.shift.ShiftRequestStatus;
import com.anesu.project.managerservice.entity.shift.ShiftType;
import com.anesu.project.managerservice.model.ScheduleService;
import com.anesu.project.managerservice.model.repository.ShiftRequestRepository;
import com.anesu.project.managerservice.service.ShiftRequestServiceImpl;
import com.anesu.project.managerservice.service.exception.ShiftRequestNotFoundException;
import com.anesu.project.managerservice.service.exception.ShiftValidationException;
import com.anesu.project.managerservice.service.util.ShiftRequestValidator;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShiftRequestServiceImplTest {

  @Mock private ShiftRequestRepository shiftRequestRepositoryMock;
  @Mock private ShiftRequestValidator shiftRequestValidatorMock;
  @Mock private ScheduleService scheduleServiceMock;

  private ShiftRequestServiceImpl cut;

  @BeforeEach
  void setUp() {
    cut =
        new ShiftRequestServiceImpl(
            shiftRequestRepositoryMock, shiftRequestValidatorMock, scheduleServiceMock);
  }

  @Test
  void shouldSuccessfullyApprove_AndChangeShiftRequestStatusToApproved() {

    // Given
    Long shiftRequestId = 1L;
    Long employeeId = 20L;
    ShiftRequest shiftRequest = new ShiftRequest();
    shiftRequest.setId(shiftRequestId);
    shiftRequest.setEmployeeId(employeeId);
    shiftRequest.setStatus(ShiftRequestStatus.PENDING);
    shiftRequest.setShiftType(ShiftType.AFTERNOON_SHIFT);
    shiftRequest.setShiftDate(LocalDateTime.of(2025, 5, 10, 13, 0));
    shiftRequest.setShiftLengthInHours(8L);

    when(shiftRequestRepositoryMock.findByIdAndStatus(
            shiftRequest.getId(), ShiftRequestStatus.PENDING))
        .thenReturn(Optional.of(shiftRequest));
    when(shiftRequestRepositoryMock.save(any(ShiftRequest.class))).thenReturn(shiftRequest);

    // When
    ShiftRequest approvedShiftRequest = cut.approveShiftRequest(employeeId, shiftRequest.getId());

    // Then
    assertEquals(ShiftRequestStatus.APPROVED, approvedShiftRequest.getStatus());

    verify(shiftRequestRepositoryMock, times(1)).save(shiftRequest);
  }

  @Test
  void approveShiftRequest_ShouldNotApproveShiftRequestIfValidationHasFailed() {

    // Given
    Long shiftRequestId = 10L;
    ShiftRequest shiftRequest = new ShiftRequest();
    shiftRequest.setId(shiftRequestId);
    shiftRequest.setEmployeeId(20L);
    shiftRequest.setStatus(ShiftRequestStatus.PENDING);

    when(shiftRequestRepositoryMock.findByIdAndStatus(shiftRequestId, ShiftRequestStatus.PENDING))
        .thenReturn(Optional.of(shiftRequest));

    doThrow(ShiftValidationException.class)
        .when(shiftRequestValidatorMock)
        .validateShiftRequest(any(ShiftRequest.class), any(ShiftRequestRepository.class));

    // When

    assertThrows(
        ShiftValidationException.class,
        () -> cut.approveShiftRequest(shiftRequest.getEmployeeId(), shiftRequest.getId()));

    // Then

    verify(shiftRequestValidatorMock)
        .validateShiftRequest(shiftRequest, shiftRequestRepositoryMock);
    verifyNoMoreInteractions(shiftRequestRepositoryMock);
  }

  @Test
  void declineShiftRequest_ShouldChangeStatusToDeclined() {

    // Given
    Long shiftRequestId = 1L;
    String rejectionReason = "The reason here";
    ShiftRequest shiftRequest = new ShiftRequest();
    shiftRequest.setId(shiftRequestId);
    shiftRequest.setStatus(ShiftRequestStatus.PENDING);
    shiftRequest.setShiftType(ShiftType.AFTERNOON_SHIFT);
    shiftRequest.setShiftDate(LocalDateTime.of(2025, 4, 10, 12, 30));
    shiftRequest.setShiftLengthInHours(6L);

    when(shiftRequestRepositoryMock.findByIdAndStatus(
            shiftRequest.getId(), ShiftRequestStatus.PENDING))
        .thenReturn(Optional.of(shiftRequest));
    when(shiftRequestRepositoryMock.save(any(ShiftRequest.class))).thenReturn(shiftRequest);

    // When
    ShiftRequest rejectedShiftRequest =
        cut.declineShiftRequest(shiftRequest.getId(), rejectionReason);

    // Then
    assertEquals(rejectionReason, rejectedShiftRequest.getRejectionReason());
    assertEquals(ShiftRequestStatus.REJECTED, rejectedShiftRequest.getStatus());

    verify(shiftRequestRepositoryMock, times(1)).save(shiftRequest);
  }

  @Test
  void shouldRetrieveShiftRequestByIdAndStatusWithinTheDatabase() {

    // Given
    Long shiftRequestId = 1L;
    ShiftRequest shiftRequest = new ShiftRequest();
    shiftRequest.setId(shiftRequestId);
    ShiftRequestStatus status = ShiftRequestStatus.PENDING;
    when(shiftRequestRepositoryMock.findByIdAndStatus(shiftRequest.getId(), status))
        .thenReturn(Optional.of(shiftRequest));

    // When
    ShiftRequest retreivedShiftRequest = cut.getShiftRequestByIdAndStatus(shiftRequestId, status);

    // Then
    assertNotNull(retreivedShiftRequest);
  }

  @Test
  void getShiftRequestByIdAndStatus_ThrowException_WhenShiftRequestIdOrStatusIsNotFound() {
    // Given
    Long shiftRequestId = 1L;
    ShiftRequest shiftRequest = new ShiftRequest();
    shiftRequest.setId(shiftRequestId);
    ShiftRequestStatus status = ShiftRequestStatus.PENDING;
    when(shiftRequestRepositoryMock.findByIdAndStatus(shiftRequest.getId(), status))
        .thenReturn(Optional.empty());

    // When
    assertThrows(
        ShiftRequestNotFoundException.class,
        () -> cut.getShiftRequestByIdAndStatus(shiftRequest.getId(), status));

    // Then

    verify(shiftRequestRepositoryMock, times(1)).findByIdAndStatus(shiftRequestId, status);
    verifyNoMoreInteractions(shiftRequestRepositoryMock);
  }

  @Test
  void getShiftRequestsByEmployeeId_ThrowException_WhenEmployeeIdIsNotFound() {

    // Given
    Long employeeId = 20L;

    doThrow(ShiftRequestNotFoundException.class)
        .when(shiftRequestRepositoryMock)
        .findByEmployeeId(employeeId);

    // When
    assertThrows(
        ShiftRequestNotFoundException.class, () -> cut.getShiftRequestByEmployeeId(employeeId));

    // Then
    verify(shiftRequestRepositoryMock, times(1)).findByEmployeeId(employeeId);
    verifyNoMoreInteractions(shiftRequestRepositoryMock);
  }

  @Test
  void validateShiftTheRequest_ThrowException_WhenShiftRequestExceeds_MaximumLegalWorkingHours() {

    // Given
    Long shiftRequestId = 1L;
    Long employeeId = 2L;

    LocalDateTime shiftDate = LocalDateTime.now();

    ShiftRequest shiftRequest = new ShiftRequest();
    shiftRequest.setId(shiftRequestId);
    shiftRequest.setEmployeeId(employeeId);
    shiftRequest.setShiftDate(shiftDate);
    shiftRequest.setShiftLengthInHours(12L);
    ShiftRequestStatus status = ShiftRequestStatus.PENDING;

    when(shiftRequestRepositoryMock.findByIdAndStatus(shiftRequest.getId(), status))
        .thenReturn(Optional.of(shiftRequest));
    doThrow(ShiftValidationException.class)
        .when(shiftRequestValidatorMock)
        .validateShiftRequest(shiftRequest, shiftRequestRepositoryMock);

    // When

    assertThrows(
        ShiftValidationException.class, () -> cut.approveShiftRequest(employeeId, shiftRequestId));

    // Then

    verify(shiftRequestRepositoryMock, times(1)).findByIdAndStatus(shiftRequestId, status);
    verifyNoMoreInteractions(shiftRequestRepositoryMock);
  }


}
