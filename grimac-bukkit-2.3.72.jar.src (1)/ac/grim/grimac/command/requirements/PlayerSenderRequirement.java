/*    */ package ac.grim.grimac.command.requirements;
/*    */ 
/*    */ import ac.grim.grimac.command.SenderRequirement;
/*    */ import ac.grim.grimac.platform.api.sender.Sender;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.utils.anticheat.MessageUtil;
/*    */ 
/*    */ public final class PlayerSenderRequirement
/*    */   implements SenderRequirement
/*    */ {
/* 12 */   public static final PlayerSenderRequirement PLAYER_SENDER_REQUIREMENT = new PlayerSenderRequirement();
/*    */ 
/*    */   
/*    */   public Component errorMessage(Sender sender) {
/* 16 */     return MessageUtil.getParsedComponent(sender, "run-as-player", "%prefix% &cThis command can only be used by players!");
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean evaluateRequirement(CommandContext<Sender> commandContext) {
/* 21 */     return ((Sender)commandContext.sender()).isPlayer();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\command\requirements\PlayerSenderRequirement.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */