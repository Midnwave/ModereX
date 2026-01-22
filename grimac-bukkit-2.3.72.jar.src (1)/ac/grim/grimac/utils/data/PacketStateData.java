/*    */ package ac.grim.grimac.utils.data;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.InteractionHand;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public class PacketStateData
/*    */ {
/*    */   public boolean packetPlayerOnGround = false;
/*    */   public boolean lastPacketWasTeleport = false;
/*    */   public boolean cancelDuplicatePacket;
/*    */   public boolean lastPacketWasOnePointSeventeenDuplicate = false;
/*    */   public boolean lastTransactionPacketWasValid = false;
/*    */   public int lastSlotSelected;
/* 15 */   public InteractionHand itemInUseHand = InteractionHand.MAIN_HAND;
/* 16 */   public long lastRiptide = 0L;
/*    */   public boolean tryingToRiptide = false;
/* 18 */   public int slowedByUsingItemTransaction = Integer.MIN_VALUE;
/*    */   
/*    */   public boolean receivedSteerVehicle = false;
/*    */   
/*    */   public boolean didLastLastMovementIncludePosition = false;
/*    */   public boolean didLastMovementIncludePosition = false;
/*    */   public boolean didSendMovementBeforeTickEnd = false;
/* 25 */   public KnownInput knownInput = KnownInput.DEFAULT;
/* 26 */   public Vector3d lastClaimedPosition = new Vector3d(0.0D, 0.0D, 0.0D); public float lastHealth;
/*    */   public float lastSaturation;
/*    */   public int lastFood;
/*    */   public boolean lastServerTransWasValid = false;
/* 30 */   private int slowedByUsingItemSlot = Integer.MIN_VALUE; @Generated public int getSlowedByUsingItemSlot() { return this.slowedByUsingItemSlot; }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean horseInteractCausedForcedRotation = false;
/*    */   
/*    */   public void setSlowedByUsingItem(boolean slowedByUsingItem) {
/* 37 */     this.slowedByUsingItemSlot = slowedByUsingItem ? this.lastSlotSelected : Integer.MIN_VALUE;
/*    */   }
/*    */   
/*    */   public boolean isSlowedByUsingItem() {
/* 41 */     return (this.slowedByUsingItemSlot != Integer.MIN_VALUE);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\PacketStateData.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */