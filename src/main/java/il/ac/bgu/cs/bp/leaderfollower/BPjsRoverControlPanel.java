package il.ac.bgu.cs.bp.leaderfollower;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import il.ac.bgu.cs.bp.leaderfollower.events.StaticEvents;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.execution.BProgramRunner;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class BPjsRoverControlPanel {

    private final BProgram bp;
    private final BProgramRunner rnr;

    private final JFrame window = new JFrame("Robot Control Panel");
    public JButton button = new JButton();
    public JButton Startbutton = new JButton();
    public JLabel message = new JLabel();
    public JLabel TimeLabel = new JLabel();
    public Long TimeLabelint = new Long(0);

    JPanel ButtonsPanel;
    JPanel MainPanel;
    public JLabel MainTitle = new JLabel("RoverControl");
    JPanel TelemPanel;
    public JLabel RoverGPSX_Lable = new JLabel("Rover Gps X:        ");
    public JLabel RoverGPSY_Lable = new JLabel("Rover Gps Y:        ");
    public JLabel LeaderGPSX_Lable = new JLabel("Leader Gps X:        ");
    public JLabel LeaderGPSY_Lable = new JLabel("Leader Gps Y:        ");
    public JLabel Distance_Lable = new JLabel("Distance:        ");
    public JLabel Deg2Target_Lable = new JLabel("Deg2Target:        ");

    public JTextField RoverGPSX_Text = new JTextField("-----", 8);
    public JTextField RoverGPSY_Text = new JTextField("-----", 8);
    public JTextField LeaderGPSX_Text = new JTextField("-----", 8);
    public JTextField LeaderGPSY_Text = new JTextField("-----", 8);
    public JTextField Distance_Text = new JTextField("-----", 8);
    public JTextField Deg2Target_Text = new JTextField("-----", 8);

    public BPjsRoverControlPanel(BProgram bp, BProgramRunner rnr) {
        button = new JButton();
        Startbutton = new JButton();

        this.bp = bp;
        this.rnr = rnr;

        // Create window
        window.setSize(400, 400);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new BorderLayout());
        window.setLocation(new Point(500, 100));
        MainPanel = new JPanel();
        ButtonsPanel = new JPanel();;
        TelemPanel = new JPanel();
        // The message label
        message.setHorizontalAlignment(SwingConstants.CENTER);
        TimeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        Startbutton.setText("start sim");
        Startbutton.setSelected(true);
        Startbutton.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    bp.enqueueExternalEvent(StaticEvents.StartControl);
                    System.out.println("SimStarted");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        Startbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bp.enqueueExternalEvent(StaticEvents.StartControl);
                System.out.println("SimStarted");
            }
        });
        button.setText("type Bla");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println("bla1");

            }
        });

        TelemPanel.add(RoverGPSX_Lable);
        TelemPanel.add(RoverGPSX_Text);
        niceText(RoverGPSX_Text);
        TelemPanel.add(RoverGPSY_Lable);
        TelemPanel.add(RoverGPSY_Text);
        niceText(RoverGPSY_Text);
        TelemPanel.add(LeaderGPSX_Lable);
        TelemPanel.add(LeaderGPSX_Text);
        niceText(LeaderGPSX_Text);
        TelemPanel.add(LeaderGPSY_Lable);
        TelemPanel.add(LeaderGPSY_Text);
        niceText(LeaderGPSY_Text);
        TelemPanel.add(Distance_Lable);
        TelemPanel.add(Distance_Text);
        niceText(Distance_Text);
        TelemPanel.add(Deg2Target_Lable);
        TelemPanel.add(Deg2Target_Text);
        niceText(Deg2Target_Text);
        TelemPanel.setLayout(new GridLayout(6, 2));

        ButtonsPanel.add(Startbutton);

        ButtonsPanel.setLayout(new GridLayout(1, 2));

        //Main Panel
        GridBagConstraints MainPanelconst = new GridBagConstraints();
        MainPanel.setLayout(new GridBagLayout());

        MainTitle.setFont(new Font(MainTitle.getFont().getName(), Font.ITALIC, 35));
        MainPanelconst.gridx = 0;
        MainPanelconst.gridy = 0;
        MainPanelconst.weightx = 1;
        MainPanelconst.weighty = 0.5;
        MainPanelconst.insets = new Insets(3, 3, 3, 3);
        MainPanelconst.anchor = GridBagConstraints.CENTER;
        MainPanel.add(MainTitle, MainPanelconst);

        MainPanelconst.gridx = 0;
        MainPanelconst.gridy = 1;
        MainPanelconst.insets = new Insets(20, 50, 20, 50);
        MainPanelconst.fill = GridBagConstraints.BOTH;
        MainPanelconst.weightx = 1;
        MainPanelconst.weighty = 0.1;
        MainPanelconst.anchor = GridBagConstraints.NORTHWEST;
        MainPanel.add(ButtonsPanel, MainPanelconst);

        MainPanelconst.gridx = 0;
        MainPanelconst.gridy = 2;
        MainPanelconst.weightx = 1;
        MainPanelconst.weighty = 20;
        MainPanelconst.anchor = GridBagConstraints.NORTHWEST;
        MainPanel.add(TelemPanel, MainPanelconst);

        MainPanelconst.gridx = 0;
        MainPanelconst.gridy = 3;
        MainPanelconst.weightx = 0.01;
        MainPanelconst.weighty = 0.01;
        MainPanelconst.anchor = GridBagConstraints.NORTHWEST;
        MainPanel.add(TimeLabel, MainPanelconst);

        window.add(MainPanel);

        window.setVisible(true);
    }


    public static void niceText(JTextField theTextField) {
        theTextField.setEditable(false);
        theTextField.setBackground(Color.WHITE);
        theTextField.setHorizontalAlignment(theTextField.CENTER);
    }


//    @SuppressWarnings("serial")
}
