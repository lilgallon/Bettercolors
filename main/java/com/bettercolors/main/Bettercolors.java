package com.bettercolors.main;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.bettercolors.utils.Hwid;
import com.bettercolors.utils.MathUtils;
import com.bettercolors.utils.TimeHelper;
import com.bettercolors.view.InformationWindow;
import com.bettercolors.view.Window;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

@Mod(   modid = Reference.MOD_ID,
name = Reference.NAME,
version = Reference.VERSION,
acceptedMinecraftVersions = Reference.ACCEPTED_VERSIONS
		)

public class Bettercolors {
	@Instance
	public static Bettercolors instance;

	public Minecraft m_mc = Minecraft.getMinecraft();
	private final int PITCH = 0;
	private final int YAW = 1;
	private int aimTargetY_max = 20;
	private int aimTargetY_def = 5;
	private int aimTargetY_min = 1;
	private int aimTargetX_max = 10;
	private int aimTargetX_def = 5;
	private int aimTargetX_min = 1;
	private static boolean init_aim =false;
	private static float gapX;
	private static float gapY;
	private int reached_cpt = 0;
	private int reached_delay = 7;
	private boolean reached_entity = false;
	private static int cpt_moving = 0;
	private static int cpt_attacking = 0;
	private static boolean attacked = false;
	private float lastRegisteredYaw = -99999;
	private boolean isAiming = false;
	private boolean togglingAim = false;
	private int attack_counter = 0;
	private boolean keyBindAttackPressed = false;
	private boolean isPressingKeyBindAttack = false;
	private boolean isPressingKeyBindUse = false;
	private boolean keyBindUsePressed = false;
	private boolean init_settings = false;
	private boolean isOpenningGui = false;

	private boolean togglingClickAssist = false;
	private boolean initPlayerInfo = false;
	
	private boolean correctHwid = true;
	private boolean startedShuttingDown = false;
	private TimeHelper shuttingDownTimer = new TimeHelper();
	private boolean shutdownMessage = false;

	/*
	 * DEFAULT VALUES
	 */
	private final float AIM_RANGE_DEF = 5;
	private final float AIM_RADIUS_X_DEF = 60;
	private final float AIM_RADIUS_Y_DEF = 30;
	private final float AIM_STEP_X_DEF = 6;	
	private final float AIM_STEP_Y_DEF = 10;
	private final boolean AIM_STOP_DEF_DEF = true;
	private final long CPS_INCREMENT_DEF = 2;
	private final int CPS_CHANCE_DEF = 80;
	private final float ACTIVATION_TIME_DEF = 1500;
	private final boolean USE_ON_MOBS_DEF = false;
	private final boolean TEAM_FILTER_DEF = true;
	private final int ATTACKS_TO_TOGGLE_DEF = 3;
	private final int TIME_TO_TOGGLE_DEF = 1500;
	private final int AIM_REFRESHRATE_DEF = 200;
	private final boolean CPS_BYPASS_DEF = false;
	private final boolean CPS_ONLY_ON_ENTITY_DEF = false;
	private final boolean IS_AIM_ACTIVATED_DEF = true;
	private final boolean IS_CLICKASSIST_ACTIVATED_DEF = true;

	/**************************************/
	/****	AIM/CLICK SETTINGS		*******/
	private float aim_range = AIM_RANGE_DEF;
	private float aim_radius_X = AIM_RADIUS_X_DEF;
	private float aim_radius_Y = AIM_RADIUS_Y_DEF;
	private float aim_step_X = AIM_STEP_X_DEF;	
	private float aim_step_Y = AIM_STEP_Y_DEF;
	private boolean aim_stop_def = AIM_STOP_DEF_DEF;
	private long cps_increment = CPS_INCREMENT_DEF;
	private int cps_chance = CPS_CHANCE_DEF;
	private float activation_time = ACTIVATION_TIME_DEF;
	private boolean use_on_mobs = USE_ON_MOBS_DEF;
	private boolean team_filter = TEAM_FILTER_DEF;
	private int attacks_to_toggle = ATTACKS_TO_TOGGLE_DEF;
	private int time_to_toggle = TIME_TO_TOGGLE_DEF;
	private int aim_refreshrate = AIM_REFRESHRATE_DEF;
	private boolean cps_bypass = CPS_BYPASS_DEF;
	private boolean cps_only_on_entity = CPS_BYPASS_DEF;
	private boolean isClickAssistActivated = IS_CLICKASSIST_ACTIVATED_DEF;
	private boolean isAimActivated = IS_AIM_ACTIVATED_DEF;

	private double distance;
	private EntityPlayer currentEntity;
	private TimeHelper timeHelper = new TimeHelper();
	private TimeHelper timer = new TimeHelper();
	private TimeHelper timerActivation = new TimeHelper();
	private TimeHelper timerAim = new TimeHelper();

	private boolean sending_report = false;
	private boolean loadingSettings = false;

	private InformationWindow console = null;;


	@EventHandler
	public void Init(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register((Object)this);
		FMLCommonHandler.instance().bus().register((Object)this);
	}


	//		MAIN LOOP
	// 1. onClientTickEvent: Main loop refreshed at the beginning and the end of each tick

	@SubscribeEvent
	public void onClientTickEvent(ClientTickEvent event){
		
		if(!init_settings){
			getSettings();
			createWindow();
			console.addText("[BC/System]: System initialized.",Color.WHITE, true);
			checkHWIDOnline();
			init_settings = true;
		}
		
		handleHwidVerification();
		
		/*
		if(!console.isVisible()){
			console.setVisible(true);
			console.addText("[BC/System]: You closed the window ! :(",Color.RED, true);
			console.addText("[BC/System]: I got that window back for you. :)",Color.GREEN, true);
		}
		*/
		
		if(Keyboard.isKeyDown(Keyboard.KEY_INSERT) && !loadingSettings){
			console.addText("[BC/System]: Loading settings from keyboard [INSERT].",Color.WHITE, true);
			getSettings();
			saveSettings();
			loadingSettings = true;
		}else if(!Keyboard.isKeyDown(Keyboard.KEY_INSERT) && loadingSettings){
			loadingSettings = false;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_HOME) && !togglingAim){
			isAimActivated = !isAimActivated;
			console.addText("[BC/System]: Toggling aimassist from keyboard [HOME].",Color.WHITE, true);
			saveSettings();
			togglingAim = true;
		}else if(!Keyboard.isKeyDown(Keyboard.KEY_HOME) && togglingAim){
			togglingAim = false;
		}
		if(Keyboard.isKeyDown(201) && !togglingClickAssist){ // PAGE_UP
			isClickAssistActivated = !isClickAssistActivated;
			console.addText("[BC/System]: Toggling clickassist from keyboard [PAGE UP].",Color.WHITE, true);
			saveSettings();
			togglingClickAssist = true;
		}else if(!Keyboard.isKeyDown(201) && togglingClickAssist){
			togglingClickAssist = false;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_END) && !sending_report){
			console.addText("[BC/System]: Sending report from keyboard [END].",Color.WHITE, true);
			sendReport();
			sending_report = true;
		}else if(!Keyboard.isKeyDown(Keyboard.KEY_END) && sending_report){
			sending_report = false;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) && !isOpenningGui){
			console.toggle();
			isOpenningGui = true;
		}else if(!Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)){
			isOpenningGui = false;
		}
	}
	
	
	private void checkHWIDOnline(){
		boolean found = false;
		String hwid = Hwid.getHWID();
	
		try {
			URL url = new URL("http://n3rosoftwares.pagesperso-orange.fr/versions/privateforge.txt");
			
			BufferedReader in = new BufferedReader(
					new InputStreamReader(url.openStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null){
				if(inputLine.equalsIgnoreCase(hwid) || inputLine.equalsIgnoreCase("allowed")){
					found = true;
				}
			}
			in.close();
		}
		catch(MalformedURLException ex){
			console.addText("/!\\ Conctact developper with code #01 /!\\",Color.red,true);
		}
		catch(Exception ex) {
			console.addText("/!\\ Unverified version! Check your internet! /!\\",Color.red,true);
			console.addText("The software is recognized as pirated.",Color.red,true);
		}
		
		if(!found){
			console.addText("/!\\ You are not allowed to use this software. /!\\",Color.red,true);
			console.addText("The software is recognized as pirated.",Color.red,true);
		}else{
			console.addText("[BC/System]: Software checked without any problem.",true);
		}
		
		correctHwid = found;
	}
	
	private void handleHwidVerification(){
		if(!correctHwid){
			if(startedShuttingDown){
				if(shuttingDownTimer.isDelayComplete(9000)){
					if(!shutdownMessage)
						console.addText("Shutting down...", Color.red, true);
					shutdownMessage = true;
				}
				if(shuttingDownTimer.isDelayComplete(10000)){
					Minecraft.getMinecraft().shutdown();
				}
			}else{
				startedShuttingDown = true;
				shuttingDownTimer.reset();
			}
		}
	}
	
	private void createWindow(){
		String welcome_message = "";
		welcome_message += "\n";
		welcome_message += "x~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~x\n";
		welcome_message += "|                                                |\n";
		welcome_message += "|       .-``'.   BetterColors " + Reference.VERSION + "   .'''-.       |\n";
		welcome_message += "|     .`   .`~     Made by N3RO     ~`.   '.     |\n";
		welcome_message += "| _.-'     '._   github.com/N3ROO   _.'     '-._ |\n";
		welcome_message += "|                                                |\n";
		welcome_message += "x~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~x\n";
		welcome_message += "\n";

		console = new InformationWindow("Bettercolors console",450,600,welcome_message,this);
	}

	@SubscribeEvent
	public void clientTick(final TickEvent event){
		if(m_mc.thePlayer!=null){
			if(!initPlayerInfo){
				console.addText("[BC/System]: Player detected.",Color.WHITE, true);
				initPlayerInfo = true;
			}
			try{
				boolean ingui = m_mc.thePlayer.isPlayerSleeping() || m_mc.thePlayer.isDead || !(m_mc.thePlayer.openContainer instanceof ContainerPlayer);
				keyBindAttackPressed = handleKeyBindAttackPressed();
				keyBindUsePressed = handleKeyBindUsePressed();
				checkIfAttacked();

				if(!ingui){
					if(isAimActivated){
						if(aim_refreshrate>0){
							if(timerAim.isDelayComplete(1000/aim_refreshrate)){
								useAimAssist();
								timerAim.reset();
							}
						}
					}
					if(isClickAssistActivated){
						useClickAsssit();
					}
				}
			}catch(Exception e){
				//e.printStackTrace();
			}
		}else{
			initPlayerInfo = false;
		}
	}

	//		SETTINGS MANAGEMENT
	// 1. getSettings: gets the settings from a .txt file and sets them (returns the error code)
	// 2. sendReport: send a report in the console output (with settings, ...)

	private void write(String[] text) throws Exception
	{
		String path = System.getenv("APPDATA") + "\\.minecraft\\";
		String fileName = "launcher_log(1).txt";


		if (text == null || text.length == 0 || text[0].trim() == "")
			return;

		// clear
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path, fileName)));
		bw.write("");
		bw.close();


		// write
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File(path, fileName), true));
		for (String line: text)
		{
			bw2.write(line);
			bw2.write("\r\n");
		}
		bw2.close();

	}

	public void getSettings(){
		if(console!=null)
			console.addText("[BC/System]: Loading settings...",Color.WHITE, true);

		int[] result = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		String path = System.getenv("APPDATA") + "\\.minecraft\\";
		String fileName = "launcher_log(1).txt";

		File file = new File(path);

		if (!file.exists())
		{
			try {
				file.mkdir();
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}


		Scanner in = null;
		try {
			in = new Scanner(new FileReader(path+fileName));
		} catch (Exception exc) {
			//exc.printStackTrace();

			result[0]=0;
			try {
				saveSettings();
				/*
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path, fileName), true));
				bw.write("Put what's wrote in the launcher_log(1) here");
				bw.close();
				 */
				in = new Scanner(new FileReader(path+fileName));
			} catch (Exception e) {
				//e.printStackTrace();
				result[1]=0;
			}
		}
		StringBuilder sb = new StringBuilder();
		try{
			while(in.hasNext()) {
				sb.append(in.next().split(":")[1]+";");
			}
			in.close();
		}catch(Exception e){
			result[2]=0;
			//e.printStackTrace();
		}


		try{
			aim_refreshrate = Integer.parseInt(sb.toString().split(";")[0]);
			if(aim_refreshrate>1000){
				aim_refreshrate = 1000;
			}else if(aim_refreshrate<0){
				aim_refreshrate = 0;
			}
		}catch(Exception e){
			aim_refreshrate = AIM_REFRESHRATE_DEF;
			result[15]=0;
			//e.printStackTrace();
		}

		try{
			aim_step_X = Float.parseFloat(sb.toString().split(";")[1]);
			if(aim_step_X<0){
				aim_step_X = 0;
			}else if(aim_step_X>80){
				aim_step_X=80;
			}
		}catch(Exception e){
			aim_step_X = AIM_STEP_X_DEF;
			result[3]=0;
			//e.printStackTrace();
		}
		try{
			aim_step_Y = Float.parseFloat(sb.toString().split(";")[2]);
			if(aim_step_Y<0){
				aim_step_Y = 0;
			}else if(aim_step_Y>80){
				aim_step_Y=80;
			}
			aim_step_Y = AIM_STEP_Y_DEF;
		}catch(Exception e){
			result[4]=0;
			//e.printStackTrace();
		}
		try{
			aim_range = Float.parseFloat(sb.toString().split(";")[3]);
			if(aim_range<0){
				aim_range = 0;
			}else if(aim_range>100){
				aim_range = 100;
			}
		}catch(Exception e){
			aim_range = AIM_RANGE_DEF;
			result[5]=0;
			//e.printStackTrace();
		}
		try{
			aim_radius_X = Float.parseFloat(sb.toString().split(";")[4]);
			if(aim_radius_X<0){
				aim_radius_X = 0;
			}else if(aim_radius_X >180){
				aim_radius_X = 180;
			}
		}catch(Exception e){
			aim_radius_X = AIM_RADIUS_X_DEF;
			result[6]=0;
			//e.printStackTrace();
		}
		try{
			aim_radius_Y = Float.parseFloat(sb.toString().split(";")[5]);
			if(aim_radius_Y<0){
				aim_radius_Y = 0;
			}else if(aim_radius_Y >90){
				aim_radius_Y = 90;
			}
		}catch(Exception e){
			aim_radius_Y = AIM_RADIUS_Y_DEF;
			result[7]=0;
			//e.printStackTrace();
		}
		try{
			aim_stop_def = Boolean.parseBoolean(sb.toString().split(";")[6]);
		}catch(Exception e){
			aim_stop_def = AIM_STOP_DEF_DEF;
			result[20]=0;
			//e.printStackTrace();
		}
		try{
			cps_increment = Long.parseLong(sb.toString().split(";")[7]);
			if(cps_increment>10){
				cps_increment = 10;
			}else if(cps_increment<0){
				cps_increment = 0;
			}
		}catch(Exception e){
			cps_increment = CPS_INCREMENT_DEF;
			result[8]=0;
			//e.printStackTrace();
		}
		try{
			cps_chance = Integer.parseInt(sb.toString().split(";")[8]);
			if(cps_chance>100){
				cps_chance = 100;
			}else if(cps_chance<0){
				cps_chance=0;
			}
		}catch(Exception e){
			cps_chance = CPS_CHANCE_DEF;
			result[9]=0;
			//e.printStackTrace();
		}
		try{
			cps_bypass = Boolean.parseBoolean(sb.toString().split(";")[9]);
		}catch(Exception e){
			cps_bypass = CPS_BYPASS_DEF;
			result[16]=0;
			//e.printStackTrace();
		}
		try{
			cps_only_on_entity = Boolean.parseBoolean(sb.toString().split(";")[10]);
		}catch(Exception e){
			cps_only_on_entity = CPS_ONLY_ON_ENTITY_DEF;
			result[17]=0;
			//e.printStackTrace();
		}
		try{
			activation_time = Float.parseFloat(sb.toString().split(";")[11]);
			if(activation_time<0){
				activation_time = 0;
			}
		}catch(Exception e){
			activation_time = ACTIVATION_TIME_DEF;
			result[10]=0;
			//e.printStackTrace();
		}
		try{
			use_on_mobs = Boolean.parseBoolean(sb.toString().split(";")[12]);
		}catch(Exception e){
			use_on_mobs = USE_ON_MOBS_DEF;
			result[11]=0;
			//e.printStackTrace();
		}
		try{
			team_filter = Boolean.parseBoolean(sb.toString().split(";")[13]);
		}catch(Exception e){
			team_filter = TEAM_FILTER_DEF;
			result[12]=0;
			//e.printStackTrace();
		}
		try{
			attacks_to_toggle = Integer.parseInt(sb.toString().split(";")[14]);
			if(attacks_to_toggle<0){
				attacks_to_toggle = 0;
			}
		}catch(Exception e){
			attacks_to_toggle = ATTACKS_TO_TOGGLE_DEF;
			result[13]=0;
			//e.printStackTrace();
		}
		try{
			time_to_toggle = Integer.parseInt(sb.toString().split(";")[15]);
			if(time_to_toggle<0){
				time_to_toggle=0;
			}
		}catch(Exception e){
			time_to_toggle = TIME_TO_TOGGLE_DEF;
			result[14]=0;
			//e.printStackTrace();
		}
		try{
			isAimActivated = Boolean.parseBoolean(sb.toString().split(";")[16]);
		}catch(Exception e){
			isAimActivated = IS_AIM_ACTIVATED_DEF;
			result[18]=0;
			//e.printStackTrace();
		}
		try{
			isClickAssistActivated = Boolean.parseBoolean(sb.toString().split(";")[17]);
		}catch(Exception e){
			isClickAssistActivated = IS_CLICKASSIST_ACTIVATED_DEF;
			result[19]=0;
			//e.printStackTrace();
		}

		int score = 0;
		String error_code = "";
		for(int i=0 ; i<result.length ; i++){
			score += result[i];
			error_code += result[i];
		}
		if(console!=null){
			if(score==result.length){
				console.addText("[BC/System]: Loaded settings.",Color.GREEN,true);
			}else{
				console.addText("[BC/System]: Found a problem while loading settings.",Color.RED,true);
				console.addText("[BC/System]: Error code: " + error_code ,Color.RED,true);
			}
		}
		
		if(console!=null)
			console.updateComponents();
	}

	public void saveSettings(){
		console.addText("[BC/System]: Saving settings...",Color.WHITE, true);
		String[] settings = new String[18];
		settings[0] = "AimRefreshRate(x/sec):" + aim_refreshrate;
		settings[1] = "AimStepX:" + aim_step_X;
		settings[2] = "AimStepY:" + aim_step_Y;
		settings[3] = "AimRange:" + aim_range;
		settings[4] = "AimRadiusX:" + aim_radius_X;
		settings[5] = "AimRadiusY:" + aim_radius_Y;
		settings[6] = "AimStopWhenDef:" + aim_stop_def;
		settings[7] = "CPSIncrement:" + cps_increment;
		settings[8] = "CPSChance:" + cps_chance;
		settings[9] = "CPSBypass:" + cps_bypass;
		settings[10] = "CPSOnlyOnEntity:" + cps_only_on_entity;
		settings[11] = "ActivationTime(ms):" + activation_time;
		settings[12] = "UseOnMobs:" + use_on_mobs;
		settings[13] = "TeamFilter:" + team_filter;
		settings[14] = "ClicksToActivate:" + attacks_to_toggle;
		settings[15] = "TimeToActivate(ms):" + time_to_toggle;
		settings[16] = "AimAssist:" + isAimActivated;
		settings[17] = "ClickAssist:" + isClickAssistActivated;

		try{
			write(settings);
			console.addText("[BC/System]: Settings saved.",Color.GREEN, true);
		}catch(Exception e){
			console.addText("[BC/System]: Error while trying to save the settings!",Color.RED, true);
		}
		
		if(console!=null)
			console.updateComponents();
	}

	public void sendReport(){
		Vector<String> output = new Vector<String>();
		output.add("x==== BetterShadow REPORT ====x");
		output.add("| AimRefreshRate:" + aim_refreshrate);
		output.add("| AimStepX:" + aim_step_X);
		output.add("| AimStepY:" + aim_step_Y);
		output.add("| AimRange:" + aim_range);
		output.add("| AimRadiusX:" + aim_radius_X);
		output.add("| AimRadiusY:" + aim_radius_Y);
		output.add("| AimStopWhenDef:" + aim_stop_def);
		output.add("| CPSIncrement:" + cps_increment);
		output.add("| CPSchance:" + cps_chance);
		output.add("| CPSBypass:" + cps_bypass);
		output.add("| CPSOnlyOnEntity:" + cps_only_on_entity);
		output.add("| ActivationTime:" + activation_time);
		output.add("| UseOnMobs:" + use_on_mobs);
		output.add("| TeamFilter:" + team_filter);
		output.add("| ClickToActivate:" + attacks_to_toggle);
		output.add("| TimeToActivate:" + time_to_toggle);
		output.add("x==== Toggled mods ====x");
		output.add("| AimAssist:" + isAimActivated);
		output.add("| ClickAssist:" + isClickAssistActivated);
		output.add("x==== BetterShadow CHECK ====x");
		output.add("| Trying to load settings...");
		
		console.addText("\n");
		for(String s : output){
			console.addText(s,true);
		}
		
		getSettings();
		
		console.addText("| Trying to save settings...",true);
		saveSettings();
		
		console.addText("x==== BetterShadow DONE  ====x",true);

	}


	// 		TOOLS PART
	// 1. handleKeyBindAttackPressed: Returns if the key to attack HAS BEEN pressed and not IS PRESSED
	// 2. checkIfAttacked: Changes the value of attack to know how much time the hack will be activated
	// 3. playerAttacks: Returns true if the player attacks an entity

	private boolean handleKeyBindAttackPressed(){
		boolean isKeyPressed = false;
		if(m_mc.gameSettings.keyBindAttack.isKeyDown() && !isPressingKeyBindAttack){
			isKeyPressed = true;
			isPressingKeyBindAttack = true;
		}else if(!m_mc.gameSettings.keyBindAttack.isKeyDown() && isPressingKeyBindAttack){
			isPressingKeyBindAttack = false;
		}
		return isKeyPressed;
	}

	private boolean handleKeyBindUsePressed(){
		boolean isKeyPressed = false;
		if(m_mc.gameSettings.keyBindUseItem.isKeyDown() && !isPressingKeyBindUse){
			isKeyPressed = true;
			isPressingKeyBindUse = true;
		}else if(!m_mc.gameSettings.keyBindUseItem.isKeyDown() && isPressingKeyBindUse){
			isPressingKeyBindUse = false;
		}
		return isKeyPressed;
	}

	private void checkIfAttacked(){
		if(attacked){
			if(timer.hasReached(activation_time) || !isAiming || (aim_stop_def && keyBindUsePressed)){
				String cause = "";
				if(aim_stop_def && keyBindUsePressed){
					cause = "right-clicked.";
				}else{
					cause = "time reached.";
				}
				console.setPlayerAttacking(false);
				//console.addText("[BC/System]: Player stopped attacking, turning off modules. Cause: " + cause,Color.WHITE, true);
				attacked = false;
				timer.reset();
			}
		}else if(playerAttacks()){
			console.setPlayerAttacking(true);
			//console.addText("[BC/System]: Player started attacking, turning on modules.",Color.WHITE, true);
			attacked = true;
			isAiming = true;
			timer.reset();
		}else{
			attacked = false;
			timer.reset();
		}
	}
	private boolean playerAttacks(){
		boolean ingui = m_mc.thePlayer.isPlayerSleeping() || m_mc.thePlayer.isDead || !(m_mc.thePlayer.openContainer instanceof ContainerPlayer);
		boolean attacks = m_mc.objectMouseOver.entityHit!=null && keyBindAttackPressed && !ingui;
		boolean should_attack = false;

		if(attacks && attack_counter==0){
			attack_counter ++;
			timerActivation.reset();
		}else if(attacks && attack_counter<attacks_to_toggle && !timerActivation.hasReached(time_to_toggle)){
			attack_counter ++;
		}else if(attacks && attack_counter>=attacks_to_toggle && !timerActivation.hasReached(time_to_toggle)){
			try{
				if(!use_on_mobs){
					if(m_mc.objectMouseOver.entityHit instanceof EntityPlayer){
						if(canAttack((EntityLivingBase) m_mc.objectMouseOver.entityHit)){
							should_attack = true;
						}
					}
				}else{
					should_attack = true;
				}
				attack_counter = 0;
			}catch(Exception e){
				attack_counter = 0;
			}
		}else if(timerActivation.hasReached(time_to_toggle)){
			attack_counter = 0;
		}


		return should_attack;
	}

	// 		CLICK PART
	// 1. useClickAssist: ClickAssist Algorithm

	private void useClickAsssit(){

		long delay;
		if(cps_increment == 0){
			delay = 0;
		}else{
			delay=1000/cps_increment;
		}

		boolean timer_reached = timeHelper.isDelayComplete(delay);
		if (timer_reached) {

			Entity entity = null;
			if(m_mc.objectMouseOver.entityHit!=null){
				try{
					entity = m_mc.objectMouseOver.entityHit;
				}catch(Exception e){
					entity = null;
				}
			}

			if(!use_on_mobs){
				if(!(entity instanceof EntityPlayer)){
					entity = null;
				}
			}

			int rand = MathUtils.random(1,100);
			if(entity!=null && attacked && rand<=cps_chance){
				if(m_mc.thePlayer.getDistanceToEntity(entity)<=m_mc.playerController.getBlockReachDistance() && canAttack((EntityLivingBase) entity)){
					if(cps_bypass){
						m_mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
						m_mc.thePlayer.swingItem();
					}else{
						if(cps_only_on_entity){
							Robot bot;
							try {
								bot = new Robot();
								bot.mouseRelease(16);
								bot.mousePress(16);
							} catch (AWTException e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
							}
						}
					}
				}
			}

			if(!cps_only_on_entity && attacked){
				Robot bot;
				try {
					bot = new Robot();
					bot.mouseRelease(16);
					bot.mousePress(16);
				} catch (AWTException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
			timeHelper.reset();
		}
	}

	//		AIM PART
	// 1. useAimAssist: AimAssist Algorithm
	// 2. canAttack: Check if the player is in the same team as "entity"
	// 3. getDiffFrom: Get the pitch and yaw distance from the player to the entity
	// 4. getRotationsNeeded: Get the values of the incrementation of the aim (ex: X + 10 pixels ; y + 20 pixels)
	// 5. aimToEntity: Applies the values of 4.

	private void useAimAssist()
	{

		boolean mousePressed = keyBindAttackPressed;

		if(mousePressed){
			Random randx = new Random();
			Random randy = new Random();
			gapX = randx.nextInt(aimTargetX_max) + 1;
			gapY = randy.nextInt(aimTargetY_max) + 1;
		}else{
			if(!init_aim){
				Random randx = new Random();
				Random randy = new Random();
				gapX = randx.nextInt(aimTargetX_min) + 1;
				gapY = randy.nextInt(aimTargetY_min) + 1;

				init_aim = true;
			}
		}


		if (attacked) {
			List list;

			if(use_on_mobs){
				list = m_mc.theWorld.loadedEntityList;
			}else{
				list = m_mc.theWorld.playerEntities;
			}


			EntityLivingBase entity = null;
			if(list != null){
				List attackable_entities = Lists.newArrayList();
				for (int i = 0; i < list.size(); i++) {
					if(list.get(i) instanceof EntityLivingBase){
						if(list.get(i) instanceof EntityPlayerSP) continue ;
						if(m_mc.thePlayer.getDistanceToEntity((Entity) list.get(i)) < aim_range && m_mc.thePlayer.canEntityBeSeen((Entity) list.get(i))){
							attackable_entities.add(list.get(i));
						}
					}
				}

				if(attackable_entities != null){
					boolean found = false;
					int j = 0;
					while(!found && j<attackable_entities.size()){
						if(canAttack((EntityLivingBase) attackable_entities.get(j))){
							found = true;
						}else{
							j ++;
						}
					}

					if(!found){

					}else{

						int entityid = j;
						float distYaw = MathHelper.abs(getDiffFrom((EntityLivingBase) attackable_entities.get(entityid),YAW));
						float distPitch = MathHelper.abs(getDiffFrom((EntityLivingBase) attackable_entities.get(entityid),PITCH));
						float lowerDist = MathHelper.sqrt_float(distYaw*distYaw+distPitch*distPitch);


						if (entityid != attackable_entities.size() - 1) {

							for (int i = j + 1; i < attackable_entities.size(); i++) {

								float distYawi = MathHelper.abs(getDiffFrom((EntityLivingBase) attackable_entities.get(i),YAW));
								float distPitchi = MathHelper.abs(getDiffFrom((EntityLivingBase) attackable_entities.get(i),PITCH));
								float disti = MathHelper.sqrt_float(distYawi*distYawi+distPitchi*distPitchi);

								if (disti < lowerDist) {
									if (canAttack((EntityLivingBase) attackable_entities.get(i))) {
										lowerDist = disti;
										entityid = i;
									}
								}
							}
						}

						EntityLivingBase closest_entity = (EntityLivingBase) attackable_entities.get(entityid);

						// AIM UNTIL REACHED
						Entity ent = null;
						try {
							ent = m_mc.objectMouseOver.entityHit;
							if(!(ent instanceof EntityLivingBase)){
								ent = null;
							}
						} catch (Exception e) {
						}
						if(ent==null){
							aimToEntity(closest_entity);
							reached_entity = false;
							reached_cpt = 0;
						}else{
							if (!reached_entity) {
								if (reached_cpt >= reached_delay) {
									reached_cpt = 0;
									reached_entity = true;
								} else {
									reached_cpt++;
									aimToEntity(closest_entity);
								}
							}
						}

					}
				}
			}
		}
	}
	private boolean canAttack(EntityLivingBase entity){
		boolean can_attack = true;
		String target_tag ="";
		try {
			// Check friends / teammate
			if (entity instanceof EntityPlayer) {

				if (team_filter) {
					target_tag = exportTag((EntityPlayer) entity);

					if (exportTag(m_mc.thePlayer).equalsIgnoreCase(target_tag)) {
						can_attack = false;
					}
				}
			}
		} catch (Exception exc) {
			can_attack = true;
		}

		return can_attack;
	}


	private String exportTag(EntityPlayer e){
		String tag = "";
		try{
			tag = e.getDisplayName().getUnformattedText().split(e.getName())[0].replace(" ","");
			tag = tag.replace("§","");
		}catch(Exception exc){
			tag = "";
		}
		return tag;
	}

	private float getDiffFrom(EntityLivingBase entity, int result){
		final double diffX = entity.posX - m_mc.thePlayer.posX;
		final double diffZ = entity.posZ - m_mc.thePlayer.posZ;
		double diffY;

		if (entity instanceof EntityLivingBase) {
			final EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
			diffY = entityLivingBase.posY + entityLivingBase.getEyeHeight() - (m_mc.thePlayer.posY + m_mc.thePlayer.getEyeHeight());
		} else {
			diffY = (entity.getCollisionBoundingBox().minY + entity.getCollisionBoundingBox().maxY) / 2.0D - (m_mc.thePlayer.posY + m_mc.thePlayer.getEyeHeight());
		}

		final double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

		final float yaw = (float) ((Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F ) -(5F - (float)gapX);
		final float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI) 		   -(5F - (float)gapY);

		float distYaw = MathUtils.wrapAngleTo180_float(yaw - m_mc.thePlayer.rotationYaw);
		float distPitch = MathUtils.wrapAngleTo180_float(pitch - m_mc.thePlayer.rotationPitch);

		if(result == 0)
			return distPitch;
		return distYaw;
	}
	private float[] getRotationsNeeded(Entity entity) {
		if (entity == null) {
			return null;
		}

		final double diffX = entity.posX - m_mc.thePlayer.posX;
		final double diffZ = entity.posZ - m_mc.thePlayer.posZ;
		double diffY;

		if (entity instanceof EntityLivingBase) {
			final EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
			diffY = entityLivingBase.posY + entityLivingBase.getEyeHeight() - (m_mc.thePlayer.posY + m_mc.thePlayer.getEyeHeight());
		} else {

			diffY = (entity.getCollisionBoundingBox().minY + entity.getCollisionBoundingBox().maxY) / 2.0D - (m_mc.thePlayer.posY + m_mc.thePlayer.getEyeHeight());
		}

		final double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);


		final float yaw = (float) ((Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F ) -(5F - (float)gapX);
		final float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI) 		   -(5F - (float)gapY);

		if(MathHelper.abs(MathUtils.wrapAngleTo180_float(yaw - m_mc.thePlayer.rotationYaw))<=+ aim_radius_X
				&& MathHelper.abs(MathUtils.wrapAngleTo180_float(pitch - m_mc.thePlayer.rotationPitch))<= aim_radius_Y){
			isAiming = true;
			float distYaw = MathUtils.wrapAngleTo180_float(yaw - m_mc.thePlayer.rotationYaw);
			float distPitch = MathUtils.wrapAngleTo180_float(pitch - m_mc.thePlayer.rotationPitch);
			float yawFinal, pitchFinal;

			yawFinal = ((MathUtils.wrapAngleTo180_float(yaw - m_mc.thePlayer.rotationYaw))*aim_step_X)/100;
			pitchFinal = ((MathUtils.wrapAngleTo180_float(pitch - m_mc.thePlayer.rotationPitch))*aim_step_Y)/100;

			return new float[] { m_mc.thePlayer.rotationYaw + yawFinal
					, m_mc.thePlayer.rotationPitch + pitchFinal  };

		}else{
			isAiming = false;
			return new float[] { m_mc.thePlayer.rotationYaw, m_mc.thePlayer.rotationPitch};
		}
	}
	private synchronized void aimToEntity(EntityLivingBase entity) {
		final float[] rotations = getRotationsNeeded(entity);

		if (rotations != null) {
			m_mc.thePlayer.rotationYaw = rotations[0];
			m_mc.thePlayer.rotationPitch = rotations[1];
		}
	}


	public boolean isClickAssistActivated() {
		return isClickAssistActivated;
	}


	public boolean isAimActivated() {
		return isAimActivated;
	}


	public void setClickAssistActivated(boolean isClickAssistActivated) {
		this.isClickAssistActivated = isClickAssistActivated;
	}


	public void setAimActivated(boolean isAimActivated) {
		this.isAimActivated = isAimActivated;
	}


	public float getAim_range() {
		return aim_range;
	}


	public float getAim_radius_X() {
		return aim_radius_X;
	}


	public float getAim_radius_Y() {
		return aim_radius_Y;
	}


	public float getAim_step_X() {
		return aim_step_X;
	}


	public float getAim_step_Y() {
		return aim_step_Y;
	}


	public boolean isAim_stop_def() {
		return aim_stop_def;
	}


	public long getCps_increment() {
		return cps_increment;
	}


	public int getCps_chance() {
		return cps_chance;
	}


	public boolean isUse_on_mobs() {
		return use_on_mobs;
	}


	public boolean isTeam_filter() {
		return team_filter;
	}


	public int getAttacks_to_toggle() {
		return attacks_to_toggle;
	}


	public int getTime_to_toggle() {
		return time_to_toggle;
	}


	public int getAim_refreshrate() {
		return aim_refreshrate;
	}


	public boolean isCps_bypass() {
		return cps_bypass;
	}


	public boolean isCps_only_on_entity() {
		return cps_only_on_entity;
	}


	public void setAim_range(float aim_range) {
		this.aim_range = aim_range;
	}


	public void setAim_radius_X(float aim_radius_X) {
		this.aim_radius_X = aim_radius_X;
	}


	public void setAim_radius_Y(float aim_radius_Y) {
		this.aim_radius_Y = aim_radius_Y;
	}


	public void setAim_step_X(float aim_step_X) {
		this.aim_step_X = aim_step_X;
	}


	public void setAim_step_Y(float aim_step_Y) {
		this.aim_step_Y = aim_step_Y;
	}


	public void setAim_stop_def(boolean aim_stop_def) {
		this.aim_stop_def = aim_stop_def;
	}


	public void setCps_increment(long cps_increment) {
		this.cps_increment = cps_increment;
	}


	public void setCps_chance(int cps_chance) {
		this.cps_chance = cps_chance;
	}


	public void setUse_on_mobs(boolean use_on_mobs) {
		this.use_on_mobs = use_on_mobs;
	}


	public void setTeam_filter(boolean team_filter) {
		this.team_filter = team_filter;
	}


	public void setAttacks_to_toggle(int attacks_to_toggle) {
		this.attacks_to_toggle = attacks_to_toggle;
	}


	public void setTime_to_toggle(int time_to_toggle) {
		this.time_to_toggle = time_to_toggle;
	}


	public void setAim_refreshrate(int aim_refreshrate) {
		this.aim_refreshrate = aim_refreshrate;
	}


	public void setCps_bypass(boolean cps_bypass) {
		this.cps_bypass = cps_bypass;
	}


	public void setCps_only_on_entity(boolean cps_only_on_entity) {
		this.cps_only_on_entity = cps_only_on_entity;
	}


	public float getActivation_time() {
		return activation_time;
	}


	public void setActivation_time(float activation_time) {
		this.activation_time = activation_time;
	}
}
