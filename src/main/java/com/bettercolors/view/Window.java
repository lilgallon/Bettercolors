package com.bettercolors.view;

import com.bettercolors.modules.Module;
import com.bettercolors.modules.options.Option;
import com.bettercolors.modules.options.ToggleOption;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

public class Window extends AbstractWindow{

    private ArrayList<Option> _activation_status;
    private ArrayList<Module> _modules;

    public Window(String title, ArrayList<Option> activation_status, ArrayList<Module> modules) {
        super(title, 450, 600);
        _activation_status = activation_status;
        _modules = modules;

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
        activation_grid.setLayout(new GridLayout(_activation_status.size() / 2, 2));
        activation_grid.setBorder(new TitledBorder(new EtchedBorder(), "Modules"));

        for(Option option : _activation_status){
            // Setup checkboxes
            ToggleOption toggleOption = (ToggleOption) option;
            final JCheckBox checkBox = new JCheckBox(option.getName());
            checkBox.setSelected(toggleOption.isActivated());
            checkBox.addActionListener(e -> {
                toggleOption.toggle();
                checkBox.setSelected(toggleOption.isActivated());
                repaint();
            });
            // Put checkboxes on grid
            activation_grid.add(checkBox);
        }
        global_grid.add(activation_grid);
    }


}
