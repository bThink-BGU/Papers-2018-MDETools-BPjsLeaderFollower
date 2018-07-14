/**
 * This file contains requirement b-threads for the follower controller.
 */

// turn off specific JS warnings.
/* global bp importPackage Packages */
// Rhino import
importPackage(Packages.il.ac.bgu.cs.bp.leaderfollower.events);

/**
 * Filters names of events that are emitted by the rover, and are
 * translated to instructions to the actuators.
 */
var EXTERNAL_ROVER_EVENT_NAME_FILTER = /^(GoSlow|Turn|Stop|GoToTarget)/

var esExternalRoverEvents = bp.EventSet("externalRoverEvents", function(e){
  return e.name.match(EXTERNAL_ROVER_EVENT_NAME_FILTER);
});

var esVeryCloseTelems = bp.EventSet("telem-close", function(e){
  if ( e instanceof Telemetry ) {
    var dist = distance({x:e.LeadX, y:e.LeadY}, {x:e.RovX, y:e.RovY});
    return (dist < 10);
  } else {
    return false;
  }
});

// too close
bp.registerBThread("When too close, don't go forward", function(){
  bp.sync({waitFor:esVeryCloseTelems});
  var instruction = bp.sync({waitFor:esExternalRoverEvents});
  bp.ASSERT(instruction !== StaticEvents.GoToTarget, "Rover advanced while too close to leader." );
});

// not in direction (must spin)

// far and in direction -> go

/**
 * Pythagoras distance between point a (pA) and point b (pB).
 */
function distance( pA, pB ) {
  var dx = Math.abs(pA.x-pB.x);
  var dy = Math.abs(pA.y-pB.y);
  return Math.sqrt( Math.pow(dx,2) + Math.pow(dy,2) );
}
