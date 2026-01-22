/*    */ package ac.grim.grimac.checks.impl.chat;
/*    */ 
/*    */ import ac.grim.grimac.api.config.ConfigManager;
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.impl.multiactions.MultiActionsC;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatCommand;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatCommandUnsigned;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.function.Predicate;
/*    */ import java.util.regex.Pattern;
/*    */ 
/*    */ @CheckData(name = "ChatC", description = "Moving while chatting", experimental = true)
/*    */ public class ChatC
/*    */   extends Check implements PacketCheck {
/*    */   public ChatC(GrimPlayer player) {
/* 22 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   private Predicate<String> exemptRegex;
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 30 */     if (event.getPacketType() == PacketType.Play.Client.CHAT_MESSAGE)
/*    */     {
/* 32 */       check((new WrapperPlayClientChatMessage(event)).getMessage(), event);
/*    */     }
/*    */     
/* 35 */     if (event.getPacketType() == PacketType.Play.Client.CHAT_COMMAND_UNSIGNED) {
/* 36 */       check("/" + (new WrapperPlayClientChatCommandUnsigned(event)).getCommand(), event);
/*    */     }
/*    */     
/* 39 */     if (event.getPacketType() == PacketType.Play.Client.CHAT_COMMAND)
/*    */     {
/* 41 */       check("/" + (new WrapperPlayClientChatCommand(event)).getCommand(), event);
/*    */     }
/*    */   }
/*    */   
/*    */   private void check(String message, PacketReceiveEvent event) {
/* 46 */     if (this.exemptRegex != null && this.exemptRegex.test(message)) {
/*    */       return;
/*    */     }
/*    */     
/* 50 */     String verbose = MultiActionsC.getVerbose(this.player);
/* 51 */     if (!verbose.isEmpty() && flagAndAlert(verbose) && shouldModifyPackets()) {
/* 52 */       event.setCancelled(true);
/* 53 */       this.player.onPacketCancel();
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onReload(ConfigManager config) {
/* 59 */     String regexString = config.getStringElse(getConfigName() + ".exempt-regex", null);
/* 60 */     this.exemptRegex = (regexString == null) ? null : Pattern.compile(regexString).asMatchPredicate();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\chat\ChatC.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */