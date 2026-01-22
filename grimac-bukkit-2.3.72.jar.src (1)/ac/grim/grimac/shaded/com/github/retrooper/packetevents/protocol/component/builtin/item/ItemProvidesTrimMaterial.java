/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.trimmaterial.TrimMaterial;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.trimmaterial.TrimMaterials;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MaybeMappedEntity;
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
/*    */ 
/*    */ public class ItemProvidesTrimMaterial
/*    */ {
/*    */   private MaybeMappedEntity<TrimMaterial> material;
/*    */   
/*    */   public ItemProvidesTrimMaterial(MaybeMappedEntity<TrimMaterial> material) {
/* 33 */     this.material = material;
/*    */   }
/*    */   
/*    */   public static ItemProvidesTrimMaterial read(PacketWrapper<?> wrapper) {
/* 37 */     MaybeMappedEntity<TrimMaterial> material = MaybeMappedEntity.read(wrapper, (IRegistry)TrimMaterials.getRegistry(), TrimMaterial::read);
/* 38 */     return new ItemProvidesTrimMaterial(material);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemProvidesTrimMaterial material) {
/* 42 */     MaybeMappedEntity.write(wrapper, material.material, TrimMaterial::write);
/*    */   }
/*    */   
/*    */   public MaybeMappedEntity<TrimMaterial> getMaterial() {
/* 46 */     return this.material;
/*    */   }
/*    */   
/*    */   public void setMaterial(MaybeMappedEntity<TrimMaterial> material) {
/* 50 */     this.material = material;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 55 */     if (!(obj instanceof ItemProvidesTrimMaterial)) return false; 
/* 56 */     ItemProvidesTrimMaterial that = (ItemProvidesTrimMaterial)obj;
/* 57 */     return this.material.equals(that.material);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 62 */     return Objects.hashCode(this.material);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemProvidesTrimMaterial.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */