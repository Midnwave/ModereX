/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.input;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
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
/*    */ public class Input
/*    */ {
/*    */   private final String key;
/*    */   private final InputControl control;
/*    */   
/*    */   public Input(String key, InputControl control) {
/* 34 */     this.key = key;
/* 35 */     this.control = control;
/*    */   }
/*    */   
/*    */   public static Input decode(NBT nbt, PacketWrapper<?> wrapper) {
/* 39 */     NBTCompound compound = (NBTCompound)nbt;
/* 40 */     String key = compound.getStringTagValueOrThrow("key");
/* 41 */     InputControl control = InputControl.decode(compound, wrapper);
/* 42 */     return new Input(key, control);
/*    */   }
/*    */   
/*    */   public static NBT encode(PacketWrapper<?> wrapper, Input input) {
/* 46 */     NBTCompound compound = new NBTCompound();
/* 47 */     compound.setTag("key", (NBT)new NBTString(input.key));
/* 48 */     InputControl.encode(compound, wrapper, input.control);
/* 49 */     return (NBT)compound;
/*    */   }
/*    */   
/*    */   public String getKey() {
/* 53 */     return this.key;
/*    */   }
/*    */   
/*    */   public InputControl getControl() {
/* 57 */     return this.control;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\input\Input.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */