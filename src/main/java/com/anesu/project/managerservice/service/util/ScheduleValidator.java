package com.anesu.project.managerservice.service.util;

import com.anesu.project.managerservice.entity.schedule.Schedule;
import com.anesu.project.managerservice.entity.shift.ShiftEntry;
import com.anesu.project.managerservice.service.exception.InvalidScheduleException;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ScheduleValidator {

  public void validateShiftApproval(Schedule schedule, ShiftEntry newShift) {
    if (schedule == null) {
      throw new InvalidScheduleException("Schedule not found for employee with ID");
    }

    // Check for overlapping shifts
    if (hasOverlappingShift(schedule.getShifts(), newShift)) {
      throw new InvalidScheduleException("Shift conflicts with an existing approved shift.");
    }
  }

  public boolean hasOverlappingShift(List<ShiftEntry> existingShifts, ShiftEntry newShift) {
    for (ShiftEntry shift : existingShifts) {
      if (isOverlapping(shift, newShift)) {
        return true;
      }
    }
    return false;
  }

  public boolean isOverlapping(ShiftEntry shift1, ShiftEntry shift2) {
    return !(shift1.getShiftDate().isBefore(shift2.getShiftDate())
        || shift1.getShiftDate().isAfter(shift2.getShiftDate()));
  }
}
