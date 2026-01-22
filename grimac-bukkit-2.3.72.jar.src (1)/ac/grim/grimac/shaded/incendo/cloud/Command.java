/*      */ package ac.grim.grimac.shaded.incendo.cloud;
/*      */ 
/*      */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.component.DefaultValue;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.component.TypedCommandComponent;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.description.CommandDescription;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.description.Description;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.execution.CommandExecutionHandler;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKey;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.meta.CommandMeta;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.parser.aggregate.AggregateParser;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.parser.aggregate.AggregateParserPairBuilder;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.parser.aggregate.AggregateParserTripletBuilder;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.parser.flag.CommandFlag;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.parser.flag.CommandFlagParser;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.LiteralParser;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.permission.Permission;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.permission.PredicatePermission;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.type.tuple.Pair;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.type.tuple.Triplet;
/*      */ import java.lang.reflect.Type;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ import java.util.Objects;
/*      */ import java.util.Optional;
/*      */ import org.apiguardian.api.API;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ @API(status = API.Status.STABLE)
/*      */ public class Command<C>
/*      */ {
/*      */   private final List<CommandComponent<C>> components;
/*      */   private final CommandComponent<C> flagComponent;
/*      */   private final CommandExecutionHandler<C> commandExecutionHandler;
/*      */   private final Type senderType;
/*      */   private final Permission permission;
/*      */   private final CommandMeta commandMeta;
/*      */   private final CommandDescription commandDescription;
/*      */   
/*      */   @API(status = API.Status.INTERNAL)
/*      */   public Command(List<CommandComponent<C>> commandComponents, CommandExecutionHandler<C> commandExecutionHandler, Type senderType, Permission permission, CommandMeta commandMeta, CommandDescription commandDescription) {
/*  114 */     this.components = Objects.<List<CommandComponent<C>>>requireNonNull(commandComponents, "Command components may not be null");
/*  115 */     if (this.components.isEmpty()) {
/*  116 */       throw new IllegalArgumentException("At least one command component is required");
/*      */     }
/*      */     
/*  119 */     this
/*      */ 
/*      */ 
/*      */       
/*  123 */       .flagComponent = this.components.stream().filter(ca -> (ca.type() == CommandComponent.ComponentType.FLAG)).findFirst().orElse(null);
/*      */ 
/*      */     
/*  126 */     boolean foundOptional = false;
/*  127 */     for (CommandComponent<C> component : this.components) {
/*  128 */       if (component.name().isEmpty()) {
/*  129 */         throw new IllegalArgumentException("Component names may not be empty");
/*      */       }
/*  131 */       if (foundOptional && component.required())
/*  132 */         throw new IllegalArgumentException(
/*  133 */             String.format("Command component '%s' cannot be placed after an optional argument", new Object[] {
/*      */                 
/*  135 */                 component.name()
/*      */               })); 
/*  137 */       if (!component.required()) {
/*  138 */         foundOptional = true;
/*      */       }
/*      */     } 
/*  141 */     this.commandExecutionHandler = commandExecutionHandler;
/*  142 */     this.senderType = senderType;
/*  143 */     this.permission = permission;
/*  144 */     this.commandMeta = commandMeta;
/*  145 */     this.commandDescription = commandDescription;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @API(status = API.Status.STABLE)
/*      */   public static <C> Builder<C> newBuilder(String commandName, CommandMeta commandMeta, Description description, String... aliases) {
/*  167 */     List<CommandComponent<C>> commands = new ArrayList<>();
/*  168 */     ParserDescriptor<C, String> staticParser = LiteralParser.literal(commandName, aliases);
/*  169 */     commands.add(
/*  170 */         CommandComponent.builder(commandName, staticParser)
/*  171 */         .description(description)
/*  172 */         .build());
/*      */     
/*  174 */     return new Builder<>(null, commandMeta, null, commands, 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  179 */         CommandExecutionHandler.noOpCommandExecutionHandler(), 
/*  180 */         Permission.empty(), 
/*  181 */         Collections.emptyList(), 
/*  182 */         CommandDescription.empty());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C> Builder<C> newBuilder(String commandName, CommandMeta commandMeta, String... aliases) {
/*  202 */     List<CommandComponent<C>> commands = new ArrayList<>();
/*  203 */     ParserDescriptor<C, String> staticParser = LiteralParser.literal(commandName, aliases);
/*  204 */     commands.add(
/*  205 */         CommandComponent.builder()
/*  206 */         .name(commandName)
/*  207 */         .parser(staticParser)
/*  208 */         .build());
/*      */     
/*  210 */     return new Builder<>(null, commandMeta, null, commands, 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  215 */         CommandExecutionHandler.noOpCommandExecutionHandler(), 
/*  216 */         Permission.empty(), 
/*  217 */         Collections.emptyList(), 
/*  218 */         CommandDescription.empty());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @API(status = API.Status.STABLE)
/*      */   public List<CommandComponent<C>> components() {
/*  229 */     return new ArrayList<>(this.components);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @API(status = API.Status.STABLE)
/*      */   public CommandComponent<C> rootComponent() {
/*  239 */     return this.components.get(0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @API(status = API.Status.EXPERIMENTAL)
/*      */   public List<CommandComponent<C>> nonFlagArguments() {
/*  249 */     List<CommandComponent<C>> components = new ArrayList<>(this.components);
/*  250 */     if (flagComponent() != null) {
/*  251 */       components.remove(flagComponent());
/*      */     }
/*  253 */     return components;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @API(status = API.Status.STABLE)
/*      */   public CommandComponent<C> flagComponent() {
/*  263 */     return this.flagComponent;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @API(status = API.Status.STABLE)
/*      */   public CommandFlagParser<C> flagParser() {
/*  274 */     CommandComponent<C> flagComponent = flagComponent();
/*  275 */     if (flagComponent == null) {
/*  276 */       return null;
/*      */     }
/*  278 */     return (CommandFlagParser<C>)flagComponent.parser();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @API(status = API.Status.STABLE)
/*      */   public CommandExecutionHandler<C> commandExecutionHandler() {
/*  292 */     return this.commandExecutionHandler;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @API(status = API.Status.STABLE)
/*      */   public Optional<TypeToken<? extends C>> senderType() {
/*  307 */     if (this.senderType == null) {
/*  308 */       return Optional.empty();
/*      */     }
/*  310 */     return Optional.of(TypeToken.get(this.senderType));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @API(status = API.Status.STABLE)
/*      */   public Permission commandPermission() {
/*  323 */     return this.permission;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @API(status = API.Status.STABLE)
/*      */   public CommandMeta commandMeta() {
/*  335 */     return this.commandMeta;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @API(status = API.Status.STABLE)
/*      */   public CommandDescription commandDescription() {
/*  348 */     return this.commandDescription;
/*      */   }
/*      */ 
/*      */   
/*      */   public final String toString() {
/*  353 */     StringBuilder stringBuilder = new StringBuilder();
/*  354 */     for (CommandComponent<C> component : components()) {
/*  355 */       stringBuilder.append(component.name()).append(' ');
/*      */     }
/*  357 */     String build = stringBuilder.toString();
/*  358 */     return build.substring(0, build.length() - 1);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @API(status = API.Status.STABLE)
/*      */   public static final class Builder<C>
/*      */   {
/*      */     private final CommandMeta commandMeta;
/*      */ 
/*      */     
/*      */     private final List<CommandComponent<C>> commandComponents;
/*      */ 
/*      */     
/*      */     private final CommandExecutionHandler<C> commandExecutionHandler;
/*      */ 
/*      */     
/*      */     private final Type senderType;
/*      */ 
/*      */     
/*      */     private final Permission permission;
/*      */ 
/*      */     
/*      */     private final CommandManager<C> commandManager;
/*      */ 
/*      */     
/*      */     private final Collection<CommandFlag<?>> flags;
/*      */     
/*      */     private final CommandDescription commandDescription;
/*      */ 
/*      */     
/*      */     private Builder(CommandManager<C> commandManager, CommandMeta commandMeta, Type senderType, List<CommandComponent<C>> commandComponents, CommandExecutionHandler<C> commandExecutionHandler, Permission permission, Collection<CommandFlag<?>> flags, CommandDescription commandDescription) {
/*  390 */       this.commandManager = commandManager;
/*  391 */       this.senderType = senderType;
/*  392 */       this.commandComponents = Objects.<List<CommandComponent<C>>>requireNonNull(commandComponents, "Components may not be null");
/*  393 */       this.commandExecutionHandler = Objects.<CommandExecutionHandler<C>>requireNonNull(commandExecutionHandler, "Execution handler may not be null");
/*  394 */       this.permission = Objects.<Permission>requireNonNull(permission, "Permission may not be null");
/*  395 */       this.commandMeta = Objects.<CommandMeta>requireNonNull(commandMeta, "Meta may not be null");
/*  396 */       this.flags = Objects.<Collection<CommandFlag<?>>>requireNonNull(flags, "Flags may not be null");
/*  397 */       this.commandDescription = Objects.<CommandDescription>requireNonNull(commandDescription, "Command description may not be null");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public TypeToken<? extends C> senderType() {
/*  410 */       if (this.senderType == null) {
/*  411 */         return null;
/*      */       }
/*  413 */       return TypeToken.get(this.senderType);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public Permission commandPermission() {
/*  425 */       return this.permission;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public CommandMeta meta() {
/*  435 */       return this.commandMeta;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public Builder<C> apply(Applicable<C> applicable) {
/*  448 */       return applicable.applyToCommandBuilder(this);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <V> Builder<C> meta(CloudKey<V> key, V value) {
/*  461 */       CommandMeta commandMeta = CommandMeta.builder().with(this.commandMeta).with(key, value).build();
/*  462 */       return new Builder(this.commandManager, commandMeta, this.senderType, this.commandComponents, this.commandExecutionHandler, this.permission, this.flags, this.commandDescription);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder<C> manager(CommandManager<C> commandManager) {
/*  486 */       return new Builder(commandManager, this.commandMeta, this.senderType, this.commandComponents, this.commandExecutionHandler, this.permission, this.flags, this.commandDescription);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public Builder<C> commandDescription(CommandDescription commandDescription) {
/*  508 */       return new Builder(this.commandManager, this.commandMeta, this.senderType, this.commandComponents, this.commandExecutionHandler, this.permission, this.flags, commandDescription);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public CommandDescription commandDescription() {
/*  526 */       return this.commandDescription;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder<C> commandDescription(Description commandDescription) {
/*  537 */       return commandDescription(CommandDescription.commandDescription(commandDescription));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder<C> commandDescription(Description commandDescription, Description verboseCommandDescription) {
/*  552 */       return commandDescription(CommandDescription.commandDescription(commandDescription, verboseCommandDescription));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder<C> literal(String main, String... aliases) {
/*  566 */       return required(main, LiteralParser.literal(main, aliases));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public Builder<C> literal(String main, Description description, String... aliases) {
/*  583 */       return required(main, LiteralParser.literal(main, aliases), description);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> required(String name, CommandComponent.Builder<? super C, T> builder) {
/*  599 */       return argument(builder.name(name).required());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> optional(String name, CommandComponent.Builder<? super C, T> builder) {
/*  615 */       return argument(builder.name(name).optional());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> required(CommandComponent.Builder<? super C, T> builder) {
/*  629 */       return argument(builder.required());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> optional(CommandComponent.Builder<? super C, T> builder) {
/*  643 */       return argument(builder.optional());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> required(String name, ParserDescriptor<? super C, T> parser) {
/*  659 */       return argument((CommandComponent<? super C>)CommandComponent.builder(name, parser).build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> required(String name, ParserDescriptor<? super C, T> parser, SuggestionProvider<? super C> suggestions) {
/*  677 */       return argument(
/*  678 */           (CommandComponent<? super C>)CommandComponent.builder(name, parser)
/*  679 */           .suggestionProvider(suggestions)
/*  680 */           .build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> required(CloudKey<T> name, ParserDescriptor<? super C, T> parser) {
/*  697 */       return argument((CommandComponent<? super C>)CommandComponent.builder(name, parser).build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> required(CloudKey<T> name, ParserDescriptor<? super C, T> parser, SuggestionProvider<? super C> suggestions) {
/*  715 */       return argument(
/*  716 */           (CommandComponent<? super C>)CommandComponent.builder(name, parser)
/*  717 */           .suggestionProvider(suggestions)
/*  718 */           .build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> required(CloudKey<T> name, ParserDescriptor<? super C, T> parser, Description description) {
/*  737 */       return argument((CommandComponent<? super C>)CommandComponent.builder(name, parser).description(description).build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> required(CloudKey<T> name, ParserDescriptor<? super C, T> parser, Description description, SuggestionProvider<? super C> suggestions) {
/*  757 */       return argument(
/*  758 */           (CommandComponent<? super C>)CommandComponent.builder(name, parser)
/*  759 */           .description(description)
/*  760 */           .suggestionProvider(suggestions)
/*  761 */           .build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> required(String name, ParserDescriptor<? super C, T> parser, Description description) {
/*  780 */       return argument((CommandComponent<? super C>)CommandComponent.builder(name, parser).description(description).build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> required(String name, ParserDescriptor<? super C, T> parser, Description description, SuggestionProvider<? super C> suggestions) {
/*  800 */       return argument(
/*  801 */           (CommandComponent<? super C>)CommandComponent.builder(name, parser)
/*  802 */           .description(description)
/*  803 */           .suggestionProvider(suggestions)
/*  804 */           .build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> optional(String name, ParserDescriptor<? super C, T> parser) {
/*  822 */       return argument((CommandComponent<? super C>)CommandComponent.builder(name, parser).optional().build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> optional(String name, ParserDescriptor<? super C, T> parser, SuggestionProvider<? super C> suggestions) {
/*  840 */       return argument(
/*  841 */           (CommandComponent<? super C>)CommandComponent.builder(name, parser)
/*  842 */           .optional()
/*  843 */           .suggestionProvider(suggestions)
/*  844 */           .build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> optional(CloudKey<T> name, ParserDescriptor<? super C, T> parser) {
/*  861 */       return argument((CommandComponent<? super C>)CommandComponent.builder(name, parser).optional().build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> optional(CloudKey<T> name, ParserDescriptor<? super C, T> parser, SuggestionProvider<? super C> suggestions) {
/*  879 */       return argument(
/*  880 */           (CommandComponent<? super C>)CommandComponent.builder(name, parser)
/*  881 */           .optional()
/*  882 */           .suggestionProvider(suggestions)
/*  883 */           .build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> optional(String name, ParserDescriptor<? super C, T> parser, Description description) {
/*  902 */       return argument(
/*  903 */           (CommandComponent<? super C>)CommandComponent.builder(name, parser)
/*  904 */           .description(description)
/*  905 */           .optional()
/*  906 */           .build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> optional(String name, ParserDescriptor<? super C, T> parser, Description description, SuggestionProvider<? super C> suggestions) {
/*  927 */       return argument(
/*  928 */           (CommandComponent<? super C>)CommandComponent.builder(name, parser)
/*  929 */           .description(description)
/*  930 */           .optional()
/*  931 */           .suggestionProvider(suggestions)
/*  932 */           .build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> optional(CloudKey<T> name, ParserDescriptor<? super C, T> parser, Description description) {
/*  951 */       return argument(
/*  952 */           (CommandComponent<? super C>)CommandComponent.builder(name, parser)
/*  953 */           .description(description)
/*  954 */           .optional()
/*  955 */           .build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> optional(CloudKey<T> name, ParserDescriptor<? super C, T> parser, Description description, SuggestionProvider<? super C> suggestions) {
/*  976 */       return argument(
/*  977 */           (CommandComponent<? super C>)CommandComponent.builder(name, parser)
/*  978 */           .description(description)
/*  979 */           .optional()
/*  980 */           .suggestionProvider(suggestions)
/*  981 */           .build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> optional(String name, ParserDescriptor<? super C, T> parser, DefaultValue<? super C, T> defaultValue) {
/* 1000 */       return argument(
/* 1001 */           (CommandComponent<? super C>)CommandComponent.builder(name, parser)
/* 1002 */           .optional(defaultValue)
/* 1003 */           .build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> optional(String name, ParserDescriptor<? super C, T> parser, DefaultValue<? super C, T> defaultValue, SuggestionProvider<? super C> suggestions) {
/* 1024 */       return argument(
/* 1025 */           (CommandComponent<? super C>)CommandComponent.builder(name, parser)
/* 1026 */           .optional(defaultValue)
/* 1027 */           .suggestionProvider(suggestions)
/* 1028 */           .build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> optional(CloudKey<T> name, ParserDescriptor<? super C, T> parser, DefaultValue<? super C, T> defaultValue) {
/* 1047 */       return argument(
/* 1048 */           (CommandComponent<? super C>)CommandComponent.builder(name, parser)
/* 1049 */           .optional(defaultValue)
/* 1050 */           .build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> optional(CloudKey<T> name, ParserDescriptor<? super C, T> parser, DefaultValue<? super C, T> defaultValue, SuggestionProvider<? super C> suggestions) {
/* 1071 */       return argument(
/* 1072 */           (CommandComponent<? super C>)CommandComponent.builder(name, parser)
/* 1073 */           .optional(defaultValue)
/* 1074 */           .suggestionProvider(suggestions)
/* 1075 */           .build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> optional(String name, ParserDescriptor<? super C, T> parser, DefaultValue<? super C, T> defaultValue, Description description) {
/* 1096 */       return argument(
/* 1097 */           (CommandComponent<? super C>)CommandComponent.builder(name, parser)
/* 1098 */           .optional(defaultValue)
/* 1099 */           .description(description)
/* 1100 */           .build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> optional(String name, ParserDescriptor<? super C, T> parser, DefaultValue<? super C, T> defaultValue, Description description, SuggestionProvider<? super C> suggestions) {
/* 1123 */       return argument(
/* 1124 */           (CommandComponent<? super C>)CommandComponent.builder(name, parser)
/* 1125 */           .optional(defaultValue)
/* 1126 */           .description(description)
/* 1127 */           .suggestionProvider(suggestions)
/* 1128 */           .build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> optional(CloudKey<T> name, ParserDescriptor<? super C, T> parser, DefaultValue<? super C, T> defaultValue, Description description) {
/* 1149 */       return argument(
/* 1150 */           (CommandComponent<? super C>)CommandComponent.builder(name, parser)
/* 1151 */           .optional(defaultValue)
/* 1152 */           .description(description)
/* 1153 */           .build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> optional(CloudKey<T> name, ParserDescriptor<? super C, T> parser, DefaultValue<? super C, T> defaultValue, Description description, SuggestionProvider<? super C> suggestions) {
/* 1176 */       return argument(
/* 1177 */           (CommandComponent<? super C>)CommandComponent.builder(name, parser)
/* 1178 */           .optional(defaultValue)
/* 1179 */           .description(description)
/* 1180 */           .suggestionProvider(suggestions)
/* 1181 */           .build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public Builder<C> argument(CommandComponent<? super C> argument) {
/* 1196 */       List<CommandComponent<C>> commandComponents = new ArrayList<>(this.commandComponents);
/* 1197 */       commandComponents.add(argument);
/* 1198 */       return new Builder(this.commandManager, this.commandMeta, this.senderType, commandComponents, this.commandExecutionHandler, this.permission, this.flags, this.commandDescription);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <T> Builder<C> argument(CommandComponent.Builder<? super C, T> builder) {
/* 1222 */       if (this.commandManager != null) {
/* 1223 */         return argument((CommandComponent<? super C>)builder.commandManager(this.commandManager).build());
/*      */       }
/* 1225 */       return argument((CommandComponent<? super C>)builder.build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <U, V> Builder<C> requiredArgumentPair(String name, String firstName, ParserDescriptor<C, U> firstParser, String secondName, ParserDescriptor<C, V> secondParser, Description description) {
/* 1253 */       if (this.commandManager == null) {
/* 1254 */         throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
/*      */       }
/* 1256 */       return required(name, 
/*      */           
/* 1258 */           (ParserDescriptor<? super C, ?>)AggregateParser.pairBuilder(firstName, firstParser, secondName, secondParser).build(), description);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <U, V> Builder<C> requiredArgumentPair(CloudKey<Pair<U, V>> name, String firstName, ParserDescriptor<C, U> firstParser, String secondName, ParserDescriptor<C, V> secondParser, Description description) {
/* 1285 */       if (this.commandManager == null) {
/* 1286 */         throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
/*      */       }
/* 1288 */       return required(name, 
/*      */           
/* 1290 */           (ParserDescriptor<? super C, Pair<U, V>>)AggregateParser.pairBuilder(firstName, firstParser, secondName, secondParser).build(), description);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <U, V> Builder<C> optionalArgumentPair(String name, String firstName, ParserDescriptor<C, U> firstParser, String secondName, ParserDescriptor<C, V> secondParser, Description description) {
/* 1317 */       if (this.commandManager == null) {
/* 1318 */         throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
/*      */       }
/* 1320 */       return optional(name, 
/*      */           
/* 1322 */           (ParserDescriptor<? super C, ?>)AggregateParser.pairBuilder(firstName, firstParser, secondName, secondParser).build(), description);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <U, V> Builder<C> optionalArgumentPair(CloudKey<Pair<U, V>> name, String firstName, ParserDescriptor<C, U> firstParser, String secondName, ParserDescriptor<C, V> secondParser, Description description) {
/* 1349 */       if (this.commandManager == null) {
/* 1350 */         throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
/*      */       }
/* 1352 */       return optional(name, 
/*      */           
/* 1354 */           (ParserDescriptor<? super C, Pair<U, V>>)AggregateParser.pairBuilder(firstName, firstParser, secondName, secondParser).build(), description);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <U, V, O> Builder<C> requiredArgumentPair(String name, TypeToken<O> outputType, String firstName, ParserDescriptor<C, U> firstParser, String secondName, ParserDescriptor<C, V> secondParser, AggregateParserPairBuilder.Mapper<C, U, V, O> mapper, Description description) {
/* 1386 */       if (this.commandManager == null) {
/* 1387 */         throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
/*      */       }
/* 1389 */       return required(name, 
/*      */           
/* 1391 */           (ParserDescriptor<? super C, ?>)AggregateParser.pairBuilder(firstName, firstParser, secondName, secondParser)
/* 1392 */           .withMapper(outputType, mapper)
/* 1393 */           .build(), description);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <U, V, O> Builder<C> requiredArgumentPair(CloudKey<O> name, TypeToken<O> outputType, String firstName, ParserDescriptor<C, U> firstParser, String secondName, ParserDescriptor<C, V> secondParser, AggregateParserPairBuilder.Mapper<C, U, V, O> mapper, Description description) {
/* 1425 */       if (this.commandManager == null) {
/* 1426 */         throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
/*      */       }
/* 1428 */       return required(name, 
/*      */           
/* 1430 */           (ParserDescriptor<? super C, O>)AggregateParser.pairBuilder(firstName, firstParser, secondName, secondParser)
/* 1431 */           .withMapper(outputType, mapper)
/* 1432 */           .build(), description);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <U, V, O> Builder<C> optionalArgumentPair(String name, TypeToken<O> outputType, String firstName, ParserDescriptor<C, U> firstParser, String secondName, ParserDescriptor<C, V> secondParser, AggregateParserPairBuilder.Mapper<C, U, V, O> mapper, Description description) {
/* 1464 */       if (this.commandManager == null) {
/* 1465 */         throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
/*      */       }
/* 1467 */       return optional(name, 
/*      */           
/* 1469 */           (ParserDescriptor<? super C, ?>)AggregateParser.pairBuilder(firstName, firstParser, secondName, secondParser)
/* 1470 */           .withMapper(outputType, mapper)
/* 1471 */           .build(), description);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <U, V, O> Builder<C> optionalArgumentPair(CloudKey<O> name, TypeToken<O> outputType, String firstName, ParserDescriptor<C, U> firstParser, String secondName, ParserDescriptor<C, V> secondParser, AggregateParserPairBuilder.Mapper<C, U, V, O> mapper, Description description) {
/* 1503 */       if (this.commandManager == null) {
/* 1504 */         throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
/*      */       }
/* 1506 */       return optional(name, 
/*      */           
/* 1508 */           (ParserDescriptor<? super C, O>)AggregateParser.pairBuilder(firstName, firstParser, secondName, secondParser)
/* 1509 */           .withMapper(outputType, mapper)
/* 1510 */           .build(), description);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <U, V, W> Builder<C> requiredArgumentTriplet(String name, String firstName, ParserDescriptor<C, U> firstParser, String secondName, ParserDescriptor<C, V> secondParser, String thirdName, ParserDescriptor<C, W> thirdParser, Description description) {
/* 1542 */       if (this.commandManager == null) {
/* 1543 */         throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
/*      */       }
/* 1545 */       return required(name, 
/*      */           
/* 1547 */           (ParserDescriptor<? super C, ?>)AggregateParser.tripletBuilder(firstName, firstParser, secondName, secondParser, thirdName, thirdParser)
/* 1548 */           .build(), description);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <U, V, W> Builder<C> requiredArgumentTriplet(CloudKey<Triplet<U, V, W>> name, String firstName, ParserDescriptor<C, U> firstParser, String secondName, ParserDescriptor<C, V> secondParser, String thirdName, ParserDescriptor<C, W> thirdParser, Description description) {
/* 1584 */       if (this.commandManager == null) {
/* 1585 */         throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
/*      */       }
/* 1587 */       return required(name, 
/*      */           
/* 1589 */           (ParserDescriptor<? super C, Triplet<U, V, W>>)AggregateParser.tripletBuilder(firstName, firstParser, secondName, secondParser, thirdName, thirdParser)
/* 1590 */           .build(), description);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <U, V, W> Builder<C> optionalArgumentTriplet(String name, String firstName, ParserDescriptor<C, U> firstParser, String secondName, ParserDescriptor<C, V> secondParser, String thirdName, ParserDescriptor<C, W> thirdParser, Description description) {
/* 1626 */       if (this.commandManager == null) {
/* 1627 */         throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
/*      */       }
/* 1629 */       return optional(name, 
/*      */ 
/*      */           
/* 1632 */           (ParserDescriptor<? super C, ?>)AggregateParser.tripletBuilder(firstName, firstParser, secondName, secondParser, thirdName, thirdParser)
/* 1633 */           .build(), description);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <U, V, W> Builder<C> optionalArgumentTriplet(CloudKey<Triplet<U, V, W>> name, String firstName, ParserDescriptor<C, U> firstParser, String secondName, ParserDescriptor<C, V> secondParser, String thirdName, ParserDescriptor<C, W> thirdParser, Description description) {
/* 1669 */       if (this.commandManager == null) {
/* 1670 */         throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
/*      */       }
/* 1672 */       return optional(name, 
/*      */ 
/*      */           
/* 1675 */           (ParserDescriptor<? super C, Triplet<U, V, W>>)AggregateParser.tripletBuilder(firstName, firstParser, secondName, secondParser, thirdName, thirdParser)
/* 1676 */           .build(), description);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <U, V, W, O> Builder<C> requiredArgumentTriplet(String name, TypeToken<O> outputType, String firstName, ParserDescriptor<C, U> firstParser, String secondName, ParserDescriptor<C, V> secondParser, String thirdName, ParserDescriptor<C, W> thirdParser, AggregateParserTripletBuilder.Mapper<C, U, V, W, O> mapper, Description description) {
/* 1717 */       if (this.commandManager == null) {
/* 1718 */         throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
/*      */       }
/* 1720 */       return required(name, 
/*      */           
/* 1722 */           (ParserDescriptor<? super C, ?>)AggregateParser.tripletBuilder(firstName, firstParser, secondName, secondParser, thirdName, thirdParser)
/* 1723 */           .withMapper(outputType, mapper)
/* 1724 */           .build(), description);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <U, V, W, O> Builder<C> requiredArgumentTriplet(CloudKey<O> name, TypeToken<O> outputType, String firstName, ParserDescriptor<C, U> firstParser, String secondName, ParserDescriptor<C, V> secondParser, String thirdName, ParserDescriptor<C, W> thirdParser, AggregateParserTripletBuilder.Mapper<C, U, V, W, O> mapper, Description description) {
/* 1765 */       if (this.commandManager == null) {
/* 1766 */         throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
/*      */       }
/* 1768 */       return required(name, 
/*      */           
/* 1770 */           (ParserDescriptor<? super C, O>)AggregateParser.tripletBuilder(firstName, firstParser, secondName, secondParser, thirdName, thirdParser)
/* 1771 */           .withMapper(outputType, mapper)
/* 1772 */           .build(), description);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <U, V, W, O> Builder<C> optionalArgumentTriplet(String name, TypeToken<O> outputType, String firstName, ParserDescriptor<C, U> firstParser, String secondName, ParserDescriptor<C, V> secondParser, String thirdName, ParserDescriptor<C, W> thirdParser, AggregateParserTripletBuilder.Mapper<C, U, V, W, O> mapper, Description description) {
/* 1813 */       if (this.commandManager == null) {
/* 1814 */         throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
/*      */       }
/* 1816 */       return optional(name, 
/*      */           
/* 1818 */           (ParserDescriptor<? super C, ?>)AggregateParser.tripletBuilder(firstName, firstParser, secondName, secondParser, thirdName, thirdParser)
/* 1819 */           .withMapper(outputType, mapper)
/* 1820 */           .build(), description);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public <U, V, W, O> Builder<C> optionalArgumentTriplet(CloudKey<O> name, TypeToken<O> outputType, String firstName, ParserDescriptor<C, U> firstParser, String secondName, ParserDescriptor<C, V> secondParser, String thirdName, ParserDescriptor<C, W> thirdParser, AggregateParserTripletBuilder.Mapper<C, U, V, W, O> mapper, Description description) {
/* 1861 */       if (this.commandManager == null) {
/* 1862 */         throw new IllegalStateException("This cannot be called from a command that has no command manager attached");
/*      */       }
/* 1864 */       return optional(name, 
/*      */           
/* 1866 */           (ParserDescriptor<? super C, O>)AggregateParser.tripletBuilder(firstName, firstParser, secondName, secondParser, thirdName, thirdParser)
/* 1867 */           .withMapper(outputType, mapper)
/* 1868 */           .build(), description);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder<C> handler(CommandExecutionHandler<C> commandExecutionHandler) {
/* 1882 */       return new Builder(this.commandManager, this.commandMeta, this.senderType, this.commandComponents, commandExecutionHandler, this.permission, this.flags, this.commandDescription);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder<C> futureHandler(CommandExecutionHandler.FutureCommandExecutionHandler<C> commandExecutionHandler) {
/* 1903 */       return handler((CommandExecutionHandler<C>)commandExecutionHandler);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public CommandExecutionHandler<C> handler() {
/* 1913 */       return this.commandExecutionHandler;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public Builder<C> prependHandler(CommandExecutionHandler<C> handler) {
/* 1925 */       return handler(CommandExecutionHandler.delegatingExecutionHandler(Arrays.asList(new CommandExecutionHandler[] { handler, handler() })));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     public Builder<C> appendHandler(CommandExecutionHandler<C> handler) {
/* 1937 */       return handler(CommandExecutionHandler.delegatingExecutionHandler(Arrays.asList(new CommandExecutionHandler[] { handler(), handler })));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <N extends C> Builder<N> senderType(Class<? extends N> senderType) {
/* 1948 */       return senderType(TypeToken.get(senderType));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <N extends C> Builder<N> senderType(TypeToken<? extends N> senderType) {
/* 1960 */       return new Builder(this.commandManager, this.commandMeta, senderType
/*      */ 
/*      */           
/* 1963 */           .getType(), this.commandComponents, this.commandExecutionHandler, this.permission, this.flags, this.commandDescription);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder<C> permission(Permission permission) {
/* 1979 */       return new Builder(this.commandManager, this.commandMeta, this.senderType, this.commandComponents, this.commandExecutionHandler, permission, this.flags, this.commandDescription);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder<C> permission(PredicatePermission<C> permission) {
/* 1998 */       return new Builder(this.commandManager, this.commandMeta, this.senderType, this.commandComponents, this.commandExecutionHandler, (Permission)permission, this.flags, this.commandDescription);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder<C> permission(String permission) {
/* 2017 */       return new Builder(this.commandManager, this.commandMeta, this.senderType, this.commandComponents, this.commandExecutionHandler, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2023 */           Permission.of(permission), this.flags, this.commandDescription);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <N extends C> Builder<N> proxies(Command<N> command) {
/*      */       Builder<N> builder;
/* 2045 */       if (command.senderType().isPresent()) {
/* 2046 */         builder = senderType(command.senderType().get());
/*      */       } else {
/* 2048 */         builder = this;
/*      */       } 
/* 2050 */       for (CommandComponent<N> component : command.components()) {
/* 2051 */         if (component.type() == CommandComponent.ComponentType.LITERAL) {
/*      */           continue;
/*      */         }
/* 2054 */         builder = builder.argument(component);
/*      */       } 
/* 2056 */       if (this.permission.permissionString().isEmpty()) {
/* 2057 */         builder = builder.permission(command.commandPermission());
/*      */       }
/* 2059 */       return builder.handler(command.commandExecutionHandler);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <T> Builder<C> flag(CommandFlag<T> flag) {
/* 2070 */       List<CommandFlag<?>> flags = new ArrayList<>(this.flags);
/* 2071 */       flags.add(flag);
/* 2072 */       return new Builder(this.commandManager, this.commandMeta, this.senderType, this.commandComponents, this.commandExecutionHandler, this.permission, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2079 */           Collections.unmodifiableList(flags), this.commandDescription);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <T> Builder<C> flag(CommandFlag.Builder<C, T> builder) {
/* 2092 */       return flag(builder.build());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Command<C> build() {
/* 2101 */       List<CommandComponent<C>> commandComponents = new ArrayList<>(this.commandComponents);
/*      */       
/* 2103 */       if (!this.flags.isEmpty()) {
/* 2104 */         CommandFlagParser<C> flagParser = new CommandFlagParser(this.flags);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2111 */         TypedCommandComponent typedCommandComponent = CommandComponent.builder().name("flags").parser((ArgumentParser)flagParser).valueType(Object.class).description(Description.of("Command flags")).build();
/* 2112 */         commandComponents.add(typedCommandComponent);
/*      */       } 
/* 2114 */       return new Command<>(
/* 2115 */           Collections.unmodifiableList(commandComponents), this.commandExecutionHandler, this.senderType, this.permission, this.commandMeta, this.commandDescription);
/*      */     }
/*      */     
/*      */     @API(status = API.Status.STABLE)
/*      */     @FunctionalInterface
/*      */     public static interface Applicable<C> {
/*      */       @API(status = API.Status.STABLE)
/*      */       Command.Builder<C> applyToCommandBuilder(Command.Builder<C> param2Builder);
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\Command.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */