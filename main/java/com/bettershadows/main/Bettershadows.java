package com.bettershadows.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
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

	/**************************************/
	/****		AIM SETTINGS		*******/
	private float aim_range = 5;
	private float aim_radius_X = 40;
	private float aim_radius_Y = 30;
	private float aim_step_X = 20;	
	private float aim_step_Y = 20;

	private double distance;
	private EntityPlayer currentEntity;

	@EventHandler
	public void Init(FMLInitializationEvent event)
	{
		Bettershadows bettershadows = new Bettershadows();
		FMLCommonHandler.instance().bus().register(bettershadows);
		MinecraftForge.EVENT_BUS.register(bettershadows);
		getSettings();
	}


	@SubscribeEvent
	public void onClientTickEvent(TickEvent.ClientTickEvent event)
	{
		if(m_mc.thePlayer!=null){
			try{
				if(Keyboard.isKeyDown(Keyboard.KEY_INSERT)){
					getSettings();
				}
				checkIfAttacked();
				useAimbot();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}




	private void getSettings(){
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
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path, fileName), true));
				bw.write("0");
				bw.close();
				in = new Scanner(new FileReader(path+fileName));
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
		StringBuilder sb = new StringBuilder();
		try{
			while(in.hasNext()) {
				sb.append(in.next().split(":")[1]+";");
			}
			in.close();
		}catch(Exception e){
			//e.printStackTrace();
		}


		try{
			aim_step_X = Float.parseFloat(sb.toString().split(";")[0]);
		}catch(Exception e){
			aim_step_X = 20;
		}
		try{
			aim_step_Y = Float.parseFloat(sb.toString().split(";")[1]);
		}catch(Exception e){
			aim_step_Y = 20;
		}
		try{
			aim_range = Float.parseFloat(sb.toString().split(";")[2]);
		}catch(Exception e){
			aim_range = 5;
		}
		try{
			aim_radius_X = Float.parseFloat(sb.toString().split(";")[3]);
		}catch(Exception e){
			aim_radius_X = 40;
		}
		try{
			aim_radius_Y = Float.parseFloat(sb.toString().split(";")[4]);
		}catch(Exception e){
			aim_radius_Y = 30;
		}
	}


	private void checkIfAttacked(){
		if(cpt_attacking<75 && cpt_attacking!=0){
			cpt_attacking ++;
		}else if(m_mc.objectMouseOver.entityHit!=null && (Mouse.isButtonDown(0) || Mouse.isButtonDown(1) || m_mc.gameSettings.keyBindAttack.isPressed())){
			attacked = true;
			cpt_attacking = 1;
			lastRegisteredYaw = m_mc.thePlayer.attackedAtYaw;
		}else{
			attacked = false;
			cpt_attacking = 0;
		}
	}

	private void useAimbot()
	{

		boolean ingui = m_mc.thePlayer.isPlayerSleeping() || m_mc.thePlayer.isDead || !(m_mc.thePlayer.openContainer instanceof ContainerPlayer);
		if(ingui){
			return;
		}

		boolean mousePressed = (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) || m_mc.gameSettings.keyBindAttack.isPressed();

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


			//if(getOption(ModsConfig.aimMobs) || getOption(ModsConfig.aimHypixelZombies)){
			//	list = m_mc.theWorld.loadedEntityList;
			//}else{
			list = m_mc.theWorld.playerEntities;
			//}


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

	private synchronized void aimToEntity(EntityLivingBase entity) {
		final float[] rotations = getRotationsNeeded(entity);

		if (rotations != null) {
			m_mc.thePlayer.rotationYaw = rotations[0];
			m_mc.thePlayer.rotationPitch = rotations[1];
		}
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

		if(MathHelper.abs(wrapAngleTo180_float(yaw - m_mc.thePlayer.rotationYaw))<=+ aim_radius_X
				&& MathHelper.abs(wrapAngleTo180_float(pitch - m_mc.thePlayer.rotationPitch))<= aim_radius_Y){


			float distYaw = wrapAngleTo180_float(yaw - m_mc.thePlayer.rotationYaw);
			float distPitch = wrapAngleTo180_float(pitch - m_mc.thePlayer.rotationPitch);
			float yawFinal, pitchFinal;

			yawFinal = ((wrapAngleTo180_float(yaw - m_mc.thePlayer.rotationYaw))*aim_step_X)/100;
			pitchFinal = ((wrapAngleTo180_float(pitch - m_mc.thePlayer.rotationPitch))*aim_step_Y)/100;

			return new float[] { m_mc.thePlayer.rotationYaw + yawFinal
					, m_mc.thePlayer.rotationPitch + pitchFinal  };

		}else{
			return new float[] { m_mc.thePlayer.rotationYaw, m_mc.thePlayer.rotationPitch};
		}
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

		float distYaw = wrapAngleTo180_float(yaw - m_mc.thePlayer.rotationYaw);
		float distPitch = wrapAngleTo180_float(pitch - m_mc.thePlayer.rotationPitch);

		if(result == 0)
			return distPitch;
		return distYaw;
	}


	private boolean canAttack(EntityLivingBase entity){
		boolean can_attack = true;
		// v2?
		return can_attack;
	}


	private static float wrapAngleTo180_float(float p_76142_0_)
	{
		p_76142_0_ %= 360.0F;

		if (p_76142_0_ >= 180.0F)
		{
			p_76142_0_ -= 360.0F;
		}

		if (p_76142_0_ < -180.0F)
		{
			p_76142_0_ += 360.0F;
		}

		return p_76142_0_;
	}


}
