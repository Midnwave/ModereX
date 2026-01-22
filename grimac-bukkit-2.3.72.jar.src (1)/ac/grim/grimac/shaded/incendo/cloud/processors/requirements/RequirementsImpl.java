/*    */ package ac.grim.grimac.shaded.incendo.cloud.processors.requirements;
/*    */ 
/*    */ import java.util.List;
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
/*    */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.requirement.*"}, since = "1.0.0")
/*    */ final class RequirementsImpl<C, R extends Requirement<C, R>>
/*    */   extends Record
/*    */   implements Requirements<C, R>
/*    */ {
/*    */   private final List<R> requirements;
/*    */   
/*    */   public List<R> requirements() {
/* 30 */     return this.requirements; } RequirementsImpl(List<R> requirements) {
/* 31 */     this.requirements = requirements;
/*    */   }
/*    */   
/*    */   public final boolean equals(Object o) {
/*    */     // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: aload_1
/*    */     //   2: <illegal opcode> equals : (Lac/grim/grimac/shaded/incendo/cloud/processors/requirements/RequirementsImpl;Ljava/lang/Object;)Z
/*    */     //   7: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #30	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	8	0	this	Lac/grim/grimac/shaded/incendo/cloud/processors/requirements/RequirementsImpl;
/*    */     //   0	8	1	o	Ljava/lang/Object;
/*    */     // Local variable type table:
/*    */     //   start	length	slot	name	signature
/*    */     //   0	8	0	this	Lac/grim/grimac/shaded/incendo/cloud/processors/requirements/RequirementsImpl<TC;TR;>;
/*    */   }
/*    */   
/*    */   public final int hashCode() {
/*    */     // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> hashCode : (Lac/grim/grimac/shaded/incendo/cloud/processors/requirements/RequirementsImpl;)I
/*    */     //   6: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #30	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	7	0	this	Lac/grim/grimac/shaded/incendo/cloud/processors/requirements/RequirementsImpl;
/*    */     // Local variable type table:
/*    */     //   start	length	slot	name	signature
/*    */     //   0	7	0	this	Lac/grim/grimac/shaded/incendo/cloud/processors/requirements/RequirementsImpl<TC;TR;>;
/*    */   }
/*    */   
/*    */   public final String toString() {
/*    */     // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> toString : (Lac/grim/grimac/shaded/incendo/cloud/processors/requirements/RequirementsImpl;)Ljava/lang/String;
/*    */     //   6: areturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #30	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	7	0	this	Lac/grim/grimac/shaded/incendo/cloud/processors/requirements/RequirementsImpl;
/*    */     // Local variable type table:
/*    */     //   start	length	slot	name	signature
/*    */     //   0	7	0	this	Lac/grim/grimac/shaded/incendo/cloud/processors/requirements/RequirementsImpl<TC;TR;>;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\processors\requirements\RequirementsImpl.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */