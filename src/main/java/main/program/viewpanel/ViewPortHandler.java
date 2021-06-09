package main.program.viewpanel;

import main.program.viewpanel.contents.ViewPortImageDisplay;
import main.program.viewpanel.contents.ViewPortImageHandler;
import main.program.viewpanel.contents.icons.IconManager;
import main.program.viewpanel.controls.ViewPortControlFrame;

import java.awt.*;

public class ViewPortHandler implements Runnable {

    /**
     *
     * Viewport handler. handles the communication to and from the viewport
     * will also
     *
     */

    ViewPortFrame viewPort; //actual viewport frame

    ViewPortImageHandler imageManager; //image handler that handles both shadow layer and image layer

    ViewPortControlFrame controlPanel; //view port control panel
    int currentFloorDisplayed = 0;

    IconManager iconManager; //counter/icon manager of viewport


    @Override
    public void run() {
        //generate modules on run:
        imageManager = new ViewPortImageHandler();
        viewPort = new ViewPortFrame(imageManager);
        imageManager.initialiseManager(this);
        controlPanel = new ViewPortControlFrame(this);
        iconManager = imageManager.getDrawingLabel().getIconManager();
    }

    /**
     *
     * sends request to repaint image. is ignored if image isnt currently on display
     */

    public void repaintFloor(int floorNumber){
        if(floorNumber == currentFloorDisplayed){
            //curent displayed floor needs update!
            imageManager.updateFloorContainer(floorNumber);
        }
    }

    /**
     *
     * gets frame of viewport!
     *
     */
    public ViewPortFrame getViewPort() {
        return viewPort;
    }

    /**
     *
     * viewport control panel methods:
     *
     *
     */

    /**
     *
     * called when update to floor is requested by the user
     *
     */

    public void confirmFloorChange(int newFloor){
        iconManager.floorChange(newFloor); //sends warning to icon manager to save and load its data
        currentFloorDisplayed = newFloor; //updates displayed floor number
        imageManager.updateFloorContainer(newFloor); //updates floor displayed on viewport
        imageManager.getDrawingLabel().fadeOutAll(); //fades out and then clears all custom drawn lines
    }

    /**
     *
     * called when a zoom change is requested!
     *
     */

    public void setZoom(double zoomValue){
        imageManager.setZoomValue(zoomValue);
    }

    /**
     *
     * can be used to update slider from outside
     *      */

    public void updateZoomSlider(double zoomValue){
        controlPanel.setZoom(zoomValue);
    }

    /**
     *
     * enables or disables zoom guide corners
     *
     */

    public void showCorners(boolean showCorners){
        imageManager.setShowCorners(showCorners);
    }

    /**
     *
     * selects brush color and clears selected color
     *
     */

    public void setPaintColor(Color color){
        imageManager.getDrawingLabel().setDrawingColor(color);
    }

    public void clearCurrentPaint(Color color){
        imageManager.getDrawingLabel().clearColor(color);
    }
}
