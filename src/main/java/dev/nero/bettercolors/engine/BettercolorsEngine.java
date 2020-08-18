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

package dev.nero.bettercolors.engine;

import dev.nero.bettercolors.engine.module.Module;
import dev.nero.bettercolors.engine.io.PropertiesFiler;
import dev.nero.bettercolors.engine.io.SettingsUtils;
import dev.nero.bettercolors.engine.utils.Friends;
import dev.nero.bettercolors.engine.utils.KeyName;
import dev.nero.bettercolors.engine.utils.KeysManager;
import dev.nero.bettercolors.engine.option.Option;
import dev.nero.bettercolors.engine.option.ToggleOption;
import dev.nero.bettercolors.engine.version.Version;
import dev.nero.bettercolors.engine.view.Window;
import mdlaf.MaterialLookAndFeel;
import mdlaf.themes.JMarsDarkTheme;
import mdlaf.themes.MaterialLiteTheme;
import mdlaf.themes.MaterialOceanicTheme;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Your main class (the one with @Mod annotation) needs to instantiate this one.
 *
 * Stuff to know:
 * - Make sure to write System.setProperty("java.awt.headless", "false"); before calling init() if you're using MC
 * >= 1.13
 *
 * Functions to call:
 * - init(...) during forge init
 * - onKeyEvent(code, pressed) or onKeyReleased if you can't use the first function
 * - update() to update all the modules and components
 */
public class BettercolorsEngine {

    public static boolean VERBOSE = false;
    public static final String DEBUG_OPTION = "debug";
    public static BettercolorsEngine instance;

    // Used to know if the mod is being built with the new forge api or not (>=1.13 is new, <1.13 is old)
    public enum MC_INPUTS { NEW, OLD }

    public static class Key {
        public Key(int code, String name) { this.code = code; this.name = name; }
        public int code;
        public String name;
    }

    public static class IntAndBoolean {
        public IntAndBoolean(int i, boolean b) { this.i = i; this.b = b; }
        public int i;
        public boolean b;
    }

    private HashMap<Class<? extends Module>, IntAndBoolean> modulesAndDetails;

    protected ArrayList<Module> modules;
    private Window window;

    public BettercolorsEngine() {
        instance = this;
        System.setProperty("java.awt.headless", "false");
    }

    /**
     * It initializes everything
     *
     * -> Needs to be called first after or before registering forge event
     *
     * @param windowTitle the title of the window (hud)
     * @param modVersion the mod version (ex: 6.2.0 for minecraft 1.8.9)
     * @param versionSuffix the mod version prefix (ex: fa for fabric, fo for forge, or even nothing). It will be
     *                      added to the end of the version to find the github tag. Ex: 6.2.0-MC1.8.9fa (fa here)
     * @param mcVersion the minecraft version (ex: 1.8.9)
     * @param releasesApiUrl the github releases API link (ex: https://api.github.com/repos/n3roo/bettercolors/releases)
     * @param releasesDownloadUrl the download page (ex: https://github.com/n3roo/bettercolors/releases)
     * @param issuesTrackerUrl the issues tracker url
     * @param modulesAndDetails the modules with their default state (turned on or off: boolean) and their toggle key
     *                         (int), -1 if they haven't any toggle key.
     * @param keyToToggleWindow the key to toggle the window
     * @param keyNameFunc a function that takes a key code and returns its string representation
     */
    public void init(
            String windowTitle,
            String modVersion,
            String versionSuffix,
            String mcVersion,
            String releasesApiUrl,
            String releasesDownloadUrl,
            String issuesTrackerUrl,
            HashMap<Class<? extends Module>, IntAndBoolean> modulesAndDetails,
            Key keyToToggleWindow,
            KeyName keyNameFunc
        )
    {
        Reference.MOD_VERSION = new Version(
                mcVersion,
                Integer.parseInt(modVersion.split("\\.")[0]),
                Integer.parseInt(modVersion.split("\\.")[1]),
                Integer.parseInt(modVersion.split("\\.")[2]),
                modVersion.split("b").length > 1 ? Integer.parseInt(modVersion.split("b")[1]) : 0,
                ""
        );
        Reference.VERSION_SUFFIX = versionSuffix;
        Reference.MC_VERSION = mcVersion;
        Reference.RELEASES_API_URL = releasesApiUrl;
        Reference.RELEASES_DOWNLOAD_URL = releasesDownloadUrl;
        Reference.ISSUES_TRACKER_URL = issuesTrackerUrl;

        this.modulesAndDetails = modulesAndDetails;

        // Used to know if the mod is being built with the new forge api or not (>=1.13 is new, <1.13 is old)
        Reference.MC_INPUTS_VERSION = (
                (
                    new Version(
                            Integer.parseInt(mcVersion.split("\\.")[0]),
                            Integer.parseInt(mcVersion.split("\\.")[1]),
                            Integer.parseInt(mcVersion.split("\\.")[2])
                    )
                ).compareWith(
                    new Version(
                            1,
                            13,
                            0
                    )
                ) == Version.VersionDiff.OUTDATED) ? MC_INPUTS.OLD : MC_INPUTS.NEW;

        if (VERBOSE) {
            System.out.println((Reference.MC_INPUTS_VERSION == MC_INPUTS.NEW ? "New " : "Old ") + "MC inputs detected");
        }

        // Update the window toggle key with the given one
        Window.TOGGLE_KEY = keyToToggleWindow.code;
        Window.TOGGLE_KEY_NAME = keyToToggleWindow.name;

        // It tells swing (the "library" that handles the GUI) to use antialiasing to render fonts so that it looks
        // smooth
        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext", "true");

        // Does not work here, needs to be in the main class for some reason
        // if (Reference.FORGE_API == FORGE.NEW) System.setProperty("java.awt.headless", "false");

        // We need to load the settings of the current user. First, we need to figure out what settings file the user
        // is currently using
        Map<String, String> option = new HashMap<>();

        // We need to store what settings file is used by the user. That option is called "settings_file", and the
        // default settings file used is called "default" (see: SettingsUtils.SETTINGS_FILENAME)
        option.put("settings_file", SettingsUtils.SETTINGS_FILENAME);

        // If the file does not exist, it means that it is the first time that we run the mod. In that case, we create
        // the file that contains the settings filename that is used by the user. The parameter only_absents=true means
        // that we will only write if the option is not in the file.
        PropertiesFiler propertiesFiler = new PropertiesFiler("_bc_settingsfile");
        propertiesFiler.write(option, true);

        // Now that we are sure that the file containing the settings file used by the user exists, we can load it.
        // We update the settings utils static variable to the current used settings file
        SettingsUtils.SETTINGS_FILENAME = propertiesFiler.read("settings_file");

        // Alright, now that we know what settings file to read, we can read it. We will load and store everything in
        // the options variable.
        Map<String, String> options = SettingsUtils.getOptions();

        if (options == null) {
            Window.INFO("[~] Could not find " + SettingsUtils.SETTINGS_FILENAME + ", creating it");
            // The file does not exist, so we will create it
            SettingsUtils.setOptions(this.getDefaultOptions(), false);
        } else {
            Window.INFO("[+] Loaded " + SettingsUtils.SETTINGS_FILENAME);
            // The file already exists so we will only save what's missing
            SettingsUtils.setOptions(this.getDefaultOptions(), true);
        }

        // Now that we are sure that all the options have been loaded, we will send them to our modules while
        // initializing them.
        options = SettingsUtils.getOptions();

        // Debug mode
        BettercolorsEngine.VERBOSE = Boolean.parseBoolean(options.get(DEBUG_OPTION));

        // The last thing to do with external files is to load all the friends
        Friends.loadFriends();

        // Mods initialisation
        this.modules = new ArrayList<>();

        for (Map.Entry<Class<? extends Module>, IntAndBoolean> entry : modulesAndDetails.entrySet()) {
            Class<Module> moduleClass = (Class<Module>) entry.getKey();
            int toggleKey = entry.getValue().i;

            try {
                Module module = moduleClass.getDeclaredConstructor(
                        Integer.class,
                        Boolean.class,
                        Map.class).newInstance(
                        toggleKey,
                        Boolean.parseBoolean(options.get(moduleClass.getSimpleName())),
                        options
                );

                this.modules.add(module);
                if (VERBOSE) System.out.println("It worked, " + moduleClass.getSimpleName() + " created!");
                Window.INFO("[+] " + moduleClass.getSimpleName() + " initialized");

            } catch (InstantiationException
                    | IllegalAccessException
                    | InvocationTargetException
                    | NoSuchMethodException e1) {

                if (VERBOSE) e1.printStackTrace();

                if (e1 instanceof InvocationTargetException) {
                    Window.ERROR("Could not instantiate " + moduleClass.getSimpleName());
                    if (VERBOSE)
                        System.out.println(
                                "Exception caught during " + moduleClass.getSimpleName() + " creation"
                        );
                } else {
                    if (VERBOSE)
                        System.out.println(
                                "Failed to instantiate " + moduleClass.getSimpleName() + " trying with other parameters"
                        );
                }

                try {
                    Module module = moduleClass.getDeclaredConstructor(
                            Integer.class,
                            Boolean.class).newInstance(
                            toggleKey,
                            Boolean.parseBoolean(options.get(moduleClass.getSimpleName()))
                    );

                    this.modules.add(module);
                    if (VERBOSE) System.out.println("It worked, " + moduleClass.getSimpleName() + " created!");
                    Window.INFO("[+] " + moduleClass.getSimpleName() + " initialized");

                } catch (InstantiationException
                        | IllegalAccessException
                        | InvocationTargetException
                        | NoSuchMethodException e2) {

                    if (VERBOSE) e2.printStackTrace();

                    if (e2 instanceof InvocationTargetException) {
                        if (VERBOSE)
                            System.out.println(
                                    "Exception caught during " + moduleClass.getSimpleName() + " creation"
                            );
                    } else {
                        if (VERBOSE)
                            System.out.println(
                                    "Failed to instantiate " + moduleClass.getSimpleName() + " (second try)"
                            );
                    }

                    Window.ERROR("Could not instantiate " + moduleClass.getSimpleName() + " (second try)");
                    Window.ERROR("^ It should not happen. Try to rename the config file and restart your client " +
                            "so that a new file will be created");

                    if (VERBOSE) {
                        System.out.println("1: Make sure that your module constructor has object and not type (ex: Integer and not int)");
                        System.out.println("2: Make sure that your module constructor matches one from the following list:");
                        System.out.println("- (Integer, Boolean, Map)");
                        System.out.println("- (Integer, Boolean)");
                    }
                }
            }
        }

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
        Window.TOGGLE_KEY_NAME =  "code: " + Window.TOGGLE_KEY;

        // Now we do the same thing but for the modules
        for (Module module : modules) {
            String optionName = module.getPrefix() + "_toggle_key";
            try {
                module.setToggleKey(Integer.parseInt(options.get(optionName)));
            } catch (Exception ignored) {
                SettingsUtils.setOption(optionName, Integer.toString(module.getToggleKey()));
            }
        }

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
                    Window.ERROR("Issue with the MaterialUI themes");
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
                windowTitle,
                this.modules,
                Reference.MOD_VERSION,
                keyNameFunc
        );
    }

    /**
     * Works with any version of forge as long as you can provide the right parameters.
     * You can then use KeyManager.getKeyState(code) to get the state of a given key.
     *
     * You need to call this function every time a key event occurs.
     *
     * @param code key code
     * @param pressed true if the key is pressed
     */
    public void keyEvent(int code, boolean pressed) {
        if (KeysManager.getKeyState(code) == KeysManager.KeyState.RELEASED && pressed) {
            // If it's pressed and it was released last time we recorded it, then it has just been pressed
            KeysManager.setKeyState(code, KeysManager.KeyState.JUST_PRESSED);
        } else if (KeysManager.getKeyState(code) == KeysManager.KeyState.JUST_PRESSED && pressed) {
            // If it's pressed and it was just pressed last time we recorded it, then it is being pressed
            KeysManager.setKeyState(code, KeysManager.KeyState.BEING_PRESSED);
        } else if (
                (KeysManager.getKeyState(code) == KeysManager.KeyState.JUST_PRESSED && !pressed) |
                (KeysManager.getKeyState(code) == KeysManager.KeyState.BEING_PRESSED && !pressed)) {
            // If it's released and it was pressed last time we recorded it, then it is just released
            KeysManager.setKeyState(code, KeysManager.KeyState.JUST_RELEASED);
            this.keyReleased(code);
        } else if (KeysManager.getKeyState(code) == KeysManager.KeyState.JUST_RELEASED && !pressed) {
            // If it's released and it was just released last time we recorded it, then it is released
            KeysManager.setKeyState(code, KeysManager.KeyState.RELEASED);
        } else if (pressed){
            // The rest, not needed, but more clear with that
            KeysManager.setKeyState(code, KeysManager.KeyState.BEING_PRESSED);
        } else {
            KeysManager.setKeyState(code, KeysManager.KeyState.RELEASED);
        }
    }

    /**
     * !! You should use keyEvent(code, pressed) !!
     * If you use this function, you won't be able to use KeyManager. It could be fine according to your use case.
     *
     * Needs to be called every time a key is RELEASED, and only RELEASED.
     *
     * @param code the library that handles key events is not important, we only keep track of the integer code of keys
     */
    public void keyReleased(int code) {

        for (Module mod : this.modules){
            if (mod.getToggleKey() != -1) { // = if the module has a toggle key
                if (code == mod.getToggleKey()) {
                    // Toggle the module
                    mod.toggle(true);

                    // Synchronize the window's checkboxes
                    this.window.synchronizeComponents();

                    // Save the module's activation in the settings file
                    SettingsUtils.setOption(mod.getClass().getSimpleName(), Boolean.toString(mod.isActivated()));
                }
            }
        }

        if (code == Window.TOGGLE_KEY) {
            this.window.toggle();
        }
    }

    /**
     * This needs to be called each time the screen is rendered. Not a each game tick, because some stuff like aimbots
     * will need an high refresh-rate
     */
    public void update() {
        for(Module mod : this.modules){
            if(mod.isActivated()){
                mod.update();
            }
        }
    }

    /**
     * @param name name of the module to get
     * @return the module with the given name, or null if not found
     */
    public Module getModule(String name) {
        for (Module module : this.modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    /**
     * It toggles the given module, synchronizes the window, and save the changes.
     *
     * @param name name of the module to toggle
     * @param activated true means turn on, false, turn off
     * @return true if succeed, false if the module could not be found
     */
    public boolean toggleModule(String name, boolean activated) {
        Module module = getModule(name);
        if (module != null) {
            module.setActivated(activated);
            this.window.synchronizeComponents();
            SettingsUtils.setOption(module.getClass().getSimpleName(), Boolean.toString(module.isActivated()));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Make sure that BettercolorsEngine#init has been called before
     * @return all the loaded modules
     */
    public ArrayList<Module> getModules() {
        return this.modules;
    }

    /**
     * @return the default options for each modules, and the default options for the mod (debug mode, key to toggle
     * window, ...)
     */
    public ArrayList<ArrayList<Option>> getDefaultOptions() {
        ArrayList<ArrayList<Option>> options = new ArrayList<>();
        ArrayList<Option> specialOptions = new ArrayList<>();

        for (Map.Entry<Class<? extends Module>, IntAndBoolean> entry : this.modulesAndDetails.entrySet()) {
            // We are going to take the default option of each module. The thing is that they're not initialized yet
            // but we have their classes. We will use java's reflection to call their static methods to get their
            // default options

            // First we need to get the class
            Class<Module> moduleClass = (Class<Module>) entry.getKey();

            try {
                // Then, find the static method that we will use
                Method method = moduleClass.getMethod("getDefaultOptions");
                try {
                    // Call (invoke) that method without arguments. null is used to say that it's static.
                    ArrayList<Option> defaultOptions = (ArrayList<Option> ) method.invoke(null);

                    // If that module has default options, then we need to add them to the list of options
                    if (defaultOptions.size() != 0) {
                        // System.out.println("Got default options for " + moduleClass.getSimpleName());
                        options.add(defaultOptions);
                    }
                } catch (IllegalAccessException e) {
                    Window.ERROR(
                            "The engine doesn't have access to default options of " +
                            moduleClass.getSimpleName()
                    );
                } catch (InvocationTargetException e) {
                    Window.ERROR(
                            "Error caught when trying to get default options of " +
                                    moduleClass.getSimpleName()
                    );
                }
            } catch (NoSuchMethodException e) {
                Window.WARN(
                        "Could not find the method getDefaultOptions associated to class " +
                                moduleClass.getSimpleName()
                );

                if (VERBOSE)
                    System.out.println(
                            "If you are the developer you should implement a static method called "
                            + "getDefaultOptions which returns an ArrayList<Option>. Return an empty one"
                            + "if the module hasn't any option"
                    );
            }

            // We also need to add the option that says if the module is activated or not by default
            // We need to load all the modules and turn them on or off according to what's given
            specialOptions.add(new ToggleOption("", entry.getKey().getSimpleName(), entry.getValue().b));
        }

        // Manually add a debug option to the file (false by default)
        ToggleOption debug = new ToggleOption("", DEBUG_OPTION, false);
        specialOptions.add(debug);

        options.add(specialOptions);

        return options;
    }

    /**
     * @return the engine's instance
     */
    public static BettercolorsEngine getInstance() {
        return instance;
    }
}