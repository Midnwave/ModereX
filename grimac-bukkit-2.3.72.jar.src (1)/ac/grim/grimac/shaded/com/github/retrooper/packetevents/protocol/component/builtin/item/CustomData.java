/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CustomData
/*    */ {
/*    */   public static NBTCompound read(PacketWrapper<?> wrapper) {
/* 32 */     NBT nbt = wrapper.readNBTRaw();
/* 33 */     if (nbt instanceof NBTCompound) {
/* 34 */       return (NBTCompound)nbt;
/*    */     }
/* 36 */     if (nbt instanceof ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString);
/*    */ 
/*    */     
/* 39 */     throw new UnsupportedOperationException("Unsupported custom data nbt type: " + nbt.getType());
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, NBTCompound compound) {
/* 43 */     wrapper.writeNBT(compound);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\CustomData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */