/*     */ package ac.grim.grimac.shaded.incendo.cloud;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.Caption;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionFormatter;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionProvider;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionRegistry;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionVariable;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.StandardCaptionsProvider;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContextFactory;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.StandardCommandContextFactory;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.handling.ExceptionController;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.execution.CommandExecutor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.execution.ExecutionCoordinator;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.execution.postprocessor.AcceptingCommandPostprocessor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.execution.postprocessor.CommandPostprocessingContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.execution.postprocessor.CommandPostprocessor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.execution.preprocessor.AcceptingCommandPreprocessor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.execution.preprocessor.CommandPreprocessingContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.execution.preprocessor.CommandPreprocessor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.help.CommandPredicate;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.help.HelpHandler;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.help.HelpHandlerFactory;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.injection.ParameterInjectorRegistry;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.internal.CommandNode;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.internal.CommandRegistrationHandler;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.meta.CommandMeta;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserRegistry;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.StandardParserRegistry;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.flag.CommandFlag;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.permission.Permission;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.permission.PermissionResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.permission.PredicatePermission;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.services.ServicePipeline;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.services.State;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.services.type.Service;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.setting.Configurable;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.setting.ManagerSetting;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.setting.Setting;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.state.RegistrationState;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.state.State;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.state.Stateful;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.DelegatingSuggestionFactory;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.FilteringSuggestionProcessor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestion;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionFactory;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionMapper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProcessor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.syntax.CommandSyntaxFormatter;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.syntax.StandardCommandSyntaxFormatter;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.tuple.Pair;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.tuple.Triplet;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.util.annotation.AnnotationAccessor;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.atomic.AtomicReference;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public abstract class CommandManager<C>
/*     */   implements Stateful<RegistrationState>, CommandBuilderSource<C>
/*     */ {
/* 104 */   private final Configurable<ManagerSetting> settings = Configurable.enumConfigurable(ManagerSetting.class);
/* 105 */   private final ServicePipeline servicePipeline = ServicePipeline.builder().build();
/* 106 */   private final ParserRegistry<C> parserRegistry = (ParserRegistry<C>)new StandardParserRegistry();
/* 107 */   private final Collection<Command<C>> commands = new LinkedList<>();
/* 108 */   private final ParameterInjectorRegistry<C> parameterInjectorRegistry = new ParameterInjectorRegistry();
/*     */   private final CommandTree<C> commandTree;
/*     */   private final SuggestionFactory<C, ? extends Suggestion> suggestionFactory;
/* 111 */   private final Set<CloudCapability> capabilities = new HashSet<>();
/* 112 */   private final ExceptionController<C> exceptionController = new ExceptionController();
/*     */   
/*     */   private final CommandExecutor<C> commandExecutor;
/* 115 */   private CaptionFormatter<C, String> captionVariableReplacementHandler = CaptionFormatter.placeholderReplacing();
/* 116 */   private CommandSyntaxFormatter<C> commandSyntaxFormatter = (CommandSyntaxFormatter<C>)new StandardCommandSyntaxFormatter(this);
/* 117 */   private SuggestionProcessor<C> suggestionProcessor = (SuggestionProcessor<C>)new FilteringSuggestionProcessor();
/*     */   private CommandRegistrationHandler<C> commandRegistrationHandler;
/*     */   private CaptionRegistry<C> captionRegistry;
/* 120 */   private HelpHandlerFactory<C> helpHandlerFactory = HelpHandlerFactory.standard(this);
/* 121 */   private SuggestionMapper<? extends Suggestion> mapper = SuggestionMapper.identity();
/* 122 */   private final AtomicReference<RegistrationState> state = new AtomicReference<>(RegistrationState.BEFORE_REGISTRATION);
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
/*     */   protected CommandManager(ExecutionCoordinator<C> executionCoordinator, CommandRegistrationHandler<C> commandRegistrationHandler) {
/* 137 */     StandardCommandContextFactory standardCommandContextFactory = new StandardCommandContextFactory(this);
/* 138 */     this.commandTree = CommandTree.newTree(this);
/* 139 */     this.commandRegistrationHandler = commandRegistrationHandler;
/* 140 */     this.suggestionFactory = (SuggestionFactory<C, ? extends Suggestion>)new DelegatingSuggestionFactory(this, this.commandTree, (CommandContextFactory)standardCommandContextFactory, executionCoordinator, suggestion -> this.mapper.map(suggestion));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 147 */     this.commandExecutor = new StandardCommandExecutor<>(this, executionCoordinator, (CommandContextFactory<C>)standardCommandContextFactory);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 153 */     this.servicePipeline.registerServiceType(new TypeToken<CommandPreprocessor<C>>() {  }, (Service)new AcceptingCommandPreprocessor());
/*     */     
/* 155 */     this.servicePipeline.registerServiceType(new TypeToken<CommandPostprocessor<C>>() {  }, (Service)new AcceptingCommandPostprocessor());
/*     */ 
/*     */     
/* 158 */     this.captionRegistry = CaptionRegistry.captionRegistry();
/* 159 */     this.captionRegistry.registerProvider((CaptionProvider)new StandardCaptionsProvider());
/*     */     
/* 161 */     parameterInjectorRegistry().registerInjector(CommandContext.class, (context, annotationAccessor) -> context);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public CommandExecutor<C> commandExecutor() {
/* 176 */     return this.commandExecutor;
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
/*     */   public SuggestionFactory<C, ? extends Suggestion> suggestionFactory() {
/* 188 */     return this.suggestionFactory;
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
/*     */   public SuggestionMapper<? extends Suggestion> suggestionMapper() {
/* 202 */     return this.mapper;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void appendSuggestionMapper(SuggestionMapper<? extends Suggestion> mapper) {
/* 212 */     suggestionMapper(suggestionMapper().then(mapper));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void suggestionMapper(SuggestionMapper<? extends Suggestion> mapper) {
/* 222 */     this.mapper = Objects.<SuggestionMapper<? extends Suggestion>>requireNonNull(mapper, "mapper");
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
/*     */   public CommandManager<C> command(Command<? extends C> command) {
/* 239 */     if (!transitionIfPossible(RegistrationState.BEFORE_REGISTRATION, RegistrationState.REGISTERING) && 
/* 240 */       !isCommandRegistrationAllowed()) {
/* 241 */       throw new IllegalStateException("Unable to register commands because the manager is no longer in a registration state. Your platform may allow unsafe registrations by enabling the appropriate manager setting.");
/*     */     }
/*     */     
/* 244 */     this.commandTree.insertCommand((Command)command);
/* 245 */     this.commands.add(command);
/* 246 */     return this;
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public CommandManager<C> command(CommandFactory<C> commandFactory) {
/* 264 */     commandFactory.createCommands(this).forEach(this::command);
/* 265 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandManager<C> command(Command.Builder<? extends C> command) {
/* 276 */     return command(command.manager(this).build());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public CaptionFormatter<C, String> captionFormatter() {
/* 287 */     return this.captionVariableReplacementHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public void captionFormatter(CaptionFormatter<C, String> captionFormatter) {
/* 298 */     this.captionVariableReplacementHandler = captionFormatter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public CommandSyntaxFormatter<C> commandSyntaxFormatter() {
/* 309 */     return this.commandSyntaxFormatter;
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public void commandSyntaxFormatter(CommandSyntaxFormatter<C> commandSyntaxFormatter) {
/* 322 */     this.commandSyntaxFormatter = commandSyntaxFormatter;
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
/*     */   public CommandRegistrationHandler<C> commandRegistrationHandler() {
/* 337 */     return this.commandRegistrationHandler;
/*     */   }
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   protected final void commandRegistrationHandler(CommandRegistrationHandler<C> commandRegistrationHandler) {
/* 342 */     requireState((State)RegistrationState.BEFORE_REGISTRATION);
/* 343 */     this.commandRegistrationHandler = commandRegistrationHandler;
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
/*     */   protected final void registerCapability(CloudCapability capability) {
/* 355 */     this.capabilities.add(capability);
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
/*     */   public boolean hasCapability(CloudCapability capability) {
/* 367 */     return this.capabilities.contains(capability);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public Collection<CloudCapability> capabilities() {
/* 378 */     return Collections.unmodifiableSet(new HashSet<>(this.capabilities));
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
/*     */   public PermissionResult testPermission(C sender, Permission permission) {
/* 394 */     if (permission instanceof PredicatePermission)
/* 395 */       return ((PredicatePermission)permission).testPermission(sender); 
/* 396 */     if (permission instanceof ac.grim.grimac.shaded.incendo.cloud.permission.OrPermission) {
/* 397 */       for (Permission innerPermission : permission.permissions()) {
/* 398 */         PermissionResult result = testPermission(sender, innerPermission);
/* 399 */         if (result.allowed()) {
/* 400 */           return result;
/*     */         }
/*     */       } 
/* 403 */       return PermissionResult.denied(permission);
/* 404 */     }  if (permission instanceof ac.grim.grimac.shaded.incendo.cloud.permission.AndPermission) {
/* 405 */       for (Permission innerPermission : permission.permissions()) {
/* 406 */         PermissionResult result = testPermission(sender, innerPermission);
/* 407 */         if (!result.allowed()) {
/* 408 */           return result;
/*     */         }
/*     */       } 
/* 411 */       return PermissionResult.allowed(permission);
/*     */     } 
/* 413 */     return PermissionResult.of((permission.isEmpty() || hasPermission(sender, permission.permissionString())), permission);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public final CaptionRegistry<C> captionRegistry() {
/* 424 */     return this.captionRegistry;
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public final void captionRegistry(CaptionRegistry<C> captionRegistry) {
/* 438 */     this.captionRegistry = captionRegistry;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.EXPERIMENTAL)
/*     */   public void deleteRootCommand(String rootCommand) throws CloudCapability.CloudCapabilityMissingException {
/* 461 */     if (!hasCapability(CloudCapability.StandardCapabilities.ROOT_COMMAND_DELETION)) {
/* 462 */       throw new CloudCapability.CloudCapabilityMissingException(CloudCapability.StandardCapabilities.ROOT_COMMAND_DELETION);
/*     */     }
/*     */ 
/*     */     
/* 466 */     CommandNode<C> node = this.commandTree.getNamedNode(rootCommand);
/* 467 */     if (node == null || node.component() == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 473 */     this.commandRegistrationHandler.unregisterRootCommand(node.component());
/*     */ 
/*     */     
/* 476 */     Objects.requireNonNull(this.commands); this.commandTree.deleteRecursively(node, true, this.commands::remove);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public Collection<String> rootCommands() {
/* 486 */     return (Collection<String>)this.commandTree.rootNodes()
/* 487 */       .stream()
/* 488 */       .map(CommandNode::component)
/* 489 */       .filter(Objects::nonNull)
/* 490 */       .filter(component -> (component.type() == CommandComponent.ComponentType.LITERAL))
/* 491 */       .map(CommandComponent::name)
/* 492 */       .collect(Collectors.toList());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Command.Builder<C> decorateBuilder(Command.Builder<C> builder) {
/* 503 */     return builder.manager(this);
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
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public <T> CommandComponent.Builder<C, T> componentBuilder(Class<T> type, String name) {
/* 523 */     return CommandComponent.ofType(type, name).commandManager(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandFlag.Builder<C, Void> flagBuilder(String name) {
/* 533 */     return CommandFlag.builder(name);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public CommandTree<C> commandTree() {
/* 546 */     return this.commandTree;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandMeta createDefaultCommandMeta() {
/* 556 */     return CommandMeta.empty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerCommandPreProcessor(CommandPreprocessor<C> processor) {
/* 567 */     this.servicePipeline.registerServiceImplementation(new TypeToken<CommandPreprocessor<C>>() {  }, (Service)processor, 
/*     */ 
/*     */ 
/*     */         
/* 571 */         Collections.emptyList());
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
/*     */   public void registerCommandPostProcessor(CommandPostprocessor<C> processor) {
/* 583 */     this.servicePipeline.registerServiceImplementation(new TypeToken<CommandPostprocessor<C>>() {  }, (Service)processor, 
/*     */         
/* 585 */         Collections.emptyList());
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public State preprocessContext(CommandContext<C> context, CommandInput commandInput) {
/* 602 */     this.servicePipeline.pump(CommandPreprocessingContext.of(context, commandInput))
/* 603 */       .through(new TypeToken<CommandPreprocessor<C>>() {
/*     */         
/* 605 */         }).complete();
/* 606 */     return ((String)context.optional("__COMMAND_PRE_PROCESSED__").orElse("")).isEmpty() ? 
/* 607 */       State.REJECTED : 
/* 608 */       State.ACCEPTED;
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
/*     */   public State postprocessContext(CommandContext<C> context, Command<C> command) {
/* 623 */     this.servicePipeline.pump(CommandPostprocessingContext.of(context, command))
/* 624 */       .through(new TypeToken<CommandPostprocessor<C>>() {
/*     */         
/* 626 */         }).complete();
/* 627 */     return ((String)context.optional("__COMMAND_POST_PROCESSED__").orElse("")).isEmpty() ? 
/* 628 */       State.REJECTED : 
/* 629 */       State.ACCEPTED;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SuggestionProcessor<C> suggestionProcessor() {
/* 639 */     return this.suggestionProcessor;
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
/*     */   public void suggestionProcessor(SuggestionProcessor<C> suggestionProcessor) {
/* 652 */     this.suggestionProcessor = suggestionProcessor;
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
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public ParserRegistry<C> parserRegistry() {
/* 671 */     return this.parserRegistry;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ParameterInjectorRegistry<C> parameterInjectorRegistry() {
/* 680 */     return this.parameterInjectorRegistry;
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
/*     */   public final ExceptionController<C> exceptionController() {
/* 692 */     return this.exceptionController;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public final Collection<Command<C>> commands() {
/* 702 */     return Collections.unmodifiableCollection(this.commands);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public final HelpHandler<C> createHelpHandler() {
/* 716 */     return this.helpHandlerFactory.createHelpHandler(cmd -> true);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public final HelpHandler<C> createHelpHandler(CommandPredicate<C> filter) {
/* 734 */     return this.helpHandlerFactory.createHelpHandler(filter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public final HelpHandlerFactory<C> helpHandlerFactory() {
/* 744 */     return this.helpHandlerFactory;
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
/*     */   public final void helpHandlerFactory(HelpHandlerFactory<C> helpHandlerFactory) {
/* 756 */     this.helpHandlerFactory = helpHandlerFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public Configurable<ManagerSetting> settings() {
/* 766 */     return this.settings;
/*     */   }
/*     */ 
/*     */   
/*     */   public final RegistrationState state() {
/* 771 */     return this.state.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean transitionIfPossible(RegistrationState in, RegistrationState out) {
/* 776 */     return (this.state.compareAndSet(in, out) || this.state.get() == out);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   protected final void lockRegistration() {
/* 787 */     if (state() == RegistrationState.BEFORE_REGISTRATION) {
/* 788 */       transitionOrThrow((State)RegistrationState.BEFORE_REGISTRATION, (State)RegistrationState.AFTER_REGISTRATION);
/*     */       return;
/*     */     } 
/* 791 */     transitionOrThrow((State)RegistrationState.REGISTERING, (State)RegistrationState.AFTER_REGISTRATION);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public boolean isCommandRegistrationAllowed() {
/* 804 */     return (settings().get((Setting)ManagerSetting.ALLOW_UNSAFE_REGISTRATION) || this.state
/* 805 */       .get() != RegistrationState.AFTER_REGISTRATION);
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
/*     */   protected void registerDefaultExceptionHandlers(Consumer<Triplet<CommandContext<C>, Caption, List<CaptionVariable>>> messageSender, Consumer<Pair<String, Throwable>> logger) {
/* 818 */     DefaultExceptionHandlers<C> defaultExceptionHandlers = new DefaultExceptionHandlers<>(messageSender, logger, this.exceptionController);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 823 */     defaultExceptionHandlers.register();
/*     */   }
/*     */   
/*     */   public abstract boolean hasPermission(C paramC, String paramString);
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\CommandManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */