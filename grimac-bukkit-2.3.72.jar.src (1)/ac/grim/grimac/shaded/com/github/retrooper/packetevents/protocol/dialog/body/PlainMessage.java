/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.body;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtDecoder;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtEncoder;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.adventure.AdventureSerializer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
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
/*    */ public class PlainMessage
/*    */ {
/*    */   private final Component contents;
/*    */   private final int width;
/*    */   
/*    */   public PlainMessage(Component contents, int width) {
/* 36 */     this.contents = contents;
/* 37 */     this.width = width;
/*    */   }
/*    */   
/*    */   public static PlainMessage decode(NBT nbt, PacketWrapper<?> wrapper) {
/* 41 */     return decode((NBTCompound)nbt, wrapper);
/*    */   }
/*    */   
/*    */   public static PlainMessage decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/* 45 */     Component contents = (Component)compound.getOrThrow("contents", (NbtDecoder)AdventureSerializer.serializer(wrapper), wrapper);
/* 46 */     int width = compound.getNumberTagValueOrDefault("width", Integer.valueOf(200)).intValue();
/* 47 */     return new PlainMessage(contents, width);
/*    */   }
/*    */   
/*    */   public static NBT encode(PacketWrapper<?> wrapper, PlainMessage message) {
/* 51 */     NBTCompound compound = new NBTCompound();
/* 52 */     encode(compound, wrapper, message);
/* 53 */     return (NBT)compound;
/*    */   }
/*    */   
/*    */   public static void encode(NBTCompound compound, PacketWrapper<?> wrapper, PlainMessage message) {
/* 57 */     compound.set("contents", message.contents, (NbtEncoder)AdventureSerializer.serializer(wrapper), wrapper);
/* 58 */     if (message.width != 200) {
/* 59 */       compound.setTag("width", (NBT)new NBTInt(message.width));
/*    */     }
/*    */   }
/*    */   
/*    */   public Component getContents() {
/* 64 */     return this.contents;
/*    */   }
/*    */   
/*    */   public int getWidth() {
/* 68 */     return this.width;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\body\PlainMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */