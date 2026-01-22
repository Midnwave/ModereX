/*    */ package ac.grim.grimac.shaded.snakeyaml.tokens;
/*    */ 
/*    */ import ac.grim.grimac.shaded.snakeyaml.error.Mark;
/*    */ import ac.grim.grimac.shaded.snakeyaml.error.YAMLException;
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
/*    */ public abstract class Token
/*    */ {
/*    */   private final Mark startMark;
/*    */   private final Mark endMark;
/*    */   
/*    */   public enum ID
/*    */   {
/* 22 */     Alias("<alias>"), Anchor("<anchor>"), BlockEnd("<block end>"), BlockEntry("-"),
/* 23 */     BlockMappingStart("<block mapping start>"), BlockSequenceStart("<block sequence start>"),
/* 24 */     Directive("<directive>"), DocumentEnd("<document end>"),
/* 25 */     DocumentStart("<document start>"), FlowEntry(","),
/* 26 */     FlowMappingEnd("}"), FlowMappingStart("{"), FlowSequenceEnd("]"),
/* 27 */     FlowSequenceStart("["), Key("?"), Scalar("<scalar>"), StreamEnd("<stream end>"),
/* 28 */     StreamStart("<stream start>"), Tag("<tag>"), Value(":"),
/* 29 */     Whitespace("<whitespace>"), Comment("#"), Error("<error>");
/*    */     
/*    */     private final String description;
/*    */     
/*    */     ID(String s) {
/* 34 */       this.description = s;
/*    */     }
/*    */ 
/*    */     
/*    */     public String toString() {
/* 39 */       return this.description;
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Token(Mark startMark, Mark endMark) {
/* 47 */     if (startMark == null || endMark == null) {
/* 48 */       throw new YAMLException("Token requires marks.");
/*    */     }
/* 50 */     this.startMark = startMark;
/* 51 */     this.endMark = endMark;
/*    */   }
/*    */   
/*    */   public Mark getStartMark() {
/* 55 */     return this.startMark;
/*    */   }
/*    */   
/*    */   public Mark getEndMark() {
/* 59 */     return this.endMark;
/*    */   }
/*    */   
/*    */   public abstract ID getTokenId();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\tokens\Token.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */