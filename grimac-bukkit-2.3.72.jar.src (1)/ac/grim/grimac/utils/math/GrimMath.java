/*     */ package ac.grim.grimac.utils.math;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import lombok.Generated;
/*     */ 
/*     */ public final class GrimMath {
/*     */   @Generated
/*     */   private GrimMath() {
/*  10 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*  11 */   } public static final double MINIMUM_DIVISOR = Math.pow(0.20000000298023224D, 3.0D) * 8.0D * 0.15D - 0.001D;
/*     */   private static final float DEGREES_TO_RADIANS = 0.017453292F;
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static double gcd(double a, double b) {
/*  16 */     if (a == 0.0D) return 0.0D;
/*     */ 
/*     */     
/*  19 */     if (a < b) {
/*  20 */       double temp = a;
/*  21 */       a = b;
/*  22 */       b = temp;
/*     */     } 
/*     */     
/*  25 */     while (b > MINIMUM_DIVISOR) {
/*  26 */       double temp = a - Math.floor(a / b) * b;
/*  27 */       a = b;
/*  28 */       b = temp;
/*     */     } 
/*     */     
/*  31 */     return a;
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static double calculateSD(@NotNull List<Double> numbers) {
/*  36 */     double sum = 0.0D;
/*  37 */     double standardDeviation = 0.0D;
/*     */     
/*  39 */     for (Iterator<Double> iterator1 = numbers.iterator(); iterator1.hasNext(); ) { double rotation = ((Double)iterator1.next()).doubleValue();
/*  40 */       sum += rotation; }
/*     */ 
/*     */     
/*  43 */     double mean = sum / numbers.size();
/*     */     
/*  45 */     for (Iterator<Double> iterator2 = numbers.iterator(); iterator2.hasNext(); ) { double num = ((Double)iterator2.next()).doubleValue();
/*  46 */       standardDeviation += Math.pow(num - mean, 2.0D); }
/*     */ 
/*     */     
/*  49 */     return Math.sqrt(standardDeviation / numbers.size());
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static int floor(double d) {
/*  54 */     return (int)Math.floor(d);
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static int ceil(double d) {
/*  59 */     return (int)Math.ceil(d);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static int mojangFloor(double num) {
/*  66 */     int floor = (int)num;
/*  67 */     return (floor == num) ? floor : (floor - (int)(Double.doubleToRawLongBits(num) >>> 63L));
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static int mojangCeil(double num) {
/*  72 */     int floor = (int)num;
/*  73 */     return (floor == num) ? floor : (floor + (int)((Double.doubleToRawLongBits(num) ^ 0xFFFFFFFFFFFFFFFFL) >>> 63L));
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static double clamp(double num, double min, double max) {
/*  78 */     if (num < min) {
/*  79 */       return min;
/*     */     }
/*  81 */     return Math.min(num, max);
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static int clamp(int num, int min, int max) {
/*  86 */     if (num < min) {
/*  87 */       return min;
/*     */     }
/*  89 */     return Math.min(num, max);
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static float clamp(float num, float min, float max) {
/*  94 */     if (num < min) {
/*  95 */       return min;
/*     */     }
/*  97 */     return Math.min(num, max);
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static double lerp(double lerpAmount, double start, double end) {
/* 102 */     return start + lerpAmount * (end - start);
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static double frac(double p_14186_) {
/* 107 */     return p_14186_ - lfloor(p_14186_);
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static long lfloor(double p_14135_) {
/* 112 */     long i = (long)p_14135_;
/* 113 */     return (p_14135_ < i) ? (i - 1L) : i;
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static int sign(double x) {
/* 118 */     return (x == 0.0D) ? 0 : ((x > 0.0D) ? 1 : -1);
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static float square(float value) {
/* 123 */     return value * value;
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static float sqrt(float value) {
/* 128 */     return (float)Math.sqrt(value);
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
/*     */   
/*     */   public static double distanceToHorizontalCollision(double position) {
/* 142 */     return Math.min(Math.abs(position % 0.0015625D), Math.abs(Math.abs(position % 0.0015625D) - 0.0015625D));
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static boolean betweenRange(double value, double min, double max) {
/* 147 */     return (value > min && value < max);
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static boolean inRange(double value, double min, double max) {
/* 152 */     return (value >= min && value <= max);
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static boolean inRange(int value, int min, int max) {
/* 157 */     return (value >= min && value <= max);
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static boolean isNearlySame(double a, double b, double epoch) {
/* 162 */     return (Math.abs(a - b) < epoch);
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static long hashCode(double x, int y, double z) {
/* 167 */     long l = (long)(x * 3129871.0D) ^ (long)z * 116129781L ^ y;
/* 168 */     l = l * l * 42317861L + l * 11L;
/* 169 */     return l >> 16L;
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static float radians(float degrees) {
/* 174 */     return degrees * 0.017453292F;
/*     */   }
/*     */   
/* 177 */   private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[] { 0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9 };
/*     */ 
/*     */ 
/*     */   
/* 181 */   public static final int PACKED_HORIZONTAL_LENGTH = 1 + log2(smallestEncompassingPowerOfTwo(30000000));
/* 182 */   public static final int PACKED_Y_LENGTH = 64 - 2 * PACKED_HORIZONTAL_LENGTH;
/* 183 */   private static final long PACKED_X_MASK = (1L << PACKED_HORIZONTAL_LENGTH) - 1L;
/* 184 */   private static final long PACKED_Y_MASK = (1L << PACKED_Y_LENGTH) - 1L;
/* 185 */   private static final long PACKED_Z_MASK = (1L << PACKED_HORIZONTAL_LENGTH) - 1L;
/* 186 */   private static final int Z_OFFSET = PACKED_Y_LENGTH;
/* 187 */   private static final int X_OFFSET = PACKED_Y_LENGTH + PACKED_HORIZONTAL_LENGTH;
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static long asLong(int x, int y, int z) {
/* 191 */     return (x & PACKED_X_MASK) << X_OFFSET | y & PACKED_Y_MASK | (z & PACKED_Z_MASK) << Z_OFFSET;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static int log2(int value) {
/* 197 */     return ceillog2(value) - (isPowerOfTwo(value) ? 0 : 1);
/*     */   }
/*     */   
/*     */   public static int ceillog2(int value) {
/* 201 */     value = isPowerOfTwo(value) ? value : smallestEncompassingPowerOfTwo(value);
/* 202 */     return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)(value * 125613361L >> 27L) & 0x1F];
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static boolean isPowerOfTwo(int value) {
/* 207 */     return (value != 0 && (value & value - 1) == 0);
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static int smallestEncompassingPowerOfTwo(int value) {
/* 212 */     int output = value - 1;
/* 213 */     output |= output >> 1;
/* 214 */     output |= output >> 2;
/* 215 */     output |= output >> 4;
/* 216 */     output |= output >> 8;
/* 217 */     output |= output >> 16;
/* 218 */     return output + 1;
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static boolean equal(double first, double second) {
/* 223 */     return (Math.abs(second - first) < 9.999999747378752E-6D);
/*     */   }
/*     */   
/*     */   @Contract(pure = true)
/*     */   public static double square(double num) {
/* 228 */     return num * num;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\math\GrimMath.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */