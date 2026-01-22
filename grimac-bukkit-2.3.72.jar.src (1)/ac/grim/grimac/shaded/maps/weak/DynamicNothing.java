/*    */ package ac.grim.grimac.shaded.maps.weak;
/*    */ 
/*    */ import java.util.NoSuchElementException;
/*    */ import java.util.stream.Stream;
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
/*    */ enum DynamicNothing
/*    */   implements Dynamic, Describer
/*    */ {
/* 22 */   INSTANCE;
/*    */ 
/*    */   
/*    */   public Dynamic get(Object key) {
/* 26 */     return new ParentAbsence.Barren<>(this, key);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isPresent() {
/* 31 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object asObject() {
/* 36 */     throw new NoSuchElementException("null 'root' premature end of path *root*");
/*    */   }
/*    */ 
/*    */   
/*    */   public Stream<Dynamic> children() {
/* 41 */     return Stream.empty();
/*    */   }
/*    */ 
/*    */   
/*    */   public Dynamic key() {
/* 46 */     return DynamicChild.key(this, "root");
/*    */   }
/*    */ 
/*    */   
/*    */   public String describe() {
/* 51 */     return "null";
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 56 */     return "root:" + describe();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\maps\weak\DynamicNothing.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */