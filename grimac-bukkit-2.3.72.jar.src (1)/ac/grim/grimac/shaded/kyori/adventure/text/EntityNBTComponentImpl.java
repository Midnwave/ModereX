/*     */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
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
/*     */ 
/*     */ final class EntityNBTComponentImpl
/*     */   extends NBTComponentImpl<EntityNBTComponent, EntityNBTComponent.Builder>
/*     */   implements EntityNBTComponent
/*     */ {
/*     */   private final String selector;
/*     */   
/*     */   static EntityNBTComponent create(@NotNull List<? extends ComponentLike> children, @NotNull Style style, String nbtPath, boolean interpret, @Nullable ComponentLike separator, String selector) {
/*  39 */     return new EntityNBTComponentImpl(
/*  40 */         ComponentLike.asComponents(children, IS_NOT_EMPTY), 
/*  41 */         Objects.<Style>requireNonNull(style, "style"), 
/*  42 */         Objects.<String>requireNonNull(nbtPath, "nbtPath"), interpret, 
/*     */         
/*  44 */         ComponentLike.unbox(separator), 
/*  45 */         Objects.<String>requireNonNull(selector, "selector"));
/*     */   }
/*     */ 
/*     */   
/*     */   EntityNBTComponentImpl(@NotNull List<Component> children, @NotNull Style style, String nbtPath, boolean interpret, @Nullable Component separator, String selector) {
/*  50 */     super(children, style, nbtPath, interpret, separator);
/*  51 */     this.selector = selector;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public EntityNBTComponent nbtPath(@NotNull String nbtPath) {
/*  56 */     if (Objects.equals(this.nbtPath, nbtPath)) return this; 
/*  57 */     return create((List)this.children, this.style, nbtPath, this.interpret, this.separator, this.selector);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public EntityNBTComponent interpret(boolean interpret) {
/*  62 */     if (this.interpret == interpret) return this; 
/*  63 */     return create((List)this.children, this.style, this.nbtPath, interpret, this.separator, this.selector);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Component separator() {
/*  68 */     return this.separator;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public EntityNBTComponent separator(@Nullable ComponentLike separator) {
/*  73 */     return create((List)this.children, this.style, this.nbtPath, this.interpret, separator, this.selector);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String selector() {
/*  78 */     return this.selector;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public EntityNBTComponent selector(@NotNull String selector) {
/*  83 */     if (Objects.equals(this.selector, selector)) return this; 
/*  84 */     return create((List)this.children, this.style, this.nbtPath, this.interpret, this.separator, selector);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public EntityNBTComponent children(@NotNull List<? extends ComponentLike> children) {
/*  89 */     return create(children, this.style, this.nbtPath, this.interpret, this.separator, this.selector);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public EntityNBTComponent style(@NotNull Style style) {
/*  94 */     return create((List)this.children, style, this.nbtPath, this.interpret, this.separator, this.selector);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/*  99 */     if (this == other) return true; 
/* 100 */     if (!(other instanceof EntityNBTComponent)) return false; 
/* 101 */     if (!super.equals(other)) return false; 
/* 102 */     EntityNBTComponentImpl that = (EntityNBTComponentImpl)other;
/* 103 */     return Objects.equals(this.selector, that.selector());
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 108 */     int result = super.hashCode();
/* 109 */     result = 31 * result + this.selector.hashCode();
/* 110 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 115 */     return Internals.toString(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public EntityNBTComponent.Builder toBuilder() {
/* 120 */     return new BuilderImpl(this);
/*     */   }
/*     */   
/*     */   static final class BuilderImpl extends AbstractNBTComponentBuilder<EntityNBTComponent, EntityNBTComponent.Builder> implements EntityNBTComponent.Builder {
/*     */     @Nullable
/*     */     private String selector;
/*     */     
/*     */     BuilderImpl() {}
/*     */     
/*     */     BuilderImpl(@NotNull EntityNBTComponent component) {
/* 130 */       super(component);
/* 131 */       this.selector = component.selector();
/*     */     }
/*     */ 
/*     */     
/*     */     public EntityNBTComponent.Builder selector(@NotNull String selector) {
/* 136 */       this.selector = Objects.<String>requireNonNull(selector, "selector");
/* 137 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public EntityNBTComponent build() {
/* 142 */       if (this.nbtPath == null) throw new IllegalStateException("nbt path must be set"); 
/* 143 */       if (this.selector == null) throw new IllegalStateException("selector must be set"); 
/* 144 */       return EntityNBTComponentImpl.create((List)this.children, buildStyle(), this.nbtPath, this.interpret, this.separator, this.selector);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\EntityNBTComponentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */