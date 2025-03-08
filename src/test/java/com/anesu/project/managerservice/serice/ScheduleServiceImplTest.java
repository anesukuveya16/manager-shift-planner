package com.anesu.project.managerservice.serice;

import com.anesu.project.managerservice.entity.schedule.Schedule;
import com.anesu.project.managerservice.model.repository.ScheduleRepository;
import com.anesu.project.managerservice.service.ScheduleServiceImpl;
import com.anesu.project.managerservice.service.util.ScheduleValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


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
        //Given
        Schedule oldSchedule = new Schedule();
        oldSchedule.setEmployeeId(1L);
        oldSchedule.setStartDate(oldSchedule.getStartDate());
        oldSchedule.setEndDate(LocalDateTime.from(LocalDate.now().plusDays(3).atTime(8, 30)));
        oldSchedule.setTotalWorkingHours(oldSchedule.getTotalWorkingHours());

        Schedule newSchedule = new Schedule();
        newSchedule.setEmployeeId(1L);
        newSchedule.setStartDate(oldSchedule.getStartDate());
        newSchedule.setEndDate(LocalDateTime.from(LocalDate.now().plusDays(3).atTime(10, 45)));
        newSchedule.setTotalWorkingHours(oldSchedule.getTotalWorkingHours());

        when(scheduleRepositoryMock.findById(oldSchedule.getEmployeeId())).thenReturn(Optional.of(newSchedule));
        doNothing().when(scheduleValidatorMock).validateSchedule(any(Schedule.class));
        when(scheduleRepositoryMock.save(any(Schedule.class))).thenReturn(newSchedule);

        // When
        Schedule newlyUpdatedSchedule = cut.updateEmployeeSchedule(oldSchedule.getEmployeeId(), newSchedule);

        // Then

        assertNotNull(newlyUpdatedSchedule);
        assertThat(newlyUpdatedSchedule.getEmployeeId()).isEqualTo(oldSchedule.getEmployeeId());
        assertThat(newlyUpdatedSchedule.getTotalWorkingHours()).isEqualTo(newSchedule.getTotalWorkingHours());

        verify(scheduleRepositoryMock, times(1)).findById(oldSchedule.getId());
        verify(scheduleValidatorMock).validateSchedule(oldSchedule);
        verify(scheduleRepositoryMock, times(1)).save(oldSchedule);

    }
}
