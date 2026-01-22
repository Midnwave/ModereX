/*     */ package ac.grim.grimac.shaded.kyori.adventure.platform.viaversion;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.audience.MessageType;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.identity.Identity;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.Facet;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.FacetBase;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.Knob;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
/*     */ import com.viaversion.viaversion.api.Via;
/*     */ import com.viaversion.viaversion.api.connection.UserConnection;
/*     */ import com.viaversion.viaversion.api.protocol.Protocol;
/*     */ import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
/*     */ import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
/*     */ import com.viaversion.viaversion.api.type.Type;
/*     */ import com.viaversion.viaversion.libs.gson.JsonElement;
/*     */ import com.viaversion.viaversion.libs.gson.JsonParser;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.CopyOnWriteArraySet;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Function;
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
/*     */ public class ViaFacet<V>
/*     */   extends FacetBase<V>
/*     */   implements Facet.Message<V, String>
/*     */ {
/*     */   private static final String PACKAGE = "com.viaversion.viaversion";
/*     */   private static final int SUPPORTED_VIA_MAJOR_VERSION = 4;
/*     */   private static final boolean SUPPORTED;
/*     */   private final Function<V, UserConnection> connectionFunction;
/*     */   private final int minProtocol;
/*     */   
/*     */   static {
/*  64 */     boolean supported = false;
/*     */     
/*     */     try {
/*  67 */       Class.forName("com.viaversion.viaversion.api.ViaAPI").getDeclaredMethod("majorVersion", new Class[0]);
/*  68 */       supported = (Via.getAPI().majorVersion() == 4);
/*  69 */     } catch (Throwable throwable) {}
/*     */ 
/*     */     
/*  72 */     SUPPORTED = (supported && Knob.isEnabled("viaversion", true));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ViaFacet(@NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> connectionFunction, int minProtocol) {
/*  79 */     super(viewerClass);
/*  80 */     this.connectionFunction = connectionFunction;
/*  81 */     this.minProtocol = minProtocol;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSupported() {
/*  86 */     return (super.isSupported() && SUPPORTED && this.connectionFunction != null && this.minProtocol >= 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isApplicable(@NotNull V viewer) {
/*  94 */     return (super.isApplicable(viewer) && this.minProtocol > 
/*  95 */       Via.getAPI().getServerVersion().lowestSupportedVersion() && 
/*  96 */       findProtocol(viewer) >= this.minProtocol);
/*     */   }
/*     */   @Nullable
/*     */   public UserConnection findConnection(@NotNull V viewer) {
/* 100 */     return this.connectionFunction.apply(viewer);
/*     */   }
/*     */   
/*     */   public int findProtocol(@NotNull V viewer) {
/* 104 */     UserConnection connection = findConnection(viewer);
/* 105 */     if (connection != null) {
/* 106 */       return connection.getProtocolInfo().getProtocolVersion();
/*     */     }
/* 108 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public String createMessage(@NotNull V viewer, @NotNull Component message) {
/* 114 */     int protocol = findProtocol(viewer);
/* 115 */     if (protocol >= 713) {
/* 116 */       return (String)GsonComponentSerializer.gson().serialize(message);
/*     */     }
/* 118 */     return (String)GsonComponentSerializer.colorDownsamplingGson().serialize(message);
/*     */   }
/*     */   
/*     */   public static class ProtocolBased<V>
/*     */     extends ViaFacet<V>
/*     */   {
/*     */     private final Class<? extends Protocol<?, ?, ?, ?>> protocolClass;
/*     */     private final Class<? extends ClientboundPacketType> packetClass;
/*     */     private final int packetId;
/*     */     
/*     */     protected ProtocolBased(@NotNull String fromProtocol, @NotNull String toProtocol, int minProtocol, @NotNull String packetName, @NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> connectionFunction) {
/* 129 */       super(viewerClass, connectionFunction, minProtocol);
/*     */       
/* 131 */       String protocolClassName = MessageFormat.format("{0}.protocols.protocol{1}to{2}.Protocol{1}To{2}", new Object[] { "com.viaversion.viaversion", fromProtocol, toProtocol });
/* 132 */       String packetClassName = MessageFormat.format("{0}.protocols.protocol{1}to{2}.ClientboundPackets{1}", new Object[] { "com.viaversion.viaversion", fromProtocol, toProtocol });
/*     */       
/* 134 */       Class<? extends Protocol<?, ?, ?, ?>> protocolClass = null;
/* 135 */       Class<? extends ClientboundPacketType> packetClass = null;
/* 136 */       int packetId = -1;
/*     */       try {
/* 138 */         protocolClass = (Class)Class.forName(protocolClassName);
/* 139 */         packetClass = (Class)Class.forName(packetClassName);
/* 140 */         for (ClientboundPacketType type : (ClientboundPacketType[])packetClass.getEnumConstants()) {
/* 141 */           if (type.getName().equals(packetName)) {
/* 142 */             packetId = type.getId();
/*     */             break;
/*     */           } 
/*     */         } 
/* 146 */       } catch (Throwable throwable) {}
/*     */ 
/*     */ 
/*     */       
/* 150 */       this.protocolClass = protocolClass;
/* 151 */       this.packetClass = packetClass;
/* 152 */       this.packetId = packetId;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isSupported() {
/* 157 */       return (super.isSupported() && this.protocolClass != null && this.packetClass != null && this.packetId >= 0);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public PacketWrapper createPacket(@NotNull V viewer) {
/* 164 */       return PacketWrapper.create(this.packetId, null, findConnection(viewer));
/*     */     }
/*     */     
/*     */     public void sendPacket(@NotNull PacketWrapper packet) {
/* 168 */       if (packet.user() == null)
/*     */         return;  try {
/* 170 */         packet.scheduleSend(this.protocolClass);
/* 171 */       } catch (Throwable error) {
/* 172 */         Knob.logError(error, "Failed to send ViaVersion packet: %s %s", new Object[] { packet.user(), packet });
/*     */       } 
/*     */     }
/*     */     @NotNull
/*     */     public JsonElement parse(@NotNull String message) {
/* 177 */       return JsonParser.parseString(message);
/*     */     }
/*     */   }
/*     */   
/*     */   public static class Chat<V> extends ProtocolBased<V> implements Facet.ChatPacket<V, String> {
/*     */     public Chat(@NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> connectionFunction) {
/* 183 */       super("1_16", "1_15_2", 713, "CHAT_MESSAGE", viewerClass, connectionFunction);
/*     */     }
/*     */ 
/*     */     
/*     */     public void sendMessage(@NotNull V viewer, @NotNull Identity source, @NotNull String message, @NotNull Object type) {
/* 188 */       PacketWrapper packet = createPacket(viewer);
/* 189 */       packet.write(Type.COMPONENT, parse(message));
/* 190 */       packet.write((Type)Type.BYTE, Byte.valueOf(createMessageType((type instanceof MessageType) ? (MessageType)type : MessageType.SYSTEM)));
/* 191 */       packet.write(Type.UUID, source.uuid());
/* 192 */       sendPacket(packet);
/*     */     }
/*     */   }
/*     */   
/*     */   public static class ActionBar<V> extends Chat<V> implements Facet.ActionBar<V, String> {
/*     */     public ActionBar(@NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> connectionFunction) {
/* 198 */       super(viewerClass, connectionFunction);
/*     */     }
/*     */ 
/*     */     
/*     */     public byte createMessageType(@NotNull MessageType type) {
/* 203 */       return 2;
/*     */     }
/*     */ 
/*     */     
/*     */     public void sendMessage(@NotNull V viewer, @NotNull String message) {
/* 208 */       sendMessage(viewer, Identity.nil(), message, MessageType.CHAT);
/*     */     }
/*     */   }
/*     */   
/*     */   public static class ActionBarTitle<V> extends ProtocolBased<V> implements Facet.ActionBar<V, String> {
/*     */     public ActionBarTitle(@NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> connectionFunction) {
/* 214 */       super("1_11", "1_10", 310, "TITLE", viewerClass, connectionFunction);
/*     */     }
/*     */ 
/*     */     
/*     */     public void sendMessage(@NotNull V viewer, @NotNull String message) {
/* 219 */       PacketWrapper packet = createPacket(viewer);
/* 220 */       packet.write((Type)Type.VAR_INT, Integer.valueOf(2));
/* 221 */       packet.write(Type.COMPONENT, parse(message));
/* 222 */       sendPacket(packet);
/*     */     }
/*     */   }
/*     */   
/*     */   public static class Title<V> extends ProtocolBased<V> implements Facet.TitlePacket<V, String, List<Consumer<PacketWrapper>>, Consumer<V>> {
/*     */     protected Title(@NotNull String fromProtocol, @NotNull String toProtocol, int minProtocol, @NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> connectionFunction) {
/* 228 */       super(fromProtocol, toProtocol, minProtocol, "TITLE", viewerClass, connectionFunction);
/*     */     }
/*     */     
/*     */     public Title(@NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> connectionFunction) {
/* 232 */       this("1_16", "1_15_2", 713, viewerClass, connectionFunction);
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public List<Consumer<PacketWrapper>> createTitleCollection() {
/* 237 */       return new ArrayList<>();
/*     */     }
/*     */ 
/*     */     
/*     */     public void contributeTitle(@NotNull List<Consumer<PacketWrapper>> coll, @NotNull String title) {
/* 242 */       coll.add(packet -> {
/*     */             packet.write((Type)Type.VAR_INT, Integer.valueOf(0));
/*     */             packet.write(Type.COMPONENT, parse(title));
/*     */           });
/*     */     }
/*     */ 
/*     */     
/*     */     public void contributeSubtitle(@NotNull List<Consumer<PacketWrapper>> coll, @NotNull String subtitle) {
/* 250 */       coll.add(packet -> {
/*     */             packet.write((Type)Type.VAR_INT, Integer.valueOf(1));
/*     */             packet.write(Type.COMPONENT, parse(subtitle));
/*     */           });
/*     */     }
/*     */ 
/*     */     
/*     */     public void contributeTimes(@NotNull List<Consumer<PacketWrapper>> coll, int inTicks, int stayTicks, int outTicks) {
/* 258 */       coll.add(packet -> {
/*     */             packet.write((Type)Type.VAR_INT, Integer.valueOf(3));
/*     */             packet.write((Type)Type.INT, Integer.valueOf(inTicks));
/*     */             packet.write((Type)Type.INT, Integer.valueOf(stayTicks));
/*     */             packet.write((Type)Type.INT, Integer.valueOf(outTicks));
/*     */           });
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     public Consumer<V> completeTitle(@NotNull List<Consumer<PacketWrapper>> coll) {
/* 268 */       return v -> {
/*     */           int i = 0;
/*     */           int length = coll.size();
/*     */           while (i < length) {
/*     */             PacketWrapper pkt = createPacket((V)v);
/*     */             ((Consumer<PacketWrapper>)coll.get(i)).accept(pkt);
/*     */             sendPacket(pkt);
/*     */             i++;
/*     */           } 
/*     */         };
/*     */     } public void showTitle(@NotNull V viewer, @NotNull Consumer<V> title) {
/* 279 */       title.accept(viewer);
/*     */     }
/*     */ 
/*     */     
/*     */     public void clearTitle(@NotNull V viewer) {
/* 284 */       PacketWrapper packet = createPacket(viewer);
/* 285 */       packet.write((Type)Type.VAR_INT, Integer.valueOf(4));
/* 286 */       sendPacket(packet);
/*     */     }
/*     */ 
/*     */     
/*     */     public void resetTitle(@NotNull V viewer) {
/* 291 */       PacketWrapper packet = createPacket(viewer);
/* 292 */       packet.write((Type)Type.VAR_INT, Integer.valueOf(5));
/* 293 */       sendPacket(packet);
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class BossBar<V> extends ProtocolBased<V> implements Facet.BossBarPacket<V> {
/*     */     private final Set<V> viewers;
/*     */     private UUID id;
/*     */     private String title;
/*     */     private float health;
/*     */     private int color;
/*     */     private int overlay;
/*     */     private byte flags;
/*     */     
/*     */     private BossBar(@NotNull String fromProtocol, @NotNull String toProtocol, @NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> connectionFunction, Collection<V> viewers) {
/* 307 */       super(fromProtocol, toProtocol, 356, "BOSSBAR", viewerClass, connectionFunction);
/* 308 */       this.viewers = new CopyOnWriteArraySet<>(viewers);
/*     */     }
/*     */     
/*     */     public static class Builder<V> extends ViaFacet<V> implements Facet.BossBar.Builder<V, Facet.BossBar<V>> {
/*     */       public Builder(@NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> connectionFunction) {
/* 313 */         super(viewerClass, connectionFunction, 713);
/*     */       }
/*     */ 
/*     */       
/*     */       public Facet.BossBar<V> createBossBar(@NotNull Collection<V> viewer) {
/* 318 */         return (Facet.BossBar<V>)new ViaFacet.BossBar("1_16", "1_15_2", this.viewerClass, this::findConnection, viewer);
/*     */       }
/*     */     }
/*     */     
/*     */     public static class Builder1_9_To_1_15<V> extends ViaFacet<V> implements Facet.BossBar.Builder<V, Facet.BossBar<V>> {
/*     */       public Builder1_9_To_1_15(@NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> connectionFunction) {
/* 324 */         super(viewerClass, connectionFunction, 356);
/*     */       }
/*     */ 
/*     */       
/*     */       public Facet.BossBar<V> createBossBar(@NotNull Collection<V> viewer) {
/* 329 */         return (Facet.BossBar<V>)new ViaFacet.BossBar("1_9", "1_8", this.viewerClass, this::findConnection, viewer);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void bossBarInitialized(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar bar) {
/* 335 */       super.bossBarInitialized(bar);
/* 336 */       this.id = UUID.randomUUID();
/* 337 */       broadcastPacket(0);
/*     */     }
/*     */ 
/*     */     
/*     */     public void bossBarNameChanged(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar bar, @NotNull Component oldName, @NotNull Component newName) {
/* 342 */       if (!this.viewers.isEmpty()) {
/* 343 */         this.title = createMessage(this.viewers.iterator().next(), newName);
/* 344 */         broadcastPacket(3);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void bossBarProgressChanged(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar bar, float oldPercent, float newPercent) {
/* 350 */       this.health = newPercent;
/* 351 */       broadcastPacket(2);
/*     */     }
/*     */ 
/*     */     
/*     */     public void bossBarColorChanged(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar bar, ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Color oldColor, ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Color newColor) {
/* 356 */       this.color = createColor(newColor);
/* 357 */       broadcastPacket(4);
/*     */     }
/*     */ 
/*     */     
/*     */     public void bossBarOverlayChanged(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar bar, ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Overlay oldOverlay, ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Overlay newOverlay) {
/* 362 */       this.overlay = createOverlay(newOverlay);
/* 363 */       broadcastPacket(4);
/*     */     }
/*     */ 
/*     */     
/*     */     public void bossBarFlagsChanged(ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar bar, @NotNull Set<ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Flag> flagsAdded, @NotNull Set<ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar.Flag> flagsRemoved) {
/* 368 */       this.flags = createFlag(this.flags, flagsAdded, flagsRemoved);
/* 369 */       broadcastPacket(5);
/*     */     }
/*     */     
/*     */     public void sendPacket(@NotNull V viewer, int action) {
/* 373 */       PacketWrapper packet = createPacket(viewer);
/* 374 */       packet.write(Type.UUID, this.id);
/* 375 */       packet.write((Type)Type.VAR_INT, Integer.valueOf(action));
/* 376 */       if (action == 0 || action == 3) {
/* 377 */         packet.write(Type.COMPONENT, parse(this.title));
/*     */       }
/* 379 */       if (action == 0 || action == 2) {
/* 380 */         packet.write((Type)Type.FLOAT, Float.valueOf(this.health));
/*     */       }
/* 382 */       if (action == 0 || action == 4) {
/* 383 */         packet.write((Type)Type.VAR_INT, Integer.valueOf(this.color));
/* 384 */         packet.write((Type)Type.VAR_INT, Integer.valueOf(this.overlay));
/*     */       } 
/* 386 */       if (action == 0 || action == 5) {
/* 387 */         packet.write((Type)Type.BYTE, Byte.valueOf(this.flags));
/*     */       }
/* 389 */       sendPacket(packet);
/*     */     }
/*     */     
/*     */     public void broadcastPacket(int action) {
/* 393 */       if (isEmpty())
/* 394 */         return;  for (V viewer : this.viewers) {
/* 395 */         sendPacket(viewer, action);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void addViewer(@NotNull V viewer) {
/* 401 */       if (this.viewers.add(viewer)) {
/* 402 */         sendPacket(viewer, 0);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void removeViewer(@NotNull V viewer) {
/* 408 */       if (this.viewers.remove(viewer)) {
/* 409 */         sendPacket(viewer, 1);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty() {
/* 415 */       return (this.id == null || this.viewers.isEmpty());
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() {
/* 420 */       broadcastPacket(1);
/* 421 */       this.viewers.clear();
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class TabList<V>
/*     */     extends ProtocolBased<V> implements Facet.TabList<V, String> {
/*     */     public TabList(@NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> userConnection) {
/* 428 */       super("1_16", "1_15_2", 713, "TAB_LIST", viewerClass, userConnection);
/*     */     }
/*     */ 
/*     */     
/*     */     public void send(V viewer, @Nullable String header, @Nullable String footer) {
/* 433 */       PacketWrapper packet = createPacket(viewer);
/* 434 */       packet.write(Type.COMPONENT, parse(header));
/* 435 */       packet.write(Type.COMPONENT, parse(footer));
/* 436 */       sendPacket(packet);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\platform\viaversion\ViaFacet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */