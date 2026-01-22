/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.standard;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.Context;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Inserting;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.RGBLike;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.OptionalDouble;
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
/*     */ public final class TransitionTag
/*     */   implements Inserting, Examinable
/*     */ {
/*     */   public static final String TRANSITION = "transition";
/*     */   private final TextColor[] colors;
/*     */   private final float phase;
/*     */   private final boolean negativePhase;
/*  56 */   static final TagResolver RESOLVER = TagResolver.resolver("transition", TransitionTag::create);
/*     */   static Tag create(ArgumentQueue args, Context ctx) {
/*     */     List<TextColor> textColors;
/*  59 */     float phase = 0.0F;
/*     */     
/*  61 */     if (args.hasNext()) {
/*  62 */       textColors = new ArrayList<>();
/*  63 */       while (args.hasNext()) {
/*  64 */         Tag.Argument arg = args.pop();
/*     */ 
/*     */         
/*  67 */         String argValue = arg.value();
/*  68 */         TextColor color = ColorTagResolver.resolveColorOrNull(argValue);
/*     */         
/*  70 */         if (color != null) {
/*  71 */           textColors.add(color);
/*     */           continue;
/*     */         } 
/*  74 */         if (!args.hasNext()) {
/*  75 */           OptionalDouble possiblePhase = arg.asDouble();
/*  76 */           if (possiblePhase.isPresent()) {
/*  77 */             phase = (float)possiblePhase.getAsDouble();
/*  78 */             if (phase < -1.0F || phase > 1.0F) {
/*  79 */               throw ctx.newException(String.format("Gradient phase is out of range (%s). Must be in the range [-1.0f, 1.0f] (inclusive).", new Object[] { Float.valueOf(phase) }), args);
/*     */             }
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*     */         
/*  86 */         throw ctx.newException(String.format("Unable to parse a color from '%s'. Please use named colors or hex (#RRGGBB) colors.", new Object[] { argValue }), args);
/*     */       } 
/*     */ 
/*     */       
/*  90 */       if (textColors.size() < 2) {
/*  91 */         throw ctx.newException("Invalid transition, not enough colors. Transitions must have at least two colors.", args);
/*     */       }
/*     */     } else {
/*  94 */       textColors = Collections.emptyList();
/*     */     } 
/*     */     
/*  97 */     return (Tag)new TransitionTag(phase, textColors);
/*     */   }
/*     */   
/*     */   private TransitionTag(float phase, List<TextColor> colors) {
/* 101 */     if (phase < 0.0F) {
/* 102 */       this.negativePhase = true;
/* 103 */       this.phase = 1.0F + phase;
/* 104 */       Collections.reverse(colors);
/*     */     } else {
/* 106 */       this.negativePhase = false;
/* 107 */       this.phase = phase;
/*     */     } 
/*     */     
/* 110 */     if (colors.isEmpty()) {
/* 111 */       this.colors = new TextColor[] { TextColor.color(16777215), TextColor.color(0) };
/*     */     } else {
/* 113 */       this.colors = colors.<TextColor>toArray(new TextColor[0]);
/*     */     } 
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Component value() {
/* 119 */     return (Component)Component.text("", color());
/*     */   }
/*     */   
/*     */   private TextColor color() {
/* 123 */     float steps = 1.0F / (this.colors.length - 1);
/* 124 */     for (int colorIndex = 1; colorIndex < this.colors.length; colorIndex++) {
/* 125 */       float val = colorIndex * steps;
/* 126 */       if (val >= this.phase) {
/* 127 */         float factor = 1.0F + (this.phase - val) * (this.colors.length - 1);
/*     */         
/* 129 */         if (this.negativePhase)
/*     */         {
/* 131 */           return TextColor.lerp(1.0F - factor, (RGBLike)this.colors[colorIndex], (RGBLike)this.colors[colorIndex - 1]);
/*     */         }
/* 133 */         return TextColor.lerp(factor, (RGBLike)this.colors[colorIndex - 1], (RGBLike)this.colors[colorIndex]);
/*     */       } 
/*     */     } 
/*     */     
/* 137 */     return this.colors[0];
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 142 */     return Stream.of(new ExaminableProperty[] {
/* 143 */           ExaminableProperty.of("phase", this.phase), 
/* 144 */           ExaminableProperty.of("colors", this.colors)
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 150 */     if (this == other) return true; 
/* 151 */     if (other == null || getClass() != other.getClass()) return false; 
/* 152 */     TransitionTag that = (TransitionTag)other;
/* 153 */     return (this.phase == that.phase && Arrays.equals((Object[])this.colors, (Object[])that.colors));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 158 */     int result = Objects.hash(new Object[] { Float.valueOf(this.phase) });
/* 159 */     result = 31 * result + Arrays.hashCode((Object[])this.colors);
/* 160 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\standard\TransitionTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */