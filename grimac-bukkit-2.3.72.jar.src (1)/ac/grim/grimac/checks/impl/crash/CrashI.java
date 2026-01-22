/*    */ package ac.grim.grimac.checks.impl.crash;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSelectBundleItem;
/*    */ 
/*    */ @CheckData(name = "CrashI")
/*    */ public class CrashI extends Check implements PacketCheck {
/*    */   public CrashI(GrimPlayer player) {
/* 14 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 19 */     if (event.getPacketType() == PacketType.Play.Client.SELECT_BUNDLE_ITEM) {
/*    */       int selectedItemIndex;
/*    */       try {
/* 22 */         selectedItemIndex = (new WrapperPlayClientSelectBundleItem(event)).getSelectedItemIndex();
/* 23 */       } catch (IllegalArgumentException e) {
/*    */         
/* 25 */         if (e.getMessage().startsWith("Invalid selectedItemIndex: ")) {
/* 26 */           selectedItemIndex = Integer.parseInt(e.getMessage().substring(27));
/*    */         } else {
/* 28 */           throw e;
/*    */         } 
/*    */       } 
/*    */       
/* 32 */       if (selectedItemIndex < -1) {
/* 33 */         flagAndAlert("selectedItemIndex=" + selectedItemIndex);
/* 34 */         event.setCancelled(true);
/* 35 */         this.player.onPacketCancel();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\crash\CrashI.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */