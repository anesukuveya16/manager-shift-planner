package com.anesu.project.managerservice.entity.shift;

import jakarta.persistence.*;
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

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long shiftId;

  private LocalDateTime shiftDate;
  private ShiftType shiftType;
  private Long workingHours;

  // insert the builder to add approved shift requests into the schedule

  public static ShiftEntry from(ShiftRequest approvedShiftRequest) {
    return builder()
        .shiftDate(approvedShiftRequest.getShiftDate())
        .shiftType(approvedShiftRequest.getShiftType())
        .workingHours(approvedShiftRequest.getShiftLengthInHours())
        .build();
  }
}
