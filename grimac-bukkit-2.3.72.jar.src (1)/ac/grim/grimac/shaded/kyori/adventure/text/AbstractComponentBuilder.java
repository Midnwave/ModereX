/*     */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.ClickEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEventSource;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.StyleSetter;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextDecoration;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.ARGBLike;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Objects;
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
/*     */ abstract class AbstractComponentBuilder<C extends BuildableComponent<C, B>, B extends ComponentBuilder<C, B>>
/*     */   implements ComponentBuilder<C, B>
/*     */ {
/*  53 */   protected List<Component> children = Collections.emptyList();
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Style style;
/*     */ 
/*     */   
/*     */   private Style.Builder styleBuilder;
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractComponentBuilder(@NotNull C component) {
/*  66 */     List<Component> children = component.children();
/*  67 */     if (!children.isEmpty()) {
/*  68 */       this.children = new ArrayList<>(children);
/*     */     }
/*  70 */     if (component.hasStyling()) {
/*  71 */       this.style = component.style();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public B append(@NotNull Component component) {
/*  78 */     if (component == Component.empty()) return (B)this; 
/*  79 */     prepareChildren();
/*  80 */     this.children.add(Objects.<Component>requireNonNull(component, "component"));
/*  81 */     return (B)this;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public B append(@NotNull Component... components) {
/*  86 */     return append((ComponentLike[])components);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public B append(@NotNull ComponentLike... components) {
/*  92 */     Objects.requireNonNull(components, "components");
/*  93 */     boolean prepared = false;
/*  94 */     for (int i = 0, length = components.length; i < length; i++) {
/*  95 */       Component component = ((ComponentLike)Objects.<ComponentLike>requireNonNull(components[i], "components[?]")).asComponent();
/*  96 */       if (component != Component.empty()) {
/*  97 */         if (!prepared) {
/*  98 */           prepareChildren();
/*  99 */           prepared = true;
/*     */         } 
/* 101 */         this.children.add(Objects.<Component>requireNonNull(component, "components[?]"));
/*     */       } 
/*     */     } 
/* 104 */     return (B)this;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public B append(@NotNull Iterable<? extends ComponentLike> components) {
/* 110 */     Objects.requireNonNull(components, "components");
/* 111 */     boolean prepared = false;
/* 112 */     for (ComponentLike like : components) {
/* 113 */       Component component = ((ComponentLike)Objects.<ComponentLike>requireNonNull(like, "components[?]")).asComponent();
/* 114 */       if (component != Component.empty()) {
/* 115 */         if (!prepared) {
/* 116 */           prepareChildren();
/* 117 */           prepared = true;
/*     */         } 
/* 119 */         this.children.add(Objects.<Component>requireNonNull(component, "components[?]"));
/*     */       } 
/*     */     } 
/* 122 */     return (B)this;
/*     */   }
/*     */   
/*     */   private void prepareChildren() {
/* 126 */     if (this.children == Collections.emptyList()) {
/* 127 */       this.children = new ArrayList<>();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public B applyDeep(@NotNull Consumer<? super ComponentBuilder<?, ?>> consumer) {
/* 134 */     apply(consumer);
/* 135 */     if (this.children == Collections.emptyList()) {
/* 136 */       return (B)this;
/*     */     }
/* 138 */     ListIterator<Component> it = this.children.listIterator();
/* 139 */     while (it.hasNext()) {
/* 140 */       Component child = it.next();
/* 141 */       if (!(child instanceof BuildableComponent)) {
/*     */         continue;
/*     */       }
/* 144 */       ComponentBuilder<?, ?> childBuilder = (ComponentBuilder<?, ?>)((BuildableComponent)child).toBuilder();
/* 145 */       childBuilder.applyDeep(consumer);
/* 146 */       it.set((Component)childBuilder.build());
/*     */     } 
/* 148 */     return (B)this;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public B mapChildren(@NotNull Function<BuildableComponent<?, ?>, ? extends BuildableComponent<?, ?>> function) {
/* 154 */     if (this.children == Collections.emptyList()) {
/* 155 */       return (B)this;
/*     */     }
/* 157 */     ListIterator<Component> it = this.children.listIterator();
/* 158 */     while (it.hasNext()) {
/* 159 */       Component child = it.next();
/* 160 */       if (!(child instanceof BuildableComponent)) {
/*     */         continue;
/*     */       }
/* 163 */       BuildableComponent<?, ?> mappedChild = Objects.<BuildableComponent<?, ?>>requireNonNull(function.apply((BuildableComponent<?, ?>)child), "mappedChild");
/* 164 */       if (child == mappedChild) {
/*     */         continue;
/*     */       }
/* 167 */       it.set(mappedChild);
/*     */     } 
/* 169 */     return (B)this;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public B mapChildrenDeep(@NotNull Function<BuildableComponent<?, ?>, ? extends BuildableComponent<?, ?>> function) {
/* 175 */     if (this.children == Collections.emptyList()) {
/* 176 */       return (B)this;
/*     */     }
/* 178 */     ListIterator<Component> it = this.children.listIterator();
/* 179 */     while (it.hasNext()) {
/* 180 */       Component child = it.next();
/* 181 */       if (!(child instanceof BuildableComponent)) {
/*     */         continue;
/*     */       }
/* 184 */       BuildableComponent<?, ?> mappedChild = Objects.<BuildableComponent<?, ?>>requireNonNull(function.apply((BuildableComponent<?, ?>)child), "mappedChild");
/* 185 */       if (mappedChild.children().isEmpty()) {
/* 186 */         if (child == mappedChild) {
/*     */           continue;
/*     */         }
/* 189 */         it.set(mappedChild); continue;
/*     */       } 
/* 191 */       ComponentBuilder<?, ?> builder = (ComponentBuilder<?, ?>)mappedChild.toBuilder();
/* 192 */       builder.mapChildrenDeep(function);
/* 193 */       it.set((Component)builder.build());
/*     */     } 
/*     */     
/* 196 */     return (B)this;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public List<Component> children() {
/* 201 */     return Collections.unmodifiableList(this.children);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public B style(@NotNull Style style) {
/* 207 */     this.style = style;
/* 208 */     this.styleBuilder = null;
/* 209 */     return (B)this;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public B style(@NotNull Consumer<Style.Builder> consumer) {
/* 215 */     consumer.accept(styleBuilder());
/* 216 */     return (B)this;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public B font(@Nullable Key font) {
/* 222 */     styleBuilder().font(font);
/* 223 */     return (B)this;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public B color(@Nullable TextColor color) {
/* 229 */     styleBuilder().color(color);
/* 230 */     return (B)this;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public B colorIfAbsent(@Nullable TextColor color) {
/* 236 */     styleBuilder().colorIfAbsent(color);
/* 237 */     return (B)this;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public B shadowColor(@Nullable ARGBLike argb) {
/* 243 */     styleBuilder().shadowColor(argb);
/* 244 */     return (B)this;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public B shadowColorIfAbsent(@Nullable ARGBLike argb) {
/* 250 */     styleBuilder().shadowColorIfAbsent(argb);
/* 251 */     return (B)this;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public B decoration(@NotNull TextDecoration decoration, TextDecoration.State state) {
/* 257 */     styleBuilder().decoration(decoration, state);
/* 258 */     return (B)this;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public B decorationIfAbsent(@NotNull TextDecoration decoration, TextDecoration.State state) {
/* 264 */     styleBuilder().decorationIfAbsent(decoration, state);
/* 265 */     return (B)this;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public B clickEvent(@Nullable ClickEvent event) {
/* 271 */     styleBuilder().clickEvent(event);
/* 272 */     return (B)this;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public B hoverEvent(@Nullable HoverEventSource<?> source) {
/* 278 */     styleBuilder().hoverEvent(source);
/* 279 */     return (B)this;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public B insertion(@Nullable String insertion) {
/* 285 */     styleBuilder().insertion(insertion);
/* 286 */     return (B)this;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public B mergeStyle(@NotNull Component that, @NotNull Set<Style.Merge> merges) {
/* 292 */     Style thatStyle = ((Component)Objects.<Component>requireNonNull(that, "that")).style();
/* 293 */     if (thatStyle.isEmpty() && merges.isEmpty()) return (B)this; 
/* 294 */     styleBuilder().merge(thatStyle, merges);
/* 295 */     return (B)this;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public B resetStyle() {
/* 301 */     this.style = null;
/* 302 */     this.styleBuilder = null;
/* 303 */     return (B)this;
/*     */   }
/*     */   
/*     */   private Style.Builder styleBuilder() {
/* 307 */     if (this.styleBuilder == null) {
/* 308 */       if (this.style != null) {
/* 309 */         this.styleBuilder = this.style.toBuilder();
/* 310 */         this.style = null;
/*     */       } else {
/* 312 */         this.styleBuilder = Style.style();
/*     */       } 
/*     */     }
/* 315 */     return this.styleBuilder;
/*     */   }
/*     */ 
/*     */   
/*     */   protected final boolean hasStyle() {
/* 320 */     return (this.styleBuilder != null || this.style != null);
/*     */   }
/*     */   @NotNull
/*     */   protected Style buildStyle() {
/* 324 */     if (this.styleBuilder != null)
/* 325 */       return this.styleBuilder.build(); 
/* 326 */     if (this.style != null) {
/* 327 */       return this.style;
/*     */     }
/* 329 */     return Style.empty();
/*     */   }
/*     */   
/*     */   protected AbstractComponentBuilder() {}
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\AbstractComponentBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */