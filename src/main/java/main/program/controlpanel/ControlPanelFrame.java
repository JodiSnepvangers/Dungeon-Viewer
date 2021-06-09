package main.program.controlpanel;

import main.Main;
import main.program.FloorContainer;
import main.program.JFrameAutomaticWindow;
import main.program.ProgramHandler;
import main.program.controlpanel.contents.ControlInterface;
import main.program.controlpanel.contents.PaintingArea;
import main.program.properties.PropertyStorage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ControlPanelFrame extends JFrameAutomaticWindow {
    /**
     * creates and handles the control panel frame, showing and controlling DM interface
     *
     *
     */


    ControlInterface controlPanel; //control panel interface!
    PaintingArea paintingArea; //actual painting area

    int floorNumber = 0; //floor number the edit panel is currently set at
    ProgramHandler programHandler; //parent program handler, for calls

    public ControlPanelFrame(ProgramHandler programHandler){
        //retrieve program handler
        this.programHandler = programHandler;


        //set frame parameters!
        BorderLayout layout = new BorderLayout();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(layout);
        setTitle(Main.programName + ": Shadow Port");

        //setup automatic window system:
        initialiseAutomaticWindow(PropertyStorage.WindowType.CONTROLPORT);

        //create components:
        controlPanel = new ControlInterface(this);
        paintingArea = new PaintingArea(this);

        //add components to panel
        add(controlPanel, BorderLayout.NORTH);
        add(paintingArea, BorderLayout.CENTER);

        //make panel visible
        setVisible(true);




        //set default parameters of painting area:
        paintingArea.setBrushSize(ControlInterface.defaultBrush);
        paintingArea.setSelectedBrush(ControlInterface.BrushSelect.SQUARESELECT);

        //set current floor to 0 in the painting area
        paintingArea.updateFloorContainer(0);

        //retrieve panel components from control panel:
        floorIndicator = controlPanel.floorPanel.floorIndicator;
        zoomSlider = controlPanel.sliderPanel.zoomSlider;
        brushSlider = controlPanel.sliderPanel.brushSlider;
        shadowCheckBox = controlPanel.fogCheckerBox.fogCheckBox;


        //add event listeners:
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                paintingArea.updateImagePosition();
            }
        });

        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
               paintingArea.disableRefresh(true);
            }

            @Override
            public void windowGainedFocus(WindowEvent e) {
                paintingArea.disableRefresh(false);
            }
        });


    }

    public void updateFloorNumber(boolean resetView) {
        //updates floor painted on paint area with the floor number
        paintingArea.updateFloorContainer(floorNumber);
        floorIndicator.setText(floorNumber + "");
        shadowCheckBox.setSelected(paintingArea.getFloorContainer().isShadowLayerEnabled());

        if(resetView == false)return; //view should not be reset!
        paintingArea.updateImagePosition();
        paintingArea.resetZoomValue();
        setZoomSlider(ControlInterface.defaultZoom);
    }

    public void floorFunctionEnabled(boolean state){
        controlPanel.setFloorChangesAllowed(state);
    }









    //add control panel controls

    //static components:
    JLabel floorIndicator;
    JSlider zoomSlider;
    JSlider brushSlider;
    JCheckBox shadowCheckBox;

    /**
     * Here are all buttons and slider provided by the UI, each called when the button is pressed or the slider is updated
     *
     *
     *
     *
     *
     *
     * goes back a floor!
     */
    public void buttonPreviousFloor(){
        if(!(ProgramHandler.floorList.containsKey(floorNumber - 1)))return; //previous floor does not exist
        floorNumber--;
        updateFloorNumber(true);
    }

    /**
     * goes forward a floor!
     */
    public void buttonNextFloor(){
        if(!(ProgramHandler.floorList.containsKey(floorNumber + 1)))return; //next floor does not exist
        floorNumber++;
        updateFloorNumber(true);
    }

    /**
     * commits to viewport!
     */
    public void buttonCommit(){
        FloorContainer oldContainer = ProgramHandler.floorList.get(floorNumber);
        oldContainer.replaceFloorContainer(paintingArea.getFloorContainer());
        ProgramHandler.floorList.get(floorNumber).setShadowLayerEnabled(paintingArea.isShadowEnabled());
        floorFunctionEnabled(true);
        updateFloorNumber(false);
        programHandler.sendFloorUpdate(floorNumber);
    }

    /**
     * roll back to previous state (before commit)
     *
     */

    public void buttonRollback(){
        updateFloorNumber(false);
        floorFunctionEnabled(true);
    }

    /**
     * called when zoom slider is updated
     */

    public void zoomSliderUpdate(int value){
        paintingArea.setZoomValue((double) value / 100);
    }

    /**
     * called when brush slider is updated
     */

    public void brushSliderUpdate(int value){
        paintingArea.setBrushSize(value);
    }

    /**
     * resets zoom slider to a new value.
     * calls zoomSliderUpdate
     *
     */

    public void resetZoomSlider(){
        setZoomSlider(ControlInterface.defaultZoom);
    }

    public void setZoomSlider(int newValue){
        zoomSlider.setValue(newValue);
    }

    /**
     * resets brush slider to a new value.
     * calls brushSliderUpdate
     *
     */

    public void resetBrushSlider(){
        setBrushSlider(ControlInterface.defaultBrush);
    }

    public void setBrushSlider(int newValue){
        brushSlider.setValue(newValue);
    }

    /**
     * called when brush select is updated, carrying a new brush to be used!
     *
     *
     */

    public void updateBrushSelect(ControlInterface.BrushSelect brushSelect){
        paintingArea.setSelectedBrush(brushSelect);
    }

    /**called when the checker box is updated:
     * fog of war enabled
     */

    public void checkerBoxUpdate(boolean state){
        paintingArea.setShadowEnabled(state);
    }

    /**
     *
     * called when tutorial is requested by clicking the icon
     *
     *
     */

    public void tutorialRequested(){
        System.out.println("tutorial");
    }
}
