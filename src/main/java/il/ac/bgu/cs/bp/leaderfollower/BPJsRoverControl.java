package il.ac.bgu.cs.bp.leaderfollower;

import il.ac.bgu.cs.bp.bpjs.execution.BProgramRunner;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.BProgramRunnerListenerAdapter;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.PrintBProgramRunnerListener;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.model.ResourceBProgram;
import static il.ac.bgu.cs.bp.leaderfollower.SourceUtils.readResource;
import il.ac.bgu.cs.bp.leaderfollower.PlayerCommands.ExtractedGpsData;
import il.ac.bgu.cs.bp.leaderfollower.events.GoSlowGradient;
import il.ac.bgu.cs.bp.leaderfollower.events.StaticEvents;
import il.ac.bgu.cs.bp.leaderfollower.events.Telemetry;
import java.awt.Color;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Math.*;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author Aviran
 * @author Michael
 * @author Achiya
 */
public class BPJsRoverControl {
  // GUI of the robot control panel
  public static BPjsRoverControlPanel robotControlPanel;

  private static final SocketCommunicator playerBridge = new SocketCommunicator();
  private static PlayerCommands player;

  public static void main(String[] args) throws InterruptedException, IOException {
    System.out.println("Starting player control program");
    int maxSteps = 310;
    Double[] d2TAllTime = new Double[maxSteps];
    Double[] disAllTime = new Double[maxSteps];
    int playerPort = 0;
    int playerId = 1;
    String ip = "127.0.0.1";
    String playerName = "player";
    String opponentPlayerName = "player";
    ExtractedGpsData playerGate;
    ExtractedGpsData opponentGate;

    try {
      if (args.length == 1) {
        playerId = Integer.parseInt(args[0]);
      }
      if (playerId == 1) {
        playerName += playerId;
        opponentPlayerName += 3;
        playerGate = new ExtractedGpsData(0, 50);
        opponentGate = new ExtractedGpsData(0, -50);
      } else {
        playerName += 3;
        opponentPlayerName += playerId;
        playerGate = new ExtractedGpsData(0, -50);
        opponentGate = new ExtractedGpsData(0, 50);
      }
      playerName += (playerId == 1 ? playerId : 3);
      opponentPlayerName += (playerId == 1 ? 3 : playerId);

      // ------ Load config
      java.util.List<String> conf = Arrays.asList(readResource("config.txt").split("\n"));
      Iterator<String> conIt = conf.iterator();
      while (conIt.hasNext()) {
        String a = conIt.next();
        if (a.contains("=")) {
          String vals[] = a.split("=", 2);
          String key = vals[0].trim();
          String value = vals[1].trim();
          if (key.equals("simulationIP")) {
            ip = value;
          }
          if (key.equals("player" + playerId + "Port")) {
            playerPort = Integer.parseInt(value);
          }
        }
      }
      System.out.println("Connecting rover...");
      playerBridge.connectToServer(ip, playerPort);

      System.out.println("Connecting ref...");
      player = new PlayerCommands(playerBridge, playerName);

    } catch (Exception e) {
      System.out.println("Error setting up program");
      e.printStackTrace();
      System.exit(-1);
    }

    BProgram bprog = new ResourceBProgram("ControllerLogic.js");
    bprog.prependSource(readResource("CommonLib.js"));

    bprog.setWaitForExternalEvents(true);
    BProgramRunner rnr = new BProgramRunner(bprog);

    // Print program events to the console
    rnr.addListener(new PrintBProgramRunnerListener());
    rnr.addListener(new BProgramRunnerListenerAdapter() {
      int stepCount = 0;
      DecimalFormat fmt = new DecimalFormat("#0.000");

      @Override
      public void eventSelected(BProgram bp, BEvent theEvent) {
        if (theEvent.equals(StaticEvents.START_CONTROL)) {
          robotControlPanel.Startbutton.setEnabled(false);
          Thread tmrThread = new Thread(() -> {
            try {
              Thread.sleep(250);
            } catch (InterruptedException ex) {
              Logger.getLogger(BPJsRoverControl.class.getName()).log(Level.SEVERE, null, ex);
            }
            while (true) {
              try {
                Thread.sleep(180);
              } catch (InterruptedException e1) {
                e1.printStackTrace();
              }
              bp.enqueueExternalEvent(StaticEvents.TICK);
            }
          });
          tmrThread.start();
        }

        if (theEvent.equals(StaticEvents.TURN_LEFT)) {
          player.left();
        }
        if (theEvent.equals(StaticEvents.TURN_RIGHT)) {
          player.right();
        }
        if (theEvent.equals(StaticEvents.GO_TO_TARGET)) {
          player.go();
        }
        if (theEvent instanceof GoSlowGradient) {
          player.controlPower(((GoSlowGradient) theEvent).power, ((GoSlowGradient) theEvent).power);
        }
        if (theEvent.equals(StaticEvents.TICK)) {
          Double playerDistanceToBall, opponentDistanceToBall, playerCompassDeg, opponentCompassDeg,
              degreeToBall, degreeToGate;
          ExtractedGpsData playerGpsData = player.getPlayerGps();
          ExtractedGpsData opponentGpsData = player.getOpponentGps();
          ExtractedGpsData ballGpsData = player.getBallGps();
          playerCompassDeg = player.getPlayerCompass();
          opponentCompassDeg = player.getOpponentCompass();
          degreeToBall = player.getDegreeToTarget(ballGpsData, playerGpsData, playerCompassDeg);
          degreeToGate = player.getDegreeToTarget(playerGate, playerGpsData, playerCompassDeg);

          SwingUtilities.invokeLater(() -> {
            robotControlPanel.PlayerGpsY_Text.setText(fmt.format(playerGpsData.y));
            robotControlPanel.PlayerGpsX_Text.setText(fmt.format(playerGpsData.x));
            robotControlPanel.OpponentGpsY_Text.setText(fmt.format(opponentGpsData.y));
            robotControlPanel.OpponentGpsX_Text.setText(fmt.format(opponentGpsData.x));
            robotControlPanel.Distance2Ball_Text.setText(fmt.format(playerDistanceToBall));
            robotControlPanel.Distance2Ball_Text.setBackground(
                (playerDistanceToBall < 12 || playerDistanceToBall > 15) ? Color.RED : Color.GREEN);
            robotControlPanel.Deg2Ball_Text.setText(fmt.format(degreeToBall));
            robotControlPanel.Deg2Gate_Text.setText(fmt.format(degreeToGate));
          });

          bp.enqueueExternalEvent(new Telemetry(playerGpsData.x, playerGpsData.y, opponentGpsData.x,
              opponentGpsData.y, playerCompassDeg, playerDistanceToBall));
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
              playerBridge.close();
              System.exit(0);
            } catch (IOException ex) {
              Logger.getLogger(BPJsRoverControl.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
          stepCount++;
          robotControlPanel.TimeLabel.setText(Integer.toString(stepCount));
        }
      }
    });

    SwingUtilities.invokeLater(() -> {
      robotControlPanel = new BPjsRoverControlPanel(bprog, rnr);
    });
    rnr.run();
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
