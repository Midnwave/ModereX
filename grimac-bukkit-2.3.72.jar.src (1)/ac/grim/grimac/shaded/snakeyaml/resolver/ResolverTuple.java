/*    */ package ac.grim.grimac.shaded.snakeyaml.resolver;
/*    */ 
/*    */ import ac.grim.grimac.shaded.snakeyaml.nodes.Tag;
/*    */ import java.util.regex.Pattern;
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
/*    */ final class ResolverTuple
/*    */ {
/*    */   private final Tag tag;
/*    */   private final Pattern regexp;
/*    */   private final int limit;
/*    */   
/*    */   public ResolverTuple(Tag tag, Pattern regexp, int limit) {
/* 26 */     this.tag = tag;
/* 27 */     this.regexp = regexp;
/* 28 */     this.limit = limit;
/*    */   }
/*    */   
/*    */   public Tag getTag() {
/* 32 */     return this.tag;
/*    */   }
/*    */   
/*    */   public Pattern getRegexp() {
/* 36 */     return this.regexp;
/*    */   }
/*    */   
/*    */   public int getLimit() {
/* 40 */     return this.limit;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 45 */     return "Tuple tag=" + this.tag + " regexp=" + this.regexp + " limit=" + this.limit;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\resolver\ResolverTuple.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */