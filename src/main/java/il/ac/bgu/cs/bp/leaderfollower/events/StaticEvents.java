package il.ac.bgu.cs.bp.leaderfollower.events;

import il.ac.bgu.cs.bp.bpjs.model.BEvent;

@SuppressWarnings({"serial"})
public class StaticEvents {
    public static final BEvent TICK = new BEvent("Tick");
    public static final BEvent START_CONTROL = new BEvent("StartControl");
    public static final BEvent TURN_LEFT = new BEvent("TurnLeft");
    public static final BEvent TURN_RIGHT = new BEvent("TurnRight");
//    public static final BEvent ForwardTurnLeft = new BEvent("ForwardTurnLeft");
//    public static final BEvent ForwardTurnRight = new BEvent("ForwardTurnRight");
//    public static final BEvent Stop = new BEvent("Stop");
    public static final BEvent BrakeOn = new BEvent("BrakeOn");
    public static final BEvent SPIN_DONE = new BEvent("SpinDone");
    public static final BEvent GO_TO_TARGET = new BEvent("GoToTarget");
}
