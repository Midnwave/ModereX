/*     */ package ac.grim.grimac.shaded.incendo.cloud.description;
/*     */ 
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
/*     */ 
/*     */ @ParametersAreNonnullByDefault
/*     */ @CheckReturnValue
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ @Generated(from = "CommandDescription", generator = "Immutables")
/*     */ @Immutable
/*     */ final class CommandDescriptionImpl
/*     */   implements CommandDescription
/*     */ {
/*     */   private final Description description;
/*     */   private final Description verboseDescription;
/*     */   
/*     */   private CommandDescriptionImpl(Description description, Description verboseDescription) {
/*  56 */     this.description = Objects.<Description>requireNonNull(description, "description");
/*  57 */     this.verboseDescription = Objects.<Description>requireNonNull(verboseDescription, "verboseDescription");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private CommandDescriptionImpl(CommandDescriptionImpl original, Description description, Description verboseDescription) {
/*  64 */     this.description = description;
/*  65 */     this.verboseDescription = verboseDescription;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Description description() {
/*  73 */     return this.description;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Description verboseDescription() {
/*  81 */     return this.verboseDescription;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final CommandDescriptionImpl withDescription(Description value) {
/*  91 */     if (this.description == value) return this; 
/*  92 */     Description newValue = Objects.<Description>requireNonNull(value, "description");
/*  93 */     return new CommandDescriptionImpl(this, newValue, this.verboseDescription);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final CommandDescriptionImpl withVerboseDescription(Description value) {
/* 103 */     if (this.verboseDescription == value) return this; 
/* 104 */     Description newValue = Objects.<Description>requireNonNull(value, "verboseDescription");
/* 105 */     return new CommandDescriptionImpl(this, this.description, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/* 114 */     if (this == another) return true; 
/* 115 */     return (another instanceof CommandDescriptionImpl && 
/* 116 */       equalTo(0, (CommandDescriptionImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, CommandDescriptionImpl another) {
/* 120 */     return (this.description.equals(another.description) && this.verboseDescription
/* 121 */       .equals(another.verboseDescription));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 130 */     int h = 5381;
/* 131 */     h += (h << 5) + this.description.hashCode();
/* 132 */     h += (h << 5) + this.verboseDescription.hashCode();
/* 133 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 142 */     return "CommandDescription{description=" + this.description + ", verboseDescription=" + this.verboseDescription + "}";
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
/*     */   public static CommandDescriptionImpl of(Description description, Description verboseDescription) {
/* 155 */     return new CommandDescriptionImpl(description, verboseDescription);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CommandDescriptionImpl copyOf(CommandDescription instance) {
/* 166 */     if (instance instanceof CommandDescriptionImpl) {
/* 167 */       return (CommandDescriptionImpl)instance;
/*     */     }
/* 169 */     return of(instance.description(), instance.verboseDescription());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\description\CommandDescriptionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */