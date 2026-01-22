/*    */ package ac.grim.grimac.checks.impl.badpackets;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3f;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
/*    */ 
/*    */ @CheckData(name = "BadPacketsU", description = "Sent impossible use item packet")
/*    */ public class BadPacketsU extends Check implements PacketCheck {
/*    */   public BadPacketsU(GrimPlayer player) {
/* 20 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 25 */     if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
/* 26 */       WrapperPlayClientPlayerBlockPlacement packet = new WrapperPlayClientPlayerBlockPlacement(event);
/*    */       
/* 28 */       if (packet.getFace() == BlockFace.OTHER) {
/*    */ 
/*    */ 
/*    */         
/* 32 */         int expectedY = this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8) ? 4095 : 255;
/*    */ 
/*    */ 
/*    */         
/* 36 */         boolean failedItemCheck = (packet.getItemStack().isPresent() && isEmpty(packet.getItemStack().get()) && this.player.getClientVersion().isOlderThan(ClientVersion.V_1_9));
/*    */         
/* 38 */         Vector3i pos = packet.getBlockPosition();
/* 39 */         Vector3f cursor = packet.getCursorPosition();
/*    */         
/* 41 */         if (failedItemCheck || pos.x != -1 || pos.y != expectedY || pos.z != -1 || cursor.x != 0.0F || cursor.y != 0.0F || cursor.z != 0.0F || packet
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */           
/* 48 */           .getSequence() != 0) {
/*    */           
/* 50 */           String verbose = String.format("xyz=%s, %s, %s, cursor=%s, %s, %s, item=%s, sequence=%s", new Object[] {
/*    */                 
/* 52 */                 Integer.valueOf(pos.x), Integer.valueOf(pos.y), Integer.valueOf(pos.z), Float.valueOf(cursor.x), Float.valueOf(cursor.y), Float.valueOf(cursor.z), Boolean.valueOf(!failedItemCheck), Integer.valueOf(packet.getSequence())
/*    */               });
/* 54 */           if (flagAndAlert(verbose) && shouldModifyPackets()) {
/* 55 */             this.player.onPacketCancel();
/* 56 */             event.setCancelled(true);
/*    */           } 
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   private boolean isEmpty(ItemStack itemStack) {
/* 64 */     return (itemStack.getType() == null || itemStack.getType() == ItemTypes.AIR);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsU.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */