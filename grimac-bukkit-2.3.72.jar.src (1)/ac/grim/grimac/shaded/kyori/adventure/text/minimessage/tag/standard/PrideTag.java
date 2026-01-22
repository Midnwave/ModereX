/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.standard;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.Context;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.stream.Collectors;
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
/*     */ final class PrideTag
/*     */   extends GradientTag
/*     */ {
/*     */   private static final String PRIDE = "pride";
/*  52 */   static final TagResolver RESOLVER = TagResolver.resolver("pride", PrideTag::create);
/*     */   private static final Map<String, List<TextColor>> FLAGS;
/*     */   private final String flag;
/*     */   
/*     */   static {
/*  57 */     Map<String, List<TextColor>> flags = new HashMap<>();
/*     */ 
/*     */     
/*  60 */     flags.put("pride", colors(new int[] { 15007744, 16747776, 16772608, 164129, 19711, 7798920 }));
/*  61 */     flags.put("progress", colors(new int[] { 16777215, 16756679, 7591918, 6371605, 0, 15007744, 16747776, 16772608, 164129, 19711, 7798920 }));
/*  62 */     flags.put("trans", colors(new int[] { 6017019, 16100281, 16777215, 16100281, 6017019 }));
/*  63 */     flags.put("bi", colors(new int[] { 14025328, 10178454, 14504 }));
/*  64 */     flags.put("pan", colors(new int[] { 16718989, 16766720, 1750015 }));
/*  65 */     flags.put("nb", colors(new int[] { 16577585, 16579836, 10312146, 2631720 }));
/*  66 */     flags.put("lesbian", colors(new int[] { 14034944, 16751446, 16777215, 13918886, 10748002 }));
/*  67 */     flags.put("ace", colors(new int[] { 0, 10790052, 16777215, 8454273 }));
/*  68 */     flags.put("agender", colors(new int[] { 0, 12237498, 16777215, 12252292, 16777215, 12237498, 0 }));
/*  69 */     flags.put("demisexual", colors(new int[] { 0, 16777215, 7209073, 13882323 }));
/*  70 */     flags.put("genderqueer", colors(new int[] { 11894749, 16777215, 4817438 }));
/*  71 */     flags.put("genderfluid", colors(new int[] { 16676514, 16777215, 12522199, 0, 3161278 }));
/*  72 */     flags.put("intersex", colors(new int[] { 16766976, 7930538, 16766976 }));
/*  73 */     flags.put("aro", colors(new int[] { 3909440, 11064442, 16777215, 11250603, 0 }));
/*     */ 
/*     */     
/*  76 */     flags.put("baker", colors(new int[] { 13461247, 16737689, 16646144, 16685312, 16776961, 39168, 39371, 3473561, 10027161 }));
/*  77 */     flags.put("philly", colors(new int[] { 0, 7884567, 16646144, 16616448, 16770304, 1154827, 410803, 12725980 }));
/*  78 */     flags.put("queer", colors(new int[] { 0, 10148330, 41960, 11920669, 16777215, 16763149, 16541287, 16690889, 0 }));
/*  79 */     flags.put("gay", colors(new int[] { 495216, 2543274, 10021057, 16777215, 8105442, 5261771, 4004472 }));
/*  80 */     flags.put("bigender", colors(new int[] { 12876192, 15509195, 14010344, 16777215, 14010344, 10143720, 7111631 }));
/*  81 */     flags.put("demigender", colors(new int[] { 8355711, 12829635, 16514932, 16777215, 16514932, 12829635, 8355711 }));
/*     */     
/*  83 */     FLAGS = Collections.unmodifiableMap(flags);
/*     */   }
/*     */   
/*     */   static Tag create(ArgumentQueue args, Context ctx) {
/*  87 */     double phase = 0.0D;
/*  88 */     String flag = "pride";
/*     */     
/*  90 */     if (args.hasNext()) {
/*  91 */       String value = args.pop().value().toLowerCase(Locale.ROOT);
/*  92 */       if (FLAGS.containsKey(value)) {
/*  93 */         flag = value;
/*  94 */       } else if (!value.isEmpty()) {
/*     */         try {
/*  96 */           phase = Double.parseDouble(value);
/*  97 */         } catch (NumberFormatException ex) {
/*  98 */           throw ctx.newException("Expected phase, got " + value);
/*     */         } 
/*     */         
/* 101 */         if (phase < -1.0D || phase > 1.0D) {
/* 102 */           throw ctx.newException(String.format("Gradient phase is out of range (%s). Must be in the range [-1.0, 1.0] (inclusive).", new Object[] { Double.valueOf(phase) }), args);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 107 */     return (Tag)new PrideTag(phase, FLAGS.get(flag), flag, ctx);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   PrideTag(double phase, @NotNull List<TextColor> colors, @NotNull String flag, Context ctx) {
/* 113 */     super(phase, colors, ctx);
/* 114 */     this.flag = flag;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 119 */     return Stream.of(new ExaminableProperty[] {
/* 120 */           ExaminableProperty.of("flag", this.flag), 
/* 121 */           ExaminableProperty.of("phase", this.phase)
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 127 */     return Objects.hash(new Object[] { this.flag, Double.valueOf(this.phase) });
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 132 */     if (this == other) return true; 
/* 133 */     if (other == null || getClass() != other.getClass()) return false; 
/* 134 */     PrideTag that = (PrideTag)other;
/* 135 */     return (this.phase == that.phase && this.flag
/* 136 */       .equals(that.flag));
/*     */   }
/*     */   @NotNull
/*     */   private static List<TextColor> colors(int... colors) {
/* 140 */     return (List<TextColor>)Arrays.stream(colors).mapToObj(TextColor::color).collect(Collectors.toList());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\standard\PrideTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */