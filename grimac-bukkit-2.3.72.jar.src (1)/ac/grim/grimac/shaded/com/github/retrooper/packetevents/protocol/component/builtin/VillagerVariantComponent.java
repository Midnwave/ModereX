/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.villager.type.VillagerType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.villager.type.VillagerTypes;
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
/*    */ public class VillagerVariantComponent
/*    */ {
/*    */   private VillagerType villagerType;
/*    */   
/*    */   public VillagerVariantComponent(VillagerType villagerType) {
/* 32 */     this.villagerType = villagerType;
/*    */   }
/*    */   
/*    */   public static VillagerVariantComponent read(PacketWrapper<?> wrapper) {
/* 36 */     VillagerType type = (VillagerType)wrapper.readMappedEntity((IRegistry)VillagerTypes.getRegistry());
/* 37 */     return new VillagerVariantComponent(type);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, VillagerVariantComponent component) {
/* 41 */     wrapper.writeMappedEntity((MappedEntity)component.villagerType);
/*    */   }
/*    */   
/*    */   public VillagerType getVillagerType() {
/* 45 */     return this.villagerType;
/*    */   }
/*    */   
/*    */   public void setVillagerType(VillagerType villagerType) {
/* 49 */     this.villagerType = villagerType;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 54 */     if (!(obj instanceof VillagerVariantComponent)) return false; 
/* 55 */     VillagerVariantComponent that = (VillagerVariantComponent)obj;
/* 56 */     return this.villagerType.equals(that.villagerType);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 61 */     return Objects.hashCode(this.villagerType);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\VillagerVariantComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */