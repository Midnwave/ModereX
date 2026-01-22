/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.parrot.ParrotVariant;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.parrot.ParrotVariants;
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
/*    */ public class ParrotVariantComponent
/*    */ {
/*    */   private ParrotVariant variant;
/*    */   
/*    */   public ParrotVariantComponent(ParrotVariant variant) {
/* 32 */     this.variant = variant;
/*    */   }
/*    */   
/*    */   public static ParrotVariantComponent read(PacketWrapper<?> wrapper) {
/* 36 */     ParrotVariant type = (ParrotVariant)wrapper.readMappedEntity((IRegistry)ParrotVariants.getRegistry());
/* 37 */     return new ParrotVariantComponent(type);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ParrotVariantComponent component) {
/* 41 */     wrapper.writeMappedEntity((MappedEntity)component.variant);
/*    */   }
/*    */   
/*    */   public ParrotVariant getVariant() {
/* 45 */     return this.variant;
/*    */   }
/*    */   
/*    */   public void setVariant(ParrotVariant variant) {
/* 49 */     this.variant = variant;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 54 */     if (!(obj instanceof ParrotVariantComponent)) return false; 
/* 55 */     ParrotVariantComponent that = (ParrotVariantComponent)obj;
/* 56 */     return this.variant.equals(that.variant);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 61 */     return Objects.hashCode(this.variant);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\ParrotVariantComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */