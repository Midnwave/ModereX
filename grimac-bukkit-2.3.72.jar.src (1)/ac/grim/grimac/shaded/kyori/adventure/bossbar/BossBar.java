/*     */ package ac.grim.grimac.shaded.kyori.adventure.bossbar;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.OverrideOnly;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.audience.Audience;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.ComponentLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Index;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public interface BossBar
/*     */   extends Examinable
/*     */ {
/*     */   public static final float MIN_PROGRESS = 0.0F;
/*     */   public static final float MAX_PROGRESS = 1.0F;
/*     */   @Deprecated
/*     */   @ScheduledForRemoval(inVersion = "5.0.0")
/*     */   public static final float MIN_PERCENT = 0.0F;
/*     */   @Deprecated
/*     */   @ScheduledForRemoval(inVersion = "5.0.0")
/*     */   public static final float MAX_PERCENT = 1.0F;
/*     */   
/*     */   @NotNull
/*     */   static BossBar bossBar(@NotNull ComponentLike name, float progress, @NotNull Color color, @NotNull Overlay overlay) {
/* 101 */     BossBarImpl.checkProgress(progress);
/* 102 */     return bossBar(name.asComponent(), progress, color, overlay);
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
/*     */   static BossBar bossBar(@NotNull Component name, float progress, @NotNull Color color, @NotNull Overlay overlay) {
/* 117 */     BossBarImpl.checkProgress(progress);
/* 118 */     return new BossBarImpl(name, progress, color, overlay);
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
/*     */   @NotNull
/*     */   static BossBar bossBar(@NotNull ComponentLike name, float progress, @NotNull Color color, @NotNull Overlay overlay, @NotNull Set<Flag> flags) {
/* 134 */     BossBarImpl.checkProgress(progress);
/* 135 */     return bossBar(name.asComponent(), progress, color, overlay, flags);
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
/*     */   @NotNull
/*     */   static BossBar bossBar(@NotNull Component name, float progress, @NotNull Color color, @NotNull Overlay overlay, @NotNull Set<Flag> flags) {
/* 151 */     BossBarImpl.checkProgress(progress);
/* 152 */     return new BossBarImpl(name, progress, color, overlay, flags);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   Component name();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   default BossBar name(@NotNull ComponentLike name) {
/* 172 */     return name(name.asComponent());
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
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   BossBar name(@NotNull Component paramComponent);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   float progress();
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
/*     */   BossBar progress(float paramFloat);
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
/*     */   @ScheduledForRemoval(inVersion = "5.0.0")
/*     */   default float percent() {
/* 220 */     return progress();
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
/*     */   @Deprecated
/*     */   @ScheduledForRemoval(inVersion = "5.0.0")
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   default BossBar percent(float progress) {
/* 238 */     return progress(progress);
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
/*     */   Color color();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   BossBar color(@NotNull Color paramColor);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   Overlay overlay();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   BossBar overlay(@NotNull Overlay paramOverlay);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   Set<Flag> flags();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   BossBar flags(@NotNull Set<Flag> paramSet);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean hasFlag(@NotNull Flag paramFlag);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   BossBar addFlag(@NotNull Flag paramFlag);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   BossBar removeFlag(@NotNull Flag paramFlag);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   BossBar addFlags(@NotNull Flag... paramVarArgs);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   BossBar removeFlags(@NotNull Flag... paramVarArgs);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   BossBar addFlags(@NotNull Iterable<Flag> paramIterable);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   BossBar removeFlags(@NotNull Iterable<Flag> paramIterable);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   BossBar addListener(@NotNull Listener paramListener);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract("_ -> this")
/*     */   @NotNull
/*     */   BossBar removeListener(@NotNull Listener paramListener);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   Iterable<? extends BossBarViewer> viewers();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default BossBar addViewer(@NotNull Audience viewer) {
/* 404 */     viewer.showBossBar(this);
/* 405 */     return this;
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
/*     */   default BossBar removeViewer(@NotNull Audience viewer) {
/* 417 */     viewer.hideBossBar(this);
/* 418 */     return this;
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
/*     */   @OverrideOnly
/*     */   public static interface Listener
/*     */   {
/*     */     default void bossBarNameChanged(@NotNull BossBar bar, @NotNull Component oldName, @NotNull Component newName) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     default void bossBarProgressChanged(@NotNull BossBar bar, float oldProgress, float newProgress) {
/* 448 */       bossBarPercentChanged(bar, oldProgress, newProgress);
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
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     @ScheduledForRemoval(inVersion = "5.0.0")
/*     */     default void bossBarPercentChanged(@NotNull BossBar bar, float oldProgress, float newProgress) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     default void bossBarColorChanged(@NotNull BossBar bar, @NotNull BossBar.Color oldColor, @NotNull BossBar.Color newColor) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     default void bossBarOverlayChanged(@NotNull BossBar bar, @NotNull BossBar.Overlay oldOverlay, @NotNull BossBar.Overlay newOverlay) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     default void bossBarFlagsChanged(@NotNull BossBar bar, @NotNull Set<BossBar.Flag> flagsAdded, @NotNull Set<BossBar.Flag> flagsRemoved) {}
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
/*     */   public enum Color
/*     */   {
/* 516 */     PINK("pink"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 523 */     BLUE("blue"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 530 */     RED("red"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 537 */     GREEN("green"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 544 */     YELLOW("yellow"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 550 */     PURPLE("purple"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 557 */     WHITE("white");
/*     */     
/*     */     public static final Index<String, Color> NAMES;
/*     */     
/*     */     private final String name;
/*     */     
/*     */     static {
/* 564 */       NAMES = Index.create(Color.class, color -> color.name);
/*     */     }
/*     */     
/*     */     Color(String name) {
/* 568 */       this.name = name;
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
/*     */   public enum Flag
/*     */   {
/* 584 */     DARKEN_SCREEN("darken_screen"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 591 */     PLAY_BOSS_MUSIC("play_boss_music"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 598 */     CREATE_WORLD_FOG("create_world_fog");
/*     */ 
/*     */     
/*     */     public static final Index<String, Flag> NAMES;
/*     */     
/*     */     private final String name;
/*     */ 
/*     */     
/*     */     static {
/* 607 */       NAMES = Index.create(Flag.class, flag -> flag.name);
/*     */     }
/*     */     
/*     */     Flag(String name) {
/* 611 */       this.name = name;
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
/*     */   public enum Overlay
/*     */   {
/* 626 */     PROGRESS("progress"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 633 */     NOTCHED_6("notched_6"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 640 */     NOTCHED_10("notched_10"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 647 */     NOTCHED_12("notched_12"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 654 */     NOTCHED_20("notched_20");
/*     */     
/*     */     public static final Index<String, Overlay> NAMES;
/*     */     
/*     */     private final String name;
/*     */     
/*     */     static {
/* 661 */       NAMES = Index.create(Overlay.class, overlay -> overlay.name);
/*     */     }
/*     */     
/*     */     Overlay(String name) {
/* 665 */       this.name = name;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\bossbar\BossBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */