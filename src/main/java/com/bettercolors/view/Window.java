package com.bettercolors.view;

import com.bettercolors.io.Filer;
import com.bettercolors.io.SettingsUtils;
import com.bettercolors.main.Bettercolors;
import com.bettercolors.main.Reference;
import com.bettercolors.modules.Module;
import com.bettercolors.modules.options.Option;
import com.bettercolors.modules.options.ToggleOption;
import com.bettercolors.modules.options.ValueOption;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Window extends JFrame{

    public static Window instance;
    private final ArrayList<Module> MODULES;
    private JTextPane _console;
    private JScrollPane _scroll;
    private final ArrayList<JCheckBox> CHECKBOXES_ACTIVATION;
    private final ArrayList<JCheckBox> CHECKBOXES_MODULES;
    private final Map<JLabel, JSlider> SLIDERS_MODULES;
    private final String LOG_PREFIX = "[Gui] ";

    public Window(String title, ArrayList<Module> modules, String last_version) {
        super(title);

        int width = 450;
        int height = 600;
        setBounds((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-width/2,(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-height/2,width,height);
        setIconImage(new ImageIcon(this.getClass().getResource("/images/bettercolors_symbol.png")).getImage());
        setResizable(true);
        setVisible(false);

        instance = this;
        MODULES = modules;
        CHECKBOXES_ACTIVATION = new ArrayList<>();
        CHECKBOXES_MODULES = new ArrayList<>();
        SLIDERS_MODULES = new HashMap<>();

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
        setupFooter(footer_layout, last_version);

        // getContentPane().add(header_layout, "North");
        getContentPane().add(modules_related_layout, "Center");
        getContentPane().add(footer_layout, "South");
        pack();
        repaint();
    }

    /*
    private void setupHeader(JPanel header_layout){
        // READY TO GET IMPLEMENTED IF NEEDED
    }
    */

    private void setupFooter(JPanel footer_layout, String last_version){
        JLabel credits = new JLabel(" Bettercolors " + Reference.VERSION + " for MC " + Reference.ACCEPTED_VERSIONS.replace("[", "").replace("]", "") + " by N3RO. ");

        JLabel update = new JLabel();
        if(last_version.equalsIgnoreCase(Reference.VERSION)){
            update.setForeground(new Color(0, 100, 0 ));
            update.setText("Mod up-to-date ! :)");
        }else if(last_version.equalsIgnoreCase(Bettercolors.INTERNET_PROBLEM)){
            update.setForeground(new Color(200, 100, 0));
            update.setText(Bettercolors.INTERNET_PROBLEM);
        }else if(last_version.equalsIgnoreCase(Bettercolors.URL_PROBLEM)){
            update.setForeground(new Color(100, 0, 0));
            update.setText(Bettercolors.URL_PROBLEM);
        }else{
            update.setForeground(new Color(0, 70, 100));
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

        // Modules' related tabs
        for(Module module : MODULES){
            if(module.getOptions().size() == 0) continue;

            JPanel module_options_panel = new JPanel();
            module_options_panel.setLayout(new BorderLayout());

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
                    final JLabel label = new JLabel(value_option.getName() + " [" + Integer.toString(value_option.getVal()) + "]");
                    final JSlider slider = new JSlider();
                    slider.setMinimum(value_option.getMin());
                    slider.setMaximum(value_option.getMax());
                    slider.setValue(value_option.getVal());
                    slider.setMinorTickSpacing(value_option.getMinorTickSpacing());
                    slider.setMajorTickSpacing(value_option.getMajorTickSpacing());
                    slider.setPaintTicks(true);
                    slider.addChangeListener(e -> {
                        value_option.setVal(slider.getValue());
                        label.setText(value_option.getName() + " [" + Integer.toString(value_option.getVal()) + "]");
                        repaint();
                    });
                    SLIDERS_MODULES.put(label, slider);
                    sliders_grid.add(label);
                    sliders_grid.add(slider);
                }

                module_options_panel.add(sliders_grid, "Center");
            }
            ImageIcon icon = new ImageIcon(this.getClass().getResource("/images/" + module.getSymbol()));
            tabbedPane.addTab(module.getName(), icon, module_options_panel);
        }
        // --

        // Settings' related tab
        JPanel settings_panel = new JPanel();
        settings_panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        settings_panel.setLayout(new BorderLayout(0, 15));

        final String selected_file_prefix = "Selected config : ";
        JLabel selected_file = new JLabel(selected_file_prefix + SettingsUtils.SETTINGS_FILENAME);
        settings_panel.add(selected_file, "North");

        DefaultListModel<String> filenames = SettingsUtils.getAllSettingsFilenames();
        JList<String> list = new JList<>(filenames);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(filenames.indexOf(SettingsUtils.SETTINGS_FILENAME));
        settings_panel.add(new JScrollPane(list), "Center");

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
            list.setModel(SettingsUtils.getAllSettingsFilenames());
            list.setSelectedIndex(SettingsUtils.getAllSettingsFilenames().indexOf(SettingsUtils.SETTINGS_FILENAME));
            // todo log found files
        });
        buttons.add(refresh_button);
        settings_panel.add(buttons, "South");

        ImageIcon icon = new ImageIcon(this.getClass().getResource("/images/settings_symbol.png"));
        tabbedPane.addTab("Settings", icon, settings_panel);
        // --

        modules_related_layout.add(tabbedPane, "Center");
    }

    private void setupConsole(JPanel modules_related_layout){
        JPanel panel = new JPanel ();
        panel.setBorder ( new TitledBorder ( new EtchedBorder (), "Console" ) );
        panel.setLayout(new GridLayout(1,1));

        // TextArea & ScrollPane init
        _console = new JTextPane();
        _scroll = new JScrollPane (_console);
        _scroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        _scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        _scroll.setPreferredSize(new Dimension(425,100));

        // TextArea custom
        //info_box.setWrapStyleWord(true);
        _console.setEditable(false);
        _console.setFont(new Font("Lucida Console", Font.PLAIN, 14));
        _console.setBackground(new Color(0,30,50));
        _console.setForeground(Color.WHITE);
        String welcome_message = "";
        welcome_message += "x~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~x\n";
        welcome_message += "|                                                |\n";
        welcome_message += "|       .-``'.    Bettercolors 6    .'''-.       |\n";
        welcome_message += "|     .`   .`~     Made by N3RO     ~`.   '.     |\n";
        welcome_message += "| _.-'     '._   github.com/N3ROO   _.'     '-._ |\n";
        welcome_message += "|                                                |\n";
        welcome_message += "x~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~x\n";
        _console.setText(welcome_message);

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

        attribute_set = sc.addAttribute(attribute_set, StyleConstants.FontFamily, "Lucida Console");
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

        if(new_line){
            appendToPane(_console, "\n"+text, color);
        }else{
            appendToPane(_console, text, color);
        }

        // auto _scroll
        _console.validate();
        _scroll.getVerticalScrollBar().setValue(_scroll.getVerticalScrollBar().getMaximum());
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
                        entry.getKey().setText(value_option.getName() + " [" + Integer.toString(value_option.getVal()) + "]");
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
