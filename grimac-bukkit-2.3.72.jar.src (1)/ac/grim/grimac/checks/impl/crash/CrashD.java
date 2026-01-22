/*    */ package ac.grim.grimac.checks.impl.crash;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
/*    */ import ac.grim.grimac.utils.inventory.inventory.MenuType;
/*    */ 
/*    */ @CheckData(name = "CrashD", description = "Clicking slots in lectern window")
/*    */ public class CrashD
/*    */   extends Check implements PacketCheck {
/* 19 */   private MenuType type = MenuType.UNKNOWN;
/* 20 */   private int lecternId = -1;
/*    */   
/*    */   public CrashD(GrimPlayer playerData) {
/* 23 */     super(playerData);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketSend(PacketSendEvent event) {
/* 28 */     if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW && isSupportedVersion()) {
/* 29 */       WrapperPlayServerOpenWindow window = new WrapperPlayServerOpenWindow(event);
/* 30 */       this.type = MenuType.getMenuType(window.getType());
/* 31 */       if (this.type == MenuType.LECTERN) this.lecternId = window.getContainerId();
/*    */     
/*    */     } 
/*    */   }
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 37 */     if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW && isSupportedVersion()) {
/* 38 */       WrapperPlayClientClickWindow click = new WrapperPlayClientClickWindow(event);
/* 39 */       int clickType = click.getWindowClickType().ordinal();
/* 40 */       int button = click.getButton();
/* 41 */       int windowId = click.getWindowId();
/*    */       
/* 43 */       if (this.type == MenuType.LECTERN && windowId > 0 && windowId == this.lecternId && 
/* 44 */         flagAndAlert("clickType=" + clickType + " button=" + button)) {
/* 45 */         event.setCancelled(true);
/* 46 */         this.player.onPacketCancel();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   private boolean isSupportedVersion() {
/* 53 */     return PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_14);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\crash\CrashD.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */