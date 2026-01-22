/*     */ package ac.grim.grimac.shaded.maps.weak;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Spliterators;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.stream.Stream;
/*     */ import java.util.stream.StreamSupport;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface Dynamic
/*     */   extends Weak<Dynamic>
/*     */ {
/*     */   public static final String ROOT_KEY = "root";
/*     */   
/*     */   static Dynamic from(Object val) {
/*  64 */     if (val == null) return DynamicNothing.INSTANCE; 
/*  65 */     if (val instanceof Dynamic) return (Dynamic)val; 
/*  66 */     if (val instanceof Map) return new DynamicMap((Map<?, ?>)val); 
/*  67 */     if (val instanceof List) return new DynamicList((List)val); 
/*  68 */     if (val instanceof Collection) return new DynamicCollection((Collection)val); 
/*  69 */     return new DynamicSomething(val);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default Dynamic get(String keyPath, String separator) {
/*  92 */     Dynamic result = this;
/*  93 */     for (String part : keyPath.split(Pattern.quote(separator)))
/*  94 */       result = result.get(part); 
/*  95 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default Dynamic dget(String dotSeparatedPath) {
/* 105 */     return get(dotSeparatedPath, ".");
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
/*     */   default Stream<Dynamic> allChildren() {
/* 120 */     return allChildrenDepthFirst();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default Stream<Dynamic> allChildrenDepthFirst() {
/* 145 */     return children().flatMap(child -> Stream.concat(Stream.of(child), child.allChildrenDepthFirst()));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default Stream<Dynamic> allChildrenBreadthFirst() {
/* 169 */     return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new BreadthChildIterator(this), 16), false);
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
/*     */   default <T> T as(Class<T> type) {
/*     */     try {
/* 182 */       return type.cast(asObject());
/* 183 */     } catch (ClassCastException ex) {
/* 184 */       throw new ClassCastException(String.format("'root' miscast: %s. Avoid by checking `if (aDynamic.is(%s.class)) ...` or using `aDynamic.maybe().as(%<s.class)`", new Object[] { ex
/* 185 */               .getMessage(), type.getSimpleName() }));
/*     */     } 
/*     */   }
/*     */   
/*     */   Dynamic get(Object paramObject);
/*     */   
/*     */   Stream<Dynamic> children();
/*     */   
/*     */   Weak<?> key();
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\maps\weak\Dynamic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */