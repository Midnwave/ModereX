/*     */ package ac.grim.grimac.shaded.kyori.adventure.platform.facet;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.audience.MessageType;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.identity.Identity;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.sound.SoundStop;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import java.io.Closeable;
/*     */ import java.time.Duration;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.function.Supplier;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Internal
/*     */ public interface Facet<V>
/*     */ {
/*     */   @SafeVarargs
/*     */   @NotNull
/*     */   static <V, F extends Facet<? extends V>> Collection<F> of(@NotNull Supplier<F>... suppliers) {
/*  67 */     List<F> facets = new LinkedList<>();
/*  68 */     for (Supplier<F> supplier : suppliers) {
/*     */       Facet facet;
/*     */       try {
/*  71 */         facet = (Facet)supplier.get();
/*  72 */       } catch (NoClassDefFoundError error) {
/*  73 */         Knob.logMessage("Skipped facet: %s", new Object[] { supplier.getClass().getName() });
/*     */       }
/*  75 */       catch (Throwable error) {
/*  76 */         Knob.logError(error, "Failed facet: %s", new Object[] { supplier });
/*     */       } 
/*     */       
/*  79 */       if (!facet.isSupported()) {
/*  80 */         Knob.logMessage("Skipped facet: %s", new Object[] { facet });
/*     */       } else {
/*     */         
/*  83 */         facets.add((F)facet);
/*  84 */         Knob.logMessage("Added facet: %s", new Object[] { facet });
/*     */       } 
/*  86 */     }  return facets;
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
/*     */   @Nullable
/*     */   static <V, F extends Facet<V>> F of(@Nullable Collection<F> facets, @Nullable V viewer) {
/* 100 */     if (facets == null || viewer == null) return null; 
/* 101 */     for (Facet<V> facet : facets) {
/*     */       try {
/* 103 */         if (facet.isApplicable(viewer)) {
/* 104 */           Knob.logMessage("Selected facet: %s for %s", new Object[] { facet, viewer });
/* 105 */           return (F)facet;
/* 106 */         }  if (Knob.DEBUG) {
/* 107 */           Knob.logMessage("Not selecting %s for %s", new Object[] { facet, viewer });
/*     */         }
/* 109 */       } catch (ClassCastException error) {
/* 110 */         if (Knob.DEBUG) {
/* 111 */           Knob.logMessage("Exception while getting facet %s for %s: %s", new Object[] { facet, viewer, error.getMessage() });
/*     */         }
/*     */       } 
/*     */     } 
/* 115 */     return null;
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
/*     */   default boolean isSupported() {
/* 127 */     return true;
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
/*     */   default boolean isApplicable(@NotNull V viewer) {
/* 140 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface Message<V, M>
/*     */     extends Facet<V>
/*     */   {
/*     */     public static final int PROTOCOL_HEX_COLOR = 713;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final int PROTOCOL_JSON = 5;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     M createMessage(@NotNull V param1V, @NotNull Component param1Component);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface Chat<V, M>
/*     */     extends Message<V, M>
/*     */   {
/*     */     void sendMessage(@NotNull V param1V, @NotNull Identity param1Identity, @NotNull M param1M, @NotNull Object param1Object);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface ChatPacket<V, M>
/*     */     extends Chat<V, M>
/*     */   {
/*     */     public static final byte TYPE_CHAT = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final byte TYPE_SYSTEM = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final byte TYPE_ACTION_BAR = 2;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     default byte createMessageType(@NotNull MessageType type) {
/* 207 */       if (type == MessageType.CHAT)
/* 208 */         return 0; 
/* 209 */       if (type == MessageType.SYSTEM) {
/* 210 */         return 1;
/*     */       }
/* 212 */       Knob.logUnsupported(this, type);
/* 213 */       return 0;
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
/*     */   public static interface ActionBar<V, M>
/*     */     extends Message<V, M>
/*     */   {
/*     */     void sendMessage(@NotNull V param1V, @NotNull M param1M);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface Title<V, M, C, T>
/*     */     extends Message<V, M>
/*     */   {
/*     */     public static final int PROTOCOL_ACTION_BAR = 310;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final long MAX_SECONDS = 461168601842738790L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     C createTitleCollection();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void contributeTitle(@NotNull C param1C, @NotNull M param1M);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void contributeSubtitle(@NotNull C param1C, @NotNull M param1M);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void contributeTimes(@NotNull C param1C, int param1Int1, int param1Int2, int param1Int3);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     T completeTitle(@NotNull C param1C);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void showTitle(@NotNull V param1V, @NotNull T param1T);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void clearTitle(@NotNull V param1V);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void resetTitle(@NotNull V param1V);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     default int toTicks(@Nullable Duration duration) {
/* 333 */       if (duration == null || duration.isNegative()) {
/* 334 */         return -1;
/*     */       }
/*     */       
/* 337 */       if (duration.getSeconds() > 461168601842738790L)
/*     */       {
/* 339 */         return Integer.MAX_VALUE;
/*     */       }
/*     */       
/* 342 */       return 
/* 343 */         (int)(duration.getSeconds() * 20L + (duration.getNano() / 50000000));
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
/*     */   public static interface TitlePacket<V, M, C, T>
/*     */     extends Title<V, M, C, T>
/*     */   {
/*     */     public static final int ACTION_TITLE = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final int ACTION_SUBTITLE = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final int ACTION_ACTIONBAR = 2;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final int ACTION_TIMES = 3;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final int ACTION_CLEAR = 4;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final int ACTION_RESET = 5;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface Position<V, P>
/*     */     extends Facet<V>
/*     */   {
/*     */     @Nullable
/*     */     P createPosition(@NotNull V param1V);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     P createPosition(double param1Double1, double param1Double2, double param1Double3);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface Sound<V, P>
/*     */     extends Position<V, P>
/*     */   {
/*     */     void playSound(@NotNull V param1V, ac.grim.grimac.shaded.kyori.adventure.sound.Sound param1Sound, @NotNull P param1P);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void stopSound(@NotNull V param1V, @NotNull SoundStop param1SoundStop);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface EntitySound<V, M>
/*     */     extends Facet<V>
/*     */   {
/*     */     M createForSelf(V param1V, ac.grim.grimac.shaded.kyori.adventure.sound.Sound param1Sound);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     M createForEmitter(ac.grim.grimac.shaded.kyori.adventure.sound.Sound param1Sound, ac.grim.grimac.shaded.kyori.adventure.sound.Sound.Emitter param1Emitter);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void playSound(@NotNull V param1V, M param1M);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface Book<V, M, B>
/*     */     extends Message<V, M>
/*     */   {
/*     */     @Nullable
/*     */     B createBook(@NotNull String param1String1, @NotNull String param1String2, @NotNull Iterable<M> param1Iterable);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void openBook(@NotNull V param1V, @NotNull B param1B);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface BossBar<V>
/*     */     extends ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Listener, Closeable
/*     */   {
/*     */     public static final int PROTOCOL_BOSS_BAR = 356;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     default void bossBarInitialized(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar bar) {
/* 525 */       bossBarNameChanged(bar, bar.name(), bar.name());
/* 526 */       bossBarColorChanged(bar, bar.color(), bar.color());
/* 527 */       bossBarProgressChanged(bar, bar.progress(), bar.progress());
/* 528 */       bossBarFlagsChanged(bar, bar.flags(), Collections.emptySet());
/* 529 */       bossBarOverlayChanged(bar, bar.overlay(), bar.overlay());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void addViewer(@NotNull V param1V);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void removeViewer(@NotNull V param1V);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     boolean isEmpty();
/*     */ 
/*     */ 
/*     */     
/*     */     void close();
/*     */ 
/*     */ 
/*     */     
/*     */     @FunctionalInterface
/*     */     public static interface Builder<V, B extends BossBar<V>>
/*     */       extends Facet<V>
/*     */     {
/*     */       @NotNull
/*     */       B createBossBar(@NotNull Collection<V> param2Collection);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface BossBarPacket<V>
/*     */     extends BossBar<V>
/*     */   {
/*     */     public static final int ACTION_ADD = 0;
/*     */ 
/*     */     
/*     */     public static final int ACTION_REMOVE = 1;
/*     */ 
/*     */     
/*     */     public static final int ACTION_HEALTH = 2;
/*     */ 
/*     */     
/*     */     public static final int ACTION_TITLE = 3;
/*     */ 
/*     */     
/*     */     public static final int ACTION_STYLE = 4;
/*     */ 
/*     */     
/*     */     public static final int ACTION_FLAG = 5;
/*     */ 
/*     */ 
/*     */     
/*     */     default int createColor(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Color color) {
/* 587 */       if (color == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Color.PURPLE)
/* 588 */         return 5; 
/* 589 */       if (color == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Color.PINK)
/* 590 */         return 0; 
/* 591 */       if (color == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Color.BLUE)
/* 592 */         return 1; 
/* 593 */       if (color == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Color.RED)
/* 594 */         return 2; 
/* 595 */       if (color == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Color.GREEN)
/* 596 */         return 3; 
/* 597 */       if (color == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Color.YELLOW)
/* 598 */         return 4; 
/* 599 */       if (color == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Color.WHITE) {
/* 600 */         return 6;
/*     */       }
/* 602 */       Knob.logUnsupported(this, color);
/* 603 */       return 5;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     default int createOverlay(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Overlay overlay) {
/* 614 */       if (overlay == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Overlay.PROGRESS)
/* 615 */         return 0; 
/* 616 */       if (overlay == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Overlay.NOTCHED_6)
/* 617 */         return 1; 
/* 618 */       if (overlay == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Overlay.NOTCHED_10)
/* 619 */         return 2; 
/* 620 */       if (overlay == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Overlay.NOTCHED_12)
/* 621 */         return 3; 
/* 622 */       if (overlay == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Overlay.NOTCHED_20) {
/* 623 */         return 4;
/*     */       }
/* 625 */       Knob.logUnsupported(this, overlay);
/* 626 */       return 0;
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
/*     */     default byte createFlag(byte flagBit, @NotNull Set<ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Flag> flagsAdded, @NotNull Set<ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Flag> flagsRemoved) {
/* 639 */       byte bit = flagBit;
/* 640 */       for (ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Flag flag : flagsAdded) {
/* 641 */         if (flag == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Flag.DARKEN_SCREEN) {
/* 642 */           bit = (byte)(bit | 0x1); continue;
/* 643 */         }  if (flag == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Flag.PLAY_BOSS_MUSIC) {
/* 644 */           bit = (byte)(bit | 0x2); continue;
/* 645 */         }  if (flag == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Flag.CREATE_WORLD_FOG) {
/* 646 */           bit = (byte)(bit | 0x4); continue;
/*     */         } 
/* 648 */         Knob.logUnsupported(this, flag);
/*     */       } 
/*     */       
/* 651 */       for (ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Flag flag : flagsRemoved) {
/* 652 */         if (flag == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Flag.DARKEN_SCREEN) {
/* 653 */           bit = (byte)(bit & 0xFFFFFFFE); continue;
/* 654 */         }  if (flag == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Flag.PLAY_BOSS_MUSIC) {
/* 655 */           bit = (byte)(bit & 0xFFFFFFFD); continue;
/* 656 */         }  if (flag == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Flag.CREATE_WORLD_FOG) {
/* 657 */           bit = (byte)(bit & 0xFFFFFFFB); continue;
/*     */         } 
/* 659 */         Knob.logUnsupported(this, flag);
/*     */       } 
/*     */       
/* 662 */       return bit;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static interface BossBarEntity<V, P>
/*     */     extends BossBar<V>, FakeEntity<V, P>
/*     */   {
/*     */     public static final int OFFSET_PITCH = 30;
/*     */     
/*     */     public static final int OFFSET_YAW = 0;
/*     */     
/*     */     public static final int OFFSET_MAGNITUDE = 40;
/*     */     
/*     */     public static final int INVULNERABLE_KEY = 20;
/*     */     
/*     */     public static final int INVULNERABLE_TICKS = 890;
/*     */ 
/*     */     
/*     */     default void bossBarProgressChanged(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar bar, float oldProgress, float newProgress) {
/* 682 */       health(newProgress);
/*     */     }
/*     */ 
/*     */     
/*     */     default void bossBarNameChanged(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar bar, @NotNull Component oldName, @NotNull Component newName) {
/* 687 */       name(newName);
/*     */     }
/*     */ 
/*     */     
/*     */     default void addViewer(@NotNull V viewer) {
/* 692 */       teleport(viewer, createPosition(viewer));
/*     */     }
/*     */ 
/*     */     
/*     */     default void removeViewer(@NotNull V viewer) {
/* 697 */       teleport(viewer, null);
/*     */     }
/*     */   }
/*     */   
/*     */   public static interface FakeEntity<V, P> extends Position<V, P>, Closeable {
/*     */     void teleport(@NotNull V param1V, @Nullable P param1P);
/*     */     
/*     */     void metadata(int param1Int, @NotNull Object param1Object);
/*     */     
/*     */     void invisible(boolean param1Boolean);
/*     */     
/*     */     void health(float param1Float);
/*     */     
/*     */     void name(@NotNull Component param1Component);
/*     */     
/*     */     void close();
/*     */   }
/*     */   
/*     */   public static interface TabList<V, M> extends Message<V, M> {
/*     */     void send(V param1V, @Nullable M param1M1, @Nullable M param1M2);
/*     */   }
/*     */   
/*     */   public static interface Pointers<V> extends Facet<V> {
/*     */     void contributePointers(V param1V, ac.grim.grimac.shaded.kyori.adventure.pointer.Pointers.Builder param1Builder);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\platform\facet\Facet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */