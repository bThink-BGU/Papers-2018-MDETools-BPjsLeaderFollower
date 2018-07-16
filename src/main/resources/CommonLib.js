//// Contains common utility code.

/**
 * Filters names of events that are emitted by the rover, and are
 * translated to instructions to the actuators.
 */
var EXTERNAL_ROVER_EVENT_NAME_FILTER = /^(GoSlow|Turn|Stop|GoToTarget)/

var esExternalRoverEvents = bp.EventSet("externalRoverEvents", function(e){
  return (e.name.match(EXTERNAL_ROVER_EVENT_NAME_FILTER) !== null);
});

var AnyTelemetry = bp.EventSet("", function (e) {
    return e instanceof Telemetry;
});

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
