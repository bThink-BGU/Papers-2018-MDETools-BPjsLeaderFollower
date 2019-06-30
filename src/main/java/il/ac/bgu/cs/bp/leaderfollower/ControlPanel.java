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

import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class ControlPanel {
    private final JFrame window = new JFrame("Robot Control Panel");
    public JButton button = new JButton();
    public JButton Startbutton = new JButton();
    public JLabel message = new JLabel();
    public JLabel TimeLabel = new JLabel();
    public Long TimeLabelint = Long.valueOf(0);

    JPanel ButtonsPanel;
    JPanel MainPanel;
    public JLabel MainTitle = new JLabel("GameControl");
    JPanel TelemPanel;
    public JLabel PlayerGpsX_Lable = new JLabel("Player Gps X:        ");
    public JLabel PlayerGpsZ_Lable = new JLabel("Player Gps Z:        ");
    public JLabel BallGpsX_Lable = new JLabel("Ball Gps X:        ");
    public JLabel BallGpsZ_Lable = new JLabel("Ball Gps Z:        ");
    public JLabel Distance2Ball_Lable = new JLabel("Distance To Ball:        ");
    public JLabel PlayerDegree_Lable = new JLabel("Player Degree:        ");
    public JLabel Deg2Ball_Lable = new JLabel("Degree 2 Ball:        ");
    public JLabel Deg2Gate_Lable = new JLabel("Degree 2 Gate:        ");
    public JTextField PlayerGpsX_Text = new JTextField("-----", 8);
    public JTextField PlayerGpsZ_Text = new JTextField("-----", 8);
    public JTextField BallGpsX_Text = new JTextField("-----", 8);
    public JTextField BallGpsZ_Text = new JTextField("-----", 8);
    public JTextField Distance2Ball_Text = new JTextField("-----", 8);
    public JTextField PlayerDegree_Text = new JTextField("-----", 8);
    public JTextField Deg2Ball_Text = new JTextField("-----", 8);
    public JTextField Deg2Gate_Text = new JTextField("-----", 8);
    
    JPanel RefereePanel;
    public JLabel BallPosession_Label = new JLabel("Ball Posession:        ");
    public JLabel Timeout_Label = new JLabel("Timeout:        ");
    public JLabel Scoring_Label = new JLabel("Scoring:        ");
    public JLabel EndOfGame_Label = new JLabel("End of Game:        ");
    public JTextField BallPosession_Text = new JTextField("-----", 8);
    public JTextField Timeout_Text = new JTextField("-----", 8);
    public JTextField Scoring_Text = new JTextField("-----", 8);
    public JTextField EndOfGame_Text = new JTextField("-----", 8);
    
    public ControlPanel(BProgram bp) {
        button = new JButton();
        Startbutton = new JButton();

        // Create window
        window.setSize(800, 400);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new BorderLayout());
        window.setLocation(new Point(500, 100));
        MainPanel = new JPanel();
        ButtonsPanel = new JPanel();;
        TelemPanel = new JPanel();
        RefereePanel = new JPanel();
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
                    bp.enqueueExternalEvent(new BEvent("Start Control"));
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
                bp.enqueueExternalEvent(new BEvent("Start Control"));
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

        RefereePanel.add(BallPosession_Label);
        RefereePanel.add(BallPosession_Text);
        niceText(BallPosession_Text);
        RefereePanel.add(Timeout_Label);
        RefereePanel.add(Timeout_Text);
        niceText(Timeout_Text);
        RefereePanel.add(Scoring_Label);
        RefereePanel.add(Scoring_Text);
        niceText(Scoring_Text);
        RefereePanel.add(EndOfGame_Label);
        RefereePanel.add(EndOfGame_Text);
        niceText(EndOfGame_Text);
        RefereePanel.setLayout(new GridLayout(4, 2));

        TelemPanel.add(PlayerGpsX_Lable);
        TelemPanel.add(PlayerGpsX_Text);
        niceText(PlayerGpsX_Text);
        TelemPanel.add(PlayerGpsZ_Lable);
        TelemPanel.add(PlayerGpsZ_Text);
        niceText(PlayerGpsZ_Text);
        TelemPanel.add(BallGpsX_Lable);
        TelemPanel.add(BallGpsX_Text);
        niceText(BallGpsX_Text);
        TelemPanel.add(BallGpsZ_Lable);
        TelemPanel.add(BallGpsZ_Text);
        niceText(BallGpsZ_Text);
        TelemPanel.add(Distance2Ball_Lable);
        TelemPanel.add(Distance2Ball_Text);
        niceText(Distance2Ball_Text);
        TelemPanel.add(PlayerDegree_Lable);
        TelemPanel.add(PlayerDegree_Text);
        niceText(PlayerDegree_Text);
        TelemPanel.add(Deg2Ball_Lable);
        TelemPanel.add(Deg2Ball_Text);
        niceText(Deg2Ball_Text);
        TelemPanel.add(Deg2Gate_Lable);
        TelemPanel.add(Deg2Gate_Text);
        niceText(Deg2Gate_Text);
        TelemPanel.setLayout(new GridLayout(8, 2));

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
        MainPanelconst.weightx = 0.5;
        MainPanelconst.weighty = 20;
        MainPanelconst.anchor = GridBagConstraints.NORTHWEST;
        MainPanel.add(TelemPanel, MainPanelconst);
        
        MainPanelconst.gridx = 1;
        MainPanelconst.gridy = 2;
        MainPanelconst.weightx = 0.5;
        MainPanelconst.weighty = 10;
        MainPanelconst.anchor = GridBagConstraints.NORTHWEST;
        MainPanel.add(RefereePanel, MainPanelconst);

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
        theTextField.setHorizontalAlignment(SwingConstants.CENTER);
    }
}
