/*    */ package ac.grim.grimac.manager.init.load;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEventsAPI;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*    */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*    */ import java.util.concurrent.Executors;
/*    */ 
/*    */ public class PacketEventsInit
/*    */   implements LoadableInitable
/*    */ {
/*    */   PacketEventsAPI<?> packetEventsAPI;
/*    */   
/*    */   public PacketEventsInit(PacketEventsAPI<?> packetEventsAPI) {
/* 21 */     this.packetEventsAPI = packetEventsAPI;
/*    */   }
/*    */ 
/*    */   
/*    */   public void load() {
/* 26 */     LogUtil.info("Loading PacketEvents...");
/* 27 */     PacketEvents.setAPI(this.packetEventsAPI);
/* 28 */     PacketEvents.getAPI().getSettings()
/* 29 */       .fullStackTrace(true)
/* 30 */       .kickOnPacketException(true)
/* 31 */       .preViaInjection(true)
/* 32 */       .checkForUpdates(false)
/* 33 */       .reEncodeByDefault(false)
/* 34 */       .debug(false);
/* 35 */     PacketEvents.getAPI().load();
/*    */     
/* 37 */     Executors.defaultThreadFactory().newThread(() -> {
/*    */           StateTypes.AIR.getName();
/*    */           ItemTypes.AIR.getName();
/*    */           EntityTypes.PLAYER.getParent();
/*    */           EntityDataTypes.BOOLEAN.getName();
/*    */           ChatTypes.CHAT.getName();
/*    */           EnchantmentTypes.ALL_DAMAGE_PROTECTION.getName();
/*    */           ParticleTypes.DUST.getName();
/* 45 */         }).start();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\init\load\PacketEventsInit.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */