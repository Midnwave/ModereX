/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.tropicalfish.TropicalFishPattern;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.tropicalfish.TropicalFishPatterns;
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
/*    */ public class TropicalFishPatternComponent
/*    */ {
/*    */   private TropicalFishPattern variant;
/*    */   
/*    */   public TropicalFishPatternComponent(TropicalFishPattern variant) {
/* 32 */     this.variant = variant;
/*    */   }
/*    */   
/*    */   public static TropicalFishPatternComponent read(PacketWrapper<?> wrapper) {
/* 36 */     TropicalFishPattern type = (TropicalFishPattern)wrapper.readMappedEntity((IRegistry)TropicalFishPatterns.getRegistry());
/* 37 */     return new TropicalFishPatternComponent(type);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, TropicalFishPatternComponent component) {
/* 41 */     wrapper.writeMappedEntity((MappedEntity)component.variant);
/*    */   }
/*    */   
/*    */   public TropicalFishPattern getVariant() {
/* 45 */     return this.variant;
/*    */   }
/*    */   
/*    */   public void setVariant(TropicalFishPattern variant) {
/* 49 */     this.variant = variant;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 54 */     if (!(obj instanceof TropicalFishPatternComponent)) return false; 
/* 55 */     TropicalFishPatternComponent that = (TropicalFishPatternComponent)obj;
/* 56 */     return this.variant.equals(that.variant);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 61 */     return Objects.hashCode(this.variant);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\TropicalFishPatternComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */