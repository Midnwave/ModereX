/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.cat.CatVariant;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.cat.CatVariants;
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
/*    */ public class CatVariantComponent
/*    */ {
/*    */   private CatVariant variant;
/*    */   
/*    */   public CatVariantComponent(CatVariant variant) {
/* 32 */     this.variant = variant;
/*    */   }
/*    */   
/*    */   public static CatVariantComponent read(PacketWrapper<?> wrapper) {
/* 36 */     CatVariant type = (CatVariant)wrapper.readMappedEntity((IRegistry)CatVariants.getRegistry());
/* 37 */     return new CatVariantComponent(type);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, CatVariantComponent component) {
/* 41 */     wrapper.writeMappedEntity((MappedEntity)component.variant);
/*    */   }
/*    */   
/*    */   public CatVariant getVariant() {
/* 45 */     return this.variant;
/*    */   }
/*    */   
/*    */   public void setVariant(CatVariant variant) {
/* 49 */     this.variant = variant;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 54 */     if (!(obj instanceof CatVariantComponent)) return false; 
/* 55 */     CatVariantComponent that = (CatVariantComponent)obj;
/* 56 */     return this.variant.equals(that.variant);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 61 */     return Objects.hashCode(this.variant);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\CatVariantComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */