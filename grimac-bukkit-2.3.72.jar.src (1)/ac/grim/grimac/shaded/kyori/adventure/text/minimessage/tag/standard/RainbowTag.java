/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.standard;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.Context;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.HSVLike;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.Objects;
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
/*     */ 
/*     */ final class RainbowTag
/*     */   extends AbstractColorChangingTag
/*     */ {
/*     */   private static final String REVERSE = "!";
/*     */   private static final String RAINBOW = "rainbow";
/*  50 */   static final TagResolver RESOLVER = SerializableResolver.claimingComponent("rainbow", RainbowTag::create, AbstractColorChangingTag::claimComponent);
/*     */   
/*     */   private final boolean reversed;
/*     */   
/*     */   private final double dividedPhase;
/*  55 */   private int colorIndex = 0;
/*     */   
/*     */   static Tag create(ArgumentQueue args, Context ctx) {
/*  58 */     boolean reversed = false;
/*  59 */     int phase = 0;
/*     */     
/*  61 */     if (args.hasNext()) {
/*  62 */       String value = args.pop().value();
/*  63 */       if (value.startsWith("!")) {
/*  64 */         reversed = true;
/*  65 */         value = value.substring("!".length());
/*     */       } 
/*  67 */       if (value.length() > 0) {
/*     */         try {
/*  69 */           phase = Integer.parseInt(value);
/*  70 */         } catch (NumberFormatException ex) {
/*  71 */           throw ctx.newException("Expected phase, got " + value);
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/*  76 */     return (Tag)new RainbowTag(reversed, phase, ctx);
/*     */   }
/*     */   
/*     */   private RainbowTag(boolean reversed, int phase, Context ctx) {
/*  80 */     super(ctx);
/*  81 */     this.reversed = reversed;
/*  82 */     this.dividedPhase = phase / 10.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void init() {
/*  87 */     if (this.reversed) {
/*  88 */       this.colorIndex = size() - 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void advanceColor() {
/*  94 */     if (this.reversed) {
/*  95 */       if (this.colorIndex == 0) {
/*  96 */         this.colorIndex = size() - 1;
/*     */       } else {
/*  98 */         this.colorIndex--;
/*     */       } 
/*     */     } else {
/* 101 */       this.colorIndex++;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected TextColor color() {
/* 107 */     float index = this.colorIndex;
/* 108 */     float hue = (float)(((index / size()) + this.dividedPhase) % 1.0D);
/* 109 */     return TextColor.color(HSVLike.hsvLike(hue, 1.0F, 1.0F));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected Consumer<TokenEmitter> preserveData() {
/* 114 */     boolean reversed = this.reversed;
/* 115 */     int phase = (int)Math.round(this.dividedPhase * 10.0D);
/* 116 */     return emit -> {
/*     */         emit.tag("rainbow");
/*     */         if (reversed && phase != 0) {
/*     */           emit.argument("!" + phase);
/*     */         } else if (reversed) {
/*     */           emit.argument("!");
/*     */         } else if (phase != 0) {
/*     */           emit.argument(Integer.toString(phase));
/*     */         } 
/*     */       };
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 130 */     return Stream.of(ExaminableProperty.of("phase", this.dividedPhase));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 135 */     if (this == other) return true; 
/* 136 */     if (other == null || getClass() != other.getClass()) return false; 
/* 137 */     RainbowTag that = (RainbowTag)other;
/* 138 */     return (this.colorIndex == that.colorIndex && this.dividedPhase == that.dividedPhase);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 143 */     return Objects.hash(new Object[] { Integer.valueOf(this.colorIndex), Double.valueOf(this.dividedPhase) });
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\standard\RainbowTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */