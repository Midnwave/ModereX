/*      */ package ac.grim.grimac.shaded.snakeyaml.scanner;
/*      */ 
/*      */ import ac.grim.grimac.shaded.snakeyaml.DumperOptions;
/*      */ import ac.grim.grimac.shaded.snakeyaml.LoaderOptions;
/*      */ import ac.grim.grimac.shaded.snakeyaml.comments.CommentType;
/*      */ import ac.grim.grimac.shaded.snakeyaml.error.Mark;
/*      */ import ac.grim.grimac.shaded.snakeyaml.error.YAMLException;
/*      */ import ac.grim.grimac.shaded.snakeyaml.reader.StreamReader;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.AliasToken;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.AnchorToken;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.BlockEndToken;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.BlockEntryToken;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.BlockMappingStartToken;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.BlockSequenceStartToken;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.CommentToken;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.DirectiveToken;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.DocumentEndToken;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.DocumentStartToken;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.FlowEntryToken;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.FlowMappingEndToken;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.FlowMappingStartToken;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.FlowSequenceEndToken;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.FlowSequenceStartToken;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.KeyToken;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.ScalarToken;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.StreamEndToken;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.StreamStartToken;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.TagToken;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.TagTuple;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.Token;
/*      */ import ac.grim.grimac.shaded.snakeyaml.tokens.ValueToken;
/*      */ import ac.grim.grimac.shaded.snakeyaml.util.ArrayStack;
/*      */ import ac.grim.grimac.shaded.snakeyaml.util.UriEncoder;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.charset.CharacterCodingException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class ScannerImpl
/*      */   implements Scanner
/*      */ {
/*   89 */   private static final Pattern NOT_HEXA = Pattern.compile("[^0-9A-Fa-f]");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   99 */   public static final Map<Character, String> ESCAPE_REPLACEMENTS = new HashMap<>();
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
/*  114 */   public static final Map<Character, Integer> ESCAPE_CODES = new HashMap<>();
/*      */   private final StreamReader reader;
/*      */   
/*      */   static {
/*  118 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('0'), "\000");
/*      */     
/*  120 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('a'), "\007");
/*      */     
/*  122 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('b'), "\b");
/*      */     
/*  124 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('t'), "\t");
/*      */     
/*  126 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('n'), "\n");
/*      */     
/*  128 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('v'), "\013");
/*      */     
/*  130 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('f'), "\f");
/*      */     
/*  132 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('r'), "\r");
/*      */     
/*  134 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('e'), "\033");
/*      */     
/*  136 */     ESCAPE_REPLACEMENTS.put(Character.valueOf(' '), " ");
/*      */     
/*  138 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('"'), "\"");
/*      */     
/*  140 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('\\'), "\\");
/*      */     
/*  142 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('N'), "");
/*      */     
/*  144 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('_'), " ");
/*      */     
/*  146 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('L'), " ");
/*      */     
/*  148 */     ESCAPE_REPLACEMENTS.put(Character.valueOf('P'), " ");
/*      */ 
/*      */     
/*  151 */     ESCAPE_CODES.put(Character.valueOf('x'), Integer.valueOf(2));
/*      */     
/*  153 */     ESCAPE_CODES.put(Character.valueOf('u'), Integer.valueOf(4));
/*      */     
/*  155 */     ESCAPE_CODES.put(Character.valueOf('U'), Integer.valueOf(8));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean done = false;
/*      */ 
/*      */   
/*  163 */   private int flowLevel = 0;
/*      */ 
/*      */   
/*      */   private final List<Token> tokens;
/*      */ 
/*      */   
/*      */   private Token lastToken;
/*      */ 
/*      */   
/*  172 */   private int tokensTaken = 0;
/*      */ 
/*      */   
/*  175 */   private int indent = -1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final ArrayStack<Integer> indents;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final boolean parseComments;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final LoaderOptions loaderOptions;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean allowSimpleKey = true;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Map<Integer, SimpleKey> possibleSimpleKeys;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ScannerImpl(StreamReader reader, LoaderOptions options) {
/*  220 */     if (options == null) {
/*  221 */       throw new NullPointerException("LoaderOptions must be provided.");
/*      */     }
/*  223 */     this.parseComments = options.isProcessComments();
/*  224 */     this.reader = reader;
/*  225 */     this.tokens = new ArrayList<>(100);
/*  226 */     this.indents = new ArrayStack(10);
/*      */     
/*  228 */     this.possibleSimpleKeys = new LinkedHashMap<>();
/*  229 */     this.loaderOptions = options;
/*  230 */     fetchStreamStart();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean checkToken(Token.ID... choices) {
/*  237 */     while (needMoreTokens()) {
/*  238 */       fetchMoreTokens();
/*      */     }
/*  240 */     if (!this.tokens.isEmpty()) {
/*  241 */       if (choices.length == 0) {
/*  242 */         return true;
/*      */       }
/*      */ 
/*      */       
/*  246 */       Token.ID first = ((Token)this.tokens.get(0)).getTokenId();
/*  247 */       for (int i = 0; i < choices.length; i++) {
/*  248 */         if (first == choices[i]) {
/*  249 */           return true;
/*      */         }
/*      */       } 
/*      */     } 
/*  253 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Token peekToken() {
/*  260 */     while (needMoreTokens()) {
/*  261 */       fetchMoreTokens();
/*      */     }
/*  263 */     return this.tokens.get(0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Token getToken() {
/*  270 */     this.tokensTaken++;
/*  271 */     return this.tokens.remove(0);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void addToken(Token token) {
/*  277 */     this.lastToken = token;
/*  278 */     this.tokens.add(token);
/*      */   }
/*      */   
/*      */   private void addToken(int index, Token token) {
/*  282 */     if (index == this.tokens.size()) {
/*  283 */       this.lastToken = token;
/*      */     }
/*  285 */     this.tokens.add(index, token);
/*      */   }
/*      */   
/*      */   private void addAllTokens(List<Token> tokens) {
/*  289 */     this.lastToken = tokens.get(tokens.size() - 1);
/*  290 */     this.tokens.addAll(tokens);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean needMoreTokens() {
/*  298 */     if (this.done) {
/*  299 */       return false;
/*      */     }
/*      */     
/*  302 */     if (this.tokens.isEmpty()) {
/*  303 */       return true;
/*      */     }
/*      */ 
/*      */     
/*  307 */     stalePossibleSimpleKeys();
/*  308 */     return (nextPossibleSimpleKey() == this.tokensTaken);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchMoreTokens() {
/*  315 */     if (this.reader.getDocumentIndex() > this.loaderOptions.getCodePointLimit()) {
/*  316 */       throw new YAMLException("The incoming YAML document exceeds the limit: " + this.loaderOptions
/*  317 */           .getCodePointLimit() + " code points.");
/*      */     }
/*      */     
/*  320 */     scanToNextToken();
/*      */     
/*  322 */     stalePossibleSimpleKeys();
/*      */ 
/*      */     
/*  325 */     unwindIndent(this.reader.getColumn());
/*      */ 
/*      */     
/*  328 */     int c = this.reader.peek();
/*  329 */     switch (c) {
/*      */       
/*      */       case 0:
/*  332 */         fetchStreamEnd();
/*      */         return;
/*      */       
/*      */       case 37:
/*  336 */         if (checkDirective()) {
/*  337 */           fetchDirective();
/*      */           return;
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 45:
/*  343 */         if (checkDocumentStart()) {
/*  344 */           fetchDocumentStart();
/*      */           return;
/*      */         } 
/*  347 */         if (checkBlockEntry()) {
/*  348 */           fetchBlockEntry();
/*      */           return;
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 46:
/*  354 */         if (checkDocumentEnd()) {
/*  355 */           fetchDocumentEnd();
/*      */           return;
/*      */         } 
/*      */         break;
/*      */ 
/*      */       
/*      */       case 91:
/*  362 */         fetchFlowSequenceStart();
/*      */         return;
/*      */       
/*      */       case 123:
/*  366 */         fetchFlowMappingStart();
/*      */         return;
/*      */       
/*      */       case 93:
/*  370 */         fetchFlowSequenceEnd();
/*      */         return;
/*      */       
/*      */       case 125:
/*  374 */         fetchFlowMappingEnd();
/*      */         return;
/*      */       
/*      */       case 44:
/*  378 */         fetchFlowEntry();
/*      */         return;
/*      */ 
/*      */       
/*      */       case 63:
/*  383 */         if (checkKey()) {
/*  384 */           fetchKey();
/*      */           return;
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 58:
/*  390 */         if (checkValue()) {
/*  391 */           fetchValue();
/*      */           return;
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 42:
/*  397 */         fetchAlias();
/*      */         return;
/*      */       
/*      */       case 38:
/*  401 */         fetchAnchor();
/*      */         return;
/*      */       
/*      */       case 33:
/*  405 */         fetchTag();
/*      */         return;
/*      */       
/*      */       case 124:
/*  409 */         if (this.flowLevel == 0) {
/*  410 */           fetchLiteral();
/*      */           return;
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 62:
/*  416 */         if (this.flowLevel == 0) {
/*  417 */           fetchFolded();
/*      */           return;
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 39:
/*  423 */         fetchSingle();
/*      */         return;
/*      */       
/*      */       case 34:
/*  427 */         fetchDouble();
/*      */         return;
/*      */     } 
/*      */     
/*  431 */     if (checkPlain()) {
/*  432 */       fetchPlain();
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  438 */     String chRepresentation = escapeChar(String.valueOf(Character.toChars(c)));
/*  439 */     if (c == 9) {
/*  440 */       chRepresentation = chRepresentation + "(TAB)";
/*      */     }
/*  442 */     String text = String.format("found character '%s' that cannot start any token. (Do not use %s for indentation)", new Object[] { chRepresentation, chRepresentation });
/*      */ 
/*      */     
/*  445 */     throw new ScannerException("while scanning for the next token", null, text, this.reader.getMark());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String escapeChar(String chRepresentation) {
/*  452 */     for (Character s : ESCAPE_REPLACEMENTS.keySet()) {
/*  453 */       String v = ESCAPE_REPLACEMENTS.get(s);
/*  454 */       if (v.equals(chRepresentation)) {
/*  455 */         return "\\" + s;
/*      */       }
/*      */     } 
/*  458 */     return chRepresentation;
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
/*      */   private int nextPossibleSimpleKey() {
/*  472 */     if (!this.possibleSimpleKeys.isEmpty()) {
/*  473 */       return ((SimpleKey)this.possibleSimpleKeys.values().iterator().next()).getTokenNumber();
/*      */     }
/*  475 */     return -1;
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
/*      */   private void stalePossibleSimpleKeys() {
/*  489 */     if (!this.possibleSimpleKeys.isEmpty()) {
/*  490 */       Iterator<SimpleKey> iterator = this.possibleSimpleKeys.values().iterator();
/*  491 */       while (iterator.hasNext()) {
/*  492 */         SimpleKey key = iterator.next();
/*  493 */         if (key.getLine() != this.reader.getLine() || this.reader.getIndex() - key.getIndex() > 1024) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  498 */           if (key.isRequired())
/*      */           {
/*      */             
/*  501 */             throw new ScannerException("while scanning a simple key", key.getMark(), "could not find expected ':'", this.reader
/*  502 */                 .getMark());
/*      */           }
/*  504 */           iterator.remove();
/*      */         } 
/*      */       } 
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void savePossibleSimpleKey() {
/*  522 */     boolean required = (this.flowLevel == 0 && this.indent == this.reader.getColumn());
/*      */     
/*  524 */     if (this.allowSimpleKey || !required) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  534 */       if (this.allowSimpleKey) {
/*  535 */         removePossibleSimpleKey();
/*  536 */         int tokenNumber = this.tokensTaken + this.tokens.size();
/*      */         
/*  538 */         SimpleKey key = new SimpleKey(tokenNumber, required, this.reader.getIndex(), this.reader.getLine(), this.reader.getColumn(), this.reader.getMark());
/*  539 */         this.possibleSimpleKeys.put(Integer.valueOf(this.flowLevel), key);
/*      */       } 
/*      */       return;
/*      */     } 
/*      */     throw new YAMLException("A simple key is required only if it is the first token in the current line");
/*      */   }
/*      */   
/*      */   private void removePossibleSimpleKey() {
/*  547 */     SimpleKey key = this.possibleSimpleKeys.remove(Integer.valueOf(this.flowLevel));
/*  548 */     if (key != null && key.isRequired()) {
/*  549 */       throw new ScannerException("while scanning a simple key", key.getMark(), "could not find expected ':'", this.reader
/*  550 */           .getMark());
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
/*      */   private void unwindIndent(int col) {
/*  578 */     if (this.flowLevel != 0) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  583 */     while (this.indent > col) {
/*  584 */       Mark mark = this.reader.getMark();
/*  585 */       this.indent = ((Integer)this.indents.pop()).intValue();
/*  586 */       addToken((Token)new BlockEndToken(mark, mark));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean addIndent(int column) {
/*  594 */     if (this.indent < column) {
/*  595 */       this.indents.push(Integer.valueOf(this.indent));
/*  596 */       this.indent = column;
/*  597 */       return true;
/*      */     } 
/*  599 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchStreamStart() {
/*  609 */     Mark mark = this.reader.getMark();
/*      */ 
/*      */     
/*  612 */     StreamStartToken streamStartToken = new StreamStartToken(mark, mark);
/*  613 */     addToken((Token)streamStartToken);
/*      */   }
/*      */ 
/*      */   
/*      */   private void fetchStreamEnd() {
/*  618 */     unwindIndent(-1);
/*      */ 
/*      */     
/*  621 */     removePossibleSimpleKey();
/*  622 */     this.allowSimpleKey = false;
/*  623 */     this.possibleSimpleKeys.clear();
/*      */ 
/*      */     
/*  626 */     Mark mark = this.reader.getMark();
/*      */ 
/*      */     
/*  629 */     StreamEndToken streamEndToken = new StreamEndToken(mark, mark);
/*  630 */     addToken((Token)streamEndToken);
/*      */ 
/*      */     
/*  633 */     this.done = true;
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
/*      */   private void fetchDirective() {
/*  645 */     unwindIndent(-1);
/*      */ 
/*      */     
/*  648 */     removePossibleSimpleKey();
/*  649 */     this.allowSimpleKey = false;
/*      */ 
/*      */     
/*  652 */     List<Token> tok = scanDirective();
/*  653 */     addAllTokens(tok);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchDocumentStart() {
/*  660 */     fetchDocumentIndicator(true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchDocumentEnd() {
/*  667 */     fetchDocumentIndicator(false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchDocumentIndicator(boolean isDocumentStart) {
/*      */     DocumentEndToken documentEndToken;
/*  676 */     unwindIndent(-1);
/*      */ 
/*      */ 
/*      */     
/*  680 */     removePossibleSimpleKey();
/*  681 */     this.allowSimpleKey = false;
/*      */ 
/*      */     
/*  684 */     Mark startMark = this.reader.getMark();
/*  685 */     this.reader.forward(3);
/*  686 */     Mark endMark = this.reader.getMark();
/*      */     
/*  688 */     if (isDocumentStart) {
/*  689 */       DocumentStartToken documentStartToken = new DocumentStartToken(startMark, endMark);
/*      */     } else {
/*  691 */       documentEndToken = new DocumentEndToken(startMark, endMark);
/*      */     } 
/*  693 */     addToken((Token)documentEndToken);
/*      */   }
/*      */   
/*      */   private void fetchFlowSequenceStart() {
/*  697 */     fetchFlowCollectionStart(false);
/*      */   }
/*      */   
/*      */   private void fetchFlowMappingStart() {
/*  701 */     fetchFlowCollectionStart(true);
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
/*      */   private void fetchFlowCollectionStart(boolean isMappingStart) {
/*      */     FlowSequenceStartToken flowSequenceStartToken;
/*  717 */     savePossibleSimpleKey();
/*      */ 
/*      */     
/*  720 */     this.flowLevel++;
/*      */ 
/*      */     
/*  723 */     this.allowSimpleKey = true;
/*      */ 
/*      */     
/*  726 */     Mark startMark = this.reader.getMark();
/*  727 */     this.reader.forward(1);
/*  728 */     Mark endMark = this.reader.getMark();
/*      */     
/*  730 */     if (isMappingStart) {
/*  731 */       FlowMappingStartToken flowMappingStartToken = new FlowMappingStartToken(startMark, endMark);
/*      */     } else {
/*  733 */       flowSequenceStartToken = new FlowSequenceStartToken(startMark, endMark);
/*      */     } 
/*  735 */     addToken((Token)flowSequenceStartToken);
/*      */   }
/*      */   
/*      */   private void fetchFlowSequenceEnd() {
/*  739 */     fetchFlowCollectionEnd(false);
/*      */   }
/*      */   
/*      */   private void fetchFlowMappingEnd() {
/*  743 */     fetchFlowCollectionEnd(true);
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
/*      */   private void fetchFlowCollectionEnd(boolean isMappingEnd) {
/*      */     FlowSequenceEndToken flowSequenceEndToken;
/*  757 */     removePossibleSimpleKey();
/*      */ 
/*      */     
/*  760 */     this.flowLevel--;
/*      */ 
/*      */     
/*  763 */     this.allowSimpleKey = false;
/*      */ 
/*      */     
/*  766 */     Mark startMark = this.reader.getMark();
/*  767 */     this.reader.forward();
/*  768 */     Mark endMark = this.reader.getMark();
/*      */     
/*  770 */     if (isMappingEnd) {
/*  771 */       FlowMappingEndToken flowMappingEndToken = new FlowMappingEndToken(startMark, endMark);
/*      */     } else {
/*  773 */       flowSequenceEndToken = new FlowSequenceEndToken(startMark, endMark);
/*      */     } 
/*  775 */     addToken((Token)flowSequenceEndToken);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchFlowEntry() {
/*  786 */     this.allowSimpleKey = true;
/*      */ 
/*      */     
/*  789 */     removePossibleSimpleKey();
/*      */ 
/*      */     
/*  792 */     Mark startMark = this.reader.getMark();
/*  793 */     this.reader.forward();
/*  794 */     Mark endMark = this.reader.getMark();
/*  795 */     FlowEntryToken flowEntryToken = new FlowEntryToken(startMark, endMark);
/*  796 */     addToken((Token)flowEntryToken);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchBlockEntry() {
/*  806 */     if (this.flowLevel == 0) {
/*      */       
/*  808 */       if (!this.allowSimpleKey) {
/*  809 */         throw new ScannerException(null, null, "sequence entries are not allowed here", this.reader
/*  810 */             .getMark());
/*      */       }
/*      */ 
/*      */       
/*  814 */       if (addIndent(this.reader.getColumn())) {
/*  815 */         Mark mark = this.reader.getMark();
/*  816 */         addToken((Token)new BlockSequenceStartToken(mark, mark));
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  823 */     this.allowSimpleKey = true;
/*      */ 
/*      */     
/*  826 */     removePossibleSimpleKey();
/*      */ 
/*      */     
/*  829 */     Mark startMark = this.reader.getMark();
/*  830 */     this.reader.forward();
/*  831 */     Mark endMark = this.reader.getMark();
/*  832 */     BlockEntryToken blockEntryToken = new BlockEntryToken(startMark, endMark);
/*  833 */     addToken((Token)blockEntryToken);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchKey() {
/*  843 */     if (this.flowLevel == 0) {
/*      */       
/*  845 */       if (!this.allowSimpleKey) {
/*  846 */         throw new ScannerException(null, null, "mapping keys are not allowed here", this.reader
/*  847 */             .getMark());
/*      */       }
/*      */       
/*  850 */       if (addIndent(this.reader.getColumn())) {
/*  851 */         Mark mark = this.reader.getMark();
/*  852 */         addToken((Token)new BlockMappingStartToken(mark, mark));
/*      */       } 
/*      */     } 
/*      */     
/*  856 */     this.allowSimpleKey = (this.flowLevel == 0);
/*      */ 
/*      */     
/*  859 */     removePossibleSimpleKey();
/*      */ 
/*      */     
/*  862 */     Mark startMark = this.reader.getMark();
/*  863 */     this.reader.forward();
/*  864 */     Mark endMark = this.reader.getMark();
/*  865 */     KeyToken keyToken = new KeyToken(startMark, endMark);
/*  866 */     addToken((Token)keyToken);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchValue() {
/*  876 */     SimpleKey key = this.possibleSimpleKeys.remove(Integer.valueOf(this.flowLevel));
/*  877 */     if (key != null) {
/*      */       
/*  879 */       addToken(key.getTokenNumber() - this.tokensTaken, (Token)new KeyToken(key.getMark(), key.getMark()));
/*      */ 
/*      */ 
/*      */       
/*  883 */       if (this.flowLevel == 0 && 
/*  884 */         addIndent(key.getColumn())) {
/*  885 */         addToken(key.getTokenNumber() - this.tokensTaken, (Token)new BlockMappingStartToken(key
/*  886 */               .getMark(), key.getMark()));
/*      */       }
/*      */ 
/*      */       
/*  890 */       this.allowSimpleKey = false;
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */       
/*  896 */       if (this.flowLevel == 0)
/*      */       {
/*      */ 
/*      */         
/*  900 */         if (!this.allowSimpleKey) {
/*  901 */           throw new ScannerException(null, null, "mapping values are not allowed here", this.reader
/*  902 */               .getMark());
/*      */         }
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  909 */       if (this.flowLevel == 0 && 
/*  910 */         addIndent(this.reader.getColumn())) {
/*  911 */         Mark mark = this.reader.getMark();
/*  912 */         addToken((Token)new BlockMappingStartToken(mark, mark));
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  917 */       this.allowSimpleKey = (this.flowLevel == 0);
/*      */ 
/*      */       
/*  920 */       removePossibleSimpleKey();
/*      */     } 
/*      */     
/*  923 */     Mark startMark = this.reader.getMark();
/*  924 */     this.reader.forward();
/*  925 */     Mark endMark = this.reader.getMark();
/*  926 */     ValueToken valueToken = new ValueToken(startMark, endMark);
/*  927 */     addToken((Token)valueToken);
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
/*      */   private void fetchAlias() {
/*  941 */     savePossibleSimpleKey();
/*      */ 
/*      */     
/*  944 */     this.allowSimpleKey = false;
/*      */ 
/*      */     
/*  947 */     Token tok = scanAnchor(false);
/*  948 */     addToken(tok);
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
/*      */   private void fetchAnchor() {
/*  962 */     savePossibleSimpleKey();
/*      */ 
/*      */     
/*  965 */     this.allowSimpleKey = false;
/*      */ 
/*      */     
/*  968 */     Token tok = scanAnchor(true);
/*  969 */     addToken(tok);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchTag() {
/*  979 */     savePossibleSimpleKey();
/*      */ 
/*      */     
/*  982 */     this.allowSimpleKey = false;
/*      */ 
/*      */     
/*  985 */     Token tok = scanTag();
/*  986 */     addToken(tok);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchLiteral() {
/*  996 */     fetchBlockScalar('|');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchFolded() {
/* 1006 */     fetchBlockScalar('>');
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
/*      */   private void fetchBlockScalar(char style) {
/* 1018 */     this.allowSimpleKey = true;
/*      */ 
/*      */     
/* 1021 */     removePossibleSimpleKey();
/*      */ 
/*      */     
/* 1024 */     List<Token> tok = scanBlockScalar(style);
/* 1025 */     addAllTokens(tok);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchSingle() {
/* 1032 */     fetchFlowScalar('\'');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchDouble() {
/* 1039 */     fetchFlowScalar('"');
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
/*      */   private void fetchFlowScalar(char style) {
/* 1051 */     savePossibleSimpleKey();
/*      */ 
/*      */     
/* 1054 */     this.allowSimpleKey = false;
/*      */ 
/*      */     
/* 1057 */     Token tok = scanFlowScalar(style);
/* 1058 */     addToken(tok);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fetchPlain() {
/* 1066 */     savePossibleSimpleKey();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1071 */     this.allowSimpleKey = false;
/*      */ 
/*      */     
/* 1074 */     Token tok = scanPlain();
/* 1075 */     addToken(tok);
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
/*      */   private boolean checkDirective() {
/* 1089 */     return (this.reader.getColumn() == 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean checkDocumentStart() {
/* 1098 */     if (this.reader.getColumn() == 0) {
/* 1099 */       return ("---".equals(this.reader.prefix(3)) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3)));
/*      */     }
/* 1101 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean checkDocumentEnd() {
/* 1110 */     if (this.reader.getColumn() == 0) {
/* 1111 */       return ("...".equals(this.reader.prefix(3)) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3)));
/*      */     }
/* 1113 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean checkBlockEntry() {
/* 1121 */     return Constant.NULL_BL_T_LINEBR.has(this.reader.peek(1));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean checkKey() {
/* 1129 */     if (this.flowLevel != 0) {
/* 1130 */       return true;
/*      */     }
/*      */     
/* 1133 */     return Constant.NULL_BL_T_LINEBR.has(this.reader.peek(1));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean checkValue() {
/* 1142 */     if (this.flowLevel != 0) {
/* 1143 */       return true;
/*      */     }
/*      */     
/* 1146 */     return Constant.NULL_BL_T_LINEBR.has(this.reader.peek(1));
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
/*      */   private boolean checkPlain() {
/* 1170 */     int c = this.reader.peek();
/*      */ 
/*      */     
/* 1173 */     return (Constant.NULL_BL_T_LINEBR.hasNo(c, "-?:,[]{}#&*!|>'\"%@`") || (Constant.NULL_BL_T_LINEBR
/* 1174 */       .hasNo(this.reader.peek(1)) && (c == 45 || (this.flowLevel == 0 && "?:"
/* 1175 */       .indexOf(c) != -1))));
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
/*      */   private void scanToNextToken() {
/* 1204 */     if (this.reader.getIndex() == 0 && this.reader.peek() == 65279) {
/* 1205 */       this.reader.forward();
/*      */     }
/* 1207 */     boolean found = false;
/* 1208 */     int inlineStartColumn = -1;
/* 1209 */     while (!found) {
/* 1210 */       Mark startMark = this.reader.getMark();
/* 1211 */       int columnBeforeComment = this.reader.getColumn();
/* 1212 */       boolean commentSeen = false;
/* 1213 */       int ff = 0;
/*      */ 
/*      */       
/* 1216 */       while (this.reader.peek(ff) == 32) {
/* 1217 */         ff++;
/*      */       }
/* 1219 */       if (ff > 0) {
/* 1220 */         this.reader.forward(ff);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1226 */       if (this.reader.peek() == 35) {
/* 1227 */         CommentType type; commentSeen = true;
/*      */         
/* 1229 */         if (columnBeforeComment != 0 && (this.lastToken == null || this.lastToken
/* 1230 */           .getTokenId() != Token.ID.BlockEntry)) {
/* 1231 */           type = CommentType.IN_LINE;
/* 1232 */           inlineStartColumn = this.reader.getColumn();
/* 1233 */         } else if (inlineStartColumn == this.reader.getColumn()) {
/* 1234 */           type = CommentType.IN_LINE;
/*      */         } else {
/* 1236 */           inlineStartColumn = -1;
/* 1237 */           type = CommentType.BLOCK;
/*      */         } 
/* 1239 */         CommentToken token = scanComment(type);
/* 1240 */         if (this.parseComments) {
/* 1241 */           addToken((Token)token);
/*      */         }
/*      */       } 
/*      */ 
/*      */       
/* 1246 */       String breaks = scanLineBreak();
/* 1247 */       if (breaks.length() != 0) {
/* 1248 */         if (this.parseComments && !commentSeen && 
/* 1249 */           columnBeforeComment == 0) {
/* 1250 */           Mark endMark = this.reader.getMark();
/* 1251 */           addToken((Token)new CommentToken(CommentType.BLANK_LINE, breaks, startMark, endMark));
/*      */         } 
/*      */         
/* 1254 */         if (this.flowLevel == 0)
/*      */         {
/*      */           
/* 1257 */           this.allowSimpleKey = true; } 
/*      */         continue;
/*      */       } 
/* 1260 */       found = true;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private CommentToken scanComment(CommentType type) {
/* 1267 */     Mark startMark = this.reader.getMark();
/* 1268 */     this.reader.forward();
/* 1269 */     int length = 0;
/* 1270 */     while (Constant.NULL_OR_LINEBR.hasNo(this.reader.peek(length))) {
/* 1271 */       length++;
/*      */     }
/* 1273 */     String value = this.reader.prefixForward(length);
/* 1274 */     Mark endMark = this.reader.getMark();
/* 1275 */     return new CommentToken(type, value, startMark, endMark);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private List<Token> scanDirective() {
/* 1281 */     Mark endMark, startMark = this.reader.getMark();
/*      */     
/* 1283 */     this.reader.forward();
/* 1284 */     String name = scanDirectiveName(startMark);
/* 1285 */     List<?> value = null;
/* 1286 */     if ("YAML".equals(name)) {
/* 1287 */       value = scanYamlDirectiveValue(startMark);
/* 1288 */       endMark = this.reader.getMark();
/* 1289 */     } else if ("TAG".equals(name)) {
/* 1290 */       value = scanTagDirectiveValue(startMark);
/* 1291 */       endMark = this.reader.getMark();
/*      */     } else {
/* 1293 */       endMark = this.reader.getMark();
/* 1294 */       int ff = 0;
/* 1295 */       while (Constant.NULL_OR_LINEBR.hasNo(this.reader.peek(ff))) {
/* 1296 */         ff++;
/*      */       }
/* 1298 */       if (ff > 0) {
/* 1299 */         this.reader.forward(ff);
/*      */       }
/*      */     } 
/* 1302 */     CommentToken commentToken = scanDirectiveIgnoredLine(startMark);
/* 1303 */     DirectiveToken token = new DirectiveToken(name, value, startMark, endMark);
/* 1304 */     return makeTokenList(new Token[] { (Token)token, (Token)commentToken });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String scanDirectiveName(Mark startMark) {
/* 1314 */     int length = 0;
/*      */ 
/*      */ 
/*      */     
/* 1318 */     int c = this.reader.peek(length);
/* 1319 */     while (Constant.ALPHA.has(c)) {
/* 1320 */       length++;
/* 1321 */       c = this.reader.peek(length);
/*      */     } 
/*      */     
/* 1324 */     if (length == 0) {
/* 1325 */       String s = String.valueOf(Character.toChars(c));
/* 1326 */       throw new ScannerException("while scanning a directive", startMark, "expected alphabetic or numeric character, but found " + s + "(" + c + ")", this.reader
/*      */           
/* 1328 */           .getMark());
/*      */     } 
/* 1330 */     String value = this.reader.prefixForward(length);
/* 1331 */     c = this.reader.peek();
/* 1332 */     if (Constant.NULL_BL_LINEBR.hasNo(c)) {
/* 1333 */       String s = String.valueOf(Character.toChars(c));
/* 1334 */       throw new ScannerException("while scanning a directive", startMark, "expected alphabetic or numeric character, but found " + s + "(" + c + ")", this.reader
/*      */           
/* 1336 */           .getMark());
/*      */     } 
/* 1338 */     return value;
/*      */   }
/*      */ 
/*      */   
/*      */   private List<Integer> scanYamlDirectiveValue(Mark startMark) {
/* 1343 */     while (this.reader.peek() == 32) {
/* 1344 */       this.reader.forward();
/*      */     }
/* 1346 */     Integer major = scanYamlDirectiveNumber(startMark);
/* 1347 */     int c = this.reader.peek();
/* 1348 */     if (c != 46) {
/* 1349 */       String s = String.valueOf(Character.toChars(c));
/* 1350 */       throw new ScannerException("while scanning a directive", startMark, "expected a digit or '.', but found " + s + "(" + c + ")", this.reader
/* 1351 */           .getMark());
/*      */     } 
/* 1353 */     this.reader.forward();
/* 1354 */     Integer minor = scanYamlDirectiveNumber(startMark);
/* 1355 */     c = this.reader.peek();
/* 1356 */     if (Constant.NULL_BL_LINEBR.hasNo(c)) {
/* 1357 */       String s = String.valueOf(Character.toChars(c));
/* 1358 */       throw new ScannerException("while scanning a directive", startMark, "expected a digit or ' ', but found " + s + "(" + c + ")", this.reader
/* 1359 */           .getMark());
/*      */     } 
/* 1361 */     List<Integer> result = new ArrayList<>(2);
/* 1362 */     result.add(major);
/* 1363 */     result.add(minor);
/* 1364 */     return result;
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
/*      */   private Integer scanYamlDirectiveNumber(Mark startMark) {
/* 1376 */     int c = this.reader.peek();
/* 1377 */     if (!Character.isDigit(c)) {
/* 1378 */       String s = String.valueOf(Character.toChars(c));
/* 1379 */       throw new ScannerException("while scanning a directive", startMark, "expected a digit, but found " + s + "(" + c + ")", this.reader
/* 1380 */           .getMark());
/*      */     } 
/* 1382 */     int length = 0;
/* 1383 */     while (Character.isDigit(this.reader.peek(length))) {
/* 1384 */       length++;
/*      */     }
/* 1386 */     String number = this.reader.prefixForward(length);
/* 1387 */     if (length > 3) {
/* 1388 */       throw new ScannerException("while scanning a YAML directive", startMark, "found a number which cannot represent a valid version: " + number, this.reader
/* 1389 */           .getMark());
/*      */     }
/* 1391 */     Integer value = Integer.valueOf(Integer.parseInt(number));
/* 1392 */     return value;
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
/*      */   private List<String> scanTagDirectiveValue(Mark startMark) {
/* 1409 */     while (this.reader.peek() == 32) {
/* 1410 */       this.reader.forward();
/*      */     }
/* 1412 */     String handle = scanTagDirectiveHandle(startMark);
/* 1413 */     while (this.reader.peek() == 32) {
/* 1414 */       this.reader.forward();
/*      */     }
/* 1416 */     String prefix = scanTagDirectivePrefix(startMark);
/* 1417 */     List<String> result = new ArrayList<>(2);
/* 1418 */     result.add(handle);
/* 1419 */     result.add(prefix);
/* 1420 */     return result;
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
/*      */   private String scanTagDirectiveHandle(Mark startMark) {
/* 1432 */     String value = scanTagHandle("directive", startMark);
/* 1433 */     int c = this.reader.peek();
/* 1434 */     if (c != 32) {
/* 1435 */       String s = String.valueOf(Character.toChars(c));
/* 1436 */       throw new ScannerException("while scanning a directive", startMark, "expected ' ', but found " + s + "(" + c + ")", this.reader
/* 1437 */           .getMark());
/*      */     } 
/* 1439 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String scanTagDirectivePrefix(Mark startMark) {
/* 1449 */     String value = scanTagUri("directive", startMark);
/* 1450 */     int c = this.reader.peek();
/* 1451 */     if (Constant.NULL_BL_LINEBR.hasNo(c)) {
/* 1452 */       String s = String.valueOf(Character.toChars(c));
/* 1453 */       throw new ScannerException("while scanning a directive", startMark, "expected ' ', but found " + s + "(" + c + ")", this.reader
/* 1454 */           .getMark());
/*      */     } 
/* 1456 */     return value;
/*      */   }
/*      */ 
/*      */   
/*      */   private CommentToken scanDirectiveIgnoredLine(Mark startMark) {
/* 1461 */     while (this.reader.peek() == 32) {
/* 1462 */       this.reader.forward();
/*      */     }
/* 1464 */     CommentToken commentToken = null;
/* 1465 */     if (this.reader.peek() == 35) {
/* 1466 */       CommentToken comment = scanComment(CommentType.IN_LINE);
/* 1467 */       if (this.parseComments) {
/* 1468 */         commentToken = comment;
/*      */       }
/*      */     } 
/* 1471 */     int c = this.reader.peek();
/* 1472 */     String lineBreak = scanLineBreak();
/* 1473 */     if (lineBreak.length() == 0 && c != 0) {
/* 1474 */       String s = String.valueOf(Character.toChars(c));
/* 1475 */       throw new ScannerException("while scanning a directive", startMark, "expected a comment or a line break, but found " + s + "(" + c + ")", this.reader
/* 1476 */           .getMark());
/*      */     } 
/* 1478 */     return commentToken;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Token scanAnchor(boolean isAnchor) {
/*      */     AliasToken aliasToken;
/* 1490 */     Mark startMark = this.reader.getMark();
/* 1491 */     int indicator = this.reader.peek();
/* 1492 */     String name = (indicator == 42) ? "alias" : "anchor";
/* 1493 */     this.reader.forward();
/* 1494 */     int length = 0;
/* 1495 */     int c = this.reader.peek(length);
/* 1496 */     while (Constant.NULL_BL_T_LINEBR.hasNo(c, ":,[]{}/.*&")) {
/* 1497 */       length++;
/* 1498 */       c = this.reader.peek(length);
/*      */     } 
/* 1500 */     if (length == 0) {
/* 1501 */       String s = String.valueOf(Character.toChars(c));
/* 1502 */       throw new ScannerException("while scanning an " + name, startMark, "unexpected character found " + s + "(" + c + ")", this.reader
/* 1503 */           .getMark());
/*      */     } 
/* 1505 */     String value = this.reader.prefixForward(length);
/* 1506 */     c = this.reader.peek();
/* 1507 */     if (Constant.NULL_BL_T_LINEBR.hasNo(c, "?:,]}%@`")) {
/* 1508 */       String s = String.valueOf(Character.toChars(c));
/* 1509 */       throw new ScannerException("while scanning an " + name, startMark, "unexpected character found " + s + "(" + c + ")", this.reader
/* 1510 */           .getMark());
/*      */     } 
/* 1512 */     Mark endMark = this.reader.getMark();
/*      */     
/* 1514 */     if (isAnchor) {
/* 1515 */       AnchorToken anchorToken = new AnchorToken(value, startMark, endMark);
/*      */     } else {
/* 1517 */       aliasToken = new AliasToken(value, startMark, endMark);
/*      */     } 
/* 1519 */     return (Token)aliasToken;
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
/*      */   private Token scanTag() {
/* 1553 */     Mark startMark = this.reader.getMark();
/*      */ 
/*      */     
/* 1556 */     int c = this.reader.peek(1);
/* 1557 */     String handle = null;
/* 1558 */     String suffix = null;
/*      */     
/* 1560 */     if (c == 60) {
/*      */ 
/*      */       
/* 1563 */       this.reader.forward(2);
/* 1564 */       suffix = scanTagUri("tag", startMark);
/* 1565 */       c = this.reader.peek();
/* 1566 */       if (c != 62) {
/*      */ 
/*      */         
/* 1569 */         String s = String.valueOf(Character.toChars(c));
/* 1570 */         throw new ScannerException("while scanning a tag", startMark, "expected '>', but found '" + s + "' (" + c + ")", this.reader
/* 1571 */             .getMark());
/*      */       } 
/* 1573 */       this.reader.forward();
/* 1574 */     } else if (Constant.NULL_BL_T_LINEBR.has(c)) {
/*      */ 
/*      */       
/* 1577 */       suffix = "!";
/* 1578 */       this.reader.forward();
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */       
/* 1584 */       int length = 1;
/* 1585 */       boolean useHandle = false;
/* 1586 */       while (Constant.NULL_BL_LINEBR.hasNo(c)) {
/* 1587 */         if (c == 33) {
/* 1588 */           useHandle = true;
/*      */           break;
/*      */         } 
/* 1591 */         length++;
/* 1592 */         c = this.reader.peek(length);
/*      */       } 
/*      */ 
/*      */       
/* 1596 */       if (useHandle) {
/* 1597 */         handle = scanTagHandle("tag", startMark);
/*      */       } else {
/* 1599 */         handle = "!";
/* 1600 */         this.reader.forward();
/*      */       } 
/* 1602 */       suffix = scanTagUri("tag", startMark);
/*      */     } 
/* 1604 */     c = this.reader.peek();
/*      */ 
/*      */     
/* 1607 */     if (Constant.NULL_BL_LINEBR.hasNo(c)) {
/* 1608 */       String s = String.valueOf(Character.toChars(c));
/* 1609 */       throw new ScannerException("while scanning a tag", startMark, "expected ' ', but found '" + s + "' (" + c + ")", this.reader
/* 1610 */           .getMark());
/*      */     } 
/* 1612 */     TagTuple value = new TagTuple(handle, suffix);
/* 1613 */     Mark endMark = this.reader.getMark();
/* 1614 */     return (Token)new TagToken(value, startMark, endMark);
/*      */   }
/*      */ 
/*      */   
/*      */   private List<Token> scanBlockScalar(char style) {
/*      */     String breaks;
/*      */     int indent;
/*      */     Mark mark1;
/* 1622 */     boolean folded = (style == '>');
/* 1623 */     StringBuilder chunks = new StringBuilder();
/* 1624 */     Mark startMark = this.reader.getMark();
/*      */     
/* 1626 */     this.reader.forward();
/* 1627 */     Chomping chompi = scanBlockScalarIndicators(startMark);
/* 1628 */     int increment = chompi.getIncrement();
/* 1629 */     CommentToken commentToken = scanBlockScalarIgnoredLine(startMark);
/*      */ 
/*      */     
/* 1632 */     int minIndent = this.indent + 1;
/* 1633 */     if (minIndent < 1) {
/* 1634 */       minIndent = 1;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1640 */     if (increment == -1) {
/* 1641 */       Object[] brme = scanBlockScalarIndentation();
/* 1642 */       breaks = (String)brme[0];
/* 1643 */       int maxIndent = ((Integer)brme[1]).intValue();
/* 1644 */       mark1 = (Mark)brme[2];
/* 1645 */       indent = Math.max(minIndent, maxIndent);
/*      */     } else {
/* 1647 */       indent = minIndent + increment - 1;
/* 1648 */       Object[] brme = scanBlockScalarBreaks(indent);
/* 1649 */       breaks = (String)brme[0];
/* 1650 */       mark1 = (Mark)brme[1];
/*      */     } 
/*      */     
/* 1653 */     String lineBreak = "";
/*      */ 
/*      */     
/* 1656 */     while (this.reader.getColumn() == indent && this.reader.peek() != 0) {
/* 1657 */       chunks.append(breaks);
/* 1658 */       boolean leadingNonSpace = (" \t".indexOf(this.reader.peek()) == -1);
/* 1659 */       int length = 0;
/* 1660 */       while (Constant.NULL_OR_LINEBR.hasNo(this.reader.peek(length))) {
/* 1661 */         length++;
/*      */       }
/* 1663 */       chunks.append(this.reader.prefixForward(length));
/* 1664 */       lineBreak = scanLineBreak();
/* 1665 */       Object[] brme = scanBlockScalarBreaks(indent);
/* 1666 */       breaks = (String)brme[0];
/* 1667 */       mark1 = (Mark)brme[1];
/* 1668 */       if (this.reader.getColumn() == indent && this.reader.peek() != 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1673 */         if (folded && "\n".equals(lineBreak) && leadingNonSpace && " \t"
/* 1674 */           .indexOf(this.reader.peek()) == -1) {
/* 1675 */           if (breaks.length() == 0)
/* 1676 */             chunks.append(" "); 
/*      */           continue;
/*      */         } 
/* 1679 */         chunks.append(lineBreak);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1688 */     if (chompi.chompTailIsNotFalse()) {
/* 1689 */       chunks.append(lineBreak);
/*      */     }
/* 1691 */     if (chompi.chompTailIsTrue()) {
/* 1692 */       chunks.append(breaks);
/*      */     }
/*      */ 
/*      */     
/* 1696 */     ScalarToken scalarToken = new ScalarToken(chunks.toString(), false, startMark, mark1, DumperOptions.ScalarStyle.createStyle(Character.valueOf(style)));
/* 1697 */     return makeTokenList(new Token[] { (Token)commentToken, (Token)scalarToken });
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
/*      */   private Chomping scanBlockScalarIndicators(Mark startMark) {
/* 1717 */     Boolean chomping = null;
/* 1718 */     int increment = -1;
/* 1719 */     int c = this.reader.peek();
/* 1720 */     if (c == 45 || c == 43) {
/* 1721 */       if (c == 43) {
/* 1722 */         chomping = Boolean.TRUE;
/*      */       } else {
/* 1724 */         chomping = Boolean.FALSE;
/*      */       } 
/* 1726 */       this.reader.forward();
/* 1727 */       c = this.reader.peek();
/* 1728 */       if (Character.isDigit(c)) {
/* 1729 */         String s = String.valueOf(Character.toChars(c));
/* 1730 */         increment = Integer.parseInt(s);
/* 1731 */         if (increment == 0) {
/* 1732 */           throw new ScannerException("while scanning a block scalar", startMark, "expected indentation indicator in the range 1-9, but found 0", this.reader
/* 1733 */               .getMark());
/*      */         }
/* 1735 */         this.reader.forward();
/*      */       } 
/* 1737 */     } else if (Character.isDigit(c)) {
/* 1738 */       String s = String.valueOf(Character.toChars(c));
/* 1739 */       increment = Integer.parseInt(s);
/* 1740 */       if (increment == 0) {
/* 1741 */         throw new ScannerException("while scanning a block scalar", startMark, "expected indentation indicator in the range 1-9, but found 0", this.reader
/* 1742 */             .getMark());
/*      */       }
/* 1744 */       this.reader.forward();
/* 1745 */       c = this.reader.peek();
/* 1746 */       if (c == 45 || c == 43) {
/* 1747 */         if (c == 43) {
/* 1748 */           chomping = Boolean.TRUE;
/*      */         } else {
/* 1750 */           chomping = Boolean.FALSE;
/*      */         } 
/* 1752 */         this.reader.forward();
/*      */       } 
/*      */     } 
/* 1755 */     c = this.reader.peek();
/* 1756 */     if (Constant.NULL_BL_LINEBR.hasNo(c)) {
/* 1757 */       String s = String.valueOf(Character.toChars(c));
/* 1758 */       throw new ScannerException("while scanning a block scalar", startMark, "expected chomping or indentation indicators, but found " + s + "(" + c + ")", this.reader
/*      */           
/* 1760 */           .getMark());
/*      */     } 
/* 1762 */     return new Chomping(chomping, increment);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private CommentToken scanBlockScalarIgnoredLine(Mark startMark) {
/* 1773 */     while (this.reader.peek() == 32) {
/* 1774 */       this.reader.forward();
/*      */     }
/*      */ 
/*      */     
/* 1778 */     CommentToken commentToken = null;
/* 1779 */     if (this.reader.peek() == 35) {
/* 1780 */       commentToken = scanComment(CommentType.IN_LINE);
/*      */     }
/*      */ 
/*      */     
/* 1784 */     int c = this.reader.peek();
/* 1785 */     String lineBreak = scanLineBreak();
/* 1786 */     if (lineBreak.length() == 0 && c != 0) {
/* 1787 */       String s = String.valueOf(Character.toChars(c));
/* 1788 */       throw new ScannerException("while scanning a block scalar", startMark, "expected a comment or a line break, but found " + s + "(" + c + ")", this.reader
/* 1789 */           .getMark());
/*      */     } 
/* 1791 */     return commentToken;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Object[] scanBlockScalarIndentation() {
/* 1802 */     StringBuilder chunks = new StringBuilder();
/* 1803 */     int maxIndent = 0;
/* 1804 */     Mark endMark = this.reader.getMark();
/*      */ 
/*      */ 
/*      */     
/* 1808 */     while (Constant.LINEBR.has(this.reader.peek(), " \r")) {
/* 1809 */       if (this.reader.peek() != 32) {
/*      */ 
/*      */         
/* 1812 */         chunks.append(scanLineBreak());
/* 1813 */         endMark = this.reader.getMark();
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 1818 */       this.reader.forward();
/* 1819 */       if (this.reader.getColumn() > maxIndent) {
/* 1820 */         maxIndent = this.reader.getColumn();
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1825 */     return new Object[] { chunks.toString(), Integer.valueOf(maxIndent), endMark };
/*      */   }
/*      */ 
/*      */   
/*      */   private Object[] scanBlockScalarBreaks(int indent) {
/* 1830 */     StringBuilder chunks = new StringBuilder();
/* 1831 */     Mark endMark = this.reader.getMark();
/* 1832 */     int col = this.reader.getColumn();
/*      */ 
/*      */     
/* 1835 */     while (col < indent && this.reader.peek() == 32) {
/* 1836 */       this.reader.forward();
/* 1837 */       col++;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1842 */     String lineBreak = null;
/* 1843 */     while ((lineBreak = scanLineBreak()).length() != 0) {
/* 1844 */       chunks.append(lineBreak);
/* 1845 */       endMark = this.reader.getMark();
/*      */ 
/*      */       
/* 1848 */       col = this.reader.getColumn();
/* 1849 */       while (col < indent && this.reader.peek() == 32) {
/* 1850 */         this.reader.forward();
/* 1851 */         col++;
/*      */       } 
/*      */     } 
/*      */     
/* 1855 */     return new Object[] { chunks.toString(), endMark };
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
/*      */   private Token scanFlowScalar(char style) {
/* 1877 */     boolean _double = (style == '"');
/* 1878 */     StringBuilder chunks = new StringBuilder();
/* 1879 */     Mark startMark = this.reader.getMark();
/* 1880 */     int quote = this.reader.peek();
/* 1881 */     this.reader.forward();
/* 1882 */     chunks.append(scanFlowScalarNonSpaces(_double, startMark));
/* 1883 */     while (this.reader.peek() != quote) {
/* 1884 */       chunks.append(scanFlowScalarSpaces(startMark));
/* 1885 */       chunks.append(scanFlowScalarNonSpaces(_double, startMark));
/*      */     } 
/* 1887 */     this.reader.forward();
/* 1888 */     Mark endMark = this.reader.getMark();
/* 1889 */     return (Token)new ScalarToken(chunks.toString(), false, startMark, endMark, 
/* 1890 */         DumperOptions.ScalarStyle.createStyle(Character.valueOf(style)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String scanFlowScalarNonSpaces(boolean doubleQuoted, Mark startMark) {
/* 1898 */     StringBuilder chunks = new StringBuilder();
/*      */ 
/*      */     
/*      */     while (true) {
/* 1902 */       int length = 0;
/* 1903 */       while (Constant.NULL_BL_T_LINEBR.hasNo(this.reader.peek(length), "'\"\\")) {
/* 1904 */         length++;
/*      */       }
/* 1906 */       if (length != 0) {
/* 1907 */         chunks.append(this.reader.prefixForward(length));
/*      */       }
/*      */ 
/*      */       
/* 1911 */       int c = this.reader.peek();
/* 1912 */       if (!doubleQuoted && c == 39 && this.reader.peek(1) == 39) {
/* 1913 */         chunks.append("'");
/* 1914 */         this.reader.forward(2); continue;
/* 1915 */       }  if ((doubleQuoted && c == 39) || (!doubleQuoted && "\"\\".indexOf(c) != -1)) {
/* 1916 */         chunks.appendCodePoint(c);
/* 1917 */         this.reader.forward(); continue;
/* 1918 */       }  if (doubleQuoted && c == 92) {
/* 1919 */         this.reader.forward();
/* 1920 */         c = this.reader.peek();
/* 1921 */         if (!Character.isSupplementaryCodePoint(c) && ESCAPE_REPLACEMENTS
/* 1922 */           .containsKey(Character.valueOf((char)c))) {
/*      */ 
/*      */ 
/*      */           
/* 1926 */           chunks.append(ESCAPE_REPLACEMENTS.get(Character.valueOf((char)c)));
/* 1927 */           this.reader.forward(); continue;
/* 1928 */         }  if (!Character.isSupplementaryCodePoint(c) && ESCAPE_CODES
/* 1929 */           .containsKey(Character.valueOf((char)c))) {
/*      */ 
/*      */           
/* 1932 */           length = ((Integer)ESCAPE_CODES.get(Character.valueOf((char)c))).intValue();
/* 1933 */           this.reader.forward();
/* 1934 */           String hex = this.reader.prefix(length);
/* 1935 */           if (NOT_HEXA.matcher(hex).find()) {
/* 1936 */             throw new ScannerException("while scanning a double-quoted scalar", startMark, "expected escape sequence of " + length + " hexadecimal numbers, but found: " + hex, this.reader
/*      */                 
/* 1938 */                 .getMark());
/*      */           }
/* 1940 */           int decimal = Integer.parseInt(hex, 16);
/*      */           try {
/* 1942 */             String unicode = new String(Character.toChars(decimal));
/* 1943 */             chunks.append(unicode);
/* 1944 */             this.reader.forward(length);
/* 1945 */           } catch (IllegalArgumentException e) {
/* 1946 */             throw new ScannerException("while scanning a double-quoted scalar", startMark, "found unknown escape character " + hex, this.reader
/* 1947 */                 .getMark());
/*      */           }  continue;
/* 1949 */         }  if (scanLineBreak().length() != 0) {
/* 1950 */           chunks.append(scanFlowScalarBreaks(startMark)); continue;
/*      */         } 
/* 1952 */         String s = String.valueOf(Character.toChars(c));
/* 1953 */         throw new ScannerException("while scanning a double-quoted scalar", startMark, "found unknown escape character " + s + "(" + c + ")", this.reader
/* 1954 */             .getMark());
/*      */       }  break;
/*      */     } 
/* 1957 */     return chunks.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String scanFlowScalarSpaces(Mark startMark) {
/* 1964 */     StringBuilder chunks = new StringBuilder();
/* 1965 */     int length = 0;
/*      */ 
/*      */     
/* 1968 */     while (" \t".indexOf(this.reader.peek(length)) != -1) {
/* 1969 */       length++;
/*      */     }
/* 1971 */     String whitespaces = this.reader.prefixForward(length);
/* 1972 */     int c = this.reader.peek();
/* 1973 */     if (c == 0)
/*      */     {
/* 1975 */       throw new ScannerException("while scanning a quoted scalar", startMark, "found unexpected end of stream", this.reader
/* 1976 */           .getMark());
/*      */     }
/*      */     
/* 1979 */     String lineBreak = scanLineBreak();
/* 1980 */     if (lineBreak.length() != 0) {
/* 1981 */       String breaks = scanFlowScalarBreaks(startMark);
/* 1982 */       if (!"\n".equals(lineBreak)) {
/* 1983 */         chunks.append(lineBreak);
/* 1984 */       } else if (breaks.length() == 0) {
/* 1985 */         chunks.append(" ");
/*      */       } 
/* 1987 */       chunks.append(breaks);
/*      */     } else {
/* 1989 */       chunks.append(whitespaces);
/*      */     } 
/* 1991 */     return chunks.toString();
/*      */   }
/*      */ 
/*      */   
/*      */   private String scanFlowScalarBreaks(Mark startMark) {
/* 1996 */     StringBuilder chunks = new StringBuilder();
/*      */ 
/*      */     
/*      */     while (true) {
/* 2000 */       String prefix = this.reader.prefix(3);
/* 2001 */       if (("---".equals(prefix) || "...".equals(prefix)) && Constant.NULL_BL_T_LINEBR
/* 2002 */         .has(this.reader.peek(3))) {
/* 2003 */         throw new ScannerException("while scanning a quoted scalar", startMark, "found unexpected document separator", this.reader
/* 2004 */             .getMark());
/*      */       }
/*      */       
/* 2007 */       while (" \t".indexOf(this.reader.peek()) != -1) {
/* 2008 */         this.reader.forward();
/*      */       }
/*      */ 
/*      */       
/* 2012 */       String lineBreak = scanLineBreak();
/* 2013 */       if (lineBreak.length() != 0) {
/* 2014 */         chunks.append(lineBreak); continue;
/*      */       }  break;
/* 2016 */     }  return chunks.toString();
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
/*      */   private Token scanPlain() {
/* 2033 */     StringBuilder chunks = new StringBuilder();
/* 2034 */     Mark startMark = this.reader.getMark();
/* 2035 */     Mark endMark = startMark;
/* 2036 */     int indent = this.indent + 1;
/* 2037 */     String spaces = "";
/*      */     
/*      */     do {
/* 2040 */       int length = 0;
/*      */       
/* 2042 */       if (this.reader.peek() == 35) {
/*      */         break;
/*      */       }
/*      */       while (true) {
/* 2046 */         int c = this.reader.peek(length);
/* 2047 */         if (Constant.NULL_BL_T_LINEBR.has(c) || (c == 58 && Constant.NULL_BL_T_LINEBR
/* 2048 */           .has(this.reader.peek(length + 1), 
/* 2049 */             (this.flowLevel != 0) ? ",[]{}" : "")) || (this.flowLevel != 0 && ",?[]{}"
/* 2050 */           .indexOf(c) != -1)) {
/*      */           break;
/*      */         }
/* 2053 */         length++;
/*      */       } 
/* 2055 */       if (length == 0) {
/*      */         break;
/*      */       }
/* 2058 */       this.allowSimpleKey = false;
/* 2059 */       chunks.append(spaces);
/* 2060 */       chunks.append(this.reader.prefixForward(length));
/* 2061 */       endMark = this.reader.getMark();
/* 2062 */       spaces = scanPlainSpaces();
/*      */     }
/* 2064 */     while (spaces.length() != 0 && this.reader.peek() != 35 && (this.flowLevel != 0 || this.reader
/* 2065 */       .getColumn() >= indent));
/*      */ 
/*      */ 
/*      */     
/* 2069 */     return (Token)new ScalarToken(chunks.toString(), startMark, endMark, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean atEndOfPlain() {
/* 2077 */     int wsLength = 0;
/* 2078 */     int wsColumn = this.reader.getColumn();
/*      */     
/*      */     int c;
/* 2081 */     while ((c = this.reader.peek(wsLength)) != 0 && Constant.NULL_BL_T_LINEBR.has(c)) {
/* 2082 */       wsLength++;
/* 2083 */       if (!Constant.LINEBR.has(c) && (c != 13 || this.reader.peek(wsLength + 1) != 10) && c != 65279) {
/*      */         
/* 2085 */         wsColumn++; continue;
/*      */       } 
/* 2087 */       wsColumn = 0;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2094 */     if (this.reader.peek(wsLength) == 35 || this.reader.peek(wsLength + 1) == 0 || (this.flowLevel == 0 && wsColumn < this.indent))
/*      */     {
/* 2096 */       return true;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 2101 */     if (this.flowLevel == 0) {
/*      */       
/* 2103 */       int extra = 1; while (true) { if ((c = 
/* 2104 */           this.reader.peek(wsLength + extra)) != 0 && !Constant.NULL_BL_T_LINEBR.has(c)) {
/* 2105 */           if (c == 58 && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(wsLength + extra + 1)))
/* 2106 */             return true;  extra++;
/*      */           continue;
/*      */         } 
/*      */         break; }
/*      */     
/*      */     } 
/* 2112 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String scanPlainSpaces() {
/* 2119 */     int length = 0;
/* 2120 */     while (this.reader.peek(length) == 32 || this.reader.peek(length) == 9) {
/* 2121 */       length++;
/*      */     }
/* 2123 */     String whitespaces = this.reader.prefixForward(length);
/* 2124 */     String lineBreak = scanLineBreak();
/* 2125 */     if (lineBreak.length() != 0) {
/* 2126 */       this.allowSimpleKey = true;
/* 2127 */       String prefix = this.reader.prefix(3);
/* 2128 */       if ("---".equals(prefix) || ("..."
/* 2129 */         .equals(prefix) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3)))) {
/* 2130 */         return "";
/*      */       }
/* 2132 */       if (this.parseComments && atEndOfPlain()) {
/* 2133 */         return "";
/*      */       }
/* 2135 */       StringBuilder breaks = new StringBuilder();
/*      */       while (true) {
/* 2137 */         while (this.reader.peek() == 32) {
/* 2138 */           this.reader.forward();
/*      */         }
/* 2140 */         String lb = scanLineBreak();
/* 2141 */         if (lb.length() != 0) {
/* 2142 */           breaks.append(lb);
/* 2143 */           prefix = this.reader.prefix(3);
/* 2144 */           if ("---".equals(prefix) || ("..."
/* 2145 */             .equals(prefix) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3)))) {
/* 2146 */             return "";
/*      */           }
/*      */           
/*      */           continue;
/*      */         } 
/*      */         break;
/*      */       } 
/* 2153 */       if (!"\n".equals(lineBreak))
/* 2154 */         return lineBreak + breaks; 
/* 2155 */       if (breaks.length() == 0) {
/* 2156 */         return " ";
/*      */       }
/* 2158 */       return breaks.toString();
/*      */     } 
/* 2160 */     return whitespaces;
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
/*      */   private String scanTagHandle(String name, Mark startMark) {
/* 2186 */     int c = this.reader.peek();
/* 2187 */     if (c != 33) {
/* 2188 */       String s = String.valueOf(Character.toChars(c));
/* 2189 */       throw new ScannerException("while scanning a " + name, startMark, "expected '!', but found " + s + "(" + c + ")", this.reader
/* 2190 */           .getMark());
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 2195 */     int length = 1;
/* 2196 */     c = this.reader.peek(length);
/* 2197 */     if (c != 32) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2202 */       while (Constant.ALPHA.has(c)) {
/* 2203 */         length++;
/* 2204 */         c = this.reader.peek(length);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 2209 */       if (c != 33) {
/* 2210 */         this.reader.forward(length);
/* 2211 */         String s = String.valueOf(Character.toChars(c));
/* 2212 */         throw new ScannerException("while scanning a " + name, startMark, "expected '!', but found " + s + "(" + c + ")", this.reader
/* 2213 */             .getMark());
/*      */       } 
/* 2215 */       length++;
/*      */     } 
/* 2217 */     String value = this.reader.prefixForward(length);
/* 2218 */     return value;
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
/*      */   private String scanTagUri(String name, Mark startMark) {
/* 2238 */     StringBuilder chunks = new StringBuilder();
/*      */ 
/*      */ 
/*      */     
/* 2242 */     int length = 0;
/* 2243 */     int c = this.reader.peek(length);
/* 2244 */     while (Constant.URI_CHARS.has(c)) {
/* 2245 */       if (c == 37) {
/* 2246 */         chunks.append(this.reader.prefixForward(length));
/* 2247 */         length = 0;
/* 2248 */         chunks.append(scanUriEscapes(name, startMark));
/*      */       } else {
/* 2250 */         length++;
/*      */       } 
/* 2252 */       c = this.reader.peek(length);
/*      */     } 
/*      */ 
/*      */     
/* 2256 */     if (length != 0) {
/* 2257 */       chunks.append(this.reader.prefixForward(length));
/*      */     }
/* 2259 */     if (chunks.length() == 0) {
/*      */       
/* 2261 */       String s = String.valueOf(Character.toChars(c));
/* 2262 */       throw new ScannerException("while scanning a " + name, startMark, "expected URI, but found " + s + "(" + c + ")", this.reader
/* 2263 */           .getMark());
/*      */     } 
/* 2265 */     return chunks.toString();
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
/*      */   private String scanUriEscapes(String name, Mark startMark) {
/* 2282 */     int length = 1;
/* 2283 */     while (this.reader.peek(length * 3) == 37) {
/* 2284 */       length++;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2290 */     Mark beginningMark = this.reader.getMark();
/* 2291 */     ByteBuffer buff = ByteBuffer.allocate(length);
/* 2292 */     while (this.reader.peek() == 37) {
/* 2293 */       this.reader.forward();
/*      */       try {
/* 2295 */         byte code = (byte)Integer.parseInt(this.reader.prefix(2), 16);
/* 2296 */         buff.put(code);
/* 2297 */       } catch (NumberFormatException nfe) {
/* 2298 */         int c1 = this.reader.peek();
/* 2299 */         String s1 = String.valueOf(Character.toChars(c1));
/* 2300 */         int c2 = this.reader.peek(1);
/* 2301 */         String s2 = String.valueOf(Character.toChars(c2));
/* 2302 */         throw new ScannerException("while scanning a " + name, startMark, "expected URI escape sequence of 2 hexadecimal numbers, but found " + s1 + "(" + c1 + ") and " + s2 + "(" + c2 + ")", this.reader
/*      */ 
/*      */             
/* 2305 */             .getMark());
/*      */       } 
/* 2307 */       this.reader.forward(2);
/*      */     } 
/* 2309 */     buff.flip();
/*      */     try {
/* 2311 */       return UriEncoder.decode(buff);
/* 2312 */     } catch (CharacterCodingException e) {
/* 2313 */       throw new ScannerException("while scanning a " + name, startMark, "expected URI in UTF-8: " + e
/* 2314 */           .getMessage(), beginningMark);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String scanLineBreak() {
/* 2330 */     int c = this.reader.peek();
/* 2331 */     if (c == 13 || c == 10 || c == 133) {
/* 2332 */       if (c == 13 && 10 == this.reader.peek(1)) {
/* 2333 */         this.reader.forward(2);
/*      */       } else {
/* 2335 */         this.reader.forward();
/*      */       } 
/* 2337 */       return "\n";
/* 2338 */     }  if (c == 8232 || c == 8233) {
/* 2339 */       this.reader.forward();
/* 2340 */       return String.valueOf(Character.toChars(c));
/*      */     } 
/* 2342 */     return "";
/*      */   }
/*      */   
/*      */   private List<Token> makeTokenList(Token... tokens) {
/* 2346 */     List<Token> tokenList = new ArrayList<>();
/* 2347 */     for (int ix = 0; ix < tokens.length; ix++) {
/* 2348 */       if (tokens[ix] != null)
/*      */       {
/*      */         
/* 2351 */         if (this.parseComments || !(tokens[ix] instanceof CommentToken))
/*      */         {
/*      */           
/* 2354 */           tokenList.add(tokens[ix]); }  } 
/*      */     } 
/* 2356 */     return tokenList;
/*      */   }
/*      */ 
/*      */   
/*      */   public void resetDocumentIndex() {
/* 2361 */     this.reader.resetDocumentIndex();
/*      */   }
/*      */ 
/*      */   
/*      */   private static class Chomping
/*      */   {
/*      */     private final Boolean value;
/*      */     
/*      */     private final int increment;
/*      */ 
/*      */     
/*      */     public Chomping(Boolean value, int increment) {
/* 2373 */       this.value = value;
/* 2374 */       this.increment = increment;
/*      */     }
/*      */     
/*      */     public boolean chompTailIsNotFalse() {
/* 2378 */       return (this.value == null || this.value.booleanValue());
/*      */     }
/*      */     
/*      */     public boolean chompTailIsTrue() {
/* 2382 */       return (this.value != null && this.value.booleanValue());
/*      */     }
/*      */     
/*      */     public int getIncrement() {
/* 2386 */       return this.increment;
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\scanner\ScannerImpl.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */