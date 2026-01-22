/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.salmon.SalmonVariant;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.salmon.SalmonVariants;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
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
/*    */ public class SalmonSizeComponent
/*    */ {
/*    */   private SalmonVariant variant;
/*    */   
/*    */   public SalmonSizeComponent(SalmonVariant variant) {
/* 32 */     this.variant = variant;
/*    */   }
/*    */   
/*    */   public static SalmonSizeComponent read(PacketWrapper<?> wrapper) {
/* 36 */     SalmonVariant type = (SalmonVariant)wrapper.readMappedEntity((IRegistry)SalmonVariants.getRegistry());
/* 37 */     return new SalmonSizeComponent(type);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, SalmonSizeComponent component) {
/* 41 */     wrapper.writeMappedEntity((MappedEntity)component.variant);
/*    */   }
/*    */   
/*    */   public SalmonVariant getVariant() {
/* 45 */     return this.variant;
/*    */   }
/*    */   
/*    */   public void setVariant(SalmonVariant variant) {
/* 49 */     this.variant = variant;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 54 */     if (!(obj instanceof SalmonSizeComponent)) return false; 
/* 55 */     SalmonSizeComponent that = (SalmonSizeComponent)obj;
/* 56 */     return this.variant.equals(that.variant);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 61 */     return Objects.hashCode(this.variant);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\SalmonSizeComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */