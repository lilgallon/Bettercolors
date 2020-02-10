package dev.nero.bettercolors.view;

import dev.nero.bettercolors.io.Filer;
import dev.nero.bettercolors.io.SettingsUtils;
import dev.nero.bettercolors.main.Bettercolors;
import dev.nero.bettercolors.main.Reference;
import dev.nero.bettercolors.modules.Module;
import dev.nero.bettercolors.modules.options.Option;
import dev.nero.bettercolors.modules.options.ToggleOption;
import dev.nero.bettercolors.modules.options.ValueOption;
import dev.nero.bettercolors.utils.VKtoAWT;
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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class Window extends JFrame{

    public static Window instance;
    public static LookAndFeel defaultLookAndFeel;
    private final ArrayList<Module> MODULES;
    private JTextPane _console;
    private int _messageCounter = 0;
    private JScrollPane _scroll;
    private final ArrayList<JCheckBox> CHECKBOXES_ACTIVATION;
    private final ArrayList<JCheckBox> CHECKBOXES_MODULES;
    private final Map<JLabel, JSlider> SLIDERS_MODULES;
    private final String LOG_PREFIX = "[Gui] ";

    private Queue<Message> waitingMessages;

    public Window(String title, ArrayList<Module> modules, String[] versionInfo) {
        super(title);

        int width = 450;
        int height = 600;
        setBounds((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-width/2,(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-height/2,width,height);
        try {
            setIconImage(new ImageIcon(this.getClass().getResource("/images/bettercolors_symbol.png")).getImage());
        } catch (Exception e) {
            e.printStackTrace();
            addText("Failed to load /images/bettercolors_symbol.png", Color.RED, true);
        }
        setResizable(true);
        setVisible(false);

        instance = this;
        MODULES = modules;
        CHECKBOXES_ACTIVATION = new ArrayList<>();
        CHECKBOXES_MODULES = new ArrayList<>();
        SLIDERS_MODULES = new HashMap<>();

        waitingMessages = new LinkedList<>();

        // Toolbar
        JMenuBar toolbar = new JMenuBar();
        JMenu themes = new JMenu("Themes");
        JMenuItem themeDefault = new JMenuItem("Default");
        JMenuItem themeLight = new JMenuItem("Material Light");
        JMenuItem themeDark = new JMenuItem("Material Oceanic");
        JMenuItem themeDark2 = new JMenuItem("Material Gold");
        themeDefault.addActionListener((event) -> this.changeTheme(null));
        themeLight.addActionListener((event) -> this.changeTheme(new MaterialLiteTheme()));
        themeDark.addActionListener((event) -> this.changeTheme(new MaterialOceanicTheme()));
        themeDark2.addActionListener((event) -> {
            this.changeTheme(new JMarsDarkTheme());
            this._console.setBackground(Color.DARK_GRAY);
        });
        themes.add(themeDefault);
        themes.add(themeLight);
        themes.add(themeDark);
        themes.add(themeDark2);
        toolbar.add(themes);
        setJMenuBar(toolbar);

        // Header
        // JPanel header_layout = new JPanel();
        // header_layout.setLayout(new BorderLayout());
        // setupHeader(header_layout);

        // Modules
        JPanel modules_related_layout = new JPanel();
        modules_related_layout.setLayout(new BorderLayout());
        setupModulesActivationStatus(modules_related_layout);
        setupModulesOptions(modules_related_layout);
        setupConsole(modules_related_layout);

        // Footer
        JPanel footer_layout = new JPanel();
        footer_layout.setLayout(new BorderLayout());
        setupFooter(footer_layout, versionInfo);

        // getContentPane().add(header_layout, "North");
        getContentPane().add(modules_related_layout, "Center");
        getContentPane().add(footer_layout, "South");
        pack();
        repaint();
    }

    private void changeTheme(MaterialTheme theme) {
        // TODO: save selected theme
        try {
            // null means not material theme
            if (theme != null) {
                if (!(UIManager.getLookAndFeel() instanceof MaterialLookAndFeel)) {
                    UIManager.setLookAndFeel(new MaterialLookAndFeel());
                }
                MaterialLookAndFeel.changeTheme(theme);
            } else {
                UIManager.setLookAndFeel(defaultLookAndFeel);
            }

            SwingUtilities.updateComponentTreeUI(this);

            Font consoleFont = new Font("Lucida Console", Font.PLAIN, 14);
            try {
                consoleFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResource("/fonts/Cascadia.ttf").openStream());
                GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
                genv.registerFont(consoleFont);
                consoleFont = consoleFont.deriveFont(14f);
            } catch (Exception e) {
                e.printStackTrace();
            }

            _console.setFont(consoleFont);
            _console.setBackground(new Color(0, 30, 50));
            _console.setForeground(Color.WHITE);

            JOptionPane.showMessageDialog(this, "You should restart your game to apply the new theme completely");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    private void setupHeader(JPanel header_layout){
        // READY TO GET IMPLEMENTED IF NEEDED
    }
    */

    private void setupFooter(JPanel footer_layout, String[] versionInfo){
        String last_version = versionInfo[0];
        String changelog = versionInfo[1];

        JLabel credits = new JLabel(" Bettercolors " + Reference.VERSION + " for MC " + Reference.ACCEPTED_VERSIONS.replace("[", "").replace("]", "") + " by N3RO. ");
        JLabel update = new JLabel();

        if(last_version.equalsIgnoreCase(Reference.VERSION)){
            update.setForeground(new Color(0, 100, 0 ));
            update.setText("Mod up-to-date ! :)");
            addText("You are using the last version ! ;) " + last_version + ".", Color.GREEN, true);
        }else if(last_version.equalsIgnoreCase(Bettercolors.INTERNET_PROBLEM)){
            update.setForeground(new Color(200, 100, 0));
            update.setText(Bettercolors.INTERNET_PROBLEM);
        }else if(last_version.equalsIgnoreCase(Bettercolors.URL_PROBLEM)){
            update.setForeground(new Color(100, 0, 0));
            update.setText(Bettercolors.URL_PROBLEM);
        }else if(last_version.equalsIgnoreCase(Bettercolors.UNRELEASED)) {
            update.setForeground(new Color(200, 200, 0));
            update.setText(Bettercolors.UNRELEASED);
        }else if(last_version.equalsIgnoreCase(Bettercolors.EMPTY_PAGE)) {
            update.setForeground(new Color(100, 0, 0));
            update.setText(Bettercolors.EMPTY_PAGE);
        }else{
            int[] version_dif = Bettercolors.compareVersions(Reference.VERSION, last_version);
            if(version_dif != null) {
                int total_dif = 0;
                for (int i = 0; i < 4; i ++) {
                    if(version_dif[i] != 0) {
                        total_dif = version_dif[i];
                        break;
                    }
                }

                if(total_dif > 1){
                    update.setForeground(new Color(0, 70, 100));
                    addText(total_dif + " updates available !", Color.ORANGE, true);
                }else if(total_dif == 1){
                    update.setForeground(new Color(0, 70, 100));
                    addText("One update available !", Color.ORANGE, true);
                }else{
                    update.setForeground(new Color(150, 70, 0));
                    addText("You are using a dev version. (dif=" + total_dif + ").", Color.ORANGE, true);
                    addText("Be careful ! It is used for testing.", Color.ORANGE, true);
                }
                addText("Last stable version : " + last_version + ".", Color.ORANGE, true);

                update.setText("Update available ! Version " + last_version + ".");
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
                            Desktop.getDesktop().browse(new URI(Bettercolors.DOWNLOAD_URL));
                        } catch (URISyntaxException | IOException ex) {
                            addText("Error while trying to go to the download page.", Color.RED, true);
                            addText("Here is the download page : " + Bettercolors.DOWNLOAD_URL, Color.RED, true);
                        }
                    }
                });
            }else{
                update.setForeground(new Color(120, 20, 20));
                addText("Unable to compare versions !", Color.RED, true);
                addText("This problem should be reported to https://github.com/N3ROO/Bettercolors/issues.", Color.RED, true);
                update.setText("Unable to compare versions");
            }

            addText("", true);
            addText("", true);
            String[] lines = changelog.split("\\\\n");
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
        }

        footer_layout.add(credits, "West");
        footer_layout.add(update, "Center");
    }

    private void setupModulesActivationStatus(JPanel modules_related_layout){
        // Setup grid
        JPanel activation_grid = new JPanel();
        activation_grid.setLayout(new GridLayout((int)Math.ceil((double) MODULES.size() / 2d), 2));
        activation_grid.setBorder(new TitledBorder(new EtchedBorder(), "Modules"));

        for(Module module : MODULES) {
            // Setup checkboxes
            final JCheckBox checkBox = new JCheckBox(module.getClass().getSimpleName());
            checkBox.setSelected(module.isActivated());
            checkBox.addActionListener(e -> {
                module.toggle();
                SettingsUtils.setOption(module.getClass().getSimpleName(), Boolean.toString(module.isActivated()));
                checkBox.setSelected(module.isActivated());
                repaint();
            });
            CHECKBOXES_ACTIVATION.add(checkBox);
            // Put checkboxes on grid
            activation_grid.add(checkBox);
        }
        modules_related_layout.add(activation_grid, "North");
    }

    private void setupModulesOptions(JPanel modules_related_layout){
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(100, 350));

        // Modules' related tabs
        for(Module module : MODULES){
            if(module.getOptions().size() == 0) continue;

            JPanel module_options_panel = new JPanel();
            module_options_panel.setLayout(new BorderLayout());
            module_options_panel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JPanel content = new JPanel(new BorderLayout());

            ArrayList<ToggleOption> toggle_options = Option.getToggleOptions(module.getOptions());
            if(toggle_options != null) {
                JPanel checkboxes_grid = new JPanel();
                checkboxes_grid.setLayout(new GridLayout((int)Math.ceil((double)toggle_options.size() / 2d), 2));

                for(ToggleOption toggle_option : toggle_options){
                    final JCheckBox checkBox = new JCheckBox(toggle_option.getName());
                    checkBox.setSelected(toggle_option.isActivated());
                    checkBox.addActionListener(e -> {
                        toggle_option.toggle();
                        checkBox.setSelected(toggle_option.isActivated());
                        repaint();
                    });
                    CHECKBOXES_MODULES.add(checkBox);
                    // Put checkboxes on grid
                    checkboxes_grid.add(checkBox);
                }
                module_options_panel.add(checkboxes_grid, "North");
            }

            ArrayList<ValueOption> value_options = Option.getValueOptions(module.getOptions());
            if(value_options != null){
                JPanel sliders_grid = new JPanel();
                sliders_grid.setLayout(new GridLayout(value_options.size(), 2));

                for(ValueOption value_option : value_options){
                    final JLabel label = new JLabel(value_option.getName() + " [" + value_option.getVal() + "]");
                    final JSlider slider = new JSlider();
                    slider.setMinimum(value_option.getMin());
                    slider.setMaximum(value_option.getMax());
                    slider.setValue(value_option.getVal());
                    slider.setMaximumSize(new Dimension(100, 10));
                    slider.setMinimumSize(new Dimension(100, 5));
                    slider.addChangeListener(e -> {
                        value_option.setVal(slider.getValue());
                        label.setText(value_option.getName() + " [" + value_option.getVal() + "]");
                        repaint();
                    });
                    SLIDERS_MODULES.put(label, slider);
                    sliders_grid.add(label);
                    sliders_grid.add(slider);
                }

                module_options_panel.add(sliders_grid, "Center");

            }

            content.add(module_options_panel);

            JScrollPane scrollPane = new JScrollPane(module_options_panel,
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            content.add(scrollPane);

            try{
                ImageIcon icon = new ImageIcon(this.getClass().getResource("/images/" + module.getSymbol()));
                tabbedPane.addTab(module.getName(), icon, content);
            } catch (Exception e) {
                addText("Failed to load /images/" + module.getSymbol(), Color.RED, true);
                tabbedPane.addTab(module.getName(), content);
            }

        }
        // --

        // Settings' related tab
        JPanel settings_panel = new JPanel();
        settings_panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        settings_panel.setLayout(new BorderLayout(0, 15));

        // List of parameters
        // ! Update GridLayout if you add a new one
        JButton keybind = new JButton("Change the key to toggle the GUI [" + Bettercolors.TOGGLE_KEY_NAME + "]");
        keybind.addActionListener(e -> {
            JDialog dialog = new JDialog(instance, "Message");
            JLabel msg = new JLabel(
                    "<html>Press a key...<br>" +
                            "Please note that due to the difference between<br>" +
                            "VK and AWT key events, CTRL and SHIFT<br>" +
                            "keys do not take into account left / right. Only<br>" +
                            "the right key is working. So if you choose<br>" +
                            "the left key, it will register the right one.<br>" +
                            "Also, ALT is not working, because there is no mapping <br>" +
                            "for ALT with AWT events.</html>");
            msg.setHorizontalAlignment(JLabel.CENTER);
            dialog.getRootPane().setBorder(new EmptyBorder(10, 10, 10, 10));
            dialog.setLayout(new BorderLayout(0, 15));
            dialog.add(msg, "North");
            dialog.pack();
            dialog.setLocationRelativeTo(instance);
            dialog.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {}

                @Override
                public void keyPressed(KeyEvent e) {
                    if(dialog.isVisible()) {
                        int code = VKtoAWT.convertVKSwingtoAWT(e.getKeyCode());
                        Bettercolors.TOGGLE_KEY_NAME = e.getKeyChar() + " code: " + code;
                        Bettercolors.TOGGLE_KEY = code;
                        SettingsUtils.setOption(Bettercolors.TOGGLE_KEY_OPTION, Integer.toString(code));
                        keybind.setText("Change the key to toggle the GUI [" + Bettercolors.TOGGLE_KEY_NAME + "]");
                    }
                    dialog.setVisible(false);
                }

                @Override
                public void keyReleased(KeyEvent e) {}
            });

            dialog.setVisible(true);
        });
        settings_panel.add(keybind, "North");

        JPanel config_panel = new JPanel();
        config_panel.setLayout(new BorderLayout());

        final String selected_file_prefix = "Selected config : ";
        JLabel selected_file = new JLabel(selected_file_prefix + SettingsUtils.SETTINGS_FILENAME);
        config_panel.add(selected_file, "North");

        DefaultListModel<String> filenames = SettingsUtils.getAllSettingsFilenames();
        JList<String> list = new JList<>(filenames);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(filenames.indexOf(SettingsUtils.SETTINGS_FILENAME));
        config_panel.add(new JScrollPane(list), "Center");

        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());
        // select button
        JButton select_button = new JButton("Load");
        select_button.addActionListener(e -> {
            SettingsUtils.SETTINGS_FILENAME = list.getSelectedValue();
            selected_file.setText(selected_file_prefix + SettingsUtils.SETTINGS_FILENAME);
            // Load configuration
            Map<String, String> options = SettingsUtils.getOptions();
            for(Module module : MODULES){
                module.setOptions(options);
                module.setActivated(Boolean.parseBoolean(options.get(module.getClass().getSimpleName())));
            }
            addText(LOG_PREFIX + "Loaded \"" + SettingsUtils.SETTINGS_FILENAME + "\".", true);
            synchronizeComponents();
        });
        buttons.add(select_button);
        // open button
        JButton open_button = new JButton("Open");
        open_button.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(Filer.getSettingsDirectory());
            } catch (IOException e1) {
                addText(LOG_PREFIX + "unable to open the settings directory !", Color.RED, true);
            }
        });
        buttons.add(open_button);
        // refresh button
        JButton refresh_button = new JButton("Refresh");
        refresh_button.addActionListener(e ->{
            DefaultListModel<String> new_list = SettingsUtils.getAllSettingsFilenames();
            int length_diff = new_list.size() - list.getModel().getSize();
            list.setModel(new_list);
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

        try {
            ImageIcon icon = new ImageIcon(this.getClass().getResource("/images/settings_symbol.png"));
            tabbedPane.addTab("Settings", icon, settings_panel);
        } catch (Exception e) {
            addText("Failed to load /images/settings_symbol.png", Color.RED, true);
            tabbedPane.addTab("Settings", settings_panel);
        }
        settings_panel.add(config_panel, "Center");
        // --

        modules_related_layout.add(tabbedPane, "Center");
    }

    private void setupConsole(JPanel modules_related_layout){
        JPanel panel = new JPanel ();
        panel.setBorder ( new TitledBorder ( new EtchedBorder (), "Console" ) );
        panel.setLayout(new GridLayout(1,1));
        panel.setPreferredSize(new Dimension(100, 200));

        // TextArea & ScrollPane init
        _console = new JTextPane();
        _scroll = new JScrollPane (_console);
        _scroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        _scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        _scroll.setPreferredSize(new Dimension(425,200));

        // TextArea custom
        //info_box.setWrapStyleWord(true);

        Font consoleFont = new Font("Lucida Console", Font.PLAIN, 14);
        try {
            consoleFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResource("/fonts/Cascadia.ttf").openStream());
            GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
            genv.registerFont(consoleFont);
            consoleFont = consoleFont.deriveFont(14f);
        } catch (Exception e) {
            e.printStackTrace();
        }

        _console.setEditable(false);
        _console.setFont(consoleFont);
        if (Bettercolors.selectedTheme instanceof JMarsDarkTheme) {
            _console.setBackground(Color.DARK_GRAY);
        } else {
            _console.setBackground(new Color(0,30,50));
        }
        _console.setForeground(Color.WHITE);
        String welcome_message = "";
        welcome_message += "x~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~x\n";
        welcome_message += "|                                                |\n";
        welcome_message += "|       .-``'.    Bettercolors 6    .'''-.       |\n";
        welcome_message += "|     .`   .`~     Made by N3RO     ~`.   '.     |\n";
        welcome_message += "| _.-'     '._   github.com/N3ROO   _.'     '-._ |\n";
        welcome_message += "| Acknowledgements: @shorebre4k  @patricktelling |\n";
        welcome_message += "x~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~x\n";
        _console.setText(welcome_message);

        while(!waitingMessages.isEmpty()){
            Message message = waitingMessages.poll();
            addText(message.text, message.color, message.newline);
        }

        // Put the panel on the window
        panel.add(_scroll);
        modules_related_layout.add(panel,"South");
    }

    /**
     * Writes the message "msg" to the console "tp" with the color "c".
     * @param tp the console text pane,
     * @param msg the message to write,
     * @param c the color of the message.
     */
    private void appendToPane(JTextPane tp, String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet attribute_set = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        //attribute_set = sc.addAttribute(attribute_set, StyleConstants.FontFamily, "Lucida Console");
        attribute_set = sc.addAttribute(attribute_set, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();

        try {
            tp.getDocument().insertString(len, msg, attribute_set);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds text to the console.
     * @param text the text,
     * @param new_line if it should create a new line.
     */
    public void addText(String text, boolean new_line){
        addText(text, Color.WHITE, new_line);
    }

    /**
     * Adds text to the console.
     * @param text the text,
     * @param color color of the text,
     * @param new_line if it should create a new line.
     */
    public void addText(String text, Color color, boolean new_line){

        if(_console == null) {
            waitingMessages.add(new Message(text, color, new_line));
            return;
        }

        _messageCounter ++;
        if(_messageCounter > 30) {
            resetText();
            _messageCounter = 0;
        }

        if(new_line){
            appendToPane(_console, "\n"+text, color);
        }else{
            appendToPane(_console, text, color);
        }

        // auto _scroll
        _console.validate();
        try {
            _scroll.getVerticalScrollBar().setValue(_scroll.getVerticalScrollBar().getMaximum());
        }catch(NullPointerException ignored){
            // Seems to happen when the console has a lot of text, but nothing is sure. Need to take a look at it ;)
        }

        repaint();
    }

    /**
     * It clears the console.
     */
    public void resetText(){
        _console.setText("");
        repaint();
    }

    /**
     * It synchronizes the modules' components with the modules' current configuration.
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
            ArrayList<ToggleOption> toggle_options = Option.getToggleOptions(module.getOptions());
            for(ToggleOption toggle_option : toggle_options){
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