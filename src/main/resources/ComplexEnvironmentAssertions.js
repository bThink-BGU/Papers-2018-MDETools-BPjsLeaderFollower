// Assertions about the scenarios in the complex environment.

bp.registerBThread("outOfRange-singleState", function(){
  while (true) {
    var telem = bp.sync({waitFor:AnyTelemetry});
    bp.ASSERT( telem.Dist<15.0, "Follower too far from the leader (" + telem.Dist + ")");
  }
});

bp.registerBThread("outOfRange-range", function(){
  while (true) {
    var telem = bp.sync({waitFor:AnyTelemetry});
    bp.ASSERT( telem.Dist<15.0, "Follower too far from the leader (" + telem.Dist + ")");
  }
});
