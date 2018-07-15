package il.ac.bgu.cs.bp.leaderfollower;

/**
 * 
 * @author Michal Pasternak
 */
public class DriveCommands {

    public String name;
    private final SocketCommunicator rover;
    private static final boolean PRINT_DBG = false;
    
    public DriveCommands(SocketCommunicator sc, String n) {
        rover = sc;
        name = n;
    }

    public void left() {
        rover.noReply(name + ",setLRPower(-50,50)");
        if ( PRINT_DBG ) System.out.println(name + ",setLRPower(-50,50)");
    }

    public void right() {
        rover.noReply(name + ",setLRPower(50,-50)");
        if ( PRINT_DBG ) System.out.println(name + ",setLRPower(50,-50)");
    }

    public void go() {
        rover.noReply(name + ",setLRPower(100,100)");
        if ( PRINT_DBG ) System.out.println(name + ",setLRPower(100,100)");
    }

    public void stop() {
        rover.noReply(name + ",setLRPower(0,0)");
        if ( PRINT_DBG ) System.out.println(name + ",setLRPower(0,0)");
    }

    public void reverse() {
        rover.noReply(name + ",setLRPower(-100,-100)");
        if ( PRINT_DBG ) System.out.println(name + ",setLRPower(-100,-100)");
    }

    public void brake(boolean on) {
        if ( on ) {
            rover.noReply(name + ",brake(100)");
            if ( PRINT_DBG ) System.out.println(name + ",brake(100)");
        } else {
            rover.noReply(name + ",brake(0)");
            if ( PRINT_DBG ) System.out.println(name + ",brake(0)");
        }
    }

    public void place() {
        rover.noReply(name + ",place()");
        if ( PRINT_DBG ) System.out.println(name + ",place()");
    }

    public void controlPower(int L, int R) {
        rover.noReply(name + ",setLRPower(" + L + "," + R + ")");
        if ( PRINT_DBG ) System.out.println(name + ",setLRPower(" + L + "," + R + ")");
    }
}
