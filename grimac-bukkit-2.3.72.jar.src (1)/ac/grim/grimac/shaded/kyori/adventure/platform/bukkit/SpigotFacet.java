/*     */ package ac.grim.grimac.shaded.kyori.adventure.platform.bukkit;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.audience.MessageType;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.identity.Identity;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.Facet;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.FacetBase;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.FacetComponentFlattener;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.Knob;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
/*     */ import net.md_5.bungee.api.ChatMessageType;
/*     */ import net.md_5.bungee.api.chat.BaseComponent;
/*     */ import net.md_5.bungee.chat.TranslationRegistry;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.meta.BookMeta;
/*     */ import org.bukkit.inventory.meta.ItemMeta;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class SpigotFacet<V extends CommandSender>
/*     */   extends FacetBase<V>
/*     */ {
/*  56 */   private static final boolean SUPPORTED = (Knob.isEnabled("spigot", true) && BungeeComponentSerializer.isNative());
/*     */   
/*     */   protected SpigotFacet(@Nullable Class<? extends V> viewerClass) {
/*  59 */     super(viewerClass);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSupported() {
/*  64 */     return (super.isSupported() && SUPPORTED);
/*     */   }
/*     */   
/*  67 */   private static final Class<?> BUNGEE_CHAT_MESSAGE_TYPE = MinecraftReflection.findClass(new String[] { "net.md_5.bungee.api.ChatMessageType" });
/*  68 */   static final Class<?> BUNGEE_COMPONENT_TYPE = MinecraftReflection.findClass(new String[] { "net.md_5.bungee.api.chat.BaseComponent" });
/*     */   
/*     */   static class Message<V extends CommandSender> extends SpigotFacet<V> implements Facet.Message<V, BaseComponent[]> {
/*  71 */     private static final BungeeComponentSerializer SERIALIZER = BungeeComponentSerializer.of(BukkitComponentSerializer.gson(), BukkitComponentSerializer.legacy());
/*     */     
/*     */     protected Message(@Nullable Class<? extends V> viewerClass) {
/*  74 */       super(viewerClass);
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public BaseComponent[] createMessage(@NotNull V viewer, @NotNull Component message) {
/*  79 */       return SERIALIZER.serialize(message);
/*     */     }
/*     */   }
/*     */   
/*     */   static final class Chat extends Message<CommandSender> implements Facet.Chat<CommandSender, BaseComponent[]> {
/*  84 */     private static final boolean SUPPORTED = MinecraftReflection.hasClass(new String[] { "org.bukkit.command.CommandSender$Spigot" });
/*     */     
/*     */     protected Chat() {
/*  87 */       super(CommandSender.class);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isSupported() {
/*  92 */       return (super.isSupported() && SUPPORTED);
/*     */     }
/*     */ 
/*     */     
/*     */     public void sendMessage(@NotNull CommandSender viewer, @NotNull Identity source, BaseComponent[] message, @NotNull Object type) {
/*  97 */       viewer.spigot().sendMessage(message);
/*     */     }
/*     */   }
/*     */   
/*     */   static class ChatWithType extends Message<Player> implements Facet.Chat<Player, BaseComponent[]> {
/* 102 */     private static final Class<?> PLAYER_CLASS = MinecraftReflection.findClass(new String[] { "org.bukkit.entity.Player$Spigot" });
/* 103 */     private static final boolean SUPPORTED = MinecraftReflection.hasMethod(PLAYER_CLASS, "sendMessage", new Class[] { SpigotFacet.access$000(), BUNGEE_COMPONENT_TYPE });
/*     */     
/*     */     protected ChatWithType() {
/* 106 */       super(Player.class);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isSupported() {
/* 111 */       return (super.isSupported() && SUPPORTED);
/*     */     }
/*     */     @Nullable
/*     */     private ChatMessageType createType(@NotNull MessageType type) {
/* 115 */       if (type == MessageType.CHAT)
/* 116 */         return ChatMessageType.CHAT; 
/* 117 */       if (type == MessageType.SYSTEM) {
/* 118 */         return ChatMessageType.SYSTEM;
/*     */       }
/* 120 */       Knob.logUnsupported(this, type);
/* 121 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void sendMessage(@NotNull Player viewer, @NotNull Identity source, BaseComponent[] message, @NotNull Object type) {
/* 127 */       ChatMessageType chat = (type instanceof MessageType) ? createType((MessageType)type) : ChatMessageType.SYSTEM;
/* 128 */       if (chat != null)
/* 129 */         viewer.spigot().sendMessage(chat, message); 
/*     */     }
/*     */   }
/*     */   
/*     */   static final class ActionBar
/*     */     extends ChatWithType
/*     */     implements Facet.ActionBar<Player, BaseComponent[]>
/*     */   {
/*     */     public void sendMessage(@NotNull Player viewer, BaseComponent[] message) {
/* 138 */       viewer.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
/*     */     }
/*     */   }
/*     */   
/*     */   static final class Book extends Message<Player> implements Facet.Book<Player, BaseComponent[], ItemStack> {
/* 143 */     private static final boolean SUPPORTED = MinecraftReflection.hasMethod(Player.class, "openBook", new Class[] { ItemStack.class });
/*     */     
/*     */     protected Book() {
/* 146 */       super(Player.class);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isSupported() {
/* 151 */       return (super.isSupported() && SUPPORTED);
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public ItemStack createBook(@NotNull String title, @NotNull String author, @NotNull Iterable<BaseComponent[]> pages) {
/* 156 */       ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
/* 157 */       ItemMeta meta = book.getItemMeta();
/* 158 */       if (meta instanceof BookMeta) {
/* 159 */         BookMeta spigot = (BookMeta)meta;
/* 160 */         for (BaseComponent[] page : pages) {
/* 161 */           spigot.spigot().addPage(new BaseComponent[][] { page });
/*     */         } 
/* 163 */         spigot.setTitle(title);
/* 164 */         spigot.setAuthor(author);
/* 165 */         book.setItemMeta((ItemMeta)spigot);
/*     */       } 
/* 167 */       return book;
/*     */     }
/*     */ 
/*     */     
/*     */     public void openBook(@NotNull Player viewer, @NotNull ItemStack book) {
/* 172 */       viewer.openBook(book);
/*     */     }
/*     */   }
/*     */   
/*     */   static class Translator extends FacetBase<Server> implements FacetComponentFlattener.Translator<Server> {
/* 177 */     private static final boolean SUPPORTED = MinecraftReflection.hasClass(new String[] { "net.md_5.bungee.chat.TranslationRegistry" });
/*     */     
/*     */     Translator() {
/* 180 */       super(Server.class);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isSupported() {
/* 185 */       return (super.isSupported() && SUPPORTED);
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public String valueOrDefault(@NotNull Server game, @NotNull String key) {
/* 190 */       return TranslationRegistry.INSTANCE.translate(key);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\platform\bukkit\SpigotFacet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */