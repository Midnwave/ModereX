/*     */ package ac.grim.grimac.shaded.incendo.cloud.syntax;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.GenericTypeReflector;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.internal.CommandNode;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.aggregate.AggregateParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.flag.CommandFlag;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.flag.CommandFlagParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.permission.Permission;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ public class StandardCommandSyntaxFormatter<C>
/*     */   implements CommandSyntaxFormatter<C>
/*     */ {
/*     */   private final CommandManager<C> manager;
/*     */   
/*     */   public StandardCommandSyntaxFormatter(CommandManager<C> manager) {
/*  67 */     this.manager = manager;
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
/*     */   public final String apply(C sender, List<CommandComponent<C>> commandComponents, CommandNode<C> node) {
/*  79 */     return apply(commandComponents, node, n -> {
/*     */           if (sender == null) {
/*     */             return true;
/*     */           }
/*     */           Map<Type, Permission> accessMap = (Map<Type, Permission>)n.nodeMeta().getOrDefault(CommandNode.META_KEY_ACCESS, Collections.emptyMap());
/*     */           for (Map.Entry<Type, Permission> entry : accessMap.entrySet()) {
/*     */             if (GenericTypeReflector.isSuperType(entry.getKey(), sender.getClass()) && this.manager.testPermission(sender, entry.getValue()).allowed()) {
/*     */               return true;
/*     */             }
/*     */           } 
/*     */           return false;
/*     */         });
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
/*     */   private String apply(List<CommandComponent<C>> commandComponents, CommandNode<C> node, Predicate<CommandNode<C>> filter) {
/* 103 */     FormattingInstance formattingInstance = createInstance();
/* 104 */     Iterator<CommandComponent<C>> iterator = commandComponents.iterator();
/* 105 */     while (iterator.hasNext()) {
/* 106 */       CommandComponent<C> commandComponent = iterator.next();
/* 107 */       if (commandComponent.type() == CommandComponent.ComponentType.LITERAL) {
/* 108 */         formattingInstance.appendLiteral(commandComponent);
/* 109 */       } else if (commandComponent.parser() instanceof AggregateParser) {
/* 110 */         AggregateParser<?, ?> aggregateParser = (AggregateParser<?, ?>)commandComponent.parser();
/* 111 */         formattingInstance.appendAggregate(commandComponent, aggregateParser);
/* 112 */       } else if (commandComponent.type() == CommandComponent.ComponentType.FLAG) {
/* 113 */         formattingInstance.appendFlag((CommandFlagParser)commandComponent.parser());
/*     */       }
/* 115 */       else if (commandComponent.required()) {
/* 116 */         formattingInstance.appendRequired(commandComponent);
/*     */       } else {
/* 118 */         formattingInstance.appendOptional(commandComponent);
/*     */       } 
/*     */       
/* 121 */       if (iterator.hasNext()) {
/* 122 */         formattingInstance.appendBlankSpace();
/*     */       }
/*     */     } 
/* 125 */     CommandNode<C> tail = node;
/* 126 */     while (tail != null && !tail.isLeaf() && filter.test(tail)) {
/* 127 */       if (tail.children().size() > 1) {
/* 128 */         formattingInstance.appendBlankSpace();
/* 129 */         Iterator<CommandNode<C>> childIterator = tail.children().stream().filter(filter).iterator();
/* 130 */         while (childIterator.hasNext()) {
/* 131 */           CommandNode<C> child = childIterator.next();
/*     */           
/* 133 */           if (child.component() == null) {
/*     */             continue;
/*     */           }
/*     */           
/* 137 */           switch (child.component().type()) {
/*     */             case LITERAL:
/* 139 */               formattingInstance.appendName(child.component().name());
/*     */               break;
/*     */             case REQUIRED_VARIABLE:
/* 142 */               formattingInstance.appendRequired(child.component());
/*     */               break;
/*     */             case OPTIONAL_VARIABLE:
/* 145 */               formattingInstance.appendOptional(child.component());
/*     */               break;
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 151 */           if (childIterator.hasNext()) {
/* 152 */             formattingInstance.appendPipe();
/*     */           }
/*     */         } 
/*     */         break;
/*     */       } 
/* 157 */       if (!filter.test(tail.children().get(0))) {
/*     */         break;
/*     */       }
/* 160 */       CommandComponent<C> component = ((CommandNode)tail.children().get(0)).component();
/* 161 */       if (component.parser() instanceof AggregateParser) {
/* 162 */         AggregateParser<?, ?> aggregateParser = (AggregateParser<?, ?>)component.parser();
/* 163 */         formattingInstance.appendBlankSpace();
/* 164 */         formattingInstance.appendAggregate(component, aggregateParser);
/* 165 */       } else if (component.type() == CommandComponent.ComponentType.FLAG) {
/* 166 */         formattingInstance.appendBlankSpace();
/* 167 */         formattingInstance.appendFlag((CommandFlagParser)component.parser());
/* 168 */       } else if (component.type() == CommandComponent.ComponentType.LITERAL) {
/* 169 */         formattingInstance.appendBlankSpace();
/* 170 */         formattingInstance.appendLiteral(component);
/*     */       } else {
/* 172 */         formattingInstance.appendBlankSpace();
/* 173 */         if (component.required()) {
/* 174 */           formattingInstance.appendRequired(component);
/*     */         } else {
/* 176 */           formattingInstance.appendOptional(component);
/*     */         } 
/*     */       } 
/* 179 */       tail = tail.children().get(0);
/*     */     } 
/* 181 */     return formattingInstance.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected FormattingInstance createInstance() {
/* 190 */     return new FormattingInstance();
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
/*     */   public static class FormattingInstance
/*     */   {
/* 206 */     private final StringBuilder builder = new StringBuilder();
/*     */ 
/*     */ 
/*     */     
/*     */     public final String toString() {
/* 211 */       return this.builder.toString();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void appendLiteral(CommandComponent<?> literal) {
/* 220 */       appendName(literal.name());
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
/*     */     @API(status = API.Status.STABLE)
/*     */     public void appendAggregate(CommandComponent<?> component, AggregateParser<?, ?> parser) {
/* 234 */       String prefix = component.required() ? requiredPrefix() : optionalPrefix();
/* 235 */       String suffix = component.required() ? requiredSuffix() : optionalSuffix();
/* 236 */       this.builder.append(prefix);
/*     */       
/* 238 */       Iterator<? extends CommandComponent<?>> innerComponents = parser.components().iterator();
/* 239 */       while (innerComponents.hasNext()) {
/* 240 */         CommandComponent<?> innerComponent = innerComponents.next();
/* 241 */         this.builder.append(prefix);
/* 242 */         appendName(innerComponent.name());
/* 243 */         this.builder.append(suffix);
/* 244 */         if (innerComponents.hasNext()) {
/* 245 */           this.builder.append(' ');
/*     */         }
/*     */       } 
/* 248 */       this.builder.append(suffix);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void appendFlag(CommandFlagParser<?> flagParser) {
/* 257 */       this.builder.append(optionalPrefix());
/*     */ 
/*     */ 
/*     */       
/* 261 */       Iterator<CommandFlag<?>> flagIterator = flagParser.flags().iterator();
/*     */       
/* 263 */       while (flagIterator.hasNext()) {
/* 264 */         CommandFlag<?> flag = flagIterator.next();
/* 265 */         appendName(String.format("--%s", new Object[] { flag.name() }));
/*     */         
/* 267 */         if (flag.commandComponent() != null) {
/* 268 */           this.builder.append(' ');
/* 269 */           this.builder.append(optionalPrefix());
/* 270 */           appendName(flag.commandComponent().name());
/* 271 */           this.builder.append(optionalSuffix());
/*     */         } 
/*     */         
/* 274 */         if (flagIterator.hasNext()) {
/* 275 */           appendBlankSpace();
/* 276 */           appendPipe();
/* 277 */           appendBlankSpace();
/*     */         } 
/*     */       } 
/*     */       
/* 281 */       this.builder.append(optionalSuffix());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void appendRequired(CommandComponent<?> argument) {
/* 290 */       this.builder.append(requiredPrefix());
/* 291 */       appendName(argument.name());
/* 292 */       this.builder.append(requiredSuffix());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void appendOptional(CommandComponent<?> argument) {
/* 301 */       this.builder.append(optionalPrefix());
/* 302 */       appendName(argument.name());
/* 303 */       this.builder.append(optionalSuffix());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void appendPipe() {
/* 310 */       this.builder.append("|");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void appendName(String name) {
/* 319 */       this.builder.append(name);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String requiredPrefix() {
/* 328 */       return "<";
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String requiredSuffix() {
/* 337 */       return ">";
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String optionalPrefix() {
/* 346 */       return "[";
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String optionalSuffix() {
/* 355 */       return "]";
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void appendBlankSpace() {
/* 362 */       this.builder.append(' ');
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\syntax\StandardCommandSyntaxFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */