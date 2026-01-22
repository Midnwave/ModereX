/*    */ package ac.grim.grimac.manager.init.start;
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.command.SenderRequirement;
/*    */ import ac.grim.grimac.command.commands.GrimSendAlert;
/*    */ import ac.grim.grimac.command.commands.GrimStopSpectating;
/*    */ import ac.grim.grimac.command.commands.GrimVersion;
/*    */ import ac.grim.grimac.platform.api.sender.Sender;
/*    */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.exception.InvalidSyntaxException;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.exception.handling.ExceptionContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKey;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.processors.requirements.RequirementApplicable;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.processors.requirements.RequirementFailureHandler;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.processors.requirements.RequirementPostprocessor;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.processors.requirements.Requirements;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.ComponentLike;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.NamedTextColor;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*    */ import ac.grim.grimac.utils.anticheat.MessageUtil;
/*    */ import java.util.function.Function;
/*    */ import java.util.function.Supplier;
/*    */ 
/*    */ public class CommandRegister implements StartableInitable {
/* 25 */   public static final CloudKey<Requirements<Sender, SenderRequirement>> REQUIREMENT_KEY = CloudKey.of("requirements", new TypeToken<Requirements<Sender, SenderRequirement>>()
/*    */       {
/*    */       
/*    */       });
/*    */ 
/*    */   
/* 31 */   public static final RequirementApplicable.RequirementApplicableFactory<Sender, SenderRequirement> REQUIREMENT_FACTORY = RequirementApplicable.factory(REQUIREMENT_KEY);
/*    */   
/*    */   private static boolean commandsRegistered = false;
/*    */   
/*    */   private final Supplier<CommandManager<Sender>> commandManagerSupplier;
/*    */   
/*    */   public CommandRegister(Supplier<CommandManager<Sender>> commandManagerSupplier) {
/* 38 */     this.commandManagerSupplier = commandManagerSupplier;
/*    */   }
/*    */ 
/*    */   
/*    */   public static void registerCommands(CommandManager<Sender> commandManager) {
/* 43 */     if (commandsRegistered)
/* 44 */       return;  (new GrimPerf()).register(commandManager);
/* 45 */     (new GrimDebug()).register(commandManager);
/* 46 */     (new GrimAlerts()).register(commandManager);
/* 47 */     (new GrimProfile()).register(commandManager);
/* 48 */     (new GrimSendAlert()).register(commandManager);
/* 49 */     (new GrimHelp()).register(commandManager);
/* 50 */     (new GrimHistory()).register(commandManager);
/* 51 */     (new GrimReload()).register(commandManager);
/* 52 */     (new GrimSpectate()).register(commandManager);
/* 53 */     (new GrimStopSpectating()).register(commandManager);
/* 54 */     (new GrimLog()).register(commandManager);
/* 55 */     (new GrimVerbose()).register(commandManager);
/* 56 */     (new GrimVersion()).register(commandManager);
/* 57 */     (new GrimDump()).register(commandManager);
/* 58 */     (new GrimBrands()).register(commandManager);
/* 59 */     (new GrimList()).register(commandManager);
/*    */ 
/*    */     
/* 62 */     RequirementPostprocessor<Sender, SenderRequirement> senderRequirementPostprocessor = RequirementPostprocessor.of(REQUIREMENT_KEY, (RequirementFailureHandler)new GrimCommandFailureHandler());
/*    */ 
/*    */ 
/*    */     
/* 66 */     commandManager.registerCommandPostProcessor((CommandPostprocessor)senderRequirementPostprocessor);
/* 67 */     registerExceptionHandler(commandManager, InvalidSyntaxException.class, e -> MessageUtil.miniMessage(e.correctSyntax()));
/* 68 */     commandsRegistered = true;
/*    */   }
/*    */   
/*    */   protected static <E extends Exception> void registerExceptionHandler(CommandManager<Sender> commandManager, Class<E> ex, Function<E, ComponentLike> toComponent) {
/* 72 */     commandManager.exceptionController().registerHandler(ex, c -> ((Sender)c.context().sender()).sendMessage(((ComponentLike)toComponent.apply((Exception)c.exception())).asComponent().colorIfAbsent((TextColor)NamedTextColor.RED)));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void start() {
/* 80 */     CommandManager<Sender> commandManager = this.commandManagerSupplier.get();
/* 81 */     registerCommands(commandManager);
/*    */     
/* 83 */     if (GrimAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("check-for-updates", true))
/* 84 */       GrimVersion.checkForUpdatesAsync(GrimAPI.INSTANCE.getPlatformServer().getConsoleSender()); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\init\start\CommandRegister.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */