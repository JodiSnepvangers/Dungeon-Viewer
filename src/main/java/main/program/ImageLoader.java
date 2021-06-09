package main.program;

import main.Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ImageLoader {

    /**
     *
     * loads all images from disk!. should support not finding images!
     *
     */

    public static HashMap<Integer, ImageContainer> floorLibary = new HashMap<>();
    public static int maxPositiveFloors = 0;
    public static int maxNegativeFloors = 0;
    public static int maxImageWidth;
    public static int maxImageHeight;
    public static boolean finishedLoading = false;

    public ImageLoader(){

        //intialise all variables
        File folder;
        List<File> fileList;
        List<ImageContainer> containerList;
        HashMap<Integer, ImageContainer> floorList;

        //run all routines!
        folder = handleFilePath(); //creates input folder and retrieves it
        fileList = retrieveFiles(folder); //takes input folder and loads all files into list
        fileList = filterSupportedImages(fileList); //takes file list and removes all unsupported files

        //error message!
        if(fileList.size() == 0){
            //file list was 0 after support filter! toss error!
            String supported = "";
            for(int i = 0; i < Main.supportedImages.length; i++){
                supported = supported + Main.supportedImages[i] + " ";
            }
            ProgramHandler.tossError("No supported files were found! ensure only to use: " + supported, true);
            return;
        }

        containerList = packImageContainer(fileList); //packs files into ImageContainers and loads them into ram
        containerList = filterIllegalFileNames(containerList); //searches all file names, and tosses out those which arent numbers!

        //error message!
        if(containerList.size() == 0){
            ProgramHandler.tossError("all images found are in the wrong format. name them according to floor number!", true);
            return;
        }

        //check if there is a 0 ground floor!
        boolean noZeroGround = true;
        for(ImageContainer image : containerList){
            if(image.name.equals("0")){
                noZeroGround = false;
                break;
            }
        }
        if(noZeroGround){
            //there was no zero floor! toss error!
            ProgramHandler.tossError("There was no ground floor defined! please name a file '0'", true);
        }

        floorList = createFloorList(containerList); //generates a hashmap with all floors contained
        floorList = floorFilter(floorList); //filters out unconnected floors!



        //save data for consumption
        floorLibary = floorList;
        calculateBiggestImage(floorList);

        finishedLoading = true;


        //debug output!
        for (ImageContainer file : floorList.values()) {
            System.out.println(file.name + "." + file.extension + " " + file.image.getWidth() + "x" + file.image.getHeight());
        }
        System.out.println("positive floors: " + maxPositiveFloors);
        System.out.println("negative floors: " + maxNegativeFloors);
        System.out.println("max height:  " + maxImageHeight);
        System.out.println("max width:  " + maxImageWidth);
    }

    /**
     * creates the file path, and tosses a error if input folder did not exist!
     * returns folder created
     *
     */

    private File handleFilePath(){
        //get base path of the program
        File currentDir = new File("");
        String basePath = currentDir.getAbsolutePath();

        //creates the path if it doesnt already exist!
        File folder = new File(basePath + "/input");
        if(folder.exists() == false){
            folder.mkdir();
            ProgramHandler.tossError("Input folder created! Please fill it up!", true);
        }
        return folder;
    }

    /**
     *
     * given a folder, returns all files inside!
     * @param folder
     * @return
     */

    private List<File> retrieveFiles(File folder){
        //retrieves a list of files from that directory!
        File[] listOfFiles = folder.listFiles();
        if(listOfFiles.length == 0){
            ProgramHandler.tossError("Input folder was empty! Please fill it up!", true);
        }
        List<File> outPut = new ArrayList<>();
        outPut.addAll(Arrays.asList(listOfFiles));
        return outPut;
    }

    /**
     *
     * given a list of files, filters out those who do not match the image types
     * @param listOfFiles
     * @return
     */
    private List<File> filterSupportedImages(List<File> listOfFiles){
        //create use able list for storage!
        List<File> fileList = new ArrayList<>();

        //check all files for their extentions! remove those who do not match!
        for (File file : listOfFiles) {
            String fileExt = file.getName().split("\\.")[1].toLowerCase(); //get file extention
            if(file.isFile() == false)continue; //if not a file, continue
            if(ImageContainer.supportedImages.contains(fileExt) == false)continue; //file is not supported! continue!
            //image is supported! save!
            fileList.add(file);
        }
        return fileList;
    }


    /**
     *
     * packs all files into ImageContainers, loading the images!
     * @param listOfFiles
     * @return
     */

    private List<ImageContainer> packImageContainer(List<File> listOfFiles){
        List<ImageContainer> containerList = new ArrayList<>();
        for(File file : listOfFiles){
            ImageContainer fileCon = new ImageContainer(file);
            containerList.add(fileCon);
        }
        List<ImageContainer> output = new ArrayList<>();
        output.addAll(containerList);
        return output;
    }

    /**
     *
     * goes though image containers, and filters out those who do not have a number name!
     * @param list
     * @return
     */

    private List<ImageContainer> filterIllegalFileNames(List<ImageContainer> list){
        List<ImageContainer> outputList = new ArrayList<>();
        for(ImageContainer image : list){
            try{
                Integer.parseInt(image.name);
                outputList.add(image);
            } catch (NumberFormatException e){
                e.printStackTrace();
            }

        }
        return outputList;
    }

    /**
     *
     *
     * takes container images and puts them in a hashmap, based on what floor number they represent
     * @param list
     * @return
     */

    private HashMap<Integer, ImageContainer> createFloorList(List<ImageContainer> list){
        HashMap<Integer, ImageContainer> outputList = new HashMap<>();
        for(ImageContainer imageContainer : list){
            int floorNumber = 0;
            try{
                floorNumber = Integer.parseInt(imageContainer.name);
            } catch (NumberFormatException e){
                ProgramHandler.tossError("floor number failed to be parsed! notify the developer!", true);
                return null;
            }
            outputList.put(floorNumber, imageContainer);
        }
        return outputList;
    }

    /**
     *
     * goes though floors and removes those who arent connected with floors in between
     * with floors: -3, -1, 0, 1, 2, 3, 4, 6, 7.
     * floors -3, 6, 7 are removed!
     *
     * also sets maximumFloors and minimumFloors
     * @param intakeList
     * @return
     */

    private HashMap<Integer, ImageContainer> floorFilter(HashMap<Integer, ImageContainer> intakeList){
        //create hashmap for specific floor creation
        HashMap<Integer, ImageContainer> outputList = new HashMap<>();

        //go though images. start at 0 and work up!
        boolean endNotReached = true;
        int count = 0;
        while(endNotReached){
            //counts up and puts images in hashmap!
            ImageContainer image = intakeList.get(count);
            if(image == null){
                endNotReached = false;
            } else {
                outputList.put(count, intakeList.get(count));
                count++;
            }
        }
        maxPositiveFloors = count - 1; //set global var

        //positive numbers discovered! going negative!
        endNotReached = true;
        count = -1;
        while(endNotReached){
            //counts down and puts images in hashmap!
            ImageContainer image = intakeList.get(count);
            if(image == null){
                endNotReached = false;
            } else {
                outputList.put(count, intakeList.get(count));
                count--;
            }
        }

        maxNegativeFloors = count + 1; //set global var!

        //check if there are any removed floors. if so, notify the user!
        if(intakeList.size() != outputList.size()){
            //floor numbers dont match!
            ProgramHandler.tossError("there were '" + (intakeList.size() - outputList.size()) + "' unused rooms! ensure image names are correct", false);
        }

        return outputList;
    }

    /**
     *
     * takes the final floor list, and calculates the biggest image within, setting the global variables!
     *
     */

    private void calculateBiggestImage(HashMap<Integer, ImageContainer> floorList){
        int biggestWidth = 0;
        int biggestHeight = 0;
        for(ImageContainer floor : floorList.values()){
            int floorWidth = floor.image.getWidth();
            int floorHeight = floor.image.getHeight();
            if(floorWidth > biggestWidth){biggestWidth = floorWidth;}
            if(floorHeight > biggestHeight){biggestHeight = floorHeight;}
        }
        if(biggestWidth > Main.maxWindowWidth)biggestWidth = Main.maxWindowWidth;
        if(biggestHeight > Main.maxWindowHeight)biggestHeight = Main.maxWindowHeight;
        maxImageHeight = biggestHeight;
        maxImageWidth = biggestWidth;
    }



    public static class ImageContainer{
        /**
         * holds file data, such as image, name, and extention
         *
         */
        public static List<String> supportedImages = new ArrayList<>(Arrays.asList(Main.supportedImages));

        BufferedImage image;
        String name;
        String extension;

        public ImageContainer(File file){
            this.name = file.getName().split("\\.")[0];
            this.extension = file.getName().split("\\.")[1];
            image = new BufferedImage(1, 1,BufferedImage.TYPE_3BYTE_BGR);
            if(Main.forgoImageLoading)return; //cancels image loading routines!
            try {
                image = ImageIO.read(new File(file.getPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    }

}
