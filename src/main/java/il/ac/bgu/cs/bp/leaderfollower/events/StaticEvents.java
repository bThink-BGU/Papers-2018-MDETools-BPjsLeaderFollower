package il.ac.bgu.cs.bp.leaderfollower.events;

import il.ac.bgu.cs.bp.bpjs.model.BEvent;

@SuppressWarnings({"serial"})
public class StaticEvents {
    public static BEvent Tick = new BEvent("Tick");
    public static BEvent StartControl = new BEvent("StartControl");
    public static BEvent TurnLeft = new BEvent("TurnLeft");
    public static BEvent TurnRight = new BEvent("TurnRight");
    public static BEvent ForwardTurnLeft = new BEvent("ForwardTurnLeft");
    public static BEvent ForwardTurnRight = new BEvent("ForwardTurnRight");
    public static BEvent Stop = new BEvent("Stop");
    public static BEvent BrakeOn = new BEvent("BrakeOn");
    public static BEvent BrakeOff = new BEvent("BrakeOff");
    public static BEvent GoSlow = new BEvent("GoSlow");
    public static BEvent SpinDone = new BEvent("SpinDone");
    public static BEvent Go2Target = new BEvent("GoToTarget");
    public static BEvent Test1 = new BEvent("Test1");
}
