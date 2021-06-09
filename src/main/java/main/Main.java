package main;

import main.program.ImageLoader;
import main.program.ProgramHandler;
import main.program.properties.PropertyHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


public class Main {

    public static final String programVersion = "Beta 1.1";
    public static final String programName = "Dungeon Viewer" + " " + programVersion;

    public static final String[] supportedImages = {"jpg", "png"}; // supported image formats
    public static final boolean forgoImageLoading = false; //debug purposes! disables image loading to speed up load times

    public static int maxWindowWidth = 2560;
    public static int maxWindowHeight = 1080;

    public static int shadowTransparency = 180; // number between 0-255 to dictate shadow mask transparency in control panel
    public static float shadowTransparencyNew = 0.8f;

    public static final Color cursorColor = new Color(255, 100,100);

    public static final Color genericViewportBackground = new Color(10, 10, 10); //color of shadow mask and viewport background


    public static final double zoomModifier = 0.1;
    public static final double maxZoom = 8.0;
    public static final double minimumZoom = 0.2;

    public static final int iconResizeX = 100;
    public static final int iconResizeY = 100;





    public static final PropertyHandler propertyHandler = new PropertyHandler(); // handles saving of window data


    public static void main(String[] args) {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        UIManager.put("control", new Color(50, 50, 50));
        UIManager.put("nimbusFocus", new Color(20, 20, 20));
        UIManager.put("nimbusLightBackground", new Color(20, 20, 20));
        UIManager.put("textForeground", new Color(200, 200, 200));
        UIManager.put("nimbusBase", new Color(0, 0, 0));
        UIManager.put("InternalFrameTitlePane.background", new Color(255, 0, 0));

        new ImageLoader(); //load images from disk
        new ProgramHandler(); //load program handler, opens gui

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                propertyHandler.savePropsToDisk();
            }
        }));

    }

    /**
     *
     * returns a rescaled version of BufferedImage
     *
     * @param image image to be scaled
     * @param width width to scale to
     * @param height height to scale to
     * @return
     */

    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return bi;
    }


}
