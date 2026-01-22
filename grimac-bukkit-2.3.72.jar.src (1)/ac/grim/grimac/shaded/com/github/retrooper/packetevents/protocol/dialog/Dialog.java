/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.CopyableEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.DeepComparableEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.dialog.DialogLike;
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
/*    */ @NullMarked
/*    */ public interface Dialog
/*    */   extends MappedEntity, DeepComparableEntity, CopyableEntity<Dialog>, DialogLike
/*    */ {
/*    */   static Dialog read(PacketWrapper<?> wrapper) {
/* 38 */     return (Dialog)wrapper.readMappedEntityOrDirect((IRegistry)Dialogs.getRegistry(), Dialog::readDirect);
/*    */   }
/*    */   
/*    */   static void write(PacketWrapper<?> wrapper, Dialog dialog) {
/* 42 */     wrapper.writeMappedEntityOrDirect(dialog, Dialog::writeDirect);
/*    */   }
/*    */   
/*    */   static Dialog readDirect(PacketWrapper<?> wrapper) {
/* 46 */     return decodeDirect(wrapper.readNBTRaw(), wrapper, (TypesBuilderData)null);
/*    */   }
/*    */   
/*    */   static void writeDirect(PacketWrapper<?> wrapper, Dialog dialog) {
/* 50 */     wrapper.writeNBTRaw(encodeDirect(dialog, wrapper));
/*    */   }
/*    */   
/*    */   static Dialog decode(NBT nbt, PacketWrapper<?> wrapper) {
/* 54 */     if (nbt instanceof NBTString) {
/* 55 */       return (Dialog)wrapper.replaceRegistry((IRegistry)Dialogs.getRegistry()).getByNameOrThrow(((NBTString)nbt).getValue());
/*    */     }
/* 57 */     return decodeDirect(nbt, wrapper, (TypesBuilderData)null);
/*    */   }
/*    */   
/*    */   static NBT encode(PacketWrapper<?> wrapper, Dialog dialog) {
/* 61 */     if (dialog.isRegistered()) {
/* 62 */       return (NBT)new NBTString(dialog.getName().toString());
/*    */     }
/* 64 */     return encodeDirect(dialog, wrapper);
/*    */   }
/*    */   
/*    */   @Internal
/*    */   static Dialog decodeDirect(NBT nbt, PacketWrapper<?> wrapper, @Nullable TypesBuilderData data) {
/* 69 */     NBTCompound compound = (NBTCompound)nbt;
/* 70 */     String dialogTypeName = compound.getStringTagValueOrThrow("type");
/* 71 */     DialogType<?> dialogType = (DialogType)DialogTypes.getRegistry().getByNameOrThrow(dialogTypeName);
/*    */     
/* 73 */     return (Dialog)dialogType.decode(compound, wrapper).copy(data);
/*    */   }
/*    */ 
/*    */   
/*    */   @Internal
/*    */   static NBT encodeDirect(Dialog dialog, PacketWrapper<?> wrapper) {
/* 79 */     NBTCompound compound = new NBTCompound();
/* 80 */     compound.setTag("type", (NBT)new NBTString(dialog.getType().getName().toString()));
/* 81 */     dialog.getType().encode(compound, wrapper, dialog);
/* 82 */     return (NBT)compound;
/*    */   }
/*    */   
/*    */   DialogType<?> getType();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\Dialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */