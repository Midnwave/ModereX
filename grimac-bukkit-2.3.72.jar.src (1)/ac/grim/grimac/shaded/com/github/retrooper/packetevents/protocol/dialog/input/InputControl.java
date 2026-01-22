/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.input;
/*    */ 
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
/*    */ public interface InputControl
/*    */ {
/*    */   static InputControl decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/* 30 */     String typeName = compound.getStringTagValueOrThrow("type");
/* 31 */     InputControlType<?> type = (InputControlType)InputControlTypes.getRegistry().getByNameOrThrow(typeName);
/* 32 */     return (InputControl)type.decode(compound, wrapper);
/*    */   }
/*    */ 
/*    */   
/*    */   static void encode(NBTCompound compound, PacketWrapper<?> wrapper, InputControl control) {
/* 37 */     compound.set("type", control.getType().getName(), ResourceLocation::encode, wrapper);
/* 38 */     control.getType().encode(compound, wrapper, control);
/*    */   }
/*    */   
/*    */   InputControlType<?> getType();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\input\InputControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */