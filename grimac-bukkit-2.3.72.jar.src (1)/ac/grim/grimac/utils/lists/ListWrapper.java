/*    */ package ac.grim.grimac.utils.lists;
/*    */ 
/*    */ import java.util.List;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public abstract class ListWrapper<T> implements List<T> {
/*    */   protected final List<T> base;
/*    */   
/*    */   @Generated
/* 10 */   public ListWrapper(List<T> base) { this.base = base; } @Generated
/*    */   public List<T> getBase() {
/* 12 */     return this.base;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\lists\ListWrapper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */