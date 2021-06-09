package main.program.properties;

import main.program.ProgramHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class PropertyHandler {

    HashMap<String, String> textSaveList = new HashMap<>();

    final String fileName = "WindowConfig.properties";

    PropertyStorage storage = new PropertyStorage();
    /**
     *
     * saves and loads properties from disk!
     *
     */

    public PropertyHandler(){
        loadPropsFromDisk();
    }

    /**
     *
     * load routines to load all values to disk!
     *
     *
     */

    public void loadPropsFromDisk(){
        //first, try to load from disk. if fails, create defaults
        try {
            load();
        } catch (IOException e) {
            try {
                createDefault();
            } catch (IOException ioException) {
                ioException.printStackTrace();

                //if this is ever thrown, we have a big problem!
                ProgramHandler.tossError("a terrible error has occured while trying to create defaults", false);
            }
        }

        updateGlobalLibrary();
    }


    /**
     *
     * save routines to save all values from disk!
     *
     *
     */

    public void savePropsToDisk(){
        //update local list:
        updateTextSaveList();

        //attempt to save it to disk!
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    /**
     *
     * updates the internal propery save list to prepare for saving
     *
     */

    private void updateTextSaveList(){
        //retrieve library and start loop
        HashMap<PropertyStorage.WindowType, WindowPropertyContainer> propertyLibrary = PropertyStorage.propertyLibrary;
        for(PropertyStorage.WindowType windowType : propertyLibrary.keySet()){
            //create a string for saving:
            String propertyToText = "";

            //retrieve each container in the library, and turn them into a readable string
            WindowPropertyContainer container = propertyLibrary.get(windowType);

            //save variables in a readable format
            propertyToText = container.posX + ", ";
            propertyToText = propertyToText + container.posY + ", ";
            propertyToText = propertyToText + container.sizeX + ", ";
            propertyToText = propertyToText + container.sizeY;

            //push variable to textSaveList
            textSaveList.put(windowType.name(), propertyToText);
        }
    }

    /**
     *
     * reads the internal text save list and updates the global property library
     *
     */

    private void updateGlobalLibrary(){
        //create new library and start loop
        HashMap<PropertyStorage.WindowType, WindowPropertyContainer> propertyLibrary = new HashMap<>();

        boolean loadError = false; //if this boolean is found to be true, then a load error happened. must reset saved file
        for(String key : textSaveList.keySet()){

            //try block. tries to find enum in list
            PropertyStorage.WindowType windowType;
            try {
                windowType = PropertyStorage.WindowType.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException e) {
                loadError = true;
                e.printStackTrace();
                break;
            }

            //loading succeeded. save data to library
            String[] valueList = textSaveList.get(key).split(", ", 4);
            int posX;
            int posY;
            int sizeX;
            int sizeY;

            //try parsing int!
            try {
                posX = Integer.parseInt(valueList[0]);
                posY = Integer.parseInt(valueList[1]);
                sizeX = Integer.parseInt(valueList[2]);
                sizeY = Integer.parseInt(valueList[3]);
            } catch (NumberFormatException e) {
                loadError = true;
                e.printStackTrace();
                break;
            }

            //file checks out! save!
            propertyLibrary.put(windowType, new WindowPropertyContainer(posX, posY, sizeX, sizeY, windowType));
        }
        //finished converting! check if error occured:
        if(loadError){
            ProgramHandler.tossError("a load error occured. window config file reset", false);
        } else {
            //error didnt occur. safe to overwrite
            PropertyStorage.propertyLibrary.putAll(propertyLibrary);
        }

    }

    /**
     *
     *
     * saves current text save list to property file on disk
     */


    private void save() throws IOException {
        //create properties group
        Properties prop = new Properties();

        //loop: save all in save text list!
        for(String key : textSaveList.keySet()){
            String values = textSaveList.get(key);
            prop.setProperty(key, values);
        }
        prop.store(new FileOutputStream(fileName), "Location and size of all windows");
    }

    /**
     *
     *loads file from disk and saves it in textsavelist for further work
     */

    private void load() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream(fileName));

        //go into for loop. discover all elements and try to load them. at null, nothing should be saved!
        for(PropertyStorage.WindowType windowType : PropertyStorage.WindowType.values()){
            String keyString = windowType.name(); //turn key into string
            String values = prop.getProperty(keyString); //attempt to load property

            //do error checking:
            if(values == null)continue; // if no such key was found, continue to next key
            if(values.split(", ").length != 4)continue; //must find only 4 values, else dont load key!

            //no errors found! key can be saved!
            textSaveList.put(keyString, values);
        }
    }

    /**
     *
     * creates a default file on root. also sets all other variables such as the local and global propery table to their defaults
     *
     */

    private void createDefault() throws IOException {
        PropertyStorage.generateDefaultValues();
        updateTextSaveList();
        save();
    }
}
