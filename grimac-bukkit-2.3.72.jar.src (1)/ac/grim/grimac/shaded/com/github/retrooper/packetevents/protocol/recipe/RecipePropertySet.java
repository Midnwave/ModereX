/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import java.util.LinkedHashSet;
/*    */ import java.util.Set;
/*    */ import java.util.function.IntFunction;
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
/*    */ public final class RecipePropertySet
/*    */ {
/* 31 */   public static final ResourceLocation SMITHING_BASE = ResourceLocation.minecraft("smithing_base");
/* 32 */   public static final ResourceLocation SMITHING_TEMPLATE = ResourceLocation.minecraft("smithing_template");
/* 33 */   public static final ResourceLocation SMITHING_ADDITION = ResourceLocation.minecraft("smithing_addition");
/* 34 */   public static final ResourceLocation FURNACE_INPUT = ResourceLocation.minecraft("furnace_input");
/* 35 */   public static final ResourceLocation BLAST_FURNACE_INPUT = ResourceLocation.minecraft("blast_furnace_input");
/* 36 */   public static final ResourceLocation SMOKER_INPUT = ResourceLocation.minecraft("smoker_input");
/* 37 */   public static final ResourceLocation CAMPFIRE_INPUT = ResourceLocation.minecraft("campfire_input");
/*    */   
/*    */   private Set<ItemType> items;
/*    */   
/*    */   public RecipePropertySet(Set<ItemType> items) {
/* 42 */     this.items = items;
/*    */   }
/*    */   
/*    */   public static RecipePropertySet read(PacketWrapper<?> wrapper) {
/* 46 */     LinkedHashSet<ItemType> items = (LinkedHashSet<ItemType>)wrapper.readCollection(LinkedHashSet::new, ew -> (ItemType)ew.readMappedEntity((IRegistry)ItemTypes.getRegistry()));
/*    */     
/* 48 */     return new RecipePropertySet(items);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, RecipePropertySet set) {
/* 52 */     wrapper.writeCollection(set.items, PacketWrapper::writeMappedEntity);
/*    */   }
/*    */   
/*    */   public Set<ItemType> getItems() {
/* 56 */     return this.items;
/*    */   }
/*    */   
/*    */   public void setItems(Set<ItemType> items) {
/* 60 */     this.items = items;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\RecipePropertySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */