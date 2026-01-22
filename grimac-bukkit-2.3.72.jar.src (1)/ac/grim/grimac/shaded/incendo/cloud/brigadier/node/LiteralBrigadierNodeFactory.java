/*     */ package ac.grim.grimac.shaded.incendo.cloud.brigadier.node;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.GenericTypeReflector;
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.BrigadierSetting;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.CloudBrigadierManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.argument.ArgumentTypeFactory;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.argument.BrigadierMapping;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.permission.BrigadierPermissionChecker;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.permission.BrigadierPermissionPredicate;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.suggestion.BrigadierSuggestionFactory;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.suggestion.CloudDelegatingSuggestionProvider;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.suggestion.SuggestionsType;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.suggestion.TooltipSuggestion;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.internal.CommandNode;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.MappedArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.aggregate.AggregateParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.permission.Permission;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.setting.Setting;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionFactory;
/*     */ import com.mojang.brigadier.Command;
/*     */ import com.mojang.brigadier.arguments.ArgumentType;
/*     */ import com.mojang.brigadier.arguments.StringArgumentType;
/*     */ import com.mojang.brigadier.builder.ArgumentBuilder;
/*     */ import com.mojang.brigadier.builder.LiteralArgumentBuilder;
/*     */ import com.mojang.brigadier.builder.RequiredArgumentBuilder;
/*     */ import com.mojang.brigadier.suggestion.SuggestionProvider;
/*     */ import com.mojang.brigadier.tree.CommandNode;
/*     */ import com.mojang.brigadier.tree.LiteralCommandNode;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.function.Predicate;
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
/*     */ @API(status = API.Status.STABLE, since = "2.0.0")
/*     */ public final class LiteralBrigadierNodeFactory<C, S>
/*     */   implements BrigadierNodeFactory<C, S, LiteralCommandNode<S>>
/*     */ {
/*     */   private final CloudBrigadierManager<C, S> cloudBrigadierManager;
/*     */   private final CommandManager<C> commandManager;
/*     */   private final BrigadierSuggestionFactory<C, S> brigadierSuggestionFactory;
/*     */   
/*     */   public LiteralBrigadierNodeFactory(CloudBrigadierManager<C, S> cloudBrigadierManager, CommandManager<C> commandManager, SuggestionFactory<C, ? extends TooltipSuggestion> suggestionFactory) {
/*  81 */     this.cloudBrigadierManager = cloudBrigadierManager;
/*  82 */     this.commandManager = commandManager;
/*  83 */     this.brigadierSuggestionFactory = new BrigadierSuggestionFactory(cloudBrigadierManager, commandManager, suggestionFactory);
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
/*     */   public LiteralCommandNode<S> createNode(String label, CommandNode<C> cloudCommand, Command<S> executor, BrigadierPermissionChecker<C> permissionChecker) {
/*  99 */     LiteralArgumentBuilder<S> literalArgumentBuilder = (LiteralArgumentBuilder<S>)LiteralArgumentBuilder.literal(label).requires((Predicate)requirement(cloudCommand, permissionChecker));
/*     */     
/* 101 */     updateExecutes((ArgumentBuilder)literalArgumentBuilder, cloudCommand, executor);
/*     */     
/* 103 */     LiteralCommandNode<S> constructedRoot = literalArgumentBuilder.build();
/* 104 */     for (CommandNode<C> child : (Iterable<CommandNode<C>>)cloudCommand.children()) {
/* 105 */       constructedRoot.addChild(constructCommandNode(child, permissionChecker, executor).build());
/*     */     }
/* 107 */     return constructedRoot;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private BrigadierPermissionPredicate<C, S> requirement(CommandNode<C> cloudCommand, BrigadierPermissionChecker<C> permissionChecker) {
/* 114 */     return new BrigadierPermissionPredicate(this.cloudBrigadierManager.senderMapper(), permissionChecker, cloudCommand);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LiteralCommandNode<S> createNode(String label, Command<C> cloudCommand, Command<S> executor, BrigadierPermissionChecker<C> permissionChecker) {
/* 125 */     CommandNode<C> node = this.commandManager.commandTree().getNamedNode(cloudCommand.rootComponent().name());
/* 126 */     Objects.requireNonNull(node, "node");
/*     */     
/* 128 */     return createNode(label, node, executor, permissionChecker);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LiteralCommandNode<S> createNode(String label, Command<C> cloudCommand, Command<S> executor) {
/* 137 */     return createNode(label, cloudCommand, executor, (sender, permission) -> this.commandManager.testPermission(sender, permission).allowed());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ArgumentBuilder<S, ?> constructCommandNode(CommandNode<C> root, BrigadierPermissionChecker<C> permissionChecker, Command<S> executor) {
/*     */     ArgumentBuilder<S, ?> argumentBuilder;
/* 146 */     if (root.component().parser() instanceof AggregateParser) {
/* 147 */       AggregateParser<C, ?> aggregateParser = (AggregateParser<C, ?>)root.component().parser();
/* 148 */       return constructAggregateNode(aggregateParser, root, permissionChecker, executor);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 157 */     if (root.component().type() == CommandComponent.ComponentType.LITERAL) {
/* 158 */       argumentBuilder = createLiteralArgumentBuilder(root.component(), root, permissionChecker);
/*     */     } else {
/* 160 */       argumentBuilder = createVariableArgumentBuilder(root.component(), root, permissionChecker);
/*     */     } 
/* 162 */     updateExecutes(argumentBuilder, root, executor);
/* 163 */     for (CommandNode<C> node : (Iterable<CommandNode<C>>)root.children()) {
/* 164 */       argumentBuilder.then(constructCommandNode(node, permissionChecker, executor));
/*     */     }
/* 166 */     return argumentBuilder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ArgumentBuilder<S, ?> createLiteralArgumentBuilder(CommandComponent<C> component, CommandNode<C> root, BrigadierPermissionChecker<C> permissionChecker) {
/* 174 */     return LiteralArgumentBuilder.literal(component.name())
/* 175 */       .requires((Predicate)requirement(root, permissionChecker));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ArgumentBuilder<S, ?> createVariableArgumentBuilder(CommandComponent<C> component, CommandNode<C> root, BrigadierPermissionChecker<C> permissionChecker) {
/*     */     SuggestionProvider<S> provider;
/* 183 */     ArgumentMapping<S> argumentMapping = getArgument(component
/* 184 */         .valueType(), component
/* 185 */         .parser());
/*     */ 
/*     */ 
/*     */     
/* 189 */     if (argumentMapping.suggestionsType() == SuggestionsType.CLOUD_SUGGESTIONS) {
/* 190 */       CloudDelegatingSuggestionProvider cloudDelegatingSuggestionProvider = new CloudDelegatingSuggestionProvider(this.brigadierSuggestionFactory, root);
/*     */     } else {
/* 192 */       provider = argumentMapping.suggestionProvider();
/*     */     } 
/*     */     
/* 195 */     return 
/* 196 */       RequiredArgumentBuilder.argument(component.name(), argumentMapping.argumentType())
/* 197 */       .suggests(provider)
/* 198 */       .requires((Predicate)requirement(root, permissionChecker));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ArgumentBuilder<S, ?> constructAggregateNode(AggregateParser<C, ?> aggregateParser, CommandNode<C> root, BrigadierPermissionChecker<C> permissionChecker, Command<S> executor) {
/* 207 */     Iterator<CommandComponent<C>> components = aggregateParser.components().iterator();
/* 208 */     List<ArgumentBuilder<S, ?>> argumentBuilders = new ArrayList<>();
/*     */     
/* 210 */     while (components.hasNext()) {
/* 211 */       CommandComponent<C> component = components.next();
/* 212 */       ArgumentBuilder<S, ?> fragmentBuilder = createVariableArgumentBuilder(component, root, permissionChecker);
/*     */       
/* 214 */       if (this.cloudBrigadierManager.settings().get((Setting)BrigadierSetting.FORCE_EXECUTABLE)) {
/* 215 */         fragmentBuilder.executes(executor);
/*     */       }
/*     */       
/* 218 */       argumentBuilders.add(fragmentBuilder);
/*     */     } 
/*     */ 
/*     */     
/* 222 */     ArgumentBuilder<S, ?> tail = argumentBuilders.get(argumentBuilders.size() - 1);
/* 223 */     for (CommandNode<C> node : (Iterable<CommandNode<C>>)root.children()) {
/* 224 */       tail.then(constructCommandNode(node, permissionChecker, executor));
/*     */     }
/*     */     
/* 227 */     updateExecutes(tail, root, executor);
/*     */ 
/*     */ 
/*     */     
/* 231 */     for (int i = argumentBuilders.size() - 1; i > 0; i--) {
/* 232 */       ((ArgumentBuilder)argumentBuilders.get(i - 1)).then(argumentBuilders.get(i));
/*     */     }
/*     */     
/* 235 */     return argumentBuilders.get(0);
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
/*     */   private <K extends ArgumentParser<C, ?>> ArgumentMapping<S> getArgument(TypeToken<?> valueType, K argumentParser) {
/* 251 */     if (argumentParser instanceof MappedArgumentParser) {
/* 252 */       return getArgument(valueType, ((MappedArgumentParser)argumentParser).baseParser());
/*     */     }
/*     */     
/* 255 */     BrigadierMapping<C, K, S> mapping = this.cloudBrigadierManager.mappings().mapping(argumentParser.getClass());
/* 256 */     if (mapping == null || mapping.mapper() == null) {
/* 257 */       return getDefaultMapping(valueType);
/*     */     }
/*     */     
/* 260 */     SuggestionProvider<S> suggestionProvider = mapping.makeSuggestionProvider((ArgumentParser)argumentParser);
/* 261 */     if (suggestionProvider == BrigadierMapping.delegateSuggestions()) {
/* 262 */       return ImmutableArgumentMapping.<S>builder()
/* 263 */         .argumentType(mapping.mapper().apply(argumentParser))
/* 264 */         .suggestionsType(SuggestionsType.CLOUD_SUGGESTIONS)
/* 265 */         .build();
/*     */     }
/* 267 */     return ImmutableArgumentMapping.<S>builder()
/* 268 */       .argumentType(mapping.mapper().apply(argumentParser))
/* 269 */       .suggestionProvider(suggestionProvider)
/* 270 */       .build();
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
/*     */   private ArgumentMapping<S> getDefaultMapping(TypeToken<?> type) {
/* 282 */     ArgumentTypeFactory<?> argumentTypeSupplier = (ArgumentTypeFactory)this.cloudBrigadierManager.defaultArgumentTypeFactories().get(GenericTypeReflector.erase(type.getType()));
/* 283 */     if (argumentTypeSupplier != null) {
/* 284 */       ArgumentType<?> argumentType = argumentTypeSupplier.create();
/* 285 */       if (argumentType != null) {
/* 286 */         return ImmutableArgumentMapping.<S>builder()
/* 287 */           .argumentType(argumentType)
/* 288 */           .build();
/*     */       }
/*     */     } 
/* 291 */     return ImmutableArgumentMapping.<S>builder()
/* 292 */       .argumentType((ArgumentType<?>)StringArgumentType.word())
/* 293 */       .suggestionsType(SuggestionsType.CLOUD_SUGGESTIONS)
/* 294 */       .build();
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
/*     */   private void updateExecutes(ArgumentBuilder<S, ?> builder, CommandNode<C> node, Command<S> executor) {
/* 315 */     if (this.cloudBrigadierManager.settings().get((Setting)BrigadierSetting.FORCE_EXECUTABLE) || node
/* 316 */       .isLeaf() || node
/* 317 */       .component().optional() || node
/* 318 */       .command() != null || node
/* 319 */       .children().stream().map(CommandNode::component)
/* 320 */       .filter(Objects::nonNull).anyMatch(CommandComponent::optional))
/* 321 */       builder.executes(executor); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\brigadier\node\LiteralBrigadierNodeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */