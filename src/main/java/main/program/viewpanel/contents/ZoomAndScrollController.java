package main.program.viewpanel.contents;

import main.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import static main.Main.zoomModifier;

public class ZoomAndScrollController {

    /**
     *
     * standardises the zoom and scroll controls. must be extended in another class
     *
     */

    public void setupMouseListeners(JFrame targetFrame, boolean enableZoomScroll){

        targetFrame.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                scrollingUpdate();
            }
        });
        targetFrame.addMouseListener(new MouseAdapter() {
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

        // if true, enable zooming in and out with scroll wheel
        if(enableZoomScroll) {
            targetFrame.addMouseWheelListener(new MouseAdapter() {
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

    }


    /**
     *
     * handles scrolling by tracking the middle mouse button as the mouse is moved
     *
     *
     */

    int imageOffsetX = 0;
    int imageOffsetY = 0;

    int scrollOriginX = 0; //location of where mouse was when scroller is activated
    int scrollOriginY = 0; //location of where mouse was when scroller is activated

    int previousOffsetX = 0; //saved offset before scroller was enabled
    int previousOffsetY = 0; //saved offset before scroller was enabled

    boolean trackScrolling = false; //state of if scroller is enabled

    private void scrollerDown(){
        scrollOriginX = (int) ((MouseInfo.getPointerInfo().getLocation().x) / zoomValue);
        scrollOriginY = (int) ((MouseInfo.getPointerInfo().getLocation().y) / zoomValue);
        trackScrolling = true;
        previousOffsetX = imageOffsetX;
        previousOffsetY = imageOffsetY;
    }

    private void scrollerUp(){
        trackScrolling = false;
    }

    private void scrollingUpdate(){
        if(trackScrolling == false)return;

        int rawMousePosX = (int) ((MouseInfo.getPointerInfo().getLocation().x) / zoomValue);
        int rawMousePosY = (int) ((MouseInfo.getPointerInfo().getLocation().y) / zoomValue);

        imageOffsetX = (rawMousePosX - scrollOriginX) + previousOffsetX;
        imageOffsetY = (rawMousePosY - scrollOriginY) + previousOffsetY;

        //scroll offset changes: call update
        updateCall();
    }

    /**
     *
     * handles zooming in and out.
     * if true, increases value by fixed amount
     * if false, decrease value by fixed amount
     */

    double zoomValue = 1.0;

    public void scrollWheelUpdate(boolean increase){
        if(increase){
            if(zoomValue > Main.maxZoom)return; //upper zoom bound
            zoomValue = zoomValue + zoomModifier; //add zoom modifier
        } else {
            if(zoomValue <= Main.minimumZoom)return; //lower zoom bound
            zoomValue = zoomValue - zoomModifier; //remove zoom modifier
        }

        //zoom was changed. send update to image layers:
        updateCall();
    }

    public void setZoomValue(double zoomValue){
        //sets zoom value directly
        this.zoomValue = zoomValue;
        updateCall();
    }

    public double getZoomValue() {
        return zoomValue;
    }

    /**
     *
     *
     * this must be overwritten! update call is called when either the zoom value or scroll value change!
     *
     *
     */


    public void updateCall(){

    }
}
