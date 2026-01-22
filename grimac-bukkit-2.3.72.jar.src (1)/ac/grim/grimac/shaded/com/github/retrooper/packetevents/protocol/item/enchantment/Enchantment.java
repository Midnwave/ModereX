/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentType;
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
/*    */ public class Enchantment
/*    */ {
/*    */   private EnchantmentType type;
/*    */   private int level;
/*    */   
/*    */   public Enchantment(EnchantmentType type, int level) {
/* 28 */     this.type = type;
/* 29 */     this.level = level;
/*    */   }
/*    */   
/*    */   public EnchantmentType getType() {
/* 33 */     return this.type;
/*    */   }
/*    */   
/*    */   public void setType(EnchantmentType type) {
/* 37 */     this.type = type;
/*    */   }
/*    */   
/*    */   public int getLevel() {
/* 41 */     return this.level;
/*    */   }
/*    */   
/*    */   public void setLevel(int level) {
/* 45 */     this.level = level;
/*    */   }
/*    */   
/*    */   public static Builder builder() {
/* 49 */     return new Builder();
/*    */   }
/*    */   
/*    */   public static class Builder {
/*    */     private EnchantmentType type;
/*    */     private int level;
/*    */     
/*    */     public Builder type(EnchantmentType type) {
/* 57 */       this.type = type;
/* 58 */       return this;
/*    */     }
/*    */     
/*    */     public Builder level(int level) {
/* 62 */       this.level = level;
/* 63 */       return this;
/*    */     }
/*    */     
/*    */     public Enchantment build() {
/* 67 */       return new Enchantment(this.type, this.level);
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\enchantment\Enchantment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */