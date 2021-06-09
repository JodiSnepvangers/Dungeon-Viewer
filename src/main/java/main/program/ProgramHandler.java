package main.program;

import main.program.controlpanel.ControlPanelFrame;
import main.program.viewpanel.ViewPortHandler;

import javax.swing.*;
import java.util.HashMap;

public class ProgramHandler {
    /**
     * creates the windows and deals with the communication between them!
     * holds a library of loaded floors, aswell as the active floor for both the control port and view port
     *
     * runs on its own thread!
     *
     */
    public static HashMap<Integer, FloorContainer> floorList = new HashMap<>();

    ViewPortHandler viewPort = new ViewPortHandler();


    public ProgramHandler(){
        //generate floorList
        floorListInitialize();


        new ControlPanelFrame(this);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                viewPort.run();
            }
        });
    }



    public void sendFloorUpdate(int floorNumber){
        viewPort.repaintFloor(floorNumber);
    }





    private void floorListInitialize(){
        for(ImageLoader.ImageContainer image : ImageLoader.floorLibary.values()){
            int floorNumber = Integer.parseInt(image.name);
            FloorContainer floorContainer = new FloorContainer(image.image, floorNumber);
            floorList.put(floorNumber, floorContainer);
        }
    }
















    public static void tossError(String errorMessage, boolean fatalError){
        new ErrorMessage(errorMessage, fatalError);
    }


}
