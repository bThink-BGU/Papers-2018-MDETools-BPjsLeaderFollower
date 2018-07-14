/**
 * Rover Control program.
 */

/* global bp Packages importPackage Telemetry StaticEvents*/
importPackage(Packages.il.ac.bgu.cs.bp.leaderfollower.events);


var AnyTelemetry = bp.EventSet("", function (e) {
    return e instanceof Telemetry;
});

bp.registerBThread("SpinToTarget", function () {
    while (true) {
        var et = bp.sync({waitFor: AnyTelemetry});
        var degToTarget = compDegToTarget(et.LeadX, et.LeadY, et.RovX, et.RovY, et.Compass);
        bp.log.info('Js-degToTarget:' + degToTarget);
        if (Math.abs(degToTarget) > 4) {
            if (degToTarget > 0) {
                    bp.sync({request: StaticEvents.TURN_RIGHT});
            } else {
                    bp.sync({request: StaticEvents.TURN_LEFT});
            }
        } else {
            bp.sync({request: StaticEvents.SPIN_DONE});
        }
    }
});

bp.registerBThread("GoToTarget", function () {
    while (true) {
        bp.sync({waitFor: AnyTelemetry});
        bp.sync({waitFor: StaticEvents.SPIN_DONE});
        bp.sync({request: StaticEvents.GO_TO_TARGET});
    }
});

var tooClose = 12.5;
var tooFar = 14.5;

// bp.registerBThread("NotTooClose", function () {
//     while (true) {
//         var et3 = bp.sync({waitFor: AnyTelemetry});
//         while (et3.Dist < tooFar) {
//             if (et3.Dist >= tooClose - (tooFar - tooClose)) {
//                 bp.sync({waitFor: StaticEvents.SPIN_DONE, block: StaticEvents.GO_TO_TARGET});
//                 slowDownPower = Math.round(((et3.Dist - tooClose) / (tooFar - tooClose)) * 100);
//                 bp.sync({request: GoSlowGradient(slowDownPower), block: StaticEvents.GO_TO_TARGET});
//             } else {
//                 bp.sync({waitFor: StaticEvents.SPIN_DONE, block: StaticEvents.GO_TO_TARGET});
//                 bp.sync({request: GoSlowGradient(-100), block: StaticEvents.GO_TO_TARGET});
//             }
//             et3 = bp.sync({waitFor: AnyTelemetry, block: StaticEvents.GO_TO_TARGET});
//         }
//     }
// });


function compDegToTarget (xL, yL, xR, yR, CompassDeg) {
    var LRDeg = Math.atan2((yL - yR), (xL - xR));
    var LRDeg = (LRDeg / Math.PI) * 180;
    var DDeg = (90 - CompassDeg) - LRDeg;

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
