/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ public class ItemWeapon
/*    */ {
/*    */   private int itemDamagePerAttack;
/*    */   private float disableBlockingForSeconds;
/*    */   
/*    */   public ItemWeapon(int itemDamagePerAttack, float disableBlockingForSeconds) {
/* 31 */     this.itemDamagePerAttack = itemDamagePerAttack;
/* 32 */     this.disableBlockingForSeconds = disableBlockingForSeconds;
/*    */   }
/*    */   
/*    */   public static ItemWeapon read(PacketWrapper<?> wrapper) {
/* 36 */     int itemDamagePerAttack = wrapper.readVarInt();
/* 37 */     float disableBlockingForSeconds = wrapper.readFloat();
/* 38 */     return new ItemWeapon(itemDamagePerAttack, disableBlockingForSeconds);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemWeapon weapon) {
/* 42 */     wrapper.writeVarInt(weapon.itemDamagePerAttack);
/* 43 */     wrapper.writeFloat(weapon.disableBlockingForSeconds);
/*    */   }
/*    */   
/*    */   public int getItemDamagePerAttack() {
/* 47 */     return this.itemDamagePerAttack;
/*    */   }
/*    */   
/*    */   public void setItemDamagePerAttack(int itemDamagePerAttack) {
/* 51 */     this.itemDamagePerAttack = itemDamagePerAttack;
/*    */   }
/*    */   
/*    */   public float getDisableBlockingForSeconds() {
/* 55 */     return this.disableBlockingForSeconds;
/*    */   }
/*    */   
/*    */   public void setDisableBlockingForSeconds(float disableBlockingForSeconds) {
/* 59 */     this.disableBlockingForSeconds = disableBlockingForSeconds;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 64 */     if (!(obj instanceof ItemWeapon)) return false; 
/* 65 */     ItemWeapon that = (ItemWeapon)obj;
/* 66 */     if (this.itemDamagePerAttack != that.itemDamagePerAttack) return false; 
/* 67 */     return (Float.compare(that.disableBlockingForSeconds, this.disableBlockingForSeconds) == 0);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 72 */     return Objects.hash(new Object[] { Integer.valueOf(this.itemDamagePerAttack), Float.valueOf(this.disableBlockingForSeconds) });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemWeapon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */