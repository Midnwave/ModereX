/*     */ package ac.grim.grimac.shaded.incendo.cloud.execution.postprocessor;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import java.util.Objects;
/*     */ import javax.annotation.CheckReturnValue;
/*     */ import javax.annotation.Nullable;
/*     */ import javax.annotation.ParametersAreNonnullByDefault;
/*     */ import javax.annotation.concurrent.Immutable;
/*     */ import org.apiguardian.api.API;
/*     */ import org.immutables.value.Generated;
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
/*     */ @ParametersAreNonnullByDefault
/*     */ @CheckReturnValue
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ @Generated(from = "CommandPostprocessingContext", generator = "Immutables")
/*     */ @Immutable
/*     */ final class CommandPostprocessingContextImpl<C>
/*     */   implements CommandPostprocessingContext<C>
/*     */ {
/*     */   private final CommandContext<C> commandContext;
/*     */   private final Command<C> command;
/*     */   
/*     */   private CommandPostprocessingContextImpl(CommandContext<C> commandContext, Command<C> command) {
/*  57 */     this.commandContext = Objects.<CommandContext<C>>requireNonNull(commandContext, "commandContext");
/*  58 */     this.command = Objects.<Command<C>>requireNonNull(command, "command");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private CommandPostprocessingContextImpl(CommandPostprocessingContextImpl<C> original, CommandContext<C> commandContext, Command<C> command) {
/*  65 */     this.commandContext = commandContext;
/*  66 */     this.command = command;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandContext<C> commandContext() {
/*  74 */     return this.commandContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Command<C> command() {
/*  82 */     return this.command;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final CommandPostprocessingContextImpl<C> withCommandContext(CommandContext<C> value) {
/*  92 */     if (this.commandContext == value) return this; 
/*  93 */     CommandContext<C> newValue = Objects.<CommandContext<C>>requireNonNull(value, "commandContext");
/*  94 */     return new CommandPostprocessingContextImpl(this, newValue, this.command);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final CommandPostprocessingContextImpl<C> withCommand(Command<C> value) {
/* 104 */     if (this.command == value) return this; 
/* 105 */     Command<C> newValue = Objects.<Command<C>>requireNonNull(value, "command");
/* 106 */     return new CommandPostprocessingContextImpl(this, this.commandContext, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/* 115 */     if (this == another) return true; 
/* 116 */     return (another instanceof CommandPostprocessingContextImpl && 
/* 117 */       equalTo(0, (CommandPostprocessingContextImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, CommandPostprocessingContextImpl<?> another) {
/* 121 */     return (this.commandContext.equals(another.commandContext) && this.command
/* 122 */       .equals(another.command));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 131 */     int h = 5381;
/* 132 */     h += (h << 5) + this.commandContext.hashCode();
/* 133 */     h += (h << 5) + this.command.hashCode();
/* 134 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 143 */     return "CommandPostprocessingContext{commandContext=" + this.commandContext + ", command=" + this.command + "}";
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
/*     */   public static <C> CommandPostprocessingContextImpl<C> of(CommandContext<C> commandContext, Command<C> command) {
/* 157 */     return new CommandPostprocessingContextImpl<>(commandContext, command);
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
/*     */   public static <C> CommandPostprocessingContextImpl<C> copyOf(CommandPostprocessingContext<C> instance) {
/* 169 */     if (instance instanceof CommandPostprocessingContextImpl) {
/* 170 */       return (CommandPostprocessingContextImpl<C>)instance;
/*     */     }
/* 172 */     return of(instance.commandContext(), instance.command());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\execution\postprocessor\CommandPostprocessingContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */