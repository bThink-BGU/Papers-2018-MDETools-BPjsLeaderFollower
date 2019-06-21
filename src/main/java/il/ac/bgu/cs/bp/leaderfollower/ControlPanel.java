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


public class ControlPanel {
    private final JFrame window = new JFrame("Robot Control Panel");
    public JButton button = new JButton();
    public JButton Startbutton = new JButton();
    public JLabel message = new JLabel();
    public JLabel TimeLabel = new JLabel();
    public Long TimeLabelint = new Long(0);

    JPanel ButtonsPanel;
    JPanel MainPanel;
    public JLabel MainTitle = new JLabel("GameControl");
    JPanel TelemPanel;
    public JLabel PlayerGpsX_Lable = new JLabel("Player Gps X:        ");
    public JLabel PlayerGpsY_Lable = new JLabel("Player Gps Y:        ");
    public JLabel OpponentGpsX_Lable = new JLabel("Opponent Gps X:        ");
    public JLabel OpponentGpsY_Lable = new JLabel("Opponent Gps Y:        ");
    public JLabel Distance2Ball_Lable = new JLabel("Distance To Ball:        ");
    public JLabel Deg2Ball_Lable = new JLabel("Degree 2 Ball:        ");
    public JLabel Deg2Gate_Lable = new JLabel("Degree 2 Gate:        ");

    public JTextField PlayerGpsX_Text = new JTextField("-----", 8);
    public JTextField PlayerGpsY_Text = new JTextField("-----", 8);
    public JTextField OpponentGpsX_Text = new JTextField("-----", 8);
    public JTextField OpponentGpsY_Text = new JTextField("-----", 8);
    public JTextField Distance2Ball_Text = new JTextField("-----", 8);
    public JTextField Deg2Ball_Text = new JTextField("-----", 8);
    public JTextField Deg2Gate_Text = new JTextField("-----", 8);

    public ControlPanel(BProgram bp) {
        button = new JButton();
        Startbutton = new JButton();

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
                    bp.enqueueExternalEvent(StaticEvents.START_CONTROL);
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
                bp.enqueueExternalEvent(StaticEvents.START_CONTROL);
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

        TelemPanel.add(PlayerGpsX_Lable);
        TelemPanel.add(PlayerGpsX_Text);
        niceText(PlayerGpsX_Text);
        TelemPanel.add(PlayerGpsY_Lable);
        TelemPanel.add(PlayerGpsY_Text);
        niceText(PlayerGpsY_Text);
        TelemPanel.add(OpponentGpsX_Lable);
        TelemPanel.add(OpponentGpsX_Text);
        niceText(OpponentGpsX_Text);
        TelemPanel.add(OpponentGpsY_Lable);
        TelemPanel.add(OpponentGpsY_Text);
        niceText(OpponentGpsY_Text);
        TelemPanel.add(Distance2Ball_Lable);
        TelemPanel.add(Distance2Ball_Text);
        niceText(Distance2Ball_Text);
        TelemPanel.add(Deg2Ball_Lable);
        TelemPanel.add(Deg2Ball_Text);
        niceText(Deg2Ball_Text);
        TelemPanel.add(Deg2Gate_Lable);
        TelemPanel.add(Deg2Gate_Text);
        niceText(Deg2Gate_Text);
        TelemPanel.setLayout(new GridLayout(7, 2));

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
