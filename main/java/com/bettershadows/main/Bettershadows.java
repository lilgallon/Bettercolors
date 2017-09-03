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
	/** INIT TOUT CE QUI CORRESPOND A LA CIBLE (POINT CIBLE) **/
	private int aimTargetY_max = 20;
	private int aimTargetY_def = 5;
	private int aimTargetY_min = 1;
	private int aimTargetX_max = 10;
	private int aimTargetX_def = 5;
	private int aimTargetX_min = 1;
	// Initie au calcul aléatoire
	private static boolean init=false;
	// C'est gapX et gapY qui vont recevoir le décalage calculé dans le cas d'un ciblage aléatoire
	private static float gapX;
	private static float gapY;
	// Pour continuer à aim pour aimUntilReached (eviter que ce soit brusque)
	private int reached_cpt = 0;
	// temps que ça va continuer aim une fois reach (prgm ticks)
	private int reached_delay = 7;
	private boolean reached_entity = false;
	// garde un aim après avoir bougé un peu la souris / attaqué
	private static int cpt_moving = 0;
	private static int cpt_attacking = 0;
	private static boolean attacked = false;
	private float lastRegisteredYaw = -99999;
	// COMPTEUR DE TICKS (pas IG ticks mais PROG ticks)
	private int ticks = 0;
	
	/**************************************/
	/****		AIM SETTINGS		*******/
	private float aim_range = 5;
	private float aim_radius_X = 40; // (180 deg)
	private float aim_radius_Y = 30;
	private float aim_refreshrate = 30; //(ticks)
	private float aim_step_X = 20;	// Pixels / refresh
	private float aim_step_Y = 20;
	
	
	/* Aim assist */
	private double distance;
	private EntityPlayer currentEntity;
	
	@EventHandler
	public void Init(FMLInitializationEvent event)
	{
		// Init les trucs
		Bettershadows aimbot = new Bettershadows();
	    FMLCommonHandler.instance().bus().register(aimbot);
	    MinecraftForge.EVENT_BUS.register(aimbot);
	}
	
	
	  @SubscribeEvent
	  public void onClientTickEvent(TickEvent.ClientTickEvent event)
	  {
		  if(m_mc.inGameHasFocus){
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
		  //System.out.println("oui");
		  
		  // HANDLE GUI HERE
		  
		/*
	    handleGui();
	    if (shouldToggle()) {
	      this.gui.toggleAimbot();
	    }
	    if (this.gui.isAimbotEnabled()) {
	      useAimbot();
	    }
	    if ((this.mc.field_71439_g != null) && (
	      (this.mc.field_71439_g.field_70128_L) || (this.mc.field_71439_g.func_110143_aJ() <= 0.0F))) {
	      this.gui.toggleAimbot();
	    }
	    */
		
	  }
	  /*
	  private void handleGui()
	  {
	    if (Keyboard.isKeyDown(41))
	    {
	      if (!this.isGuiOpen)
	      {
	        this.isGuiOpen = true;
	        this.mc.func_147108_a(this.gui);
	      }
	    }
	    else if (!(this.mc.field_71462_r instanceof AimbotGui))
	    {
	      if (this.isGuiOpen)
	      {
	        this.speed = this.gui.getSpeed();
	        this.fov = this.gui.getFov();
	        this.distance = this.gui.getDistance();
	      }
	      this.isGuiOpen = false;
	    }
	  }
	  */
	  
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
			  aim_refreshrate = Float.parseFloat(sb.toString().split(";")[0]); 
		  }catch(Exception e){
			  aim_refreshrate = 30;
		  }

		  try{
			  aim_step_X = Float.parseFloat(sb.toString().split(";")[1]);
		  }catch(Exception e){
			  aim_step_X = 20;
		  }
		  try{
			  aim_step_Y = Float.parseFloat(sb.toString().split(";")[2]);
		  }catch(Exception e){
			  aim_step_Y = 20;
		  }
		  try{
			  aim_range = Float.parseFloat(sb.toString().split(";")[3]);
		  }catch(Exception e){
			  aim_range = 5;
		  }
		  try{
			  aim_radius_X = Float.parseFloat(sb.toString().split(";")[4]);
		  }catch(Exception e){
			  aim_radius_X = 40;
		  }
		  try{
			  aim_radius_Y = Float.parseFloat(sb.toString().split(";")[5]);
		  }catch(Exception e){
			  aim_radius_Y = 30;
		  }
	  }
	  
		
	  private void checkIfAttacked(){
		  // Attaque pendant 75 ticks après une premiere update d'attaque
		  // (update quand le joueur se fait attaquer d'un angle different)
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

			/** STEP 2: On vérifie que le joueur est autorisé à aim **/
			boolean ingui = m_mc.thePlayer.isPlayerSleeping() || m_mc.thePlayer.isDead || !(m_mc.thePlayer.openContainer instanceof ContainerPlayer);
			if(ingui){
				return;
			}

			/** STEP 3: Définition de la cible (décalage en fait) **/
			boolean mousePressed = (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) || m_mc.gameSettings.keyBindAttack.isPressed();

			/*
			if(mousePressed && getOption(ModsConfig.aimHypixelZombies)){
				aimTargetY_def = 8;
			}else{
				aimTargetY_def = 5;
			}
			*/

			if(mousePressed){
				// On demande un aim random automatique
				// -> c'est update à chaque clic
				Random randx = new Random();
				Random randy = new Random();
				gapX = randx.nextInt(aimTargetX_max) + 1;
				gapY = randy.nextInt(aimTargetY_max) + 1;
			}else{
				// On est ici psq:
				// - On a choisit un aim random automatique
				// - On a pas encore cliqué
				// Dans ce cas, si ca a pas été init, on init avec des valeurs aléatoires
				
				if(!init){
					Random randx = new Random();
					Random randy = new Random();
					gapX = randx.nextInt(aimTargetX_min) + 1;
					gapY = randy.nextInt(aimTargetY_min) + 1;

					init = true;
				}
			}
			
			


			// Là on va entrer dans le if si on doit assister l'aim
			// 1: Aim on mouse click
			// 2: Aim 24/24
			// 3: Aim on mouse move
			if (attacked) {
				
				ticks++;
				if(ticks >= 20 - aim_refreshrate){
					// on est ici psq on a attendu assez de tick (sensibility)
					ticks = 0;
					List list;

					// On aim que les joueurs ici
					
					//if(getOption(ModsConfig.aimMobs) || getOption(ModsConfig.aimHypixelZombies)){
					//	list = m_mc.theWorld.loadedEntityList;
					//}else{
						list = m_mc.theWorld.playerEntities;
					//}


					// Là on va trouver l'entité la plus proche

					EntityLivingBase entity = null;
					if(list != null){

						// On récupère les entités attaquables
						List attackable_entities = Lists.newArrayList();
						for (int i = 0; i < list.size(); i++) {
							if(list.get(i) instanceof EntityLivingBase){
								if(list.get(i) instanceof EntityPlayerSP) continue ;
								if(m_mc.thePlayer.getDistanceToEntity((Entity) list.get(i)) < aim_range && m_mc.thePlayer.canEntityBeSeen((Entity) list.get(i))){
									attackable_entities.add(list.get(i));
								}
							}
						}

						// On prend la plus proche du viseur
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
								// On a pas trouvé d'entité attaquable au final
								//console_output[2] = "[AA]: You are in the same team !";

							}else{

								int entityid = j;
								float distYaw = MathHelper.abs(getDiffFrom((EntityLivingBase) attackable_entities.get(entityid),YAW));
								float distPitch = MathHelper.abs(getDiffFrom((EntityLivingBase) attackable_entities.get(entityid),PITCH));
								float lowerDist = MathHelper.sqrt_float(distYaw*distYaw+distPitch*distPitch);

								// On regarde s'il y en a d'autres plus proches et attaquables

								// On vérifie s'il en reste
								if (entityid != attackable_entities.size() - 1) {

									// on regarde s'il y a une entité plus proche du viseur
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
	  }
	  
	  private synchronized void aimToEntity(EntityLivingBase entity) {
			final float[] rotations = getRotationsNeeded(entity);

			if (rotations != null) {
				m_mc.thePlayer.rotationYaw = rotations[0];
				m_mc.thePlayer.rotationPitch = rotations[1] /*+ 1.0F*/;// 14
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


				// CAS STEP EN %

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
			/*
			try {
				// Check friends / teammate
				if (entity instanceof EntityPlayer) {

					if (Neroghost.getMod(TeammatesColorFilter.class).isToggled()) {

						String target_tag = ((TeammatesColorFilter) Neroghost.getMod(TeammatesColorFilter.class)).exportTag((EntityPlayer) entity);

						if (((TeammatesColorFilter) Neroghost.getMod(TeammatesColorFilter.class)).getColorTag().equalsIgnoreCase(target_tag)) {
							can_attack = false;
						}
					}
					// si on a pas déjà filtré avec la team, on regarde s'ils sont amis
					if (Neroghost.getMod(MiddleClickFriends.class).isToggled()) {
						if (can_attack && ((MiddleClickFriends) Neroghost.getMod(MiddleClickFriends.class)).getFriends() != null) {
							boolean found = false;
							int i = 0;
							while (!found && i < ((MiddleClickFriends) Neroghost.getMod(MiddleClickFriends.class)).getFriends().size()) {

								if (((MiddleClickFriends) Neroghost.getMod(MiddleClickFriends.class)).getFriends().get(i).equalsIgnoreCase(entity.getName())) {
									found = true;
								} else {
									i++;
								}
							}

							if (found) {
								can_attack = false;
							}
						}
					}
				}
			} catch (Exception exc) {
				can_attack = true;
			}
			*/

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
