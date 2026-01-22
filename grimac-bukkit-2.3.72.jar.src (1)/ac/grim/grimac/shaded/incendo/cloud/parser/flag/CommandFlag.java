/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser.flag;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.TypedCommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.description.Description;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.permission.Permission;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
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
/*     */ public final class CommandFlag<T>
/*     */ {
/*     */   private final String name;
/*     */   private final String[] aliases;
/*     */   private final Description description;
/*     */   private final Permission permission;
/*     */   private final FlagMode mode;
/*     */   private final TypedCommandComponent<?, T> commandComponent;
/*     */   
/*     */   private CommandFlag(String name, String[] aliases, Description description, Permission permission, TypedCommandComponent<?, T> commandComponent, FlagMode mode) {
/*  67 */     this.name = Objects.<String>requireNonNull(name, "name cannot be null");
/*  68 */     this.aliases = Objects.<String[]>requireNonNull(aliases, "aliases cannot be null");
/*  69 */     this.description = Objects.<Description>requireNonNull(description, "description cannot be null");
/*  70 */     this.permission = Objects.<Permission>requireNonNull(permission, "permission cannot be null");
/*  71 */     this.commandComponent = commandComponent;
/*  72 */     this.mode = Objects.<FlagMode>requireNonNull(mode, "mode cannot be null");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> Builder<C, Void> builder(String name) {
/*  84 */     return new Builder<>(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String name() {
/*  93 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<String> aliases() {
/* 102 */     return Arrays.asList(this.aliases);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public FlagMode mode() {
/* 112 */     return this.mode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public Description description() {
/* 122 */     return this.description;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public CommandComponent<?> commandComponent() {
/* 132 */     return (CommandComponent<?>)this.commandComponent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public Permission permission() {
/* 142 */     return this.permission;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 147 */     return String.format("--%s", new Object[] { this.name });
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 152 */     if (this == o) {
/* 153 */       return true;
/*     */     }
/* 155 */     if (o == null || getClass() != o.getClass()) {
/* 156 */       return false;
/*     */     }
/* 158 */     CommandFlag<?> that = (CommandFlag)o;
/* 159 */     return name().equals(that.name());
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 164 */     return Objects.hash(new Object[] { name() });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static final class Builder<C, T>
/*     */   {
/*     */     private final String name;
/*     */     
/*     */     private final String[] aliases;
/*     */     
/*     */     private final Description description;
/*     */     
/*     */     private final Permission permission;
/*     */     
/*     */     private final TypedCommandComponent<C, T> commandComponent;
/*     */     
/*     */     private final CommandFlag.FlagMode mode;
/*     */ 
/*     */     
/*     */     private Builder(String name, String[] aliases, Description description, Permission permission, TypedCommandComponent<C, T> commandComponent, CommandFlag.FlagMode mode) {
/* 186 */       this.name = name;
/* 187 */       this.aliases = aliases;
/* 188 */       this.description = description;
/* 189 */       this.permission = permission;
/* 190 */       this.commandComponent = commandComponent;
/* 191 */       this.mode = mode;
/*     */     }
/*     */     
/*     */     private Builder(String name) {
/* 195 */       this(name, new String[0], Description.empty(), Permission.empty(), null, CommandFlag.FlagMode.SINGLE);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<C, T> withAliases(String... aliases) {
/* 206 */       return withAliases(Arrays.asList(aliases));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @API(status = API.Status.STABLE)
/*     */     public Builder<C, T> withAliases(Collection<String> aliases) {
/* 218 */       Set<String> filteredAliases = new HashSet<>();
/* 219 */       for (String alias : aliases) {
/* 220 */         if (alias.isEmpty()) {
/*     */           continue;
/*     */         }
/* 223 */         if (alias.length() > 1) {
/* 224 */           throw new IllegalArgumentException(
/* 225 */               String.format("Alias '%s' has name longer than one character. This is not allowed", new Object[] { alias }));
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 231 */         filteredAliases.add(alias);
/*     */       } 
/* 233 */       return new Builder(this.name, filteredAliases
/*     */           
/* 235 */           .<String>toArray(new String[0]), this.description, this.permission, this.commandComponent, this.mode);
/*     */     }
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
/*     */     @API(status = API.Status.STABLE)
/*     */     public Builder<C, T> withDescription(Description description) {
/* 251 */       return new Builder(this.name, this.aliases, description, this.permission, this.commandComponent, this.mode);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public <N> Builder<C, N> withComponent(TypedCommandComponent<C, N> component) {
/* 262 */       return new Builder(this.name, this.aliases, this.description, this.permission, component, this.mode);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public <N> Builder<C, N> withComponent(ParserDescriptor<? super C, N> parserDescriptor) {
/* 273 */       return withComponent(CommandComponent.builder(this.name, parserDescriptor));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public <N> Builder<C, N> withComponent(CommandComponent.Builder<C, N> builder) {
/* 284 */       return withComponent(builder.build());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @API(status = API.Status.STABLE)
/*     */     public Builder<C, T> withPermission(Permission permission) {
/* 295 */       return new Builder(this.name, this.aliases, this.description, permission, this.commandComponent, this.mode);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @API(status = API.Status.STABLE)
/*     */     public Builder<C, T> withPermission(String permissionString) {
/* 306 */       return withPermission(Permission.of(permissionString));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @API(status = API.Status.STABLE)
/*     */     public Builder<C, T> asRepeatable() {
/* 316 */       return new Builder(this.name, this.aliases, this.description, this.permission, this.commandComponent, CommandFlag.FlagMode.REPEATABLE);
/*     */     }
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
/*     */     public CommandFlag<T> build() {
/* 332 */       return new CommandFlag<>(this.name, this.aliases, this.description, this.permission, this.commandComponent, this.mode);
/*     */     }
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public enum FlagMode
/*     */   {
/* 350 */     SINGLE,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 355 */     REPEATABLE;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\flag\CommandFlag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */