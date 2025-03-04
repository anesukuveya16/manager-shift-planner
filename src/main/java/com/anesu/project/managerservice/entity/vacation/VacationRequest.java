package com.anesu.project.managerservice.entity.vacation;

import com.anesu.project.managerservice.entity.manager.Manager;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class VacationRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_id", nullable = false)
  private Long employeeId;

  private Long officeLocationId;
  private LocalDateTime startDate;
  private LocalDateTime endDate;

  @Enumerated(EnumType.STRING)
  private VacationRequestStatus status;

  private String rejectionReason;

  @ManyToOne
  @JoinColumn(name = "approved_by_manager_id")
  private Manager approvedByManager;

}
