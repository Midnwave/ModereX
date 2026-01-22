/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat;
/*    */ 
/*    */ public class SignedCommandArgument {
/*    */   private String argument;
/*    */   private MessageSignature signature;
/*    */   
/*    */   public SignedCommandArgument(String argument, MessageSignature signature) {
/*  8 */     this.argument = argument;
/*  9 */     this.signature = signature;
/*    */   }
/*    */   
/*    */   public String getArgument() {
/* 13 */     return this.argument;
/*    */   }
/*    */   
/*    */   public void setArgument(String argument) {
/* 17 */     this.argument = argument;
/*    */   }
/*    */   
/*    */   public MessageSignature getSignature() {
/* 21 */     return this.signature;
/*    */   }
/*    */   
/*    */   public void setSignature(MessageSignature signature) {
/* 25 */     this.signature = signature;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\SignedCommandArgument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */