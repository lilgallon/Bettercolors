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
import dev.nero.bettercolors.engine.io.SettingsUtils;
import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.engine.option.ToggleOption;
import dev.nero.bettercolors.engine.option.ValueOption;
import dev.nero.bettercolors.engine.utils.Keymap;
import dev.nero.bettercolors.engine.version.Version;
import dev.nero.bettercolors.engine.version.VersionException;
import dev.nero.bettercolors.engine.Reference;
import mdlaf.MaterialLookAndFeel;
import mdlaf.themes.JMarsDarkTheme;
import mdlaf.themes.MaterialLiteTheme;
import mdlaf.themes.MaterialOceanicTheme;
import mdlaf.themes.MaterialTheme;

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
    public static Window instance;

    // Preferred size for the GUI
    private final int WIDTH = 500;
    private final int HEIGHT = 700;

    // Used to display / change the key used to toggle the GUI
    public final static String TOGGLE_KEY_OPTION = "toggle_key";
    public static String TOGGLE_KEY_NAME;
    public static int TOGGLE_KEY;

    // Modules that will be displayed in the GUI
    private final ArrayList<Module> MODULES;
    // These arrays will by the modules to modify their settings. It is important to have them as attributes because
    // we will need to synchronize them at some point
    private final ArrayList<JCheckBox> CHECKBOXES_ACTIVATION;
    private final ArrayList<JCheckBox> CHECKBOXES_MODULES;
    private final Map<JLabel, JSlider> SLIDERS_MODULES;

    // Used by the console
    private Font consoleFont;
    private int textCounter = 0;
    private JScrollPane scroll;
    private Queue<Message> waitingMessages;

    // Gui components that will be used by other components
    private JTextPane console;

    // Theme-related attributes
    public static LookAndFeel defaultLookAndFeel;
    public final static String THEME_DEFAULT = "default";
    public final static String THEME_MATERIAL_LIGHT = "light";
    public final static String THEME_MATERIAL_OCEANIC = "oceanic";
    public final static String THEME_MATERIAL_GOLD = "gold";
    public static String selectedTheme = THEME_DEFAULT;
    public final static String THEME_OPTION = "theme";

    // If a message comes from this class, we need to append this prefix to the message
    private final String LOG_PREFIX = "[Gui] ";

    /**
     * Creates the GUI
     * @param title window title
     * @param modules modules that will be shown in the GUI
     * @param version current version of the mod
     */
    public Window(String title, ArrayList<Module> modules, Version version) {
        // It calls the JFrame constructor
        super(title);

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
            addText(LOG_PREFIX + "Failed to load images/bettercolors_symbol.png", Color.RED, true);
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
        this.SLIDERS_MODULES = new HashMap<>();

        // This array contains the messages that need to be displayed to the console while the console is still not
        // initialized
        this.waitingMessages = new LinkedList<>();

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
            this.addText(LOG_PREFIX + "Could not load the CascadiaCode font", Color.ORANGE, true);
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
        // JPanel header_layout = new JPanel();
        // header_layout.setLayout(new BorderLayout());
        // setupHeader(header_layout);

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
        // getContentPane().add(header_layout, "North");
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
        JMenu report_menu = new JMenu("Found a bug?");
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
        report_menu.add(report);
        menuBar.add(report_menu);

        // Last thing that we need is to show if the current version is the last one
        JLabel update = new JLabel();

        try {
            Version latest = Version.getLatestVersion(Reference.MC_VERSION);
            Version.VersionDiff diff = version.compareWith(latest);
            switch (diff) {
                case DEVELOPMENT:
                    update.setForeground(new Color(150, 70, 0));
                    update.setText("Development build");
                    addText("You are using a development build", Color.ORANGE, true);
                    break;
                case UPDATED:
                    update.setText("No update available");
                    addText("You are using the last version", Color.GREEN, true);
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
                                Desktop.getDesktop().browse(new URI(Reference.DOWNLOAD_URL));
                            } catch (URISyntaxException | IOException ex) {
                                addText("Error while trying to go to the download page", Color.RED, true);
                                addText("Here is the download page: " + Reference.DOWNLOAD_URL, Color.RED, true);
                            }
                        }
                    });

                    // Show the changelog
                    addText("", true);
                    addText("", true);
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

                    addText("There is an update available", Color.ORANGE, true);
                    break;
            }
        } catch (VersionException e) {
            switch (e.getCode()) {
                case NO_INTERNET:
                    update.setForeground(new Color(100, 0, 0));
                    update.setText("Could not check the version");
                    addText("Could not check the version", Color.RED, true);
                    addText("If you are not connected to internet, it's normal", Color.RED, true);
                    break;
                case URL_ISSUE:
                    update.setForeground(new Color(100, 0, 0));
                    update.setText("URL issue");
                    addText("Could not read the URL to check for the version", Color.RED, true);
                    addText("It should not happen, you can open an issue to github", Color.RED, true);
                    break;
                case NO_VERSION:
                    update.setForeground(new Color(100, 0, 0));
                    update.setText("No version found");
                    addText("No version found, was bettercolors released for MC " + Reference.MC_VERSION + "?", Color.RED, true);
                    addText("If yes, then the API may have changed, you can open an issue to github", Color.RED, true);
                    break;
            }
        }
        menuBar.add(update);

        // We can add the menuBar to the window
        this.setJMenuBar(menuBar);
    }

    /*
    private void setupHeader(JPanel header_layout){
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
                module.toggle();
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
        tabbedPane.setPreferredSize(new Dimension(100, 350));

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
            if(toggleOptions != null) {
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
            ArrayList<ValueOption> value_options = Option.getValueOptions(module.getOptions());
            // Yes, there is a warning, because at the moment, all the modules have value options, so it is always true
            if(value_options != null){
                // We will create a slider grid that contains one slider per line. The first column contains the text
                // while the second one contains the actual slider
                JPanel slidersPanel = new JPanel();
                slidersPanel.setLayout(new GridLayout(value_options.size(), 2));

                for(ValueOption value_option : value_options){
                    // The label contains the value next to the its name
                    final JLabel label = new JLabel(value_option.getName() + " [" + value_option.getVal() + "]");
                    final JSlider slider = new JSlider();

                    // Preferred size
                    slider.setPreferredSize(new Dimension(WIDTH/2, 10));

                    // Slider value settings
                    slider.setMinimum(value_option.getMin());
                    slider.setMaximum(value_option.getMax());
                    slider.setValue(value_option.getVal());

                    // Max/min size
                    slider.setMaximumSize(new Dimension(100, 10));
                    slider.setMinimumSize(new Dimension(100, 5));

                    // The theme does not feel right with the material gold, so we update it manually here
                    if (Window.selectedTheme.equalsIgnoreCase(Window.THEME_MATERIAL_GOLD)) {
                        slider.setForeground(Color.ORANGE);
                    }

                    // What happens when the user uses the slider
                    slider.addChangeListener(e -> {
                        // Change the module's option
                        value_option.setVal(slider.getValue());
                        // Update the label with the slider's new value
                        label.setText(value_option.getName() + " [" + value_option.getVal() + "]");
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
                addText(LOG_PREFIX + "Failed to load images/" + module.getSymbol(), Color.RED, true);
                tabbedPane.addTab(module.getName(), content);
            }
        }

        // Now that we have one tab for each module, we want to create a custom tab for settings management
        JPanel settings_panel = new JPanel();
        settings_panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        settings_panel.setLayout(new BorderLayout(0, 15));

        // Used to change the key to toggle the GUI
        JButton keybind = new JButton("Change the key to toggle the GUI [" + Window.TOGGLE_KEY_NAME + "]");
        keybind.addActionListener(e -> {
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
                        int code = Keymap.map(e.getKeyCode(), Reference.FORGE_API == BettercolorsEngine.FORGE.NEW);

                        if (code == -2) {
                            JOptionPane.showMessageDialog(Window.instance, "This key is not supported, please use an other one");
                        } else {
                            // Change the message that shows the current key used
                            Window.TOGGLE_KEY_NAME = e.getKeyChar() + " code: " + code;
                            // Change the key code used
                            Window.TOGGLE_KEY = code;
                            // Save the new key in the settings file
                            SettingsUtils.setOption(Window.TOGGLE_KEY_OPTION, Integer.toString(code));
                            // Change the label to show the new key used
                            keybind.setText("Change the key to toggle the GUI [" + Window.TOGGLE_KEY_NAME + "]");
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
        settings_panel.add(keybind, "North");

        // Now that we have the button to change the key to toggle the GUI, we want to add a new panel to manage the
        // settings file
        JPanel config_panel = new JPanel();
        config_panel.setLayout(new BorderLayout());

        // The label that will show which settings file is currently selected. Note that the bettercolors settings file
        // have the bc_ prefix. So we only want to show the files with the bc_ prefix.
        final String selected_file_prefix = "Selected config : ";
        JLabel selected_file = new JLabel(
                // Remoe the bc_ prefix for a user-friendly interface lol
                selected_file_prefix + SettingsUtils.SETTINGS_FILENAME.replaceFirst("bc_", "")
        );
        config_panel.add(selected_file, "North");

        // Show all the available settings file in a list
        DefaultListModel<String> filenames = SettingsUtils.getAllSettingsFilenames();
        JList<String> list = new JList<>(filenames);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(filenames.indexOf(SettingsUtils.SETTINGS_FILENAME));
        config_panel.add(new JScrollPane(list), "Center");

        // Now we want the buttons to load, open and refresh
        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());

        // Load button: loads the selected settings file
        JButton select_button = new JButton("Load");
        select_button.addActionListener(e -> {
            // Get selected filename and update it in our SettingsUtils class
            SettingsUtils.SETTINGS_FILENAME = list.getSelectedValue();
            // Show that the one selected is now this one
            selected_file.setText(
                    selected_file_prefix + SettingsUtils.SETTINGS_FILENAME.replaceFirst("bc_", "")
            );

            // Update the selected settings file
            Map<String, String> option = new HashMap<>();
            option.put("settings_file", SettingsUtils.SETTINGS_FILENAME);
            Filer filer = new Filer(SettingsUtils.FILE_WITH_CURRENT_SETTINGS_USED);
            filer.write(option, false);

            // Load the settings (the code is designed for it to be very simple)
            Map<String, String> options = SettingsUtils.getOptions();
            for(Module module : MODULES){
                module.setOptions(options);
                module.setActivated(Boolean.parseBoolean(options.get(module.getClass().getSimpleName())));
            }

            // Update the toggle key and the HUD
            Window.TOGGLE_KEY = Integer.parseInt(options.get(Window.TOGGLE_KEY_OPTION));
            Window.TOGGLE_KEY_NAME = "code: " + Window.TOGGLE_KEY;
            keybind.setText("Change the key to toggle the GUI [" + Window.TOGGLE_KEY_NAME + "]");

            // Tell the user that we loaded the settings file
            addText(LOG_PREFIX + "Loaded \"" + SettingsUtils.SETTINGS_FILENAME + "\".", true);

            // Synchronize all the components of the GUI using the arrays that we created for the modules' components
            synchronizeComponents();
        });
        buttons.add(select_button);

        // Open button: opens the directory where are stored the settings in the OS' default explorer
        JButton open_button = new JButton("Open");
        open_button.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(Filer.getSettingsDirectory());
            } catch (IOException e1) {
                addText(LOG_PREFIX + "unable to open the settings directory !", Color.RED, true);
            }
        });
        buttons.add(open_button);

        // Refresh the settings file (if the user added one in the meantime)
        JButton refresh_button = new JButton("Refresh");
        refresh_button.addActionListener(e ->{
            DefaultListModel<String> newList = SettingsUtils.getAllSettingsFilenames();
            int length_diff = newList.size() - list.getModel().getSize();
            list.setModel(newList);
            list.setSelectedIndex(SettingsUtils.getAllSettingsFilenames().indexOf(SettingsUtils.SETTINGS_FILENAME));
            if(length_diff == 0){
                addText(LOG_PREFIX + "No new files found.", true);
            }else if(length_diff == 1){
                addText(LOG_PREFIX + "Found " + length_diff + " new file.", true);
            }else if(length_diff > 1){
                addText(LOG_PREFIX + "Found " + length_diff + " new files.", true);
            }
        });
        buttons.add(refresh_button);
        config_panel.add(buttons, "South");

        // Add a specific icon to the settings management tab
        try {
            ImageIcon icon = new ImageIcon(
                    Objects.requireNonNull(
                            Thread.currentThread()
                                    .getContextClassLoader()
                                    .getResource("images/settings_symbol.png")
                    )
            );
            tabbedPane.addTab("Settings", icon, settings_panel);
        } catch (Exception e) {
            e.printStackTrace();
            addText(LOG_PREFIX + "Failed to load images/settings_symbol.png", Color.RED, true);
            tabbedPane.addTab("Settings", settings_panel);
        }

        settings_panel.add(config_panel, "Center");
        modulesPanel.add(tabbedPane, "Center");
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
                " Bettercolors " + Reference.MOD_VERSION + " for MC " + Reference.MC_VERSION
        );
        credits.setFont(new Font(credits.getFont().getFontName(), Font.PLAIN, 12));
        footerPanel.add(credits, "West");
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
     * Adds text to the console
     * @param text the text
     * @param new_line if it should create a new line
     */
    public void addText(String text, boolean new_line){
        addText(text, Color.WHITE, new_line);
    }

    /**
     * Adds text to the console
     * @param text the text
     * @param color color of the text
     * @param new_line if it should create a new line
     */
    public void addText(String text, Color color, boolean new_line){
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
            ArrayList<ValueOption> value_options = Option.getValueOptions(module.getOptions());
            for(ValueOption value_option : value_options){
                for(Map.Entry<JLabel, JSlider> entry : SLIDERS_MODULES.entrySet()){
                    if(entry.getKey().getText().contains(value_option.getName())){
                        entry.getKey().setText(value_option.getName() + " [" + value_option.getVal() + "]");
                        entry.getValue().setValue(value_option.getVal());
                        break; // :(
                    }
                }
            }
        }

        repaint();
    }

    /**
     * It hides / reveals the window.
     */
    public void toggle(){
        setVisible(!isVisible());
    }

    class Message{
        String text;
        Color color;
        Boolean newline;

        Message(String text, Color color, Boolean newline){
            this.text = text;
            this.color = color;
            this.newline = newline;
        }
    }
}