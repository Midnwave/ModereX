/*     */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Buildable;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class StorageNBTComponentImpl
/*     */   extends NBTComponentImpl<StorageNBTComponent, StorageNBTComponent.Builder>
/*     */   implements StorageNBTComponent
/*     */ {
/*     */   private final Key storage;
/*     */   
/*     */   @NotNull
/*     */   static StorageNBTComponent create(@NotNull List<? extends ComponentLike> children, @NotNull Style style, String nbtPath, boolean interpret, @Nullable ComponentLike separator, @NotNull Key storage) {
/*  40 */     return new StorageNBTComponentImpl(
/*  41 */         ComponentLike.asComponents(children, IS_NOT_EMPTY), 
/*  42 */         Objects.<Style>requireNonNull(style, "style"), 
/*  43 */         Objects.<String>requireNonNull(nbtPath, "nbtPath"), interpret, 
/*     */         
/*  45 */         ComponentLike.unbox(separator), 
/*  46 */         Objects.<Key>requireNonNull(storage, "storage"));
/*     */   }
/*     */ 
/*     */   
/*     */   StorageNBTComponentImpl(@NotNull List<Component> children, @NotNull Style style, String nbtPath, boolean interpret, @Nullable Component separator, Key storage) {
/*  51 */     super(children, style, nbtPath, interpret, separator);
/*  52 */     this.storage = storage;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public StorageNBTComponent nbtPath(@NotNull String nbtPath) {
/*  57 */     if (Objects.equals(this.nbtPath, nbtPath)) return this; 
/*  58 */     return create((List)this.children, this.style, nbtPath, this.interpret, this.separator, this.storage);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public StorageNBTComponent interpret(boolean interpret) {
/*  63 */     if (this.interpret == interpret) return this; 
/*  64 */     return create((List)this.children, this.style, this.nbtPath, interpret, this.separator, this.storage);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Component separator() {
/*  69 */     return this.separator;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public StorageNBTComponent separator(@Nullable ComponentLike separator) {
/*  74 */     return create((List)this.children, this.style, this.nbtPath, this.interpret, separator, this.storage);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Key storage() {
/*  79 */     return this.storage;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public StorageNBTComponent storage(@NotNull Key storage) {
/*  84 */     if (Objects.equals(this.storage, storage)) return this; 
/*  85 */     return create((List)this.children, this.style, this.nbtPath, this.interpret, this.separator, storage);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public StorageNBTComponent children(@NotNull List<? extends ComponentLike> children) {
/*  90 */     return create(children, this.style, this.nbtPath, this.interpret, this.separator, this.storage);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public StorageNBTComponent style(@NotNull Style style) {
/*  95 */     return create((List)this.children, style, this.nbtPath, this.interpret, this.separator, this.storage);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 100 */     if (this == other) return true; 
/* 101 */     if (!(other instanceof StorageNBTComponent)) return false; 
/* 102 */     if (!super.equals(other)) return false; 
/* 103 */     StorageNBTComponentImpl that = (StorageNBTComponentImpl)other;
/* 104 */     return Objects.equals(this.storage, that.storage());
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 109 */     int result = super.hashCode();
/* 110 */     result = 31 * result + this.storage.hashCode();
/* 111 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 116 */     return Internals.toString(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public StorageNBTComponent.Builder toBuilder() {
/* 121 */     return new BuilderImpl(this);
/*     */   }
/*     */   
/*     */   static class BuilderImpl extends AbstractNBTComponentBuilder<StorageNBTComponent, StorageNBTComponent.Builder> implements StorageNBTComponent.Builder {
/*     */     @Nullable
/*     */     private Key storage;
/*     */     
/*     */     BuilderImpl() {}
/*     */     
/*     */     BuilderImpl(@NotNull StorageNBTComponent component) {
/* 131 */       super(component);
/* 132 */       this.storage = component.storage();
/*     */     }
/*     */ 
/*     */     
/*     */     public StorageNBTComponent.Builder storage(@NotNull Key storage) {
/* 137 */       this.storage = Objects.<Key>requireNonNull(storage, "storage");
/* 138 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public StorageNBTComponent build() {
/* 143 */       if (this.nbtPath == null) throw new IllegalStateException("nbt path must be set"); 
/* 144 */       if (this.storage == null) throw new IllegalStateException("storage must be set"); 
/* 145 */       return StorageNBTComponentImpl.create((List)this.children, buildStyle(), this.nbtPath, this.interpret, this.separator, this.storage);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\StorageNBTComponentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */