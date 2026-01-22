/*    */ package ac.grim.grimac.checks.impl.crash;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
/*    */ 
/*    */ @CheckData(name = "CrashF")
/*    */ public class CrashF
/*    */   extends Check
/*    */   implements PacketCheck {
/*    */   public CrashF(GrimPlayer playerData) {
/* 16 */     super(playerData);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 21 */     if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
/* 22 */       WrapperPlayClientClickWindow click = new WrapperPlayClientClickWindow(event);
/* 23 */       WrapperPlayClientClickWindow.WindowClickType clickType = click.getWindowClickType();
/* 24 */       int button = click.getButton();
/* 25 */       int windowId = click.getWindowId();
/* 26 */       int slot = click.getSlot();
/*    */       
/* 28 */       if ((clickType == WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE || clickType == WrapperPlayClientClickWindow.WindowClickType.SWAP) && windowId >= 0 && button < 0) {
/* 29 */         if (flagAndAlert("clickType=" + String.valueOf(clickType) + " button=" + button)) {
/* 30 */           event.setCancelled(true);
/* 31 */           this.player.onPacketCancel();
/*    */         } 
/* 33 */       } else if (windowId >= 0 && clickType == WrapperPlayClientClickWindow.WindowClickType.SWAP && slot < 0 && 
/* 34 */         flagAndAlert("clickType=" + String.valueOf(clickType) + " button=" + button + " slot=" + slot)) {
/* 35 */         event.setCancelled(true);
/* 36 */         this.player.onPacketCancel();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\crash\CrashF.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */