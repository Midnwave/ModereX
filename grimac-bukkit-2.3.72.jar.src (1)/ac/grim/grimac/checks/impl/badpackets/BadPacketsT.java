/*    */ package ac.grim.grimac.checks.impl.badpackets;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3f;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
/*    */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*    */ 
/*    */ @CheckData(name = "BadPacketsT")
/*    */ public class BadPacketsT
/*    */   extends Check
/*    */   implements PacketCheck {
/* 20 */   private final boolean hasLegacyExpansion = this.player.getClientVersion().isOlderThan(ClientVersion.V_1_9);
/* 21 */   private final double maxHorizontalDisplacement = 0.3001D + (this.hasLegacyExpansion ? 0.1D : 0.0D);
/* 22 */   private final double minVerticalDisplacement = -1.0E-4D - (this.hasLegacyExpansion ? 0.1D : 0.0D);
/* 23 */   private final double maxVerticalDisplacement = 1.8001D + (this.hasLegacyExpansion ? 0.1D : 0.0D);
/*    */   
/*    */   public BadPacketsT(GrimPlayer player) {
/* 26 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 31 */     if (event.getPacketType().equals(PacketType.Play.Client.INTERACT_ENTITY)) {
/* 32 */       WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
/*    */       
/* 34 */       wrapper.getTarget().ifPresent(targetVector -> {
/*    */             PacketEntity packetEntity = this.player.compensatedEntities.getEntity(wrapper.getEntityId());
/*    */             if (packetEntity == null)
/*    */               return; 
/*    */             if (!EntityTypes.PLAYER.equals(packetEntity.type))
/*    */               return; 
/*    */             float scale = (float)packetEntity.getAttributeValue(Attributes.SCALE);
/*    */             if (targetVector.y > this.minVerticalDisplacement * scale && targetVector.y < this.maxVerticalDisplacement * scale && Math.abs(targetVector.x) < this.maxHorizontalDisplacement * scale && Math.abs(targetVector.z) < this.maxHorizontalDisplacement * scale)
/*    */               return; 
/*    */             String verbose = String.format("%.5f/%.5f/%.5f", new Object[] { Float.valueOf(targetVector.x), Float.valueOf(targetVector.y), Float.valueOf(targetVector.z) });
/*    */             flagAndAlert(verbose);
/*    */           });
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsT.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */