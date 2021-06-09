package main.program.viewpanel.contents.icons;

import main.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class IconRawContainer {

    final int defaultCurrent = 10; //default current health for icons with no saved value
    final int defaultMaximum = 10; //default maximum health for icons with no saved value

    BufferedImage image; //icon image from file
    String name = ""; //icon name from file\
    String extension = ""; //icon name from file

    int currentHealth = 0; //current health of this icon. saved to disk
    int maximumHealth = 0; //maximum health of this icon. saved to disk
    boolean dead = false; //if true, the icon is displayed in grayscale. is not saved to disk
    boolean automaticDeath = true; //if true, 'dead' is set to true if health is or below 0, or it is false otherwise. is not saved to disk
    boolean nameVisibility = false; //if true, name is shown on screen
    int localScale = 100; //local scale value of icon object

    private boolean isGeneric = false; //if true, this icon was created on the spot, and is not a unique icon

    public IconRawContainer(File imageFile){
        this.name = imageFile.getName().split("\\.")[0];
        this.extension = imageFile.getName().split("\\.")[1];
        image = new BufferedImage(1, 1,BufferedImage.TYPE_3BYTE_BGR);
        if(Main.forgoImageLoading)return; //cancels image loading routines!
        try {
            image = ImageIO.read(new File(imageFile.getPath()));
            resizeImage(); //limits image to 100x100 by default
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resizeImage(){
        /**
         * resizes image to a fixed size so icons are limited!
         */
        image = Main.resize(image, Main.iconResizeX, Main.iconResizeY);
    }



    public IconRawContainer(BufferedImage iconImage, String name){
        this.image = iconImage;
        this.name = name;
        isGeneric = true;
    }


    public BufferedImage getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }

    public boolean isAutoDeath() {
        return automaticDeath;
    }

    public boolean isDead() {
        updateDeathValue();//update dead value to current health
        return dead;
    }

    public void setAutoDeath(boolean automaticDeath) {
        this.automaticDeath = automaticDeath;
    }

    public void setDead(boolean dead) {
        this.dead = dead; //set dead value
        updateDeathValue();//update dead value to current health
    }

    public boolean nameVisibility() {
        return nameVisibility;
    }

    public void setNameVisibility(boolean nameVisibility) {
        this.nameVisibility = nameVisibility;
    }

    public void setLocalScale(int localScale) {
        this.localScale = localScale;
    }

    public int getLocalScale() {
        return localScale;
    }

    public boolean isGeneric() {
        return isGeneric;
    }

    public String getHealthAsString(){
        //returns the currenthealth and max health as a string: currentHealth/MaxHealth
        return "" + currentHealth + "/" + maximumHealth;
    }

    public void setHealthfromString(String healthString){
        //converts string back into useable numbers: currentHealth/MaxHealth
        if(healthString.split("/").length != 2)return; //health string was not in correct amount!

        //retrieve values from string!
        String currentString = healthString.split("/")[0];
        String maximumString = healthString.split("/")[1];

        //convert text to numbers!
        int currentHealth = defaultCurrent;
        int maximumHealth = defaultMaximum;
        try{
            currentHealth = Integer.parseInt(currentString);
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        try{
            maximumHealth = Integer.parseInt(maximumString);
        } catch (NumberFormatException e){
            e.printStackTrace();
        }

        //set internal variables!
        this.currentHealth = currentHealth;
        this.maximumHealth = maximumHealth;
    }

    private void updateDeathValue(){
        if(automaticDeath){
            //does a check whenever health is updated!
            dead = (currentHealth <= 0);
        }
    }

    public void setHealth(int currentHealth, int maximumHealth){
        updateDeathValue();
        this.currentHealth = currentHealth;
        this.maximumHealth = maximumHealth;
    }

    public int getCurrentHealth() {
        updateDeathValue();
        return currentHealth;
    }

    public int getMaximumHealth() {
        updateDeathValue();
        return maximumHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        updateDeathValue();
        this.currentHealth = currentHealth;
    }

    public void setMaximumHealth(int maximumHealth) {
        updateDeathValue();
        this.maximumHealth = maximumHealth;
    }
}
