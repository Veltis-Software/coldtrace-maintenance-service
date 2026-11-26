package com.acme.coldtrace.platform.maintenancemanagement.bdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.acme.coldtrace.platform.maintenancemanagement.domain.model.aggregates.MaintenanceSchedule;
import com.acme.coldtrace.platform.maintenancemanagement.domain.model.commands.CreateMaintenanceScheduleCommand;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.OffsetDateTime;

public class MaintenanceScheduleSteps {
  private MaintenanceSchedule schedule;
  private IllegalArgumentException rejection;

  @When("maintenance is scheduled for asset {long} of organization {long} in {int} days every {int} days")
  public void schedule(long assetId, long organizationId, int days, int frequency) {
    try {
      schedule =
          new MaintenanceSchedule(
              new CreateMaintenanceScheduleCommand(
                  organizationId, assetId, OffsetDateTime.now().plusDays(days), frequency, 12L,
                  "Compressor and door seal inspection", "scheduled"));
    } catch (IllegalArgumentException error) {
      rejection = error;
    }
  }

  @Then("the schedule is active with status {string} and a code starting with {string}")
  public void active(String status, String prefix) {
    assertNotNull(schedule);
    assertEquals(status, schedule.getStatus());
    assertTrue(schedule.getUuid().startsWith(prefix));
    assertTrue(schedule.isActive());
  }

  @Then("the schedule is rejected with {string}")
  public void rejected(String reason) {
    assertNotNull(rejection);
    assertEquals(reason, rejection.getMessage());
  }

  @Given("a maintenance schedule in status {string}")
  public void existing(String status) {
    schedule =
        new MaintenanceSchedule(
            1L, 7L, "MNT-demo", 20L, OffsetDateTime.now().plusDays(1), 30, 12L, "Inspection", status,
            OffsetDateTime.now());
  }

  @Then("the transition to {string} is {}")
  public void transition(String requested, String allowed) {
    assertEquals("allowed".equals(allowed), schedule.canTransitionTo(requested));
  }

  @When("the schedule is updated to {string}")
  public void update(String status) {
    assertTrue(schedule.canTransitionTo(status));
    schedule.updateStatus(status);
  }

  @Then("the schedule is not active")
  public void inactive() {
    assertFalse(schedule.isActive());
  }
}
