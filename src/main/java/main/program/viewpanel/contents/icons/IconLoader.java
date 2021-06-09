package main.program.viewpanel.contents.icons;

import main.Main;
import main.program.ImageLoader;
import main.program.ProgramHandler;
import main.program.properties.PropertyStorage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class IconLoader {

    /**
     * creates icon folder and loads all images found within it. loads unqiue icons from disk on start
     * on request, will create all unique icons inside iconManager
     *
     * TODO: limit intake resolution to 100x100. rescale any incoming images to this size if they are too large or too small
     *
     */

    IconManager iconManager; //parent icon manager
    List<IconRawContainer> iconRawContainerList = new ArrayList<>();

    public IconLoader(IconManager iconManager){
        this.iconManager = iconManager;
        loadingRoutine();
        try {
            loadFromDisk();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
                    saveToDisk();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    /**
     *
     * loading routine. called on creation and will load all images into memory
     *
     */

    private void loadingRoutine(){
        //creating generic variables:
        File folderPath; //folder path
        List<File> fileList; //all files found at this folder


        folderPath = handleFilePath(); //retrieves file path. generates folder if path does not exist!
        fileList = retrieveFiles(folderPath); //retrieves all files inside folder.
        fileList = filterSupportedImages(fileList); //filter out all file types that are not supported!
        iconRawContainerList = packIconRawContainer(fileList); //creates containers for all files found and supported!

        //debugging information send to console:
        System.out.println(iconRawContainerList.size());
        for(IconRawContainer icon : iconRawContainerList){
            System.out.println(icon.getName() + ": " + icon.getImage().getWidth() + "x" + icon.getImage().getHeight());
        }
    }


    private File handleFilePath(){
        //get base path of the program
        File currentDir = new File("");
        String basePath = currentDir.getAbsolutePath();

        //creates the path if it doesnt already exist!
        File folder = new File(basePath + "/counters");
        if(folder.exists() == false){
            folder.mkdir();
        }
        return folder;
    }

    private List<File> retrieveFiles(File folder){
        //retrieves a list of files from that directory!
        File[] listOfFiles = folder.listFiles();
        List<File> outPut = new ArrayList<>();
        outPut.addAll(Arrays.asList(listOfFiles));
        return outPut;
    }

    private List<File> filterSupportedImages(List<File> listOfFiles){
        //create use able list for storage!
        List<File> fileList = new ArrayList<>();

        //check all files for their extentions! remove those who do not match!
        for (File file : listOfFiles) {
            String fileExt = file.getName().split("\\.")[1].toLowerCase(); //get file extention
            if(file.isFile() == false)continue; //if not a file, continue
            if(ImageLoader.ImageContainer.supportedImages.contains(fileExt) == false)continue; //file is not supported! continue!
            //image is supported! save!
            fileList.add(file);
        }
        return fileList;
    }

    private List<IconRawContainer> packIconRawContainer(List<File> listOfFiles){
        List<IconRawContainer> containerList = new ArrayList<>();
        for(File file : listOfFiles){
            IconRawContainer fileCon = new IconRawContainer(file);
            containerList.add(fileCon);
        }
        List<IconRawContainer> output = new ArrayList<>();
        output.addAll(containerList);
        return output;
    }










    /**
     *
     * property handler: handles the loading and saving of health values from and to disk.
     * TODO: create new saving and loading routines, using IconRawContainer as a middle man! each IconContainer should be given their raw container too! property manager uses raw containers to load and save data
     */

    final String fileName = "counters/Health.txt"; //file name to store health values
    final int defaultCurrent = 10; //default current health for icons with no saved value
    final int defaultMaximum = 10; //default maximum health for icons with no saved value

    private void saveToDisk() throws IOException {
        File folderPath = handleFilePath();
        //create properties group
        Properties prop = new Properties();

        //loop: save all in save text list!
        for(IconRawContainer icon : iconRawContainerList){
            prop.setProperty(icon.name, icon.getHealthAsString());
        }
        prop.store(new FileOutputStream(fileName), "stored health values of all unique icons! currentHealth/maximumHealth");
    }


    private void loadFromDisk() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream(fileName));

        //go into for loop. discover all elements and try to load them. if nothing is found, load defaults!
        for(IconRawContainer icon : iconRawContainerList){
            String keyString = icon.getName(); //turn key into string
            String values = prop.getProperty(keyString); //attempt to load property

            //create temporary variables
            String valueString = defaultCurrent + "/" + defaultMaximum; //default. overridden by values if values is tested correctly!

            //do error checking:
            if(values != null) {//only run if a value was found to be saved!
                if(values.split("/").length == 2) {//must find only 2 values, else dont load key!
                    valueString = values; //value was successfully tested!
                }
            }

            //save key to icon raw container!~
            icon.setHealthfromString(valueString);
        }
    }
}
