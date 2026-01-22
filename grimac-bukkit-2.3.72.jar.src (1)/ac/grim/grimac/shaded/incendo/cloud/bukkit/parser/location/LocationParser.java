/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.location;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.BukkitCaptionKeys;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.BukkitCommandContextKeys;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.Caption;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionVariable;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.parsing.ParserException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.IntegerParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.BlockingSuggestionProvider;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.Range;
/*     */ import java.util.List;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Collectors;
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
/*     */ public final class LocationParser<C>
/*     */   implements ArgumentParser<C, Location>, BlockingSuggestionProvider.Strings<C>
/*     */ {
/*  60 */   private static final Range<Integer> SUGGESTION_RANGE = (Range<Integer>)Range.intRange(-2147483648, 2147483647);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> ParserDescriptor<C, Location> locationParser() {
/*  71 */     return ParserDescriptor.of(new LocationParser(), Location.class);
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
/*     */   public static <C> CommandComponent.Builder<C, Location> locationComponent() {
/*  83 */     return CommandComponent.builder().parser(locationParser());
/*     */   }
/*     */   
/*  86 */   private final LocationCoordinateParser<C> locationCoordinateParser = new LocationCoordinateParser<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParseResult<Location> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/*     */     Location originalLocation;
/*  93 */     if (commandInput.remainingTokens() < 3) {
/*  94 */       return ArgumentParseResult.failure((Throwable)new LocationParseException(commandContext, LocationParseException.FailureReason.WRONG_FORMAT, commandInput
/*     */ 
/*     */ 
/*     */             
/*  98 */             .remainingInput()));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 103 */     LocationCoordinate[] coordinates = new LocationCoordinate[3];
/* 104 */     for (int i = 0; i < 3; i++) {
/* 105 */       if (commandInput.peekString().isEmpty()) {
/* 106 */         return ArgumentParseResult.failure((Throwable)new LocationParseException(commandContext, LocationParseException.FailureReason.WRONG_FORMAT, commandInput
/*     */ 
/*     */ 
/*     */               
/* 110 */               .remainingInput()));
/*     */       }
/*     */ 
/*     */       
/* 114 */       ArgumentParseResult<LocationCoordinate> coordinate = this.locationCoordinateParser.parse(commandContext, commandInput);
/*     */ 
/*     */ 
/*     */       
/* 118 */       if (coordinate.failure().isPresent()) {
/* 119 */         return ArgumentParseResult.failure(coordinate
/* 120 */             .failure().get());
/*     */       }
/*     */       
/* 123 */       coordinates[i] = (LocationCoordinate)coordinate.parsedValue().orElseThrow(NullPointerException::new);
/*     */     } 
/*     */     
/* 126 */     CommandSender bukkitSender = (CommandSender)commandContext.get(BukkitCommandContextKeys.BUKKIT_COMMAND_SENDER);
/*     */     
/* 128 */     if (bukkitSender instanceof BlockCommandSender) {
/* 129 */       originalLocation = ((BlockCommandSender)bukkitSender).getBlock().getLocation();
/* 130 */     } else if (bukkitSender instanceof Entity) {
/* 131 */       originalLocation = ((Entity)bukkitSender).getLocation();
/*     */     }
/* 133 */     else if (Bukkit.getWorlds().isEmpty()) {
/*     */       
/* 135 */       originalLocation = new Location(null, 0.0D, 0.0D, 0.0D);
/*     */     } else {
/* 137 */       originalLocation = new Location(Bukkit.getWorlds().get(0), 0.0D, 0.0D, 0.0D);
/*     */     } 
/*     */ 
/*     */     
/* 141 */     if (((coordinates[0].type() == LocationCoordinateType.LOCAL) ? true : false) == (
/* 142 */       (coordinates[1].type() == LocationCoordinateType.LOCAL) ? true : false))
/* 143 */       if (((coordinates[0].type() == LocationCoordinateType.LOCAL) ? true : false) == (
/* 144 */         (coordinates[2].type() == LocationCoordinateType.LOCAL) ? true : false)) {
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
/* 155 */         if (coordinates[0].type() == LocationCoordinateType.ABSOLUTE) {
/* 156 */           originalLocation.setX(coordinates[0].coordinate());
/* 157 */         } else if (coordinates[0].type() == LocationCoordinateType.RELATIVE) {
/* 158 */           originalLocation.add(coordinates[0].coordinate(), 0.0D, 0.0D);
/*     */         } 
/*     */         
/* 161 */         if (coordinates[1].type() == LocationCoordinateType.ABSOLUTE) {
/* 162 */           originalLocation.setY(coordinates[1].coordinate());
/* 163 */         } else if (coordinates[1].type() == LocationCoordinateType.RELATIVE) {
/* 164 */           originalLocation.add(0.0D, coordinates[1].coordinate(), 0.0D);
/*     */         } 
/*     */         
/* 167 */         if (coordinates[2].type() == LocationCoordinateType.ABSOLUTE) {
/* 168 */           originalLocation.setZ(coordinates[2].coordinate());
/* 169 */         } else if (coordinates[2].type() == LocationCoordinateType.RELATIVE) {
/* 170 */           originalLocation.add(0.0D, 0.0D, coordinates[2].coordinate());
/*     */         
/*     */         }
/*     */         else {
/*     */           
/* 175 */           Vector declaredPos = new Vector(coordinates[0].coordinate(), coordinates[1].coordinate(), coordinates[2].coordinate());
/*     */           
/* 177 */           return ArgumentParseResult.success(
/* 178 */               toLocalSpace(originalLocation, declaredPos));
/*     */         } 
/*     */ 
/*     */         
/* 182 */         return ArgumentParseResult.success(originalLocation);
/*     */       }  
/*     */     return ArgumentParseResult.failure((Throwable)new LocationParseException(commandContext, LocationParseException.FailureReason.MIXED_LOCAL_ABSOLUTE, ""));
/*     */   }
/*     */   
/*     */   static Location toLocalSpace(Location originalLocation, Vector declaredPos) {
/* 188 */     double cosYaw = Math.cos(toRadians(originalLocation.getYaw() + 90.0F));
/* 189 */     double sinYaw = Math.sin(toRadians(originalLocation.getYaw() + 90.0F));
/* 190 */     double cosPitch = Math.cos(toRadians(-originalLocation.getPitch()));
/* 191 */     double sinPitch = Math.sin(toRadians(-originalLocation.getPitch()));
/* 192 */     double cosNegYaw = Math.cos(toRadians(-originalLocation.getPitch() + 90.0F));
/* 193 */     double sinNegYaw = Math.sin(toRadians(-originalLocation.getPitch() + 90.0F));
/* 194 */     Vector zModifier = new Vector(cosYaw * cosPitch, sinPitch, sinYaw * cosPitch);
/* 195 */     Vector yModifier = new Vector(cosYaw * cosNegYaw, sinNegYaw, sinYaw * cosNegYaw);
/* 196 */     Vector xModifier = zModifier.crossProduct(yModifier).multiply(-1);
/* 197 */     double xOffset = dotProduct(declaredPos, xModifier.getX(), yModifier.getX(), zModifier.getX());
/* 198 */     double yOffset = dotProduct(declaredPos, xModifier.getY(), yModifier.getY(), zModifier.getY());
/* 199 */     double zOffset = dotProduct(declaredPos, xModifier.getZ(), yModifier.getZ(), zModifier.getZ());
/* 200 */     return originalLocation.add(xOffset, yOffset, zOffset);
/*     */   }
/*     */   
/*     */   private static double dotProduct(Vector location, double x, double y, double z) {
/* 204 */     return location.getX() * x + location.getY() * y + location.getZ() * z;
/*     */   }
/*     */   
/*     */   private static float toRadians(float degrees) {
/* 208 */     return degrees * 3.1415927F / 180.0F;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterable<String> stringSuggestions(CommandContext<C> commandContext, CommandInput input) {
/* 216 */     return getSuggestions(3, commandContext, input);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static <C> List<String> getSuggestions(int components, CommandContext<C> commandContext, CommandInput input) {
/* 224 */     CommandInput inputCopy = input.copy();
/*     */     
/* 226 */     int idx = input.cursor();
/* 227 */     for (int i = 0; i < components; i++) {
/* 228 */       idx = input.cursor();
/* 229 */       if (!input.hasRemainingInput(true)) {
/*     */         break;
/*     */       }
/* 232 */       ArgumentParseResult<LocationCoordinate> coordinateResult = (new LocationCoordinateParser<>()).parse(commandContext, input);
/*     */ 
/*     */ 
/*     */       
/* 236 */       if (coordinateResult.failure().isPresent()) {
/*     */         break;
/*     */       }
/*     */     } 
/* 240 */     input.cursor(idx);
/*     */     
/* 242 */     if (input.hasRemainingInput() && (input.peek() == '~' || input.peek() == '^')) {
/* 243 */       input.read();
/*     */     }
/*     */     
/* 246 */     String prefix = inputCopy.difference(input, true);
/*     */     
/* 248 */     return (List<String>)IntegerParser.getSuggestions(SUGGESTION_RANGE, input)
/*     */ 
/*     */       
/* 251 */       .stream().map(string -> prefix + string).collect(Collectors.toList());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class LocationParseException
/*     */     extends ParserException
/*     */   {
/*     */     protected LocationParseException(CommandContext<?> context, FailureReason reason, String input) {
/* 263 */       super(LocationParser.class, context, reason
/*     */ 
/*     */           
/* 266 */           .caption(), new CaptionVariable[] {
/* 267 */             CaptionVariable.of("input", input)
/*     */           });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public enum FailureReason
/*     */     {
/* 277 */       WRONG_FORMAT((String)BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_LOCATION_INVALID_FORMAT),
/* 278 */       MIXED_LOCAL_ABSOLUTE((String)BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_LOCATION_MIXED_LOCAL_ABSOLUTE);
/*     */       
/*     */       private final Caption caption;
/*     */ 
/*     */       
/*     */       FailureReason(Caption caption) {
/* 284 */         this.caption = caption;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       public Caption caption()
/*     */       {
/* 293 */         return this.caption; } } } public enum FailureReason { public Caption caption() { return this.caption; }
/*     */ 
/*     */     
/*     */     WRONG_FORMAT((String)BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_LOCATION_INVALID_FORMAT),
/*     */     MIXED_LOCAL_ABSOLUTE((String)BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_LOCATION_MIXED_LOCAL_ABSOLUTE);
/*     */     private final Caption caption;
/*     */     
/*     */     FailureReason(Caption caption) {
/*     */       this.caption = caption;
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\parser\location\LocationParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */