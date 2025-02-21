package com.anesu.project.managerservice.entity.shift;

import com.anesu.project.managerservice.entity.manager.Manager;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ShiftRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long employeeId;
  private LocalDateTime shiftDate;

  @Enumerated(EnumType.STRING)
  private ShiftRequestStatus status;

  private String rejectionReason;

  private Long shiftLengthInHours;

  private ShiftType shiftType;

  private Long approvedBy;

  @ManyToOne
  @JoinColumn(name = "approved_by_manager_id")
  private Manager approvedByManager;
}
