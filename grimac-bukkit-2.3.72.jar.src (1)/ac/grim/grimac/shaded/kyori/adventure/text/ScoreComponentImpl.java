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
/*     */ final class ScoreComponentImpl
/*     */   extends AbstractComponent
/*     */   implements ScoreComponent
/*     */ {
/*     */   private final String name;
/*     */   private final String objective;
/*     */   @Deprecated
/*     */   @Nullable
/*     */   private final String value;
/*     */   
/*     */   static ScoreComponent create(@NotNull List<? extends ComponentLike> children, @NotNull Style style, @NotNull String name, @NotNull String objective, @Nullable String value) {
/*  42 */     return new ScoreComponentImpl(
/*  43 */         ComponentLike.asComponents(children, IS_NOT_EMPTY), 
/*  44 */         Objects.<Style>requireNonNull(style, "style"), 
/*  45 */         Objects.<String>requireNonNull(name, "name"), 
/*  46 */         Objects.<String>requireNonNull(objective, "objective"), value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ScoreComponentImpl(@NotNull List<Component> children, @NotNull Style style, @NotNull String name, @NotNull String objective, @Nullable String value) {
/*  52 */     super((List)children, style);
/*  53 */     this.name = name;
/*  54 */     this.objective = objective;
/*  55 */     this.value = value;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String name() {
/*  60 */     return this.name;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public ScoreComponent name(@NotNull String name) {
/*  65 */     if (Objects.equals(this.name, name)) return this; 
/*  66 */     return create((List)this.children, this.style, name, this.objective, this.value);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String objective() {
/*  71 */     return this.objective;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public ScoreComponent objective(@NotNull String objective) {
/*  76 */     if (Objects.equals(this.objective, objective)) return this; 
/*  77 */     return create((List)this.children, this.style, this.name, objective, this.value);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   @Nullable
/*     */   public String value() {
/*  83 */     return this.value;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   @NotNull
/*     */   public ScoreComponent value(@Nullable String value) {
/*  89 */     if (Objects.equals(this.value, value)) return this; 
/*  90 */     return create((List)this.children, this.style, this.name, this.objective, value);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public ScoreComponent children(@NotNull List<? extends ComponentLike> children) {
/*  95 */     return create(children, this.style, this.name, this.objective, this.value);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public ScoreComponent style(@NotNull Style style) {
/* 100 */     return create((List)this.children, style, this.name, this.objective, this.value);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 106 */     if (this == other) return true; 
/* 107 */     if (!(other instanceof ScoreComponent)) return false; 
/* 108 */     if (!super.equals(other)) return false; 
/* 109 */     ScoreComponent that = (ScoreComponent)other;
/* 110 */     return (Objects.equals(this.name, that.name()) && 
/* 111 */       Objects.equals(this.objective, that.objective()) && 
/* 112 */       Objects.equals(this.value, that.value()));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 117 */     int result = super.hashCode();
/* 118 */     result = 31 * result + this.name.hashCode();
/* 119 */     result = 31 * result + this.objective.hashCode();
/* 120 */     result = 31 * result + Objects.hashCode(this.value);
/* 121 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 126 */     return Internals.toString(this);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public ScoreComponent.Builder toBuilder() {
/* 131 */     return new BuilderImpl(this);
/*     */   }
/*     */   
/*     */   static final class BuilderImpl extends AbstractComponentBuilder<ScoreComponent, ScoreComponent.Builder> implements ScoreComponent.Builder { @Nullable
/*     */     private String name;
/*     */     @Nullable
/*     */     private String objective;
/*     */     @Nullable
/*     */     private String value;
/*     */     
/*     */     BuilderImpl() {}
/*     */     
/*     */     BuilderImpl(@NotNull ScoreComponent component) {
/* 144 */       super(component);
/* 145 */       this.name = component.name();
/* 146 */       this.objective = component.objective();
/* 147 */       this.value = component.value();
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public ScoreComponent.Builder name(@NotNull String name) {
/* 152 */       this.name = Objects.<String>requireNonNull(name, "name");
/* 153 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public ScoreComponent.Builder objective(@NotNull String objective) {
/* 158 */       this.objective = Objects.<String>requireNonNull(objective, "objective");
/* 159 */       return this;
/*     */     }
/*     */     
/*     */     @Deprecated
/*     */     @NotNull
/*     */     public ScoreComponent.Builder value(@Nullable String value) {
/* 165 */       this.value = value;
/* 166 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public ScoreComponent build() {
/* 171 */       if (this.name == null) throw new IllegalStateException("name must be set"); 
/* 172 */       if (this.objective == null) throw new IllegalStateException("objective must be set"); 
/* 173 */       return ScoreComponentImpl.create((List)this.children, buildStyle(), this.name, this.objective, this.value);
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\ScoreComponentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */