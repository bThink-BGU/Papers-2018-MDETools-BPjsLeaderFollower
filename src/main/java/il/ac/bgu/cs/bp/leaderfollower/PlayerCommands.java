package il.ac.bgu.cs.bp.leaderfollower;

import java.io.IOException;

// format commands to unity string messages.
public class PlayerCommands {
  private String playerName;
  private SocketCommunicator player;
  private final String ip;
  private final int port;
  private boolean moving = false;

  public PlayerCommands(String playerName, String simulationIp, int playerPort) throws IOException {
    this.playerName = playerName;
    this.ip = simulationIp;
    this.port = playerPort;
  }

  public void connectToServer() throws IOException {
    player = new SocketCommunicator();
    player.connectToServer(this.ip, this.port);
  }

  public void forward(int power, int sleep) throws InterruptedException {
    player.noReply(playerName + ",moveForward(" + power + ")");
    System.out.println(playerName + ",moveForward(" + power + ")");
    moving = power != 0;
    Thread.sleep(sleep);
  }

  public void parameterizedMove(Integer powerForward, Integer powerLeft, Integer spin, int sleep)
      throws InterruptedException {
    if (powerForward != null) {
      this.forward(powerForward, sleep);
    }
    if (powerLeft != null) {
      this.left(powerLeft, sleep);
    }
    if (spin != null) {
      this.stopMoving(sleep);
      this.spinR(spin, sleep);
      Thread.sleep(sleep);
      this.spinR(0, sleep);
    }
  }

  public void left(int power, int sleep) throws InterruptedException {
    player.noReply(playerName + ",moveRight(" + power + ")");
    System.out.println(playerName + ",moveRight(" + power + ")");
    moving = power != 0;
    Thread.sleep(sleep);
  }

  public void spinR(int power, int sleep) throws InterruptedException {
    player.noReply(playerName + ",spin(" + power + ")");
    System.out.println(playerName + ",spin(" + power + ")");
    Thread.sleep(sleep);
  }

  public void suck(int sleep) throws InterruptedException {
    player.noReply(playerName + ",setSuction(-100)");
    System.out.println(playerName + ",setSuction(-100)");
    Thread.sleep(sleep);
  }

  public void expel(int sleep) throws InterruptedException {
    player.noReply(playerName + ",setSuction(100)");
    System.out.println(playerName + ",setSuction(100)");
    Thread.sleep(sleep);
  }

  public void stopMoving(int sleep) throws InterruptedException {
    if (!moving)
      return;
    player.noReply(playerName + ",stop()");
    System.out.println(playerName + ",stop()");
    moving = false;
    Thread.sleep(sleep);
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
