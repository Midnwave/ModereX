/*    */ package ac.grim.grimac.events.packets;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerAbstract;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerPriority;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockAction;
/*    */ import ac.grim.grimac.utils.data.ShulkerData;
/*    */ import ac.grim.grimac.utils.nmsutil.Materials;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PacketBlockAction
/*    */   extends PacketListenerAbstract
/*    */ {
/*    */   public PacketBlockAction() {
/* 23 */     super(PacketListenerPriority.HIGH);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketSend(PacketSendEvent event) {
/* 28 */     if (event.getPacketType() == PacketType.Play.Server.BLOCK_ACTION) {
/* 29 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 30 */       if (player == null)
/*    */         return; 
/* 32 */       WrapperPlayServerBlockAction blockAction = new WrapperPlayServerBlockAction(event);
/* 33 */       Vector3i blockPos = blockAction.getBlockPosition();
/*    */       
/* 35 */       player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
/*    */             WrappedBlockState existing = player.compensatedWorld.getBlock(blockPos);
/*    */             if (Materials.isShulker(existing.getType()))
/*    */               if (blockAction.getActionData() >= 1) {
/*    */                 ShulkerData data = new ShulkerData(blockPos, player.lastTransactionSent.get(), false);
/*    */                 player.compensatedWorld.openShulkerBoxes.remove(data);
/*    */                 player.compensatedWorld.openShulkerBoxes.add(data);
/*    */               } else {
/*    */                 ShulkerData data = new ShulkerData(blockPos, player.lastTransactionSent.get(), true);
/*    */                 player.compensatedWorld.openShulkerBoxes.remove(data);
/*    */                 player.compensatedWorld.openShulkerBoxes.add(data);
/*    */               }  
/*    */           });
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\PacketBlockAction.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */