/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.wolfvariant.WolfSoundVariant;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.wolfvariant.WolfSoundVariants;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
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
/*    */ public class WolfSoundVariantComponent
/*    */ {
/*    */   private WolfSoundVariant soundVariant;
/*    */   
/*    */   public WolfSoundVariantComponent(WolfSoundVariant soundVariant) {
/* 32 */     this.soundVariant = soundVariant;
/*    */   }
/*    */   
/*    */   public static WolfSoundVariantComponent read(PacketWrapper<?> wrapper) {
/* 36 */     WolfSoundVariant type = (WolfSoundVariant)wrapper.readMappedEntity((IRegistry)WolfSoundVariants.getRegistry());
/* 37 */     return new WolfSoundVariantComponent(type);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, WolfSoundVariantComponent component) {
/* 41 */     wrapper.writeMappedEntity((MappedEntity)component.soundVariant);
/*    */   }
/*    */   
/*    */   public WolfSoundVariant getSoundVariant() {
/* 45 */     return this.soundVariant;
/*    */   }
/*    */   
/*    */   public void setSoundVariant(WolfSoundVariant soundVariant) {
/* 49 */     this.soundVariant = soundVariant;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 54 */     if (!(obj instanceof WolfSoundVariantComponent)) return false; 
/* 55 */     WolfSoundVariantComponent that = (WolfSoundVariantComponent)obj;
/* 56 */     return this.soundVariant.equals(that.soundVariant);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 61 */     return Objects.hashCode(this.soundVariant);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\WolfSoundVariantComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */