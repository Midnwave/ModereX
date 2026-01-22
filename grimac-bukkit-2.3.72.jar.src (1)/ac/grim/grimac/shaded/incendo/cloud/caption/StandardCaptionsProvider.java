/*     */ package ac.grim.grimac.shaded.incendo.cloud.caption;
/*     */ 
/*     */ import org.apiguardian.api.API;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public final class StandardCaptionsProvider<C>
/*     */   extends DelegatingCaptionProvider<C>
/*     */ {
/*     */   public static final String ARGUMENT_PARSE_FAILURE_BOOLEAN = "Could not parse boolean from '<input>'";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_NUMBER = "'<input>' is not a valid number in the range <min> to <max>";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_CHAR = "'<input>' is not a valid character";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_ENUM = "'<input>' is not one of the following: <acceptableValues>";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_STRING = "'<input>' is not a valid string of type <stringMode>";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_UUID = "'<input>' is not a valid UUID";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_REGEX = "'<input>' does not match '<pattern>'";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_FLAG_UNKNOWN_FLAG = "Unknown flag '<flag>'";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_FLAG_DUPLICATE_FLAG = "Duplicate flag '<flag>'";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_FLAG_NO_FLAG_STARTED = "No flag started. Don't know what to do with '<input>'";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_FLAG_MISSING_ARGUMENT = "Missing argument for '<flag>'";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_FLAG_NO_PERMISSION = "You don't have permission to use '<flag>'";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_COLOR = "'<input>' is not a valid color";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_DURATION = "'<input>' is not a duration format";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_AGGREGATE_MISSING_INPUT = "Missing component '<component>'";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_AGGREGATE_COMPONENT_FAILURE = "Invalid component '<component>': <failure>";
/*     */   public static final String ARGUMENT_PARSE_FAILURE_EITHER = "Could not resolve <primary> or <fallback> from '<input>'";
/*     */   public static final String EXCEPTION_UNEXPECTED = "An internal error occurred while attempting to perform this command.";
/*     */   public static final String EXCEPTION_INVALID_ARGUMENT = "Invalid command argument: <cause>.";
/*     */   public static final String EXCEPTION_NO_SUCH_COMMAND = "Unknown command.";
/*     */   public static final String EXCEPTION_NO_PERMISSION = "I'm sorry, but you do not have permission to perform this command.";
/*     */   public static final String EXCEPTION_INVALID_SENDER = "<actual> is not allowed to execute that command. Must be of type <expected>";
/*     */   public static final String EXCEPTION_INVALID_SENDER_LIST = "<actual> is not allowed to execute that command. Must be one of <expected>";
/*     */   public static final String EXCEPTION_INVALID_SYNTAX = "Invalid command syntax. Correct command syntax is: <syntax>.";
/* 137 */   private static final CaptionProvider<?> PROVIDER = CaptionProvider.constantProvider()
/* 138 */     .putCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_BOOLEAN, "Could not parse boolean from '<input>'")
/*     */ 
/*     */     
/* 141 */     .putCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_NUMBER, "'<input>' is not a valid number in the range <min> to <max>")
/*     */ 
/*     */     
/* 144 */     .putCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_CHAR, "'<input>' is not a valid character")
/*     */ 
/*     */     
/* 147 */     .putCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_ENUM, "'<input>' is not one of the following: <acceptableValues>")
/*     */ 
/*     */     
/* 150 */     .putCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_STRING, "'<input>' is not a valid string of type <stringMode>")
/*     */ 
/*     */     
/* 153 */     .putCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_UUID, "'<input>' is not a valid UUID")
/*     */ 
/*     */     
/* 156 */     .putCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_REGEX, "'<input>' does not match '<pattern>'")
/*     */ 
/*     */     
/* 159 */     .putCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_COLOR, "'<input>' is not a valid color")
/*     */ 
/*     */     
/* 162 */     .putCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_DURATION, "'<input>' is not a duration format")
/*     */ 
/*     */     
/* 165 */     .putCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_UNKNOWN_FLAG, "Unknown flag '<flag>'")
/*     */ 
/*     */     
/* 168 */     .putCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_DUPLICATE_FLAG, "Duplicate flag '<flag>'")
/*     */ 
/*     */     
/* 171 */     .putCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_NO_FLAG_STARTED, "No flag started. Don't know what to do with '<input>'")
/*     */ 
/*     */     
/* 174 */     .putCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_MISSING_ARGUMENT, "Missing argument for '<flag>'")
/*     */ 
/*     */     
/* 177 */     .putCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_NO_PERMISSION, "You don't have permission to use '<flag>'")
/*     */ 
/*     */     
/* 180 */     .putCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_AGGREGATE_MISSING_INPUT, "Missing component '<component>'")
/*     */ 
/*     */     
/* 183 */     .putCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_AGGREGATE_COMPONENT_FAILURE, "Invalid component '<component>': <failure>")
/*     */ 
/*     */     
/* 186 */     .putCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_EITHER, "Could not resolve <primary> or <fallback> from '<input>'")
/*     */ 
/*     */     
/* 189 */     .putCaption(StandardCaptionKeys.EXCEPTION_UNEXPECTED, "An internal error occurred while attempting to perform this command.")
/*     */ 
/*     */     
/* 192 */     .putCaption(StandardCaptionKeys.EXCEPTION_INVALID_ARGUMENT, "Invalid command argument: <cause>.")
/*     */ 
/*     */     
/* 195 */     .putCaption(StandardCaptionKeys.EXCEPTION_NO_SUCH_COMMAND, "Unknown command.")
/*     */ 
/*     */     
/* 198 */     .putCaption(StandardCaptionKeys.EXCEPTION_NO_PERMISSION, "I'm sorry, but you do not have permission to perform this command.")
/*     */ 
/*     */     
/* 201 */     .putCaption(StandardCaptionKeys.EXCEPTION_INVALID_SENDER, "<actual> is not allowed to execute that command. Must be of type <expected>")
/*     */ 
/*     */     
/* 204 */     .putCaption(StandardCaptionKeys.EXCEPTION_INVALID_SENDER_LIST, "<actual> is not allowed to execute that command. Must be one of <expected>")
/*     */ 
/*     */     
/* 207 */     .putCaption(StandardCaptionKeys.EXCEPTION_INVALID_SYNTAX, "Invalid command syntax. Correct command syntax is: <syntax>.")
/*     */ 
/*     */     
/* 210 */     .build();
/*     */ 
/*     */ 
/*     */   
/*     */   public CaptionProvider<C> delegate() {
/* 215 */     return (CaptionProvider)PROVIDER;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\caption\StandardCaptionsProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */