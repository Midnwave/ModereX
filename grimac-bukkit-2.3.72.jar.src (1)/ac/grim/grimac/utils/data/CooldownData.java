/*    */ package ac.grim.grimac.utils.data;
/*    */ public class CooldownData {
/*    */   int ticksRemaining;
/*    */   int transaction;
/*    */   
/*    */   @Generated
/*  7 */   public CooldownData(int ticksRemaining, int transaction) { this.ticksRemaining = ticksRemaining; this.transaction = transaction; }
/*    */   @Generated
/*  9 */   public void setTicksRemaining(int ticksRemaining) { this.ticksRemaining = ticksRemaining; } @Generated public void setTransaction(int transaction) { this.transaction = transaction; }
/*    */   @Generated
/* 11 */   public int getTicksRemaining() { return this.ticksRemaining; } @Generated
/* 12 */   public int getTransaction() { return this.transaction; }
/*    */   
/*    */   public void tick() {
/* 15 */     this.ticksRemaining--;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\CooldownData.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */