/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.ParsingException;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.TagInternals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.match.MatchedTokenConsumer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.match.StringResolvingMatchedTokenConsumer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.match.TokenListProducingMatchedTokenConsumer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.node.ElementNode;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.node.RootNode;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.node.TagNode;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.node.TagPart;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.parser.node.TextNode;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Inserting;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.ParserDirective;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Locale;
/*     */ import java.util.function.IntPredicate;
/*     */ import java.util.function.Predicate;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Internal
/*     */ public final class TokenParser
/*     */ {
/*     */   private static final int MAX_DEPTH = 16;
/*     */   public static final char TAG_START = '<';
/*     */   public static final char TAG_END = '>';
/*     */   public static final char CLOSE_TAG = '/';
/*     */   public static final char SEPARATOR = ':';
/*     */   public static final char ESCAPE = '\\';
/*     */   
/*     */   public static RootNode parse(@NotNull TagProvider tagProvider, @NotNull Predicate<String> tagNameChecker, @NotNull String message, @NotNull String originalMessage, boolean strict) throws ParsingException {
/*  89 */     List<Token> tokens = tokenize(message, false);
/*     */ 
/*     */     
/*  92 */     return buildTree(tagProvider, tagNameChecker, tokens, message, originalMessage, strict);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String resolvePreProcessTags(String message, TagProvider provider) {
/*     */     String lastResult;
/* 104 */     int passes = 0;
/*     */     
/* 106 */     String result = message;
/*     */     
/*     */     do {
/* 109 */       lastResult = result;
/* 110 */       StringResolvingMatchedTokenConsumer stringTokenResolver = new StringResolvingMatchedTokenConsumer(lastResult, provider);
/*     */       
/* 112 */       parseString(lastResult, false, (MatchedTokenConsumer<?>)stringTokenResolver);
/* 113 */       result = stringTokenResolver.result();
/* 114 */       ++passes;
/* 115 */     } while (passes < 16 && !lastResult.equals(result));
/*     */     
/* 117 */     return lastResult;
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
/*     */   public static List<Token> tokenize(String message, boolean lenient) {
/* 129 */     TokenListProducingMatchedTokenConsumer listProducer = new TokenListProducingMatchedTokenConsumer(message);
/* 130 */     parseString(message, lenient, (MatchedTokenConsumer<?>)listProducer);
/* 131 */     List<Token> tokens = listProducer.result();
/* 132 */     parseSecondPass(message, tokens);
/* 133 */     return tokens;
/*     */   }
/*     */   
/*     */   enum FirstPassState {
/* 137 */     NORMAL,
/* 138 */     TAG,
/* 139 */     STRING;
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
/*     */   public static void parseString(String message, boolean lenient, MatchedTokenConsumer<?> consumer) {
/* 151 */     FirstPassState state = FirstPassState.NORMAL;
/*     */     
/* 153 */     boolean escaped = false;
/*     */     
/* 155 */     int currentTokenEnd = 0;
/*     */     
/* 157 */     int marker = -1;
/* 158 */     char currentStringChar = Character.MIN_VALUE;
/*     */     
/* 160 */     int length = message.length();
/* 161 */     for (int i = 0; i < length; i++) {
/* 162 */       TokenType thisType; int codePoint = message.codePointAt(i);
/* 163 */       if (!lenient && codePoint == 167 && i + 1 < length) {
/* 164 */         int nextChar = Character.toLowerCase(message.codePointAt(i + 1));
/*     */         
/* 166 */         if ((nextChar >= 48 && nextChar <= 57) || (nextChar >= 97 && nextChar <= 102) || nextChar == 114 || (nextChar >= 107 && nextChar <= 111))
/*     */         {
/*     */ 
/*     */           
/* 170 */           throw new ParsingExceptionImpl("Legacy formatting codes have been detected in a MiniMessage string - this is unsupported behaviour. Please refer to the Adventure documentation (https://docs.advntr.dev) for more information.", message, null, true, new Token[] { new Token(i, i + 2, TokenType.TEXT) });
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 180 */       if (!Character.isBmpCodePoint(codePoint)) {
/* 181 */         i++;
/*     */       }
/* 183 */       if (!escaped) {
/*     */         
/* 185 */         if (codePoint == 92 && i + 1 < message.length()) {
/* 186 */           int nextCodePoint = message.codePointAt(i + 1);
/*     */           
/* 188 */           switch (state) {
/*     */             
/*     */             case TEXT:
/* 191 */               escaped = (nextCodePoint == 60 || nextCodePoint == 92);
/*     */               break;
/*     */             
/*     */             case OPEN_TAG:
/* 195 */               escaped = (currentStringChar == nextCodePoint || nextCodePoint == 92);
/*     */               break;
/*     */             
/*     */             case OPEN_CLOSE_TAG:
/* 199 */               if (nextCodePoint == 60) {
/* 200 */                 escaped = true;
/* 201 */                 state = FirstPassState.NORMAL;
/*     */               } 
/*     */               break;
/*     */           } 
/*     */ 
/*     */           
/* 207 */           if (escaped) {
/*     */             continue;
/*     */           }
/*     */         } 
/*     */       } else {
/* 212 */         escaped = false;
/*     */         
/*     */         continue;
/*     */       } 
/* 216 */       switch (state) {
/*     */         case TEXT:
/* 218 */           if (codePoint == 60) {
/*     */             
/* 220 */             marker = i;
/* 221 */             state = FirstPassState.TAG;
/*     */           } 
/*     */           break;
/*     */         case OPEN_CLOSE_TAG:
/* 225 */           switch (codePoint) {
/*     */             case 62:
/* 227 */               if (i == marker + 1) {
/*     */                 
/* 229 */                 state = FirstPassState.NORMAL;
/*     */                 
/*     */                 break;
/*     */               } 
/*     */               
/* 234 */               if (currentTokenEnd != marker)
/*     */               {
/* 236 */                 consumer.accept(currentTokenEnd, marker, TokenType.TEXT);
/*     */               }
/* 238 */               currentTokenEnd = i + 1;
/*     */ 
/*     */               
/* 241 */               thisType = TokenType.OPEN_TAG;
/* 242 */               if (boundsCheck(message, marker, 1) && message.charAt(marker + 1) == '/') {
/* 243 */                 thisType = TokenType.CLOSE_TAG;
/* 244 */               } else if (boundsCheck(message, marker, 2) && message.charAt(i - 1) == '/') {
/* 245 */                 thisType = TokenType.OPEN_CLOSE_TAG;
/*     */               } 
/* 247 */               consumer.accept(marker, currentTokenEnd, thisType);
/* 248 */               state = FirstPassState.NORMAL;
/*     */               break;
/*     */             
/*     */             case 60:
/* 252 */               marker = i;
/*     */               break;
/*     */             case 34:
/*     */             case 39:
/* 256 */               currentStringChar = (char)codePoint;
/*     */               
/* 258 */               if (message.indexOf(codePoint, i + 1) != -1) {
/* 259 */                 state = FirstPassState.STRING;
/*     */               }
/*     */               break;
/*     */           } 
/*     */           break;
/*     */         case OPEN_TAG:
/* 265 */           if (codePoint == currentStringChar) {
/* 266 */             state = FirstPassState.TAG;
/*     */           }
/*     */           break;
/*     */       } 
/*     */       
/* 271 */       if (i == length - 1 && state == FirstPassState.TAG) {
/*     */ 
/*     */ 
/*     */         
/* 275 */         i = marker;
/* 276 */         state = FirstPassState.NORMAL;
/*     */       } 
/*     */       
/*     */       continue;
/*     */     } 
/* 281 */     int end = consumer.lastEndIndex();
/* 282 */     if (end == -1) {
/* 283 */       consumer.accept(0, message.length(), TokenType.TEXT);
/* 284 */     } else if (end != message.length()) {
/* 285 */       consumer.accept(end, message.length(), TokenType.TEXT);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void parseSecondPass(String message, List<Token> tokens) {
/* 294 */     for (Token token : tokens) {
/* 295 */       TokenType type = token.type();
/* 296 */       if (type != TokenType.OPEN_TAG && type != TokenType.OPEN_CLOSE_TAG && type != TokenType.CLOSE_TAG) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */       
/* 301 */       int startIndex = (type == TokenType.CLOSE_TAG) ? (token.startIndex() + 2) : (token.startIndex() + 1);
/* 302 */       int endIndex = (type == TokenType.OPEN_CLOSE_TAG) ? (token.endIndex() - 2) : (token.endIndex() - 1);
/*     */       
/* 304 */       SecondPassState state = SecondPassState.NORMAL;
/* 305 */       boolean escaped = false;
/* 306 */       char currentStringChar = Character.MIN_VALUE;
/*     */ 
/*     */       
/* 309 */       int marker = startIndex;
/*     */       
/* 311 */       for (int i = startIndex; i < endIndex; i++) {
/* 312 */         int codePoint = message.codePointAt(i);
/* 313 */         if (!Character.isBmpCodePoint(i)) {
/* 314 */           i++;
/*     */         }
/*     */         
/* 317 */         if (!escaped) {
/*     */           
/* 319 */           if (codePoint == 92 && i + 1 < message.length()) {
/* 320 */             int nextCodePoint = message.codePointAt(i + 1);
/*     */             
/* 322 */             switch (state) {
/*     */               
/*     */               case TEXT:
/* 325 */                 escaped = (nextCodePoint == 60 || nextCodePoint == 92);
/*     */                 break;
/*     */               
/*     */               case OPEN_TAG:
/* 329 */                 escaped = (currentStringChar == nextCodePoint || nextCodePoint == 92);
/*     */                 break;
/*     */             } 
/*     */ 
/*     */             
/* 334 */             if (escaped) {
/*     */               continue;
/*     */             }
/*     */           } 
/*     */         } else {
/* 339 */           escaped = false;
/*     */           
/*     */           continue;
/*     */         } 
/* 343 */         switch (state) {
/*     */           
/*     */           case TEXT:
/* 346 */             if (codePoint == 58) {
/* 347 */               if (boundsCheck(message, i, 2) && message.charAt(i + 1) == '/' && message.charAt(i + 2) == '/') {
/*     */                 break;
/*     */               }
/* 350 */               if (marker == i) {
/*     */                 
/* 352 */                 insert(token, new Token(i, i, TokenType.TAG_VALUE));
/* 353 */                 marker++; break;
/*     */               } 
/* 355 */               insert(token, new Token(marker, i, TokenType.TAG_VALUE));
/* 356 */               marker = i + 1; break;
/*     */             } 
/* 358 */             if (codePoint == 39 || codePoint == 34) {
/* 359 */               state = SecondPassState.STRING;
/* 360 */               currentStringChar = (char)codePoint;
/*     */             } 
/*     */             break;
/*     */           case OPEN_TAG:
/* 364 */             if (codePoint == currentStringChar) {
/* 365 */               state = SecondPassState.NORMAL;
/*     */             }
/*     */             break;
/*     */         } 
/*     */         
/*     */         continue;
/*     */       } 
/* 372 */       if (token.childTokens() == null || token.childTokens().isEmpty()) {
/* 373 */         insert(token, new Token(startIndex, endIndex, TokenType.TAG_VALUE)); continue;
/*     */       } 
/* 375 */       int end = ((Token)token.childTokens().get(token.childTokens().size() - 1)).endIndex();
/* 376 */       if (end != endIndex) {
/* 377 */         insert(token, new Token(end + 1, endIndex, TokenType.TAG_VALUE));
/*     */       }
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
/*     */   private static RootNode buildTree(@NotNull TagProvider tagProvider, @NotNull Predicate<String> tagNameChecker, @NotNull List<Token> tokens, @NotNull String message, @NotNull String originalMessage, boolean strict) throws ParsingException {
/*     */     ElementNode elementNode;
/* 394 */     RootNode root = new RootNode(message, originalMessage);
/* 395 */     RootNode rootNode1 = root;
/*     */     
/* 397 */     for (Token token : tokens) {
/* 398 */       TagNode tagNode1; Token tagNamePart; String tagName; TagNode tagNode; List<Token> childTokens; ArrayList<String> closeValues; String closeTagName; TagNode tagNode2; ElementNode elementNode1; TokenType type = token.type();
/* 399 */       switch (type) {
/*     */         case TEXT:
/* 401 */           rootNode1.addChild((ElementNode)new TextNode((ElementNode)rootNode1, token, message));
/*     */ 
/*     */ 
/*     */         
/*     */         case OPEN_TAG:
/*     */         case OPEN_CLOSE_TAG:
/* 407 */           tagNamePart = token.childTokens().get(0);
/* 408 */           tagName = message.substring(tagNamePart.startIndex(), tagNamePart.endIndex());
/* 409 */           if (!TagInternals.sanitizeAndCheckValidTagName(tagName)) {
/*     */             
/* 411 */             rootNode1.addChild((ElementNode)new TextNode((ElementNode)rootNode1, token, message));
/*     */             
/*     */             continue;
/*     */           } 
/* 415 */           tagNode = new TagNode((ElementNode)rootNode1, token, message, tagProvider);
/* 416 */           if (tagNameChecker.test(tagNode.name())) {
/* 417 */             Tag tag = tagProvider.resolve(tagNode);
/* 418 */             if (tag == null) {
/*     */ 
/*     */               
/* 421 */               rootNode1.addChild((ElementNode)new TextNode((ElementNode)rootNode1, token, message)); continue;
/* 422 */             }  if (tag == ParserDirective.RESET) {
/*     */ 
/*     */               
/* 425 */               if (strict) {
/* 426 */                 throw new ParsingExceptionImpl("<reset> tags are not allowed when strict mode is enabled", message, new Token[] { token });
/*     */               }
/* 428 */               rootNode1 = root;
/*     */               continue;
/*     */             } 
/* 431 */             tagNode.tag(tag);
/* 432 */             rootNode1.addChild((ElementNode)tagNode);
/* 433 */             if (type != TokenType.OPEN_CLOSE_TAG && (!(tag instanceof Inserting) || ((Inserting)tag).allowsChildren())) {
/* 434 */               tagNode1 = tagNode;
/*     */             }
/*     */             
/*     */             continue;
/*     */           } 
/* 439 */           tagNode1.addChild((ElementNode)new TextNode((ElementNode)tagNode1, token, message));
/*     */ 
/*     */ 
/*     */         
/*     */         case CLOSE_TAG:
/* 444 */           childTokens = token.childTokens();
/* 445 */           if (childTokens.isEmpty()) {
/* 446 */             throw new IllegalStateException("CLOSE_TAG token somehow has no children - the parser should not allow this. Original text: " + message);
/*     */           }
/*     */ 
/*     */           
/* 450 */           closeValues = new ArrayList<>(childTokens.size());
/* 451 */           for (Token childToken : childTokens) {
/* 452 */             closeValues.add(TagPart.unquoteAndEscape(message, childToken.startIndex(), childToken.endIndex()));
/*     */           }
/*     */           
/* 455 */           closeTagName = closeValues.get(0);
/*     */           
/* 457 */           if (tagNameChecker.test(closeTagName)) {
/* 458 */             Tag tag = tagProvider.resolve(closeTagName);
/*     */             
/* 460 */             if (tag == ParserDirective.RESET) {
/*     */               continue;
/*     */             }
/*     */           }
/*     */           else {
/*     */             
/* 466 */             tagNode1.addChild((ElementNode)new TextNode((ElementNode)tagNode1, token, message));
/*     */             
/*     */             continue;
/*     */           } 
/* 470 */           tagNode2 = tagNode1;
/* 471 */           while (tagNode2 instanceof TagNode) {
/* 472 */             List<TagPart> openParts = tagNode2.parts();
/*     */             
/* 474 */             if (tagCloses(closeValues, openParts)) {
/* 475 */               if (tagNode2 != tagNode1 && strict) {
/*     */                 
/* 477 */                 String msg = "Unclosed tag encountered; " + tagNode1.name() + " is not closed, because " + (String)closeValues.get(0) + " was closed first.";
/* 478 */                 throw new ParsingExceptionImpl(msg, message, new Token[] { tagNode2.token(), tagNode1.token(), token });
/*     */               } 
/*     */               
/* 481 */               ElementNode par = tagNode2.parent();
/* 482 */               if (par != null) {
/* 483 */                 elementNode = par; break;
/*     */               } 
/* 485 */               throw new IllegalStateException("Root node matched with close tag value, this should not be possible. Original text: " + message);
/*     */             } 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 491 */             elementNode1 = tagNode2.parent();
/*     */           } 
/* 493 */           if (elementNode1 == null || elementNode1 instanceof RootNode)
/*     */           {
/*     */             
/* 496 */             elementNode.addChild((ElementNode)new TextNode(elementNode, token, message));
/*     */           }
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/* 505 */     if (strict && root != elementNode) {
/* 506 */       ArrayList<TagNode> openTags = new ArrayList<>();
/*     */       
/* 508 */       ElementNode n = elementNode;
/* 509 */       while (n != null && 
/* 510 */         n instanceof TagNode) {
/* 511 */         openTags.add((TagNode)n);
/*     */ 
/*     */ 
/*     */         
/* 515 */         n = n.parent();
/*     */       } 
/*     */ 
/*     */       
/* 519 */       Token[] errorTokens = new Token[openTags.size()];
/*     */       
/* 521 */       StringBuilder sb = new StringBuilder("All tags must be explicitly closed while in strict mode. End of string found with open tags: ");
/*     */ 
/*     */       
/* 524 */       int i = 0;
/* 525 */       ListIterator<TagNode> iter = openTags.listIterator(openTags.size());
/* 526 */       while (iter.hasPrevious()) {
/* 527 */         TagNode tagNode = iter.previous();
/* 528 */         errorTokens[i++] = tagNode.token();
/*     */         
/* 530 */         sb.append(tagNode.name());
/* 531 */         if (iter.hasPrevious()) {
/* 532 */           sb.append(", ");
/*     */         }
/*     */       } 
/*     */       
/* 536 */       throw new ParsingExceptionImpl(sb.toString(), message, errorTokens);
/*     */     } 
/*     */     
/* 539 */     return root;
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
/*     */   private static boolean tagCloses(List<String> closeParts, List<TagPart> openParts) {
/* 551 */     if (closeParts.size() > openParts.size()) {
/* 552 */       return false;
/*     */     }
/*     */     
/* 555 */     if (!((String)closeParts.get(0)).equalsIgnoreCase(((TagPart)openParts.get(0)).value())) {
/* 556 */       return false;
/*     */     }
/* 558 */     for (int i = 1; i < closeParts.size(); i++) {
/* 559 */       if (!((String)closeParts.get(i)).equals(((TagPart)openParts.get(i)).value())) {
/* 560 */         return false;
/*     */       }
/*     */     } 
/* 563 */     return true;
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
/*     */   private static boolean boundsCheck(String text, int index, int length) {
/* 576 */     return (index + length < text.length());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void insert(Token token, Token value) {
/* 586 */     if (token.childTokens() == null) {
/* 587 */       token.childTokens(Collections.singletonList(value));
/*     */       return;
/*     */     } 
/* 590 */     if (token.childTokens().size() == 1) {
/* 591 */       ArrayList<Token> list = new ArrayList<>(3);
/* 592 */       list.add(token.childTokens().get(0));
/* 593 */       list.add(value);
/* 594 */       token.childTokens(list);
/*     */     } else {
/* 596 */       token.childTokens().add(value);
/*     */     } 
/*     */   }
/*     */   
/*     */   enum SecondPassState {
/* 601 */     NORMAL,
/* 602 */     STRING;
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
/*     */   public static String unescape(String text, int startIndex, int endIndex, IntPredicate escapes) {
/* 616 */     int from = startIndex;
/*     */     
/* 618 */     int i = text.indexOf('\\', from);
/* 619 */     if (i == -1 || i >= endIndex) {
/* 620 */       return text.substring(from, endIndex);
/*     */     }
/*     */     
/* 623 */     StringBuilder sb = new StringBuilder(endIndex - startIndex);
/* 624 */     while (i != -1 && i + 1 < endIndex) {
/* 625 */       if (escapes.test(text.codePointAt(i + 1))) {
/* 626 */         sb.append(text, from, i);
/* 627 */         i++;
/*     */         
/* 629 */         if (i >= endIndex) {
/* 630 */           from = endIndex;
/*     */           
/*     */           break;
/*     */         } 
/* 634 */         int codePoint = text.codePointAt(i);
/* 635 */         sb.appendCodePoint(codePoint);
/*     */         
/* 637 */         if (Character.isBmpCodePoint(codePoint)) {
/* 638 */           i++;
/*     */         } else {
/* 640 */           i += 2;
/*     */         } 
/*     */         
/* 643 */         if (i >= endIndex) {
/* 644 */           from = endIndex;
/*     */           break;
/*     */         } 
/*     */       } else {
/* 648 */         i++;
/* 649 */         sb.append(text, from, i);
/*     */       } 
/*     */       
/* 652 */       from = i;
/* 653 */       i = text.indexOf('\\', from);
/*     */     } 
/*     */     
/* 656 */     sb.append(text, from, endIndex);
/*     */     
/* 658 */     return sb.toString();
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
/*     */   @Internal
/*     */   public static interface TagProvider
/*     */   {
/*     */     @Nullable
/*     */     Tag resolve(@NotNull String param1String, @NotNull List<? extends Tag.Argument> param1List, @Nullable Token param1Token);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     default Tag resolve(@NotNull String name) {
/* 689 */       return resolve(name, Collections.emptyList(), null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     default Tag resolve(@NotNull TagNode node) {
/* 700 */       return resolve(
/* 701 */           sanitizePlaceholderName(node.name()), node
/* 702 */           .parts().subList(1, node.parts().size()), node
/* 703 */           .token());
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
/*     */     @NotNull
/*     */     static String sanitizePlaceholderName(@NotNull String name) {
/* 717 */       return name.toLowerCase(Locale.ROOT);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\internal\parser\TokenParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */