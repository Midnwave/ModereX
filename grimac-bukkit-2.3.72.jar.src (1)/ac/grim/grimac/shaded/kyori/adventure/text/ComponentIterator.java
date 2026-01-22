/*    */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import java.util.ArrayDeque;
/*    */ import java.util.Deque;
/*    */ import java.util.Iterator;
/*    */ import java.util.NoSuchElementException;
/*    */ import java.util.Set;
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
/*    */ final class ComponentIterator
/*    */   implements Iterator<Component>
/*    */ {
/*    */   private Component component;
/*    */   private final ComponentIteratorType type;
/*    */   private final Set<ComponentIteratorFlag> flags;
/*    */   private final Deque<Component> deque;
/*    */   
/*    */   ComponentIterator(@NotNull Component component, @NotNull ComponentIteratorType type, @NotNull Set<ComponentIteratorFlag> flags) {
/* 40 */     this.component = component;
/* 41 */     this.type = type;
/* 42 */     this.flags = flags;
/* 43 */     this.deque = new ArrayDeque<>();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 48 */     return (this.component != null || !this.deque.isEmpty());
/*    */   }
/*    */ 
/*    */   
/*    */   public Component next() {
/* 53 */     if (this.component != null) {
/* 54 */       Component next = this.component;
/* 55 */       this.component = null;
/* 56 */       this.type.populate(next, this.deque, this.flags);
/* 57 */       return next;
/*    */     } 
/* 59 */     if (this.deque.isEmpty()) throw new NoSuchElementException(); 
/* 60 */     this.component = this.deque.poll();
/* 61 */     return next();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\ComponentIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */