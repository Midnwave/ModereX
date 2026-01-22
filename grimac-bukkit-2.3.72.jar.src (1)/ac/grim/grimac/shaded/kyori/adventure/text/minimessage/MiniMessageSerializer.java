/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TextComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.ClaimConsumer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.Emitable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.QuotingOverride;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class MiniMessageSerializer
/*     */ {
/*     */   @NotNull
/*     */   static String serialize(@NotNull Component component, @NotNull SerializableResolver resolver, boolean strict) {
/*  53 */     StringBuilder sb = new StringBuilder();
/*  54 */     Collector emitter = new Collector(resolver, strict, sb);
/*     */     
/*  56 */     emitter.mark();
/*  57 */     visit(component, emitter, resolver, true);
/*  58 */     if (strict) {
/*     */       
/*  60 */       emitter.popAll();
/*     */     } else {
/*  62 */       emitter.completeTag();
/*     */     } 
/*     */     
/*  65 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   private static void visit(@NotNull Component component, Collector emitter, SerializableResolver resolver, boolean lastChild) {
/*  70 */     resolver.handle(component, emitter);
/*  71 */     Component childSource = emitter.flushClaims(component);
/*  72 */     if (childSource == null) {
/*  73 */       childSource = component;
/*     */     }
/*     */ 
/*     */     
/*  77 */     for (Iterator<Component> it = childSource.children().iterator(); it.hasNext(); ) {
/*  78 */       emitter.mark();
/*  79 */       visit(it.next(), emitter, resolver, (lastChild && !it.hasNext()));
/*     */     } 
/*     */     
/*  82 */     if (!lastChild)
/*  83 */       emitter.popToMark(); 
/*     */   }
/*     */   
/*     */   static final class Collector implements TokenEmitter, ClaimConsumer { private static final String MARK = "__<'\"\\MARK__";
/*     */     
/*     */     enum TagState {
/*  89 */       TEXT(false),
/*  90 */       MID(true),
/*  91 */       MID_SELF_CLOSING(true);
/*     */       
/*     */       final boolean isTag;
/*     */       
/*     */       TagState(boolean isTag) {
/*  96 */         this.isTag = isTag;
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 104 */     private static final char[] TEXT_ESCAPES = new char[] { '\\', '<' };
/* 105 */     private static final char[] TAG_TOKENS = new char[] { '>', ':' };
/* 106 */     private static final char[] SINGLE_QUOTED_ESCAPES = new char[] { '\\', '\'' };
/* 107 */     private static final char[] DOUBLE_QUOTED_ESCAPES = new char[] { '\\', '"' };
/*     */     
/*     */     private final SerializableResolver resolver;
/*     */     private final boolean strict;
/*     */     private final StringBuilder consumer;
/* 112 */     private String[] activeTags = new String[4];
/* 113 */     private int tagLevel = 0;
/* 114 */     private TagState tagState = TagState.TEXT;
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     Emitable componentClaim;
/*     */     
/*     */     final Set<String> claimedStyleElements;
/*     */ 
/*     */     
/*     */     private void pushActiveTag(String tag) {
/* 124 */       if (this.tagLevel >= this.activeTags.length) {
/* 125 */         this.activeTags = Arrays.<String>copyOf(this.activeTags, this.activeTags.length * 2);
/*     */       }
/* 127 */       this.activeTags[this.tagLevel++] = tag;
/*     */     }
/*     */     
/*     */     private String popTag(boolean allowMarks) {
/* 131 */       if (this.tagLevel-- <= 0) {
/* 132 */         throw new IllegalStateException("Unbalanced tags, tried to pop below depth");
/*     */       }
/* 134 */       String tag = this.activeTags[this.tagLevel];
/* 135 */       if (!allowMarks && tag == "__<'\"\\MARK__") {
/* 136 */         throw new IllegalStateException("Tried to pop past mark, tag stack: " + Arrays.toString(this.activeTags) + " @ " + this.tagLevel);
/*     */       }
/* 138 */       return tag;
/*     */     }
/*     */     
/*     */     void mark() {
/* 142 */       pushActiveTag("__<'\"\\MARK__");
/*     */     }
/*     */     
/*     */     void popToMark() {
/* 146 */       if (this.tagLevel == 0) {
/*     */         return;
/*     */       }
/*     */       String tag;
/* 150 */       while ((tag = popTag(true)) != "__<'\"\\MARK__") {
/* 151 */         emitClose(tag);
/*     */       }
/*     */     }
/*     */     
/*     */     void popAll() {
/* 156 */       while (this.tagLevel > 0) {
/* 157 */         String tag = this.activeTags[--this.tagLevel];
/* 158 */         if (tag != "__<'\"\\MARK__") {
/* 159 */           emitClose(tag);
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/*     */     void completeTag() {
/* 165 */       if (this.tagState.isTag) {
/* 166 */         this.consumer.append('>');
/* 167 */         this.tagState = TagState.TEXT;
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public Collector tag(@NotNull String token) {
/* 175 */       completeTag();
/* 176 */       this.consumer.append('<');
/* 177 */       escapeTagContent(token, QuotingOverride.UNQUOTED);
/* 178 */       this.tagState = TagState.MID;
/* 179 */       pushActiveTag(token);
/* 180 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public TokenEmitter selfClosingTag(@NotNull String token) {
/* 185 */       completeTag();
/* 186 */       this.consumer.append('<');
/* 187 */       escapeTagContent(token, QuotingOverride.UNQUOTED);
/* 188 */       this.tagState = TagState.MID_SELF_CLOSING;
/* 189 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public TokenEmitter argument(@NotNull String arg) {
/* 194 */       if (!this.tagState.isTag) {
/* 195 */         throw new IllegalStateException("Not within a tag!");
/*     */       }
/* 197 */       this.consumer.append(':');
/* 198 */       escapeTagContent(arg, null);
/* 199 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public TokenEmitter argument(@NotNull String arg, @NotNull QuotingOverride quotingPreference) {
/* 204 */       if (!this.tagState.isTag) {
/* 205 */         throw new IllegalStateException("Not within a tag!");
/*     */       }
/* 207 */       this.consumer.append(':');
/* 208 */       escapeTagContent(arg, Objects.<QuotingOverride>requireNonNull(quotingPreference, "quotingPreference"));
/* 209 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public TokenEmitter argument(@NotNull Component arg) {
/* 214 */       String serialized = MiniMessageSerializer.serialize(arg, this.resolver, this.strict);
/* 215 */       return argument(serialized, QuotingOverride.QUOTED);
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Collector text(@NotNull String text) {
/* 220 */       completeTag();
/*     */       
/* 222 */       appendEscaping(this.consumer, text, TEXT_ESCAPES, true);
/* 223 */       return this;
/*     */     }
/*     */     
/*     */     private void escapeTagContent(String content, @Nullable QuotingOverride preference) {
/* 227 */       boolean mustBeQuoted = (preference == QuotingOverride.QUOTED);
/* 228 */       boolean hasSingleQuote = false;
/* 229 */       boolean hasDoubleQuote = false;
/*     */       
/* 231 */       for (int i = 0; i < content.length(); i++) {
/* 232 */         char active = content.charAt(i);
/* 233 */         if (active == '>' || active == ':' || active == ' ')
/* 234 */         { mustBeQuoted = true;
/* 235 */           if (hasSingleQuote && hasDoubleQuote)
/* 236 */             break;  } else { if (active == '\'') {
/* 237 */             hasSingleQuote = true; break;
/*     */           } 
/* 239 */           if (active == '"') {
/* 240 */             hasDoubleQuote = true;
/* 241 */             if (mustBeQuoted && hasSingleQuote)
/*     */               break; 
/*     */           }  }
/*     */       
/* 245 */       }  if (hasSingleQuote) {
/* 246 */         this.consumer.append('"');
/* 247 */         appendEscaping(this.consumer, content, DOUBLE_QUOTED_ESCAPES, true);
/* 248 */         this.consumer.append('"');
/* 249 */       } else if (hasDoubleQuote || mustBeQuoted) {
/*     */         
/* 251 */         this.consumer.append('\'');
/* 252 */         appendEscaping(this.consumer, content, SINGLE_QUOTED_ESCAPES, true);
/* 253 */         this.consumer.append('\'');
/*     */       } else {
/* 255 */         appendEscaping(this.consumer, content, TAG_TOKENS, false);
/*     */       } 
/*     */     }
/*     */     
/*     */     static void appendEscaping(StringBuilder builder, String text, char[] escapeChars, boolean allowEscapes) {
/* 260 */       int startIdx = 0;
/* 261 */       boolean unescapedFound = false;
/*     */       
/* 263 */       for (int i = 0; i < text.length(); i++) {
/* 264 */         char test = text.charAt(i);
/* 265 */         boolean escaped = false;
/* 266 */         for (char c : escapeChars) {
/* 267 */           if (test == c) {
/* 268 */             if (!allowEscapes) {
/* 269 */               throw new IllegalArgumentException("Invalid escapable character '" + test + "' found at index " + i + " in string '" + text + "'");
/*     */             }
/* 271 */             escaped = true;
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/* 276 */         if (escaped) {
/* 277 */           if (unescapedFound) builder.append(text, startIdx, i); 
/* 278 */           startIdx = i + 1;
/* 279 */           builder.append('\\').append(test);
/*     */         } else {
/* 281 */           unescapedFound = true;
/*     */         } 
/*     */       } 
/*     */       
/* 285 */       if (startIdx < text.length() && unescapedFound) {
/* 286 */         builder.append(text, startIdx, text.length());
/*     */       }
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Collector pop() {
/* 292 */       emitClose(popTag(false));
/* 293 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     private void emitClose(@NotNull String tag) {
/* 298 */       if (this.tagState.isTag) {
/* 299 */         if (this.tagState == TagState.MID) {
/* 300 */           this.consumer.append('/');
/*     */         }
/* 302 */         this.consumer.append('>');
/* 303 */         this.tagState = TagState.TEXT;
/*     */       } else {
/* 305 */         this.consumer.append('<')
/* 306 */           .append('/');
/* 307 */         escapeTagContent(tag, QuotingOverride.UNQUOTED);
/* 308 */         this.consumer.append('>');
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     Collector(SerializableResolver resolver, boolean strict, StringBuilder consumer) {
/* 315 */       this.claimedStyleElements = new HashSet<>();
/*     */       this.resolver = resolver;
/*     */       this.strict = strict;
/*     */       this.consumer = consumer; } public void style(@NotNull String claimKey, @NotNull Emitable styleClaim) {
/* 319 */       if (this.claimedStyleElements.add(Objects.<String>requireNonNull(claimKey, "claimKey"))) {
/* 320 */         styleClaim.emit(this);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean component(@NotNull Emitable componentClaim) {
/* 326 */       if (this.componentClaim != null) return false;
/*     */       
/* 328 */       this.componentClaim = Objects.<Emitable>requireNonNull(componentClaim, "componentClaim");
/* 329 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean componentClaimed() {
/* 334 */       return (this.componentClaim != null);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean styleClaimed(@NotNull String claimId) {
/* 339 */       return this.claimedStyleElements.contains(claimId);
/*     */     }
/*     */     @Nullable
/*     */     Component flushClaims(Component component) {
/* 343 */       Component ret = null;
/* 344 */       if (this.componentClaim != null) {
/* 345 */         this.componentClaim.emit(this);
/* 346 */         ret = this.componentClaim.substitute();
/* 347 */         this.componentClaim = null;
/* 348 */       } else if (component instanceof TextComponent) {
/* 349 */         text(((TextComponent)component).content());
/*     */       } else {
/*     */         
/* 352 */         throw new IllegalStateException("Unclaimed component " + component);
/*     */       } 
/* 354 */       this.claimedStyleElements.clear();
/* 355 */       return ret;
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\MiniMessageSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */