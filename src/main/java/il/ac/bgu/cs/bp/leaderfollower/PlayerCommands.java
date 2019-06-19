package il.ac.bgu.cs.bp.leaderfollower;

// format commands to unity string messages.
public class PlayerCommands {
  private String playerName;
  private String opponentName;
  private SocketCommunicator player;

  public PlayerCommands(SocketCommunicator sc, String n) {
    player = sc;
    playerName = n;
  }

  public void forward() {
    player.noReply(playerName + ",moveForward(100)");
    System.out.println(playerName + ",moveForward(100)");
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

  public ExtractedGpsData getPlayerGps() {
    return new ExtractedGpsData(player.send(this.playerName + ",GPS()"));
  }

  public ExtractedGpsData getOpponentGps() {
    return new ExtractedGpsData(player.send(this.opponentName + ",GPS()"));
  }

  public ExtractedGpsData getBallGps() {
    return new ExtractedGpsData(player.send("ball,GPS()"));
  }

  public Double getPlayerCompass() {
    return extractData(player.send(this.playerName + ",getCompass()"));
  }

  public Double getOpponentCompass() {
    return extractData(player.send(this.opponentName + ",getCompass()"));
  }

  public Double getDegreeToTarget(ExtractedGpsData target, ExtractedGpsData source, Double sourceDegree) {
    return compDeg2Target(source.x, source.y, target.x, target.y, sourceDegree);
  }

  private static Double extractData(String TheDistanceMessage) {
    String[] StringSplit;
    String StringofDistance;

    StringSplit = TheDistanceMessage.split(",");
    StringofDistance = StringSplit[1].subSequence(0, StringSplit[1].indexOf(";")).toString();
    return Double.parseDouble(StringofDistance);
  }

  private static Double compDeg2Target(Double xL, Double yL, Double xR, Double yR, Double CompassDeg) {
    Double DDeg, LRDeg;

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

  public static class ExtractedGpsData {
    public final Double x;
    public final Double y;

    public ExtractedGpsData(double x, double y) {
      this.x = x;
      this.y = y;
    }

    /**
     * Extracts The Data
     * 
     * @param theGpsMessage message text form the GPS.
     */
    private ExtractedGpsData(String theGpsMessage) {
      String[] StringSplit;
      String Stringofx;
      String Stringofy;

      StringSplit = theGpsMessage.split(",");
      Stringofx = StringSplit[1];
      Stringofy = StringSplit[2].subSequence(0, StringSplit[2].indexOf(";")).toString();

      this.x = Double.parseDouble(Stringofx);
      this.y = Double.parseDouble(Stringofy);
    }
  }
}
