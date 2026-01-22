/*    */ package ac.grim.grimac.utils.inventory.inventory;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
/*    */ import ac.grim.grimac.utils.inventory.Inventory;
/*    */ 
/*    */ public class NotImplementedMenu extends AbstractContainerMenu {
/*    */   public NotImplementedMenu(GrimPlayer player, Inventory playerInventory) {
/*  9 */     super(player, playerInventory);
/* 10 */     player.inventory.isPacketInventoryActive = false;
/* 11 */     player.inventory.needResend = true;
/*    */   }
/*    */   
/*    */   public void doClick(int button, int slotID, WrapperPlayClientClickWindow.WindowClickType clickType) {}
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\inventory\inventory\NotImplementedMenu.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */