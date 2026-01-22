/*     */ package ac.grim.grimac.shaded.kyori.adventure.bossbar;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Services;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumSet;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.BiPredicate;
/*     */ import java.util.function.Consumer;
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
/*     */ final class BossBarImpl
/*     */   extends HackyBossBarPlatformBridge
/*     */   implements BossBar
/*     */ {
/*  48 */   private final List<BossBar.Listener> listeners = new CopyOnWriteArrayList<>();
/*     */   private Component name;
/*     */   private float progress;
/*     */   private BossBar.Color color;
/*     */   private BossBar.Overlay overlay;
/*  53 */   private final Set<BossBar.Flag> flags = EnumSet.noneOf(BossBar.Flag.class);
/*     */   @Nullable
/*     */   BossBarImplementation implementation;
/*     */   
/*     */   @Internal
/*     */   static final class ImplementationAccessor {
/*  59 */     private static final Optional<BossBarImplementation.Provider> SERVICE = Services.service(BossBarImplementation.Provider.class);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     static <I extends BossBarImplementation> I get(@NotNull BossBar bar, @NotNull Class<I> type) {
/*  66 */       BossBarImplementation implementation = ((BossBarImpl)bar).implementation;
/*  67 */       if (implementation == null) {
/*  68 */         implementation = ((BossBarImplementation.Provider)SERVICE.get()).create(bar);
/*  69 */         ((BossBarImpl)bar).implementation = implementation;
/*     */       } 
/*  71 */       return type.cast(implementation);
/*     */     }
/*     */   }
/*     */   
/*     */   BossBarImpl(@NotNull Component name, float progress, @NotNull BossBar.Color color, @NotNull BossBar.Overlay overlay) {
/*  76 */     this.name = Objects.<Component>requireNonNull(name, "name");
/*  77 */     this.progress = progress;
/*  78 */     this.color = Objects.<BossBar.Color>requireNonNull(color, "color");
/*  79 */     this.overlay = Objects.<BossBar.Overlay>requireNonNull(overlay, "overlay");
/*     */   }
/*     */   
/*     */   BossBarImpl(@NotNull Component name, float progress, @NotNull BossBar.Color color, @NotNull BossBar.Overlay overlay, @NotNull Set<BossBar.Flag> flags) {
/*  83 */     this(name, progress, color, overlay);
/*  84 */     this.flags.addAll(flags);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Component name() {
/*  89 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public BossBar name(@NotNull Component newName) {
/*  96 */     Objects.requireNonNull(newName, "name");
/*  97 */     Component oldName = this.name;
/*  98 */     this.name = newName;
/*  99 */     forEachListener(listener -> listener.bossBarNameChanged(this, oldName, newName));
/* 100 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public float progress() {
/* 105 */     return this.progress;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BossBar progress(float newProgress) {
/* 110 */     checkProgress(newProgress);
/* 111 */     float oldProgress = this.progress;
/* 112 */     if (newProgress != oldProgress) {
/* 113 */       this.progress = newProgress;
/* 114 */       forEachListener(listener -> listener.bossBarProgressChanged(this, oldProgress, newProgress));
/*     */     } 
/* 116 */     return this;
/*     */   }
/*     */   
/*     */   static void checkProgress(float progress) {
/* 120 */     if (progress < 0.0F || progress > 1.0F) {
/* 121 */       throw new IllegalArgumentException("progress must be between 0.0 and 1.0, was " + progress);
/*     */     }
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BossBar.Color color() {
/* 127 */     return this.color;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BossBar color(@NotNull BossBar.Color newColor) {
/* 132 */     Objects.requireNonNull(newColor, "color");
/* 133 */     BossBar.Color oldColor = this.color;
/* 134 */     if (newColor != oldColor) {
/* 135 */       this.color = newColor;
/* 136 */       forEachListener(listener -> listener.bossBarColorChanged(this, oldColor, newColor));
/*     */     } 
/* 138 */     return this;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BossBar.Overlay overlay() {
/* 143 */     return this.overlay;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BossBar overlay(@NotNull BossBar.Overlay newOverlay) {
/* 148 */     Objects.requireNonNull(newOverlay, "overlay");
/* 149 */     BossBar.Overlay oldOverlay = this.overlay;
/* 150 */     if (newOverlay != oldOverlay) {
/* 151 */       this.overlay = newOverlay;
/* 152 */       forEachListener(listener -> listener.bossBarOverlayChanged(this, oldOverlay, newOverlay));
/*     */     } 
/* 154 */     return this;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Set<BossBar.Flag> flags() {
/* 159 */     return Collections.unmodifiableSet(this.flags);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BossBar flags(@NotNull Set<BossBar.Flag> newFlags) {
/* 164 */     if (newFlags.isEmpty() && !this.flags.isEmpty()) {
/* 165 */       Set<BossBar.Flag> oldFlags = EnumSet.copyOf(this.flags);
/* 166 */       this.flags.clear();
/* 167 */       forEachListener(listener -> listener.bossBarFlagsChanged(this, Collections.emptySet(), oldFlags));
/* 168 */     } else if (!this.flags.equals(newFlags)) {
/* 169 */       Set<BossBar.Flag> oldFlags = EnumSet.copyOf(this.flags);
/* 170 */       this.flags.clear();
/* 171 */       this.flags.addAll(newFlags);
/* 172 */       Set<BossBar.Flag> added = EnumSet.copyOf(newFlags);
/* 173 */       Objects.requireNonNull(oldFlags); added.removeIf(oldFlags::contains);
/* 174 */       Set<BossBar.Flag> removed = EnumSet.copyOf(oldFlags);
/* 175 */       Objects.requireNonNull(this.flags); removed.removeIf(this.flags::contains);
/* 176 */       forEachListener(listener -> listener.bossBarFlagsChanged(this, added, removed));
/*     */     } 
/* 178 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasFlag(@NotNull BossBar.Flag flag) {
/* 183 */     return this.flags.contains(flag);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BossBar addFlag(@NotNull BossBar.Flag flag) {
/* 188 */     return editFlags(flag, Set::add, BossBarImpl::onFlagsAdded);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BossBar removeFlag(@NotNull BossBar.Flag flag) {
/* 193 */     return editFlags(flag, Set::remove, BossBarImpl::onFlagsRemoved);
/*     */   }
/*     */   @NotNull
/*     */   private BossBar editFlags(@NotNull BossBar.Flag flag, @NotNull BiPredicate<Set<BossBar.Flag>, BossBar.Flag> predicate, BiConsumer<BossBarImpl, Set<BossBar.Flag>> onChange) {
/* 197 */     if (predicate.test(this.flags, flag)) {
/* 198 */       onChange.accept(this, Collections.singleton(flag));
/*     */     }
/* 200 */     return this;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BossBar addFlags(@NotNull BossBar.Flag... flags) {
/* 205 */     return editFlags(flags, Set::add, BossBarImpl::onFlagsAdded);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BossBar removeFlags(@NotNull BossBar.Flag... flags) {
/* 210 */     return editFlags(flags, Set::remove, BossBarImpl::onFlagsRemoved);
/*     */   }
/*     */   @NotNull
/*     */   private BossBar editFlags(BossBar.Flag[] flags, BiPredicate<Set<BossBar.Flag>, BossBar.Flag> predicate, BiConsumer<BossBarImpl, Set<BossBar.Flag>> onChange) {
/* 214 */     if (flags.length == 0) return this; 
/* 215 */     Set<BossBar.Flag> changes = null;
/* 216 */     for (int i = 0, length = flags.length; i < length; i++) {
/* 217 */       if (predicate.test(this.flags, flags[i])) {
/* 218 */         if (changes == null) {
/* 219 */           changes = EnumSet.noneOf(BossBar.Flag.class);
/*     */         }
/* 221 */         changes.add(flags[i]);
/*     */       } 
/*     */     } 
/* 224 */     if (changes != null) {
/* 225 */       onChange.accept(this, changes);
/*     */     }
/* 227 */     return this;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BossBar addFlags(@NotNull Iterable<BossBar.Flag> flags) {
/* 232 */     return editFlags(flags, Set::add, BossBarImpl::onFlagsAdded);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BossBar removeFlags(@NotNull Iterable<BossBar.Flag> flags) {
/* 237 */     return editFlags(flags, Set::remove, BossBarImpl::onFlagsRemoved);
/*     */   }
/*     */   @NotNull
/*     */   private BossBar editFlags(Iterable<BossBar.Flag> flags, BiPredicate<Set<BossBar.Flag>, BossBar.Flag> predicate, BiConsumer<BossBarImpl, Set<BossBar.Flag>> onChange) {
/* 241 */     Set<BossBar.Flag> changes = null;
/* 242 */     for (BossBar.Flag flag : flags) {
/* 243 */       if (predicate.test(this.flags, flag)) {
/* 244 */         if (changes == null) {
/* 245 */           changes = EnumSet.noneOf(BossBar.Flag.class);
/*     */         }
/* 247 */         changes.add(flag);
/*     */       } 
/*     */     } 
/* 250 */     if (changes != null) {
/* 251 */       onChange.accept(this, changes);
/*     */     }
/* 253 */     return this;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BossBar addListener(@NotNull BossBar.Listener listener) {
/* 258 */     this.listeners.add(listener);
/* 259 */     return this;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public BossBar removeListener(@NotNull BossBar.Listener listener) {
/* 264 */     this.listeners.remove(listener);
/* 265 */     return this;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Iterable<? extends BossBarViewer> viewers() {
/* 270 */     if (this.implementation != null) {
/* 271 */       return this.implementation.viewers();
/*     */     }
/* 273 */     return Collections.emptyList();
/*     */   }
/*     */   
/*     */   private void forEachListener(@NotNull Consumer<BossBar.Listener> consumer) {
/* 277 */     for (BossBar.Listener listener : this.listeners) {
/* 278 */       consumer.accept(listener);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void onFlagsAdded(BossBarImpl bar, Set<BossBar.Flag> flagsAdded) {
/* 283 */     bar.forEachListener(listener -> listener.bossBarFlagsChanged(bar, flagsAdded, Collections.emptySet()));
/*     */   }
/*     */   
/*     */   private static void onFlagsRemoved(BossBarImpl bar, Set<BossBar.Flag> flagsRemoved) {
/* 287 */     bar.forEachListener(listener -> listener.bossBarFlagsChanged(bar, Collections.emptySet(), flagsRemoved));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 292 */     return Stream.of(new ExaminableProperty[] {
/* 293 */           ExaminableProperty.of("name", this.name), 
/* 294 */           ExaminableProperty.of("progress", this.progress), 
/* 295 */           ExaminableProperty.of("color", this.color), 
/* 296 */           ExaminableProperty.of("overlay", this.overlay), 
/* 297 */           ExaminableProperty.of("flags", this.flags)
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 303 */     return Internals.toString(this);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\bossbar\BossBarImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */