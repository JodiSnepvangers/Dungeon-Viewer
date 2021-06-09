package main.program.viewpanel.controls;

import main.Main;
import main.program.ImageLoader;
import main.program.JFrameAutomaticWindow;
import main.program.ProgramHandler;
import main.program.controlpanel.contents.ControlInterface;
import main.program.properties.PropertyStorage;
import main.program.viewpanel.ViewPortHandler;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

public class ViewPortControlFrame extends JFrameAutomaticWindow {
    /**
     *
     * creates a small window to control the viewport with!
     * handles all interactions and visuals, and sends updates out to handler
     * TODO: only update image when a floor change is requested OR the current floor was edited!
     * TODO: become middleman betweem control panel and viewport
     *
     */

    ViewPortHandler handler; //parent handler to send updates to


    ZoomControls zoomControls = new ZoomControls();

    public ViewPortControlFrame(ViewPortHandler handler){
        //set internal variables:
        this.handler = handler;

        //set frame parameters:
        initialiseAutomaticWindow(PropertyStorage.WindowType.VIEWCONTROLS);
        setMaximumSize(new Dimension(500, 380));
        setLayout(new FlowLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle(Main.programName + ": Viewport Controls");

        //add components:
        add(new FloorControlPanel());
        add(zoomControls);
        add(new DrawControlPanel());




        //reveal window
        setVisible(true);

    }






    class FloorControlPanel extends JPanel{
        /**
         *
         * control panel for floor changes of viewport!
         *
         */
            public JButton previousFloor = new JButton("\\/");
            public JLabel floorIndicator = new JLabel("0", SwingConstants.CENTER);
            public JButton nextFloor = new JButton("/\\");
            public JLabel floorText = new JLabel("Floor", SwingConstants.CENTER);

            JButton confirmButton = new JButton("Confirm");



            int currentFloor = 0;
            int updatedFloor = 0;

        public FloorControlPanel(){
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
                floorText.setFont(new Font("Default", 0, 15));
                floorText.setPreferredSize(new Dimension(30, 30));

                //set panel parameters:
                setLayout(new BorderLayout());
                setBackground(ControlInterface.backgroundColor);
                setPreferredSize(new Dimension(120, 95));

                //add components center
                add(floorText, BorderLayout.NORTH);
                add(previousFloor, BorderLayout.WEST);
                add(floorIndicator, BorderLayout.CENTER);
                add(nextFloor, BorderLayout.EAST);
                add(confirmButton, BorderLayout.SOUTH);

                //add mouse listeners:
                previousFloor.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if(e.getButton() != 1)return; //wasnt left mouse button
                        if(updatedFloor <= ImageLoader.maxNegativeFloors)return; //lowest floor reached!
                        updatedFloor--;
                        updateFloorIndicator();
                    }
                });
                nextFloor.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if(e.getButton() != 1)return; //wasnt left mouse button
                        if(updatedFloor >= ImageLoader.maxPositiveFloors)return; //highest floor reached!
                        updatedFloor++;
                        updateFloorIndicator();
                    }
                });
                confirmButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if(e.getButton() != 1)return; //wasnt left mouse button
                        //TODO: send update out to handler
                        currentFloor = updatedFloor;
                        updateFloorIndicator();
                        handler.confirmFloorChange(currentFloor);
                    }
                });
        }

        private void updateFloorIndicator(){
            //changes floor indicator color when a floor change is done that is not confirmed!
            floorIndicator.setText(updatedFloor + "");
            if(currentFloor == updatedFloor){
                floorIndicator.setBackground(Color.black);
                floorIndicator.setForeground(Color.WHITE);
            } else {
                floorIndicator.setBackground(Main.cursorColor);
                floorIndicator.setForeground(Color.BLACK);
            }
        }
    }

    public class ZoomControls extends JPanel{
        /**
         *
         * holds zoom controls and zoom helper of viewport
         *
         */

        JSlider zoomSlider = new JSlider(20, 800, 100);
        JLabel zoomText = new JLabel("Zoom: 100%", SwingConstants.CENTER);
        JCheckBox guideCheckBox = new JCheckBox("Zoom Guide");
        JButton resetZoom = new JButton("Reset");

        ZoomControls(){
            //set component parameters:
            zoomText.setFont(new Font("Default", 0, 14));

            //set panel parameters:
            setLayout(new GridLayout(4, 0));
            setBackground(ControlInterface.backgroundColor);


            //add components:
            add(zoomText);
            add(zoomSlider);
            add(guideCheckBox);
            add(resetZoom);

            //handle listeners:
            zoomSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    zoomText.setText("Zoom: " + zoomSlider.getValue() + "%");
                    handler.setZoom((double)zoomSlider.getValue() / 100);
                }
            });

            resetZoom.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    zoomSlider.setValue(100);
                }
            });
            guideCheckBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handler.showCorners(guideCheckBox.isSelected());
                    System.out.println(guideCheckBox.isSelected());
                }
            });
        }

        public void setZoomSlider(double zoomValue){
            int zoomNumber = (int) (zoomValue * 100);
            zoomSlider.setValue(zoomNumber);
        }
    }

    public void setZoom(double zoom){
        zoomControls.setZoomSlider(zoom);
    }

    class DrawControlPanel extends JPanel{
        /**
         *
         * holds draw controls for interactive layer
         * holds a clear button that clears the current color off the interactive layer! (might need multiple layers?)
         *
         */

        ColorPickerPanel colorPicker = new ColorPickerPanel();
        JButton clearButton = new JButton("Clear");

        public DrawControlPanel(){

            //set up panel parameters:
            setLayout(new BorderLayout());
            setBackground(ControlInterface.backgroundColor);
            setPreferredSize(new Dimension(150, 100));

            //add components:
            add(colorPicker, BorderLayout.EAST);
            add(clearButton, BorderLayout.CENTER);

            //add mouse listener:
            clearButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    handler.clearCurrentPaint(currentSelected);
                }
            });
        }
    }

    public enum AvailableColors {
        RED(Color.RED),
        ORANGE(Color.ORANGE),
        YELLOW(Color.YELLOW),
        GREEN(Color.GREEN),
        BLUE(Color.BLUE),
        PURPLE(new Color(146, 44, 179)),
        WHITE(Color.WHITE),
        BLACK(Color.BLACK);

        public final Color color;

        AvailableColors(Color color) {
            this.color = color;
        }

        public Color getColor(){
            return color;
        }
    }

    private Color currentSelected = Color.white;

    class ColorPickerPanel extends JPanel{
        //holds several different color of marker for DrawControlPanel

        JButton redButton = new JButton();
        JButton orangeButton = new JButton();
        JButton yellowButton = new JButton();
        JButton greenButton = new JButton();
        JButton blueButton = new JButton();
        JButton purpleButton = new JButton();
        JButton whiteButton = new JButton();
        JButton blackButton = new JButton();

        public ColorPickerPanel() {
            //set component parameters:
            redButton.setBackground(AvailableColors.RED.getColor());
            orangeButton.setBackground(AvailableColors.ORANGE.getColor());
            yellowButton.setBackground(AvailableColors.YELLOW.getColor());
            greenButton.setBackground(AvailableColors.GREEN.getColor());
            blueButton.setBackground(AvailableColors.BLUE.getColor());
            purpleButton.setBackground(AvailableColors.PURPLE.getColor());
            whiteButton.setBackground(AvailableColors.WHITE.getColor());
            blackButton.setBackground(AvailableColors.BLACK.getColor());

            redButton.setFocusable(false);
            orangeButton.setFocusable(false);
            yellowButton.setFocusable(false);
            greenButton.setFocusable(false);
            blueButton.setFocusable(false);
            purpleButton.setFocusable(false);
            whiteButton.setFocusable(false);
            blackButton.setFocusable(false);

            //debug
            whiteButton.setBorder(new LineBorder(Main.cursorColor, 2));

            //set panel parameters
            setPreferredSize(new Dimension(60, 80));
            setLayout(new GridLayout(4, 2));

            //add components:
            add(redButton);
            add(orangeButton);
            add(yellowButton);
            add(greenButton);
            add(blueButton);
            add(purpleButton);
            add(whiteButton);
            add(blackButton);

            //add mouse listener:
            redButton.addMouseListener(mouseListener);
            orangeButton.addMouseListener(mouseListener);
            yellowButton.addMouseListener(mouseListener);
            greenButton.addMouseListener(mouseListener);
            blueButton.addMouseListener(mouseListener);
            purpleButton.addMouseListener(mouseListener);
            whiteButton.addMouseListener(mouseListener);
            blackButton.addMouseListener(mouseListener);

        }

        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                AvailableColors selectedColor = null;
                JButton selectedButton = (JButton) e.getComponent();

                //check which button got pressed
                if(selectedButton == redButton){ selectedColor = AvailableColors.RED; }
                else if(selectedButton == orangeButton){ selectedColor = AvailableColors.ORANGE; }
                else if(selectedButton == yellowButton){ selectedColor = AvailableColors.YELLOW; }
                else if(selectedButton == greenButton){ selectedColor = AvailableColors.GREEN; }
                else if(selectedButton == blueButton){ selectedColor = AvailableColors.BLUE; }
                else if(selectedButton == purpleButton){ selectedColor = AvailableColors.PURPLE; }
                else if(selectedButton == whiteButton){ selectedColor = AvailableColors.WHITE; }
                else if(selectedButton == blackButton){ selectedColor = AvailableColors.BLACK; }

                //if a unknown button triggered this, do nothing!
                if(selectedColor == null){
                    System.out.println("A ILLEGAL COLOR WAS ACCESSED!");
                    return;
                }

                //known button was pressed. change current selected color!
                //set all borders to null!
                redButton.setBorder(null);
                orangeButton.setBorder(null);
                yellowButton.setBorder(null);
                greenButton.setBorder(null);
                blueButton.setBorder(null);
                purpleButton.setBorder(null);
                whiteButton.setBorder(null);
                blackButton.setBorder(null);

                //set selected border special:
                selectedButton.setBorder(new LineBorder(Main.cursorColor, 2));

                //send selected color to handler:
                //TODO: make adding buttons fully automatic
                handler.setPaintColor(selectedColor.color);
                currentSelected = selectedColor.color;

            }
        };

    }

}
