/*    */ package ac.grim.grimac.shaded.maps.weak;
/*    */ 
/*    */ import ac.grim.grimac.shaded.maps.LiteJoiner;
/*    */ import java.util.Objects;
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
/*    */ 
/*    */ abstract class AbstractAbsence<Parent extends Dynamic>
/*    */   implements DynamicChild
/*    */ {
/*    */   protected final Parent parent;
/*    */   protected final Object key;
/*    */   
/*    */   AbstractAbsence(Parent parent, Object key) {
/* 28 */     this.parent = parent;
/* 29 */     this.key = key;
/*    */   }
/*    */ 
/*    */   
/*    */   public Parent parent() {
/* 34 */     return this.parent;
/*    */   }
/*    */ 
/*    */   
/*    */   public Dynamic key() {
/* 39 */     return DynamicChild.key(this, this.key);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isPresent() {
/* 44 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public Stream<Dynamic> children() {
/* 49 */     return Stream.empty();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 54 */     if (this == o) return true; 
/* 55 */     if (o == null || getClass() != o.getClass()) return false; 
/* 56 */     AbstractAbsence other = (AbstractAbsence)o;
/* 57 */     return (Objects.equals(this.parent, other.parent) && 
/* 58 */       Objects.equals(this.key, other.key));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 63 */     return Objects.hash(new Object[] { this.parent, this.key });
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 68 */     return LiteJoiner.on("->").join(DynamicChildLogic.using(this).getAscendingKeyChainWithRoot()) + ":absent";
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\maps\weak\AbstractAbsence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */