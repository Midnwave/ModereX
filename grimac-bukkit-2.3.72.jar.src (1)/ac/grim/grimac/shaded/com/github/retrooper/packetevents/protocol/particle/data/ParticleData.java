/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data;
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
/*    */ public class ParticleData
/*    */ {
/* 23 */   static ParticleData EMPTY = new ParticleData();
/*    */ 
/*    */   
/*    */   public static <T extends ParticleData> T emptyData() {
/* 27 */     return (T)EMPTY;
/*    */   }
/*    */   
/*    */   public boolean isEmpty() {
/* 31 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\particle\data\ParticleData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */