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
/*     */ final class SelectorComponentImpl
/*     */   extends AbstractComponent
/*     */   implements SelectorComponent
/*     */ {
/*     */   private final String pattern;
/*     */   @Nullable
/*     */   private final Component separator;
/*     */   
/*     */   static SelectorComponent create(@NotNull List<? extends ComponentLike> children, @NotNull Style style, @NotNull String pattern, @Nullable ComponentLike separator) {
/*  40 */     return new SelectorComponentImpl(
/*  41 */         ComponentLike.asComponents(children, IS_NOT_EMPTY), 
/*  42 */         Objects.<Style>requireNonNull(style, "style"), 
/*  43 */         Objects.<String>requireNonNull(pattern, "pattern"), 
/*  44 */         ComponentLike.unbox(separator));
/*     */   }
/*     */ 
/*     */   
/*     */   SelectorComponentImpl(@NotNull List<Component> children, @NotNull Style style, @NotNull String pattern, @Nullable Component separator) {
/*  49 */     super((List)children, style);
/*  50 */     this.pattern = pattern;
/*  51 */     this.separator = separator;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String pattern() {
/*  56 */     return this.pattern;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public SelectorComponent pattern(@NotNull String pattern) {
/*  61 */     if (Objects.equals(this.pattern, pattern)) return this; 
/*  62 */     return create((List)this.children, this.style, pattern, this.separator);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Component separator() {
/*  67 */     return this.separator;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public SelectorComponent separator(@Nullable ComponentLike separator) {
/*  72 */     return create((List)this.children, this.style, this.pattern, separator);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public SelectorComponent children(@NotNull List<? extends ComponentLike> children) {
/*  77 */     return create(children, this.style, this.pattern, this.separator);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public SelectorComponent style(@NotNull Style style) {
/*  82 */     return create((List)this.children, style, this.pattern, this.separator);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/*  87 */     if (this == other) return true; 
/*  88 */     if (!(other instanceof SelectorComponent)) return false; 
/*  89 */     if (!super.equals(other)) return false; 
/*  90 */     SelectorComponent that = (SelectorComponent)other;
/*  91 */     return (Objects.equals(this.pattern, that.pattern()) && Objects.equals(this.separator, that.separator()));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  96 */     int result = super.hashCode();
/*  97 */     result = 31 * result + this.pattern.hashCode();
/*  98 */     result = 31 * result + Objects.hashCode(this.separator);
/*  99 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 104 */     return Internals.toString(this);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public SelectorComponent.Builder toBuilder() {
/* 109 */     return new BuilderImpl(this);
/*     */   }
/*     */   
/*     */   static final class BuilderImpl extends AbstractComponentBuilder<SelectorComponent, SelectorComponent.Builder> implements SelectorComponent.Builder { @Nullable
/*     */     private String pattern;
/*     */     @Nullable
/*     */     private Component separator;
/*     */     
/*     */     BuilderImpl() {}
/*     */     
/*     */     BuilderImpl(@NotNull SelectorComponent component) {
/* 120 */       super(component);
/* 121 */       this.pattern = component.pattern();
/* 122 */       this.separator = component.separator();
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public SelectorComponent.Builder pattern(@NotNull String pattern) {
/* 127 */       this.pattern = Objects.<String>requireNonNull(pattern, "pattern");
/* 128 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public SelectorComponent.Builder separator(@Nullable ComponentLike separator) {
/* 133 */       this.separator = ComponentLike.unbox(separator);
/* 134 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public SelectorComponent build() {
/* 139 */       if (this.pattern == null) throw new IllegalStateException("pattern must be set"); 
/* 140 */       return SelectorComponentImpl.create((List)this.children, buildStyle(), this.pattern, this.separator);
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\SelectorComponentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */