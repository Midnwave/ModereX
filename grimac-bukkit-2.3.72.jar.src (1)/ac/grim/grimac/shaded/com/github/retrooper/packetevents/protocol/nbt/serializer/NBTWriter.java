/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.serializer;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import java.io.IOException;
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
/*    */ public interface NBTWriter<T extends NBT, OUT>
/*    */ {
/*    */   default void serializeTag(OUT to, T tag) throws IOException {
/* 27 */     serializeTag(to, (NBT)tag, true);
/*    */   }
/*    */   
/*    */   void serializeTag(OUT paramOUT, NBT paramNBT, boolean paramBoolean) throws IOException;
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\nbt\serializer\NBTWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */