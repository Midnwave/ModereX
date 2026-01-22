/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.score;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
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
/*    */ public final class FixedScoreFormat
/*    */   implements ScoreFormat
/*    */ {
/*    */   private final Component value;
/*    */   
/*    */   public FixedScoreFormat(Component value) {
/* 29 */     this.value = value;
/*    */   }
/*    */   
/*    */   public static FixedScoreFormat read(PacketWrapper<?> wrapper) {
/* 33 */     return new FixedScoreFormat(wrapper.readComponent());
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, FixedScoreFormat format) {
/* 37 */     wrapper.writeComponent(format.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public Component format(int score) {
/* 42 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public ScoreFormatType<FixedScoreFormat> getType() {
/* 47 */     return ScoreFormatTypes.FIXED;
/*    */   }
/*    */   
/*    */   public Component getValue() {
/* 51 */     return this.value;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\score\FixedScoreFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */