/*    */ package ac.grim.grimac.utils.reflection;
/*    */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*    */ 
/*    */ public final class ViaVersionUtil {
/*    */   @Generated
/*    */   private ViaVersionUtil() {
/*  7 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*  8 */   } public static final boolean isAvailable = ReflectionUtils.hasClass("com.viaversion.viaversion.api.Via");
/*    */   
/*    */   static {
/* 11 */     if (!isAvailable && ReflectionUtils.hasClass("us.myles.ViaVersion.api.Via"))
/* 12 */       LogUtil.error("Using unsupported ViaVersion 4.0 API, update ViaVersion to 5.0"); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\reflection\ViaVersionUtil.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */