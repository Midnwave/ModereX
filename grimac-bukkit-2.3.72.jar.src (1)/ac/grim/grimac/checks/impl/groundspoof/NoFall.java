/*    */ package ac.grim.grimac.checks.impl.groundspoof;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.predictionengine.GhostBlockDetector;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*    */ import ac.grim.grimac.utils.nmsutil.Collisions;
/*    */ import ac.grim.grimac.utils.nmsutil.GetBoundingBox;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ @CheckData(name = "NoFall", setback = 10.0D)
/*    */ public class NoFall
/*    */   extends Check
/*    */   implements PacketCheck
/*    */ {
/*    */   public boolean flipPlayerGroundStatus = false;
/*    */   
/*    */   public NoFall(GrimPlayer player) {
/* 26 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 31 */     if (event.getPacketType() == PacketType.Play.Client.PLAYER_FLYING || event.getPacketType() == PacketType.Play.Client.PLAYER_ROTATION) {
/*    */       
/* 33 */       if (this.player.getSetbackTeleportUtil().insideUnloadedChunk())
/*    */         return; 
/* 35 */       if ((this.player.getSetbackTeleportUtil()).blockOffsets)
/*    */         return; 
/* 37 */       WrapperPlayClientPlayerFlying wrapper = new WrapperPlayClientPlayerFlying(event);
/*    */ 
/*    */ 
/*    */       
/* 41 */       if (wrapper.isOnGround() && !wrapper.hasPositionChanged() && 
/* 42 */         !isNearGround(wrapper.isOnGround())) {
/*    */         
/* 44 */         if (!GhostBlockDetector.isGhostBlock(this.player)) flagAndAlertWithSetback(); 
/* 45 */         if (shouldModifyPackets()) {
/* 46 */           wrapper.setOnGround(false);
/* 47 */           event.markForReEncode(true);
/*    */         } 
/*    */       } 
/*    */     } 
/*    */ 
/*    */     
/* 53 */     if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
/* 54 */       WrapperPlayClientPlayerFlying wrapper = new WrapperPlayClientPlayerFlying(event);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 62 */       if (this.flipPlayerGroundStatus) {
/* 63 */         this.flipPlayerGroundStatus = false;
/* 64 */         if (shouldModifyPackets()) {
/* 65 */           wrapper.setOnGround(!wrapper.isOnGround());
/* 66 */           event.markForReEncode(true);
/*    */         } 
/*    */       } 
/* 69 */       if (this.player.packetStateData.lastPacketWasTeleport && 
/* 70 */         shouldModifyPackets()) {
/* 71 */         wrapper.setOnGround(false);
/* 72 */         event.markForReEncode(true);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   private boolean isNearGround(boolean onGround) {
/* 79 */     if (onGround) {
/* 80 */       SimpleCollisionBox feetBB = GetBoundingBox.getBoundingBoxFromPosAndSize(this.player, this.player.x, this.player.y, this.player.z, 0.6F, 0.001F);
/* 81 */       feetBB.expand(this.player.getMovementThreshold());
/*    */       
/* 83 */       return checkForBoxes(feetBB);
/*    */     } 
/* 85 */     return true;
/*    */   }
/*    */   
/*    */   private boolean checkForBoxes(SimpleCollisionBox playerBB) {
/* 89 */     List<SimpleCollisionBox> boxes = new ArrayList<>();
/* 90 */     Collisions.getCollisionBoxes(this.player, playerBB, boxes, false);
/*    */     
/* 92 */     for (SimpleCollisionBox box : boxes) {
/* 93 */       if (playerBB.collidesVertically(box)) {
/* 94 */         return true;
/*    */       }
/*    */     } 
/*    */     
/* 98 */     return this.player.compensatedWorld.isNearHardEntity(playerBB.copy().expand(4.0D));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\groundspoof\NoFall.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */