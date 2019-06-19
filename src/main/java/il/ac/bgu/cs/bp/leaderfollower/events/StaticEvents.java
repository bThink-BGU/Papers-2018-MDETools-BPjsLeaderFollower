package il.ac.bgu.cs.bp.leaderfollower.events;

import il.ac.bgu.cs.bp.bpjs.model.BEvent;

@SuppressWarnings({"serial"})
public class StaticEvents {
    public static final BEvent TICK = new BEvent("Tick");
    public static final BEvent START_CONTROL = new BEvent("StartControl");
    public static final BEvent ORIENTATION_OK = new BEvent("OrientationOK");
    public static final BEvent TURN_LEFT = new BEvent("TurnLeft");
    public static final BEvent TURN_RIGHT = new BEvent("TurnRight");
    public static final BEvent STOP_TURNING = new BEvent("StopTurning");
    public static final BEvent FORWARD = new BEvent("Forward");
    public static final BEvent BACKWARD = new BEvent("Backward");
    public static final BEvent RIGHT = new BEvent("Right");
    public static final BEvent LEFT = new BEvent("Lef");
    public static final BEvent STOP_MOVEING = new BEvent("StopMoving");
    public static final BEvent SUCK = new BEvent("Suck");
    public static final BEvent EXPEL = new BEvent("Expel");
}
