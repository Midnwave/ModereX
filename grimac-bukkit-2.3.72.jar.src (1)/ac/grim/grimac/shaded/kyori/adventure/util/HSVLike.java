/*     */ package ac.grim.grimac.shaded.kyori.adventure.util;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface HSVLike
/*     */   extends Examinable
/*     */ {
/*     */   @NotNull
/*     */   static HSVLike hsvLike(float h, float s, float v) {
/*  51 */     return new HSVLikeImpl(h, s, v);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @ScheduledForRemoval(inVersion = "5.0.0")
/*     */   @NotNull
/*     */   static HSVLike of(float h, float s, float v) {
/*  67 */     return new HSVLikeImpl(h, s, v);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static HSVLike fromRGB(int red, int green, int blue) {
/*  80 */     float s, h, r = red / 255.0F;
/*  81 */     float g = green / 255.0F;
/*  82 */     float b = blue / 255.0F;
/*     */     
/*  84 */     float min = Math.min(r, Math.min(g, b));
/*  85 */     float max = Math.max(r, Math.max(g, b));
/*  86 */     float delta = max - min;
/*     */ 
/*     */     
/*  89 */     if (max != 0.0F) {
/*  90 */       s = delta / max;
/*     */     } else {
/*     */       
/*  93 */       s = 0.0F;
/*     */     } 
/*  95 */     if (s == 0.0F) {
/*  96 */       return new HSVLikeImpl(0.0F, s, max);
/*     */     }
/*     */ 
/*     */     
/* 100 */     if (r == max) {
/* 101 */       h = (g - b) / delta;
/* 102 */     } else if (g == max) {
/* 103 */       h = 2.0F + (b - r) / delta;
/*     */     } else {
/* 105 */       h = 4.0F + (r - g) / delta;
/*     */     } 
/* 107 */     h *= 60.0F;
/* 108 */     if (h < 0.0F) {
/* 109 */       h += 360.0F;
/*     */     }
/*     */     
/* 112 */     return new HSVLikeImpl(h / 360.0F, s, max);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   float h();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   float s();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   float v();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default Stream<? extends ExaminableProperty> examinableProperties() {
/* 141 */     return Stream.of(new ExaminableProperty[] {
/* 142 */           ExaminableProperty.of("h", h()), 
/* 143 */           ExaminableProperty.of("s", s()), 
/* 144 */           ExaminableProperty.of("v", v())
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventur\\util\HSVLike.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */