/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
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
/*    */ public class ItemFireworks
/*    */ {
/*    */   private int flightDuration;
/*    */   private List<FireworkExplosion> explosions;
/*    */   
/*    */   public ItemFireworks(int flightDuration, List<FireworkExplosion> explosions) {
/* 32 */     this.flightDuration = flightDuration;
/* 33 */     this.explosions = explosions;
/*    */   }
/*    */   
/*    */   public static ItemFireworks read(PacketWrapper<?> wrapper) {
/* 37 */     int flightDuration = wrapper.readVarInt();
/* 38 */     List<FireworkExplosion> explosions = wrapper.readList(FireworkExplosion::read);
/* 39 */     return new ItemFireworks(flightDuration, explosions);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemFireworks fireworks) {
/* 43 */     wrapper.writeVarInt(fireworks.flightDuration);
/* 44 */     wrapper.writeList(fireworks.explosions, FireworkExplosion::write);
/*    */   }
/*    */   
/*    */   public int getFlightDuration() {
/* 48 */     return this.flightDuration;
/*    */   }
/*    */   
/*    */   public void setFlightDuration(int flightDuration) {
/* 52 */     this.flightDuration = flightDuration;
/*    */   }
/*    */   
/*    */   public void addExplosion(FireworkExplosion explosion) {
/* 56 */     this.explosions.add(explosion);
/*    */   }
/*    */   
/*    */   public List<FireworkExplosion> getExplosions() {
/* 60 */     return this.explosions;
/*    */   }
/*    */   
/*    */   public void setExplosions(List<FireworkExplosion> explosions) {
/* 64 */     this.explosions = explosions;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 69 */     if (this == obj) return true; 
/* 70 */     if (!(obj instanceof ItemFireworks)) return false; 
/* 71 */     ItemFireworks that = (ItemFireworks)obj;
/* 72 */     if (this.flightDuration != that.flightDuration) return false; 
/* 73 */     return this.explosions.equals(that.explosions);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 78 */     return Objects.hash(new Object[] { Integer.valueOf(this.flightDuration), this.explosions });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemFireworks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */