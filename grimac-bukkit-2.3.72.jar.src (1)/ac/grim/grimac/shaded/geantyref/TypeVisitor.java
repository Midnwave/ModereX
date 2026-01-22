/*     */ package ac.grim.grimac.shaded.geantyref;
/*     */ 
/*     */ import java.lang.reflect.AnnotatedArrayType;
/*     */ import java.lang.reflect.AnnotatedParameterizedType;
/*     */ import java.lang.reflect.AnnotatedType;
/*     */ import java.lang.reflect.AnnotatedTypeVariable;
/*     */ import java.lang.reflect.AnnotatedWildcardType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.lang.reflect.WildcardType;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class TypeVisitor
/*     */ {
/*  21 */   private final Map<TypeVariable, AnnotatedTypeVariable> varCache = new IdentityHashMap<>();
/*  22 */   private final Map<AnnotatedCaptureCacheKey, AnnotatedType> captureCache = new HashMap<>();
/*     */ 
/*     */ 
/*     */   
/*     */   protected AnnotatedType visitParameterizedType(AnnotatedParameterizedType type) {
/*  27 */     AnnotatedType[] params = (AnnotatedType[])Arrays.<AnnotatedType>stream(type.getAnnotatedActualTypeArguments()).map(param -> GenericTypeReflector.transform(param, this)).toArray(x$0 -> new AnnotatedType[x$0]);
/*     */     
/*  29 */     return GenericTypeReflector.replaceParameters(type, params);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected AnnotatedType visitWildcardType(AnnotatedWildcardType type) {
/*  35 */     AnnotatedType[] lowerBounds = (AnnotatedType[])Arrays.<AnnotatedType>stream(type.getAnnotatedLowerBounds()).map(bound -> GenericTypeReflector.transform(bound, this)).toArray(x$0 -> new AnnotatedType[x$0]);
/*     */ 
/*     */     
/*  38 */     AnnotatedType[] upperBounds = (AnnotatedType[])Arrays.<AnnotatedType>stream(type.getAnnotatedUpperBounds()).map(bound -> GenericTypeReflector.transform(bound, this)).toArray(x$0 -> new AnnotatedType[x$0]);
/*     */ 
/*     */ 
/*     */     
/*  42 */     (new Type[1])[0] = Object.class;
/*  43 */     WildcardType inner = new WildcardTypeImpl((upperBounds.length > 0) ? (Type[])Arrays.<AnnotatedType>stream(upperBounds).map(AnnotatedType::getType).toArray(x$0 -> new Type[x$0]) : new Type[1], (Type[])Arrays.<AnnotatedType>stream(lowerBounds).map(AnnotatedType::getType).toArray(x$0 -> new Type[x$0]));
/*  44 */     return new AnnotatedWildcardTypeImpl(inner, type.getAnnotations(), lowerBounds, upperBounds);
/*     */   }
/*     */ 
/*     */   
/*     */   protected AnnotatedType visitVariable(AnnotatedTypeVariable type) {
/*  49 */     TypeVariable<?> var = (TypeVariable)type.getType();
/*  50 */     if (this.varCache.containsKey(var)) {
/*  51 */       return this.varCache.get(var);
/*     */     }
/*  53 */     AnnotatedTypeVariableImpl variable = new AnnotatedTypeVariableImpl(var, type.getAnnotations());
/*  54 */     this.varCache.put(var, variable);
/*     */ 
/*     */     
/*  57 */     AnnotatedType[] bounds = (AnnotatedType[])Arrays.<AnnotatedType>stream(type.getAnnotatedBounds()).map(bound -> GenericTypeReflector.transform(bound, this)).toArray(x$0 -> new AnnotatedType[x$0]);
/*  58 */     variable.init(bounds);
/*  59 */     return variable;
/*     */   }
/*     */   
/*     */   protected AnnotatedType visitArray(AnnotatedArrayType type) {
/*  63 */     AnnotatedType componentType = GenericTypeReflector.transform(type.getAnnotatedGenericComponentType(), this);
/*  64 */     return new AnnotatedArrayTypeImpl(GenericArrayTypeImpl.createArrayType(componentType.getType()), type.getAnnotations(), componentType);
/*     */   }
/*     */   
/*     */   protected AnnotatedType visitCaptureType(AnnotatedCaptureType type) {
/*  68 */     AnnotatedCaptureCacheKey key = new AnnotatedCaptureCacheKey(type);
/*  69 */     if (this.captureCache.containsKey(key)) {
/*  70 */       return this.captureCache.get(key);
/*     */     }
/*  72 */     AnnotatedType[] lowerBounds = type.getAnnotatedLowerBounds();
/*  73 */     if (lowerBounds != null)
/*     */     {
/*     */       
/*  76 */       lowerBounds = (AnnotatedType[])Arrays.<AnnotatedType>stream(lowerBounds).map(bound -> GenericTypeReflector.transform(bound, this)).toArray(x$0 -> new AnnotatedType[x$0]);
/*     */     }
/*     */ 
/*     */     
/*  80 */     AnnotatedCaptureType annotatedCapture = new AnnotatedCaptureTypeImpl((CaptureType)type.getType(), type.getAnnotatedWildcardType(), type.getAnnotatedTypeVariable(), lowerBounds, null, type.getAnnotations());
/*  81 */     this.captureCache.put(key, annotatedCapture);
/*     */ 
/*     */     
/*  84 */     AnnotatedType[] upperBounds = (AnnotatedType[])Arrays.<AnnotatedType>stream(type.getAnnotatedUpperBounds()).map(bound -> GenericTypeReflector.transform(bound, this)).toArray(x$0 -> new AnnotatedType[x$0]);
/*  85 */     annotatedCapture.setAnnotatedUpperBounds(upperBounds);
/*  86 */     return annotatedCapture;
/*     */   }
/*     */   
/*     */   protected AnnotatedType visitClass(AnnotatedType type) {
/*  90 */     return type;
/*     */   }
/*     */   
/*     */   protected AnnotatedType visitUnmatched(AnnotatedType type) {
/*  94 */     return type;
/*     */   }
/*     */   
/*     */   private static class AnnotatedCaptureCacheKey {
/*     */     AnnotatedCaptureType capture;
/*     */     CaptureType raw;
/*     */     
/*     */     AnnotatedCaptureCacheKey(AnnotatedCaptureType capture) {
/* 102 */       this.capture = capture;
/* 103 */       this.raw = (CaptureType)capture.getType();
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 108 */       return 127 * this.raw.getWildcardType().hashCode() ^ this.raw.getTypeVariable().hashCode() ^ GenericTypeReflector.hashCode(Arrays.stream(this.capture.getAnnotations()));
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 113 */       if (this == obj) return true; 
/* 114 */       if (!(obj instanceof AnnotatedCaptureCacheKey)) return false;
/*     */       
/* 116 */       AnnotatedCaptureCacheKey that = (AnnotatedCaptureCacheKey)obj;
/* 117 */       return (this.capture == that.capture || ((new GenericTypeReflector.CaptureCacheKey(this.raw))
/* 118 */         .equals(new GenericTypeReflector.CaptureCacheKey(that.raw)) && 
/* 119 */         Arrays.equals((Object[])this.capture.getAnnotations(), (Object[])that.capture.getAnnotations())));
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\geantyref\TypeVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */