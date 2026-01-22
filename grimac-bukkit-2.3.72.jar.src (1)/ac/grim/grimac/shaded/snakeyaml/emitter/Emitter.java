/*      */ package ac.grim.grimac.shaded.snakeyaml.emitter;
/*      */ 
/*      */ import ac.grim.grimac.shaded.snakeyaml.DumperOptions;
/*      */ import ac.grim.grimac.shaded.snakeyaml.comments.CommentEventsCollector;
/*      */ import ac.grim.grimac.shaded.snakeyaml.comments.CommentLine;
/*      */ import ac.grim.grimac.shaded.snakeyaml.comments.CommentType;
/*      */ import ac.grim.grimac.shaded.snakeyaml.error.YAMLException;
/*      */ import ac.grim.grimac.shaded.snakeyaml.events.CollectionStartEvent;
/*      */ import ac.grim.grimac.shaded.snakeyaml.events.DocumentEndEvent;
/*      */ import ac.grim.grimac.shaded.snakeyaml.events.DocumentStartEvent;
/*      */ import ac.grim.grimac.shaded.snakeyaml.events.Event;
/*      */ import ac.grim.grimac.shaded.snakeyaml.events.MappingStartEvent;
/*      */ import ac.grim.grimac.shaded.snakeyaml.events.NodeEvent;
/*      */ import ac.grim.grimac.shaded.snakeyaml.events.ScalarEvent;
/*      */ import ac.grim.grimac.shaded.snakeyaml.events.SequenceStartEvent;
/*      */ import ac.grim.grimac.shaded.snakeyaml.reader.StreamReader;
/*      */ import ac.grim.grimac.shaded.snakeyaml.scanner.Constant;
/*      */ import ac.grim.grimac.shaded.snakeyaml.util.ArrayStack;
/*      */ import java.io.IOException;
/*      */ import java.io.Writer;
/*      */ import java.util.ArrayDeque;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Queue;
/*      */ import java.util.Set;
/*      */ import java.util.TreeSet;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
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
/*      */ public final class Emitter
/*      */   implements Emitable
/*      */ {
/*      */   public static final int MIN_INDENT = 1;
/*      */   public static final int MAX_INDENT = 10;
/*   78 */   private static final char[] SPACE = new char[] { ' ' };
/*      */   
/*   80 */   private static final Pattern SPACES_PATTERN = Pattern.compile("\\s");
/*   81 */   private static final Set<Character> INVALID_ANCHOR = new HashSet<>();
/*      */   
/*      */   static {
/*   84 */     INVALID_ANCHOR.add(Character.valueOf('['));
/*   85 */     INVALID_ANCHOR.add(Character.valueOf(']'));
/*   86 */     INVALID_ANCHOR.add(Character.valueOf('{'));
/*   87 */     INVALID_ANCHOR.add(Character.valueOf('}'));
/*   88 */     INVALID_ANCHOR.add(Character.valueOf(','));
/*   89 */     INVALID_ANCHOR.add(Character.valueOf('*'));
/*   90 */     INVALID_ANCHOR.add(Character.valueOf('&'));
/*      */   }
/*      */   
/*   93 */   private static final Map<Character, String> ESCAPE_REPLACEMENTS = new HashMap<>();
/*      */ 
/*      */   
/*      */   static {
/*   97 */     ESCAPE_REPLACEMENTS.put(Character.valueOf(false), "0");
/*   98 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('\007'), "a");
/*   99 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('\b'), "b");
/*  100 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('\t'), "t");
/*  101 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('\n'), "n");
/*  102 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('\013'), "v");
/*  103 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('\f'), "f");
/*  104 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('\r'), "r");
/*  105 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('\033'), "e");
/*  106 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('"'), "\"");
/*  107 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('\\'), "\\");
/*  108 */     ESCAPE_REPLACEMENTS.put(Character.valueOf(''), "N");
/*  109 */     ESCAPE_REPLACEMENTS.put(Character.valueOf(' '), "_");
/*  110 */     ESCAPE_REPLACEMENTS.put(Character.valueOf(' '), "L");
/*  111 */     ESCAPE_REPLACEMENTS.put(Character.valueOf(' '), "P");
/*      */   }
/*      */   
/*  114 */   private static final Map<String, String> DEFAULT_TAG_PREFIXES = new LinkedHashMap<>();
/*      */   private final Writer stream;
/*      */   
/*      */   static {
/*  118 */     DEFAULT_TAG_PREFIXES.put("!", "!");
/*  119 */     DEFAULT_TAG_PREFIXES.put("tag:yaml.org,2002:", "!!");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final ArrayStack<EmitterState> states;
/*      */ 
/*      */   
/*      */   private EmitterState state;
/*      */ 
/*      */   
/*      */   private final Queue<Event> events;
/*      */ 
/*      */   
/*      */   private Event event;
/*      */ 
/*      */   
/*      */   private final ArrayStack<Integer> indents;
/*      */ 
/*      */   
/*      */   private Integer indent;
/*      */ 
/*      */   
/*      */   private int flowLevel;
/*      */ 
/*      */   
/*      */   private boolean rootContext;
/*      */ 
/*      */   
/*      */   private boolean mappingContext;
/*      */ 
/*      */   
/*      */   private boolean simpleKeyContext;
/*      */   
/*      */   private int column;
/*      */   
/*      */   private boolean whitespace;
/*      */   
/*      */   private boolean indention;
/*      */   
/*      */   private boolean openEnded;
/*      */   
/*      */   private final Boolean canonical;
/*      */   
/*      */   private final Boolean prettyFlow;
/*      */   
/*      */   private final boolean allowUnicode;
/*      */   
/*      */   private int bestIndent;
/*      */   
/*      */   private final int indicatorIndent;
/*      */   
/*      */   private final boolean indentWithIndicator;
/*      */   
/*      */   private int bestWidth;
/*      */   
/*      */   private final char[] bestLineBreak;
/*      */   
/*      */   private final boolean splitLines;
/*      */   
/*      */   private final int maxSimpleKeyLength;
/*      */   
/*      */   private final boolean emitComments;
/*      */   
/*      */   private Map<String, String> tagPrefixes;
/*      */   
/*      */   private String preparedAnchor;
/*      */   
/*      */   private String preparedTag;
/*      */   
/*      */   private ScalarAnalysis analysis;
/*      */   
/*      */   private DumperOptions.ScalarStyle style;
/*      */   
/*      */   private final CommentEventsCollector blockCommentsCollector;
/*      */   
/*      */   private final CommentEventsCollector inlineCommentsCollector;
/*      */ 
/*      */   
/*      */   public Emitter(Writer stream, DumperOptions opts) {
/*  199 */     if (stream == null) {
/*  200 */       throw new NullPointerException("Writer must be provided.");
/*      */     }
/*  202 */     if (opts == null) {
/*  203 */       throw new NullPointerException("DumperOptions must be provided.");
/*      */     }
/*      */     
/*  206 */     this.stream = stream;
/*      */     
/*  208 */     this.states = new ArrayStack(100);
/*  209 */     this.state = new ExpectStreamStart();
/*      */     
/*  211 */     this.events = new ArrayDeque<>(100);
/*  212 */     this.event = null;
/*      */     
/*  214 */     this.indents = new ArrayStack(10);
/*  215 */     this.indent = null;
/*      */     
/*  217 */     this.flowLevel = 0;
/*      */     
/*  219 */     this.mappingContext = false;
/*  220 */     this.simpleKeyContext = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  228 */     this.column = 0;
/*  229 */     this.whitespace = true;
/*  230 */     this.indention = true;
/*      */ 
/*      */     
/*  233 */     this.openEnded = false;
/*      */ 
/*      */     
/*  236 */     this.canonical = Boolean.valueOf(opts.isCanonical());
/*  237 */     this.prettyFlow = Boolean.valueOf(opts.isPrettyFlow());
/*  238 */     this.allowUnicode = opts.isAllowUnicode();
/*  239 */     this.bestIndent = 2;
/*  240 */     if (opts.getIndent() > 1 && opts.getIndent() < 10) {
/*  241 */       this.bestIndent = opts.getIndent();
/*      */     }
/*  243 */     this.indicatorIndent = opts.getIndicatorIndent();
/*  244 */     this.indentWithIndicator = opts.getIndentWithIndicator();
/*  245 */     this.bestWidth = 80;
/*  246 */     if (opts.getWidth() > this.bestIndent * 2) {
/*  247 */       this.bestWidth = opts.getWidth();
/*      */     }
/*  249 */     this.bestLineBreak = opts.getLineBreak().getString().toCharArray();
/*  250 */     this.splitLines = opts.getSplitLines();
/*  251 */     this.maxSimpleKeyLength = opts.getMaxSimpleKeyLength();
/*  252 */     this.emitComments = opts.isProcessComments();
/*      */ 
/*      */     
/*  255 */     this.tagPrefixes = new LinkedHashMap<>();
/*      */ 
/*      */     
/*  258 */     this.preparedAnchor = null;
/*  259 */     this.preparedTag = null;
/*      */ 
/*      */     
/*  262 */     this.analysis = null;
/*  263 */     this.style = null;
/*      */ 
/*      */     
/*  266 */     this.blockCommentsCollector = new CommentEventsCollector(this.events, new CommentType[] { CommentType.BLANK_LINE, CommentType.BLOCK });
/*      */     
/*  268 */     this.inlineCommentsCollector = new CommentEventsCollector(this.events, new CommentType[] { CommentType.IN_LINE });
/*      */   }
/*      */   
/*      */   public void emit(Event event) throws IOException {
/*  272 */     this.events.add(event);
/*  273 */     while (!needMoreEvents()) {
/*  274 */       this.event = this.events.poll();
/*  275 */       this.state.expect();
/*  276 */       this.event = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean needMoreEvents() {
/*  283 */     if (this.events.isEmpty()) {
/*  284 */       return true;
/*      */     }
/*      */     
/*  287 */     Iterator<Event> iter = this.events.iterator();
/*  288 */     Event event = iter.next();
/*  289 */     while (event instanceof ac.grim.grimac.shaded.snakeyaml.events.CommentEvent) {
/*  290 */       if (!iter.hasNext()) {
/*  291 */         return true;
/*      */       }
/*  293 */       event = iter.next();
/*      */     } 
/*      */     
/*  296 */     if (event instanceof DocumentStartEvent)
/*  297 */       return needEvents(iter, 1); 
/*  298 */     if (event instanceof SequenceStartEvent)
/*  299 */       return needEvents(iter, 2); 
/*  300 */     if (event instanceof MappingStartEvent)
/*  301 */       return needEvents(iter, 3); 
/*  302 */     if (event instanceof ac.grim.grimac.shaded.snakeyaml.events.StreamStartEvent)
/*  303 */       return needEvents(iter, 2); 
/*  304 */     if (event instanceof ac.grim.grimac.shaded.snakeyaml.events.StreamEndEvent)
/*  305 */       return false; 
/*  306 */     if (this.emitComments) {
/*  307 */       return needEvents(iter, 1);
/*      */     }
/*  309 */     return false;
/*      */   }
/*      */   
/*      */   private boolean needEvents(Iterator<Event> iter, int count) {
/*  313 */     int level = 0;
/*  314 */     int actualCount = 0;
/*  315 */     while (iter.hasNext()) {
/*  316 */       Event event = iter.next();
/*  317 */       if (event instanceof ac.grim.grimac.shaded.snakeyaml.events.CommentEvent) {
/*      */         continue;
/*      */       }
/*  320 */       actualCount++;
/*  321 */       if (event instanceof DocumentStartEvent || event instanceof CollectionStartEvent) {
/*  322 */         level++;
/*  323 */       } else if (event instanceof DocumentEndEvent || event instanceof ac.grim.grimac.shaded.snakeyaml.events.CollectionEndEvent) {
/*  324 */         level--;
/*  325 */       } else if (event instanceof ac.grim.grimac.shaded.snakeyaml.events.StreamEndEvent) {
/*  326 */         level = -1;
/*      */       } 
/*  328 */       if (level < 0) {
/*  329 */         return false;
/*      */       }
/*      */     } 
/*  332 */     return (actualCount < count);
/*      */   }
/*      */   
/*      */   private void increaseIndent(boolean flow, boolean indentless) {
/*  336 */     this.indents.push(this.indent);
/*  337 */     if (this.indent == null) {
/*  338 */       if (flow) {
/*  339 */         this.indent = Integer.valueOf(this.bestIndent);
/*      */       } else {
/*  341 */         this.indent = Integer.valueOf(0);
/*      */       } 
/*  343 */     } else if (!indentless) {
/*  344 */       this.indent = Integer.valueOf(this.indent.intValue() + this.bestIndent);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private class ExpectStreamStart
/*      */     implements EmitterState
/*      */   {
/*      */     private ExpectStreamStart() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  355 */       if (Emitter.this.event instanceof ac.grim.grimac.shaded.snakeyaml.events.StreamStartEvent) {
/*  356 */         Emitter.this.writeStreamStart();
/*  357 */         Emitter.this.state = new Emitter.ExpectFirstDocumentStart();
/*      */       } else {
/*  359 */         throw new EmitterException("expected StreamStartEvent, but got " + Emitter.this.event);
/*      */       } 
/*      */     } }
/*      */   
/*      */   private class ExpectNothing implements EmitterState {
/*      */     private ExpectNothing() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  367 */       throw new EmitterException("expecting nothing, but got " + Emitter.this.event);
/*      */     }
/*      */   }
/*      */   
/*      */   private class ExpectFirstDocumentStart
/*      */     implements EmitterState {
/*      */     private ExpectFirstDocumentStart() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  376 */       (new Emitter.ExpectDocumentStart(true)).expect();
/*      */     }
/*      */   }
/*      */   
/*      */   private class ExpectDocumentStart
/*      */     implements EmitterState {
/*      */     private final boolean first;
/*      */     
/*      */     public ExpectDocumentStart(boolean first) {
/*  385 */       this.first = first;
/*      */     }
/*      */     
/*      */     public void expect() throws IOException {
/*  389 */       if (Emitter.this.event instanceof DocumentStartEvent) {
/*  390 */         DocumentStartEvent ev = (DocumentStartEvent)Emitter.this.event;
/*  391 */         if ((ev.getVersion() != null || ev.getTags() != null) && Emitter.this.openEnded) {
/*  392 */           Emitter.this.writeIndicator("...", true, false, false);
/*  393 */           Emitter.this.writeIndent();
/*      */         } 
/*  395 */         if (ev.getVersion() != null) {
/*  396 */           String versionText = Emitter.this.prepareVersion(ev.getVersion());
/*  397 */           Emitter.this.writeVersionDirective(versionText);
/*      */         } 
/*  399 */         Emitter.this.tagPrefixes = (Map)new LinkedHashMap<>(Emitter.DEFAULT_TAG_PREFIXES);
/*  400 */         if (ev.getTags() != null) {
/*  401 */           Set<String> handles = new TreeSet<>(ev.getTags().keySet());
/*  402 */           for (String handle : handles) {
/*  403 */             String prefix = (String)ev.getTags().get(handle);
/*  404 */             Emitter.this.tagPrefixes.put(prefix, handle);
/*  405 */             String handleText = Emitter.this.prepareTagHandle(handle);
/*  406 */             String prefixText = Emitter.this.prepareTagPrefix(prefix);
/*  407 */             Emitter.this.writeTagDirective(handleText, prefixText);
/*      */           } 
/*      */         } 
/*      */         
/*  411 */         boolean implicit = (this.first && !ev.getExplicit() && !Emitter.this.canonical.booleanValue() && ev.getVersion() == null && (ev.getTags() == null || ev.getTags().isEmpty()) && !Emitter.this.checkEmptyDocument());
/*  412 */         if (!implicit) {
/*  413 */           Emitter.this.writeIndent();
/*  414 */           Emitter.this.writeIndicator("---", true, false, false);
/*  415 */           if (Emitter.this.canonical.booleanValue()) {
/*  416 */             Emitter.this.writeIndent();
/*      */           }
/*      */         } 
/*  419 */         Emitter.this.state = new Emitter.ExpectDocumentRoot();
/*  420 */       } else if (Emitter.this.event instanceof ac.grim.grimac.shaded.snakeyaml.events.StreamEndEvent) {
/*  421 */         Emitter.this.writeStreamEnd();
/*  422 */         Emitter.this.state = new Emitter.ExpectNothing();
/*  423 */       } else if (Emitter.this.event instanceof ac.grim.grimac.shaded.snakeyaml.events.CommentEvent) {
/*  424 */         Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
/*  425 */         Emitter.this.writeBlockComment();
/*      */       } else {
/*      */         
/*  428 */         throw new EmitterException("expected DocumentStartEvent, but got " + Emitter.this.event);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private class ExpectDocumentEnd
/*      */     implements EmitterState {
/*      */     public void expect() throws IOException {
/*  436 */       Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
/*  437 */       Emitter.this.writeBlockComment();
/*  438 */       if (Emitter.this.event instanceof DocumentEndEvent) {
/*  439 */         Emitter.this.writeIndent();
/*  440 */         if (((DocumentEndEvent)Emitter.this.event).getExplicit()) {
/*  441 */           Emitter.this.writeIndicator("...", true, false, false);
/*  442 */           Emitter.this.writeIndent();
/*      */         } 
/*  444 */         Emitter.this.flushStream();
/*  445 */         Emitter.this.state = new Emitter.ExpectDocumentStart(false);
/*      */       } else {
/*  447 */         throw new EmitterException("expected DocumentEndEvent, but got " + Emitter.this.event);
/*      */       } 
/*      */     }
/*      */     private ExpectDocumentEnd() {} }
/*      */   
/*      */   private class ExpectDocumentRoot implements EmitterState { private ExpectDocumentRoot() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  455 */       Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
/*  456 */       if (!Emitter.this.blockCommentsCollector.isEmpty()) {
/*  457 */         Emitter.this.writeBlockComment();
/*  458 */         if (Emitter.this.event instanceof DocumentEndEvent) {
/*  459 */           (new Emitter.ExpectDocumentEnd()).expect();
/*      */           return;
/*      */         } 
/*      */       } 
/*  463 */       Emitter.this.states.push(new Emitter.ExpectDocumentEnd());
/*  464 */       Emitter.this.expectNode(true, false, false);
/*      */     } }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void expectNode(boolean root, boolean mapping, boolean simpleKey) throws IOException {
/*  471 */     this.rootContext = root;
/*  472 */     this.mappingContext = mapping;
/*  473 */     this.simpleKeyContext = simpleKey;
/*  474 */     if (this.event instanceof ac.grim.grimac.shaded.snakeyaml.events.AliasEvent) {
/*  475 */       expectAlias();
/*  476 */     } else if (this.event instanceof ScalarEvent || this.event instanceof CollectionStartEvent) {
/*  477 */       processAnchor("&");
/*  478 */       processTag();
/*  479 */       if (this.event instanceof ScalarEvent) {
/*  480 */         expectScalar();
/*  481 */       } else if (this.event instanceof SequenceStartEvent) {
/*  482 */         if (this.flowLevel != 0 || this.canonical.booleanValue() || ((SequenceStartEvent)this.event).isFlow() || 
/*  483 */           checkEmptySequence()) {
/*  484 */           expectFlowSequence();
/*      */         } else {
/*  486 */           expectBlockSequence();
/*      */         }
/*      */       
/*  489 */       } else if (this.flowLevel != 0 || this.canonical.booleanValue() || ((MappingStartEvent)this.event).isFlow() || 
/*  490 */         checkEmptyMapping()) {
/*  491 */         expectFlowMapping();
/*      */       } else {
/*  493 */         expectBlockMapping();
/*      */       } 
/*      */     } else {
/*      */       
/*  497 */       throw new EmitterException("expected NodeEvent, but got " + this.event);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void expectAlias() throws IOException {
/*  502 */     if (!(this.event instanceof ac.grim.grimac.shaded.snakeyaml.events.AliasEvent)) {
/*  503 */       throw new EmitterException("Alias must be provided");
/*      */     }
/*  505 */     processAnchor("*");
/*  506 */     this.state = (EmitterState)this.states.pop();
/*      */   }
/*      */   
/*      */   private void expectScalar() throws IOException {
/*  510 */     increaseIndent(true, false);
/*  511 */     processScalar();
/*  512 */     this.indent = (Integer)this.indents.pop();
/*  513 */     this.state = (EmitterState)this.states.pop();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void expectFlowSequence() throws IOException {
/*  519 */     writeIndicator("[", true, true, false);
/*  520 */     this.flowLevel++;
/*  521 */     increaseIndent(true, false);
/*  522 */     if (this.prettyFlow.booleanValue()) {
/*  523 */       writeIndent();
/*      */     }
/*  525 */     this.state = new ExpectFirstFlowSequenceItem();
/*      */   }
/*      */   
/*      */   private class ExpectFirstFlowSequenceItem implements EmitterState { private ExpectFirstFlowSequenceItem() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  531 */       if (Emitter.this.event instanceof ac.grim.grimac.shaded.snakeyaml.events.SequenceEndEvent) {
/*  532 */         Emitter.this.indent = (Integer)Emitter.this.indents.pop();
/*  533 */         Emitter.this.flowLevel--;
/*  534 */         Emitter.this.writeIndicator("]", false, false, false);
/*  535 */         Emitter.this.inlineCommentsCollector.collectEvents();
/*  536 */         Emitter.this.writeInlineComments();
/*  537 */         Emitter.this.state = (EmitterState)Emitter.this.states.pop();
/*  538 */       } else if (Emitter.this.event instanceof ac.grim.grimac.shaded.snakeyaml.events.CommentEvent) {
/*  539 */         Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
/*  540 */         Emitter.this.writeBlockComment();
/*      */       } else {
/*  542 */         if (Emitter.this.canonical.booleanValue() || (Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines) || Emitter.this.prettyFlow.booleanValue()) {
/*  543 */           Emitter.this.writeIndent();
/*      */         }
/*  545 */         Emitter.this.states.push(new Emitter.ExpectFlowSequenceItem());
/*  546 */         Emitter.this.expectNode(false, false, false);
/*  547 */         Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
/*  548 */         Emitter.this.writeInlineComments();
/*      */       } 
/*      */     } }
/*      */   
/*      */   private class ExpectFlowSequenceItem implements EmitterState {
/*      */     private ExpectFlowSequenceItem() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  556 */       if (Emitter.this.event instanceof ac.grim.grimac.shaded.snakeyaml.events.SequenceEndEvent) {
/*  557 */         Emitter.this.indent = (Integer)Emitter.this.indents.pop();
/*  558 */         Emitter.this.flowLevel--;
/*  559 */         if (Emitter.this.canonical.booleanValue()) {
/*  560 */           Emitter.this.writeIndicator(",", false, false, false);
/*  561 */           Emitter.this.writeIndent();
/*  562 */         } else if (Emitter.this.prettyFlow.booleanValue()) {
/*  563 */           Emitter.this.writeIndent();
/*      */         } 
/*  565 */         Emitter.this.writeIndicator("]", false, false, false);
/*  566 */         Emitter.this.inlineCommentsCollector.collectEvents();
/*  567 */         Emitter.this.writeInlineComments();
/*  568 */         if (Emitter.this.prettyFlow.booleanValue()) {
/*  569 */           Emitter.this.writeIndent();
/*      */         }
/*  571 */         Emitter.this.state = (EmitterState)Emitter.this.states.pop();
/*  572 */       } else if (Emitter.this.event instanceof ac.grim.grimac.shaded.snakeyaml.events.CommentEvent) {
/*  573 */         Emitter.this.event = Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
/*      */       } else {
/*  575 */         Emitter.this.writeIndicator(",", false, false, false);
/*  576 */         Emitter.this.writeBlockComment();
/*  577 */         if (Emitter.this.canonical.booleanValue() || (Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines) || Emitter.this.prettyFlow.booleanValue()) {
/*  578 */           Emitter.this.writeIndent();
/*      */         }
/*  580 */         Emitter.this.states.push(new ExpectFlowSequenceItem());
/*  581 */         Emitter.this.expectNode(false, false, false);
/*  582 */         Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
/*  583 */         Emitter.this.writeInlineComments();
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void expectFlowMapping() throws IOException {
/*  591 */     writeIndicator("{", true, true, false);
/*  592 */     this.flowLevel++;
/*  593 */     increaseIndent(true, false);
/*  594 */     if (this.prettyFlow.booleanValue()) {
/*  595 */       writeIndent();
/*      */     }
/*  597 */     this.state = new ExpectFirstFlowMappingKey();
/*      */   }
/*      */   
/*      */   private class ExpectFirstFlowMappingKey implements EmitterState { private ExpectFirstFlowMappingKey() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  603 */       Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
/*  604 */       Emitter.this.writeBlockComment();
/*  605 */       if (Emitter.this.event instanceof ac.grim.grimac.shaded.snakeyaml.events.MappingEndEvent) {
/*  606 */         Emitter.this.indent = (Integer)Emitter.this.indents.pop();
/*  607 */         Emitter.this.flowLevel--;
/*  608 */         Emitter.this.writeIndicator("}", false, false, false);
/*  609 */         Emitter.this.inlineCommentsCollector.collectEvents();
/*  610 */         Emitter.this.writeInlineComments();
/*  611 */         Emitter.this.state = (EmitterState)Emitter.this.states.pop();
/*      */       } else {
/*  613 */         if (Emitter.this.canonical.booleanValue() || (Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines) || Emitter.this.prettyFlow.booleanValue()) {
/*  614 */           Emitter.this.writeIndent();
/*      */         }
/*  616 */         if (!Emitter.this.canonical.booleanValue() && Emitter.this.checkSimpleKey()) {
/*  617 */           Emitter.this.states.push(new Emitter.ExpectFlowMappingSimpleValue());
/*  618 */           Emitter.this.expectNode(false, true, true);
/*      */         } else {
/*  620 */           Emitter.this.writeIndicator("?", true, false, false);
/*  621 */           Emitter.this.states.push(new Emitter.ExpectFlowMappingValue());
/*  622 */           Emitter.this.expectNode(false, true, false);
/*      */         } 
/*      */       } 
/*      */     } }
/*      */   
/*      */   private class ExpectFlowMappingKey implements EmitterState {
/*      */     private ExpectFlowMappingKey() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  631 */       if (Emitter.this.event instanceof ac.grim.grimac.shaded.snakeyaml.events.MappingEndEvent) {
/*  632 */         Emitter.this.indent = (Integer)Emitter.this.indents.pop();
/*  633 */         Emitter.this.flowLevel--;
/*  634 */         if (Emitter.this.canonical.booleanValue()) {
/*  635 */           Emitter.this.writeIndicator(",", false, false, false);
/*  636 */           Emitter.this.writeIndent();
/*      */         } 
/*  638 */         if (Emitter.this.prettyFlow.booleanValue()) {
/*  639 */           Emitter.this.writeIndent();
/*      */         }
/*  641 */         Emitter.this.writeIndicator("}", false, false, false);
/*  642 */         Emitter.this.inlineCommentsCollector.collectEvents();
/*  643 */         Emitter.this.writeInlineComments();
/*  644 */         Emitter.this.state = (EmitterState)Emitter.this.states.pop();
/*      */       } else {
/*  646 */         Emitter.this.writeIndicator(",", false, false, false);
/*  647 */         Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
/*  648 */         Emitter.this.writeBlockComment();
/*  649 */         if (Emitter.this.canonical.booleanValue() || (Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines) || Emitter.this.prettyFlow.booleanValue()) {
/*  650 */           Emitter.this.writeIndent();
/*      */         }
/*  652 */         if (!Emitter.this.canonical.booleanValue() && Emitter.this.checkSimpleKey()) {
/*  653 */           Emitter.this.states.push(new Emitter.ExpectFlowMappingSimpleValue());
/*  654 */           Emitter.this.expectNode(false, true, true);
/*      */         } else {
/*  656 */           Emitter.this.writeIndicator("?", true, false, false);
/*  657 */           Emitter.this.states.push(new Emitter.ExpectFlowMappingValue());
/*  658 */           Emitter.this.expectNode(false, true, false);
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private class ExpectFlowMappingSimpleValue implements EmitterState { private ExpectFlowMappingSimpleValue() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  667 */       Emitter.this.writeIndicator(":", false, false, false);
/*  668 */       Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
/*  669 */       Emitter.this.writeInlineComments();
/*  670 */       Emitter.this.states.push(new Emitter.ExpectFlowMappingKey());
/*  671 */       Emitter.this.expectNode(false, true, false);
/*  672 */       Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
/*  673 */       Emitter.this.writeInlineComments();
/*      */     } }
/*      */   
/*      */   private class ExpectFlowMappingValue implements EmitterState {
/*      */     private ExpectFlowMappingValue() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  680 */       if (Emitter.this.canonical.booleanValue() || Emitter.this.column > Emitter.this.bestWidth || Emitter.this.prettyFlow.booleanValue()) {
/*  681 */         Emitter.this.writeIndent();
/*      */       }
/*  683 */       Emitter.this.writeIndicator(":", true, false, false);
/*  684 */       Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
/*  685 */       Emitter.this.writeInlineComments();
/*  686 */       Emitter.this.states.push(new Emitter.ExpectFlowMappingKey());
/*  687 */       Emitter.this.expectNode(false, true, false);
/*  688 */       Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
/*  689 */       Emitter.this.writeInlineComments();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void expectBlockSequence() throws IOException {
/*  696 */     boolean indentless = (this.mappingContext && !this.indention);
/*  697 */     increaseIndent(false, indentless);
/*  698 */     this.state = new ExpectFirstBlockSequenceItem();
/*      */   }
/*      */   
/*      */   private class ExpectFirstBlockSequenceItem implements EmitterState { private ExpectFirstBlockSequenceItem() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  704 */       (new Emitter.ExpectBlockSequenceItem(true)).expect();
/*      */     } }
/*      */ 
/*      */   
/*      */   private class ExpectBlockSequenceItem
/*      */     implements EmitterState {
/*      */     private final boolean first;
/*      */     
/*      */     public ExpectBlockSequenceItem(boolean first) {
/*  713 */       this.first = first;
/*      */     }
/*      */     
/*      */     public void expect() throws IOException {
/*  717 */       if (!this.first && Emitter.this.event instanceof ac.grim.grimac.shaded.snakeyaml.events.SequenceEndEvent) {
/*  718 */         Emitter.this.indent = (Integer)Emitter.this.indents.pop();
/*  719 */         Emitter.this.state = (EmitterState)Emitter.this.states.pop();
/*  720 */       } else if (Emitter.this.event instanceof ac.grim.grimac.shaded.snakeyaml.events.CommentEvent) {
/*  721 */         Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
/*      */       } else {
/*  723 */         Emitter.this.writeIndent();
/*  724 */         if (!Emitter.this.indentWithIndicator || this.first) {
/*  725 */           Emitter.this.writeWhitespace(Emitter.this.indicatorIndent);
/*      */         }
/*  727 */         Emitter.this.writeIndicator("-", true, false, true);
/*  728 */         if (Emitter.this.indentWithIndicator && this.first) {
/*  729 */           Emitter.this.indent = Integer.valueOf(Emitter.this.indent.intValue() + Emitter.this.indicatorIndent);
/*      */         }
/*  731 */         if (!Emitter.this.blockCommentsCollector.isEmpty()) {
/*  732 */           Emitter.this.increaseIndent(false, false);
/*  733 */           Emitter.this.writeBlockComment();
/*  734 */           if (Emitter.this.event instanceof ScalarEvent) {
/*  735 */             Emitter.this.analysis = Emitter.this.analyzeScalar(((ScalarEvent)Emitter.this.event).getValue());
/*  736 */             if (!Emitter.this.analysis.isEmpty()) {
/*  737 */               Emitter.this.writeIndent();
/*      */             }
/*      */           } 
/*  740 */           Emitter.this.indent = (Integer)Emitter.this.indents.pop();
/*      */         } 
/*  742 */         Emitter.this.states.push(new ExpectBlockSequenceItem(false));
/*  743 */         Emitter.this.expectNode(false, false, false);
/*  744 */         Emitter.this.inlineCommentsCollector.collectEvents();
/*  745 */         Emitter.this.writeInlineComments();
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void expectBlockMapping() throws IOException {
/*  752 */     increaseIndent(false, false);
/*  753 */     this.state = new ExpectFirstBlockMappingKey();
/*      */   }
/*      */   
/*      */   private class ExpectFirstBlockMappingKey implements EmitterState { private ExpectFirstBlockMappingKey() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  759 */       (new Emitter.ExpectBlockMappingKey(true)).expect();
/*      */     } }
/*      */ 
/*      */   
/*      */   private class ExpectBlockMappingKey
/*      */     implements EmitterState {
/*      */     private final boolean first;
/*      */     
/*      */     public ExpectBlockMappingKey(boolean first) {
/*  768 */       this.first = first;
/*      */     }
/*      */     
/*      */     public void expect() throws IOException {
/*  772 */       Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
/*  773 */       Emitter.this.writeBlockComment();
/*  774 */       if (!this.first && Emitter.this.event instanceof ac.grim.grimac.shaded.snakeyaml.events.MappingEndEvent) {
/*  775 */         Emitter.this.indent = (Integer)Emitter.this.indents.pop();
/*  776 */         Emitter.this.state = (EmitterState)Emitter.this.states.pop();
/*      */       } else {
/*  778 */         Emitter.this.writeIndent();
/*  779 */         if (Emitter.this.checkSimpleKey()) {
/*  780 */           Emitter.this.states.push(new Emitter.ExpectBlockMappingSimpleValue());
/*  781 */           Emitter.this.expectNode(false, true, true);
/*      */         } else {
/*  783 */           Emitter.this.writeIndicator("?", true, false, true);
/*  784 */           Emitter.this.states.push(new Emitter.ExpectBlockMappingValue());
/*  785 */           Emitter.this.expectNode(false, true, false);
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private boolean isFoldedOrLiteral(Event event) {
/*  792 */     if (!event.is(Event.ID.Scalar)) {
/*  793 */       return false;
/*      */     }
/*  795 */     ScalarEvent scalarEvent = (ScalarEvent)event;
/*  796 */     DumperOptions.ScalarStyle style = scalarEvent.getScalarStyle();
/*  797 */     return (style == DumperOptions.ScalarStyle.FOLDED || style == DumperOptions.ScalarStyle.LITERAL);
/*      */   }
/*      */   
/*      */   private class ExpectBlockMappingSimpleValue implements EmitterState { private ExpectBlockMappingSimpleValue() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  803 */       Emitter.this.writeIndicator(":", false, false, false);
/*  804 */       Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
/*  805 */       if (!Emitter.this.isFoldedOrLiteral(Emitter.this.event) && 
/*  806 */         Emitter.this.writeInlineComments()) {
/*  807 */         Emitter.this.increaseIndent(true, false);
/*  808 */         Emitter.this.writeIndent();
/*  809 */         Emitter.this.indent = (Integer)Emitter.this.indents.pop();
/*      */       } 
/*      */       
/*  812 */       Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
/*  813 */       if (!Emitter.this.blockCommentsCollector.isEmpty()) {
/*  814 */         Emitter.this.increaseIndent(true, false);
/*  815 */         Emitter.this.writeBlockComment();
/*  816 */         Emitter.this.writeIndent();
/*  817 */         Emitter.this.indent = (Integer)Emitter.this.indents.pop();
/*      */       } 
/*  819 */       Emitter.this.states.push(new Emitter.ExpectBlockMappingKey(false));
/*  820 */       Emitter.this.expectNode(false, true, false);
/*  821 */       Emitter.this.inlineCommentsCollector.collectEvents();
/*  822 */       Emitter.this.writeInlineComments();
/*      */     } }
/*      */   
/*      */   private class ExpectBlockMappingValue implements EmitterState {
/*      */     private ExpectBlockMappingValue() {}
/*      */     
/*      */     public void expect() throws IOException {
/*  829 */       Emitter.this.writeIndent();
/*  830 */       Emitter.this.writeIndicator(":", true, false, true);
/*  831 */       Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
/*  832 */       Emitter.this.writeInlineComments();
/*  833 */       Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
/*  834 */       Emitter.this.writeBlockComment();
/*  835 */       Emitter.this.states.push(new Emitter.ExpectBlockMappingKey(false));
/*  836 */       Emitter.this.expectNode(false, true, false);
/*  837 */       Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
/*  838 */       Emitter.this.writeInlineComments();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean checkEmptySequence() {
/*  845 */     return (this.event instanceof SequenceStartEvent && !this.events.isEmpty() && this.events
/*  846 */       .peek() instanceof ac.grim.grimac.shaded.snakeyaml.events.SequenceEndEvent);
/*      */   }
/*      */   
/*      */   private boolean checkEmptyMapping() {
/*  850 */     return (this.event instanceof MappingStartEvent && !this.events.isEmpty() && this.events
/*  851 */       .peek() instanceof ac.grim.grimac.shaded.snakeyaml.events.MappingEndEvent);
/*      */   }
/*      */   
/*      */   private boolean checkEmptyDocument() {
/*  855 */     if (!(this.event instanceof DocumentStartEvent) || this.events.isEmpty()) {
/*  856 */       return false;
/*      */     }
/*  858 */     Event event = this.events.peek();
/*  859 */     if (event instanceof ScalarEvent) {
/*  860 */       ScalarEvent e = (ScalarEvent)event;
/*  861 */       return (e.getAnchor() == null && e.getTag() == null && e.getImplicit() != null && e
/*  862 */         .getValue().length() == 0);
/*      */     } 
/*  864 */     return false;
/*      */   }
/*      */   
/*      */   private boolean checkSimpleKey() {
/*  868 */     int length = 0;
/*  869 */     if (this.event instanceof NodeEvent && ((NodeEvent)this.event).getAnchor() != null) {
/*  870 */       if (this.preparedAnchor == null) {
/*  871 */         this.preparedAnchor = prepareAnchor(((NodeEvent)this.event).getAnchor());
/*      */       }
/*  873 */       length += this.preparedAnchor.length();
/*      */     } 
/*  875 */     String tag = null;
/*  876 */     if (this.event instanceof ScalarEvent) {
/*  877 */       tag = ((ScalarEvent)this.event).getTag();
/*  878 */     } else if (this.event instanceof CollectionStartEvent) {
/*  879 */       tag = ((CollectionStartEvent)this.event).getTag();
/*      */     } 
/*  881 */     if (tag != null) {
/*  882 */       if (this.preparedTag == null) {
/*  883 */         this.preparedTag = prepareTag(tag);
/*      */       }
/*  885 */       length += this.preparedTag.length();
/*      */     } 
/*  887 */     if (this.event instanceof ScalarEvent) {
/*  888 */       if (this.analysis == null) {
/*  889 */         this.analysis = analyzeScalar(((ScalarEvent)this.event).getValue());
/*      */       }
/*  891 */       length += this.analysis.getScalar().length();
/*      */     } 
/*  893 */     return (length < this.maxSimpleKeyLength && (this.event instanceof ac.grim.grimac.shaded.snakeyaml.events.AliasEvent || (this.event instanceof ScalarEvent && 
/*  894 */       !this.analysis.isEmpty() && !this.analysis.isMultiline()) || 
/*  895 */       checkEmptySequence() || checkEmptyMapping()));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void processAnchor(String indicator) throws IOException {
/*  901 */     NodeEvent ev = (NodeEvent)this.event;
/*  902 */     if (ev.getAnchor() == null) {
/*  903 */       this.preparedAnchor = null;
/*      */       return;
/*      */     } 
/*  906 */     if (this.preparedAnchor == null) {
/*  907 */       this.preparedAnchor = prepareAnchor(ev.getAnchor());
/*      */     }
/*  909 */     writeIndicator(indicator + this.preparedAnchor, true, false, false);
/*  910 */     this.preparedAnchor = null;
/*      */   }
/*      */   
/*      */   private void processTag() throws IOException {
/*  914 */     String tag = null;
/*  915 */     if (this.event instanceof ScalarEvent) {
/*  916 */       ScalarEvent ev = (ScalarEvent)this.event;
/*  917 */       tag = ev.getTag();
/*  918 */       if (this.style == null) {
/*  919 */         this.style = chooseScalarStyle();
/*      */       }
/*  921 */       if ((!this.canonical.booleanValue() || tag == null) && ((this.style == null && ev
/*  922 */         .getImplicit().canOmitTagInPlainScalar()) || (this.style != null && ev
/*  923 */         .getImplicit().canOmitTagInNonPlainScalar()))) {
/*  924 */         this.preparedTag = null;
/*      */         return;
/*      */       } 
/*  927 */       if (ev.getImplicit().canOmitTagInPlainScalar() && tag == null) {
/*  928 */         tag = "!";
/*  929 */         this.preparedTag = null;
/*      */       } 
/*      */     } else {
/*  932 */       CollectionStartEvent ev = (CollectionStartEvent)this.event;
/*  933 */       tag = ev.getTag();
/*  934 */       if ((!this.canonical.booleanValue() || tag == null) && ev.getImplicit()) {
/*  935 */         this.preparedTag = null;
/*      */         return;
/*      */       } 
/*      */     } 
/*  939 */     if (tag == null) {
/*  940 */       throw new EmitterException("tag is not specified");
/*      */     }
/*  942 */     if (this.preparedTag == null) {
/*  943 */       this.preparedTag = prepareTag(tag);
/*      */     }
/*  945 */     writeIndicator(this.preparedTag, true, false, false);
/*  946 */     this.preparedTag = null;
/*      */   }
/*      */   
/*      */   private DumperOptions.ScalarStyle chooseScalarStyle() {
/*  950 */     ScalarEvent ev = (ScalarEvent)this.event;
/*  951 */     if (this.analysis == null) {
/*  952 */       this.analysis = analyzeScalar(ev.getValue());
/*      */     }
/*  954 */     if ((!ev.isPlain() && ev.getScalarStyle() == DumperOptions.ScalarStyle.DOUBLE_QUOTED) || this.canonical
/*  955 */       .booleanValue()) {
/*  956 */       return DumperOptions.ScalarStyle.DOUBLE_QUOTED;
/*      */     }
/*  958 */     if (ev.isPlain() && ev.getImplicit().canOmitTagInPlainScalar() && (
/*  959 */       !this.simpleKeyContext || (!this.analysis.isEmpty() && !this.analysis.isMultiline())) && ((this.flowLevel != 0 && this.analysis
/*  960 */       .isAllowFlowPlain()) || (this.flowLevel == 0 && this.analysis
/*  961 */       .isAllowBlockPlain()))) {
/*  962 */       return null;
/*      */     }
/*      */     
/*  965 */     if (!ev.isPlain() && (ev.getScalarStyle() == DumperOptions.ScalarStyle.LITERAL || ev
/*  966 */       .getScalarStyle() == DumperOptions.ScalarStyle.FOLDED) && 
/*  967 */       this.flowLevel == 0 && !this.simpleKeyContext && this.analysis.isAllowBlock()) {
/*  968 */       return ev.getScalarStyle();
/*      */     }
/*      */     
/*  971 */     if ((ev.isPlain() || ev.getScalarStyle() == DumperOptions.ScalarStyle.SINGLE_QUOTED) && 
/*  972 */       this.analysis.isAllowSingleQuoted() && (!this.simpleKeyContext || !this.analysis.isMultiline())) {
/*  973 */       return DumperOptions.ScalarStyle.SINGLE_QUOTED;
/*      */     }
/*      */     
/*  976 */     return DumperOptions.ScalarStyle.DOUBLE_QUOTED;
/*      */   }
/*      */   
/*      */   private void processScalar() throws IOException {
/*  980 */     ScalarEvent ev = (ScalarEvent)this.event;
/*  981 */     if (this.analysis == null) {
/*  982 */       this.analysis = analyzeScalar(ev.getValue());
/*      */     }
/*  984 */     if (this.style == null) {
/*  985 */       this.style = chooseScalarStyle();
/*      */     }
/*  987 */     boolean split = (!this.simpleKeyContext && this.splitLines);
/*  988 */     if (this.style == null) {
/*  989 */       writePlain(this.analysis.getScalar(), split);
/*      */     } else {
/*  991 */       switch (this.style) {
/*      */         case DOUBLE_QUOTED:
/*  993 */           writeDoubleQuoted(this.analysis.getScalar(), split);
/*      */           break;
/*      */         case SINGLE_QUOTED:
/*  996 */           writeSingleQuoted(this.analysis.getScalar(), split);
/*      */           break;
/*      */         case FOLDED:
/*  999 */           writeFolded(this.analysis.getScalar(), split);
/*      */           break;
/*      */         case LITERAL:
/* 1002 */           writeLiteral(this.analysis.getScalar());
/*      */           break;
/*      */         default:
/* 1005 */           throw new YAMLException("Unexpected style: " + this.style);
/*      */       } 
/*      */     } 
/* 1008 */     this.analysis = null;
/* 1009 */     this.style = null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private String prepareVersion(DumperOptions.Version version) {
/* 1015 */     if (version.major() != 1) {
/* 1016 */       throw new EmitterException("unsupported YAML version: " + version);
/*      */     }
/* 1018 */     return version.getRepresentation();
/*      */   }
/*      */   
/* 1021 */   private static final Pattern HANDLE_FORMAT = Pattern.compile("^![-_\\w]*!$");
/*      */   
/*      */   private String prepareTagHandle(String handle) {
/* 1024 */     if (handle.length() == 0)
/* 1025 */       throw new EmitterException("tag handle must not be empty"); 
/* 1026 */     if (handle.charAt(0) != '!' || handle.charAt(handle.length() - 1) != '!')
/* 1027 */       throw new EmitterException("tag handle must start and end with '!': " + handle); 
/* 1028 */     if (!"!".equals(handle) && !HANDLE_FORMAT.matcher(handle).matches()) {
/* 1029 */       throw new EmitterException("invalid character in the tag handle: " + handle);
/*      */     }
/* 1031 */     return handle;
/*      */   }
/*      */   
/*      */   private String prepareTagPrefix(String prefix) {
/* 1035 */     if (prefix.length() == 0) {
/* 1036 */       throw new EmitterException("tag prefix must not be empty");
/*      */     }
/* 1038 */     StringBuilder chunks = new StringBuilder();
/* 1039 */     int start = 0;
/* 1040 */     int end = 0;
/* 1041 */     if (prefix.charAt(0) == '!') {
/* 1042 */       end = 1;
/*      */     }
/* 1044 */     while (end < prefix.length()) {
/* 1045 */       end++;
/*      */     }
/* 1047 */     if (start < end) {
/* 1048 */       chunks.append(prefix, start, end);
/*      */     }
/* 1050 */     return chunks.toString();
/*      */   }
/*      */   
/*      */   private String prepareTag(String tag) {
/* 1054 */     if (tag.length() == 0) {
/* 1055 */       throw new EmitterException("tag must not be empty");
/*      */     }
/* 1057 */     if ("!".equals(tag)) {
/* 1058 */       return tag;
/*      */     }
/* 1060 */     String handle = null;
/* 1061 */     String suffix = tag;
/*      */     
/* 1063 */     for (String prefix : this.tagPrefixes.keySet()) {
/* 1064 */       if (tag.startsWith(prefix) && ("!".equals(prefix) || prefix.length() < tag.length())) {
/* 1065 */         handle = prefix;
/*      */       }
/*      */     } 
/* 1068 */     if (handle != null) {
/* 1069 */       suffix = tag.substring(handle.length());
/* 1070 */       handle = this.tagPrefixes.get(handle);
/*      */     } 
/*      */     
/* 1073 */     int end = suffix.length();
/* 1074 */     String suffixText = (end > 0) ? suffix.substring(0, end) : "";
/*      */     
/* 1076 */     if (handle != null) {
/* 1077 */       return handle + suffixText;
/*      */     }
/* 1079 */     return "!<" + suffixText + ">";
/*      */   }
/*      */   
/*      */   static String prepareAnchor(String anchor) {
/* 1083 */     if (anchor.length() == 0) {
/* 1084 */       throw new EmitterException("anchor must not be empty");
/*      */     }
/* 1086 */     for (Character invalid : INVALID_ANCHOR) {
/* 1087 */       if (anchor.indexOf(invalid.charValue()) > -1) {
/* 1088 */         throw new EmitterException("Invalid character '" + invalid + "' in the anchor: " + anchor);
/*      */       }
/*      */     } 
/* 1091 */     Matcher matcher = SPACES_PATTERN.matcher(anchor);
/* 1092 */     if (matcher.find()) {
/* 1093 */       throw new EmitterException("Anchor may not contain spaces: " + anchor);
/*      */     }
/* 1095 */     return anchor;
/*      */   }
/*      */ 
/*      */   
/*      */   private static boolean hasLeadingZero(String scalar) {
/* 1100 */     if (scalar.length() > 1 && scalar.charAt(0) == '0') {
/* 1101 */       for (int i = 1; i < scalar.length(); i++) {
/* 1102 */         char ch = scalar.charAt(i);
/* 1103 */         boolean isDigitOrUnderscore = ((ch >= '0' && ch <= '9') || ch == '_');
/* 1104 */         if (!isDigitOrUnderscore) {
/* 1105 */           return false;
/*      */         }
/*      */       } 
/* 1108 */       return true;
/*      */     } 
/* 1110 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private ScalarAnalysis analyzeScalar(String scalar) {
/* 1115 */     if (scalar.length() == 0) {
/* 1116 */       return new ScalarAnalysis(scalar, true, false, false, true, true, false);
/*      */     }
/*      */     
/* 1119 */     boolean blockIndicators = false;
/* 1120 */     boolean flowIndicators = false;
/* 1121 */     boolean lineBreaks = false;
/* 1122 */     boolean specialCharacters = false;
/* 1123 */     boolean leadingZeroNumber = hasLeadingZero(scalar);
/*      */ 
/*      */     
/* 1126 */     boolean leadingSpace = false;
/* 1127 */     boolean leadingBreak = false;
/* 1128 */     boolean trailingSpace = false;
/* 1129 */     boolean trailingBreak = false;
/* 1130 */     boolean breakSpace = false;
/* 1131 */     boolean spaceBreak = false;
/*      */ 
/*      */     
/* 1134 */     if (scalar.startsWith("---") || scalar.startsWith("...")) {
/* 1135 */       blockIndicators = true;
/* 1136 */       flowIndicators = true;
/*      */     } 
/*      */     
/* 1139 */     boolean preceededByWhitespace = true;
/*      */     
/* 1141 */     boolean followedByWhitespace = (scalar.length() == 1 || Constant.NULL_BL_T_LINEBR.has(scalar.codePointAt(1)));
/*      */     
/* 1143 */     boolean previousSpace = false;
/*      */ 
/*      */     
/* 1146 */     boolean previousBreak = false;
/*      */     
/* 1148 */     int index = 0;
/*      */     
/* 1150 */     while (index < scalar.length()) {
/* 1151 */       int c = scalar.codePointAt(index);
/*      */       
/* 1153 */       if (index == 0) {
/*      */         
/* 1155 */         if ("#,[]{}&*!|>'\"%@`".indexOf(c) != -1) {
/* 1156 */           flowIndicators = true;
/* 1157 */           blockIndicators = true;
/*      */         } 
/* 1159 */         if (c == 63 || c == 58) {
/* 1160 */           flowIndicators = true;
/* 1161 */           if (followedByWhitespace) {
/* 1162 */             blockIndicators = true;
/*      */           }
/*      */         } 
/* 1165 */         if (c == 45 && followedByWhitespace) {
/* 1166 */           flowIndicators = true;
/* 1167 */           blockIndicators = true;
/*      */         } 
/*      */       } else {
/*      */         
/* 1171 */         if (",?[]{}".indexOf(c) != -1) {
/* 1172 */           flowIndicators = true;
/*      */         }
/* 1174 */         if (c == 58) {
/* 1175 */           flowIndicators = true;
/* 1176 */           if (followedByWhitespace) {
/* 1177 */             blockIndicators = true;
/*      */           }
/*      */         } 
/* 1180 */         if (c == 35 && preceededByWhitespace) {
/* 1181 */           flowIndicators = true;
/* 1182 */           blockIndicators = true;
/*      */         } 
/*      */       } 
/*      */       
/* 1186 */       boolean isLineBreak = Constant.LINEBR.has(c);
/* 1187 */       if (isLineBreak) {
/* 1188 */         lineBreaks = true;
/*      */       }
/* 1190 */       if (c != 10 && (32 > c || c > 126)) {
/* 1191 */         if (c == 133 || (c >= 160 && c <= 55295) || (c >= 57344 && c <= 65533) || (c >= 65536 && c <= 1114111)) {
/*      */ 
/*      */           
/* 1194 */           if (!this.allowUnicode) {
/* 1195 */             specialCharacters = true;
/*      */           }
/*      */         } else {
/* 1198 */           specialCharacters = true;
/*      */         } 
/*      */       }
/*      */       
/* 1202 */       if (c == 32) {
/* 1203 */         if (index == 0) {
/* 1204 */           leadingSpace = true;
/*      */         }
/* 1206 */         if (index == scalar.length() - 1) {
/* 1207 */           trailingSpace = true;
/*      */         }
/* 1209 */         if (previousBreak) {
/* 1210 */           breakSpace = true;
/*      */         }
/* 1212 */         previousSpace = true;
/* 1213 */         previousBreak = false;
/* 1214 */       } else if (isLineBreak) {
/* 1215 */         if (index == 0) {
/* 1216 */           leadingBreak = true;
/*      */         }
/* 1218 */         if (index == scalar.length() - 1) {
/* 1219 */           trailingBreak = true;
/*      */         }
/* 1221 */         if (previousSpace) {
/* 1222 */           spaceBreak = true;
/*      */         }
/* 1224 */         previousSpace = false;
/* 1225 */         previousBreak = true;
/*      */       } else {
/* 1227 */         previousSpace = false;
/* 1228 */         previousBreak = false;
/*      */       } 
/*      */ 
/*      */       
/* 1232 */       index += Character.charCount(c);
/* 1233 */       preceededByWhitespace = (Constant.NULL_BL_T.has(c) || isLineBreak);
/* 1234 */       followedByWhitespace = true;
/* 1235 */       if (index + 1 < scalar.length()) {
/* 1236 */         int nextIndex = index + Character.charCount(scalar.codePointAt(index));
/* 1237 */         if (nextIndex < scalar.length())
/*      */         {
/* 1239 */           followedByWhitespace = (Constant.NULL_BL_T.has(scalar.codePointAt(nextIndex)) || isLineBreak);
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1244 */     boolean allowFlowPlain = true;
/* 1245 */     boolean allowBlockPlain = true;
/* 1246 */     boolean allowSingleQuoted = true;
/* 1247 */     boolean allowBlock = true;
/*      */     
/* 1249 */     if (leadingSpace || leadingBreak || trailingSpace || trailingBreak || leadingZeroNumber) {
/* 1250 */       allowFlowPlain = allowBlockPlain = false;
/*      */     }
/*      */     
/* 1253 */     if (trailingSpace) {
/* 1254 */       allowBlock = false;
/*      */     }
/*      */ 
/*      */     
/* 1258 */     if (breakSpace) {
/* 1259 */       allowFlowPlain = allowBlockPlain = allowSingleQuoted = false;
/*      */     }
/*      */ 
/*      */     
/* 1263 */     if (spaceBreak || specialCharacters) {
/* 1264 */       allowFlowPlain = allowBlockPlain = allowSingleQuoted = allowBlock = false;
/*      */     }
/*      */ 
/*      */     
/* 1268 */     if (lineBreaks) {
/* 1269 */       allowFlowPlain = false;
/*      */     }
/*      */     
/* 1272 */     if (flowIndicators) {
/* 1273 */       allowFlowPlain = false;
/*      */     }
/*      */     
/* 1276 */     if (blockIndicators) {
/* 1277 */       allowBlockPlain = false;
/*      */     }
/*      */     
/* 1280 */     return new ScalarAnalysis(scalar, false, lineBreaks, allowFlowPlain, allowBlockPlain, allowSingleQuoted, allowBlock);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void flushStream() throws IOException {
/* 1287 */     this.stream.flush();
/*      */   }
/*      */ 
/*      */   
/*      */   void writeStreamStart() {}
/*      */ 
/*      */   
/*      */   void writeStreamEnd() throws IOException {
/* 1295 */     flushStream();
/*      */   }
/*      */ 
/*      */   
/*      */   void writeIndicator(String indicator, boolean needWhitespace, boolean whitespace, boolean indentation) throws IOException {
/* 1300 */     if (!this.whitespace && needWhitespace) {
/* 1301 */       this.column++;
/* 1302 */       this.stream.write(SPACE);
/*      */     } 
/* 1304 */     this.whitespace = whitespace;
/* 1305 */     this.indention = (this.indention && indentation);
/* 1306 */     this.column += indicator.length();
/* 1307 */     this.openEnded = false;
/* 1308 */     this.stream.write(indicator);
/*      */   }
/*      */   
/*      */   void writeIndent() throws IOException {
/*      */     int indent;
/* 1313 */     if (this.indent != null) {
/* 1314 */       indent = this.indent.intValue();
/*      */     } else {
/* 1316 */       indent = 0;
/*      */     } 
/*      */     
/* 1319 */     if (!this.indention || this.column > indent || (this.column == indent && !this.whitespace)) {
/* 1320 */       writeLineBreak(null);
/*      */     }
/*      */     
/* 1323 */     writeWhitespace(indent - this.column);
/*      */   }
/*      */   
/*      */   private void writeWhitespace(int length) throws IOException {
/* 1327 */     if (length <= 0) {
/*      */       return;
/*      */     }
/* 1330 */     this.whitespace = true;
/* 1331 */     char[] data = new char[length];
/* 1332 */     for (int i = 0; i < data.length; i++) {
/* 1333 */       data[i] = ' ';
/*      */     }
/* 1335 */     this.column += length;
/* 1336 */     this.stream.write(data);
/*      */   }
/*      */   
/*      */   private void writeLineBreak(String data) throws IOException {
/* 1340 */     this.whitespace = true;
/* 1341 */     this.indention = true;
/* 1342 */     this.column = 0;
/* 1343 */     if (data == null) {
/* 1344 */       this.stream.write(this.bestLineBreak);
/*      */     } else {
/* 1346 */       this.stream.write(data);
/*      */     } 
/*      */   }
/*      */   
/*      */   void writeVersionDirective(String versionText) throws IOException {
/* 1351 */     this.stream.write("%YAML ");
/* 1352 */     this.stream.write(versionText);
/* 1353 */     writeLineBreak(null);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   void writeTagDirective(String handleText, String prefixText) throws IOException {
/* 1359 */     this.stream.write("%TAG ");
/* 1360 */     this.stream.write(handleText);
/* 1361 */     this.stream.write(SPACE);
/* 1362 */     this.stream.write(prefixText);
/* 1363 */     writeLineBreak(null);
/*      */   }
/*      */ 
/*      */   
/*      */   private void writeSingleQuoted(String text, boolean split) throws IOException {
/* 1368 */     writeIndicator("'", true, false, false);
/* 1369 */     boolean spaces = false;
/* 1370 */     boolean breaks = false;
/* 1371 */     int start = 0, end = 0;
/*      */     
/* 1373 */     while (end <= text.length()) {
/* 1374 */       char ch = Character.MIN_VALUE;
/* 1375 */       if (end < text.length()) {
/* 1376 */         ch = text.charAt(end);
/*      */       }
/* 1378 */       if (spaces) {
/* 1379 */         if (ch == '\000' || ch != ' ') {
/* 1380 */           if (start + 1 == end && this.column > this.bestWidth && split && start != 0 && end != text
/* 1381 */             .length()) {
/* 1382 */             writeIndent();
/*      */           } else {
/* 1384 */             int len = end - start;
/* 1385 */             this.column += len;
/* 1386 */             this.stream.write(text, start, len);
/*      */           } 
/* 1388 */           start = end;
/*      */         } 
/* 1390 */       } else if (breaks) {
/* 1391 */         if (ch == '\000' || Constant.LINEBR.hasNo(ch)) {
/* 1392 */           if (text.charAt(start) == '\n') {
/* 1393 */             writeLineBreak(null);
/*      */           }
/* 1395 */           String data = text.substring(start, end);
/* 1396 */           for (char br : data.toCharArray()) {
/* 1397 */             if (br == '\n') {
/* 1398 */               writeLineBreak(null);
/*      */             } else {
/* 1400 */               writeLineBreak(String.valueOf(br));
/*      */             } 
/*      */           } 
/* 1403 */           writeIndent();
/* 1404 */           start = end;
/*      */         }
/*      */       
/* 1407 */       } else if (Constant.LINEBR.has(ch, "\000 '") && 
/* 1408 */         start < end) {
/* 1409 */         int len = end - start;
/* 1410 */         this.column += len;
/* 1411 */         this.stream.write(text, start, len);
/* 1412 */         start = end;
/*      */       } 
/*      */ 
/*      */       
/* 1416 */       if (ch == '\'') {
/* 1417 */         this.column += 2;
/* 1418 */         this.stream.write("''");
/* 1419 */         start = end + 1;
/*      */       } 
/* 1421 */       if (ch != '\000') {
/* 1422 */         spaces = (ch == ' ');
/* 1423 */         breaks = Constant.LINEBR.has(ch);
/*      */       } 
/* 1425 */       end++;
/*      */     } 
/* 1427 */     writeIndicator("'", false, false, false);
/*      */   }
/*      */   
/*      */   private void writeDoubleQuoted(String text, boolean split) throws IOException {
/* 1431 */     writeIndicator("\"", true, false, false);
/* 1432 */     int start = 0;
/* 1433 */     int end = 0;
/* 1434 */     while (end <= text.length()) {
/* 1435 */       Character ch = null;
/* 1436 */       if (end < text.length()) {
/* 1437 */         ch = Character.valueOf(text.charAt(end));
/*      */       }
/* 1439 */       if (ch == null || "\"\\  ﻿".indexOf(ch.charValue()) != -1 || ' ' > ch
/* 1440 */         .charValue() || ch.charValue() > '~') {
/* 1441 */         if (start < end) {
/* 1442 */           int len = end - start;
/* 1443 */           this.column += len;
/* 1444 */           this.stream.write(text, start, len);
/* 1445 */           start = end;
/*      */         } 
/* 1447 */         if (ch != null) {
/*      */           String data;
/*      */           
/* 1450 */           if (ESCAPE_REPLACEMENTS.containsKey(ch)) {
/* 1451 */             data = "\\" + (String)ESCAPE_REPLACEMENTS.get(ch);
/*      */           } else {
/*      */             int codePoint;
/*      */             
/* 1455 */             if (Character.isHighSurrogate(ch.charValue()) && end + 1 < text.length()) {
/* 1456 */               char ch2 = text.charAt(end + 1);
/* 1457 */               codePoint = Character.toCodePoint(ch.charValue(), ch2);
/*      */             } else {
/* 1459 */               codePoint = ch.charValue();
/*      */             } 
/*      */             
/* 1462 */             if (this.allowUnicode && StreamReader.isPrintable(codePoint)) {
/* 1463 */               data = String.valueOf(Character.toChars(codePoint));
/*      */               
/* 1465 */               if (Character.charCount(codePoint) == 2) {
/* 1466 */                 end++;
/*      */               
/*      */               }
/*      */             
/*      */             }
/* 1471 */             else if (ch.charValue() <= 'ÿ') {
/* 1472 */               String s = "0" + Integer.toString(ch.charValue(), 16);
/* 1473 */               data = "\\x" + s.substring(s.length() - 2);
/* 1474 */             } else if (Character.charCount(codePoint) == 2) {
/* 1475 */               end++;
/* 1476 */               String s = "000" + Long.toHexString(codePoint);
/* 1477 */               data = "\\U" + s.substring(s.length() - 8);
/*      */             } else {
/* 1479 */               String s = "000" + Integer.toString(ch.charValue(), 16);
/* 1480 */               data = "\\u" + s.substring(s.length() - 4);
/*      */             } 
/*      */           } 
/*      */ 
/*      */           
/* 1485 */           this.column += data.length();
/* 1486 */           this.stream.write(data);
/* 1487 */           start = end + 1;
/*      */         } 
/*      */       } 
/* 1490 */       if (0 < end && end < text.length() - 1 && (ch.charValue() == ' ' || start >= end) && this.column + end - start > this.bestWidth && split) {
/*      */         String data;
/*      */         
/* 1493 */         if (start >= end) {
/* 1494 */           data = "\\";
/*      */         } else {
/* 1496 */           data = text.substring(start, end) + "\\";
/*      */         } 
/* 1498 */         if (start < end) {
/* 1499 */           start = end;
/*      */         }
/* 1501 */         this.column += data.length();
/* 1502 */         this.stream.write(data);
/* 1503 */         writeIndent();
/* 1504 */         this.whitespace = false;
/* 1505 */         this.indention = false;
/* 1506 */         if (text.charAt(start) == ' ') {
/* 1507 */           data = "\\";
/* 1508 */           this.column += data.length();
/* 1509 */           this.stream.write(data);
/*      */         } 
/*      */       } 
/* 1512 */       end++;
/*      */     } 
/* 1514 */     writeIndicator("\"", false, false, false);
/*      */   }
/*      */   
/*      */   private boolean writeCommentLines(List<CommentLine> commentLines) throws IOException {
/* 1518 */     boolean wroteComment = false;
/* 1519 */     if (this.emitComments) {
/* 1520 */       int indentColumns = 0;
/* 1521 */       boolean firstComment = true;
/* 1522 */       for (CommentLine commentLine : commentLines) {
/* 1523 */         if (commentLine.getCommentType() != CommentType.BLANK_LINE) {
/* 1524 */           if (firstComment) {
/* 1525 */             firstComment = false;
/* 1526 */             writeIndicator("#", (commentLine.getCommentType() == CommentType.IN_LINE), false, false);
/* 1527 */             indentColumns = (this.column > 0) ? (this.column - 1) : 0;
/*      */           } else {
/* 1529 */             writeWhitespace(indentColumns);
/* 1530 */             writeIndicator("#", false, false, false);
/*      */           } 
/* 1532 */           this.stream.write(commentLine.getValue());
/* 1533 */           writeLineBreak(null);
/*      */         } else {
/* 1535 */           writeLineBreak(null);
/* 1536 */           writeIndent();
/*      */         } 
/* 1538 */         wroteComment = true;
/*      */       } 
/*      */     } 
/* 1541 */     return wroteComment;
/*      */   }
/*      */   
/*      */   private void writeBlockComment() throws IOException {
/* 1545 */     if (!this.blockCommentsCollector.isEmpty()) {
/* 1546 */       writeIndent();
/* 1547 */       writeCommentLines(this.blockCommentsCollector.consume());
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean writeInlineComments() throws IOException {
/* 1552 */     return writeCommentLines(this.inlineCommentsCollector.consume());
/*      */   }
/*      */   
/*      */   private String determineBlockHints(String text) {
/* 1556 */     StringBuilder hints = new StringBuilder();
/* 1557 */     if (Constant.LINEBR.has(text.charAt(0), " ")) {
/* 1558 */       hints.append(this.bestIndent);
/*      */     }
/* 1560 */     char ch1 = text.charAt(text.length() - 1);
/* 1561 */     if (Constant.LINEBR.hasNo(ch1)) {
/* 1562 */       hints.append("-");
/* 1563 */     } else if (text.length() == 1 || Constant.LINEBR.has(text.charAt(text.length() - 2))) {
/* 1564 */       hints.append("+");
/*      */     } 
/* 1566 */     return hints.toString();
/*      */   }
/*      */   
/*      */   void writeFolded(String text, boolean split) throws IOException {
/* 1570 */     String hints = determineBlockHints(text);
/* 1571 */     writeIndicator(">" + hints, true, false, false);
/* 1572 */     if (hints.length() > 0 && hints.charAt(hints.length() - 1) == '+') {
/* 1573 */       this.openEnded = true;
/*      */     }
/* 1575 */     if (!writeInlineComments()) {
/* 1576 */       writeLineBreak(null);
/*      */     }
/* 1578 */     boolean leadingSpace = true;
/* 1579 */     boolean spaces = false;
/* 1580 */     boolean breaks = true;
/* 1581 */     int start = 0, end = 0;
/* 1582 */     while (end <= text.length()) {
/* 1583 */       char ch = Character.MIN_VALUE;
/* 1584 */       if (end < text.length()) {
/* 1585 */         ch = text.charAt(end);
/*      */       }
/* 1587 */       if (breaks) {
/* 1588 */         if (ch == '\000' || Constant.LINEBR.hasNo(ch)) {
/* 1589 */           if (!leadingSpace && ch != '\000' && ch != ' ' && text.charAt(start) == '\n') {
/* 1590 */             writeLineBreak(null);
/*      */           }
/* 1592 */           leadingSpace = (ch == ' ');
/* 1593 */           String data = text.substring(start, end);
/* 1594 */           for (char br : data.toCharArray()) {
/* 1595 */             if (br == '\n') {
/* 1596 */               writeLineBreak(null);
/*      */             } else {
/* 1598 */               writeLineBreak(String.valueOf(br));
/*      */             } 
/*      */           } 
/* 1601 */           if (ch != '\000') {
/* 1602 */             writeIndent();
/*      */           }
/* 1604 */           start = end;
/*      */         } 
/* 1606 */       } else if (spaces) {
/* 1607 */         if (ch != ' ') {
/* 1608 */           if (start + 1 == end && this.column > this.bestWidth && split) {
/* 1609 */             writeIndent();
/*      */           } else {
/* 1611 */             int len = end - start;
/* 1612 */             this.column += len;
/* 1613 */             this.stream.write(text, start, len);
/*      */           } 
/* 1615 */           start = end;
/*      */         }
/*      */       
/* 1618 */       } else if (Constant.LINEBR.has(ch, "\000 ")) {
/* 1619 */         int len = end - start;
/* 1620 */         this.column += len;
/* 1621 */         this.stream.write(text, start, len);
/* 1622 */         if (ch == '\000') {
/* 1623 */           writeLineBreak(null);
/*      */         }
/* 1625 */         start = end;
/*      */       } 
/*      */       
/* 1628 */       if (ch != '\000') {
/* 1629 */         breaks = Constant.LINEBR.has(ch);
/* 1630 */         spaces = (ch == ' ');
/*      */       } 
/* 1632 */       end++;
/*      */     } 
/*      */   }
/*      */   
/*      */   void writeLiteral(String text) throws IOException {
/* 1637 */     String hints = determineBlockHints(text);
/* 1638 */     writeIndicator("|" + hints, true, false, false);
/* 1639 */     if (hints.length() > 0 && hints.charAt(hints.length() - 1) == '+') {
/* 1640 */       this.openEnded = true;
/*      */     }
/* 1642 */     if (!writeInlineComments()) {
/* 1643 */       writeLineBreak(null);
/*      */     }
/* 1645 */     boolean breaks = true;
/* 1646 */     int start = 0, end = 0;
/* 1647 */     while (end <= text.length()) {
/* 1648 */       char ch = Character.MIN_VALUE;
/* 1649 */       if (end < text.length()) {
/* 1650 */         ch = text.charAt(end);
/*      */       }
/* 1652 */       if (breaks) {
/* 1653 */         if (ch == '\000' || Constant.LINEBR.hasNo(ch)) {
/* 1654 */           String data = text.substring(start, end);
/* 1655 */           for (char br : data.toCharArray()) {
/* 1656 */             if (br == '\n') {
/* 1657 */               writeLineBreak(null);
/*      */             } else {
/* 1659 */               writeLineBreak(String.valueOf(br));
/*      */             } 
/*      */           } 
/* 1662 */           if (ch != '\000') {
/* 1663 */             writeIndent();
/*      */           }
/* 1665 */           start = end;
/*      */         }
/*      */       
/* 1668 */       } else if (ch == '\000' || Constant.LINEBR.has(ch)) {
/* 1669 */         this.stream.write(text, start, end - start);
/* 1670 */         if (ch == '\000') {
/* 1671 */           writeLineBreak(null);
/*      */         }
/* 1673 */         start = end;
/*      */       } 
/*      */       
/* 1676 */       if (ch != '\000') {
/* 1677 */         breaks = Constant.LINEBR.has(ch);
/*      */       }
/* 1679 */       end++;
/*      */     } 
/*      */   }
/*      */   
/*      */   void writePlain(String text, boolean split) throws IOException {
/* 1684 */     if (this.rootContext) {
/* 1685 */       this.openEnded = true;
/*      */     }
/* 1687 */     if (text.length() == 0) {
/*      */       return;
/*      */     }
/* 1690 */     if (!this.whitespace) {
/* 1691 */       this.column++;
/* 1692 */       this.stream.write(SPACE);
/*      */     } 
/* 1694 */     this.whitespace = false;
/* 1695 */     this.indention = false;
/* 1696 */     boolean spaces = false;
/* 1697 */     boolean breaks = false;
/* 1698 */     int start = 0, end = 0;
/* 1699 */     while (end <= text.length()) {
/* 1700 */       char ch = Character.MIN_VALUE;
/* 1701 */       if (end < text.length()) {
/* 1702 */         ch = text.charAt(end);
/*      */       }
/* 1704 */       if (spaces) {
/* 1705 */         if (ch != ' ') {
/* 1706 */           if (start + 1 == end && this.column > this.bestWidth && split) {
/* 1707 */             writeIndent();
/* 1708 */             this.whitespace = false;
/* 1709 */             this.indention = false;
/*      */           } else {
/* 1711 */             int len = end - start;
/* 1712 */             this.column += len;
/* 1713 */             this.stream.write(text, start, len);
/*      */           } 
/* 1715 */           start = end;
/*      */         } 
/* 1717 */       } else if (breaks) {
/* 1718 */         if (Constant.LINEBR.hasNo(ch)) {
/* 1719 */           if (text.charAt(start) == '\n') {
/* 1720 */             writeLineBreak(null);
/*      */           }
/* 1722 */           String data = text.substring(start, end);
/* 1723 */           for (char br : data.toCharArray()) {
/* 1724 */             if (br == '\n') {
/* 1725 */               writeLineBreak(null);
/*      */             } else {
/* 1727 */               writeLineBreak(String.valueOf(br));
/*      */             } 
/*      */           } 
/* 1730 */           writeIndent();
/* 1731 */           this.whitespace = false;
/* 1732 */           this.indention = false;
/* 1733 */           start = end;
/*      */         }
/*      */       
/* 1736 */       } else if (Constant.LINEBR.has(ch, "\000 ")) {
/* 1737 */         int len = end - start;
/* 1738 */         this.column += len;
/* 1739 */         this.stream.write(text, start, len);
/* 1740 */         start = end;
/*      */       } 
/*      */       
/* 1743 */       if (ch != '\000') {
/* 1744 */         spaces = (ch == ' ');
/* 1745 */         breaks = Constant.LINEBR.has(ch);
/*      */       } 
/* 1747 */       end++;
/*      */     } 
/*      */   }
/*      */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\emitter\Emitter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */