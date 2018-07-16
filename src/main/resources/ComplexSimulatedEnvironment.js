// This is a complex environment with some basic physics simulation.
importPackage(Packages.il.ac.bgu.cs.bp.bpjs.model.eventsets);

bp.log.setLevel("Fine");

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

//////////////////////////////////////////////////////////////
// tests
function sampleRun( stages ) {
  var cur = statusCreate(100,100,0);
  var rot = 360/stages;
  for ( var i=0; i<stages; i++ ) {
    statusPrint(cur);
    cur = statusMove(statusRotate(cur, rot), 10);
  }
}

function makeTelemetry(rover, leader) {
  return Telemetry(rover.x, rover.y, leader.x, leader.y, rover.azimuth, Trigo.distance(rover,leader));
}

//////////////////////////////////////////////////////////////
// b-threads
var DONE = bp.Event("DONE");
var ROUND_LIMIT = 100;
var esLeader = bp.EventSet("leaderEvents", function(e){
  return e.name=="leader";
});

bp.registerBThread("length-limit", function(){
  for (var i=0; i<ROUND_LIMIT; i++ ) {
    bp.sync({waitFor:StaticEvents.TICK});
  }
  bp.sync({request:DONE});
});

// Leader doing a random walk (enter political joke here)
bp.registerBThread("leader-acts", function(){
  var possibleLeaderMoves = [
    bp.Event("leader",{type:"move", amount:5}),
    bp.Event("leader",{type:"move", amount:4}),
    bp.Event("leader",{type:"move", amount:3}),
    bp.Event("leader",{type:"move", amount:2}),
    bp.Event("leader",{type:"move", amount:1}),
    bp.Event("leader",{type:"move", amount:0}),
    bp.Event("leader",{type:"rotate", amount:-5}),
    bp.Event("leader",{type:"rotate", amount:-3}),
    bp.Event("leader",{type:"rotate", amount:-1}),
    bp.Event("leader",{type:"rotate", amount:1}),
    bp.Event("leader",{type:"rotate", amount:3}),
    bp.Event("leader",{type:"rotate", amount:5})
  ];
  bp.sync({waitFor:StaticEvents.START_CONTROL});
  while ( true ) {
    bp.sync({request:possibleLeaderMoves, interrupt:DONE});
  }
})

// This b-thread maintains a picture of the world.
bp.registerBThread("world-status", function(){
  var leader = statusCreate(1, 15, 0);
  var rover = statusCreate(1, 1, 0);

  bp.sync({waitFor:StaticEvents.START_CONTROL});

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

    bp.log.info("%% " + statusToString(rover) + "\t" + statusToString(leader) );

    // telemetry
    bp.sync({request:makeTelemetry(rover, leader)});
  }
});

bp.registerBThread("regulator", function(){
  bp.sync({request:StaticEvents.START_CONTROL});
  while (true) {
    bp.sync({
      waitFor:AnyTelemetry,
      block:ComposableEventSet.anyOf(esExternalRoverEvents, esLeader, StaticEvents.TICK),
      interrupt:DONE});
    bp.sync({
      waitFor:esLeader,
      block:ComposableEventSet.anyOf(esExternalRoverEvents, AnyTelemetry, StaticEvents.TICK),
      interrupt:DONE});
    bp.sync({
      waitFor:esExternalRoverEvents,
      block:ComposableEventSet.anyOf(esLeader, AnyTelemetry, StaticEvents.TICK),
      interrupt:DONE});
    bp.sync({
      waitFor:StaticEvents.TICK,
      block:ComposableEventSet.anyOf(esLeader, AnyTelemetry, esExternalRoverEvents),
      interrupt:DONE});
  }
});

// Rotation amount in a single step (degrees);
var ROVER_ROTATION_UNIT = 1;

// Length of movement of the rover in a single step, with power=100.
var ROVER_MAX_STEP = 6;

function parseExternalRoverEvent( rover, evt ) {
  bp.log.info("Rover event:" + evt );
  switch (evt.name) {
    case StaticEvents.TURN_LEFT.name:
      return statusRotate(rover, -ROVER_ROTATION_UNIT);
      break;
    case StaticEvents.TURN_RIGHT.name:
      return statusRotate(rover, ROVER_ROTATION_UNIT);
      break;
    case StaticEvents.GO_TO_TARGET.name:
      return statusMove(rover, ROVER_MAX_STEP);
      break;
    default:
      if ( evt.name.match(/^GoSlowGradient/) ) {
        var amount = evt.power;
        bp.log.info(" ~ Move amount: " + amount);
        return statusMove(rover, (ROVER_MAX_STEP*amount/100));
      } else {
        bp.warn.log("Unknown external event: " + evt.name);
      }
  }
}
