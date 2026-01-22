/*     */ package ac.grim.grimac.shaded.snakeyaml.parser;
/*     */ 
/*     */ import ac.grim.grimac.shaded.snakeyaml.DumperOptions;
/*     */ import ac.grim.grimac.shaded.snakeyaml.LoaderOptions;
/*     */ import ac.grim.grimac.shaded.snakeyaml.comments.CommentType;
/*     */ import ac.grim.grimac.shaded.snakeyaml.error.Mark;
/*     */ import ac.grim.grimac.shaded.snakeyaml.error.YAMLException;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.AliasEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.CommentEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.DocumentEndEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.DocumentStartEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.Event;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.ImplicitTuple;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.MappingEndEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.MappingStartEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.ScalarEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.SequenceEndEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.SequenceStartEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.StreamEndEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.events.StreamStartEvent;
/*     */ import ac.grim.grimac.shaded.snakeyaml.reader.StreamReader;
/*     */ import ac.grim.grimac.shaded.snakeyaml.scanner.Scanner;
/*     */ import ac.grim.grimac.shaded.snakeyaml.scanner.ScannerImpl;
/*     */ import ac.grim.grimac.shaded.snakeyaml.tokens.AliasToken;
/*     */ import ac.grim.grimac.shaded.snakeyaml.tokens.AnchorToken;
/*     */ import ac.grim.grimac.shaded.snakeyaml.tokens.BlockEntryToken;
/*     */ import ac.grim.grimac.shaded.snakeyaml.tokens.CommentToken;
/*     */ import ac.grim.grimac.shaded.snakeyaml.tokens.DirectiveToken;
/*     */ import ac.grim.grimac.shaded.snakeyaml.tokens.ScalarToken;
/*     */ import ac.grim.grimac.shaded.snakeyaml.tokens.StreamEndToken;
/*     */ import ac.grim.grimac.shaded.snakeyaml.tokens.StreamStartToken;
/*     */ import ac.grim.grimac.shaded.snakeyaml.tokens.TagToken;
/*     */ import ac.grim.grimac.shaded.snakeyaml.tokens.TagTuple;
/*     */ import ac.grim.grimac.shaded.snakeyaml.tokens.Token;
/*     */ import ac.grim.grimac.shaded.snakeyaml.util.ArrayStack;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ParserImpl
/*     */   implements Parser
/*     */ {
/* 121 */   private static final Map<String, String> DEFAULT_TAGS = new HashMap<>();
/*     */   
/*     */   static {
/* 124 */     DEFAULT_TAGS.put("!", "!");
/* 125 */     DEFAULT_TAGS.put("!!", "tag:yaml.org,2002:");
/*     */   }
/*     */   
/*     */   protected final Scanner scanner;
/*     */   private Event currentEvent;
/*     */   private final ArrayStack<Production> states;
/*     */   private final ArrayStack<Mark> marks;
/*     */   private Production state;
/*     */   private VersionTagsTuple directives;
/*     */   
/*     */   public ParserImpl(StreamReader reader, LoaderOptions options) {
/* 136 */     this((Scanner)new ScannerImpl(reader, options));
/*     */   }
/*     */   
/*     */   public ParserImpl(Scanner scanner) {
/* 140 */     this.scanner = scanner;
/* 141 */     this.currentEvent = null;
/* 142 */     this.directives = new VersionTagsTuple(null, new HashMap<>(DEFAULT_TAGS));
/* 143 */     this.states = new ArrayStack(100);
/* 144 */     this.marks = new ArrayStack(10);
/* 145 */     this.state = new ParseStreamStart();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean checkEvent(Event.ID choice) {
/* 152 */     peekEvent();
/* 153 */     return (this.currentEvent != null && this.currentEvent.is(choice));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Event peekEvent() {
/* 160 */     if (this.currentEvent == null && this.state != null) {
/* 161 */       this.currentEvent = this.state.produce();
/*     */     }
/* 163 */     return this.currentEvent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Event getEvent() {
/* 170 */     peekEvent();
/* 171 */     Event value = this.currentEvent;
/* 172 */     this.currentEvent = null;
/* 173 */     return value;
/*     */   }
/*     */   
/*     */   private CommentEvent produceCommentEvent(CommentToken token) {
/* 177 */     Mark startMark = token.getStartMark();
/* 178 */     Mark endMark = token.getEndMark();
/* 179 */     String value = token.getValue();
/* 180 */     CommentType type = token.getCommentType();
/*     */ 
/*     */ 
/*     */     
/* 184 */     return new CommentEvent(type, value, startMark, endMark);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class ParseStreamStart
/*     */     implements Production
/*     */   {
/*     */     private ParseStreamStart() {}
/*     */ 
/*     */ 
/*     */     
/*     */     public Event produce() {
/* 198 */       StreamStartToken token = (StreamStartToken)ParserImpl.this.scanner.getToken();
/* 199 */       StreamStartEvent streamStartEvent = new StreamStartEvent(token.getStartMark(), token.getEndMark());
/*     */       
/* 201 */       ParserImpl.this.state = new ParserImpl.ParseImplicitDocumentStart();
/* 202 */       return (Event)streamStartEvent;
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseImplicitDocumentStart implements Production {
/*     */     private ParseImplicitDocumentStart() {}
/*     */     
/*     */     public Event produce() {
/* 210 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 211 */         ParserImpl.this.state = new ParseImplicitDocumentStart();
/* 212 */         return (Event)ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
/*     */       } 
/* 214 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Directive, Token.ID.DocumentStart, Token.ID.StreamEnd })) {
/* 215 */         Token token = ParserImpl.this.scanner.peekToken();
/* 216 */         Mark startMark = token.getStartMark();
/* 217 */         Mark endMark = startMark;
/* 218 */         DocumentStartEvent documentStartEvent = new DocumentStartEvent(startMark, endMark, false, null, null);
/*     */         
/* 220 */         ParserImpl.this.states.push(new ParserImpl.ParseDocumentEnd());
/* 221 */         ParserImpl.this.state = new ParserImpl.ParseBlockNode();
/* 222 */         return (Event)documentStartEvent;
/*     */       } 
/* 224 */       return (new ParserImpl.ParseDocumentStart()).produce();
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseDocumentStart implements Production {
/*     */     private ParseDocumentStart() {}
/*     */     
/*     */     public Event produce() {
/* 232 */       while (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.DocumentEnd })) {
/* 233 */         ParserImpl.this.scanner.getToken();
/*     */       }
/*     */ 
/*     */       
/* 237 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.StreamEnd })) {
/* 238 */         ParserImpl.this.scanner.resetDocumentIndex();
/* 239 */         Token token1 = ParserImpl.this.scanner.peekToken();
/* 240 */         Mark startMark = token1.getStartMark();
/* 241 */         VersionTagsTuple tuple = ParserImpl.this.processDirectives();
/* 242 */         while (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment }))
/*     */         {
/* 244 */           ParserImpl.this.scanner.getToken();
/*     */         }
/* 246 */         if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.StreamEnd })) {
/* 247 */           if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.DocumentStart })) {
/* 248 */             throw new ParserException(null, null, "expected '<document start>', but found '" + ParserImpl.this.scanner
/* 249 */                 .peekToken().getTokenId() + "'", ParserImpl.this.scanner
/* 250 */                 .peekToken().getStartMark());
/*     */           }
/* 252 */           token1 = ParserImpl.this.scanner.getToken();
/* 253 */           Mark endMark = token1.getEndMark();
/*     */           
/* 255 */           DocumentStartEvent documentStartEvent = new DocumentStartEvent(startMark, endMark, true, tuple.getVersion(), tuple.getTags());
/* 256 */           ParserImpl.this.states.push(new ParserImpl.ParseDocumentEnd());
/* 257 */           ParserImpl.this.state = new ParserImpl.ParseDocumentContent();
/* 258 */           return (Event)documentStartEvent;
/*     */         } 
/*     */       } 
/*     */       
/* 262 */       StreamEndToken token = (StreamEndToken)ParserImpl.this.scanner.getToken();
/* 263 */       StreamEndEvent streamEndEvent = new StreamEndEvent(token.getStartMark(), token.getEndMark());
/* 264 */       if (!ParserImpl.this.states.isEmpty()) {
/* 265 */         throw new YAMLException("Unexpected end of stream. States left: " + ParserImpl.this.states);
/*     */       }
/* 267 */       if (!ParserImpl.this.marks.isEmpty()) {
/* 268 */         throw new YAMLException("Unexpected end of stream. Marks left: " + ParserImpl.this.marks);
/*     */       }
/* 270 */       ParserImpl.this.state = null;
/* 271 */       return (Event)streamEndEvent;
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseDocumentEnd implements Production {
/*     */     private ParseDocumentEnd() {}
/*     */     
/*     */     public Event produce() {
/* 279 */       Token token = ParserImpl.this.scanner.peekToken();
/* 280 */       Mark startMark = token.getStartMark();
/* 281 */       Mark endMark = startMark;
/* 282 */       boolean explicit = false;
/* 283 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.DocumentEnd })) {
/* 284 */         token = ParserImpl.this.scanner.getToken();
/* 285 */         endMark = token.getEndMark();
/* 286 */         explicit = true;
/*     */       } 
/* 288 */       DocumentEndEvent documentEndEvent = new DocumentEndEvent(startMark, endMark, explicit);
/*     */       
/* 290 */       ParserImpl.this.state = new ParserImpl.ParseDocumentStart();
/* 291 */       return (Event)documentEndEvent;
/*     */     } }
/*     */   
/*     */   private class ParseDocumentContent implements Production {
/*     */     private ParseDocumentContent() {}
/*     */     
/*     */     public Event produce() {
/* 298 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 299 */         ParserImpl.this.state = new ParseDocumentContent();
/* 300 */         return (Event)ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
/*     */       } 
/* 302 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Directive, Token.ID.DocumentStart, Token.ID.DocumentEnd, Token.ID.StreamEnd })) {
/*     */         
/* 304 */         Event event = ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
/* 305 */         ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
/* 306 */         return event;
/*     */       } 
/* 308 */       return (new ParserImpl.ParseBlockNode()).produce();
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
/*     */   private VersionTagsTuple processDirectives() {
/* 322 */     HashMap<String, String> tagHandles = new HashMap<>(this.directives.getTags());
/* 323 */     for (String key : DEFAULT_TAGS.keySet()) {
/* 324 */       tagHandles.remove(key);
/*     */     }
/*     */     
/* 327 */     this.directives = new VersionTagsTuple(null, tagHandles);
/* 328 */     while (this.scanner.checkToken(new Token.ID[] { Token.ID.Directive })) {
/*     */       
/* 330 */       DirectiveToken token = (DirectiveToken)this.scanner.getToken();
/* 331 */       if (token.getName().equals("YAML")) {
/* 332 */         if (this.directives.getVersion() != null) {
/* 333 */           throw new ParserException(null, null, "found duplicate YAML directive", token
/* 334 */               .getStartMark());
/*     */         }
/* 336 */         List<Integer> value = token.getValue();
/* 337 */         Integer major = value.get(0);
/* 338 */         if (major.intValue() != 1) {
/* 339 */           throw new ParserException(null, null, "found incompatible YAML document (version 1.* is required)", token
/* 340 */               .getStartMark());
/*     */         }
/* 342 */         Integer minor = value.get(1);
/* 343 */         if (minor.intValue() == 0) {
/* 344 */           this.directives = new VersionTagsTuple(DumperOptions.Version.V1_0, tagHandles); continue;
/*     */         } 
/* 346 */         this.directives = new VersionTagsTuple(DumperOptions.Version.V1_1, tagHandles); continue;
/*     */       } 
/* 348 */       if (token.getName().equals("TAG")) {
/* 349 */         List<String> value = token.getValue();
/* 350 */         String handle = value.get(0);
/* 351 */         String prefix = value.get(1);
/* 352 */         if (tagHandles.containsKey(handle)) {
/* 353 */           throw new ParserException(null, null, "duplicate tag handle " + handle, token
/* 354 */               .getStartMark());
/*     */         }
/* 356 */         tagHandles.put(handle, prefix);
/*     */       } 
/*     */     } 
/* 359 */     HashMap<String, String> detectedTagHandles = new HashMap<>();
/* 360 */     if (!tagHandles.isEmpty())
/*     */     {
/* 362 */       detectedTagHandles = new HashMap<>(tagHandles);
/*     */     }
/*     */     
/* 365 */     for (String key : DEFAULT_TAGS.keySet()) {
/*     */       
/* 367 */       if (!tagHandles.containsKey(key)) {
/* 368 */         tagHandles.put(key, DEFAULT_TAGS.get(key));
/*     */       }
/*     */     } 
/*     */     
/* 372 */     return new VersionTagsTuple(this.directives.getVersion(), detectedTagHandles);
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
/*     */   private class ParseBlockNode
/*     */     implements Production
/*     */   {
/*     */     private ParseBlockNode() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Event produce() {
/* 398 */       return ParserImpl.this.parseNode(true, false);
/*     */     }
/*     */   }
/*     */   
/*     */   private Event parseFlowNode() {
/* 403 */     return parseNode(false, false);
/*     */   }
/*     */   
/*     */   private Event parseBlockNodeOrIndentlessSequence() {
/* 407 */     return parseNode(true, true);
/*     */   }
/*     */   
/*     */   private Event parseNode(boolean block, boolean indentlessSequence) {
/*     */     ScalarEvent scalarEvent;
/* 412 */     Mark startMark = null;
/* 413 */     Mark endMark = null;
/* 414 */     Mark tagMark = null;
/* 415 */     if (this.scanner.checkToken(new Token.ID[] { Token.ID.Alias })) {
/* 416 */       AliasToken token = (AliasToken)this.scanner.getToken();
/* 417 */       AliasEvent aliasEvent = new AliasEvent(token.getValue(), token.getStartMark(), token.getEndMark());
/* 418 */       this.state = (Production)this.states.pop();
/*     */     } else {
/* 420 */       String anchor = null;
/* 421 */       TagTuple tagTokenTag = null;
/* 422 */       if (this.scanner.checkToken(new Token.ID[] { Token.ID.Anchor })) {
/* 423 */         AnchorToken token = (AnchorToken)this.scanner.getToken();
/* 424 */         startMark = token.getStartMark();
/* 425 */         endMark = token.getEndMark();
/* 426 */         anchor = token.getValue();
/* 427 */         if (this.scanner.checkToken(new Token.ID[] { Token.ID.Tag })) {
/* 428 */           TagToken tagToken = (TagToken)this.scanner.getToken();
/* 429 */           tagMark = tagToken.getStartMark();
/* 430 */           endMark = tagToken.getEndMark();
/* 431 */           tagTokenTag = tagToken.getValue();
/*     */         } 
/*     */       } else {
/* 434 */         TagToken tagToken = (TagToken)this.scanner.getToken();
/* 435 */         startMark = tagToken.getStartMark();
/* 436 */         tagMark = startMark;
/* 437 */         endMark = tagToken.getEndMark();
/* 438 */         tagTokenTag = tagToken.getValue();
/* 439 */         if (this.scanner.checkToken(new Token.ID[] { Token.ID.Tag }) && this.scanner.checkToken(new Token.ID[] { Token.ID.Anchor })) {
/* 440 */           AnchorToken token = (AnchorToken)this.scanner.getToken();
/* 441 */           endMark = token.getEndMark();
/* 442 */           anchor = token.getValue();
/*     */         } 
/*     */       } 
/* 445 */       String tag = null;
/* 446 */       if (tagTokenTag != null) {
/* 447 */         String handle = tagTokenTag.getHandle();
/* 448 */         String suffix = tagTokenTag.getSuffix();
/* 449 */         if (handle != null) {
/* 450 */           if (!this.directives.getTags().containsKey(handle)) {
/* 451 */             throw new ParserException("while parsing a node", startMark, "found undefined tag handle " + handle, tagMark);
/*     */           }
/*     */           
/* 454 */           tag = (String)this.directives.getTags().get(handle) + suffix;
/*     */         } else {
/* 456 */           tag = suffix;
/*     */         } 
/*     */       } 
/* 459 */       if (startMark == null) {
/* 460 */         startMark = this.scanner.peekToken().getStartMark();
/* 461 */         endMark = startMark;
/*     */       } 
/* 463 */       Event event = null;
/* 464 */       boolean implicit = (tag == null || tag.equals("!"));
/* 465 */       if (indentlessSequence && this.scanner.checkToken(new Token.ID[] { Token.ID.BlockEntry })) {
/* 466 */         endMark = this.scanner.peekToken().getEndMark();
/* 467 */         SequenceStartEvent sequenceStartEvent = new SequenceStartEvent(anchor, tag, implicit, startMark, endMark, DumperOptions.FlowStyle.BLOCK);
/*     */         
/* 469 */         this.state = new ParseIndentlessSequenceEntryKey();
/*     */       }
/* 471 */       else if (this.scanner.checkToken(new Token.ID[] { Token.ID.Scalar })) {
/* 472 */         ImplicitTuple implicitValues; ScalarToken token = (ScalarToken)this.scanner.getToken();
/* 473 */         endMark = token.getEndMark();
/*     */         
/* 475 */         if ((token.getPlain() && tag == null) || "!".equals(tag)) {
/* 476 */           implicitValues = new ImplicitTuple(true, false);
/* 477 */         } else if (tag == null) {
/* 478 */           implicitValues = new ImplicitTuple(false, true);
/*     */         } else {
/* 480 */           implicitValues = new ImplicitTuple(false, false);
/*     */         } 
/*     */         
/* 483 */         scalarEvent = new ScalarEvent(anchor, tag, implicitValues, token.getValue(), startMark, endMark, token.getStyle());
/* 484 */         this.state = (Production)this.states.pop();
/* 485 */       } else if (this.scanner.checkToken(new Token.ID[] { Token.ID.FlowSequenceStart })) {
/* 486 */         endMark = this.scanner.peekToken().getEndMark();
/* 487 */         SequenceStartEvent sequenceStartEvent = new SequenceStartEvent(anchor, tag, implicit, startMark, endMark, DumperOptions.FlowStyle.FLOW);
/*     */         
/* 489 */         this.state = new ParseFlowSequenceFirstEntry();
/* 490 */       } else if (this.scanner.checkToken(new Token.ID[] { Token.ID.FlowMappingStart })) {
/* 491 */         endMark = this.scanner.peekToken().getEndMark();
/* 492 */         MappingStartEvent mappingStartEvent = new MappingStartEvent(anchor, tag, implicit, startMark, endMark, DumperOptions.FlowStyle.FLOW);
/*     */         
/* 494 */         this.state = new ParseFlowMappingFirstKey();
/* 495 */       } else if (block && this.scanner.checkToken(new Token.ID[] { Token.ID.BlockSequenceStart })) {
/* 496 */         endMark = this.scanner.peekToken().getStartMark();
/* 497 */         SequenceStartEvent sequenceStartEvent = new SequenceStartEvent(anchor, tag, implicit, startMark, endMark, DumperOptions.FlowStyle.BLOCK);
/*     */         
/* 499 */         this.state = new ParseBlockSequenceFirstEntry();
/* 500 */       } else if (block && this.scanner.checkToken(new Token.ID[] { Token.ID.BlockMappingStart })) {
/* 501 */         endMark = this.scanner.peekToken().getStartMark();
/* 502 */         MappingStartEvent mappingStartEvent = new MappingStartEvent(anchor, tag, implicit, startMark, endMark, DumperOptions.FlowStyle.BLOCK);
/*     */         
/* 504 */         this.state = new ParseBlockMappingFirstKey();
/* 505 */       } else if (anchor != null || tag != null) {
/*     */ 
/*     */         
/* 508 */         scalarEvent = new ScalarEvent(anchor, tag, new ImplicitTuple(implicit, false), "", startMark, endMark, DumperOptions.ScalarStyle.PLAIN);
/*     */         
/* 510 */         this.state = (Production)this.states.pop();
/*     */       } else {
/* 512 */         Token token = this.scanner.peekToken();
/* 513 */         throw new ParserException("while parsing a " + (block ? "block" : "flow") + " node", startMark, "expected the node content, but found '" + token
/* 514 */             .getTokenId() + "'", token
/* 515 */             .getStartMark());
/*     */       } 
/*     */     } 
/*     */     
/* 519 */     return (Event)scalarEvent;
/*     */   }
/*     */   
/*     */   private class ParseBlockSequenceFirstEntry
/*     */     implements Production
/*     */   {
/*     */     private ParseBlockSequenceFirstEntry() {}
/*     */     
/*     */     public Event produce() {
/* 528 */       Token token = ParserImpl.this.scanner.getToken();
/* 529 */       ParserImpl.this.marks.push(token.getStartMark());
/* 530 */       return (new ParserImpl.ParseBlockSequenceEntryKey()).produce();
/*     */     } }
/*     */   
/*     */   private class ParseBlockSequenceEntryKey implements Production {
/*     */     private ParseBlockSequenceEntryKey() {}
/*     */     
/*     */     public Event produce() {
/* 537 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 538 */         ParserImpl.this.state = new ParseBlockSequenceEntryKey();
/* 539 */         return (Event)ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
/*     */       } 
/* 541 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.BlockEntry })) {
/* 542 */         BlockEntryToken blockEntryToken = (BlockEntryToken)ParserImpl.this.scanner.getToken();
/* 543 */         return (new ParserImpl.ParseBlockSequenceEntryValue(blockEntryToken)).produce();
/*     */       } 
/* 545 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.BlockEnd })) {
/* 546 */         Token token1 = ParserImpl.this.scanner.peekToken();
/* 547 */         throw new ParserException("while parsing a block collection", (Mark)ParserImpl.this.marks.pop(), "expected <block end>, but found '" + token1
/* 548 */             .getTokenId() + "'", token1.getStartMark());
/*     */       } 
/* 550 */       Token token = ParserImpl.this.scanner.getToken();
/* 551 */       SequenceEndEvent sequenceEndEvent = new SequenceEndEvent(token.getStartMark(), token.getEndMark());
/* 552 */       ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
/* 553 */       ParserImpl.this.marks.pop();
/* 554 */       return (Event)sequenceEndEvent;
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseBlockSequenceEntryValue
/*     */     implements Production {
/*     */     BlockEntryToken token;
/*     */     
/*     */     public ParseBlockSequenceEntryValue(BlockEntryToken token) {
/* 563 */       this.token = token;
/*     */     }
/*     */     
/*     */     public Event produce() {
/* 567 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 568 */         ParserImpl.this.state = new ParseBlockSequenceEntryValue(this.token);
/* 569 */         return (Event)ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
/*     */       } 
/* 571 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.BlockEntry, Token.ID.BlockEnd })) {
/* 572 */         ParserImpl.this.states.push(new ParserImpl.ParseBlockSequenceEntryKey());
/* 573 */         return (new ParserImpl.ParseBlockNode()).produce();
/*     */       } 
/* 575 */       ParserImpl.this.state = new ParserImpl.ParseBlockSequenceEntryKey();
/* 576 */       return ParserImpl.this.processEmptyScalar(this.token.getEndMark());
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseIndentlessSequenceEntryKey
/*     */     implements Production
/*     */   {
/*     */     private ParseIndentlessSequenceEntryKey() {}
/*     */     
/*     */     public Event produce() {
/* 586 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 587 */         ParserImpl.this.state = new ParseIndentlessSequenceEntryKey();
/* 588 */         return (Event)ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
/*     */       } 
/* 590 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.BlockEntry })) {
/* 591 */         BlockEntryToken blockEntryToken = (BlockEntryToken)ParserImpl.this.scanner.getToken();
/* 592 */         return (new ParserImpl.ParseIndentlessSequenceEntryValue(blockEntryToken)).produce();
/*     */       } 
/* 594 */       Token token = ParserImpl.this.scanner.peekToken();
/* 595 */       SequenceEndEvent sequenceEndEvent = new SequenceEndEvent(token.getStartMark(), token.getEndMark());
/* 596 */       ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
/* 597 */       return (Event)sequenceEndEvent;
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseIndentlessSequenceEntryValue
/*     */     implements Production {
/*     */     BlockEntryToken token;
/*     */     
/*     */     public ParseIndentlessSequenceEntryValue(BlockEntryToken token) {
/* 606 */       this.token = token;
/*     */     }
/*     */     
/*     */     public Event produce() {
/* 610 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 611 */         ParserImpl.this.state = new ParseIndentlessSequenceEntryValue(this.token);
/* 612 */         return (Event)ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
/*     */       } 
/* 614 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.BlockEntry, Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd })) {
/*     */         
/* 616 */         ParserImpl.this.states.push(new ParserImpl.ParseIndentlessSequenceEntryKey());
/* 617 */         return (new ParserImpl.ParseBlockNode()).produce();
/*     */       } 
/* 619 */       ParserImpl.this.state = new ParserImpl.ParseIndentlessSequenceEntryKey();
/* 620 */       return ParserImpl.this.processEmptyScalar(this.token.getEndMark());
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseBlockMappingFirstKey implements Production {
/*     */     private ParseBlockMappingFirstKey() {}
/*     */     
/*     */     public Event produce() {
/* 628 */       Token token = ParserImpl.this.scanner.getToken();
/* 629 */       ParserImpl.this.marks.push(token.getStartMark());
/* 630 */       return (new ParserImpl.ParseBlockMappingKey()).produce();
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseBlockMappingKey implements Production { private ParseBlockMappingKey() {}
/*     */     
/*     */     public Event produce() {
/* 637 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 638 */         ParserImpl.this.state = new ParseBlockMappingKey();
/* 639 */         return (Event)ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
/*     */       } 
/* 641 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Key })) {
/* 642 */         Token token1 = ParserImpl.this.scanner.getToken();
/* 643 */         if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd })) {
/* 644 */           ParserImpl.this.states.push(new ParserImpl.ParseBlockMappingValue());
/* 645 */           return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
/*     */         } 
/* 647 */         ParserImpl.this.state = new ParserImpl.ParseBlockMappingValue();
/* 648 */         return ParserImpl.this.processEmptyScalar(token1.getEndMark());
/*     */       } 
/*     */       
/* 651 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.BlockEnd })) {
/* 652 */         Token token1 = ParserImpl.this.scanner.peekToken();
/* 653 */         throw new ParserException("while parsing a block mapping", (Mark)ParserImpl.this.marks.pop(), "expected <block end>, but found '" + token1
/* 654 */             .getTokenId() + "'", token1.getStartMark());
/*     */       } 
/* 656 */       Token token = ParserImpl.this.scanner.getToken();
/* 657 */       MappingEndEvent mappingEndEvent = new MappingEndEvent(token.getStartMark(), token.getEndMark());
/* 658 */       ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
/* 659 */       ParserImpl.this.marks.pop();
/* 660 */       return (Event)mappingEndEvent;
/*     */     } }
/*     */   
/*     */   private class ParseBlockMappingValue implements Production {
/*     */     private ParseBlockMappingValue() {}
/*     */     
/*     */     public Event produce() {
/* 667 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Value })) {
/* 668 */         Token token1 = ParserImpl.this.scanner.getToken();
/* 669 */         if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 670 */           ParserImpl.this.state = new ParserImpl.ParseBlockMappingValueComment();
/* 671 */           return ParserImpl.this.state.produce();
/* 672 */         }  if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd })) {
/* 673 */           ParserImpl.this.states.push(new ParserImpl.ParseBlockMappingKey());
/* 674 */           return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
/*     */         } 
/* 676 */         ParserImpl.this.state = new ParserImpl.ParseBlockMappingKey();
/* 677 */         return ParserImpl.this.processEmptyScalar(token1.getEndMark());
/*     */       } 
/* 679 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Scalar })) {
/* 680 */         ParserImpl.this.states.push(new ParserImpl.ParseBlockMappingKey());
/* 681 */         return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
/*     */       } 
/* 683 */       ParserImpl.this.state = new ParserImpl.ParseBlockMappingKey();
/* 684 */       Token token = ParserImpl.this.scanner.peekToken();
/* 685 */       return ParserImpl.this.processEmptyScalar(token.getStartMark());
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseBlockMappingValueComment
/*     */     implements Production {
/* 691 */     List<CommentToken> tokens = new LinkedList<>();
/*     */     
/*     */     public Event produce() {
/* 694 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 695 */         this.tokens.add((CommentToken)ParserImpl.this.scanner.getToken());
/* 696 */         return produce();
/* 697 */       }  if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd })) {
/* 698 */         if (!this.tokens.isEmpty()) {
/* 699 */           return (Event)ParserImpl.this.produceCommentEvent(this.tokens.remove(0));
/*     */         }
/* 701 */         ParserImpl.this.states.push(new ParserImpl.ParseBlockMappingKey());
/* 702 */         return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
/*     */       } 
/* 704 */       ParserImpl.this.state = new ParserImpl.ParseBlockMappingValueCommentList(this.tokens);
/* 705 */       return ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
/*     */     }
/*     */     
/*     */     private ParseBlockMappingValueComment() {}
/*     */   }
/*     */   
/*     */   private class ParseBlockMappingValueCommentList implements Production {
/*     */     List<CommentToken> tokens;
/*     */     
/*     */     public ParseBlockMappingValueCommentList(List<CommentToken> tokens) {
/* 715 */       this.tokens = tokens;
/*     */     }
/*     */     
/*     */     public Event produce() {
/* 719 */       if (!this.tokens.isEmpty()) {
/* 720 */         return (Event)ParserImpl.this.produceCommentEvent(this.tokens.remove(0));
/*     */       }
/* 722 */       return (new ParserImpl.ParseBlockMappingKey()).produce();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class ParseFlowSequenceFirstEntry
/*     */     implements Production
/*     */   {
/*     */     private ParseFlowSequenceFirstEntry() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Event produce() {
/* 742 */       Token token = ParserImpl.this.scanner.getToken();
/* 743 */       ParserImpl.this.marks.push(token.getStartMark());
/* 744 */       return (new ParserImpl.ParseFlowSequenceEntry(true)).produce();
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseFlowSequenceEntry
/*     */     implements Production {
/*     */     private final boolean first;
/*     */     
/*     */     public ParseFlowSequenceEntry(boolean first) {
/* 753 */       this.first = first;
/*     */     }
/*     */     
/*     */     public Event produce() {
/* 757 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 758 */         ParserImpl.this.state = new ParseFlowSequenceEntry(this.first);
/* 759 */         return (Event)ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
/*     */       } 
/* 761 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowSequenceEnd })) {
/* 762 */         if (!this.first) {
/* 763 */           if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowEntry })) {
/* 764 */             ParserImpl.this.scanner.getToken();
/* 765 */             if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 766 */               ParserImpl.this.state = new ParseFlowSequenceEntry(true);
/* 767 */               return (Event)ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
/*     */             } 
/*     */           } else {
/* 770 */             Token token1 = ParserImpl.this.scanner.peekToken();
/* 771 */             throw new ParserException("while parsing a flow sequence", (Mark)ParserImpl.this.marks.pop(), "expected ',' or ']', but got " + token1
/* 772 */                 .getTokenId(), token1.getStartMark());
/*     */           } 
/*     */         }
/* 775 */         if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Key })) {
/* 776 */           Token token1 = ParserImpl.this.scanner.peekToken();
/*     */           
/* 778 */           MappingStartEvent mappingStartEvent = new MappingStartEvent(null, null, true, token1.getStartMark(), token1.getEndMark(), DumperOptions.FlowStyle.FLOW);
/* 779 */           ParserImpl.this.state = new ParserImpl.ParseFlowSequenceEntryMappingKey();
/* 780 */           return (Event)mappingStartEvent;
/* 781 */         }  if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowSequenceEnd })) {
/* 782 */           ParserImpl.this.states.push(new ParseFlowSequenceEntry(false));
/* 783 */           return ParserImpl.this.parseFlowNode();
/*     */         } 
/*     */       } 
/* 786 */       Token token = ParserImpl.this.scanner.getToken();
/* 787 */       SequenceEndEvent sequenceEndEvent = new SequenceEndEvent(token.getStartMark(), token.getEndMark());
/* 788 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 789 */         ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
/*     */       } else {
/* 791 */         ParserImpl.this.state = new ParserImpl.ParseFlowEndComment();
/*     */       } 
/* 793 */       ParserImpl.this.marks.pop();
/* 794 */       return (Event)sequenceEndEvent;
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseFlowEndComment implements Production { private ParseFlowEndComment() {}
/*     */     
/*     */     public Event produce() {
/* 801 */       CommentEvent commentEvent = ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
/* 802 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 803 */         ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
/*     */       }
/* 805 */       return (Event)commentEvent;
/*     */     } }
/*     */   
/*     */   private class ParseFlowSequenceEntryMappingKey implements Production {
/*     */     private ParseFlowSequenceEntryMappingKey() {}
/*     */     
/*     */     public Event produce() {
/* 812 */       Token token = ParserImpl.this.scanner.getToken();
/* 813 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowSequenceEnd })) {
/* 814 */         ParserImpl.this.states.push(new ParserImpl.ParseFlowSequenceEntryMappingValue());
/* 815 */         return ParserImpl.this.parseFlowNode();
/*     */       } 
/* 817 */       ParserImpl.this.state = new ParserImpl.ParseFlowSequenceEntryMappingValue();
/* 818 */       return ParserImpl.this.processEmptyScalar(token.getEndMark());
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseFlowSequenceEntryMappingValue implements Production {
/*     */     private ParseFlowSequenceEntryMappingValue() {}
/*     */     
/*     */     public Event produce() {
/* 826 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Value })) {
/* 827 */         Token token1 = ParserImpl.this.scanner.getToken();
/* 828 */         if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowEntry, Token.ID.FlowSequenceEnd })) {
/* 829 */           ParserImpl.this.states.push(new ParserImpl.ParseFlowSequenceEntryMappingEnd());
/* 830 */           return ParserImpl.this.parseFlowNode();
/*     */         } 
/* 832 */         ParserImpl.this.state = new ParserImpl.ParseFlowSequenceEntryMappingEnd();
/* 833 */         return ParserImpl.this.processEmptyScalar(token1.getEndMark());
/*     */       } 
/*     */       
/* 836 */       ParserImpl.this.state = new ParserImpl.ParseFlowSequenceEntryMappingEnd();
/* 837 */       Token token = ParserImpl.this.scanner.peekToken();
/* 838 */       return ParserImpl.this.processEmptyScalar(token.getStartMark());
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseFlowSequenceEntryMappingEnd implements Production {
/*     */     private ParseFlowSequenceEntryMappingEnd() {}
/*     */     
/*     */     public Event produce() {
/* 846 */       ParserImpl.this.state = new ParserImpl.ParseFlowSequenceEntry(false);
/* 847 */       Token token = ParserImpl.this.scanner.peekToken();
/* 848 */       return (Event)new MappingEndEvent(token.getStartMark(), token.getEndMark());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class ParseFlowMappingFirstKey
/*     */     implements Production
/*     */   {
/*     */     private ParseFlowMappingFirstKey() {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Event produce() {
/* 864 */       Token token = ParserImpl.this.scanner.getToken();
/* 865 */       ParserImpl.this.marks.push(token.getStartMark());
/* 866 */       return (new ParserImpl.ParseFlowMappingKey(true)).produce();
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseFlowMappingKey
/*     */     implements Production {
/*     */     private final boolean first;
/*     */     
/*     */     public ParseFlowMappingKey(boolean first) {
/* 875 */       this.first = first;
/*     */     }
/*     */     
/*     */     public Event produce() {
/* 879 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 880 */         ParserImpl.this.state = new ParseFlowMappingKey(this.first);
/* 881 */         return (Event)ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
/*     */       } 
/* 883 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowMappingEnd })) {
/* 884 */         if (!this.first) {
/* 885 */           if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowEntry })) {
/* 886 */             ParserImpl.this.scanner.getToken();
/* 887 */             if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 888 */               ParserImpl.this.state = new ParseFlowMappingKey(true);
/* 889 */               return (Event)ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
/*     */             } 
/*     */           } else {
/* 892 */             Token token1 = ParserImpl.this.scanner.peekToken();
/* 893 */             throw new ParserException("while parsing a flow mapping", (Mark)ParserImpl.this.marks.pop(), "expected ',' or '}', but got " + token1
/* 894 */                 .getTokenId(), token1.getStartMark());
/*     */           } 
/*     */         }
/* 897 */         if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Key })) {
/* 898 */           Token token1 = ParserImpl.this.scanner.getToken();
/* 899 */           if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowMappingEnd })) {
/* 900 */             ParserImpl.this.states.push(new ParserImpl.ParseFlowMappingValue());
/* 901 */             return ParserImpl.this.parseFlowNode();
/*     */           } 
/* 903 */           ParserImpl.this.state = new ParserImpl.ParseFlowMappingValue();
/* 904 */           return ParserImpl.this.processEmptyScalar(token1.getEndMark());
/*     */         } 
/* 906 */         if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowMappingEnd })) {
/* 907 */           ParserImpl.this.states.push(new ParserImpl.ParseFlowMappingEmptyValue());
/* 908 */           return ParserImpl.this.parseFlowNode();
/*     */         } 
/*     */       } 
/* 911 */       Token token = ParserImpl.this.scanner.getToken();
/* 912 */       MappingEndEvent mappingEndEvent = new MappingEndEvent(token.getStartMark(), token.getEndMark());
/* 913 */       ParserImpl.this.marks.pop();
/* 914 */       if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Comment })) {
/* 915 */         ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
/*     */       } else {
/* 917 */         ParserImpl.this.state = new ParserImpl.ParseFlowEndComment();
/*     */       } 
/* 919 */       return (Event)mappingEndEvent;
/*     */     } }
/*     */   
/*     */   private class ParseFlowMappingValue implements Production {
/*     */     private ParseFlowMappingValue() {}
/*     */     
/*     */     public Event produce() {
/* 926 */       if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Value })) {
/* 927 */         Token token1 = ParserImpl.this.scanner.getToken();
/* 928 */         if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowEntry, Token.ID.FlowMappingEnd })) {
/* 929 */           ParserImpl.this.states.push(new ParserImpl.ParseFlowMappingKey(false));
/* 930 */           return ParserImpl.this.parseFlowNode();
/*     */         } 
/* 932 */         ParserImpl.this.state = new ParserImpl.ParseFlowMappingKey(false);
/* 933 */         return ParserImpl.this.processEmptyScalar(token1.getEndMark());
/*     */       } 
/*     */       
/* 936 */       ParserImpl.this.state = new ParserImpl.ParseFlowMappingKey(false);
/* 937 */       Token token = ParserImpl.this.scanner.peekToken();
/* 938 */       return ParserImpl.this.processEmptyScalar(token.getStartMark());
/*     */     }
/*     */   }
/*     */   
/*     */   private class ParseFlowMappingEmptyValue implements Production {
/*     */     private ParseFlowMappingEmptyValue() {}
/*     */     
/*     */     public Event produce() {
/* 946 */       ParserImpl.this.state = new ParserImpl.ParseFlowMappingKey(false);
/* 947 */       return ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
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
/*     */   private Event processEmptyScalar(Mark mark) {
/* 960 */     return (Event)new ScalarEvent(null, null, new ImplicitTuple(true, false), "", mark, mark, DumperOptions.ScalarStyle.PLAIN);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\parser\ParserImpl.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */