/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.AbstractMappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*    */ public class StaticAttribute
/*    */   extends AbstractMappedEntity
/*    */   implements Attribute
/*    */ {
/*    */   @Nullable
/*    */   private final ResourceLocation legacyName;
/*    */   private final double defaultValue;
/*    */   private final double minValue;
/*    */   private final double maxValue;
/*    */   
/*    */   @Internal
/*    */   public StaticAttribute(@Nullable TypesBuilderData data, String legacyPrefix, double defaultValue, double minValue, double maxValue) {
/* 40 */     super(data);
/* 41 */     this.defaultValue = defaultValue;
/* 42 */     this.minValue = minValue;
/* 43 */     this.maxValue = maxValue;
/* 44 */     this
/* 45 */       .legacyName = (legacyPrefix == null || data == null) ? null : new ResourceLocation(data.getName().getNamespace(), legacyPrefix + "." + data.getName().getKey());
/*    */   }
/*    */ 
/*    */   
/*    */   public ResourceLocation getName(ClientVersion version) {
/* 50 */     if (this.data == null) {
/* 51 */       throw new UnsupportedOperationException();
/*    */     }
/* 53 */     return (version.isNewerThanOrEquals(ClientVersion.V_1_21_2) || this.legacyName == null) ? 
/* 54 */       this.data.getName() : this.legacyName;
/*    */   }
/*    */ 
/*    */   
/*    */   public double getDefaultValue() {
/* 59 */     return this.defaultValue;
/*    */   }
/*    */ 
/*    */   
/*    */   public double getMinValue() {
/* 64 */     return this.minValue;
/*    */   }
/*    */ 
/*    */   
/*    */   public double getMaxValue() {
/* 69 */     return this.maxValue;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\attribute\StaticAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */