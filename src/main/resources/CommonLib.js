//// Contains common utility code.

////////////////////////////////////////////////////
// Event Sets
/**
 * Filters names of events that are emitted by the rover, and are
 * translated to instructions to the actuators.
 */
var EXTERNAL_ROVER_EVENT_NAME_FILTER = /^(GoSlow|Turn|Stop|GoToTarget)/

var esExternalRoverEvents = bp.EventSet("externalRoverEvents", function(e){
  return (e.name.match(EXTERNAL_ROVER_EVENT_NAME_FILTER) !== null);
});

var AnyTelemetry = bp.EventSet("telemetries", function (e) {
    return e instanceof Telemetry;
});

var AnyGoSlowGradient = bp.EventSet("goSlowGradients", function (e) {
    return e instanceof GoSlowGradient;
});

var esLeader = bp.EventSet("leaderEvents", function(e){
  return e.name=="leader";
});

////
////////////////////////////////////////////////////

var Trigo = {
  // Degrees to radians.
  d2r:function( d ){
     return (d*Math.PI*2)/360;
  },

  distance:function(pA, pB) {
    var dx = Math.abs(pA.x-pB.x);
    var dy = Math.abs(pA.y-pB.y);
    return Math.sqrt( Math.pow(dx,2) + Math.pow(dy,2) );
  }
}


///////////////////////////////////////////////////////////////
// Robot status library
/**
 * Generates a status of a robot in 2D space.
 * @param x X coordinate
 * @param y Y coordinate
 * @param a azimuth of robot heading, from North clockwise.
 */
function statusCreate(x, y, a) {
  return {x:x, y:y, azimuth:a};
}

function statusRotate( status, deg ) {
  return statusCreate(status.x, status.y, status.azimuth + deg);
}

function statusMove(status, amt) {
  var newX = status.x + Math.sin(Trigo.d2r(status.azimuth))*amt;
  var newY = status.y + Math.cos(Trigo.d2r(status.azimuth))*amt;
  return statusCreate(newX, newY, status.azimuth);
}

function statusToString( status ) {
  return status.x + "\t" + status.y + "\t" + status.azimuth;
}

function statusPrint( status ){
  print(statusToString(status));
}
//
////////////////////////////////////

function makeTelemetry(follower, leader) {
  return Telemetry(follower.x, follower.y, leader.x, leader.y, follower.azimuth, Trigo.distance(follower,leader));
}


// Rotation amount in a single step (degrees);
var ROVER_ROTATION_UNIT = 1;

// Length of movement of the rover in a single step, with power=100.
var ROVER_MAX_STEP = 3;

function parseExternalRoverEvent( rover, evt ) {
  if ( evt.name == StaticEvents.TURN_LEFT.name ) {
    return statusRotate(rover, -ROVER_ROTATION_UNIT);
  } else if ( evt.name == StaticEvents.TURN_RIGHT.name ) {
    return statusRotate(rover, ROVER_ROTATION_UNIT);
  } else if ( evt.name == StaticEvents.GO_TO_TARGET.name ) {
    return statusMove(rover, ROVER_MAX_STEP);
  } else {
    if ( evt.name.match(/^GoSlowGradient/) ) {
      var amount = evt.power;
      return statusMove(rover, (ROVER_MAX_STEP*amount/100));
    } else if ( evt.name == "GoToTarget" ) {
      return statusMove(rover, ROVER_MAX_STEP);
    } else {
      bp.log.warn("Unknown external event: '" + evt.name + "'");
      return rover;
    }
  }
}
