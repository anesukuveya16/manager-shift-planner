package com.anesu.project.managerservice.serice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.anesu.project.managerservice.entity.shift.ShiftRequest;
import com.anesu.project.managerservice.entity.shift.ShiftRequestStatus;
import com.anesu.project.managerservice.entity.shift.ShiftType;
import com.anesu.project.managerservice.model.ScheduleService;
import com.anesu.project.managerservice.model.repository.ShiftRequestRepository;
import com.anesu.project.managerservice.service.ShiftRequestServiceImpl;
import com.anesu.project.managerservice.service.exception.ShiftRequestNotFoundException;
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
  void declineShiftRequest_ShouldChangeStatusToDeclined() {

    // Given
    Long shiftRequestId = 1L;
    String rejectionReason = "The reason here";
    ShiftRequest shiftRequest = new ShiftRequest();
    shiftRequest.setId(shiftRequestId);
    shiftRequest.setStatus(ShiftRequestStatus.PENDING);
    shiftRequest.setShiftType(ShiftType.AFTERNOON_SHIFT);
    shiftRequest.setShiftDate(LocalDateTime.of(2025, 4,10,12,30));
    shiftRequest.setShiftLengthInHours(6L);

    when(shiftRequestRepositoryMock.findById(shiftRequestId)).thenReturn(Optional.of(shiftRequest));

    // When



    // Then



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
    ShiftRequestNotFoundException shiftRequestNotFoundException =
        assertThrows(
            ShiftRequestNotFoundException.class,
            () -> cut.getShiftRequestByIdAndStatus(shiftRequest.getId(), status));

    // Then
    assertThat(shiftRequestNotFoundException.getMessage())
        .isEqualTo("Could not find pending shift request with ID: " + shiftRequestId);

    verify(shiftRequestRepositoryMock, times(1)).findByIdAndStatus(shiftRequestId, status);
    verifyNoMoreInteractions(shiftRequestRepositoryMock);
  }

  @Test
  void getShiftRequestsByEmployeeId_ThrowException_WhenEmployeeIdIsNotFound() {

    // Given
    Long employeeId = 20L;

    when(shiftRequestRepositoryMock.findById(employeeId)).thenReturn(Optional.empty());

    // When
    ShiftRequestNotFoundException shiftRequestNotFoundException = assertThrows(
            ShiftRequestNotFoundException.class,
            () -> cut.getShiftRequestByEmployeeId(employeeId));

    // Then
    assertThat(shiftRequestNotFoundException.getMessage()).isEqualTo("Could not find  shift request with ID: " + employeeId);

    verify(shiftRequestRepositoryMock, times(1)).findById(employeeId);
  }

}
