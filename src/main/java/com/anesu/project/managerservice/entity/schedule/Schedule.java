package com.anesu.project.managerservice.entity.schedule;

import com.anesu.project.managerservice.entity.ScheduleStatus;
import com.anesu.project.managerservice.entity.manager.Manager;
import com.anesu.project.managerservice.entity.shift.ShiftEntry;
import com.anesu.project.managerservice.entity.vacation.VacationRequest;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long employeeId;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private Long totalWorkingHours;

  private ScheduleStatus status;

  @ElementCollection List<ShiftEntry> shifts;

  @ManyToOne
  @JoinColumn(name = "approved_by_manager_id")
  private Manager approvedByManager;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "schedule_id")
  private List<VacationRequest> vacations;

  // TODO: implement the getShiftsInRange and getVacationsInRange public methods to allow for
  // encapsulation.

  public List<LocalDateTime> getShiftsInRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
    return shifts.stream()
        .filter(
            shift ->
                !shift.getShiftDate().isBefore(rangeStart)
                    && !shift.getShiftDate().isAfter(rangeEnd))
        .map(ShiftEntry::getShiftDate)
        .toList();
  }

  public List<LocalDate> getVacationsInRange(LocalDate rangeStart, LocalDate rangeEnd) {
    return vacations.stream()
        .filter(
            vacation ->
                vacation.getStartDate().isBefore(rangeEnd.plusDays(1))
                    && vacation.getEndDate().isAfter(rangeStart.minusDays(1)))
        .flatMap(vacation -> vacation.getStartDate().datesUntil(vacation.getEndDate().plusDays(1)))
        .toList();
  }
}
