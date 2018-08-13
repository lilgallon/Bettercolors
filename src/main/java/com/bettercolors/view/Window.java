package com.bettercolors.view;

import com.bettercolors.io.SettingsUtils;
import com.bettercolors.modules.Module;
import com.bettercolors.modules.options.Option;
import com.bettercolors.modules.options.ToggleOption;
import com.bettercolors.modules.options.ValueOption;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Window extends AbstractWindow{

    private ArrayList<Module> _modules;

    private ArrayList<JCheckBox> _checkboxes_activation;
    private ArrayList<JCheckBox> _checkboxes_modules;
    private Map<JLabel, JSlider> _sliders_modules;

    public Window(String title, ArrayList<Module> modules) {
        super(title, 450, 600);
        _modules = modules;
        _checkboxes_activation = new ArrayList<>();
        _checkboxes_modules = new ArrayList<>();
        _sliders_modules = new HashMap<>();

        JPanel global_grid = new JPanel();
        //global_grid.setLayout(new GridLayout(modules.size() + 2, 0)); // +2 -> + Module activation & + console
        global_grid.setLayout(new BorderLayout());

        setupModulesActivationStatus(global_grid);
        setupModulesOptions(global_grid);

        getContentPane().add(global_grid);
        pack();
        super.update();
    }

    private void setupModulesActivationStatus(JPanel global_grid){
        // Setup grid
        JPanel activation_grid = new JPanel();
        activation_grid.setLayout(new GridLayout((int)Math.ceil((double)_modules.size() / 2d), 2));
        activation_grid.setBorder(new TitledBorder(new EtchedBorder(), "Modules"));

        for(Module module : _modules) {
            // Setup checkboxes
            final JCheckBox checkBox = new JCheckBox(module.getClass().getSimpleName());
            checkBox.setSelected(module.isActivated());
            checkBox.addActionListener(e -> {
                module.toggle();
                SettingsUtils.setOption(module.getClass().getSimpleName(), Boolean.toString(module.isActivated()));
                checkBox.setSelected(module.isActivated());
                repaint();
            });
            _checkboxes_activation.add(checkBox);
            // Put checkboxes on grid
            activation_grid.add(checkBox);
        }
        global_grid.add(activation_grid, "North");
    }

    private void setupModulesOptions(JPanel global_grid){
        JTabbedPane tabbedPane = new JTabbedPane();

        for(Module module : _modules){
            JPanel module_options_panel = new JPanel();
            module_options_panel.setLayout(new BorderLayout());
            module_options_panel.setBorder(new TitledBorder(new EtchedBorder(), module.getName()));

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
                    _checkboxes_modules.add(checkBox);
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
                    slider.setMinorTickSpacing(value_option.getMinortTickSpacing());
                    slider.setMajorTickSpacing(value_option.getMajortTickSpacing());
                    slider.setPaintTicks(true);
                    slider.addChangeListener(e -> {
                        value_option.setVal(slider.getValue());
                        label.setText(value_option.getName() + " [" + Integer.toString(value_option.getVal()) + "]");
                        repaint();
                    });
                    _sliders_modules.put(label, slider);
                    sliders_grid.add(label);
                    sliders_grid.add(slider);
                }

                module_options_panel.add(sliders_grid, "Center");
            }
            tabbedPane.addTab(module.getName(), module_options_panel);
        }
        global_grid.add(tabbedPane);
    }


    public void synchronizeComponents(){
        for(JCheckBox checkbox : _checkboxes_activation){
            boolean found = false;
            int i = 0;
            while(!found && i < _modules.size()){
                if(checkbox.getText().equalsIgnoreCase(_modules.get(i).getClass().getSimpleName())){
                    checkbox.setSelected((_modules.get(i)).isActivated());
                    found = true;
                }else{
                    ++ i;
                }
            }
        }

        for(Module module : _modules){
            ArrayList<ToggleOption> toggle_options = Option.getToggleOptions(module.getOptions());
            for(ToggleOption toggle_option : toggle_options){
                boolean found = false;
                int i = 0;
                while(!found && i < _checkboxes_modules.size()){
                    if(_checkboxes_modules.get(i).getText().equalsIgnoreCase(toggle_option.getName())){
                        _checkboxes_modules.get(i).setSelected(toggle_option.isActivated());
                        found = true;
                    }else{
                        ++ i;
                    }
                }
            }
        }

        for(Module module : _modules){
            ArrayList<ValueOption> value_options = Option.getValueOptions(module.getOptions());
            for(ValueOption value_option : value_options){
                for(Map.Entry<JLabel, JSlider> entry : _sliders_modules.entrySet()){
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


}
