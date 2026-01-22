/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.format;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.ClickEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEventSource;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.ARGBLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Buildable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
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
/*     */ final class StyleImpl
/*     */   implements Style
/*     */ {
/*  43 */   static final StyleImpl EMPTY = new StyleImpl(null, null, null, DecorationMap.EMPTY, null, null, null);
/*     */   
/*     */   @Nullable
/*     */   final Key font;
/*     */   
/*     */   @Nullable
/*     */   final TextColor color;
/*     */   @Nullable
/*     */   final ShadowColor shadowColor;
/*     */   @NotNull
/*     */   final DecorationMap decorations;
/*     */   @Nullable
/*     */   final ClickEvent clickEvent;
/*     */   @Nullable
/*     */   final HoverEvent<?> hoverEvent;
/*     */   @Nullable
/*     */   final String insertion;
/*     */   
/*     */   StyleImpl(@Nullable Key font, @Nullable TextColor color, @Nullable ShadowColor shadowColor, @NotNull Map<TextDecoration, TextDecoration.State> decorations, @Nullable ClickEvent clickEvent, @Nullable HoverEvent<?> hoverEvent, @Nullable String insertion) {
/*  62 */     this.font = font;
/*  63 */     this.color = color;
/*  64 */     this.shadowColor = shadowColor;
/*  65 */     this.decorations = DecorationMap.fromMap(decorations);
/*  66 */     this.clickEvent = clickEvent;
/*  67 */     this.hoverEvent = hoverEvent;
/*  68 */     this.insertion = insertion;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Key font() {
/*  73 */     return this.font;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Style font(@Nullable Key font) {
/*  78 */     if (Objects.equals(this.font, font)) return this; 
/*  79 */     return new StyleImpl(font, this.color, this.shadowColor, this.decorations, this.clickEvent, this.hoverEvent, this.insertion);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public TextColor color() {
/*  84 */     return this.color;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Style color(@Nullable TextColor color) {
/*  89 */     if (Objects.equals(this.color, color)) return this; 
/*  90 */     return new StyleImpl(this.font, color, this.shadowColor, this.decorations, this.clickEvent, this.hoverEvent, this.insertion);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Style colorIfAbsent(@Nullable TextColor color) {
/*  95 */     if (this.color == null) {
/*  96 */       return color(color);
/*     */     }
/*  98 */     return this;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public ShadowColor shadowColor() {
/* 103 */     return this.shadowColor;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Style shadowColor(@Nullable ARGBLike argb) {
/* 108 */     if (Objects.equals(this.shadowColor, argb)) return this; 
/* 109 */     return new StyleImpl(this.font, this.color, (argb == null) ? null : ShadowColor.shadowColor(argb), this.decorations, this.clickEvent, this.hoverEvent, this.insertion);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Style shadowColorIfAbsent(@Nullable ARGBLike argb) {
/* 114 */     if (this.shadowColor == null) {
/* 115 */       return shadowColor(argb);
/*     */     }
/* 117 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public TextDecoration.State decoration(@NotNull TextDecoration decoration) {
/* 123 */     TextDecoration.State state = this.decorations.get(decoration);
/* 124 */     if (state != null) {
/* 125 */       return state;
/*     */     }
/* 127 */     throw new IllegalArgumentException(String.format("unknown decoration '%s'", new Object[] { decoration }));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Style decoration(@NotNull TextDecoration decoration, TextDecoration.State state) {
/* 132 */     Objects.requireNonNull(state, "state");
/* 133 */     if (decoration(decoration) == state) return this; 
/* 134 */     return new StyleImpl(this.font, this.color, this.shadowColor, this.decorations.with(decoration, state), this.clickEvent, this.hoverEvent, this.insertion);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Style decorationIfAbsent(@NotNull TextDecoration decoration, TextDecoration.State state) {
/* 139 */     Objects.requireNonNull(state, "state");
/* 140 */     TextDecoration.State oldState = this.decorations.get(decoration);
/* 141 */     if (oldState == TextDecoration.State.NOT_SET) {
/* 142 */       return new StyleImpl(this.font, this.color, this.shadowColor, this.decorations.with(decoration, state), this.clickEvent, this.hoverEvent, this.insertion);
/*     */     }
/* 144 */     if (oldState != null) {
/* 145 */       return this;
/*     */     }
/* 147 */     throw new IllegalArgumentException(String.format("unknown decoration '%s'", new Object[] { decoration }));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Map<TextDecoration, TextDecoration.State> decorations() {
/* 152 */     return this.decorations;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Style decorations(@NotNull Map<TextDecoration, TextDecoration.State> decorations) {
/* 157 */     return new StyleImpl(this.font, this.color, this.shadowColor, DecorationMap.merge(decorations, this.decorations), this.clickEvent, this.hoverEvent, this.insertion);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public ClickEvent clickEvent() {
/* 162 */     return this.clickEvent;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Style clickEvent(@Nullable ClickEvent event) {
/* 167 */     return new StyleImpl(this.font, this.color, this.shadowColor, this.decorations, event, this.hoverEvent, this.insertion);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public HoverEvent<?> hoverEvent() {
/* 172 */     return this.hoverEvent;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Style hoverEvent(@Nullable HoverEventSource<?> source) {
/* 177 */     return new StyleImpl(this.font, this.color, this.shadowColor, this.decorations, this.clickEvent, HoverEventSource.unbox(source), this.insertion);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public String insertion() {
/* 182 */     return this.insertion;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Style insertion(@Nullable String insertion) {
/* 187 */     if (Objects.equals(this.insertion, insertion)) return this; 
/* 188 */     return new StyleImpl(this.font, this.color, this.shadowColor, this.decorations, this.clickEvent, this.hoverEvent, insertion);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Style merge(@NotNull Style that, Style.Merge.Strategy strategy, @NotNull Set<Style.Merge> merges) {
/* 193 */     if (nothingToMerge(that, strategy, merges)) {
/* 194 */       return this;
/*     */     }
/*     */     
/* 197 */     if (isEmpty() && Style.Merge.hasAll(merges))
/*     */     {
/*     */       
/* 200 */       return that;
/*     */     }
/*     */     
/* 203 */     Style.Builder builder = toBuilder();
/* 204 */     builder.merge(that, strategy, merges);
/* 205 */     return builder.build();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Style unmerge(@NotNull Style that) {
/* 210 */     if (isEmpty())
/*     */     {
/* 212 */       return this;
/*     */     }
/*     */     
/* 215 */     Style.Builder builder = new BuilderImpl(this);
/*     */     
/* 217 */     if (Objects.equals(font(), that.font())) {
/* 218 */       builder.font((Key)null);
/*     */     }
/*     */     
/* 221 */     if (Objects.equals(color(), that.color())) {
/* 222 */       builder.color((TextColor)null);
/*     */     }
/*     */     
/* 225 */     if (Objects.equals(shadowColor(), that.shadowColor())) {
/* 226 */       builder.shadowColor(null);
/*     */     }
/*     */     
/* 229 */     for (int i = 0, length = DecorationMap.DECORATIONS.length; i < length; i++) {
/* 230 */       TextDecoration decoration = DecorationMap.DECORATIONS[i];
/* 231 */       if (decoration(decoration) == that.decoration(decoration)) {
/* 232 */         builder.decoration(decoration, TextDecoration.State.NOT_SET);
/*     */       }
/*     */     } 
/*     */     
/* 236 */     if (Objects.equals(clickEvent(), that.clickEvent())) {
/* 237 */       builder.clickEvent((ClickEvent)null);
/*     */     }
/*     */     
/* 240 */     if (Objects.equals(hoverEvent(), that.hoverEvent())) {
/* 241 */       builder.hoverEvent((HoverEventSource<?>)null);
/*     */     }
/*     */     
/* 244 */     if (Objects.equals(insertion(), that.insertion())) {
/* 245 */       builder.insertion((String)null);
/*     */     }
/*     */     
/* 248 */     return builder.build();
/*     */   }
/*     */ 
/*     */   
/*     */   static boolean nothingToMerge(@NotNull Style mergeFrom, Style.Merge.Strategy strategy, @NotNull Set<Style.Merge> merges) {
/* 253 */     if (strategy == Style.Merge.Strategy.NEVER) return true; 
/* 254 */     if (mergeFrom.isEmpty()) return true; 
/* 255 */     if (merges.isEmpty()) return true; 
/* 256 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 261 */     return (this == EMPTY);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Style.Builder toBuilder() {
/* 266 */     return new BuilderImpl(this);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 271 */     return Stream.concat(this.decorations
/* 272 */         .examinableProperties(), 
/* 273 */         Stream.of(new ExaminableProperty[] {
/* 274 */             ExaminableProperty.of("color", this.color), 
/* 275 */             ExaminableProperty.of("shadowColor", this.shadowColor), 
/* 276 */             ExaminableProperty.of("clickEvent", this.clickEvent), 
/* 277 */             ExaminableProperty.of("hoverEvent", this.hoverEvent), 
/* 278 */             ExaminableProperty.of("insertion", this.insertion), 
/* 279 */             ExaminableProperty.of("font", this.font)
/*     */           }));
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public String toString() {
/* 286 */     return Internals.toString(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 291 */     if (this == other) return true; 
/* 292 */     if (!(other instanceof StyleImpl)) return false; 
/* 293 */     StyleImpl that = (StyleImpl)other;
/* 294 */     return (Objects.equals(this.color, that.color) && this.decorations
/* 295 */       .equals(that.decorations) && 
/* 296 */       Objects.equals(this.shadowColor, that.shadowColor) && 
/* 297 */       Objects.equals(this.clickEvent, that.clickEvent) && 
/* 298 */       Objects.equals(this.hoverEvent, that.hoverEvent) && 
/* 299 */       Objects.equals(this.insertion, that.insertion) && 
/* 300 */       Objects.equals(this.font, that.font));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 305 */     int result = Objects.hashCode(this.color);
/* 306 */     result = 31 * result + Objects.hashCode(this.shadowColor);
/* 307 */     result = 31 * result + this.decorations.hashCode();
/* 308 */     result = 31 * result + Objects.hashCode(this.clickEvent);
/* 309 */     result = 31 * result + Objects.hashCode(this.hoverEvent);
/* 310 */     result = 31 * result + Objects.hashCode(this.insertion);
/* 311 */     result = 31 * result + Objects.hashCode(this.font);
/* 312 */     return result;
/*     */   }
/*     */   
/*     */   static final class BuilderImpl
/*     */     implements Style.Builder {
/*     */     @Nullable
/*     */     Key font;
/*     */     @Nullable
/*     */     TextColor color;
/*     */     @Nullable
/*     */     ShadowColor shadowColor;
/*     */     
/*     */     BuilderImpl() {
/* 325 */       this.decorations = DecorationMap.EMPTY; } DecorationMap decorations; @Nullable
/*     */     ClickEvent clickEvent; @Nullable
/*     */     HoverEvent<?> hoverEvent; @Nullable
/*     */     String insertion; BuilderImpl(@NotNull StyleImpl style) {
/* 329 */       this.color = style.color;
/* 330 */       this.shadowColor = style.shadowColor;
/* 331 */       this.decorations = style.decorations;
/* 332 */       this.clickEvent = style.clickEvent;
/* 333 */       this.hoverEvent = style.hoverEvent;
/* 334 */       this.insertion = style.insertion;
/* 335 */       this.font = style.font;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Style.Builder font(@Nullable Key font) {
/* 340 */       this.font = font;
/* 341 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Style.Builder color(@Nullable TextColor color) {
/* 346 */       this.color = color;
/* 347 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Style.Builder colorIfAbsent(@Nullable TextColor color) {
/* 352 */       if (this.color == null) {
/* 353 */         this.color = color;
/*     */       }
/* 355 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Style.Builder shadowColor(@Nullable ARGBLike argb) {
/* 360 */       this.shadowColor = (argb == null) ? null : ShadowColor.shadowColor(argb);
/* 361 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Style.Builder shadowColorIfAbsent(@Nullable ARGBLike argb) {
/* 366 */       if (this.shadowColor == null) {
/* 367 */         this.shadowColor = (argb == null) ? null : ShadowColor.shadowColor(argb);
/*     */       }
/* 369 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Style.Builder decoration(@NotNull TextDecoration decoration, TextDecoration.State state) {
/* 374 */       Objects.requireNonNull(state, "state");
/* 375 */       Objects.requireNonNull(decoration, "decoration");
/* 376 */       this.decorations = this.decorations.with(decoration, state);
/* 377 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Style.Builder decorationIfAbsent(@NotNull TextDecoration decoration, TextDecoration.State state) {
/* 382 */       Objects.requireNonNull(state, "state");
/* 383 */       TextDecoration.State oldState = this.decorations.get(decoration);
/* 384 */       if (oldState == TextDecoration.State.NOT_SET) {
/* 385 */         this.decorations = this.decorations.with(decoration, state);
/*     */       }
/* 387 */       if (oldState != null) {
/* 388 */         return this;
/*     */       }
/* 390 */       throw new IllegalArgumentException(String.format("unknown decoration '%s'", new Object[] { decoration }));
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Style.Builder clickEvent(@Nullable ClickEvent event) {
/* 395 */       this.clickEvent = event;
/* 396 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Style.Builder hoverEvent(@Nullable HoverEventSource<?> source) {
/* 401 */       this.hoverEvent = HoverEventSource.unbox(source);
/* 402 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Style.Builder insertion(@Nullable String insertion) {
/* 407 */       this.insertion = insertion;
/* 408 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Style.Builder merge(@NotNull Style that, Style.Merge.Strategy strategy, @NotNull Set<Style.Merge> merges) {
/* 413 */       Objects.requireNonNull(that, "style");
/* 414 */       Objects.requireNonNull(strategy, "strategy");
/* 415 */       Objects.requireNonNull(merges, "merges");
/*     */       
/* 417 */       if (StyleImpl.nothingToMerge(that, strategy, merges)) {
/* 418 */         return this;
/*     */       }
/*     */       
/* 421 */       if (merges.contains(Style.Merge.COLOR)) {
/* 422 */         TextColor color = that.color();
/* 423 */         if (color != null && (
/* 424 */           strategy == Style.Merge.Strategy.ALWAYS || (strategy == Style.Merge.Strategy.IF_ABSENT_ON_TARGET && this.color == null))) {
/* 425 */           color(color);
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 430 */       if (merges.contains(Style.Merge.SHADOW_COLOR)) {
/* 431 */         ShadowColor shadowColor = that.shadowColor();
/* 432 */         if (shadowColor != null && (
/* 433 */           strategy == Style.Merge.Strategy.ALWAYS || (strategy == Style.Merge.Strategy.IF_ABSENT_ON_TARGET && this.shadowColor == null))) {
/* 434 */           shadowColor(shadowColor);
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 439 */       if (merges.contains(Style.Merge.DECORATIONS)) {
/* 440 */         for (int i = 0, length = DecorationMap.DECORATIONS.length; i < length; i++) {
/* 441 */           TextDecoration decoration = DecorationMap.DECORATIONS[i];
/* 442 */           TextDecoration.State state = that.decoration(decoration);
/* 443 */           if (state != TextDecoration.State.NOT_SET) {
/* 444 */             if (strategy == Style.Merge.Strategy.ALWAYS) {
/* 445 */               decoration(decoration, state);
/* 446 */             } else if (strategy == Style.Merge.Strategy.IF_ABSENT_ON_TARGET) {
/* 447 */               decorationIfAbsent(decoration, state);
/*     */             } 
/*     */           }
/*     */         } 
/*     */       }
/*     */       
/* 453 */       if (merges.contains(Style.Merge.EVENTS)) {
/* 454 */         ClickEvent clickEvent = that.clickEvent();
/* 455 */         if (clickEvent != null && (
/* 456 */           strategy == Style.Merge.Strategy.ALWAYS || (strategy == Style.Merge.Strategy.IF_ABSENT_ON_TARGET && this.clickEvent == null))) {
/* 457 */           clickEvent(clickEvent);
/*     */         }
/*     */ 
/*     */         
/* 461 */         HoverEvent<?> hoverEvent = that.hoverEvent();
/* 462 */         if (hoverEvent != null && (
/* 463 */           strategy == Style.Merge.Strategy.ALWAYS || (strategy == Style.Merge.Strategy.IF_ABSENT_ON_TARGET && this.hoverEvent == null))) {
/* 464 */           hoverEvent((HoverEventSource<?>)hoverEvent);
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 469 */       if (merges.contains(Style.Merge.INSERTION)) {
/* 470 */         String insertion = that.insertion();
/* 471 */         if (insertion != null && (
/* 472 */           strategy == Style.Merge.Strategy.ALWAYS || (strategy == Style.Merge.Strategy.IF_ABSENT_ON_TARGET && this.insertion == null))) {
/* 473 */           insertion(insertion);
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 478 */       if (merges.contains(Style.Merge.FONT)) {
/* 479 */         Key font = that.font();
/* 480 */         if (font != null && (
/* 481 */           strategy == Style.Merge.Strategy.ALWAYS || (strategy == Style.Merge.Strategy.IF_ABSENT_ON_TARGET && this.font == null))) {
/* 482 */           font(font);
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 487 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public StyleImpl build() {
/* 492 */       if (isEmpty()) {
/* 493 */         return StyleImpl.EMPTY;
/*     */       }
/* 495 */       return new StyleImpl(this.font, this.color, this.shadowColor, this.decorations, this.clickEvent, this.hoverEvent, this.insertion);
/*     */     }
/*     */     
/*     */     private boolean isEmpty() {
/* 499 */       return (this.color == null && this.shadowColor == null && this.decorations == DecorationMap.EMPTY && this.clickEvent == null && this.hoverEvent == null && this.insertion == null && this.font == null);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\format\StyleImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */