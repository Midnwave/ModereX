/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.standard;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.NamedTextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.Context;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.RGBLike;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.OptionalDouble;
/*     */ import java.util.function.Consumer;
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
/*     */ class GradientTag
/*     */   extends AbstractColorChangingTag
/*     */ {
/*     */   private static final String GRADIENT = "gradient";
/*  54 */   private static final TextColor DEFAULT_WHITE = TextColor.color(16777215);
/*  55 */   private static final TextColor DEFAULT_BLACK = TextColor.color(0);
/*     */   
/*  57 */   static final TagResolver RESOLVER = SerializableResolver.claimingComponent("gradient", GradientTag::create, AbstractColorChangingTag::claimComponent);
/*     */   
/*  59 */   private int index = 0;
/*     */   
/*  61 */   private double multiplier = 1.0D;
/*     */   
/*     */   private final TextColor[] colors;
/*     */   double phase;
/*     */   private final boolean negativePhase;
/*     */   
/*     */   static Tag create(ArgumentQueue args, Context ctx) {
/*     */     List<TextColor> textColors;
/*  69 */     double phase = 0.0D;
/*     */     
/*  71 */     if (args.hasNext()) {
/*  72 */       textColors = new ArrayList<>();
/*  73 */       while (args.hasNext()) {
/*  74 */         Tag.Argument arg = args.pop();
/*     */ 
/*     */         
/*  77 */         String argValue = arg.value();
/*  78 */         TextColor color = ColorTagResolver.resolveColorOrNull(argValue);
/*     */         
/*  80 */         if (color != null) {
/*  81 */           textColors.add(color);
/*     */           continue;
/*     */         } 
/*  84 */         if (!args.hasNext()) {
/*  85 */           OptionalDouble possiblePhase = arg.asDouble();
/*  86 */           if (possiblePhase.isPresent()) {
/*  87 */             phase = possiblePhase.getAsDouble();
/*  88 */             if (phase < -1.0D || phase > 1.0D) {
/*  89 */               throw ctx.newException(String.format("Gradient phase is out of range (%s). Must be in the range [-1.0, 1.0] (inclusive).", new Object[] { Double.valueOf(phase) }), args);
/*     */             }
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*     */         
/*  96 */         throw ctx.newException(String.format("Unable to parse a color from '%s'. Please use named colors or hex (#RRGGBB) colors.", new Object[] { argValue }), args);
/*     */       } 
/*     */ 
/*     */       
/* 100 */       if (textColors.size() == 1) {
/* 101 */         throw ctx.newException("Invalid gradient, not enough colors. Gradients must have at least two colors.", args);
/*     */       }
/*     */     } else {
/* 104 */       textColors = Collections.emptyList();
/*     */     } 
/*     */     
/* 107 */     return (Tag)new GradientTag(phase, textColors, ctx);
/*     */   }
/*     */   
/*     */   GradientTag(double phase, List<TextColor> colors, Context ctx) {
/* 111 */     super(ctx);
/* 112 */     if (colors.isEmpty()) {
/* 113 */       this.colors = new TextColor[] { DEFAULT_WHITE, DEFAULT_BLACK };
/*     */     } else {
/* 115 */       this.colors = colors.<TextColor>toArray(new TextColor[0]);
/*     */     } 
/*     */     
/* 118 */     if (phase < 0.0D) {
/* 119 */       this.negativePhase = true;
/* 120 */       this.phase = 1.0D + phase;
/* 121 */       Collections.reverse(Arrays.asList((Object[])this.colors));
/*     */     } else {
/* 123 */       this.negativePhase = false;
/* 124 */       this.phase = phase;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void init() {
/* 132 */     this.multiplier = (size() == 1) ? 0.0D : ((this.colors.length - 1) / (size() - 1));
/* 133 */     this.phase *= (this.colors.length - 1);
/* 134 */     this.index = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void advanceColor() {
/* 139 */     this.index++;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected TextColor color() {
/* 146 */     double position = this.index * this.multiplier + this.phase;
/* 147 */     int lowUnclamped = (int)Math.floor(position);
/*     */     
/* 149 */     int high = (int)Math.ceil(position) % this.colors.length;
/* 150 */     int low = lowUnclamped % this.colors.length;
/*     */     
/* 152 */     return TextColor.lerp((float)position - lowUnclamped, (RGBLike)this.colors[low], (RGBLike)this.colors[high]);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected Consumer<TokenEmitter> preserveData() {
/*     */     TextColor[] colors;
/*     */     double phase;
/* 160 */     if (this.negativePhase) {
/* 161 */       colors = Arrays.<TextColor>copyOf(this.colors, this.colors.length);
/* 162 */       Collections.reverse(Arrays.asList((Object[])colors));
/* 163 */       phase = this.phase - 1.0D;
/*     */     } else {
/* 165 */       colors = this.colors;
/* 166 */       phase = this.phase;
/*     */     } 
/*     */     
/* 169 */     return emit -> {
/*     */         emit.tag("gradient");
/*     */         if (colors.length != 2 || !colors[0].equals(DEFAULT_WHITE) || !colors[1].equals(DEFAULT_BLACK)) {
/*     */           for (TextColor color : colors) {
/*     */             if (color instanceof NamedTextColor) {
/*     */               emit.argument((String)NamedTextColor.NAMES.keyOrThrow(color));
/*     */             } else {
/*     */               emit.argument(color.asHexString());
/*     */             } 
/*     */           } 
/*     */         }
/*     */         if (phase != 0.0D) {
/*     */           emit.argument(Double.toString(phase));
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 189 */     return Stream.of(new ExaminableProperty[] {
/* 190 */           ExaminableProperty.of("phase", this.phase), 
/* 191 */           ExaminableProperty.of("colors", this.colors)
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 197 */     if (this == other) return true; 
/* 198 */     if (other == null || getClass() != other.getClass()) return false; 
/* 199 */     GradientTag that = (GradientTag)other;
/* 200 */     return (this.index == that.index && this.phase == that.phase && 
/*     */       
/* 202 */       Arrays.equals((Object[])this.colors, (Object[])that.colors));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 207 */     int result = Objects.hash(new Object[] { Integer.valueOf(this.index), Double.valueOf(this.phase) });
/* 208 */     result = 31 * result + Arrays.hashCode((Object[])this.colors);
/* 209 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\standard\GradientTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */