package main.program.viewpanel.contents;

import main.Main;
import main.program.FloorContainer;
import main.program.ProgramHandler;
import main.program.viewpanel.ViewPortHandler;
import main.program.viewpanel.contents.icons.IconsViewPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import static main.Main.zoomModifier;

public class ViewPortImageHandler extends ZoomAndScrollController {

    /**
     *
     * image handler: automatically updates floor image and plays animations when nessecary
     *
     */

    int currentFloor = 0;


    ViewPortImageDisplay imageLayer = new ViewPortImageDisplay(); //displays the floor image
    ViewPortImageDisplay shadowLayer = new ViewPortImageDisplay();//displays the shadow layer

    ViewPortFadeDisplay ghostLayer = new ViewPortFadeDisplay(this); //creates a fading layer that deals with the fading animations

    DrawingLabel drawingLabel; //creates a drawing layer that can show off lines. handles its own refreshing. should be cleared on changing floors

    /**
     * initialises the manager. must be called before anything else can be done
     *
     */
    ViewPortHandler handler;

    public void initialiseManager(ViewPortHandler handler){
        //setup internal variables
        JFrame parentFrame = handler.getViewPort();
        this.handler = handler;

        //give parent frame to layers:
        imageLayer.initialise(parentFrame);
        shadowLayer.initialise(parentFrame);
        ghostLayer.initialise(parentFrame);

        //add mouse listeners to parent frame!
        setupMouseListeners(parentFrame, false);
        parentFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                windowResizeEvent(parentFrame);
            }
        });

        //update floor to 0
        updateFloorContainer(0, false);


        //add zoom scrollers manually to support zoom slider:
        parentFrame.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if(e.getWheelRotation() == 1){
                    //zoom out!
                    scrollWheelUpdate(false);
                } else {
                    //zoom in!
                    scrollWheelUpdate(true);
                }
            }
        });

    }

    /**
     *
     * updates current floor.
     * if it is the same floor, play fade in animation, else play fade out fade in global
     */

    boolean fadeBackIn = false; //fade back in boolean, to prevent loop when fading in new level

    public void updateFloorContainer(int floorNumber){
        updateFloorContainer(floorNumber, true);
    }

    public void updateFloorContainer(int floorNumber, boolean fadingAnimation){
        //if fading animation is requested:
        if(fadingAnimation){
            //if current floor is the same, fade out old copy of shadow layer!
            if(floorNumber == currentFloor){
                //ghost layer is given copy of shadow layer!
                ghostLayer.updateImage(shadowLayer.image);
                imageUpdateCall(); //call update to set shadow layer before fading in occurs. this makes it impossible to fade in fog of war!
                ghostLayer.startFade(0.08f, 20, true, false);
            } else {


                //FUCKING BROKE! no clue why. removing this crap another day. make bypass
                currentFloor = floorNumber;
                resetScrollZoom();
                imageUpdateCall();
                if(true)return;

                //play fade in fade out animation
                ghostLayer.updateBlackScreen(imageLayer.image.getWidth(), imageLayer.image.getHeight());
                ghostLayer.startFade(1.00f, 20, false, true);

                //set anti loop boolean:
                fadeBackIn = true;
                callFadeFinishUpdate();

                //update current floor
                currentFloor = floorNumber;
            }
        } else {
            //no animation to be played. do update directly:
            imageUpdateCall();
        }



        //update current floor value
        currentFloor = floorNumber;
    }

    /**
     *
     * called when fading is finished
     *
     */

    public void callFadeFinishUpdate(){
        if(fadeBackIn == false) return; //ignore call when not fading back in new level
        //used to time when faded screen stopped fading. change floor images and fade back!
        resetScrollZoom();
        imageUpdateCall();
        ghostLayer.startFade(0.04f, 20, true, true);
        fadeBackIn = false;
    }

    public void imageUpdateCall(){
        //retrieve images of the given floor
        BufferedImage floorImage = ProgramHandler.floorList.get(currentFloor).getFloorImage();
        BufferedImage shadowImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
        if(ProgramHandler.floorList.get(currentFloor).isShadowLayerEnabled()){
            shadowImage = ProgramHandler.floorList.get(currentFloor).getShadowMask();
        }


        //update ghost image black screen:
        ghostLayer.updateBlackScreen(floorImage.getWidth(), floorImage.getHeight());

        imageLayer.updateImage(floorImage);
        shadowLayer.updateImage(shadowImage);
        drawingLabel.updateImage(floorImage);
    }

    /**
     *
     * functions to retrieve the image layer and shadow layer
     * @return
     */

    public ViewPortImageDisplay getImageLayer() {
        return imageLayer;
    }

    public ViewPortImageDisplay getShadowLayer() {
        return shadowLayer;
    }

    public ViewPortFadeDisplay getGhostLayer() {
        return ghostLayer;
    }

    public DrawingLabel createDrawingLabel(JFrame requesterFrame) {//creates drawing label
        drawingLabel = new DrawingLabel(requesterFrame);
        drawingLabel.initialise(requesterFrame);
        return drawingLabel;
    }

    public DrawingLabel getDrawingLabel() {
        return drawingLabel;
    }

    /**
     *
     * override update call and update images
     *
     */

    @Override
    public void updateCall() {
        //update scroll values:
        shadowLayer.updateImageOffset(imageOffsetX, imageOffsetY);
        imageLayer.updateImageOffset(imageOffsetX, imageOffsetY);
        ghostLayer.updateImageOffset(imageOffsetX, imageOffsetY);
        drawingLabel.updateImageOffset(imageOffsetX, imageOffsetY);

        //update zoom values:
        shadowLayer.setZoomValue(zoomValue);
        imageLayer.setZoomValue(zoomValue);
        ghostLayer.setZoomValue(zoomValue);
        drawingLabel.setZoomValue(zoomValue);

        //trigger image update:
        shadowLayer.repaint();
        imageLayer.repaint();
        ghostLayer.repaint();
        drawingLabel.repaint();

        //deal with icon panel:
        if(iconsViewPanel == null)return; //icon panel does not exist! abort!
        //update icon panel with scroll information
        iconsViewPanel.setGlobalOffset(imageOffsetX, imageOffsetY);
        iconsViewPanel.setGlobalZoom(zoomValue);
    }

    /**
     *
     * called whenever the parent window is resized!
     *
     */

    public void windowResizeEvent(JFrame parentFrame){
        //called whenever the window resizes
        imageLayer.setBounds(0,0 , parentFrame.getWidth(), parentFrame.getHeight());
        shadowLayer.setBounds(0,0 , parentFrame.getWidth(), parentFrame.getHeight());
        ghostLayer.setBounds(0,0 , parentFrame.getWidth(), parentFrame.getHeight());
        drawingLabel.setBounds(0,0 , parentFrame.getWidth(), parentFrame.getHeight());
        imageLayer.updateImagePosition();
        shadowLayer.updateImagePosition();
        ghostLayer.updateImagePosition();
        drawingLabel.updateImagePosition();

        if(iconsViewPanel == null)return; //icon panel does not exist! abort!
        //update icon panel with size information
        iconsViewPanel.setBounds(0,0 , parentFrame.getWidth(), parentFrame.getHeight());
    }

    /**
     *
     * updates icon view panel to be updated with scroll and zoom values!
     *
     */
    IconsViewPanel iconsViewPanel = null; //might be null!

    public void updateIconViewPanel(IconsViewPanel iconsViewPanel){
        this.iconsViewPanel = iconsViewPanel;
    }

    /**
     *
     * override zoom calculations:
     * only modify and send! dont save!
     *
     */

    @Override
    public void scrollWheelUpdate(boolean increase) {
        double tempZoom = 0;
        if(increase){
            if(zoomValue > Main.maxZoom)return; //upper zoom bound
            tempZoom = zoomValue + zoomModifier; //add zoom modifier
        } else {
            if(zoomValue <= Main.minimumZoom)return; //lower zoom bound
            tempZoom = zoomValue - zoomModifier; //remove zoom modifier
        }
        handler.updateZoomSlider(tempZoom);
    }



    /**
     *
     * enables or disables border for zoom guide
     */

    public void setShowCorners(boolean showCorners){
        imageLayer.setShowCorners(showCorners);
        shadowLayer.setShowCorners(showCorners);
        ghostLayer.setShowCorners(showCorners);
        drawingLabel.setShowCorners(showCorners);
        updateCall();
    }

    /**
     *
     * resets scroll and zoom values!
     * also sends update to handler for zoom slider
     *
     */

    public void resetScrollZoom(){
        this.zoomValue = 1.0;
        this.imageOffsetX = 0;
        this.imageOffsetY = 0;
        handler.updateZoomSlider(1.0);
    }
}
