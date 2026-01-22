/*     */ package ac.grim.grimac.shaded.incendo.cloud.execution;
/*     */ 
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
/*     */ @ParametersAreNonnullByDefault
/*     */ @CheckReturnValue
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ @Generated(from = "CommandResult", generator = "Immutables")
/*     */ @Immutable
/*     */ final class CommandResultImpl<C>
/*     */   implements CommandResult<C>
/*     */ {
/*     */   private final CommandContext<C> commandContext;
/*     */   
/*     */   private CommandResultImpl(CommandContext<C> commandContext) {
/*  54 */     this.commandContext = Objects.<CommandContext<C>>requireNonNull(commandContext, "commandContext");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private CommandResultImpl(CommandResultImpl<C> original, CommandContext<C> commandContext) {
/*  60 */     this.commandContext = commandContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandContext<C> commandContext() {
/*  68 */     return this.commandContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final CommandResultImpl<C> withCommandContext(CommandContext<C> value) {
/*  78 */     if (this.commandContext == value) return this; 
/*  79 */     CommandContext<C> newValue = Objects.<CommandContext<C>>requireNonNull(value, "commandContext");
/*  80 */     return new CommandResultImpl(this, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/*  89 */     if (this == another) return true; 
/*  90 */     return (another instanceof CommandResultImpl && 
/*  91 */       equalTo(0, (CommandResultImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, CommandResultImpl<?> another) {
/*  95 */     return this.commandContext.equals(another.commandContext);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 104 */     int h = 5381;
/* 105 */     h += (h << 5) + this.commandContext.hashCode();
/* 106 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 115 */     return "CommandResult{commandContext=" + this.commandContext + "}";
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
/*     */   public static <C> CommandResultImpl<C> of(CommandContext<C> commandContext) {
/* 127 */     return new CommandResultImpl<>(commandContext);
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
/*     */   public static <C> CommandResultImpl<C> copyOf(CommandResult<C> instance) {
/* 139 */     if (instance instanceof CommandResultImpl) {
/* 140 */       return (CommandResultImpl<C>)instance;
/*     */     }
/* 142 */     return of(instance.commandContext());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\execution\CommandResultImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */