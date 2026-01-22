/*     */ package ac.grim.grimac.shaded.kyori.examination;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ExaminableProperty
/*     */ {
/*     */   private ExaminableProperty() {}
/*     */   
/*     */   public String toString() {
/*  58 */     return "ExaminableProperty{" + name() + "}";
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
/*     */   public static ExaminableProperty of(@NotNull final String name, @Nullable final Object value) {
/*  71 */     return new ExaminableProperty() {
/*     */         @NotNull
/*     */         public String name() {
/*  74 */           return name;
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         public <R> R examine(@NotNull Examiner<? extends R> examiner) {
/*  79 */           return examiner.examine(value);
/*     */         }
/*     */       };
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
/*     */   public static ExaminableProperty of(@NotNull final String name, @Nullable final String value) {
/*  94 */     return new ExaminableProperty() {
/*     */         @NotNull
/*     */         public String name() {
/*  97 */           return name;
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         public <R> R examine(@NotNull Examiner<? extends R> examiner) {
/* 102 */           return examiner.examine(value);
/*     */         }
/*     */       };
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
/*     */   public static ExaminableProperty of(@NotNull final String name, final boolean value) {
/* 117 */     return new ExaminableProperty() {
/*     */         @NotNull
/*     */         public String name() {
/* 120 */           return name;
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         public <R> R examine(@NotNull Examiner<? extends R> examiner) {
/* 125 */           return examiner.examine(value);
/*     */         }
/*     */       };
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
/*     */   public static ExaminableProperty of(@NotNull final String name, final boolean[] value) {
/* 140 */     return new ExaminableProperty() {
/*     */         @NotNull
/*     */         public String name() {
/* 143 */           return name;
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         public <R> R examine(@NotNull Examiner<? extends R> examiner) {
/* 148 */           return examiner.examine(value);
/*     */         }
/*     */       };
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
/*     */   public static ExaminableProperty of(@NotNull final String name, final byte value) {
/* 163 */     return new ExaminableProperty() {
/*     */         @NotNull
/*     */         public String name() {
/* 166 */           return name;
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         public <R> R examine(@NotNull Examiner<? extends R> examiner) {
/* 171 */           return examiner.examine(value);
/*     */         }
/*     */       };
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
/*     */   public static ExaminableProperty of(@NotNull final String name, final byte[] value) {
/* 186 */     return new ExaminableProperty() {
/*     */         @NotNull
/*     */         public String name() {
/* 189 */           return name;
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         public <R> R examine(@NotNull Examiner<? extends R> examiner) {
/* 194 */           return examiner.examine(value);
/*     */         }
/*     */       };
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
/*     */   public static ExaminableProperty of(@NotNull final String name, final char value) {
/* 209 */     return new ExaminableProperty() {
/*     */         @NotNull
/*     */         public String name() {
/* 212 */           return name;
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         public <R> R examine(@NotNull Examiner<? extends R> examiner) {
/* 217 */           return examiner.examine(value);
/*     */         }
/*     */       };
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
/*     */   public static ExaminableProperty of(@NotNull final String name, final char[] value) {
/* 232 */     return new ExaminableProperty() {
/*     */         @NotNull
/*     */         public String name() {
/* 235 */           return name;
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         public <R> R examine(@NotNull Examiner<? extends R> examiner) {
/* 240 */           return examiner.examine(value);
/*     */         }
/*     */       };
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
/*     */   public static ExaminableProperty of(@NotNull final String name, final double value) {
/* 255 */     return new ExaminableProperty() {
/*     */         @NotNull
/*     */         public String name() {
/* 258 */           return name;
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         public <R> R examine(@NotNull Examiner<? extends R> examiner) {
/* 263 */           return examiner.examine(value);
/*     */         }
/*     */       };
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
/*     */   public static ExaminableProperty of(@NotNull final String name, final double[] value) {
/* 278 */     return new ExaminableProperty() {
/*     */         @NotNull
/*     */         public String name() {
/* 281 */           return name;
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         public <R> R examine(@NotNull Examiner<? extends R> examiner) {
/* 286 */           return examiner.examine(value);
/*     */         }
/*     */       };
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
/*     */   public static ExaminableProperty of(@NotNull final String name, final float value) {
/* 301 */     return new ExaminableProperty() {
/*     */         @NotNull
/*     */         public String name() {
/* 304 */           return name;
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         public <R> R examine(@NotNull Examiner<? extends R> examiner) {
/* 309 */           return examiner.examine(value);
/*     */         }
/*     */       };
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
/*     */   public static ExaminableProperty of(@NotNull final String name, final float[] value) {
/* 324 */     return new ExaminableProperty() {
/*     */         @NotNull
/*     */         public String name() {
/* 327 */           return name;
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         public <R> R examine(@NotNull Examiner<? extends R> examiner) {
/* 332 */           return examiner.examine(value);
/*     */         }
/*     */       };
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
/*     */   public static ExaminableProperty of(@NotNull final String name, final int value) {
/* 347 */     return new ExaminableProperty() {
/*     */         @NotNull
/*     */         public String name() {
/* 350 */           return name;
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         public <R> R examine(@NotNull Examiner<? extends R> examiner) {
/* 355 */           return examiner.examine(value);
/*     */         }
/*     */       };
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
/*     */   public static ExaminableProperty of(@NotNull final String name, final int[] value) {
/* 370 */     return new ExaminableProperty() {
/*     */         @NotNull
/*     */         public String name() {
/* 373 */           return name;
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         public <R> R examine(@NotNull Examiner<? extends R> examiner) {
/* 378 */           return examiner.examine(value);
/*     */         }
/*     */       };
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
/*     */   public static ExaminableProperty of(@NotNull final String name, final long value) {
/* 393 */     return new ExaminableProperty() {
/*     */         @NotNull
/*     */         public String name() {
/* 396 */           return name;
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         public <R> R examine(@NotNull Examiner<? extends R> examiner) {
/* 401 */           return examiner.examine(value);
/*     */         }
/*     */       };
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
/*     */   public static ExaminableProperty of(@NotNull final String name, final long[] value) {
/* 416 */     return new ExaminableProperty() {
/*     */         @NotNull
/*     */         public String name() {
/* 419 */           return name;
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         public <R> R examine(@NotNull Examiner<? extends R> examiner) {
/* 424 */           return examiner.examine(value);
/*     */         }
/*     */       };
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
/*     */   public static ExaminableProperty of(@NotNull final String name, final short value) {
/* 439 */     return new ExaminableProperty() {
/*     */         @NotNull
/*     */         public String name() {
/* 442 */           return name;
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         public <R> R examine(@NotNull Examiner<? extends R> examiner) {
/* 447 */           return examiner.examine(value);
/*     */         }
/*     */       };
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
/*     */   public static ExaminableProperty of(@NotNull final String name, final short[] value) {
/* 462 */     return new ExaminableProperty() {
/*     */         @NotNull
/*     */         public String name() {
/* 465 */           return name;
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         public <R> R examine(@NotNull Examiner<? extends R> examiner) {
/* 470 */           return examiner.examine(value);
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public abstract String name();
/*     */   
/*     */   @NotNull
/*     */   public abstract <R> R examine(@NotNull Examiner<? extends R> paramExaminer);
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\examination\ExaminableProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */