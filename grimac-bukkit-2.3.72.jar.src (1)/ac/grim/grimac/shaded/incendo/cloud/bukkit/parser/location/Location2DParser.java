/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.location;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.BukkitCommandContextKeys;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.BlockingSuggestionProvider;
/*     */ import java.util.function.Supplier;
/*     */ import org.apiguardian.api.API;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.command.BlockCommandSender;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.util.Vector;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Location2DParser<C>
/*     */   implements ArgumentParser<C, Location2D>, BlockingSuggestionProvider.Strings<C>
/*     */ {
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> ParserDescriptor<C, Location2D> location2DParser() {
/*  61 */     return ParserDescriptor.of(new Location2DParser(), Location2D.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> CommandComponent.Builder<C, Location2D> location2DComponent() {
/*  73 */     return CommandComponent.builder().parser(location2DParser());
/*     */   }
/*     */   
/*  76 */   private final LocationCoordinateParser<C> locationCoordinateParser = new LocationCoordinateParser<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParseResult<Location2D> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/*     */     Location originalLocation;
/*  83 */     if (commandInput.remainingTokens() < 2) {
/*  84 */       return ArgumentParseResult.failure((Throwable)new LocationParser.LocationParseException(commandContext, LocationParser.LocationParseException.FailureReason.WRONG_FORMAT, commandInput
/*     */ 
/*     */ 
/*     */             
/*  88 */             .remainingInput()));
/*     */     }
/*     */ 
/*     */     
/*  92 */     LocationCoordinate[] coordinates = new LocationCoordinate[2];
/*  93 */     for (int i = 0; i < 2; i++) {
/*  94 */       if (commandInput.peekString().isEmpty()) {
/*  95 */         return ArgumentParseResult.failure((Throwable)new LocationParser.LocationParseException(commandContext, LocationParser.LocationParseException.FailureReason.WRONG_FORMAT, commandInput
/*     */ 
/*     */ 
/*     */               
/*  99 */               .remainingInput()));
/*     */       }
/*     */ 
/*     */       
/* 103 */       ArgumentParseResult<LocationCoordinate> coordinate = this.locationCoordinateParser.parse(commandContext, commandInput);
/*     */ 
/*     */ 
/*     */       
/* 107 */       if (coordinate.failure().isPresent()) {
/* 108 */         return ArgumentParseResult.failure(coordinate
/* 109 */             .failure().get());
/*     */       }
/*     */       
/* 112 */       coordinates[i] = (LocationCoordinate)coordinate.parsedValue().orElseThrow(NullPointerException::new);
/*     */     } 
/*     */     
/* 115 */     CommandSender bukkitSender = (CommandSender)commandContext.get(BukkitCommandContextKeys.BUKKIT_COMMAND_SENDER);
/*     */     
/* 117 */     if (bukkitSender instanceof BlockCommandSender) {
/* 118 */       originalLocation = ((BlockCommandSender)bukkitSender).getBlock().getLocation();
/* 119 */     } else if (bukkitSender instanceof Entity) {
/* 120 */       originalLocation = ((Entity)bukkitSender).getLocation();
/*     */     } else {
/* 122 */       originalLocation = new Location(Bukkit.getWorlds().get(0), 0.0D, 0.0D, 0.0D);
/*     */     } 
/*     */     
/* 125 */     if (coordinates[0].type() == LocationCoordinateType.LOCAL && coordinates[1].type() != LocationCoordinateType.LOCAL) {
/* 126 */       return ArgumentParseResult.failure((Throwable)new LocationParser.LocationParseException(commandContext, LocationParser.LocationParseException.FailureReason.MIXED_LOCAL_ABSOLUTE, ""));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 135 */     if (coordinates[0].type() == LocationCoordinateType.ABSOLUTE) {
/* 136 */       originalLocation.setX(coordinates[0].coordinate());
/* 137 */     } else if (coordinates[0].type() == LocationCoordinateType.RELATIVE) {
/* 138 */       originalLocation.add(coordinates[0].coordinate(), 0.0D, 0.0D);
/*     */     } 
/*     */     
/* 141 */     if (coordinates[1].type() == LocationCoordinateType.ABSOLUTE) {
/* 142 */       originalLocation.setZ(coordinates[1].coordinate());
/* 143 */     } else if (coordinates[1].type() == LocationCoordinateType.RELATIVE) {
/* 144 */       originalLocation.add(0.0D, 0.0D, coordinates[1].coordinate());
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 149 */       Vector declaredPos = new Vector(coordinates[0].coordinate(), 0.0D, coordinates[1].coordinate());
/*     */       
/* 151 */       Location local = LocationParser.toLocalSpace(originalLocation, declaredPos);
/* 152 */       return ArgumentParseResult.success(Location2D.from(originalLocation
/* 153 */             .getWorld(), local
/* 154 */             .getX(), local
/* 155 */             .getZ()));
/*     */     } 
/*     */ 
/*     */     
/* 159 */     return ArgumentParseResult.success(Location2D.from(originalLocation
/* 160 */           .getWorld(), originalLocation
/* 161 */           .getX(), originalLocation
/* 162 */           .getZ()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterable<String> stringSuggestions(CommandContext<C> commandContext, CommandInput input) {
/* 171 */     return LocationParser.getSuggestions(2, commandContext, input);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\parser\location\Location2DParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */