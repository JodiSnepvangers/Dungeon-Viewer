package main.program.viewpanel.contents;

import main.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class ViewPortFadeDisplay extends ViewPortImageDisplay {

    /**
     *
     * extends image display and provides fade over time function
     *
     */

    float opaque = 0.0f; //current image transparency. 1 = fully opague, 0 = fully transparent
    float fadingLimit = 1.0f;

    float fadeModifier = 0.01f; //mod added or removed each step
    int fadeSpeed = 16; //time between steps in miliseconds
    boolean fadeOut = true; //weither we are fading in or our

    boolean fadingCurrently = false; //state for if we are fading. clock will run till this is false


    //black screen to cover entire screen!
    private BufferedImage blackScreen = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR);
    boolean useBlackScreen = false;

    ViewPortImageHandler imageManager;


    public ViewPortFadeDisplay(ViewPortImageHandler imageManager){
        this.imageManager = imageManager;
    }

    /**
     *
     * create a new timer that starts whenever a fade action is happening!
     *
     */

    Timer timer = new Timer(0, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(fadingCurrently){
                fadeUpdateCall();
            } else {
                timer.stop();
            }
        }
    });

    /**
     *
     * called when fade is completed
     *
     */

    private void fadeComplete(){
        //image = null; //set image to clear!
        imageManager.callFadeFinishUpdate();
    }

    /**
     *
     * starts fade action, setting the fade to either full on or full off depending on boolean
     * cannot be called if already fading
     */

    public void startFade(float transModifier, int fadeSpeed, boolean fadeOut, boolean blackScreen){
        if(image == null && blackScreen == false)return; //no need to fade if there is no image to fade~!
        if(fadingCurrently)return; //if we are fading, ignore fade request!
        //fading started: set parameters
        this.fadeModifier = transModifier;
        this.fadeSpeed = fadeSpeed;
        this.fadeOut = fadeOut;

        //do error checking to see if numbers are within bounds:
        if(transModifier < 0.0001f)transModifier = 0.0001f; //lower limit to trans modifier
        if(transModifier > 1.0f)transModifier = 1.0f; //upper limit of trans modifier
        if(fadeSpeed < 0)fadeSpeed = 0; //lower fadespeed limit!

        this.useBlackScreen = blackScreen;

        //set opaque to min or max:
        if(fadeOut){
            //fading out, opaque should decrease!
            opaque = 1.0f;
            fadingLimit = 0.0f;
        } else {
            //fading in, opaque should increase!
            opaque = 0.0f;
            fadingLimit = 1.0f;
        }
        //start timer!
        timer.setDelay(fadeSpeed);
        timer.start();
        fadingCurrently = true;
    }

    /**
     *
     * called every time by timer. updates the transparency modifiers and calls repaint. sets fading to false when it reaches the end!
     */

    private void fadeUpdateCall(){
        //apply modifier. checks to go in right direction
        if(fadeOut){
            //fading out, opaque should decrease!
            opaque = opaque - fadeModifier;
        } else {
            //fading in, opaque should increase!
            opaque = opaque + fadeModifier;
        }

        //ensure that opaque doesnt leave bounds:
        if(opaque < 0.0f)opaque = 0.0f;
        if(opaque > 1.0f)opaque = 1.0f;

        //check if a limit has been reached. if so, set fading to false
        if(opaque == fadingLimit){
            fadingCurrently = false;
            fadeComplete();
        }

        //update image
        repaint();
    }



    @Override
    public void paintImage(Graphics2D graphics2D){
        if(useBlackScreen){
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opaque);
            graphics2D.setComposite(ac);
            graphics2D.drawImage(blackScreen, imagePosX + imageOffsetX, imagePosY + imageOffsetY,null); //draw image to screen
        } else {
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opaque);
            graphics2D.setComposite(ac);
            graphics2D.drawImage(image, imagePosX + imageOffsetX, imagePosY + imageOffsetY,null); //draw image to screen
        }

    }

    /**
     *
     * updates black screen to fit parent frame size!
     *
     */

    public void updateBlackScreen(int imageX, int imageY){
        if(initialised == false)return; //not initialised!

        blackScreen = new BufferedImage(imageX, imageY, BufferedImage.TYPE_BYTE_GRAY);//blackScreen = new BufferedImage(imageX, imageY, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics2D = blackScreen.createGraphics();
        //graphics2D.setBackground(Main.genericViewportBackground);
        graphics2D.setColor(Main.genericViewportBackground);
        graphics2D.fillRect(-20, 0, imageX, imageY);
        graphics2D.dispose();
        if(image == null){
            this.image = blackScreen;
        }
        updateImagePosition();
    }
}
