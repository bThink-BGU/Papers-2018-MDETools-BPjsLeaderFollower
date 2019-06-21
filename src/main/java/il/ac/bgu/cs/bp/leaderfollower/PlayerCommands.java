package il.ac.bgu.cs.bp.leaderfollower;

import java.io.IOException;

// format commands to unity string messages.
public class PlayerCommands {
  private String playerName;
  private String opponentName;
  private SocketCommunicator player;

  public PlayerCommands(String playerName, String opponentName)
      throws IOException {
    this.playerName = playerName;
    this.opponentName = opponentName;
  }

  public void connectToServer(String ip, int port) throws IOException {
    player = new SocketCommunicator();
    player.connectToServer(ip, port);
  }

  public void forward() {
    player.noReply(playerName + ",moveForward(100)");
    System.out.println(playerName + ",moveForward(100)");
  }

  public void parameterizedMove(int powerX, int powerZ, int spin) {
    player.noReply(playerName + ",moveForward(" + powerX + ")");
    player.noReply(playerName + ",moveRight(" + powerZ + ")");
    player.noReply(playerName + ",spin(" + spin + ")");
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

  public void stop() {
    player.noReply(playerName + ",stop()");
    System.out.println(playerName + ",stop()");
  }

  public void spinStop() {
    player.noReply(playerName + ",spin(0)");
    System.out.println(playerName + ",spin(0)");
  }

  public GpsData getPlayerGps() {
    return new GpsData(player.send(this.playerName + ",GPS()"));
  }

  public GpsData getOpponentGps() {
    return new GpsData(player.send(this.opponentName + ",GPS()"));
  }

  public GpsData getBallGps() {
    return new GpsData(player.send("ball,GPS()"));
  }

  public Double getPlayerCompass() {
    return extractData(player.send(this.playerName + ",getCompass()"));
  }

  public Double getOpponentCompass() {
    return extractData(player.send(this.opponentName + ",getCompass()"));
  }

  public Double getDegreeToTarget(GpsData target, GpsData source, Double sourceDegree) {
    return compDeg2Target(target.x, target.y, source.x, source.y, sourceDegree);
  }

  public Double getDistance(GpsData pA, GpsData pB) {
    double dx = Math.abs(pA.x - pB.x);
    double dz = Math.abs(pA.z - pB.z);
    return Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));
  }

  private static Double extractData(String TheDistanceMessage) {
    String[] StringSplit;
    String StringofDistance;

    StringSplit = TheDistanceMessage.split(",");
    StringofDistance = StringSplit[1].subSequence(0, StringSplit[1].indexOf(";")).toString();
    return Double.parseDouble(StringofDistance);
  }

  private static Double compDeg2Target(Double xTarget, Double yTarget, Double xSource,
      Double ySource, Double sourceDegree) {
    Double DDeg, LRDeg;

    LRDeg = Math.atan2((yTarget - ySource), (xTarget - xSource));
    LRDeg = (LRDeg / Math.PI) * 180;
    DDeg = (90 - sourceDegree) - LRDeg;
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
