/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.selector;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.data.MultipleEntitySelector;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import org.apiguardian.api.API;
/*     */ import org.bukkit.entity.Entity;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class MultipleEntitySelectorParser<C>
/*     */   extends SelectorUtils.EntitySelectorParser<C, MultipleEntitySelector>
/*     */ {
/*     */   private final boolean allowEmpty;
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> ParserDescriptor<C, MultipleEntitySelector> multipleEntitySelectorParser() {
/*  55 */     return multipleEntitySelectorParser(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> ParserDescriptor<C, MultipleEntitySelector> multipleEntitySelectorParser(boolean allowEmpty) {
/*  68 */     return ParserDescriptor.of((ArgumentParser)new MultipleEntitySelectorParser(allowEmpty), MultipleEntitySelector.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> CommandComponent.Builder<C, MultipleEntitySelector> multipleEntitySelectorComponent() {
/*  80 */     return CommandComponent.builder().parser(multipleEntitySelectorParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "1.8.0")
/*     */   public MultipleEntitySelectorParser(boolean allowEmpty) {
/*  93 */     super(false);
/*  94 */     this.allowEmpty = allowEmpty;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MultipleEntitySelectorParser() {
/* 101 */     this(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */   public MultipleEntitySelector mapResult(final String input, SelectorUtils.EntitySelectorWrapper wrapper) {
/* 110 */     final List<Entity> entities = wrapper.entities();
/* 111 */     if (entities.isEmpty() && !this.allowEmpty) {
/* 112 */       (new SelectorUtils.SelectorParser.Thrower(NO_ENTITIES_EXCEPTION_TYPE.get())).throwIt();
/*     */     }
/* 114 */     return new MultipleEntitySelector()
/*     */       {
/*     */         public String inputString() {
/* 117 */           return input;
/*     */         }
/*     */ 
/*     */         
/*     */         public Collection<Entity> values() {
/* 122 */           return Collections.unmodifiableCollection(entities);
/*     */         }
/*     */       };
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\parser\selector\MultipleEntitySelectorParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */