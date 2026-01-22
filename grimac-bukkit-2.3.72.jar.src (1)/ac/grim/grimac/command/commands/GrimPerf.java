/*    */ package ac.grim.grimac.command.commands;
/*    */ import ac.grim.grimac.platform.api.sender.Sender;
/*    */ import ac.grim.grimac.predictionengine.MovementCheckRunner;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.BuildableComponent;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.TextComponent;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.NamedTextColor;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*    */ 
/*    */ public class GrimPerf {
/*    */   public void register(CommandManager<Sender> commandManager) {
/* 15 */     Command.Builder<Sender> grimCommand = commandManager.commandBuilder("grim", new String[] { "grimac" });
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 20 */     Command.Builder<Sender> configuredBuilder = grimCommand.literal("perf", new String[] { "performance" }).permission("grim.performance").handler(this::handlePerformance);
/*    */     
/* 22 */     commandManager.command(configuredBuilder);
/*    */   }
/*    */   
/*    */   private void handlePerformance(CommandContext<Sender> context) {
/* 26 */     Sender sender = (Sender)context.sender();
/*    */     
/* 28 */     double millis = MovementCheckRunner.predictionNanos / 1000000.0D;
/* 29 */     double longMillis = MovementCheckRunner.longPredictionNanos / 1000000.0D;
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 34 */     BuildableComponent buildableComponent1 = ((TextComponent.Builder)((TextComponent.Builder)Component.text().append((Component)Component.text("Milliseconds per prediction (avg. 500): ", (TextColor)NamedTextColor.GRAY))).append((Component)Component.text(millis, (TextColor)NamedTextColor.WHITE))).build();
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 39 */     BuildableComponent buildableComponent2 = ((TextComponent.Builder)((TextComponent.Builder)Component.text().append((Component)Component.text("Milliseconds per prediction (avg. 20k): ", (TextColor)NamedTextColor.GRAY))).append((Component)Component.text(longMillis, (TextColor)NamedTextColor.WHITE))).build();
/*    */     
/* 41 */     sender.sendMessage((Component)buildableComponent1);
/* 42 */     sender.sendMessage((Component)buildableComponent2);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\command\commands\GrimPerf.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */