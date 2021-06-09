package main.program.controlpanel.contents;

import main.Main;
import main.program.FloorContainer;
import main.program.ProgramHandler;
import main.program.controlpanel.ControlPanelFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PaintingArea extends JLabel {

    static int timerFast = 1;
    static int timerSlow = 20;

    Timer timer = new Timer(0, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            repaint();
        }
    });

    Timer delayTimer = new Timer(1, new ActionListener() { //this is needed so the timer runs fast for a moment, or else there is a 'first lag' moment
        @Override
        public void actionPerformed(ActionEvent e) {
            timer.setDelay(timerSlow);
        }
    });

    /**
     *
     * displays and handles the canvas area of the control panel! upon command, should take a clone of FloorContainer and allow it to be edited
     *
     */

    //prepare floor container:
    FloorContainer floorContainer; //contains the floor data thats currently being edited!
    //loads placeholder till images are loaded

    //prepare image variables:
    int imagePosX = 0; //image position in panel
    int imagePosY = 0; //image position in panel

    int imageOffsetX = 0; //image offset in panel
    int imageOffsetY = 0; //image offset in panel

    double zoomValue = 1.0; //zoom value for zooming in

    //prepare shadow graphics:
    Graphics2D shadowGraphics;

    //prepare mouse variables:
    int rawMousePosX = 0; //raw position of mouse pointer, without modifiers
    int rawMousePosY = 0; //raw position of mouse pointer, without modifiers
    int actualMousePosX = 0; //actual location of mouse pointer
    int actualMousePosY = 0; //actual location of mouse pointer
    int calculatedMousePosX = 0; //calculated point after brush size is considered
    int calculatedMousePosY = 0; //calculated point after brush size is considered

    boolean mouseLeftDown = false; //state of mouse left button
    boolean mouseRightDown = false;//state of mouse right button

    boolean mouseInside = false; //true whenever the mouse is over the paint area

    //prepare drawing variables:
    enum DrawingMode { //drawing mode, weither to create or destroy shadow
        REVEAL,
        HIDE
    }
    DrawingMode drawingMode = DrawingMode.REVEAL;
    boolean drawingModeSet = false;

    //prepare remaining variables
    boolean shadowEnabled = true;
    ControlPanelFrame parentFrame;






    public PaintingArea(ControlPanelFrame parentFrame){
        //set image position:
        this.parentFrame = parentFrame;

        //start automatic timer!
        timer.setRepeats(true);
        timer.setDelay(timerFast);
        timer.start();

        //run anti lag timer
        delayTimer.setRepeats(false);
        delayTimer.start();

        //add mouse listeners to the painting area!
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {
                if(mouseInside == false)return; //mouse not in paint area! ignore!
                if(trackScrolling)return; //if scrolling, dont allow drawing!

                //set mouse buttons!
                if(e.getButton() == 1){ mouseLeftDown = true;}
                else if (e.getButton() == 3){mouseRightDown = true;}

                if(mouseLeftDown == false && mouseRightDown == false)return; //if both mouse buttons werent used, ignore them!

                //deal with drawing mode:
                if(drawingModeSet)return; //drawing mode is set! do not alter!
                if(mouseLeftDown){
                    drawingMode = DrawingMode.REVEAL;
                } else if(mouseRightDown){
                    drawingMode = DrawingMode.HIDE;
                }
                //set drawing mode so that it cannot be altered unless both buttons are false
                drawingModeSet = true;
                mousePressedDown();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() == 2)return; //ignore middle mouse button
                if(e.getButton() == 1){ mouseLeftDown = false;}
                else if (e.getButton() == 3){mouseRightDown = false;}
                //if both buttons are false, set drawing mode set to false!
                if(mouseRightDown == false && mouseLeftDown == false){
                    drawingModeSet = false;
                    PaintingArea.this.mouseReleased();
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {mouseInside = true;}
            @Override
            public void mouseExited(MouseEvent e) {mouseInside = false;}
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                scrollingUpdate();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() != 2)return; //middle mouse button wasnt requested!
                scrollerDown();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() != 2)return; //middle mouse button wasnt requested!
                scrollerUp();
            }
        });
    }






    private void mousePressedDown(){ //called by mouse listener to trigger whenever mouse goes down. is not triggered if drawingModeSet is true
        timer.setDelay(timerFast);
        switch(selectedBrush){
            case SQUARESELECT:{
                createSelectionBox();
                break;
            }

            default:{
                parentFrame.floorFunctionEnabled(false);
            }
        }

    }
    private void mouseReleased(){ //same as above, except called on mouse release! only called when all buttons are released!
        timer.setDelay(timerSlow);
        parentFrame.floorFunctionEnabled(false);
        switch(selectedBrush){
            case SQUARESELECT:{
                writeSelectionBoxToFloor();
                break;
            }
        }
    }


    int scrollOriginX = 0; //location of where mouse was when scroller is activated
    int scrollOriginY = 0; //location of where mouse was when scroller is activated

    int previousOffsetX = 0; //saved offset before scroller was enabled
    int previousOffsetY = 0; //saved offset before scroller was enabled

    boolean trackScrolling = false; //state of if scroller is enabled

    private void scrollerDown(){
        updateMousePosition();

        scrollOriginX = rawMousePosX;
        scrollOriginY = rawMousePosY;
        trackScrolling = true;
        previousOffsetX = imageOffsetX;
        previousOffsetY = imageOffsetY;
    }

    private void scrollerUp(){
        trackScrolling = false;
    }

    private void scrollingUpdate(){
        if(trackScrolling == false)return;

        imageOffsetX = (rawMousePosX - scrollOriginX) + previousOffsetX;
        imageOffsetY = (rawMousePosY - scrollOriginY) + previousOffsetY;
    }

    public void disableRefresh(boolean state){
        if(state){
            timer.stop();
        } else {
            timer.start();
        }
    }















    /**
     *
     * updates image position to place current image in the center of the screen!
     */

    public void updateImagePosition(){
        //retrieve all values
        int imageWidth = floorContainer.getImageWidth();
        int imageHeight = floorContainer.getImageHeight();

        int windowWidth = parentFrame.getWidth() - 16; //compensate 16 for the shadow drop swing creates
        int windowHeight = parentFrame.getHeight() - (ControlInterface.genericComponentHeight + 48) ; //compensate for control panel and 48 for the windows border at the top

        //calculate middle position of screen
        int locationPointPosX = windowWidth / 2;
        int locationPointPosY = (windowHeight / 2);// + ControlInterface.genericComponentHeight;

        //take off half of the image size to locate upper left corner
        locationPointPosX = locationPointPosX - (imageWidth / 2);
        locationPointPosY = locationPointPosY - (imageHeight / 2);

        //store location in global variable
        imagePosX = locationPointPosX;
        imagePosY = locationPointPosY;
    }

    /**
     *
     * loads new image into the painting area!
     */

    public void updateFloorContainer(int floorNumber){
        //retrieves floor container with floor number
        if(ProgramHandler.floorList.containsKey(floorNumber) == false){
            //no floor with this number was found!
            System.out.println("ILLEGAL FLOOR NUMBER WAS REQUESTED!");
            return;
        }
        updateFloorContainer(ProgramHandler.floorList.get(floorNumber));
    }

    public void updateFloorContainer(FloorContainer container){
        if(floorContainer != null) floorContainer.dispose();
        floorContainer = container.getClone();
    }

    /**
     *
     * retrieves floor container currently in painting area. keep in mind it is a clone of the original.
     *
     */

    public FloorContainer getFloorContainer() {
        return floorContainer;
    }

    /**
     *
     * updates mouse pointers to new position
     *
     */

    private void updateMousePosition(){
        //raw mouse position: raw mouse position relative to the entire screen! apply zoom value
        rawMousePosX = (int) ((MouseInfo.getPointerInfo().getLocation().x) / zoomValue);
        rawMousePosY = (int) ((MouseInfo.getPointerInfo().getLocation().y) / zoomValue);

        //actual mouse position: calculates the position relative to the window
        actualMousePosX = (int) (rawMousePosX - getLocationOnScreen().x / zoomValue);
        actualMousePosY = (int) (rawMousePosY - getLocationOnScreen().y / zoomValue);

        //calculates mouse position: calculates the center of the brush based on brush size
        calculatedMousePosX = actualMousePosX - (brushSize / 2);
        calculatedMousePosY = actualMousePosY - (brushSize / 2);
    }

    public void setZoomValue(double zoomValue) {
        this.zoomValue = zoomValue;
    }


    public void resetZoomValue(){
        imageOffsetX = 0;
        imageOffsetY = 0;
    }

    public void setShadowEnabled(boolean shadowEnabled) {
        this.shadowEnabled = shadowEnabled;
    }

    public boolean isShadowEnabled() {
        return shadowEnabled;
    }

    /**
     *
     * handles custom drawing routines!
     * make sure to keep everything tidy!
     *
     * @param graphics
     */


    //global paint variables:
    boolean cursorEnabled = true;


    @Override
    public void paintComponent(Graphics graphics) {
        //run super, to not break paint chain!
        super.paintComponent(graphics);

        //if floor container is null, do not paint anything!
        if(floorContainer == null)return;

        //update mouse location
        updateMousePosition();

        //prepare variables:
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.scale(zoomValue, zoomValue);

        //draw image
        graphics2D.drawImage(floorContainer.getFloorImage(), imagePosX + imageOffsetX, imagePosY + imageOffsetY, null);

        //draw shadowmask with transparency: ensure that graphics is restored!
        if(floorContainer.isShadowLayerEnabled()){
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Main.shadowTransparencyNew);
            AlphaComposite old = (AlphaComposite) graphics2D.getComposite();
            graphics2D.setComposite(ac);
            graphics2D.drawImage(floorContainer.getShadowMask(), imagePosX + imageOffsetX, imagePosY + imageOffsetY, null);
            graphics2D.setComposite(old);
        }


        //disable drawing if mouse pointer is outside paint area. but keep drawing if drawing was requested
        if(mouseInside == false && drawingModeSet == false)return; //escape if mouse isnt inside

        //draw cursor. retrieve color from main
        if(cursorEnabled){
            graphics2D.setColor(Main.cursorColor);
            paintCursor(graphics2D);
        }

        //if mouse isnt down, return as to not waste framerate on drawing mouse down graphics
        if(drawingModeSet == false) {
            return;
        }

        //if panel shadowmode is disabled, do not draw anything to the screen!
        if(floorContainer.isShadowLayerEnabled() == false){
            timer.setDelay(timerSlow); //sets timer back to slow so cpu is spared
            return;
        }

        //drawing mode is set! some drawing action was requested!
        switch(selectedBrush){
            case SQUARESELECT:{
                    paintSelectionBox(graphics2D);
                break;
            }

            case CIRCLEBRUSH:{
                writeCircleToFloor();
                break;
            }

            case BOXBRUSH:{
                writeBoxToFloor();
                break;
            }
        }

    }




    /**
     *
     * draws the cursor's brush on the screen, at mouse position
     * carries all vartiables and set methods!
     * @param graphics graphics object
     */

    private int brushSize = 10;
    private ControlInterface.BrushSelect selectedBrush = ControlInterface.BrushSelect.BOXBRUSH;

    public void setBrushSize(int brushSize) {
        this.brushSize = brushSize;
    }

    public void setSelectedBrush(ControlInterface.BrushSelect selectedBrush) {
        this.selectedBrush = selectedBrush;
    }

    private void paintCursor(Graphics2D graphics){
        //choose drawing method with brush select!
        switch(selectedBrush){
            case SQUARESELECT:{
                //draws a cross on the screen to guide box placement. uses actual position
                graphics.drawLine(actualMousePosX - brushSize, actualMousePosY, actualMousePosX + brushSize, actualMousePosY);
                graphics.drawLine(actualMousePosX, actualMousePosY - brushSize, actualMousePosX, actualMousePosY + brushSize);
                break;
            }

            case BOXBRUSH:{
                //draws a actual box cursor on the screen. uses mouse displacement to stay centered!
                graphics.drawRect(calculatedMousePosX, calculatedMousePosY, brushSize, brushSize);
                break;
            }
            case CIRCLEBRUSH:{
                //draws a actual circle cursor on the screen. uses mouse displacement to stay centered!
                graphics.drawOval(calculatedMousePosX, calculatedMousePosY, brushSize, brushSize);
                break;
            }
        }
    }

    /**
     *
     * draws the selection box as long as mouse is held down!
     *
     * creatseSelectionBox: called when mouse first goes down in SQUARESELECT mode
     * writeSelectionBoxToFloor: called when selectionBox is released. writes changes to shadowMask of the current floor!
     */

    int selectBoxPosX = 0;
    int selectBoxPosY = 0;

    private void createSelectionBox(){ //is created by the mouse listener! sets static positions
        updateMousePosition();
        selectBoxPosX = actualMousePosX;
        selectBoxPosY = actualMousePosY;
    }

    private void writeSelectionBoxToFloor(){
        //must remove image offset from positions!
        int selectPosX = selectBoxPosX - (imagePosX + imageOffsetX);
        int selectPosY = selectBoxPosY - (imagePosY + imageOffsetY);

        int mousePosX = actualMousePosX - (imagePosX + imageOffsetX);
        int mousePosY = actualMousePosY - (imagePosY + imageOffsetY);

        floorContainer.drawRectangle(selectPosX, selectPosY, mousePosX, mousePosY, (drawingMode == DrawingMode.REVEAL)); //if set to reveal, returns true, else returns false!
    }

    private void paintSelectionBox(Graphics2D graphics){
        //square select was selected. drawing selection box!
        //create Rectancle object
        Rectangle rect= new Rectangle(new Point(selectBoxPosX, selectBoxPosY)); //set static positions
        rect.add(new Point(actualMousePosX, actualMousePosY)); // set dymanic mouse positions

        //draw selection rectangle
        graphics.drawRect(rect.x, rect.y, rect.width, rect.height);
    }

    /**
     *
     * circle brush: draw a circle on position every frame!
     *
     */

    private void writeCircleToFloor(){
        //must remove image offset from positions!

        int mousePosX = calculatedMousePosX - (imagePosX + imageOffsetX);
        int mousePosY = calculatedMousePosY - (imagePosY + imageOffsetY);

        floorContainer.drawMarker(mousePosX, mousePosY, brushSize, (drawingMode == DrawingMode.REVEAL));//if set to reveal, returns true, else returns false!
    }

    /**
     *
     * box brush: draw box at position every frame!
     */

    private void writeBoxToFloor(){
        //must remove image offset from positions!
        int mousePosX = calculatedMousePosX - (imagePosX + imageOffsetX);
        int mousePosY = calculatedMousePosY - (imagePosY + imageOffsetY);

        floorContainer.drawBox(mousePosX, mousePosY, brushSize, (drawingMode == DrawingMode.REVEAL));//if set to reveal, returns true, else returns false!
    }







}
