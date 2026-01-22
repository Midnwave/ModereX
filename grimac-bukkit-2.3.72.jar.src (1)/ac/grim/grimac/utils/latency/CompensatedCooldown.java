/*    */ package ac.grim.grimac.utils.latency;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.type.PositionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.ComponentTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemUseCooldown;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.utils.anticheat.update.PositionUpdate;
/*    */ import ac.grim.grimac.utils.data.CooldownData;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.Optional;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CompensatedCooldown
/*    */   extends Check
/*    */   implements PositionCheck
/*    */ {
/* 27 */   private final ConcurrentHashMap<ResourceLocation, CooldownData> itemCooldownMap = new ConcurrentHashMap<>();
/*    */   
/*    */   public CompensatedCooldown(GrimPlayer playerData) {
/* 30 */     super(playerData);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPositionUpdate(PositionUpdate positionUpdate) {
/* 35 */     for (Iterator<Map.Entry<ResourceLocation, CooldownData>> it = this.itemCooldownMap.entrySet().iterator(); it.hasNext(); ) {
/* 36 */       Map.Entry<ResourceLocation, CooldownData> entry = it.next();
/*    */ 
/*    */       
/* 39 */       if (((CooldownData)entry.getValue()).getTransaction() < this.player.lastTransactionReceived.get()) {
/* 40 */         ((CooldownData)entry.getValue()).tick();
/*    */       }
/*    */ 
/*    */       
/* 44 */       if (((CooldownData)entry.getValue()).getTicksRemaining() <= 0) it.remove();
/*    */     
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasItem(ItemStack item) {
/* 51 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_21_2)) {
/* 52 */       ItemUseCooldown cooldown = (ItemUseCooldown)item.getComponentOr(ComponentTypes.USE_COOLDOWN, null);
/* 53 */       if (cooldown != null) {
/* 54 */         Optional<ResourceLocation> cooldownGroup = cooldown.getCooldownGroup();
/*    */ 
/*    */         
/* 57 */         if (cooldownGroup.isPresent()) {
/* 58 */           return this.itemCooldownMap.containsKey(cooldownGroup.get());
/*    */         }
/*    */       } 
/*    */     } 
/*    */     
/* 63 */     return this.itemCooldownMap.containsKey(item.getType().getName());
/*    */   }
/*    */ 
/*    */   
/*    */   public void addCooldown(ResourceLocation location, int cooldown, int transaction) {
/* 68 */     if (cooldown == 0) {
/* 69 */       removeCooldown(location);
/*    */       
/*    */       return;
/*    */     } 
/* 73 */     this.itemCooldownMap.put(location, new CooldownData(cooldown, transaction));
/*    */   }
/*    */   
/*    */   public void removeCooldown(ResourceLocation location) {
/* 77 */     this.itemCooldownMap.remove(location);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\latency\CompensatedCooldown.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */