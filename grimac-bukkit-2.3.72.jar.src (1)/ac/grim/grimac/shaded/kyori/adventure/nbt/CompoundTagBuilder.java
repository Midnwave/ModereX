/*    */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.function.Consumer;
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
/*    */ final class CompoundTagBuilder
/*    */   implements CompoundBinaryTag.Builder
/*    */ {
/*    */   @Nullable
/*    */   private Map<String, BinaryTag> tags;
/*    */   
/*    */   private Map<String, BinaryTag> tags() {
/* 36 */     if (this.tags == null) {
/* 37 */       this.tags = new HashMap<>();
/*    */     }
/* 39 */     return this.tags;
/*    */   }
/*    */ 
/*    */   
/*    */   public CompoundBinaryTag.Builder put(@NotNull String key, @NotNull BinaryTag tag) {
/* 44 */     tags().put(key, tag);
/* 45 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public CompoundBinaryTag.Builder put(@NotNull CompoundBinaryTag tag) {
/* 50 */     Map<String, BinaryTag> tags = tags();
/* 51 */     for (String key : tag.keySet()) {
/* 52 */       tags.put(key, tag.get(key));
/*    */     }
/* 54 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public CompoundBinaryTag.Builder put(@NotNull Map<String, ? extends BinaryTag> tags) {
/* 59 */     tags().putAll(tags);
/* 60 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public CompoundBinaryTag.Builder remove(@NotNull String key, @Nullable Consumer<? super BinaryTag> removed) {
/* 65 */     if (this.tags != null) {
/* 66 */       BinaryTag tag = this.tags.remove(key);
/* 67 */       if (removed != null) {
/* 68 */         removed.accept(tag);
/*    */       }
/*    */     } 
/* 71 */     return this;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public CompoundBinaryTag build() {
/* 76 */     if (this.tags == null) return CompoundBinaryTag.empty(); 
/* 77 */     return new CompoundBinaryTagImpl(new HashMap<>(this.tags));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\CompoundTagBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */