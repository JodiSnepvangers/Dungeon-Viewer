package main.program.viewpanel.contents;

import main.Main;
import main.program.JFrameAutomaticWindow;
import main.program.ProgramHandler;
import main.program.controlpanel.contents.ControlInterface;
import main.program.viewpanel.ViewPortHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

public class ViewPortImageDisplay extends JLabel {


    /**
     *
     * image displayer that both the image and shadow layer use to display their graphics
     *
     */


    //prepare image variables:
    int imagePosX = 0; //image position in panel
    int imagePosY = 0; //image position in panel

    int imageOffsetX = 0; //image offset in panel
    int imageOffsetY = 0; //image offset in panel

    BufferedImage image = null; // image to be displayed

    double zoomValue = 1.0;

    JFrame parentFrame; //frame of parent
    boolean initialised = false; //boolean for initialisation. is set to true if parent frame is set!

    boolean showCorners = false; //shows corners of image by drawing it directly

    /**
     *
     * updates internal image
     */

    public void updateImage(BufferedImage image){
        this.image = image;
        updateImagePosition();
        repaint();
    }

    /**
     *
     * initialises the display port element and sets the parent frame
     *
     */

    public void initialise(JFrame parentFrame){
        this.parentFrame = parentFrame;
        initialised = true;
    }


    /**
     *
     * updates image position to place current image in the center of the screen!
     */

    public void updateImagePosition(){
        if (image == null)return; //checks if image is null

        //retrieve all values
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        int windowWidth = parentFrame.getWidth() - 16; //compensate 16 for the shadow drop swing creates
        int windowHeight = parentFrame.getHeight() - 48 ; //compensate 48 for the windows border at the top

        //calculate middle position of screen
        int locationPointPosX = windowWidth / 2;
        int locationPointPosY = (windowHeight / 2);

        //take off half of the image size to locate upper left corner
        locationPointPosX = locationPointPosX - (imageWidth / 2);
        locationPointPosY = locationPointPosY - (imageHeight / 2);

        //store location in global variable
        imagePosX = locationPointPosX;
        imagePosY = locationPointPosY;
    }

    /**
     *
     *
     * updates the image offset parameters and triggers a repaint
     *
     */

    public void updateImageOffset(int imageOffsetX, int imageOffsetY){
        this.imageOffsetX = imageOffsetX;
        this.imageOffsetY = imageOffsetY;
    }

    /**
     *
     * sends zoom update for zoom value
     */

    public void setZoomValue(double value){
        zoomValue = value;
    }

    /**
     *
     * enables or disables border for zoom guide
     */

    public void setShowCorners(boolean showCorners){
        this.showCorners = showCorners;
    }






    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); //call super components. dont break paint chain!
        
        Graphics2D graphics2D = (Graphics2D) g; //cast graphics to better component
        graphics2D.scale(zoomValue, zoomValue);

        if(image == null)return; //if no image is stored, then no image should be displayed
        paintImage(graphics2D);

        //draw corners if requested:
        if(showCorners){
            //initialise variables:
            int posX1 = 0;
            int posY1 = 0;
            int posX2 = 0;
            int posY2 = 0;
            graphics2D.setColor(Main.cursorColor);

            //draw topmost corner
            posX1 = imagePosX + imageOffsetX;
            posY1 = imagePosY + imageOffsetY;
            posX2 = posX1;
            posY2 = posY1 + 20;

            graphics2D.drawLine(posX1,posY1, posX2, posY2);

            posX1 = imagePosX + imageOffsetX;
            posY1 = imagePosY + imageOffsetY;
            posX2 = posX1 + 20;
            posY2 = posY1;

            graphics2D.drawLine(posX1,posY1, posX2, posY2);

            //draw bottom most corner:
            posX1 = imagePosX + imageOffsetX + image.getWidth();
            posY1 = imagePosY + imageOffsetY + image.getHeight();
            posX2 = posX1;
            posY2 = posY1 - 20;

            graphics2D.drawLine(posX1,posY1, posX2, posY2);

            posX1 = imagePosX + imageOffsetX + image.getWidth();
            posY1 = imagePosY + imageOffsetY + image.getHeight();
            posX2 = posX1 - 20;
            posY2 = posY1;

            graphics2D.drawLine(posX1,posY1, posX2, posY2);
        }

    }

    public void paintImage(Graphics2D graphics2D){
        graphics2D.drawImage(image, imagePosX + imageOffsetX, imagePosY + imageOffsetY,null); //draw image to screen
    }
}
