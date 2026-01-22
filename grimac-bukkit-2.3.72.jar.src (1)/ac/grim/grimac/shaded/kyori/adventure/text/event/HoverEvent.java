/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.event;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Keyed;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.nbt.api.BinaryTagHolder;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.ComponentLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.StyleBuilderApplicable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.renderer.ComponentRenderer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Index;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.UUID;
/*     */ import java.util.function.UnaryOperator;
/*     */ import java.util.stream.Stream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class HoverEvent<V>
/*     */   implements Examinable, HoverEventSource<V>, StyleBuilderApplicable
/*     */ {
/*     */   private final Action<V> action;
/*     */   private final V value;
/*     */   
/*     */   @NotNull
/*     */   public static HoverEvent<Component> showText(@NotNull ComponentLike text) {
/*  70 */     return showText(text.asComponent());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static HoverEvent<Component> showText(@NotNull Component text) {
/*  81 */     return new HoverEvent<>(Action.SHOW_TEXT, text);
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
/*     */   public static HoverEvent<ShowItem> showItem(@NotNull Key item, int count) {
/*  93 */     return showItem((Keyed)item, count, Collections.emptyMap());
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
/*     */   public static HoverEvent<ShowItem> showItem(@NotNull Keyed item, int count) {
/* 105 */     return showItem(item, count, Collections.emptyMap());
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
/*     */   @Deprecated
/*     */   @NotNull
/*     */   public static HoverEvent<ShowItem> showItem(@NotNull Key item, int count, @Nullable BinaryTagHolder nbt) {
/* 119 */     return showItem(ShowItem.showItem(item, count, nbt));
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
/*     */   @Deprecated
/*     */   @NotNull
/*     */   public static HoverEvent<ShowItem> showItem(@NotNull Keyed item, int count, @Nullable BinaryTagHolder nbt) {
/* 134 */     return showItem(ShowItem.showItem(item, count, nbt));
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
/*     */   @NotNull
/*     */   public static HoverEvent<ShowItem> showItem(@NotNull Keyed item, int count, @NotNull Map<Key, ? extends DataComponentValue> dataComponents) {
/* 147 */     return showItem(ShowItem.showItem(item, count, dataComponents));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static HoverEvent<ShowItem> showItem(@NotNull ShowItem item) {
/* 158 */     return new HoverEvent<>(Action.SHOW_ITEM, item);
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
/*     */   @NotNull
/*     */   public static HoverEvent<ShowEntity> showEntity(@NotNull Key type, @NotNull UUID id) {
/* 172 */     return showEntity(type, id, (Component)null);
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
/*     */   @NotNull
/*     */   public static HoverEvent<ShowEntity> showEntity(@NotNull Keyed type, @NotNull UUID id) {
/* 186 */     return showEntity(type, id, (Component)null);
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
/*     */   @NotNull
/*     */   public static HoverEvent<ShowEntity> showEntity(@NotNull Key type, @NotNull UUID id, @Nullable Component name) {
/* 201 */     return showEntity(ShowEntity.of(type, id, name));
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
/*     */   @NotNull
/*     */   public static HoverEvent<ShowEntity> showEntity(@NotNull Keyed type, @NotNull UUID id, @Nullable Component name) {
/* 216 */     return showEntity(ShowEntity.of(type, id, name));
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
/*     */   @NotNull
/*     */   public static HoverEvent<ShowEntity> showEntity(@NotNull ShowEntity entity) {
/* 229 */     return new HoverEvent<>(Action.SHOW_ENTITY, entity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @NotNull
/*     */   public static HoverEvent<String> showAchievement(@NotNull String value) {
/* 242 */     return new HoverEvent<>(Action.SHOW_ACHIEVEMENT, value);
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
/*     */   @NotNull
/*     */   public static <V> HoverEvent<V> hoverEvent(@NotNull Action<V> action, @NotNull V value) {
/* 255 */     return new HoverEvent<>(action, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private HoverEvent(@NotNull Action<V> action, @NotNull V value) {
/* 262 */     this.action = Objects.<Action<V>>requireNonNull(action, "action");
/* 263 */     this.value = Objects.requireNonNull(value, "value");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public Action<V> action() {
/* 273 */     return this.action;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public V value() {
/* 283 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public HoverEvent<V> value(@NotNull V value) {
/* 294 */     return new HoverEvent(this.action, value);
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
/*     */   @NotNull
/*     */   public <C> HoverEvent<V> withRenderedValue(@NotNull ComponentRenderer<C> renderer, @NotNull C context) {
/* 307 */     V oldValue = this.value;
/* 308 */     V newValue = this.action.renderer.render(renderer, context, oldValue);
/* 309 */     if (newValue != oldValue) return new HoverEvent(this.action, newValue); 
/* 310 */     return this;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public HoverEvent<V> asHoverEvent() {
/* 315 */     return this;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public HoverEvent<V> asHoverEvent(@NotNull UnaryOperator<V> op) {
/* 320 */     if (op == UnaryOperator.identity()) return this; 
/* 321 */     return new HoverEvent(this.action, op.apply(this.value));
/*     */   }
/*     */ 
/*     */   
/*     */   public void styleApply(Style.Builder style) {
/* 326 */     style.hoverEvent(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 331 */     if (this == other) return true; 
/* 332 */     if (other == null || getClass() != other.getClass()) return false; 
/* 333 */     HoverEvent<?> that = (HoverEvent)other;
/* 334 */     return (this.action == that.action && this.value.equals(that.value));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 339 */     int result = this.action.hashCode();
/* 340 */     result = 31 * result + this.value.hashCode();
/* 341 */     return result;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 346 */     return Stream.of(new ExaminableProperty[] {
/* 347 */           ExaminableProperty.of("action", this.action), 
/* 348 */           ExaminableProperty.of("value", this.value)
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 354 */     return Internals.toString(this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class ShowItem
/*     */     implements Examinable
/*     */   {
/*     */     private final Key item;
/*     */ 
/*     */     
/*     */     private final int count;
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private final BinaryTagHolder nbt;
/*     */ 
/*     */     
/*     */     private final Map<Key, DataComponentValue> dataComponents;
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public static ShowItem showItem(@NotNull Key item, int count) {
/* 377 */       return showItem((Keyed)item, count, Collections.emptyMap());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     @ScheduledForRemoval(inVersion = "5.0.0")
/*     */     @NotNull
/*     */     public static ShowItem of(@NotNull Key item, int count) {
/* 392 */       return showItem((Keyed)item, count, Collections.emptyMap());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public static ShowItem showItem(@NotNull Keyed item, int count) {
/* 404 */       return showItem(item, count, Collections.emptyMap());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     @ScheduledForRemoval(inVersion = "5.0.0")
/*     */     @NotNull
/*     */     public static ShowItem of(@NotNull Keyed item, int count) {
/* 419 */       return of(item, count, (BinaryTagHolder)null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     @NotNull
/*     */     public static ShowItem showItem(@NotNull Key item, int count, @Nullable BinaryTagHolder nbt) {
/* 434 */       return new ShowItem(Objects.<Key>requireNonNull(item, "item"), count, nbt, Collections.emptyMap());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     @ScheduledForRemoval(inVersion = "5.0.0")
/*     */     @NotNull
/*     */     public static ShowItem of(@NotNull Key item, int count, @Nullable BinaryTagHolder nbt) {
/* 450 */       return new ShowItem(Objects.<Key>requireNonNull(item, "item"), count, nbt, Collections.emptyMap());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     @NotNull
/*     */     public static ShowItem showItem(@NotNull Keyed item, int count, @Nullable BinaryTagHolder nbt) {
/* 465 */       return new ShowItem(((Keyed)Objects.<Keyed>requireNonNull(item, "item")).key(), count, nbt, Collections.emptyMap());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     @ScheduledForRemoval(inVersion = "5.0.0")
/*     */     @NotNull
/*     */     public static ShowItem of(@NotNull Keyed item, int count, @Nullable BinaryTagHolder nbt) {
/* 481 */       return new ShowItem(((Keyed)Objects.<Keyed>requireNonNull(item, "item")).key(), count, nbt, Collections.emptyMap());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public static ShowItem showItem(@NotNull Keyed item, int count, @NotNull Map<Key, ? extends DataComponentValue> dataComponents) {
/* 495 */       return new ShowItem(((Keyed)Objects.<Keyed>requireNonNull(item, "item")).key(), count, null, dataComponents);
/*     */     }
/*     */     
/*     */     private ShowItem(@NotNull Key item, int count, @Nullable BinaryTagHolder nbt, @NotNull Map<Key, ? extends DataComponentValue> dataComponents) {
/* 499 */       this.item = item;
/* 500 */       this.count = count;
/* 501 */       this.nbt = nbt;
/* 502 */       this.dataComponents = Collections.unmodifiableMap(new HashMap<>(dataComponents));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public Key item() {
/* 512 */       return this.item;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public ShowItem item(@NotNull Key item) {
/* 523 */       if (((Key)Objects.<Key>requireNonNull(item, "item")).equals(this.item)) return this; 
/* 524 */       return new ShowItem(item, this.count, this.nbt, this.dataComponents);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int count() {
/* 534 */       return this.count;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public ShowItem count(int count) {
/* 545 */       if (count == this.count) return this; 
/* 546 */       return new ShowItem(this.item, count, this.nbt, this.dataComponents);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     @Nullable
/*     */     public BinaryTagHolder nbt() {
/* 560 */       return this.nbt;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     @NotNull
/*     */     public ShowItem nbt(@Nullable BinaryTagHolder nbt) {
/* 575 */       if (Objects.equals(nbt, this.nbt)) return this; 
/* 576 */       return new ShowItem(this.item, this.count, nbt, Collections.emptyMap());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public Map<Key, DataComponentValue> dataComponents() {
/* 589 */       return this.dataComponents;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public ShowItem dataComponents(@NotNull Map<Key, DataComponentValue> holder) {
/* 603 */       if (Objects.equals(this.dataComponents, holder)) return this; 
/* 604 */       return new ShowItem(this.item, this.count, null, holder.isEmpty() ? Collections.<Key, DataComponentValue>emptyMap() : Collections.<Key, DataComponentValue>unmodifiableMap(new HashMap<>(holder)));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public <V extends DataComponentValue> Map<Key, V> dataComponentsAs(@NotNull Class<V> targetType) {
/* 618 */       if (this.dataComponents.isEmpty()) {
/* 619 */         return Collections.emptyMap();
/*     */       }
/* 621 */       Map<Key, V> results = new HashMap<>(this.dataComponents.size());
/* 622 */       for (Map.Entry<Key, DataComponentValue> entry : this.dataComponents.entrySet()) {
/* 623 */         results.put(entry.getKey(), DataComponentValueConverterRegistry.convert(targetType, entry.getKey(), entry.getValue()));
/*     */       }
/* 625 */       return Collections.unmodifiableMap(results);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean equals(@Nullable Object other) {
/* 631 */       if (this == other) return true; 
/* 632 */       if (other == null || getClass() != other.getClass()) return false; 
/* 633 */       ShowItem that = (ShowItem)other;
/* 634 */       return (this.item.equals(that.item) && this.count == that.count && Objects.equals(this.nbt, that.nbt) && Objects.equals(this.dataComponents, that.dataComponents));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 639 */       int result = this.item.hashCode();
/* 640 */       result = 31 * result + Integer.hashCode(this.count);
/* 641 */       result = 31 * result + Objects.hashCode(this.nbt);
/* 642 */       result = 31 * result + Objects.hashCode(this.dataComponents);
/* 643 */       return result;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Stream<? extends ExaminableProperty> examinableProperties() {
/* 648 */       return Stream.of(new ExaminableProperty[] {
/* 649 */             ExaminableProperty.of("item", this.item), 
/* 650 */             ExaminableProperty.of("count", this.count), 
/* 651 */             ExaminableProperty.of("nbt", this.nbt), 
/* 652 */             ExaminableProperty.of("dataComponents", this.dataComponents)
/*     */           });
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 658 */       return Internals.toString(this);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class ShowEntity
/*     */     implements Examinable
/*     */   {
/*     */     private final Key type;
/*     */ 
/*     */ 
/*     */     
/*     */     private final UUID id;
/*     */ 
/*     */     
/*     */     private final Component name;
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public static ShowEntity showEntity(@NotNull Key type, @NotNull UUID id) {
/* 681 */       return showEntity(type, id, (Component)null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     @ScheduledForRemoval(inVersion = "5.0.0")
/*     */     @NotNull
/*     */     public static ShowEntity of(@NotNull Key type, @NotNull UUID id) {
/* 696 */       return of(type, id, (Component)null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public static ShowEntity showEntity(@NotNull Keyed type, @NotNull UUID id) {
/* 708 */       return showEntity(type, id, (Component)null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     @ScheduledForRemoval(inVersion = "5.0.0")
/*     */     @NotNull
/*     */     public static ShowEntity of(@NotNull Keyed type, @NotNull UUID id) {
/* 723 */       return of(type, id, (Component)null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public static ShowEntity showEntity(@NotNull Key type, @NotNull UUID id, @Nullable Component name) {
/* 736 */       return new ShowEntity(Objects.<Key>requireNonNull(type, "type"), Objects.<UUID>requireNonNull(id, "id"), name);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     @ScheduledForRemoval(inVersion = "5.0.0")
/*     */     @NotNull
/*     */     public static ShowEntity of(@NotNull Key type, @NotNull UUID id, @Nullable Component name) {
/* 752 */       return new ShowEntity(Objects.<Key>requireNonNull(type, "type"), Objects.<UUID>requireNonNull(id, "id"), name);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public static ShowEntity showEntity(@NotNull Keyed type, @NotNull UUID id, @Nullable Component name) {
/* 765 */       return new ShowEntity(((Keyed)Objects.<Keyed>requireNonNull(type, "type")).key(), Objects.<UUID>requireNonNull(id, "id"), name);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     @ScheduledForRemoval(inVersion = "5.0.0")
/*     */     @NotNull
/*     */     public static ShowEntity of(@NotNull Keyed type, @NotNull UUID id, @Nullable Component name) {
/* 781 */       return new ShowEntity(((Keyed)Objects.<Keyed>requireNonNull(type, "type")).key(), Objects.<UUID>requireNonNull(id, "id"), name);
/*     */     }
/*     */     
/*     */     private ShowEntity(@NotNull Key type, @NotNull UUID id, @Nullable Component name) {
/* 785 */       this.type = type;
/* 786 */       this.id = id;
/* 787 */       this.name = name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public Key type() {
/* 797 */       return this.type;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public ShowEntity type(@NotNull Key type) {
/* 808 */       if (((Key)Objects.<Key>requireNonNull(type, "type")).equals(this.type)) return this; 
/* 809 */       return new ShowEntity(type, this.id, this.name);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public ShowEntity type(@NotNull Keyed type) {
/* 820 */       return type(((Keyed)Objects.<Keyed>requireNonNull(type, "type")).key());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public UUID id() {
/* 830 */       return this.id;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public ShowEntity id(@NotNull UUID id) {
/* 841 */       if (((UUID)Objects.<UUID>requireNonNull(id)).equals(this.id)) return this; 
/* 842 */       return new ShowEntity(this.type, id, this.name);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Component name() {
/* 852 */       return this.name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public ShowEntity name(@Nullable Component name) {
/* 863 */       if (Objects.equals(name, this.name)) return this; 
/* 864 */       return new ShowEntity(this.type, this.id, name);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(@Nullable Object other) {
/* 869 */       if (this == other) return true; 
/* 870 */       if (other == null || getClass() != other.getClass()) return false; 
/* 871 */       ShowEntity that = (ShowEntity)other;
/* 872 */       return (this.type.equals(that.type) && this.id.equals(that.id) && Objects.equals(this.name, that.name));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 877 */       int result = this.type.hashCode();
/* 878 */       result = 31 * result + this.id.hashCode();
/* 879 */       result = 31 * result + Objects.hashCode(this.name);
/* 880 */       return result;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Stream<? extends ExaminableProperty> examinableProperties() {
/* 885 */       return Stream.of(new ExaminableProperty[] {
/* 886 */             ExaminableProperty.of("type", this.type), 
/* 887 */             ExaminableProperty.of("id", this.id), 
/* 888 */             ExaminableProperty.of("name", this.name)
/*     */           });
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 894 */       return Internals.toString(this);
/*     */     }
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
/*     */   public static final class Action<V>
/*     */   {
/* 910 */     public static final Action<Component> SHOW_TEXT = new Action("show_text", (Class)Component.class, true, (Renderer)new Renderer<Component>() {
/*     */           @NotNull
/*     */           public <C> Component render(@NotNull ComponentRenderer<C> renderer, @NotNull C context, @NotNull Component value) {
/* 913 */             return renderer.render(value, context);
/*     */           }
/*     */         });
/*     */     @FunctionalInterface
/*     */     static interface Renderer<V> {
/*     */       @NotNull
/*     */       <C> V render(@NotNull ComponentRenderer<C> param2ComponentRenderer, @NotNull C param2C, @NotNull V param2V); }
/*     */     
/* 921 */     public static final Action<HoverEvent.ShowItem> SHOW_ITEM = new Action("show_item", (Class)HoverEvent.ShowItem.class, true, (Renderer)new Renderer<HoverEvent.ShowItem>() {
/*     */           @NotNull
/*     */           public <C> HoverEvent.ShowItem render(@NotNull ComponentRenderer<C> renderer, @NotNull C context, @NotNull HoverEvent.ShowItem value) {
/* 924 */             return value;
/*     */           }
/*     */         });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 932 */     public static final Action<HoverEvent.ShowEntity> SHOW_ENTITY = new Action("show_entity", (Class)HoverEvent.ShowEntity.class, true, (Renderer)new Renderer<HoverEvent.ShowEntity>() {
/*     */           @NotNull
/*     */           public <C> HoverEvent.ShowEntity render(@NotNull ComponentRenderer<C> renderer, @NotNull C context, @NotNull HoverEvent.ShowEntity value) {
/* 935 */             if (value.name == null) return value; 
/* 936 */             return value.name(renderer.render(value.name, context));
/*     */           }
/*     */         });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/* 946 */     public static final Action<String> SHOW_ACHIEVEMENT = new Action("show_achievement", (Class)String.class, true, (Renderer)new Renderer<String>() {
/*     */           @NotNull
/*     */           public <C> String render(@NotNull ComponentRenderer<C> renderer, @NotNull C context, @NotNull String value) {
/* 949 */             return value;
/*     */           }
/*     */         });
/*     */ 
/*     */     
/*     */     public static final Index<String, Action<?>> NAMES;
/*     */     private final String name;
/*     */     
/*     */     static {
/* 958 */       NAMES = Index.create(constant -> constant.name, (Object[])new Action[] { SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY, SHOW_ACHIEVEMENT });
/*     */     }
/*     */ 
/*     */     
/*     */     private final Class<V> type;
/*     */     
/*     */     private final boolean readable;
/*     */     
/*     */     private final Renderer<V> renderer;
/*     */ 
/*     */     
/*     */     Action(String name, Class<V> type, boolean readable, Renderer<V> renderer) {
/* 970 */       this.name = name;
/* 971 */       this.type = type;
/* 972 */       this.readable = readable;
/* 973 */       this.renderer = renderer;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public Class<V> type() {
/* 983 */       return this.type;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean readable() {
/* 994 */       return this.readable;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public String toString() {
/* 999 */       return this.name;
/*     */     }
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   static interface Renderer<V> {
/*     */     @NotNull
/*     */     <C> V render(@NotNull ComponentRenderer<C> param1ComponentRenderer, @NotNull C param1C, @NotNull V param1V);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\event\HoverEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */