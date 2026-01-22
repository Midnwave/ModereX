/*     */ package ac.grim.grimac.shaded.incendo.cloud.processors.requirements;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
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
/*     */ @API(status = API.Status.STABLE, since = "1.0.0")
/*     */ public interface Requirements<C, R extends Requirement<C, R>>
/*     */   extends Iterable<R>
/*     */ {
/*     */   private static <C, R extends Requirement<C, R>> List<R> extractRequirements(List<R> requirements) {
/*  47 */     Objects.requireNonNull(requirements, "requirements");
/*  48 */     List<R> extractedRequirements = new ArrayList<>();
/*  49 */     for (Requirement requirement : requirements) {
/*  50 */       Objects.requireNonNull(requirement, "requirement");
/*  51 */       for (Requirement requirement1 : extractRequirements(requirement.parents())) {
/*  52 */         if (!extractedRequirements.contains(requirement1)) {
/*  53 */           extractedRequirements.add((R)requirement1);
/*     */         }
/*     */       } 
/*  56 */       if (!extractedRequirements.contains(requirement)) {
/*  57 */         extractedRequirements.add((R)requirement);
/*     */       }
/*     */     } 
/*  60 */     return List.copyOf(extractedRequirements);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static <C, R extends Requirement<C, R>> Requirements<C, R> empty() {
/*  71 */     return new RequirementsImpl<>(List.of());
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
/*     */ 
/*     */   
/*     */   static <C, R extends Requirement<C, R>> Requirements<C, R> of(List<R> requirements) {
/*  85 */     return new RequirementsImpl<>(extractRequirements(requirements));
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
/*     */ 
/*     */ 
/*     */   
/*     */   @SafeVarargs
/*     */   static <C, R extends Requirement<C, R>> Requirements<C, R> of(R... requirements) {
/* 101 */     return of(Arrays.asList(requirements));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default Requirements<C, R> with(R requirement) {
/* 111 */     List<R> requirements = new ArrayList<>(requirements());
/* 112 */     requirements.add(requirement);
/* 113 */     return of(requirements);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   List<R> requirements();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default Iterator<R> iterator() {
/* 130 */     return requirements().iterator();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\processors\requirements\Requirements.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */