/*    */ package ac.grim.grimac.shaded.kyori.adventure.platform.facet;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.pointer.Pointer;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Internal
/*    */ public final class FacetPointers
/*    */ {
/*    */   private static final String NAMESPACE = "adventure_platform";
/* 41 */   public static final Pointer<String> SERVER = Pointer.pointer(String.class, Key.key("adventure_platform", "server"));
/* 42 */   public static final Pointer<Key> WORLD = Pointer.pointer(Key.class, Key.key("adventure_platform", "world"));
/* 43 */   public static final Pointer<Type> TYPE = Pointer.pointer(Type.class, Key.key("adventure_platform", "type"));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public enum Type
/*    */   {
/* 51 */     PLAYER,
/* 52 */     CONSOLE,
/* 53 */     OTHER;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\platform\facet\FacetPointers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */