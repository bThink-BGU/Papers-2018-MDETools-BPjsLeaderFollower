/**
 * Rover Control program.
 */

/* global bp Packages importPackage Telemetry StaticEvents*/
importPackage(Packages.il.ac.bgu.cs.bp.leaderfollower.events);

var AnyTelemetry = bp.EventSet("Telemetries", function (e) {
    return e instanceof Telemetry;
});

var FBWARD_EVENT_REGEX = /^(GoToTarget|GoSlowGradient)/
var esFBwardEvents = bp.EventSet("FBwardEvents", function (e) {
    return (e.name.match(FBWARD_EVENT_REGEX) !== null)
})

bp.registerBThread("Go", function () {
    while (true) {
        bp.sync({waitFor: AnyTelemetry});
        bp.sync({request: StaticEvents.GO_TO_TARGET});
    }
});

// Fixes rover orientation while blocking forward movement.
bp.registerBThread("SpinToTarget", function () {
    while (true) {
        var et = bp.sync({waitFor: AnyTelemetry});
        var degToTarget = compDegToTarget(et.LeadX, et.LeadY, et.RovX, et.RovY, et.Compass);
        while (Math.abs(degToTarget) > 4) {
            // must correct rover orientation
            if (degToTarget > 0) {
                bp.sync({request: StaticEvents.TURN_RIGHT, block: esFBwardEvents});
            } else {
                bp.sync({request: StaticEvents.TURN_LEFT, block: esFBwardEvents});
            }
            et = bp.sync({waitFor: AnyTelemetry, block: esFBwardEvents});
            degToTarget = compDegToTarget(et.LeadX, et.LeadY, et.RovX, et.RovY, et.Compass);
        }
    }
});

var tooClose = 12.5;
var tooFar = 15;

bp.registerBThread("NotTooClose", function () {
    while (true) {
        var lastTelemetry = bp.sync({waitFor: AnyTelemetry});
        while (lastTelemetry.Dist < tooFar) {
            if (lastTelemetry.Dist >= tooClose - (tooFar - tooClose)) {
                var slowDownPower = Math.round(((lastTelemetry.Dist - tooClose) / (tooFar - tooClose)) * 100);
                bp.sync({waitFor: [StaticEvents.TURN_RIGHT,StaticEvents.TURN_LEFT],request: GoSlowGradient(slowDownPower), block: StaticEvents.GO_TO_TARGET});
            } else {
                bp.sync({waitFor: [StaticEvents.TURN_RIGHT,StaticEvents.TURN_LEFT],request: GoSlowGradient(-100), block: StaticEvents.GO_TO_TARGET});
            }
            lastTelemetry = bp.sync({waitFor: AnyTelemetry, block: StaticEvents.GO_TO_TARGET});
        }
    }
});

function compDegToTarget(xL, yL, xR, yR, CompassDeg) {
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
