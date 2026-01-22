/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TextComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.ParsingExceptionImpl;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.Token;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.TokenParser;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.TokenType;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.node.ElementNode;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.node.RootNode;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.node.TagNode;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.node.ValueNode;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Inserting;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Modifying;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tree.Node;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examiner;
/*     */ import ac.grim.grimac.shaded.kyori.examination.string.MultiLineStringExaminer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Predicate;
/*     */ import java.util.stream.Collectors;
/*     */ import java.util.stream.Stream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class MiniMessageParser
/*     */ {
/*     */   final TagResolver tagResolver;
/*     */   
/*     */   MiniMessageParser() {
/*  55 */     this.tagResolver = TagResolver.standard();
/*     */   }
/*     */   
/*     */   MiniMessageParser(TagResolver tagResolver) {
/*  59 */     this.tagResolver = tagResolver;
/*     */   }
/*     */   @NotNull
/*     */   String escapeTokens(@NotNull ContextImpl context) {
/*  63 */     StringBuilder sb = new StringBuilder(context.message().length());
/*  64 */     escapeTokens(sb, context);
/*  65 */     return sb.toString();
/*     */   }
/*     */   
/*     */   void escapeTokens(StringBuilder sb, @NotNull ContextImpl context) {
/*  69 */     escapeTokens(sb, context.message(), context);
/*     */   }
/*     */   
/*     */   private void escapeTokens(StringBuilder sb, String richMessage, ContextImpl context) {
/*  73 */     processTokens(sb, richMessage, context, (token, builder) -> {
/*     */           builder.append('\\').append('<');
/*     */           if (token.type() == TokenType.CLOSE_TAG) {
/*     */             builder.append('/');
/*     */           }
/*     */           List<Token> childTokens = token.childTokens();
/*     */           for (int i = 0; i < childTokens.size(); i++) {
/*     */             if (i != 0)
/*     */               builder.append(':'); 
/*     */             escapeTokens(builder, ((Token)childTokens.get(i)).get(richMessage).toString(), context);
/*     */           } 
/*     */           builder.append('>');
/*     */         });
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   String stripTokens(@NotNull ContextImpl context) {
/*  90 */     StringBuilder sb = new StringBuilder(context.message().length());
/*  91 */     processTokens(sb, context, (token, builder) -> { 
/*  92 */         }); return sb.toString();
/*     */   }
/*     */   
/*     */   private void processTokens(@NotNull StringBuilder sb, @NotNull ContextImpl context, BiConsumer<Token, StringBuilder> tagHandler) {
/*  96 */     processTokens(sb, context.message(), context, tagHandler);
/*     */   }
/*     */   
/*     */   private void processTokens(@NotNull StringBuilder sb, @NotNull String richMessage, @NotNull ContextImpl context, BiConsumer<Token, StringBuilder> tagHandler) {
/* 100 */     TagResolver combinedResolver = TagResolver.resolver(new TagResolver[] { this.tagResolver, context.extraTags() });
/* 101 */     List<Token> root = TokenParser.tokenize(richMessage, true);
/* 102 */     for (Token token : root) {
/* 103 */       String sanitized; switch (token.type()) {
/*     */         case TEXT:
/* 105 */           sb.append(richMessage, token.startIndex(), token.endIndex());
/*     */           continue;
/*     */         
/*     */         case OPEN_TAG:
/*     */         case CLOSE_TAG:
/*     */         case OPEN_CLOSE_TAG:
/* 111 */           if (token.childTokens().isEmpty()) {
/* 112 */             sb.append(richMessage, token.startIndex(), token.endIndex());
/*     */             continue;
/*     */           } 
/* 115 */           sanitized = TokenParser.TagProvider.sanitizePlaceholderName(((Token)token.childTokens().get(0)).get(richMessage).toString());
/* 116 */           if (combinedResolver.has(sanitized)) {
/* 117 */             tagHandler.accept(token, sb); continue;
/*     */           } 
/* 119 */           sb.append(richMessage, token.startIndex(), token.endIndex());
/*     */           continue;
/*     */       } 
/*     */       
/* 123 */       throw new IllegalArgumentException("Unsupported token type " + token.type());
/*     */     } 
/*     */   }
/*     */   @NotNull
/*     */   RootNode parseToTree(@NotNull ContextImpl context) {
/*     */     TokenParser.TagProvider transformationFactory;
/* 129 */     TagResolver combinedResolver = TagResolver.resolver(new TagResolver[] { this.tagResolver, context.extraTags() });
/* 130 */     String processedMessage = context.preProcessor().apply(context.message());
/* 131 */     Consumer<String> debug = context.debugOutput();
/* 132 */     if (debug != null) {
/* 133 */       debug.accept("Beginning parsing message ");
/* 134 */       debug.accept(processedMessage);
/* 135 */       debug.accept("\n");
/*     */     } 
/*     */ 
/*     */     
/* 139 */     if (debug != null) {
/* 140 */       transformationFactory = ((name, args, token) -> {
/*     */           try {
/*     */             debug.accept("Attempting to match node '");
/*     */             
/*     */             debug.accept(name);
/*     */             
/*     */             debug.accept("'");
/*     */             
/*     */             if (token != null) {
/*     */               debug.accept(" at column ");
/*     */               debug.accept(String.valueOf(token.startIndex()));
/*     */             } 
/*     */             debug.accept("\n");
/*     */             Tag transformation = combinedResolver.resolve(name, new ArgumentQueueImpl<>(context, args), context);
/*     */             if (transformation == null) {
/*     */               debug.accept("Could not match node '");
/*     */               debug.accept(name);
/*     */               debug.accept("'\n");
/*     */             } else {
/*     */               debug.accept("Successfully matched node '");
/*     */               debug.accept(name);
/*     */               debug.accept("' to tag ");
/*     */               debug.accept((transformation instanceof Examinable) ? ((Examinable)transformation).examinableName() : transformation.getClass().getName());
/*     */               debug.accept("\n");
/*     */             } 
/*     */             return transformation;
/* 166 */           } catch (ParsingException e) {
/*     */             if (token != null && e instanceof ParsingExceptionImpl) {
/*     */               ParsingExceptionImpl impl = (ParsingExceptionImpl)e;
/*     */               if ((impl.tokens()).length == 0) {
/*     */                 impl.tokens(new Token[] { token });
/*     */               }
/*     */             } 
/*     */             debug.accept("Could not match node '");
/*     */             debug.accept(name);
/*     */             debug.accept("' - ");
/*     */             debug.accept(e.getMessage());
/*     */             debug.accept("\n");
/*     */             return null;
/*     */           } 
/*     */         });
/*     */     } else {
/* 182 */       transformationFactory = ((name, args, token) -> {
/*     */           try {
/*     */             return combinedResolver.resolve(name, new ArgumentQueueImpl<>(context, args), context);
/* 185 */           } catch (ParsingException ignored) {
/*     */             return null;
/*     */           } 
/*     */         });
/*     */     } 
/* 190 */     Predicate<String> tagNameChecker = name -> {
/*     */         String sanitized = TokenParser.TagProvider.sanitizePlaceholderName(name);
/*     */         
/*     */         return combinedResolver.has(sanitized);
/*     */       };
/* 195 */     String preProcessed = TokenParser.resolvePreProcessTags(processedMessage, transformationFactory);
/* 196 */     context.message(preProcessed);
/*     */     
/* 198 */     RootNode root = TokenParser.parse(transformationFactory, tagNameChecker, preProcessed, processedMessage, context.strict());
/*     */     
/* 200 */     if (debug != null) {
/* 201 */       debug.accept("Text parsed into element tree:\n");
/* 202 */       debug.accept(root.toString());
/*     */     } 
/*     */     
/* 205 */     return root;
/*     */   }
/*     */   @NotNull
/*     */   Component parseFormat(@NotNull ContextImpl context) {
/* 209 */     RootNode rootNode = parseToTree(context);
/* 210 */     return Objects.<Component>requireNonNull(context.postProcessor().apply(treeToComponent((ElementNode)rootNode, context)), "Post-processor must not return null");
/*     */   } @NotNull
/*     */   Component treeToComponent(@NotNull ElementNode node, @NotNull ContextImpl context) {
/*     */     Component component;
/* 214 */     TextComponent textComponent = Component.empty();
/* 215 */     Tag tag = null;
/* 216 */     if (node instanceof ValueNode) {
/* 217 */       textComponent = Component.text(((ValueNode)node).value());
/* 218 */     } else if (node instanceof TagNode) {
/* 219 */       TagNode tagNode = (TagNode)node;
/*     */       
/* 221 */       tag = tagNode.tag();
/*     */ 
/*     */       
/* 224 */       if (tag instanceof Modifying) {
/* 225 */         Modifying modTransformation = (Modifying)tag;
/*     */ 
/*     */         
/* 228 */         visitModifying(modTransformation, (ElementNode)tagNode, 0);
/* 229 */         modTransformation.postVisit();
/*     */       } 
/*     */       
/* 232 */       if (tag instanceof Inserting) {
/* 233 */         component = ((Inserting)tag).value();
/*     */       }
/*     */     } 
/*     */     
/* 237 */     if (!node.unsafeChildren().isEmpty()) {
/* 238 */       List<Component> children = new ArrayList<>(component.children().size() + node.children().size());
/* 239 */       children.addAll(component.children());
/* 240 */       for (ElementNode child : node.unsafeChildren()) {
/* 241 */         children.add(treeToComponent(child, context));
/*     */       }
/* 243 */       component = component.children(children);
/*     */     } 
/*     */ 
/*     */     
/* 247 */     if (tag instanceof Modifying) {
/* 248 */       component = handleModifying((Modifying)tag, component, 0);
/*     */     }
/*     */     
/* 251 */     Consumer<String> debug = context.debugOutput();
/* 252 */     if (debug != null) {
/* 253 */       debug.accept("==========\ntreeToComponent \n");
/* 254 */       debug.accept(node.toString());
/* 255 */       debug.accept("\n");
/* 256 */       debug.accept(((Stream<CharSequence>)component.examine((Examiner)MultiLineStringExaminer.simpleEscaping())).collect(Collectors.joining("\n")));
/* 257 */       debug.accept("\n==========\n");
/*     */     } 
/*     */     
/* 260 */     return component;
/*     */   }
/*     */   
/*     */   private void visitModifying(Modifying modTransformation, ElementNode node, int depth) {
/* 264 */     modTransformation.visit((Node)node, depth);
/* 265 */     for (ElementNode child : node.unsafeChildren()) {
/* 266 */       visitModifying(modTransformation, child, depth + 1);
/*     */     }
/*     */   }
/*     */   
/*     */   private Component handleModifying(Modifying modTransformation, Component current, int depth) {
/* 271 */     Component newComp = modTransformation.apply(current, depth);
/* 272 */     for (Component child : current.children()) {
/* 273 */       newComp = newComp.append(handleModifying(modTransformation, child, depth + 1));
/*     */     }
/* 275 */     return newComp;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\MiniMessageParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */