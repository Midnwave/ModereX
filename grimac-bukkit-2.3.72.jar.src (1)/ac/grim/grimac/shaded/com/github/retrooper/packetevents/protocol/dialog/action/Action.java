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
/*    */ 
/*    */ @NullMarked
/*    */ public interface Action
/*    */ {
/*    */   static Action decode(NBT nbt, PacketWrapper<?> wrapper) {
/* 31 */     NBTCompound compound = (NBTCompound)nbt;
/* 32 */     String typeName = compound.getStringTagValueOrThrow("type");
/* 33 */     ActionType<?> action = (ActionType)ActionTypes.getRegistry().getByNameOrThrow(typeName);
/* 34 */     return (Action)action.decode(compound, wrapper);
/*    */   }
/*    */ 
/*    */   
/*    */   static NBT encode(PacketWrapper<?> wrapper, Action action) {
/* 39 */     NBTCompound compound = new NBTCompound();
/* 40 */     compound.set("type", action.getType().getName(), ResourceLocation::encode, wrapper);
/* 41 */     action.getType().encode(compound, wrapper, action);
/* 42 */     return (NBT)compound;
/*    */   }
/*    */   
/*    */   ActionType<?> getType();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\action\Action.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */