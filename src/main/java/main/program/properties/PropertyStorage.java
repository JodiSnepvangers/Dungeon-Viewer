package main.program.properties;

import java.util.HashMap;

public class PropertyStorage {
    /**
     *
     * storage for all possible properties.
     *
     */

    public static HashMap<WindowType, WindowPropertyContainer> propertyLibrary = new HashMap<>();

    public enum WindowType {
        CONTROLPORT,
        VIEWPORT,
        VIEWCONTROLS,
        ICONCONTROL
    }

    public PropertyStorage(){
        generateDefaultValues();
    }

    public static void generateDefaultValues(){
        int posX;
        int posY;
        int sizeX;
        int sizeY;
        WindowType windowType;

        //initialise a list of default values:
        posX = 0;
        posY = 0;
        sizeX = 900;
        sizeY = 600;
        windowType = WindowType.CONTROLPORT;
        propertyLibrary.put(windowType, new WindowPropertyContainer(posX, posY, sizeX, sizeY, windowType));

        posX = 0;
        posY = 0;
        sizeX = 1000;
        sizeY = 1000;
        windowType = WindowType.VIEWPORT;
        propertyLibrary.put(windowType, new WindowPropertyContainer(posX, posY, sizeX, sizeY, windowType));

        posX = 0;
        posY = 0;
        sizeX = 400;
        sizeY = 400;
        windowType = WindowType.VIEWCONTROLS;
        propertyLibrary.put(windowType, new WindowPropertyContainer(posX, posY, sizeX, sizeY, windowType));

        posX = 0;
        posY = 0;
        sizeX = 1000;
        sizeY = 800;
        windowType = WindowType.ICONCONTROL;
        propertyLibrary.put(windowType, new WindowPropertyContainer(posX, posY, sizeX, sizeY, windowType));
    }
}
