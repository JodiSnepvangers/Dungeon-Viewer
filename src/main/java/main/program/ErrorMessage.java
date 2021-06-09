package main.program;

import javax.swing.*;
import java.awt.*;

public class ErrorMessage extends JFrame {
    /**
     *
     * can be used to show fatal or non fatal errors!
     *
     */
    public ErrorMessage(String errormsg, boolean fatal){
        //set window parameters!
        setBounds(0, 0, 500, 200);
        setLayout(new BorderLayout());
        JLabel label;
        if(fatal){
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            label = new JLabel("A fatal error has occurred during runtime!", SwingConstants.CENTER);
        } else {
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            label = new JLabel("A error has occurred during runtime!", SwingConstants.CENTER);
        }
        setAlwaysOnTop(true);
        setResizable(false);


        JLabel error = new JLabel(errormsg, SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);
        add(error, BorderLayout.CENTER);


        setVisible(true);
    }
}
