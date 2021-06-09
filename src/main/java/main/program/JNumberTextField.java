package main.program;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class JNumberTextField extends JTextField {

    /**
     *
     * a text field variant that accepts only numbers, resets itself if a wrong value is left, and automatically converts text to number
     *
     */

    int numberValue = 0;

    Color wrongColor = Color.RED;
    Color correctColor = Color.white;

    boolean isCorrect = true;


    public JNumberTextField(){
        initialise();
    }

    public JNumberTextField(int startingValue){
        numberValue = startingValue;
        initialise();
    }

    public JNumberTextField(int startingValue, Color wrongColor){
        numberValue = startingValue;
        this.wrongColor = wrongColor;
        initialise();
    }

    public JNumberTextField(int startingValue, Color wrongColor, Color correctColor){
        numberValue = startingValue;
        this.wrongColor = wrongColor;
        this.correctColor = correctColor;
        initialise();
    }


    private void initialise(){
        setText(numberValue + "");
        //add listeners:
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                illegalSymbolChecker();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                illegalSymbolChecker();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                illegalSymbolChecker();
            }
        });

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateNumber();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == 10){
                    setFocusable(false);
                    setFocusable(true);
                }
            }
        });
    }

    private void updateNumber(){
        if(isCorrect){
            try{
                numberValue = Integer.parseInt(getText());
                setText(numberValue + "");
            } catch (NumberFormatException numberFormatException) {
                numberFormatException.printStackTrace();
                setText(numberValue + "");
                isCorrect = true;
            }
        } else {
            //entered string was invalid! get last valid value!
            setText(numberValue + "");
            isCorrect = true;
        }
    }

    private void illegalSymbolChecker(){
        String string = getText();

        if(string.startsWith("-")){
            //removes minus symbol, but only from start
            string = string.substring(1);
        }

        if(string.matches("[0-9]+") == false){
            //checks if string is only numbers
            setForeground(wrongColor);
            isCorrect = false;
        } else {
            setForeground(correctColor);
            isCorrect = true;
        }
    }

    public void checkValue(){
        /**
         * forces a value check (normally this is done automatically on focus loss
         *
         */
        updateNumber();
    }

    public int getNumberValue(){
        return numberValue;
    }

    public void setNumberValue(int numberValue) {
        this.numberValue = numberValue;
        setText(numberValue + "");
    }

    public Color getCorrectColor() {
        return correctColor;
    }

    public Color getWrongColor() {
        return wrongColor;
    }

    public void setCorrectColor(Color correctColor) {
        this.correctColor = correctColor;
        illegalSymbolChecker();
    }

    public void setWrongColor(Color wrongColor) {
        this.wrongColor = wrongColor;
        illegalSymbolChecker();
    }
}
