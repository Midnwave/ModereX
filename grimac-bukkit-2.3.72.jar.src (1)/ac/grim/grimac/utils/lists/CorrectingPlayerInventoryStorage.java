/*     */ package ac.grim.grimac.utils.lists;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.platform.api.entity.GrimEntity;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.utils.inventory.InventoryStorage;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CorrectingPlayerInventoryStorage
/*     */   extends InventoryStorage
/*     */ {
/*  45 */   private static final Set<String> SUPPORTED_INVENTORIES = new HashSet<>(
/*  46 */       Arrays.asList(new String[] { "CHEST", "DISPENSER", "DROPPER", "PLAYER", "ENDER_CHEST", "SHULKER_BOX", "BARREL", "CRAFTING", "CREATIVE" }));
/*     */ 
/*     */   
/*     */   private final GrimPlayer player;
/*     */ 
/*     */   
/*  52 */   private final Map<Integer, Integer> serverIsCurrentlyProcessingThesePredictions = new ConcurrentHashMap<>();
/*     */ 
/*     */   
/*  55 */   private final Map<Integer, Integer> pendingFinalizedSlot = new ConcurrentHashMap<>();
/*     */   
/*     */   public CorrectingPlayerInventoryStorage(GrimPlayer player, int size) {
/*  58 */     super(size);
/*  59 */     this.player = player;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleClientClaimedSlotSet(int slotID) {
/*  65 */     if (slotID >= 0 && slotID <= 45) {
/*  66 */       this.pendingFinalizedSlot.put(Integer.valueOf(slotID), Integer.valueOf((GrimAPI.INSTANCE.getTickManager()).currentTick + 5));
/*     */     }
/*     */   }
/*     */   
/*     */   public void handleServerCorrectSlot(int slotID) {
/*  71 */     if (slotID >= 0 && slotID <= 45) {
/*  72 */       this.serverIsCurrentlyProcessingThesePredictions.put(Integer.valueOf(slotID), Integer.valueOf(this.player.lastTransactionSent.get()));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setItem(int item, ItemStack stack) {
/*  81 */     int finalTransaction = ((Integer)this.serverIsCurrentlyProcessingThesePredictions.getOrDefault(Integer.valueOf(item), Integer.valueOf(-1))).intValue();
/*     */ 
/*     */ 
/*     */     
/*  85 */     if (finalTransaction == -1 || this.player.lastTransactionReceived.get() >= finalTransaction) {
/*     */       
/*  87 */       this.pendingFinalizedSlot.put(Integer.valueOf(item), Integer.valueOf((GrimAPI.INSTANCE.getTickManager()).currentTick + 5));
/*  88 */       this.serverIsCurrentlyProcessingThesePredictions.remove(Integer.valueOf(item));
/*     */     } 
/*     */     
/*  91 */     super.setItem(item, stack);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkThatBukkitIsSynced(int slot) {
/* 100 */     if (this.player.platformPlayer == null)
/*     */       return; 
/* 102 */     if (!this.player.inventory.isPacketInventoryActive) {
/*     */       return;
/*     */     }
/* 105 */     int bukkitSlot = this.player.inventory.getBukkitSlot(slot);
/*     */     
/* 107 */     if (bukkitSlot != -1) {
/* 108 */       ItemStack existing = getItem(slot);
/* 109 */       ItemStack toPE = this.player.platformPlayer.getInventory().getStack(bukkitSlot, slot);
/*     */       
/* 111 */       if (existing.getType() != toPE.getType() || existing.getAmount() != toPE.getAmount()) {
/* 112 */         GrimAPI.INSTANCE.getScheduler().getEntityScheduler().execute((GrimEntity)this.player.platformPlayer, GrimAPI.INSTANCE.getGrimPlugin(), () -> this.player.platformPlayer.updateInventory(), null, 0L);
/*     */         
/* 114 */         setItem(slot, toPE);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void tickWithBukkit() {
/* 120 */     if (this.player.platformPlayer == null) {
/*     */       return;
/*     */     }
/* 123 */     int tickID = (GrimAPI.INSTANCE.getTickManager()).currentTick;
/* 124 */     for (Iterator<Map.Entry<Integer, Integer>> it = this.pendingFinalizedSlot.entrySet().iterator(); it.hasNext(); ) {
/* 125 */       Map.Entry<Integer, Integer> entry = it.next();
/*     */       
/* 127 */       if (((Integer)entry.getValue()).intValue() <= tickID) {
/* 128 */         checkThatBukkitIsSynced(((Integer)entry.getKey()).intValue());
/* 129 */         it.remove();
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 135 */     if (this.player.inventory.needResend) {
/* 136 */       GrimAPI.INSTANCE.getScheduler().getEntityScheduler().execute((GrimEntity)this.player.platformPlayer, GrimAPI.INSTANCE.getGrimPlugin(), () -> { if (!this.player.inventory.needResend) return;  if (SUPPORTED_INVENTORIES.contains(this.player.platformPlayer.getInventory().getOpenInventoryKey().toUpperCase(Locale.ROOT))) { this.player.inventory.needResend = false; this.player.platformPlayer.updateInventory(); }  }null, 0L);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 151 */     if (tickID % 5 == 0) {
/* 152 */       int slotToCheck = tickID / 5 % getSize();
/*     */       
/* 154 */       if (!this.pendingFinalizedSlot.containsKey(Integer.valueOf(slotToCheck)) && !this.serverIsCurrentlyProcessingThesePredictions.containsKey(Integer.valueOf(slotToCheck)))
/* 155 */         checkThatBukkitIsSynced(slotToCheck); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\lists\CorrectingPlayerInventoryStorage.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */