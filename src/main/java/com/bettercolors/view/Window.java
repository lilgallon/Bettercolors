package com.bettercolors.view;

import com.bettercolors.io.SettingsUtils;
import com.bettercolors.modules.Module;
import com.bettercolors.modules.options.Option;
import com.bettercolors.modules.options.ToggleOption;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

public class Window extends AbstractWindow{

    private ArrayList<Module> _modules;

    private ArrayList<JCheckBox> _checkboxes;

    public Window(String title, ArrayList<Module> modules) {
        super(title, 450, 600);
        _modules = modules;
        _checkboxes = new ArrayList<>();

        JPanel global_grid = new JPanel();
        global_grid.setLayout(new GridLayout(3, 0));

        setupModulesActivationStatus(global_grid);

        getContentPane().add(global_grid);
        pack();
        super.update();
    }

    private void setupModulesActivationStatus(JPanel global_grid){
        // Setup grid
        JPanel activation_grid = new JPanel();
        activation_grid.setLayout(new GridLayout(_modules.size() / 2, 2));
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
            _checkboxes.add(checkBox);
            // Put checkboxes on grid
            activation_grid.add(checkBox);
        }
        global_grid.add(activation_grid);
    }

    public void synchronizeComponents(){
        for(JCheckBox checkbox : _checkboxes){
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
        repaint();
    }


}
