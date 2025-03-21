package com.anesu.project.managerservice.service.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.anesu.project.managerservice.entity.schedule.Schedule;
import com.anesu.project.managerservice.entity.shift.ShiftEntry;
import com.anesu.project.managerservice.service.exception.InvalidScheduleException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ScheduleValidatorTest {

  private static final LocalDateTime START_DATE =
      LocalDateTime.from(LocalDate.of(2024, 2, 20).atTime(10, 0));
  private static final LocalDateTime END_DATE =
      LocalDateTime.from(LocalDate.of(2024, 2, 25).atTime(18, 0, 0));

  private ScheduleValidator cut;

  @BeforeEach
  void setUp() {
    cut = new ScheduleValidator();
  }

  @Test
  void validateDates_shouldThrowExceptionWhenStartDateIsNull() {
    // Given

    Schedule schedule = new Schedule();
    List<ShiftEntry> shiftEntries = new ArrayList<>();

    schedule.setStartDate(null);
    schedule.setEndDate(END_DATE);
    schedule.setShifts(shiftEntries);

    // When
    InvalidScheduleException invalidScheduleException =
        assertThrows(InvalidScheduleException.class, () -> cut.validateSchedule(schedule));

    // Then
    assertEquals(invalidScheduleException.getMessage(), "Start date or end date is null");
  }

  @Test
  void validateDates_shouldThrowExceptionWhenEndDateIsNull() {
    // Given
    List<ShiftEntry> shiftEntries = new ArrayList<>();

    Schedule schedule = new Schedule();
    schedule.setStartDate(START_DATE);
    schedule.setEndDate(null);
    schedule.setShifts(shiftEntries);

    // When
    InvalidScheduleException invalidScheduleException =
        assertThrows(InvalidScheduleException.class, () -> cut.validateSchedule(schedule));

    // Then
    assertEquals(invalidScheduleException.getMessage(), "Start date or end date is null");
  }

  @Test
  void validateDates_shouldThrowExceptionWhenShiftsOrVacationAreNull() {
    // Given
    Schedule schedule = new Schedule();
    schedule.setStartDate(START_DATE);
    schedule.setEndDate(END_DATE);
    schedule.setShifts(null);
    schedule.setVacations(null);

    // When
    InvalidScheduleException invalidScheduleException =
        assertThrows(InvalidScheduleException.class, () -> cut.validateSchedule(schedule));

    // Then
    assertEquals(invalidScheduleException.getMessage(), "Shifts or vacations must be provided.");
  }

  @Test
  void validateDates_shouldThrowException_WhenEndDateAfterStartDate() {
    // Given

    List<ShiftEntry> shiftEntries = new ArrayList<>();

    Schedule schedule = new Schedule();
    schedule.setStartDate(LocalDateTime.of(2025, 12, 10, 14, 0));
    schedule.setEndDate(END_DATE);

    // When
    InvalidScheduleException invalidScheduleException =
        assertThrows(InvalidScheduleException.class, () -> cut.validateSchedule(schedule));

    // Then
    assertEquals(invalidScheduleException.getMessage(), "Start date cannot be after end date");
  }

  @Test
  void validateWorkingHours_shouldThrowExceptionWhenWorkingHoursExceed_MaxHoursPerShift() {
    // Given

    List<ShiftEntry> shiftEntries = new ArrayList<>();

    Schedule schedule = new Schedule();
    schedule.setStartDate(START_DATE);
    schedule.setEndDate(END_DATE);
    schedule.setTotalWorkingHours(12L);
    schedule.setShifts(shiftEntries);

    // When
    InvalidScheduleException invalidScheduleException =
        assertThrows(InvalidScheduleException.class, () -> cut.validateSchedule(schedule));

    // Then
    assertEquals(invalidScheduleException.getMessage(), "Shift exceeds maximum working hours.");
  }
}
