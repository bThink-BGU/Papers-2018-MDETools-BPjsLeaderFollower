package il.ac.bgu.cs.bp.leaderfollower;

import il.ac.bgu.cs.bp.leaderfollower.PlayerCommands.GpsData;

public class PlayerData {
  public final int port;
  public final String name;
  public final int id;
  public final GpsData gate;
  
  public PlayerData(int port, String name, int id, GpsData gate) {
    this.port = port;
    this.name = name;
    this.id = id; this.gate = gate;
  }
}