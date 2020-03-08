package dev.nero.bettercolors.main;

import dev.nero.bettercolors.io.SettingsUtils;
import dev.nero.bettercolors.modules.*;
import dev.nero.bettercolors.modules.options.Option;
import dev.nero.bettercolors.modules.options.ToggleOption;
import dev.nero.bettercolors.view.Window;
import mdlaf.MaterialLookAndFeel;
import mdlaf.themes.JMarsDarkTheme;
import mdlaf.themes.MaterialLiteTheme;
import mdlaf.themes.MaterialOceanicTheme;
import mdlaf.themes.MaterialTheme;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@Mod(Reference.MOD_ID)
public class Bettercolors {

    public final static String URL_PROBLEM = "Url problem (please contact developer).";
    public final static String INTERNET_PROBLEM = "No internet connection. :(";
    public final static String NO_VERSION_FOUND = "No version found.";
    public final static String DOWNLOAD_URL = "https://github.com/N3ROO/Bettercolors/releases/latest";

    public final static String TOGGLE_KEY_OPTION = "toggle_key";
    public static String TOGGLE_KEY_NAME = "insert code: 260";
    public static int TOGGLE_KEY = GLFW.GLFW_KEY_INSERT;

    public final static String THEME_OPTION = "theme";

    private final static ArrayList<Option> DEFAULT_ACTIVATION_STATUS;
    static{
        DEFAULT_ACTIVATION_STATUS = new ArrayList<>();
        DEFAULT_ACTIVATION_STATUS.add(new ToggleOption("", AimAssistance.class.getSimpleName(), true));
        DEFAULT_ACTIVATION_STATUS.add(new ToggleOption("", ClickAssistance.class.getSimpleName(), true));
        DEFAULT_ACTIVATION_STATUS.add(new ToggleOption("", AutoSprint.class.getSimpleName(), true));
        DEFAULT_ACTIVATION_STATUS.add(new ToggleOption("", AutoSword.class.getSimpleName(), true));
    }

    private ArrayList<Module> _modules;
    private final String WINDOW = "windowGUI";
    private Window _window;

    public Bettercolors(){
        // Forge event registering
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onKey);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientTickEvent);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientTick);
        MinecraftForge.EVENT_BUS.register(this);

        // This new forge version does not like swing ;(
        System.setProperty("java.awt.headless", "false");

        // Antialiasing font
        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext", "true");

        // Mod init
        initMod();
    }

    private void initMod(){
        // Settings management
        Map<String, String> options = SettingsUtils.getOptions();
        ArrayList<ArrayList<Option>> modules_options = new ArrayList<>();
        modules_options.add(AimAssistance.getDefaultOptions());
        modules_options.add(ClickAssistance.getDefaultOptions());
        modules_options.add(DEFAULT_ACTIVATION_STATUS);
        // There is no settings file, we need to create it, otherwise we only add the settings that are not already
        // in the settings file (can happen after updating the mod to a newer version)
        SettingsUtils.setOptions(modules_options, options != null);
        options = SettingsUtils.getOptions();

        // Mods initialisation
        _modules = new ArrayList<>();
        _modules.add(new AimAssistance("Aim assistance", GLFW.GLFW_KEY_HOME, Boolean.parseBoolean(options.get(AimAssistance.class.getSimpleName())), options, "aim_symbol.png"));
        _modules.add(new ClickAssistance("Click assistance", GLFW.GLFW_KEY_PAGE_UP, Boolean.parseBoolean(options.get(ClickAssistance.class.getSimpleName())), options, "click_symbol.png"));
        _modules.add(new AutoSprint("Auto sprint", -1, Boolean.parseBoolean(options.get(AutoSprint.class.getSimpleName())), "sprint_symbol.png"));
        _modules.add(new AutoSword("Auto sword", -1, Boolean.parseBoolean(options.get(AutoSword.class.getSimpleName())), "sword_symbol.png"));

        // Gui toggle key
        try {
            String gui_toggle_key = SettingsUtils.getOption(TOGGLE_KEY_OPTION);
            Bettercolors.TOGGLE_KEY = Integer.parseInt(gui_toggle_key);
        } catch (Exception ignored) { } // We are here because the setting does not exist yet (the user never updated the GUI toggle key)

        Window.defaultLookAndFeel = UIManager.getLookAndFeel();

        try {
            String theme = SettingsUtils.getOption(THEME_OPTION);

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
                e.printStackTrace();
            }
        } catch (Exception ignored) { } // We are here because the setting does not exist yet (the user never updated the GUI toggle key)

        // AbstractWindow initialisation
        _window = new Window("Bettercolors " + Reference.VERSION, _modules, getVersionInformation());
    }

    @SubscribeEvent
    public void onKey(final InputEvent.KeyInputEvent event) {

        for(Module mod : _modules){
            if(mod.getToggleKey() != -1) {
                if (event.getKey() == mod.getToggleKey() && event.getAction() == GLFW.GLFW_RELEASE) {
                    mod.toggle();
                    _window.synchronizeComponents();
                    SettingsUtils.setOption(mod.getClass().getSimpleName(), Boolean.toString(mod.isActivated()));
                }
            }
        }

        if(event.getKey() == TOGGLE_KEY && event.getAction() == GLFW.GLFW_RELEASE){
            _window.toggle();
        }
    }

	@SubscribeEvent
    public void onClientTickEvent(ClientTickEvent event){

	}

    @SubscribeEvent
    public void clientTick(final TickEvent event){
        for(Module mod : _modules){
            mod.updateKeyHandler();
            if(mod.isActivated()){
                mod.update();
            }
        }
	}

    /**
     * @return the last version tag from the github release page (without the MC version in it).
     */
	private String[] getVersionInformation(){
        final String MC_PREFIX = "-MC";
        String last_version = "";
        String changelog = "";

        try{
            // Retrieve JSON
            URL url = new URL("https://api.github.com/repos/n3roo/bettercolors/releases");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String json = in.lines().collect(Collectors.joining());
            in.close();

            // Get last version from JSON
            String[] tags = json.split("\"tag_name\"");
            String[] bodies = json.split("\"body\"");

            int i = 0;
            boolean found = false;
            while(i < tags.length && !found){
                last_version = tags[i].split("\"")[1];
                if(last_version.endsWith(MC_PREFIX + Reference.MAIN_MC_VERSION)){
                    found = true;
                }else{
                    i ++;
                }
            }

            if(!found){
                return new String[]{NO_VERSION_FOUND, ""};
            }else{
                last_version = last_version.replace(MC_PREFIX + Reference.MAIN_MC_VERSION, "");
            }
        } catch (MalformedURLException e){
            return new String[]{URL_PROBLEM, ""};
        } catch (IOException e){
            return new String[]{INTERNET_PROBLEM, ""};
        }

        return new String[]{last_version, changelog};
    }

    /**
     * It compares the two given versions, and return the difference :
     * int[a, b, c, d]
     * - a is the major version dif,
     * - b is the minor version dif,
     * - c is the patch version dif,
     * - d is the beta version dif.
     * If error, returns null.
     * @param current_version current version
     * @param last_version last version
     * @return the version difference
     */
    public static int[] compareVersions(String current_version, String last_version){
        int[] diff = {0, 0, 0, 0};

        String[] current_version_split = current_version.split("\\.");
        String[] last_version_split = last_version.split("\\.");

        if(current_version_split.length == 3 && last_version_split.length == 3) {
            diff[0] = Integer.parseInt(last_version_split[0]) -  Integer.parseInt(current_version_split[0]);
            diff[1] = Integer.parseInt(last_version_split[1]) -  Integer.parseInt(current_version_split[1]);
            diff[2] = Integer.parseInt(last_version_split[2].split("-")[0]) -  Integer.parseInt(current_version_split[2].split("-")[0]);

            int current_beta_number = current_version_split[2].split("-").length == 2 ? Integer.parseInt(current_version_split[2].split("-")[1].replace("b", "")) : 0;
            int last_beta_number = last_version_split[2].split("-").length == 2 ? Integer.parseInt(last_version_split[2].split("-")[1].replace("b", "")) : 0;
            diff[3] = last_beta_number - current_beta_number;
        } else {
            System.out.println("Error when comparing versions : expected a version format maj.min.patch(-bnumber), but received :");
            System.out.println("[" + current_version + "] and [" + last_version + "]");
            diff = null;
        }

        return diff;
    }
}
