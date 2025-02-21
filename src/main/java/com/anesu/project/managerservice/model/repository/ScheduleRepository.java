package com.anesu.project.managerservice.model.repository;

import com.anesu.project.managerservice.entity.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {}
