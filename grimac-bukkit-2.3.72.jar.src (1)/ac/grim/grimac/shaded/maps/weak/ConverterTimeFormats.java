/*     */ package ac.grim.grimac.shaded.maps.weak;
/*     */ 
/*     */ import java.time.Instant;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.ZoneId;
/*     */ import java.time.format.DateTimeFormatter;
/*     */ import java.time.format.DateTimeFormatterBuilder;
/*     */ import java.time.format.SignStyle;
/*     */ import java.time.temporal.ChronoField;
/*     */ import java.time.temporal.TemporalAccessor;
/*     */ import java.util.function.Function;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConverterTimeFormats
/*     */ {
/*  22 */   public static DateTimeFormatter ISO_PERMISSIVE = (new DateTimeFormatterBuilder())
/*  23 */     .parseLenient()
/*  24 */     .parseCaseInsensitive()
/*  25 */     .appendValue(ChronoField.YEAR, 1, 10, SignStyle.EXCEEDS_PAD)
/*  26 */     .appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, SignStyle.NOT_NEGATIVE)
/*  27 */     .optionalStart()
/*  28 */     .appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE)
/*  29 */     .optionalStart().appendLiteral('T').optionalEnd()
/*  30 */     .optionalStart().appendLiteral(' ').optionalEnd()
/*  31 */     .optionalStart()
/*  32 */     .appendValue(ChronoField.HOUR_OF_DAY, 1, 2, SignStyle.NOT_NEGATIVE)
/*  33 */     .optionalStart()
/*  34 */     .appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR, 1, 2, SignStyle.NOT_NEGATIVE).optionalEnd()
/*  35 */     .optionalStart()
/*  36 */     .appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE, 1, 2, SignStyle.NOT_NEGATIVE).optionalEnd()
/*  37 */     .optionalStart()
/*  38 */     .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).parseDefaulting(ChronoField.NANO_OF_SECOND, 0L).optionalEnd()
/*  39 */     .optionalStart().appendZoneOrOffsetId().optionalEnd()
/*  40 */     .optionalStart().appendOffset("+HHmm", "+0000").optionalEnd()
/*     */     
/*  42 */     .optionalStart()
/*  43 */     .appendLiteral('[').parseCaseSensitive().appendZoneRegionId().appendLiteral(']')
/*  44 */     .toFormatter();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  50 */   public static DateTimeFormatter ISO_PERMISSIVE_WITH_DEFAULTS = (new DateTimeFormatterBuilder())
/*  51 */     .append(ISO_PERMISSIVE)
/*  52 */     .parseDefaulting(ChronoField.DAY_OF_MONTH, 1L)
/*  53 */     .parseDefaulting(ChronoField.HOUR_OF_DAY, 0L)
/*  54 */     .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0L)
/*  55 */     .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0L)
/*  56 */     .parseDefaulting(ChronoField.NANO_OF_SECOND, 0L)
/*  57 */     .toFormatter();
/*     */ 
/*     */   
/*  60 */   public static DateTimeFormatter ISO_LONESOME_YEAR = (new DateTimeFormatterBuilder())
/*  61 */     .parseStrict()
/*  62 */     .appendValue(ChronoField.YEAR, 1, 4, SignStyle.NORMAL)
/*  63 */     .toFormatter();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  69 */   public static DateTimeFormatter ISO_LONESOME_YEAR_WITH_DEFAULTS = (new DateTimeFormatterBuilder())
/*  70 */     .append(ISO_LONESOME_YEAR)
/*  71 */     .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1L)
/*  72 */     .parseDefaulting(ChronoField.DAY_OF_MONTH, 1L)
/*  73 */     .parseDefaulting(ChronoField.HOUR_OF_DAY, 0L)
/*  74 */     .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0L)
/*  75 */     .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0L)
/*  76 */     .parseDefaulting(ChronoField.NANO_OF_SECOND, 0L)
/*  77 */     .toFormatter();
/*     */ 
/*     */   
/*  80 */   public static DateTimeFormatter DAY_MONTH_YEAR_PERMISSIVE_DASH = (new DateTimeFormatterBuilder())
/*  81 */     .parseCaseInsensitive()
/*  82 */     .parseLenient()
/*  83 */     .appendPattern("dd")
/*  84 */     .appendLiteral('-')
/*  85 */     .appendPattern("MMM")
/*  86 */     .appendLiteral('-')
/*  87 */     .appendValue(ChronoField.YEAR, 1, 10, SignStyle.EXCEEDS_PAD)
/*  88 */     .optionalStart()
/*  89 */     .appendLiteral(' ').appendValue(ChronoField.HOUR_OF_DAY, 1, 2, SignStyle.NOT_NEGATIVE)
/*  90 */     .optionalStart()
/*  91 */     .appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR, 1, 2, SignStyle.NOT_NEGATIVE)
/*  92 */     .optionalStart()
/*  93 */     .appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE, 1, 2, SignStyle.NOT_NEGATIVE)
/*  94 */     .optionalStart()
/*  95 */     .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
/*  96 */     .optionalStart()
/*  97 */     .appendOffsetId()
/*  98 */     .toFormatter();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 104 */   public static DateTimeFormatter DAY_MONTH_YEAR_PERMISSIVE_DASH_WITH_DEFAULTS = (new DateTimeFormatterBuilder())
/* 105 */     .append(DAY_MONTH_YEAR_PERMISSIVE_DASH)
/* 106 */     .parseDefaulting(ChronoField.HOUR_OF_DAY, 0L)
/* 107 */     .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0L)
/* 108 */     .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0L)
/* 109 */     .parseDefaulting(ChronoField.NANO_OF_SECOND, 0L)
/* 110 */     .toFormatter();
/*     */ 
/*     */   
/* 113 */   public static DateTimeFormatter DAY_MONTH_YEAR_PERMISSIVE_SLASH = (new DateTimeFormatterBuilder())
/* 114 */     .parseCaseInsensitive()
/* 115 */     .parseLenient()
/* 116 */     .appendPattern("dd")
/* 117 */     .appendLiteral('/')
/* 118 */     .appendPattern("MMM")
/* 119 */     .appendLiteral('/')
/* 120 */     .appendValue(ChronoField.YEAR, 1, 10, SignStyle.EXCEEDS_PAD)
/* 121 */     .optionalStart()
/* 122 */     .appendLiteral(' ').appendValue(ChronoField.HOUR_OF_DAY, 1, 2, SignStyle.NOT_NEGATIVE)
/* 123 */     .optionalStart()
/* 124 */     .appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR, 1, 2, SignStyle.NOT_NEGATIVE)
/* 125 */     .optionalStart()
/* 126 */     .appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE, 1, 2, SignStyle.NOT_NEGATIVE)
/* 127 */     .optionalStart()
/* 128 */     .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
/* 129 */     .optionalStart()
/* 130 */     .appendOffsetId()
/* 131 */     .toFormatter();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 137 */   public static DateTimeFormatter DAY_MONTH_YEAR_PERMISSIVE_SLASH_WITH_DEFAULTS = (new DateTimeFormatterBuilder())
/* 138 */     .append(DAY_MONTH_YEAR_PERMISSIVE_SLASH)
/* 139 */     .parseDefaulting(ChronoField.HOUR_OF_DAY, 0L)
/* 140 */     .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0L)
/* 141 */     .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0L)
/* 142 */     .parseDefaulting(ChronoField.NANO_OF_SECOND, 0L)
/* 143 */     .toFormatter();
/*     */ 
/*     */   
/* 146 */   public static DateTimeFormatter UTIL_DATE_TO_STRING = (new DateTimeFormatterBuilder())
/* 147 */     .parseLenient()
/* 148 */     .appendPattern("EEE")
/* 149 */     .appendLiteral(' ').appendPattern("MMM")
/* 150 */     .appendLiteral(' ').appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE)
/* 151 */     .appendLiteral(' ').appendValue(ChronoField.HOUR_OF_DAY, 1, 2, SignStyle.NOT_NEGATIVE)
/* 152 */     .appendLiteral(':').appendPattern("mm")
/* 153 */     .optionalStart()
/* 154 */     .appendLiteral(':').appendPattern("ss")
/* 155 */     .optionalStart().appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).optionalEnd()
/* 156 */     .optionalEnd()
/* 157 */     .appendLiteral(' ').appendPattern("zzz")
/* 158 */     .appendLiteral(' ').appendPattern("yyyy")
/* 159 */     .toFormatter();
/*     */   
/* 161 */   public static DateTimeFormatter UTIL_DATE_WITHOUT_ZONE_TO_STRING = (new DateTimeFormatterBuilder())
/* 162 */     .parseLenient()
/* 163 */     .appendPattern("EEE")
/* 164 */     .appendLiteral(' ').appendPattern("MMM")
/* 165 */     .appendLiteral(' ').appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE)
/* 166 */     .appendLiteral(' ').appendValue(ChronoField.HOUR_OF_DAY, 1, 2, SignStyle.NOT_NEGATIVE)
/* 167 */     .appendLiteral(':').appendPattern("mm")
/* 168 */     .optionalStart()
/* 169 */     .appendLiteral(':').appendPattern("ss")
/* 170 */     .optionalStart().appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).optionalEnd()
/* 171 */     .optionalEnd()
/* 172 */     .appendLiteral(' ').appendPattern("yyyy")
/* 173 */     .toFormatter();
/*     */   
/*     */   static {
/* 176 */     EPOCH_MILLIS_PARSER = (s -> {
/*     */         Long millis = Long.valueOf(Converter.convert(s).intoLong());
/*     */         if (millis.longValue() > -10000L && millis.longValue() < 10000L) {
/*     */           throw new IllegalArgumentException("Small value '" + s + "' indicates it is not valid epoch millis");
/*     */         }
/*     */         return LocalDateTime.ofInstant(Instant.ofEpochMilli(Converter.convert(s).intoLong()), ZoneId.systemDefault());
/*     */       });
/*     */   }
/*     */ 
/*     */   
/*     */   static final Function<CharSequence, TemporalAccessor> EPOCH_MILLIS_PARSER;
/*     */   
/* 188 */   public static final Function<CharSequence, TemporalAccessor> ALL_PARSER = orderedParseAttempter((Function<CharSequence, TemporalAccessor>[])new Function[] { ISO_PERMISSIVE::parse, DAY_MONTH_YEAR_PERMISSIVE_DASH::parse, DAY_MONTH_YEAR_PERMISSIVE_SLASH::parse, UTIL_DATE_TO_STRING::parse, UTIL_DATE_WITHOUT_ZONE_TO_STRING::parse, ISO_LONESOME_YEAR::parse, EPOCH_MILLIS_PARSER });
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
/* 202 */   public static final Function<CharSequence, TemporalAccessor> ALL_PARSER_WITH_DEFAULTS = orderedParseAttempter((Function<CharSequence, TemporalAccessor>[])new Function[] { ISO_PERMISSIVE_WITH_DEFAULTS::parse, DAY_MONTH_YEAR_PERMISSIVE_DASH_WITH_DEFAULTS::parse, DAY_MONTH_YEAR_PERMISSIVE_SLASH_WITH_DEFAULTS::parse, UTIL_DATE_TO_STRING::parse, UTIL_DATE_WITHOUT_ZONE_TO_STRING::parse, ISO_LONESOME_YEAR_WITH_DEFAULTS::parse, EPOCH_MILLIS_PARSER });
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
/*     */   @SafeVarargs
/*     */   public static Function<CharSequence, TemporalAccessor> orderedParseAttempter(Function<CharSequence, TemporalAccessor>... parsers) {
/* 219 */     return date -> {
/*     */         RuntimeException first = null; for (Function<CharSequence, TemporalAccessor> parser : parsers) {
/*     */           try {
/*     */             return parser.apply(date);
/* 223 */           } catch (RuntimeException ex) {
/*     */             if (first == null) {
/*     */               first = ex;
/*     */             }
/*     */           } 
/*     */         } 
/*     */         if (first == null) {
/*     */           throw new IllegalStateException("Empty parse attempter");
/*     */         }
/*     */         throw first;
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public static TemporalAccessor parse(CharSequence dateChars) {
/* 238 */     return ALL_PARSER.apply(dateChars);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TemporalAccessor parseWithDefaults(CharSequence dateChars) {
/* 247 */     return ALL_PARSER_WITH_DEFAULTS.apply(dateChars);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\maps\weak\ConverterTimeFormats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */