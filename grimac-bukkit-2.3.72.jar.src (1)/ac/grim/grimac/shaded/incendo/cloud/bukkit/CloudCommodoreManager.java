/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.SenderMapper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.CloudBrigadierManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.BukkitBackwardsBrigadierSenderMapper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.BukkitBrigadierMapper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.permission.Permission;
/*     */ import com.mojang.brigadier.CommandDispatcher;
/*     */ import com.mojang.brigadier.context.CommandContext;
/*     */ import com.mojang.brigadier.exceptions.CommandSyntaxException;
/*     */ import com.mojang.brigadier.tree.CommandNode;
/*     */ import com.mojang.brigadier.tree.LiteralCommandNode;
/*     */ import com.mojang.brigadier.tree.RootCommandNode;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.function.Function;
/*     */ import me.lucko.commodore.Commodore;
/*     */ import me.lucko.commodore.CommodoreProvider;
/*     */ import org.bukkit.command.CommandSender;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class CloudCommodoreManager<C>
/*     */   extends BukkitPluginRegistrationHandler<C>
/*     */ {
/*     */   private final BukkitCommandManager<C> commandManager;
/*     */   private final CloudBrigadierManager<C, Object> brigadierManager;
/*     */   private final Commodore commodore;
/*     */   
/*     */   CloudCommodoreManager(BukkitCommandManager<C> commandManager) {
/*  53 */     if (!CommodoreProvider.isSupported()) {
/*  54 */       throw new IllegalStateException("CommodoreProvider reports isSupported = false");
/*     */     }
/*  56 */     this.commandManager = commandManager;
/*  57 */     this.commodore = CommodoreProvider.getCommodore(commandManager.owningPlugin());
/*  58 */     this
/*     */       
/*  60 */       .brigadierManager = new CloudBrigadierManager(commandManager, SenderMapper.create(sender -> {
/*     */             CommandSender bukkitSender = getBukkitSender(sender);
/*     */ 
/*     */             
/*     */             return this.commandManager.senderMapper().map(bukkitSender);
/*  65 */           }(Function)new BukkitBackwardsBrigadierSenderMapper(this.commandManager.senderMapper())));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  70 */     BukkitBrigadierMapper<C> mapper = new BukkitBrigadierMapper(this.commandManager.owningPlugin().getLogger(), this.brigadierManager);
/*  71 */     mapper.registerBuiltInMappings();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void registerExternal(String label, Command<?> command, BukkitCommand<C> bukkitCommand) {
/*  80 */     registerWithCommodore(label, (Command)command);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void unregisterExternal(String label) {
/*  85 */     unregisterWithCommodore(label);
/*     */   }
/*     */   
/*     */   protected CloudBrigadierManager<C, Object> brigadierManager() {
/*  89 */     return this.brigadierManager;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void registerWithCommodore(String label, Command<C> command) {
/*  97 */     LiteralCommandNode<?> literalCommandNode = this.brigadierManager.literalBrigadierNodeFactory().createNode(label, command, o -> 1, (sender, commandPermission) -> (this.commandManager.commandTree().getNamedNode(label) == null) ? false : this.commandManager.testPermission(sender, commandPermission).allowed());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 105 */     CommandNode<?> existingNode = getDispatcher().findNode(Collections.singletonList(label));
/* 106 */     if (existingNode != null) {
/* 107 */       mergeChildren(existingNode, (CommandNode<?>)literalCommandNode);
/*     */     } else {
/* 109 */       this.commodore.register(literalCommandNode);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void unregisterWithCommodore(String label) {
/* 116 */     CommandDispatcher<?> dispatcher = getDispatcher();
/* 117 */     CommandNode node = dispatcher.findNode(Collections.singletonList(label));
/* 118 */     if (node == null) {
/*     */       return;
/*     */     }
/*     */     try {
/*     */       Method removeChild;
/* 123 */       Class<?> commodoreImpl = this.commodore.getClass();
/*     */ 
/*     */       
/*     */       try {
/* 127 */         removeChild = commodoreImpl.getDeclaredMethod("removeChild", new Class[] { RootCommandNode.class, String.class });
/* 128 */       } catch (NoSuchMethodException ex) {
/* 129 */         removeChild = commodoreImpl.getSuperclass().getDeclaredMethod("removeChild", new Class[] { RootCommandNode.class, String.class });
/*     */       } 
/* 131 */       removeChild.setAccessible(true);
/*     */       
/* 133 */       removeChild.invoke(null, new Object[] { dispatcher
/*     */             
/* 135 */             .getRoot(), node
/* 136 */             .getName() });
/*     */ 
/*     */       
/* 139 */       Field registeredNodesField = commodoreImpl.getDeclaredField("registeredNodes");
/* 140 */       registeredNodesField.setAccessible(true);
/*     */       
/* 142 */       List<?> registeredNodes = (List)registeredNodesField.get(this.commodore);
/* 143 */       registeredNodes.remove(node);
/* 144 */     } catch (Exception e) {
/* 145 */       throw new RuntimeException(String.format("Failed to unregister command '%s' with commodore", new Object[] { label }), e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void mergeChildren(CommandNode<?> existingNode, CommandNode<?> node) {
/* 150 */     for (CommandNode<?> child : (Iterable<CommandNode<?>>)node.getChildren()) {
/* 151 */       CommandNode<?> existingChild = existingNode.getChild(child.getName());
/* 152 */       if (existingChild == null) {
/* 153 */         existingNode.addChild(child); continue;
/*     */       } 
/* 155 */       mergeChildren(existingChild, child);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private CommandDispatcher<?> getDispatcher() {
/*     */     try {
/* 162 */       Method getDispatcherMethod = this.commodore.getClass().getDeclaredMethod("getDispatcher", new Class[0]);
/* 163 */       getDispatcherMethod.setAccessible(true);
/* 164 */       return (CommandDispatcher)getDispatcherMethod.invoke(this.commodore, new Object[0]);
/* 165 */     } catch (ReflectiveOperationException ex) {
/* 166 */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static CommandSender getBukkitSender(Object commandSourceStack) {
/* 171 */     Objects.requireNonNull(commandSourceStack, "commandSourceStack");
/*     */     try {
/* 173 */       Method getBukkitSenderMethod = commandSourceStack.getClass().getDeclaredMethod("getBukkitSender", new Class[0]);
/* 174 */       getBukkitSenderMethod.setAccessible(true);
/* 175 */       return (CommandSender)getBukkitSenderMethod.invoke(commandSourceStack, new Object[0]);
/* 176 */     } catch (ReflectiveOperationException ex) {
/* 177 */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\CloudCommodoreManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */