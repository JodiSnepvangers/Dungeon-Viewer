package main.program;

import main.Main;

import java.awt.*;
import java.awt.image.BufferedImage;

public class FloorContainer {
    /**
     *
     * hold floor data, such as image, floor number, shadowmask, and more
     * TODO: create all get methods
     */

    BufferedImage floorImage; //image of the floor itself
    BufferedImage shadowMask; //image of the shadow mask overlaying the floor. fully opague.

    Graphics2D shadowGraphics;

    int floorNumber; //floor number
    int imageHeight; //original image height
    int imageWidth; //original image width

    boolean shadowLayerEnabled = true;


    public FloorContainer(BufferedImage floorImage, int floorNumber){
        this.floorImage = floorImage;
        this.floorNumber = floorNumber;

        imageHeight = floorImage.getHeight();
        imageWidth = floorImage.getWidth();

        generateShadowMask();
        System.out.println("floor images for floor '" + floorNumber + "' created successfully");
    }

    /**
     * private constructor for cloning method
     *
     */

    private FloorContainer(BufferedImage floorImage, BufferedImage externalShadowMask, int floorNumber, boolean shadowLayerEnabled){
        //save variables:
        this.shadowLayerEnabled = shadowLayerEnabled;

        //save image data:
        this.floorImage = floorImage;
        this.floorNumber = floorNumber;

        imageHeight = floorImage.getHeight();
        imageWidth = floorImage.getWidth();

        //make copies of the shadow mask and transparency mask! make them non volatile!
        shadowMask = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
        shadowGraphics = shadowMask.createGraphics();
        shadowGraphics.drawImage(externalShadowMask, 0, 0, null);

        //set correct colors:
        shadowGraphics.setColor(Main.genericViewportBackground); //retrieve color from main

        //set transparency mode!
        shadowGraphics.setComposite(AlphaComposite.Clear);
    }

    /**
     *
     * clones the floor container and outputs it~ should be non volatile
     *
     */

    public FloorContainer getClone(){
        return new FloorContainer(floorImage, shadowMask, floorNumber, shadowLayerEnabled);
    }

    /**
     *
     * disposes of the graphic objects within this floor container
     * run this when the floor container is no longer required!
     *
     */

    public void dispose(){
        shadowGraphics.dispose();
    }

    /**
     * generates a shadow mask of the same size as the internal image!
     *
     */

    public void generateShadowMask(){
        //create the shadow mask images
        shadowMask = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);

        //create graphic objects for masks!
        shadowGraphics = shadowMask.createGraphics();

        //create actual shadows. make sure to make opague shadow for shadowmask and transparent shadow for transparency mask!
        shadowGraphics.setBackground(new Color(0,0,0,0));
        shadowGraphics.setColor(Main.genericViewportBackground); //get viewport background color
        shadowGraphics.fillRect(0,0, imageWidth, imageHeight);

        //set transparency mode!
        shadowGraphics.setComposite(AlphaComposite.Clear);
    }

    /**
     *
     * draws a box between position 1 and 2 on both shadow masks
     */

    public void drawRectangle(int posX1, int posY1, int posX2, int posY2, boolean revealFog){
        //first generate box class to calculate position, width, height
        Rectangle rect= new Rectangle(new Point(posX1, posY1));
        rect.add(new Point(posX2, posY2));

        if(revealFog){ //if true, reveal fog!
            shadowGraphics.setComposite(AlphaComposite.Clear);
        } else {
            shadowGraphics.setComposite(AlphaComposite.SrcOver);
        }

        //use box to draw rectancles:
        shadowGraphics.fillRect(rect.x, rect.y, rect.width, rect.height);
    }

    /**
     *
     * draws circulair marker on position!
     */

    public void drawMarker(int posX, int posY, int markerSize, boolean revealFog){
        if(revealFog){ //if true, reveal fog!
            shadowGraphics.setComposite(AlphaComposite.Clear);
        } else {
            shadowGraphics.setComposite(AlphaComposite.SrcOver);
        }

        //draw circles
        shadowGraphics.fillOval(posX, posY, markerSize, markerSize);
    }

    /**
     *
     * draws box marker on position!
     */

    public void drawBox(int posX, int posY, int markerSize, boolean revealFog){
        if(revealFog){ //if true, reveal fog!
            shadowGraphics.setComposite(AlphaComposite.Clear);
        } else {
            shadowGraphics.setComposite(AlphaComposite.SrcOver);
        }

        //draw circles
        shadowGraphics.fillRect(posX, posY, markerSize, markerSize);
    }


    /**
     *
     * replaces this floor container with a new one by directly accessing ProgramHandler
     * @param floorContainer
     */


    public void replaceFloorContainer(FloorContainer floorContainer){
        floorContainer.setShadowLayerEnabled(shadowLayerEnabled);
        ProgramHandler.floorList.put(floorNumber, floorContainer);
        dispose();
    }








    public int getFloorNumber() {
        return floorNumber;
    }

    public BufferedImage getFloorImage() {
        return floorImage;
    }

    public BufferedImage getShadowMask() {
        return shadowMask;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setShadowLayerEnabled(boolean shadowLayerEnabled) {
        this.shadowLayerEnabled = shadowLayerEnabled;
    }

    public boolean isShadowLayerEnabled() {
        return shadowLayerEnabled;
    }
}
