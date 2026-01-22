/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.serializer;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTLimiter;
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
/*    */ 
/*    */ public interface NBTReader<T extends ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT, IN>
/*    */ {
/*    */   default T deserializeTag(NBTLimiter limiter, IN from) throws IOException {
/* 28 */     return deserializeTag(limiter, from, true);
/*    */   }
/*    */   
/*    */   T deserializeTag(NBTLimiter paramNBTLimiter, IN paramIN, boolean paramBoolean) throws IOException;
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\nbt\serializer\NBTReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */