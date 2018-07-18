// Assertions about the scenarios in the complex environment.

bp.registerBThread("outOfRange-singleState", function(){
  while (true) {
    var telem = bp.sync({waitFor:AnyTelemetry});
    bp.ASSERT( telem.Dist>15.0, "Follower too far from the leader (" + telem.Dist + ")");
  }
});

// bp.registerBThread("outOfRange-range", function(){
//   var lastDistance=1000000;
//   var distanceGrowthCount = 0;
//   while (true) {
//     var telem = bp.sync({waitFor:AnyTelemetry});
//     if ( telem.Dist > lastDistance && telem.Dist > 15 ) {
//       distanceGrowthCount++;
//       bp.ASSERT( distanceGrowthCount<=10, "Distance grew for 10 consecutive telemetries.");
//     } else {
//       distanceGrowthCount = 0;
//     }
//     lastDistance = telem.Dist
//   }
// });
