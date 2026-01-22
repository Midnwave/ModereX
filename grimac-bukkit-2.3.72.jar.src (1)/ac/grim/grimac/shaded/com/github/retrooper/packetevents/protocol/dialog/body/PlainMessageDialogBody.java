/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.body;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
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
/*    */ public class PlainMessageDialogBody
/*    */   implements DialogBody
/*    */ {
/*    */   private final PlainMessage message;
/*    */   
/*    */   public PlainMessageDialogBody(PlainMessage message) {
/* 31 */     this.message = message;
/*    */   }
/*    */   
/*    */   public static PlainMessageDialogBody decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/* 35 */     return new PlainMessageDialogBody(PlainMessage.decode(compound, wrapper));
/*    */   }
/*    */   
/*    */   public static void encode(NBTCompound compound, PacketWrapper<?> wrapper, PlainMessageDialogBody body) {
/* 39 */     PlainMessage.encode(compound, wrapper, body.message);
/*    */   }
/*    */ 
/*    */   
/*    */   public DialogBodyType<?> getType() {
/* 44 */     return DialogBodyTypes.PLAIN_MESSAGE;
/*    */   }
/*    */   
/*    */   public PlainMessage getMessage() {
/* 48 */     return this.message;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\body\PlainMessageDialogBody.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */