/*     */ package ac.grim.grimac.shaded.incendo.cloud.description;
/*     */ 
/*     */ import org.apiguardian.api.API;
/*     */ import org.immutables.value.Value.Immutable;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ @Immutable
/*     */ public interface CommandDescription
/*     */   extends Describable
/*     */ {
/*     */   static CommandDescription empty() {
/*  45 */     return CommandDescriptionImpl.of(Description.empty(), Description.empty());
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
/*     */   static CommandDescription commandDescription(Description description, Description verboseDescription) {
/*  59 */     return CommandDescriptionImpl.of(description, verboseDescription);
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
/*     */   static CommandDescription commandDescription(Description description) {
/*  71 */     return CommandDescriptionImpl.of(description, description);
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
/*     */   static CommandDescription commandDescription(String description, String verboseDescription) {
/*  85 */     return CommandDescriptionImpl.of(Description.of(description), Description.of(verboseDescription));
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
/*     */   static CommandDescription commandDescription(String description) {
/*  97 */     return CommandDescriptionImpl.of(Description.of(description), Description.of(description));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Description description();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Description verboseDescription();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default boolean isEmpty() {
/* 121 */     return description().equals(Description.empty());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\description\CommandDescription.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */