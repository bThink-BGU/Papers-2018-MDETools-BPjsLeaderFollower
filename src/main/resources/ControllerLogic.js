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
        et = bp.sync({waitFor: AnyTelemetry});
        DegToTarget = compDegToTarget(et.LeadX, et.LeadY, et.RovX, et.RovY, et.Compass);
        bp.log.info('Js-DegToTarget:' + DegToTarget);
        if (Math.abs(DegToTarget) > 4) {
            if (DegToTarget > 0) {
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
        bp.sync({waitFor:  StaticEvents.SPIN_DONE});
        bp.sync({request: StaticEvents.GO_TO_TARGET});
    }
});

bp.registerBThread("NotTooClose", function () {
    tooClose = 12.5;
    tooFar = 14.5;
    while (true) {
        et3 = bp.sync({waitFor: AnyTelemetry});
        while (et3.Dist < tooFar) {
            if (et3.Dist >= tooClose - (tooFar - tooClose)) {
                bp.sync({waitFor: StaticEvents.SPIN_DONE, block: StaticEvents.GO_TO_TARGET});
                slowDownPower = Math.round(((et3.Dist - tooClose) / (tooFar - tooClose)) * 100);
                bp.sync({request: GoSlowGradient(slowDownPower), block: StaticEvents.GO_TO_TARGET});
            } else {
                bp.sync({waitFor: StaticEvents.SPIN_DONE, block: StaticEvents.GO_TO_TARGET});
                bp.sync({request: GoSlowGradient(-100), block: StaticEvents.GO_TO_TARGET});
            }
            et3 = bp.sync({waitFor: AnyTelemetry, block: StaticEvents.GO_TO_TARGET});
        }
    }
});


function compDegToTarget (xL, yL, xR, yR, CompassDeg) {
    LRDeg = Math.atan2((yL - yR), (xL - xR));
    LRDeg = (LRDeg / Math.PI) * 180;
    DDeg = (90 - CompassDeg) - LRDeg;

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
