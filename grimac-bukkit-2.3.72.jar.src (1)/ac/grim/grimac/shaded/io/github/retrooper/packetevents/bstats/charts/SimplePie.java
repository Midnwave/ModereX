/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.bstats.charts;
/*    */ 
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.bstats.json.JsonObjectBuilder;
/*    */ import java.util.concurrent.Callable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SimplePie
/*    */   extends CustomChart
/*    */ {
/*    */   private final Callable<String> callable;
/*    */   
/*    */   public SimplePie(String chartId, Callable<String> callable) {
/* 18 */     super(chartId);
/* 19 */     this.callable = callable;
/*    */   }
/*    */ 
/*    */   
/*    */   protected JsonObjectBuilder.JsonObject getChartData() throws Exception {
/* 24 */     String value = this.callable.call();
/* 25 */     if (value == null || value.isEmpty())
/*    */     {
/* 27 */       return null;
/*    */     }
/* 29 */     return (new JsonObjectBuilder())
/* 30 */       .appendField("value", value)
/* 31 */       .build();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\bstats\charts\SimplePie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */