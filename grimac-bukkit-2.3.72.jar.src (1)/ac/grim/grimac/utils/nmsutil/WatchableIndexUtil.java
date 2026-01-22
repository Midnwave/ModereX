/*    */ package ac.grim.grimac.utils.nmsutil;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.data.EntityData;
/*    */ import java.util.List;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public final class WatchableIndexUtil {
/*    */   @Generated
/*    */   private WatchableIndexUtil() {
/*  9 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*    */   } public static EntityData<?> getIndex(List<EntityData<?>> objects, int index) {
/* 11 */     for (EntityData<?> object : objects) {
/* 12 */       if (object.getIndex() == index) return object;
/*    */     
/*    */     } 
/* 15 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\nmsutil\WatchableIndexUtil.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */