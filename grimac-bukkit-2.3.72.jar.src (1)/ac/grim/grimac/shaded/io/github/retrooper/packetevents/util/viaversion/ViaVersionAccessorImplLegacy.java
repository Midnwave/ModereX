/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.viaversion;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.reflection.Reflection;
/*     */ import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
/*     */ import io.netty.channel.Channel;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import org.bukkit.Bukkit;
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
/*     */ public class ViaVersionAccessorImplLegacy
/*     */   implements ViaVersionAccessor
/*     */ {
/*     */   private Class<?> viaClass;
/*     */   private Class<?> bukkitDecodeHandlerClass;
/*     */   private Class<?> bukkitEncodeHandlerClass;
/*     */   private Field viaManagerField;
/*     */   private Method apiAccessor;
/*     */   private Method getPlayerVersionMethod;
/*     */   private Class<?> userConnectionClass;
/*     */   
/*     */   private void load() {
/*  43 */     if (this.viaClass == null) {
/*     */       try {
/*  45 */         ClassLoader classLoader = PacketEvents.getAPI().getPlugin().getClass().getClassLoader();
/*  46 */         this.viaClass = classLoader.loadClass("us.myles.ViaVersion.api.Via");
/*  47 */         this.viaManagerField = this.viaClass.getDeclaredField("manager");
/*  48 */         this.bukkitDecodeHandlerClass = classLoader.loadClass("us.myles.ViaVersion.bukkit.handlers.BukkitDecodeHandler");
/*  49 */         this.bukkitEncodeHandlerClass = classLoader.loadClass("us.myles.ViaVersion.bukkit.handlers.BukkitEncodeHandler");
/*  50 */         Class<?> viaAPIClass = classLoader.loadClass("us.myles.ViaVersion.api.ViaAPI");
/*  51 */         this.apiAccessor = this.viaClass.getMethod("getAPI", new Class[0]);
/*  52 */         this.getPlayerVersionMethod = viaAPIClass.getMethod("getPlayerVersion", new Class[] { Object.class });
/*  53 */       } catch (ClassNotFoundException|NoSuchMethodException|NoSuchFieldException e) {
/*  54 */         e.printStackTrace();
/*     */       } 
/*     */     }
/*     */     
/*  58 */     if (this.userConnectionClass == null) {
/*     */       try {
/*  60 */         ClassLoader classLoader = PacketEvents.getAPI().getPlugin().getClass().getClassLoader();
/*  61 */         this.userConnectionClass = classLoader.loadClass("us.myles.ViaVersion.api.data.UserConnection");
/*  62 */       } catch (ClassNotFoundException e) {
/*  63 */         e.printStackTrace();
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public int getProtocolVersion(Player player) {
/*  70 */     load();
/*     */     try {
/*  72 */       Object viaAPI = this.apiAccessor.invoke(null, new Object[0]);
/*  73 */       return ((Integer)this.getPlayerVersionMethod.invoke(viaAPI, new Object[] { player })).intValue();
/*  74 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  75 */       e.printStackTrace();
/*     */       
/*  77 */       return -1;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getProtocolVersion(User user) {
/*     */     try {
/*  83 */       if (user.getUUID() != null) {
/*  84 */         Player player = Bukkit.getPlayer(user.getUUID());
/*  85 */         if (player != null) {
/*  86 */           int version = getProtocolVersion(player);
/*     */           
/*  88 */           if (version != -1) return version; 
/*     */         } 
/*     */       } 
/*  91 */       Object viaEncoder = ((Channel)user.getChannel()).pipeline().get("via-encoder");
/*  92 */       Object connection = Reflection.getField(viaEncoder.getClass(), "connection").get(viaEncoder);
/*  93 */       Object protocolInfo = Reflection.getField(connection.getClass(), "protocolInfo").get(connection);
/*  94 */       Object protocolVersion = Reflection.getField(protocolInfo.getClass(), "protocolVersion").get(protocolInfo);
/*  95 */       return (protocolVersion instanceof Integer) ? ((Integer)protocolVersion).intValue() : ((ProtocolVersion)protocolVersion).getVersion();
/*  96 */     } catch (Exception e) {
/*  97 */       PacketEvents.getAPI().getLogManager().warn("Unable to grab ViaVersion client version for player!");
/*  98 */       return -1;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<?> getUserConnectionClass() {
/* 104 */     load();
/* 105 */     return this.userConnectionClass;
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<?> getBukkitDecodeHandlerClass() {
/* 110 */     load();
/* 111 */     return this.bukkitDecodeHandlerClass;
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<?> getBukkitEncodeHandlerClass() {
/* 116 */     load();
/* 117 */     return this.bukkitEncodeHandlerClass;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevent\\util\viaversion\ViaVersionAccessorImplLegacy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */