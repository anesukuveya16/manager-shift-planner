package com.anesu.project.managerservice.model.repository;

import com.anesu.project.managerservice.entity.vacation.VacationRequest;
import com.anesu.project.managerservice.entity.vacation.VacationRequestStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacationRequestRepository extends JpaRepository<VacationRequest, Long> {

  List<VacationRequest> findByEmployeeId(Long employeeId);

  List<VacationRequest> findByEmployeeIdAndDateRange(
      Long employeeId, LocalDateTime startDate, LocalDateTime endDate);

  Optional<VacationRequest> findByIdAndStatus(Long vacationRequestId, VacationRequestStatus status);
}
