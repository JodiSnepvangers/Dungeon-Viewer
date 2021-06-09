package main.program.properties;

import java.awt.*;

public class WindowPropertyContainer {

    /**
     *
     * saves the location and size data of all the program windows!
     *
     */

    int posX; //X location on screen
    int posY; //Y location on screen
    int sizeX; //x size of window
    int sizeY; //y size of window

    PropertyStorage.WindowType windowType;

    public WindowPropertyContainer(Rectangle bounds, PropertyStorage.WindowType windowType){
        this.posX = bounds.x;
        this.posY = bounds.y;
        this.sizeX = bounds.width;
        this.sizeY = bounds.height;

        this.windowType = windowType;
    }

    public WindowPropertyContainer(int posX, int posY, int sizeX, int sizeY, PropertyStorage.WindowType windowType){
        this.posX = posX;
        this.posY = posY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        this.windowType = windowType;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setSizeX(int sizeX) {
        this.sizeX = sizeX;
    }

    public void setSizeY(int sizeY) {
        this.sizeY = sizeY;
    }

    public PropertyStorage.WindowType getWindowType() {
        return windowType;
    }

    public Rectangle getBounds(){
        return new Rectangle(posX, posY, sizeX, sizeY);
    }
}
