package com.anesu.project.managerservice.entity.schedule;

import com.anesu.project.managerservice.entity.ScheduleStatus;
import com.anesu.project.managerservice.entity.manager.Manager;
import com.anesu.project.managerservice.entity.shift.ShiftEntry;
import com.anesu.project.managerservice.entity.vacation.VacationRequest;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
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
  private String rejectionReason;

  @ElementCollection List<ShiftEntry> shifts;

  @ManyToOne
  @JoinColumn(name = "approved_by_manager_id")
  private Manager approvedByManager;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "schedule_id")
  private List<VacationRequest> vacations;

  public List<LocalDateTime> getShiftsInRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
    return shifts.stream()
        .filter(
            shift ->
                !shift.getShiftDate().isBefore(rangeStart)
                    && !shift.getShiftDate().isAfter(rangeEnd))
        .map(ShiftEntry::getShiftDate)
        .toList();
  }

  public List<LocalDateTime> getVacationsInRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
    return vacations.stream()
        .filter(
            vacation ->
                vacation
                        .getStartDate()
                        .isBefore(
                            rangeEnd.plusDays(
                                1)) // Check if vacation start date is before the rangeEnd
                    && vacation
                        .getEndDate()
                        .isAfter(
                            rangeStart.minusDays(
                                1))) // Check if vacation end date is after the rangeStart
        .flatMap(
            vacation -> {
              LocalDateTime current = vacation.getStartDate();
              LocalDateTime end = vacation.getEndDate().plusDays(1); // Include the end date
              return Stream.iterate(current, date -> date.plusDays(1)) // Increment by 1 day
                  .limit(
                      ChronoUnit.DAYS.between(current, end)) // Limit to days between start and end
                  .filter(
                      date ->
                          !date.isBefore(rangeStart)
                              && !date.isAfter(
                                  rangeEnd)); // Ensure it is within the specified range
            })
        .collect(Collectors.toList());
  }
}
