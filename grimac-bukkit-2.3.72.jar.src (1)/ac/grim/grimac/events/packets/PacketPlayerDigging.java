/*     */ package ac.grim.grimac.events.packets;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.checks.impl.movement.NoSlow;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerAbstract;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerPriority;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.ComponentTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.FoodProperties;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemConsumable;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.InteractionHand;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUseItem;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.utils.item.ItemBehaviour;
/*     */ import ac.grim.grimac.utils.item.ItemBehaviourRegistry;
/*     */ 
/*     */ public class PacketPlayerDigging
/*     */   extends PacketListenerAbstract {
/*     */   public PacketPlayerDigging() {
/*  37 */     super(PacketListenerPriority.LOW);
/*     */   }
/*     */   
/*  40 */   private static final boolean RELIABLE_COMPONENT_SYSTEM = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_21_4);
/*  41 */   private static final boolean SERVER_HAS_OFFHAND = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9);
/*     */   
/*     */   public static void handleUseItem(@NotNull GrimPlayer player, @NotNull InteractionHand hand) {
/*  44 */     ItemStack item = player.inventory.getItemInHand(hand);
/*     */     
/*  46 */     if (item == null) {
/*  47 */       player.packetStateData.setSlowedByUsingItem(false);
/*     */       
/*     */       return;
/*     */     } 
/*  51 */     if (player.checkManager.getCompensatedCooldown().hasItem(item)) {
/*  52 */       player.packetStateData.setSlowedByUsingItem(false);
/*     */       
/*     */       return;
/*     */     } 
/*  56 */     ItemType material = item.getType();
/*     */ 
/*     */     
/*  59 */     if (RELIABLE_COMPONENT_SYSTEM && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_4)) {
/*  60 */       ItemBehaviour itemBehaviour = ItemBehaviourRegistry.getItemBehaviour(material);
/*     */       
/*  62 */       if (itemBehaviour.canUse(item, player.compensatedWorld, player, hand)) {
/*  63 */         player.packetStateData.setSlowedByUsingItem(true);
/*  64 */         player.packetStateData.itemInUseHand = hand;
/*     */       } else {
/*  66 */         player.packetStateData.setSlowedByUsingItem(false);
/*     */       } 
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/*  73 */     ItemConsumable consumable = (ItemConsumable)item.getComponentOr(ComponentTypes.CONSUMABLE, null);
/*  74 */     FoodProperties foodComponent = (FoodProperties)item.getComponentOr(ComponentTypes.FOOD, null);
/*     */ 
/*     */     
/*  77 */     if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_2) && consumable != null && foodComponent == null) {
/*  78 */       player.packetStateData.setSlowedByUsingItem(true);
/*  79 */       player.packetStateData.itemInUseHand = hand;
/*     */     } 
/*     */ 
/*     */     
/*  83 */     if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20_5) && foodComponent != null) {
/*  84 */       if (foodComponent.isCanAlwaysEat() || player.food < 20 || player.gamemode == GameMode.CREATIVE) {
/*  85 */         player.packetStateData.setSlowedByUsingItem(true);
/*  86 */         player.packetStateData.itemInUseHand = hand;
/*     */         return;
/*     */       } 
/*  89 */       player.packetStateData.setSlowedByUsingItem(false);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  94 */     if ((material.hasAttribute(ItemTypes.ItemAttribute.EDIBLE) && (player
/*  95 */       .getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_15) || player.gamemode != GameMode.CREATIVE)) || material == ItemTypes.POTION || material == ItemTypes.MILK_BUCKET) {
/*     */ 
/*     */ 
/*     */       
/*  99 */       if (item.getType() == ItemTypes.SPLASH_POTION) {
/*     */         return;
/*     */       }
/* 102 */       if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_9) && item.getLegacyData() > 16384) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/* 107 */       if (material == ItemTypes.POTION || material == ItemTypes.MILK_BUCKET || material == ItemTypes.GOLDEN_APPLE || material == ItemTypes.ENCHANTED_GOLDEN_APPLE || material == ItemTypes.HONEY_BOTTLE || material == ItemTypes.SUSPICIOUS_STEW || material == ItemTypes.CHORUS_FRUIT) {
/*     */ 
/*     */ 
/*     */         
/* 111 */         player.packetStateData.setSlowedByUsingItem(true);
/* 112 */         player.packetStateData.itemInUseHand = hand;
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 117 */       if (item.getType().hasAttribute(ItemTypes.ItemAttribute.EDIBLE) && ((player.platformPlayer != null && player.food < 20) || player.gamemode == GameMode.CREATIVE)) {
/* 118 */         player.packetStateData.setSlowedByUsingItem(true);
/* 119 */         player.packetStateData.itemInUseHand = hand;
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 124 */       player.packetStateData.setSlowedByUsingItem(false);
/*     */     } 
/*     */     
/* 127 */     if (material == ItemTypes.SHIELD && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
/* 128 */       player.packetStateData.setSlowedByUsingItem(true);
/* 129 */       player.packetStateData.itemInUseHand = hand;
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 134 */     NBTCompound nbt = item.getNBT();
/* 135 */     if (material == ItemTypes.CROSSBOW && nbt != null && nbt.getBoolean("Charged")) {
/* 136 */       player.packetStateData.setSlowedByUsingItem(false);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 141 */     if (material == ItemTypes.TRIDENT && item
/* 142 */       .getDamageValue() < item.getMaxDamage() - 1 && (player
/* 143 */       .getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13_2) || player
/* 144 */       .getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8))) {
/* 145 */       player.packetStateData.setSlowedByUsingItem((item.getEnchantmentLevel(EnchantmentTypes.RIPTIDE) <= 0));
/* 146 */       player.packetStateData.itemInUseHand = hand;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 151 */     if (material == ItemTypes.BOW || material == ItemTypes.CROSSBOW)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 162 */       player.packetStateData.setSlowedByUsingItem(false);
/*     */     }
/*     */     
/* 165 */     if (material == ItemTypes.SPYGLASS && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_17)) {
/* 166 */       player.packetStateData.setSlowedByUsingItem(true);
/* 167 */       player.packetStateData.itemInUseHand = hand;
/*     */     } 
/*     */     
/* 170 */     if (material == ItemTypes.GOAT_HORN && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_19)) {
/* 171 */       player.packetStateData.setSlowedByUsingItem(true);
/* 172 */       player.packetStateData.itemInUseHand = hand;
/*     */     } 
/*     */ 
/*     */     
/* 176 */     if (material.hasAttribute(ItemTypes.ItemAttribute.SWORD)) {
/* 177 */       if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8)) {
/* 178 */         player.packetStateData.setSlowedByUsingItem(true);
/* 179 */       } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_9)) {
/* 180 */         player.packetStateData.setSlowedByUsingItem(false);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public void onPacketReceive(PacketReceiveEvent event) {
/* 186 */     if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
/* 187 */       WrapperPlayClientPlayerDigging dig = new WrapperPlayClientPlayerDigging(event);
/*     */       
/* 189 */       if (dig.getAction() == DiggingAction.RELEASE_USE_ITEM) {
/* 190 */         GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 191 */         if (player == null)
/*     */           return; 
/* 193 */         player.packetStateData.setSlowedByUsingItem(false);
/* 194 */         player.packetStateData.slowedByUsingItemTransaction = player.lastTransactionReceived.get();
/*     */         
/* 196 */         if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13)) {
/* 197 */           ItemStack hand = player.inventory.getItemInHand(player.packetStateData.itemInUseHand);
/*     */           
/* 199 */           if (hand.getType() == ItemTypes.TRIDENT && hand.getEnchantmentLevel(EnchantmentTypes.RIPTIDE) > 0) {
/* 200 */             player.packetStateData.tryingToRiptide = true;
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 206 */     if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType()) || event.getPacketType() == PacketType.Play.Client.CLIENT_TICK_END) {
/* 207 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 208 */       if (player != null && player.packetStateData.isSlowedByUsingItem() && !player.packetStateData.lastPacketWasTeleport && !player.packetStateData.lastPacketWasOnePointSeventeenDuplicate) {
/*     */ 
/*     */ 
/*     */         
/* 212 */         boolean slotChanged = (player.packetStateData.itemInUseHand != InteractionHand.OFF_HAND && player.packetStateData.getSlowedByUsingItemSlot() != player.packetStateData.lastSlotSelected);
/* 213 */         if (slotChanged || player.inventory.getItemInHand(player.packetStateData.itemInUseHand).isEmpty()) {
/* 214 */           player.packetStateData.setSlowedByUsingItem(false);
/* 215 */           if (slotChanged) ((NoSlow)player.checkManager.getPostPredictionCheck(NoSlow.class)).didSlotChangeLastTick = true;
/*     */         
/*     */         } 
/*     */       } 
/*     */     } 
/* 220 */     if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
/* 221 */       int slot = (new WrapperPlayClientHeldItemChange(event)).getSlot();
/*     */ 
/*     */       
/* 224 */       if (slot > 8 || slot < 0)
/*     */         return; 
/* 226 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 227 */       if (player == null) {
/*     */         return;
/*     */       }
/*     */       
/* 231 */       CheckManagerListener.handleQueuedPlaces(player, false, 0.0F, 0.0F, System.currentTimeMillis());
/*     */       
/* 233 */       if (player.packetStateData.lastSlotSelected != slot) {
/* 234 */         if (player.isResetItemUsageOnSlotChange() && GrimAPI.INSTANCE.getItemResetHandler().getItemUsageHand(player.platformPlayer) == InteractionHand.MAIN_HAND) {
/* 235 */           GrimAPI.INSTANCE.getItemResetHandler().resetItemUsage(player.platformPlayer);
/*     */         }
/*     */ 
/*     */         
/* 239 */         if (player.canSkipTicks() && !player.isTickingReliablyFor(3) && player.packetStateData.itemInUseHand != InteractionHand.OFF_HAND) {
/* 240 */           player.packetStateData.setSlowedByUsingItem(false);
/*     */         }
/*     */       } 
/* 243 */       player.packetStateData.lastSlotSelected = slot;
/*     */     } 
/*     */     
/* 246 */     if (event.getPacketType() == PacketType.Play.Client.USE_ITEM || (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT && (new WrapperPlayClientPlayerBlockPlacement(event)).getFace() == BlockFace.OTHER)) {
/* 247 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 248 */       if (player == null)
/*     */         return; 
/* 250 */       if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_8) && player.gamemode == GameMode.SPECTATOR) {
/*     */         return;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 256 */       InteractionHand hand = (SERVER_HAS_OFFHAND && event.getPacketType() == PacketType.Play.Client.USE_ITEM) ? (new WrapperPlayClientUseItem(event)).getHand() : InteractionHand.MAIN_HAND;
/*     */       
/* 258 */       player.packetStateData.slowedByUsingItemTransaction = player.lastTransactionReceived.get();
/*     */       
/* 260 */       handleUseItem(player, hand);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\PacketPlayerDigging.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */