package il.ac.bgu.cs.bp.leaderfollower;

import il.ac.bgu.cs.bp.bpjs.execution.BProgramRunner;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.BProgramRunnerListenerAdapter;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.PrintBProgramRunnerListener;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.SingleResourceBProgram;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.leaderfollower.events.GoSlowGradient;
import il.ac.bgu.cs.bp.leaderfollower.events.StaticEvents;
import il.ac.bgu.cs.bp.leaderfollower.events.Telemetry;
import java.awt.Color;

import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
 */
public class BPJsRoverControl {

    // GUI of the robot control panel 
    public static BPjsRoverControlPanel robotControlPanel;

    private static final SocketCommunicator rover = new SocketCommunicator();
    private static final SocketCommunicator ref = new SocketCommunicator();
    private static DriveCommands drive;
    public static String IP = "127.0.0.1";

    public static void main(String[] args) throws InterruptedException {
        
        System.out.println("Starting rover control program");
        int refPort = 0;
        int roverPort = 0;
        int maxSteps = 310;
        Double[] d2TAllTime = new Double[maxSteps + 1];
        Double[] disAllTime = new Double[maxSteps + 1];

        try {
            //------ Load config
            java.util.List<String> conf = readResource("config.txt");
            Iterator<String> conIt = conf.iterator();
            while (conIt.hasNext()) {
                String a = conIt.next();
                if (a.contains("=")) {
                    String vals[] = a.split("=", 2);
                    vals[0] = vals[0].trim();
                    vals[1] = vals[1].trim();
                    if (vals[0].equals("controlPort")) {
                        roverPort = Integer.parseInt(vals[1]);
                    }
                    if (vals[0].equals("observationPort")) {
                        refPort = Integer.parseInt(vals[1]);
                    }
                }
            }
            System.out.println("Connecting rover...");
            rover.connectToServer(IP, roverPort);

            System.out.println("Connecting ref...");
            ref.connectToServer(IP, refPort);
            drive = new DriveCommands(rover, "Rover");
            
        } catch (Exception e) {
            System.out.println("Error setting up program");
            e.printStackTrace();
            System.exit(-1);
        }

        BProgram bprog = new SingleResourceBProgram("ControllerLogic.js");
        bprog.setDaemonMode(true);
        BProgramRunner rnr = new BProgramRunner(bprog);

        // Print program events to the console
        rnr.addListener(new PrintBProgramRunnerListener());
        rnr.addListener(new BProgramRunnerListenerAdapter() {
            int stepCount = 0;
            DecimalFormat fmt = new DecimalFormat("#0.000");
            
            @Override
            public void eventSelected(BProgram bp, BEvent theEvent) {
                ExtractedGpsData LeaderGpsData;
                ExtractedGpsData RoverGpsData;
                Double distance, compassDeg, deg2Target;

                if (theEvent.equals(StaticEvents.StartControl)) {
                    robotControlPanel.Startbutton.setEnabled(false);
                    System.out.println("program: " + ref.send("ready"));
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
                            bp.enqueueExternalEvent(StaticEvents.Tick);
                        }
                    });
                    tmrThread.start();
                }

                if (theEvent.equals(StaticEvents.TurnLeft)) {
                    drive.left();
                }
                if (theEvent.equals(StaticEvents.TurnRight)) {
                    drive.right();
                }
                if (theEvent.equals(StaticEvents.ForwardTurnLeft)) {
                    drive.controlPower(0, 100);
                }
                if (theEvent.equals(StaticEvents.ForwardTurnRight)) {
                    drive.controlPower(100, 0);
                }
                if (theEvent.equals(StaticEvents.Go2Target)) {
                    drive.go();
                }
                if (theEvent.equals(StaticEvents.Stop)) {
                    drive.controlPower(0, 0);
                }
                if (theEvent.equals(StaticEvents.GoSlow)) {
                    drive.controlPower(40, 40);
                }
                if (theEvent instanceof GoSlowGradient) {
                    drive.controlPower(((GoSlowGradient) theEvent).power, ((GoSlowGradient) theEvent).power);
                }
                if (theEvent.equals(StaticEvents.BrakeOn)) {
                    drive.brake(true);
                }
                if (theEvent.equals(StaticEvents.BrakeOff)) {
                }
                if (theEvent.equals(StaticEvents.Tick)) {
                    LeaderGpsData = new ExtractedGpsData(ref.send("Leader,GPS()"));
                    RoverGpsData = new ExtractedGpsData(rover.send("Rover,GPS()"));
                    distance = extractData(ref.send("Leader,Distance()"));
                    compassDeg = extractData(rover.send("Rover,getCompass()"));
                    deg2Target = compDeg2Target(LeaderGpsData.x, LeaderGpsData.y, RoverGpsData.x, RoverGpsData.y, compassDeg);
                    
                    SwingUtilities.invokeLater(()->{
                        robotControlPanel.LeaderGPSY_Text.setText(fmt.format(LeaderGpsData.y));
                        robotControlPanel.LeaderGPSX_Text.setText(fmt.format(LeaderGpsData.x));
                        robotControlPanel.RoverGPSX_Text.setText(fmt.format(LeaderGpsData.x));
                        robotControlPanel.RoverGPSY_Text.setText(fmt.format(LeaderGpsData.y));
                        robotControlPanel.Distance_Text.setText(fmt.format(distance));
                        if (distance < 12 || distance > 15) {
                            robotControlPanel.Distance_Text.setBackground(Color.RED);
                        } else {
                            robotControlPanel.Distance_Text.setBackground(Color.GREEN);
                        }
                        robotControlPanel.Deg2Target_Text.setText(fmt.format(deg2Target));
                    });

                    bp.enqueueExternalEvent(new Telemetry(RoverGpsData.x, RoverGpsData.y, LeaderGpsData.x, LeaderGpsData.y, compassDeg, distance));
                    if (stepCount < maxSteps) {
                        System.out.println(stepCount);
                        d2TAllTime[stepCount] = deg2Target;
                        disAllTime[stepCount] = distance;
                        
                    } else if (stepCount == maxSteps) {
                        System.out.println("D2tArray: " + Arrays.toString(d2TAllTime));
                        System.out.println("DistArray: " + Arrays.toString(disAllTime));
                        try {
                            writeToFile("SimData5NDegN.csv", "DegToTarget: ", d2TAllTime);
                            writeToFile("SimData5NDistN.csv", "DistanceToTarget: ", disAllTime);
                            drive.brake(true);
                            rover.close();
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
        
        SwingUtilities.invokeLater(()->{
            robotControlPanel = new BPjsRoverControlPanel(bprog, rnr);
        });
        rnr.run();
    }
    // file read

    public static List<String> readResource(String fileName) {
        File file = new File(fileName);
        // this gives you a 2-dimensional array of strings
        List<String> data = new ArrayList<>();

        try (InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
             Scanner inputStream = new Scanner(resource)) {

            while (inputStream.hasNext()) {
                data.add(inputStream.next());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    // file read
    public static void writeToFile(String fileName, String dataInfo, Double[] theArray) throws IOException {
        FileWriter fW = new FileWriter(fileName, true);
        BufferedWriter bW = new BufferedWriter(fW);
        try {
            Instant a = Instant.now();
            bW.append("[" + a.toString() + "], " + dataInfo + "," + Arrays.toString(theArray).substring(1, Arrays.toString(theArray).length() - 1) + "\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            bW.flush();
            bW.close();
        }
    }

    public static class ExtractedGpsData {

        public Double x;
        public Double y;

        /**
         * Extracts The Data
         * @param theGpsMessage message text form the GPS
         */
        public ExtractedGpsData(String theGpsMessage) {
            String[] StringSplit;
            String Stringofx;
            String Stringofy;

            StringSplit = theGpsMessage.split(",");
            Stringofx = StringSplit[1];
            Stringofy = StringSplit[2].subSequence(0, StringSplit[2].indexOf(";")).toString();

            this.x = Double.parseDouble(Stringofx);
            this.y = Double.parseDouble(Stringofy);
        }
    }

    public static Double extractData(String TheDistanceMessage) {
        String[] StringSplit;
        String StringofDistance;

        StringSplit = TheDistanceMessage.split(",");
        StringofDistance = StringSplit[1].subSequence(0, StringSplit[1].indexOf(";")).toString();

        return Double.parseDouble(StringofDistance);
    }

    public static Double compDeg2Target(Double xL, Double yL, Double xR, Double yR, Double CompassDeg) {
        Double DDeg, LRDeg;

        LRDeg = atan2((yL - yR), (xL - xR));
//        System.out.println("LRDeg1: " + LRDeg.toString());
        LRDeg = (LRDeg / Math.PI) * 180;
//        System.out.println("LRDeg2: " + LRDeg.toString());
        DDeg = (90 - CompassDeg) - LRDeg;
//        System.out.println("DDeg1: " + DDeg.toString());
        if (abs(DDeg) >= 360) {
            if (DDeg > 0) {
                DDeg = DDeg - 360;
//                System.out.println("DDeg2: " + DDeg.toString());
            } else {
                DDeg = DDeg + 360;
//                System.out.println("DDeg3: " + DDeg.toString());
            }
        }
        if (abs(DDeg) > 180) {
            if (DDeg > 180) {
                DDeg = DDeg - 360;
//                System.out.println("DDeg4: " + DDeg.toString());
            }
            if (DDeg < (-180)) {
                DDeg = DDeg + 360;
//                System.out.println("DDeg5: " + DDeg.toString());
            }
        }
        System.out.println("DDeg5: " + DDeg.toString());
        return DDeg;
    }
}
