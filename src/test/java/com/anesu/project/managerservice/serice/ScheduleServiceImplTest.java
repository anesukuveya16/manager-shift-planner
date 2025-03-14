package com.anesu.project.managerservice.serice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.anesu.project.managerservice.entity.schedule.Schedule;
import com.anesu.project.managerservice.entity.shift.ShiftRequest;
import com.anesu.project.managerservice.entity.shift.ShiftRequestStatus;
import com.anesu.project.managerservice.entity.shift.ShiftType;
import com.anesu.project.managerservice.entity.vacation.VacationRequest;
import com.anesu.project.managerservice.entity.vacation.VacationRequestStatus;
import com.anesu.project.managerservice.model.repository.ScheduleRepository;
import com.anesu.project.managerservice.service.ScheduleServiceImpl;
import com.anesu.project.managerservice.service.exception.InvalidScheduleException;
import com.anesu.project.managerservice.service.exception.ScheduleNotFoundException;
import com.anesu.project.managerservice.service.util.ScheduleValidator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceImplTest {

  @Mock private ScheduleRepository scheduleRepositoryMock;
  @Mock private ScheduleValidator scheduleValidatorMock;

  private ScheduleServiceImpl cut;

  @BeforeEach
  void setUp() {
    cut = new ScheduleServiceImpl(scheduleRepositoryMock, scheduleValidatorMock);
  }

  @Test
  void shouldUpdateAndSaveTheNewlyUpdatedSchedule() {
    // Given
    Schedule oldSchedule = new Schedule();
    oldSchedule.setId(100L);
    oldSchedule.setEmployeeId(1L);
    oldSchedule.setStartDate(LocalDate.now().plusDays(2).atTime(9, 0));
    oldSchedule.setEndDate(LocalDate.now().plusDays(3).atTime(15, 0));
    oldSchedule.setTotalWorkingHours(6L); // Assign working hours

    Schedule newSchedule = new Schedule();
    newSchedule.setId(100L);
    newSchedule.setEmployeeId(1L);
    newSchedule.setStartDate(oldSchedule.getStartDate()); // same start time from old schedule
    oldSchedule.setEndDate(LocalDate.now().plusDays(3).atTime(17, 0));
    newSchedule.setTotalWorkingHours(8L);

    when(scheduleRepositoryMock.findById(oldSchedule.getEmployeeId()))
        .thenReturn(Optional.of(oldSchedule));
    doNothing().when(scheduleValidatorMock).validateSchedule(any(Schedule.class)); // validate the
    when(scheduleRepositoryMock.save(any(Schedule.class))).thenReturn(newSchedule);

    // When
    Schedule newlyUpdatedSchedule =
        cut.updateEmployeeSchedule(oldSchedule.getEmployeeId(), newSchedule);

    // Then
    assertNotNull(newlyUpdatedSchedule);
    assertThat(newlyUpdatedSchedule.getEmployeeId()).isEqualTo(oldSchedule.getEmployeeId());
    assertThat(newlyUpdatedSchedule.getTotalWorkingHours())
        .isEqualTo(newSchedule.getTotalWorkingHours());

    verify(scheduleRepositoryMock, times(1))
        .findById(
            newlyUpdatedSchedule
                .getId()); // makes sure that the existing schedule was called and retrieved once
    verify(scheduleValidatorMock)
        .validateSchedule(newlyUpdatedSchedule); // validation the new schedule
    verify(scheduleRepositoryMock, times(1)).save(newlyUpdatedSchedule);
  }

  @Test
  void shouldThrowExceptionWhenScheduleToBeUpdatedIsNotFound() {}

  @Test
  void shouldRetrieveTheScheduleByGivenScheduleId() {
    // Given
    long scheduleId = 100L;
    Schedule schedule = new Schedule();
    when(scheduleRepositoryMock.findById(scheduleId)).thenReturn(Optional.of(schedule));

    // When
    Optional<Schedule> retrievedSchedule = cut.getScheduleById(scheduleId);

    // Then
    assertNotNull(retrievedSchedule);
    assertTrue(retrievedSchedule.isPresent());
  }

  @Test
  void
      addOnlyTheApprovedShiftToSchedule_ShouldThrowExceptionWhenShiftToBeAddedHasNotBeenApproved() {
    // Given

    ShiftRequest shiftRequest = new ShiftRequest();
    shiftRequest.setEmployeeId(1L);
    shiftRequest.setId(shiftRequest.getId());
    shiftRequest.setShiftLengthInHours(6L);
    shiftRequest.setShiftType(ShiftType.NIGHT_SHIFT);
    shiftRequest.setStatus(ShiftRequestStatus.PENDING);
    shiftRequest.setShiftDate(LocalDateTime.from(LocalDate.of(2025, 5, 29).atTime(20, 30)));

    // When
    InvalidScheduleException invalidScheduleException =
        assertThrows(
            InvalidScheduleException.class,
            () -> cut.addShiftToSchedule(shiftRequest.getEmployeeId(), shiftRequest));

    // Then
    assertThat(invalidScheduleException.getMessage())
        .isEqualTo(
            "Invalid schedule operation. Only approved shifts can be added to the schedule.");
  }

  @Test
  void
      addOnlyTheApprovedVacationToSchedule_ShouldThrowExceptionWhenShiftToBeAddedHasNotBeenApproved() {
    // Given

    VacationRequest vacationRequest = new VacationRequest();
    vacationRequest.setEmployeeId(10L);
    vacationRequest.setOfficeLocationId(200L);
    vacationRequest.setStatus(VacationRequestStatus.PENDING);
    vacationRequest.setStartDate(LocalDateTime.from(LocalDate.of(2025, 5, 12).atTime(8, 0)));
    vacationRequest.setEndDate(LocalDateTime.from(LocalDate.of(2025, 5, 23).atTime(8, 0)));

    ShiftRequest shiftRequest = new ShiftRequest();
    shiftRequest.setEmployeeId(1L);
    shiftRequest.setId(shiftRequest.getId());
    shiftRequest.setShiftLengthInHours(6L);
    shiftRequest.setShiftType(ShiftType.NIGHT_SHIFT);
    shiftRequest.setStatus(ShiftRequestStatus.PENDING);
    shiftRequest.setShiftDate(LocalDateTime.from(LocalDate.of(2025, 5, 29).atTime(20, 30)));

    // When
    InvalidScheduleException invalidScheduleException =
        assertThrows(
            InvalidScheduleException.class,
            () ->
                cut.addApprovedVacationRequestToSchedule(
                    vacationRequest.getEmployeeId(), vacationRequest));

    // Then
    assertThat(invalidScheduleException.getMessage())
        .isEqualTo(
            "Invalid schedule operation. Only approved vacation requests can be added to the schedule.");
  }

  @Test
  void shouldThrowScheduleNotFoundException_WhenScheduleIsNotFoundByGivenId() {
    // Given
    long scheduleId = 100L;
    doThrow(ScheduleNotFoundException.class).when(scheduleRepositoryMock).findById(scheduleId);

    // When
    assertThrows(ScheduleNotFoundException.class, () -> cut.getScheduleById(scheduleId));

    // Then
    verify(scheduleRepositoryMock, times(1)).findById(scheduleId);
    verifyNoMoreInteractions(scheduleRepositoryMock);
  }

  @Test
  void deleteSchedule_ShouldThrowExceptionWhenScheduleIsNotFound() {
    // Given
    long employeeId = 1L;
    doThrow(ScheduleNotFoundException.class).when(scheduleRepositoryMock).existsById(employeeId);

    // When
    assertThrows(ScheduleNotFoundException.class, () -> cut.deleteSchedule(employeeId));

    // Then
    verify(scheduleRepositoryMock, times(1)).existsById(employeeId);
    verifyNoMoreInteractions(scheduleRepositoryMock);
  }
}
