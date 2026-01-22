/*    */ package ac.grim.grimac.utils.data.tags;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import java.util.Set;
/*    */ import java.util.function.Function;
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
/*    */ public final class Builder<T>
/*    */ {
/*    */   private final ResourceLocation location;
/*    */   private Function<Integer, T> remapper;
/*    */   private Set<T> defaultValues;
/*    */   private boolean supported = true;
/*    */   
/*    */   private Builder(ResourceLocation location) {
/* 55 */     this.location = location;
/*    */   }
/*    */   
/*    */   public Builder<T> remapper(Function<Integer, T> remapper) {
/* 59 */     this.remapper = remapper;
/* 60 */     return this;
/*    */   }
/*    */   
/*    */   public Builder<T> supported(boolean supported) {
/* 64 */     this.supported = supported;
/* 65 */     return this;
/*    */   }
/*    */   
/*    */   public Builder<T> defaults(Set<T> defaultValues) {
/* 69 */     this.defaultValues = defaultValues;
/* 70 */     return this;
/*    */   }
/*    */   
/*    */   public SyncedTag<T> build() {
/* 74 */     return new SyncedTag<>(this.location, this.remapper, this.defaultValues, this.supported);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\tags\SyncedTag$Builder.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */