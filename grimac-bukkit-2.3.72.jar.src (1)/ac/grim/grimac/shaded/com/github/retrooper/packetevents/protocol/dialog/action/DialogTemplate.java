/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.action;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ @NullMarked
/*    */ public class DialogTemplate
/*    */ {
/*    */   private final String raw;
/*    */   
/*    */   public DialogTemplate(String raw) {
/* 35 */     this.raw = raw;
/*    */   }
/*    */   
/*    */   public static DialogTemplate decode(NBT nbt, PacketWrapper<?> wrapper) {
/* 39 */     return new DialogTemplate(((NBTString)nbt).getValue());
/*    */   }
/*    */   
/*    */   public static NBT encode(PacketWrapper<?> wrapper, DialogTemplate template) {
/* 43 */     return (NBT)new NBTString(template.raw);
/*    */   }
/*    */   
/*    */   public String getRaw() {
/* 47 */     return this.raw;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\action\DialogTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */