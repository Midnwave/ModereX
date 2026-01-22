/*    */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.selector;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.data.SingleEntitySelector;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ import org.apiguardian.api.API;
/*    */ import org.bukkit.entity.Entity;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class SingleEntitySelectorParser<C>
/*    */   extends SelectorUtils.EntitySelectorParser<C, SingleEntitySelector>
/*    */ {
/*    */   @API(status = API.Status.STABLE, since = "2.0.0")
/*    */   public static <C> ParserDescriptor<C, SingleEntitySelector> singleEntitySelectorParser() {
/* 52 */     return ParserDescriptor.of((ArgumentParser)new SingleEntitySelectorParser(), SingleEntitySelector.class);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @API(status = API.Status.STABLE, since = "2.0.0")
/*    */   public static <C> CommandComponent.Builder<C, SingleEntitySelector> singleEntitySelectorComponent() {
/* 64 */     return CommandComponent.builder().parser(singleEntitySelectorParser());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SingleEntitySelectorParser() {
/* 71 */     super(true);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*    */   public SingleEntitySelector mapResult(final String input, SelectorUtils.EntitySelectorWrapper wrapper) {
/* 80 */     final Entity entity = wrapper.singleEntity();
/* 81 */     return new SingleEntitySelector()
/*    */       {
/*    */         public Entity single() {
/* 84 */           return entity;
/*    */         }
/*    */ 
/*    */         
/*    */         public String inputString() {
/* 89 */           return input;
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\parser\selector\SingleEntitySelectorParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */