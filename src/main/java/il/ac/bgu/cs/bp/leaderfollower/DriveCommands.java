package il.ac.bgu.cs.bp.leaderfollower;

// Author: Michal Pasternak
public class DriveCommands {

    public String name;
    private final SocketCommunicator rover;

    public DriveCommands(SocketCommunicator sc, String n) {
        rover = sc;
        name = n;
    }

    public void Left() {
        rover.noReply(name + ",setLRPower(-50,50)");
        System.out.println(name + ",setLRPower(-50,50)");
    }

    public void Right() {
        rover.noReply(name + ",setLRPower(50,-50)");
        System.out.println(name + ",setLRPower(50,-50)");
    }

    public void Go() {
        rover.noReply(name + ",setLRPower(100,100)");
        System.out.println(name + ",setLRPower(100,100)");
    }

    public void Stop() {
        rover.noReply(name + ",setLRPower(0,0)");
        System.out.println(name + ",setLRPower(0,0)");
    }

    public void Reverse() {
        rover.noReply(name + ",setLRPower(-100,-100)");
        System.out.println(name + ",setLRPower(-100,-100)");
    }

    public void Brake(boolean on) {
        if (on == true) {
            rover.noReply(name + ",brake(100)");
            System.out.println(name + ",brake(100)");
        } else {
            rover.noReply(name + ",brake(0)");
        }
        System.out.println(name + ",brake(0)");
    }

    public void Place() {
        rover.noReply(name + ",place()");
        System.out.println(name + ",place()");
    }

    public void ControlPower(int L, int R) {
        rover.noReply(name + ",setLRPower(" + L + "," + R + ")");
        System.out.println(name + ",setLRPower(" + L + "," + R + ")");
    }
}
