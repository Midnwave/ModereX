/*     */ package ac.grim.grimac.shaded.kyori.adventure.platform.bukkit;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.Facet;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.FacetAudience;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.FacetAudienceProvider;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.viaversion.ViaFacet;
/*     */ import com.viaversion.viaversion.api.connection.UserConnection;
/*     */ import java.util.Collection;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Supplier;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
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
/*     */ final class BukkitAudience
/*     */   extends FacetAudience<CommandSender>
/*     */ {
/*  42 */   static final ThreadLocal<Plugin> PLUGIN = new ThreadLocal<>();
/*  43 */   private static final Function<Player, UserConnection> VIA = new BukkitFacet.ViaHook();
/*  44 */   private static final Collection<Facet.Chat<? extends CommandSender, ?>> CHAT = Facet.of(new Supplier[] { () -> new ViaFacet.Chat(Player.class, VIA), () -> new CraftBukkitFacet.Chat1_19_3(), () -> new CraftBukkitFacet.Chat(), () -> new BukkitFacet.Chat() });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   private static final Collection<Facet.ActionBar<Player, ?>> ACTION_BAR = Facet.of(new Supplier[] { () -> new ViaFacet.ActionBarTitle(Player.class, VIA), () -> new ViaFacet.ActionBar(Player.class, VIA), () -> new CraftBukkitFacet.ActionBar_1_17(), () -> new CraftBukkitFacet.ActionBar(), () -> new CraftBukkitFacet.ActionBarLegacy() });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  58 */   private static final Collection<Facet.Title<Player, ?, ?, ?>> TITLE = Facet.of(new Supplier[] { () -> new ViaFacet.Title(Player.class, VIA), () -> new CraftBukkitFacet.Title_1_17(), () -> new CraftBukkitFacet.Title() });
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  63 */   private static final Collection<Facet.Sound<Player, Vector>> SOUND = Facet.of(new Supplier[] { () -> new BukkitFacet.SoundWithCategory(), () -> new BukkitFacet.Sound() });
/*     */ 
/*     */   
/*  66 */   private static final Collection<Facet.EntitySound<Player, Object>> ENTITY_SOUND = Facet.of(new Supplier[] { () -> new CraftBukkitFacet.EntitySound_1_19_3(), () -> new CraftBukkitFacet.EntitySound() });
/*     */ 
/*     */ 
/*     */   
/*  70 */   private static final Collection<Facet.Book<Player, ?, ?>> BOOK = Facet.of(new Supplier[] { () -> new CraftBukkitFacet.Book_1_20_5(), () -> new CraftBukkitFacet.BookPost1_13(), () -> new CraftBukkitFacet.Book1_13(), () -> new CraftBukkitFacet.BookPre1_13() });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  76 */   private static final Collection<Facet.BossBar.Builder<Player, ?>> BOSS_BAR = Facet.of(new Supplier[] { () -> new ViaFacet.BossBar.Builder(Player.class, VIA), () -> new ViaFacet.BossBar.Builder1_9_To_1_15(Player.class, VIA), () -> new CraftBukkitFacet.BossBar.Builder(), () -> new BukkitFacet.BossBarBuilder(), () -> new CraftBukkitFacet.BossBarWither.Builder() });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  82 */   private static final Collection<Facet.TabList<Player, ?>> TAB_LIST = Facet.of(new Supplier[] { () -> new ViaFacet.TabList(Player.class, VIA), () -> new PaperFacet.TabList(), () -> new CraftBukkitFacet.TabList(), () -> new BukkitFacet.TabList() });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  88 */   private static final Collection<Facet.Pointers<? extends CommandSender>> POINTERS = Facet.of(new Supplier[] { () -> new BukkitFacet.CommandSenderPointers(), () -> new BukkitFacet.ConsoleCommandSenderPointers(), () -> new BukkitFacet.PlayerPointers() });
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   private final Plugin plugin;
/*     */ 
/*     */ 
/*     */   
/*     */   BukkitAudience(@NotNull Plugin plugin, FacetAudienceProvider<?, ?> provider, @NotNull Collection<CommandSender> viewers) {
/*  97 */     super(provider, viewers, CHAT, ACTION_BAR, TITLE, SOUND, ENTITY_SOUND, BOOK, BOSS_BAR, TAB_LIST, POINTERS);
/*  98 */     this.plugin = plugin;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void showBossBar(@NotNull BossBar bar) {
/* 105 */     PLUGIN.set(this.plugin);
/*     */     
/* 107 */     super.showBossBar(bar);
/*     */ 
/*     */     
/* 110 */     PLUGIN.set(null);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\platform\bukkit\BukkitAudience.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */