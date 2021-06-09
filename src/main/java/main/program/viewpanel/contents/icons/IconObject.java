package main.program.viewpanel.contents.icons;

import main.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

public class IconObject extends JLabel {

    /**
     *
     * Icon object: the object created and used by the icon view panel for displaying!
     * should take scaling into account, aswell as zooming and offset!
     *
     */
    IconContainer icon; //parent icon container for retrieving information!

    BufferedImage iconImage; //copy of original image that is scaled locally and globally
    BufferedImage grayImage; //copy of original image that is grayscaled and scaled

    //setting parameters:
    boolean visible = true; //determines if icon is visible on screen!
    boolean dead = false; //if true, icon is displayed with a grayscale instead
    boolean nameVisible = false; //if true, name is displayed on screen

    //set internal parameters
    int iconOffsetX = 0; //x offset of icon position
    int iconOffsetY = 0; //y offset of icon position

    int iconPositionX = 0;//x position of icon
    int iconPositionY = 0;//y position of icon

    boolean defaultPosition = true; //defaults to true and is set to false once default position is set! allows begin position to be middle of screen

    int windowPosX = 0; // 0.0 location of parent frame
    int windowPosY = 0;

    double localScale = 1.0; //local scale of this individual icon!
    double globalScale = 0.3; //global scale across all icons!

    double zoomScale = 1.0; //zoom scale of viewport. must be handled differenty!

    public IconObject(IconContainer icon){
        //initialise inner variables:
        this.icon = icon;

        generateIconImage();
        setBounds(retrieveBounds()); //set default position

        //add mouse listeners
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == 1){
                    mouseButtonEvent(true);
                    super.mousePressed(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() == 1){
                    mouseButtonEvent(false);
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                mouseMovementEvent();
            }
        });
    }

    /**
     *
     * position handlers; handle updating the position on scrolling, and also the size of the label!
     *
     * retrieveBounds: calculates the new position and size based on the offset and scale modifiers
     *
     * updateDisplayPosition: called whenever the position needs updating
     *
     * updateVisuals: should be called when setting have changed, and the appearance needs to be updated
     *
     * updateOffset: called when the offset parameters are changed
     *
     * updateGlobalScale: updates the internal zoom scale of the viewport
     */

    public Rectangle retrieveBounds(){
        Rectangle rectangle = new Rectangle();

        //calculate position parameters:
        int posX = (int) ((iconOffsetX + iconPositionX) * zoomScale);
        int posY = (int) ((iconOffsetY + iconPositionY) * zoomScale);

        //calculate scale parameters:
        int sizeX = (int) (iconImage.getWidth() * (zoomScale * globalScale));
        int sizeY = (int) (iconImage.getHeight() * (zoomScale * globalScale));

        //create rectangle
        rectangle.setLocation(posX, posY);
        rectangle.setSize(sizeX, sizeY);
        return rectangle;
    }

    public void setBeginnerPosition(int positionX, int positionY){
        if(defaultPosition){
            this.iconPositionX = positionX;
            this.iconPositionY = positionY;
            defaultPosition = false;
        }
    }

    public void updateDisplayPosition(){
        setBounds(retrieveBounds()); //updates display position after calculations
    }

    public void updateVisuals(){
        //retrieve new settings!
        visible = icon.isIconVisible();
        dead = icon.isDead();
        nameVisible = icon.isNameVisible();

        //check local scale:
        if(icon.getObjectLocalScale() != localScale){
            //local scale changed!
            localScale = icon.getObjectLocalScale();
            generateIconImage();
        }

        if(iconsViewPanel != null){
            if(iconsViewPanel.globalScale != globalScale){
                globalScale = iconsViewPanel.globalScale;
                generateIconImage();
            }
        }

        //check if any setting changed, and do updates if so!

        setVisible(visible);

        if(visible){
            //icon is set to visible
            if(defaultPosition){
                //default position not set
                if(iconsViewPanel != null){
                    //icon view panel is known!
                    setBeginnerPosition(iconsViewPanel.initialPosX, iconsViewPanel.initialPosY);
                }
            }
        }

        updateDisplayPosition();
        repaint();
        viewPanelRepaint();
    }

    public void updateOffset(int iconOffsetX, int iconOffsetY) {
        this.iconOffsetX = iconOffsetX;
        this.iconOffsetY = iconOffsetY;
        updateDisplayPosition();
    }

    public void updateGlobalZoom(double globalZoom) {
        this.zoomScale = globalZoom;
        updateDisplayPosition();
    }

    public void setGlobalScale(double globalScale) {
        this.globalScale = globalScale;
        updateDisplayPosition();
        repaint();
    }

    /**
     *
     * this section deals with painting the icon on screen.
     *
     * local zoom and global zoom should be pre calculates. when these variables changes, new iconImage and grayImage should be created!
     *
     * zoom value is constantly watched and tied to viewport zoom!
     *
     */

    public void generateIconImage(){
        //retrieves the icon image and generates a color and grayscale version that are then stored!
        BufferedImage original = icon.getImage();

        //generated scaled colored variant
        double newSize = globalScale + localScale - 1.0;
        int width = (int)(original.getWidth() * localScale);
        int height = (int)(original.getHeight() * localScale);
        //perform value checks:
        if(width <= 0)width = 1;
        if(height <= 0)height = 1;

        //generate images:
        iconImage = Main.resize(original, width, height);

        //generate gray scaled version!
        grayImage = GrayImage(iconImage);

        //trigger repaint!
        updateDisplayPosition();
    }

    /**
     *
     * generates a gray BufferedImage from a BufferedImage
     */

    public BufferedImage GrayImage(BufferedImage image){
        BufferedImage grayImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        op.filter(image, grayImage);
        return grayImage;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D graphics2D = (Graphics2D) g;

        double newScale = zoomScale * globalScale;

        graphics2D.scale(newScale, newScale);

        if(dead){
            graphics2D.drawImage(grayImage, 0, 0, null);
        } else {
            graphics2D.drawImage(iconImage, 0, 0, null);
        }

    }

    /**
     *
     *  mouse handlers: handle the dragging around of icons on the map!
     *
     */

    boolean mouseLocked = false; //state if mouse is locked down
    int staticPosX = 0;
    int staticPosY = 0;

    public void mouseButtonEvent(boolean mouseDown){
        mouseLocked = mouseDown;

        double zoomTotal = (zoomScale - 1.0) + (globalScale - 1.0) + (localScale - 1.0) + 1.0;

        staticPosX = (int) ((MouseInfo.getPointerInfo().getLocation().getX()) / zoomTotal);
        staticPosY = (int) ((MouseInfo.getPointerInfo().getLocation().getY()) / zoomTotal);

        staticPosX = windowPosX;
        staticPosY = windowPosY;
    }

    public void mouseMovementEvent(){
        if(mouseLocked){
            int mousePosX = (int) ((MouseInfo.getPointerInfo().getLocation().getX() - 16) / zoomScale);
            int mousePosY = (int) ((MouseInfo.getPointerInfo().getLocation().getY() - 48) / zoomScale);

            int locationX = (int) (mousePosX - staticPosX / zoomScale) - iconOffsetX;
            int locationY = (int) (mousePosY - staticPosY / zoomScale) - iconOffsetY;

            this.iconPositionX = locationX;
            this.iconPositionY = locationY;

            updateDisplayPosition();
            viewPanelRepaint();
        }
    }

    /**
     * test setup
     * testing if viewport updates will help
     *
     */

    IconsViewPanel iconsViewPanel = null;

    public void setViewPanel(IconsViewPanel iconsViewPanel){
        this.iconsViewPanel = iconsViewPanel;
    }

    public void viewPanelRepaint(){
        if(iconsViewPanel == null)return; //view panel isnt set~!
        iconsViewPanel.repaint();
    }


}
