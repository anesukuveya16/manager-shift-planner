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
        Schedule schedule = new Schedule();
        schedule.setEmployeeId(1L);
        schedule.setStartDate(schedule.getStartDate());
        schedule.setEndDate(LocalDateTime.from(LocalDate.now().plusDays(3).atTime(8, 30)));
        schedule.setTotalWorkingHours(schedule.getTotalWorkingHours());


        // When


        // Then
    }
}
