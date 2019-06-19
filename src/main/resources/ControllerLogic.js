/**
 * Rover Control program.
 */

/* global bp Packages importPackage Telemetry StaticEvents*/
importPackage(Packages.il.ac.bgu.cs.bp.leaderfollower.events);

var FBWARD_EVENT_REGEX = /^(MoveForward|MoveBackward|ParameterizedMoves)/
var esFBwardEvents = bp.EventSet("FBwardEvents", function (e) {
  return e.name.equals("MoveForward") || e.name.equals("MoveBackward") ||
    (e.name.equals("ParameterizedMoves") && e.powerX !== 0);
})

bp.registerBThread("MoveForward", function () {
  while (true) {
    bp.sync({ waitFor: AnyTelemetry });
    bp.sync({ request: StaticEvents.MOVE_FORWARD });
  }
});

// Fixes rover orientation while blocking forward movement.
bp.registerBThread("SpinToBall", function () {
  while (true) {
    var et = bp.sync({ waitFor: AnyTelemetry });
    var degToTarget = compDegToTarget(et.BallGps.x, et.BallGps.y, et.PlayerGps.x, et.PlayerGps.y, et.PlayerCompass);
    while (Math.abs(degToTarget) > 4) {
      // must correct rover orientation
      if (degToTarget > 0) {
        bp.sync({ request: StaticEvents.TURN_RIGHT, block: esFBwardEvents });
      } else {
        bp.sync({ request: StaticEvents.TURN_LEFT, block: esFBwardEvents });
      }
      et = bp.sync({ waitFor: AnyTelemetry, block: esFBwardEvents });
      var degToTarget = compDegToTarget(et.BallGps.x, et.BallGps.y, et.PlayerGps.x, et.PlayerGps.y, et.PlayerCompass);
    }
  }
});

var tooClose = 12.5;
var tooFar = 15;

bp.registerBThread("NotTooClose", function () {
  while (true) {
    var lastTelemetry = bp.sync({ waitFor: AnyTelemetry });
    while (lastTelemetry.Dist < tooFar) {
      if (lastTelemetry.Dist >= tooClose - (tooFar - tooClose)) {
        var slowDownPower = Math.round(((lastTelemetry.Dist - tooClose) / (tooFar - tooClose)) * 100);
        bp.sync({ waitFor: [StaticEvents.TURN_RIGHT, StaticEvents.TURN_LEFT], request: GoSlowGradient(slowDownPower), block: StaticEvents.GO_TO_TARGET });
      } else {
        bp.sync({ waitFor: [StaticEvents.TURN_RIGHT, StaticEvents.TURN_LEFT], request: GoSlowGradient(-100), block: StaticEvents.GO_TO_TARGET });
      }
      lastTelemetry = bp.sync({ waitFor: AnyTelemetry, block: StaticEvents.GO_TO_TARGET });
    }
  }
});

function compDegToTarget(xTarget, yTarget, xSource, ySource, sourceCompass) {
  var LRDeg = Math.atan2((yTarget - ySource), (xTarget - xSource));
  var LRDeg = (LRDeg / Math.PI) * 180;
  var DDeg = (90 - sourceCompass) - LRDeg;

  if (Math.abs(DDeg) >= 360) {
    if (DDeg > 0) {
      DDeg = DDeg - 360;
    } else {
      DDeg = DDeg + 360;
    }
  }
  if (Math.abs(DDeg) > 180) {
    if (DDeg > 180) {
      DDeg = DDeg - 360;
    }
    if (DDeg < (-180)) {
      DDeg = DDeg + 360;
    }
  }
  return DDeg;
}
