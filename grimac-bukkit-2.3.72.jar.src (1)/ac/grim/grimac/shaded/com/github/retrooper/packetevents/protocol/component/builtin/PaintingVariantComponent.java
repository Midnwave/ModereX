/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.painting.PaintingVariant;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.painting.PaintingVariants;
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
/*    */ public class PaintingVariantComponent
/*    */ {
/*    */   private PaintingVariant variant;
/*    */   
/*    */   public PaintingVariantComponent(PaintingVariant variant) {
/* 32 */     this.variant = variant;
/*    */   }
/*    */   
/*    */   public static PaintingVariantComponent read(PacketWrapper<?> wrapper) {
/* 36 */     PaintingVariant variant = (PaintingVariant)wrapper.readMappedEntity((IRegistry)PaintingVariants.getRegistry());
/* 37 */     return new PaintingVariantComponent(variant);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, PaintingVariantComponent component) {
/* 41 */     wrapper.writeMappedEntity((MappedEntity)component.variant);
/*    */   }
/*    */   
/*    */   public PaintingVariant getVariant() {
/* 45 */     return this.variant;
/*    */   }
/*    */   
/*    */   public void setVariant(PaintingVariant variant) {
/* 49 */     this.variant = variant;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 54 */     if (!(obj instanceof PaintingVariantComponent)) return false; 
/* 55 */     PaintingVariantComponent that = (PaintingVariantComponent)obj;
/* 56 */     return this.variant.equals(that.variant);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 61 */     return Objects.hashCode(this.variant);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\PaintingVariantComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */