/*     */ package ac.grim.grimac.shaded.incendo.cloud.help.result;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.Command;
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
/*     */ @Generated(from = "CommandEntry", generator = "Immutables")
/*     */ @Immutable
/*     */ final class CommandEntryImpl<C>
/*     */   implements CommandEntry<C>
/*     */ {
/*     */   private final Command<C> command;
/*     */   private final String syntax;
/*     */   
/*     */   private CommandEntryImpl(Command<C> command, String syntax) {
/*  56 */     this.command = Objects.<Command<C>>requireNonNull(command, "command");
/*  57 */     this.syntax = Objects.<String>requireNonNull(syntax, "syntax");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private CommandEntryImpl(CommandEntryImpl<C> original, Command<C> command, String syntax) {
/*  64 */     this.command = command;
/*  65 */     this.syntax = syntax;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Command<C> command() {
/*  73 */     return this.command;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String syntax() {
/*  81 */     return this.syntax;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final CommandEntryImpl<C> withCommand(Command<C> value) {
/*  91 */     if (this.command == value) return this; 
/*  92 */     Command<C> newValue = Objects.<Command<C>>requireNonNull(value, "command");
/*  93 */     return new CommandEntryImpl(this, newValue, this.syntax);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final CommandEntryImpl<C> withSyntax(String value) {
/* 103 */     String newValue = Objects.<String>requireNonNull(value, "syntax");
/* 104 */     if (this.syntax.equals(newValue)) return this; 
/* 105 */     return new CommandEntryImpl(this, this.command, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/* 114 */     if (this == another) return true; 
/* 115 */     return (another instanceof CommandEntryImpl && 
/* 116 */       equalTo(0, (CommandEntryImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, CommandEntryImpl<?> another) {
/* 120 */     return (this.command.equals(another.command) && this.syntax
/* 121 */       .equals(another.syntax));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 130 */     int h = 5381;
/* 131 */     h += (h << 5) + this.command.hashCode();
/* 132 */     h += (h << 5) + this.syntax.hashCode();
/* 133 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 142 */     return "CommandEntry{command=" + this.command + ", syntax=" + this.syntax + "}";
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
/*     */   public static <C> CommandEntryImpl<C> of(Command<C> command, String syntax) {
/* 156 */     return new CommandEntryImpl<>(command, syntax);
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
/*     */   public static <C> CommandEntryImpl<C> copyOf(CommandEntry<C> instance) {
/* 168 */     if (instance instanceof CommandEntryImpl) {
/* 169 */       return (CommandEntryImpl<C>)instance;
/*     */     }
/* 171 */     return of(instance.command(), instance.syntax());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\help\result\CommandEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */