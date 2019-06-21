package il.ac.bgu.cs.bp.leaderfollower;

import il.ac.bgu.cs.bp.bpjs.execution.BProgramRunner;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.BProgramRunnerListenerAdapter;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.PrintBProgramRunnerListener;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.model.ResourceBProgram;
import static il.ac.bgu.cs.bp.leaderfollower.SourceUtils.readResource;
import il.ac.bgu.cs.bp.leaderfollower.PlayerCommands.GpsData;
import il.ac.bgu.cs.bp.leaderfollower.events.ParameterizedMove;
import il.ac.bgu.cs.bp.leaderfollower.events.StaticEvents;
import java.util.*;
import java.io.IOException;
import javax.swing.SwingUtilities;

/**
 *
 * @author Aviran
 * @author Michael
 * @author Achiya
 */
public class BPJsRobotControl {
  // GUI of the robot control panel
  public static ControlPanel controlPanel;
  private static PlayerCommands player;
  private static String ip = "127.0.0.1";
  private static int playerPort = 0;

  public static void main(String[] args) throws InterruptedException, IOException {
    System.out.println("Starting player control program");
    int playerId = 1;
    String playerName = "player";
    String opponentPlayerName = "player";
    GpsData playerGate;
    GpsData opponentGate;

    try {
      if (args.length == 1) {
        playerId = Integer.parseInt(args[0]);
      }
      if (playerId == 1) {
        playerName += playerId;
        opponentPlayerName += 3;
        playerGate = new GpsData(0, 50);
        opponentGate = new GpsData(0, -50);
      } else {
        playerName += 3;
        opponentPlayerName += playerId;
        playerGate = new GpsData(0, -50);
        opponentGate = new GpsData(0, 50);
      }

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
      player = new PlayerCommands(playerName, opponentPlayerName);

      System.out.println("Connecting ref...");

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
      @Override
      public void eventSelected(BProgram bp, BEvent theEvent) {
        if (theEvent.equals(StaticEvents.START_CONTROL)) {
          try {
            player.connectToServer(ip, playerPort);
          } catch (IOException e) {
          }
          controlPanel.Startbutton.setEnabled(false);
          Thread telemetryCollector = new Thread(new TelemetryCollector(bp, player, controlPanel));
          telemetryCollector.start();
        } else if (theEvent.equals(StaticEvents.TURN_LEFT)) {
          player.spinL();
        } else if (theEvent.equals(StaticEvents.TURN_RIGHT)) {
          player.spinR();
        } else if (theEvent.equals(StaticEvents.STOP_TURNING)) {
          player.spinStop();
        } else if (theEvent.equals(StaticEvents.FORWARD)) {
          player.forward();
        } else if (theEvent.equals(StaticEvents.BACKWARD)) {
          player.backward();
        } else if (theEvent.equals(StaticEvents.RIGHT)) {
          player.right();
        } else if (theEvent.equals(StaticEvents.LEFT)) {
          player.left();
        } else if (theEvent.equals(StaticEvents.STOP_MOVEING)) {
          player.stop();
        } else if (theEvent.equals(StaticEvents.SUCK)) {
          player.suck();
        } else if (theEvent.equals(StaticEvents.EXPEL)) {
          player.expel();
        } else if (theEvent instanceof ParameterizedMove) {
          ParameterizedMove move = (ParameterizedMove) theEvent;
          player.parameterizedMove(move.powerX, move.powerZ, move.spin);
        }
      }
    });

    SwingUtilities.invokeLater(() -> {
      controlPanel = new ControlPanel(bprog);
    });
    rnr.run();
  }
}
