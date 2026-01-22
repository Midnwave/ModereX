/*     */ package ac.grim.grimac.shaded.kyori.adventure.platform.bukkit;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.audience.Audience;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.identity.Identity;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.AudienceProvider;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.FacetAudience;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.FacetAudienceProvider;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.Knob;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.pointer.Pointered;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.flattener.ComponentFlattener;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.renderer.ComponentRenderer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.translation.GlobalTranslator;
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import com.google.common.graph.MutableGraph;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Function;
/*     */ import java.util.logging.Level;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.command.ConsoleCommandSender;
/*     */ import org.bukkit.command.ProxiedCommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.Event;
/*     */ import org.bukkit.event.EventException;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.player.PlayerJoinEvent;
/*     */ import org.bukkit.event.player.PlayerQuitEvent;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class BukkitAudiencesImpl
/*     */   extends FacetAudienceProvider<CommandSender, BukkitAudience>
/*     */   implements BukkitAudiences, Listener
/*     */ {
/*     */   static {
/*  70 */     Knob.OUT = (message -> Bukkit.getLogger().log(Level.INFO, message));
/*  71 */     Knob.ERR = ((message, error) -> Bukkit.getLogger().log(Level.WARNING, message, error));
/*     */   }
/*     */   
/*  74 */   private static final Map<String, BukkitAudiences> INSTANCES = Collections.synchronizedMap(new HashMap<>(4));
/*     */   
/*     */   static Builder builder(@NotNull Plugin plugin) {
/*  77 */     return new Builder(plugin);
/*     */   }
/*     */   private final Plugin plugin;
/*     */   static BukkitAudiences instanceFor(@NotNull Plugin plugin) {
/*  81 */     return builder(plugin).build();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   BukkitAudiencesImpl(@NotNull Plugin plugin, @NotNull ComponentRenderer<Pointered> componentRenderer) {
/*  87 */     super(componentRenderer);
/*  88 */     this.plugin = Objects.<Plugin>requireNonNull(plugin, "plugin");
/*     */     
/*  90 */     ConsoleCommandSender consoleCommandSender = this.plugin.getServer().getConsoleSender();
/*  91 */     addViewer(consoleCommandSender);
/*     */     
/*  93 */     for (Player player : this.plugin.getServer().getOnlinePlayers()) {
/*  94 */       addViewer(player);
/*     */     }
/*     */     
/*  97 */     registerEvent(PlayerJoinEvent.class, EventPriority.LOWEST, event -> addViewer(event.getPlayer()));
/*     */     
/*  99 */     registerEvent(PlayerQuitEvent.class, EventPriority.MONITOR, event -> removeViewer(event.getPlayer()));
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public Audience sender(@NotNull CommandSender sender) {
/* 105 */     if (sender instanceof Player)
/* 106 */       return player((Player)sender); 
/* 107 */     if (sender instanceof ConsoleCommandSender)
/* 108 */       return console(); 
/* 109 */     if (sender instanceof ProxiedCommandSender)
/* 110 */       return sender(((ProxiedCommandSender)sender).getCallee()); 
/* 111 */     if (sender instanceof org.bukkit.entity.Entity || sender instanceof org.bukkit.block.Block) {
/* 112 */       return Audience.empty();
/*     */     }
/* 114 */     return (Audience)createAudience(Collections.singletonList(sender));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Audience player(@NotNull Player player) {
/* 119 */     return player(player.getUniqueId());
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected BukkitAudience createAudience(@NotNull Collection<CommandSender> viewers) {
/* 124 */     return new BukkitAudience(this.plugin, this, viewers);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 129 */     INSTANCES.remove(this.plugin.getName());
/* 130 */     super.close();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public ComponentFlattener flattener() {
/* 135 */     return BukkitComponentSerializer.FLATTENER;
/*     */   }
/*     */   
/*     */   static final class Builder implements BukkitAudiences.Builder { @NotNull
/*     */     private final Plugin plugin;
/*     */     private ComponentRenderer<Pointered> componentRenderer;
/*     */     
/*     */     Builder(@NotNull Plugin plugin) {
/* 143 */       this.plugin = Objects.<Plugin>requireNonNull(plugin, "plugin");
/* 144 */       componentRenderer(ptr -> (Locale)ptr.getOrDefault(Identity.LOCALE, BukkitAudiencesImpl.DEFAULT_LOCALE), (ComponentRenderer)GlobalTranslator.renderer());
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder componentRenderer(@NotNull ComponentRenderer<Pointered> componentRenderer) {
/* 149 */       this.componentRenderer = Objects.<ComponentRenderer<Pointered>>requireNonNull(componentRenderer, "component renderer");
/* 150 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public BukkitAudiences.Builder partition(@NotNull Function<Pointered, ?> partitionFunction) {
/* 155 */       Objects.requireNonNull(partitionFunction, "partitionFunction");
/* 156 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public BukkitAudiences build() {
/* 161 */       return BukkitAudiencesImpl.INSTANCES.computeIfAbsent(this.plugin.getName(), name -> {
/*     */             softDepend("ViaVersion");
/*     */             return new BukkitAudiencesImpl(this.plugin, this.componentRenderer);
/*     */           });
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
/*     */     
/*     */     private void softDepend(@NotNull String pluginName) {
/* 180 */       PluginDescriptionFile file = this.plugin.getDescription();
/* 181 */       if (file.getName().equals(pluginName))
/*     */         return; 
/*     */       try {
/* 184 */         Field softDepend = MinecraftReflection.needField(file.getClass(), "softDepend");
/* 185 */         List<String> dependencies = (List<String>)softDepend.get(file);
/* 186 */         if (!dependencies.contains(pluginName)) {
/* 187 */           ImmutableList immutableList = ImmutableList.builder().addAll(dependencies).add(pluginName).build();
/* 188 */           softDepend.set(file, immutableList);
/*     */         } 
/* 190 */       } catch (Throwable error) {
/* 191 */         Knob.logError(error, "Failed to inject softDepend in plugin.yml: %s %s", new Object[] { this.plugin, pluginName });
/*     */       } 
/*     */       
/*     */       try {
/* 195 */         PluginManager manager = this.plugin.getServer().getPluginManager();
/* 196 */         Field dependencyGraphField = MinecraftReflection.needField(manager.getClass(), "dependencyGraph");
/* 197 */         MutableGraph<String> graph = (MutableGraph<String>)dependencyGraphField.get(manager);
/* 198 */         graph.putEdge(file.getName(), pluginName);
/* 199 */       } catch (Throwable throwable) {}
/*     */     } }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private <T extends Event> void registerEvent(@NotNull Class<T> type, @NotNull EventPriority priority, @NotNull Consumer<T> callback) {
/* 217 */     Objects.requireNonNull(callback, "callback");
/* 218 */     this.plugin.getServer().getPluginManager().registerEvent(type, this, priority, (listener, event) -> callback.accept(event), this.plugin, true);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\platform\bukkit\BukkitAudiencesImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */