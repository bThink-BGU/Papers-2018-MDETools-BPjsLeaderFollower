package il.ac.bgu.cs.bp.leaderfollower;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.leaderfollower.PlayerCommands.GpsData;
import il.ac.bgu.cs.bp.leaderfollower.events.Telemetry;

public class TelemetryCollector implements Runnable {
  private final int maxSteps = 310;
  private Double[] d2TAllTime = new Double[maxSteps];
  private Double[] disAllTime = new Double[maxSteps];
  private int stepCount = 0;
  private final DecimalFormat fmt = new DecimalFormat("#0.000");
  private final BProgram bp;
  private final PlayerCommands player;
  private final ControlPanel controlPanel;

  public TelemetryCollector(BProgram bp, PlayerCommands player, ControlPanel controlPanel) {
    this.bp = bp;
    this.player = player;
    this.controlPanel = controlPanel;
  }

  @Override
  public void run() {
    try {
      Thread.sleep(250);
    } catch (InterruptedException ex) {
      Logger.getLogger(BPJsRobotControl.class.getName()).log(Level.SEVERE, null, ex);
    }
    while (true) {
      try {
        Thread.sleep(180);
      } catch (InterruptedException e1) {
        e1.printStackTrace();
      }
      collectTelemetry();
    }
  }

  private void collectTelemetry() {
    Double playerDistanceToBall, opponentDistanceToBall, playerCompassDeg, opponentCompassDeg,
          degreeToBall, degreeToGate;
      GpsData playerGpsData = player.getPlayerGps();
      // GpsData opponentGpsData = null;
      GpsData ballGpsData = player.getBallGps();
      playerCompassDeg = player.getPlayerCompass();
      // opponentCompassDeg = player.getOpponentCompass();
      degreeToBall = player.getDegreeToTarget(ballGpsData, playerGpsData, playerCompassDeg);
      degreeToGate = player.getDegreeToTarget(playerGpsData, playerGpsData, playerCompassDeg);
      playerDistanceToBall = player.getDistance(playerGpsData, ballGpsData);

      SwingUtilities.invokeLater(() -> {
        controlPanel.PlayerGpsY_Text.setText(fmt.format(playerGpsData.y));
        controlPanel.PlayerGpsX_Text.setText(fmt.format(playerGpsData.x));
        // robotControlPanel.OpponentGpsY_Text.setText(fmt.format(opponentGpsData.y));
        // robotControlPanel.OpponentGpsX_Text.setText(fmt.format(opponentGpsData.x));
        controlPanel.Distance2Ball_Text.setText(fmt.format(playerDistanceToBall));
        controlPanel.Deg2Ball_Text.setText(fmt.format(degreeToBall));
        controlPanel.Deg2Gate_Text.setText(fmt.format(degreeToGate));
      });

      bp.enqueueExternalEvent(
          new Telemetry(ballGpsData, playerGpsData, playerCompassDeg, playerDistanceToBall));
      if (stepCount < maxSteps) {
        System.out.println(stepCount);
        d2TAllTime[stepCount] = degreeToBall;
        disAllTime[stepCount] = playerDistanceToBall;
      } else if (stepCount == maxSteps) {
        System.out.println("D2tArray: " + Arrays.toString(d2TAllTime));
        System.out.println("DistArray: " + Arrays.toString(disAllTime));
        try {
          writeToFile("SimDataDeg.csv", "DegToTarget: ", d2TAllTime);
          writeToFile("SimDataDist.csv", "DistanceToTarget: ", disAllTime);
          player.close();
          System.exit(0);
        } catch (IOException ex) {
          Logger.getLogger(BPJsRobotControl.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
      stepCount++;
      controlPanel.TimeLabel.setText(Integer.toString(stepCount));
  }

  // file read
  public static void writeToFile(String fileName, String dataInfo, Double[] theArray)
      throws IOException {
    FileWriter fW = new FileWriter(fileName, true);
    BufferedWriter bW = new BufferedWriter(fW);
    try {
      Instant a = Instant.now();
      bW.append("[" + a.toString() + "], " + dataInfo + ","
          + Arrays.toString(theArray).substring(1, Arrays.toString(theArray).length() - 1) + "\n");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      bW.flush();
      bW.close();
    }
  }
}
