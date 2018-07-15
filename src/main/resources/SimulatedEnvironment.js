/**
 * This file contains simulated environment for the follower controller.
 */

// turn off specific JS warnings.
/* global bp importPackage Packages */
/* global esExternalRoverEvents */  // defined in ModelAssertions.js

// Rhino import
importPackage(Packages.il.ac.bgu.cs.bp.leaderfollower.events);

// Actual code starts now

bp.registerBThread("environment", function(){
  bp.sync({request:StaticEvents.START_CONTROL});

  bp.sync({request:StaticEvents.TICK});
  var tooClose = new Telemetry(10, 10, 10, 12, 0, 2);
  bp.sync({request:tooClose});

  bp.sync({request:StaticEvents.TICK});
  var inRangeNotInDirection = new Telemetry(10, 10, 23, 10, 0, 13);
  bp.sync({request:inRangeNotInDirection});

  bp.sync({request:StaticEvents.TICK});
  var inRangeInDirection = new Telemetry(10, 10, 10, 23, 0, 13);
  bp.sync({request:inRangeInDirection});

  bp.sync({request:StaticEvents.TICK});

});

bp.registerBThread("SuperStepper", function(){
  while ( true ) {
    bp.sync({waitFor:AnyTelemetry});
    bp.sync({waitFor:esExternalRoverEvents, block:StaticEvents.TICK});
  }
})
