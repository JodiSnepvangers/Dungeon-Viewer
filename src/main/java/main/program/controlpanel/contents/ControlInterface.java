package main.program.controlpanel.contents;

import main.program.ProgramHandler;
import main.program.controlpanel.ControlPanelFrame;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ControlInterface extends JPanel {

    ControlPanelFrame mainControls;

    public static final int defaultZoom = 100;
    public static final int defaultBrush = 30;

    public static final int genericComponentWidth = 130;
    public static final int genericComponentHeight = 62;

    public static final Color backgroundColor = new Color(35, 35, 35, 255);

    /**
     * holds all the controls for the control panel!
     * only holds visuals!
     *
     */


    public CommitPanel commitPanel = new CommitPanel();
    public FloorPanel floorPanel = new FloorPanel();
    public SliderPanel sliderPanel = new SliderPanel();
    public SelectorPanel selectorPanel = new SelectorPanel();
    public FogCheckerBox fogCheckerBox = new FogCheckerBox();

    public ControlInterface(ControlPanelFrame mainControls){
        this.mainControls = mainControls;

        //set panel parameters
        setPreferredSize(new Dimension(500, 80));
        setMaximumSize(new Dimension(500, 200));
        setBackground(backgroundColor);
        //setLayout(new GridLayout(0, 20));
        setLayout(new FlowLayout());

        //add components
        add(commitPanel);
        add(floorPanel);
        add(sliderPanel);
        add(selectorPanel);
        add(fogCheckerBox);
    }

    public void setFloorChangesAllowed(boolean floorChangesAllowed){
        floorPanel.updateButtons(floorChangesAllowed);
    }















    /**
     *
     * set up panel classes!
     * CommitPanel: holds the commit and rollback button
     * FloorPanel: controller of floor layer
     * SliderPanel: holds slider components
     * SelectorPanel: holds all different brush options
     * FogCheckerBox: holds checkerbox for fog of war in general. also holds icon
     *
     * TODO: make a way to switch on or off the floor change feature!
     */



    public class CommitPanel extends JPanel{

        private final String commitText = "Commit";
        private final String undoText = "Rollback";
        private final String confirmText = "Are you sure?";

        public JButton commitButton = new JButton(commitText);
        public JButton rollBackButton = new JButton(undoText);

        public CommitPanel(){
            //set up component parameters
            commitButton.setPreferredSize(new Dimension( 110, 30));
            commitButton.setFont(new Font("Default", 0, 14));
            commitButton.setFocusable(false);

            rollBackButton.setPreferredSize(new Dimension( 110, 30));
            rollBackButton.setFont(new Font("Default", 0, 14));
            rollBackButton.setFocusable(false);

            //set panel parameters:
            setPreferredSize(new Dimension(genericComponentWidth, genericComponentHeight));
            setLayout(new GridLayout(2, 0));
            setBackground(backgroundColor);

            //add components to panel
            add(commitButton);
            add(rollBackButton);



            //add mouse listener
            commitButton.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {}
                @Override
                public void mousePressed(MouseEvent e) {
                    if(e.getButton() != 1) return; //ensure its a left mouse button
                    if(commitButton.getText() == commitText){ //check if button says its original text
                        commitButton.setText(confirmText); //if yes, replace it with confirm text
                    } else {
                        mainControls.buttonCommit(); //if not, user pressed a second time!
                        commitButton.setText(commitText);
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {}
                @Override
                public void mouseEntered(MouseEvent e) {}
                @Override
                public void mouseExited(MouseEvent e) {
                    commitButton.setText(commitText);
                }
            });

            rollBackButton.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {}
                @Override
                public void mousePressed(MouseEvent e) {
                    if(e.getButton() != 1) return; //ensure its a left mouse button
                    if(rollBackButton.getText() == undoText){ //check if button says its original text
                        rollBackButton.setText(confirmText); //if yes, replace it with confirm text
                    } else {
                        mainControls.buttonRollback(); //if not, user pressed a second time!
                        rollBackButton.setText(undoText);
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {}
                @Override
                public void mouseEntered(MouseEvent e) {}
                @Override
                public void mouseExited(MouseEvent e) {
                    rollBackButton.setText(undoText);
                }
            });
        }


    }

    public class FloorPanel extends JPanel{

        public JButton previousFloor = new JButton("\\/");
        public JLabel floorIndicator = new JLabel("0", SwingConstants.CENTER);
        public JButton nextFloor = new JButton("/\\");
        public JLabel floorText = new JLabel("Floor", SwingConstants.CENTER);

        Color buttonColor = previousFloor.getBackground();
        Color disablesColor = new Color(102, 25, 25);

        public FloorPanel(){
            //set up component parameters:
            previousFloor.setFont(new Font("Default", 0, 20));
            previousFloor.setPreferredSize(new Dimension(40, 40));
            previousFloor.setFocusable(false);

            floorIndicator.setOpaque(true);
            floorIndicator.setBackground(Color.black);
            floorIndicator.setFont(new Font("Default", 0, 15));
            floorIndicator.setPreferredSize(new Dimension(30, 30));

            nextFloor.setFont(new Font("Default", 0, 20));
            nextFloor.setPreferredSize(new Dimension(40, 40));
            nextFloor.setFocusable(false);

            floorText.setOpaque(false);
            floorText.setBackground(disablesColor);
            floorText.setFont(new Font("Default", 0, 15));
            floorText.setPreferredSize(new Dimension(30, 30));

            //set panel parameters:
            setPreferredSize(new Dimension(genericComponentWidth - 20, genericComponentHeight - 10));
            setLayout(new BorderLayout());
            setBackground(backgroundColor);

            //add components center
            add(floorText, BorderLayout.NORTH);
            add(previousFloor, BorderLayout.WEST);
            add(floorIndicator, BorderLayout.CENTER);
            add(nextFloor, BorderLayout.EAST);

            //add mouse listeners
            previousFloor.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {}
                @Override
                public void mousePressed(MouseEvent e) {
                    if(e.getButton() != 1) return; //ensure its a left mouse button
                    if(previousFloor.isEnabled()){
                        mainControls.buttonPreviousFloor();
                    } else {
                        floorText.setText("Commit or Undo");
                    }

                }
                @Override
                public void mouseReleased(MouseEvent e) {}
                @Override
                public void mouseEntered(MouseEvent e) {}
                @Override
                public void mouseExited(MouseEvent e) {
                    floorText.setText("Floor");
                }
            });

            nextFloor.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {}
                @Override
                public void mousePressed(MouseEvent e) {
                    if(e.getButton() != 1) return; //ensure its a left mouse button
                    if(nextFloor.isEnabled()){
                        mainControls.buttonNextFloor();
                    } else {
                        floorText.setText("Commit or Undo");
                    }

                }
                @Override
                public void mouseReleased(MouseEvent e) {}
                @Override
                public void mouseEntered(MouseEvent e) {}
                @Override
                public void mouseExited(MouseEvent e) {
                    floorText.setText("Floor");
                }
            });
        }

        public void updateButtons(boolean state){
            previousFloor.setEnabled(state);
            nextFloor.setEnabled(state);
            Color disablesColor = new Color(102, 25, 25);
            if(state){
                //buttons are enables
                previousFloor.setOpaque(false);
                previousFloor.setBackground(buttonColor);
                nextFloor.setOpaque(false);
                nextFloor.setBackground(buttonColor);
                floorText.setOpaque(false);
                ControlInterface.this.repaint();
            } else {
                previousFloor.setOpaque(true);
                previousFloor.setBackground(disablesColor);
                nextFloor.setOpaque(true);
                nextFloor.setBackground(disablesColor);
                floorText.setOpaque(true);
                ControlInterface.this.repaint();
            }
        }
    }

    public class SliderPanel extends JPanel{

        public JSlider brushSlider = new JSlider(2, 80, defaultBrush);
        public JSlider zoomSlider = new JSlider(20, 400, defaultZoom);

        public JLabel zoomLabel = new JLabel("Zoom level: " + defaultZoom + "%");
        public JLabel brushLabel = new JLabel("Brush size: " + defaultBrush);

        public SliderPanel(){

            //set panel parameters:
            setPreferredSize(new Dimension(genericComponentWidth, genericComponentHeight));
            setLayout(new GridLayout(4, 0));
            setBackground(backgroundColor);

            //add components to panel
            add(zoomLabel);
            add(zoomSlider);
            add(brushLabel);
            add(brushSlider);

            //add update listeners to sliders:
            zoomSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    mainControls.zoomSliderUpdate(zoomSlider.getValue());
                    zoomLabel.setText("Zoom level: " + zoomSlider.getValue() + "%");
                }
            });
            brushSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    mainControls.brushSliderUpdate(brushSlider.getValue());
                    brushLabel.setText("Brush size: " + brushSlider.getValue());
                }
            });
        }


    }

    public class SelectorPanel extends JPanel{
        String squareName = "Square Select";
        String boxName = "Box Brush";
        String circleName = "Circle Brush";

        JRadioButton squareSelector = new JRadioButton(squareName);
        JRadioButton boxBrushSelector = new JRadioButton(boxName);
        JRadioButton circleBrushSelector = new JRadioButton(circleName);

        public SelectorPanel(){
            //set component parameters:
            squareSelector.setSelected(true);
            squareSelector.setFocusable(false);

            boxBrushSelector.setFocusable(false);

            circleBrushSelector.setFocusable(false);

            //set panel parameters:
            setPreferredSize(new Dimension(genericComponentWidth - 30, genericComponentHeight));
            setLayout(new GridLayout(3, 0));
            setBackground(backgroundColor);

            //add components
            add(squareSelector);
            add(boxBrushSelector);
            add(circleBrushSelector);

            //group up buttons
            ButtonGroup group = new ButtonGroup();
            group.add(squareSelector);
            group.add(boxBrushSelector);
            group.add(circleBrushSelector);

            //create universal listener
            ActionListener actionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String buttonName = e.getActionCommand();

                    //checks button name against input button! outputs the id of that button!
                    BrushSelect brushSelect = null;
                    if(buttonName == squareName){brushSelect = BrushSelect.SQUARESELECT;}
                    else if(buttonName == boxName){brushSelect = BrushSelect.BOXBRUSH;}
                    else if(buttonName == circleName){brushSelect = BrushSelect.CIRCLEBRUSH;}

                    //error checking!
                    if(brushSelect == null){
                        //this should never run!
                        ProgramHandler.tossError("Incorrect brush was passed: " + buttonName, false);
                        brushSelect = BrushSelect.SQUARESELECT;
                    }

                    //output to control frame
                    mainControls.updateBrushSelect(brushSelect);
                }
            };

            //add action listener to all buttons
            squareSelector.addActionListener(actionListener);
            boxBrushSelector.addActionListener(actionListener);
            circleBrushSelector.addActionListener(actionListener);
        }

    }

    public enum BrushSelect{
        SQUARESELECT,
        BOXBRUSH,
        CIRCLEBRUSH
    }

    public class FogCheckerBox extends JPanel{

        public JCheckBox fogCheckBox = new JCheckBox("Fog of War");
        public JLabel iconLabel = new JLabel();

        ImageIcon icon = new ImageIcon(getClass().getResource("/CatIcon.png"));
        ImageIcon iconQuestion = new ImageIcon(getClass().getResource("/CatQuestionIcon.png"));

        public FogCheckerBox(){
            //set component parameters:
            fogCheckBox.setSelected(true);
            fogCheckBox.setFocusable(false);


            iconLabel.setIcon(icon);

            //set panel parameters:
            setPreferredSize(new Dimension(genericComponentWidth - 20, genericComponentHeight + 11));
            setLayout(new GridLayout(2, 0));
            setBackground(backgroundColor);

            //add components
            add(fogCheckBox);
            add(iconLabel);

            //add action listeners
            fogCheckBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mainControls.checkerBoxUpdate(fogCheckBox.isSelected());
                }
            });

            //add mouse listeners:
            iconLabel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {
                    mainControls.tutorialRequested();
                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    iconLabel.setIcon(iconQuestion);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    iconLabel.setIcon(icon);
                }
            });
        }
    }
}
