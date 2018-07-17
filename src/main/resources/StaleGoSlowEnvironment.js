// This is an environment where the leader starts side by side with the
// follower. It exposes a bug in ControllerLogic-flawed.js, where a
// stale GoSlowGradient event is selected.
importPackage(Packages.il.ac.bgu.cs.bp.bpjs.model.eventsets);

bp.registerBThread("leader", function(){
  bp.sync({waitFor:StaticEvents.START_CONTROL});
  while (true) {
    bp.sync({request:bp.Event("leader",{type:"move", amount:1})});
  }
});

var tooClose = 12.5;
var tooFar = 15;

bp.registerBThread("ProperGradientPower", function(){
  while (true) {
    var lastTelemetry = bp.sync({waitFor: AnyTelemetry});
    if (lastTelemetry.Dist < tooFar ) {
      var slowDownPower;
      if (lastTelemetry.Dist >= tooClose - (tooFar - tooClose)) {
          slowDownPower = Math.round(((lastTelemetry.Dist - tooClose) / (tooFar - tooClose)) * 100);
      } else {
          slowDownPower = -100;
      }
      var goSlowGradientEvent = bp.sync({waitFor:AnyGoSlowGradient});
      bp.ASSERT( goSlowGradientEvent.power === slowDownPower,
                 "Expected slowdown power is: " + slowDownPower + ", while the actual power was " + goSlowGradientEvent.power );
      lastTelemetry = bp.sync({waitFor: AnyTelemetry});
    }
  }
});

bp.registerBThread("world-status", function(){
  var leader = statusCreate(10, 10, 0);
  var rover = statusCreate(0, 0, 0);

  bp.sync({request:StaticEvents.START_CONTROL});

  bp.sync({request:makeTelemetry(rover, leader)});

  var done = false;
  var lastEvent;
  while ( !done ) {
    // leader acts
    lastEvent = bp.sync({waitFor:esLeader});
    if ( lastEvent.data.type==="rotate" ) {
      leader = statusRotate( leader, lastEvent.data.amount );
    } else if ( lastEvent.data.type==="move" ) {
      leader = statusMove( leader, lastEvent.data.amount );
    } else {
      bp.log.warn("Unknown leader event type: " + lastEvent.data.type);
    }

    // rover acts
    lastEvent = bp.sync({waitFor:esExternalRoverEvents});
    rover = parseExternalRoverEvent( rover, lastEvent );

    // TICK
    bp.sync({request:StaticEvents.TICK});

    bp.log.fine("%% " + statusToString(rover) + "\t" + statusToString(leader) );

    // telemetry
    bp.sync({request:makeTelemetry(rover, leader)});
  }
});
