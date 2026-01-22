/*     */ package ac.grim.grimac.shaded.incendo.cloud.processors.requirements;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKey;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import org.apiguardian.api.API;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.STABLE, since = "1.0.0")
/*     */ public final class RequirementApplicable<C, R extends Requirement<C, R>>
/*     */   implements Command.Builder.Applicable<C>
/*     */ {
/*     */   private final CloudKey<Requirements<C, R>> requirementKey;
/*     */   private final Requirements<C, R> requirements;
/*     */   
/*     */   public static <C, R extends Requirement<C, R>> RequirementApplicableFactory<C, R> factory(CloudKey<Requirements<C, R>> requirementKey) {
/*  58 */     return new RequirementApplicableFactory<>(requirementKey);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private RequirementApplicable(CloudKey<Requirements<C, R>> requirementKey, Requirements<C, R> requirements) {
/*  68 */     this.requirementKey = Objects.<CloudKey<Requirements<C, R>>>requireNonNull(requirementKey, "requirementKey");
/*  69 */     this.requirements = Objects.<Requirements<C, R>>requireNonNull(requirements, "requirements");
/*     */   }
/*     */ 
/*     */   
/*     */   public Command.Builder<C> applyToCommandBuilder(Command.Builder<C> builder) {
/*  74 */     return builder.meta(this.requirementKey, this.requirements);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "1.0.0")
/*     */   public static final class RequirementApplicableFactory<C, R extends Requirement<C, R>>
/*     */   {
/*     */     private final CloudKey<Requirements<C, R>> requirementKey;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private RequirementApplicableFactory(CloudKey<Requirements<C, R>> requirementKey) {
/*  91 */       this.requirementKey = Objects.<CloudKey<Requirements<C, R>>>requireNonNull(requirementKey, "requirementKey");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public RequirementApplicable<C, R> create(Requirements<C, R> requirements) {
/* 101 */       Objects.requireNonNull(requirements, "requirements");
/* 102 */       return new RequirementApplicable<>(this.requirementKey, requirements);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public RequirementApplicable<C, R> create(List<R> requirements) {
/* 112 */       Objects.requireNonNull(requirements, "requirements");
/* 113 */       return new RequirementApplicable<>(this.requirementKey, Requirements.of(requirements));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @SafeVarargs
/*     */     public final RequirementApplicable<C, R> create(R... requirements) {
/* 125 */       Objects.requireNonNull(requirements, "requirements");
/* 126 */       return new RequirementApplicable<>(this.requirementKey, Requirements.of(requirements));
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\processors\requirements\RequirementApplicable.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */