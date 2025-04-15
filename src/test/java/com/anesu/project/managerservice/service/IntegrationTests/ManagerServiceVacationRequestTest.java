package com.anesu.project.managerservice.service.IntegrationTests;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.anesu.project.managerservice.controller.ManagerServiceRestEndpoints;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ManagerServiceVacationRequestTest {

  private static WireMockServer wireMockServer;

  @Autowired private TestRestTemplate restTemplate;

  @BeforeAll
  public static void setupWireMockServer() {
    wireMockServer = new WireMockServer(9091);
    wireMockServer.start();
    configureFor("localhost", 9091);
  }

  @AfterAll
  public static void stopWireMockServer() {
    wireMockServer.stop();
  }

  @Test
  void testApproveVacationRequest() {
    // Stub the GET request to retrieve vacation details using the endpoint constant
    String getVacationRequestUrl = "/employees/1/vacations";

    stubFor(
        get(urlEqualTo(getVacationRequestUrl))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withBody(
                        """
                    [
                        {
                          "id": 123,
                          "employeeId": 1,
                          "status": "PENDING",
                          "startDate": "2025-04-15",
                          "endDate": "2025-04-20"
                        }
                    ]
                """)));

    // Stub the PUT request to approve the vacation using the endpoint constant
    String approveVacationUrl =
        ManagerServiceRestEndpoints.APPROVE_VACATION_REQUEST.replace(
                "{vacationRequestId}", "123") // Replace with the vacation request ID
            + "?status=APPROVED"; // Add query parameter to approve the vacation

    stubFor(
        put(urlEqualTo(approveVacationUrl))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withBody(
                        """
                    {
                      "id": 123,
                      "employeeId": 1,
                      "status": "APPROVED",
                      "startDate": "2025-04-15",
                      "endDate": "2025-04-20"
                    }
                """)));

    // Call your actual service (ManagerService) and test the approval logic
    String url = "http://localhost:9091" + approveVacationUrl; // Full URL for the PUT request
    ResponseEntity<String> response =
        restTemplate.exchange(url, HttpMethod.PUT, null, String.class);

    // Assert the response
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().contains("APPROVED"));
  }
}
