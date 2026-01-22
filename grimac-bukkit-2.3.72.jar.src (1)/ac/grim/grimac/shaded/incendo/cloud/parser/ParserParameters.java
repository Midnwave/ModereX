/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public final class ParserParameters
/*     */ {
/*  38 */   private final Map<ParserParameter<?>, Object> internalMap = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ParserParameters empty() {
/*  46 */     return new ParserParameters();
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
/*     */   public static <T> ParserParameters single(ParserParameter<T> parameter, T value) {
/*  61 */     ParserParameters parameters = new ParserParameters();
/*  62 */     parameters.store(parameter, value);
/*  63 */     return parameters;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> boolean has(ParserParameter<T> parameter) {
/*  74 */     return this.internalMap.containsKey(parameter);
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
/*     */   public <T> void store(ParserParameter<T> parameter, T value) {
/*  88 */     this.internalMap.put(parameter, value);
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
/*     */   public <T> T get(ParserParameter<T> parameter, T defaultValue) {
/* 104 */     return (T)this.internalMap.getOrDefault(parameter, defaultValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void merge(ParserParameters other) {
/* 114 */     this.internalMap.putAll(other.internalMap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<ParserParameter<?>, Object> parameters() {
/* 123 */     return Collections.unmodifiableMap(this.internalMap);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\ParserParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */