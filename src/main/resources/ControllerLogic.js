/**
 * Rover Control program.
 */

/* global bp Packages importPackage Telemetry StaticEvents*/
importPackage(Packages.il.ac.bgu.cs.bp.leaderfollower.events);

var player;
var opponent;

/* var FBWARD_EVENT_REGEX = /^(MoveForward|MoveBackward|ParameterizedMoves)/
var esFBwardEvents = bp.EventSet("FBwardEvents", function (e) {
  return e.name.equals("MoveForward") || e.name.equals("MoveBackward") ||
    (e.name.equals("ParameterizedMoves") && e.powerX !== 0);
}) */

bp.registerBThread("MoveTowardsBallX", function () {
  while (true) {
    var t = bp.sync({ waitFor: Telemetry.ANY });
    var dx = t.playerToBall.dx;
    var dz = t.playerToBall.dz;
    var dxA = Math.abs(dx);
    var dzA = Math.abs(dz);
    var larger = dxA > dzA && dxA !== 0 ? dxA : dzA;
    var multiplier = 50 / larger;
    var powerForward = dx * multiplier;
    bp.sync({ request: ParameterizedMove(powerForward, null, null) });
  }
});

bp.registerBThread("MoveTowardsBallZ", function () {
  while (true) {
    var t = bp.sync({ waitFor: Telemetry.ANY });
    var dx = t.playerToBall.dx;
    var dz = t.playerToBall.dz;
    var dxA = Math.abs(dx);
    var dzA = Math.abs(dz);
    var larger = dxA > dzA  && dxA ? dxA : dzA;
    var multiplier = 50 / larger;
    var powerLeft = dz * multiplier;
    bp.sync({ request: ParameterizedMove(null, powerLeft, null) });
  }
});

bp.registerBThread("SpinToBall", function () {
  const power = 15;
  while (true) {
    var degToBall = bp.sync({ waitFor: Telemetry.ANY }).playerToBall.degree;
    if (Math.abs(degToBall) > 4) {
      // must correct orientation
      if (degToBall > 0) {
        bp.sync({ request: ParameterizedMove(null, null, power) },50);
      } else {
        bp.sync({ request: ParameterizedMove(null, null, -power) },50);
      }
    } else {
      bp.sync({ request: ParameterizedMove(null, null, 0) },50);
    }
  }
});

bp.registerBThread("SuckBall", function () {
  bp.sync({ waitFor: Telemetry.ANY });
  bp.sync({ request: bp.Event("Suck") });
});

var tooClose = 12.5;
var tooFar = 15;

/* bp.registerBThread("NotTooClose", function () {
  while (true) {
    var lastTelemetry = bp.sync({ waitFor: Telemetry.ANY });
    while (lastTelemetry.distancePlayerToBall < tooFar) {
      if (lastTelemetry.distancePlayerToBall >= tooClose - (tooFar - tooClose)) {
        var slowDownPower = Math.round(((lastTelemetry.Dist - tooClose) / (tooFar - tooClose)) * 100);
        bp.sync({ waitFor: [StaticEvents.TURN_RIGHT, StaticEvents.TURN_LEFT], request: ParameterizedMove(slowDownPower,0,0), block: StaticEvents.FORWARD });
      } else {
        bp.sync({ waitFor: [StaticEvents.TURN_RIGHT, StaticEvents.TURN_LEFT], request: ParameterizedMove(-100,0,0), block: StaticEvents.FORWARD });
      }
      lastTelemetry = bp.sync({ waitFor: Telemetry.ANY, block: StaticEvents.FORWARD });
    }
  }
}); */
