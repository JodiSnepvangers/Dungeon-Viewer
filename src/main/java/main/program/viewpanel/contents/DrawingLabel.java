package main.program.viewpanel.contents;


import main.program.viewpanel.contents.icons.IconManager;

import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DrawingLabel extends ViewPortImageDisplay {

    int rawMousePosX = 0; //raw position of mouse pointer, without modifiers
    int rawMousePosY = 0; //raw position of mouse pointer, without modifiers
    int actualMousePosX = 0; //actual location of mouse pointer
    int actualMousePosY = 0; //actual location of mouse pointer

    IconManager iconManager = new IconManager();




    /**
     *
     * forms a drawing layer that can be drawed on with left mouse click
     * offers methods to set the color and to clear the selected color from the layer
     *
     * extends ViewPortImageDisplay to have access to the image scrolling and resizing.
     * should be fed a floor image, but will not draw that image. just uses it for size parameters
     *
     */

    public DrawingLabel(JFrame parentFrame){
        parentFrame.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    mouseDragCall();
                }

            }
        });
        parentFrame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() != 1)return; // was not left mouse button! ignore!
                isMouseSet = false; //sets mouse boolean to false if mouse is ever released!
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() != 3)return; //button was not right mouse button!
                if(screenMemory.size() == 0)return; //there is nothing to erase!
                Color keyColor = colorAddList.get(colorAddList.size() - 1); //retrieve last used color from list
                clearColor(keyColor); //clear color from list!
            }
        });

        //setup auto refresh timer:
        autoRefresh.setDelay(30); //frame rate of refreshes
        autoRefresh.setRepeats(true);
        autoRefresh.start();

    }


    /**
     *
     * drag event: called whenever the mouse is dragged around on the screen
     */

    boolean isMouseSet = false; //is set to true when the previous positions are set properly
    int lastPosX = 0; //last position X
    int lastPosY = 0; //last position Y
    Color selectedColor = Color.WHITE;

    private void mouseDragCall(){
        updateMousePosition();
        if(changesToMemory == true)return; //testing. will slow down drawing to only draw once between updates!
        if(isMouseSet){
            drawLineToMemory(lastPosX, lastPosY, actualMousePosX - imageOffsetX, actualMousePosY - imageOffsetY, selectedColor);
        }
        lastPosX = actualMousePosX - imageOffsetX;
        lastPosY = actualMousePosY - imageOffsetY;
        isMouseSet = true;
    }

    public void setDrawingColor(Color color){
        selectedColor = color;
    }

    /**
     *
     * internally handles mouse position calculations:
     *
     */

    private void updateMousePosition() {
        //raw mouse position: raw mouse position relative to the entire screen! apply zoom value
        rawMousePosX = (int) ((MouseInfo.getPointerInfo().getLocation().x) / zoomValue);
        rawMousePosY = (int) ((MouseInfo.getPointerInfo().getLocation().y) / zoomValue);

        //actual mouse position: calculates the position relative to the window
        actualMousePosX = (int) (rawMousePosX - getLocationOnScreen().x / zoomValue);
        actualMousePosY = (int) (rawMousePosY - getLocationOnScreen().y / zoomValue);
    }


    /**
     *
     *
     * memory handler: for each color, there is a saved container that will save the positions of the lines
     *
     *
     */
    List<Color> colorAddList = new ArrayList<>(); //a list that keeps the order of the added colors
    HashMap<Color, List<LineContainer>> screenMemory = new HashMap<>();
    private void drawLineToMemory(int posX1, int posY1, int posX2, int posY2, Color color){
        if(screenMemory.containsKey(color) == false){
            screenMemory.put(color, new ArrayList<>()); //create a new list if color is unknown to drawing process
            colorAddList.add(color);
        }
        List<LineContainer> colorMemory = screenMemory.get(color); //retrieve color list from memory for editing!
        if(colorMemory.size() >= 5000) return; //if limit is reached, stop writing to this memory!

        LineContainer lineContainer = new LineContainer(posX1, posY1, posX2, posY2); //create line container

        colorMemory.add(lineContainer); //save line container to memory

        changesToMemory = true; //tells timer to refresh the screen
    }

    /**
     *
     * clears selected color from screen:
     * must be the exact same color class that went in!
     *
     * clear all clears all colors from memory
     * fade out will fade out colors before clearing them
     */

    public void clearColor(Color color){
        if(screenMemory.containsKey(color) == false)return; //key was not found! nothing to erase!
        screenMemory.remove(color).clear();
        colorAddList.remove(color);

        changesToMemory = true; //tells timer to refresh the screen
    }

    public void fadeOutAll(){
        opaque = 1.0f;
        fadingOutEnabled = true;
    }

    public void clearAll(){
        for(int colorIndex = 0; colorIndex < screenMemory.size(); colorIndex++) {
            //gets each color list from memory:
            Color keyColor = (Color) screenMemory.keySet().toArray()[colorIndex]; //restrieves the key set in memory at index
            clearColor(keyColor);
        }
    }
















    class LineContainer { //just a simple container for two positions to form a line!

        public int posX1;
        public int posY1;
        public int posX2;
        public int posY2;

        public LineContainer(int posX1, int posY1, int posX2, int posY2){
            this.posX1 = posX1;
            this.posY1 = posY1;
            this.posX2 = posX2;
            this.posY2 = posY2;
        }
    }





    boolean fadingOutEnabled = true;
    float opaque = 0.2f;
    float fadeSpeed = 0.20f;

    @Override
    public void paintImage(Graphics2D graphics2D) {

        if(fadingOutEnabled){
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opaque);
            graphics2D.setComposite(ac);
        }

        //test draw:
        graphics2D.setStroke(new BasicStroke(2, BasicStroke.JOIN_ROUND, BasicStroke.CAP_ROUND));
        for(int colorIndex = 0; colorIndex < screenMemory.size(); colorIndex++){
            //gets each color list from memory:
            Color keyColor = (Color) screenMemory.keySet().toArray()[colorIndex]; //restrieves the key set in memory at index
            List<LineContainer> colorMemory = screenMemory.get(keyColor); //retrieves color memory with key color
            graphics2D.setColor(keyColor); //sets color of screen to key color!
            for(int lineIndex = 0; lineIndex < colorMemory.size(); lineIndex++){
                //gets each line in color memory
                LineContainer lineContainer = colorMemory.get(lineIndex); //get next line from memory
                int posX1 = lineContainer.posX1 + imageOffsetX;
                int posY1 = lineContainer.posY1 + imageOffsetY;
                int posX2 = lineContainer.posX2 + imageOffsetX;
                int posY2 = lineContainer.posY2 + imageOffsetY;
                graphics2D.drawLine(posX1 , posY1, posX2, posY2); //draw line to screen
            }
        }
    }

    /**
     *
     * auto refresh timer: triggers a repaint if changes have been made!
     */

    boolean changesToMemory = false;

    Timer autoRefresh = new Timer(0, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(fadingOutEnabled){
                opaque = opaque - fadeSpeed;
                if(opaque < 0){
                    opaque = 0.0f;
                    fadingOutEnabled = false;
                    clearAll();
                }
            }
            if(changesToMemory){
                repaint();
                changesToMemory = false;
            }
        }
    });

    /**
     *
     * retrieve icon manager from interactive layer!~
     * @return
     */

    public IconManager getIconManager() {
        return iconManager;
    }
}
