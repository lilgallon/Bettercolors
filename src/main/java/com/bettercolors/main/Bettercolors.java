package com.bettercolors.main;

import com.bettercolors.io.SettingsUtils;
import com.bettercolors.modules.AimAssistance;
import com.bettercolors.modules.ClickAssistance;
import com.bettercolors.modules.Module;
import com.bettercolors.modules.options.Option;
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

    private static int KEY_PAGE_UP = 201;
	private ArrayList<Module> _mods;

	@EventHandler
	public void Init(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);

		// Settings management
        // todo
        Map<String, String> options = SettingsUtils.getOptions();
        if(options == null){
            // There is no settings file, we need to create it
            ArrayList<ArrayList<Option>> modules_options = new ArrayList<>();
            modules_options.add(AimAssistance.getDefaultOptions());
            modules_options.add(ClickAssistance.getDefaultOptions());
            SettingsUtils.resetToDefault(modules_options);
        }

		// Mods initialisation
		_mods = new ArrayList<>();
		//_mods.add(new AimAssistance("Aim assistance", Keyboard.KEY_HOME, true));
		//_mods.add(new ClickAssistance("Click assistance", KEY_PAGE_UP, true));

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
