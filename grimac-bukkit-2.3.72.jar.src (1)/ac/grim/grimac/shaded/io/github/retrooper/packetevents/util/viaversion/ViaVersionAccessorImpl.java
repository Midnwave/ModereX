/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.viaversion;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.reflection.Reflection;
/*    */ import com.viaversion.viaversion.api.Via;
/*    */ import com.viaversion.viaversion.api.connection.UserConnection;
/*    */ import com.viaversion.viaversion.bukkit.handlers.BukkitDecodeHandler;
/*    */ import com.viaversion.viaversion.bukkit.handlers.BukkitEncodeHandler;
/*    */ import io.netty.channel.Channel;
/*    */ import java.lang.reflect.Field;
/*    */ import org.bukkit.entity.Player;
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
/*    */ 
/*    */ public class ViaVersionAccessorImpl
/*    */   implements ViaVersionAccessor
/*    */ {
/*    */   private static Field CONNECTION_FIELD;
/*    */   
/*    */   public int getProtocolVersion(Player player) {
/* 37 */     return Via.getAPI().getPlayerVersion(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getProtocolVersion(User user) {
/*    */     try {
/* 43 */       Object viaEncoder = ((Channel)user.getChannel()).pipeline().get("via-encoder");
/* 44 */       if (CONNECTION_FIELD == null) {
/* 45 */         CONNECTION_FIELD = Reflection.getField(viaEncoder.getClass(), "connection");
/*    */       }
/* 47 */       UserConnection connection = (UserConnection)CONNECTION_FIELD.get(viaEncoder);
/* 48 */       return connection.getProtocolInfo().getProtocolVersion();
/*    */     }
/* 50 */     catch (IllegalAccessException e) {
/* 51 */       PacketEvents.getAPI().getLogManager().warn("Unable to grab ViaVersion client version for player!");
/* 52 */       return -1;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?> getUserConnectionClass() {
/* 58 */     return UserConnection.class;
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?> getBukkitDecodeHandlerClass() {
/* 63 */     return BukkitDecodeHandler.class;
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?> getBukkitEncodeHandlerClass() {
/* 68 */     return BukkitEncodeHandler.class;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevent\\util\viaversion\ViaVersionAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */