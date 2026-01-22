/*    */ package ac.grim.grimac.utils.reflection;
/*    */ import java.util.UUID;
/*    */ import lombok.Generated;
/*    */ import org.geysermc.api.Geyser;
/*    */ import org.geysermc.floodgate.api.FloodgateApi;
/*    */ 
/*    */ public final class GeyserUtil {
/*    */   @Generated
/*    */   private GeyserUtil() {
/* 10 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*    */   }
/* 12 */   private static final boolean floodgate = ReflectionUtils.hasClass("org.geysermc.floodgate.api.FloodgateApi");
/* 13 */   private static final boolean geyser = ReflectionUtils.hasClass("org.geysermc.api.Geyser");
/*    */   
/*    */   public static boolean isBedrockPlayer(UUID uuid) {
/* 16 */     return ((floodgate && FloodgateApi.getInstance().isFloodgatePlayer(uuid)) || (geyser && 
/* 17 */       Geyser.api().isBedrockPlayer(uuid)));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\reflection\GeyserUtil.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */