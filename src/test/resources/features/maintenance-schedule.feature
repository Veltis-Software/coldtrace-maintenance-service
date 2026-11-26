@maintenance
Feature: Preventive maintenance of refrigerated assets
  As an operations manager
  I want to schedule and follow maintenance per asset
  So that technical interventions can be related to the thermal behavior of the equipment

  @US48
  Scenario: The manager schedules preventive maintenance for an asset
    When maintenance is scheduled for asset 20 of organization 7 in 3 days every 30 days
    Then the schedule is active with status "scheduled" and a code starting with "MNT-"

  @US48
  Scenario Outline: Invalid schedules are rejected before they are stored
    When maintenance is scheduled for asset <asset> of organization 7 in <days> days every 30 days
    Then the schedule is rejected with "<reason>"

    Examples:
      | asset | days | reason                                                       |
      | 0     | 3    | maintenance-management.maintenance-schedule.error.assetId.invalid      |
      | 20    | -2   | maintenance-management.maintenance-schedule.error.scheduledDate.invalid |

  @US18 @US49
  Scenario Outline: A schedule follows its allowed status transitions
    Given a maintenance schedule in status "<current>"
    Then the transition to "<requested>" is <allowed>

    Examples:
      | current     | requested   | allowed     |
      | scheduled   | in_progress | allowed     |
      | in_progress | completed   | allowed     |
      | scheduled   | canceled    | allowed     |
      | completed   | in_progress | not allowed |
      | canceled    | scheduled   | not allowed |
      | in_progress | scheduled   | not allowed |

  @US18
  Scenario: A completed maintenance is no longer active
    Given a maintenance schedule in status "in_progress"
    When the schedule is updated to "completed"
    Then the schedule is not active
