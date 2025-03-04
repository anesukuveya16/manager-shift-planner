package com.anesu.project.managerservice.entity.shift;

import com.anesu.project.managerservice.entity.vacation.VacationRequest;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

  @Id private Long shiftId;
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
