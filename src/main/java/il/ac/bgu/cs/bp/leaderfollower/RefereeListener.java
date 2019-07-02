package il.ac.bgu.cs.bp.leaderfollower;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;

// format commands to unity string messages.
public class RefereeListener implements Runnable {
  private final int port;
  private final String ip;
  private final BProgram bp;
  private final ControlPanel controlPanel;
  private boolean stop = false;
  private SocketCommunicator referee;
  private Thread thread = null;

  public RefereeListener(String ip, int port, BProgram bp, ControlPanel controlPanel) {
    this.ip = ip;
    this.port = port;
    this.bp = bp;
    this.controlPanel = controlPanel;
  }

  private void connectToServer() throws IOException {
    referee = new SocketCommunicator();
    referee.connectToServer(ip, port);
  }

  public void stop() {
    this.stop = true;
  }

  @Override
  public void run() {
    if (thread == null) {
      throw new IllegalAccessError("The referee must be started through the start method");
    }
    try {
      Thread.sleep(200);
    } catch (InterruptedException ex) {
      Logger.getLogger(BPJsRobotControl.class.getName()).log(Level.SEVERE, null, ex);
    }
    try {
      connectToServer();
      while (!stop) {
        String msg = referee.getMessage();
        Logger.getLogger(BPJsRobotControl.class.getName()).log(Level.INFO, msg);
        System.out.println("Received: " + msg);
        String[] splitted = msg.split(";");
        SwingUtilities.invokeLater(() -> {
          if (splitted[0].equals("Possesion"))
            controlPanel.BallPosession_Text.setText(splitted[1]);
          if (splitted[0].equals("TimeOut"))
            controlPanel.Timeout_Text.setText(splitted[1]);
          if (splitted[0].equals("scored"))
            controlPanel.Scoring_Text.setText(String.format("Who:%s Score:%s", splitted[1], splitted[2]));
          if (splitted[0].equals("Done"))
            controlPanel.EndOfGame_Text.setText("Done");
        });
        bp.enqueueExternalEvent(new BEvent(splitted[0], splitted));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void start() {
    thread = new Thread(this);
    thread.start();
  }
}
