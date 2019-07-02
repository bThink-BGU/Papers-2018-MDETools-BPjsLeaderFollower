package il.ac.bgu.cs.bp.leaderfollower.eventListeners;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.BProgramRunnerListenerAdapter;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.leaderfollower.BPJsRobotControl;
import il.ac.bgu.cs.bp.leaderfollower.PlayerCommands;
import il.ac.bgu.cs.bp.leaderfollower.TelemetryCollector;
import il.ac.bgu.cs.bp.leaderfollower.events.ParameterizedMove;

public class Actuator extends BProgramRunnerListenerAdapter implements Runnable {
  private final BlockingQueue<BEvent> eventsQueue = new LinkedBlockingQueue<>();
  private final PlayerCommands player;
  private final TelemetryCollector telemetryCollector;
  private final int sleep = 30;
  private Thread thread;

  public Actuator(PlayerCommands player, TelemetryCollector telemetryCollector) {
    this.player = player;
    this.telemetryCollector = telemetryCollector;
  }

  @Override
  public void run() {
    try {
      while (true) {
        BEvent e = eventsQueue.poll(30, TimeUnit.MILLISECONDS);
        if (e == null) {
          telemetryCollector.collectTelemetry();
        } else if (e.name.equals("Suck")) {
          player.suck(sleep);
        } else if (e.name.equals("Expel")) {
          player.expel(sleep);
        } else if (e instanceof ParameterizedMove) {
          ParameterizedMove move = (ParameterizedMove) e;
          player.parameterizedMove(move.powerForward, move.powerLeft, move.spin, sleep);
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
}
