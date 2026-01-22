/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.util;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.ColorUtil;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.LogManager;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.NamedTextColor;
/*    */ import java.util.logging.Level;
/*    */ import org.bukkit.Bukkit;
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
/*    */ public class BukkitLogManager
/*    */   extends LogManager
/*    */ {
/* 31 */   private final String prefixText = ColorUtil.toString(NamedTextColor.AQUA) + "[packetevents] " + ColorUtil.toString(NamedTextColor.WHITE);
/*    */ 
/*    */   
/*    */   protected void log(Level level, @Nullable NamedTextColor color, String message) {
/* 35 */     Bukkit.getConsoleSender().sendMessage(this.prefixText + ColorUtil.toString(color) + message);
/*    */   }
/*    */ 
/*    */   
/*    */   public void info(String message) {
/* 40 */     log(Level.INFO, NamedTextColor.WHITE, message);
/*    */   }
/*    */ 
/*    */   
/*    */   public void warn(String message) {
/* 45 */     log(Level.WARNING, NamedTextColor.YELLOW, message);
/*    */   }
/*    */ 
/*    */   
/*    */   public void severe(String message) {
/* 50 */     log(Level.SEVERE, NamedTextColor.RED, message);
/*    */   }
/*    */ 
/*    */   
/*    */   public void debug(String message) {
/* 55 */     if (PacketEvents.getAPI().getSettings().isDebugEnabled())
/* 56 */       log(Level.FINE, NamedTextColor.GRAY, message); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevent\\util\BukkitLogManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */