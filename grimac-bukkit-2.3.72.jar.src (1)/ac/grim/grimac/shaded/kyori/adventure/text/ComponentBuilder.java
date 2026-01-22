/*     */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.builder.AbstractBuilder;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.ClickEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEventSource;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.MutableStyleSetter;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.StyleSetter;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextDecoration;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Buildable;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Function;
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
/*     */ @NonExtendable
/*     */ public interface ComponentBuilder<C extends BuildableComponent<C, B>, B extends ComponentBuilder<C, B>>
/*     */   extends AbstractBuilder<C>, Buildable.Builder<C>, ComponentBuilderApplicable, ComponentLike, MutableStyleSetter<B>
/*     */ {
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   default B append(@NotNull ComponentLike component) {
/*  73 */     return append(component.asComponent());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   default B append(@NotNull ComponentBuilder<?, ?> builder) {
/*  85 */     return append((Component)builder.build());
/*     */   }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default B appendNewline() {
/* 125 */     return append(Component.newline());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default B appendSpace() {
/* 135 */     return append(Component.space());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   default B apply(@NotNull Consumer<? super ComponentBuilder<?, ?>> consumer) {
/* 148 */     consumer.accept(this);
/* 149 */     return (B)this;
/*     */   }
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
/*     */   @Contract("_, _ -> this")
/*     */   @NotNull
/*     */   default B decorations(@NotNull Set<TextDecoration> decorations, boolean flag) {
/* 257 */     return (B)super.decorations(decorations, flag);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   default B decorate(@NotNull TextDecoration decoration) {
/* 270 */     return decoration(decoration, TextDecoration.State.TRUE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   B decorate(@NotNull TextDecoration... decorations) {
/* 283 */     return (B)super.decorate(decorations);
/*     */   }
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
/*     */   @Contract("_, _ -> this")
/*     */   @NotNull
/*     */   default B decoration(@NotNull TextDecoration decoration, boolean flag) {
/* 298 */     return decoration(decoration, TextDecoration.State.byBoolean(flag));
/*     */   }
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
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   default B decorations(@NotNull Map<TextDecoration, TextDecoration.State> decorations) {
/* 313 */     return (B)super.decorations(decorations);
/*     */   }
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
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   default B mergeStyle(@NotNull Component that) {
/* 386 */     return mergeStyle(that, Style.Merge.all());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract("_, _ -> this")
/*     */   @NotNull
/*     */   B mergeStyle(@NotNull Component that, Style.Merge... merges) {
/* 399 */     return mergeStyle(that, Style.Merge.merges(merges));
/*     */   }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   default B applicableApply(@NotNull ComponentBuilderApplicable applicable) {
/* 439 */     applicable.componentBuilderApply(this);
/* 440 */     return (B)this;
/*     */   }
/*     */ 
/*     */   
/*     */   default void componentBuilderApply(@NotNull ComponentBuilder<?, ?> component) {
/* 445 */     component.append(this);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   default Component asComponent() {
/* 450 */     return (Component)build();
/*     */   }
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   B append(@NotNull Component paramComponent);
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   B append(@NotNull Component... paramVarArgs);
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   B append(@NotNull ComponentLike... paramVarArgs);
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   B append(@NotNull Iterable<? extends ComponentLike> paramIterable);
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   B applyDeep(@NotNull Consumer<? super ComponentBuilder<?, ?>> paramConsumer);
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   B mapChildren(@NotNull Function<BuildableComponent<?, ?>, ? extends BuildableComponent<?, ?>> paramFunction);
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   B mapChildrenDeep(@NotNull Function<BuildableComponent<?, ?>, ? extends BuildableComponent<?, ?>> paramFunction);
/*     */   
/*     */   @NotNull
/*     */   List<Component> children();
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   B style(@NotNull Style paramStyle);
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   B style(@NotNull Consumer<Style.Builder> paramConsumer);
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   B font(@Nullable Key paramKey);
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   B color(@Nullable TextColor paramTextColor);
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   B colorIfAbsent(@Nullable TextColor paramTextColor);
/*     */   
/*     */   @Contract("_, _ -> this")
/*     */   @NotNull
/*     */   B decoration(@NotNull TextDecoration paramTextDecoration, TextDecoration.State paramState);
/*     */   
/*     */   @Contract("_, _ -> this")
/*     */   @NotNull
/*     */   B decorationIfAbsent(@NotNull TextDecoration paramTextDecoration, TextDecoration.State paramState);
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   B clickEvent(@Nullable ClickEvent paramClickEvent);
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   B hoverEvent(@Nullable HoverEventSource<?> paramHoverEventSource);
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   B insertion(@Nullable String paramString);
/*     */   
/*     */   @Contract("_, _ -> this")
/*     */   @NotNull
/*     */   B mergeStyle(@NotNull Component paramComponent, @NotNull Set<Style.Merge> paramSet);
/*     */   
/*     */   @NotNull
/*     */   B resetStyle();
/*     */   
/*     */   @NotNull
/*     */   C build();
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\ComponentBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */