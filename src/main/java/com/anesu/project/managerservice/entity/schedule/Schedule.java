package com.anesu.project.managerservice.entity.schedule;

import com.anesu.project.managerservice.entity.ScheduleStatus;
import com.anesu.project.managerservice.entity.manager.Manager;
import com.anesu.project.managerservice.entity.shift.ShiftEntry;
import com.anesu.project.managerservice.entity.vacation.VacationRequest;
import jakarta.persistence.*;
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

}
