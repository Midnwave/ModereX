/*     */ package ac.grim.grimac.shaded.snakeyaml;
/*     */ 
/*     */ import ac.grim.grimac.shaded.snakeyaml.error.YAMLException;
/*     */ import ac.grim.grimac.shaded.snakeyaml.serializer.AnchorGenerator;
/*     */ import ac.grim.grimac.shaded.snakeyaml.serializer.NumberAnchorGenerator;
/*     */ import java.util.Map;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DumperOptions
/*     */ {
/*     */   public enum ScalarStyle
/*     */   {
/*  42 */     DOUBLE_QUOTED((String)Character.valueOf('"')),
/*     */ 
/*     */ 
/*     */     
/*  46 */     SINGLE_QUOTED((String)Character.valueOf('\'')),
/*     */ 
/*     */ 
/*     */     
/*  50 */     LITERAL((String)Character.valueOf('|')),
/*     */ 
/*     */ 
/*     */     
/*  54 */     FOLDED((String)Character.valueOf('>')),
/*     */ 
/*     */ 
/*     */     
/*  58 */     PLAIN(null);
/*     */     
/*     */     private final Character styleChar;
/*     */     
/*     */     ScalarStyle(Character style) {
/*  63 */       this.styleChar = style;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Character getChar() {
/*  72 */       return this.styleChar;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String toString() {
/*  82 */       return "Scalar style: '" + this.styleChar + "'";
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static ScalarStyle createStyle(Character style) {
/*  92 */       if (style == null) {
/*  93 */         return PLAIN;
/*     */       }
/*  95 */       switch (style.charValue()) {
/*     */         case '"':
/*  97 */           return DOUBLE_QUOTED;
/*     */         case '\'':
/*  99 */           return SINGLE_QUOTED;
/*     */         case '|':
/* 101 */           return LITERAL;
/*     */         case '>':
/* 103 */           return FOLDED;
/*     */       } 
/* 105 */       throw new YAMLException("Unknown scalar style character: " + style);
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
/*     */   
/*     */   public enum FlowStyle
/*     */   {
/* 122 */     FLOW((String)Boolean.TRUE),
/*     */ 
/*     */ 
/*     */     
/* 126 */     BLOCK((String)Boolean.FALSE),
/*     */ 
/*     */ 
/*     */     
/* 130 */     AUTO(null);
/*     */     
/*     */     private final Boolean styleBoolean;
/*     */     
/*     */     FlowStyle(Boolean flowStyle) {
/* 135 */       this.styleBoolean = flowStyle;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 140 */       return "Flow style: '" + this.styleBoolean + "'";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum LineBreak
/*     */   {
/* 151 */     WIN("\r\n"),
/*     */ 
/*     */ 
/*     */     
/* 155 */     MAC("\r"),
/*     */ 
/*     */ 
/*     */     
/* 159 */     UNIX("\n");
/*     */ 
/*     */ 
/*     */     
/*     */     private final String lineBreak;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     LineBreak(String lineBreak) {
/* 169 */       this.lineBreak = lineBreak;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getString() {
/* 178 */       return this.lineBreak;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String toString() {
/* 188 */       return "Line break: " + name();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static LineBreak getPlatformLineBreak() {
/* 197 */       String platformLineBreak = System.getProperty("line.separator");
/* 198 */       for (LineBreak lb : values()) {
/* 199 */         if (lb.lineBreak.equals(platformLineBreak)) {
/* 200 */           return lb;
/*     */         }
/*     */       } 
/* 203 */       return UNIX;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum Version
/*     */   {
/* 214 */     V1_0((String)new Integer[] { Integer.valueOf(1), Integer.valueOf(0)
/*     */ 
/*     */       
/*     */       }),
/* 218 */     V1_1((String)new Integer[] { Integer.valueOf(1), Integer.valueOf(1) });
/*     */ 
/*     */ 
/*     */     
/*     */     private final Integer[] version;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     Version(Integer[] version) {
/* 228 */       this.version = version;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int major() {
/* 237 */       return this.version[0].intValue();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int minor() {
/* 246 */       return this.version[1].intValue();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getRepresentation() {
/* 255 */       return this.version[0] + "." + this.version[1];
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String toString() {
/* 265 */       return "Version: " + getRepresentation();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum NonPrintableStyle
/*     */   {
/* 276 */     BINARY,
/*     */ 
/*     */ 
/*     */     
/* 280 */     ESCAPE;
/*     */   }
/*     */   
/* 283 */   private ScalarStyle defaultStyle = ScalarStyle.PLAIN;
/* 284 */   private FlowStyle defaultFlowStyle = FlowStyle.AUTO;
/*     */   private boolean canonical = false;
/*     */   private boolean allowUnicode = true;
/*     */   private boolean allowReadOnlyProperties = false;
/* 288 */   private int indent = 2;
/* 289 */   private int indicatorIndent = 0;
/*     */   private boolean indentWithIndicator = false;
/* 291 */   private int bestWidth = 80;
/*     */   private boolean splitLines = true;
/* 293 */   private LineBreak lineBreak = LineBreak.UNIX;
/*     */   private boolean explicitStart = false;
/*     */   private boolean explicitEnd = false;
/* 296 */   private TimeZone timeZone = null;
/* 297 */   private int maxSimpleKeyLength = 128;
/*     */   private boolean processComments = false;
/* 299 */   private NonPrintableStyle nonPrintableStyle = NonPrintableStyle.BINARY;
/*     */   
/* 301 */   private Version version = null;
/* 302 */   private Map<String, String> tags = null;
/* 303 */   private Boolean prettyFlow = Boolean.valueOf(false);
/* 304 */   private AnchorGenerator anchorGenerator = (AnchorGenerator)new NumberAnchorGenerator(0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAllowUnicode() {
/* 312 */     return this.allowUnicode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAllowUnicode(boolean allowUnicode) {
/* 323 */     this.allowUnicode = allowUnicode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ScalarStyle getDefaultScalarStyle() {
/* 332 */     return this.defaultStyle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDefaultScalarStyle(ScalarStyle defaultStyle) {
/* 342 */     if (defaultStyle == null) {
/* 343 */       throw new NullPointerException("Use ScalarStyle enum.");
/*     */     }
/* 345 */     this.defaultStyle = defaultStyle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIndent(int indent) {
/* 354 */     if (indent < 1) {
/* 355 */       throw new YAMLException("Indent must be at least 1");
/*     */     }
/* 357 */     if (indent > 10) {
/* 358 */       throw new YAMLException("Indent must be at most 10");
/*     */     }
/* 360 */     this.indent = indent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIndent() {
/* 369 */     return this.indent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIndicatorIndent(int indicatorIndent) {
/* 378 */     if (indicatorIndent < 0) {
/* 379 */       throw new YAMLException("Indicator indent must be non-negative.");
/*     */     }
/* 381 */     if (indicatorIndent > 9) {
/* 382 */       throw new YAMLException("Indicator indent must be at most Emitter.MAX_INDENT-1: 9");
/*     */     }
/*     */     
/* 385 */     this.indicatorIndent = indicatorIndent;
/*     */   }
/*     */   
/*     */   public int getIndicatorIndent() {
/* 389 */     return this.indicatorIndent;
/*     */   }
/*     */   
/*     */   public boolean getIndentWithIndicator() {
/* 393 */     return this.indentWithIndicator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIndentWithIndicator(boolean indentWithIndicator) {
/* 402 */     this.indentWithIndicator = indentWithIndicator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setVersion(Version version) {
/* 411 */     this.version = version;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Version getVersion() {
/* 420 */     return this.version;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCanonical(boolean canonical) {
/* 429 */     this.canonical = canonical;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCanonical() {
/* 438 */     return this.canonical;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPrettyFlow(boolean prettyFlow) {
/* 447 */     this.prettyFlow = Boolean.valueOf(prettyFlow);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isPrettyFlow() {
/* 456 */     return this.prettyFlow.booleanValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWidth(int bestWidth) {
/* 466 */     this.bestWidth = bestWidth;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getWidth() {
/* 475 */     return this.bestWidth;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSplitLines(boolean splitLines) {
/* 484 */     this.splitLines = splitLines;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getSplitLines() {
/* 493 */     return this.splitLines;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LineBreak getLineBreak() {
/* 502 */     return this.lineBreak;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDefaultFlowStyle(FlowStyle defaultFlowStyle) {
/* 511 */     if (defaultFlowStyle == null) {
/* 512 */       throw new NullPointerException("Use FlowStyle enum.");
/*     */     }
/* 514 */     this.defaultFlowStyle = defaultFlowStyle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FlowStyle getDefaultFlowStyle() {
/* 523 */     return this.defaultFlowStyle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLineBreak(LineBreak lineBreak) {
/* 533 */     if (lineBreak == null) {
/* 534 */       throw new NullPointerException("Specify line break.");
/*     */     }
/* 536 */     this.lineBreak = lineBreak;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isExplicitStart() {
/* 545 */     return this.explicitStart;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setExplicitStart(boolean explicitStart) {
/* 554 */     this.explicitStart = explicitStart;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isExplicitEnd() {
/* 563 */     return this.explicitEnd;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setExplicitEnd(boolean explicitEnd) {
/* 572 */     this.explicitEnd = explicitEnd;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, String> getTags() {
/* 581 */     return this.tags;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTags(Map<String, String> tags) {
/* 590 */     this.tags = tags;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAllowReadOnlyProperties() {
/* 600 */     return this.allowReadOnlyProperties;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAllowReadOnlyProperties(boolean allowReadOnlyProperties) {
/* 611 */     this.allowReadOnlyProperties = allowReadOnlyProperties;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TimeZone getTimeZone() {
/* 620 */     return this.timeZone;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTimeZone(TimeZone timeZone) {
/* 629 */     this.timeZone = timeZone;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AnchorGenerator getAnchorGenerator() {
/* 639 */     return this.anchorGenerator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAnchorGenerator(AnchorGenerator anchorGenerator) {
/* 648 */     this.anchorGenerator = anchorGenerator;
/*     */   }
/*     */   
/*     */   public int getMaxSimpleKeyLength() {
/* 652 */     return this.maxSimpleKeyLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxSimpleKeyLength(int maxSimpleKeyLength) {
/* 662 */     if (maxSimpleKeyLength > 1024) {
/* 663 */       throw new YAMLException("The simple key must not span more than 1024 stream characters. See https://yaml.org/spec/1.1/#id934537");
/*     */     }
/*     */     
/* 666 */     this.maxSimpleKeyLength = maxSimpleKeyLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProcessComments(boolean processComments) {
/* 675 */     this.processComments = processComments;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isProcessComments() {
/* 684 */     return this.processComments;
/*     */   }
/*     */   
/*     */   public NonPrintableStyle getNonPrintableStyle() {
/* 688 */     return this.nonPrintableStyle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNonPrintableStyle(NonPrintableStyle style) {
/* 699 */     this.nonPrintableStyle = style;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\DumperOptions.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */