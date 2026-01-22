/*     */ package ac.grim.grimac.utils.data.packetentity;
/*     */ 
/*     */ import ac.grim.grimac.checks.impl.sprint.SprintD;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import ac.grim.grimac.utils.data.attribute.ValuedAttribute;
/*     */ import ac.grim.grimac.utils.inventory.EnchantmentHelper;
/*     */ import ac.grim.grimac.utils.math.GrimMath;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class PacketEntitySelf
/*     */   extends PacketEntity
/*     */ {
/*     */   private final GrimPlayer player;
/*     */   public int opLevel;
/*     */   
/*     */   public PacketEntitySelf(GrimPlayer player) {
/*  28 */     super(player, EntityTypes.PLAYER);
/*  29 */     this.player = player;
/*     */   }
/*     */   
/*     */   public PacketEntitySelf(GrimPlayer player, PacketEntitySelf old) {
/*  33 */     super(player, EntityTypes.PLAYER);
/*  34 */     this.player = player;
/*  35 */     this.opLevel = old.opLevel;
/*  36 */     this.attributeMap.putAll(old.attributeMap);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void initAttributes(GrimPlayer player) {
/*  41 */     super.initAttributes(player);
/*  42 */     if (player.getClientVersion().isOlderThan(ClientVersion.V_1_8)) {
/*  43 */       setAttribute(Attributes.STEP_HEIGHT, 0.5D);
/*     */     }
/*     */     
/*  46 */     ((ValuedAttribute)getAttribute(Attributes.SCALE).orElseThrow()).withSetRewriter((oldValue, newValue) -> {
/*     */           if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_20_5) || newValue.equals(oldValue)) {
/*     */             return oldValue;
/*     */           }
/*     */           
/*     */           player.possibleEyeHeights[2][0] = 0.4D * newValue.doubleValue();
/*     */           
/*     */           player.possibleEyeHeights[2][1] = 1.62D * newValue.doubleValue();
/*     */           
/*     */           player.possibleEyeHeights[2][2] = 1.27D * newValue.doubleValue();
/*     */           
/*     */           player.possibleEyeHeights[1][0] = 1.27D * newValue.doubleValue();
/*     */           
/*     */           player.possibleEyeHeights[1][1] = 1.62D * newValue.doubleValue();
/*     */           
/*     */           player.possibleEyeHeights[1][2] = 0.4D * newValue.doubleValue();
/*     */           
/*     */           player.possibleEyeHeights[0][0] = 1.62D * newValue.doubleValue();
/*     */           player.possibleEyeHeights[0][1] = 1.27D * newValue.doubleValue();
/*     */           player.possibleEyeHeights[0][2] = 0.4D * newValue.doubleValue();
/*     */           return newValue;
/*     */         });
/*  68 */     ValuedAttribute movementSpeed = ValuedAttribute.ranged(Attributes.MOVEMENT_SPEED, 0.10000000149011612D, 0.0D, 1024.0D);
/*  69 */     movementSpeed.with(new WrapperPlayServerUpdateAttributes.Property(Attributes.MOVEMENT_SPEED, 0.10000000149011612D, new ArrayList()));
/*  70 */     trackAttribute(movementSpeed);
/*  71 */     trackAttribute(ValuedAttribute.ranged(Attributes.ATTACK_DAMAGE, 2.0D, 0.0D, 2048.0D));
/*  72 */     trackAttribute(ValuedAttribute.ranged(Attributes.ATTACK_SPEED, 4.0D, 0.0D, 1024.0D)
/*  73 */         .requiredVersion(player, ClientVersion.V_1_9));
/*  74 */     trackAttribute(ValuedAttribute.ranged(Attributes.JUMP_STRENGTH, 0.41999998688697815D, 0.0D, 32.0D)
/*  75 */         .requiredVersion(player, ClientVersion.V_1_20_5));
/*  76 */     trackAttribute(ValuedAttribute.ranged(Attributes.BLOCK_BREAK_SPEED, 1.0D, 0.0D, 1024.0D)
/*  77 */         .requiredVersion(player, ClientVersion.V_1_20_5));
/*  78 */     trackAttribute(ValuedAttribute.ranged(Attributes.MINING_EFFICIENCY, 0.0D, 0.0D, 1024.0D)
/*  79 */         .requiredVersion(player, ClientVersion.V_1_21));
/*  80 */     trackAttribute(ValuedAttribute.ranged(Attributes.SUBMERGED_MINING_SPEED, 0.2D, 0.0D, 20.0D)
/*  81 */         .requiredVersion(player, ClientVersion.V_1_21));
/*  82 */     trackAttribute(ValuedAttribute.ranged(Attributes.ENTITY_INTERACTION_RANGE, 3.0D, 0.0D, 64.0D)
/*  83 */         .requiredVersion(player, ClientVersion.V_1_20_5));
/*  84 */     trackAttribute(ValuedAttribute.ranged(Attributes.BLOCK_INTERACTION_RANGE, 4.5D, 0.0D, 64.0D)
/*  85 */         .withGetRewriter(value -> 
/*     */           
/*  87 */           (player.gamemode == GameMode.CREATIVE && PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_20_5)) ? Double.valueOf(5.0D) : value)
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  94 */         .requiredVersion(player, ClientVersion.V_1_20_5));
/*  95 */     trackAttribute(ValuedAttribute.ranged(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.0D, 0.0D, 1.0D)
/*  96 */         .withGetRewriter(value -> {
/*     */             if (player.getClientVersion().isOlderThan(ClientVersion.V_1_8)) {
/*     */               return Double.valueOf(0.0D);
/*     */             }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             double depthStrider = EnchantmentHelper.getMaximumEnchantLevel(player.inventory, EnchantmentTypes.DEPTH_STRIDER);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             return player.getClientVersion().isOlderThan(ClientVersion.V_1_21) ? Double.valueOf(depthStrider) : (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_21) ? Double.valueOf(depthStrider / 3.0D) : value);
/* 117 */           }).requiredVersion(player, ClientVersion.V_1_21));
/* 118 */     trackAttribute(ValuedAttribute.ranged(Attributes.MOVEMENT_EFFICIENCY, 0.0D, 0.0D, 1.0D)
/* 119 */         .requiredVersion(player, ClientVersion.V_1_21));
/* 120 */     trackAttribute(ValuedAttribute.ranged(Attributes.SNEAKING_SPEED, 0.3D, 0.0D, 1.0D)
/* 121 */         .withGetRewriter(value -> {
/*     */             if (player.getClientVersion().isOlderThan(ClientVersion.V_1_19)) {
/*     */               return Double.valueOf(0.30000001192092896D);
/*     */             }
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             int swiftSneak = player.inventory.getLeggings().getEnchantmentLevel(EnchantmentTypes.SWIFT_SNEAK);
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             double clamped = GrimMath.clamp(0.3F + swiftSneak * 0.15F, 0.0F, 1.0F);
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             return player.getClientVersion().isOlderThan(ClientVersion.V_1_21) ? Double.valueOf(clamped) : (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_21) ? Double.valueOf(clamped) : value);
/* 140 */           }).requiredVersion(player, ClientVersion.V_1_21));
/*     */   }
/*     */   
/*     */   public boolean inVehicle() {
/* 144 */     return (getRiding() != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addPotionEffect(PotionType effect, int amplifier) {
/* 149 */     if (effect == PotionTypes.BLINDNESS && !hasPotionEffect(PotionTypes.BLINDNESS)) {
/* 150 */       ((SprintD)this.player.checkManager.getPostPredictionCheck(SprintD.class)).startedSprintingBeforeBlind = this.player.isSprinting;
/*     */     }
/*     */     
/* 153 */     this.player.pointThreeEstimator.updatePlayerPotions(effect, Integer.valueOf(amplifier));
/* 154 */     super.addPotionEffect(effect, amplifier);
/*     */   }
/*     */ 
/*     */   
/*     */   public void removePotionEffect(PotionType effect) {
/* 159 */     this.player.pointThreeEstimator.updatePlayerPotions(effect, null);
/* 160 */     super.removePotionEffect(effect);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onFirstTransaction(boolean relative, boolean hasPos, double relX, double relY, double relZ, GrimPlayer player) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void onSecondTransaction() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCollisionBox getPossibleCollisionBoxes() {
/* 175 */     return this.player.boundingBox.copy();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\packetentity\PacketEntitySelf.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */