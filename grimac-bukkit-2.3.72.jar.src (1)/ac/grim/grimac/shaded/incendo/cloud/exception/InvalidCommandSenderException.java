/*     */ package ac.grim.grimac.shaded.incendo.cloud.exception;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.util.TypeUtils;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.STABLE)
/*     */ public final class InvalidCommandSenderException
/*     */   extends CommandParseException
/*     */ {
/*     */   private final Set<Type> requiredSenderTypes;
/*     */   private final Command<?> command;
/*     */   
/*     */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */   public InvalidCommandSenderException(Object commandSender, Type requiredSenderTypes, List<CommandComponent<?>> currentChain, Command<?> command) {
/*  64 */     this(commandSender, new HashSet<>(Collections.singletonList(requiredSenderTypes)), currentChain, command);
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
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */   public InvalidCommandSenderException(Object commandSender, Set<Type> requiredSenderTypes, List<CommandComponent<?>> currentChain, Command<?> command) {
/*  82 */     super(commandSender, currentChain);
/*  83 */     this.requiredSenderTypes = Collections.unmodifiableSet(requiredSenderTypes);
/*  84 */     this.command = command;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<Type> requiredSenderTypes() {
/*  93 */     return this.requiredSenderTypes;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMessage() {
/*  98 */     if (this.requiredSenderTypes.size() == 1) {
/*  99 */       return String.format("%s is not allowed to execute that command. Must be of type %s", new Object[] {
/*     */             
/* 101 */             commandSender().getClass().getSimpleName(), 
/* 102 */             TypeUtils.simpleName(this.requiredSenderTypes.iterator().next())
/*     */           });
/*     */     }
/* 105 */     return String.format("%s is not allowed to execute that command. Must be one of %s", new Object[] {
/*     */           
/* 107 */           commandSender().getClass().getSimpleName(), this.requiredSenderTypes
/* 108 */           .stream().map(TypeUtils::simpleName).collect(Collectors.joining(", "))
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public Command<?> command() {
/* 119 */     return this.command;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\exception\InvalidCommandSenderException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */