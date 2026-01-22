/*    */ package ac.grim.grimac.shaded.snakeyaml.serializer;
/*    */ 
/*    */ import ac.grim.grimac.shaded.snakeyaml.nodes.Node;
/*    */ import java.text.NumberFormat;
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
/*    */ public class NumberAnchorGenerator
/*    */   implements AnchorGenerator
/*    */ {
/* 21 */   private int lastAnchorId = 0;
/*    */   
/*    */   public NumberAnchorGenerator(int lastAnchorId) {
/* 24 */     this.lastAnchorId = lastAnchorId;
/*    */   }
/*    */   
/*    */   public String nextAnchor(Node node) {
/* 28 */     this.lastAnchorId++;
/* 29 */     NumberFormat format = NumberFormat.getNumberInstance();
/* 30 */     format.setMinimumIntegerDigits(3);
/* 31 */     format.setMaximumFractionDigits(0);
/* 32 */     format.setGroupingUsed(false);
/* 33 */     String anchorId = format.format(this.lastAnchorId);
/* 34 */     return "id" + anchorId;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\serializer\NumberAnchorGenerator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */