/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.score;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
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
/*    */ @NonExtendable
/*    */ public interface ScoreFormat
/*    */ {
/*    */   static BlankScoreFormat blankScore() {
/* 30 */     return BlankScoreFormat.INSTANCE;
/*    */   }
/*    */   
/*    */   static StyledScoreFormat styledScore(Style style) {
/* 34 */     return new StyledScoreFormat(style);
/*    */   }
/*    */   
/*    */   static FixedScoreFormat fixedScore(Component value) {
/* 38 */     return new FixedScoreFormat(value);
/*    */   }
/*    */   
/*    */   static ScoreFormat readTyped(PacketWrapper<?> wrapper) {
/* 42 */     return ((ScoreFormatType<ScoreFormat>)wrapper.readMappedEntity((IRegistry)ScoreFormatTypes.getRegistry())).read(wrapper);
/*    */   }
/*    */ 
/*    */   
/*    */   static <T extends ScoreFormat> void writeTyped(PacketWrapper<?> wrapper, T format) {
/* 47 */     wrapper.writeMappedEntity((MappedEntity)format.getType());
/* 48 */     format.getType().write(wrapper, format);
/*    */   }
/*    */   
/*    */   Component format(int paramInt);
/*    */   
/*    */   ScoreFormatType<?> getType();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\score\ScoreFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */