/*    */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ final class ListTagBuilder<T extends BinaryTag>
/*    */   implements ListBinaryTag.Builder<T>
/*    */ {
/*    */   @Nullable
/*    */   private List<BinaryTag> tags;
/*    */   private final boolean permitsHeterogeneity;
/*    */   private BinaryTagType<? extends BinaryTag> elementType;
/*    */   
/*    */   ListTagBuilder(boolean permitsHeterogeneity) {
/* 37 */     this(permitsHeterogeneity, (BinaryTagType)BinaryTagTypes.END);
/*    */   }
/*    */   
/*    */   ListTagBuilder(boolean permitsHeterogeneity, BinaryTagType<? extends BinaryTag> type) {
/* 41 */     this.permitsHeterogeneity = permitsHeterogeneity;
/* 42 */     this.elementType = type;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public ListBinaryTag.Builder<T> add(BinaryTag tag) {
/* 48 */     this.elementType = (BinaryTagType)ListBinaryTagImpl.validateTagType(tag, this.elementType, this.permitsHeterogeneity);
/* 49 */     if (this.tags == null) {
/* 50 */       this.tags = new ArrayList<>();
/*    */     }
/* 52 */     this.tags.add(tag);
/* 53 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public ListBinaryTag.Builder<T> add(Iterable<? extends T> tagsToAdd) {
/* 58 */     for (BinaryTag binaryTag : tagsToAdd) {
/* 59 */       add(binaryTag);
/*    */     }
/* 61 */     return this;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public ListBinaryTag build() {
/* 66 */     if (this.tags == null) return ListBinaryTag.empty(); 
/* 67 */     return new ListBinaryTagImpl(this.elementType, this.permitsHeterogeneity, new ArrayList<>(this.tags));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\ListTagBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */