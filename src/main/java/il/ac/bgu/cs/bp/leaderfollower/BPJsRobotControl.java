package il.ac.bgu.cs.bp.leaderfollower;

import il.ac.bgu.cs.bp.bpjs.execution.BProgramRunner;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.BProgramRunnerListenerAdapter;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.PrintBProgramRunnerListener;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.model.ResourceBProgram;
import il.ac.bgu.cs.bp.bpjs.model.eventselection.PrioritizedBSyncEventSelectionStrategy;
import static il.ac.bgu.cs.bp.leaderfollower.SourceUtils.readResource;
import il.ac.bgu.cs.bp.leaderfollower.PlayerCommands.GpsData;
import il.ac.bgu.cs.bp.leaderfollower.events.StaticEvents;
import java.util.*;
import java.io.IOException;

/**
 *
 * @author Aviran
 * @author Michael
 * @author Achiya
 */
public class BPJsRobotControl {
  // GUI of the robot control panel
  private ControlPanel controlPanel;
  private final PlayerData[] players;
  private final PlayerCommands playerCommands;
  private final PlayerData player;
  private final Referee referee;
  private final String ip;
  private final int playerId;
  private final BProgram bprog;
  private final BProgramRunner rnr;
  private final Communicator communicator;
 
  public BPJsRobotControl(Optional<String> simulationIp, OptionalInt player1Port, OptionalInt player2Port,
      OptionalInt refereePort, OptionalInt thePlayerId) throws IOException {
    this.ip = simulationIp.orElse("127.0.0.1");
    this.playerId = thePlayerId.orElse(2);
    int opponentId = (this.playerId + 1) % 2;
    this.players = new PlayerData[] {
      new PlayerData(player1Port.orElse(9001), "player"+1, 1, new GpsData(50, 0, 0)),
      new PlayerData(player2Port.orElse(9003), "player"+2, 2, new GpsData(-50, 0, 0))
    };
    this.player = players[playerId-1];
    this.playerCommands = new PlayerCommands(player.name, this.ip, player.port);
        this.bprog = new ResourceBProgram("ControllerLogic.js");
    // this.bprog.prependSource(readResource("CommonLib.js"));
    this.bprog.putInGlobalScope("player", player);
    this.bprog.putInGlobalScope("opponent", players[opponentId-1]);
    this.bprog.setWaitForExternalEvents(true);
    bprog.setEventSelectionStrategy(new PrioritizedBSyncEventSelectionStrategy());
    // SwingUtilities.invokeLater(() -> {
      this.controlPanel = new ControlPanel(bprog);
    // });
    this.referee = new Referee(this.ip, refereePort.orElse(9007), bprog, controlPanel);
    this.communicator = new Communicator(bprog, playerCommands, player, controlPanel);
    this.rnr = new BProgramRunner(bprog);

    rnr.addListener(new PrintBProgramRunnerListener());
    rnr.addListener(communicator);
    rnr.addListener(new BProgramRunnerListenerAdapter() {
      @Override
      public void eventSelected(BProgram bp, BEvent theEvent) {
        if (theEvent.equals(StaticEvents.START_CONTROL)) {
          try {
            playerCommands.connectToServer();
          } catch (IOException e) {
          }
          controlPanel.Startbutton.setEnabled(false);
          referee.start();
          communicator.start();
        }
      }
    });
  }

  private void start() {
    rnr.run();
  }

  public static void main(String[] args) throws InterruptedException, IOException {
    System.out.println("Starting player control program");
    OptionalInt playerId = OptionalInt.empty();
    OptionalInt player1Port = OptionalInt.empty();
    OptionalInt player2Port = OptionalInt.empty();
    OptionalInt refereePort = OptionalInt.empty();
    Optional<String> ip = Optional.empty();
    try {
      if (args.length == 1) {
        if (!(args[0].equals("1") || args[0].equals("2"))) {
          System.err.println("player id must be 1 or 2");
          System.exit(1);
        }
        playerId = OptionalInt.of(Integer.parseInt(args[0]));
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
            ip = Optional.of(value);
          }
          if (key.equals("player1Port")) {
            player1Port = OptionalInt.of(Integer.parseInt(value));
          }
          if (key.equals("player2Port")) {
            player2Port = OptionalInt.of(Integer.parseInt(value));
          }
          if (key.equals("refereePort")) {
            refereePort = OptionalInt.of(Integer.parseInt(value));
          }
        }
      }

    } catch (Exception e) {
      System.out.println("Error setting up program");
      e.printStackTrace();
      System.exit(-1);
    }

    BPJsRobotControl control = new BPJsRobotControl(ip, player1Port, player2Port, refereePort, playerId);
    control.start();
  }
}
