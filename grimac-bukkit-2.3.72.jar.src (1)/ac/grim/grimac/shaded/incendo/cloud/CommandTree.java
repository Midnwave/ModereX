/*      */ package ac.grim.grimac.shaded.incendo.cloud;
/*      */ 
/*      */ import ac.grim.grimac.shaded.geantyref.GenericTypeReflector;
/*      */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.component.DefaultValue;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.context.ParsingContext;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.exception.AmbiguousNodeException;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.exception.ArgumentParseException;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.exception.InvalidCommandSenderException;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.exception.InvalidSyntaxException;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.exception.NoCommandInLeafException;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.exception.NoPermissionException;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.exception.NoSuchCommandException;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.internal.CommandNode;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.internal.SuggestionContext;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKey;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.parser.flag.CommandFlagParser;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.LiteralParser;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.permission.Permission;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.permission.PermissionResult;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.setting.ManagerSetting;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.setting.Setting;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestion;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionMapper;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestions;
/*      */ import ac.grim.grimac.shaded.incendo.cloud.util.CompletableFutures;
/*      */ import java.lang.reflect.Type;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Objects;
/*      */ import java.util.Optional;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.CompletableFuture;
/*      */ import java.util.concurrent.CompletionStage;
/*      */ import java.util.concurrent.Executor;
/*      */ import java.util.function.Consumer;
/*      */ import java.util.stream.Collectors;
/*      */ import java.util.stream.Stream;
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
/*      */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*      */ public final class CommandTree<C>
/*      */ {
/*  105 */   private final Object commandLock = new Object();
/*      */   
/*  107 */   private final CommandNode<C> internalTree = new CommandNode(null);
/*      */   private final CommandManager<C> commandManager;
/*      */   
/*      */   private CommandTree(CommandManager<C> commandManager) {
/*  111 */     this.commandManager = commandManager;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C> CommandTree<C> newTree(CommandManager<C> commandManager) {
/*  122 */     return new CommandTree<>(commandManager);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @API(status = API.Status.STABLE)
/*      */   public CommandManager<C> commandManager() {
/*  132 */     return this.commandManager;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @API(status = API.Status.STABLE)
/*      */   public Collection<CommandNode<C>> rootNodes() {
/*  142 */     return this.internalTree.children();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CommandNode<C> getNamedNode(String name) {
/*  152 */     for (CommandNode<C> node : rootNodes()) {
/*  153 */       CommandComponent<C> component = node.component();
/*  154 */       if (component == null || component.type() != CommandComponent.ComponentType.LITERAL) {
/*      */         continue;
/*      */       }
/*  157 */       for (String alias : component.aliases()) {
/*  158 */         if (alias.equalsIgnoreCase(name)) {
/*  159 */           return node;
/*      */         }
/*      */       } 
/*      */     } 
/*  163 */     return null;
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
/*      */   @API(status = API.Status.STABLE)
/*      */   public CompletableFuture<Command<C>> parse(CommandContext<C> commandContext, CommandInput commandInput, Executor parsingExecutor) {
/*  180 */     return CompletableFutures.scheduleOn(parsingExecutor, () -> parseDirect(commandContext, commandInput, parsingExecutor))
/*  181 */       .thenApply(command -> {
/*      */           if (command != null) {
/*      */             commandContext.command(command);
/*      */           }
/*      */           return command;
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private CompletableFuture<Command<C>> parseDirect(CommandContext<C> commandContext, CommandInput commandInput, Executor parsingExecutor) {
/*  195 */     if (this.internalTree.isLeaf() && this.internalTree.component() == null) {
/*  196 */       return CompletableFutures.failedFuture((Throwable)new NoSuchCommandException(commandContext
/*      */             
/*  198 */             .sender(), new ArrayList(), commandInput
/*      */             
/*  200 */             .peekString()));
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  205 */     return parseCommand(new ArrayList<>(), commandContext, commandInput, this.internalTree, parsingExecutor)
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  211 */       .thenCompose(command -> 
/*  212 */         (command != null && command.senderType().isPresent() && !GenericTypeReflector.isSuperType(((TypeToken)command.senderType().get()).getType(), commandContext.sender().getClass())) ? CompletableFutures.failedFuture((Throwable)new InvalidCommandSenderException(commandContext.sender(), ((TypeToken)command.senderType().get()).getType(), new ArrayList(command.components()), command)) : CompletableFuture.completedFuture(command));
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
/*      */ 
/*      */   
/*      */   private CompletableFuture<Command<C>> parseCommand(List<CommandComponent<C>> parsedArguments, CommandContext<C> commandContext, CommandInput commandInput, CommandNode<C> root, Executor executor) {
/*  235 */     Optional<PermissionResult> permissionResult = determineAccess((C)commandContext.sender(), root);
/*  236 */     if (!permissionResult.isPresent()) {
/*  237 */       return CompletableFutures.failedFuture((Throwable)new InvalidCommandSenderException(commandContext
/*      */             
/*  239 */             .sender(), (Set)root
/*  240 */             .nodeMeta().get(CommandNode.META_KEY_SENDER_TYPES), 
/*  241 */             getComponentChain(root), null));
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  246 */     if (((PermissionResult)permissionResult.get()).denied()) {
/*  247 */       return CompletableFutures.failedFuture((Throwable)new NoPermissionException(permissionResult
/*      */             
/*  249 */             .get(), commandContext
/*  250 */             .sender(), 
/*  251 */             getComponentChain(root)));
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  256 */     CompletableFuture<Command<C>> parsedChild = attemptParseUnambiguousChild(parsedArguments, commandContext, root, commandInput, executor);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  263 */     if (parsedChild != null) {
/*  264 */       return parsedChild;
/*      */     }
/*      */ 
/*      */     
/*  268 */     if (root.children().isEmpty()) {
/*  269 */       CommandComponent<C> rootComponent = root.component();
/*  270 */       if (rootComponent == null || root.command() == null || !commandInput.isEmpty())
/*      */       {
/*  272 */         return CompletableFutures.failedFuture((Throwable)new InvalidSyntaxException(this.commandManager
/*      */               
/*  274 */               .commandSyntaxFormatter()
/*  275 */               .apply(commandContext.sender(), parsedArguments, root), commandContext
/*  276 */               .sender(), getComponentChain(root)));
/*      */       }
/*      */ 
/*      */       
/*  280 */       return CompletableFuture.completedFuture(root.command());
/*      */     } 
/*      */     
/*  283 */     CompletableFuture<Command<C>> childCompletable = CompletableFuture.completedFuture(null);
/*  284 */     for (CommandNode<C> child : (Iterable<CommandNode<C>>)new ArrayList(root.children())) {
/*  285 */       if (child.component() == null) {
/*      */         continue;
/*      */       }
/*      */       
/*  289 */       childCompletable = childCompletable.thenCompose(previousResult -> {
/*      */             if (previousResult != null) {
/*      */               return CompletableFuture.completedFuture(previousResult);
/*      */             }
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*      */             CommandComponent<C> component = Objects.<CommandComponent<C>>requireNonNull(child.component());
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*      */             ParsingContext<C> parsingContext = commandContext.createParsingContext(component);
/*      */ 
/*      */ 
/*      */             
/*      */             commandInput.skipWhitespace(1);
/*      */ 
/*      */ 
/*      */             
/*      */             CommandInput currentInput = commandInput.copy();
/*      */ 
/*      */ 
/*      */             
/*      */             parsingContext.markStart();
/*      */ 
/*      */ 
/*      */             
/*      */             return component.parser().parseFuture(commandContext, commandInput).thenComposeAsync((), executor);
/*      */           });
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  324 */     return childCompletable.thenCompose(completedCommand -> {
/*      */           if (completedCommand != null) {
/*      */             return CompletableFuture.completedFuture(completedCommand);
/*      */           }
/*      */           if (root.equals(this.internalTree)) {
/*      */             return CompletableFutures.failedFuture((Throwable)new NoSuchCommandException(commandContext.sender(), (List)getChain(root).stream().map(CommandNode::component).collect(Collectors.toList()), commandInput.peekString()));
/*      */           }
/*      */           CommandComponent<C> rootComponent = root.component();
/*      */           if (rootComponent != null && root.command() != null && commandInput.isEmpty()) {
/*      */             Command<C> command = root.command();
/*      */             PermissionResult check = this.commandManager.testPermission((C)commandContext.sender(), command.commandPermission());
/*      */             return check.denied() ? CompletableFutures.failedFuture((Throwable)new NoPermissionException(check, commandContext.sender(), getComponentChain(root))) : CompletableFuture.completedFuture(root.command());
/*      */           } 
/*      */           return CompletableFutures.failedFuture((Throwable)new InvalidSyntaxException(this.commandManager.commandSyntaxFormatter().apply(commandContext.sender(), parsedArguments, root), commandContext.sender(), getComponentChain(root)));
/*      */         });
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private CompletableFuture<Command<C>> attemptParseUnambiguousChild(List<CommandComponent<C>> parsedArguments, CommandContext<C> commandContext, CommandNode<C> root, CommandInput commandInput, Executor executor) {
/*      */     CompletableFuture<?> parseResult;
/*  378 */     C sender = (C)commandContext.sender();
/*  379 */     List<CommandNode<C>> children = root.children();
/*      */ 
/*      */     
/*  382 */     if (!commandInput.isEmpty() && matchesLiteral(children, commandInput.peekString())) {
/*  383 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  390 */     List<CommandNode<C>> argumentNodes = (List<CommandNode<C>>)children.stream().filter(n -> (n.component() != null && n.component().type() != CommandComponent.ComponentType.LITERAL)).collect(Collectors.toList());
/*  391 */     if (argumentNodes.size() > 1)
/*  392 */       throw new IllegalStateException("Unexpected ambiguity detected, number of dynamic child nodes should not exceed 1"); 
/*  393 */     if (argumentNodes.isEmpty()) {
/*  394 */       return null;
/*      */     }
/*  396 */     CommandNode<C> child = argumentNodes.get(0);
/*      */ 
/*      */     
/*  399 */     Optional<PermissionResult> childCheck = determineAccess(sender, child);
/*  400 */     if (!childCheck.isPresent()) {
/*  401 */       return CompletableFutures.failedFuture((Throwable)new InvalidCommandSenderException(sender, (Set)child
/*      */ 
/*      */             
/*  404 */             .nodeMeta().get(CommandNode.META_KEY_SENDER_TYPES), 
/*  405 */             getComponentChain(child), null));
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  410 */     if (!commandInput.isEmpty() && ((PermissionResult)childCheck.get()).denied()) {
/*  411 */       return CompletableFutures.failedFuture((Throwable)new NoPermissionException(childCheck
/*      */             
/*  413 */             .get(), sender, 
/*      */             
/*  415 */             getComponentChain(child)));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  421 */     if (child.component() == null) {
/*  422 */       return null;
/*      */     }
/*      */ 
/*      */     
/*  426 */     ArgumentParseResult<?> argumentValue = null;
/*      */ 
/*      */     
/*  429 */     if (commandInput.isEmpty() && child.component().type() != CommandComponent.ComponentType.FLAG) {
/*  430 */       CommandComponent<C> childComponent = Objects.<CommandComponent<C>>requireNonNull(child.component());
/*  431 */       if (childComponent.hasDefaultValue()) {
/*  432 */         DefaultValue<C, ?> defaultValue = Objects.<DefaultValue<C, ?>>requireNonNull(childComponent.defaultValue(), "defaultValue");
/*      */         
/*  434 */         if (defaultValue instanceof DefaultValue.ParsedDefaultValue) {
/*  435 */           return attemptParseUnambiguousChild(parsedArguments, commandContext, root, commandInput
/*      */ 
/*      */ 
/*      */               
/*  439 */               .appendString(((DefaultValue.ParsedDefaultValue)defaultValue).value()), executor);
/*      */         }
/*      */ 
/*      */         
/*  443 */         argumentValue = defaultValue.evaluateDefault(commandContext);
/*      */       } else {
/*  445 */         if (!child.component().required()) {
/*  446 */           if (child.command() == null) {
/*      */ 
/*      */             
/*  449 */             CommandNode<C> node = child;
/*  450 */             while (!node.isLeaf()) {
/*  451 */               node = node.children().get(0);
/*  452 */               CommandComponent<C> nodeComponent = node.component();
/*  453 */               if (nodeComponent != null && node.command() != null) {
/*  454 */                 child.command(node.command());
/*      */               }
/*      */             } 
/*      */           } 
/*  458 */           return CompletableFuture.completedFuture(child.command());
/*  459 */         }  if (child.isLeaf()) {
/*  460 */           CommandComponent<C> commandComponent = root.component();
/*  461 */           if (commandComponent == null || root.command() == null) {
/*  462 */             List<CommandComponent<C>> components = ((Command<C>)Objects.<Command<C>>requireNonNull(child.command())).components();
/*  463 */             return CompletableFutures.failedFuture((Throwable)new InvalidSyntaxException(this.commandManager
/*      */                   
/*  465 */                   .commandSyntaxFormatter()
/*  466 */                   .apply(commandContext.sender(), components, child), sender, 
/*      */                   
/*  468 */                   getComponentChain(root)));
/*      */           } 
/*      */ 
/*      */ 
/*      */           
/*  473 */           Command<C> command1 = root.command();
/*  474 */           PermissionResult permissionResult = commandManager().testPermission(sender, command1.commandPermission());
/*  475 */           if (permissionResult.allowed()) {
/*  476 */             return CompletableFuture.completedFuture(command1);
/*      */           }
/*  478 */           return CompletableFutures.failedFuture((Throwable)new NoPermissionException(permissionResult, sender, 
/*      */ 
/*      */ 
/*      */                 
/*  482 */                 getComponentChain(root)));
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/*  487 */         CommandComponent<C> rootComponent = root.component();
/*  488 */         if (rootComponent == null || root.command() == null)
/*      */         {
/*  490 */           return CompletableFutures.failedFuture((Throwable)new InvalidSyntaxException(this.commandManager
/*      */                 
/*  492 */                 .commandSyntaxFormatter()
/*  493 */                 .apply(commandContext.sender(), parsedArguments, root), sender, 
/*      */                 
/*  495 */                 getComponentChain(root)));
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  501 */         Command<C> command = Objects.<Command<C>>requireNonNull(root.command());
/*  502 */         PermissionResult check = commandManager().testPermission(sender, command.commandPermission());
/*  503 */         if (check.allowed()) {
/*  504 */           return CompletableFuture.completedFuture(command);
/*      */         }
/*      */         
/*  507 */         return CompletableFutures.failedFuture((Throwable)new NoPermissionException(check, sender, 
/*      */ 
/*      */ 
/*      */               
/*  511 */               getComponentChain(root)));
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  517 */     CommandComponent<C> component = Objects.<CommandComponent<C>>requireNonNull(child.component());
/*      */ 
/*      */     
/*  520 */     if (argumentValue != null) {
/*  521 */       if (argumentValue.parsedValue().isPresent()) {
/*  522 */         parseResult = CompletableFuture.completedFuture(argumentValue.parsedValue().get());
/*      */       } else {
/*  524 */         parseResult = CompletableFutures.failedFuture((Throwable)argumentParseException(commandContext, child, argumentValue));
/*      */       } 
/*      */     } else {
/*      */       
/*  528 */       parseResult = parseArgument(commandContext, child, commandInput, executor).thenApply(result -> result.parsedValue().orElse(null));
/*      */     } 
/*      */     
/*  531 */     return parseResult.thenComposeAsync(value -> { if (value == null) return CompletableFuture.completedFuture(null);  commandContext.store(component.name(), value); if (child.isLeaf()) return commandInput.isEmpty() ? CompletableFuture.completedFuture(child.command()) : CompletableFutures.failedFuture((Throwable)new InvalidSyntaxException(this.commandManager.commandSyntaxFormatter().apply(commandContext.sender(), parsedArguments, child), sender, getComponentChain(root)));  parsedArguments.add(Objects.<CommandComponent>requireNonNull(child.component())); return parseCommand((List)parsedArguments, commandContext, commandInput, child, executor); }executor);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean matchesLiteral(List<CommandNode<C>> children, String input) {
/*  557 */     return children.stream()
/*  558 */       .map(CommandNode::component)
/*  559 */       .filter(Objects::nonNull)
/*  560 */       .filter(n -> (n.type() == CommandComponent.ComponentType.LITERAL))
/*  561 */       .flatMap(arg -> Stream.concat(Stream.of(arg.name()), arg.aliases().stream()))
/*  562 */       .anyMatch(arg -> arg.equals(input));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private CompletableFuture<ArgumentParseResult<?>> parseArgument(CommandContext<C> commandContext, CommandNode<C> node, CommandInput commandInput, Executor executor) {
/*  571 */     ParsingContext<C> parsingContext = commandContext.createParsingContext(node.component());
/*  572 */     parsingContext.markStart();
/*      */     
/*  574 */     ArgumentParseResult<Boolean> preParseResult = node.component().preprocess(commandContext, commandInput);
/*      */     
/*  576 */     if (preParseResult.failure().isPresent() || !((Boolean)preParseResult.parsedValue().orElse(Boolean.valueOf(false))).booleanValue()) {
/*  577 */       parsingContext.markEnd();
/*  578 */       parsingContext.success(false);
/*  579 */       if (preParseResult.failure().isPresent()) {
/*  580 */         return CompletableFutures.failedFuture((Throwable)
/*  581 */             argumentParseException(commandContext, node, preParseResult));
/*      */       }
/*      */       
/*  584 */       return CompletableFuture.completedFuture(preParseResult);
/*      */     } 
/*      */ 
/*      */     
/*  588 */     commandInput.skipWhitespace(1);
/*      */     
/*  590 */     CommandInput currentInput = commandInput.copy();
/*      */     
/*  592 */     return node.component().parser()
/*  593 */       .parseFuture(commandContext, commandInput)
/*  594 */       .thenComposeAsync(result -> {
/*      */           parsingContext.consumedInput(currentInput, commandInput);
/*      */           parsingContext.markEnd();
/*      */           parsingContext.success(false);
/*      */           if (result.failure().isPresent()) {
/*      */             commandInput.cursor(currentInput.cursor());
/*      */             return CompletableFutures.failedFuture((Throwable)argumentParseException(commandContext, node, result));
/*      */           } 
/*      */           return CompletableFuture.completedFuture(result);
/*      */         }executor);
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
/*      */   private ArgumentParseException argumentParseException(CommandContext<C> commandContext, CommandNode<C> node, ArgumentParseResult<?> result) {
/*  615 */     return new ArgumentParseException(result
/*  616 */         .failure().get(), commandContext
/*  617 */         .sender(), 
/*  618 */         getComponentChain(node));
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
/*      */   @API(status = API.Status.STABLE)
/*      */   public <S extends Suggestion> CompletableFuture<Suggestions<C, S>> getSuggestions(CommandContext<C> context, CommandInput commandInput, SuggestionMapper<S> mapper, Executor executor) {
/*  639 */     return CompletableFutures.scheduleOn(executor, () -> getSuggestionsDirect(context, commandInput, mapper, executor));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private <S extends Suggestion> CompletableFuture<Suggestions<C, S>> getSuggestionsDirect(CommandContext<C> context, CommandInput commandInput, SuggestionMapper<S> mapper, Executor executor) {
/*  649 */     SuggestionContext<C, S> suggestionCtx = new SuggestionContext(this.commandManager.suggestionProcessor(), context, commandInput, mapper);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  654 */     return getSuggestions(suggestionCtx, commandInput, this.internalTree, executor)
/*  655 */       .thenApply($ -> suggestionCtx.makeSuggestions());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private CompletableFuture<SuggestionContext<C, ?>> getSuggestions(SuggestionContext<C, ?> context, CommandInput commandInput, CommandNode<C> root, Executor executor) {
/*  666 */     if (!((Boolean)determineAccess((C)context.commandContext().sender(), root).<Boolean>map(PermissionResult::allowed).orElse(Boolean.valueOf(false))).booleanValue()) {
/*  667 */       return CompletableFuture.completedFuture(context);
/*      */     }
/*      */     
/*  670 */     List<CommandNode<C>> children = root.children();
/*      */ 
/*      */ 
/*      */     
/*  674 */     List<CommandNode<C>> staticArguments = (List<CommandNode<C>>)children.stream().filter(n -> (n.component() != null)).filter(n -> (n.component().type() == CommandComponent.ComponentType.LITERAL)).collect(Collectors.toList());
/*      */     
/*  676 */     if (!commandInput.isEmpty()) {
/*  677 */       commandInput.skipWhitespace(1);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  682 */     if (!staticArguments.isEmpty() && !commandInput.isEmpty(true)) {
/*  683 */       CommandInput commandInputCopy = commandInput.copy();
/*  684 */       for (CommandNode<C> child : staticArguments) {
/*  685 */         CommandComponent<C> childComponent = child.component();
/*  686 */         if (childComponent == null) {
/*      */           continue;
/*      */         }
/*      */         
/*  690 */         ArgumentParseResult<?> result = childComponent.parser().parse(context
/*  691 */             .commandContext(), commandInput);
/*      */ 
/*      */ 
/*      */         
/*  695 */         if (result.failure().isPresent()) {
/*  696 */           commandInput.cursor(commandInputCopy.cursor());
/*      */         }
/*      */         
/*  699 */         if (!result.parsedValue().isPresent()) {
/*      */           continue;
/*      */         }
/*      */         
/*  703 */         if (commandInput.isEmpty()) {
/*      */           break;
/*      */         }
/*      */ 
/*      */         
/*  708 */         return getSuggestions(context, commandInput, child, executor);
/*      */       } 
/*      */ 
/*      */       
/*  712 */       commandInput.cursor(commandInputCopy.cursor());
/*      */     } 
/*      */ 
/*      */     
/*  716 */     CompletableFuture<SuggestionContext<C, ?>> suggestionFuture = CompletableFuture.completedFuture(context);
/*  717 */     if (commandInput.remainingTokens() <= 1) {
/*  718 */       for (CommandNode<C> node : staticArguments)
/*      */       {
/*  720 */         suggestionFuture = suggestionFuture.thenCompose(ctx -> addSuggestionsForLiteralArgument(context, node, commandInput));
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*  725 */     for (CommandNode<C> child : (Iterable<CommandNode<C>>)root.children()) {
/*  726 */       if (child.component() == null || child.component().type() == CommandComponent.ComponentType.LITERAL) {
/*      */         continue;
/*      */       }
/*      */       
/*  730 */       suggestionFuture = suggestionFuture.thenCompose(ctx -> addSuggestionsForDynamicArgument(context, commandInput, child, executor, false));
/*      */     } 
/*      */     
/*  733 */     return suggestionFuture;
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
/*      */   private CompletableFuture<SuggestionContext<C, ?>> addSuggestionsForLiteralArgument(SuggestionContext<C, ?> context, CommandNode<C> node, CommandInput input) {
/*  749 */     if (!((Boolean)determineAccess((C)context.commandContext().sender(), node).<Boolean>map(PermissionResult::allowed).orElse(Boolean.valueOf(false))).booleanValue()) {
/*  750 */       return CompletableFuture.completedFuture(context);
/*      */     }
/*  752 */     CommandComponent<C> component = Objects.<CommandComponent<C>>requireNonNull(node.component());
/*  753 */     return component.suggestionProvider()
/*  754 */       .suggestionsFuture(context.commandContext(), input.copy())
/*  755 */       .thenApply(suggestionsToAdd -> {
/*      */           String string = input.peekString();
/*      */           for (Suggestion suggestion : suggestionsToAdd) {
/*      */             if (suggestion.suggestion().equals(string) || !suggestion.suggestion().startsWith(string)) {
/*      */               continue;
/*      */             }
/*      */             context.addSuggestion(suggestion);
/*      */           } 
/*      */           return context;
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private CompletableFuture<SuggestionContext<C, ?>> addSuggestionsForDynamicArgument(SuggestionContext<C, ?> context, CommandInput commandInput, CommandNode<C> child, Executor executor, boolean inFlag) {
/*      */     CompletableFuture<SuggestionContext<C, ?>> parsingFuture;
/*  775 */     CommandComponent<C> component = child.component();
/*  776 */     if (component == null) {
/*  777 */       return CompletableFuture.completedFuture(context);
/*      */     }
/*      */     
/*  780 */     if (!inFlag && component.parser() instanceof CommandFlagParser) {
/*      */ 
/*      */ 
/*      */       
/*  784 */       CommandFlagParser<C> parser = (CommandFlagParser<C>)component.parser();
/*      */       
/*  786 */       return parser.parseCurrentFlag(context.commandContext(), commandInput, executor).thenCompose(lastFlag -> {
/*      */             if (lastFlag.isPresent()) {
/*      */               context.commandContext().store(CommandFlagParser.FLAG_META_KEY, lastFlag.get());
/*      */             } else {
/*      */               context.commandContext().remove(CommandFlagParser.FLAG_META_KEY);
/*      */             } 
/*      */             
/*      */             return addSuggestionsForDynamicArgument(context, commandInput, child, executor, true);
/*      */           });
/*      */     } 
/*  796 */     if (commandInput.isEmpty() || commandInput.remainingTokens() == 1 || (child
/*  797 */       .isLeaf() && child.component().parser() instanceof ac.grim.grimac.shaded.incendo.cloud.parser.aggregate.AggregateParser) || (child
/*  798 */       .isLeaf() && child.component().parser() instanceof CommandFlagParser)) {
/*  799 */       return addArgumentSuggestions(context, child, commandInput, executor);
/*      */     }
/*      */ 
/*      */     
/*  803 */     CommandInput commandInputOriginal = commandInput.copy();
/*      */ 
/*      */     
/*  806 */     ArgumentParseResult<Boolean> preParseResult = component.preprocess(context
/*  807 */         .commandContext(), commandInput);
/*      */ 
/*      */ 
/*      */     
/*  811 */     boolean preParseSuccess = (!preParseResult.failure().isPresent() && ((Boolean)preParseResult.parsedValue().orElse(Boolean.valueOf(false))).booleanValue());
/*      */ 
/*      */ 
/*      */     
/*  815 */     if (!preParseSuccess) {
/*  816 */       parsingFuture = CompletableFuture.completedFuture(null);
/*      */     } else {
/*      */       
/*  819 */       ParsingContext<C> parsingContext = context.commandContext().createParsingContext(child.component());
/*  820 */       parsingContext.markStart();
/*  821 */       CommandInput preParseInput = commandInput.copy();
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  826 */       parsingFuture = child.component().parser().parseFuture(context.commandContext(), commandInput).thenComposeAsync(result -> {
/*      */             Optional<?> parsedValue = result.parsedValue();
/*      */             
/*      */             boolean parseSuccess = parsedValue.isPresent();
/*      */             
/*      */             if (result.failure().isPresent()) {
/*      */               commandInput.cursor(preParseInput.cursor());
/*      */               
/*      */               return addArgumentSuggestions(context, child, commandInput, executor);
/*      */             } 
/*      */             
/*      */             if (child.isLeaf()) {
/*      */               if (!commandInput.isEmpty()) {
/*      */                 return CompletableFuture.completedFuture(context);
/*      */               }
/*      */               
/*      */               commandInput.cursor(commandInputOriginal.cursor());
/*      */               
/*      */               addArgumentSuggestions(context, child, commandInput, executor);
/*      */             } 
/*      */             
/*      */             if (parseSuccess && (!commandInput.isEmpty() || commandInput.input().endsWith(" "))) {
/*      */               if (commandInput.isEmpty()) {
/*      */                 commandInput.moveCursor(-1);
/*      */               }
/*      */               
/*      */               context.commandContext().store(child.component().name(), parsedValue.get());
/*      */               
/*      */               parsingContext.success(true);
/*      */               
/*      */               return getSuggestions(context, commandInput, child, executor);
/*      */             } 
/*      */             
/*      */             if (!parseSuccess && commandInputOriginal.remainingTokens() > 1) {
/*      */               commandInput.cursor(commandInputOriginal.cursor());
/*      */               return CompletableFuture.completedFuture(context);
/*      */             } 
/*      */             return CompletableFuture.completedFuture(null);
/*      */           }executor);
/*      */     } 
/*  866 */     return parsingFuture.thenCompose(previousResult -> {
/*      */           if (previousResult != null) {
/*      */             return CompletableFuture.completedFuture(previousResult);
/*      */           }
/*      */ 
/*      */           
/*      */           commandInput.cursor(commandInputOriginal.cursor());
/*      */           
/*  874 */           return (CompletionStage)((!preParseSuccess && commandInput.remainingTokens() > 1) ? CompletableFuture.completedFuture(context) : addArgumentSuggestions(context, child, commandInput, executor));
/*      */         });
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private CompletableFuture<SuggestionContext<C, ?>> addArgumentSuggestions(SuggestionContext<C, ?> context, CommandNode<C> node, CommandInput input, Executor executor) {
/*  901 */     CommandComponent<C> component = Objects.<CommandComponent<C>>requireNonNull(node.component());
/*  902 */     return addArgumentSuggestions(context, component, input, executor).thenCompose(ctx -> {
/*      */           
/*  904 */           boolean isParsingFlag = (component.type() == CommandComponent.ComponentType.FLAG && !node.children().isEmpty() && (!input.hasRemainingInput() || input.peek() != '-') && !context.commandContext().optional(CommandFlagParser.FLAG_META_KEY).isPresent());
/*      */           return !isParsingFlag ? CompletableFuture.completedFuture(ctx) : CompletableFuture.allOf((CompletableFuture<?>[])node.children().stream().map(()).toArray(())).thenApply(());
/*      */         });
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private CompletableFuture<SuggestionContext<C, ?>> addArgumentSuggestions(SuggestionContext<C, ?> context, CommandComponent<C> component, CommandInput input, Executor executor) {
/*  940 */     Objects.requireNonNull(context); return component.suggestionProvider().suggestionsFuture(context.commandContext(), input.copy()).thenAcceptAsync(context::addSuggestions, executor)
/*  941 */       .thenApply(in -> context);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void insertCommand(Command<C> command) {
/*  951 */     synchronized (this.commandLock) {
/*  952 */       CommandComponent<C> flagComponent = command.flagComponent();
/*  953 */       List<CommandComponent<C>> nonFlagArguments = command.nonFlagArguments();
/*  954 */       int flagStartIdx = flagStartIndex(nonFlagArguments);
/*      */       
/*  956 */       CommandNode<C> node = this.internalTree;
/*  957 */       for (int i = 0; i < nonFlagArguments.size(); i++) {
/*  958 */         CommandComponent<C> component = nonFlagArguments.get(i);
/*      */         
/*  960 */         CommandNode<C> tempNode = node.getChild(component);
/*  961 */         if (tempNode == null) {
/*  962 */           tempNode = node.addChild(component);
/*  963 */         } else if (component.type() == CommandComponent.ComponentType.LITERAL && tempNode.component() != null) {
/*  964 */           for (String alias : component.aliases()) {
/*  965 */             ((LiteralParser)tempNode.component().parser()).insertAlias(alias);
/*      */           }
/*      */         } 
/*  968 */         if (!node.children().isEmpty()) {
/*  969 */           node.sortChildren();
/*      */         }
/*  971 */         tempNode.parent(node);
/*  972 */         node = tempNode;
/*      */         
/*  974 */         if (flagComponent != null && i >= flagStartIdx) {
/*  975 */           tempNode = node.addChild(flagComponent);
/*  976 */           tempNode.parent(node);
/*  977 */           node = tempNode;
/*      */         } 
/*      */       } 
/*      */       
/*  981 */       CommandComponent<C> nodeComponent = node.component();
/*  982 */       if (nodeComponent != null) {
/*  983 */         if (node.command() != null) {
/*  984 */           throw new IllegalStateException(String.format("Duplicate command chains detected. Node '%s' already has an owning command (%s)", new Object[] { node, node
/*      */                   
/*  986 */                   .command() }));
/*      */         }
/*      */ 
/*      */         
/*  990 */         node.command(command);
/*      */       } 
/*      */       
/*  993 */       verifyAndRegister();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int flagStartIndex(List<CommandComponent<C>> components) {
/* 1005 */     if (this.commandManager.settings().get((Setting)ManagerSetting.LIBERAL_FLAG_PARSING)) {
/* 1006 */       for (int i = components.size() - 1; i >= 0; i--) {
/* 1007 */         if (((CommandComponent)components.get(i)).type() == CommandComponent.ComponentType.LITERAL) {
/* 1008 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/* 1014 */     return components.size() - 1;
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
/*      */   private Optional<PermissionResult> determineAccess(C sender, CommandNode<C> node) {
/* 1031 */     Map<Type, Permission> accessMap = (Map<Type, Permission>)node.nodeMeta().getOrNull(CommandNode.META_KEY_ACCESS);
/* 1032 */     if (accessMap == null) {
/* 1033 */       throw new IllegalStateException("Expected access requirements to be propagated");
/*      */     }
/* 1035 */     Set<Permission> failed = new HashSet<>();
/* 1036 */     for (Map.Entry<Type, Permission> entry : accessMap.entrySet()) {
/* 1037 */       if (GenericTypeReflector.isSuperType(entry.getKey(), sender.getClass())) {
/* 1038 */         PermissionResult result = this.commandManager.testPermission(sender, entry.getValue());
/* 1039 */         if (result.allowed()) {
/* 1040 */           return Optional.of(result);
/*      */         }
/* 1042 */         failed.add(entry.getValue());
/*      */       } 
/*      */     } 
/*      */     
/* 1046 */     if (failed.isEmpty()) {
/* 1047 */       return Optional.empty();
/*      */     }
/* 1049 */     return Optional.of(PermissionResult.denied(Permission.anyOf(failed)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void verifyAndRegister() {
/* 1057 */     this.internalTree.children().stream().map(CommandNode::component).forEach(component -> {
/*      */           if (component.type() != CommandComponent.ComponentType.LITERAL) {
/*      */             throw new IllegalStateException("Top level command argument cannot be a variable");
/*      */           }
/*      */         });
/*      */     
/* 1063 */     checkAmbiguity(this.internalTree);
/*      */ 
/*      */     
/* 1066 */     getLeaves(this.internalTree).forEach(leaf -> {
/*      */           if (leaf.command() == null) {
/*      */             throw new NoCommandInLeafException(leaf.component());
/*      */           }
/*      */           
/*      */           Command<C> owningCommand = leaf.command();
/*      */           
/*      */           this.commandManager.commandRegistrationHandler().registerCommand(owningCommand);
/*      */         });
/* 1075 */     getExecutorNodes(this.internalTree).forEach(this::propagateRequirements);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @API(status = API.Status.INTERNAL)
/*      */   public CommandNode<C> rootNode() {
/* 1085 */     return this.internalTree;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void propagateRequirements(CommandNode<C> leafNode) {
/* 1095 */     Permission commandPermission = leafNode.command().commandPermission();
/* 1096 */     Type<Object> senderType = leafNode.command().senderType().map(TypeToken::getType).orElse(null);
/* 1097 */     if (senderType == null) {
/* 1098 */       senderType = Object.class;
/*      */     }
/*      */     
/* 1101 */     List<CommandNode<C>> chain = getChain(leafNode);
/* 1102 */     Collections.reverse(chain);
/* 1103 */     for (CommandNode<C> commandArgumentNode : chain) {
/*      */       
/* 1105 */       Set<Type> senderTypes = (Set<Type>)commandArgumentNode.nodeMeta().computeIfAbsent(CommandNode.META_KEY_SENDER_TYPES, $ -> new HashSet());
/* 1106 */       updateSenderRequirements(senderTypes, senderType);
/*      */ 
/*      */       
/* 1109 */       Map<Type, Permission> accessMap = (Map<Type, Permission>)commandArgumentNode.nodeMeta().computeIfAbsent(CommandNode.META_KEY_ACCESS, $ -> new HashMap<>());
/* 1110 */       updateAccess(accessMap, senderType, commandPermission);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void updateAccess(Map<Type, Permission> senderTypes, Type senderType, Permission commandPermission) {
/* 1119 */     senderTypes.compute(senderType, (key, existing) -> (existing == null) ? commandPermission : Permission.anyOf(new Permission[] { existing, commandPermission }));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void updateSenderRequirements(Set<Type> senderTypes, Type senderType) {
/* 1128 */     boolean add = true;
/* 1129 */     for (Iterator<Type> iterator = senderTypes.iterator(); iterator.hasNext(); ) {
/* 1130 */       Type existingType = iterator.next();
/* 1131 */       if (GenericTypeReflector.isSuperType(existingType, senderType)) {
/* 1132 */         add = false;
/*      */         break;
/*      */       } 
/* 1135 */       if (GenericTypeReflector.isSuperType(senderType, existingType)) {
/* 1136 */         iterator.remove();
/*      */         break;
/*      */       } 
/*      */     } 
/* 1140 */     if (add) {
/* 1141 */       senderTypes.add(senderType);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkAmbiguity(CommandNode<C> node) throws AmbiguousNodeException {
/* 1152 */     if (node.isLeaf()) {
/*      */       return;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1161 */     List<CommandNode<C>> childVariableArguments = (List<CommandNode<C>>)node.children().stream().filter(n -> (n.component() != null)).filter(n -> (n.component().type() != CommandComponent.ComponentType.LITERAL)).collect(Collectors.toList());
/*      */ 
/*      */     
/* 1164 */     if (childVariableArguments.size() > 1) {
/* 1165 */       CommandNode<C> child = childVariableArguments.get(0);
/*      */       
/* 1167 */       throw new AmbiguousNodeException(node, child, (List)node
/*      */ 
/*      */           
/* 1170 */           .children()
/* 1171 */           .stream()
/* 1172 */           .filter(n -> (n.component() != null))
/* 1173 */           .collect(Collectors.toList()));
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1182 */     List<CommandNode<C>> childStaticArguments = (List<CommandNode<C>>)node.children().stream().filter(n -> (n.component() != null)).filter(n -> (n.component().type() == CommandComponent.ComponentType.LITERAL)).collect(Collectors.toList());
/*      */ 
/*      */ 
/*      */     
/* 1186 */     Set<String> checkedLiterals = new HashSet<>();
/* 1187 */     for (CommandNode<C> child : childStaticArguments) {
/* 1188 */       for (String nameOrAlias : child.component().aliases()) {
/* 1189 */         if (!checkedLiterals.add(nameOrAlias))
/*      */         {
/* 1191 */           throw new AmbiguousNodeException(node, child, (List)node
/*      */ 
/*      */               
/* 1194 */               .children()
/* 1195 */               .stream()
/* 1196 */               .filter(n -> (n.component() != null))
/* 1197 */               .collect(Collectors.toList()));
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1204 */     node.children().forEach(this::checkAmbiguity);
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
/*      */   @API(status = API.Status.INTERNAL)
/*      */   public List<CommandNode<C>> getLeavesRaw(CommandNode<C> node) {
/* 1217 */     List<CommandNode<C>> leaves = new LinkedList<>();
/* 1218 */     if (node.isLeaf()) {
/* 1219 */       if (node.component() != null) {
/* 1220 */         leaves.add(node);
/*      */       }
/*      */     } else {
/* 1223 */       node.children().forEach(child -> leaves.addAll(getLeavesRaw(child)));
/*      */     } 
/* 1225 */     return leaves;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private List<CommandNode<C>> getExecutorNodes(CommandNode<C> node) {
/* 1231 */     List<CommandNode<C>> leaves = new LinkedList<>();
/* 1232 */     if (node.command() != null) {
/* 1233 */       leaves.add(node);
/*      */     }
/* 1235 */     for (CommandNode<C> child : (Iterable<CommandNode<C>>)node.children()) {
/* 1236 */       leaves.addAll(getExecutorNodes(child));
/*      */     }
/* 1238 */     return leaves;
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
/*      */   @API(status = API.Status.INTERNAL)
/*      */   public List<CommandNode<C>> getLeaves(CommandNode<C> node) {
/* 1251 */     return (List<CommandNode<C>>)getLeavesRaw(node).stream()
/* 1252 */       .filter(n -> (n.component() != null))
/* 1253 */       .collect(Collectors.toList());
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
/*      */   private List<CommandComponent<?>> getComponentChain(CommandNode<C> end) {
/* 1265 */     return (List<CommandComponent<?>>)getChain(end).stream()
/* 1266 */       .map(CommandNode::component)
/* 1267 */       .filter(Objects::nonNull)
/* 1268 */       .collect(Collectors.toList());
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
/*      */   private List<CommandNode<C>> getChain(CommandNode<C> end) {
/* 1280 */     List<CommandNode<C>> chain = new LinkedList<>();
/* 1281 */     CommandNode<C> tail = end;
/* 1282 */     while (tail != null) {
/* 1283 */       chain.add(tail);
/* 1284 */       tail = tail.parent();
/*      */     } 
/* 1286 */     Collections.reverse(chain);
/* 1287 */     return chain;
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
/*      */   void deleteRecursively(CommandNode<C> node, boolean root, Consumer<Command<C>> commandConsumer) {
/* 1303 */     for (CommandNode<C> child : (Iterable<CommandNode<C>>)new ArrayList(node.children())) {
/* 1304 */       deleteRecursively(child, false, commandConsumer);
/*      */     }
/*      */     
/* 1307 */     CommandComponent<C> component = node.component();
/* 1308 */     Command<C> owner = (component == null) ? null : node.command();
/* 1309 */     if (owner != null) {
/* 1310 */       commandConsumer.accept(owner);
/*      */     }
/* 1312 */     removeNode(node, root);
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
/*      */   private void removeNode(CommandNode<C> node, boolean root) {
/* 1326 */     if (root) {
/* 1327 */       this.internalTree.removeChild(node);
/*      */     } else {
/* 1329 */       ((CommandNode)Objects.<CommandNode>requireNonNull(node.parent(), "parent")).removeChild(node);
/*      */     } 
/*      */   }
/*      */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\CommandTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */