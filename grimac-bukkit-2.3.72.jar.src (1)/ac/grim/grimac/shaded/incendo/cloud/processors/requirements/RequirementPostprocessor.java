/*    */ package ac.grim.grimac.shaded.incendo.cloud.processors.requirements;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.execution.postprocessor.CommandPostprocessingContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.execution.postprocessor.CommandPostprocessor;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKey;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.services.type.ConsumerService;
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
/*    */ @API(status = API.Status.STABLE, since = "1.0.0")
/*    */ public final class RequirementPostprocessor<C, R extends Requirement<C, R>>
/*    */   implements CommandPostprocessor<C>
/*    */ {
/*    */   private final CloudKey<Requirements<C, R>> requirementKey;
/*    */   private final RequirementFailureHandler<C, R> failureHandler;
/*    */   
/*    */   public static <C, R extends Requirement<C, R>> RequirementPostprocessor<C, R> of(CloudKey<Requirements<C, R>> requirementKey, RequirementFailureHandler<C, R> failureHandler) {
/* 57 */     return new RequirementPostprocessor<>(requirementKey, failureHandler);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private RequirementPostprocessor(CloudKey<Requirements<C, R>> requirementKey, RequirementFailureHandler<C, R> failureHandler) {
/* 67 */     this.requirementKey = Objects.<CloudKey<Requirements<C, R>>>requireNonNull(requirementKey, "requirementKey");
/* 68 */     this.failureHandler = Objects.<RequirementFailureHandler<C, R>>requireNonNull(failureHandler, "failureHandler");
/*    */   }
/*    */ 
/*    */   
/*    */   public void accept(CommandPostprocessingContext<C> context) {
/* 73 */     Requirements<C, R> requirements = (Requirements<C, R>)context.command().commandMeta().getOrDefault(this.requirementKey, null);
/* 74 */     if (requirements == null) {
/*    */       return;
/*    */     }
/*    */     
/* 78 */     for (Requirement requirement : requirements) {
/* 79 */       if (requirement.evaluateRequirement(context.commandContext())) {
/*    */         continue;
/*    */       }
/* 82 */       this.failureHandler.handleFailure(context.commandContext(), (R)requirement);
/* 83 */       ConsumerService.interrupt();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\processors\requirements\RequirementPostprocessor.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */