package main.program.viewpanel.contents.icons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.UIManager.get;

public class IconsViewPanel extends JPanel {


    /**
     *
     * does the processing and displaying of icons!
     * TODO: retrieve icons from icon manager, and display all icons!
     *
     */

    JFrame parentFrame; //parent frame that displays this
    IconManager iconManager; //icon manager connected to this panel

    int globalOffsetX = 0; //global offset of all icons, for scrolling
    int globalOffsetY = 0; //global offset of all icons, for scrolling

    public int posX = 0; //test: 0.0 location of parent frame
    public int posY = 0; //test
    public int initialPosX = 0;
    public int initialPosY = 0;

    double globalZoom = 1.0;//global zoom value for all icons
    double globalScale = 0.3; // global scale for all icons

    List<IconObject> currentObjectList = new ArrayList<>(); //contains a list of currently displayed items!

    public IconsViewPanel(JFrame parentFrame, IconManager iconManager){
        //initialise local variables
        this.parentFrame = parentFrame;
        this.iconManager = iconManager;

        //connect this display panel to icon manager!
        iconManager.connectIconPanel(this);

        //set parameters:
        setLayout(null); //set layout to null so precision location and sizes are possible!
        setOpaque(false); //set opague to false to reveal behind layers!

        parentFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                windowMoveUpdate();
            }
        });
    }

    /**
     *
     * sends scroll and zoom updates to all current displayed items!
     *
     */

    private void sendScrollUpdate(){
        for(int i = 0; i < currentObjectList.size(); i++){
            IconObject icon = currentObjectList.get(i); //retrieve icon object from memory
            icon.updateOffset(globalOffsetX, globalOffsetY);
            icon.updateGlobalZoom(globalZoom);
        }
        repaint();
    }

    /**
     *
     * retrieves all icons from current floor that have visibility enabled
     *
     */

    public void updateDisplayedIcons(){
        removeAll(); //remove all current displayed objects!
        currentObjectList.clear(); //remove all icons from list!
        List<IconContainer> iconList = iconManager.getIconList(); //retrieve current icon list from manager!

        for(int i = 0; i < iconList.size(); i++) {
            IconContainer icon = iconList.get(i); //retrieve icon container from memory
            IconObject iconObject = icon.getObject(); //pull object from container
            addObject(iconObject); //add icon to display!
        }

        repaint();
    }

    /**
     *
     * adds object to this display! ensures object has lastest information
     *
     */

    public void addObject(IconObject icon){
        //send updated information to object
        icon.updateOffset(globalOffsetX, globalOffsetY);
        icon.updateGlobalZoom(globalZoom);
        icon.windowPosX = posX;
        icon.windowPosY = posY;
        icon.setViewPanel(this);
        icon.updateVisuals();
        //add icon to display
        add(icon);
        currentObjectList.add(icon);
    }

    /**
     *
     * updates foundation window parameter for panel and all icons!
     *
     */

    public void windowMoveUpdate(){
        this.posX = parentFrame.getLocationOnScreen().x;
        this.posY = parentFrame.getLocationOnScreen().y;
        this.initialPosX = (parentFrame.getWidth() - 57) / 2;
        this.initialPosY = (parentFrame.getHeight() - 57) / 2;
        for(int i = 0; i < currentObjectList.size(); i++){
            IconObject icon = currentObjectList.get(i);
            icon.windowPosX = posX;
            icon.windowPosY = posY;
        }
    }


    /**
     *
     * overwrite paint method. for labels under icons!\
     *
     */

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setFont(new Font("Default", 0, 16));
        for(int i = currentObjectList.size() -1; i >= 0; i--){
            IconObject object = currentObjectList.get(i);
            if(object.nameVisible && object.visible){
                String drawnString = object.icon.getName();
                int width = g.getFontMetrics().stringWidth(drawnString);
                int xOffset = (int) (((object.getWidth() * globalZoom) / 2) - (width / 2));
                graphics2D.setColor(new Color(0, 0, 0, 220));
                graphics2D.fillRect(object.getBounds().x + xOffset, object.getBounds().y - 24, width, 18);
                if(object.icon.isDead()){
                    graphics2D.setColor(new Color(120, 120, 120));
                } else {
                    graphics2D.setColor(new Color(255, 255, 255));
                }
                graphics2D.drawString(drawnString, object.getBounds().x + xOffset, object.getBounds().y - 10);
            }
        }
    }

    /**
     *
     *
     * basic getter and setter methods:
     */

    public void setGlobalOffset(int globalOffsetX, int globalOffsetY) {
        this.globalOffsetX = globalOffsetX;
        this.globalOffsetY = globalOffsetY;
        sendScrollUpdate();
    }

    public void setGlobalZoom(double globalZoom) {
        this.globalZoom = globalZoom;
        sendScrollUpdate();
    }

    public void setGlobalScale(double value){
        this.globalScale = value;
        for(int i = 0; i < currentObjectList.size(); i++) {
            IconObject icon = currentObjectList.get(i); //retrieve icon object from memory
            icon.setGlobalScale(globalScale);
        }
        repaint();
    }

    public int getGlobalOffsetX() {
        return globalOffsetX;
    }

    public int getGlobalOffsetY() {
        return globalOffsetY;
    }

    public double getGlobalZoom() {
        return globalZoom;
    }
}
