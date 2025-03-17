package com.anesu.project.managerservice.serice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.anesu.project.managerservice.entity.schedule.Schedule;
import com.anesu.project.managerservice.entity.shift.ShiftEntry;
import com.anesu.project.managerservice.entity.shift.ShiftRequest;
import com.anesu.project.managerservice.entity.shift.ShiftRequestStatus;
import com.anesu.project.managerservice.entity.shift.ShiftType;
import com.anesu.project.managerservice.entity.vacation.VacationEntry;
import com.anesu.project.managerservice.entity.vacation.VacationRequest;
import com.anesu.project.managerservice.entity.vacation.VacationRequestStatus;
import com.anesu.project.managerservice.model.repository.ScheduleRepository;
import com.anesu.project.managerservice.service.ScheduleServiceImpl;
import com.anesu.project.managerservice.service.exception.ScheduleNotFoundException;
import com.anesu.project.managerservice.service.util.ScheduleValidator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
    oldSchedule.setTotalWorkingHours(6L);
    oldSchedule.setStartDate(LocalDate.now().plusDays(2).atTime(9, 0));
    oldSchedule.setEndDate(LocalDate.now().plusDays(3).atTime(15, 0));

    Schedule newSchedule = new Schedule();
    newSchedule.setId(100L);
    newSchedule.setEmployeeId(1L);
    newSchedule.setTotalWorkingHours(8L);
    newSchedule.setStartDate(oldSchedule.getStartDate());
    newSchedule.setEndDate(LocalDate.now().plusDays(3).atTime(17, 0));

    when(scheduleRepositoryMock.findById(oldSchedule.getEmployeeId()))
        .thenReturn(Optional.of(oldSchedule));
    doNothing().when(scheduleValidatorMock).validateSchedule(any(Schedule.class));
    when(scheduleRepositoryMock.save(any(Schedule.class))).thenReturn(newSchedule);

    // When
    Schedule newlyUpdatedSchedule =
        cut.updateEmployeeSchedule(oldSchedule.getEmployeeId(), newSchedule);

    // Then
    assertNotNull(newlyUpdatedSchedule);
    assertThat(newlyUpdatedSchedule.getEmployeeId()).isEqualTo(oldSchedule.getEmployeeId());
    assertThat(newlyUpdatedSchedule.getTotalWorkingHours())
        .isEqualTo(newSchedule.getTotalWorkingHours());

    verify(scheduleValidatorMock).validateSchedule(newlyUpdatedSchedule);
    verify(scheduleRepositoryMock, times(1)).save(newlyUpdatedSchedule);
  }

  @Test
  void shouldThrowExceptionWhenScheduleToBeUpdatedIsNotFound() {

    // Given
    Long scheduleId = 100L;
    Schedule schedule = new Schedule();
    schedule.setId(scheduleId);

    when(scheduleRepositoryMock.findById(scheduleId)).thenReturn(Optional.empty());

    // When
    assertThrows(
        ScheduleNotFoundException.class, () -> cut.updateEmployeeSchedule(scheduleId, schedule));

    // Then
    verify(scheduleRepositoryMock, times(1)).findById(scheduleId);
    verifyNoMoreInteractions(scheduleRepositoryMock);
  }

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
  void shouldAddApprovedShiftRequestToSchedule() {
    // Given
    Long employeeId = 1L;
    ShiftRequest shiftRequest = new ShiftRequest();
    shiftRequest.setEmployeeId(employeeId);
    shiftRequest.setId(100L);
    shiftRequest.setShiftLengthInHours(6L);
    shiftRequest.setShiftType(ShiftType.NIGHT_SHIFT);
    shiftRequest.setStatus(ShiftRequestStatus.APPROVED);
    shiftRequest.setShiftDate(LocalDateTime.of(2025, 5, 29, 20, 30));

    Schedule approvedShiftRequest = new Schedule();

    when(scheduleRepositoryMock.save(any(Schedule.class))).thenReturn(approvedShiftRequest);

    // When
    Schedule updatedSchedule = cut.addShiftToSchedule(employeeId, shiftRequest);

    // Then
    assertNotNull(updatedSchedule);

    List<ShiftEntry> shifts = updatedSchedule.getShifts();
    assertThat(shifts).hasSize(1);

    ShiftEntry shiftEntry = shifts.getFirst();
    assertThat(shiftEntry.getShiftDate()).isEqualTo(shiftRequest.getShiftDate());
    assertThat(shiftEntry.getWorkingHours()).isEqualTo(shiftRequest.getShiftLengthInHours());
    assertThat(shiftEntry.getShiftType()).isEqualTo(shiftRequest.getShiftType());
  }

  @Test
  void updateEmployeeSchedule_shouldNotProceedWithScheduleUpdate_WhenValidationFails() {

    // Given
    Schedule oldSchedule = new Schedule();
    oldSchedule.setId(100L);
    oldSchedule.setEmployeeId(1L);
    oldSchedule.setTotalWorkingHours(6L);
    oldSchedule.setStartDate(LocalDate.now().plusDays(2).atTime(9, 0));
    oldSchedule.setEndDate(LocalDate.now().plusDays(3).atTime(15, 0));

    Schedule updatedSchedule = new Schedule();
    updatedSchedule.setId(100L);
    updatedSchedule.setEmployeeId(1L);
    updatedSchedule.setTotalWorkingHours(8L);
    updatedSchedule.setStartDate(LocalDate.now().plusDays(1).atTime(8,0));
    updatedSchedule.setEndDate(LocalDate.now().plusDays(3).atTime(17, 0));

    when(scheduleRepositoryMock.findById(oldSchedule.getId())).thenReturn(Optional.of(oldSchedule));

    doThrow(ScheduleNotFoundException.class)
        .when(scheduleValidatorMock)
        .validateSchedule(updatedSchedule);

    // When
    assertThrows(
        ScheduleNotFoundException.class,
        () -> cut.updateEmployeeSchedule(oldSchedule.getId(), updatedSchedule));

    // Then
    verify(scheduleRepositoryMock, times(1)).findById(oldSchedule.getId());
    verify(scheduleValidatorMock).validateSchedule(updatedSchedule);
    verifyNoMoreInteractions(scheduleRepositoryMock);
  }

  @Test
  void addOnlyTheApprovedVacationToSchedule() {

    // Given
    Long employeeId = 1L;

    VacationRequest vacationRequest = new VacationRequest();
    vacationRequest.setEmployeeId(employeeId);
    vacationRequest.setId(100L);
    vacationRequest.setStatus(VacationRequestStatus.APPROVED);
    vacationRequest.setStartDate(LocalDateTime.now());
    vacationRequest.setEndDate(LocalDateTime.now().plusDays(10));

    Schedule approvedVacationRequest = new Schedule();

    when(scheduleRepositoryMock.save(any(Schedule.class))).thenReturn(approvedVacationRequest);

    // When
    Schedule updatedSchedule =
        cut.addApprovedVacationRequestToSchedule(employeeId, vacationRequest);

    // Then
    List<VacationEntry> vacations = updatedSchedule.getVacations();

    VacationEntry vacationEntry = vacations.getFirst();
    assertThat(vacations).hasSize(10);
    assertThat(vacationEntry.getStartDate()).isEqualTo(vacationRequest.getStartDate());
    assertThat(vacationEntry.getEndDate()).isEqualTo(vacationRequest.getEndDate());
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
