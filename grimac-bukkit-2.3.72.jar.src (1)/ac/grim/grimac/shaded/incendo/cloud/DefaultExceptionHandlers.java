/*     */ package ac.grim.grimac.shaded.incendo.cloud;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.Caption;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionVariable;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.StandardCaptionKeys;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.ArgumentParseException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.CommandExecutionException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.InvalidCommandSenderException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.InvalidSyntaxException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.NoPermissionException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.NoSuchCommandException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.handling.ExceptionContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.handling.ExceptionController;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.tuple.Pair;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.tuple.Triplet;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.util.TypeUtils;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.stream.Collectors;
/*     */ import org.apiguardian.api.API;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.INTERNAL)
/*     */ final class DefaultExceptionHandlers<C>
/*     */ {
/*     */   private final Consumer<Triplet<CommandContext<C>, Caption, List<CaptionVariable>>> messageSender;
/*     */   private final Consumer<Pair<String, Throwable>> logger;
/*     */   private final ExceptionController<C> exceptionController;
/*     */   
/*     */   DefaultExceptionHandlers(Consumer<Triplet<CommandContext<C>, Caption, List<CaptionVariable>>> messageSender, Consumer<Pair<String, Throwable>> logger, ExceptionController<C> exceptionController) {
/*  66 */     this.messageSender = Objects.<Consumer<Triplet<CommandContext<C>, Caption, List<CaptionVariable>>>>requireNonNull(messageSender, "messageSender");
/*  67 */     this.logger = Objects.<Consumer<Pair<String, Throwable>>>requireNonNull(logger, "logger");
/*  68 */     this.exceptionController = Objects.<ExceptionController<C>>requireNonNull(exceptionController, "exceptionController");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void register() {
/*  75 */     this.exceptionController.registerHandler(Throwable.class, context -> {
/*     */           sendMessage(context, StandardCaptionKeys.EXCEPTION_UNEXPECTED, new CaptionVariable[0]);
/*     */           log("An unhandled exception was thrown during command execution", context.exception());
/*     */         });
/*  79 */     this.exceptionController.registerHandler(CommandExecutionException.class, context -> {
/*     */           sendMessage(context, StandardCaptionKeys.EXCEPTION_UNEXPECTED, new CaptionVariable[0]);
/*     */           log("Exception executing command handler", ((CommandExecutionException)context.exception()).getCause());
/*     */         });
/*  83 */     this.exceptionController.registerHandler(ArgumentParseException.class, context -> sendMessage(context, StandardCaptionKeys.EXCEPTION_INVALID_ARGUMENT, new CaptionVariable[] { CaptionVariable.of("cause", ((ArgumentParseException)context.exception()).getCause().getMessage()) }));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  90 */     this.exceptionController.registerHandler(NoSuchCommandException.class, context -> sendMessage(context, StandardCaptionKeys.EXCEPTION_NO_SUCH_COMMAND, new CaptionVariable[] { CaptionVariable.of("command", ((NoSuchCommandException)context.exception()).suppliedCommand()) }));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  97 */     this.exceptionController.registerHandler(NoPermissionException.class, context -> sendMessage(context, StandardCaptionKeys.EXCEPTION_NO_PERMISSION, new CaptionVariable[] { CaptionVariable.of("permission", ((NoPermissionException)context.exception()).permissionResult().permission().permissionString()) }));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 104 */     this.exceptionController.registerHandler(InvalidCommandSenderException.class, context -> {
/*     */           boolean multiple = (((InvalidCommandSenderException)context.exception()).requiredSenderTypes().size() != 1);
/*     */ 
/*     */ 
/*     */           
/*     */           String expected = multiple ? ((InvalidCommandSenderException)context.exception()).requiredSenderTypes().stream().map(TypeUtils::simpleName).collect(Collectors.joining(", ")) : TypeUtils.simpleName(((InvalidCommandSenderException)context.exception()).requiredSenderTypes().iterator().next());
/*     */ 
/*     */ 
/*     */           
/*     */           sendMessage(context, multiple ? StandardCaptionKeys.EXCEPTION_INVALID_SENDER_LIST : StandardCaptionKeys.EXCEPTION_INVALID_SENDER, new CaptionVariable[] { CaptionVariable.of("actual", context.context().sender().getClass().getSimpleName()), CaptionVariable.of("expected", expected) });
/*     */         });
/*     */ 
/*     */     
/* 117 */     this.exceptionController.registerHandler(InvalidSyntaxException.class, context -> sendMessage(context, StandardCaptionKeys.EXCEPTION_INVALID_SYNTAX, new CaptionVariable[] { CaptionVariable.of("syntax", ((InvalidSyntaxException)context.exception()).correctSyntax()) }));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void sendMessage(ExceptionContext<C, ?> context, Caption caption, CaptionVariable... variables) {
/* 131 */     sendMessage(context.context(), caption, variables);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void sendMessage(CommandContext<C> context, Caption caption, CaptionVariable... variables) {
/* 139 */     this.messageSender.accept(Triplet.of(context, caption, Arrays.asList(variables)));
/*     */   }
/*     */   
/*     */   private void log(String message, Throwable exception) {
/* 143 */     this.logger.accept(Pair.of(message, exception));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\DefaultExceptionHandlers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */