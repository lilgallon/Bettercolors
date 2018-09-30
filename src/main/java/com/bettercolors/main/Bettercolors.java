package com.bettercolors.main;

import com.bettercolors.io.SettingsUtils;
import com.bettercolors.modules.*;
import com.bettercolors.modules.options.Option;
import com.bettercolors.modules.options.ToggleOption;
import com.bettercolors.view.Window;
import com.google.gson.JsonObject;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

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

    public final static String URL_PROBLEM = "Url problem (please contact developer).";
    public final static String INTERNET_PROBLEM = "No internet connection. :(";
    public final static String DOWNLOAD_URL = "https://github.com/N3ROO/Bettercolors/releases/latest";

    private final static ArrayList<Option> DEFAULT_ACTIVATION_STATUS;
    static{
        DEFAULT_ACTIVATION_STATUS = new ArrayList<>();
        DEFAULT_ACTIVATION_STATUS.add(new ToggleOption(AimAssistance.class.getSimpleName(), true));
        DEFAULT_ACTIVATION_STATUS.add(new ToggleOption(ClickAssistance.class.getSimpleName(), true));
        DEFAULT_ACTIVATION_STATUS.add(new ToggleOption(AutoSprint.class.getSimpleName(), true));
        DEFAULT_ACTIVATION_STATUS.add(new ToggleOption(AutoSword.class.getSimpleName(), true));
    }

    private ArrayList<Module> _modules;
    private Map<String, Boolean> _key_down;
    private final String WINDOW = "windowGUI";
    private Window _window;

    @EventHandler
	public void Init(FMLInitializationEvent event)
	{
	    // Registers the rest
		MinecraftForge.EVENT_BUS.register(this);
		// Registers tick event
        FMLCommonHandler.instance().bus().register(this);

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
        int KEY_HOME = Keyboard.KEY_HOME;
        int KEY_PAGE_UP = 201;
		_modules = new ArrayList<>();
		_modules.add(new AimAssistance("Aim assistance", KEY_HOME, Boolean.parseBoolean(options.get(AimAssistance.class.getSimpleName())), options, "aim_symbol.png"));
        _modules.add(new ClickAssistance("Click assistance", KEY_PAGE_UP, Boolean.parseBoolean(options.get(ClickAssistance.class.getSimpleName())), options, "click_symbol.png"));
		_modules.add(new AutoSprint("Auto sprint", -1, Boolean.parseBoolean(options.get(AutoSprint.class.getSimpleName())), "sprint_symbol.png"));
		_modules.add(new AutoSword("Auto sword", -1, Boolean.parseBoolean(options.get(AutoSword.class.getSimpleName())), "sword_symbol.png"));

		// KeyEvent
        _key_down = new HashMap<>();
        for(Module module : _modules){
            _key_down.put(module.getClass().getSimpleName(), false);
        }
        _key_down.put(WINDOW, false);

		// AbstractWindow initialisation
        _window = new Window("Bettercolors " + Reference.VERSION, _modules, getLastVersion());
	}

	@SubscribeEvent
	public void onClientTickEvent(ClientTickEvent event){
        for(Module mod : _modules){
            if(mod.getToggleKey() != -1) {
                if (Keyboard.isKeyDown(mod.getToggleKey())) {
                    _key_down.replace(mod.getClass().getSimpleName(), true);
                } else if (_key_down.get(mod.getClass().getSimpleName())) {
                    // KEY RELEASED !
                    mod.toggle();
                    _window.synchronizeComponents();
                    SettingsUtils.setOption(mod.getClass().getSimpleName(), Boolean.toString(mod.isActivated()));
                    _key_down.replace(mod.getClass().getSimpleName(), false);
                }
            }
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)){
            _key_down.replace(WINDOW, true);
        }else if(_key_down.get(WINDOW)){
            _key_down.replace(WINDOW, false);
            _window.toggle();
        }
	}

	@SubscribeEvent
	public void clientTick(final TickEvent event){
        for(Module mod : _modules){
            if(mod.isActivated()){
                mod.update();
            }
        }
	}

    /**
     * @return the last version tag from the github release page.
     */
	private String getLastVersion(){
        String last_version;

        try{
            // Retrieve JSON
            URL url = new URL("https://api.github.com/repos/n3roo/bettercolors/releases/latest");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String json = in.lines().collect(Collectors.joining());
            in.close();

            // Get last version from JSON
            last_version = json.split("\"tag_name\"")[1].split("\"")[1];
        } catch (MalformedURLException e){
            return URL_PROBLEM;
        } catch (IOException e){
            return INTERNET_PROBLEM;
        }

        return last_version;
    }
}
