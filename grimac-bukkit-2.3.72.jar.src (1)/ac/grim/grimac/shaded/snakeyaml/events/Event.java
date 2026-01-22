/*     */ package ac.grim.grimac.shaded.snakeyaml.events;
/*     */ 
/*     */ import ac.grim.grimac.shaded.snakeyaml.error.Mark;
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
/*     */ public abstract class Event
/*     */ {
/*     */   private final Mark startMark;
/*     */   private final Mark endMark;
/*     */   
/*     */   public enum ID
/*     */   {
/*  28 */     Alias, Comment, DocumentEnd, DocumentStart, MappingEnd, MappingStart, Scalar, SequenceEnd, SequenceStart, StreamEnd, StreamStart;
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
/*     */   public Event(Mark startMark, Mark endMark) {
/*  41 */     this.startMark = startMark;
/*  42 */     this.endMark = endMark;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  46 */     return "<" + getClass().getName() + "(" + getArguments() + ")>";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mark getStartMark() {
/*  55 */     return this.startMark;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mark getEndMark() {
/*  64 */     return this.endMark;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getArguments() {
/*  74 */     return "";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean is(ID id) {
/*  84 */     return (getEventId() == id);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract ID getEventId();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  99 */     if (obj instanceof Event) {
/* 100 */       return toString().equals(obj.toString());
/*     */     }
/* 102 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 111 */     return toString().hashCode();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\events\Event.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */