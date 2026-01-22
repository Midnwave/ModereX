/*     */ package ac.grim.grimac.shaded.kyori.adventure.platform.bukkit;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.identity.Identity;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.permission.PermissionChecker;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.Facet;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.FacetBase;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.FacetPointers;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.Knob;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.pointer.Pointers;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.sound.SoundStop;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.translation.Translator;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.TriState;
/*     */ import com.viaversion.viaversion.api.Via;
/*     */ import com.viaversion.viaversion.api.connection.UserConnection;
/*     */ import java.lang.invoke.MethodHandle;
/*     */ import java.util.Collection;
/*     */ import java.util.Locale;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.function.Function;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.SoundCategory;
/*     */ import org.bukkit.boss.BarColor;
/*     */ import org.bukkit.boss.BarFlag;
/*     */ import org.bukkit.boss.BarStyle;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.command.ConsoleCommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.util.Vector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class BukkitFacet<V extends CommandSender>
/*     */   extends FacetBase<V>
/*     */ {
/*     */   protected BukkitFacet(@Nullable Class<? extends V> viewerClass) {
/*  66 */     super(viewerClass);
/*     */   }
/*     */   
/*     */   static class Message<V extends CommandSender> extends BukkitFacet<V> implements Facet.Message<V, String> {
/*     */     protected Message(@Nullable Class<? extends V> viewerClass) {
/*  71 */       super(viewerClass);
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public String createMessage(@NotNull V viewer, @NotNull Component message) {
/*  76 */       return BukkitComponentSerializer.legacy().serialize(message);
/*     */     }
/*     */   }
/*     */   
/*     */   static class Chat extends Message<CommandSender> implements Facet.Chat<CommandSender, String> {
/*     */     protected Chat() {
/*  82 */       super(CommandSender.class);
/*     */     }
/*     */ 
/*     */     
/*     */     public void sendMessage(@NotNull CommandSender viewer, @NotNull Identity source, @NotNull String message, @NotNull Object type) {
/*  87 */       viewer.sendMessage(message);
/*     */     }
/*     */   }
/*     */   
/*     */   static class Position extends BukkitFacet<Player> implements Facet.Position<Player, Vector> {
/*     */     protected Position() {
/*  93 */       super(Player.class);
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Vector createPosition(@NotNull Player viewer) {
/*  98 */       return viewer.getLocation().toVector();
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Vector createPosition(double x, double y, double z) {
/* 103 */       return new Vector(x, y, z);
/*     */     }
/*     */   }
/*     */   
/*     */   static class Sound extends Position implements Facet.Sound<Player, Vector> {
/* 108 */     private static final boolean KEY_SUPPORTED = MinecraftReflection.hasClass(new String[] { "org.bukkit.NamespacedKey" });
/* 109 */     private static final boolean STOP_SUPPORTED = MinecraftReflection.hasMethod(Player.class, "stopSound", new Class[] { String.class });
/* 110 */     private static final MethodHandle STOP_ALL_SUPPORTED = MinecraftReflection.findMethod(Player.class, "stopAllSounds", void.class, new Class[0]);
/*     */ 
/*     */     
/*     */     public void playSound(@NotNull Player viewer, ac.grim.grimac.shaded.kyori.adventure.sound.Sound sound, @NotNull Vector vector) {
/* 114 */       String name = name(sound.name());
/* 115 */       Location location = vector.toLocation(viewer.getWorld());
/*     */       
/* 117 */       viewer.playSound(location, name, sound.volume(), sound.pitch());
/*     */     }
/*     */ 
/*     */     
/*     */     public void stopSound(@NotNull Player viewer, @NotNull SoundStop stop) {
/* 122 */       if (STOP_SUPPORTED) {
/* 123 */         String name = name(stop.sound());
/* 124 */         if (name.isEmpty() && STOP_ALL_SUPPORTED != null) {
/*     */           try {
/* 126 */             STOP_ALL_SUPPORTED.invoke(viewer);
/* 127 */           } catch (Throwable error) {
/* 128 */             Knob.logError(error, "Could not invoke stopAllSounds on %s", new Object[] { viewer });
/*     */           } 
/*     */           return;
/*     */         } 
/* 132 */         viewer.stopSound(name);
/*     */       } 
/*     */     }
/*     */     @NotNull
/*     */     protected static String name(@Nullable Key name) {
/* 137 */       if (name == null) {
/* 138 */         return "";
/*     */       }
/* 140 */       if (KEY_SUPPORTED) {
/* 141 */         return name.asString();
/*     */       }
/* 143 */       return name.value();
/*     */     }
/*     */   }
/*     */   
/*     */   static class SoundWithCategory
/*     */     extends Sound {
/* 149 */     private static final boolean SUPPORTED = MinecraftReflection.hasMethod(Player.class, "stopSound", new Class[] { String.class, MinecraftReflection.findClass(new String[] { "org.bukkit.SoundCategory" }) });
/*     */ 
/*     */     
/*     */     public boolean isSupported() {
/* 153 */       return (super.isSupported() && SUPPORTED);
/*     */     }
/*     */ 
/*     */     
/*     */     public void playSound(@NotNull Player viewer, ac.grim.grimac.shaded.kyori.adventure.sound.Sound sound, @NotNull Vector vector) {
/* 158 */       SoundCategory category = category(sound.source());
/* 159 */       if (category == null) {
/* 160 */         super.playSound(viewer, sound, vector);
/*     */       } else {
/* 162 */         String name = name(sound.name());
/* 163 */         viewer.playSound(vector.toLocation(viewer.getWorld()), name, category, sound.volume(), sound.pitch());
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void stopSound(@NotNull Player viewer, @NotNull SoundStop stop) {
/* 169 */       SoundCategory category = category(stop.source());
/* 170 */       if (category == null) {
/* 171 */         super.stopSound(viewer, stop);
/*     */       } else {
/* 173 */         String name = name(stop.sound());
/* 174 */         viewer.stopSound(name, category);
/*     */       } 
/*     */     }
/*     */     @Nullable
/*     */     private SoundCategory category(ac.grim.grimac.shaded.kyori.adventure.sound.Sound.Source source) {
/* 179 */       if (source == null) {
/* 180 */         return null;
/*     */       }
/* 182 */       if (source == ac.grim.grimac.shaded.kyori.adventure.sound.Sound.Source.MASTER)
/* 183 */         return SoundCategory.MASTER; 
/* 184 */       if (source == ac.grim.grimac.shaded.kyori.adventure.sound.Sound.Source.MUSIC)
/* 185 */         return SoundCategory.MUSIC; 
/* 186 */       if (source == ac.grim.grimac.shaded.kyori.adventure.sound.Sound.Source.RECORD)
/* 187 */         return SoundCategory.RECORDS; 
/* 188 */       if (source == ac.grim.grimac.shaded.kyori.adventure.sound.Sound.Source.WEATHER)
/* 189 */         return SoundCategory.WEATHER; 
/* 190 */       if (source == ac.grim.grimac.shaded.kyori.adventure.sound.Sound.Source.BLOCK)
/* 191 */         return SoundCategory.BLOCKS; 
/* 192 */       if (source == ac.grim.grimac.shaded.kyori.adventure.sound.Sound.Source.HOSTILE)
/* 193 */         return SoundCategory.HOSTILE; 
/* 194 */       if (source == ac.grim.grimac.shaded.kyori.adventure.sound.Sound.Source.NEUTRAL)
/* 195 */         return SoundCategory.NEUTRAL; 
/* 196 */       if (source == ac.grim.grimac.shaded.kyori.adventure.sound.Sound.Source.PLAYER)
/* 197 */         return SoundCategory.PLAYERS; 
/* 198 */       if (source == ac.grim.grimac.shaded.kyori.adventure.sound.Sound.Source.AMBIENT)
/* 199 */         return SoundCategory.AMBIENT; 
/* 200 */       if (source == ac.grim.grimac.shaded.kyori.adventure.sound.Sound.Source.VOICE) {
/* 201 */         return SoundCategory.VOICE;
/*     */       }
/* 203 */       Knob.logUnsupported(this, source);
/* 204 */       return null;
/*     */     }
/*     */   }
/*     */   
/*     */   static class BossBarBuilder extends BukkitFacet<Player> implements Facet.BossBar.Builder<Player, BossBar> {
/* 209 */     private static final boolean SUPPORTED = MinecraftReflection.hasClass(new String[] { "org.bukkit.boss.BossBar" });
/*     */     
/*     */     protected BossBarBuilder() {
/* 212 */       super(Player.class);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isSupported() {
/* 217 */       return (super.isSupported() && SUPPORTED);
/*     */     }
/*     */ 
/*     */     
/*     */     public BukkitFacet.BossBar createBossBar(@NotNull Collection<Player> viewers) {
/* 222 */       return new BukkitFacet.BossBar(viewers);
/*     */     }
/*     */   }
/*     */   
/*     */   static class BossBar extends Message<Player> implements Facet.BossBar<Player> {
/*     */     protected final org.bukkit.boss.BossBar bar;
/*     */     
/*     */     protected BossBar(@NotNull Collection<Player> viewers) {
/* 230 */       super(Player.class);
/* 231 */       this.bar = Bukkit.createBossBar("", BarColor.PINK, BarStyle.SOLID, new BarFlag[0]);
/* 232 */       this.bar.setVisible(false);
/* 233 */       for (Player viewer : viewers) {
/* 234 */         this.bar.addPlayer(viewer);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void bossBarInitialized(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar bar) {
/* 240 */       super.bossBarInitialized(bar);
/* 241 */       this.bar.setVisible(true);
/*     */     }
/*     */ 
/*     */     
/*     */     public void bossBarNameChanged(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar bar, @NotNull Component oldName, @NotNull Component newName) {
/* 246 */       if (!this.bar.getPlayers().isEmpty()) {
/* 247 */         this.bar.setTitle(createMessage(this.bar.getPlayers().get(0), newName));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void bossBarProgressChanged(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar bar, float oldPercent, float newPercent) {
/* 253 */       this.bar.setProgress(newPercent);
/*     */     }
/*     */ 
/*     */     
/*     */     public void bossBarColorChanged(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar bar, ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Color oldColor, ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Color newColor) {
/* 258 */       BarColor color = color(newColor);
/* 259 */       if (color != null)
/* 260 */         this.bar.setColor(color); 
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     private BarColor color(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Color color) {
/* 265 */       if (color == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Color.PINK)
/* 266 */         return BarColor.PINK; 
/* 267 */       if (color == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Color.BLUE)
/* 268 */         return BarColor.BLUE; 
/* 269 */       if (color == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Color.RED)
/* 270 */         return BarColor.RED; 
/* 271 */       if (color == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Color.GREEN)
/* 272 */         return BarColor.GREEN; 
/* 273 */       if (color == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Color.YELLOW)
/* 274 */         return BarColor.YELLOW; 
/* 275 */       if (color == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Color.PURPLE)
/* 276 */         return BarColor.PURPLE; 
/* 277 */       if (color == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Color.WHITE) {
/* 278 */         return BarColor.WHITE;
/*     */       }
/* 280 */       Knob.logUnsupported(this, color);
/* 281 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public void bossBarOverlayChanged(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar bar, ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Overlay oldOverlay, ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Overlay newOverlay) {
/* 286 */       BarStyle style = style(newOverlay);
/* 287 */       if (style != null)
/* 288 */         this.bar.setStyle(style); 
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     private BarStyle style(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Overlay overlay) {
/* 293 */       if (overlay == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Overlay.PROGRESS)
/* 294 */         return BarStyle.SOLID; 
/* 295 */       if (overlay == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Overlay.NOTCHED_6)
/* 296 */         return BarStyle.SEGMENTED_6; 
/* 297 */       if (overlay == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Overlay.NOTCHED_10)
/* 298 */         return BarStyle.SEGMENTED_10; 
/* 299 */       if (overlay == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Overlay.NOTCHED_12)
/* 300 */         return BarStyle.SEGMENTED_12; 
/* 301 */       if (overlay == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Overlay.NOTCHED_20) {
/* 302 */         return BarStyle.SEGMENTED_20;
/*     */       }
/* 304 */       Knob.logUnsupported(this, overlay);
/* 305 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public void bossBarFlagsChanged(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar bar, @NotNull Set<ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Flag> flagsAdded, @NotNull Set<ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Flag> flagsRemoved) {
/* 310 */       for (ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Flag removeFlag : flagsRemoved) {
/* 311 */         BarFlag flag = flag(removeFlag);
/* 312 */         if (flag != null) {
/* 313 */           this.bar.removeFlag(flag);
/*     */         }
/*     */       } 
/* 316 */       for (ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Flag addFlag : flagsAdded) {
/* 317 */         BarFlag flag = flag(addFlag);
/* 318 */         if (flag != null)
/* 319 */           this.bar.addFlag(flag); 
/*     */       } 
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     private BarFlag flag(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Flag flag) {
/* 325 */       if (flag == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Flag.DARKEN_SCREEN)
/* 326 */         return BarFlag.DARKEN_SKY; 
/* 327 */       if (flag == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Flag.PLAY_BOSS_MUSIC)
/* 328 */         return BarFlag.PLAY_BOSS_MUSIC; 
/* 329 */       if (flag == ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Flag.CREATE_WORLD_FOG) {
/* 330 */         return BarFlag.CREATE_FOG;
/*     */       }
/* 332 */       Knob.logUnsupported(this, flag);
/* 333 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public void addViewer(@NotNull Player viewer) {
/* 338 */       this.bar.addPlayer(viewer);
/*     */     }
/*     */ 
/*     */     
/*     */     public void removeViewer(@NotNull Player viewer) {
/* 343 */       this.bar.removePlayer(viewer);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty() {
/* 348 */       return (!this.bar.isVisible() || this.bar.getPlayers().isEmpty());
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() {
/* 353 */       this.bar.removeAll();
/*     */     }
/*     */   }
/*     */   
/*     */   static final class ViaHook
/*     */     implements Function<Player, UserConnection> {
/*     */     public UserConnection apply(@NotNull Player player) {
/* 360 */       return Via.getManager().getConnectionManager().getConnectedClient(player.getUniqueId());
/*     */     }
/*     */   }
/*     */   
/*     */   static final class TabList
/*     */     extends Message<Player> implements Facet.TabList<Player, String> {
/* 366 */     private static final boolean SUPPORTED = MinecraftReflection.hasMethod(Player.class, "setPlayerListHeader", new Class[] { String.class });
/*     */     
/*     */     TabList() {
/* 369 */       super(Player.class);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isSupported() {
/* 374 */       return (SUPPORTED && super.isSupported());
/*     */     }
/*     */ 
/*     */     
/*     */     public void send(Player viewer, @Nullable String header, @Nullable String footer) {
/* 379 */       if (header != null && footer != null) {
/* 380 */         viewer.setPlayerListHeaderFooter(header, footer);
/* 381 */       } else if (header != null) {
/* 382 */         viewer.setPlayerListHeader(header);
/* 383 */       } else if (footer != null) {
/* 384 */         viewer.setPlayerListFooter(footer);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   static final class CommandSenderPointers
/*     */     extends BukkitFacet<CommandSender> implements Facet.Pointers<CommandSender> {
/*     */     CommandSenderPointers() {
/* 392 */       super(CommandSender.class);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void contributePointers(CommandSender viewer, Pointers.Builder builder) {
/* 398 */       Objects.requireNonNull(viewer); builder.withDynamic(Identity.NAME, viewer::getName);
/*     */       
/* 400 */       builder.withStatic(PermissionChecker.POINTER, perm -> viewer.isPermissionSet(perm) ? (viewer.hasPermission(perm) ? TriState.TRUE : TriState.FALSE) : TriState.NOT_SET);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final class ConsoleCommandSenderPointers
/*     */     extends BukkitFacet<ConsoleCommandSender>
/*     */     implements Facet.Pointers<ConsoleCommandSender>
/*     */   {
/*     */     ConsoleCommandSenderPointers() {
/* 412 */       super(ConsoleCommandSender.class);
/*     */     }
/*     */ 
/*     */     
/*     */     public void contributePointers(ConsoleCommandSender viewer, Pointers.Builder builder) {
/* 417 */       builder.withStatic(FacetPointers.TYPE, FacetPointers.Type.CONSOLE);
/*     */     }
/*     */   }
/*     */   
/*     */   static final class PlayerPointers
/*     */     extends BukkitFacet<Player> implements Facet.Pointers<Player> {
/*     */     private static final MethodHandle LOCALE_SUPPORTED;
/*     */     
/*     */     static {
/* 426 */       MethodHandle asLocale = MinecraftReflection.findMethod(Player.class, "getLocale", Locale.class, new Class[0]);
/* 427 */       MethodHandle asString = MinecraftReflection.findMethod(Player.class, "getLocale", String.class, new Class[0]);
/* 428 */       LOCALE_SUPPORTED = (asLocale != null) ? asLocale : asString;
/*     */     }
/*     */     
/*     */     PlayerPointers() {
/* 432 */       super(Player.class);
/*     */     }
/*     */ 
/*     */     
/*     */     public void contributePointers(Player viewer, Pointers.Builder builder) {
/* 437 */       Objects.requireNonNull(viewer); builder.withDynamic(Identity.UUID, viewer::getUniqueId);
/* 438 */       builder.withDynamic(Identity.DISPLAY_NAME, () -> BukkitComponentSerializer.legacy().deserializeOrNull(viewer.getDisplayName()));
/* 439 */       builder.withDynamic(Identity.LOCALE, () -> {
/*     */             if (LOCALE_SUPPORTED != null) {
/*     */               try {
/*     */                 Object result = LOCALE_SUPPORTED.invoke(viewer);
/*     */                 return (result instanceof Locale) ? (Locale)result : Translator.parseLocale((String)result);
/* 444 */               } catch (Throwable error) {
/*     */                 Knob.logError(error, "Failed to call getLocale() for %s", new Object[] { viewer });
/*     */               } 
/*     */             }
/*     */             return Locale.getDefault();
/*     */           });
/* 450 */       builder.withStatic(FacetPointers.TYPE, FacetPointers.Type.PLAYER);
/* 451 */       builder.withDynamic(FacetPointers.WORLD, () -> Key.key(viewer.getWorld().getName()));
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\platform\bukkit\BukkitFacet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */