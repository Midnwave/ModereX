/*    */ package ac.grim.grimac.shaded.snakeyaml.error;
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
/*    */ public class MarkedYAMLException
/*    */   extends YAMLException
/*    */ {
/*    */   private static final long serialVersionUID = -9119388488683035101L;
/*    */   private final String context;
/*    */   private final Mark contextMark;
/*    */   private final String problem;
/*    */   private final Mark problemMark;
/*    */   private final String note;
/*    */   
/*    */   protected MarkedYAMLException(String context, Mark contextMark, String problem, Mark problemMark, String note) {
/* 27 */     this(context, contextMark, problem, problemMark, note, null);
/*    */   }
/*    */ 
/*    */   
/*    */   protected MarkedYAMLException(String context, Mark contextMark, String problem, Mark problemMark, String note, Throwable cause) {
/* 32 */     super(context + "; " + problem + "; " + problemMark, cause);
/* 33 */     this.context = context;
/* 34 */     this.contextMark = contextMark;
/* 35 */     this.problem = problem;
/* 36 */     this.problemMark = problemMark;
/* 37 */     this.note = note;
/*    */   }
/*    */ 
/*    */   
/*    */   protected MarkedYAMLException(String context, Mark contextMark, String problem, Mark problemMark) {
/* 42 */     this(context, contextMark, problem, problemMark, null, null);
/*    */   }
/*    */ 
/*    */   
/*    */   protected MarkedYAMLException(String context, Mark contextMark, String problem, Mark problemMark, Throwable cause) {
/* 47 */     this(context, contextMark, problem, problemMark, null, cause);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 52 */     return toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 57 */     StringBuilder lines = new StringBuilder();
/* 58 */     if (this.context != null) {
/* 59 */       lines.append(this.context);
/* 60 */       lines.append("\n");
/*    */     } 
/* 62 */     if (this.contextMark != null && (this.problem == null || this.problemMark == null || this.contextMark
/* 63 */       .getName().equals(this.problemMark.getName()) || this.contextMark
/* 64 */       .getLine() != this.problemMark.getLine() || this.contextMark
/* 65 */       .getColumn() != this.problemMark.getColumn())) {
/* 66 */       lines.append(this.contextMark);
/* 67 */       lines.append("\n");
/*    */     } 
/* 69 */     if (this.problem != null) {
/* 70 */       lines.append(this.problem);
/* 71 */       lines.append("\n");
/*    */     } 
/* 73 */     if (this.problemMark != null) {
/* 74 */       lines.append(this.problemMark);
/* 75 */       lines.append("\n");
/*    */     } 
/* 77 */     if (this.note != null) {
/* 78 */       lines.append(this.note);
/* 79 */       lines.append("\n");
/*    */     } 
/* 81 */     return lines.toString();
/*    */   }
/*    */   
/*    */   public String getContext() {
/* 85 */     return this.context;
/*    */   }
/*    */   
/*    */   public Mark getContextMark() {
/* 89 */     return this.contextMark;
/*    */   }
/*    */   
/*    */   public String getProblem() {
/* 93 */     return this.problem;
/*    */   }
/*    */   
/*    */   public Mark getProblemMark() {
/* 97 */     return this.problemMark;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\error\MarkedYAMLException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */