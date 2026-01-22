/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.injector;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.injector.ChannelInjector;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.ConnectionState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.reflection.ReflectionObject;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.injector.connection.ServerChannelHandler;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.injector.connection.ServerConnectionInitializer;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.injector.handlers.PacketEventsDecoder;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.injector.handlers.PacketEventsEncoder;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.InjectedList;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.SpigotReflectionUtil;
/*     */ import io.netty.channel.Channel;
/*     */ import io.netty.channel.ChannelFuture;
/*     */ import io.netty.channel.ChannelHandler;
/*     */ import io.netty.channel.ChannelPipeline;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.bukkit.entity.Player;
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
/*     */ public class SpigotChannelInjector
/*     */   implements ChannelInjector
/*     */ {
/*  46 */   public final Set<Channel> injectedConnectionChannels = new HashSet<>();
/*     */   public List<Object> networkManagers;
/*  48 */   private int connectionChannelsListIndex = -1;
/*     */   
/*     */   public void updatePlayer(User user, Object player) {
/*  51 */     Object channel = user.getChannel();
/*  52 */     if (channel == null) {
/*  53 */       channel = PacketEvents.getAPI().getPlayerManager().getChannel(player);
/*     */     }
/*  55 */     setPlayer(channel, player);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPlayerSet(Object channel) {
/*  60 */     if (channel == null) return false; 
/*  61 */     PacketEventsEncoder encoder = getEncoder((Channel)channel);
/*  62 */     if (encoder != null && encoder.player != null) return true;
/*     */     
/*  64 */     PacketEventsDecoder decoder = getDecoder((Channel)channel);
/*  65 */     return (decoder != null && decoder.player != null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isServerBound() {
/*  71 */     Object serverConnection = SpigotReflectionUtil.getMinecraftServerConnectionInstance();
/*  72 */     if (serverConnection != null) {
/*  73 */       ReflectionObject reflectServerConnection = new ReflectionObject(serverConnection);
/*     */       
/*  75 */       for (int i = 0; i < 2; i++) {
/*  76 */         List<?> list = reflectServerConnection.readList(i);
/*  77 */         for (Object value : list) {
/*  78 */           if (value instanceof ChannelFuture) {
/*  79 */             this.connectionChannelsListIndex = i;
/*     */ 
/*     */             
/*  82 */             return true;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*  87 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void inject() {
/*  92 */     Object serverConnection = SpigotReflectionUtil.getMinecraftServerConnectionInstance();
/*  93 */     if (serverConnection != null) {
/*  94 */       ReflectionObject reflectServerConnection = new ReflectionObject(serverConnection);
/*  95 */       List<ChannelFuture> connectionChannelFutures = reflectServerConnection.readList(this.connectionChannelsListIndex);
/*  96 */       InjectedList<ChannelFuture> wrappedList = new InjectedList(connectionChannelFutures, future -> {
/*     */             Channel channel = future.channel();
/*     */ 
/*     */             
/*     */             injectServerChannel(channel);
/*     */ 
/*     */             
/*     */             this.injectedConnectionChannels.add(channel);
/*     */           });
/*     */       
/* 106 */       reflectServerConnection.writeList(this.connectionChannelsListIndex, (List)wrappedList);
/*     */ 
/*     */       
/* 109 */       if (this.networkManagers == null) {
/* 110 */         this.networkManagers = SpigotReflectionUtil.getNetworkManagers();
/*     */       }
/* 112 */       synchronized (this.networkManagers) {
/* 113 */         if (!this.networkManagers.isEmpty()) {
/* 114 */           PacketEvents.getAPI().getLogManager().debug("Late bind not enabled, injecting into existing channel");
/*     */         }
/*     */         
/* 117 */         for (Object networkManager : this.networkManagers) {
/* 118 */           ReflectionObject networkManagerWrapper = new ReflectionObject(networkManager);
/* 119 */           Channel channel = (Channel)networkManagerWrapper.readObject(0, Channel.class);
/* 120 */           if (channel == null) {
/*     */             continue;
/*     */           }
/*     */           try {
/* 124 */             ServerConnectionInitializer.initChannel(channel, ConnectionState.PLAY);
/* 125 */           } catch (Exception e) {
/* 126 */             PacketEvents.getAPI().getLogManager().severe("PacketEvents Spigot injector failed to inject into an existing channel. If you need assistance, join our Discord server: https://discord.gg/DVHxPPxHZc");
/* 127 */             e.printStackTrace();
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void uninject() {
/* 137 */     for (Channel connectionChannel : this.injectedConnectionChannels) {
/* 138 */       uninjectServerChannel(connectionChannel);
/*     */     }
/* 140 */     this.injectedConnectionChannels.clear();
/* 141 */     Object serverConnection = SpigotReflectionUtil.getMinecraftServerConnectionInstance();
/* 142 */     if (serverConnection != null) {
/* 143 */       ReflectionObject reflectServerConnection = new ReflectionObject(serverConnection);
/* 144 */       List<ChannelFuture> connectionChannelFutures = reflectServerConnection.readList(this.connectionChannelsListIndex);
/* 145 */       if (connectionChannelFutures instanceof InjectedList)
/*     */       {
/* 147 */         reflectServerConnection.writeList(this.connectionChannelsListIndex, ((InjectedList)connectionChannelFutures)
/* 148 */             .originalList());
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void injectServerChannel(Channel serverChannel) {
/* 154 */     ChannelPipeline pipeline = serverChannel.pipeline();
/* 155 */     ChannelHandler connectionHandler = pipeline.get(PacketEvents.CONNECTION_HANDLER_NAME);
/* 156 */     if (connectionHandler != null)
/*     */     {
/* 158 */       pipeline.remove(PacketEvents.CONNECTION_HANDLER_NAME);
/*     */     }
/*     */     
/* 161 */     if (pipeline.get("SpigotNettyServerChannelHandler#0") != null) {
/* 162 */       pipeline.addAfter("SpigotNettyServerChannelHandler#0", PacketEvents.CONNECTION_HANDLER_NAME, (ChannelHandler)new ServerChannelHandler());
/*     */     
/*     */     }
/* 165 */     else if (pipeline.get("floodgate-init") != null) {
/* 166 */       pipeline.addAfter("floodgate-init", PacketEvents.CONNECTION_HANDLER_NAME, (ChannelHandler)new ServerChannelHandler());
/*     */     
/*     */     }
/* 169 */     else if (pipeline.get("MinecraftPipeline#0") != null) {
/* 170 */       pipeline.addAfter("MinecraftPipeline#0", PacketEvents.CONNECTION_HANDLER_NAME, (ChannelHandler)new ServerChannelHandler());
/*     */     }
/*     */     else {
/*     */       
/* 174 */       pipeline.addFirst(PacketEvents.CONNECTION_HANDLER_NAME, (ChannelHandler)new ServerChannelHandler());
/*     */     } 
/*     */     
/* 177 */     if (this.networkManagers == null) {
/* 178 */       this.networkManagers = SpigotReflectionUtil.getNetworkManagers();
/*     */     }
/*     */     
/* 181 */     synchronized (this.networkManagers) {
/* 182 */       for (Object networkManager : this.networkManagers) {
/* 183 */         ReflectionObject networkManagerWrapper = new ReflectionObject(networkManager);
/* 184 */         Channel channel = (Channel)networkManagerWrapper.readObject(0, Channel.class);
/*     */         
/* 186 */         if (channel != null && channel.isOpen() && 
/* 187 */           channel.localAddress().equals(serverChannel.localAddress())) {
/* 188 */           channel.close();
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void uninjectServerChannel(Channel serverChannel) {
/* 196 */     if (serverChannel.pipeline().get(PacketEvents.CONNECTION_HANDLER_NAME) != null) {
/* 197 */       serverChannel.pipeline().remove(PacketEvents.CONNECTION_HANDLER_NAME);
/*     */     } else {
/* 199 */       PacketEvents.getAPI().getLogManager().warn("Failed to uninject server channel, handler not found");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateUser(Object channel, User user) {
/* 205 */     PacketEventsEncoder encoder = getEncoder((Channel)channel);
/* 206 */     if (encoder != null) {
/* 207 */       encoder.user = user;
/*     */     }
/*     */     
/* 210 */     PacketEventsDecoder decoder = getDecoder((Channel)channel);
/* 211 */     if (decoder != null) {
/* 212 */       decoder.user = user;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPlayer(Object channel, Object player) {
/* 218 */     PacketEventsEncoder encoder = getEncoder((Channel)channel);
/* 219 */     if (encoder != null) {
/* 220 */       encoder.player = (Player)player;
/*     */     }
/*     */     
/* 223 */     PacketEventsDecoder decoder = getDecoder((Channel)channel);
/* 224 */     if (decoder != null) {
/* 225 */       decoder.player = (Player)player;
/* 226 */       decoder.user.getProfile().setName(((Player)player).getName());
/* 227 */       decoder.user.getProfile().setUUID(((Player)player).getUniqueId());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private PacketEventsEncoder getEncoder(Channel channel) {
/* 235 */     return (PacketEventsEncoder)channel.pipeline().get(PacketEvents.ENCODER_NAME);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private PacketEventsDecoder getDecoder(Channel channel) {
/* 242 */     return (PacketEventsDecoder)channel.pipeline().get(PacketEvents.DECODER_NAME);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isProxy() {
/* 247 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\injector\SpigotChannelInjector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */