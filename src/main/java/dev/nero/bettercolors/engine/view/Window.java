/*
 * Copyright 2018-2020
 * - Bettercolors Contributors (https://github.com/N3ROO/Bettercolors) and
 * - Bettercolors Engine Contributors (https://github.com/N3ROO/BettercolorsEngine)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.nero.bettercolors.engine.view;

import dev.nero.bettercolors.engine.BettercolorsEngine;
import dev.nero.bettercolors.engine.io.Filer;
import dev.nero.bettercolors.engine.io.PropertiesFiler;
import dev.nero.bettercolors.engine.io.SettingsUtils;
import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.engine.option.ToggleOption;
import dev.nero.bettercolors.engine.option.ValueFloatOption;
import dev.nero.bettercolors.engine.option.ValueOption;
import dev.nero.bettercolors.engine.utils.Friends;
import dev.nero.bettercolors.engine.utils.KeyName;
import dev.nero.bettercolors.engine.utils.Keymap;
import dev.nero.bettercolors.engine.version.Version;
import dev.nero.bettercolors.engine.version.VersionException;
import dev.nero.bettercolors.engine.Reference;
import mdlaf.MaterialLookAndFeel;
import mdlaf.themes.JMarsDarkTheme;
import mdlaf.themes.MaterialLiteTheme;
import mdlaf.themes.MaterialOceanicTheme;
import mdlaf.themes.MaterialTheme;
import org.lwjgl.glfw.GLFW;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.*;
import java.util.*;

public class Window extends JFrame{

    // Used to access the window from outside the class. It is used to add text in the console
    private static Window instance;

    // Preferred size for the GUI
    private final int WIDTH = 500;
    private final int HEIGHT = 700;

    // Used to display / change the key used to toggle the GUI
    public final static String TOGGLE_KEY_OPTION = "toggle_key";
    public static int TOGGLE_KEY;

    // Modules that will be displayed in the GUI
    private final ArrayList<Module> MODULES;
    // These arrays will by the modules to modify their settings. It is important to have them as attributes because
    // we will need to synchronize them at some point
    private final ArrayList<JCheckBox> CHECKBOXES_ACTIVATION;
    private final ArrayList<JCheckBox> CHECKBOXES_MODULES;
    private final ArrayList<JButton> KEYBIND_BUTTONS;
    private final Map<JLabel, JSlider> SLIDERS_MODULES;

    // Used by the console
    private Font consoleFont;
    private int textCounter = 0;
    private JScrollPane scroll;
    private static Queue<Message> waitingMessages = new LinkedList<>();

    // Gui components that will be used by other components
    private JTextPane console;
    private JList<String> friendList;

    // Theme-related attributes
    public static LookAndFeel defaultLookAndFeel;
    public final static String THEME_DEFAULT = "default";
    public final static String THEME_MATERIAL_LIGHT = "light";
    public final static String THEME_MATERIAL_OCEANIC = "oceanic";
    public final static String THEME_MATERIAL_GOLD = "gold";
    public static String selectedTheme = THEME_DEFAULT;
    public final static String THEME_OPTION = "theme";

    // Function used to get the key name
    private KeyName keyNameFunc;

    /**
     * Creates the GUI
     * @param title window title
     * @param modules modules that will be shown in the GUI
     * @param version current version of the mod
     * @param keyNameFunc a function that takes a key code and returns its string representation
     */
    public Window(String title, ArrayList<Module> modules, Version version, KeyName keyNameFunc) {
        // It calls the JFrame constructor
        super(title);

        this.keyNameFunc = keyNameFunc;

        // The window will try to match these dimensions. If there is blank, it will wrap it to match the given
        // dimensions. Otherwise it will be bigger than that.
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        // Used to show the GUI in the middle of the screen, and not at the top left of the screen
        setBounds(
                (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-WIDTH/2,
                (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-HEIGHT/2,
                WIDTH, HEIGHT
        );

        // Icon of the GUI
        try {
            // Thread.currentThread works with the current Forge build
            setIconImage(new ImageIcon(Objects.requireNonNull(
                    Thread.currentThread()
                            .getContextClassLoader()
                            .getResource("images/bettercolors_symbol.png")))
                    .getImage()
            );
        } catch (Exception e) {
            e.printStackTrace();
            WARN("Failed to load images/bettercolors_symbol.png");
        }

        // It is possible to resize the GUI
        setResizable(true);

        // The GUI is turned off by default
        setVisible(false);

        // Used to access the class from outside (to add some text in the console for example)
        Window.instance = this;

        // Everything related to the modules
        this.MODULES = modules;
        this.CHECKBOXES_ACTIVATION = new ArrayList<>();
        this.CHECKBOXES_MODULES = new ArrayList<>();
        this.KEYBIND_BUTTONS = new ArrayList<>();
        this.SLIDERS_MODULES = new HashMap<>();

        // We want to use a custom font for the console. We will initialize it here
        // Here is the default font if we can't load the one that we want
        consoleFont = new Font("Lucida Console", Font.PLAIN, 14);
        try {
            consoleFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    getClass().getResource("/fonts/CascadiaCode.ttf").openStream()
            );
            GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
            gEnv.registerFont(consoleFont);
            consoleFont = consoleFont.deriveFont(14f);
        } catch (Exception e) {
            WARN("Could not load the CascadiaCode font");
            e.printStackTrace();
        }

        // Now, we will set up the window. The window is built this way:
        // - menuBar
        // - header
        // - modules (activation + settings)
        // - console
        // - footer

        // MenuBar
        this.setupMenuBar(version);

        // Header (ready if needed)
        // JPanel headerLayour = new JPanel();
        // headerLayour.setLayout(new BorderLayout());
        // setupHeader(headerLayour);

        // Modules & console
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        setupModulesActivationStatus(mainPanel); // Creates a panel with the activation status of each module (on / off)
        setupModulesOptions(mainPanel); // Creates a panel with all the settings for each modules + settings manager
        setupConsole(mainPanel); // Creates the console

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BorderLayout());
        setupFooter(footerPanel); // Creates the footer with the version status

        // Now we can add the layouts to the window
        // getContentPane().add(headerLayour, "North");
        getContentPane().add(mainPanel, "Center");
        getContentPane().add(footerPanel, "South");

        // Adjust the components properly
        pack();

        // Update the GUI
        repaint();
    }

    /**
     * Used to change the theme of the GUI.
     * @param theme the theme wanted (to apply it to the window)
     * @param themeId the theme id (to save it in the settings file)
     */
    private void changeTheme(MaterialTheme theme, String themeId) {
        try {
            // null means not material theme, so it means the default theme
            if (theme != null) {
                // We need to change the lookAndFeel if the current theme is not the material one
                if (!(UIManager.getLookAndFeel() instanceof MaterialLookAndFeel)) {
                    UIManager.setLookAndFeel(new MaterialLookAndFeel());
                }
                // Apply the theme
                MaterialLookAndFeel.changeTheme(theme);
            } else {
                UIManager.setLookAndFeel(defaultLookAndFeel);
            }

            // We want to change the console background according to the theme
            if (Window.selectedTheme.equalsIgnoreCase(THEME_MATERIAL_GOLD)) {
                this.console.setBackground(Color.DARK_GRAY);
            } else {
                this.console.setBackground(new Color(0,30,50));
            }

            // Force GUI to update
            SwingUtilities.updateComponentTreeUI(this);

            // Change the selected GUI
            Window.selectedTheme = themeId;

            // Save the theme to the settings file
            SettingsUtils.setOption(Window.THEME_OPTION, themeId);

            // Alert the user that the theme will be fully applied once that the game is restarted
            JOptionPane.showMessageDialog(this, "You should restart your game to apply the new theme completely");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * It creates the menuBar containing:
     * - Theme selector
     * - Bug report button
     * It also verify the version and shows the status
     */
    private void setupMenuBar(Version version) {
        JMenuBar menuBar = new JMenuBar();
        // Create a menu called Themes (it will be added to the menuBar)
        JMenu themes = new JMenu("Themes");

        // Items of that menu
        JMenuItem themeDefault = new JMenuItem("Default");
        JMenuItem themeLight = new JMenuItem("Material Light");
        JMenuItem themeDark = new JMenuItem("Material Oceanic");
        JMenuItem themeDark2 = new JMenuItem("Material Gold");

        // Define what each item does when the user clicks on it
        themeDefault.addActionListener(
                (event) -> this.changeTheme(null, Window.THEME_DEFAULT)
        );

        themeLight.addActionListener(
                (event) -> this.changeTheme(new MaterialLiteTheme(), Window.THEME_MATERIAL_LIGHT)
        );

        themeDark.addActionListener(
                (event) -> this.changeTheme(new MaterialOceanicTheme(), Window.THEME_MATERIAL_OCEANIC)
        );

        themeDark2.addActionListener(
                (event) -> {
                    this.changeTheme(new JMarsDarkTheme(), Window.THEME_MATERIAL_GOLD);
                    this.console.setBackground(Color.DARK_GRAY);
                }
        );

        // Now we can add them to the menu
        themes.add(themeDefault);
        themes.add(themeLight);
        themes.add(themeDark);
        themes.add(themeDark2);

        // Finally we can add the menu to the menuBar
        menuBar.add(themes);

        // We want an other menu for the user to report a bug
        JMenu reportMenu = new JMenu("Found a bug?");
        JMenuItem report = new JMenuItem("Report it");
        report.addActionListener(
                (event) -> {
                    try {
                        Desktop.getDesktop().browse(new URI(Reference.ISSUES_TRACKER_URL));
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
        );
        reportMenu.add(report);
        menuBar.add(reportMenu);

        // Last thing that we need is to show if the current version is the last one
        JLabel update = new JLabel();

        try {
            Version latest = Version.getLatestVersion(Reference.MC_VERSION);
            Version.VersionDiff diff = version.compareWith(latest);
            switch (diff) {
                case DEVELOPMENT:
                    update.setForeground(new Color(150, 70, 0));
                    update.setText("Development build");
                    WARN("You are using a development build");
                    break;
                case UPDATED:
                    update.setText("No update available");
                    INFO("[+] You are using the last version");
                    break;
                case OUTDATED:
                    update.setForeground(new Color(0, 70, 100));
                    update.setText("Update available");

                    // Add a link to the label
                    Font font = update.getFont();
                    Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
                    attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                    update.setFont(font.deriveFont(attributes));
                    update.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    update.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            super.mouseClicked(e);
                            try {
                                Desktop.getDesktop().browse(new URI(Reference.RELEASES_DOWNLOAD_URL));
                            } catch (URISyntaxException | IOException ex) {
                                ERROR("Error while trying to go to the download page");
                                ERROR("Here is the download page: " + Reference.RELEASES_DOWNLOAD_URL);
                            }
                        }
                    });

                    // Show the changelog
                    WARN("Update available! Changelog:");
                    String[] lines = latest.getChangelog().split("\\\\n");
                    for(String line : lines) {
                        String[] split = line.split("\\*\\*");
                        if(split.length % 2 == 1) {
                            for(int i = 0; i < split.length; i ++) {
                                if(i % 2 == 0) {
                                    addText(split[i], false);
                                } else {
                                    addText(split[i], Color.RED, false);
                                }
                            }
                            addText("", true);
                        } else {
                            addText(line, true);
                        }
                    }

                    break;
            }
        } catch (VersionException e) {
            switch (e.getCode()) {
                case NO_INTERNET:
                    update.setForeground(new Color(100, 0, 0));
                    update.setText("Could not check the version");
                    ERROR("Could not check the version");
                    ERROR("If you are not connected to internet, it's normal");
                    break;
                case URL_ISSUE:
                    update.setForeground(new Color(100, 0, 0));
                    update.setText("URL issue");
                    ERROR("Could not read the URL to check for the version");
                    ERROR("It should not happen, you can open an issue to github");
                    break;
                case NO_VERSION:
                    update.setForeground(new Color(100, 0, 0));
                    update.setText("No version found");
                    ERROR("No version found, was bettercolors released for MC " + Reference.MC_VERSION + "?");
                    ERROR("If yes, then the API may have changed, you can open an issue to github");
                    break;
            }
        }
        menuBar.add(update);

        // We can add the menuBar to the window
        this.setJMenuBar(menuBar);
    }

    /*
    private void setupHeader(JPanel headerLayour){
        // READY TO GET IMPLEMENTED IF NEEDED
    }
    */

    /**
     * It sets up the activation panel that shows checkboxes for each module
     * @param modulesPanel the panel where that panel needs to be rendered
     */
    private void setupModulesActivationStatus(JPanel modulesPanel){
        // It creates a grid where we will put the modules' activation status. Each line has two columns.
        JPanel activationPanel = new JPanel();
        activationPanel.setLayout(new GridLayout((int) Math.ceil((double) MODULES.size() / 2d), 2));
        activationPanel.setBorder(new TitledBorder(new EtchedBorder(), "Modules"));

        for(Module module : MODULES) {
            // We create checkboxes for each module showing if it's turned on or not
            final JCheckBox checkBox = new JCheckBox(module.getClass().getSimpleName());

            // Init it according to the module's activation: turned on or off?
            checkBox.setSelected(module.isActivated());

            // When clicked, we need to toggle the module and save its status to the settings file
            checkBox.addActionListener(e -> {
                // Toggle the module
                module.toggle(false);
                // Save the status
                SettingsUtils.setOption(module.getClass().getSimpleName(), Boolean.toString(module.isActivated()));
                // Makes sure that there is no desynchronization
                checkBox.setSelected(module.isActivated());
                // Update the GUI because we used setSelected on a checkBox
                repaint();
            });

            // We add that checkbox to our array so that we will be able to synchronize everything later on in the code.
            // Hint: we will synchronize everything when the user will load a settings file.
            CHECKBOXES_ACTIVATION.add(checkBox);

            // Put checkboxes on the grid
            activationPanel.add(checkBox);
        }

        modulesPanel.add(activationPanel, "North");
    }

    /**
     * It will **automatically** create a tab for each module with its settings. It is complex because it needs to be
     * working with any module, and not one specifically. Also, we need to make sure that we will be able to
     * synchronize all the inputs if the user loads a settings file.
     * @param modulesPanel the panel where that panel needs to be rendered
     */
    private void setupModulesOptions(JPanel modulesPanel){
        // As stated in the method's header, we will create a tab for each module. To do so, we will use a JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();

        for(Module module : MODULES){
            // If the module does not have any settings to customize, then it does not need a tab
            if(module.getOptions().size() == 0) continue;

            // This panel is the one that will contain all the settings. We will put in the tab later on
            JPanel optionsPanel = new JPanel();
            optionsPanel.setLayout(new BorderLayout());
            optionsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            // This panel will only contain optionsPanel, but with a scrollbar to prevent having a very big GUI if there
            // are a lot of settings for a specific module
            JPanel content = new JPanel(new BorderLayout());

            // We retrieve all the toggle options of the module in a specific array. A toggle option is an option that
            // is true or false (so we need checkboxes to render it)
            ArrayList<ToggleOption> toggleOptions = Option.getToggleOptions(module.getOptions());
            // Yes, there is a warning, because at the moment, all the modules have toggle options, so it is always true
            if(toggleOptions.size() > 0) {
                // We will render checkboxes in a grid with a maximum of 2 columns per lines
                JPanel checkboxesPanel = new JPanel();
                checkboxesPanel.setLayout(new GridLayout((int) Math.ceil((double)toggleOptions.size() / 2d), 2));

                // For each option, we will add a checkbox, add a listener to know what code will be executed when the
                // button will be clicked, and we will add each checkbox in the CHECKBOXES_MODULES array (used to
                // synchronize checkboxes if the user loads a new settings file)
                for(ToggleOption toggleOption : toggleOptions){
                    final JCheckBox checkBox = new JCheckBox(toggleOption.getName());
                    checkBox.setSelected(toggleOption.isActivated());
                    checkBox.addActionListener(e -> {
                        // Update the module's option
                        toggleOption.toggle();
                        // Update the checkbox
                        checkBox.setSelected(toggleOption.isActivated());
                        // Refresh the GUI
                        repaint();
                    });
                    CHECKBOXES_MODULES.add(checkBox);
                    checkboxesPanel.add(checkBox);
                }

                optionsPanel.add(checkboxesPanel, "North");
            }

            // We retrieve all the value options of the module in a specific array. A value option is an option that
            // is a value (so we need sliders to render it)
            ArrayList<Option> valueOptions = Option.getValueOptions(module.getOptions());
            // Yes, there is a warning, because at the moment, all the modules have value options, so it is always true
            if(valueOptions.size() > 0){
                // We will create a slider grid that contains one slider per line. The first column contains the text
                // while the second one contains the actual slider
                JPanel slidersPanel = new JPanel();
                slidersPanel.setLayout(new GridLayout(valueOptions.size(), 2));

                for(Option valueOption : valueOptions){
                    boolean decimal;
                    float value;
                    float min;
                    float max;

                    if (valueOption instanceof ValueFloatOption) {
                        decimal = true;
                        value = ((ValueFloatOption) valueOption).getVal();
                        min = ((ValueFloatOption) valueOption).getMin();
                        max = ((ValueFloatOption) valueOption).getMax();
                    } else {
                        decimal = false;
                        value = ((ValueOption) valueOption).getVal();
                        min = ((ValueOption) valueOption).getMin();
                        max = ((ValueOption) valueOption).getMax();
                    }

                    // The label contains the value next to the its name
                    final JLabel label = new JLabel(valueOption.getName() + " [" + value + "]");
                    final JSlider slider = new JSlider();

                    // Slider value settings
                    // if decimal, multiply by 100 bc the sliders only support int. Then Divide by 100 to get the
                    // decimal value.
                    slider.setMinimum(decimal ? (int) (min * 100.0f) : (int) min);
                    slider.setMaximum(decimal ? (int) (max * 100.0f) : (int) max);
                    slider.setValue(decimal ? (int) (value * 100.0f) : (int) value);

                    // Max/min size
                    slider.setMaximumSize(new Dimension(100, 10));
                    slider.setMinimumSize(new Dimension(100, 5));

                    // The theme does not feel right with the material gold, so we update it manually here
                    if (Window.selectedTheme.equalsIgnoreCase(Window.THEME_MATERIAL_GOLD)) {
                        slider.setForeground(Color.ORANGE);
                    }

                    // What happens when the user uses the slider
                    slider.addChangeListener(e -> {
                        float newValue;
                        // Change the module's option
                        if (decimal) {
                            newValue = (float) slider.getValue() / 100.0f;
                            ((ValueFloatOption) valueOption).setVal(newValue);
                        } else {
                            newValue = slider.getValue();
                            ((ValueOption) valueOption).setVal((int) newValue);
                        }

                        // Update the label with the slider's new value
                        label.setText(valueOption.getName() + " [" + newValue + "]");
                        // Update the GUI
                        repaint();
                    });

                    SLIDERS_MODULES.put(label, slider);
                    slidersPanel.add(label);
                    slidersPanel.add(slider);
                }

                optionsPanel.add(slidersPanel, "Center");
            }

            // We add the options panel to the content panel that will contain a vertical
            content.add(optionsPanel);
            JScrollPane scrollPane = new JScrollPane(optionsPanel,
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            content.add(scrollPane);

            // For each tab, we will use the module's icon
            try {
                ImageIcon icon = new ImageIcon(
                        Objects.requireNonNull(
                                Thread.currentThread().
                                        getContextClassLoader().
                                        getResource("images/" + module.getSymbol())
                        )
                );
                tabbedPane.addTab(module.getName(), icon, content);
            } catch (Exception e) {
                e.printStackTrace();
                WARN("Failed to load images/" + module.getSymbol());
                tabbedPane.addTab(module.getName(), content);
            }
        }

        // This tab will contain all the buttons to change the modules' toggle key
        createTogglePanel(tabbedPane);

        // Now that we have one tab for each module, we want to create a custom tab for settings management
        createSettingsPanel(tabbedPane);

        // We're almost done, we need the friend list now
        createFriendList(tabbedPane);

        modulesPanel.add(tabbedPane, "Center");
    }

    private void createTogglePanel(JTabbedPane tabbedPane) {
        JPanel togglePanel = new JPanel();
        togglePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        togglePanel.setLayout(new FlowLayout());

        // Used to change the key to toggle the GUI
        JButton keybindGui = new JButton("Gui toggle key: " + this.keyNameFunc.getKeyName(Window.TOGGLE_KEY));
        keybindGui.addActionListener(e -> {
            // It creates a popup window
            JDialog dialog = new JDialog(Window.instance, "Message");

            // Content of that popup window (html)
            JLabel msg = new JLabel(
                    "<html>Press a key...<br>" +
                            "Please note that due to the difference between<br>" +
                            "VK and GLFW key events, ALT, CTRL and SHIFT<br>" +
                            "keys do not take into account left / right. Only<br>" +
                            "the right key is working. So if you choose<br>" +
                            "the left key, it will register the right one.</html>");

            // Popup layout
            msg.setHorizontalAlignment(JLabel.CENTER);
            dialog.getRootPane().setBorder(new EmptyBorder(10, 10, 10, 10));
            dialog.setLayout(new BorderLayout(0, 15));
            dialog.add(msg, "North");
            dialog.pack();
            dialog.setLocationRelativeTo(Window.instance);

            // The thing that will retrieve the key pressed by the user
            dialog.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {}

                @Override
                public void keyPressed(KeyEvent e) {
                    if(dialog.isVisible()) {
                        int code = Keymap.map(e.getKeyCode(), Reference.MC_INPUTS_VERSION == BettercolorsEngine.MC_INPUTS.NEW);

                        if (code == -2) {
                            JOptionPane.showMessageDialog(Window.instance, "This key is not supported, please use an other one");
                        } else {
                            // Change the key code used
                            Window.TOGGLE_KEY = code;
                            // Save the new key in the settings file
                            SettingsUtils.setOption(Window.TOGGLE_KEY_OPTION, Integer.toString(code));
                            // Change the label to show the new key used
                            keybindGui.setText("Gui toggle key: " + keyNameFunc.getKeyName(code));
                        }
                    }

                    // Hide the GUI (it won't trigger event, no worries, it is as if it was turned off)
                    dialog.setVisible(false);
                }

                @Override
                public void keyReleased(KeyEvent e) {}
            });

            // Don't forget to show the popup, otherwise it will be hidden
            dialog.setVisible(true);
        });
        togglePanel.add(keybindGui);

        for (Module module : MODULES) {
            JButton keybindButton = createKeybindButton(module);
            togglePanel.add(keybindButton);
            this.KEYBIND_BUTTONS.add(keybindButton);
        }

        this.KEYBIND_BUTTONS.add(keybindGui);

        // Add a specific icon to the keybind management tab
        try {
            ImageIcon icon = new ImageIcon(
                    Objects.requireNonNull(
                            Thread.currentThread()
                                    .getContextClassLoader()
                                    .getResource("images/key.png")
                    )
            );
            tabbedPane.addTab("Keybinds", icon, togglePanel);
        } catch (Exception e) {
            e.printStackTrace();
            WARN("Failed to load images/key.png");
            tabbedPane.addTab("Keybinds", togglePanel);
        }
    }

    private void createSettingsPanel(JTabbedPane tabbedPane){
        JPanel settingsPanel = new JPanel();
        settingsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        settingsPanel.setLayout(new BorderLayout(0, 15));

        // Now that we have the button to change the key to toggle the GUI, we want to add a new panel to manage the
        // settings file
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new BorderLayout());

        // The label that will show which settings file is currently selected. Note that the bettercolors settings file
        // have the bc_ prefix. So we only want to show the files with the bc_ prefix.
        final String selectedFilePrefix = "Selected config : ";
        JLabel selectedFile = new JLabel(
                // Remoe the bc_ prefix for a user-friendly interface lol
                selectedFilePrefix + SettingsUtils.SETTINGS_FILENAME.replaceFirst("bc_", "")
        );
        configPanel.add(selectedFile, "North");

        // Show all the available settings file in a list
        DefaultListModel<String> filenames = SettingsUtils.getAllSettingsFilenames();
        JList<String> list = new JList<>(filenames);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(filenames.indexOf(SettingsUtils.SETTINGS_FILENAME));
        configPanel.add(new JScrollPane(list), "Center");

        // Now we want the buttons to load, open, refresh and reset (to default)
        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());

        // Load button: loads the selected settings file
        JButton select_button = new JButton("Load");
        select_button.addActionListener(e -> {
            // Get selected filename and update it in our SettingsUtils class
            SettingsUtils.SETTINGS_FILENAME = list.getSelectedValue();
            // Show that the one selected is now this one
            selectedFile.setText(
                    selectedFilePrefix + SettingsUtils.SETTINGS_FILENAME.replaceFirst("bc_", "")
            );

            // Update the selected settings file
            Map<String, String> option = new HashMap<>();
            option.put("settings_file", SettingsUtils.SETTINGS_FILENAME);
            PropertiesFiler propertiesFiler = new PropertiesFiler(SettingsUtils.FILE_WITH_CURRENT_SETTINGS_USED);
            propertiesFiler.write(option, false);

            // Load the settings (the code is designed for it to be very simple)
            this.loadSettings();
        });
        buttons.add(select_button);

        // Open button: opens the directory where are stored the settings in the OS' default explorer
        JButton openButton = new JButton("Open");
        openButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(Filer.getSettingsDirectory());
            } catch (IOException e1) {
                ERROR("Unable to open the settings directory !");
            }
        });
        buttons.add(openButton);

        // Refresh the settings file (if the user added one in the meantime)
        JButton refresh_button = new JButton("Refresh");
        refresh_button.addActionListener(e ->{
            DefaultListModel<String> newList = SettingsUtils.getAllSettingsFilenames();
            int length_diff = newList.size() - list.getModel().getSize();
            list.setModel(newList);
            list.setSelectedIndex(SettingsUtils.getAllSettingsFilenames().indexOf(SettingsUtils.SETTINGS_FILENAME));
            if(length_diff == 0){
                INFO("[+] No new files found");
            }else if(length_diff == 1){
                INFO("[+] Found " + length_diff + " new file");
            }else if(length_diff > 1){
                INFO("[+] Found " + length_diff + " new files");
            }
        });
        buttons.add(refresh_button);

        // Reset the current config file to default
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            INFO("[~] Resetting " + SettingsUtils.SETTINGS_FILENAME + "...");
            // get default options
            ArrayList<ArrayList<Option>> options = BettercolorsEngine.getInstance().getDefaultOptions();
            // save them (and override what's currently in the config file)
            SettingsUtils.setOptions(options, false);
            // load them
            this.loadSettings();
        });
        buttons.add(resetButton);

        configPanel.add(buttons, "South");

        // Add a specific icon to the settings management tab
        try {
            ImageIcon icon = new ImageIcon(
                    Objects.requireNonNull(
                            Thread.currentThread()
                                    .getContextClassLoader()
                                    .getResource("images/settings.png")
                    )
            );
            tabbedPane.addTab("Settings", icon, settingsPanel);
        } catch (Exception e) {
            e.printStackTrace();
            WARN("Failed to load images/settings.png");
            tabbedPane.addTab("Settings", settingsPanel);
        }

        settingsPanel.add(configPanel, "Center");
    }

    private void createFriendList(JTabbedPane tabbedPane){
        JPanel friendListPanel = new JPanel();
        friendListPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        friendListPanel.setLayout(new BorderLayout(0, 15));

        // Label, input, button to add a new friend & button to remove selected friend
        JPanel newFriendPanel = new JPanel(new BorderLayout());
        JLabel newFriendLabel = new JLabel("New friend: ");
        JTextField newFriendInput = new JTextField();
        JPanel buttonsPanel = new JPanel();
        JButton newFriendButton = new JButton("Add");
        JButton deleteFriendButton = new JButton("Remove" + (Friends.getFriends().size() > 0 ? " " + Friends.getFriends().get(0) : ""));
        JButton refreshButton = new JButton("Refresh");
        newFriendPanel.add(newFriendLabel, "West");
        newFriendPanel.add(newFriendInput, "Center");
        newFriendPanel.add(newFriendButton, "East");
        buttonsPanel.add(refreshButton, "West");
        buttonsPanel.add(deleteFriendButton, "Center");
        newFriendPanel.add(buttonsPanel, "South");
        friendListPanel.add(newFriendPanel, "North");

        // Friend list
        friendList = new JList(Friends.getFriends().toArray());
        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendList.setSelectedIndex(0);
        friendListPanel.add(new JScrollPane(friendList), "Center");
        friendList.addListSelectionListener(e -> deleteFriendButton.setText("Remove " + friendList.getSelectedValue()));

        // Listeners
        newFriendButton.addActionListener(e -> Friends.addFriend(newFriendInput.getText()));
        deleteFriendButton.addActionListener(e -> Friends.removeFriend(friendList.getSelectedValue()));
        // also see Window#updateFriends()
        refreshButton.addActionListener(e -> Friends.loadFriends());

        // Icon & add it to the tabs
        try {
            ImageIcon icon = new ImageIcon(
                    Objects.requireNonNull(
                            Thread.currentThread()
                                    .getContextClassLoader()
                                    .getResource("images/friends.png")
                    )
            );
            tabbedPane.addTab("Friends", icon, friendListPanel);
        } catch (Exception e) {
            e.printStackTrace();
            WARN("Failed to load images/friends.png");
            tabbedPane.addTab("Friends", friendListPanel);
        }
    }

    /**
     * It sets up the console
     * @param modulesPanel panel where the console needs to be rendered
     */
    private void setupConsole(JPanel modulesPanel){
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Console" ));
        panel.setLayout(new GridLayout(1,1));
        panel.setPreferredSize(new Dimension(100, 200));

        // TextArea & ScrollPane init
        this.console = new JTextPane();
        this.scroll = new JScrollPane (this.console);
        this.scroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.scroll.setPreferredSize(new Dimension(425,200));

        // Console customization
        this.console.setEditable(false);
        this.console.setFont(consoleFont);
        if (Window.selectedTheme.equalsIgnoreCase(Window.THEME_MATERIAL_GOLD)) {
            this.console.setBackground(Color.DARK_GRAY);
        } else {
            this.console.setBackground(new Color(0, 30, 50));
        }
        this.console.setForeground(Color.WHITE);

        // First message to be shown
        String welcome_message = "";
        welcome_message += "Powered by Bettercolors Engine " + Reference.ENGINE_VERSION + ".\n";
        welcome_message += "Source: https://github.com/N3ROO/BettercolorsEngine\n";
        welcome_message += "Thanks: shorebre4k\n";
        addText(welcome_message, true);

        // Write the messages if there were any pending
        while(!waitingMessages.isEmpty()){
            Message message = waitingMessages.poll();
            addText(message.text, message.color, message.newline);
        }

        // Put the panel on the window
        panel.add(this.scroll);
        modulesPanel.add(panel,"South");
    }

    /**
     * Sets up the footer with the version of the mod
     * @param footerPanel the panel where the footer needs to be rendered
     */
    private void setupFooter(JPanel footerPanel){
        JLabel credits = new JLabel(
                " Powered by Bettercolors Engine " + Reference.ENGINE_VERSION + " "
        );
        credits.setFont(new Font(credits.getFont().getFontName(), Font.PLAIN, 12));
        footerPanel.add(credits, "West");
    }

    private JButton createKeybindButton(Module module) {

        JButton button;
        if (module.getToggleKey() != -1) {
            button = new JButton(
                module.getName() + " toggle key: " + module.getToggleKey() +
                        " (" + keyNameFunc.getKeyName(module.getToggleKey()) + ")"
            );
        } else {
            button = new JButton(
                    module.getName() + " no toggle key yet"
            );
        }

        button.addActionListener(e -> {
            // It creates a popup window
            JDialog dialog = new JDialog(Window.instance, "Waiting for input");

            // Content of that popup window (html)
            JLabel msg = new JLabel(
                "<html>Press a key...<br>" +
                        "Please note that due to the difference between<br>" +
                        "VK and GLFW key events, ALT, CTRL and SHIFT<br>" +
                        "keys do not take into account left / right. Only<br>" +
                        "the right key is working. So if you choose<br>" +
                        "the left key, it will register the right one.</html>"
            );

            // Popup layout
            msg.setHorizontalAlignment(JLabel.CENTER);
            dialog.getRootPane().setBorder(new EmptyBorder(10, 10, 10, 10));
            dialog.setLayout(new BorderLayout(0, 15));
            dialog.add(msg, "North");
            dialog.pack();
            dialog.setLocationRelativeTo(Window.instance);

            // The thing that will retrieve the key pressed by the user
            dialog.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {}

                @Override
                public void keyPressed(KeyEvent e) {
                    if(dialog.isVisible()) {
                        int code = Keymap.map(e.getKeyCode(), Reference.MC_INPUTS_VERSION == BettercolorsEngine.MC_INPUTS.NEW);

                        if (code == -2) {
                            JOptionPane.showMessageDialog(Window.instance, "This key is not supported, please use an other one");
                        } else {
                            // Change the key code used
                            module.setToggleKey(code);
                            // Save the new key in the settings file
                            SettingsUtils.setOption(module.getPrefix() + "_toggle_key", Integer.toString(code));
                            // Change the label to show the new key used
                            button.setText(
                                module.getName() + " toggle key: " + module.getToggleKey() +
                                " (" + keyNameFunc.getKeyName(module.getToggleKey()) + ")"
                            );
                        }
                    }

                    // Hide the GUI (it won't trigger event, no worries, it is as if it was turned off)
                    dialog.setVisible(false);
                }

                @Override
                public void keyReleased(KeyEvent e) {}
            });

            // Don't forget to show the popup, otherwise it will be hidden
            dialog.setVisible(true);
        });

        return button;
    }

    /**
     * Loads the settings file and synchronizes EVERYTHING
     */
    private void loadSettings() {
        Map<String, String> options = SettingsUtils.getOptions();
        for(Module module : MODULES){
            module.setOptions(options);
            module.setActivated(Boolean.parseBoolean(options.get(module.getClass().getSimpleName())));
            module.setToggleKey(Integer.parseInt(options.get(module.getPrefix() + "_toggle_key")));
        }

        // Update the toggle key and the HUD
        Window.TOGGLE_KEY = Integer.parseInt(options.get(Window.TOGGLE_KEY_OPTION));
        BettercolorsEngine.VERBOSE = Boolean.parseBoolean(options.get(BettercolorsEngine.DEBUG_OPTION));

        // Tell the user that we loaded the settings file
        INFO( "[+] Loaded \"" + SettingsUtils.SETTINGS_FILENAME + "\"");

        // Synchronize all the components of the GUI using the arrays that we created for the modules' components
        synchronizeComponents();
    }

    /**
     * Updates the friend list (only visually!!!). To get the friend list, use the Friends class.
     */
    public void updateFriends() {
        String[] str = new String[Friends.getFriends().size()];
        friendList.setListData(Friends.getFriends().toArray(str));
    }

    /**
     * Writes the message "msg" to the console "tp" with the color "c"
     * @param tp the console text pane
     * @param msg the message to write
     * @param c the color of the message
     */
    private void appendToPane(JTextPane tp, String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet attribute_set = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        sc.addAttribute(attribute_set, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        sc.addAttribute(attribute_set, TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON);

        int len = tp.getDocument().getLength();

        try {
            tp.getDocument().insertString(len, msg, attribute_set);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs an information message to the console.
     * No prefix added to the message. Conventions:
     * - [+] For success
     * - [~] For something in progress
     * @param text text to log
     */
    public static void INFO(String text) {
        LOG(LogLevel.INFO, text);
    }

    /**
     * Logs a warning message to the console.
     * [*] prefix will automatically be added. Use Window#LOG to log something without the prefix.
     * @param text text to log
     */
    public static void WARN(String text) {
        LOG(LogLevel.WARNING, "[*] " + text);
    }

    /**
     * Logs an error message to the console.
     * [!] prefix will automatically be added. Use Window#LOG to log something without the prefix.
     * @param text text to log
     */
    public static void ERROR(String text) {
        LOG(LogLevel.ERROR, "[!] " + text);
    }

    public static void LOG(LogLevel logLevel, String text) {
        Color color;

        switch (logLevel) {
            case ERROR:
                color = Color.RED;
                break;
            case WARNING:
                color = Color.ORANGE;
                break;
            default:
                color = Color.WHITE;
        }

        if (Window.getInstance() == null) {
            Window.waitingMessages.add(new Message(text, color, true));
        } else {
            Window.getInstance().addText(text, color, true);
        }
    }

    /**
     * Adds text to the console
     * @param text the text
     * @param new_line if it should create a new line
     */
    private void addText(String text, boolean new_line){
        addText(text, Color.WHITE, new_line);
    }

    /**
     * Adds text to the console
     * @param text the text
     * @param color color of the text
     * @param new_line if it should create a new line
     */
    private void addText(String text, Color color, boolean new_line){
        if(this.console == null) {
            waitingMessages.add(new Message(text, color, new_line));
            return;
        }

        this.textCounter ++;
        if(this.textCounter > 30) {
            resetText();
            this.textCounter = 0;
        }

        if(new_line){
            appendToPane(this.console, "\n"+text, color);
        }else{
            appendToPane(this.console, text, color);
        }

        // auto _scroll
        this.console.validate();
        try {
            this.scroll.getVerticalScrollBar().setValue(this.scroll.getVerticalScrollBar().getMaximum());
        }catch(Exception ignored){
            // Seems to happen when the console has a lot of text, but nothing is sure. Need to take a look at it ;)
        }

        repaint();
    }

    /**
     * It clears the console
     */
    public void resetText(){
        this.console.setText("");
        repaint();
    }

    /**
     * It synchronizes the GUI with the modules' current configuration
     */
    public void synchronizeComponents(){
        for(JCheckBox checkbox : CHECKBOXES_ACTIVATION){
            boolean found = false;
            int i = 0;
            while(!found && i < MODULES.size()){
                if(checkbox.getText().equalsIgnoreCase(MODULES.get(i).getClass().getSimpleName())){
                    checkbox.setSelected((MODULES.get(i)).isActivated());
                    found = true;
                }else{
                    ++ i;
                }
            }
        }

        for(JButton keybindButton : KEYBIND_BUTTONS) {
            String name = keybindButton.getText();

            if (name.startsWith("Gui")) {
                keybindButton.setText("Gui toggle key: " + this.keyNameFunc.getKeyName(Window.TOGGLE_KEY));
            } else {
                for(Module module : MODULES){
                    if (name.startsWith(module.getName())) {
                        keybindButton.setText(module.getName() + " toggle key: " + module.getToggleKey());
                        break;
                    }
                }
            }
        }

        for(Module module : MODULES){
            ArrayList<ToggleOption> toggleOptions = Option.getToggleOptions(module.getOptions());
            for(ToggleOption toggle_option : toggleOptions){
                boolean found = false;
                int i = 0;
                while(!found && i < CHECKBOXES_MODULES.size()){
                    if(CHECKBOXES_MODULES.get(i).getText().equalsIgnoreCase(toggle_option.getName())){
                        CHECKBOXES_MODULES.get(i).setSelected(toggle_option.isActivated());
                        found = true;
                    }else{
                        ++ i;
                    }
                }
            }
        }

        for(Module module : MODULES){
            ArrayList<Option> valueOptions = Option.getValueOptions(module.getOptions());
            for(Option valueOption : valueOptions){
                for(Map.Entry<JLabel, JSlider> entry : SLIDERS_MODULES.entrySet()){
                    boolean decimal;
                    float val;
                    if (valueOption instanceof ValueFloatOption) {
                        decimal = true;
                        val = ((ValueFloatOption) valueOption).getVal();
                    } else {
                        decimal = false;
                        val = ((ValueOption) valueOption).getVal();
                    }

                    if(entry.getKey().getText().contains(valueOption.getName())){
                        entry.getKey().setText(valueOption.getName() + " [" + val + "]");
                        entry.getValue().setValue(decimal ? (int) (val * 100f) : (int) val);
                        break; // :(
                    }
                }
            }
        }

        repaint();
    }

    /**
     * Shows a dialog window with the given message
     * @param message message to show
     */
    public void dialog(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    /**
     * It hides / reveals the window.
     */
    public void toggle(){
        SwingUtilities.invokeLater(
                () -> Window.getInstance().setVisible(!Window.getInstance().isVisible())
        );
    }

    public static Window getInstance() {
        return instance;
    }
}