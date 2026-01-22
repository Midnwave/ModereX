/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.advancements;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import java.util.Map;
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
/*    */ 
/*    */ public final class AdvancementProgress
/*    */ {
/*    */   private Map<String, CriterionProgress> criteria;
/*    */   
/*    */   public AdvancementProgress(Map<String, CriterionProgress> criteria) {
/* 31 */     this.criteria = criteria;
/*    */   }
/*    */   
/*    */   public static AdvancementProgress read(PacketWrapper<?> wrapper) {
/* 35 */     Map<String, CriterionProgress> criteria = wrapper.readMap(PacketWrapper::readString, CriterionProgress::read);
/* 36 */     return new AdvancementProgress(criteria);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, AdvancementProgress progress) {
/* 40 */     wrapper.writeMap(progress.getCriteria(), PacketWrapper::writeString, CriterionProgress::write);
/*    */   }
/*    */   
/*    */   public Map<String, CriterionProgress> getCriteria() {
/* 44 */     return this.criteria;
/*    */   }
/*    */   
/*    */   public void setCriteria(Map<String, CriterionProgress> criteria) {
/* 48 */     this.criteria = criteria;
/*    */   }
/*    */   
/*    */   public static final class CriterionProgress
/*    */   {
/*    */     private Long obtainedTimestamp;
/*    */     
/*    */     public CriterionProgress(Long obtainedTimestamp) {
/* 56 */       this.obtainedTimestamp = obtainedTimestamp;
/*    */     }
/*    */     
/*    */     public static CriterionProgress read(PacketWrapper<?> wrapper) {
/* 60 */       return new CriterionProgress((Long)wrapper.readOptional(PacketWrapper::readLong));
/*    */     }
/*    */     
/*    */     public static void write(PacketWrapper<?> wrapper, CriterionProgress progress) {
/* 64 */       wrapper.writeOptional(progress.obtainedTimestamp, PacketWrapper::writeLong);
/*    */     }
/*    */     
/*    */     public Long getObtainedTimestamp() {
/* 68 */       return this.obtainedTimestamp;
/*    */     }
/*    */     
/*    */     public void setObtainedTimestamp(Long obtainedTimestamp) {
/* 72 */       this.obtainedTimestamp = obtainedTimestamp;
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\advancements\AdvancementProgress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */