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
/*    */ public final class BlankScoreFormat
/*    */   implements ScoreFormat
/*    */ {
/* 26 */   public static final BlankScoreFormat INSTANCE = new BlankScoreFormat();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static BlankScoreFormat read(PacketWrapper<?> wrapper) {
/* 32 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, BlankScoreFormat format) {}
/*    */ 
/*    */   
/*    */   public Component format(int score) {
/* 41 */     return (Component)Component.empty();
/*    */   }
/*    */ 
/*    */   
/*    */   public ScoreFormatType<BlankScoreFormat> getType() {
/* 46 */     return ScoreFormatTypes.BLANK;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\score\BlankScoreFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */