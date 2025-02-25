package com.anesu.project.managerservice.entity.vacation;

import com.anesu.project.managerservice.entity.manager.Manager;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class VacationRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_id", nullable = false)
  private Long employeeId;

  private Long officeLocationId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(Long employeeId) {
    this.employeeId = employeeId;
  }

  public Long getOfficeLocationId() {
    return officeLocationId;
  }

  public void setOfficeLocationId(Long officeLocationId) {
    this.officeLocationId = officeLocationId;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public VacationRequestStatus getStatus() {
    return status;
  }

  public void setStatus(VacationRequestStatus status) {
    this.status = status;
  }

  public String getRejectionReason() {
    return rejectionReason;
  }

  public void setRejectionReason(String rejectionReason) {
    this.rejectionReason = rejectionReason;
  }

  public Long getApprovedBy() {
    return approvedBy;
  }

  public void setApprovedBy(Long approvedBy) {
    this.approvedBy = approvedBy;
  }

  public Manager getApprovedByManager() {
    return approvedByManager;
  }

  public void setApprovedByManager(Manager approvedByManager) {
    this.approvedByManager = approvedByManager;
  }

  private LocalDate startDate;
  private LocalDate endDate;

  @Enumerated(EnumType.STRING)
  private VacationRequestStatus status;

  private String rejectionReason;
  private Long approvedBy; // manager ID who approved the request.

  @ManyToOne
  @JoinColumn(name = "approved_by_manager_id")
  private Manager approvedByManager;
}
