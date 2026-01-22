/*    */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*    */ 
/*    */ import java.io.IOException;
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
/*    */ interface BinaryTagScope
/*    */   extends AutoCloseable
/*    */ {
/*    */   void close() throws IOException;
/*    */   
/*    */   public static final class NoOp
/*    */     implements BinaryTagScope
/*    */   {
/* 38 */     static final NoOp INSTANCE = new NoOp();
/*    */     
/*    */     public void close() {}
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\BinaryTagScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */