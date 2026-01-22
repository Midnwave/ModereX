/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.action;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import org.jspecify.annotations.NullMarked;
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
/*    */ @NullMarked
/*    */ public class DynamicCustomAction
/*    */   implements Action
/*    */ {
/*    */   private final ResourceLocation id;
/*    */   private final NBTCompound additions;
/*    */   
/*    */   public DynamicCustomAction(ResourceLocation id, NBTCompound additions) {
/* 34 */     this.id = id;
/* 35 */     this.additions = additions;
/*    */   }
/*    */   
/*    */   public static DynamicCustomAction decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/* 39 */     ResourceLocation id = (ResourceLocation)compound.getOrThrow("id", ResourceLocation::decode, wrapper);
/* 40 */     NBTCompound additions = compound.getCompoundTagOrNull("additions");
/* 41 */     return new DynamicCustomAction(id, additions);
/*    */   }
/*    */   
/*    */   public static void encode(NBTCompound compound, PacketWrapper<?> wrapper, DynamicCustomAction action) {
/* 45 */     compound.set("id", action.id, ResourceLocation::encode, wrapper);
/* 46 */     if (action.additions != null) {
/* 47 */       compound.setTag("additions", (NBT)action.additions);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public ActionType<?> getType() {
/* 53 */     return ActionTypes.DYNAMIC_CUSTOM;
/*    */   }
/*    */   
/*    */   public ResourceLocation getId() {
/* 57 */     return this.id;
/*    */   }
/*    */   
/*    */   public NBTCompound getAdditions() {
/* 61 */     return this.additions;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\action\DynamicCustomAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */