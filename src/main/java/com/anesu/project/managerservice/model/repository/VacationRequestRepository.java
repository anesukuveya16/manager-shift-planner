package com.anesu.project.managerservice.model.repository;

import com.anesu.project.managerservice.entity.vacation.VacationRequest;
import com.anesu.project.managerservice.entity.vacation.VacationRequestStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VacationRequestRepository extends JpaRepository<VacationRequest, Long> {

  List<VacationRequest> findByEmployeeId(Long employeeId);

  List<VacationRequest> findByEmployeeIdAndDateRange(
      Long employeeId, LocalDateTime startDate, LocalDateTime endDate);

  Optional<VacationRequest> findByIdAndStatus(Long vacationRequestId, VacationRequestStatus status);

  @Query(
      "SELECT v FROM VacationRequest v WHERE v.employee.id = :employeeId "
          + "AND (v.startDate <= :endOfYear AND v.endDate >= :startOfYear)")
  List<VacationRequest> findByEmployeeIdAndOverlappingVacationDays(
      @Param("employeeId") Long employeeId,
      @Param("startOfYear") LocalDateTime startDate,
      @Param("endOfYear") LocalDateTime endDate);
}
