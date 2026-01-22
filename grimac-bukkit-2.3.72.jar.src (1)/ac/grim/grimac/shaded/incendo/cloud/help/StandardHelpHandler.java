/*     */ package ac.grim.grimac.shaded.incendo.cloud.help;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.GenericTypeReflector;
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.help.result.CommandEntry;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.help.result.HelpQueryResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.help.result.IndexCommandResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.help.result.MultipleCommandResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.help.result.VerboseCommandResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.internal.CommandInputTokenizer;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.internal.CommandNode;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Set;
/*     */ import java.util.function.Predicate;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public class StandardHelpHandler<C>
/*     */   implements HelpHandler<C>
/*     */ {
/*     */   private final CommandManager<C> commandManager;
/*     */   private final CommandPredicate<C> commandFilter;
/*     */   
/*     */   public StandardHelpHandler(CommandManager<C> commandManager, CommandPredicate<C> commandPredicate) {
/*  63 */     this.commandManager = commandManager;
/*  64 */     this.commandFilter = commandPredicate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HelpQueryResult<C> query(HelpQuery<C> query) {
/*  72 */     List<CommandEntry<C>> commands = commands(query.sender());
/*     */ 
/*     */     
/*  75 */     if (query.query().replace(" ", "").isEmpty()) {
/*  76 */       return (HelpQueryResult<C>)IndexCommandResult.of(query, commands);
/*     */     }
/*     */     
/*  79 */     List<String> queryFragments = (new CommandInputTokenizer(query.query())).tokenize();
/*  80 */     String rootFragment = queryFragments.get(0);
/*     */ 
/*     */     
/*  83 */     List<Command<C>> availableCommands = new LinkedList<>();
/*  84 */     Set<String> availableCommandLabels = new HashSet<>();
/*     */     
/*  86 */     boolean exactMatch = false;
/*     */     
/*  88 */     for (CommandEntry<C> entry : commands) {
/*  89 */       Command<C> command = entry.command();
/*     */       
/*  91 */       CommandComponent<C> component = command.rootComponent();
/*  92 */       for (String alias : component.aliases()) {
/*  93 */         if (alias.toLowerCase(Locale.ENGLISH).startsWith(rootFragment.toLowerCase(Locale.ENGLISH))) {
/*  94 */           availableCommands.add(command);
/*  95 */           availableCommandLabels.add(component.name());
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 100 */       for (String alias : component.aliases()) {
/* 101 */         if (alias.equalsIgnoreCase(rootFragment)) {
/* 102 */           exactMatch = true;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 107 */       if (rootFragment.equalsIgnoreCase(component.name())) {
/* 108 */         availableCommandLabels.clear();
/* 109 */         availableCommands.clear();
/* 110 */         availableCommandLabels.add(component.name());
/* 111 */         availableCommands.add(command);
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/*     */     
/* 117 */     if (availableCommands.isEmpty())
/* 118 */       return (HelpQueryResult<C>)IndexCommandResult.of(query, Collections.emptyList()); 
/* 119 */     if (!exactMatch || availableCommandLabels.size() > 1) {
/* 120 */       return (HelpQueryResult<C>)IndexCommandResult.of(query, (List)availableCommands
/*     */           
/* 122 */           .stream()
/* 123 */           .map(command -> CommandEntry.of(command, this.commandManager.commandSyntaxFormatter().apply(query.sender(), command.components(), null)))
/*     */           
/* 125 */           .sorted()
/* 126 */           .filter(entry -> isAllowed(query.sender(), entry.command()))
/* 127 */           .collect(Collectors.toList()));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 133 */     CommandNode<C> node = this.commandManager.commandTree().getNamedNode(availableCommandLabels.iterator().next());
/*     */     
/* 135 */     List<CommandComponent<C>> traversedNodes = new LinkedList<>();
/* 136 */     CommandNode<C> head = node;
/* 137 */     int index = 0;
/*     */ 
/*     */     
/* 140 */     while (head != null && isNodeVisible(head)) {
/* 141 */       index++;
/* 142 */       traversedNodes.add(head.component());
/*     */       
/* 144 */       if (head.component() != null && head.command() != null && (
/* 145 */         head.isLeaf() || index == queryFragments.size()) && 
/* 146 */         isAllowed(query.sender(), head.command())) {
/* 147 */         return (HelpQueryResult<C>)VerboseCommandResult.of(query, 
/*     */             
/* 149 */             CommandEntry.of(head
/* 150 */               .command(), this.commandManager
/* 151 */               .commandSyntaxFormatter()
/* 152 */               .apply(query.sender(), head.command().components(), null)));
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 159 */       if (head.children().size() == 1) {
/* 160 */         head = head.children().get(0); continue;
/*     */       } 
/* 162 */       if (index < queryFragments.size()) {
/*     */         
/* 164 */         CommandNode<C> potentialVariable = null;
/* 165 */         for (CommandNode<C> child : (Iterable<CommandNode<C>>)head.children()) {
/* 166 */           if (child.component() == null || child.component().type() != CommandComponent.ComponentType.LITERAL) {
/* 167 */             if (child.component() != null) {
/* 168 */               potentialVariable = child;
/*     */             }
/*     */             continue;
/*     */           } 
/* 172 */           for (String childAlias : child.component().aliases()) {
/* 173 */             if (childAlias.equalsIgnoreCase(queryFragments.get(index))) {
/* 174 */               head = child;
/*     */             }
/*     */           } 
/*     */         } 
/*     */         
/* 179 */         if (potentialVariable != null) {
/* 180 */           head = potentialVariable;
/*     */           
/*     */           continue;
/*     */         } 
/*     */       } 
/* 185 */       String currentDescription = this.commandManager.commandSyntaxFormatter().apply(query.sender(), traversedNodes, null);
/*     */       
/* 187 */       List<String> childSuggestions = new LinkedList<>();
/* 188 */       for (CommandNode<C> child : (Iterable<CommandNode<C>>)head.children()) {
/*     */         
/* 190 */         if (!isNodeVisible(child)) {
/*     */           continue;
/*     */         }
/*     */         
/* 194 */         List<CommandComponent<C>> traversedNodesSub = new LinkedList<>(traversedNodes);
/* 195 */         if (child.component() == null || child.command() == null || 
/* 196 */           isAllowed(query.sender(), child.command())) {
/*     */           
/* 198 */           traversedNodesSub.add(child.component());
/* 199 */           childSuggestions.add(this.commandManager.commandSyntaxFormatter()
/* 200 */               .apply(query.sender(), traversedNodesSub, child));
/*     */         } 
/*     */       } 
/* 203 */       return (HelpQueryResult<C>)MultipleCommandResult.of(query, currentDescription, childSuggestions);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 208 */     return (HelpQueryResult<C>)IndexCommandResult.of(query, Collections.emptyList());
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
/*     */   protected List<CommandEntry<C>> commands(C sender) {
/* 220 */     return (List<CommandEntry<C>>)this.commandManager.commands()
/* 221 */       .stream()
/* 222 */       .filter((Predicate<? super C>)this.commandFilter)
/* 223 */       .filter(command -> isAllowed((C)sender, command))
/* 224 */       .map(command -> CommandEntry.of(command, this.commandManager.commandSyntaxFormatter().apply(sender, command.components(), null)))
/*     */ 
/*     */ 
/*     */       
/* 228 */       .sorted()
/* 229 */       .collect(Collectors.toList());
/*     */   }
/*     */   
/*     */   private boolean isAllowed(C sender, Command<C> command) {
/* 233 */     if (command.senderType().isPresent() && 
/* 234 */       !GenericTypeReflector.isSuperType(((TypeToken)command.senderType().get()).getType(), sender.getClass())) {
/* 235 */       return false;
/*     */     }
/*     */     
/* 238 */     return this.commandManager.testPermission(sender, command.commandPermission()).allowed();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isNodeVisible(CommandNode<C> node) {
/* 249 */     CommandComponent<C> component = node.component();
/* 250 */     if (component != null) {
/* 251 */       Command<C> owningCommand = node.command();
/* 252 */       if (owningCommand != null && this.commandFilter.test(owningCommand)) {
/* 253 */         return true;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 258 */     for (CommandNode<C> childNode : (Iterable<CommandNode<C>>)node.children()) {
/* 259 */       if (isNodeVisible(childNode)) {
/* 260 */         return true;
/*     */       }
/*     */     } 
/*     */     
/* 264 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\help\StandardHelpHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */