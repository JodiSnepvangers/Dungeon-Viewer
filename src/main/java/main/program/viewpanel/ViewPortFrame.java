package main.program.viewpanel;

import main.Main;
import main.program.JFrameAutomaticWindow;
import main.program.ProgramHandler;
import main.program.controlpanel.contents.PaintingArea;
import main.program.properties.PropertyStorage;
import main.program.viewpanel.contents.DrawingLabel;
import main.program.viewpanel.contents.ViewPortImageDisplay;
import main.program.viewpanel.contents.ViewPortImageHandler;
import main.program.viewpanel.contents.icons.IconsViewPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ViewPortFrame extends JFrameAutomaticWindow {

    /**
     * the frame of the viewport. creates and displays the frame that should be streamed by discord
     *
     *
     */

    ViewPortImageHandler imageManager; //image manager that deals with floor and shadow layers

    public ViewPortFrame(ViewPortImageHandler imageManager){
        //retrieve image display objects:
        this.imageManager = imageManager;
        ViewPortImageDisplay imageLayer = imageManager.getImageLayer();
        ViewPortImageDisplay shadowLayer = imageManager.getShadowLayer();
        ViewPortImageDisplay ghostLayer = imageManager.getGhostLayer();
        DrawingLabel drawingLabel = imageManager.createDrawingLabel(this);

        //create icon layer
        IconsViewPanel iconLayer = new IconsViewPanel(this, drawingLabel.getIconManager());
        imageManager.updateIconViewPanel(iconLayer); //gives icon layer to image manager!

        //set frame parameters:
        initialiseAutomaticWindow(PropertyStorage.WindowType.VIEWPORT); //setup automatic window system
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle(Main.programName + ": Viewport");
        getContentPane().setBackground(Main.genericViewportBackground);

        //set layout and layered panel parameters
        setLayout(new BorderLayout());
        getLayeredPane().setLayout(null);

        //set component parameters:
        imageLayer.setBounds(0,0 , getWidth(), getHeight());
        shadowLayer.setBounds(0,0 , getWidth(), getHeight());
        iconLayer.setBounds(0,0,getWidth(), getHeight());


        getLayeredPane().add(imageLayer, new Integer(0));
        getLayeredPane().add(shadowLayer, new Integer(1));
        getLayeredPane().add(ghostLayer, new Integer(2));
        getLayeredPane().add(iconLayer, new Integer(3));
        getLayeredPane().add(drawingLabel, new Integer(4));

        //display frame
        setVisible(true);

        iconLayer.windowMoveUpdate();
        iconLayer.updateDisplayedIcons();



    }


    @Override
    public void paint(Graphics g) {
        super.paintComponents(g);
    }
}
