package com.acme.coldtrace.platform.maintenancemanagement.interfaces.rest;

import com.acme.coldtrace.platform.maintenancemanagement.interfaces.acl.MaintenanceManagementContextFacade;
import com.acme.coldtrace.platform.maintenancemanagement.interfaces.acl.MaintenanceManagementContextFacade.*;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/** Read-only owner API; workload authentication is enforced by MaintenanceSecurity. */
@RestController
@RequestMapping("/internal/maintenance/organizations/{organizationId}")
public class InternalMaintenanceController {
  private final MaintenanceManagementContextFacade maintenance;
  public InternalMaintenanceController(MaintenanceManagementContextFacade maintenance) { this.maintenance = maintenance; }
  @GetMapping("/schedules")
  public List<MaintenanceScheduleSnapshot> schedules(@PathVariable Long organizationId, @RequestParam Long assetId) {
    return maintenance.fetchMaintenanceSchedulesByOrganizationIdAndAssetId(organizationId, assetId);
  }
  @GetMapping("/requests")
  public List<TechnicalServiceRequestSnapshot> requests(@PathVariable Long organizationId,
      @RequestParam(required = false) Long assetId, @RequestParam(required = false) Long incidentId) {
    if ((assetId == null) == (incidentId == null))
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide exactly one asset or incident identifier");
    return assetId == null
        ? maintenance.fetchTechnicalServiceRequestsByOrganizationIdAndIncidentId(organizationId, incidentId)
        : maintenance.fetchTechnicalServiceRequestsByOrganizationIdAndAssetId(organizationId, assetId);
  }
}
