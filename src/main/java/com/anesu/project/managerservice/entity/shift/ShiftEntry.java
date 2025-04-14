package com.anesu.project.managerservice.entity.shift;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShiftEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long shiftId;

  private LocalDateTime shiftDate;
  private ShiftType shiftType;
  private Long workingHours;

  public static ShiftEntry fromApprovedShiftEntry(ShiftRequest approvedShiftRequest) {
    return ShiftEntry.builder()
        .shiftDate(approvedShiftRequest.getShiftDate())
        .shiftType(approvedShiftRequest.getShiftType())
        .workingHours(approvedShiftRequest.getShiftLengthInHours())
        .build();
  }
}
