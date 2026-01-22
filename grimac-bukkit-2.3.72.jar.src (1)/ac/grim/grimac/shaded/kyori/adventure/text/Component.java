/*      */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*      */ 
/*      */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*      */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
/*      */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*      */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*      */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.builder.AbstractBuilder;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.text.event.ClickEvent;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEvent;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEventSource;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.text.format.ShadowColor;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.text.format.StyleBuilderApplicable;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.text.format.StyleGetter;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.text.format.StyleSetter;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextDecoration;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.translation.Translatable;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.util.ARGBLike;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.util.ForwardingIterator;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.util.IntFunction2;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.util.MonkeyBars;
/*      */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*      */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Objects;
/*      */ import java.util.Set;
/*      */ import java.util.Spliterator;
/*      */ import java.util.Spliterators;
/*      */ import java.util.function.BiPredicate;
/*      */ import java.util.function.Consumer;
/*      */ import java.util.function.Function;
/*      */ import java.util.function.Predicate;
/*      */ import java.util.function.UnaryOperator;
/*      */ import java.util.regex.Pattern;
/*      */ import java.util.stream.Collector;
/*      */ import java.util.stream.Stream;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ @NonExtendable
/*      */ public interface Component
/*      */   extends ComponentBuilderApplicable, ComponentLike, Examinable, HoverEventSource<Component>, StyleGetter, StyleSetter<Component>
/*      */ {
/*  119 */   public static final BiPredicate<? super Component, ? super Component> EQUALS = Objects::equals;
/*      */   
/*      */   public static final BiPredicate<? super Component, ? super Component> EQUALS_IDENTITY;
/*      */   public static final Predicate<? super Component> IS_NOT_EMPTY;
/*      */   
/*      */   static {
/*  125 */     EQUALS_IDENTITY = ((a, b) -> (a == b));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  131 */     IS_NOT_EMPTY = (component -> (component != empty()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @NotNull
/*      */   static TextComponent empty() {
/*  140 */     return TextComponentImpl.EMPTY;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @NotNull
/*      */   static TextComponent newline() {
/*  150 */     return TextComponentImpl.NEWLINE;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @NotNull
/*      */   static TextComponent space() {
/*  160 */     return TextComponentImpl.SPACE;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   @ScheduledForRemoval(inVersion = "5.0.0")
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent join(@NotNull ComponentLike separator, @NotNull ComponentLike... components) {
/*  176 */     return join(separator, Arrays.asList(components));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   @ScheduledForRemoval(inVersion = "5.0.0")
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent join(@NotNull ComponentLike separator, Iterable<? extends ComponentLike> components) {
/*  192 */     Component component = join(JoinConfiguration.separator(separator), components);
/*      */     
/*  194 */     if (component instanceof TextComponent) return (TextComponent)component; 
/*  195 */     return text().append(component).build();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   static Component join(JoinConfiguration.Builder configBuilder, @NotNull ComponentLike... components) {
/*  211 */     return join(configBuilder, Arrays.asList(components));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   static Component join(JoinConfiguration.Builder configBuilder, @NotNull Iterable<? extends ComponentLike> components) {
/*  227 */     return JoinConfigurationImpl.join((JoinConfiguration)configBuilder.build(), components);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   static Component join(@NotNull JoinConfiguration config, @NotNull ComponentLike... components) {
/*  243 */     return join(config, Arrays.asList(components));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   static Component join(@NotNull JoinConfiguration config, @NotNull Iterable<? extends ComponentLike> components) {
/*  259 */     return JoinConfigurationImpl.join(config, components);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @NotNull
/*      */   static Collector<Component, ? extends ComponentBuilder<?, ?>, Component> toComponent() {
/*  269 */     return toComponent(empty());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @NotNull
/*      */   static Collector<Component, ? extends ComponentBuilder<?, ?>, Component> toComponent(@NotNull Component separator) {
/*  280 */     return Collector.of(Component::text, (builder, add) -> { if (separator != empty() && !builder.children().isEmpty()) builder.append(separator);  builder.append(add); }(a, b) -> { List<Component> aChildren = a.children(); TextComponent.Builder ret = text().append((Iterable)aChildren); if (!aChildren.isEmpty()) ret.append(separator);  ret.append((Iterable)b.children()); return ret; }ComponentBuilder::build, new Collector.Characteristics[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   static BlockNBTComponent.Builder blockNBT() {
/*  314 */     return new BlockNBTComponentImpl.BuilderImpl();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract("_ -> new")
/*      */   @NotNull
/*      */   static BlockNBTComponent blockNBT(@NotNull Consumer<? super BlockNBTComponent.Builder> consumer) {
/*  326 */     return (BlockNBTComponent)AbstractBuilder.configureAndBuild(blockNBT(), consumer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static BlockNBTComponent blockNBT(@NotNull String nbtPath, BlockNBTComponent.Pos pos) {
/*  339 */     return blockNBT(nbtPath, false, pos);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static BlockNBTComponent blockNBT(@NotNull String nbtPath, boolean interpret, BlockNBTComponent.Pos pos) {
/*  353 */     return blockNBT(nbtPath, interpret, null, pos);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static BlockNBTComponent blockNBT(@NotNull String nbtPath, boolean interpret, @Nullable ComponentLike separator, BlockNBTComponent.Pos pos) {
/*  368 */     return BlockNBTComponentImpl.create(Collections.emptyList(), Style.empty(), nbtPath, interpret, separator, pos);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   static EntityNBTComponent.Builder entityNBT() {
/*  385 */     return new EntityNBTComponentImpl.BuilderImpl();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract("_ -> new")
/*      */   @NotNull
/*      */   static EntityNBTComponent entityNBT(@NotNull Consumer<? super EntityNBTComponent.Builder> consumer) {
/*  397 */     return (EntityNBTComponent)AbstractBuilder.configureAndBuild(entityNBT(), consumer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract("_, _ -> new")
/*      */   @NotNull
/*      */   static EntityNBTComponent entityNBT(@NotNull String nbtPath, @NotNull String selector) {
/*  410 */     return entityNBT().nbtPath(nbtPath).selector(selector).build();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   static KeybindComponent.Builder keybind() {
/*  427 */     return new KeybindComponentImpl.BuilderImpl();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract("_ -> new")
/*      */   @NotNull
/*      */   static KeybindComponent keybind(@NotNull Consumer<? super KeybindComponent.Builder> consumer) {
/*  439 */     return (KeybindComponent)AbstractBuilder.configureAndBuild(keybind(), consumer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_ -> new", pure = true)
/*      */   @NotNull
/*      */   static KeybindComponent keybind(@NotNull String keybind) {
/*  451 */     return keybind(keybind, Style.empty());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_ -> new", pure = true)
/*      */   @NotNull
/*      */   static KeybindComponent keybind(KeybindComponent.KeybindLike keybind) {
/*  463 */     return keybind(((KeybindComponent.KeybindLike)Objects.<KeybindComponent.KeybindLike>requireNonNull(keybind, "keybind")).asKeybind(), Style.empty());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static KeybindComponent keybind(@NotNull String keybind, @NotNull Style style) {
/*  476 */     return KeybindComponentImpl.create(Collections.emptyList(), Objects.<Style>requireNonNull(style, "style"), keybind);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static KeybindComponent keybind(KeybindComponent.KeybindLike keybind, @NotNull Style style) {
/*  489 */     return KeybindComponentImpl.create(Collections.emptyList(), Objects.<Style>requireNonNull(style, "style"), ((KeybindComponent.KeybindLike)Objects.<KeybindComponent.KeybindLike>requireNonNull(keybind, "keybind")).asKeybind());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static KeybindComponent keybind(@NotNull String keybind, @Nullable TextColor color) {
/*  502 */     return keybind(keybind, Style.style(color));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static KeybindComponent keybind(KeybindComponent.KeybindLike keybind, @Nullable TextColor color) {
/*  515 */     return keybind(((KeybindComponent.KeybindLike)Objects.<KeybindComponent.KeybindLike>requireNonNull(keybind, "keybind")).asKeybind(), Style.style(color));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static KeybindComponent keybind(@NotNull String keybind, @Nullable TextColor color, TextDecoration... decorations) {
/*  529 */     return keybind(keybind, Style.style(color, decorations));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static KeybindComponent keybind(KeybindComponent.KeybindLike keybind, @Nullable TextColor color, TextDecoration... decorations) {
/*  543 */     return keybind(((KeybindComponent.KeybindLike)Objects.<KeybindComponent.KeybindLike>requireNonNull(keybind, "keybind")).asKeybind(), Style.style(color, decorations));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static KeybindComponent keybind(@NotNull String keybind, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
/*  557 */     return keybind(keybind, Style.style(color, decorations));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static KeybindComponent keybind(KeybindComponent.KeybindLike keybind, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
/*  571 */     return keybind(((KeybindComponent.KeybindLike)Objects.<KeybindComponent.KeybindLike>requireNonNull(keybind, "keybind")).asKeybind(), Style.style(color, decorations));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   static ScoreComponent.Builder score() {
/*  588 */     return new ScoreComponentImpl.BuilderImpl();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract("_ -> new")
/*      */   @NotNull
/*      */   static ScoreComponent score(@NotNull Consumer<? super ScoreComponent.Builder> consumer) {
/*  600 */     return (ScoreComponent)AbstractBuilder.configureAndBuild(score(), consumer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static ScoreComponent score(@NotNull String name, @NotNull String objective) {
/*  613 */     return score(name, objective, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static ScoreComponent score(@NotNull String name, @NotNull String objective, @Nullable String value) {
/*  629 */     return ScoreComponentImpl.create(Collections.emptyList(), Style.empty(), name, objective, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   static SelectorComponent.Builder selector() {
/*  646 */     return new SelectorComponentImpl.BuilderImpl();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract("_ -> new")
/*      */   @NotNull
/*      */   static SelectorComponent selector(@NotNull Consumer<? super SelectorComponent.Builder> consumer) {
/*  658 */     return (SelectorComponent)AbstractBuilder.configureAndBuild(selector(), consumer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_ -> new", pure = true)
/*      */   @NotNull
/*      */   static SelectorComponent selector(@NotNull String pattern) {
/*  670 */     return selector(pattern, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static SelectorComponent selector(@NotNull String pattern, @Nullable ComponentLike separator) {
/*  683 */     return SelectorComponentImpl.create(Collections.emptyList(), Style.empty(), pattern, separator);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   static StorageNBTComponent.Builder storageNBT() {
/*  700 */     return new StorageNBTComponentImpl.BuilderImpl();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract("_ -> new")
/*      */   @NotNull
/*      */   static StorageNBTComponent storageNBT(@NotNull Consumer<? super StorageNBTComponent.Builder> consumer) {
/*  712 */     return (StorageNBTComponent)AbstractBuilder.configureAndBuild(storageNBT(), consumer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static StorageNBTComponent storageNBT(@NotNull String nbtPath, @NotNull Key storage) {
/*  725 */     return storageNBT(nbtPath, false, storage);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static StorageNBTComponent storageNBT(@NotNull String nbtPath, boolean interpret, @NotNull Key storage) {
/*  739 */     return storageNBT(nbtPath, interpret, null, storage);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static StorageNBTComponent storageNBT(@NotNull String nbtPath, boolean interpret, @Nullable ComponentLike separator, @NotNull Key storage) {
/*  754 */     return StorageNBTComponentImpl.create(Collections.emptyList(), Style.empty(), nbtPath, interpret, separator, storage);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   static TextComponent.Builder text() {
/*  771 */     return new TextComponentImpl.BuilderImpl();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @NotNull
/*      */   static TextComponent textOfChildren(@NotNull ComponentLike... components) {
/*  782 */     if (components.length == 0) return empty(); 
/*  783 */     return TextComponentImpl.create(Arrays.asList(components), Style.empty(), "");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract("_ -> new")
/*      */   @NotNull
/*      */   static TextComponent text(@NotNull Consumer<? super TextComponent.Builder> consumer) {
/*  795 */     return (TextComponent)AbstractBuilder.configureAndBuild(text(), consumer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(@NotNull String content) {
/*  807 */     if (content.isEmpty()) return empty(); 
/*  808 */     return text(content, Style.empty());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(@NotNull String content, @NotNull Style style) {
/*  821 */     return TextComponentImpl.create(Collections.emptyList(), Objects.<Style>requireNonNull(style, "style"), content);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(@NotNull String content, @Nullable TextColor color) {
/*  834 */     return text(content, Style.style(color));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(@NotNull String content, @Nullable TextColor color, TextDecoration... decorations) {
/*  848 */     return text(content, Style.style(color, decorations));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(@NotNull String content, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
/*  862 */     return text(content, Style.style(color, decorations));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(boolean value) {
/*  874 */     return text(String.valueOf(value));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(boolean value, @NotNull Style style) {
/*  887 */     return text(String.valueOf(value), style);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(boolean value, @Nullable TextColor color) {
/*  900 */     return text(String.valueOf(value), color);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(boolean value, @Nullable TextColor color, TextDecoration... decorations) {
/*  914 */     return text(String.valueOf(value), color, decorations);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(boolean value, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
/*  928 */     return text(String.valueOf(value), color, decorations);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(char value) {
/*  940 */     if (value == '\n') return newline(); 
/*  941 */     if (value == ' ') return space(); 
/*  942 */     return text(String.valueOf(value));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(char value, @NotNull Style style) {
/*  955 */     return text(String.valueOf(value), style);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(char value, @Nullable TextColor color) {
/*  968 */     return text(String.valueOf(value), color);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(char value, @Nullable TextColor color, TextDecoration... decorations) {
/*  982 */     return text(String.valueOf(value), color, decorations);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(char value, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
/*  996 */     return text(String.valueOf(value), color, decorations);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(double value) {
/* 1008 */     return text(String.valueOf(value));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(double value, @NotNull Style style) {
/* 1021 */     return text(String.valueOf(value), style);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(double value, @Nullable TextColor color) {
/* 1034 */     return text(String.valueOf(value), color);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(double value, @Nullable TextColor color, TextDecoration... decorations) {
/* 1048 */     return text(String.valueOf(value), color, decorations);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(double value, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
/* 1062 */     return text(String.valueOf(value), color, decorations);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(float value) {
/* 1074 */     return text(String.valueOf(value));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(float value, @NotNull Style style) {
/* 1087 */     return text(String.valueOf(value), style);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(float value, @Nullable TextColor color) {
/* 1100 */     return text(String.valueOf(value), color);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(float value, @Nullable TextColor color, TextDecoration... decorations) {
/* 1114 */     return text(String.valueOf(value), color, decorations);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(float value, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
/* 1128 */     return text(String.valueOf(value), color, decorations);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(int value) {
/* 1140 */     return text(String.valueOf(value));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(int value, @NotNull Style style) {
/* 1153 */     return text(String.valueOf(value), style);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(int value, @Nullable TextColor color) {
/* 1166 */     return text(String.valueOf(value), color);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(int value, @Nullable TextColor color, TextDecoration... decorations) {
/* 1180 */     return text(String.valueOf(value), color, decorations);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(int value, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
/* 1194 */     return text(String.valueOf(value), color, decorations);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(long value) {
/* 1206 */     return text(String.valueOf(value));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(long value, @NotNull Style style) {
/* 1219 */     return text(String.valueOf(value), style);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(long value, @Nullable TextColor color) {
/* 1232 */     return text(String.valueOf(value), color);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(long value, @Nullable TextColor color, TextDecoration... decorations) {
/* 1246 */     return text(String.valueOf(value), color, decorations);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TextComponent text(long value, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
/* 1260 */     return text(String.valueOf(value), color, decorations);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static <C> VirtualComponent virtual(@NotNull Class<C> contextType, @NotNull VirtualComponentRenderer<C> renderer) {
/* 1280 */     Objects.requireNonNull(contextType, "context type");
/* 1281 */     Objects.requireNonNull(renderer, "renderer");
/* 1282 */     return VirtualComponentImpl.createVirtual(contextType, renderer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static <C> VirtualComponent virtual(@NotNull Class<C> contextType, @NotNull VirtualComponentRenderer<C> renderer, @NotNull Style style) {
/* 1297 */     Objects.requireNonNull(contextType, "context type");
/* 1298 */     Objects.requireNonNull(renderer, "renderer");
/* 1299 */     return VirtualComponentImpl.createVirtual(contextType, renderer, Collections.emptyList(), style);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static <C> VirtualComponent virtual(@NotNull Class<C> contextType, @NotNull VirtualComponentRenderer<C> renderer, @NotNull StyleBuilderApplicable... style) {
/* 1314 */     Objects.requireNonNull(contextType, "context type");
/* 1315 */     Objects.requireNonNull(renderer, "renderer");
/* 1316 */     return VirtualComponentImpl.createVirtual(contextType, renderer, Collections.emptyList(), Style.style(style));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static <C> VirtualComponent virtual(@NotNull Class<C> contextType, @NotNull VirtualComponentRenderer<C> renderer, @NotNull Iterable<StyleBuilderApplicable> style) {
/* 1331 */     Objects.requireNonNull(contextType, "context type");
/* 1332 */     Objects.requireNonNull(renderer, "renderer");
/* 1333 */     return VirtualComponentImpl.createVirtual(contextType, renderer, Collections.emptyList(), Style.style(style));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   static TranslatableComponent.Builder translatable() {
/* 1350 */     return new TranslatableComponentImpl.BuilderImpl();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract("_ -> new")
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull Consumer<? super TranslatableComponent.Builder> consumer) {
/* 1362 */     return (TranslatableComponent)AbstractBuilder.configureAndBuild(translatable(), consumer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull String key) {
/* 1374 */     return translatable(key, Style.empty());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull Translatable translatable) {
/* 1386 */     return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), Style.empty());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull String key, @Nullable String fallback) {
/* 1400 */     return translatable(key, fallback, Style.empty());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable String fallback) {
/* 1414 */     return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), fallback, Style.empty());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull String key, @NotNull Style style) {
/* 1427 */     return TranslatableComponentImpl.create(Collections.emptyList(), Objects.<Style>requireNonNull(style, "style"), key, (String)null, Collections.emptyList());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull Translatable translatable, @NotNull Style style) {
/* 1440 */     return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), style);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull String key, @Nullable String fallback, @NotNull Style style) {
/* 1455 */     return TranslatableComponentImpl.create(Collections.emptyList(), Objects.<Style>requireNonNull(style, "style"), key, fallback, Collections.emptyList());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable String fallback, @NotNull Style style) {
/* 1470 */     return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), fallback, style);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull String key, @Nullable String fallback, @NotNull StyleBuilderApplicable... style) {
/* 1485 */     return translatable(Objects.<String>requireNonNull(key, "key"), fallback, Style.style(style));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable String fallback, @NotNull Iterable<StyleBuilderApplicable> style) {
/* 1500 */     return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), fallback, Style.style(style));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull String key, @Nullable String fallback, @NotNull ComponentLike... args) {
/* 1515 */     return translatable(key, fallback, Style.empty(), args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable String fallback, @NotNull ComponentLike... args) {
/* 1530 */     return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), fallback, args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull String key, @Nullable String fallback, @NotNull Style style, @NotNull ComponentLike... args) {
/* 1546 */     return TranslatableComponentImpl.create(Collections.emptyList(), Objects.<Style>requireNonNull(style, "style"), key, fallback, Objects.<ComponentLike[]>requireNonNull(args, "args"));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable String fallback, @NotNull Style style, @NotNull ComponentLike... args) {
/* 1562 */     return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), fallback, style, args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull String key, @Nullable String fallback, @NotNull Style style, @NotNull List<? extends ComponentLike> args) {
/* 1578 */     return TranslatableComponentImpl.create(Collections.emptyList(), style, key, fallback, Objects.<List<? extends ComponentLike>>requireNonNull(args, "args"));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable String fallback, @NotNull Style style, @NotNull List<? extends ComponentLike> args) {
/* 1594 */     return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), fallback, style, args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull String key, @Nullable String fallback, @NotNull List<? extends ComponentLike> args, @NotNull Iterable<StyleBuilderApplicable> style) {
/* 1610 */     return TranslatableComponentImpl.create(Collections.emptyList(), Style.style(style), key, fallback, Objects.<List<? extends ComponentLike>>requireNonNull(args, "args"));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable String fallback, @NotNull List<? extends ComponentLike> args, @NotNull Iterable<StyleBuilderApplicable> style) {
/* 1626 */     return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), fallback, args, style);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull String key, @Nullable String fallback, @NotNull List<? extends ComponentLike> args, @NotNull StyleBuilderApplicable... style) {
/* 1642 */     return TranslatableComponentImpl.create(Collections.emptyList(), Style.style(style), key, fallback, Objects.<List<? extends ComponentLike>>requireNonNull(args, "args"));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable String fallback, @NotNull List<? extends ComponentLike> args, @NotNull StyleBuilderApplicable... style) {
/* 1658 */     return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), fallback, args, style);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull String key, @Nullable TextColor color) {
/* 1671 */     return translatable(key, Style.style(color));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable TextColor color) {
/* 1684 */     return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), color);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull String key, @Nullable TextColor color, TextDecoration... decorations) {
/* 1698 */     return translatable(key, Style.style(color, decorations));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable TextColor color, TextDecoration... decorations) {
/* 1712 */     return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), color, decorations);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull String key, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
/* 1726 */     return translatable(key, Style.style(color, decorations));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
/* 1740 */     return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), color, decorations);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull String key, @NotNull ComponentLike... args) {
/* 1753 */     return translatable(key, Style.empty(), args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull Translatable translatable, @NotNull ComponentLike... args) {
/* 1766 */     return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull String key, @NotNull Style style, @NotNull ComponentLike... args) {
/* 1780 */     return TranslatableComponentImpl.create(Collections.emptyList(), Objects.<Style>requireNonNull(style, "style"), key, (String)null, Objects.<ComponentLike[]>requireNonNull(args, "args"));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull Translatable translatable, @NotNull Style style, @NotNull ComponentLike... args) {
/* 1794 */     return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), style, args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull String key, @Nullable TextColor color, @NotNull ComponentLike... args) {
/* 1808 */     return translatable(key, Style.style(color), args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable TextColor color, @NotNull ComponentLike... args) {
/* 1822 */     return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), color, args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull String key, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations, @NotNull ComponentLike... args) {
/* 1837 */     return translatable(key, Style.style(color, decorations), args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations, @NotNull ComponentLike... args) {
/* 1852 */     return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), color, decorations, args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull String key, @NotNull List<? extends ComponentLike> args) {
/* 1865 */     return TranslatableComponentImpl.create(Collections.emptyList(), Style.empty(), key, (String)null, Objects.<List<? extends ComponentLike>>requireNonNull(args, "args"));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull Translatable translatable, @NotNull List<? extends ComponentLike> args) {
/* 1878 */     return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull String key, @NotNull Style style, @NotNull List<? extends ComponentLike> args) {
/* 1892 */     return TranslatableComponentImpl.create(Collections.emptyList(), Objects.<Style>requireNonNull(style, "style"), key, (String)null, Objects.<List<? extends ComponentLike>>requireNonNull(args, "args"));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull Translatable translatable, @NotNull Style style, @NotNull List<? extends ComponentLike> args) {
/* 1906 */     return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), style, args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   static TranslatableComponent translatable(@NotNull String key, @Nullable TextColor color, @NotNull List<? extends ComponentLike> args) {
/* 1920 */     return translatable(key, Style.style(color), args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _ -> new", pure = true)
/*      */   static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable TextColor color, @NotNull List<? extends ComponentLike> args) {
/* 1934 */     return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), color, args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull String key, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations, @NotNull List<? extends ComponentLike> args) {
/* 1949 */     return translatable(key, Style.style(color, decorations), args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(value = "_, _, _, _ -> new", pure = true)
/*      */   @NotNull
/*      */   static TranslatableComponent translatable(@NotNull Translatable translatable, @Nullable TextColor color, @NotNull Set<TextDecoration> decorations, @NotNull List<? extends ComponentLike> args) {
/* 1964 */     return translatable(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey(), color, decorations, args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   default boolean contains(@NotNull Component that) {
/* 1999 */     return contains(that, EQUALS_IDENTITY);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   default boolean contains(@NotNull Component that, @NotNull BiPredicate<? super Component, ? super Component> equals) {
/* 2012 */     if (equals.test(this, that)) return true; 
/* 2013 */     for (Component child : children()) {
/* 2014 */       if (child.contains(that, equals)) return true; 
/*      */     } 
/* 2016 */     HoverEvent<?> hoverEvent = hoverEvent();
/* 2017 */     if (hoverEvent != null) {
/* 2018 */       Object value = hoverEvent.value();
/* 2019 */       Component component = null;
/* 2020 */       if (value instanceof Component) {
/* 2021 */         component = (Component)hoverEvent.value();
/* 2022 */       } else if (value instanceof HoverEvent.ShowEntity) {
/* 2023 */         component = ((HoverEvent.ShowEntity)value).name();
/*      */       } 
/* 2025 */       if (component != null) {
/* 2026 */         if (equals.test(that, component)) return true; 
/* 2027 */         for (Component child : component.children()) {
/* 2028 */           if (child.contains(that, equals)) return true; 
/*      */         } 
/*      */       } 
/*      */     } 
/* 2032 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   @ScheduledForRemoval(inVersion = "5.0.0")
/*      */   default void detectCycle(@NotNull Component that) {
/* 2045 */     if (that.contains(this)) {
/* 2046 */       throw new IllegalStateException("Component cycle detected between " + this + " and " + that);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component append(@NotNull Component component) {
/* 2059 */     return append(component);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @NotNull
/*      */   default Component append(@NotNull ComponentLike like) {
/* 2070 */     Objects.requireNonNull(like, "like");
/* 2071 */     Component component = like.asComponent();
/* 2072 */     Objects.requireNonNull(component, "component");
/* 2073 */     if (component == empty()) return this; 
/* 2074 */     List<Component> oldChildren = children();
/* 2075 */     return children(MonkeyBars.addOne(oldChildren, component));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component append(@NotNull ComponentBuilder<?, ?> builder) {
/* 2087 */     return append((Component)builder.build());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component appendNewline() {
/* 2098 */     return append(newline());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component appendSpace() {
/* 2109 */     return append(space());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   Component append(@NotNull ComponentLike... components) {
/* 2121 */     if (components.length == 0) return this;
/*      */     
/* 2123 */     List<ComponentLike> newChildren = new ArrayList<>(components.length + children().size());
/* 2124 */     newChildren.addAll((Collection)children());
/* 2125 */     Collections.addAll(newChildren, components);
/* 2126 */     return children(newChildren);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component append(@NotNull List<? extends ComponentLike> components) {
/* 2138 */     if (components.isEmpty()) return this; 
/* 2139 */     if (children().isEmpty()) return children(components);
/*      */     
/* 2141 */     List<ComponentLike> newChildren = new ArrayList<>(components.size() + children().size());
/* 2142 */     newChildren.addAll((Collection)children());
/* 2143 */     newChildren.addAll(components);
/* 2144 */     return children(newChildren);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component applyFallbackStyle(@NotNull Style style) {
/* 2158 */     Objects.requireNonNull(style, "style");
/* 2159 */     return style(style().merge(style, Style.Merge.Strategy.IF_ABSENT_ON_TARGET));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   Component applyFallbackStyle(@NotNull StyleBuilderApplicable... style) {
/* 2173 */     return applyFallbackStyle(Style.style(style));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component style(@NotNull Consumer<Style.Builder> consumer) {
/* 2203 */     return style(style().edit(consumer));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component style(@NotNull Consumer<Style.Builder> consumer, Style.Merge.Strategy strategy) {
/* 2216 */     return style(style().edit(consumer, strategy));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component style(Style.Builder style) {
/* 2228 */     return style(style.build());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component mergeStyle(@NotNull Component that) {
/* 2240 */     return mergeStyle(that, Style.Merge.all());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   Component mergeStyle(@NotNull Component that, Style.Merge... merges) {
/* 2253 */     return mergeStyle(that, Style.Merge.merges(merges));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component mergeStyle(@NotNull Component that, @NotNull Set<Style.Merge> merges) {
/* 2266 */     return style(style().merge(that.style(), merges));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   default Key font() {
/* 2277 */     return style().font();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @NotNull
/*      */   default Component font(@Nullable Key key) {
/* 2289 */     return style(style().font(key));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   default TextColor color() {
/* 2300 */     return style().color();
/*      */   }
/*      */   
/*      */   @Nullable
/*      */   default ShadowColor shadowColor() {
/* 2305 */     return style().shadowColor();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component color(@Nullable TextColor color) {
/* 2318 */     return style(style().color(color));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component colorIfAbsent(@Nullable TextColor color) {
/* 2331 */     if (color() == null) return color(color); 
/* 2332 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component shadowColor(@Nullable ARGBLike argb) {
/* 2345 */     return style((Style)style().shadowColor(argb));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component shadowColorIfAbsent(@Nullable ARGBLike argb) {
/* 2358 */     if (shadowColor() == null) return shadowColor(argb); 
/* 2359 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   default boolean hasDecoration(@NotNull TextDecoration decoration) {
/* 2372 */     return super.hasDecoration(decoration);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component decorate(@NotNull TextDecoration decoration) {
/* 2385 */     return (Component)super.decorate(decoration);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   default TextDecoration.State decoration(@NotNull TextDecoration decoration) {
/* 2399 */     return style().decoration(decoration);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component decoration(@NotNull TextDecoration decoration, boolean flag) {
/* 2414 */     return (Component)super.decoration(decoration, flag);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component decoration(@NotNull TextDecoration decoration, TextDecoration.State state) {
/* 2431 */     return style(style().decoration(decoration, state));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @NotNull
/*      */   default Component decorationIfAbsent(@NotNull TextDecoration decoration, TextDecoration.State state) {
/* 2445 */     Objects.requireNonNull(state, "state");
/*      */     
/* 2447 */     TextDecoration.State oldState = decoration(decoration);
/* 2448 */     if (oldState == TextDecoration.State.NOT_SET) {
/* 2449 */       return style(style().decoration(decoration, state));
/*      */     }
/* 2451 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @NotNull
/*      */   default Map<TextDecoration, TextDecoration.State> decorations() {
/* 2462 */     return style().decorations();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component decorations(@NotNull Map<TextDecoration, TextDecoration.State> decorations) {
/* 2477 */     return style(style().decorations(decorations));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   default ClickEvent clickEvent() {
/* 2488 */     return style().clickEvent();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component clickEvent(@Nullable ClickEvent event) {
/* 2501 */     return style(style().clickEvent(event));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   default HoverEvent<?> hoverEvent() {
/* 2512 */     return style().hoverEvent();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component hoverEvent(@Nullable HoverEventSource<?> source) {
/* 2525 */     return style(style().hoverEvent(source));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   default String insertion() {
/* 2536 */     return style().insertion();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component insertion(@Nullable String insertion) {
/* 2549 */     return style(style().insertion(insertion));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   default boolean hasStyling() {
/* 2560 */     return !style().isEmpty();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @ScopedComponentOverrideNotRequired
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component replaceText(@NotNull Consumer<TextReplacementConfig.Builder> configurer) {
/* 2573 */     Objects.requireNonNull(configurer, "configurer");
/* 2574 */     return replaceText((TextReplacementConfig)AbstractBuilder.configureAndBuild(TextReplacementConfig.builder(), configurer));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @ScopedComponentOverrideNotRequired
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component replaceText(@NotNull TextReplacementConfig config) {
/* 2587 */     Objects.requireNonNull(config, "replacement");
/* 2588 */     if (!(config instanceof TextReplacementConfigImpl)) {
/* 2589 */       throw new IllegalArgumentException("Provided replacement was a custom TextReplacementConfig implementation, which is not supported.");
/*      */     }
/* 2591 */     return TextReplacementRenderer.INSTANCE.render(this, ((TextReplacementConfigImpl)config).createState());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @ScopedComponentOverrideNotRequired
/*      */   @NotNull
/*      */   default Component compact() {
/* 2602 */     return ComponentCompaction.compact(this, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @NotNull
/*      */   Iterable<Component> iterable(@NotNull ComponentIteratorType type, @NotNull ComponentIteratorFlag... flags) {
/* 2614 */     return iterable(type, (flags == null) ? Collections.<ComponentIteratorFlag>emptySet() : MonkeyBars.enumSet(ComponentIteratorFlag.class, (Enum[])flags));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @NotNull
/*      */   default Iterable<Component> iterable(@NotNull ComponentIteratorType type, @NotNull Set<ComponentIteratorFlag> flags) {
/* 2626 */     Objects.requireNonNull(type, "type");
/* 2627 */     Objects.requireNonNull(flags, "flags");
/* 2628 */     return (Iterable<Component>)new ForwardingIterator(() -> iterator(type, flags), () -> spliterator(type, flags));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @NotNull
/*      */   Iterator<Component> iterator(@NotNull ComponentIteratorType type, @NotNull ComponentIteratorFlag... flags) {
/* 2642 */     return iterator(type, (flags == null) ? Collections.<ComponentIteratorFlag>emptySet() : MonkeyBars.enumSet(ComponentIteratorFlag.class, (Enum[])flags));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @NotNull
/*      */   default Iterator<Component> iterator(@NotNull ComponentIteratorType type, @NotNull Set<ComponentIteratorFlag> flags) {
/* 2656 */     return new ComponentIterator(this, Objects.<ComponentIteratorType>requireNonNull(type, "type"), Objects.<Set<ComponentIteratorFlag>>requireNonNull(flags, "flags"));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @NotNull
/*      */   Spliterator<Component> spliterator(@NotNull ComponentIteratorType type, @NotNull ComponentIteratorFlag... flags) {
/* 2670 */     return spliterator(type, (flags == null) ? Collections.<ComponentIteratorFlag>emptySet() : MonkeyBars.enumSet(ComponentIteratorFlag.class, (Enum[])flags));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @NotNull
/*      */   default Spliterator<Component> spliterator(@NotNull ComponentIteratorType type, @NotNull Set<ComponentIteratorFlag> flags) {
/* 2684 */     return Spliterators.spliteratorUnknownSize(iterator(type, flags), 1296);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   @ScheduledForRemoval(inVersion = "5.0.0")
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component replaceText(@NotNull String search, @Nullable ComponentLike replacement) {
/* 2700 */     return replaceText(b -> b.matchLiteral(search).replacement(replacement));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   @ScheduledForRemoval(inVersion = "5.0.0")
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component replaceText(@NotNull Pattern pattern, @NotNull Function<TextComponent.Builder, ComponentLike> replacement) {
/* 2716 */     return replaceText(b -> b.match(pattern).replacement(replacement));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   @ScheduledForRemoval(inVersion = "5.0.0")
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component replaceFirstText(@NotNull String search, @Nullable ComponentLike replacement) {
/* 2732 */     return replaceText(b -> b.matchLiteral(search).once().replacement(replacement));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   @ScheduledForRemoval(inVersion = "5.0.0")
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component replaceFirstText(@NotNull Pattern pattern, @NotNull Function<TextComponent.Builder, ComponentLike> replacement) {
/* 2748 */     return replaceText(b -> b.match(pattern).once().replacement(replacement));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   @ScheduledForRemoval(inVersion = "5.0.0")
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component replaceText(@NotNull String search, @Nullable ComponentLike replacement, int numberOfReplacements) {
/* 2765 */     return replaceText(b -> b.matchLiteral(search).times(numberOfReplacements).replacement(replacement));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   @ScheduledForRemoval(inVersion = "5.0.0")
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component replaceText(@NotNull Pattern pattern, @NotNull Function<TextComponent.Builder, ComponentLike> replacement, int numberOfReplacements) {
/* 2782 */     return replaceText(b -> b.match(pattern).times(numberOfReplacements).replacement(replacement));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   @ScheduledForRemoval(inVersion = "5.0.0")
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component replaceText(@NotNull String search, @Nullable ComponentLike replacement, @NotNull IntFunction2<PatternReplacementResult> fn) {
/* 2801 */     return replaceText(b -> b.matchLiteral(search).replacement(replacement).condition(fn));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   @ScheduledForRemoval(inVersion = "5.0.0")
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   default Component replaceText(@NotNull Pattern pattern, @NotNull Function<TextComponent.Builder, ComponentLike> replacement, @NotNull IntFunction2<PatternReplacementResult> fn) {
/* 2820 */     return replaceText(b -> b.match(pattern).replacement(replacement).condition(fn));
/*      */   }
/*      */ 
/*      */   
/*      */   default void componentBuilderApply(@NotNull ComponentBuilder<?, ?> component) {
/* 2825 */     component.append(this);
/*      */   }
/*      */   
/*      */   @NotNull
/*      */   default Component asComponent() {
/* 2830 */     return this;
/*      */   }
/*      */   
/*      */   @NotNull
/*      */   default HoverEvent<Component> asHoverEvent(@NotNull UnaryOperator<Component> op) {
/* 2835 */     return HoverEvent.showText(op.apply(this));
/*      */   }
/*      */   
/*      */   @NotNull
/*      */   default Stream<? extends ExaminableProperty> examinableProperties() {
/* 2840 */     return Stream.of(new ExaminableProperty[] {
/* 2841 */           ExaminableProperty.of("style", style()), 
/* 2842 */           ExaminableProperty.of("children", children())
/*      */         });
/*      */   }
/*      */   
/*      */   @NotNull
/*      */   List<Component> children();
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   Component children(@NotNull List<? extends ComponentLike> paramList);
/*      */   
/*      */   @NotNull
/*      */   Style style();
/*      */   
/*      */   @Contract(pure = true)
/*      */   @NotNull
/*      */   Component style(@NotNull Style paramStyle);
/*      */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\Component.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */