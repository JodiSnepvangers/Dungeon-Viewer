package main.program.viewpanel.contents.icons;

import main.program.FloorContainer;
import main.program.ProgramHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IconManager {

    /**
     *
     * icon manager: controls and contains the list of icons currently in memory. also initializes loading of all unique icons
     * contains the code that is also used to create new icons
     *
     * ensure that icons can be saved and loaded from and to floor containers
     *
     */

    //loading default images:

    public static BufferedImage genericEnemy;

    {
        try {
            genericEnemy = ImageIO.read(getClass().getResource("/genericEnemy.png"));
        } catch (IOException e) {
            e.printStackTrace();
            ProgramHandler.tossError("GenericEnemy icon failed to load", true);
        }
    }

    public static BufferedImage genericAlly;

    {
        try {
            genericAlly = ImageIO.read(getClass().getResource("/genericAlly.png"));
        } catch (IOException e) {
            e.printStackTrace();
            ProgramHandler.tossError("GenericAlly icon failed to load", true);
        }
    }

    public static BufferedImage genericNeutral;

    {
        try {
            genericNeutral = ImageIO.read(getClass().getResource("/genericNeutral.png"));
        } catch (IOException e) {
            e.printStackTrace();
            ProgramHandler.tossError("GenericNeutral icon failed to load", true);
        }
    }


    //handle internal variables:
    IconLoader iconLoader = new IconLoader(this);
    IconControlFrame iconControlFrame = new IconControlFrame(this);

    //floor memory system:
    HashMap<Integer, List<IconContainer>> floorIconMemory = new HashMap<>(); //stores the list of icon containers for each floor!
    int currentFloor = 0;


    public IconManager(){
        generateDefault(); //generate a default list on startup!
        iconControlFrame.updateCall();
    }

    /**
     *
     * saves current icon list to old floor container and then loads new list from new floor container.
     * if the new floor container has no list, then a default one is generated instead!
     *
     * alternatively, stores its own local list for each floor!
     *
     * @param newFloor new floor number
     */

    public void floorChange(int newFloor){
        //TODO: ensure interface is reset and updated for new icon list!
        //TODO: update internal floor variable

        //change internal number
        currentFloor = newFloor;

        //send update call to iconControlFrame
        iconControlFrame.updateCall();

        //send update to icon panel
        updateIconPanel();
    }

    /**
     *
     * generates a default icon list! overrides current icon list!
     * by default, all unique icons are loaded!
     *
     * when a new floor is visited, this list must be populated by all unique icons!
     *
     */

    public void generateDefault(){
        List<IconContainer> iconList = new ArrayList<>(); //generates a icon list!

        for(IconRawContainer icon : iconLoader.iconRawContainerList){
            IconContainer iconContainer = new IconContainer(icon, this); //generate a icon container for icon!
            iconList.add(iconContainer);
        }

        floorIconMemory.put(currentFloor, iconList);
    }

    /**
     *
     * erases the selected icon from the floor and from the icon menu
     *
     */

    public void eraseIconContainer(IconContainer icon){
        floorIconMemory.get(currentFloor).remove(icon);
        iconPanel.updateDisplayedIcons();
        iconControlFrame.updateCall();
    }

    /**
     *
     * generates a new icon container and adds it to the current floor!
     *
     *
     */

    public IconContainer generateIconContainer(BufferedImage iconImage, String name, int hpCurrent, int hpMaximum){
        IconRawContainer iconRawContainer = new IconRawContainer(iconImage, name); //generate a icon raw container. is not saved to the icon loader!!
        IconContainer iconContainer = new IconContainer(iconRawContainer, this); //generate a icon container for icon!
        iconContainer.setCurrentHealth(hpCurrent); //set current hp to icon
        iconContainer.setMaximumHealth(hpMaximum); //set maximum hp to icon
        floorIconMemory.get(currentFloor).add(iconContainer); //save icon to current floor
        iconPanel.updateDisplayedIcons();
        iconControlFrame.updateCall();
        return iconContainer;
    }

    /**
     *
     * gets the correct icon list based on current set floor!
     * should not give out copies of the array! instead, changes should be directly mirrored into the hashmap
     *
     */

    public List<IconContainer> getIconList(){
        List<IconContainer> iconList = new ArrayList<>();
        if(floorIconMemory.containsKey(currentFloor)){
            iconList = floorIconMemory.get(currentFloor);//key exists! return icon list!
        } else {
            floorIconMemory.put(currentFloor, iconList); //key doesnt exist! push empty list to memory!
            generateDefault();
            iconList = floorIconMemory.get(currentFloor);//key exists now! return icon list!
        }
        return iconList;
    }

    /**
     *
     * connects icon viewer panel to this. once attached, will send floor updates to icon viewer panel!
     *
     */

    IconsViewPanel iconPanel = null;

    public void connectIconPanel(IconsViewPanel iconPanel){
        this.iconPanel = iconPanel;
        updateIconPanel();
        System.out.println("Icon panel connected!");
        iconControlFrame.interfacePanel.sliderPanel.connectIconPanel(iconPanel);
    }

    /**
     *
     * sends icon update to icon panel if one exists!
     *
     */

    public void updateIconPanel(){
        if(iconPanel == null)return; //icon panel does not exist yet
        iconPanel.updateDisplayedIcons();
    }
}