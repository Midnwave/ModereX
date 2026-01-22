/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.score;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
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
/*    */ public final class StyledScoreFormat
/*    */   implements ScoreFormat
/*    */ {
/*    */   private final Style style;
/*    */   
/*    */   public StyledScoreFormat(Style style) {
/* 30 */     this.style = style;
/*    */   }
/*    */   
/*    */   public static StyledScoreFormat read(PacketWrapper<?> wrapper) {
/* 34 */     return new StyledScoreFormat(wrapper.readStyle());
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, StyledScoreFormat format) {
/* 38 */     wrapper.writeStyle(format.style);
/*    */   }
/*    */ 
/*    */   
/*    */   public Component format(int score) {
/* 43 */     return (Component)Component.text(Integer.toString(score), this.style);
/*    */   }
/*    */ 
/*    */   
/*    */   public ScoreFormatType<StyledScoreFormat> getType() {
/* 48 */     return ScoreFormatTypes.STYLED;
/*    */   }
/*    */   
/*    */   public Style getStyle() {
/* 52 */     return this.style;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\score\StyledScoreFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */