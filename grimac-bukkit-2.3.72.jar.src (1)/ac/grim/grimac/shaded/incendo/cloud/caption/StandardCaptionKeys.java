/*     */ package ac.grim.grimac.shaded.incendo.cloud.caption;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedList;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public final class StandardCaptionKeys
/*     */ {
/*  38 */   private static final Collection<Caption> RECOGNIZED_CAPTIONS = new LinkedList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  43 */   public static final Caption ARGUMENT_PARSE_FAILURE_BOOLEAN = of("argument.parse.failure.boolean");
/*     */ 
/*     */ 
/*     */   
/*  47 */   public static final Caption ARGUMENT_PARSE_FAILURE_NUMBER = of("argument.parse.failure.number");
/*     */ 
/*     */ 
/*     */   
/*  51 */   public static final Caption ARGUMENT_PARSE_FAILURE_CHAR = of("argument.parse.failure.char");
/*     */ 
/*     */ 
/*     */   
/*  55 */   public static final Caption ARGUMENT_PARSE_FAILURE_STRING = of("argument.parse.failure.string");
/*     */ 
/*     */ 
/*     */   
/*  59 */   public static final Caption ARGUMENT_PARSE_FAILURE_UUID = of("argument.parse.failure.uuid");
/*     */ 
/*     */ 
/*     */   
/*  63 */   public static final Caption ARGUMENT_PARSE_FAILURE_ENUM = of("argument.parse.failure.enum");
/*     */ 
/*     */ 
/*     */   
/*  67 */   public static final Caption ARGUMENT_PARSE_FAILURE_REGEX = of("argument.parse.failure.regex");
/*     */ 
/*     */ 
/*     */   
/*  71 */   public static final Caption ARGUMENT_PARSE_FAILURE_FLAG_UNKNOWN_FLAG = of("argument.parse.failure.flag.unknown");
/*     */ 
/*     */ 
/*     */   
/*  75 */   public static final Caption ARGUMENT_PARSE_FAILURE_FLAG_DUPLICATE_FLAG = of("argument.parse.failure.flag.duplicate_flag");
/*     */ 
/*     */ 
/*     */   
/*  79 */   public static final Caption ARGUMENT_PARSE_FAILURE_FLAG_NO_FLAG_STARTED = of("argument.parse.failure.flag.no_flag_started");
/*     */ 
/*     */ 
/*     */   
/*  83 */   public static final Caption ARGUMENT_PARSE_FAILURE_FLAG_MISSING_ARGUMENT = of("argument.parse.failure.flag.missing_argument");
/*     */ 
/*     */ 
/*     */   
/*  87 */   public static final Caption ARGUMENT_PARSE_FAILURE_FLAG_NO_PERMISSION = of("argument.parse.failure.flag.no_permission");
/*     */ 
/*     */ 
/*     */   
/*  91 */   public static final Caption ARGUMENT_PARSE_FAILURE_COLOR = of("argument.parse.failure.color");
/*     */ 
/*     */ 
/*     */   
/*  95 */   public static final Caption ARGUMENT_PARSE_FAILURE_DURATION = of("argument.parse.failure.duration");
/*     */ 
/*     */ 
/*     */   
/*  99 */   public static final Caption ARGUMENT_PARSE_FAILURE_AGGREGATE_MISSING_INPUT = of("argument.parse.failure.aggregate.missing");
/*     */ 
/*     */ 
/*     */   
/* 103 */   public static final Caption ARGUMENT_PARSE_FAILURE_AGGREGATE_COMPONENT_FAILURE = of("argument.parse.failure.aggregate.failure");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 108 */   public static final Caption ARGUMENT_PARSE_FAILURE_EITHER = of("argument.parse.failure.either");
/*     */   
/* 110 */   public static final Caption EXCEPTION_UNEXPECTED = of("exception.unexpected");
/*     */ 
/*     */ 
/*     */   
/* 114 */   public static final Caption EXCEPTION_INVALID_ARGUMENT = of("exception.invalid_argument");
/*     */ 
/*     */ 
/*     */   
/* 118 */   public static final Caption EXCEPTION_NO_SUCH_COMMAND = of("exception.no_such_command");
/*     */ 
/*     */ 
/*     */   
/* 122 */   public static final Caption EXCEPTION_NO_PERMISSION = of("exception.no_permission");
/*     */ 
/*     */ 
/*     */   
/* 126 */   public static final Caption EXCEPTION_INVALID_SENDER = of("exception.invalid_sender");
/*     */ 
/*     */ 
/*     */   
/* 130 */   public static final Caption EXCEPTION_INVALID_SENDER_LIST = of("exception.invalid_sender_list");
/*     */ 
/*     */ 
/*     */   
/* 134 */   public static final Caption EXCEPTION_INVALID_SYNTAX = of("exception.invalid_syntax");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Caption of(String key) {
/* 140 */     Caption caption = Caption.of(key);
/* 141 */     RECOGNIZED_CAPTIONS.add(caption);
/* 142 */     return caption;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Collection<Caption> standardCaptionKeys() {
/* 151 */     return Collections.unmodifiableCollection(RECOGNIZED_CAPTIONS);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\caption\StandardCaptionKeys.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */