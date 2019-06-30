package il.ac.bgu.cs.bp.leaderfollower;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.BProgramRunnerListenerAdapter;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.leaderfollower.PlayerCommands.GpsData;
import il.ac.bgu.cs.bp.leaderfollower.events.ParameterizedMove;
import il.ac.bgu.cs.bp.leaderfollower.events.Telemetry;

public class Communicator extends BProgramRunnerListenerAdapter implements Runnable {
  private final BlockingQueue<BEvent> eventsQueue = new LinkedBlockingQueue<>();
  private final int maxSteps = 310;
  private Double[] d2TAllTime = new Double[maxSteps];
  private Double[] disAllTime = new Double[maxSteps];
  private int stepCount = 0;
  private final DecimalFormat fmt = new DecimalFormat("#0.000");
  private final BProgram bp;
  private final PlayerCommands player;
  private final ControlPanel controlPanel;
  private final GpsData playerGate;
  private Thread thread;

  public Communicator(BProgram bp, PlayerCommands player, PlayerData data,
      ControlPanel controlPanel) {
    this.bp = bp;
    this.player = player;
    this.controlPanel = controlPanel;
    this.playerGate = data.gate;
  }

  @Override
  public void run() {
    try {
      Thread.sleep(200);
      int sleep = 30;
      while (true) {
        BEvent e = eventsQueue.poll(sleep, TimeUnit.MILLISECONDS);
        sleep = 30;
        if (e == null) {
          collectTelemetry();
        } else if (e.name.equals("Suck")) {
          player.suck();
        } else if (e.name.equals("Expel")) {
          player.expel();
        } else if (e instanceof ParameterizedMove) {
          ParameterizedMove move = (ParameterizedMove) e;
          player.parameterizedMove(move.powerForward, move.powerLeft, move.spin);
          // if(move.spin != null) sleep -= 10;
        }
      }
    } catch (InterruptedException ex) {
      Logger.getLogger(BPJsRobotControl.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public void eventSelected(BProgram bp, BEvent theEvent) {
    if (theEvent instanceof ParameterizedMove || theEvent.name.equals("Suck")
    || theEvent.name.equals("Expel"))
      try {
        eventsQueue.put(theEvent);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
  }
  
  public void start() { 
    thread = new Thread(this);
    thread.start();
  }
  
  private void collectTelemetry() {
    GpsData playerGpsData, ballGpsData;
    Double playerCompassDeg;
    try {
      playerGpsData = player.getPlayerGps();
      ballGpsData = player.getBallGps();
      playerCompassDeg = player.getPlayerCompass();
    } catch (Exception e) {
      Logger.getLogger(BPJsRobotControl.class.getName()).log(Level.SEVERE, "NO TELEMETRY RECIEVED");
      return;
    }
    
    Telemetry t = new Telemetry(playerGpsData, ballGpsData, playerGate, playerCompassDeg);

    SwingUtilities.invokeLater(() -> {
      controlPanel.PlayerGpsZ_Text.setText(fmt.format(t.playerGps.z));
      controlPanel.PlayerGpsX_Text.setText(fmt.format(t.playerGps.x));
      controlPanel.BallGpsZ_Text.setText(fmt.format(t.ballGps.z));
      controlPanel.BallGpsX_Text.setText(fmt.format(t.ballGps.x));
      controlPanel.Distance2Ball_Text.setText(fmt.format(t.playerToBall.distance));
      controlPanel.PlayerDegree_Text.setText(fmt.format(t.playerCompass));
      controlPanel.Deg2Ball_Text.setText(fmt.format(t.playerToBall.degree));
      controlPanel.Deg2Gate_Text.setText(fmt.format(t.playerToGate.degree));
    });
    Logger.getLogger(BPJsRobotControl.class.getName()).log(Level.INFO, "Enqueuing telemetry");
    bp.enqueueExternalEvent(t);
    if (stepCount < maxSteps) {
      System.out.println(stepCount);
      d2TAllTime[stepCount] = t.playerToBall.degree;
      disAllTime[stepCount] = t.playerToBall.distance;
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
