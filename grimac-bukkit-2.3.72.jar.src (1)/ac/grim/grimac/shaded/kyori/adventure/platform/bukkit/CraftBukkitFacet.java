/*      */ package ac.grim.grimac.shaded.kyori.adventure.platform.bukkit;
/*      */ 
/*      */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*      */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.audience.MessageType;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.chat.ChatType;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.identity.Identity;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.nbt.BinaryTag;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.nbt.BinaryTagIO;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.nbt.BinaryTagTypes;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.nbt.CompoundBinaryTag;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.nbt.ListBinaryTag;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.nbt.StringBinaryTag;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.Facet;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.FacetBase;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.FacetComponentFlattener;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.Knob;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.sound.Sound;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*      */ import ac.grim.grimac.shaded.kyori.adventure.text.TextComponent;
/*      */ import io.netty.buffer.ByteBuf;
/*      */ import io.netty.buffer.Unpooled;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.DataInput;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.lang.invoke.LambdaMetafactory;
/*      */ import java.lang.invoke.MethodHandle;
/*      */ import java.lang.invoke.MethodHandles;
/*      */ import java.lang.invoke.MethodType;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.HashSet;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Optional;
/*      */ import java.util.Set;
/*      */ import java.util.UUID;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.ThreadLocalRandom;
/*      */ import java.util.function.Function;
/*      */ import java.util.function.Supplier;
/*      */ import org.bukkit.Location;
/*      */ import org.bukkit.Material;
/*      */ import org.bukkit.Server;
/*      */ import org.bukkit.World;
/*      */ import org.bukkit.command.CommandSender;
/*      */ import org.bukkit.entity.Damageable;
/*      */ import org.bukkit.entity.Entity;
/*      */ import org.bukkit.entity.Player;
/*      */ import org.bukkit.entity.Wither;
/*      */ import org.bukkit.event.EventHandler;
/*      */ import org.bukkit.event.EventPriority;
/*      */ import org.bukkit.event.HandlerList;
/*      */ import org.bukkit.event.Listener;
/*      */ import org.bukkit.event.player.PlayerMoveEvent;
/*      */ import org.bukkit.inventory.ItemStack;
/*      */ import org.bukkit.inventory.PlayerInventory;
/*      */ import org.bukkit.plugin.Plugin;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ class CraftBukkitFacet<V extends CommandSender>
/*      */   extends FacetBase<V>
/*      */ {
/*      */   protected CraftBukkitFacet(@Nullable Class<? extends V> viewerClass) {
/*  115 */     super(viewerClass);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isSupported() {
/*  120 */     return (super.isSupported() && SUPPORTED);
/*      */   }
/*      */   
/*  123 */   private static final Class<?> CLASS_NMS_ENTITY = MinecraftReflection.findClass(new String[] {
/*  124 */         MinecraftReflection.findNmsClassName("Entity"), 
/*  125 */         MinecraftReflection.findMcClassName("world.entity.Entity")
/*      */       });
/*  127 */   private static final Class<?> CLASS_CRAFT_ENTITY = MinecraftReflection.findCraftClass("entity.CraftEntity");
/*  128 */   private static final MethodHandle CRAFT_ENTITY_GET_HANDLE = MinecraftReflection.findMethod(CLASS_CRAFT_ENTITY, "getHandle", CLASS_NMS_ENTITY, new Class[0]); @Nullable
/*  129 */   static final Class<? extends Player> CLASS_CRAFT_PLAYER = MinecraftReflection.findCraftClass("entity.CraftPlayer", Player.class); @Nullable
/*      */   static final MethodHandle CRAFT_PLAYER_GET_HANDLE; @Nullable
/*      */   private static final MethodHandle ENTITY_PLAYER_GET_CONNECTION; @Nullable
/*      */   private static final MethodHandle PLAYER_CONNECTION_SEND_PACKET;
/*      */   
/*      */   static {
/*  135 */     Class<?> craftPlayerClass = MinecraftReflection.findCraftClass("entity.CraftPlayer");
/*  136 */     Class<?> packetClass = MinecraftReflection.findClass(new String[] {
/*  137 */           MinecraftReflection.findNmsClassName("Packet"), 
/*  138 */           MinecraftReflection.findMcClassName("network.protocol.Packet")
/*      */         });
/*      */     
/*  141 */     MethodHandle craftPlayerGetHandle = null;
/*  142 */     MethodHandle entityPlayerGetConnection = null;
/*  143 */     MethodHandle playerConnectionSendPacket = null;
/*  144 */     if (craftPlayerClass != null && packetClass != null) {
/*      */       try {
/*  146 */         Method getHandleMethod = craftPlayerClass.getMethod("getHandle", new Class[0]);
/*  147 */         Class<?> entityPlayerClass = getHandleMethod.getReturnType();
/*  148 */         craftPlayerGetHandle = MinecraftReflection.lookup().unreflect(getHandleMethod);
/*  149 */         Field playerConnectionField = MinecraftReflection.findField(entityPlayerClass, new String[] { "playerConnection", "connection" });
/*  150 */         Class<?> playerConnectionClass = null;
/*  151 */         if (playerConnectionField != null) {
/*  152 */           entityPlayerGetConnection = MinecraftReflection.lookup().unreflectGetter(playerConnectionField);
/*  153 */           playerConnectionClass = playerConnectionField.getType();
/*      */         } else {
/*  155 */           Class<?> serverGamePacketListenerImpl = MinecraftReflection.findClass(new String[] {
/*  156 */                 MinecraftReflection.findNmsClassName("PlayerConnection"), 
/*  157 */                 MinecraftReflection.findMcClassName("server.network.PlayerConnection"), 
/*  158 */                 MinecraftReflection.findMcClassName("server.network.ServerGamePacketListenerImpl")
/*      */               });
/*  160 */           for (Field field : entityPlayerClass.getDeclaredFields()) {
/*  161 */             int modifiers = field.getModifiers();
/*  162 */             if (Modifier.isPublic(modifiers) && !Modifier.isFinal(modifiers) && (
/*  163 */               serverGamePacketListenerImpl == null || field.getType().equals(serverGamePacketListenerImpl))) {
/*  164 */               entityPlayerGetConnection = MinecraftReflection.lookup().unreflectGetter(field);
/*  165 */               playerConnectionClass = field.getType();
/*      */             } 
/*      */           } 
/*      */         } 
/*      */         
/*  170 */         Class<?> serverCommonPacketListenerImpl = MinecraftReflection.findClass(new String[] { MinecraftReflection.findMcClassName("server.network.ServerCommonPacketListenerImpl") });
/*  171 */         if (serverCommonPacketListenerImpl != null) {
/*  172 */           playerConnectionClass = serverCommonPacketListenerImpl;
/*      */         }
/*  174 */         playerConnectionSendPacket = MinecraftReflection.searchMethod(playerConnectionClass, Integer.valueOf(1), new String[] { "sendPacket", "send" }, void.class, new Class[] { packetClass });
/*  175 */       } catch (Throwable error) {
/*  176 */         Knob.logError(error, "Failed to initialize CraftBukkit sendPacket", new Object[0]);
/*      */       } 
/*      */     }
/*      */     
/*  180 */     CRAFT_PLAYER_GET_HANDLE = craftPlayerGetHandle;
/*  181 */     ENTITY_PLAYER_GET_CONNECTION = entityPlayerGetConnection;
/*  182 */     PLAYER_CONNECTION_SEND_PACKET = playerConnectionSendPacket;
/*      */   }
/*      */   
/*  185 */   private static final boolean SUPPORTED = (Knob.isEnabled("craftbukkit", true) && 
/*  186 */     MinecraftComponentSerializer.isSupported() && CRAFT_PLAYER_GET_HANDLE != null && ENTITY_PLAYER_GET_CONNECTION != null && PLAYER_CONNECTION_SEND_PACKET != null);
/*      */   
/*      */   static class PacketFacet<V extends CommandSender>
/*      */     extends CraftBukkitFacet<V>
/*      */     implements Facet.Message<V, Object> {
/*      */     protected PacketFacet() {
/*  192 */       super((Class)CLASS_CRAFT_PLAYER);
/*      */     }
/*      */     
/*      */     public void sendPacket(@NotNull Player player, @Nullable Object packet) {
/*  196 */       if (packet == null)
/*      */         return; 
/*      */       try {
/*  199 */         CraftBukkitFacet.PLAYER_CONNECTION_SEND_PACKET.invoke(CraftBukkitFacet.ENTITY_PLAYER_GET_CONNECTION.invoke(CRAFT_PLAYER_GET_HANDLE.invoke(player)), packet);
/*  200 */       } catch (Throwable error) {
/*  201 */         Knob.logError(error, "Failed to invoke CraftBukkit sendPacket: %s", new Object[] { packet });
/*      */       } 
/*      */     }
/*      */     
/*      */     public void sendMessage(@NotNull V player, @Nullable Object packet) {
/*  206 */       sendPacket((Player)player, packet);
/*      */     }
/*      */ 
/*      */     
/*      */     @Nullable
/*      */     public Object createMessage(@NotNull V viewer, @NotNull Component message) {
/*      */       try {
/*  213 */         return MinecraftComponentSerializer.get().serialize(message);
/*  214 */       } catch (Throwable error) {
/*  215 */         Knob.logError(error, "Failed to serialize net.minecraft.server IChatBaseComponent: %s", new Object[] { message });
/*  216 */         return null;
/*      */       } 
/*      */     } }
/*      */   
/*      */   @Nullable
/*  221 */   private static final Class<?> CLASS_CHAT_COMPONENT = MinecraftReflection.findClass(new String[] {
/*  222 */         MinecraftReflection.findNmsClassName("IChatBaseComponent"), 
/*  223 */         MinecraftReflection.findMcClassName("network.chat.IChatBaseComponent"), 
/*  224 */         MinecraftReflection.findMcClassName("network.chat.Component") });
/*      */   @Nullable
/*  226 */   private static final Class<?> CLASS_MESSAGE_TYPE = MinecraftReflection.findClass(new String[] {
/*  227 */         MinecraftReflection.findNmsClassName("ChatMessageType"), 
/*  228 */         MinecraftReflection.findMcClassName("network.chat.ChatMessageType"), 
/*  229 */         MinecraftReflection.findMcClassName("network.chat.ChatType") });
/*      */   @Nullable private static final Object MESSAGE_TYPE_CHAT;
/*      */   @Nullable private static final Object MESSAGE_TYPE_SYSTEM; @Nullable private static final Object MESSAGE_TYPE_ACTIONBAR; @Nullable private static final MethodHandle LEGACY_CHAT_PACKET_CONSTRUCTOR; @Nullable private static final MethodHandle CHAT_PACKET_CONSTRUCTOR; @Nullable private static final Class<?> CLASS_TITLE_PACKET; @Nullable private static final Class<?> CLASS_TITLE_ACTION; private static final MethodHandle CONSTRUCTOR_TITLE_MESSAGE; @Nullable private static final MethodHandle CONSTRUCTOR_TITLE_TIMES; @Nullable private static final Object TITLE_ACTION_TITLE; @Nullable private static final Object TITLE_ACTION_SUBTITLE; @Nullable private static final Object TITLE_ACTION_ACTIONBAR; @Nullable private static final Object TITLE_ACTION_CLEAR; @Nullable private static final Object TITLE_ACTION_RESET; static class Chat1_19_3 extends Chat {
/*      */     public boolean isSupported() { return (super.isSupported() && CraftBukkitAccess.Chat1_19_3.isSupported()); } public void sendMessage(@NotNull CommandSender viewer, @NotNull Identity source, @NotNull Object message, @NotNull Object type) { if (!(type instanceof ChatType.Bound)) { super.sendMessage(viewer, source, message, type); } else { ChatType.Bound bound = (ChatType.Bound)type; try { Object boundNetwork, nameComponent = createMessage(viewer, bound.name()); Object targetComponent = (bound.target() != null) ? createMessage(viewer, bound.target()) : null; Object registryAccess = CraftBukkitAccess.Chat1_19_3.ACTUAL_GET_REGISTRY_ACCESS.invoke(CraftBukkitAccess.Chat1_19_3.SERVER_PLAYER_GET_LEVEL.invoke(CRAFT_PLAYER_GET_HANDLE.invoke(viewer))); Object chatTypeRegistry = CraftBukkitAccess.Chat1_19_3.REGISTRY_ACCESS_GET_REGISTRY_OPTIONAL.invoke(registryAccess, CraftBukkitAccess.Chat1_19_3.CHAT_TYPE_RESOURCE_KEY).orElseThrow(java.util.NoSuchElementException::new); Object typeResourceLocation = CraftBukkitAccess.NEW_RESOURCE_LOCATION.invoke(bound.type().key().namespace(), bound.type().key().value()); if (CraftBukkitAccess.Chat1_19_3.CHAT_TYPE_BOUND_NETWORK_CONSTRUCTOR != null) { Object chatTypeObject = CraftBukkitAccess.Chat1_19_3.REGISTRY_GET_OPTIONAL.invoke(chatTypeRegistry, typeResourceLocation).orElseThrow(java.util.NoSuchElementException::new); int networkId = CraftBukkitAccess.Chat1_19_3.REGISTRY_GET_ID.invoke(chatTypeRegistry, chatTypeObject); if (networkId < 0)
/*      */               throw new IllegalArgumentException("Could not get a valid network id from " + type);  boundNetwork = CraftBukkitAccess.Chat1_19_3.CHAT_TYPE_BOUND_NETWORK_CONSTRUCTOR.invoke(networkId, nameComponent, targetComponent); } else { Object chatTypeHolder = CraftBukkitAccess.Chat1_19_3.REGISTRY_GET_HOLDER.invoke(chatTypeRegistry, typeResourceLocation).orElseThrow(java.util.NoSuchElementException::new); boundNetwork = CraftBukkitAccess.Chat1_19_3.CHAT_TYPE_BOUND_CONSTRUCTOR.invoke(chatTypeHolder, nameComponent, Optional.ofNullable(targetComponent)); }  sendMessage(viewer, CraftBukkitAccess.Chat1_19_3.DISGUISED_CHAT_PACKET_CONSTRUCTOR.invoke(message, boundNetwork)); }
/*      */         catch (Throwable error) { Knob.logError(error, "Failed to send a 1.19.3+ message: %s %s", new Object[] { message, type }); }
/*      */          }
/*  236 */        } } static { if (CLASS_MESSAGE_TYPE != null && !CLASS_MESSAGE_TYPE.isEnum()) {
/*  237 */       MESSAGE_TYPE_CHAT = Integer.valueOf(0);
/*  238 */       MESSAGE_TYPE_SYSTEM = Integer.valueOf(1);
/*  239 */       MESSAGE_TYPE_ACTIONBAR = Integer.valueOf(2);
/*      */     } else {
/*  241 */       MESSAGE_TYPE_CHAT = MinecraftReflection.findEnum(CLASS_MESSAGE_TYPE, "CHAT", 0);
/*  242 */       MESSAGE_TYPE_SYSTEM = MinecraftReflection.findEnum(CLASS_MESSAGE_TYPE, "SYSTEM", 1);
/*  243 */       MESSAGE_TYPE_ACTIONBAR = MinecraftReflection.findEnum(CLASS_MESSAGE_TYPE, "GAME_INFO", 2);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  251 */     MethodHandle legacyChatPacketConstructor = null;
/*  252 */     MethodHandle chatPacketConstructor = null;
/*      */     
/*      */     try {
/*  255 */       if (CLASS_CHAT_COMPONENT != null) {
/*  256 */         Class<?> chatPacketClass = MinecraftReflection.needClass(new String[] {
/*  257 */               MinecraftReflection.findNmsClassName("PacketPlayOutChat"), 
/*  258 */               MinecraftReflection.findMcClassName("network.protocol.game.PacketPlayOutChat"), 
/*  259 */               MinecraftReflection.findMcClassName("network.protocol.game.ClientboundChatPacket"), 
/*  260 */               MinecraftReflection.findMcClassName("network.protocol.game.ClientboundSystemChatPacket")
/*      */             });
/*  262 */         if (MESSAGE_TYPE_CHAT == Integer.valueOf(0))
/*      */         {
/*  264 */           chatPacketConstructor = MinecraftReflection.findConstructor(chatPacketClass, new Class[] { CLASS_CHAT_COMPONENT, boolean.class });
/*      */         }
/*  266 */         if (chatPacketConstructor == null)
/*      */         {
/*  268 */           chatPacketConstructor = MinecraftReflection.findConstructor(chatPacketClass, new Class[] { CLASS_CHAT_COMPONENT, int.class });
/*      */         }
/*  270 */         if (chatPacketConstructor == null)
/*      */         {
/*  272 */           chatPacketConstructor = MinecraftReflection.findConstructor(chatPacketClass, new Class[] { CLASS_CHAT_COMPONENT });
/*      */         }
/*  274 */         if (chatPacketConstructor == null) {
/*  275 */           if (CLASS_MESSAGE_TYPE != null) {
/*  276 */             chatPacketConstructor = MinecraftReflection.findConstructor(chatPacketClass, new Class[] { CLASS_CHAT_COMPONENT, CLASS_MESSAGE_TYPE, UUID.class });
/*      */           }
/*      */         }
/*  279 */         else if (MESSAGE_TYPE_CHAT == Integer.valueOf(0)) {
/*  280 */           if (chatPacketConstructor.type().parameterType(1).equals(boolean.class)) {
/*      */             
/*  282 */             chatPacketConstructor = MethodHandles.insertArguments(chatPacketConstructor, 1, new Object[] { Boolean.FALSE });
/*  283 */             chatPacketConstructor = MethodHandles.dropArguments(chatPacketConstructor, 1, new Class[] { Integer.class, UUID.class });
/*      */           } else {
/*      */             
/*  286 */             chatPacketConstructor = MethodHandles.dropArguments(chatPacketConstructor, 2, new Class[] { UUID.class });
/*      */           } 
/*      */         } else {
/*      */           
/*  290 */           chatPacketConstructor = MethodHandles.dropArguments(chatPacketConstructor, 1, new Class[] { (CLASS_MESSAGE_TYPE == null) ? Object.class : CLASS_MESSAGE_TYPE, UUID.class });
/*      */         } 
/*      */         
/*  293 */         legacyChatPacketConstructor = MinecraftReflection.findConstructor(chatPacketClass, new Class[] { CLASS_CHAT_COMPONENT, byte.class });
/*  294 */         if (legacyChatPacketConstructor == null) {
/*  295 */           legacyChatPacketConstructor = MinecraftReflection.findConstructor(chatPacketClass, new Class[] { CLASS_CHAT_COMPONENT, int.class });
/*      */         }
/*      */       } 
/*  298 */     } catch (Throwable error) {
/*  299 */       Knob.logError(error, "Failed to initialize ClientboundChatPacket constructor", new Object[0]);
/*      */     } 
/*      */     
/*  302 */     CHAT_PACKET_CONSTRUCTOR = chatPacketConstructor;
/*  303 */     LEGACY_CHAT_PACKET_CONSTRUCTOR = legacyChatPacketConstructor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  362 */     CLASS_TITLE_PACKET = MinecraftReflection.findClass(new String[] {
/*  363 */           MinecraftReflection.findNmsClassName("PacketPlayOutTitle"), 
/*  364 */           MinecraftReflection.findMcClassName("network.protocol.game.PacketPlayOutTitle")
/*      */         });
/*  366 */     CLASS_TITLE_ACTION = MinecraftReflection.findClass(new String[] {
/*  367 */           MinecraftReflection.findNmsClassName("PacketPlayOutTitle$EnumTitleAction"), 
/*  368 */           MinecraftReflection.findMcClassName("network.protocol.game.PacketPlayOutTitle$EnumTitleAction")
/*      */         });
/*  370 */     CONSTRUCTOR_TITLE_MESSAGE = MinecraftReflection.findConstructor(CLASS_TITLE_PACKET, new Class[] { CLASS_TITLE_ACTION, CLASS_CHAT_COMPONENT });
/*  371 */     CONSTRUCTOR_TITLE_TIMES = MinecraftReflection.findConstructor(CLASS_TITLE_PACKET, new Class[] { int.class, int.class, int.class });
/*  372 */     TITLE_ACTION_TITLE = MinecraftReflection.findEnum(CLASS_TITLE_ACTION, "TITLE", 0);
/*  373 */     TITLE_ACTION_SUBTITLE = MinecraftReflection.findEnum(CLASS_TITLE_ACTION, "SUBTITLE", 1);
/*  374 */     TITLE_ACTION_ACTIONBAR = MinecraftReflection.findEnum(CLASS_TITLE_ACTION, "ACTIONBAR");
/*  375 */     TITLE_ACTION_CLEAR = MinecraftReflection.findEnum(CLASS_TITLE_ACTION, "CLEAR");
/*  376 */     TITLE_ACTION_RESET = MinecraftReflection.findEnum(CLASS_TITLE_ACTION, "RESET"); } static class Chat extends PacketFacet<CommandSender> implements Facet.Chat<CommandSender, Object> {
/*      */     public boolean isSupported() { return (super.isSupported() && CraftBukkitFacet.CHAT_PACKET_CONSTRUCTOR != null); } public void sendMessage(@NotNull CommandSender viewer, @NotNull Identity source, @NotNull Object message, @NotNull Object type) { Object messageType = (type == MessageType.CHAT) ? CraftBukkitFacet.MESSAGE_TYPE_CHAT : CraftBukkitFacet.MESSAGE_TYPE_SYSTEM; try { sendMessage(viewer, CraftBukkitFacet.CHAT_PACKET_CONSTRUCTOR.invoke(message, messageType, source.uuid())); } catch (Throwable error) { Knob.logError(error, "Failed to invoke PacketPlayOutChat constructor: %s %s", new Object[] { message, messageType }); }  }
/*      */   } static class ActionBar_1_17 extends PacketFacet<Player> implements Facet.ActionBar<Player, Object> {
/*  379 */     @Nullable private static final Class<?> CLASS_SET_ACTION_BAR_TEXT_PACKET = MinecraftReflection.findMcClass(new String[] { "network.protocol.game.ClientboundSetActionBarTextPacket" }); @Nullable
/*  380 */     private static final MethodHandle CONSTRUCTOR_ACTION_BAR = MinecraftReflection.findConstructor(CLASS_SET_ACTION_BAR_TEXT_PACKET, new Class[] { CraftBukkitFacet.access$500() });
/*      */ 
/*      */     
/*      */     public boolean isSupported() {
/*  384 */       return (super.isSupported() && CONSTRUCTOR_ACTION_BAR != null);
/*      */     }
/*      */ 
/*      */     
/*      */     @Nullable
/*      */     public Object createMessage(@NotNull Player viewer, @NotNull Component message) {
/*      */       try {
/*  391 */         return CONSTRUCTOR_ACTION_BAR.invoke(super.createMessage(viewer, message));
/*  392 */       } catch (Throwable error) {
/*  393 */         Knob.logError(error, "Failed to invoke PacketPlayOutTitle constructor: %s", new Object[] { message });
/*  394 */         return null;
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   static class ActionBar
/*      */     extends PacketFacet<Player> implements Facet.ActionBar<Player, Object> {
/*      */     public boolean isSupported() {
/*  402 */       return (super.isSupported() && CraftBukkitFacet.TITLE_ACTION_ACTIONBAR != null);
/*      */     }
/*      */ 
/*      */     
/*      */     @Nullable
/*      */     public Object createMessage(@NotNull Player viewer, @NotNull Component message) {
/*      */       try {
/*  409 */         return CraftBukkitFacet.CONSTRUCTOR_TITLE_MESSAGE.invoke(CraftBukkitFacet.TITLE_ACTION_ACTIONBAR, super.createMessage(viewer, message));
/*  410 */       } catch (Throwable error) {
/*  411 */         Knob.logError(error, "Failed to invoke PacketPlayOutTitle constructor: %s", new Object[] { message });
/*  412 */         return null;
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   static class ActionBarLegacy
/*      */     extends PacketFacet<Player> implements Facet.ActionBar<Player, Object> {
/*      */     public boolean isSupported() {
/*  420 */       return (super.isSupported() && CraftBukkitFacet.LEGACY_CHAT_PACKET_CONSTRUCTOR != null);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     @Nullable
/*      */     public Object createMessage(@NotNull Player viewer, @NotNull Component message) {
/*  427 */       TextComponent legacyMessage = Component.text(BukkitComponentSerializer.legacy().serialize(message));
/*      */       try {
/*  429 */         return CraftBukkitFacet.LEGACY_CHAT_PACKET_CONSTRUCTOR.invoke(super.createMessage(viewer, (Component)legacyMessage), (byte)2);
/*  430 */       } catch (Throwable error) {
/*  431 */         Knob.logError(error, "Failed to invoke PacketPlayOutChat constructor: %s", new Object[] { legacyMessage });
/*  432 */         return null;
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private static interface PartialEntitySound
/*      */     extends Facet.EntitySound<Player, Object> {
/*  439 */     public static final Map<String, Object> MC_SOUND_SOURCE_BY_NAME = new ConcurrentHashMap<>();
/*      */ 
/*      */     
/*      */     default Object createForSelf(Player viewer, Sound sound) {
/*  443 */       return createForEntity(sound, (Entity)viewer);
/*      */     }
/*      */ 
/*      */     
/*      */     default Object createForEmitter(Sound sound, Sound.Emitter emitter) {
/*      */       Entity entity;
/*  449 */       if (emitter instanceof BukkitEmitter) {
/*  450 */         entity = ((BukkitEmitter)emitter).entity;
/*  451 */       } else if (emitter instanceof Entity) {
/*  452 */         entity = (Entity)emitter;
/*      */       } else {
/*  454 */         return null;
/*      */       } 
/*  456 */       return createForEntity(sound, entity);
/*      */     }
/*      */     
/*      */     default Object toNativeEntity(Entity entity) throws Throwable {
/*  460 */       if (!CraftBukkitFacet.CLASS_CRAFT_ENTITY.isInstance(entity)) return null;
/*      */       
/*  462 */       return CraftBukkitFacet.CRAFT_ENTITY_GET_HANDLE.invoke(entity);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     default Object toVanilla(Sound.Source source) throws Throwable {
/*      */       // Byte code:
/*      */       //   0: getstatic ac/grim/grimac/shaded/kyori/adventure/platform/bukkit/CraftBukkitFacet$PartialEntitySound.MC_SOUND_SOURCE_BY_NAME : Ljava/util/Map;
/*      */       //   3: invokeinterface isEmpty : ()Z
/*      */       //   8: ifeq -> 61
/*      */       //   11: getstatic ac/grim/grimac/shaded/kyori/adventure/platform/bukkit/CraftBukkitAccess$EntitySound.CLASS_SOUND_SOURCE : Ljava/lang/Class;
/*      */       //   14: invokevirtual getEnumConstants : ()[Ljava/lang/Object;
/*      */       //   17: astore_2
/*      */       //   18: aload_2
/*      */       //   19: arraylength
/*      */       //   20: istore_3
/*      */       //   21: iconst_0
/*      */       //   22: istore #4
/*      */       //   24: iload #4
/*      */       //   26: iload_3
/*      */       //   27: if_icmpge -> 61
/*      */       //   30: aload_2
/*      */       //   31: iload #4
/*      */       //   33: aaload
/*      */       //   34: astore #5
/*      */       //   36: getstatic ac/grim/grimac/shaded/kyori/adventure/platform/bukkit/CraftBukkitFacet$PartialEntitySound.MC_SOUND_SOURCE_BY_NAME : Ljava/util/Map;
/*      */       //   39: getstatic ac/grim/grimac/shaded/kyori/adventure/platform/bukkit/CraftBukkitAccess$EntitySound.SOUND_SOURCE_GET_NAME : Ljava/lang/invoke/MethodHandle;
/*      */       //   42: aload #5
/*      */       //   44: invokevirtual invoke : (Ljava/lang/Object;)Ljava/lang/String;
/*      */       //   47: aload #5
/*      */       //   49: invokeinterface put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*      */       //   54: pop
/*      */       //   55: iinc #4, 1
/*      */       //   58: goto -> 24
/*      */       //   61: getstatic ac/grim/grimac/shaded/kyori/adventure/platform/bukkit/CraftBukkitFacet$PartialEntitySound.MC_SOUND_SOURCE_BY_NAME : Ljava/util/Map;
/*      */       //   64: getstatic ac/grim/grimac/shaded/kyori/adventure/sound/Sound$Source.NAMES : Lac/grim/grimac/shaded/kyori/adventure/util/Index;
/*      */       //   67: aload_1
/*      */       //   68: invokevirtual key : (Ljava/lang/Object;)Ljava/lang/Object;
/*      */       //   71: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
/*      */       //   76: areturn
/*      */       // Line number table:
/*      */       //   Java source line number -> byte code offset
/*      */       //   #466	-> 0
/*      */       //   #467	-> 11
/*      */       //   #468	-> 36
/*      */       //   #467	-> 55
/*      */       //   #472	-> 61
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	descriptor
/*      */       //   36	19	5	enumConstant	Ljava/lang/Object;
/*      */       //   0	77	0	this	Lac/grim/grimac/shaded/kyori/adventure/platform/bukkit/CraftBukkitFacet$PartialEntitySound;
/*      */       //   0	77	1	source	Lac/grim/grimac/shaded/kyori/adventure/sound/Sound$Source;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     Object createForEntity(Sound param1Sound, Entity param1Entity);
/*      */   }
/*      */ 
/*      */   
/*      */   static class EntitySound_1_19_3
/*      */     extends PacketFacet<Player>
/*      */     implements PartialEntitySound
/*      */   {
/*      */     public boolean isSupported() {
/*  482 */       return (CraftBukkitAccess.EntitySound_1_19_3.isSupported() && super.isSupported());
/*      */     }
/*      */ 
/*      */     
/*      */     public Object createForEntity(Sound sound, Entity entity) {
/*      */       try {
/*  488 */         Object soundEvent, resLoc = CraftBukkitAccess.NEW_RESOURCE_LOCATION.invoke(sound.name().namespace(), sound.name().value());
/*  489 */         Optional<?> possibleSoundEvent = CraftBukkitAccess.EntitySound_1_19_3.REGISTRY_GET_OPTIONAL.invoke(CraftBukkitAccess.EntitySound_1_19_3.SOUND_EVENT_REGISTRY, resLoc);
/*      */         
/*  491 */         if (possibleSoundEvent.isPresent()) {
/*  492 */           soundEvent = possibleSoundEvent.get();
/*      */         } else {
/*  494 */           soundEvent = CraftBukkitAccess.EntitySound_1_19_3.SOUND_EVENT_CREATE_VARIABLE_RANGE.invoke(resLoc);
/*      */         } 
/*  496 */         Object soundEventHolder = CraftBukkitAccess.EntitySound_1_19_3.REGISTRY_WRAP_AS_HOLDER.invoke(CraftBukkitAccess.EntitySound_1_19_3.SOUND_EVENT_REGISTRY, soundEvent);
/*  497 */         long seed = sound.seed().orElseGet(() -> ThreadLocalRandom.current().nextLong());
/*  498 */         return CraftBukkitAccess.EntitySound_1_19_3.NEW_CLIENTBOUND_ENTITY_SOUND.invoke(soundEventHolder, toVanilla(sound.source()), toNativeEntity(entity), sound.volume(), sound.pitch(), seed);
/*  499 */       } catch (Throwable error) {
/*  500 */         Knob.logError(error, "Failed to send sound tracking an entity", new Object[0]);
/*      */         
/*  502 */         return null;
/*      */       } 
/*      */     }
/*      */     
/*      */     public void playSound(@NotNull Player viewer, Object packet) {
/*  507 */       sendPacket(viewer, packet);
/*      */     }
/*      */   }
/*      */   
/*      */   static class EntitySound extends PacketFacet<Player> implements PartialEntitySound {
/*  512 */     private static final Class<?> CLASS_CLIENTBOUND_CUSTOM_SOUND = MinecraftReflection.findClass(new String[] {
/*  513 */           MinecraftReflection.findNmsClassName("PacketPlayOutCustomSoundEffect"), 
/*  514 */           MinecraftReflection.findMcClassName("network.protocol.game.ClientboundCustomSoundPacket"), 
/*  515 */           MinecraftReflection.findMcClassName("network.protocol.game.PacketPlayOutCustomSoundEffect")
/*      */         });
/*  517 */     private static final Class<?> CLASS_VEC3 = MinecraftReflection.findClass(new String[] {
/*  518 */           MinecraftReflection.findNmsClassName("Vec3D"), 
/*  519 */           MinecraftReflection.findMcClassName("world.phys.Vec3D"), 
/*  520 */           MinecraftReflection.findMcClassName("world.phys.Vec3")
/*      */         });
/*      */     
/*      */     private static final MethodHandle NEW_CLIENTBOUND_ENTITY_SOUND;
/*      */     private static final MethodHandle NEW_CLIENTBOUND_CUSTOM_SOUND;
/*  525 */     private static final MethodHandle NEW_VEC3 = MinecraftReflection.findConstructor(CLASS_VEC3, new Class[] { double.class, double.class, double.class });
/*  526 */     private static final MethodHandle NEW_RESOURCE_LOCATION = MinecraftReflection.findConstructor(CraftBukkitAccess.CLASS_RESOURCE_LOCATION, new Class[] { String.class, String.class });
/*  527 */     private static final MethodHandle REGISTRY_GET_OPTIONAL = MinecraftReflection.searchMethod(CraftBukkitAccess.CLASS_REGISTRY, Integer.valueOf(1), "getOptional", Optional.class, new Class[] { CraftBukkitAccess.CLASS_RESOURCE_LOCATION });
/*      */     
/*      */     private static final Object REGISTRY_SOUND_EVENT;
/*      */     
/*      */     static {
/*  532 */       MethodHandle entitySoundPacketConstructor = MinecraftReflection.findConstructor(CraftBukkitAccess.EntitySound.CLASS_CLIENTBOUND_ENTITY_SOUND, new Class[] { CraftBukkitAccess.EntitySound.CLASS_SOUND_EVENT, CraftBukkitAccess.EntitySound.CLASS_SOUND_SOURCE, CraftBukkitFacet.access$1100(), float.class, float.class, long.class });
/*  533 */       if (entitySoundPacketConstructor == null) {
/*      */         
/*  535 */         entitySoundPacketConstructor = MinecraftReflection.findConstructor(CraftBukkitAccess.EntitySound.CLASS_CLIENTBOUND_ENTITY_SOUND, new Class[] { CraftBukkitAccess.EntitySound.CLASS_SOUND_EVENT, CraftBukkitAccess.EntitySound.CLASS_SOUND_SOURCE, CraftBukkitFacet.access$1100(), float.class, float.class });
/*  536 */         if (entitySoundPacketConstructor != null) {
/*  537 */           entitySoundPacketConstructor = MethodHandles.dropArguments(entitySoundPacketConstructor, 5, new Class[] { long.class });
/*      */         }
/*      */       } 
/*  540 */       NEW_CLIENTBOUND_ENTITY_SOUND = entitySoundPacketConstructor;
/*      */ 
/*      */       
/*  543 */       MethodHandle customSoundPacketConstructor = MinecraftReflection.findConstructor(CLASS_CLIENTBOUND_CUSTOM_SOUND, new Class[] { CraftBukkitAccess.CLASS_RESOURCE_LOCATION, CraftBukkitAccess.EntitySound.CLASS_SOUND_SOURCE, CLASS_VEC3, float.class, float.class, long.class });
/*  544 */       if (customSoundPacketConstructor == null) {
/*      */         
/*  546 */         customSoundPacketConstructor = MinecraftReflection.findConstructor(CLASS_CLIENTBOUND_CUSTOM_SOUND, new Class[] { CraftBukkitAccess.CLASS_RESOURCE_LOCATION, CraftBukkitAccess.EntitySound.CLASS_SOUND_SOURCE, CLASS_VEC3, float.class, float.class });
/*  547 */         if (customSoundPacketConstructor != null) {
/*  548 */           customSoundPacketConstructor = MethodHandles.dropArguments(customSoundPacketConstructor, 5, new Class[] { long.class });
/*      */         }
/*      */       } 
/*  551 */       NEW_CLIENTBOUND_CUSTOM_SOUND = customSoundPacketConstructor;
/*      */       
/*  553 */       Object registrySoundEvent = null;
/*  554 */       if (CraftBukkitAccess.CLASS_REGISTRY != null) {
/*      */         
/*      */         try {
/*      */           
/*  558 */           Field soundEventField = MinecraftReflection.findField(CraftBukkitAccess.CLASS_REGISTRY, new String[] { "SOUND_EVENT" });
/*  559 */           if (soundEventField != null) {
/*  560 */             registrySoundEvent = soundEventField.get(null);
/*      */           
/*      */           }
/*      */           else {
/*      */ 
/*      */             
/*  566 */             Object rootRegistry = null;
/*  567 */             for (Field field : CraftBukkitAccess.CLASS_REGISTRY.getDeclaredFields()) {
/*  568 */               int mask = 28;
/*  569 */               if ((field.getModifiers() & 0x1C) == 28 && field
/*  570 */                 .getType().equals(CraftBukkitAccess.CLASS_WRITABLE_REGISTRY)) {
/*      */                 
/*  572 */                 field.setAccessible(true);
/*  573 */                 rootRegistry = field.get(null);
/*      */                 
/*      */                 break;
/*      */               } 
/*      */             } 
/*  578 */             if (rootRegistry != null) {
/*  579 */               registrySoundEvent = REGISTRY_GET_OPTIONAL.invoke(rootRegistry, NEW_RESOURCE_LOCATION.invoke("minecraft", "sound_event")).orElse(null);
/*      */             }
/*      */           } 
/*  582 */         } catch (Throwable thr) {
/*  583 */           Knob.logError(thr, "Failed to initialize EntitySound CraftBukkit facet", new Object[0]);
/*      */         } 
/*      */       }
/*  586 */       REGISTRY_SOUND_EVENT = registrySoundEvent;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isSupported() {
/*  591 */       return (super.isSupported() && NEW_CLIENTBOUND_ENTITY_SOUND != null && NEW_RESOURCE_LOCATION != null && REGISTRY_SOUND_EVENT != null && REGISTRY_GET_OPTIONAL != null && CraftBukkitFacet.CRAFT_ENTITY_GET_HANDLE != null && CraftBukkitAccess.EntitySound.isSupported());
/*      */     }
/*      */ 
/*      */     
/*      */     public Object createForEntity(Sound sound, Entity entity) {
/*      */       try {
/*  597 */         Object nmsEntity = toNativeEntity(entity);
/*  598 */         if (nmsEntity == null) return null;
/*      */         
/*  600 */         Object soundCategory = toVanilla(sound.source());
/*  601 */         if (soundCategory == null) return null; 
/*  602 */         Object nameRl = NEW_RESOURCE_LOCATION.invoke(sound.name().namespace(), sound.name().value());
/*  603 */         Optional<?> event = REGISTRY_GET_OPTIONAL.invoke(REGISTRY_SOUND_EVENT, nameRl);
/*  604 */         long seed = sound.seed().orElseGet(() -> ThreadLocalRandom.current().nextLong());
/*  605 */         if (event.isPresent())
/*  606 */           return NEW_CLIENTBOUND_ENTITY_SOUND.invoke(event.get(), soundCategory, nmsEntity, sound.volume(), sound.pitch(), seed); 
/*  607 */         if (NEW_CLIENTBOUND_CUSTOM_SOUND != null && NEW_VEC3 != null) {
/*  608 */           Location loc = entity.getLocation();
/*  609 */           return NEW_CLIENTBOUND_CUSTOM_SOUND.invoke(nameRl, soundCategory, NEW_VEC3.invoke(loc.getX(), loc.getY(), loc.getZ()), sound.volume(), sound.pitch(), seed);
/*      */         } 
/*  611 */       } catch (Throwable error) {
/*  612 */         Knob.logError(error, "Failed to send sound tracking an entity", new Object[0]);
/*      */       } 
/*  614 */       return null;
/*      */     }
/*      */ 
/*      */     
/*      */     public void playSound(@NotNull Player viewer, Object message) {
/*  619 */       sendPacket(viewer, message);
/*      */     }
/*      */   }
/*      */   
/*      */   static class Title_1_17
/*      */     extends PacketFacet<Player> implements Facet.Title<Player, Object, List<Object>, List<?>> {
/*  625 */     private static final Class<?> PACKET_SET_TITLE = MinecraftReflection.findMcClass(new String[] { "network.protocol.game.ClientboundSetTitleTextPacket" });
/*  626 */     private static final Class<?> PACKET_SET_SUBTITLE = MinecraftReflection.findMcClass(new String[] { "network.protocol.game.ClientboundSetSubtitleTextPacket" });
/*  627 */     private static final Class<?> PACKET_SET_TITLE_ANIMATION = MinecraftReflection.findMcClass(new String[] { "network.protocol.game.ClientboundSetTitlesAnimationPacket" });
/*  628 */     private static final Class<?> PACKET_CLEAR_TITLES = MinecraftReflection.findMcClass(new String[] { "network.protocol.game.ClientboundClearTitlesPacket" });
/*      */     
/*  630 */     private static final MethodHandle CONSTRUCTOR_SET_TITLE = MinecraftReflection.findConstructor(PACKET_SET_TITLE, new Class[] { CraftBukkitFacet.access$500() });
/*  631 */     private static final MethodHandle CONSTRUCTOR_SET_SUBTITLE = MinecraftReflection.findConstructor(PACKET_SET_SUBTITLE, new Class[] { CraftBukkitFacet.access$500() });
/*  632 */     private static final MethodHandle CONSTRUCTOR_SET_TITLE_ANIMATION = MinecraftReflection.findConstructor(PACKET_SET_TITLE_ANIMATION, new Class[] { int.class, int.class, int.class });
/*  633 */     private static final MethodHandle CONSTRUCTOR_CLEAR_TITLES = MinecraftReflection.findConstructor(PACKET_CLEAR_TITLES, new Class[] { boolean.class });
/*      */ 
/*      */     
/*      */     public boolean isSupported() {
/*  637 */       return (super.isSupported() && CONSTRUCTOR_SET_TITLE != null && CONSTRUCTOR_SET_SUBTITLE != null && CONSTRUCTOR_SET_TITLE_ANIMATION != null && CONSTRUCTOR_CLEAR_TITLES != null);
/*      */     }
/*      */     
/*      */     @NotNull
/*      */     public List<Object> createTitleCollection() {
/*  642 */       return new ArrayList();
/*      */     }
/*      */ 
/*      */     
/*      */     public void contributeTitle(@NotNull List<Object> coll, @NotNull Object title) {
/*      */       try {
/*  648 */         coll.add(CONSTRUCTOR_SET_TITLE.invoke(title));
/*  649 */       } catch (Throwable error) {
/*  650 */         Knob.logError(error, "Failed to invoke title packet constructor", new Object[0]);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void contributeSubtitle(@NotNull List<Object> coll, @NotNull Object subtitle) {
/*      */       try {
/*  657 */         coll.add(CONSTRUCTOR_SET_SUBTITLE.invoke(subtitle));
/*  658 */       } catch (Throwable error) {
/*  659 */         Knob.logError(error, "Failed to invoke subtitle packet constructor", new Object[0]);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void contributeTimes(@NotNull List<Object> coll, int inTicks, int stayTicks, int outTicks) {
/*      */       try {
/*  666 */         coll.add(CONSTRUCTOR_SET_TITLE_ANIMATION.invoke(inTicks, stayTicks, outTicks));
/*  667 */       } catch (Throwable error) {
/*  668 */         Knob.logError(error, "Failed to invoke title animations packet constructor", new Object[0]);
/*      */       } 
/*      */     }
/*      */     
/*      */     @Nullable
/*      */     public List<?> completeTitle(@NotNull List<Object> coll) {
/*  674 */       return coll;
/*      */     }
/*      */ 
/*      */     
/*      */     public void showTitle(@NotNull Player viewer, @NotNull List<?> packets) {
/*  679 */       for (Object packet : packets) {
/*  680 */         sendMessage(viewer, packet);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public void clearTitle(@NotNull Player viewer) {
/*      */       try {
/*  687 */         if (CONSTRUCTOR_CLEAR_TITLES != null) {
/*  688 */           sendPacket(viewer, CONSTRUCTOR_CLEAR_TITLES.invoke(false));
/*      */         } else {
/*  690 */           viewer.sendTitle("", "", -1, -1, -1);
/*      */         } 
/*  692 */       } catch (Throwable error) {
/*  693 */         Knob.logError(error, "Failed to clear title", new Object[0]);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void resetTitle(@NotNull Player viewer) {
/*      */       try {
/*  700 */         if (CONSTRUCTOR_CLEAR_TITLES != null) {
/*  701 */           sendPacket(viewer, CONSTRUCTOR_CLEAR_TITLES.invoke(true));
/*      */         } else {
/*  703 */           viewer.resetTitle();
/*      */         } 
/*  705 */       } catch (Throwable error) {
/*  706 */         Knob.logError(error, "Failed to clear title", new Object[0]);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   static class Title
/*      */     extends PacketFacet<Player> implements Facet.Title<Player, Object, List<Object>, List<?>> {
/*      */     public boolean isSupported() {
/*  714 */       return (super.isSupported() && CraftBukkitFacet.CONSTRUCTOR_TITLE_MESSAGE != null && CraftBukkitFacet.CONSTRUCTOR_TITLE_TIMES != null);
/*      */     }
/*      */     
/*      */     @NotNull
/*      */     public List<Object> createTitleCollection() {
/*  719 */       return new ArrayList();
/*      */     }
/*      */ 
/*      */     
/*      */     public void contributeTitle(@NotNull List<Object> coll, @NotNull Object title) {
/*      */       try {
/*  725 */         coll.add(CraftBukkitFacet.CONSTRUCTOR_TITLE_MESSAGE.invoke(CraftBukkitFacet.TITLE_ACTION_TITLE, title));
/*  726 */       } catch (Throwable error) {
/*  727 */         Knob.logError(error, "Failed to invoke title packet constructor", new Object[0]);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void contributeSubtitle(@NotNull List<Object> coll, @NotNull Object subtitle) {
/*      */       try {
/*  734 */         coll.add(CraftBukkitFacet.CONSTRUCTOR_TITLE_MESSAGE.invoke(CraftBukkitFacet.TITLE_ACTION_SUBTITLE, subtitle));
/*  735 */       } catch (Throwable error) {
/*  736 */         Knob.logError(error, "Failed to invoke subtitle packet constructor", new Object[0]);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void contributeTimes(@NotNull List<Object> coll, int inTicks, int stayTicks, int outTicks) {
/*      */       try {
/*  743 */         coll.add(CraftBukkitFacet.CONSTRUCTOR_TITLE_TIMES.invoke(inTicks, stayTicks, outTicks));
/*  744 */       } catch (Throwable error) {
/*  745 */         Knob.logError(error, "Failed to invoke title animations packet constructor", new Object[0]);
/*      */       } 
/*      */     }
/*      */     
/*      */     @Nullable
/*      */     public List<?> completeTitle(@NotNull List<Object> coll) {
/*  751 */       return coll;
/*      */     }
/*      */ 
/*      */     
/*      */     public void showTitle(@NotNull Player viewer, @NotNull List<?> packets) {
/*  756 */       for (Object packet : packets) {
/*  757 */         sendMessage(viewer, packet);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public void clearTitle(@NotNull Player viewer) {
/*      */       try {
/*  764 */         if (CraftBukkitFacet.TITLE_ACTION_CLEAR != null) {
/*  765 */           sendPacket(viewer, CraftBukkitFacet.CONSTRUCTOR_TITLE_MESSAGE.invoke(CraftBukkitFacet.TITLE_ACTION_CLEAR, null));
/*      */         } else {
/*  767 */           viewer.sendTitle("", "", -1, -1, -1);
/*      */         } 
/*  769 */       } catch (Throwable error) {
/*  770 */         Knob.logError(error, "Failed to clear title", new Object[0]);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void resetTitle(@NotNull Player viewer) {
/*      */       try {
/*  777 */         if (CraftBukkitFacet.TITLE_ACTION_RESET != null) {
/*  778 */           sendPacket(viewer, CraftBukkitFacet.CONSTRUCTOR_TITLE_MESSAGE.invoke(CraftBukkitFacet.TITLE_ACTION_RESET, null));
/*      */         } else {
/*  780 */           viewer.resetTitle();
/*      */         } 
/*  782 */       } catch (Throwable error) {
/*  783 */         Knob.logError(error, "Failed to clear title", new Object[0]);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   static final class Book_1_20_5
/*      */     extends PacketFacet<Player> implements Facet.Book<Player, Object, ItemStack> {
/*      */     public boolean isSupported() {
/*  791 */       return (super.isSupported() && CraftBukkitAccess.Book_1_20_5.isSupported());
/*      */     }
/*      */     
/*      */     @Nullable
/*      */     public ItemStack createBook(@NotNull String title, @NotNull String author, @NotNull Iterable<Object> pages) {
/*      */       try {
/*  797 */         ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
/*  798 */         List<Object> pageList = new ArrayList();
/*  799 */         for (Object page : pages) {
/*  800 */           pageList.add(CraftBukkitAccess.Book_1_20_5.CREATE_FILTERABLE.invoke(page));
/*      */         }
/*  802 */         Object bookContent = CraftBukkitAccess.Book_1_20_5.NEW_BOOK_CONTENT.invoke(CraftBukkitAccess.Book_1_20_5.CREATE_FILTERABLE.invoke(title), author, 0, pageList, true);
/*  803 */         Object stack = CraftBukkitAccess.Book_1_20_5.CRAFT_ITEMSTACK_NMS_COPY.invoke(item);
/*  804 */         CraftBukkitAccess.Book_1_20_5.MC_ITEMSTACK_SET.invoke(stack, CraftBukkitAccess.Book_1_20_5.WRITTEN_BOOK_COMPONENT_TYPE, bookContent);
/*  805 */         return CraftBukkitAccess.Book_1_20_5.CRAFT_ITEMSTACK_CRAFT_MIRROR.invoke(stack);
/*  806 */       } catch (Throwable error) {
/*  807 */         Knob.logError(error, "Failed to apply written_book_content component to ItemStack", new Object[0]);
/*      */         
/*  809 */         return null;
/*      */       } 
/*      */     }
/*      */     
/*      */     public void openBook(@NotNull Player viewer, @NotNull ItemStack book) {
/*  814 */       PlayerInventory inventory = viewer.getInventory();
/*  815 */       ItemStack current = inventory.getItemInHand();
/*      */       try {
/*  817 */         inventory.setItemInHand(book);
/*  818 */         sendMessage(viewer, CraftBukkitAccess.Book_1_20_5.NEW_PACKET_OPEN_BOOK.invoke(CraftBukkitAccess.Book_1_20_5.HAND_MAIN));
/*  819 */       } catch (Throwable error) {
/*  820 */         Knob.logError(error, "Failed to send openBook packet: %s", new Object[] { book });
/*      */       } finally {
/*  822 */         inventory.setItemInHand(current);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   protected static abstract class AbstractBook extends PacketFacet<Player> implements Facet.Book<Player, Object, ItemStack> {
/*      */     protected static final int HAND_MAIN = 0;
/*  829 */     private static final Material BOOK_TYPE = (Material)MinecraftReflection.findEnum(Material.class, "WRITTEN_BOOK");
/*  830 */     private static final ItemStack BOOK_STACK = (BOOK_TYPE == null) ? null : new ItemStack(BOOK_TYPE); private static final String BOOK_TITLE = "title";
/*      */     private static final String BOOK_AUTHOR = "author";
/*      */     private static final String BOOK_PAGES = "pages";
/*      */     private static final String BOOK_RESOLVED = "resolved";
/*      */     
/*      */     public boolean isSupported() {
/*  836 */       return (super.isSupported() && NBT_IO_DESERIALIZE != null && MC_ITEMSTACK_SET_TAG != null && CRAFT_ITEMSTACK_CRAFT_MIRROR != null && CRAFT_ITEMSTACK_NMS_COPY != null && BOOK_STACK != null);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @NotNull
/*      */     public String createMessage(@NotNull Player viewer, @NotNull Component message) {
/*  844 */       return (String)BukkitComponentSerializer.gson().serialize(message);
/*      */     }
/*      */ 
/*      */     
/*      */     @NotNull
/*      */     public ItemStack createBook(@NotNull String title, @NotNull String author, @NotNull Iterable<Object> pages) {
/*  850 */       return applyTag(BOOK_STACK, tagFor(title, author, pages));
/*      */     }
/*      */ 
/*      */     
/*      */     @Deprecated
/*      */     public void openBook(@NotNull Player viewer, @NotNull ItemStack book) {
/*  856 */       PlayerInventory inventory = viewer.getInventory();
/*  857 */       ItemStack current = inventory.getItemInHand();
/*      */       try {
/*  859 */         inventory.setItemInHand(book);
/*  860 */         sendOpenPacket(viewer);
/*  861 */       } catch (Throwable error) {
/*  862 */         Knob.logError(error, "Failed to send openBook packet: %s", new Object[] { book });
/*      */       } finally {
/*  864 */         inventory.setItemInHand(current);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static CompoundBinaryTag tagFor(@NotNull String title, @NotNull String author, @NotNull Iterable<Object> pages) {
/*  874 */       ListBinaryTag.Builder<StringBinaryTag> builder = ListBinaryTag.builder(BinaryTagTypes.STRING);
/*  875 */       for (Object page : pages) {
/*  876 */         builder.add((BinaryTag)StringBinaryTag.of((String)page));
/*      */       }
/*  878 */       return ((CompoundBinaryTag.Builder)((CompoundBinaryTag.Builder)((CompoundBinaryTag.Builder)((CompoundBinaryTag.Builder)CompoundBinaryTag.builder()
/*  879 */         .putString("title", title))
/*  880 */         .putString("author", author))
/*  881 */         .put("pages", (BinaryTag)builder.build()))
/*  882 */         .putByte("resolved", (byte)1))
/*  883 */         .build();
/*      */     }
/*      */     
/*  886 */     private static final Class<?> CLASS_NBT_TAG_COMPOUND = MinecraftReflection.findClass(new String[] {
/*  887 */           MinecraftReflection.findNmsClassName("NBTTagCompound"), 
/*  888 */           MinecraftReflection.findMcClassName("nbt.CompoundTag"), 
/*  889 */           MinecraftReflection.findMcClassName("nbt.NBTTagCompound")
/*      */         });
/*  891 */     private static final Class<?> CLASS_NBT_IO = MinecraftReflection.findClass(new String[] {
/*  892 */           MinecraftReflection.findNmsClassName("NBTCompressedStreamTools"), 
/*  893 */           MinecraftReflection.findMcClassName("nbt.NbtIo"), 
/*  894 */           MinecraftReflection.findMcClassName("nbt.NBTCompressedStreamTools")
/*      */         });
/*      */     private static final MethodHandle NBT_IO_DESERIALIZE;
/*      */     
/*      */     static {
/*  899 */       MethodHandle nbtIoDeserialize = null;
/*      */       
/*  901 */       if (CLASS_NBT_IO != null)
/*      */       {
/*  903 */         for (Method method : CLASS_NBT_IO.getDeclaredMethods()) {
/*  904 */           if (Modifier.isStatic(method.getModifiers()) && method
/*  905 */             .getReturnType().equals(CLASS_NBT_TAG_COMPOUND) && method
/*  906 */             .getParameterCount() == 1) {
/*  907 */             Class<?> firstParam = method.getParameterTypes()[0];
/*  908 */             if (firstParam.equals(DataInputStream.class) || firstParam.equals(DataInput.class)) {
/*      */               try {
/*  910 */                 nbtIoDeserialize = MinecraftReflection.lookup().unreflect(method);
/*  911 */               } catch (IllegalAccessException illegalAccessException) {}
/*      */               
/*      */               break;
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       }
/*      */       
/*  919 */       NBT_IO_DESERIALIZE = nbtIoDeserialize;
/*      */     }
/*      */     private static final class TrustedByteArrayOutputStream extends ByteArrayOutputStream { private TrustedByteArrayOutputStream() {}
/*      */       
/*      */       public InputStream toInputStream() {
/*  924 */         return new ByteArrayInputStream(this.buf, 0, this.count);
/*      */       } }
/*      */     
/*      */     @NotNull
/*      */     private Object createTag(@NotNull CompoundBinaryTag tag) throws IOException {
/*  929 */       TrustedByteArrayOutputStream output = new TrustedByteArrayOutputStream();
/*  930 */       BinaryTagIO.writer().write(tag, output);
/*      */       
/*  932 */       try { DataInputStream dis = new DataInputStream(output.toInputStream()); 
/*  933 */         try { Object object = NBT_IO_DESERIALIZE.invoke(dis);
/*  934 */           dis.close(); return object; } catch (Throwable throwable) { try { dis.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  } catch (Throwable err)
/*  935 */       { throw new IOException(err); }
/*      */     
/*      */     }
/*      */     
/*  939 */     private static final Class<?> CLASS_CRAFT_ITEMSTACK = MinecraftReflection.findCraftClass("inventory.CraftItemStack");
/*  940 */     private static final Class<?> CLASS_MC_ITEMSTACK = MinecraftReflection.findClass(new String[] {
/*  941 */           MinecraftReflection.findNmsClassName("ItemStack"), 
/*  942 */           MinecraftReflection.findMcClassName("world.item.ItemStack")
/*      */         });
/*      */     
/*  945 */     private static final MethodHandle MC_ITEMSTACK_SET_TAG = MinecraftReflection.searchMethod(CLASS_MC_ITEMSTACK, Integer.valueOf(1), "setTag", void.class, new Class[] { CLASS_NBT_TAG_COMPOUND });
/*      */     
/*  947 */     private static final MethodHandle CRAFT_ITEMSTACK_NMS_COPY = MinecraftReflection.findStaticMethod(CLASS_CRAFT_ITEMSTACK, "asNMSCopy", CLASS_MC_ITEMSTACK, new Class[] { ItemStack.class });
/*  948 */     private static final MethodHandle CRAFT_ITEMSTACK_CRAFT_MIRROR = MinecraftReflection.findStaticMethod(CLASS_CRAFT_ITEMSTACK, "asCraftMirror", CLASS_CRAFT_ITEMSTACK, new Class[] { CLASS_MC_ITEMSTACK });
/*      */     
/*      */     private ItemStack applyTag(@NotNull ItemStack input, CompoundBinaryTag binTag) {
/*  951 */       if (CRAFT_ITEMSTACK_NMS_COPY == null || MC_ITEMSTACK_SET_TAG == null || CRAFT_ITEMSTACK_CRAFT_MIRROR == null) {
/*  952 */         return input;
/*      */       }
/*      */       try {
/*  955 */         Object stack = CRAFT_ITEMSTACK_NMS_COPY.invoke(input);
/*  956 */         Object tag = createTag(binTag);
/*      */         
/*  958 */         MC_ITEMSTACK_SET_TAG.invoke(stack, tag);
/*  959 */         return CRAFT_ITEMSTACK_CRAFT_MIRROR.invoke(stack);
/*  960 */       } catch (Throwable error) {
/*  961 */         Knob.logError(error, "Failed to apply NBT tag to ItemStack: %s %s", new Object[] { input, binTag });
/*  962 */         return input;
/*      */       } 
/*      */     }
/*      */     
/*      */     protected abstract void sendOpenPacket(@NotNull Player param1Player) throws Throwable; }
/*      */   
/*  968 */   static final class BookPost1_13 extends AbstractBook { private static final Class<?> CLASS_ENUM_HAND = MinecraftReflection.findClass(new String[] {
/*  969 */           MinecraftReflection.findNmsClassName("EnumHand"), 
/*  970 */           MinecraftReflection.findMcClassName("world.EnumHand"), 
/*  971 */           MinecraftReflection.findMcClassName("world.InteractionHand")
/*      */         });
/*  973 */     private static final Object HAND_MAIN = MinecraftReflection.findEnum(CLASS_ENUM_HAND, "MAIN_HAND", 0);
/*  974 */     private static final Class<?> PACKET_OPEN_BOOK = MinecraftReflection.findClass(new String[] {
/*  975 */           MinecraftReflection.findNmsClassName("PacketPlayOutOpenBook"), 
/*  976 */           MinecraftReflection.findMcClassName("network.protocol.game.PacketPlayOutOpenBook"), 
/*  977 */           MinecraftReflection.findMcClassName("network.protocol.game.ClientboundOpenBookPacket")
/*      */         });
/*  979 */     private static final MethodHandle NEW_PACKET_OPEN_BOOK = MinecraftReflection.findConstructor(PACKET_OPEN_BOOK, new Class[] { CLASS_ENUM_HAND });
/*      */ 
/*      */     
/*      */     public boolean isSupported() {
/*  983 */       return (super.isSupported() && HAND_MAIN != null && NEW_PACKET_OPEN_BOOK != null);
/*      */     }
/*      */ 
/*      */     
/*      */     protected void sendOpenPacket(@NotNull Player viewer) throws Throwable {
/*  988 */       sendMessage(viewer, NEW_PACKET_OPEN_BOOK.invoke(HAND_MAIN));
/*      */     } }
/*      */ 
/*      */   
/*      */   static final class Book1_13 extends AbstractBook {
/*  993 */     private static final Class<?> CLASS_BYTE_BUF = MinecraftReflection.findClass(new String[] { "io.netty.buffer.ByteBuf" });
/*  994 */     private static final Class<?> CLASS_PACKET_CUSTOM_PAYLOAD = MinecraftReflection.findNmsClass("PacketPlayOutCustomPayload");
/*  995 */     private static final Class<?> CLASS_FRIENDLY_BYTE_BUF = MinecraftReflection.findNmsClass("PacketDataSerializer");
/*  996 */     private static final Class<?> CLASS_RESOURCE_LOCATION = MinecraftReflection.findNmsClass("MinecraftKey");
/*      */     
/*      */     private static final Object PACKET_TYPE_BOOK_OPEN;
/*  999 */     private static final MethodHandle NEW_PACKET_CUSTOM_PAYLOAD = MinecraftReflection.findConstructor(CLASS_PACKET_CUSTOM_PAYLOAD, new Class[] { CLASS_RESOURCE_LOCATION, CLASS_FRIENDLY_BYTE_BUF });
/* 1000 */     private static final MethodHandle NEW_FRIENDLY_BYTE_BUF = MinecraftReflection.findConstructor(CLASS_FRIENDLY_BYTE_BUF, new Class[] { CLASS_BYTE_BUF });
/*      */     
/*      */     static {
/* 1003 */       Object packetType = null;
/* 1004 */       if (CLASS_RESOURCE_LOCATION != null) {
/*      */         try {
/* 1006 */           packetType = CLASS_RESOURCE_LOCATION.getConstructor(new Class[] { String.class }).newInstance(new Object[] { "minecraft:book_open" });
/* 1007 */         } catch (InstantiationException|IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchMethodException instantiationException) {}
/*      */       }
/*      */ 
/*      */       
/* 1011 */       PACKET_TYPE_BOOK_OPEN = packetType;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isSupported() {
/* 1016 */       return (super.isSupported() && CLASS_BYTE_BUF != null && NEW_PACKET_CUSTOM_PAYLOAD != null && PACKET_TYPE_BOOK_OPEN != null);
/*      */     }
/*      */ 
/*      */     
/*      */     protected void sendOpenPacket(@NotNull Player viewer) throws Throwable {
/* 1021 */       ByteBuf data = Unpooled.buffer();
/* 1022 */       data.writeByte(0);
/* 1023 */       Object packetByteBuf = NEW_FRIENDLY_BYTE_BUF.invoke(data);
/* 1024 */       sendMessage(viewer, NEW_PACKET_CUSTOM_PAYLOAD.invoke(PACKET_TYPE_BOOK_OPEN, packetByteBuf));
/*      */     }
/*      */   }
/*      */   
/*      */   static final class BookPre1_13
/*      */     extends AbstractBook {
/*      */     private static final String PACKET_TYPE_BOOK_OPEN = "MC|BOpen";
/* 1031 */     private static final Class<?> CLASS_BYTE_BUF = MinecraftReflection.findClass(new String[] { "io.netty.buffer.ByteBuf" });
/* 1032 */     private static final Class<?> CLASS_PACKET_CUSTOM_PAYLOAD = MinecraftReflection.findNmsClass("PacketPlayOutCustomPayload");
/* 1033 */     private static final Class<?> CLASS_PACKET_DATA_SERIALIZER = MinecraftReflection.findNmsClass("PacketDataSerializer");
/*      */     
/* 1035 */     private static final MethodHandle NEW_PACKET_CUSTOM_PAYLOAD = MinecraftReflection.findConstructor(CLASS_PACKET_CUSTOM_PAYLOAD, new Class[] { String.class, CLASS_PACKET_DATA_SERIALIZER });
/* 1036 */     private static final MethodHandle NEW_PACKET_BYTE_BUF = MinecraftReflection.findConstructor(CLASS_PACKET_DATA_SERIALIZER, new Class[] { CLASS_BYTE_BUF });
/*      */ 
/*      */     
/*      */     public boolean isSupported() {
/* 1040 */       return (super.isSupported() && CLASS_BYTE_BUF != null && CLASS_PACKET_CUSTOM_PAYLOAD != null && NEW_PACKET_CUSTOM_PAYLOAD != null);
/*      */     }
/*      */ 
/*      */     
/*      */     protected void sendOpenPacket(@NotNull Player viewer) throws Throwable {
/* 1045 */       ByteBuf data = Unpooled.buffer();
/* 1046 */       data.writeByte(0);
/* 1047 */       Object packetByteBuf = NEW_PACKET_BYTE_BUF.invoke(data);
/* 1048 */       sendMessage(viewer, NEW_PACKET_CUSTOM_PAYLOAD.invoke("MC|BOpen", packetByteBuf));
/*      */     }
/*      */   }
/*      */   
/*      */   static final class BossBar extends BukkitFacet.BossBar {
/* 1053 */     private static final Class<?> CLASS_CRAFT_BOSS_BAR = MinecraftReflection.findCraftClass("boss.CraftBossBar");
/*      */     private static final Class<?> CLASS_BOSS_BAR_ACTION;
/*      */     private static final Object BOSS_BAR_ACTION_TITLE;
/*      */     private static final MethodHandle CRAFT_BOSS_BAR_HANDLE;
/*      */     private static final MethodHandle NMS_BOSS_BATTLE_SET_NAME;
/*      */     private static final MethodHandle NMS_BOSS_BATTLE_SEND_UPDATE;
/*      */     
/*      */     static {
/* 1061 */       Class<?> classBossBarAction = null;
/* 1062 */       Object bossBarActionTitle = null;
/* 1063 */       classBossBarAction = MinecraftReflection.findClass(new String[] {
/* 1064 */             MinecraftReflection.findNmsClassName("PacketPlayOutBoss$Action"), 
/* 1065 */             MinecraftReflection.findMcClassName("network.protocol.game.PacketPlayOutBoss$Action"), 
/* 1066 */             MinecraftReflection.findMcClassName("network.protocol.game.ClientboundBossEventPacket$Operation")
/*      */           });
/* 1068 */       if (classBossBarAction == null || !classBossBarAction.isEnum()) {
/* 1069 */         classBossBarAction = null;
/* 1070 */         Class<?> packetClass = MinecraftReflection.findClass(new String[] {
/* 1071 */               MinecraftReflection.findNmsClassName("PacketPlayOutBoss"), 
/* 1072 */               MinecraftReflection.findMcClassName("network.protocol.game.PacketPlayOutBoss"), 
/* 1073 */               MinecraftReflection.findMcClassName("network.protocol.game.ClientboundBossEventPacket")
/*      */             });
/* 1075 */         Class<?> bossEventClass = MinecraftReflection.findClass(new String[] {
/* 1076 */               MinecraftReflection.findNmsClassName("BossBattle"), 
/* 1077 */               MinecraftReflection.findMcClassName("world.BossBattle"), 
/* 1078 */               MinecraftReflection.findMcClassName("world.BossEvent")
/*      */             });
/* 1080 */         if (packetClass != null && bossEventClass != null) {
/*      */           try {
/* 1082 */             String methodName; MethodType methodType = MethodType.methodType(packetClass, bossEventClass);
/*      */             
/*      */             try {
/* 1085 */               packetClass.getDeclaredMethod("createUpdateNamePacket", new Class[] { bossEventClass });
/* 1086 */               methodName = "createUpdateNamePacket";
/* 1087 */             } catch (NoSuchMethodException ignored) {
/* 1088 */               methodName = "c";
/*      */             } 
/* 1090 */             MethodHandle factoryMethod = MinecraftReflection.lookup().findStatic(packetClass, methodName, methodType);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1098 */             bossBarActionTitle = LambdaMetafactory.metafactory(MinecraftReflection.lookup(), "apply", MethodType.methodType(Function.class), methodType.generic(), factoryMethod, methodType).getTarget().invoke();
/* 1099 */             classBossBarAction = Function.class;
/* 1100 */           } catch (Throwable error) {
/* 1101 */             Knob.logError(error, "Failed to initialize CraftBossBar constructor", new Object[0]);
/*      */           } 
/*      */         }
/*      */       } else {
/* 1105 */         bossBarActionTitle = MinecraftReflection.findEnum(classBossBarAction, "UPDATE_NAME", 3);
/*      */       } 
/*      */       
/* 1108 */       CLASS_BOSS_BAR_ACTION = classBossBarAction;
/* 1109 */       BOSS_BAR_ACTION_TITLE = bossBarActionTitle;
/*      */       
/* 1111 */       MethodHandle craftBossBarHandle = null;
/* 1112 */       MethodHandle nmsBossBattleSetName = null;
/* 1113 */       MethodHandle nmsBossBattleSendUpdate = null;
/*      */       
/* 1115 */       if (CLASS_CRAFT_BOSS_BAR != null && CraftBukkitFacet.CLASS_CHAT_COMPONENT != null && BOSS_BAR_ACTION_TITLE != null) {
/*      */         try {
/* 1117 */           Field craftBossBarHandleField = MinecraftReflection.needField(CLASS_CRAFT_BOSS_BAR, "handle");
/* 1118 */           craftBossBarHandle = MinecraftReflection.lookup().unreflectGetter(craftBossBarHandleField);
/* 1119 */           Class<?> nmsBossBattleType = craftBossBarHandleField.getType();
/* 1120 */           for (Field field : nmsBossBattleType.getFields()) {
/* 1121 */             if (field.getType().equals(CraftBukkitFacet.CLASS_CHAT_COMPONENT)) {
/* 1122 */               nmsBossBattleSetName = MinecraftReflection.lookup().unreflectSetter(field);
/*      */               break;
/*      */             } 
/*      */           } 
/* 1126 */           nmsBossBattleSendUpdate = MinecraftReflection.findMethod(nmsBossBattleType, new String[] { "sendUpdate", "a", "broadcast" }, void.class, new Class[] { CLASS_BOSS_BAR_ACTION });
/* 1127 */         } catch (Throwable error) {
/* 1128 */           Knob.logError(error, "Failed to initialize CraftBossBar constructor", new Object[0]);
/*      */         } 
/*      */       }
/*      */       
/* 1132 */       CRAFT_BOSS_BAR_HANDLE = craftBossBarHandle;
/* 1133 */       NMS_BOSS_BATTLE_SET_NAME = nmsBossBattleSetName;
/* 1134 */       NMS_BOSS_BATTLE_SEND_UPDATE = nmsBossBattleSendUpdate;
/*      */     }
/*      */     
/*      */     public static class Builder extends CraftBukkitFacet<Player> implements Facet.BossBar.Builder<Player, BossBar> {
/*      */       protected Builder() {
/* 1139 */         super(Player.class);
/*      */       }
/*      */ 
/*      */       
/*      */       public boolean isSupported() {
/* 1144 */         return (super.isSupported() && CraftBukkitFacet.BossBar
/* 1145 */           .CLASS_CRAFT_BOSS_BAR != null && CraftBukkitFacet.BossBar.CRAFT_BOSS_BAR_HANDLE != null && CraftBukkitFacet.BossBar.NMS_BOSS_BATTLE_SET_NAME != null && CraftBukkitFacet.BossBar.NMS_BOSS_BATTLE_SEND_UPDATE != null);
/*      */       }
/*      */ 
/*      */       
/*      */       public CraftBukkitFacet.BossBar createBossBar(@NotNull Collection<Player> viewers) {
/* 1150 */         return new CraftBukkitFacet.BossBar(viewers);
/*      */       }
/*      */     }
/*      */     
/*      */     private BossBar(@NotNull Collection<Player> viewers) {
/* 1155 */       super(viewers);
/*      */     }
/*      */ 
/*      */     
/*      */     public void bossBarNameChanged(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar bar, @NotNull Component oldName, @NotNull Component newName) {
/*      */       try {
/* 1161 */         Object handle = CRAFT_BOSS_BAR_HANDLE.invoke(this.bar);
/* 1162 */         Object text = MinecraftComponentSerializer.get().serialize(newName);
/*      */         
/* 1164 */         NMS_BOSS_BATTLE_SET_NAME.invoke(handle, text);
/* 1165 */         NMS_BOSS_BATTLE_SEND_UPDATE.invoke(handle, BOSS_BAR_ACTION_TITLE);
/* 1166 */       } catch (Throwable error) {
/* 1167 */         Knob.logError(error, "Failed to set CraftBossBar name: %s %s", new Object[] { this.bar, newName });
/* 1168 */         super.bossBarNameChanged(bar, oldName, newName);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   static class FakeEntity<E extends Entity> extends PacketFacet<Player> implements Facet.FakeEntity<Player, Location>, Listener {
/* 1174 */     private static final Class<? extends World> CLASS_CRAFT_WORLD = MinecraftReflection.findCraftClass("CraftWorld", World.class);
/* 1175 */     private static final Class<?> CLASS_NMS_LIVING_ENTITY = MinecraftReflection.findNmsClass("EntityLiving");
/* 1176 */     private static final Class<?> CLASS_DATA_WATCHER = MinecraftReflection.findNmsClass("DataWatcher");
/*      */     
/* 1178 */     private static final MethodHandle CRAFT_WORLD_CREATE_ENTITY = MinecraftReflection.findMethod(CLASS_CRAFT_WORLD, "createEntity", CraftBukkitFacet.CLASS_NMS_ENTITY, new Class[] { Location.class, Class.class });
/* 1179 */     private static final MethodHandle NMS_ENTITY_GET_BUKKIT_ENTITY = MinecraftReflection.findMethod(CraftBukkitFacet.CLASS_NMS_ENTITY, "getBukkitEntity", CraftBukkitFacet.CLASS_CRAFT_ENTITY, new Class[0]);
/* 1180 */     private static final MethodHandle NMS_ENTITY_GET_DATA_WATCHER = MinecraftReflection.findMethod(CraftBukkitFacet.CLASS_NMS_ENTITY, "getDataWatcher", CLASS_DATA_WATCHER, new Class[0]);
/* 1181 */     private static final MethodHandle NMS_ENTITY_SET_LOCATION = MinecraftReflection.findMethod(CraftBukkitFacet.CLASS_NMS_ENTITY, "setLocation", void.class, new Class[] { double.class, double.class, double.class, float.class, float.class });
/* 1182 */     private static final MethodHandle NMS_ENTITY_SET_INVISIBLE = MinecraftReflection.findMethod(CraftBukkitFacet.CLASS_NMS_ENTITY, "setInvisible", void.class, new Class[] { boolean.class });
/* 1183 */     private static final MethodHandle DATA_WATCHER_WATCH = MinecraftReflection.findMethod(CLASS_DATA_WATCHER, "watch", void.class, new Class[] { int.class, Object.class });
/*      */     
/* 1185 */     private static final Class<?> CLASS_SPAWN_LIVING_PACKET = MinecraftReflection.findNmsClass("PacketPlayOutSpawnEntityLiving");
/* 1186 */     private static final MethodHandle NEW_SPAWN_LIVING_PACKET = MinecraftReflection.findConstructor(CLASS_SPAWN_LIVING_PACKET, new Class[] { CLASS_NMS_LIVING_ENTITY });
/* 1187 */     private static final Class<?> CLASS_ENTITY_DESTROY_PACKET = MinecraftReflection.findNmsClass("PacketPlayOutEntityDestroy");
/* 1188 */     private static final MethodHandle NEW_ENTITY_DESTROY_PACKET = MinecraftReflection.findConstructor(CLASS_ENTITY_DESTROY_PACKET, new Class[] { int[].class });
/* 1189 */     private static final Class<?> CLASS_ENTITY_METADATA_PACKET = MinecraftReflection.findNmsClass("PacketPlayOutEntityMetadata");
/* 1190 */     private static final MethodHandle NEW_ENTITY_METADATA_PACKET = MinecraftReflection.findConstructor(CLASS_ENTITY_METADATA_PACKET, new Class[] { int.class, CLASS_DATA_WATCHER, boolean.class });
/* 1191 */     private static final Class<?> CLASS_ENTITY_TELEPORT_PACKET = MinecraftReflection.findNmsClass("PacketPlayOutEntityTeleport");
/* 1192 */     private static final MethodHandle NEW_ENTITY_TELEPORT_PACKET = MinecraftReflection.findConstructor(CLASS_ENTITY_TELEPORT_PACKET, new Class[] { CraftBukkitFacet.access$1100() });
/*      */     
/* 1194 */     private static final Class<?> CLASS_ENTITY_WITHER = MinecraftReflection.findNmsClass("EntityWither");
/* 1195 */     private static final Class<?> CLASS_WORLD = MinecraftReflection.findNmsClass("World");
/* 1196 */     private static final Class<?> CLASS_WORLD_SERVER = MinecraftReflection.findNmsClass("WorldServer");
/* 1197 */     private static final MethodHandle CRAFT_WORLD_GET_HANDLE = MinecraftReflection.findMethod(CLASS_CRAFT_WORLD, "getHandle", CLASS_WORLD_SERVER, new Class[0]);
/* 1198 */     private static final MethodHandle NEW_ENTITY_WITHER = MinecraftReflection.findConstructor(CLASS_ENTITY_WITHER, new Class[] { CLASS_WORLD });
/*      */     
/* 1200 */     private static final boolean SUPPORTED = ((CRAFT_WORLD_CREATE_ENTITY != null || (NEW_ENTITY_WITHER != null && CRAFT_WORLD_GET_HANDLE != null)) && CraftBukkitFacet
/* 1201 */       .CRAFT_ENTITY_GET_HANDLE != null && NMS_ENTITY_GET_BUKKIT_ENTITY != null && NMS_ENTITY_GET_DATA_WATCHER != null);
/*      */     
/*      */     private final E entity;
/*      */     private final Object entityHandle;
/*      */     protected final Set<Player> viewers;
/*      */     
/*      */     protected FakeEntity(@NotNull Class<E> entityClass, @NotNull Location location) {
/* 1208 */       this(BukkitAudience.PLUGIN.get(), entityClass, location);
/*      */     }
/*      */     
/*      */     protected FakeEntity(@NotNull Plugin plugin, @NotNull Class<E> entityClass, @NotNull Location location) {
/*      */       Entity entity1;
/* 1213 */       E entity = null;
/* 1214 */       Object handle = null;
/*      */       
/* 1216 */       if (SUPPORTED) {
/*      */         try {
/* 1218 */           if (CRAFT_WORLD_CREATE_ENTITY != null) {
/* 1219 */             Object nmsEntity = CRAFT_WORLD_CREATE_ENTITY.invoke(location.getWorld(), location, entityClass);
/* 1220 */             entity1 = NMS_ENTITY_GET_BUKKIT_ENTITY.invoke(nmsEntity);
/* 1221 */           } else if (Wither.class.isAssignableFrom(entityClass) && NEW_ENTITY_WITHER != null) {
/* 1222 */             Object nmsEntity = NEW_ENTITY_WITHER.invoke(CRAFT_WORLD_GET_HANDLE.invoke(location.getWorld()));
/* 1223 */             entity1 = NMS_ENTITY_GET_BUKKIT_ENTITY.invoke(nmsEntity);
/*      */           } 
/* 1225 */           if (CraftBukkitFacet.CLASS_CRAFT_ENTITY.isInstance(entity1)) {
/* 1226 */             handle = CraftBukkitFacet.CRAFT_ENTITY_GET_HANDLE.invoke(entity1);
/*      */           }
/* 1228 */         } catch (Throwable error) {
/* 1229 */           Knob.logError(error, "Failed to create fake entity: %s", new Object[] { entityClass.getSimpleName() });
/*      */         } 
/*      */       }
/*      */       
/* 1233 */       this.entity = (E)entity1;
/* 1234 */       this.entityHandle = handle;
/* 1235 */       this.viewers = new HashSet<>();
/*      */       
/* 1237 */       if (isSupported()) {
/* 1238 */         plugin.getServer().getPluginManager().registerEvents(this, plugin);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isSupported() {
/* 1244 */       return (super.isSupported() && this.entity != null && this.entityHandle != null);
/*      */     }
/*      */     
/*      */     @EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
/*      */     public void onPlayerMove(PlayerMoveEvent event) {
/* 1249 */       Player viewer = event.getPlayer();
/* 1250 */       if (this.viewers.contains(viewer)) {
/* 1251 */         teleport(viewer, createPosition(viewer));
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     @Nullable
/*      */     public Object createSpawnPacket() {
/* 1258 */       if (this.entity instanceof org.bukkit.entity.LivingEntity) {
/*      */         try {
/* 1260 */           return NEW_SPAWN_LIVING_PACKET.invoke(this.entityHandle);
/* 1261 */         } catch (Throwable error) {
/* 1262 */           Knob.logError(error, "Failed to create spawn packet: %s", new Object[] { this.entity });
/*      */         } 
/*      */       }
/* 1265 */       return null;
/*      */     }
/*      */     @Nullable
/*      */     public Object createDespawnPacket() {
/*      */       try {
/* 1270 */         return NEW_ENTITY_DESTROY_PACKET.invoke(this.entity.getEntityId());
/* 1271 */       } catch (Throwable error) {
/* 1272 */         Knob.logError(error, "Failed to create despawn packet: %s", new Object[] { this.entity });
/* 1273 */         return null;
/*      */       } 
/*      */     }
/*      */     @Nullable
/*      */     public Object createMetadataPacket() {
/*      */       try {
/* 1279 */         Object dataWatcher = NMS_ENTITY_GET_DATA_WATCHER.invoke(this.entityHandle);
/* 1280 */         return NEW_ENTITY_METADATA_PACKET.invoke(this.entity.getEntityId(), dataWatcher, false);
/* 1281 */       } catch (Throwable error) {
/* 1282 */         Knob.logError(error, "Failed to create update metadata packet: %s", new Object[] { this.entity });
/* 1283 */         return null;
/*      */       } 
/*      */     }
/*      */     @Nullable
/*      */     public Object createLocationPacket() {
/*      */       try {
/* 1289 */         return NEW_ENTITY_TELEPORT_PACKET.invoke(this.entityHandle);
/* 1290 */       } catch (Throwable error) {
/* 1291 */         Knob.logError(error, "Failed to create teleport packet: %s", new Object[] { this.entity });
/* 1292 */         return null;
/*      */       } 
/*      */     }
/*      */     
/*      */     public void broadcastPacket(@Nullable Object packet) {
/* 1297 */       for (Player viewer : this.viewers) {
/* 1298 */         sendPacket(viewer, packet);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     @NotNull
/*      */     public Location createPosition(@NotNull Player viewer) {
/* 1305 */       return viewer.getLocation();
/*      */     }
/*      */ 
/*      */     
/*      */     @NotNull
/*      */     public Location createPosition(double x, double y, double z) {
/* 1311 */       return new Location(null, x, y, z);
/*      */     }
/*      */ 
/*      */     
/*      */     public void teleport(@NotNull Player viewer, @Nullable Location position) {
/* 1316 */       if (position == null) {
/* 1317 */         this.viewers.remove(viewer);
/* 1318 */         sendPacket(viewer, createDespawnPacket());
/*      */         
/*      */         return;
/*      */       } 
/* 1322 */       if (!this.viewers.contains(viewer)) {
/* 1323 */         sendPacket(viewer, createSpawnPacket());
/* 1324 */         this.viewers.add(viewer);
/*      */       } 
/*      */       
/*      */       try {
/* 1328 */         NMS_ENTITY_SET_LOCATION.invoke(this.entityHandle, position.getX(), position.getY(), position.getZ(), position.getPitch(), position.getYaw());
/* 1329 */       } catch (Throwable error) {
/* 1330 */         Knob.logError(error, "Failed to set entity location: %s %s", new Object[] { this.entity, position });
/*      */       } 
/* 1332 */       sendPacket(viewer, createLocationPacket());
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void metadata(int position, @NotNull Object data) {
/* 1338 */       if (DATA_WATCHER_WATCH != null) {
/*      */         try {
/* 1340 */           Object dataWatcher = NMS_ENTITY_GET_DATA_WATCHER.invoke(this.entityHandle);
/* 1341 */           DATA_WATCHER_WATCH.invoke(dataWatcher, position, data);
/* 1342 */         } catch (Throwable error) {
/* 1343 */           Knob.logError(error, "Failed to set entity metadata: %s %s=%s", new Object[] { this.entity, Integer.valueOf(position), data });
/*      */         } 
/* 1345 */         broadcastPacket(createMetadataPacket());
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void invisible(boolean invisible) {
/* 1351 */       if (NMS_ENTITY_SET_INVISIBLE != null) {
/*      */         try {
/* 1353 */           NMS_ENTITY_SET_INVISIBLE.invoke(this.entityHandle, invisible);
/* 1354 */         } catch (Throwable error) {
/* 1355 */           Knob.logError(error, "Failed to change entity visibility: %s", new Object[] { this.entity });
/*      */         } 
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     @Deprecated
/*      */     public void health(float health) {
/* 1363 */       if (this.entity instanceof Damageable) {
/* 1364 */         Damageable entity = (Damageable)this.entity;
/* 1365 */         entity.setHealth(health * (entity.getMaxHealth() - 0.10000000149011612D) + 0.10000000149011612D);
/* 1366 */         broadcastPacket(createMetadataPacket());
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void name(@NotNull Component name) {
/* 1372 */       this.entity.setCustomName(BukkitComponentSerializer.legacy().serialize(name));
/* 1373 */       broadcastPacket(createMetadataPacket());
/*      */     }
/*      */ 
/*      */     
/*      */     public void close() {
/* 1378 */       HandlerList.unregisterAll(this);
/* 1379 */       for (Player viewer : new LinkedList(this.viewers))
/* 1380 */         teleport(viewer, (Location)null); 
/*      */     }
/*      */   }
/*      */   
/*      */   static final class BossBarWither
/*      */     extends FakeEntity<Wither> implements Facet.BossBarEntity<Player, Location> {
/*      */     public static class Builder extends CraftBukkitFacet<Player> implements Facet.BossBar.Builder<Player, BossBarWither> {
/*      */       protected Builder() {
/* 1388 */         super(Player.class);
/*      */       }
/*      */ 
/*      */       
/*      */       @NotNull
/*      */       public CraftBukkitFacet.BossBarWither createBossBar(@NotNull Collection<Player> viewers) {
/* 1394 */         return new CraftBukkitFacet.BossBarWither(viewers);
/*      */       }
/*      */     }
/*      */     
/*      */     private volatile boolean initialized = false;
/*      */     
/*      */     private BossBarWither(@NotNull Collection<Player> viewers) {
/* 1401 */       super(Wither.class, ((Player)viewers.iterator().next()).getWorld().getSpawnLocation());
/* 1402 */       invisible(true);
/* 1403 */       metadata(20, Integer.valueOf(890));
/*      */     }
/*      */ 
/*      */     
/*      */     public void bossBarInitialized(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar bar) {
/* 1408 */       super.bossBarInitialized(bar);
/* 1409 */       this.initialized = true;
/*      */     }
/*      */     
/*      */     @NotNull
/*      */     public Location createPosition(@NotNull Player viewer) {
/* 1414 */       Location position = super.createPosition(viewer);
/* 1415 */       position.setPitch(position.getPitch() - 30.0F);
/* 1416 */       position.setYaw(position.getYaw() + 0.0F);
/* 1417 */       position.add(position.getDirection().multiply(40));
/* 1418 */       return position;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isEmpty() {
/* 1423 */       return (!this.initialized || this.viewers.isEmpty());
/*      */     }
/*      */   }
/*      */   
/*      */   static class TabList extends PacketFacet<Player> implements Facet.TabList<Player, Object> {
/* 1428 */     private static final Class<?> CLIENTBOUND_TAB_LIST_PACKET = MinecraftReflection.findClass(new String[] {
/* 1429 */           MinecraftReflection.findNmsClassName("PacketPlayOutPlayerListHeaderFooter"), 
/* 1430 */           MinecraftReflection.findMcClassName("network.protocol.game.PacketPlayOutPlayerListHeaderFooter"), 
/* 1431 */           MinecraftReflection.findMcClassName("network.protocol.game.ClientboundTabListPacket") });
/*      */     @Nullable
/* 1433 */     private static final MethodHandle CLIENTBOUND_TAB_LIST_PACKET_CTOR_PRE_1_17 = MinecraftReflection.findConstructor(CLIENTBOUND_TAB_LIST_PACKET, new Class[0]); @Nullable
/* 1434 */     protected static final MethodHandle CLIENTBOUND_TAB_LIST_PACKET_CTOR = MinecraftReflection.findConstructor(CLIENTBOUND_TAB_LIST_PACKET, new Class[] { CraftBukkitFacet.access$500(), CraftBukkitFacet.access$500() });
/*      */     @Nullable
/* 1436 */     private static final Field CRAFT_PLAYER_TAB_LIST_HEADER = MinecraftReflection.findField(CLASS_CRAFT_PLAYER, new String[] { "playerListHeader" }); @Nullable
/* 1437 */     private static final Field CRAFT_PLAYER_TAB_LIST_FOOTER = MinecraftReflection.findField(CLASS_CRAFT_PLAYER, new String[] { "playerListFooter" });
/*      */     @Nullable
/* 1439 */     protected static final MethodHandle CLIENTBOUND_TAB_LIST_PACKET_SET_HEADER = first(new MethodHandle[] {
/* 1440 */           MinecraftReflection.findSetterOf(MinecraftReflection.findField(CLIENTBOUND_TAB_LIST_PACKET, PaperFacet.NATIVE_COMPONENT_CLASS, new String[] { "adventure$header"
/* 1441 */               })), MinecraftReflection.findSetterOf(MinecraftReflection.findField(CLIENTBOUND_TAB_LIST_PACKET, CraftBukkitFacet.access$500(), new String[] { "header", "a" })) });
/*      */     @Nullable
/* 1443 */     protected static final MethodHandle CLIENTBOUND_TAB_LIST_PACKET_SET_FOOTER = first(new MethodHandle[] {
/* 1444 */           MinecraftReflection.findSetterOf(MinecraftReflection.findField(CLIENTBOUND_TAB_LIST_PACKET, PaperFacet.NATIVE_COMPONENT_CLASS, new String[] { "adventure$footer"
/* 1445 */               })), MinecraftReflection.findSetterOf(MinecraftReflection.findField(CLIENTBOUND_TAB_LIST_PACKET, CraftBukkitFacet.access$500(), new String[] { "footer", "b" }))
/*      */         });
/*      */     
/*      */     private static MethodHandle first(MethodHandle... handles) {
/* 1449 */       for (int i = 0; i < handles.length; i++) {
/* 1450 */         MethodHandle handle = handles[i];
/* 1451 */         if (handle != null) {
/* 1452 */           return handle;
/*      */         }
/*      */       } 
/* 1455 */       return null;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isSupported() {
/* 1460 */       return ((CLIENTBOUND_TAB_LIST_PACKET_CTOR != null || (CLIENTBOUND_TAB_LIST_PACKET_CTOR_PRE_1_17 != null && CLIENTBOUND_TAB_LIST_PACKET_SET_HEADER != null && CLIENTBOUND_TAB_LIST_PACKET_SET_FOOTER != null)) && super.isSupported());
/*      */     }
/*      */     
/*      */     protected Object create117Packet(Player viewer, @Nullable Object header, @Nullable Object footer) throws Throwable {
/* 1464 */       return CLIENTBOUND_TAB_LIST_PACKET_CTOR.invoke(
/* 1465 */           (header == null) ? createMessage(viewer, (Component)Component.empty()) : header, 
/* 1466 */           (footer == null) ? createMessage(viewer, (Component)Component.empty()) : footer);
/*      */     }
/*      */ 
/*      */     
/*      */     public void send(Player viewer, @Nullable Object header, @Nullable Object footer) {
/*      */       try {
/*      */         Object packet;
/* 1473 */         if (CRAFT_PLAYER_TAB_LIST_HEADER != null && CRAFT_PLAYER_TAB_LIST_FOOTER != null) {
/* 1474 */           if (header == null) {
/* 1475 */             header = CRAFT_PLAYER_TAB_LIST_HEADER.get(viewer);
/*      */           } else {
/* 1477 */             CRAFT_PLAYER_TAB_LIST_HEADER.set(viewer, header);
/*      */           } 
/*      */           
/* 1480 */           if (footer == null) {
/* 1481 */             footer = CRAFT_PLAYER_TAB_LIST_FOOTER.get(viewer);
/*      */           } else {
/* 1483 */             CRAFT_PLAYER_TAB_LIST_FOOTER.set(viewer, footer);
/*      */           } 
/*      */         } 
/*      */ 
/*      */         
/* 1488 */         if (CLIENTBOUND_TAB_LIST_PACKET_CTOR != null) {
/* 1489 */           packet = create117Packet(viewer, header, footer);
/*      */         } else {
/* 1491 */           packet = CLIENTBOUND_TAB_LIST_PACKET_CTOR_PRE_1_17.invoke();
/* 1492 */           CLIENTBOUND_TAB_LIST_PACKET_SET_HEADER.invoke(packet, (header == null) ? createMessage(viewer, (Component)Component.empty()) : header);
/* 1493 */           CLIENTBOUND_TAB_LIST_PACKET_SET_FOOTER.invoke(packet, (footer == null) ? createMessage(viewer, (Component)Component.empty()) : footer);
/*      */         } 
/*      */         
/* 1496 */         sendPacket(viewer, packet);
/* 1497 */       } catch (Throwable thr) {
/* 1498 */         Knob.logError(thr, "Failed to send tab list header and footer to %s", new Object[] { viewer });
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   static final class Translator extends FacetBase<Server> implements FacetComponentFlattener.Translator<Server> {
/* 1504 */     private static final Class<?> CLASS_LANGUAGE = MinecraftReflection.findClass(new String[] {
/* 1505 */           MinecraftReflection.findNmsClassName("LocaleLanguage"), 
/* 1506 */           MinecraftReflection.findMcClassName("locale.LocaleLanguage"), 
/* 1507 */           MinecraftReflection.findMcClassName("locale.Language")
/*      */         });
/*      */     private static final MethodHandle LANGUAGE_GET_INSTANCE;
/*      */     private static final MethodHandle LANGUAGE_GET_OR_DEFAULT;
/*      */     
/*      */     static {
/* 1513 */       if (CLASS_LANGUAGE == null) {
/* 1514 */         LANGUAGE_GET_INSTANCE = null;
/* 1515 */         LANGUAGE_GET_OR_DEFAULT = null;
/*      */ 
/*      */       
/*      */       }
/*      */       else {
/*      */ 
/*      */ 
/*      */         
/* 1523 */         LANGUAGE_GET_INSTANCE = Arrays.<Method>stream(CLASS_LANGUAGE.getDeclaredMethods()).filter(m -> (Modifier.isStatic(m.getModifiers()) && !Modifier.isPrivate(m.getModifiers()) && m.getReturnType().equals(CLASS_LANGUAGE) && m.getParameterCount() == 0)).findFirst().map(Translator::unreflectUnchecked).orElse(null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1530 */         LANGUAGE_GET_OR_DEFAULT = Arrays.<Method>stream(CLASS_LANGUAGE.getDeclaredMethods()).filter(m -> (!Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers()) && m.getParameterCount() == 1 && m.getParameterTypes()[0] == String.class && m.getReturnType().equals(String.class))).findFirst().map(Translator::unreflectUnchecked).orElse(null);
/*      */       } 
/*      */     }
/*      */     
/*      */     private static MethodHandle unreflectUnchecked(Method m) {
/*      */       try {
/* 1536 */         m.setAccessible(true);
/* 1537 */         return MinecraftReflection.lookup().unreflect(m);
/* 1538 */       } catch (IllegalAccessException ex) {
/* 1539 */         return null;
/*      */       } 
/*      */     }
/*      */     
/*      */     Translator() {
/* 1544 */       super(Server.class);
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isSupported() {
/* 1549 */       return (super.isSupported() && LANGUAGE_GET_INSTANCE != null && LANGUAGE_GET_OR_DEFAULT != null);
/*      */     }
/*      */     
/*      */     @NotNull
/*      */     public String valueOrDefault(@NotNull Server game, @NotNull String key) {
/*      */       try {
/* 1555 */         return LANGUAGE_GET_OR_DEFAULT.invoke(LANGUAGE_GET_INSTANCE.invoke(), key);
/* 1556 */       } catch (Throwable ex) {
/* 1557 */         Knob.logError(ex, "Failed to transate key '%s'", new Object[] { key });
/* 1558 */         return key;
/*      */       } 
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\platform\bukkit\CraftBukkitFacet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */