package com.anesu.project.managerservice.model.repository;

import com.anesu.project.managerservice.entity.schedule.Schedule;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
  Optional<List<Schedule>> findByAllEmployeeIdAndGivenDateRange(
      Long scheduleId, LocalDateTime startDate, LocalDateTime endDate);

  Optional<Schedule> findByEmployeeIdAndCalendarWeek(
      Long employeeId,
      LocalDateTime startOfShiftCalendarWeek,
      LocalDateTime endOfShiftCalendarWeek);
}
