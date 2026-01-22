/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.body;
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
/*    */ public interface DialogBody
/*    */ {
/*    */   static DialogBody decode(NBT nbt, PacketWrapper<?> wrapper) {
/* 31 */     NBTCompound compound = (NBTCompound)nbt;
/* 32 */     String typeName = compound.getStringTagValueOrThrow("type");
/* 33 */     DialogBodyType<?> type = (DialogBodyType)DialogBodyTypes.getRegistry().getByNameOrThrow(typeName);
/* 34 */     return (DialogBody)type.decode(compound, wrapper);
/*    */   }
/*    */ 
/*    */   
/*    */   static NBT encode(PacketWrapper<?> wrapper, DialogBody body) {
/* 39 */     NBTCompound compound = new NBTCompound();
/* 40 */     compound.set("type", body.getType().getName(), ResourceLocation::encode, wrapper);
/* 41 */     body.getType().encode(compound, wrapper, body);
/* 42 */     return (NBT)compound;
/*    */   }
/*    */   
/*    */   DialogBodyType<?> getType();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\body\DialogBody.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */