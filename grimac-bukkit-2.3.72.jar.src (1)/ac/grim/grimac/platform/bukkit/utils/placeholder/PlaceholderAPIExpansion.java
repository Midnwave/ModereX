/*    */ package ac.grim.grimac.platform.bukkit.utils.placeholder;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.api.GrimUser;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import java.util.function.Function;
/*    */ import me.clip.placeholderapi.expansion.PlaceholderExpansion;
/*    */ import org.bukkit.OfflinePlayer;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ public class PlaceholderAPIExpansion
/*    */   extends PlaceholderExpansion
/*    */ {
/*    */   @NotNull
/*    */   public String getIdentifier() {
/* 21 */     return "grim";
/*    */   }
/*    */   @NotNull
/*    */   public String getAuthor() {
/* 25 */     return String.join(", ", GrimAPI.INSTANCE.getGrimPlugin().getDescription().getAuthors());
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public String getVersion() {
/* 30 */     return GrimAPI.INSTANCE.getExternalAPI().getGrimVersion();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean persist() {
/* 35 */     return true;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public List<String> getPlaceholders() {
/* 40 */     Set<String> staticReplacements = GrimAPI.INSTANCE.getExternalAPI().getStaticReplacements().keySet();
/* 41 */     Set<String> variableReplacements = GrimAPI.INSTANCE.getExternalAPI().getVariableReplacements().keySet();
/* 42 */     ArrayList<String> placeholders = new ArrayList<>(staticReplacements.size() + variableReplacements.size());
/* 43 */     for (String s : staticReplacements) {
/* 44 */       placeholders.add(s.equals("%grim_version%") ? s : ("%grim_" + s.replaceAll("%", "") + "%"));
/*    */     }
/* 46 */     for (String s : variableReplacements) {
/* 47 */       placeholders.add(s.equals("%player%") ? "%grim_player%" : ("%grim_player_" + s.replaceAll("%", "") + "%"));
/*    */     }
/* 49 */     return placeholders;
/*    */   }
/*    */ 
/*    */   
/*    */   public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
/* 54 */     for (Map.Entry<String, String> entry : (Iterable<Map.Entry<String, String>>)GrimAPI.INSTANCE.getExternalAPI().getStaticReplacements().entrySet()) {
/*    */ 
/*    */       
/* 57 */       String key = ((String)entry.getKey()).equals("%grim_version%") ? "version" : ((String)entry.getKey()).replaceAll("%", "");
/* 58 */       if (params.equalsIgnoreCase(key)) {
/* 59 */         return entry.getValue();
/*    */       }
/*    */     } 
/*    */     
/* 63 */     if (offlinePlayer instanceof Player) { Player player = (Player)offlinePlayer;
/* 64 */       GrimPlayer grimPlayer = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(player.getUniqueId());
/* 65 */       if (grimPlayer == null) return null;
/*    */       
/* 67 */       for (Map.Entry<String, Function<GrimUser, String>> entry : (Iterable<Map.Entry<String, Function<GrimUser, String>>>)GrimAPI.INSTANCE.getExternalAPI().getVariableReplacements().entrySet()) {
/*    */ 
/*    */         
/* 70 */         String key = ((String)entry.getKey()).equals("%player%") ? "player" : ("player_" + ((String)entry.getKey()).replaceAll("%", ""));
/* 71 */         if (params.equalsIgnoreCase(key)) {
/* 72 */           return ((Function<GrimPlayer, String>)entry.getValue()).apply(grimPlayer);
/*    */         }
/*    */       }  }
/*    */ 
/*    */     
/* 77 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukki\\utils\placeholder\PlaceholderAPIExpansion.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */