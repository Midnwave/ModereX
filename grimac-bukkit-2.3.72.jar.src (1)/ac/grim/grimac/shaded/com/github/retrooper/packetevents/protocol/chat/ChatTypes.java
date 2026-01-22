/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import java.util.Collection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ChatTypes
/*     */ {
/*  34 */   private static final VersionedRegistry<ChatType> REGISTRY = new VersionedRegistry("chat_type");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static ChatType define(String key) {
/*  41 */     return define(key, ChatTypeDecoration.withSender("chat.type.text"));
/*     */   }
/*     */   
/*     */   @Internal
/*     */   public static ChatType define(String key, ChatTypeDecoration chatDeco) {
/*  46 */     return define(key, chatDeco, ChatTypeDecoration.withSender("chat.type.text.narrate"));
/*     */   }
/*     */   
/*     */   @Internal
/*     */   public static ChatType define(String key, ChatTypeDecoration chatDeco, ChatTypeDecoration narrationDeco) {
/*  51 */     return (ChatType)REGISTRY.define(key, data -> new StaticChatType(data, chatDeco, narrationDeco));
/*     */   }
/*     */ 
/*     */   
/*     */   public static VersionedRegistry<ChatType> getRegistry() {
/*  56 */     return REGISTRY;
/*     */   }
/*     */   
/*     */   public static ChatType getByName(String name) {
/*  60 */     return (ChatType)REGISTRY.getByName(name);
/*     */   }
/*     */   
/*     */   public static ChatType getById(ClientVersion version, int id) {
/*  64 */     return (ChatType)REGISTRY.getById(version, id);
/*     */   }
/*     */   
/*  67 */   public static final ChatType CHAT = define("chat");
/*  68 */   public static final ChatType SAY_COMMAND = define("say_command", 
/*  69 */       ChatTypeDecoration.withSender("chat.type.announcement"));
/*  70 */   public static final ChatType MSG_COMMAND_INCOMING = define("msg_command_incoming", 
/*  71 */       ChatTypeDecoration.incomingDirectMessage("commands.message.display.incoming"));
/*  72 */   public static final ChatType MSG_COMMAND_OUTGOING = define("msg_command_outgoing", 
/*  73 */       ChatTypeDecoration.outgoingDirectMessage("commands.message.display.outgoing"));
/*  74 */   public static final ChatType TEAM_MSG_COMMAND_INCOMING = define("team_msg_command_incoming", 
/*  75 */       ChatTypeDecoration.teamMessage("chat.type.team.text"));
/*  76 */   public static final ChatType TEAM_MSG_COMMAND_OUTGOING = define("team_msg_command_outgoing", 
/*  77 */       ChatTypeDecoration.teamMessage("chat.type.team.sent"));
/*  78 */   public static final ChatType EMOTE_COMMAND = define("emote_command", 
/*  79 */       ChatTypeDecoration.withSender("chat.type.emote"), 
/*  80 */       ChatTypeDecoration.withSender("chat.type.emote"));
/*     */ 
/*     */   
/*  83 */   public static final ChatType RAW = define("raw");
/*     */   
/*     */   @Deprecated
/*  86 */   public static final ChatType SYSTEM = define("system");
/*     */   @Deprecated
/*  88 */   public static final ChatType GAME_INFO = define("game_info");
/*     */   @Deprecated
/*  90 */   public static final ChatType MSG_COMMAND = define("msg_command");
/*     */   @Deprecated
/*  92 */   public static final ChatType TEAM_MSG_COMMAND = define("team_msg_command");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Collection<ChatType> values() {
/* 100 */     return REGISTRY.getEntries();
/*     */   }
/*     */   
/*     */   static {
/* 104 */     REGISTRY.unloadMappings();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\ChatTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */