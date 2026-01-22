/*     */ package ac.grim.grimac.shaded.kyori.examination.string;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.AbstractExaminer;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.IntFunction;
/*     */ import java.util.stream.Collector;
/*     */ import java.util.stream.Collectors;
/*     */ import java.util.stream.DoubleStream;
/*     */ import java.util.stream.IntStream;
/*     */ import java.util.stream.LongStream;
/*     */ import java.util.stream.Stream;
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
/*     */ public class StringExaminer
/*     */   extends AbstractExaminer<String>
/*     */ {
/*     */   private static final Function<String, String> DEFAULT_ESCAPER;
/*     */   
/*     */   static {
/*  47 */     DEFAULT_ESCAPER = (string -> string.replace("\"", "\\\"").replace("\\", "\\\\").replace("\b", "\\b").replace("\f", "\\f").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  55 */   private static final Collector<CharSequence, ?, String> COMMA_CURLY = Collectors.joining(", ", "{", "}");
/*  56 */   private static final Collector<CharSequence, ?, String> COMMA_SQUARE = Collectors.joining(", ", "[", "]");
/*     */ 
/*     */ 
/*     */   
/*     */   private final Function<String, String> escaper;
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static StringExaminer simpleEscaping() {
/*  66 */     return Instances.SIMPLE_ESCAPING;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringExaminer(@NotNull Function<String, String> escaper) {
/*  76 */     this.escaper = escaper;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected <E> String array(E[] array, @NotNull Stream<String> elements) {
/*  81 */     return elements.collect(COMMA_SQUARE);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected <E> String collection(@NotNull Collection<E> collection, @NotNull Stream<String> elements) {
/*  86 */     return elements.collect(COMMA_SQUARE);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected String examinable(@NotNull String name, @NotNull Stream<Map.Entry<String, String>> properties) {
/*  91 */     return name + (String)properties.<CharSequence>map(property -> (String)property.getKey() + '=' + (String)property.getValue()).collect(COMMA_CURLY);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected <K, V> String map(@NotNull Map<K, V> map, @NotNull Stream<Map.Entry<String, String>> entries) {
/*  96 */     return entries.<CharSequence>map(entry -> (String)entry.getKey() + '=' + (String)entry.getValue()).collect(COMMA_CURLY);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected String nil() {
/* 101 */     return "null";
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected String scalar(@NotNull Object value) {
/* 106 */     return String.valueOf(value);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String examine(boolean value) {
/* 111 */     return String.valueOf(value);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String examine(byte value) {
/* 116 */     return String.valueOf(value);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String examine(char value) {
/* 121 */     return Strings.wrapIn(this.escaper.apply(String.valueOf(value)), '\'');
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String examine(double value) {
/* 126 */     return Strings.withSuffix(String.valueOf(value), 'd');
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String examine(float value) {
/* 131 */     return Strings.withSuffix(String.valueOf(value), 'f');
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String examine(int value) {
/* 136 */     return String.valueOf(value);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String examine(long value) {
/* 141 */     return String.valueOf(value);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String examine(short value) {
/* 146 */     return String.valueOf(value);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected <T> String stream(@NotNull Stream<T> stream) {
/* 151 */     return stream.<CharSequence>map(this::examine).collect(COMMA_SQUARE);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected String stream(@NotNull DoubleStream stream) {
/* 156 */     return stream.<CharSequence>mapToObj(this::examine).collect(COMMA_SQUARE);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected String stream(@NotNull IntStream stream) {
/* 161 */     return stream.<CharSequence>mapToObj(this::examine).collect(COMMA_SQUARE);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected String stream(@NotNull LongStream stream) {
/* 166 */     return stream.<CharSequence>mapToObj(this::examine).collect(COMMA_SQUARE);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String examine(@Nullable String value) {
/* 171 */     if (value == null) return nil(); 
/* 172 */     return Strings.wrapIn(this.escaper.apply(value), '"');
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected String array(int length, IntFunction<String> value) {
/* 177 */     StringBuilder sb = new StringBuilder();
/* 178 */     sb.append('[');
/* 179 */     for (int i = 0; i < length; i++) {
/* 180 */       sb.append(value.apply(i));
/* 181 */       if (i + 1 < length) {
/* 182 */         sb.append(", ");
/*     */       }
/*     */     } 
/* 185 */     sb.append(']');
/* 186 */     return sb.toString();
/*     */   }
/*     */   
/*     */   private static final class Instances {
/* 190 */     static final StringExaminer SIMPLE_ESCAPING = new StringExaminer(StringExaminer.DEFAULT_ESCAPER);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\examination\string\StringExaminer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */