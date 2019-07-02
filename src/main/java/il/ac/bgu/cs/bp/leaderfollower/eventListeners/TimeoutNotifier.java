package il.ac.bgu.cs.bp.leaderfollower.eventListeners;

import il.ac.bgu.cs.bp.bpjs.execution.listeners.BProgramRunnerListenerAdapter;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;

public class TimeoutNotifier extends BProgramRunnerListenerAdapter {
  private final int timeout;
  private String name = "";
  private Notifier notifier = null;

  public TimeoutNotifier(int timeout) {
    this.timeout = timeout;
  }

  @Override
  public void eventSelected(BProgram bp, BEvent theEvent) {
    if (theEvent.name.equals("Possesion")) {
      if (notifier != null)
        notifier.stop();
      this.name = theEvent.getData().toString();
      if (!this.name.equals("")) {
        this.notifier = new Notifier(name, timeout, bp);
        this.notifier.start();
      }
    }
  }

  private static class Notifier implements Runnable {
    private int counter;
    private Thread thread;
    private boolean stop = false;
    private final BProgram bp;
    private final String name;

    private Notifier(String name, int timeout, BProgram bp) {
      this.counter = timeout;
      this.bp = bp;
      this.name = name;
    }

    private Thread start() {
      this.thread = new Thread(this);
      this.thread.start();
      return this.thread;
    }

    private void stop() {
      this.stop = true;
      this.thread.interrupt();
    }

    @Override
    public void run() {
      while (!stop) {
        bp.enqueueExternalEvent(new BEvent("Timeout", new TimeoutData(name, counter)));
        counter--;
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
      }
    }
  }

  public static class TimeoutData {
    public final String playerName;
    public final int counter;

    private TimeoutData(String playerName, int counter) {
      this.playerName = playerName;
      this.counter = counter;
    }

    @Override
    public String toString() {
      return "Name: " + this.playerName + ", Timeout in: " + counter + " seconds";
    }
  }
}
