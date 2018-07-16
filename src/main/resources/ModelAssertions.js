/**
 * This file contains requirement b-threads for the follower controller.
 */

// turn off specific JS warnings.
/* global bp importPackage Packages */
// Rhino import
importPackage(Packages.il.ac.bgu.cs.bp.leaderfollower.events);

var esVeryCloseTelems = bp.EventSet("telem-close", function(e){
  if ( e instanceof Telemetry ) {
    var dist = Trigo.distance({x:e.LeadX, y:e.LeadY}, {x:e.RovX, y:e.RovY});
    return (dist < 10);
  } else {
    return false;
  }
});

var esNotFacingTarget = bp.EventSet("not facing target", function(et){
  if ( et instanceof Telemetry ) {
    var degToTarget = compDegToTarget(et.LeadX, et.LeadY, et.RovX, et.RovY, et.Compass);
    return (degToTarget > 4);
  } else {
    return false;
  }
});

// too close
bp.registerBThread("REQ_too close", function(){
  bp.sync({waitFor:esVeryCloseTelems});
  var instruction = bp.sync({waitFor:esExternalRoverEvents});
  bp.ASSERT(instruction !== StaticEvents.GO_TO_TARGET, "Rover advanced while too close to leader." );
});

// not in direction (must spin)
bp.registerBThread("REQ_not facing", function(){
  bp.sync({waitFor:esNotFacingTarget});
  bp.log.info("NOT FACING TARGET");
  var instruction = bp.sync({waitFor:esExternalRoverEvents});
  bp.log.info("instruction: " + instruction.name);
  bp.ASSERT(instruction !== StaticEvents.GO_TO_TARGET, "Rover advanced while not facing the leader." );
});

// far and in direction -> go
