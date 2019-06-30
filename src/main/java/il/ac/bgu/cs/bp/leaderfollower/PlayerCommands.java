package il.ac.bgu.cs.bp.leaderfollower;

import java.io.IOException;

// format commands to unity string messages.
public class PlayerCommands {
  private String playerName;
  private SocketCommunicator player;
  private final String ip;
  private final int port;

  public PlayerCommands(String playerName, String simulationIp, int playerPort)
      throws IOException {
    this.playerName = playerName;
    this.ip = simulationIp;
    this.port = playerPort;
  }

  public void connectToServer() throws IOException {
    player = new SocketCommunicator();
    player.connectToServer(this.ip, this.port);
  }

  public void forward() {
    player.noReply(playerName + ",moveForward(100)");
    System.out.println(playerName + ",moveForward(100)");
  }

  public void parameterizedMove(Integer powerForward, Integer powerLeft, Integer spin)
      throws InterruptedException {
    // System.out.println("f: "+powerForward + ",r: "+powerLeft+",s: "+spin);
    if(powerForward != null) player.noReply(playerName + ",moveForward(" + powerForward + ")");
    if(powerLeft != null) player.noReply(playerName + ",moveRight(" + powerLeft + ")");
    if(spin != null) {
      player.noReply(playerName + ",spin(" + spin + ")");
      Thread.sleep(10);
      player.noReply(playerName + ",spin(0)");
    }
  }

  public void right() {
    player.noReply(playerName + ",moveRight(-100)");
    System.out.println(playerName + ",moveRight(-100)");
  }

  public void left() {
    player.noReply(playerName + ",moveRight(100)");
    System.out.println(playerName + ",moveRight(100)");
  }

  public void backward() {
    player.noReply(playerName + ",moveForward(-100)");
    System.out.println(playerName + ",moveForward(-100)");
  }

  public void spinR() {
    player.noReply(playerName + ",spin(100)");
    System.out.println(playerName + ",spin(100)");
  }

  public void spinL() {
    player.noReply(playerName + ",spin(-100)");
    System.out.println(playerName + ",spin(-100)");
  }

  public void suck() {
    player.noReply(playerName + ",setSuction(-100)");
    System.out.println(playerName + ",setSuction(-100)");
  }

  public void expel() {
    player.noReply(playerName + ",setSuction(100)");
    System.out.println(playerName + ",setSuction(100)");
  }

  public void stopMoving() {
    player.noReply(playerName + ",stop()");
    System.out.println(playerName + ",stop()");
  }

  public void stopSpinning() {
    player.noReply(playerName + ",spin(0)");
    System.out.println(playerName + ",spin(0)");
  }

  public GpsData getPlayerGps() {
    return new GpsData(player.send(this.playerName + ",GPS()"));
  }

  public GpsData getBallGps() {
    return new GpsData(player.send("ball,GPS()"));
  }

  public Double getPlayerCompass() {
    return extractData(player.send(this.playerName + ",getCompass()"));
  }

  private static Double extractData(String TheDistanceMessage) {
    String[] StringSplit;
    String StringofDistance;

    StringSplit = TheDistanceMessage.split(",");
    StringofDistance = StringSplit[1].subSequence(0, StringSplit[1].indexOf(";")).toString();
    return Double.parseDouble(StringofDistance);
  }

  public static class GpsData {
    public final Double x;
    public final Double y;
    public final Double z;

    public GpsData(double x, double z) {
      this(x, 0, z);
    }

    public GpsData(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    /**
     * Extracts The Data
     * 
     * @param theGpsMessage message text form the GPS.
     */
    private GpsData(String theGpsMessage) {
      String[] StringSplit;
      String Stringofx;
      String Stringofz;

      StringSplit = theGpsMessage.split(",");
      Stringofx = StringSplit[1];
      Stringofz = StringSplit[2].subSequence(0, StringSplit[2].indexOf(";")).toString();

      this.x = Double.parseDouble(Stringofx);
      this.y = Double.valueOf(0);
      this.z = Double.parseDouble(Stringofz);
    }

    @Override
    public String toString() {
      return this.x + "," + this.z;
    }
  }

  public void close() {
    player.close();
  }
}
