/*     */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Buildable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
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
/*     */ final class TranslatableComponentImpl
/*     */   extends AbstractComponent
/*     */   implements TranslatableComponent
/*     */ {
/*     */   private final String key;
/*     */   @Nullable
/*     */   private final String fallback;
/*     */   private final List<TranslationArgument> args;
/*     */   
/*     */   static TranslatableComponent create(@NotNull List<Component> children, @NotNull Style style, @NotNull String key, @Nullable String fallback, @NotNull ComponentLike[] args) {
/*  40 */     Objects.requireNonNull(args, "args");
/*  41 */     return create((List)children, style, key, fallback, Arrays.asList(args));
/*     */   }
/*     */   
/*     */   static TranslatableComponent create(@NotNull List<? extends ComponentLike> children, @NotNull Style style, @NotNull String key, @Nullable String fallback, @NotNull List<? extends ComponentLike> args) {
/*  45 */     return new TranslatableComponentImpl(
/*  46 */         ComponentLike.asComponents(children, IS_NOT_EMPTY), 
/*  47 */         Objects.<Style>requireNonNull(style, "style"), 
/*  48 */         Objects.<String>requireNonNull(key, "key"), fallback, 
/*     */         
/*  50 */         asArguments(args));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   TranslatableComponentImpl(@NotNull List<Component> children, @NotNull Style style, @NotNull String key, @Nullable String fallback, @NotNull List<TranslationArgument> args) {
/*  59 */     super((List)children, style);
/*  60 */     this.key = key;
/*  61 */     this.fallback = fallback;
/*  62 */     this.args = args;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String key() {
/*  67 */     return this.key;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public TranslatableComponent key(@NotNull String key) {
/*  72 */     if (Objects.equals(this.key, key)) return this; 
/*  73 */     return create((List)this.children, this.style, key, this.fallback, (List)this.args);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   @NotNull
/*     */   public List<Component> args() {
/*  79 */     return ComponentLike.asComponents((List)this.args);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public List<TranslationArgument> arguments() {
/*  84 */     return this.args;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public TranslatableComponent arguments(@NotNull ComponentLike... args) {
/*  89 */     return create(this.children, this.style, this.key, this.fallback, args);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public TranslatableComponent arguments(@NotNull List<? extends ComponentLike> args) {
/*  94 */     return create((List)this.children, this.style, this.key, this.fallback, args);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public String fallback() {
/*  99 */     return this.fallback;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public TranslatableComponent fallback(@Nullable String fallback) {
/* 104 */     return create((List)this.children, this.style, this.key, fallback, (List)this.args);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public TranslatableComponent children(@NotNull List<? extends ComponentLike> children) {
/* 109 */     return create(children, this.style, this.key, this.fallback, (List)this.args);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public TranslatableComponent style(@NotNull Style style) {
/* 114 */     return create((List)this.children, style, this.key, this.fallback, (List)this.args);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 119 */     if (this == other) return true; 
/* 120 */     if (!(other instanceof TranslatableComponent)) return false; 
/* 121 */     if (!super.equals(other)) return false; 
/* 122 */     TranslatableComponent that = (TranslatableComponent)other;
/* 123 */     return (Objects.equals(this.key, that.key()) && Objects.equals(this.fallback, that.fallback()) && Objects.equals(this.args, that.arguments()));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 128 */     int result = super.hashCode();
/* 129 */     result = 31 * result + this.key.hashCode();
/* 130 */     result = 31 * result + Objects.hashCode(this.fallback);
/* 131 */     result = 31 * result + this.args.hashCode();
/* 132 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 137 */     return Internals.toString(this);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public TranslatableComponent.Builder toBuilder() {
/* 142 */     return new BuilderImpl(this);
/*     */   }
/*     */   
/*     */   static final class BuilderImpl extends AbstractComponentBuilder<TranslatableComponent, TranslatableComponent.Builder> implements TranslatableComponent.Builder {
/*     */     @Nullable
/*     */     private String key;
/* 148 */     private List<TranslationArgument> args = Collections.emptyList();
/*     */     
/*     */     @Nullable
/*     */     private String fallback;
/*     */     
/*     */     BuilderImpl(@NotNull TranslatableComponent component) {
/* 154 */       super(component);
/* 155 */       this.key = component.key();
/* 156 */       this.args = component.arguments();
/* 157 */       this.fallback = component.fallback();
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public TranslatableComponent.Builder key(@NotNull String key) {
/* 162 */       this.key = key;
/* 163 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public TranslatableComponent.Builder arguments(@NotNull ComponentLike... args) {
/* 168 */       Objects.requireNonNull(args, "args");
/* 169 */       if (args.length == 0) return arguments(Collections.emptyList()); 
/* 170 */       return arguments(Arrays.asList(args));
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public TranslatableComponent.Builder arguments(@NotNull List<? extends ComponentLike> args) {
/* 175 */       this.args = TranslatableComponentImpl.asArguments(Objects.<List<? extends ComponentLike>>requireNonNull(args, "args"));
/* 176 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public TranslatableComponent.Builder fallback(@Nullable String fallback) {
/* 181 */       this.fallback = fallback;
/* 182 */       return this;
/*     */     }
/*     */     BuilderImpl() {}
/*     */     @NotNull
/*     */     public TranslatableComponent build() {
/* 187 */       if (this.key == null) throw new IllegalStateException("key must be set"); 
/* 188 */       return TranslatableComponentImpl.create((List)this.children, buildStyle(), this.key, this.fallback, (List)this.args);
/*     */     }
/*     */   }
/*     */   
/*     */   static List<TranslationArgument> asArguments(@NotNull List<? extends ComponentLike> likes) {
/* 193 */     if (likes.isEmpty()) {
/* 194 */       return Collections.emptyList();
/*     */     }
/*     */     
/* 197 */     List<TranslationArgument> ret = new ArrayList<>(likes.size());
/* 198 */     for (int i = 0; i < likes.size(); i++) {
/* 199 */       ComponentLike like = likes.get(i);
/* 200 */       if (like == null) {
/* 201 */         throw new NullPointerException("likes[" + i + "]");
/*     */       }
/* 203 */       if (like instanceof TranslationArgument) {
/* 204 */         ret.add((TranslationArgument)like);
/* 205 */       } else if (like instanceof TranslationArgumentLike) {
/* 206 */         ret.add(Objects.<TranslationArgument>requireNonNull(((TranslationArgumentLike)like).asTranslationArgument(), "likes[" + i + "].asTranslationArgument()"));
/*     */       } else {
/* 208 */         ret.add(TranslationArgument.component(like));
/*     */       } 
/*     */     } 
/*     */     
/* 212 */     return Collections.unmodifiableList(ret);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\TranslatableComponentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */