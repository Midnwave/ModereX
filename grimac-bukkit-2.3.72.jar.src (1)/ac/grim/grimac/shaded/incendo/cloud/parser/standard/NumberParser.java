/*    */ package ac.grim.grimac.shaded.incendo.cloud.parser.standard;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.type.range.Range;
/*    */ import java.util.Objects;
/*    */ import org.apiguardian.api.API;
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
/*    */ @API(status = API.Status.STABLE)
/*    */ public abstract class NumberParser<C, N extends Number, R extends Range<N>>
/*    */   implements ArgumentParser<C, N>
/*    */ {
/*    */   private final R range;
/*    */   
/*    */   protected NumberParser(R range) {
/* 38 */     this.range = (R)Objects.<Range>requireNonNull((Range)range, "range");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final R range() {
/* 47 */     return this.range;
/*    */   }
/*    */   
/*    */   public abstract boolean hasMax();
/*    */   
/*    */   public abstract boolean hasMin();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\standard\NumberParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */