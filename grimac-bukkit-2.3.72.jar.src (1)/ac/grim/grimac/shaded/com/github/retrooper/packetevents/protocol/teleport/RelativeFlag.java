/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.teleport;
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
/*     */ public final class RelativeFlag
/*     */ {
/*  27 */   public static final RelativeFlag NONE = new RelativeFlag(0);
/*  28 */   public static final RelativeFlag X = new RelativeFlag(1);
/*  29 */   public static final RelativeFlag Y = new RelativeFlag(2);
/*  30 */   public static final RelativeFlag Z = new RelativeFlag(4);
/*  31 */   public static final RelativeFlag YAW = new RelativeFlag(8);
/*  32 */   public static final RelativeFlag PITCH = new RelativeFlag(16);
/*     */ 
/*     */ 
/*     */   
/*  36 */   public static final RelativeFlag DELTA_X = new RelativeFlag(32);
/*     */ 
/*     */ 
/*     */   
/*  40 */   public static final RelativeFlag DELTA_Y = new RelativeFlag(64);
/*     */ 
/*     */ 
/*     */   
/*  44 */   public static final RelativeFlag DELTA_Z = new RelativeFlag(128);
/*     */ 
/*     */ 
/*     */   
/*  48 */   public static final RelativeFlag ROTATE_DELTA = new RelativeFlag(256);
/*     */   
/*     */   private final int mask;
/*     */   
/*     */   public RelativeFlag(int mask) {
/*  53 */     this.mask = mask;
/*     */   }
/*     */   
/*     */   public RelativeFlag and(RelativeFlag other) {
/*  57 */     return new RelativeFlag(this.mask & other.mask);
/*     */   }
/*     */   
/*     */   public RelativeFlag or(RelativeFlag other) {
/*  61 */     return new RelativeFlag(this.mask | other.mask);
/*     */   }
/*     */   
/*     */   public boolean has(RelativeFlag flag) {
/*  65 */     return has(flag.mask);
/*     */   }
/*     */   
/*     */   public boolean has(int flags) {
/*  69 */     return ((flags & this.mask) != 0);
/*     */   }
/*     */   
/*     */   public RelativeFlag set(RelativeFlag flag, boolean relative) {
/*  73 */     return set(flag.mask, relative);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public RelativeFlag set(int flags, boolean relative) {
/*  79 */     int ret = relative ? (this.mask | flags) : (this.mask & (flags ^ 0xFFFFFFFF));
/*  80 */     return new RelativeFlag(ret);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public RelativeFlag combine(RelativeFlag relativeFlag) {
/*  85 */     return or(relativeFlag);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public boolean isSet(byte flags) {
/*  90 */     return has(flags);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public byte set(byte flags, boolean relative) {
/*  95 */     return (byte)(set(flags, relative)).mask;
/*     */   }
/*     */   
/*     */   public byte getMask() {
/*  99 */     return (byte)this.mask;
/*     */   }
/*     */   
/*     */   public int getFullMask() {
/* 103 */     return this.mask;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\teleport\RelativeFlag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */