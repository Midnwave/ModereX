/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*    */ import java.util.Map;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ final class MapTagResolver
/*    */   implements TagResolver.WithoutArguments, MappableResolver
/*    */ {
/*    */   private final Map<String, ? extends Tag> tagMap;
/*    */   
/*    */   MapTagResolver(@NotNull Map<String, ? extends Tag> placeholderMap) {
/* 36 */     this.tagMap = placeholderMap;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public Tag resolve(@NotNull String name) {
/* 41 */     return this.tagMap.get(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean contributeToMap(@NotNull Map<String, Tag> map) {
/* 46 */     map.putAll(this.tagMap);
/* 47 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object other) {
/* 52 */     if (this == other) {
/* 53 */       return true;
/*    */     }
/* 55 */     if (!(other instanceof MapTagResolver)) {
/* 56 */       return false;
/*    */     }
/* 58 */     MapTagResolver that = (MapTagResolver)other;
/* 59 */     return Objects.equals(this.tagMap, that.tagMap);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 64 */     return Objects.hash(new Object[] { this.tagMap });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\resolver\MapTagResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */