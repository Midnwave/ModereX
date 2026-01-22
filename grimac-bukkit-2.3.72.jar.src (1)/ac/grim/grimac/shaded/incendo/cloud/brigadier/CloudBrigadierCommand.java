/*     */ package ac.grim.grimac.shaded.incendo.cloud.brigadier;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.tuple.Pair;
/*     */ import com.mojang.brigadier.Command;
/*     */ import com.mojang.brigadier.context.CommandContext;
/*     */ import com.mojang.brigadier.context.ParsedCommandNode;
/*     */ import com.mojang.brigadier.context.StringRange;
/*     */ import com.mojang.brigadier.tree.CommandNode;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Function;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.INTERNAL)
/*     */ public final class CloudBrigadierCommand<C, S>
/*     */   implements Command<S>
/*     */ {
/*     */   private final CommandManager<C> commandManager;
/*     */   private final CloudBrigadierManager<C, S> brigadierManager;
/*     */   private final Function<String, String> inputMapper;
/*     */   
/*     */   public CloudBrigadierCommand(CommandManager<C> commandManager, CloudBrigadierManager<C, S> brigadierManager) {
/*  64 */     this.commandManager = commandManager;
/*  65 */     this.brigadierManager = brigadierManager;
/*  66 */     this.inputMapper = Function.identity();
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
/*     */   public CloudBrigadierCommand(CommandManager<C> commandManager, CloudBrigadierManager<C, S> brigadierManager, Function<String, String> inputMapper) {
/*  81 */     this.commandManager = commandManager;
/*  82 */     this.brigadierManager = brigadierManager;
/*  83 */     this.inputMapper = inputMapper;
/*     */   }
/*     */ 
/*     */   
/*     */   public int run(CommandContext<S> ctx) {
/*  88 */     S source = (S)ctx.getSource();
/*  89 */     String input = this.inputMapper.apply(ctx
/*  90 */         .getInput().substring(((StringRange)((Pair)parsedNodes(ctx.getLastChild()).get(0)).second()).getStart()));
/*     */     
/*  92 */     C sender = (C)this.brigadierManager.senderMapper().map(source);
/*     */     
/*  94 */     this.commandManager.commandExecutor().executeCommand(sender, input, cloudContext -> cloudContext.store("_cloud_brigadier_native_sender", source));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  99 */     return 1;
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
/*     */   public static <S> List<Pair<CommandNode<S>, StringRange>> parsedNodes(CommandContext<S> commandContext) {
/*     */     try {
/* 115 */       Method getNodesMethod = commandContext.getClass().getDeclaredMethod("getNodes", new Class[0]);
/* 116 */       Object nodes = getNodesMethod.invoke(commandContext, new Object[0]);
/* 117 */       if (nodes instanceof List)
/* 118 */         return ParsedCommandNodeHandler.toPairList((List)nodes); 
/* 119 */       if (nodes instanceof Map) {
/* 120 */         return (List<Pair<CommandNode<S>, StringRange>>)((Map)nodes).entrySet().stream()
/* 121 */           .map(entry -> Pair.of(entry.getKey(), entry.getValue()))
/* 122 */           .collect(Collectors.toList());
/*     */       }
/* 124 */       throw new IllegalStateException();
/*     */     }
/* 126 */     catch (ReflectiveOperationException ex) {
/* 127 */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class ParsedCommandNodeHandler
/*     */   {
/*     */     private static <S> List<Pair<CommandNode<S>, StringRange>> toPairList(List<?> nodes) {
/* 140 */       return (List<Pair<CommandNode<S>, StringRange>>)nodes.stream()
/* 141 */         .map(n -> Pair.of(n.getNode(), n.getRange()))
/* 142 */         .collect(Collectors.toList());
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\brigadier\CloudBrigadierCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */