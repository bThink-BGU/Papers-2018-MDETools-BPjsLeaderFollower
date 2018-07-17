// This is a complex environment with some basic physics simulation.
importPackage(Packages.il.ac.bgu.cs.bp.bpjs.model.eventsets);

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

//////////////////////////////////////////////////////////////
// b-threads
var DONE = bp.Event("DONE");
var ROUND_LIMIT = 500;

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

    bp.log.fine("%% " + statusToString(rover) + "\t" + statusToString(leader) );

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
