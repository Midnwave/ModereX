/*    */ package ac.grim.grimac.shaded.incendo.cloud.parser.aggregate;
/*    */ 
/*    */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
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
/*    */ 
/*    */ 
/*    */ final class AggregateParserImpl<C, O>
/*    */   implements AggregateParser<C, O>
/*    */ {
/*    */   private final List<CommandComponent<C>> components;
/*    */   private final TypeToken<O> valueType;
/*    */   private final AggregateResultMapper<C, O> mapper;
/*    */   
/*    */   AggregateParserImpl(List<CommandComponent<C>> components, TypeToken<O> valueType, AggregateResultMapper<C, O> mapper) {
/* 43 */     this.components = components;
/* 44 */     this.valueType = valueType;
/* 45 */     this.mapper = mapper;
/*    */   }
/*    */ 
/*    */   
/*    */   public List<CommandComponent<C>> components() {
/* 50 */     return Collections.unmodifiableList(this.components);
/*    */   }
/*    */ 
/*    */   
/*    */   public AggregateResultMapper<C, O> mapper() {
/* 55 */     return this.mapper;
/*    */   }
/*    */ 
/*    */   
/*    */   public TypeToken<O> valueType() {
/* 60 */     return this.valueType;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\aggregate\AggregateParserImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */