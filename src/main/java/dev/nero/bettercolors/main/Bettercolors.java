/*
 * Copyright 2018-2020 Bettercolors Contributors (https://github.com/N3ROO/Bettercolors)
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

package dev.nero.bettercolors.main;

import dev.nero.bettercolors.io.Filer;
import dev.nero.bettercolors.io.SettingsUtils;
import dev.nero.bettercolors.modules.*;
import dev.nero.bettercolors.modules.options.Option;
import dev.nero.bettercolors.modules.options.ToggleOption;
import dev.nero.bettercolors.version.Version;
import dev.nero.bettercolors.view.Window;
import mdlaf.MaterialLookAndFeel;
import mdlaf.themes.JMarsDarkTheme;
import mdlaf.themes.MaterialLiteTheme;
import mdlaf.themes.MaterialOceanicTheme;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Mod(modid = Reference.MOD_ID,
	 name = Reference.NAME,
	 version = Reference.VERSION,
	 acceptedMinecraftVersions = Reference.ACCEPTED_VERSIONS)

public class Bettercolors {

    private final static ArrayList<Option> DEFAULT_ACTIVATION_STATUS;
    static{
        DEFAULT_ACTIVATION_STATUS = new ArrayList<>();
        DEFAULT_ACTIVATION_STATUS.add(new ToggleOption("", AimAssistance.class.getSimpleName(), true));
        DEFAULT_ACTIVATION_STATUS.add(new ToggleOption("", ClickAssistance.class.getSimpleName(), true));
        DEFAULT_ACTIVATION_STATUS.add(new ToggleOption("", AutoSprint.class.getSimpleName(), true));
        DEFAULT_ACTIVATION_STATUS.add(new ToggleOption("", AutoSword.class.getSimpleName(), true));
    }

    private ArrayList<Module> modules;
    private Window window;

    // Used to manage key events
    private Map<String, Boolean> keysManager;
    private final String WINDOW = "windowGUI";

    /**
     * Called when forge needs to init our mod. This is the first "function" called.
     */
    @EventHandler
	public void Init(FMLInitializationEvent event)
	{
        // Forge event registering. We will use annotations to tell forge that we have some functions that need to be
        // called in specific contexts. This code says that forge has to look into this class to find these functions.
		MinecraftForge.EVENT_BUS.register(this);
		// This is a workaround to make the function clientTick work
        FMLCommonHandler.instance().bus().register(this);

        // It tells swing (the "library" that handles the GUI) to use antialiasing to render fonts so that it looks
        // smooth
        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext", "true");

        // Now that we initialized all the technical stuff, we can initialize our mod.
        this.initializeMod();

        // Everything is done. Now, the rest of the code will be run once that one of the above event is ran by forge
        // itself.
	}

    /**
     * It initializes everything related to the mod
     */
    private void initializeMod(){
        // We need to load the settings of the current user. First, we need to figure out what settings file the user
        // is currently using
        Map<String, String> option = new HashMap<>();

        // We need to store what settings file is used by the user. That option is called "settings_file", and the
        // default settings file used is called "default" (see: SettingsUtils.SETTINGS_FILENAME)
        option.put("settings_file", SettingsUtils.SETTINGS_FILENAME);

        // If the file does not exist, it means that it is the first time that we run the mod. In that case, we create
        // the file that contains the settings filename that is used by the user. The parameter only_absents=true means
        // that we will only write if the option is not in the file.
        Filer filer = new Filer("_bc_settingsfile");
        filer.write(option, true);

        // Now that we are sure that the file containing the settings file used by the user exists, we can load it.
        // We update the settings utils static variable to the current used settings file
        SettingsUtils.SETTINGS_FILENAME = filer.read("settings_file");

        // Alright, now that we know what settings file to read, we can read it. We will load and store everything in
        // the options variable.
        Map<String, String> options = SettingsUtils.getOptions();

        // If we could not read the options (settings), it means that the file does not exist. In that case, we will
        // create a new file with all the default parameter of each module.
        if (options == null) {
            ArrayList<ArrayList<Option>> modules_options = new ArrayList<>();
            modules_options.add(AimAssistance.getDefaultOptions());
            modules_options.add(ClickAssistance.getDefaultOptions());
            modules_options.add(DEFAULT_ACTIVATION_STATUS);
            // false means that it will override what's written in that file if it exists. If it does not exist, it will
            // simply create it with the given options.
            SettingsUtils.setOptions(modules_options, false);
        }

        // Now that we are sure that all the options have been loaded, we will send them to our modules while
        // initializing them.
        options = SettingsUtils.getOptions();

        // Mods initialisation
        int KEY_HOME = Keyboard.KEY_HOME;
        int KEY_PAGE_UP = 201;

        this.modules = new ArrayList<>();
        this.modules.add(
                new AimAssistance(
                        KEY_HOME,                                                      // Key used to toggle it
                        Boolean.parseBoolean(options.get(AimAssistance.class.getSimpleName())), // Is it turned on?
                        options                                                                 // Settings
                )
        );

        this.modules.add(
                new ClickAssistance(
                        KEY_PAGE_UP, // 201 = page up
                        Boolean.parseBoolean(options.get(ClickAssistance.class.getSimpleName())),
                        options
                )
        );

        this.modules.add(
                new AutoSprint(
                        -1,
                        Boolean.parseBoolean(options.get(AutoSprint.class.getSimpleName()))
                )
        );

        this.modules.add(
                new AutoSword(
                        -1,
                        Boolean.parseBoolean(options.get(AutoSword.class.getSimpleName()))
                )
        );

        // Now that the modules are created, we need to initialize the GUI
        // But we need to know what key is used to initialize the GUI. We will try to read it, if we can't, it means
        // that it is not in the settings file. In that case, we need to append that to the settings file.

        try {
            Window.TOGGLE_KEY = Integer.parseInt(SettingsUtils.getOption(Window.TOGGLE_KEY_OPTION));
        } catch (Exception ignored) {
            // We are here because the setting does not exist yet (the user never updated the GUI toggle key)
            SettingsUtils.setOption(Window.TOGGLE_KEY_OPTION, Integer.toString(Window.TOGGLE_KEY));
        }

        // This variable will be shown in the GUI to say what key is currently used to toggle it
        if (Keyboard.getKeyName(Window.TOGGLE_KEY).equals("")) {
            Window.TOGGLE_KEY_NAME =  "code: " + Window.TOGGLE_KEY;
        } else {
            Window.TOGGLE_KEY_NAME = Keyboard.getKeyName(Window.TOGGLE_KEY) + " code: " + Window.TOGGLE_KEY;
        }

        // Used to keep tracks of key events
        this.keysManager = new HashMap<>();
        for(Module module : this.modules){
            this.keysManager.put(module.getClass().getSimpleName(), false);
        }
        this.keysManager.put(WINDOW, false);

        // We are almost done. We need to initialize everything related to the theme here.
        // We load the default swing theme first, and we store it in "defaultLookAndFeel".
        Window.defaultLookAndFeel = UIManager.getLookAndFeel();
        try {
            String theme = SettingsUtils.getOption(Window.THEME_OPTION);

            if (theme != null) {
                try {
                    switch (theme) {
                        case Window.THEME_DEFAULT:
                            UIManager.setLookAndFeel(Window.defaultLookAndFeel);
                            Window.selectedTheme = Window.THEME_DEFAULT;
                            break;
                        case Window.THEME_MATERIAL_LIGHT:
                            UIManager.setLookAndFeel(new MaterialLookAndFeel());
                            MaterialLookAndFeel.changeTheme(new MaterialLiteTheme());
                            Window.selectedTheme = Window.THEME_MATERIAL_LIGHT;
                            break;
                        case Window.THEME_MATERIAL_OCEANIC:
                            UIManager.setLookAndFeel(new MaterialLookAndFeel());
                            MaterialLookAndFeel.changeTheme(new MaterialOceanicTheme());
                            Window.selectedTheme = Window.THEME_MATERIAL_OCEANIC;
                            break;
                        case Window.THEME_MATERIAL_GOLD:
                            UIManager.setLookAndFeel(new MaterialLookAndFeel());
                            MaterialLookAndFeel.changeTheme(new JMarsDarkTheme());
                            Window.selectedTheme = Window.THEME_MATERIAL_GOLD;
                            break;
                    }
                } catch (Exception e) {
                    // Probably an issue with the library used. It should not happen.
                    System.out.println("The following error may be coming from the library used to theme the GUI");
                    e.printStackTrace();
                }
            }
        } catch (Exception ignored) {
            // We are here because the option does not exist yet (the user never updated the GUI toggle key)
            // We won't do anything here because the theme will be added to the settings file only when the user selects
            // a theme.
        }

        // We have everything, we can finally create the GUI
        this.window = new Window(
                "Bettercolors " + Reference.VERSION,
                this.modules,
                new Version(
                        Reference.MAIN_MC_VERSION,
                        Integer.parseInt(Reference.VERSION.split("\\.")[0]),
                        Integer.parseInt(Reference.VERSION.split("\\.")[1]),
                        Integer.parseInt(Reference.VERSION.split("\\.")[2]),
                        "")
        );
    }

	@SubscribeEvent
	public void onClientTickEvent(ClientTickEvent event){
        for(Module mod : this.modules){
            mod.updateKeyHandler();
            if(mod.getToggleKey() != -1) {
                if (Keyboard.isKeyDown(mod.getToggleKey())) {
                    this.keysManager.replace(mod.getClass().getSimpleName(), true);
                } else if (this.keysManager.get(mod.getClass().getSimpleName())) {
                    // KEY RELEASED !
                    mod.toggle();
                    this.window.synchronizeComponents();
                    SettingsUtils.setOption(mod.getClass().getSimpleName(), Boolean.toString(mod.isActivated()));
                    this.keysManager.replace(mod.getClass().getSimpleName(), false);
                }
            }
        }

        if(Keyboard.isKeyDown(Window.TOGGLE_KEY)){
            this.keysManager.replace(WINDOW, true);
        }else if(this.keysManager.get(WINDOW)){
            this.keysManager.replace(WINDOW, false);
            this.window.toggle();
        }
	}

	@SubscribeEvent
	public void clientTick(final TickEvent event){
        for(Module mod : this.modules){
            if(mod.isActivated()){
                mod.update();
            }
        }
	}
}
