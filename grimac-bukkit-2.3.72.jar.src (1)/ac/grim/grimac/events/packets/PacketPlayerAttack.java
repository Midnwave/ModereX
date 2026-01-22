/*     */ package ac.grim.grimac.events.packets;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.checks.impl.badpackets.BadPacketsW;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerAbstract;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerPriority;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*     */ 
/*     */ public class PacketPlayerAttack
/*     */   extends PacketListenerAbstract
/*     */ {
/*     */   public PacketPlayerAttack() {
/*  24 */     super(PacketListenerPriority.LOW);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPacketReceive(PacketReceiveEvent event) {
/*  29 */     if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
/*  30 */       WrapperPlayClientInteractEntity interact = new WrapperPlayClientInteractEntity(event);
/*  31 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/*     */       
/*  33 */       if (player == null) {
/*     */         return;
/*     */       }
/*  36 */       if (!player.compensatedEntities.entityMap.containsKey(interact.getEntityId()) && !player.compensatedEntities.serverPositionsMap.containsKey(interact.getEntityId()) && (
/*     */         
/*  38 */         !player.compensatedEntities.entitiesRemovedThisTick.contains(interact.getEntityId()) || player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_14))) {
/*  39 */         BadPacketsW badPacketsW = (BadPacketsW)player.checkManager.getCheck(BadPacketsW.class);
/*  40 */         if (badPacketsW.flagAndAlert("entityId=" + interact.getEntityId()) && badPacketsW.shouldModifyPackets()) {
/*  41 */           event.setCancelled(true);
/*  42 */           player.onPacketCancel();
/*     */         } 
/*     */         
/*     */         return;
/*     */       } 
/*  47 */       if (interact.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
/*  48 */         if (player.isResetItemUsageOnAttack()) {
/*  49 */           GrimAPI.INSTANCE.getItemResetHandler().resetItemUsage(player.platformPlayer);
/*     */         }
/*     */ 
/*     */ 
/*     */         
/*  54 */         if (player.compensatedEntities.self.getAttributeValue(Attributes.ATTACK_DAMAGE) <= 0.0D)
/*     */           return; 
/*  56 */         ItemStack heldItem = player.inventory.getHeldItem();
/*  57 */         PacketEntity entity = player.compensatedEntities.getEntity(interact.getEntityId());
/*     */         
/*  59 */         if (entity != null && (!entity.isLivingEntity || entity.type == EntityTypes.PLAYER || entity.type == EntityTypes.PAINTING || (entity.type == EntityTypes.ENDER_DRAGON && player
/*  60 */           .getClientVersion().isOlderThan(ClientVersion.V_1_21_2)))) {
/*     */ 
/*     */           
/*  63 */           int knockbackLevel = (player.getClientVersion().isOlderThan(ClientVersion.V_1_21) && heldItem != null) ? heldItem.getEnchantmentLevel(EnchantmentTypes.KNOCKBACK) : 0;
/*  64 */           boolean hasNegativeKB = (knockbackLevel < 0);
/*     */           
/*  66 */           boolean isLegacyPlayer = player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8);
/*     */           
/*  68 */           boolean noCooldown = (isLegacyPlayer || PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_9));
/*     */           
/*  70 */           if (!isLegacyPlayer) {
/*  71 */             knockbackLevel = Math.max(knockbackLevel, 0);
/*     */           }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  78 */           if ((player.lastSprinting && !hasNegativeKB && noCooldown) || knockbackLevel > 0) {
/*  79 */             player.minAttackSlow++;
/*  80 */             player.maxAttackSlow++;
/*     */ 
/*     */             
/*  83 */             if (knockbackLevel == 0) {
/*  84 */               player.maxAttackSlow = player.minAttackSlow = 1;
/*     */             }
/*  86 */           } else if (!isLegacyPlayer && player.lastSprinting) {
/*     */             
/*  88 */             if (player.maxAttackSlow > 0 && 
/*  89 */               PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9) && player.compensatedEntities.self
/*  90 */               .getAttributeValue(Attributes.ATTACK_SPEED) < 16.0D) {
/*     */               return;
/*     */             }
/*     */ 
/*     */             
/*  95 */             player.maxAttackSlow++;
/*     */           } 
/*     */         } 
/*  98 */       } else if (interact.getAction() == WrapperPlayClientInteractEntity.InteractAction.INTERACT) {
/*     */ 
/*     */         
/* 101 */         if (player.compensatedEntities.getEntity(interact.getEntityId()) instanceof ac.grim.grimac.utils.data.packetentity.PacketEntityHorse && player
/* 102 */           .getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_13))
/* 103 */           player.packetStateData.horseInteractCausedForcedRotation = true; 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\PacketPlayerAttack.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */