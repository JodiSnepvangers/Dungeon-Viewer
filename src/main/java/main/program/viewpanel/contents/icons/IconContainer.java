package main.program.viewpanel.contents.icons;

import main.Main;
import main.program.JNumberTextField;
import main.program.controlpanel.contents.ControlInterface;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.nio.file.FileSystemNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class IconContainer {

    /**
     *
     * container for icon. contains image, and handles displaying them on the viewport, and also handles their gui displayed on the interaction control port.
     * does not store any information itself. information is stored in IconRawContainer so that multiple iconContainers can keep the same information!
     *
     * location on screen and invisiblility status are saved to the container itself. death status is saved to iconRawContainer!
     *
     * TODO: make generic icons eraseable: add a button to the upper right corner of the menu that has a red cross. if double clicked, generic icon is erased
     * TODO: either make a subset of the IconContainer for generic icons with this bit added (possibly too hard to implement)
     * TODO: or, make a internal boolean that determines the addition of this option!
     *
     */
    private IconPanel iconPanel; //menu accociated with this icon! is displayed in the interactive layer menu
    private IconRawContainer iconRawContainer; //icon raw container. contains icon image, name, and health! save all changes to this!
    private IconObject iconObject; //icon object. used by viewport for displaying
    private IconManager iconManager; //icon's manager. points back to parent that created it

    //internal variables: are saved individually per floor:
    boolean visible = false; //if true, the icon is displayed on the viewport

    public IconContainer(IconRawContainer iconRawContainer, IconManager iconManager){
        this.iconRawContainer = iconRawContainer;
        this.iconManager = iconManager;

        //set up panel: give it the current container!
        iconPanel = new IconPanel(this);

        //create viewport object!
        iconObject = new IconObject(this);
        updateIconObject();
    }

    public BufferedImage getImage(){
        return iconRawContainer.getImage();
    }

    public int getCurrentHealth(){
        return iconRawContainer.getCurrentHealth();
    }

    public int getMaximumHealth(){
        return iconRawContainer.getMaximumHealth();
    }

    public void setCurrentHealth(int value){
        iconRawContainer.setCurrentHealth(value);
        updateIconObject();
        updateIconMenu();
    }

    public void setMaximumHealth(int value){
        iconRawContainer.setMaximumHealth(value);
        updateIconObject();
        updateIconMenu();
    }

    public JPanel getInterface(){
        return iconPanel;
    }

    public String getName(){
        return iconRawContainer.getName();
    }

    public boolean isDead(){
        return iconRawContainer.isDead();
    }

    public void setDead(boolean value){
        iconRawContainer.setDead(value);
        updateIconObject();
    }

    public boolean isAutoDead(){
        return iconRawContainer.isAutoDeath();
    }

    public void setAutoDead(boolean value){
        iconRawContainer.setAutoDeath(value);
    }

    public boolean isIconVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        updateIconObject();
    }

    public boolean isNameVisible(){
        return iconRawContainer.nameVisibility();
    }

    public void setNameVisible(boolean value){
        iconRawContainer.setNameVisibility(value);
        updateIconObject();
    }

    public void setObjectLocalScale(int value){
        iconRawContainer.setLocalScale(value);
        iconObject.updateVisuals();
    }

    public double getObjectLocalScale(){
        return (double) iconRawContainer.getLocalScale() / 100;
    }

    public int getObjectLocalScaleInt(){
        return iconRawContainer.getLocalScale();
    }

    public boolean isGeneric(){
        return iconRawContainer.isGeneric();
    }

    public IconManager getIconManager(){
        return iconManager;
    }

    public void updateIconMenu(){
        iconPanel.updateInterface();
    }

    public void updateIconObject(){
        /**
         *
         * sends update command to icon object
         */
        iconObject.updateVisuals();
    }

    public IconObject getObject(){
        return iconObject;
    }

    /**
     *
     * interface side: holds a class thats unique for each container that holds the controls and information!
     *
     */

    private class IconPanel extends JPanel{

        //set up components:
        IconViewerPanel iconViewerPanel;
        HealthModifierPanel healthModifierPanel;
        CheckBoxPanel checkBoxPanel;
        SliderPanel sliderPanel;
        CloseButton closeButton;

        public IconPanel(IconContainer iconContainer){
            setPreferredSize(new Dimension(300, 450));
            setBackground(ControlInterface.backgroundColor); //TODO: move this constant to Main!
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setLayout(null);

            //deal with border
            Border border = BorderFactory.createLineBorder(Color.GRAY, 2, true);
            setBorder(border);

            //setting component parameters:
            iconViewerPanel = new IconViewerPanel(iconContainer);
            healthModifierPanel = new HealthModifierPanel(iconContainer);
            checkBoxPanel = new CheckBoxPanel(iconContainer);
            sliderPanel = new SliderPanel(iconContainer);
            closeButton = new CloseButton(iconContainer);

            //adding components. setting precise positions
            iconViewerPanel.setBounds(0,0, 150, 175);
            add(iconViewerPanel);
            healthModifierPanel.setBounds(0, 173, 300, 70);
            add(healthModifierPanel);
            checkBoxPanel.setBounds(150, 0, 100, 175);
            add(checkBoxPanel);
            sliderPanel.setBounds(0, 241, 300, 50);
            add(sliderPanel);
            if(iconContainer.isGeneric()){
                closeButton.setBounds(270, 0, 30, 30);
                add(closeButton);
            }
        }

        /**
         *
         * updates all interface elements when a change has been applied!
         *
         */

        public void updateInterface(){
            //TODO: program update code! update all displays! ensure correct image version is shown, health values are updated, and checkboxes are matched!
            iconViewerPanel.updateInterface(); //updates dead status of preview icon
            healthModifierPanel.updateInterface(); //updates health and max health!
            checkBoxPanel.updateInterface(); //updates dead checkbox!
            sliderPanel.updateInterface();
        }
    }

    /**
     *
     * components for interface down below!
     *
     */

    private class IconViewerPanel extends JPanel{
        /**
         *
         * shows off the icon in a preview window!
         *
         */

        //set up components:
        JLabel textLabel = new JLabel("Preview: ", SwingConstants.LEADING);
        JLabel iconLabel = new JLabel();

        IconContainer icon;
        boolean isDead = false; // the current state of the visible icon. only update if the death state is changed!
        BufferedImage buffImage; //original colored resized icon image!


        public IconViewerPanel(IconContainer icon){
            //update internal variable
            this.icon = icon;

            //set up components:
            iconLabel.setSize(146, 150);

            //retrieve image:
            buffImage = Main.resize(icon.getImage(), iconLabel.getWidth(), iconLabel.getHeight());

            //create preview icon
            updatePreviewIcon();

            textLabel.setFont(new Font("Default",0, 16 ));
            textLabel.setText("Preview: " + icon.getName());

            //setup panel parameters:
            setLayout(new BorderLayout());
            setOpaque(false);

            //deal with border
            Border border = BorderFactory.createLineBorder(Color.GRAY, 2, true);
            setBorder(border);

            //adding components
            add(textLabel, BorderLayout.NORTH);
            add(iconLabel, BorderLayout.CENTER);
        }

        public void updateInterface(){
            //should be called when update is required!
            //check if update is required:
            if(icon.isDead() == isDead)return; //status didnt change. no need to update

            //status has changed! update picture!
            updatePreviewIcon();
        }

        private void updatePreviewIcon(){
            if(icon.isDead() == true){
                //icon is dead! replace it with a grey one!
                drawGray();
            } else {
                iconLabel.setIcon(new ImageIcon(buffImage));
            }
            iconLabel.repaint();

            isDead = icon.isDead();
        }

        public void drawGray(){
            BufferedImage grayImage = new BufferedImage(iconLabel.getWidth(), iconLabel.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
            op.filter(buffImage, grayImage);
            iconLabel.setIcon(new ImageIcon(grayImage));
        }
    }

    private class HealthModifierPanel extends JPanel{
        /**
         *
         * provides the health modifier options such as edition current health and max health!
         *
         */

        //add components:
        JButton currentNext = new JButton(">"); //adds 1 to current health
        JButton maximumNext = new JButton(">"); //adds 1 to maximum health
        JButton currentPrev = new JButton("<"); //removes 1 to current health
        JButton maximumPrev = new JButton("<"); //removes 1 to maximum health
        JButton currentSpeedNext = new JButton("►"); //adds 10 to current health
        JButton maximumSpeedNext = new JButton("►"); //adds 10 to maximum health
        JButton currentSpeedPrev = new JButton("◄"); //removes 10 to current health
        JButton maximumSpeedPrev = new JButton("◄"); //removes 1 0to maximum health
        JLabel currentLabel = new JLabel("Curr HP:");
        JLabel maximumLabel = new JLabel("Max HP:");
        JNumberTextField currentText = new JNumberTextField();
        JNumberTextField maximumText = new JNumberTextField();

        //add internal variables:
        IconContainer iconContainer;

        public HealthModifierPanel(IconContainer icon){
            //set up internal variables:
            this.iconContainer = icon;

            //set component parameters:
            currentText.setFont(new Font("Default", 0, 14));
            maximumText.setFont(new Font("Default", 0, 14));
            currentText.setNumberValue(icon.getCurrentHealth());
            maximumText.setNumberValue(icon.getMaximumHealth());

            Font font = new Font("Default", 0, 14);

            maximumSpeedNext.setFont(font);
            maximumSpeedPrev.setFont(font);
            currentSpeedNext.setFont(font);
            currentSpeedPrev.setFont(font);


            //set panel parameters:
            setLayout(new GridLayout(2, 6));
            setOpaque(false);

            //deal with border
            Border border = BorderFactory.createLineBorder(Color.GRAY, 2, true);
            setBorder(border);

            //add components:
            //first row:
            add(currentLabel);
            add(currentSpeedPrev);
            add(currentPrev);
            add(currentText);
            add(currentSpeedNext);
            add(currentNext);

            //second row:
            add(maximumLabel);
            add(maximumSpeedPrev);
            add(maximumPrev);
            add(maximumText);
            add(maximumSpeedNext);
            add(maximumNext);

            //do button setup for mouse listeners!
            buttonSetup();

            //add focus listener to fields
            currentText.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    currentText.checkValue();
                    icon.setCurrentHealth(currentText.getNumberValue());
                }
            });

            maximumText.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    maximumText.checkValue();
                    icon.setMaximumHealth(maximumText.getNumberValue());
                }
            });
        }

        public void updateInterface(){
            //updates current health and max health!
            currentText.checkValue();
            maximumText.checkValue();
            currentText.setNumberValue(iconContainer.getCurrentHealth());
            maximumText.setNumberValue(iconContainer.getMaximumHealth());
            checkTextColor();
        }

        private void modifyCurrentHealth(int modifier){
            currentText.checkValue();
            iconContainer.setCurrentHealth(currentText.getNumberValue() + modifier);
        }

        private void modifyMaximumHealth(int modifier){
            maximumText.checkValue();
            iconContainer.setMaximumHealth(maximumText.getNumberValue() + modifier);
        }

        private void checkTextColor(){
            /**
             * if container is dead, update text color to red!
             */
            if(iconContainer.isDead()){
                currentText.setCorrectColor(Color.red);
                maximumText.setCorrectColor(Color.red);
            } else {
                currentText.setCorrectColor(Color.white);
                maximumText.setCorrectColor(Color.white);
            }

        }

        private void buttonSetup(){
            currentNext.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    modifyCurrentHealth(1);
                }
            });

            currentSpeedNext.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    modifyCurrentHealth(10);
                }
            });

            currentPrev.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    modifyCurrentHealth(-1);
                }
            });

            currentSpeedPrev.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    modifyCurrentHealth(-10);
                }
            });
            maximumNext.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    modifyMaximumHealth(1);
                }
            });

            maximumSpeedNext.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    modifyMaximumHealth(10);
                }
            });

            maximumPrev.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    modifyMaximumHealth(-1);
                }
            });

            maximumSpeedPrev.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    modifyMaximumHealth(-10);
                }
            });
        }
    }

    private class CheckBoxPanel extends JPanel{
        /**
         *
         * holds up a set of true or false parameters for the icon to keep a eye on!
         * some of this data should reset, and some of this data should be kept on floor change
         * data that needs reset should be stored in IconContainer
         * data that needs to be kept should be stored in IconRawContainer
         *
         * parameters:
         * Visible (default: false. reset: true) determines if the given icon is drawn to the screen.
         * Is Dead (default: false. reset: false)if true, displays a gray scaled image instead
         * Auto Dead (default: true. reset: false)if true, automatically sets Is Dead to true if health <= 0, and sets it false otherwise
         * Name Visible(default: false. reset: fase) if true, name is shown on screen
         *
         */

        JCheckBox visibleCheck = new JCheckBox("Visible", isIconVisible());
        JCheckBox deathCheck = new JCheckBox("Is dead", isDead());
        JCheckBox autoDeathCheck = new JCheckBox("Auto dead", isAutoDead());
        JCheckBox nameVisibleCheck = new JCheckBox("Name Visible", isNameVisible());


        IconContainer icon;

        public CheckBoxPanel(IconContainer icon){
            //set internal variables:
            this.icon = icon;

            //set component parameters:
            JPanel emptyPanel = new JPanel();
            emptyPanel.setVisible(false);

            //set panel parameters:
            setLayout(new GridLayout(6, 1));
            setOpaque(false);

            //add components:
            add(nameVisibleCheck);
            add(visibleCheck);
            add(emptyPanel);
            add(deathCheck);
            add(autoDeathCheck);

            //add mouse listeners:
            visibleCheck.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    icon.setVisible(visibleCheck.isSelected());
                    icon.updateIconMenu();
                }
            });

            deathCheck.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    icon.setDead(deathCheck.isSelected());
                    icon.updateIconMenu();
                }
            });

            autoDeathCheck.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    icon.setAutoDead(autoDeathCheck.isSelected());
                    icon.updateIconMenu();
                }
            });

            nameVisibleCheck.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    icon.setNameVisible(nameVisibleCheck.isSelected());
                    icon.updateIconMenu();
                }
            });

            //do a update check!
            updateInterface();
        }

        public void updateInterface(){
            //updates the is dead variable!
            deathCheck.setSelected(icon.isDead());
            deathCheck.setEnabled(!(autoDeathCheck.isSelected())); //sets death check only to enable if auto death check is false!
            //update visibility variable
            visibleCheck.setSelected(icon.isIconVisible());
            autoDeathCheck.setSelected(icon.isAutoDead());
            nameVisibleCheck.setSelected(icon.isNameVisible());

        }
    }

    private class SliderPanel extends JPanel{
        /**
         *
         * holds slider for local scale zooming!
         *
         */

        //set up components:
        JSlider localScaleSlider = new JSlider(20, 400, 100);
        JLabel scaleDisplay = new JLabel(" Local Scale: " + 100 + "%");

        //internal variables:
        IconContainer icon;

        public SliderPanel(IconContainer icon){
            //set internal variables:
            this.icon = icon;

            //set component parameters:

            //set panel parameters:
            setLayout(new GridLayout(2, 1));
            setOpaque(false);

            //deal with border
            Border border = BorderFactory.createLineBorder(Color.GRAY, 2, true);
            setBorder(border);

            //add components:
            add(scaleDisplay);
            add(localScaleSlider);

            //add mouse listener:
            localScaleSlider.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    icon.setObjectLocalScale(localScaleSlider.getValue());
                }
            });

            localScaleSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    scaleDisplay.setText(" Local Scale: " + localScaleSlider.getValue() + "%");
                }
            });

        }

        public void updateInterface(){
            localScaleSlider.setValue(icon.getObjectLocalScaleInt());
        }
    }

    private class CloseButton extends JLabel{
        /**
         *
         * this button will erase the icon container
         * should only exist on genericIcons
         *
         */
        IconContainer icon;
        Color deepColor = Main.cursorColor;
        Color normalColor = ControlInterface.backgroundColor;

        int clickAmount = 0;

        public CloseButton(IconContainer icon){
            //set internal variable:
            this.icon = icon;
            normalColor = getBackground();
            setOpaque(true);

            //set button parameters

            //deal with border
            Border border = BorderFactory.createLineBorder(Color.GRAY, 2, false);
            setBorder(border);

            //add mouse listener:
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if(clickAmount >= 1){
                        icon.getIconManager().eraseIconContainer(icon);
                    }
                    clickAmount++;
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(deepColor);
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(normalColor);
                    repaint();
                    clickAmount = 0;
                }
            });
        }

        int buttonHeight = 0;
        int buttonWidth = 0;

        @Override
        public void setBounds(int x, int y, int width, int height) {
            this.buttonHeight = height;
            this.buttonWidth = width;
            super.setBounds(x, y, width, height);
        }

        Stroke stroke = new BasicStroke(3);

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics2D = (Graphics2D) g;
            graphics2D.setStroke(stroke);
            graphics2D.setColor(Color.RED);
            graphics2D.drawLine(0,0, buttonWidth, buttonHeight);
            graphics2D.drawLine(0,buttonWidth, buttonHeight, 0);
        }
    }
}

