/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.wolfvariant.WolfVariant;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.wolfvariant.WolfVariants;
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
/*    */ public class WolfVariantComponent
/*    */ {
/*    */   private WolfVariant variant;
/*    */   
/*    */   public WolfVariantComponent(WolfVariant variant) {
/* 32 */     this.variant = variant;
/*    */   }
/*    */   
/*    */   public static WolfVariantComponent read(PacketWrapper<?> wrapper) {
/* 36 */     WolfVariant type = (WolfVariant)wrapper.readMappedEntity((IRegistry)WolfVariants.getRegistry());
/* 37 */     return new WolfVariantComponent(type);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, WolfVariantComponent component) {
/* 41 */     wrapper.writeMappedEntity((MappedEntity)component.variant);
/*    */   }
/*    */   
/*    */   public WolfVariant getVariant() {
/* 45 */     return this.variant;
/*    */   }
/*    */   
/*    */   public void setVariant(WolfVariant variant) {
/* 49 */     this.variant = variant;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 54 */     if (!(obj instanceof WolfVariantComponent)) return false; 
/* 55 */     WolfVariantComponent that = (WolfVariantComponent)obj;
/* 56 */     return this.variant.equals(that.variant);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 61 */     return Objects.hashCode(this.variant);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\WolfVariantComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */