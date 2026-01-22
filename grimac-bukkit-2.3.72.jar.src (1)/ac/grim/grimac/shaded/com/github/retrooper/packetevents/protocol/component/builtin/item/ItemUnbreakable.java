/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*    */ import java.util.Objects;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ItemUnbreakable
/*    */ {
/*    */   @Obsolete
/*    */   private boolean showInTooltip;
/*    */   
/*    */   public ItemUnbreakable() {
/* 36 */     this(true);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Obsolete
/*    */   public ItemUnbreakable(boolean showInTooltip) {
/* 44 */     this.showInTooltip = showInTooltip;
/*    */   }
/*    */   
/*    */   public static ItemUnbreakable read(PacketWrapper<?> wrapper) {
/* 48 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5)) {
/* 49 */       return new ItemUnbreakable();
/*    */     }
/* 51 */     boolean showInTooltip = wrapper.readBoolean();
/* 52 */     return new ItemUnbreakable(showInTooltip);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemUnbreakable value) {
/* 56 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_21_5)) {
/* 57 */       wrapper.writeBoolean(value.showInTooltip);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Obsolete
/*    */   public boolean isShowInTooltip() {
/* 66 */     return this.showInTooltip;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Obsolete
/*    */   public void setShowInTooltip(boolean showInTooltip) {
/* 74 */     this.showInTooltip = showInTooltip;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 79 */     if (!(obj instanceof ItemUnbreakable)) return false; 
/* 80 */     ItemUnbreakable that = (ItemUnbreakable)obj;
/* 81 */     return (this.showInTooltip == that.showInTooltip);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 86 */     return Objects.hashCode(Boolean.valueOf(this.showInTooltip));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemUnbreakable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */