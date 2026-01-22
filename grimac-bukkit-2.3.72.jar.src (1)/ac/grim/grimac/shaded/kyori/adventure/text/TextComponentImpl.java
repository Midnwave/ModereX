/*     */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.VisibleForTesting;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.properties.AdventureProperties;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Buildable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Nag;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class TextComponentImpl
/*     */   extends AbstractComponent
/*     */   implements TextComponent
/*     */ {
/*  40 */   private static final boolean WARN_WHEN_LEGACY_FORMATTING_DETECTED = Boolean.TRUE.equals(AdventureProperties.TEXT_WARN_WHEN_LEGACY_FORMATTING_DETECTED.value());
/*     */   
/*     */   @VisibleForTesting
/*     */   static final char SECTION_CHAR = '§';
/*  44 */   static final TextComponent EMPTY = createDirect("");
/*  45 */   static final TextComponent NEWLINE = createDirect("\n");
/*  46 */   static final TextComponent SPACE = createDirect(" ");
/*     */   
/*     */   static TextComponent create(@NotNull List<? extends ComponentLike> children, @NotNull Style style, @NotNull String content) {
/*  49 */     List<Component> filteredChildren = ComponentLike.asComponents(children, IS_NOT_EMPTY);
/*  50 */     if (filteredChildren.isEmpty() && style.isEmpty() && content.isEmpty()) return Component.empty();
/*     */     
/*  52 */     return new TextComponentImpl(filteredChildren, 
/*     */         
/*  54 */         Objects.<Style>requireNonNull(style, "style"), 
/*  55 */         Objects.<String>requireNonNull(content, "content"));
/*     */   }
/*     */   private final String content;
/*     */   
/*     */   TextComponent create0(@NotNull List<? extends ComponentLike> children, @NotNull Style style, @NotNull String content) {
/*  60 */     return create(children, style, content);
/*     */   }
/*     */   @NotNull
/*     */   private static TextComponent createDirect(@NotNull String content) {
/*  64 */     return new TextComponentImpl(Collections.emptyList(), Style.empty(), content);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   TextComponentImpl(@NotNull List<Component> children, @NotNull Style style, @NotNull String content) {
/*  70 */     super((List)children, style);
/*  71 */     this.content = content;
/*     */     
/*  73 */     if (WARN_WHEN_LEGACY_FORMATTING_DETECTED) {
/*  74 */       LegacyFormattingDetected nag = warnWhenLegacyFormattingDetected();
/*  75 */       if (nag != null)
/*  76 */         Nag.print(nag); 
/*     */     } 
/*     */   }
/*     */   
/*     */   @VisibleForTesting
/*     */   @Nullable
/*     */   final LegacyFormattingDetected warnWhenLegacyFormattingDetected() {
/*  83 */     if (this.content.indexOf('§') != -1) {
/*  84 */       return new LegacyFormattingDetected(this);
/*     */     }
/*  86 */     return null;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String content() {
/*  91 */     return this.content;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public TextComponent content(@NotNull String content) {
/*  96 */     if (Objects.equals(this.content, content)) return this; 
/*  97 */     return create0((List)this.children, this.style, content);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public TextComponent children(@NotNull List<? extends ComponentLike> children) {
/* 102 */     return create0(children, this.style, this.content);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public TextComponent style(@NotNull Style style) {
/* 107 */     return create0((List)this.children, style, this.content);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 112 */     if (this == other) return true; 
/* 113 */     if (!(other instanceof TextComponentImpl)) return false; 
/* 114 */     if (!super.equals(other)) return false; 
/* 115 */     TextComponentImpl that = (TextComponentImpl)other;
/* 116 */     return Objects.equals(this.content, that.content);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 121 */     int result = super.hashCode();
/* 122 */     result = 31 * result + this.content.hashCode();
/* 123 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 128 */     return Internals.toString(this);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public TextComponent.Builder toBuilder() {
/* 133 */     return new BuilderImpl(this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static class BuilderImpl
/*     */     extends AbstractComponentBuilder<TextComponent, TextComponent.Builder>
/*     */     implements TextComponent.Builder
/*     */   {
/* 142 */     private String content = "";
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     BuilderImpl(@NotNull TextComponent component) {
/* 148 */       super(component);
/* 149 */       this.content = component.content();
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public TextComponent.Builder content(@NotNull String content) {
/* 154 */       this.content = Objects.<String>requireNonNull(content, "content");
/* 155 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public String content() {
/* 160 */       return this.content;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public TextComponent build() {
/* 165 */       if (isEmpty()) {
/* 166 */         return Component.empty();
/*     */       }
/* 168 */       return TextComponentImpl.create((List)this.children, buildStyle(), this.content);
/*     */     }
/*     */     
/*     */     private boolean isEmpty() {
/* 172 */       return (this.content.isEmpty() && this.children.isEmpty() && !hasStyle());
/*     */     }
/*     */     
/*     */     BuilderImpl() {}
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\TextComponentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */