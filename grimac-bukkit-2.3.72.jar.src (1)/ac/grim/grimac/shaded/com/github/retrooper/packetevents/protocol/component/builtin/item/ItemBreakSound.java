/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.sound.Sound;
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
/*    */ public class ItemBreakSound
/*    */ {
/*    */   private Sound sound;
/*    */   
/*    */   public ItemBreakSound(Sound sound) {
/* 31 */     this.sound = sound;
/*    */   }
/*    */   
/*    */   public static ItemBreakSound read(PacketWrapper<?> wrapper) {
/* 35 */     Sound sound = Sound.read(wrapper);
/* 36 */     return new ItemBreakSound(sound);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemBreakSound sound) {
/* 40 */     Sound.write(wrapper, sound.sound);
/*    */   }
/*    */   
/*    */   public Sound getSound() {
/* 44 */     return this.sound;
/*    */   }
/*    */   
/*    */   public void setSound(Sound sound) {
/* 48 */     this.sound = sound;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 53 */     if (!(obj instanceof ItemBreakSound)) return false; 
/* 54 */     ItemBreakSound that = (ItemBreakSound)obj;
/* 55 */     return this.sound.equals(that.sound);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 60 */     return Objects.hashCode(this.sound);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemBreakSound.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */