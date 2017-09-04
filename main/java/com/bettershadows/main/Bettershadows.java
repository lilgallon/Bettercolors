package com.bettershadows.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.bettershadows.utils.MathUtils;
import com.bettershadows.utils.TimeHelper;
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

@Mod(   modid = Reference.MOD_ID,
name = Reference.NAME,
version = Reference.VERSION,
acceptedMinecraftVersions = Reference.ACCEPTED_VERSIONS
		)

public class Bettershadows {
	@Instance
	public static Bettershadows instance;

	public Minecraft m_mc = Minecraft.getMinecraft();
	private final int PITCH = 0;
	private final int YAW = 1;
	private int aimTargetY_max = 20;
	private int aimTargetY_def = 5;
	private int aimTargetY_min = 1;
	private int aimTargetX_max = 10;
	private int aimTargetX_def = 5;
	private int aimTargetX_min = 1;
	private static boolean init=false;
	private static float gapX;
	private static float gapY;
	private int reached_cpt = 0;
	private int reached_delay = 7;
	private boolean reached_entity = false;
	private static int cpt_moving = 0;
	private static int cpt_attacking = 0;
	private static boolean attacked = false;
	private float lastRegisteredYaw = -99999;
	private boolean isAimActivated = true;

	/**************************************/
	/****	AIM/CLICK SETTINGS		*******/
	private float aim_range = 5;
	private float aim_radius_X = 40;
	private float aim_radius_Y = 30;
	private float aim_step_X = 20;	
	private float aim_step_Y = 20;
	private long cps_increment = 2;
	private int cps_chance = 80;
	private float activation_time = 1.5F;
	private boolean use_on_mobs = false;

	private double distance;
	private EntityPlayer currentEntity;
	private TimeHelper timeHelper = new TimeHelper();
	private TimeHelper timer = new TimeHelper();
	
	private boolean sending_report = false;
	

	@EventHandler
	public void Init(FMLInitializationEvent event)
	{
		Bettershadows bettershadows = new Bettershadows();
		FMLCommonHandler.instance().bus().register(bettershadows);
		MinecraftForge.EVENT_BUS.register(bettershadows);
		getSettings();
	}

	//		MAIN LOOP
	// 1. onClientTickEvent: Main loop refreshed at the beginning and the end of each tick

	@SubscribeEvent
	public void onClientTickEvent(TickEvent.ClientTickEvent event)
	{
		if(m_mc.thePlayer!=null){
			try{
				boolean ingui = m_mc.thePlayer.isPlayerSleeping() || m_mc.thePlayer.isDead || !(m_mc.thePlayer.openContainer instanceof ContainerPlayer);
				
				if(Keyboard.isKeyDown(Keyboard.KEY_INSERT)){
					getSettings();
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_HOME)){
					isAimActivated = !isAimActivated;
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_END) && !sending_report){
					sendReport();
					sending_report = true;
				}else if(!Keyboard.isKeyDown(Keyboard.KEY_END) && sending_report){
					sending_report = false;
				}
				checkIfAttacked();
				
				if(!ingui){
					if(isAimActivated){
						useAimAssist();
					}
					useClickAsssit();
				}
			}catch(Exception e){
				//e.printStackTrace();
			}
		}
	}


	//		SETTINGS MANAGEMENT
	// 1. getSettings: gets the settings from a .txt file and sets them (returns the error code)
	// 2. sendReport: send a report in the console output (with settings, ...)
	
	private int[] getSettings(){
		int[] result = {1,1,1,1,1,1,1,1,1,1,1,1};
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
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path, fileName), true));
				bw.write("Put what's wrote in the launcher_log(1) here");
				bw.close();
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
			aim_step_X = Float.parseFloat(sb.toString().split(";")[0]);
		}catch(Exception e){
			aim_step_X = 20;
			result[3]=0;
			//e.printStackTrace();
		}
		try{
			aim_step_Y = Float.parseFloat(sb.toString().split(";")[1]);
		}catch(Exception e){
			aim_step_Y = 20;
			result[4]=0;
			//e.printStackTrace();
		}
		try{
			aim_range = Float.parseFloat(sb.toString().split(";")[2]);
		}catch(Exception e){
			aim_range = 5;
			result[5]=0;
			//e.printStackTrace();
		}
		try{
			aim_radius_X = Float.parseFloat(sb.toString().split(";")[3]);
		}catch(Exception e){
			aim_radius_X = 40;
			result[6]=0;
			//e.printStackTrace();
		}
		try{
			aim_radius_Y = Float.parseFloat(sb.toString().split(";")[4]);
		}catch(Exception e){
			aim_radius_Y = 30;
			result[7]=0;
			//e.printStackTrace();
		}
		try{
			cps_increment = Long.parseLong(sb.toString().split(";")[5]);
		}catch(Exception e){
			cps_increment = 1;
			result[8]=0;
			//e.printStackTrace();
		}
		try{
			cps_chance = Integer.parseInt(sb.toString().split(";")[6]);
		}catch(Exception e){
			cps_chance = 80;
			result[9]=0;
			//e.printStackTrace();
		}
		try{
			activation_time = Float.parseFloat(sb.toString().split(";")[7]);
		}catch(Exception e){
			activation_time = 1.5F;
			result[10]=0;
			//e.printStackTrace();
		}
		try{
			use_on_mobs = Boolean.parseBoolean(sb.toString().split(";")[8]);
		}catch(Exception e){
			use_on_mobs = false;
			result[11]=0;
			//e.printStackTrace();
		}
		
		return result;
	}
	private void sendReport(){
			System.out.println("x==== BetterShadow REPORT ====x");
			System.out.println("| AimStepX:" + aim_step_X);
			System.out.println("| AimStepY:" + aim_step_Y);
			System.out.println("| AimRange:" + aim_range);
			System.out.println("| AimRadiusX:" + aim_radius_X);
			System.out.println("| AimRadiusY:" + aim_radius_Y);
			System.out.println("| CPSIncrement:" + cps_increment);
			System.out.println("| CPSchance:" + cps_chance);
			System.out.println("| ActivationTime:" + activation_time);
			System.out.println("| UseOnMobs:" + use_on_mobs);
			System.out.println("x==== BetterShadow CHECK ====x");
			System.out.println("| Trying to load settings...");
			
			int[] result = getSettings();
			int score =0;
			String error_code = "";
			for(int i=0 ; i<result.length ; i++){
				score += result[i];
				error_code += result[i];
			}
			if(score==result.length){
				System.out.println("| No problem while loading settings.");
			}else{
				System.out.println("| Found a problem while loading settings.");
				System.out.println("| Error code: " + error_code );
			}
			System.out.println("x==== BetterShadow DONE  ====x");
	}
	
	// 		TOOLS PART
	// 1. checkIfAttacked: Changes the value of attack to know how much time the hack will be activated
	// 2. playerAttacks: Returns true if the player attacks an entity
	
	private void checkIfAttacked(){
		if(attacked){
			if(timer.hasReached(1000*activation_time)){
				attacked = false;
				timer.reset();
			}
		}else if(playerAttacks()){
			attacked = true;
			timer.reset();
		}else{
			attacked = false;
			timer.reset();
		}
	}
	private boolean playerAttacks(){
		boolean ingui = m_mc.thePlayer.isPlayerSleeping() || m_mc.thePlayer.isDead || !(m_mc.thePlayer.openContainer instanceof ContainerPlayer);
		return m_mc.objectMouseOver.entityHit!=null && (Mouse.isButtonDown(0) || m_mc.gameSettings.keyBindAttack.isPressed()) && !ingui;
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
				if(m_mc.thePlayer.getDistanceToEntity(entity)<=m_mc.playerController.getBlockReachDistance()){
					m_mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
					m_mc.thePlayer.swingItem();
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

		boolean mousePressed = (Mouse.isButtonDown(0) || m_mc.gameSettings.keyBindAttack.isPressed());

		if(mousePressed){
			Random randx = new Random();
			Random randy = new Random();
			gapX = randx.nextInt(aimTargetX_max) + 1;
			gapY = randy.nextInt(aimTargetY_max) + 1;
		}else{
			if(!init){
				Random randx = new Random();
				Random randy = new Random();
				gapX = randx.nextInt(aimTargetX_min) + 1;
				gapY = randy.nextInt(aimTargetY_min) + 1;

				init = true;
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
		// v2?
		// Check if the player is in the same time as entity
		return can_attack;
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


			float distYaw = MathUtils.wrapAngleTo180_float(yaw - m_mc.thePlayer.rotationYaw);
			float distPitch = MathUtils.wrapAngleTo180_float(pitch - m_mc.thePlayer.rotationPitch);
			float yawFinal, pitchFinal;

			yawFinal = ((MathUtils.wrapAngleTo180_float(yaw - m_mc.thePlayer.rotationYaw))*aim_step_X)/100;
			pitchFinal = ((MathUtils.wrapAngleTo180_float(pitch - m_mc.thePlayer.rotationPitch))*aim_step_Y)/100;

			return new float[] { m_mc.thePlayer.rotationYaw + yawFinal
					, m_mc.thePlayer.rotationPitch + pitchFinal  };

		}else{
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
}
