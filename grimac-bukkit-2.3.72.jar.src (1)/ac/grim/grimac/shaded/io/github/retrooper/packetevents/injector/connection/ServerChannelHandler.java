/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.injector.connection;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.PEVersion;
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.SpigotReflectionUtil;
/*    */ import io.netty.channel.Channel;
/*    */ import io.netty.channel.ChannelHandler;
/*    */ import io.netty.channel.ChannelHandlerContext;
/*    */ import io.netty.channel.ChannelInboundHandlerAdapter;
/*    */ import io.netty.util.Version;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ServerChannelHandler
/*    */   extends ChannelInboundHandlerAdapter
/*    */ {
/* 32 */   public static final PEVersion MODERN_NETTY_VERSION = new PEVersion(4, 1, 24);
/*    */   public static boolean CHECKED_NETTY_VERSION;
/*    */   public static PEVersion NETTY_VERSION;
/*    */   
/*    */   private static PEVersion resolveNettyVersion() {
/* 37 */     Map<String, Version> nettyArtifacts = Version.identify();
/* 38 */     Version version = nettyArtifacts.getOrDefault("netty-common", nettyArtifacts.get("netty-all"));
/*    */     
/* 40 */     if (version == null && !nettyArtifacts.isEmpty()) version = nettyArtifacts.values().iterator().next();
/*    */     
/* 42 */     if (version != null) {
/* 43 */       String stringVersion = version.artifactVersion();
/*    */ 
/*    */       
/* 46 */       stringVersion = stringVersion.replaceAll("[^\\d.]", "");
/*    */ 
/*    */       
/* 49 */       String[] splitVersion = stringVersion.split("\\.");
/* 50 */       if (splitVersion.length > 3) {
/* 51 */         stringVersion = splitVersion[0] + "." + splitVersion[1] + "." + splitVersion[2];
/*    */       }
/*    */ 
/*    */       
/* 55 */       stringVersion = stringVersion.endsWith(".") ? stringVersion.substring(0, stringVersion.length() - 1) : stringVersion;
/*    */       
/* 57 */       return PEVersion.fromString(stringVersion);
/*    */     } 
/* 59 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
/* 64 */     Channel channel = (Channel)msg;
/*    */     
/* 66 */     if (NETTY_VERSION == null && !CHECKED_NETTY_VERSION) {
/* 67 */       NETTY_VERSION = resolveNettyVersion();
/* 68 */       CHECKED_NETTY_VERSION = true;
/*    */     } 
/*    */ 
/*    */     
/* 72 */     if ((NETTY_VERSION != null && NETTY_VERSION.isNewerThan(MODERN_NETTY_VERSION)) || SpigotReflectionUtil.V_1_12_OR_HIGHER) {
/*    */       
/* 74 */       channel.pipeline().addLast(PacketEvents.SERVER_CHANNEL_HANDLER_NAME, (ChannelHandler)new PreChannelInitializer_v1_12());
/*    */     } else {
/* 76 */       channel.pipeline().addFirst(PacketEvents.SERVER_CHANNEL_HANDLER_NAME, (ChannelHandler)new PreChannelInitializer_v1_8());
/*    */     } 
/* 78 */     super.channelRead(ctx, msg);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\injector\connection\ServerChannelHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */