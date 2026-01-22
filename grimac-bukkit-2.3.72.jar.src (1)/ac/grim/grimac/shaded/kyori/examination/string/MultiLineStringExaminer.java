/*     */ package ac.grim.grimac.shaded.kyori.examination.string;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.AbstractExaminer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MultiLineStringExaminer
/*     */   extends AbstractExaminer<Stream<String>>
/*     */ {
/*     */   private static final String INDENT_2 = "  ";
/*     */   private final StringExaminer examiner;
/*     */   
/*     */   @NotNull
/*     */   public static MultiLineStringExaminer simpleEscaping() {
/*  60 */     return Instances.SIMPLE_ESCAPING;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MultiLineStringExaminer(@NotNull StringExaminer examiner) {
/*  70 */     this.examiner = examiner;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected <E> Stream<String> array(E[] array, @NotNull Stream<Stream<String>> elements) {
/*  75 */     return arrayLike(elements);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected <E> Stream<String> collection(@NotNull Collection<E> collection, @NotNull Stream<Stream<String>> elements) {
/*  80 */     return arrayLike(elements);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected Stream<String> examinable(@NotNull String name, @NotNull Stream<Map.Entry<String, Stream<String>>> properties) {
/*  85 */     Stream<String> flattened = flatten(",", properties.map(entry -> association(examine((String)entry.getKey()), " = ", (Stream<String>)entry.getValue())));
/*  86 */     Stream<String> indented = indent(flattened);
/*  87 */     return enclose(indented, name + "{", "}");
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected <K, V> Stream<String> map(@NotNull Map<K, V> map, @NotNull Stream<Map.Entry<Stream<String>, Stream<String>>> entries) {
/*  92 */     Stream<String> flattened = flatten(",", entries.map(entry -> association((Stream<String>)entry.getKey(), " = ", (Stream<String>)entry.getValue())));
/*  93 */     Stream<String> indented = indent(flattened);
/*  94 */     return enclose(indented, "{", "}");
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected Stream<String> nil() {
/*  99 */     return Stream.of(this.examiner.nil());
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected Stream<String> scalar(@NotNull Object value) {
/* 104 */     return Stream.of(this.examiner.scalar(value));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<String> examine(boolean value) {
/* 109 */     return Stream.of(this.examiner.examine(value));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<String> examine(byte value) {
/* 114 */     return Stream.of(this.examiner.examine(value));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<String> examine(char value) {
/* 119 */     return Stream.of(this.examiner.examine(value));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<String> examine(double value) {
/* 124 */     return Stream.of(this.examiner.examine(value));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<String> examine(float value) {
/* 129 */     return Stream.of(this.examiner.examine(value));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<String> examine(int value) {
/* 134 */     return Stream.of(this.examiner.examine(value));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<String> examine(long value) {
/* 139 */     return Stream.of(this.examiner.examine(value));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<String> examine(short value) {
/* 144 */     return Stream.of(this.examiner.examine(value));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected Stream<String> array(int length, IntFunction<Stream<String>> value) {
/* 149 */     return arrayLike(
/* 150 */         (length == 0) ? 
/* 151 */         Stream.<Stream<String>>empty() : 
/* 152 */         IntStream.range(0, length).<Stream<String>>mapToObj(value));
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected <T> Stream<String> stream(@NotNull Stream<T> stream) {
/* 158 */     return arrayLike(stream.map(this::examine));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected Stream<String> stream(@NotNull DoubleStream stream) {
/* 163 */     return arrayLike(stream.mapToObj(this::examine));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected Stream<String> stream(@NotNull IntStream stream) {
/* 168 */     return arrayLike(stream.mapToObj(this::examine));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected Stream<String> stream(@NotNull LongStream stream) {
/* 173 */     return arrayLike(stream.mapToObj(this::examine));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<String> examine(@Nullable String value) {
/* 178 */     return Stream.of(this.examiner.examine(value));
/*     */   }
/*     */   
/*     */   private Stream<String> arrayLike(Stream<Stream<String>> streams) {
/* 182 */     Stream<String> flattened = flatten(",", streams);
/* 183 */     Stream<String> indented = indent(flattened);
/* 184 */     return enclose(indented, "[", "]");
/*     */   }
/*     */   
/*     */   private static Stream<String> enclose(Stream<String> lines, String open, String close) {
/* 188 */     return enclose(lines.collect((Collector)Collectors.toList()), open, close);
/*     */   }
/*     */   
/*     */   private static Stream<String> enclose(List<String> lines, String open, String close) {
/* 192 */     if (lines.isEmpty()) {
/* 193 */       return Stream.of(open + close);
/*     */     }
/* 195 */     return Stream.<Stream<String>>of((Stream<String>[])new Stream[] {
/* 196 */           Stream.of(open), 
/* 197 */           indent(lines.stream()), 
/* 198 */           Stream.of(close)
/* 199 */         }).reduce(Stream.empty(), Stream::concat);
/*     */   }
/*     */   
/*     */   private static Stream<String> flatten(String delimiter, Stream<Stream<String>> bumpy) {
/* 203 */     List<String> flat = new ArrayList<>();
/* 204 */     bumpy.forEachOrdered(lines -> {
/*     */           if (!flat.isEmpty()) {
/*     */             int last = flat.size() - 1; flat.set(last, (String)flat.get(last) + delimiter);
/*     */           } 
/*     */           Objects.requireNonNull(flat);
/*     */           lines.forEachOrdered(flat::add);
/*     */         });
/* 211 */     return flat.stream();
/*     */   }
/*     */   
/*     */   private static Stream<String> association(Stream<String> left, String middle, Stream<String> right) {
/* 215 */     return association(left
/* 216 */         .collect((Collector)Collectors.toList()), middle, right
/*     */         
/* 218 */         .collect((Collector)Collectors.toList()));
/*     */   }
/*     */ 
/*     */   
/*     */   private static Stream<String> association(List<String> left, String middle, List<String> right) {
/* 223 */     int lefts = left.size();
/* 224 */     int rights = right.size();
/*     */     
/* 226 */     int height = Math.max(lefts, rights);
/* 227 */     int leftWidth = Strings.maxLength(left.stream());
/*     */     
/* 229 */     String leftPad = (lefts < 2) ? "" : Strings.repeat(" ", leftWidth);
/* 230 */     String middlePad = (lefts < 2) ? "" : Strings.repeat(" ", middle.length());
/*     */     
/* 232 */     List<String> result = new ArrayList<>(height);
/* 233 */     for (int i = 0; i < height; i++) {
/* 234 */       String l = (i < lefts) ? Strings.padEnd(left.get(i), leftWidth, ' ') : leftPad;
/* 235 */       String m = (i == 0) ? middle : middlePad;
/* 236 */       String r = (i < rights) ? right.get(i) : "";
/* 237 */       result.add(l + m + r);
/*     */     } 
/* 239 */     return result.stream();
/*     */   }
/*     */   
/*     */   private static Stream<String> indent(Stream<String> lines) {
/* 243 */     return lines.map(line -> "  " + line);
/*     */   }
/*     */   
/*     */   private static final class Instances {
/* 247 */     static final MultiLineStringExaminer SIMPLE_ESCAPING = new MultiLineStringExaminer(StringExaminer.simpleEscaping());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\examination\string\MultiLineStringExaminer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */