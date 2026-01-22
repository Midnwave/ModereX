/*    */ package ac.grim.grimac.utils.data.tags;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTags;
/*    */ import java.util.Collections;
/*    */ import java.util.IdentityHashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Set;
/*    */ import java.util.function.Function;
/*    */ 
/*    */ public final class SyncedTag<T>
/*    */ {
/*    */   private final ResourceLocation location;
/*    */   private final Set<T> values;
/*    */   private final Function<Integer, T> remapper;
/*    */   private final boolean supported;
/*    */   
/*    */   private SyncedTag(ResourceLocation location, Function<Integer, T> remapper, Set<T> defaultValues, boolean supported) {
/* 19 */     this.location = location;
/* 20 */     this.supported = supported;
/* 21 */     this.values = Collections.newSetFromMap(new IdentityHashMap<>());
/* 22 */     this.remapper = remapper;
/* 23 */     this.values.addAll(defaultValues);
/*    */   }
/*    */   
/*    */   public static <T> Builder<T> builder(ResourceLocation location) {
/* 27 */     return new Builder<>(location);
/*    */   }
/*    */   
/*    */   public ResourceLocation location() {
/* 31 */     return this.location;
/*    */   }
/*    */   
/*    */   public boolean contains(T value) {
/* 35 */     return this.values.contains(value);
/*    */   }
/*    */   
/*    */   public void readTagValues(WrapperPlayServerTags.Tag tag) {
/* 39 */     if (!this.supported) {
/*    */       return;
/*    */     }
/* 42 */     this.values.clear();
/* 43 */     for (Iterator<Integer> iterator = tag.getValues().iterator(); iterator.hasNext(); ) { int id = ((Integer)iterator.next()).intValue();
/* 44 */       this.values.add(this.remapper.apply(Integer.valueOf(id))); }
/*    */   
/*    */   }
/*    */   
/*    */   public static final class Builder<T> {
/*    */     private final ResourceLocation location;
/*    */     private Function<Integer, T> remapper;
/*    */     private Set<T> defaultValues;
/*    */     private boolean supported = true;
/*    */     
/*    */     private Builder(ResourceLocation location) {
/* 55 */       this.location = location;
/*    */     }
/*    */     
/*    */     public Builder<T> remapper(Function<Integer, T> remapper) {
/* 59 */       this.remapper = remapper;
/* 60 */       return this;
/*    */     }
/*    */     
/*    */     public Builder<T> supported(boolean supported) {
/* 64 */       this.supported = supported;
/* 65 */       return this;
/*    */     }
/*    */     
/*    */     public Builder<T> defaults(Set<T> defaultValues) {
/* 69 */       this.defaultValues = defaultValues;
/* 70 */       return this;
/*    */     }
/*    */     
/*    */     public SyncedTag<T> build() {
/* 74 */       return new SyncedTag<>(this.location, this.remapper, this.defaultValues, this.supported);
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\tags\SyncedTag.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */