importPackage(Packages.java.lang);
importPackage(Packages.il.ac.bgu.cs.bp.leaderfollower.events);
importPackage(Packages.il.ac.bgu.cs.bp.leaderfollower.schema);
importPackage(Packages.il.ac.bgu.cs.bp.bpjs.context);

var player;
var opponent;

var FBWARD_EVENT_REGEX = /^(Possesion|Timeout|scored|Done)/
var refereeEvents = bp.EventSet("RefereeEvents", function (e) {
  return e.name.match(FBWARD_EVENT_REGEX) !== null
});
var moveEvents = bp.EventSet("MoveEvents", function (e) {
  return e instanceof ParameterizedMove && (e.powerForward != null || e.powerLeft != null);
});
var anyParameterizedMove = bp.EventSet("AnyParameterizedMove", function (e) {
  return e instanceof ParameterizedMove;
});

//#region helper functions
function getPlayerToTargetData(telemetry, targetName) {
  if (targetName.equals("ball"))
    return telemetry.playerToBall;
  else if (targetName.equals("gate"))
    return telemetry.playerToGate;
}

function goToTargetRefactored(targetName, direction, interruptEvent) {
  while (true) {
    var t = bp.sync({ waitFor: Telemetry.ANY, interrupt: interruptEvent });
    // update the target name with any global updates
    targetName = CTX.getContextInstances("GoToTarget").get(0).target;
    var data = getPlayerToTargetData(t, targetName);
    var dx = data.dx;
    var dz = data.dz;
    var dxA = Math.abs(dx);
    var dzA = Math.abs(dz);
    var larger = dxA > dzA && dxA !== 0 ? dxA : dzA;
    var multiplier = 100 / larger;
    if (direction.equals("x")) {
      var powerForward = dx * multiplier;
      bp.sync({ waitFor: anyParameterizedMove, request: ParameterizedMove(powerForward, null, null), interrupt: interruptEvent });
    } else if (direction.equals("z")) {
      var powerLeft = dz * multiplier;
      bp.sync({ waitFor: anyParameterizedMove, request: ParameterizedMove(null, powerLeft, null), interrupt: interruptEvent });
    }
  }
}

function goToTargetGradient(targetName, direction, interruptEvent) {
  var tooClose = 3.5;
  var tooFar = 5;
  while (true) {
    var t = bp.sync({ waitFor: Telemetry.ANY, interrupt: interruptEvent });
    // update the target name with any global updates
    targetName = CTX.getContextInstances("GoToTarget").get(0).target;
    var data = getPlayerToTargetData(t, targetName);
    var d, dA;
    if (direction.equals("x")) {
      var d = data.dx;
    } else if (direction.equals("z")) {
      var d = data.dz;
    }
    dA = Math.abs(d);

    var power;
    var sign = d < 0 ? -1 : 1;

    if (dA > tooClose) {
      if (dA < tooFar) {
        power = Math.round(((dA - tooClose) / (tooFar - tooClose)) * 100);
      } else {
        power = -100;
      }
    } else {
      if (dA > (2 * tooClose - tooFar)) {
        power = Math.round(((dA - tooClose) / (tooFar - tooClose)) * 100);
      } else {
        power = -100;
      }
    }

    var powerForward = null;
    var powerLeft = null;
    if (direction.equals("x")) {
      powerForward = sign * power;
    } else if (direction.equals("z")) {
      powerLeft = sign * power;
    }
    bp.sync({ request: ParameterizedMove(powerForward, powerLeft, null), interrupt: interruptEvent });
  }
}
//#endregion

bp.registerBThread("HandleRefereeEvents", function () {
  while (true) {
    var e = bp.sync({ waitFor: refereeEvents });
    if (e.name.equals("Possesion")) {
      bp.sync({ request: CTX.UpdateEvent("UpdatePosession", { "posession": e.data[1] }) });
    } else if (e.name.equals("Timeout")) {
      bp.sync({ request: CTX.UpdateEvent("UpdateTimeout", { "timeout": e.data.counter }) });
    } else if (e.name.equals("scored")) {
      if (e.data[1].equals(player.name))
        bp.sync({ request: CTX.UpdateEvent("UpdateMyScore", { "score": Double(e.data[2]).intValue() }) });
      else
        bp.sync({ request: CTX.UpdateEvent("UpdateOpponentScore", { "score": Double(e.data[2]).intValue() }) });
    } else if (e.name.equals("Done")) {
      bp.sync({ request: CTX.UpdateEvent("MarkGameAsOver") });
    } else
      bp.log.ERROR("unknown message from referee");
  }
});

bp.registerBThread("InitData", function () {
  bp.sync({ request: CTX.InsertEvent(GoToTarget(), Referee(player.name)) });
});

CTX.subscribe("GameFlow", "Playing", function (r) {
  var endOfGameEvent = CTX.ContextEndedEvent("Playing", r);
  bp.sync({ waitFor: bp.Event("Start Control") });
  while (true) {
    bp.sync({ request: CTX.UpdateEvent("SetTarget", { "target": "ball" }), interrupt: endOfGameEvent });
    bp.sync({ waitFor: CTX.AnyNewContextEvent("IPossesTheBall"), interrupt: endOfGameEvent });
    bp.sync({ request: CTX.UpdateEvent("SetTarget", { "target": "gate" }), interrupt: endOfGameEvent });
    bp.sync({ waitFor: CTX.AnyNewContextEvent("BallIsFree"), interrupt: endOfGameEvent });
  }
});

CTX.subscribe("GoToTargetX", "GoToTarget", function (gt) {
  // goToTargetGradient(gt.target, "x", CTX.ContextEndedEvent("GoToTarget", gt));
  goToTargetRefactored(gt.target, "x", CTX.ContextEndedEvent("GoToTarget", gt));
});

CTX.subscribe("GoToTargetZ", "GoToTarget", function (gt) {
  goToTargetRefactored(gt.target, "z", CTX.ContextEndedEvent("GoToTarget", gt));
});

CTX.subscribe("SpinToTarget", "GoToTarget", function (gt) {
  const power = 100;
  while (true) {
    var t = bp.sync({ waitFor: Telemetry.ANY, interrupt: CTX.ContextEndedEvent("GoToTarget", gt) });
    // update the local context with any global updates
    gt = CTX.getContextInstances("GoToTarget").get(0);
    var data = getPlayerToTargetData(t, gt.target);
    var degree = data.degree;
    if (Math.abs(degree) > 6) {
      toBlock = moveEvents;
      // must correct orientation
      if (degree > 0) {
        bp.sync({ request: ParameterizedMove(null, null, power), block: moveEvents });
      } else {
        bp.sync({ request: ParameterizedMove(null, null, -power), block: moveEvents });
      }
    }
  }
});

CTX.subscribe("SuckBall", "GoToBall", function (gt) {
  bp.sync({ request: bp.Event("Suck") });
});

CTX.subscribe("ReleaseTheBallImmediatly", "TimeoutInASecond", function (gt) {
  bp.sync({ request: bp.Event("Expel") });
  bp.sync({ request: CTX.UpdateEvent("UpdatePosession", { "posession": "" }) });
});

CTX.subscribe("ReleaseTheBallWhenReady", "IPossesTheBall", function (gt) {
  var contextEndedEvent = CTX.ContextEndedEvent("IPossesTheBall", gt);
  while (true) {
    var t = bp.sync({ waitFor: Telemetry.ANY, interrupt: contextEndedEvent });
    var degree = getPlayerToTargetData(t, "gate").degree;
    if (Math.abs(degree) <= 10) {
      bp.sync({ request: bp.Event("Expel"), interrupt: contextEndedEvent });
    }
  }
});


/*


bp.registerBThread("NotTooClose", function () {
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
