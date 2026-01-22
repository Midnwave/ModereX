/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class SkinSection
/*    */ {
/* 24 */   public static final SkinSection CAPE = new SkinSection(1);
/* 25 */   public static final SkinSection JACKET = new SkinSection(2);
/* 26 */   public static final SkinSection LEFT_SLEEVE = new SkinSection(4);
/* 27 */   public static final SkinSection RIGHT_SLEEVE = new SkinSection(8);
/* 28 */   public static final SkinSection LEFT_PANTS = new SkinSection(16);
/* 29 */   public static final SkinSection RIGHT_PANTS = new SkinSection(32);
/* 30 */   public static final SkinSection HAT = new SkinSection(64);
/*    */   
/* 32 */   public static final SkinSection ALL = CAPE.combine(JACKET).combine(LEFT_SLEEVE).combine(RIGHT_SLEEVE).combine(LEFT_PANTS).combine(RIGHT_PANTS).combine(HAT);
/*    */   
/*    */   private final byte mask;
/*    */   
/*    */   public SkinSection(int mask) {
/* 37 */     this.mask = (byte)mask;
/*    */   }
/*    */   
/*    */   public SkinSection combine(SkinSection skinSection) {
/* 41 */     return new SkinSection(this.mask | skinSection.mask);
/*    */   }
/*    */   
/*    */   public byte getMask() {
/* 45 */     return this.mask;
/*    */   }
/*    */   
/*    */   public boolean isSet(byte skinParts) {
/* 49 */     return ((skinParts & this.mask) != 0);
/*    */   }
/*    */   
/*    */   public byte set(byte skinParts, boolean present) {
/* 53 */     if (present) {
/* 54 */       skinParts = (byte)(skinParts | this.mask);
/*    */     } else {
/* 56 */       skinParts = (byte)(skinParts & (this.mask ^ 0xFFFFFFFF));
/*    */     } 
/* 58 */     return skinParts;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\player\SkinSection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */