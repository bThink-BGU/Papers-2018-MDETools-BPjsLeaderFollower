package il.ac.bgu.cs.bp.leaderfollower;

import il.ac.bgu.cs.bp.bpjs.context.ContextService;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.BProgramRunnerListenerAdapter;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import static il.ac.bgu.cs.bp.leaderfollower.SourceUtils.readResource;
import il.ac.bgu.cs.bp.leaderfollower.PlayerCommands.GpsData;
import il.ac.bgu.cs.bp.leaderfollower.eventListeners.Actuator;
import il.ac.bgu.cs.bp.leaderfollower.eventListeners.TimeoutNotifier;
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
  private final RefereeListener referee;
  private final int possessionTimer;
  private final String ip;
  private final int playerId;
  private final BProgram bprog;
  private final ContextService contextService;
  private final TelemetryCollector telemetryCollector;
  private final Actuator actuator;

  public BPJsRobotControl(Optional<String> simulationIp, OptionalInt player1Port,
      OptionalInt player2Port, OptionalInt refereePort, OptionalInt thePlayerId, OptionalInt possessionTime)
      throws IOException {
    this.ip = simulationIp.orElse("127.0.0.1");
    this.possessionTimer = possessionTime.orElse(10);
    this.playerId = thePlayerId.orElse(2);
    int opponentId = (this.playerId + 1) % 2;
    this.players = new PlayerData[] {
        new PlayerData(player1Port.orElse(9001), "player" + 1, 1, new GpsData(50, 0, 0)),
        new PlayerData(player2Port.orElse(9003), "player" + 2, 2, new GpsData(-50, 0, 0))};
    this.player = players[playerId - 1];
    this.playerCommands = new PlayerCommands(player.name, this.ip, player.port);

    this.contextService = ContextService.getInstance();
    contextService.initFromResources("ContextDB", "ContextualControllerLogic.js");
    this.bprog = contextService.getBProgram();
    this.bprog.putInGlobalScope("player", player);
    this.bprog.putInGlobalScope("opponent", players[opponentId - 1]);
    this.bprog.setWaitForExternalEvents(true);
    // SwingUtilities.invokeLater(() -> {
    this.controlPanel = new ControlPanel(bprog);
    // });
    this.referee = new RefereeListener(this.ip, refereePort.orElse(9007), bprog, controlPanel);
    this.telemetryCollector = new TelemetryCollector(bprog, playerCommands, player, controlPanel);
    this.actuator = new Actuator(playerCommands, this.telemetryCollector);

    // contextService.addListener(new PrintBProgramRunnerListener());
    contextService.addListener(actuator);
    contextService.addListener(new TimeoutNotifier(possessionTimer));
    contextService.addListener(new BProgramRunnerListenerAdapter() {
      @Override
      public void eventSelected(BProgram bp, BEvent theEvent) {
        if (theEvent.name.equals("Start Control")) {
          try {
            playerCommands.connectToServer();
          } catch (IOException e) {
          }
          controlPanel.Startbutton.setEnabled(false);
          referee.start();
          // telemetryCollector.start();
          actuator.start();
        }
      }
    });
  }

  private void start() {
    contextService.run();
  }

  public static void main(String[] args) throws InterruptedException, IOException {
    System.out.println("Starting player control program");
    OptionalInt playerId = OptionalInt.empty();
    OptionalInt player1Port = OptionalInt.empty();
    OptionalInt player2Port = OptionalInt.empty();
    OptionalInt refereePort = OptionalInt.empty();
    OptionalInt possessionTimer = OptionalInt.empty();
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
      java.util.List<String> conf = Arrays.asList(readResource("GameSettings.txt").split("\n"));
      Iterator<String> conIt = conf.iterator();
      while (conIt.hasNext()) {
        String a = conIt.next();
        if (a.contains("=")) {
          String vals[] = a.split("=", 2);
          String key = vals[0].trim();
          String value = vals[1].trim();
          if (key.equals("simulationIP")) {
            ip = Optional.of(value);
          } else if (key.equals("player1Port")) {
            player1Port = OptionalInt.of(Integer.parseInt(value));
          } else if (key.equals("player2Port")) {
            player2Port = OptionalInt.of(Integer.parseInt(value));
          } else if (key.equals("refereePort")) {
            refereePort = OptionalInt.of(Integer.parseInt(value));
          } else if (key.equals("possessionTimer")) {
            possessionTimer = OptionalInt.of(Integer.parseInt(value.substring(0, value.length() - 1)));
          }
        }
      }

    } catch (Exception e) {
      System.out.println("Error setting up program");
      e.printStackTrace();
      System.exit(-1);
    }

    BPJsRobotControl control =
        new BPJsRobotControl(ip, player1Port, player2Port, refereePort, playerId, possessionTimer);
    control.start();
  }
}
