/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.color.DyeColor;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ public class WolfCollarComponent
/*    */ {
/*    */   private DyeColor color;
/*    */   
/*    */   public WolfCollarComponent(DyeColor color) {
/* 31 */     this.color = color;
/*    */   }
/*    */   
/*    */   public static WolfCollarComponent read(PacketWrapper<?> wrapper) {
/* 35 */     DyeColor type = DyeColor.read(wrapper);
/* 36 */     return new WolfCollarComponent(type);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, WolfCollarComponent component) {
/* 40 */     DyeColor.write(wrapper, component.color);
/*    */   }
/*    */   
/*    */   public DyeColor getDyeColor() {
/* 44 */     return this.color;
/*    */   }
/*    */   
/*    */   public void setDyeColor(DyeColor color) {
/* 48 */     this.color = color;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 53 */     if (!(obj instanceof WolfCollarComponent)) return false; 
/* 54 */     WolfCollarComponent that = (WolfCollarComponent)obj;
/* 55 */     return this.color.equals(that.color);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 60 */     return Objects.hashCode(this.color);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\WolfCollarComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */