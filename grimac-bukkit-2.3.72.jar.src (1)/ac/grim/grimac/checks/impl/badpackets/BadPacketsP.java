/*    */ package ac.grim.grimac.checks.impl.badpackets;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
/*    */ 
/*    */ @CheckData(name = "BadPacketsP", description = "Invalid click packets", experimental = true)
/*    */ public class BadPacketsP
/*    */   extends Check
/*    */   implements PacketCheck {
/* 17 */   private int containerType = -1;
/* 18 */   private int containerId = -1;
/*    */   
/*    */   public BadPacketsP(GrimPlayer playerData) {
/* 21 */     super(playerData);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketSend(PacketSendEvent event) {
/* 26 */     if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW) {
/* 27 */       WrapperPlayServerOpenWindow window = new WrapperPlayServerOpenWindow(event);
/* 28 */       this.containerType = window.getType();
/* 29 */       this.containerId = window.getContainerId();
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 35 */     if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
/* 36 */       WrapperPlayClientClickWindow wrapper = new WrapperPlayClientClickWindow(event);
/* 37 */       WrapperPlayClientClickWindow.WindowClickType clickType = wrapper.getWindowClickType();
/* 38 */       int button = wrapper.getButton();
/*    */ 
/*    */       
/* 41 */       switch (clickType) { default: throw new IncompatibleClassChangeError();
/* 42 */         case PICKUP: case QUICK_MOVE: case CLONE: if (button > 2 || button < 0);
/* 43 */         case SWAP: if ((button > 8 || button < 0) && button != 40);
/* 44 */         case THROW: if (button != 0 && button != 1);
/* 45 */         case QUICK_CRAFT: if (button == 3 || button == 7 || button > 10 || button < 0);
/* 46 */         case PICKUP_ALL: if (button != 0);
/* 47 */         case UNKNOWN: break; }  boolean flag = true;
/*    */ 
/*    */ 
/*    */       
/* 51 */       if (flag && 
/* 52 */         flagAndAlert("clickType=" + clickType.toString().toLowerCase() + ", button=" + button + ((wrapper.getWindowId() == this.containerId) ? (", container=" + this.containerType) : "")) && shouldModifyPackets()) {
/* 53 */         event.setCancelled(true);
/* 54 */         this.player.onPacketCancel();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsP.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */