package com.anesu.project.managerservice.model.repository;

import com.anesu.project.managerservice.entity.shift.ShiftRequest;
import com.anesu.project.managerservice.entity.shift.ShiftRequestStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftRequestRepository extends JpaRepository<ShiftRequest, Long> {
  Optional<ShiftRequest> existsByIdAndShiftDate(Long employeeId, LocalDateTime shiftDate);

  ShiftRequest findByEmployeeId(Long employeeId);

  List<ShiftRequest> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

  Optional<ShiftRequest> findByIdAndStatus(Long shiftRequestId, ShiftRequestStatus status);
}
