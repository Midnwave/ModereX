/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.consumables.ConsumeEffect;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import java.util.List;
/*    */ import java.util.Objects;
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
/*    */ public class ItemDeathProtection
/*    */ {
/*    */   private List<ConsumeEffect<?>> deathEffects;
/*    */   
/*    */   public ItemDeathProtection(List<ConsumeEffect<?>> deathEffects) {
/* 32 */     this.deathEffects = deathEffects;
/*    */   }
/*    */   
/*    */   public static ItemDeathProtection read(PacketWrapper<?> wrapper) {
/* 36 */     List<ConsumeEffect<?>> deathEffects = wrapper.readList(ConsumeEffect::readFull);
/* 37 */     return new ItemDeathProtection(deathEffects);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemDeathProtection deathProtection) {
/* 41 */     wrapper.writeList(deathProtection.deathEffects, ConsumeEffect::writeFull);
/*    */   }
/*    */   
/*    */   public List<ConsumeEffect<?>> getDeathEffects() {
/* 45 */     return this.deathEffects;
/*    */   }
/*    */   
/*    */   public void setDeathEffects(List<ConsumeEffect<?>> deathEffects) {
/* 49 */     this.deathEffects = deathEffects;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 54 */     if (this == obj) return true; 
/* 55 */     if (!(obj instanceof ItemDeathProtection)) return false; 
/* 56 */     ItemDeathProtection that = (ItemDeathProtection)obj;
/* 57 */     return this.deathEffects.equals(that.deathEffects);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 62 */     return Objects.hashCode(this.deathEffects);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 67 */     return "ItemDeathProtection{deathEffects=" + this.deathEffects + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemDeathProtection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */