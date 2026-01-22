/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.AbstractMappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.IdentityHashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Optional;
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
/*    */ public class StaticEntityType
/*    */   extends AbstractMappedEntity
/*    */   implements EntityType
/*    */ {
/*    */   private final Optional<EntityType> parent;
/*    */   private final Map<EntityType, Boolean> parents;
/*    */   @Nullable
/*    */   private TypesBuilderData legacyData;
/*    */   
/*    */   @Internal
/*    */   public StaticEntityType(@Nullable TypesBuilderData data, @Nullable EntityType parent) {
/* 40 */     super(data);
/* 41 */     this.parent = Optional.ofNullable(parent);
/*    */ 
/*    */     
/* 44 */     this.parents = new IdentityHashMap<>();
/* 45 */     this.parents.put(this, Boolean.valueOf(true));
/* 46 */     while (parent != null) {
/* 47 */       this.parents.put(parent, Boolean.valueOf(true));
/* 48 */       parent = parent.getParent().orElse(null);
/*    */     } 
/*    */   }
/*    */   
/*    */   StaticEntityType setLegacyData(@Nullable TypesBuilderData legacyData) {
/* 53 */     this.legacyData = legacyData;
/* 54 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isInstanceOf(EntityType parent) {
/* 59 */     return (parent != null && this.parents.containsKey(parent));
/*    */   }
/*    */ 
/*    */   
/*    */   public Optional<EntityType> getParent() {
/* 64 */     return this.parent;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getLegacyId(ClientVersion version) {
/* 69 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_14))
/* 70 */       return -1; 
/* 71 */     if (this.legacyData != null) {
/* 72 */       return this.legacyData.getId(version);
/*    */     }
/* 74 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\type\StaticEntityType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */