/*     */ package ac.grim.grimac.shaded.geantyref;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.AnnotatedArrayType;
/*     */ import java.lang.reflect.AnnotatedParameterizedType;
/*     */ import java.lang.reflect.AnnotatedType;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.lang.reflect.WildcardType;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
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
/*     */ public class TypeFactory
/*     */ {
/*  30 */   private static final WildcardType UNBOUND_WILDCARD = new WildcardTypeImpl(new Type[] { Object.class }, new Type[0]);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Type parameterizedClass(Class<?> clazz, Type... arguments) {
/*  45 */     return parameterizedInnerClass(null, clazz, arguments);
/*     */   }
/*     */   
/*     */   public static AnnotatedType annotatedClass(Class<?> clazz, Annotation[] annotations) {
/*  49 */     return parameterizedAnnotatedClass(clazz, annotations, new AnnotatedType[0]);
/*     */   }
/*     */   
/*     */   public static AnnotatedType parameterizedAnnotatedClass(Class<?> clazz, Annotation[] annotations, AnnotatedType... arguments) {
/*  53 */     return parameterizedAnnotatedInnerClass(null, clazz, annotations, arguments);
/*     */   }
/*     */   
/*     */   public static AnnotatedType annotatedInnerClass(Type owner, Class<?> clazz, Annotation[] annotations) {
/*  57 */     return parameterizedAnnotatedInnerClass(owner, clazz, annotations, new AnnotatedType[0]);
/*     */   }
/*     */   
/*     */   public static AnnotatedType parameterizedAnnotatedInnerClass(Type owner, Class<?> clazz, Annotation[] annotations, AnnotatedType... arguments) {
/*  61 */     if (arguments == null || arguments.length == 0) {
/*  62 */       return GenericTypeReflector.annotate(clazz, annotations);
/*     */     }
/*  64 */     Type[] typeArguments = (Type[])Arrays.<AnnotatedType>stream(arguments).map(AnnotatedType::getType).toArray(x$0 -> new Type[x$0]);
/*  65 */     return new AnnotatedParameterizedTypeImpl((ParameterizedType)parameterizedInnerClass(owner, clazz, typeArguments), annotations, arguments);
/*     */   }
/*     */   
/*     */   public static AnnotatedParameterizedType parameterizedAnnotatedType(ParameterizedType type, Annotation[] typeAnnotations, Annotation[]... argumentAnnotations) {
/*  69 */     if (argumentAnnotations == null || argumentAnnotations.length == 0) {
/*  70 */       return (AnnotatedParameterizedType)GenericTypeReflector.annotate(type, typeAnnotations);
/*     */     }
/*  72 */     AnnotatedType[] typeArguments = new AnnotatedType[(type.getActualTypeArguments()).length];
/*  73 */     for (int i = 0; i < typeArguments.length; i++) {
/*  74 */       Annotation[] annotations = (argumentAnnotations.length > i) ? argumentAnnotations[i] : null;
/*  75 */       typeArguments[i] = GenericTypeReflector.annotate(type.getActualTypeArguments()[i], annotations);
/*     */     } 
/*  77 */     return (AnnotatedParameterizedType)parameterizedAnnotatedClass(GenericTypeReflector.erase(type), typeAnnotations, typeArguments);
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
/*     */   public static Type innerClass(Type owner, Class<?> clazz) {
/*  91 */     return parameterizedInnerClass(owner, clazz, (Type[])null);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Type parameterizedInnerClass(Type owner, Class<?> clazz, Type... arguments) {
/* 128 */     if (clazz.getDeclaringClass() == null && owner != null) {
/* 129 */       throw new IllegalArgumentException("Cannot specify an owner type for a top level class");
/*     */     }
/*     */     
/* 132 */     Type realOwner = transformOwner(owner, clazz);
/*     */     
/* 134 */     if (arguments == null) {
/* 135 */       if ((clazz.getTypeParameters()).length == 0) {
/*     */ 
/*     */         
/* 138 */         arguments = new Type[0];
/*     */       } else {
/*     */         
/* 141 */         return clazz;
/*     */       }
/*     */     
/* 144 */     } else if (arguments.length != (clazz.getTypeParameters()).length) {
/* 145 */       throw new IllegalArgumentException("Incorrect number of type arguments for [" + clazz + "]: expected " + (clazz
/* 146 */           .getTypeParameters()).length + ", but got " + arguments.length);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 151 */     if (!GenericTypeReflector.isMissingTypeParameters(clazz)) {
/* 152 */       return clazz;
/*     */     }
/*     */ 
/*     */     
/* 156 */     if (realOwner != null && !Modifier.isStatic(clazz.getModifiers()) && 
/* 157 */       GenericTypeReflector.isMissingTypeParameters(realOwner)) {
/* 158 */       return clazz;
/*     */     }
/*     */     
/* 161 */     ParameterizedType result = new ParameterizedTypeImpl(clazz, arguments, realOwner);
/* 162 */     checkParametersWithinBound(result);
/* 163 */     return result;
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
/*     */   private static void checkParametersWithinBound(ParameterizedType type) {
/* 178 */     Type[] arguments = type.getActualTypeArguments();
/* 179 */     TypeVariable[] arrayOfTypeVariable = ((Class)type.getRawType()).getTypeParameters();
/*     */ 
/*     */     
/* 182 */     VarMap varMap = new VarMap(type);
/*     */ 
/*     */     
/* 185 */     for (int i = 0; i < arguments.length; i++) {
/* 186 */       for (Type bound : arrayOfTypeVariable[i].getBounds()) {
/*     */         
/* 188 */         Type replacedBound = varMap.map(bound);
/*     */ 
/*     */         
/* 191 */         if (arguments[i] instanceof WildcardType) {
/* 192 */           WildcardType wildcardTypeParameter = (WildcardType)arguments[i];
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 197 */           for (Type wildcardUpperBound : wildcardTypeParameter.getUpperBounds()) {
/* 198 */             if (!couldHaveCommonSubtype(replacedBound, wildcardUpperBound)) {
/* 199 */               throw new TypeArgumentNotInBoundException(arguments[i], arrayOfTypeVariable[i], bound);
/*     */             }
/*     */           } 
/*     */           
/* 203 */           for (Type wildcardLowerBound : wildcardTypeParameter.getLowerBounds()) {
/* 204 */             if (!GenericTypeReflector.isSuperType(replacedBound, wildcardLowerBound)) {
/* 205 */               throw new TypeArgumentNotInBoundException(arguments[i], arrayOfTypeVariable[i], bound);
/*     */             }
/*     */           }
/*     */         
/* 209 */         } else if (!GenericTypeReflector.isSuperType(replacedBound, arguments[i])) {
/* 210 */           throw new TypeArgumentNotInBoundException(arguments[i], arrayOfTypeVariable[i], bound);
/*     */         } 
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
/*     */   private static boolean couldHaveCommonSubtype(Type type1, Type type2) {
/* 225 */     Class<?> erased1 = GenericTypeReflector.erase(type1);
/* 226 */     Class<?> erased2 = GenericTypeReflector.erase(type2);
/*     */     
/* 228 */     if (!erased1.isInterface() && !erased2.isInterface())
/*     */     {
/* 230 */       if (!erased1.isAssignableFrom(erased2) && !erased2.isAssignableFrom(erased1)) {
/* 231 */         return false;
/*     */       }
/*     */     }
/* 234 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Type transformOwner(Type givenOwner, Class<?> clazz) {
/* 242 */     if (givenOwner == null)
/*     */     {
/*     */       
/* 245 */       return clazz.getDeclaringClass();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 251 */     Type transformedOwner = GenericTypeReflector.getExactSuperType(GenericTypeReflector.annotate(givenOwner).getType(), clazz
/* 252 */         .getDeclaringClass());
/*     */     
/* 254 */     if (transformedOwner == null) {
/* 255 */       throw new IllegalArgumentException("Given owner type [" + givenOwner + "] is not appropriate for [" + clazz + "]: it should be a subtype of " + clazz
/* 256 */           .getDeclaringClass());
/*     */     }
/*     */     
/* 259 */     if (Modifier.isStatic(clazz.getModifiers()))
/*     */     {
/* 261 */       return GenericTypeReflector.erase(transformedOwner);
/*     */     }
/* 263 */     return transformedOwner;
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
/*     */   public static WildcardType unboundWildcard() {
/* 275 */     return UNBOUND_WILDCARD;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WildcardType wildcardExtends(Type upperBound) {
/* 286 */     if (upperBound == null) {
/* 287 */       throw new NullPointerException();
/*     */     }
/* 289 */     return new WildcardTypeImpl(new Type[] { upperBound }, new Type[0]);
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
/*     */   public static WildcardType wildcardSuper(Type lowerBound) {
/* 301 */     if (lowerBound == null) {
/* 302 */       throw new NullPointerException();
/*     */     }
/* 304 */     return new WildcardTypeImpl(new Type[] { Object.class }, new Type[] { lowerBound });
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
/*     */   public static Type arrayOf(Type componentType) {
/* 319 */     return GenericArrayTypeImpl.createArrayType(componentType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static AnnotatedArrayType arrayOf(AnnotatedType componentType, Annotation[] annotations) {
/* 330 */     return AnnotatedArrayTypeImpl.createArrayType(componentType, annotations);
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
/*     */   public static <A extends Annotation> A annotation(Class<A> annotationType, Map<String, Object> values) throws AnnotationFormatException {
/* 344 */     return (A)Proxy.newProxyInstance(annotationType.getClassLoader(), new Class[] { annotationType }, new AnnotationInvocationHandler(annotationType, 
/*     */           
/* 346 */           (values == null) ? Collections.<String, Object>emptyMap() : values));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\geantyref\TypeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */