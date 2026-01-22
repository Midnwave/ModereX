/*    */ package ac.grim.grimac.shaded.maps.weak;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.stream.Stream;
/*    */ 
/*    */ class BreadthChildIterator implements Iterator<Dynamic> {
/*    */   private final Dynamic root;
/*    */   private int depth;
/*    */   private Iterator<Dynamic> current;
/*    */   
/*    */   BreadthChildIterator(Dynamic root) {
/* 12 */     this.root = root;
/* 13 */     this.depth = 1;
/* 14 */     this.current = root.children().iterator();
/*    */   }
/*    */   
/*    */   private Stream<Dynamic> nextDepth() {
/* 18 */     Stream<Dynamic> childrenAtNextDepth = this.root.children();
/* 19 */     int nextDepth = 1;
/* 20 */     while (nextDepth <= this.depth) {
/* 21 */       childrenAtNextDepth = childrenAtNextDepth.flatMap(Dynamic::children);
/* 22 */       nextDepth++;
/*    */     } 
/* 24 */     return childrenAtNextDepth;
/*    */   }
/*    */   
/*    */   private boolean moveDepthIfAvailable() {
/* 28 */     Iterator<Dynamic> nextDepth = nextDepth().iterator();
/* 29 */     if (nextDepth.hasNext()) {
/* 30 */       this.current = nextDepth;
/* 31 */       this.depth++;
/* 32 */       return true;
/*    */     } 
/* 34 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 39 */     return (this.current.hasNext() || moveDepthIfAvailable());
/*    */   }
/*    */ 
/*    */   
/*    */   public Dynamic next() {
/* 44 */     if (!this.current.hasNext()) moveDepthIfAvailable(); 
/* 45 */     return this.current.next();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\maps\weak\BreadthChildIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */