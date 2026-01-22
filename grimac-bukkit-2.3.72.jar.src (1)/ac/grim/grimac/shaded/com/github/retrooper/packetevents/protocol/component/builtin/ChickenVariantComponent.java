/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.chicken.ChickenVariant;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.chicken.ChickenVariants;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MaybeMappedEntity;
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
/*    */ 
/*    */ public class ChickenVariantComponent
/*    */ {
/*    */   private MaybeMappedEntity<ChickenVariant> variant;
/*    */   
/*    */   public ChickenVariantComponent(MaybeMappedEntity<ChickenVariant> variant) {
/* 33 */     this.variant = variant;
/*    */   }
/*    */   
/*    */   public static ChickenVariantComponent read(PacketWrapper<?> wrapper) {
/* 37 */     MaybeMappedEntity<ChickenVariant> variant = MaybeMappedEntity.read(wrapper, 
/* 38 */         (IRegistry)ChickenVariants.getRegistry(), ChickenVariant::read);
/* 39 */     return new ChickenVariantComponent(variant);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ChickenVariantComponent component) {
/* 43 */     MaybeMappedEntity.write(wrapper, component.variant, ChickenVariant::write);
/*    */   }
/*    */   
/*    */   public MaybeMappedEntity<ChickenVariant> getVariant() {
/* 47 */     return this.variant;
/*    */   }
/*    */   
/*    */   public void setVariant(MaybeMappedEntity<ChickenVariant> variant) {
/* 51 */     this.variant = variant;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 56 */     if (!(obj instanceof ChickenVariantComponent)) return false; 
/* 57 */     ChickenVariantComponent that = (ChickenVariantComponent)obj;
/* 58 */     return this.variant.equals(that.variant);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 63 */     return Objects.hashCode(this.variant);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\ChickenVariantComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */