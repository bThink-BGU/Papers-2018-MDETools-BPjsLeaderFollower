/**
 * Rover Control program.
 */
importPackage(Packages.il.ac.bgu.cs.bp.leaderfollower.events);


var Telem = bp.EventSet("", function (e) {
    return e instanceof Telemetry;
});
//var tooClose = 12;
//var tooFar = 15;

bp.registerBThread("SpinToTarget", function () {
    while (true) {
        et = bp.sync({waitFor: [Telem]});
        DegToTarget = compDegToTarget(et.LeadX, et.LeadY, et.RovX, et.RovY, et.Compass);
        bp.log.info('Js-DegToTarget:' + DegToTarget);
        if (Math.abs(DegToTarget) > 4) {
            if (DegToTarget > 0) {
                    bp.sync({request: bp.Event("TurnRight")});
            } else {
                    bp.sync({request: bp.Event("TurnLeft")});          
            }
        } else {
            bp.sync({request: bp.Event("SpinDone")});
        }
    }
});

bp.registerBThread("GoToTarget", function () {
    while (true) {
        et2 = bp.sync({waitFor: [Telem]});
        bp.sync({waitFor: bp.Event("SpinDone")});
        bp.sync({request: bp.Event("GoToTarget")});
    }
});

bp.registerBThread("NotTooClose", function () {
    tooClose = 12.5;
    tooFar = 15;
    while (true) {
        et3 = bp.sync({waitFor: [Telem]});
        while (et3.Dist < tooFar) {
            if (et3.Dist >= tooClose - (tooFar - tooClose)) {
                bp.sync({waitFor: bp.Event("SpinDone"), block: bp.Event("GoToTarget")});
                slowDownPower = Math.round(((et3.Dist - tooClose) / (tooFar - tooClose)) * 100);
                bp.sync({request: GoSlowGradient(slowDownPower), block: bp.Event("GoToTarget")});
            } else {
                bp.sync({waitFor: bp.Event("SpinDone"), block: bp.Event("GoToTarget")});
                bp.sync({request: GoSlowGradient(-100), block: bp.Event("GoToTarget")});
            }
            et3 = bp.sync({waitFor: [Telem], block: bp.Event("GoToTarget")});
        }
    }
});


function compDegToTarget(xL, yL, xR, yR, CompassDeg) {
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

