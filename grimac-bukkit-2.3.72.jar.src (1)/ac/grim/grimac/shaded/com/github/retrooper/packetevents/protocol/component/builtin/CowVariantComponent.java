/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.cow.CowVariant;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.cow.CowVariants;
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
/*    */ public class CowVariantComponent
/*    */ {
/*    */   private CowVariant variant;
/*    */   
/*    */   public CowVariantComponent(CowVariant variant) {
/* 32 */     this.variant = variant;
/*    */   }
/*    */   
/*    */   public static CowVariantComponent read(PacketWrapper<?> wrapper) {
/* 36 */     CowVariant type = (CowVariant)wrapper.readMappedEntity((IRegistry)CowVariants.getRegistry());
/* 37 */     return new CowVariantComponent(type);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, CowVariantComponent component) {
/* 41 */     wrapper.writeMappedEntity((MappedEntity)component.variant);
/*    */   }
/*    */   
/*    */   public CowVariant getVariant() {
/* 45 */     return this.variant;
/*    */   }
/*    */   
/*    */   public void setVariant(CowVariant variant) {
/* 49 */     this.variant = variant;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 54 */     if (!(obj instanceof CowVariantComponent)) return false; 
/* 55 */     CowVariantComponent that = (CowVariantComponent)obj;
/* 56 */     return this.variant.equals(that.variant);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 61 */     return Objects.hashCode(this.variant);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\CowVariantComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */