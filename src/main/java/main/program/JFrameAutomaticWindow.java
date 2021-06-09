package main.program;

import main.program.properties.PropertyHandler;
import main.program.properties.PropertyStorage;
import main.program.properties.WindowPropertyContainer;
import org.omg.CORBA.Bounds;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class JFrameAutomaticWindow extends JFrame {

    /**
     *
     * automatically adds loading and saving of window location and size
     * will set bounds
     */

    public PropertyStorage.WindowType windowType;

    public void initialiseAutomaticWindow(PropertyStorage.WindowType windowType){
        if(windowType == null){
            System.out.println("WINDOW TYPE WAS NULL!!! AUTOMATIC FUNCTIONS DISABLED");
            return;
        }

        if(this.windowType != null){
            System.out.println("AUTOMATIC WINDOW ALREADY INITIALISED!");
            return;
        }
        this.windowType = windowType;

        //adds listeners for when windows are moved or resized!
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                updateCall();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                updateCall();
            }
        });

        //set bounds!
        setBounds(PropertyStorage.propertyLibrary.get(windowType).getBounds());
    }

    /**
     *
     * acesses propertyStorage and saves location and size to there
     *
     */
    private void updateCall(){
        PropertyStorage.propertyLibrary.put(windowType, new WindowPropertyContainer(getBounds(), windowType));

    }
}
