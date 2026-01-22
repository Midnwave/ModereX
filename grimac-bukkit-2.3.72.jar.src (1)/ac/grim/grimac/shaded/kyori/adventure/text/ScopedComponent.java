/*     */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.ClickEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEventSource;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.StyleBuilderApplicable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.StyleSetter;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextDecoration;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.ARGBLike;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.function.Consumer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface ScopedComponent<C extends Component>
/*     */   extends Component
/*     */ {
/*     */   @NotNull
/*     */   default C asComponent() {
/*  51 */     return (C)super.asComponent();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C style(@NotNull Consumer<Style.Builder> style) {
/*  63 */     return (C)super.style(style);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C style(Style.Builder style) {
/*  69 */     return (C)super.style(style);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C style(@NotNull Consumer<Style.Builder> consumer, Style.Merge.Strategy strategy) {
/*  75 */     return (C)super.style(consumer, strategy);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C mergeStyle(@NotNull Component that) {
/*  81 */     return (C)super.mergeStyle(that);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   C mergeStyle(@NotNull Component that, Style.Merge... merges) {
/*  87 */     return (C)super.mergeStyle(that, merges);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C append(@NotNull Component component) {
/*  93 */     return (C)super.append(component);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C append(@NotNull ComponentLike like) {
/*  99 */     return (C)super.append(like);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C append(@NotNull ComponentBuilder<?, ?> builder) {
/* 105 */     return (C)super.append(builder);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C append(@NotNull List<? extends ComponentLike> components) {
/* 111 */     return (C)super.append(components);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   C append(@NotNull ComponentLike... components) {
/* 117 */     return (C)super.append(components);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C appendNewline() {
/* 123 */     return (C)super.appendNewline();
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C appendSpace() {
/* 129 */     return (C)super.appendSpace();
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   C applyFallbackStyle(@NotNull StyleBuilderApplicable... style) {
/* 135 */     return (C)super.applyFallbackStyle(style);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C applyFallbackStyle(@NotNull Style style) {
/* 141 */     return (C)super.applyFallbackStyle(style);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C mergeStyle(@NotNull Component that, @NotNull Set<Style.Merge> merges) {
/* 147 */     return (C)super.mergeStyle(that, merges);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C color(@Nullable TextColor color) {
/* 153 */     return (C)super.color(color);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C colorIfAbsent(@Nullable TextColor color) {
/* 159 */     return (C)super.colorIfAbsent(color);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C shadowColor(@Nullable ARGBLike argb) {
/* 165 */     return (C)super.shadowColor(argb);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C shadowColorIfAbsent(@Nullable ARGBLike argb) {
/* 171 */     return (C)super.shadowColorIfAbsent(argb);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C decorate(@NotNull TextDecoration decoration) {
/* 177 */     return (C)super.decorate(decoration);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C decoration(@NotNull TextDecoration decoration, boolean flag) {
/* 183 */     return (C)super.decoration(decoration, flag);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C decoration(@NotNull TextDecoration decoration, TextDecoration.State state) {
/* 189 */     return (C)super.decoration(decoration, state);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C decorationIfAbsent(@NotNull TextDecoration decoration, TextDecoration.State state) {
/* 195 */     return (C)super.decorationIfAbsent(decoration, state);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C decorations(@NotNull Map<TextDecoration, TextDecoration.State> decorations) {
/* 201 */     return (C)super.decorations(decorations);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C clickEvent(@Nullable ClickEvent event) {
/* 207 */     return (C)super.clickEvent(event);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C hoverEvent(@Nullable HoverEventSource<?> event) {
/* 213 */     return (C)super.hoverEvent(event);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C insertion(@Nullable String insertion) {
/* 219 */     return (C)super.insertion(insertion);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default C font(@Nullable Key key) {
/* 225 */     return (C)super.font(key);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   C children(@NotNull List<? extends ComponentLike> paramList);
/*     */   
/*     */   @NotNull
/*     */   C style(@NotNull Style paramStyle);
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\ScopedComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */