/*     */ package ac.grim.grimac.shaded.incendo.cloud.processors.requirements;
/*     */ 
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
/*     */ public final class RequirementApplicableFactory<C, R extends Requirement<C, R>>
/*     */ {
/*     */   private final CloudKey<Requirements<C, R>> requirementKey;
/*     */   
/*     */   private RequirementApplicableFactory(CloudKey<Requirements<C, R>> requirementKey) {
/*  91 */     this.requirementKey = Objects.<CloudKey<Requirements<C, R>>>requireNonNull(requirementKey, "requirementKey");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RequirementApplicable<C, R> create(Requirements<C, R> requirements) {
/* 101 */     Objects.requireNonNull(requirements, "requirements");
/* 102 */     return new RequirementApplicable<>(this.requirementKey, requirements);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RequirementApplicable<C, R> create(List<R> requirements) {
/* 112 */     Objects.requireNonNull(requirements, "requirements");
/* 113 */     return new RequirementApplicable<>(this.requirementKey, Requirements.of(requirements));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SafeVarargs
/*     */   public final RequirementApplicable<C, R> create(R... requirements) {
/* 125 */     Objects.requireNonNull(requirements, "requirements");
/* 126 */     return new RequirementApplicable<>(this.requirementKey, Requirements.of(requirements));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\processors\requirements\RequirementApplicable$RequirementApplicableFactory.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */