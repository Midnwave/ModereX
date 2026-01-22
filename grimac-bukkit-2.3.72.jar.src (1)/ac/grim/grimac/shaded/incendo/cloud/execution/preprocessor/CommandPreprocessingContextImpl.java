/*     */ package ac.grim.grimac.shaded.incendo.cloud.execution.preprocessor;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
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
/*     */ @Generated(from = "CommandPreprocessingContext", generator = "Immutables")
/*     */ @Immutable
/*     */ final class CommandPreprocessingContextImpl<C>
/*     */   implements CommandPreprocessingContext<C>
/*     */ {
/*     */   private final CommandContext<C> commandContext;
/*     */   private final CommandInput commandInput;
/*     */   
/*     */   private CommandPreprocessingContextImpl(CommandContext<C> commandContext, CommandInput commandInput) {
/*  57 */     this.commandContext = Objects.<CommandContext<C>>requireNonNull(commandContext, "commandContext");
/*  58 */     this.commandInput = Objects.<CommandInput>requireNonNull(commandInput, "commandInput");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private CommandPreprocessingContextImpl(CommandPreprocessingContextImpl<C> original, CommandContext<C> commandContext, CommandInput commandInput) {
/*  65 */     this.commandContext = commandContext;
/*  66 */     this.commandInput = commandInput;
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
/*     */   public CommandInput commandInput() {
/*  82 */     return this.commandInput;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final CommandPreprocessingContextImpl<C> withCommandContext(CommandContext<C> value) {
/*  92 */     if (this.commandContext == value) return this; 
/*  93 */     CommandContext<C> newValue = Objects.<CommandContext<C>>requireNonNull(value, "commandContext");
/*  94 */     return new CommandPreprocessingContextImpl(this, newValue, this.commandInput);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final CommandPreprocessingContextImpl<C> withCommandInput(CommandInput value) {
/* 104 */     if (this.commandInput == value) return this; 
/* 105 */     CommandInput newValue = Objects.<CommandInput>requireNonNull(value, "commandInput");
/* 106 */     return new CommandPreprocessingContextImpl(this, this.commandContext, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/* 115 */     if (this == another) return true; 
/* 116 */     return (another instanceof CommandPreprocessingContextImpl && 
/* 117 */       equalTo(0, (CommandPreprocessingContextImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, CommandPreprocessingContextImpl<?> another) {
/* 121 */     return (this.commandContext.equals(another.commandContext) && this.commandInput
/* 122 */       .equals(another.commandInput));
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
/* 133 */     h += (h << 5) + this.commandInput.hashCode();
/* 134 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 143 */     return "CommandPreprocessingContext{commandContext=" + this.commandContext + ", commandInput=" + this.commandInput + "}";
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
/*     */   public static <C> CommandPreprocessingContextImpl<C> of(CommandContext<C> commandContext, CommandInput commandInput) {
/* 157 */     return new CommandPreprocessingContextImpl<>(commandContext, commandInput);
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
/*     */   public static <C> CommandPreprocessingContextImpl<C> copyOf(CommandPreprocessingContext<C> instance) {
/* 169 */     if (instance instanceof CommandPreprocessingContextImpl) {
/* 170 */       return (CommandPreprocessingContextImpl<C>)instance;
/*     */     }
/* 172 */     return of(instance.commandContext(), instance.commandInput());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\execution\preprocessor\CommandPreprocessingContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */