package com.bettercolors.main;

import com.bettercolors.io.SettingsUtils;
import com.bettercolors.modules.AimAssistance;
import com.bettercolors.modules.ClickAssistance;
import com.bettercolors.modules.Module;
import com.bettercolors.modules.options.Option;
import com.bettercolors.modules.options.ToggleOption;
import com.bettercolors.view.Window;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Mod(modid = Reference.MOD_ID,
	 name = Reference.NAME,
	 version = Reference.VERSION,
	 acceptedMinecraftVersions = Reference.ACCEPTED_VERSIONS)

public class Bettercolors {

    private final static ArrayList<Option> DEFAULT_ACTIVATION_STATUS;
    static{
        DEFAULT_ACTIVATION_STATUS = new ArrayList<>();
        DEFAULT_ACTIVATION_STATUS.add(new ToggleOption(AimAssistance.class.getSimpleName(), true));
        DEFAULT_ACTIVATION_STATUS.add(new ToggleOption(ClickAssistance.class.getSimpleName(), true));
    }

    private ArrayList<Option> _activation_status;

    private static int KEY_PAGE_UP = 201;
    private ArrayList<Module> _mods;

    private Map<String, Boolean> _key_down;

    private Window window;

    @EventHandler
	public void Init(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);

		// Settings management
        Map<String, String> options = SettingsUtils.getOptions();
        ArrayList<ArrayList<Option>> modules_options = new ArrayList<>();
        modules_options.add(AimAssistance.getDefaultOptions());
        modules_options.add(ClickAssistance.getDefaultOptions());
        _activation_status = DEFAULT_ACTIVATION_STATUS;
        modules_options.add(_activation_status);
        // There is no settings file, we need to create it, otherwise we only add the settings that are not already
        // in the settings file (can happen after updating the mod to a newer version)
        SettingsUtils.setOptions(modules_options, options != null);
        options = SettingsUtils.getOptions();

		// Mods initialisation
		_mods = new ArrayList<>();
		_mods.add(new AimAssistance("Aim assistance", Keyboard.KEY_HOME, Boolean.parseBoolean(options.get(AimAssistance.class.getSimpleName())), options));
		_mods.add(new ClickAssistance("Click assistance", KEY_PAGE_UP, Boolean.parseBoolean(options.get(ClickAssistance.class.getSimpleName())), options));

		// KeyEvent
        _key_down = new HashMap<>();
        _key_down.put(AimAssistance.class.getSimpleName(), false);
        _key_down.put(ClickAssistance.class.getSimpleName(), false);


		// AbstractWindow initialisation
        window = new Window("Bettercolors 6", _activation_status, _mods);

        // Version check
        // todo
	}

	@SubscribeEvent
	public void onClientTickEvent(ClientTickEvent event){
        // todo : AbstractWindow updating acc. to mods
        for(Module mod : _mods){
            if(Keyboard.isKeyDown(mod.getToggleKey())){
                _key_down.replace(mod.getClass().getSimpleName(), true);
            }else if(_key_down.get(mod.getClass().getSimpleName())){
                // KEY RELEASED !
                mod.toggle();
                SettingsUtils.setOption(mod.getClass().getSimpleName(), Boolean.toString(mod.isActivated()));
                _key_down.replace(mod.getClass().getSimpleName(), false);
            }
        }
	}

	@SubscribeEvent
	public void clientTick(final TickEvent event){
        for(Module mod : _mods){
            if(mod.isActivated()){
                mod.onUpdate();
            }
        }
	}
}
