/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.NamedTextColor;
/*    */ import java.util.logging.Level;
/*    */ import java.util.regex.Pattern;
/*    */ 
/*    */ 
/*    */ public class LogManager
/*    */ {
/* 12 */   protected static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-ORX]");
/*    */ 
/*    */   
/*    */   protected void log(Level level, @Nullable NamedTextColor color, String message) {
/* 16 */     message = STRIP_COLOR_PATTERN.matcher(message).replaceAll("");
/* 17 */     PacketEvents.getAPI().getLogger().log(level, (color != null) ? color.toString() : ("" + message));
/*    */   }
/*    */   
/*    */   public void info(String message) {
/* 21 */     log(Level.INFO, null, message);
/*    */   }
/*    */   
/*    */   public void warn(String message) {
/* 25 */     log(Level.WARNING, null, message);
/*    */   }
/*    */   
/*    */   public void severe(String message) {
/* 29 */     log(Level.SEVERE, null, message);
/*    */   }
/*    */   
/*    */   public void debug(String message) {
/* 33 */     if (isDebug()) {
/* 34 */       log(Level.FINE, null, message);
/*    */     }
/*    */   }
/*    */   
/*    */   public boolean isDebug() {
/* 39 */     return PacketEvents.getAPI().getSettings().isDebugEnabled();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\LogManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */