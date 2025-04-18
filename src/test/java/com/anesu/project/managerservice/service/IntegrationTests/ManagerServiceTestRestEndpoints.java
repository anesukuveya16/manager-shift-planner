package com.anesu.project.managerservice.service.IntegrationTests;

public class ManagerServiceTestRestEndpoints {
  private static final String MANAGER_BASE = "/api/manager";

  // Schedule Endpoints
  public static final String CREATE_SCHEDULE = MANAGER_BASE + "/schedules";
  public static final String UPDATE_SCHEDULE = MANAGER_BASE + "/schedules/{scheduleId}";
  public static final String GET_SCHEDULE_BY_ID = MANAGER_BASE + "/schedules/{scheduleId}";
  public static final String GET_SCHEDULES_IN_RANGE =
      MANAGER_BASE + "/schedules/{scheduleId}/range";
  public static final String DELETE_SCHEDULE = MANAGER_BASE + "/schedules/{scheduleId}";

  // Shift Request Endpoints
  public static final String CREATE_SHIFT_REQUEST = MANAGER_BASE + "/employees/{employeeId}/shifts";
  public static final String APPROVE_SHIFT_REQUEST =
      MANAGER_BASE + "/employees/{employeeId}/shifts/{shiftRequestId}/approve";
  public static final String DECLINE_SHIFT_REQUEST =
      MANAGER_BASE + "/shifts/{shiftRequestId}/decline";
  public static final String GET_SHIFT_REQUEST_BY_EMPLOYEE_ID =
      MANAGER_BASE + "/employees/{employeeId}/shifts";
  public static final String GET_SHIFT_REQUESTS_IN_RANGE = MANAGER_BASE + "/shifts/range";

  // Vacation Request Endpoints
  public static final String APPROVE_VACATION_REQUEST =
      MANAGER_BASE + "/vacations/{vacationRequestId}/approve";
  public static final String DECLINE_VACATION_REQUEST =
      MANAGER_BASE + "/vacations/{vacationRequestId}/decline";
  public static final String GET_VACATIONS_BY_EMPLOYEE_ID =
      MANAGER_BASE + "/employees/{employeeId}/vacations";
  public static final String GET_EMPLOYEE_VACATIONS_IN_RANGE =
      MANAGER_BASE + "/employees/{employeeId}/vacations/range";
  public static final String GET_TEAM_CALENDAR =
      MANAGER_BASE + "/offices/{officeLocationId}/vacations";

  private ManagerServiceTestRestEndpoints() {
    // Prevent instantiation
  }
}
