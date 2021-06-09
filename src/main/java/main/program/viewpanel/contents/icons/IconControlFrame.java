package main.program.viewpanel.contents.icons;

import jdk.nashorn.internal.objects.Global;
import main.Main;
import main.program.JFrameAutomaticWindow;
import main.program.JNumberTextField;
import main.program.controlpanel.contents.ControlInterface;
import main.program.properties.PropertyStorage;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class IconControlFrame extends JFrameAutomaticWindow {
    /**
     *
     * Control frame for icons! holds control interface aswell as a panel for icons to show their interfaces!
     * holds a update command to reload the list of icon interfaces!
     *
     */

    IconManager iconManager;
    IconViewerPanel iconPanel = new IconViewerPanel();
    InterfacePanel interfacePanel;

    public IconControlFrame(IconManager iconManager){
        //setting frame parameters
        initialiseAutomaticWindow(PropertyStorage.WindowType.ICONCONTROL);
        setMinimumSize(new Dimension(980, 730));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setTitle(Main.programName + ": Counter Port");

        //handle internal variables:
        this.iconManager = iconManager;

        //create components:
        interfacePanel = new InterfacePanel(iconManager);
        JScrollPane scrollPane = new JScrollPane(iconPanel);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);

        //add components:
        add(scrollPane, BorderLayout.CENTER);
        add(interfacePanel, BorderLayout.NORTH);


        //reveal window
        setVisible(true);
    }

    /**
     * update call for when the icon panels must be refreshed, such as adding a new icon or doing a floor change!
     *
     */

    public void updateCall(){
        List<JComponent> componentList = generateComponentList(iconManager.getIconList());
        iconPanel.updateComponents(componentList);
    }

    /**
     *
     * generates a component list from a list of icon containers, retrieving their menus in a easy to use format
     */

    private List<JComponent> generateComponentList(List<IconContainer> iconList){
        List<JComponent> componentList = new ArrayList<>(); //generate a new component list

        //loop though icon list and retrieve menus
        for(int i = 0; i < iconList.size(); i++){
            IconContainer icon = iconList.get(i);
            componentList.add(icon.getInterface());
            icon.updateIconMenu();
        }

        return componentList;
    }

    class IconViewerPanel extends JPanel{
        /**
         *
         * takes iconmanager's current library and displays each icon's menu to the user!
         * should use scrollbar too!
         *
         */

        public IconViewerPanel(){
            //set panel parameters:
            FlowLayout layout = new FlowLayout(FlowLayout.LEADING);
            layout.setHgap(15);
            setLayout(layout);
        }

        public void updateComponents(List<JComponent> componentList){
            //first, clear all old components from list!
            removeAll();
            revalidate();
            repaint();

            //add new components from list to panel
            for(int i = 0; i < componentList.size(); i++){
                JComponent component = componentList.get(i); //retrieves component from list
                add(component);
            }
        }
    }

    class InterfacePanel extends JPanel{
        /**
         *
         * holds the global controls of all icons, aswell as the ability to create temporary icons!
         *
         */
        //add components
        GlobalSliderPanel sliderPanel;
        GenericIconGenerator iconGenerator;
        InitiativeControlPanel initiativeControlPanel;

        IconManager iconManager;
        public InterfacePanel(IconManager iconManager) {
            //set up internal variables
            this.iconManager = iconManager;

            //generate components:
            sliderPanel = new GlobalSliderPanel(iconManager);
            iconGenerator = new GenericIconGenerator(iconManager);
            initiativeControlPanel = new InitiativeControlPanel(iconManager);

            //set up panel parameters:
            setLayout(new FlowLayout());
            setPreferredSize(new Dimension(200, 200));

            //add new components:
            add(initiativeControlPanel);
            add(sliderPanel);
            add(iconGenerator);
        }
    }

    class GlobalSliderPanel extends JPanel{
        /**
         * holds global slider for all icons!
         *
         */

        //set up components:
        JSlider slider = new JSlider(5,200,30);
        JLabel textField = new JLabel("Global Scale: " + 30 + "%");

        IconManager iconManager;
        IconsViewPanel iconPanel;

        public GlobalSliderPanel(IconManager iconManager){
            //set up internal variables:
            this.iconManager = iconManager;

            //set up components parameters:
            slider.setEnabled(false);

            //set up panel parameters
            setLayout(new GridLayout(2, 1));
            setPreferredSize(new Dimension(200, 50));
            setBackground(ControlInterface.backgroundColor);

            //deal with border
            Border border = BorderFactory.createLineBorder(Color.GRAY, 2, true);
            setBorder(border);

            //adding components:
            add(textField);
            add(slider);

            //add listeners:
            slider.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    iconPanel.setGlobalScale((double) slider.getValue() / 100);
                }
            });

            slider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    textField.setText("Global Scale: " + slider.getValue() + "%");
                    iconPanel.setGlobalScale((double) slider.getValue() / 100);
                }
            });
        }

        public void connectIconPanel(IconsViewPanel iconPanel){
            //CONNECTS ICON PANEL! has to be done before slider can be unlocked
            this.iconPanel = iconPanel;
            slider.setEnabled(true);
        }
    }

    class GenericIconGenerator extends JPanel{
        /**
         *
         * allows the creation of generic icons that exist per floor!\
         * generic icons should now be saved to disk, use only generic icons, and
         *
         */

        IconManager iconManager;

        //set up components:
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();

        JLabel currentLabel = new JLabel("Current Health:");
        JNumberTextField currentField = new JNumberTextField(0);
        JLabel maximumLabel = new JLabel("Maximum Health:");
        JNumberTextField maximumField = new JNumberTextField(0);

        JButton resetButton = new JButton("reset");
        JButton createButton = new JButton("create");

        JLabel factionLabel = new JLabel("This counter is a...");
        JRadioButton allyButton = new JRadioButton("Ally");
        JRadioButton neutralButton = new JRadioButton("Neutral");
        JRadioButton enemyButton = new JRadioButton("Enemy");



        public GenericIconGenerator(IconManager iconManager){
            //set internal variables:
            this.iconManager = iconManager;

            //set panel parameters:
            setLayout(null);
            setPreferredSize(new Dimension(300, 190));
            setBackground(ControlInterface.backgroundColor);

            //set component parameters:
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add(allyButton);
            buttonGroup.add(neutralButton);
            buttonGroup.add(enemyButton);
            enemyButton.setSelected(true);

            //deal with border
            Border border = BorderFactory.createLineBorder(Color.GRAY, 2, true);
            setBorder(border);

            //add components:
            setupComponents();

            //add listeners:
            createButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    createGenericIcon();
                }
            });

            resetButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    resetMenu();
                }
            });
        }

        private void createGenericIcon(){
            /**
             * takes all available info and uses it to create a new generic icon in icon manager
             */
            currentField.checkValue();
            maximumField.checkValue();

            int currentHealth = currentField.getNumberValue();
            int maximumHealth = maximumField.getNumberValue();
            String name = nameField.getText();
            BufferedImage iconImage = null;

            //select icon image:
            if(allyButton.isSelected()){
                iconImage = IconManager.genericAlly;
            } else if(neutralButton.isSelected())  {
                iconImage = IconManager.genericNeutral;
            } else {
                iconImage = IconManager.genericEnemy;
            }

            iconManager.generateIconContainer(iconImage, name, currentHealth, maximumHealth);
        }

        private void resetMenu(){
            nameField.setText("");
            currentField.setNumberValue(0);
            maximumField.setNumberValue(0);
            enemyButton.setSelected(true);
        }

        private void setupComponents(){
            int yOffset = 2;
            nameLabel.setBounds(15, yOffset, 300, 20);
            add(nameLabel);
            yOffset = yOffset + 18;
            nameField.setBounds(10, yOffset, 150, 30);
            add(nameField);

            yOffset = yOffset + 28;

            currentLabel.setBounds(15, yOffset, 300, 20);
            add(currentLabel);
            yOffset = yOffset + 18;
            currentField.setBounds(10, yOffset, 150, 30);
            add(currentField);

            yOffset = yOffset + 28;

            maximumLabel.setBounds(15, yOffset, 300, 20);
            add(maximumLabel);
            yOffset = yOffset + 18;
            maximumField.setBounds(10, yOffset, 150, 30);
            add(maximumField);

            createButton.setBounds(10, 150, 70, 30);
            add(createButton);
            resetButton.setBounds(85, 150, 70, 30);
            add(resetButton);

            factionLabel.setBounds(180, 2, 100, 20);
            add(factionLabel);
            allyButton.setBounds(175, 25, 100, 20);
            add(allyButton);
            neutralButton.setBounds(175, 50, 100, 20);
            add(neutralButton);
            enemyButton.setBounds(175, 75, 100, 20);
            add(enemyButton);

        }

        final float dash1[] = {10.0f};
        final BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.gray);
            g2.setStroke(dashed);
            g2.drawLine(169,0, 169, 200);
        }
    }

    class InitiativeControlPanel extends JPanel{
        /**
         *
         * contains controls for initiative calculations and display
         *
         */
        //set up components:
        IconNameListPanel iconPanel;



        IconManager iconManager; //parent icon manager to get and store data to!

        public InitiativeControlPanel(IconManager iconManager){
            //set up internal variables
            this.iconManager = iconManager;

            //create components:
            this.iconPanel = new IconNameListPanel(iconManager);

            //set panel parameters:
            setLayout(null);
            setPreferredSize(new Dimension(400, 190));
            setBackground(ControlInterface.backgroundColor);

            //create scroll panel:
            JScrollPane scrollPane = new JScrollPane(iconPanel);
            scrollPane.getVerticalScrollBar().setUnitIncrement(10);
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

            //add components:
            scrollPane.setBounds(200, 0, 200, 190);
            add(scrollPane);
        }

        class IconNameListPanel extends JPanel{
            /**
             *
             * displays all the icon names in order of their intiative value
             * TODO: add initiative calculations
             *
             */

            IconManager iconManager;

            public IconNameListPanel(IconManager iconManager){
                //set up internal variables
                this.iconManager = iconManager;

                //set panel parameters:
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                //setBackground(ControlInterface.backgroundColor);

                //deal with border
                Border border = BorderFactory.createLineBorder(Color.GRAY, 2, true);
                setBorder(border);

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        generateDisplayList();
                    }
                });
            }

            public void generateDisplayList(){
                /**
                 * generates display list to panel
                 */
                removeAll(); //removes all objects
                List<IconContainer> iconList = iconManager.getIconList();
                for(int i = 0; i < iconList.size(); i++){
                    //loops though all icons to retrieve their display name
                    IconContainer icon = iconList.get(i);
                    add(new JButton(icon.getName()));
                }
            }
        }
    }
}


