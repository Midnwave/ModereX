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
/*    */ public class ItemDyeColor
/*    */ {
/*    */   private int rgb;
/*    */   @Obsolete
/*    */   private boolean showInTooltip;
/*    */   
/*    */   public ItemDyeColor(int rgb) {
/* 37 */     this(rgb, true);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Obsolete
/*    */   public ItemDyeColor(int rgb, boolean showInTooltip) {
/* 45 */     this.rgb = rgb;
/* 46 */     this.showInTooltip = showInTooltip;
/*    */   }
/*    */   
/*    */   public static ItemDyeColor read(PacketWrapper<?> wrapper) {
/* 50 */     int rgb = wrapper.readInt();
/* 51 */     boolean showInTooltip = (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5) || wrapper.readBoolean());
/* 52 */     return new ItemDyeColor(rgb, showInTooltip);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemDyeColor color) {
/* 56 */     wrapper.writeInt(color.rgb);
/* 57 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_21_5)) {
/* 58 */       wrapper.writeBoolean(color.showInTooltip);
/*    */     }
/*    */   }
/*    */   
/*    */   public int getRgb() {
/* 63 */     return this.rgb;
/*    */   }
/*    */   
/*    */   public void setRgb(int rgb) {
/* 67 */     this.rgb = rgb;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Obsolete
/*    */   public boolean isShowInTooltip() {
/* 75 */     return this.showInTooltip;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Obsolete
/*    */   public void setShowInTooltip(boolean showInTooltip) {
/* 83 */     this.showInTooltip = showInTooltip;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 88 */     if (this == obj) return true; 
/* 89 */     if (!(obj instanceof ItemDyeColor)) return false; 
/* 90 */     ItemDyeColor that = (ItemDyeColor)obj;
/* 91 */     if (this.rgb != that.rgb) return false; 
/* 92 */     return (this.showInTooltip == that.showInTooltip);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 97 */     return Objects.hash(new Object[] { Integer.valueOf(this.rgb), Boolean.valueOf(this.showInTooltip) });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemDyeColor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */