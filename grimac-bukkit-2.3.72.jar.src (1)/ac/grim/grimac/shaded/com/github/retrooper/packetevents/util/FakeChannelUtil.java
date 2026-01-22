/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util;
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
/*    */ public class FakeChannelUtil
/*    */ {
/*    */   public static boolean isFakeChannel(Object channel) {
/* 23 */     if (channel.getClass().getSimpleName().equals("FakeChannel") || channel
/* 24 */       .getClass().getSimpleName().equals("SpoofedChannel") || channel
/* 25 */       .getClass().getSimpleName().equals("EmbeddedChannel")) {
/* 26 */       return true;
/*    */     }
/* 28 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\FakeChannelUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */