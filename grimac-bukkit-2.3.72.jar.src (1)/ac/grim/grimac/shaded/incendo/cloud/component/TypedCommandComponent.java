/*    */ package ac.grim.grimac.shaded.incendo.cloud.component;
/*    */ 
/*    */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.component.preprocessor.ComponentPreprocessor;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.description.Description;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKey;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKeyHolder;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*    */ import java.util.Collection;
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
/*    */ public final class TypedCommandComponent<C, T>
/*    */   extends CommandComponent<C>
/*    */   implements CloudKeyHolder<T>
/*    */ {
/*    */   TypedCommandComponent(String name, ArgumentParser<C, ?> parser, TypeToken<?> valueType, Description description, CommandComponent.ComponentType componentType, DefaultValue<C, ?> defaultValue, SuggestionProvider<C> suggestionProvider, Collection<ComponentPreprocessor<C>> componentPreprocessors) {
/* 52 */     super(name, parser, valueType, description, componentType, defaultValue, suggestionProvider, componentPreprocessors);
/*    */   }
/*    */ 
/*    */   
/*    */   public TypeToken<T> valueType() {
/* 57 */     return (TypeToken)super.valueType();
/*    */   }
/*    */ 
/*    */   
/*    */   public ArgumentParser<C, T> parser() {
/* 62 */     return (ArgumentParser)super.parser();
/*    */   }
/*    */ 
/*    */   
/*    */   public DefaultValue<C, T> defaultValue() {
/* 67 */     return (DefaultValue)super.defaultValue();
/*    */   }
/*    */ 
/*    */   
/*    */   public CloudKey<T> key() {
/* 72 */     return CloudKey.of(name(), valueType());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\component\TypedCommandComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */