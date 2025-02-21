package com.anesu.project.managerservice.entity.shift;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

import lombok.*;

@Entity
@Builder
@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ShiftEntry {
  private Long shiftId;
  private LocalDateTime shiftDate;
  private ShiftType shiftType;
  private Long workingHours;

  // insert the builder to add approved shift requests into the schedule
}
