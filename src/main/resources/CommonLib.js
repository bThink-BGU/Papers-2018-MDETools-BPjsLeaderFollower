//// Contains common utility code.

////////////////////////////////////////////////////
// Event Sets
/**
 * Filters names of events that are emitted by the rover, and are
 * translated to instructions to the actuators.
 */
var EXTERNAL_ROVER_EVENT_NAME_FILTER = /^(GoSlow|Turn|Stop|GoToTarget)/

var esExternalRoverEvents = bp.EventSet("externalRoverEvents", function (e) {
  return (e.name.match(EXTERNAL_ROVER_EVENT_NAME_FILTER) !== null);
});

var AnyTelemetry = bp.EventSet("telemetries", function (e) {
  return e instanceof Telemetry;
});

var AnyGoSlowGradient = bp.EventSet("ParameterizedMoves", function (e) {
  return e instanceof ParameterizedMove;
});

var esLeader = bp.EventSet("opponentEvents", function (e) {
  return e.name == "opponent";
});

////
////////////////////////////////////////////////////

var Trigo = {
  // Degrees to radians.
  d2r: function (d) {
    return (d * Math.PI * 2) / 360;
  },

  distance: function (pA, pB) {
    var dx = Math.abs(pA.x - pB.x);
    var dz = Math.abs(pA.z - pB.z);
    return Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));
  }
}


///////////////////////////////////////////////////////////////
// Robot status library
/**
 * Generates a status of a robot in 2D space.
 * @param x X coordinate
 * @param z Z coordinate
 * @param a azimuth of robot heading, from North clockwise.
 */
function statusCreate(x, z, a) {
  return { x: x, z: z, azimuth: a };
}

function statusRotate(status, deg) {
  return statusCreate(status.x, status.z, status.azimuth + deg);
}

function statusMove(status, amt) {
  var newX = status.x + Math.sin(Trigo.d2r(status.azimuth)) * amt;
  var newZ = status.z + Math.cos(Trigo.d2r(status.azimuth)) * amt;
  return statusCreate(newX, newZ, status.azimuth);
}

function statusToString(status) {
  return status.x + "\t" + status.z + "\t" + status.azimuth;
}

function statusPrint(status) {
  print(statusToString(status));
}
//
////////////////////////////////////

function makeTelemetry(player, opponent) {
  return Telemetry(player, opponent, player.azimuth, Trigo.distance(player, opponent));
}

// Rotation amount in a single step (degrees);
var PLAYER_ROTATION_UNIT = 1;

// Length of movement of the rover in a single step, with power=100.
var PLAYER_MAX_STEP = 3;

function parseExternalRoverEvent(player, evt) {
  if (evt.name == StaticEvents.TURN_LEFT.name) {
    return statusRotate(player, -PLAYER_ROTATION_UNIT);
  } else if (evt.name == StaticEvents.TURN_RIGHT.name) {
    return statusRotate(player, PLAYER_ROTATION_UNIT);
  } else if (evt.name == StaticEvents.MOVE_FORWARD.name) {
    return statusMove(player, PLAYER_MAX_STEP);
  } else if (evt.name == StaticEvents.MOVE_BACKWORD.name) {
    return statusMove(player, -PLAYER_MAX_STEP);
  } else if (evt.name == StaticEvents.MOVE_RIGHT.name) {
    return statusMove(player, PLAYER_MAX_STEP);
  } else if (evt.name == StaticEvents.MOVE_LEFT.name) {
    return statusMove(player, PLAYER_MAX_STEP);
  } else if (evt.name.match(/^ParameterizedMoves/)) {
    var amountX = evt.powerX;
    var amountZ = evt.powerZ;
    var spint = evt.spin;
    return statusMove(player, (PLAYER_MAX_STEP * amount / 100));
  } else {
    bp.log.warn("Unknown external event: '" + evt.name + "'");
    return player;
  }
}
