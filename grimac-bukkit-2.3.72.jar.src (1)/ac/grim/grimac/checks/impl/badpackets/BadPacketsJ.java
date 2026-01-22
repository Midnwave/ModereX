/*    */ package ac.grim.grimac.checks.impl.badpackets;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUseItem;
/*    */ import ac.grim.grimac.utils.data.HeadRotation;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ @CheckData(name = "BadPacketsJ", description = "Rotation in use item packet did not match tick rotation")
/*    */ public class BadPacketsJ
/*    */   extends Check implements PacketCheck {
/* 21 */   private final List<HeadRotation> rotations = new ArrayList<>();
/*    */   
/*    */   public BadPacketsJ(GrimPlayer player) {
/* 24 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 29 */     if (this.player.gamemode == GameMode.SPECTATOR) {
/* 30 */       this.rotations.clear();
/*    */       
/*    */       return;
/*    */     } 
/* 34 */     if (event.getPacketType() == PacketType.Play.Client.USE_ITEM && this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21) && 
/* 35 */       PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_21)) {
/* 36 */       WrapperPlayClientUseItem packet = new WrapperPlayClientUseItem(event);
/* 37 */       this.rotations.add(new HeadRotation(packet.getYaw(), packet.getPitch()));
/*    */     } 
/*    */     
/* 40 */     if (isTickPacket(event.getPacketType())) {
/*    */       
/* 42 */       boolean allowLast = (this.player.canSkipTicks() && (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION || event.getPacketType() == PacketType.Play.Client.PLAYER_ROTATION));
/* 43 */       for (HeadRotation rotation : this.rotations) {
/* 44 */         if (rotation.getYaw() == this.player.xRot && rotation.getPitch() == this.player.yRot) {
/* 45 */           allowLast = false;
/*    */           
/*    */           continue;
/*    */         } 
/* 49 */         if (rotation.getYaw() == this.player.lastXRot && rotation.getPitch() == this.player.lastYRot && allowLast) {
/*    */           continue;
/*    */         }
/*    */         
/* 53 */         flagAndAlert();
/*    */       } 
/*    */       
/* 56 */       this.rotations.clear();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsJ.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */