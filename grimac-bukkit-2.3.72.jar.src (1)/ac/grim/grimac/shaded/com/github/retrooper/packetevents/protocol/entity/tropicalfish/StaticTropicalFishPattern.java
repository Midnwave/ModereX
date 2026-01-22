/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.tropicalfish;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.AbstractMappedEntity;
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
/*    */ public class StaticTropicalFishPattern
/*    */   extends AbstractMappedEntity
/*    */   implements TropicalFishPattern
/*    */ {
/*    */   private final TropicalFishPattern.Base base;
/*    */   
/*    */   @Internal
/*    */   public StaticTropicalFishPattern(@Nullable TypesBuilderData data, TropicalFishPattern.Base base) {
/* 30 */     super(data);
/* 31 */     this.base = base;
/*    */   }
/*    */ 
/*    */   
/*    */   public TropicalFishPattern.Base getBase() {
/* 36 */     return this.base;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\tropicalfish\StaticTropicalFishPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */