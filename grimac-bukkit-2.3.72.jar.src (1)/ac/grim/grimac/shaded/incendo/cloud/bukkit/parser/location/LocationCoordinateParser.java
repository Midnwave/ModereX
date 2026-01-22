/*    */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.location;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.DoubleParser;
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
/*    */ public final class LocationCoordinateParser<C>
/*    */   implements ArgumentParser<C, LocationCoordinate>
/*    */ {
/*    */   public ArgumentParseResult<LocationCoordinate> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/*    */     LocationCoordinateType locationCoordinateType;
/*    */     double coordinate;
/* 46 */     String input = commandInput.skipWhitespace().peekString();
/*    */ 
/*    */ 
/*    */     
/* 50 */     if (commandInput.peek() == '^') {
/* 51 */       locationCoordinateType = LocationCoordinateType.LOCAL;
/* 52 */       commandInput.moveCursor(1);
/* 53 */     } else if (commandInput.peek() == '~') {
/* 54 */       locationCoordinateType = LocationCoordinateType.RELATIVE;
/* 55 */       commandInput.moveCursor(1);
/*    */     } else {
/* 57 */       locationCoordinateType = LocationCoordinateType.ABSOLUTE;
/*    */     } 
/*    */ 
/*    */     
/*    */     try {
/* 62 */       boolean empty = (commandInput.peekString().isEmpty() || commandInput.peek() == ' ');
/* 63 */       coordinate = empty ? 0.0D : commandInput.readDouble();
/* 64 */       if (commandInput.hasRemainingInput()) {
/* 65 */         commandInput.skipWhitespace();
/*    */       }
/* 67 */     } catch (Exception e) {
/* 68 */       return ArgumentParseResult.failure((Throwable)new DoubleParser.DoubleParseException(input, new DoubleParser(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), commandContext));
/*    */     } 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 78 */     return ArgumentParseResult.success(
/* 79 */         LocationCoordinate.of(locationCoordinateType, coordinate));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\parser\location\LocationCoordinateParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */