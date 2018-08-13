package com.bettercolors.main;

import com.bettercolors.io.SettingsUtils;
import com.bettercolors.modules.AimAssistance;
import com.bettercolors.modules.ClickAssistance;
import com.bettercolors.modules.Module;
import com.bettercolors.modules.options.Option;
import com.bettercolors.modules.options.ToggleOption;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Map;

@Mod(modid = Reference.MOD_ID,
	 name = Reference.NAME,
	 version = Reference.VERSION,
	 acceptedMinecraftVersions = Reference.ACCEPTED_VERSIONS)

public class Bettercolors {

    private final static ArrayList<Option> DEFAULT_ACTIVATION_STATUS;
    static{
        DEFAULT_ACTIVATION_STATUS = new ArrayList<>();
        DEFAULT_ACTIVATION_STATUS.add(new ToggleOption(AimAssistance.class.getName(), true));
        DEFAULT_ACTIVATION_STATUS.add(new ToggleOption(ClickAssistance.class.getName(), true));
    }

    private ArrayList<Option> activation_status;

    private static int KEY_PAGE_UP = 201;
    private ArrayList<Module> _mods;

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
        activation_status = DEFAULT_ACTIVATION_STATUS;
        modules_options.add(activation_status);
        // There is no settings file, we need to create it, otherwise we only add the settings that are not already
        // in the settings file (can happen after updating the mod to a newer version)
        SettingsUtils.setOptions(modules_options, options != null);
        options = SettingsUtils.getOptions();

		// Mods initialisation
		_mods = new ArrayList<>();
		_mods.add(new AimAssistance("Aim assistance", Keyboard.KEY_HOME, Boolean.parseBoolean(options.get(AimAssistance.class.getName())), options));
		_mods.add(new ClickAssistance("Click assistance", KEY_PAGE_UP, Boolean.parseBoolean(options.get(ClickAssistance.class.getName())), options));

		// Window initialisation
        // todo

        // Version check
        // todo
	}

	@SubscribeEvent
	public void onClientTickEvent(ClientTickEvent event){
        // todo : Window updating acc. to mods
	}

	@SubscribeEvent
	public void clientTick(final TickEvent event){
        for(Module mod : _mods){
            if(Keyboard.isKeyDown(mod.getToggleKey())){
                mod.toggle();
            }
            mod.onUpdate();
        }
	}
}
