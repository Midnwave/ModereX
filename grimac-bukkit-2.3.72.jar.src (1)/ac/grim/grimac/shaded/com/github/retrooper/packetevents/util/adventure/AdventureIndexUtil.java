/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.adventure;
/*    */ 
/*    */ import ac.grim.grimac.shaded.kyori.adventure.util.Index;
/*    */ import java.util.NoSuchElementException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class AdventureIndexUtil
/*    */ {
/*    */   public static <K, V> V indexValueOrThrow(Index<K, V> index, K key) {
/* 32 */     V value = (V)index.value(key);
/* 33 */     if (value == null) {
/* 34 */       throw new NoSuchElementException("There is no value for key " + key);
/*    */     }
/* 36 */     return value;
/*    */   }
/*    */ 
/*    */   
/*    */   public static <K, V> K indexKeyOrThrow(Index<K, V> index, V value) {
/* 41 */     K key = (K)index.key(value);
/* 42 */     if (key == null) {
/* 43 */       throw new NoSuchElementException("There is no key for value " + value);
/*    */     }
/* 45 */     return key;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\adventure\AdventureIndexUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */