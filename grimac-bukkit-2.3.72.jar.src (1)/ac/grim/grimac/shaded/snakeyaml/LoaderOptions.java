/*     */ package ac.grim.grimac.shaded.snakeyaml;
/*     */ 
/*     */ import ac.grim.grimac.shaded.snakeyaml.inspector.TagInspector;
/*     */ import ac.grim.grimac.shaded.snakeyaml.inspector.UnTrustedTagInspector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LoaderOptions
/*     */ {
/*     */   private boolean allowDuplicateKeys = true;
/*     */   private boolean wrappedToRootException = false;
/*  28 */   private int maxAliasesForCollections = 50;
/*     */ 
/*     */   
/*     */   private boolean allowRecursiveKeys = false;
/*     */   
/*     */   private boolean processComments = false;
/*     */   
/*     */   private boolean enumCaseSensitive = true;
/*     */   
/*  37 */   private int nestingDepthLimit = 50;
/*     */   
/*  39 */   private int codePointLimit = 3145728;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  44 */   private TagInspector tagInspector = (TagInspector)new UnTrustedTagInspector();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isAllowDuplicateKeys() {
/*  52 */     return this.allowDuplicateKeys;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAllowDuplicateKeys(boolean allowDuplicateKeys) {
/*  70 */     this.allowDuplicateKeys = allowDuplicateKeys;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isWrappedToRootException() {
/*  79 */     return this.wrappedToRootException;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWrappedToRootException(boolean wrappedToRootException) {
/*  90 */     this.wrappedToRootException = wrappedToRootException;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getMaxAliasesForCollections() {
/*  99 */     return this.maxAliasesForCollections;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxAliasesForCollections(int maxAliasesForCollections) {
/* 109 */     this.maxAliasesForCollections = maxAliasesForCollections;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean getAllowRecursiveKeys() {
/* 118 */     return this.allowRecursiveKeys;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAllowRecursiveKeys(boolean allowRecursiveKeys) {
/* 129 */     this.allowRecursiveKeys = allowRecursiveKeys;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isProcessComments() {
/* 138 */     return this.processComments;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LoaderOptions setProcessComments(boolean processComments) {
/* 148 */     this.processComments = processComments;
/* 149 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isEnumCaseSensitive() {
/* 158 */     return this.enumCaseSensitive;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEnumCaseSensitive(boolean enumCaseSensitive) {
/* 168 */     this.enumCaseSensitive = enumCaseSensitive;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getNestingDepthLimit() {
/* 177 */     return this.nestingDepthLimit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNestingDepthLimit(int nestingDepthLimit) {
/* 187 */     this.nestingDepthLimit = nestingDepthLimit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getCodePointLimit() {
/* 196 */     return this.codePointLimit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCodePointLimit(int codePointLimit) {
/* 206 */     this.codePointLimit = codePointLimit;
/*     */   }
/*     */   
/*     */   public TagInspector getTagInspector() {
/* 210 */     return this.tagInspector;
/*     */   }
/*     */   
/*     */   public void setTagInspector(TagInspector tagInspector) {
/* 214 */     this.tagInspector = tagInspector;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\LoaderOptions.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */