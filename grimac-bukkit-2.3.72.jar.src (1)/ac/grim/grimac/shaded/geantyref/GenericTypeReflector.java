/*      */ package ac.grim.grimac.shaded.geantyref;
/*      */ 
/*      */ import java.io.Serializable;
/*      */ import java.lang.annotation.Annotation;
/*      */ import java.lang.reflect.AnnotatedArrayType;
/*      */ import java.lang.reflect.AnnotatedParameterizedType;
/*      */ import java.lang.reflect.AnnotatedType;
/*      */ import java.lang.reflect.AnnotatedTypeVariable;
/*      */ import java.lang.reflect.AnnotatedWildcardType;
/*      */ import java.lang.reflect.Executable;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.GenericArrayType;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.lang.reflect.ParameterizedType;
/*      */ import java.lang.reflect.Type;
/*      */ import java.lang.reflect.TypeVariable;
/*      */ import java.lang.reflect.WildcardType;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Objects;
/*      */ import java.util.Set;
/*      */ import java.util.function.Function;
/*      */ import java.util.function.IntFunction;
/*      */ import java.util.stream.Stream;
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
/*      */ public class GenericTypeReflector
/*      */ {
/*   47 */   private static final WildcardType UNBOUND_WILDCARD = new WildcardTypeImpl(new Type[] { Object.class }, new Type[0]);
/*      */   
/*      */   private static final Map<Class<?>, Class<?>> BOX_TYPES;
/*      */   
/*      */   static {
/*   52 */     Map<Class<?>, Class<?>> boxTypes = new HashMap<>();
/*   53 */     boxTypes.put(boolean.class, Boolean.class);
/*   54 */     boxTypes.put(byte.class, Byte.class);
/*   55 */     boxTypes.put(char.class, Character.class);
/*   56 */     boxTypes.put(double.class, Double.class);
/*   57 */     boxTypes.put(float.class, Float.class);
/*   58 */     boxTypes.put(int.class, Integer.class);
/*   59 */     boxTypes.put(long.class, Long.class);
/*   60 */     boxTypes.put(short.class, Short.class);
/*   61 */     boxTypes.put(void.class, Void.class);
/*   62 */     BOX_TYPES = Collections.unmodifiableMap(boxTypes);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<?> erase(Type type) {
/*   69 */     if (type instanceof Class)
/*   70 */       return (Class)type; 
/*   71 */     if (type instanceof ParameterizedType)
/*   72 */       return (Class)((ParameterizedType)type).getRawType(); 
/*   73 */     if (type instanceof TypeVariable) {
/*   74 */       TypeVariable<?> tv = (TypeVariable)type;
/*   75 */       if ((tv.getBounds()).length == 0) {
/*   76 */         return Object.class;
/*      */       }
/*   78 */       return erase(tv.getBounds()[0]);
/*   79 */     }  if (type instanceof GenericArrayType) {
/*   80 */       GenericArrayType aType = (GenericArrayType)type;
/*   81 */       return GenericArrayTypeImpl.createArrayType(erase(aType.getGenericComponentType()));
/*   82 */     }  if (type instanceof WildcardType) {
/*   83 */       WildcardType wildcardType = (WildcardType)type;
/*   84 */       Type[] lowerBounds = wildcardType.getLowerBounds();
/*   85 */       return erase((lowerBounds.length > 0) ? lowerBounds[0] : wildcardType.getUpperBounds()[0]);
/*   86 */     }  if (type instanceof CaptureType) {
/*   87 */       CaptureType captureType = (CaptureType)type;
/*   88 */       Type[] lowerBounds = captureType.getLowerBounds();
/*   89 */       return erase((lowerBounds.length > 0) ? lowerBounds[0] : captureType.getUpperBounds()[0]);
/*      */     } 
/*   91 */     throw new RuntimeException("not supported: " + type.getClass());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Type box(Type type) {
/*   97 */     Class<?> boxed = BOX_TYPES.get(type);
/*   98 */     return (boxed != null) ? boxed : type;
/*      */   }
/*      */ 
/*      */   
/*      */   public static boolean isBoxType(Type type) {
/*  103 */     return BOX_TYPES.containsValue(type);
/*      */   }
/*      */   
/*      */   public static boolean isFullyBound(Type type) {
/*  107 */     if (type instanceof Class) {
/*  108 */       return true;
/*      */     }
/*  110 */     if (type instanceof ParameterizedType) {
/*  111 */       return Arrays.<Type>stream(((ParameterizedType)type).getActualTypeArguments()).allMatch(GenericTypeReflector::isFullyBound);
/*      */     }
/*  113 */     if (type instanceof GenericArrayType) {
/*  114 */       return isFullyBound(((GenericArrayType)type).getGenericComponentType());
/*      */     }
/*  116 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static AnnotatedType mapTypeParameters(AnnotatedType toMapType, AnnotatedType typeAndParams) {
/*  126 */     return mapTypeParameters(toMapType, typeAndParams, VarMap.MappingMode.EXACT);
/*      */   }
/*      */   
/*      */   private static AnnotatedType mapTypeParameters(AnnotatedType toMapType, AnnotatedType typeAndParams, VarMap.MappingMode mappingMode) {
/*  130 */     if (isMissingTypeParameters(typeAndParams.getType())) {
/*  131 */       return new AnnotatedTypeImpl(erase(toMapType.getType()), toMapType.getAnnotations());
/*      */     }
/*  133 */     VarMap varMap = new VarMap();
/*  134 */     AnnotatedType handlingTypeAndParams = typeAndParams;
/*  135 */     while (handlingTypeAndParams instanceof AnnotatedParameterizedType) {
/*  136 */       AnnotatedParameterizedType pType = (AnnotatedParameterizedType)handlingTypeAndParams;
/*  137 */       Class<?> clazz = (Class)((ParameterizedType)pType.getType()).getRawType();
/*  138 */       TypeVariable[] arrayOfTypeVariable = (TypeVariable[])clazz.getTypeParameters();
/*  139 */       varMap.addAll(arrayOfTypeVariable, pType.getAnnotatedActualTypeArguments());
/*  140 */       Type owner = ((ParameterizedType)pType.getType()).getOwnerType();
/*  141 */       handlingTypeAndParams = (owner == null) ? null : annotate(owner);
/*      */     } 
/*  143 */     return varMap.map(toMapType, mappingMode);
/*      */   }
/*      */ 
/*      */   
/*      */   public static AnnotatedType resolveExactType(AnnotatedType unresolved, AnnotatedType typeAndParams) {
/*  148 */     return resolveType(unresolved, expandGenerics(typeAndParams), VarMap.MappingMode.EXACT);
/*      */   }
/*      */   
/*      */   public static Type resolveExactType(Type unresolved, Type typeAndParams) {
/*  152 */     return resolveType(annotate(unresolved), annotate(typeAndParams, true), VarMap.MappingMode.EXACT).getType();
/*      */   }
/*      */   
/*      */   public static AnnotatedType resolveType(AnnotatedType unresolved, AnnotatedType typeAndParams) {
/*  156 */     return resolveType(unresolved, expandGenerics(typeAndParams), VarMap.MappingMode.ALLOW_INCOMPLETE);
/*      */   }
/*      */   
/*      */   public static Type resolveType(Type unresolved, Type typeAndParams) {
/*  160 */     return resolveType(annotate(unresolved), annotate(typeAndParams, true), VarMap.MappingMode.ALLOW_INCOMPLETE).getType();
/*      */   }
/*      */   
/*      */   private static AnnotatedType resolveType(AnnotatedType unresolved, AnnotatedType typeAndParams, VarMap.MappingMode mappingMode) {
/*  164 */     if (unresolved instanceof AnnotatedParameterizedType) {
/*  165 */       AnnotatedParameterizedType parameterizedType = (AnnotatedParameterizedType)unresolved;
/*  166 */       AnnotatedType[] params = mapArray(parameterizedType.getAnnotatedActualTypeArguments(), x$0 -> new AnnotatedType[x$0], p -> resolveType(p, typeAndParams, mappingMode));
/*      */       
/*  168 */       return replaceParameters(parameterizedType, params);
/*      */     } 
/*  170 */     if (unresolved instanceof AnnotatedWildcardType) {
/*  171 */       AnnotatedType[] lower = mapArray(((AnnotatedWildcardType)unresolved).getAnnotatedLowerBounds(), x$0 -> new AnnotatedType[x$0], b -> resolveType(b, typeAndParams, mappingMode));
/*      */       
/*  173 */       AnnotatedType[] upper = mapArray(((AnnotatedWildcardType)unresolved).getAnnotatedUpperBounds(), x$0 -> new AnnotatedType[x$0], b -> resolveType(b, typeAndParams, mappingMode));
/*      */       
/*  175 */       return new AnnotatedWildcardTypeImpl((WildcardType)unresolved.getType(), unresolved.getAnnotations(), lower, upper);
/*      */     } 
/*  177 */     if (unresolved instanceof AnnotatedTypeVariable) {
/*  178 */       TypeVariable<?> var = (TypeVariable)unresolved.getType();
/*  179 */       if (var.getGenericDeclaration() instanceof Class) {
/*      */         
/*  181 */         AnnotatedType resolved = getTypeParameter(typeAndParams, (TypeVariable)var);
/*  182 */         if (resolved != null) {
/*  183 */           return updateAnnotations(resolved, unresolved.getAnnotations());
/*      */         }
/*      */       } 
/*  186 */       if (mappingMode.equals(VarMap.MappingMode.ALLOW_INCOMPLETE)) {
/*  187 */         return unresolved;
/*      */       }
/*  189 */       throw new IllegalArgumentException("Variable " + var.getName() + " is not declared by the given type " + typeAndParams
/*  190 */           .getType().getTypeName() + " or its super types");
/*      */     } 
/*  192 */     if (unresolved instanceof AnnotatedArrayType) {
/*  193 */       AnnotatedType componentType = resolveType(((AnnotatedArrayType)unresolved)
/*  194 */           .getAnnotatedGenericComponentType(), typeAndParams, mappingMode);
/*  195 */       return new AnnotatedArrayTypeImpl(TypeFactory.arrayOf(componentType.getType()), unresolved.getAnnotations(), componentType);
/*      */     } 
/*      */     
/*  198 */     return unresolved;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isMissingTypeParameters(Type type) {
/*  206 */     if (type instanceof Class) {
/*  207 */       Class<?> clazz = (Class)type;
/*  208 */       if (Modifier.isStatic(clazz.getModifiers())) {
/*  209 */         return ((clazz.getTypeParameters()).length != 0);
/*      */       }
/*  211 */       for (Class<?> enclosing = clazz; enclosing != null; enclosing = enclosing.getEnclosingClass()) {
/*  212 */         if ((enclosing.getTypeParameters()).length != 0)
/*  213 */           return true; 
/*      */       } 
/*  215 */       return false;
/*  216 */     }  if (type instanceof ParameterizedType) {
/*  217 */       return false;
/*      */     }
/*  219 */     throw new AssertionError("Unexpected type " + type.getClass());
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
/*      */   public static Type addWildcardParameters(Class<?> clazz) {
/*  233 */     if (clazz.isArray())
/*  234 */       return GenericArrayTypeImpl.createArrayType(addWildcardParameters(clazz.getComponentType())); 
/*  235 */     if (isMissingTypeParameters(clazz)) {
/*  236 */       TypeVariable[] arrayOfTypeVariable = (TypeVariable[])clazz.getTypeParameters();
/*  237 */       Type[] arguments = new Type[arrayOfTypeVariable.length];
/*  238 */       Arrays.fill((Object[])arguments, UNBOUND_WILDCARD);
/*  239 */       Type owner = (clazz.getDeclaringClass() == null) ? null : addWildcardParameters(clazz.getDeclaringClass());
/*  240 */       return new ParameterizedTypeImpl(clazz, arguments, owner);
/*      */     } 
/*  242 */     return clazz;
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
/*      */   public static AnnotatedType getExactSuperType(AnnotatedType subType, Class<?> searchSuperClass) {
/*  255 */     if (subType instanceof AnnotatedParameterizedType || subType.getType() instanceof Class || subType instanceof AnnotatedArrayType) {
/*  256 */       Class<?> superClass = erase(subType.getType());
/*      */       
/*  258 */       if (searchSuperClass == superClass) {
/*  259 */         return subType;
/*      */       }
/*      */       
/*  262 */       if (!searchSuperClass.isAssignableFrom(superClass)) {
/*  263 */         return null;
/*      */       }
/*      */     } 
/*      */     
/*  267 */     for (AnnotatedType superType : getExactDirectSuperTypes(subType)) {
/*  268 */       AnnotatedType result = getExactSuperType(superType, searchSuperClass);
/*  269 */       if (result != null) {
/*  270 */         return result;
/*      */       }
/*      */     } 
/*  273 */     return null;
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
/*      */   public static Type getExactSuperType(Type subType, Class<?> searchSuperClass) {
/*  297 */     AnnotatedType superType = getExactSuperType(annotate(subType), searchSuperClass);
/*  298 */     return (superType == null) ? null : superType.getType();
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
/*      */   public static AnnotatedType getExactSubType(AnnotatedType superType, Class<?> searchSubClass) {
/*  310 */     Type<?> subType = searchSubClass;
/*  311 */     if ((searchSubClass.getTypeParameters()).length > 0) {
/*  312 */       subType = TypeFactory.parameterizedClass(searchSubClass, (Type[])searchSubClass.getTypeParameters());
/*      */     }
/*  314 */     AnnotatedType annotatedSubType = annotate(subType);
/*  315 */     Class<?> rawSuperType = erase(superType.getType());
/*  316 */     if (searchSubClass.isArray() && superType instanceof AnnotatedArrayType) {
/*  317 */       if (rawSuperType.isAssignableFrom(searchSubClass)) {
/*  318 */         return AnnotatedArrayTypeImpl.createArrayType(
/*  319 */             getExactSubType(((AnnotatedArrayType)superType).getAnnotatedGenericComponentType(), searchSubClass.getComponentType()), new Annotation[0]);
/*      */       }
/*      */       
/*  322 */       return null;
/*      */     } 
/*      */     
/*  325 */     if ((searchSubClass.getTypeParameters()).length == 0) {
/*  326 */       return annotatedSubType;
/*      */     }
/*  328 */     if (!(superType instanceof AnnotatedParameterizedType)) {
/*  329 */       return annotate(searchSubClass);
/*      */     }
/*  331 */     AnnotatedParameterizedType parameterizedSuperType = (AnnotatedParameterizedType)superType;
/*  332 */     AnnotatedParameterizedType matched = (AnnotatedParameterizedType)getExactSuperType(annotatedSubType, rawSuperType);
/*  333 */     if (matched == null) return null; 
/*  334 */     VarMap varMap = new VarMap();
/*      */     try {
/*  336 */       extractVariables(parameterizedSuperType, matched, searchSubClass, varMap);
/*  337 */       return varMap.map(annotatedSubType);
/*  338 */     } catch (UnresolvedTypeVariableException e) {
/*  339 */       return annotate(searchSubClass);
/*  340 */     } catch (IllegalArgumentException e) {
/*  341 */       return null;
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
/*      */   public static Type getExactSubType(Type superType, Class<?> searchSubClass) {
/*  362 */     AnnotatedType resolvedSubtype = getExactSubType(annotate(superType), searchSubClass);
/*  363 */     return (resolvedSubtype == null) ? null : resolvedSubtype.getType();
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
/*      */   public static AnnotatedType getTypeParameter(AnnotatedType type, TypeVariable<? extends Class<?>> variable) {
/*  378 */     Class<?> clazz = variable.getGenericDeclaration();
/*  379 */     AnnotatedType superType = getExactSuperType(type, clazz);
/*  380 */     if (superType instanceof AnnotatedParameterizedType) {
/*  381 */       int index = Arrays.asList((Object[])clazz.getTypeParameters()).indexOf(variable);
/*  382 */       AnnotatedType resolvedVarType = ((AnnotatedParameterizedType)superType).getAnnotatedActualTypeArguments()[index];
/*  383 */       return updateAnnotations(resolvedVarType, variable.getAnnotations());
/*      */     } 
/*  385 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public static Type getTypeParameter(Type type, TypeVariable<? extends Class<?>> variable) {
/*  390 */     AnnotatedType typeParameter = getTypeParameter(annotate(type), variable);
/*  391 */     return (typeParameter == null) ? null : typeParameter.getType();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isSuperType(Type superType, Type subType) {
/*  398 */     if (superType instanceof ParameterizedType || superType instanceof Class || superType instanceof GenericArrayType) {
/*  399 */       Class<?> superClass = erase(superType);
/*  400 */       AnnotatedType annotatedMappedSubType = getExactSuperType(capture(annotate(subType)), superClass);
/*  401 */       Type mappedSubType = (annotatedMappedSubType == null) ? null : annotatedMappedSubType.getType();
/*  402 */       if (mappedSubType == null)
/*  403 */         return false; 
/*  404 */       if (superType instanceof Class)
/*  405 */         return true; 
/*  406 */       if (mappedSubType instanceof Class)
/*      */       {
/*  408 */         return true; } 
/*  409 */       if (mappedSubType instanceof GenericArrayType) {
/*  410 */         Type superComponentType = getArrayComponentType(superType);
/*  411 */         assert superComponentType != null;
/*  412 */         Type mappedSubComponentType = getArrayComponentType(mappedSubType);
/*  413 */         assert mappedSubComponentType != null;
/*  414 */         return isSuperType(superComponentType, mappedSubComponentType);
/*      */       } 
/*  416 */       assert mappedSubType instanceof ParameterizedType;
/*  417 */       assert superType instanceof ParameterizedType;
/*  418 */       ParameterizedType pMappedSubType = (ParameterizedType)mappedSubType;
/*  419 */       assert pMappedSubType.getRawType() == superClass;
/*  420 */       ParameterizedType pSuperType = (ParameterizedType)superType;
/*      */       
/*  422 */       Type[] superTypeArgs = pSuperType.getActualTypeArguments();
/*  423 */       Type[] subTypeArgs = pMappedSubType.getActualTypeArguments();
/*  424 */       assert superTypeArgs.length == subTypeArgs.length;
/*  425 */       for (int i = 0; i < superTypeArgs.length; i++) {
/*  426 */         if (!contains(superTypeArgs[i], subTypeArgs[i])) {
/*  427 */           return false;
/*      */         }
/*      */       } 
/*      */       
/*  431 */       return (pSuperType.getOwnerType() == null || isSuperType(pSuperType.getOwnerType(), pMappedSubType.getOwnerType()));
/*      */     } 
/*  433 */     if (superType instanceof CaptureType) {
/*  434 */       if (superType.equals(subType))
/*  435 */         return true; 
/*  436 */       for (Type lowerBound : ((CaptureType)superType).getLowerBounds()) {
/*  437 */         if (isSuperType(lowerBound, subType)) {
/*  438 */           return true;
/*      */         }
/*      */       } 
/*  441 */       return false;
/*      */     } 
/*      */ 
/*      */     
/*  445 */     throw new RuntimeException("Type not supported: " + superType.getClass());
/*      */   }
/*      */ 
/*      */   
/*      */   private static boolean isArraySupertype(Type arraySuperType, Type subType) {
/*  450 */     Type superTypeComponent = getArrayComponentType(arraySuperType);
/*  451 */     assert superTypeComponent != null;
/*  452 */     Type subTypeComponent = getArrayComponentType(subType);
/*  453 */     if (subTypeComponent == null) {
/*  454 */       return false;
/*      */     }
/*  456 */     return isSuperType(superTypeComponent, subTypeComponent);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static AnnotatedType getArrayComponentType(AnnotatedType type) {
/*  465 */     if (type.getType() instanceof Class) {
/*  466 */       Class<?> clazz = (Class)type.getType();
/*  467 */       return new AnnotatedTypeImpl(clazz.getComponentType(), clazz.getAnnotations());
/*  468 */     }  if (type instanceof AnnotatedArrayType) {
/*  469 */       AnnotatedArrayType aType = (AnnotatedArrayType)type;
/*  470 */       return aType.getAnnotatedGenericComponentType();
/*      */     } 
/*  472 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Type getArrayComponentType(Type type) {
/*  481 */     AnnotatedType componentType = getArrayComponentType(annotate(type));
/*  482 */     return (componentType == null) ? null : componentType.getType();
/*      */   }
/*      */   
/*      */   private static boolean contains(Type containingType, Type containedType) {
/*  486 */     if (containingType instanceof WildcardType) {
/*  487 */       WildcardType wContainingType = (WildcardType)containingType;
/*  488 */       for (Type upperBound : wContainingType.getUpperBounds()) {
/*  489 */         if (!isSuperType(upperBound, containedType)) {
/*  490 */           return false;
/*      */         }
/*      */       } 
/*  493 */       for (Type lowerBound : wContainingType.getLowerBounds()) {
/*  494 */         if (!isSuperType(containedType, lowerBound)) {
/*  495 */           return false;
/*      */         }
/*      */       } 
/*  498 */       return true;
/*      */     } 
/*  500 */     return containingType.equals(containedType);
/*      */   }
/*      */ 
/*      */   
/*      */   private static void extractVariables(AnnotatedParameterizedType resolvedTyped, AnnotatedParameterizedType unresolvedType, Class<?> declaringClass, VarMap variables) {
/*  505 */     for (int i = 0; i < (resolvedTyped.getAnnotatedActualTypeArguments()).length; i++) {
/*  506 */       AnnotatedType unresolvedParam = unresolvedType.getAnnotatedActualTypeArguments()[i];
/*  507 */       AnnotatedType resolvedParam = resolvedTyped.getAnnotatedActualTypeArguments()[i];
/*  508 */       Type var = unresolvedParam.getType();
/*  509 */       if (var instanceof TypeVariable && ((TypeVariable<Class<?>>)var).getGenericDeclaration() == declaringClass) {
/*  510 */         variables.add((TypeVariable)var, resolvedParam);
/*  511 */       } else if (unresolvedParam instanceof AnnotatedParameterizedType) {
/*  512 */         if (!(resolvedParam instanceof AnnotatedParameterizedType) || !erase(unresolvedParam.getType()).equals(erase(resolvedParam.getType()))) {
/*  513 */           throw new IllegalArgumentException("The provided types do not match in shape");
/*      */         }
/*  515 */         extractVariables((AnnotatedParameterizedType)resolvedParam, (AnnotatedParameterizedType)unresolvedParam, declaringClass, variables);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static AnnotatedType[] getExactDirectSuperTypes(AnnotatedType type) {
/*  524 */     if (type instanceof AnnotatedParameterizedType || (type != null && type.getType() instanceof Class)) {
/*      */       Class<?> clazz; AnnotatedType[] result; int resultIndex;
/*  526 */       if (type instanceof AnnotatedParameterizedType) {
/*  527 */         clazz = (Class)((ParameterizedType)type.getType()).getRawType();
/*      */       } else {
/*      */         
/*  530 */         clazz = (Class)type.getType();
/*  531 */         if (clazz.isArray()) {
/*  532 */           return getArrayExactDirectSuperTypes(annotate(clazz));
/*      */         }
/*      */       } 
/*      */       
/*  536 */       AnnotatedType[] superInterfaces = clazz.getAnnotatedInterfaces();
/*  537 */       AnnotatedType superClass = clazz.getAnnotatedSuperclass();
/*      */ 
/*      */       
/*  540 */       if (superClass == null && superInterfaces.length == 0 && clazz.isInterface()) {
/*  541 */         return new AnnotatedType[] { new AnnotatedTypeImpl(Object.class) };
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  546 */       if (superClass == null) {
/*  547 */         result = new AnnotatedType[superInterfaces.length];
/*  548 */         resultIndex = 0;
/*      */       } else {
/*  550 */         result = new AnnotatedType[superInterfaces.length + 1];
/*  551 */         resultIndex = 1;
/*  552 */         result[0] = mapTypeParameters(superClass, type);
/*      */       } 
/*  554 */       for (AnnotatedType superInterface : superInterfaces) {
/*  555 */         result[resultIndex++] = mapTypeParameters(superInterface, type);
/*      */       }
/*      */       
/*  558 */       return result;
/*  559 */     }  if (type instanceof AnnotatedTypeVariable) {
/*  560 */       AnnotatedTypeVariable tv = (AnnotatedTypeVariable)type;
/*  561 */       return tv.getAnnotatedBounds();
/*  562 */     }  if (type instanceof AnnotatedWildcardType)
/*      */     {
/*      */ 
/*      */       
/*  566 */       return ((AnnotatedWildcardType)type).getAnnotatedUpperBounds(); } 
/*  567 */     if (type instanceof AnnotatedCaptureTypeImpl)
/*  568 */       return ((AnnotatedCaptureTypeImpl)type).getAnnotatedUpperBounds(); 
/*  569 */     if (type instanceof AnnotatedArrayType)
/*  570 */       return getArrayExactDirectSuperTypes(type); 
/*  571 */     if (type == null) {
/*  572 */       throw new NullPointerException();
/*      */     }
/*  574 */     throw new RuntimeException("not implemented type: " + type);
/*      */   }
/*      */   
/*      */   private static AnnotatedType[] getArrayExactDirectSuperTypes(AnnotatedType arrayType) {
/*      */     AnnotatedType[] result;
/*      */     int resultIndex;
/*  580 */     AnnotatedType typeComponent = getArrayComponentType(arrayType);
/*      */ 
/*      */ 
/*      */     
/*  584 */     if (typeComponent != null && typeComponent.getType() instanceof Class && ((Class)typeComponent.getType()).isPrimitive()) {
/*  585 */       resultIndex = 0;
/*  586 */       result = new AnnotatedType[3];
/*      */     } else {
/*  588 */       AnnotatedType[] componentSupertypes = getExactDirectSuperTypes(typeComponent);
/*  589 */       result = new AnnotatedType[componentSupertypes.length + 3];
/*  590 */       for (resultIndex = 0; resultIndex < componentSupertypes.length; resultIndex++) {
/*  591 */         result[resultIndex] = AnnotatedArrayTypeImpl.createArrayType(componentSupertypes[resultIndex], new Annotation[0]);
/*      */       }
/*      */     } 
/*  594 */     result[resultIndex++] = new AnnotatedTypeImpl(Object.class);
/*  595 */     result[resultIndex++] = new AnnotatedTypeImpl(Cloneable.class);
/*  596 */     result[resultIndex++] = new AnnotatedTypeImpl(Serializable.class);
/*  597 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static AnnotatedType getExactReturnType(Method m, AnnotatedType declaringType) {
/*  606 */     return getReturnType(m, declaringType, VarMap.MappingMode.EXACT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Type getExactReturnType(Method m, Type declaringType) {
/*  615 */     return getExactReturnType(m, annotate(declaringType)).getType();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static AnnotatedType getReturnType(Method m, AnnotatedType declaringType) {
/*  625 */     return getReturnType(m, declaringType, VarMap.MappingMode.ALLOW_INCOMPLETE);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Type getReturnType(Method m, Type declaringType) {
/*  635 */     return getReturnType(m, annotate(declaringType)).getType();
/*      */   }
/*      */   
/*      */   private static AnnotatedType getReturnType(Method m, AnnotatedType declaringType, VarMap.MappingMode mappingMode) {
/*  639 */     AnnotatedType returnType = m.getAnnotatedReturnType();
/*  640 */     AnnotatedType exactDeclaringType = getExactSuperType(capture(declaringType), m.getDeclaringClass());
/*  641 */     if (exactDeclaringType == null) {
/*  642 */       throw new IllegalArgumentException("The method " + m + " is not a member of type " + declaringType);
/*      */     }
/*  644 */     return mapTypeParameters(returnType, exactDeclaringType, mappingMode);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static AnnotatedType getExactFieldType(Field f, AnnotatedType declaringType) {
/*  653 */     return getFieldType(f, declaringType, VarMap.MappingMode.EXACT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Type getExactFieldType(Field f, Type type) {
/*  662 */     return getExactFieldType(f, annotate(type)).getType();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static AnnotatedType getFieldType(Field f, AnnotatedType declaringType) {
/*  672 */     return getFieldType(f, declaringType, VarMap.MappingMode.ALLOW_INCOMPLETE);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Type getFieldType(Field f, Type type) {
/*  682 */     return getFieldType(f, annotate(type)).getType();
/*      */   }
/*      */   
/*      */   private static AnnotatedType getFieldType(Field f, AnnotatedType declaringType, VarMap.MappingMode mappingMode) {
/*  686 */     AnnotatedType returnType = f.getAnnotatedType();
/*  687 */     AnnotatedType exactDeclaringType = getExactSuperType(capture(declaringType), f.getDeclaringClass());
/*  688 */     if (exactDeclaringType == null) {
/*  689 */       throw new IllegalArgumentException("The field " + f + " is not a member of type " + declaringType);
/*      */     }
/*  691 */     return mapTypeParameters(returnType, exactDeclaringType, mappingMode);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static AnnotatedType[] getExactParameterTypes(Executable exe, AnnotatedType declaringType) {
/*  700 */     return getParameterTypes(exe, declaringType, VarMap.MappingMode.EXACT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Type[] getExactParameterTypes(Executable exe, Type declaringType) {
/*  709 */     return mapArray(getExactParameterTypes(exe, annotate(declaringType)), x$0 -> new Type[x$0], AnnotatedType::getType);
/*      */   }
/*      */   
/*      */   public static AnnotatedType[] getParameterTypes(Executable exe, AnnotatedType declaringType) {
/*  713 */     return getParameterTypes(exe, declaringType, VarMap.MappingMode.ALLOW_INCOMPLETE);
/*      */   }
/*      */   
/*      */   public static Type[] getParameterTypes(Executable exe, Type declaringType) {
/*  717 */     return mapArray(getParameterTypes(exe, annotate(declaringType)), x$0 -> new Type[x$0], AnnotatedType::getType);
/*      */   }
/*      */   
/*      */   private static AnnotatedType[] getParameterTypes(Executable exe, AnnotatedType declaringType, VarMap.MappingMode mappingMode) {
/*  721 */     AnnotatedType[] parameterTypes = exe.getAnnotatedParameterTypes();
/*  722 */     AnnotatedType exactDeclaringType = getExactSuperType(capture(declaringType), exe.getDeclaringClass());
/*  723 */     if (exactDeclaringType == null) {
/*  724 */       throw new IllegalArgumentException("The method/constructor " + exe + " is not a member of type " + declaringType);
/*      */     }
/*      */     
/*  727 */     AnnotatedType[] result = new AnnotatedType[parameterTypes.length];
/*  728 */     for (int i = 0; i < parameterTypes.length; i++) {
/*  729 */       result[i] = mapTypeParameters(parameterTypes[i], exactDeclaringType, mappingMode);
/*      */     }
/*  731 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static AnnotatedType capture(AnnotatedType type) {
/*  738 */     if (type instanceof AnnotatedParameterizedType) {
/*  739 */       return capture((AnnotatedParameterizedType)type);
/*      */     }
/*  741 */     return type;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static AnnotatedParameterizedType capture(AnnotatedParameterizedType type) {
/*  752 */     VarMap varMap = new VarMap();
/*      */ 
/*      */     
/*  755 */     List<AnnotatedCaptureTypeImpl> toInit = new ArrayList<>();
/*      */     
/*  757 */     Class<?> clazz = (Class)((ParameterizedType)type.getType()).getRawType();
/*  758 */     AnnotatedType[] arguments = type.getAnnotatedActualTypeArguments();
/*  759 */     TypeVariable[] arrayOfTypeVariable = (TypeVariable[])clazz.getTypeParameters();
/*  760 */     AnnotatedType[] capturedArguments = new AnnotatedType[arguments.length];
/*      */     
/*  762 */     assert arguments.length == arrayOfTypeVariable.length;
/*      */     
/*  764 */     for (int i = 0; i < arguments.length; i++) {
/*  765 */       AnnotatedType argument = arguments[i];
/*  766 */       if (argument instanceof AnnotatedWildcardType) {
/*  767 */         AnnotatedCaptureTypeImpl captured = new AnnotatedCaptureTypeImpl((AnnotatedWildcardType)argument, new AnnotatedTypeVariableImpl(arrayOfTypeVariable[i]));
/*  768 */         argument = captured;
/*  769 */         toInit.add(captured);
/*      */       } 
/*  771 */       capturedArguments[i] = argument;
/*  772 */       varMap.add(arrayOfTypeVariable[i], argument);
/*      */     } 
/*  774 */     for (AnnotatedCaptureTypeImpl captured : toInit) {
/*  775 */       captured.init(varMap);
/*      */     }
/*  777 */     ParameterizedType inner = (ParameterizedType)type.getType();
/*  778 */     AnnotatedType ownerType = (inner.getOwnerType() == null) ? null : capture(annotate(inner.getOwnerType()));
/*  779 */     Type[] rawArgs = mapArray(capturedArguments, x$0 -> new Type[x$0], AnnotatedType::getType);
/*  780 */     ParameterizedType nn = new ParameterizedTypeImpl(clazz, rawArgs, (ownerType == null) ? null : ownerType.getType());
/*  781 */     return new AnnotatedParameterizedTypeImpl(nn, type.getAnnotations(), capturedArguments);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getTypeName(Type type) {
/*  788 */     if (type instanceof Class) {
/*  789 */       Class<?> clazz = (Class)type;
/*  790 */       return clazz.isArray() ? (getTypeName(clazz.getComponentType()) + "[]") : clazz.getName();
/*      */     } 
/*  792 */     return type.toString();
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
/*      */   public static List<Class<?>> getUpperBoundClassAndInterfaces(Type type) {
/*  813 */     LinkedHashSet<Class<?>> result = new LinkedHashSet<>();
/*  814 */     buildUpperBoundClassAndInterfaces(type, result);
/*  815 */     return new ArrayList<>(result);
/*      */   }
/*      */   
/*      */   private static AnnotatedType annotate(Type type, boolean expandGenerics) {
/*  819 */     return annotate(type, expandGenerics, new HashMap<>());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static AnnotatedType annotate(Type type) {
/*  830 */     return annotate(type, false);
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
/*      */   public static AnnotatedType annotate(Type type, Annotation[] annotations) {
/*  844 */     return updateAnnotations(annotate(type), annotations);
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
/*      */   private static AnnotatedType annotate(Type type, boolean expandGenerics, Map<CaptureCacheKey, AnnotatedType> cache) {
/*  866 */     if (type instanceof ParameterizedType) {
/*  867 */       ParameterizedType parameterized = (ParameterizedType)type;
/*  868 */       AnnotatedType[] params = new AnnotatedType[(parameterized.getActualTypeArguments()).length];
/*  869 */       for (int i = 0; i < params.length; i++) {
/*  870 */         AnnotatedType param = annotate(parameterized.getActualTypeArguments()[i], expandGenerics, cache);
/*  871 */         params[i] = updateAnnotations(param, erase(type).getTypeParameters()[i].getAnnotations());
/*      */       } 
/*  873 */       return new AnnotatedParameterizedTypeImpl(parameterized, erase(type).getAnnotations(), params);
/*      */     } 
/*  875 */     if (type instanceof CaptureType) {
/*  876 */       CaptureCacheKey key = new CaptureCacheKey((CaptureType)type);
/*  877 */       if (cache.containsKey(key)) {
/*  878 */         return cache.get(key);
/*      */       }
/*  880 */       CaptureType capture = (CaptureType)type;
/*      */ 
/*      */       
/*  883 */       AnnotatedCaptureType annotatedCapture = new AnnotatedCaptureTypeImpl(capture, (AnnotatedWildcardType)annotate(capture.getWildcardType(), expandGenerics, cache), (AnnotatedTypeVariable)annotate(capture.getTypeVariable(), expandGenerics, cache));
/*      */       
/*  885 */       cache.put(new CaptureCacheKey(capture), annotatedCapture);
/*  886 */       AnnotatedType[] upperBounds = mapArray(capture.getUpperBounds(), x$0 -> new AnnotatedType[x$0], bound -> annotate(bound, expandGenerics, cache));
/*      */       
/*  888 */       annotatedCapture.setAnnotatedUpperBounds(upperBounds);
/*  889 */       return annotatedCapture;
/*      */     } 
/*  891 */     if (type instanceof WildcardType) {
/*  892 */       WildcardType wildcard = (WildcardType)type;
/*  893 */       AnnotatedType[] lowerBounds = mapArray(wildcard.getLowerBounds(), x$0 -> new AnnotatedType[x$0], bound -> annotate(bound, expandGenerics, cache));
/*      */       
/*  895 */       AnnotatedType[] upperBounds = mapArray(wildcard.getUpperBounds(), x$0 -> new AnnotatedType[x$0], bound -> annotate(bound, expandGenerics, cache));
/*      */       
/*  897 */       return new AnnotatedWildcardTypeImpl(wildcard, erase(type).getAnnotations(), lowerBounds, upperBounds);
/*      */     } 
/*  899 */     if (type instanceof TypeVariable) {
/*  900 */       return new AnnotatedTypeVariableImpl((TypeVariable)type);
/*      */     }
/*  902 */     if (type instanceof GenericArrayType) {
/*  903 */       GenericArrayType genArray = (GenericArrayType)type;
/*  904 */       return new AnnotatedArrayTypeImpl(genArray, new Annotation[0], annotate(genArray.getGenericComponentType(), expandGenerics, cache));
/*      */     } 
/*  906 */     if (type instanceof Class) {
/*  907 */       Class<?> clazz = (Class)type;
/*  908 */       if (clazz.isArray()) {
/*  909 */         Class<?> componentClass = clazz.getComponentType();
/*  910 */         return AnnotatedArrayTypeImpl.createArrayType(new AnnotatedTypeImpl(componentClass, componentClass
/*  911 */               .getAnnotations()), new Annotation[0]);
/*      */       } 
/*  913 */       if ((clazz.getTypeParameters()).length > 0 && expandGenerics) {
/*  914 */         return expandClassGenerics(clazz);
/*      */       }
/*  916 */       return new AnnotatedTypeImpl(clazz, clazz.getAnnotations());
/*      */     } 
/*  918 */     throw new IllegalArgumentException("Unrecognized type: " + type.getTypeName());
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
/*      */   public static <T extends AnnotatedType> T replaceAnnotations(T original, Annotation[] annotations) {
/*  932 */     if (original instanceof AnnotatedParameterizedType) {
/*  933 */       return (T)new AnnotatedParameterizedTypeImpl((ParameterizedType)original.getType(), annotations, ((AnnotatedParameterizedType)original)
/*  934 */           .getAnnotatedActualTypeArguments());
/*      */     }
/*  936 */     if (original instanceof AnnotatedCaptureType) {
/*  937 */       AnnotatedCaptureTypeImpl capture = (AnnotatedCaptureTypeImpl)original;
/*      */       
/*  939 */       return (T)capture.setAnnotations(annotations);
/*      */     } 
/*  941 */     if (original instanceof AnnotatedWildcardType) {
/*  942 */       return (T)new AnnotatedWildcardTypeImpl((WildcardType)original.getType(), annotations, ((AnnotatedWildcardType)original)
/*  943 */           .getAnnotatedLowerBounds(), ((AnnotatedWildcardType)original)
/*  944 */           .getAnnotatedUpperBounds());
/*      */     }
/*  946 */     if (original instanceof AnnotatedTypeVariable) {
/*  947 */       return (T)new AnnotatedTypeVariableImpl((TypeVariable)original.getType(), annotations);
/*      */     }
/*  949 */     if (original instanceof AnnotatedArrayType) {
/*  950 */       return (T)new AnnotatedArrayTypeImpl(original.getType(), annotations, ((AnnotatedArrayType)original)
/*  951 */           .getAnnotatedGenericComponentType());
/*      */     }
/*  953 */     return (T)new AnnotatedTypeImpl(original.getType(), annotations);
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
/*      */   public static <T extends AnnotatedType> T updateAnnotations(T original, Annotation[] annotations) {
/*  966 */     if (annotations == null || annotations.length == 0 || Arrays.equals((Object[])original.getAnnotations(), (Object[])annotations)) {
/*  967 */       return original;
/*      */     }
/*  969 */     return replaceAnnotations(original, merge(new Annotation[][] { original.getAnnotations(), annotations }));
/*      */   }
/*      */ 
/*      */   
/*      */   public static <T extends AnnotatedType> T mergeAnnotations(T t1, T t2) {
/*  974 */     Annotation[] merged = merge(new Annotation[][] { t1.getAnnotations(), t2.getAnnotations() });
/*  975 */     if (t1 instanceof AnnotatedParameterizedType) {
/*  976 */       AnnotatedType[] p1 = ((AnnotatedParameterizedType)t1).getAnnotatedActualTypeArguments();
/*  977 */       AnnotatedType[] p2 = ((AnnotatedParameterizedType)t2).getAnnotatedActualTypeArguments();
/*  978 */       AnnotatedType[] params = new AnnotatedType[p1.length];
/*  979 */       for (int i = 0; i < p1.length; i++) {
/*  980 */         params[i] = mergeAnnotations(p1[i], p2[i]);
/*      */       }
/*  982 */       return (T)new AnnotatedParameterizedTypeImpl((ParameterizedType)t1.getType(), merged, params);
/*      */     } 
/*  984 */     if (t1 instanceof AnnotatedWildcardType) {
/*  985 */       AnnotatedType[] l1 = ((AnnotatedWildcardType)t1).getAnnotatedLowerBounds();
/*  986 */       AnnotatedType[] l2 = ((AnnotatedWildcardType)t2).getAnnotatedLowerBounds();
/*  987 */       AnnotatedType[] lowerBounds = new AnnotatedType[l1.length];
/*  988 */       for (int i = 0; i < l1.length; i++) {
/*  989 */         lowerBounds[i] = mergeAnnotations(l1[i], l2[i]);
/*      */       }
/*  991 */       AnnotatedType[] u1 = ((AnnotatedWildcardType)t1).getAnnotatedUpperBounds();
/*  992 */       AnnotatedType[] u2 = ((AnnotatedWildcardType)t2).getAnnotatedUpperBounds();
/*  993 */       AnnotatedType[] upperBounds = new AnnotatedType[u1.length];
/*  994 */       for (int j = 0; j < u1.length; j++) {
/*  995 */         upperBounds[j] = mergeAnnotations(u1[j], u2[j]);
/*      */       }
/*  997 */       return (T)new AnnotatedWildcardTypeImpl((WildcardType)t1.getType(), merged, lowerBounds, upperBounds);
/*      */     } 
/*  999 */     if (t1 instanceof AnnotatedTypeVariable) {
/* 1000 */       return (T)new AnnotatedTypeVariableImpl((TypeVariable)t1.getType(), merged);
/*      */     }
/* 1002 */     if (t1 instanceof AnnotatedArrayType) {
/* 1003 */       AnnotatedType componentType = mergeAnnotations(((AnnotatedArrayType)t1)
/* 1004 */           .getAnnotatedGenericComponentType(), ((AnnotatedArrayType)t2)
/* 1005 */           .getAnnotatedGenericComponentType());
/* 1006 */       return (T)new AnnotatedArrayTypeImpl(t1.getType(), merged, componentType);
/*      */     } 
/* 1008 */     return (T)new AnnotatedTypeImpl(t1.getType(), merged);
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
/*      */   public static AnnotatedParameterizedType replaceParameters(AnnotatedParameterizedType type, AnnotatedType[] typeParameters) {
/* 1020 */     return replaceParameters(type, new Annotation[0], typeParameters);
/*      */   }
/*      */   
/*      */   private static AnnotatedParameterizedType replaceParameters(AnnotatedParameterizedType type, Annotation[] annotations, AnnotatedType[] typeParameters) {
/* 1024 */     Type[] rawArguments = mapArray(typeParameters, x$0 -> new Type[x$0], AnnotatedType::getType);
/* 1025 */     ParameterizedType inner = (ParameterizedType)type.getType();
/* 1026 */     ParameterizedType rawType = (ParameterizedType)TypeFactory.parameterizedInnerClass(inner.getOwnerType(), erase(inner), rawArguments);
/* 1027 */     return new AnnotatedParameterizedTypeImpl(rawType, merge(new Annotation[][] { type.getAnnotations(), annotations }, ), typeParameters);
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
/*      */   public static <T extends AnnotatedType> T toCanonical(T type) {
/* 1039 */     return toCanonical(type, Function.identity());
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
/*      */   public static <T extends AnnotatedType> T toCanonicalBoxed(T type) {
/* 1051 */     return toCanonical(type, GenericTypeReflector::box);
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
/*      */   private static <T extends AnnotatedType> T toCanonical(T type, final Function<Type, Type> leafTransformer) {
/* 1071 */     return (T)transform((AnnotatedType)type, new TypeVisitor()
/*      */         {
/*      */           protected AnnotatedType visitClass(AnnotatedType type) {
/* 1074 */             Annotation[] annotations = type.getAnnotations();
/* 1075 */             Class<?> raw = (Class)type.getType();
/* 1076 */             annotations = GenericTypeReflector.merge(new Annotation[][] { annotations, raw.getAnnotations() });
/* 1077 */             return new AnnotatedTypeImpl(leafTransformer.apply(type.getType()), annotations);
/*      */           }
/*      */ 
/*      */           
/*      */           protected AnnotatedType visitArray(AnnotatedArrayType type) {
/* 1082 */             return new AnnotatedArrayTypeImpl(leafTransformer.apply(type.getType()), type.getAnnotations(), 
/* 1083 */                 GenericTypeReflector.transform(type.getAnnotatedGenericComponentType(), this));
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           protected AnnotatedType visitParameterizedType(AnnotatedParameterizedType type) {
/* 1090 */             AnnotatedType[] params = (AnnotatedType[])Arrays.<AnnotatedType>stream(type.getAnnotatedActualTypeArguments()).map(param -> GenericTypeReflector.transform(param, this)).toArray(x$0 -> new AnnotatedType[x$0]);
/*      */             
/* 1092 */             Class<?> raw = (Class)((ParameterizedType)type.getType()).getRawType();
/* 1093 */             return GenericTypeReflector.replaceParameters(type, raw.getAnnotations(), params);
/*      */           }
/*      */         });
/*      */   }
/*      */   
/*      */   private static AnnotatedType expandGenerics(AnnotatedType type) {
/* 1099 */     return transform(type, new TypeVisitor()
/*      */         {
/*      */           public AnnotatedType visitClass(AnnotatedType type) {
/* 1102 */             Class<?> clazz = (Class)type.getType();
/* 1103 */             if ((clazz.getTypeParameters()).length > 0) {
/* 1104 */               return GenericTypeReflector.expandClassGenerics(clazz);
/*      */             }
/* 1106 */             return type;
/*      */           }
/*      */         });
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
/*      */   public static AnnotatedType transform(AnnotatedType type, TypeVisitor visitor) {
/* 1120 */     if (type instanceof AnnotatedParameterizedType) {
/* 1121 */       return visitor.visitParameterizedType((AnnotatedParameterizedType)type);
/*      */     }
/* 1123 */     if (type instanceof AnnotatedWildcardType) {
/* 1124 */       return visitor.visitWildcardType((AnnotatedWildcardType)type);
/*      */     }
/* 1126 */     if (type instanceof AnnotatedTypeVariable) {
/* 1127 */       return visitor.visitVariable((AnnotatedTypeVariable)type);
/*      */     }
/* 1129 */     if (type instanceof AnnotatedArrayType) {
/* 1130 */       return visitor.visitArray((AnnotatedArrayType)type);
/*      */     }
/* 1132 */     if (type instanceof AnnotatedCaptureType) {
/* 1133 */       return visitor.visitCaptureType((AnnotatedCaptureType)type);
/*      */     }
/* 1135 */     if (type.getType() instanceof Class) {
/* 1136 */       return visitor.visitClass(type);
/*      */     }
/* 1138 */     return visitor.visitUnmatched(type);
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
/*      */   public static AnnotatedType reduceBounded(AnnotatedType type) {
/* 1150 */     AnnotatedType capture = capture(type);
/* 1151 */     return transform(capture, new TypeVisitor()
/*      */         {
/*      */           protected AnnotatedType visitVariable(AnnotatedTypeVariable type) {
/* 1154 */             return GenericTypeReflector.updateAnnotations(GenericTypeReflector.transform(type.getAnnotatedBounds()[0], this), type.getAnnotations());
/*      */           }
/*      */ 
/*      */           
/*      */           protected AnnotatedType visitWildcardType(AnnotatedWildcardType type) {
/* 1159 */             return ((type.getAnnotatedLowerBounds()).length > 0) ? 
/* 1160 */               GenericTypeReflector.<AnnotatedType>updateAnnotations(GenericTypeReflector.transform(type.getAnnotatedLowerBounds()[0], this), type.getAnnotations()) : 
/* 1161 */               GenericTypeReflector.<AnnotatedType>updateAnnotations(GenericTypeReflector.transform(type.getAnnotatedUpperBounds()[0], this), type.getAnnotations());
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           protected AnnotatedType visitCaptureType(AnnotatedCaptureType type) {
/* 1168 */             AnnotatedType bound = ((type.getAnnotatedLowerBounds()).length > 0) ? type.getAnnotatedLowerBounds()[0] : type.getAnnotatedUpperBounds()[0];
/*      */             
/* 1170 */             if (bound instanceof AnnotatedParameterizedType) {
/* 1171 */               AnnotatedType[] typeArguments = ((AnnotatedParameterizedType)bound).getAnnotatedActualTypeArguments();
/* 1172 */               for (AnnotatedType typeArgument : typeArguments) {
/* 1173 */                 if (type.equals(typeArgument)) {
/*      */                   
/* 1175 */                   ParameterizedType parameterizedType = (ParameterizedType)bound.getType();
/* 1176 */                   return GenericTypeReflector.annotate(parameterizedType.getRawType(), GenericTypeReflector.merge(new Annotation[][] { type.getAnnotations(), bound.getAnnotations() }));
/*      */                 } 
/*      */               } 
/*      */             } 
/*      */             
/* 1181 */             return GenericTypeReflector.updateAnnotations(GenericTypeReflector.transform(bound, this), type.getAnnotations());
/*      */           }
/*      */         });
/*      */   }
/*      */   
/*      */   private static AnnotatedParameterizedType expandClassGenerics(Class<?> type) {
/* 1187 */     ParameterizedType inner = new ParameterizedTypeImpl(type, (Type[])type.getTypeParameters(), type.getDeclaringClass());
/* 1188 */     AnnotatedType[] params = mapArray((Object[])type.getTypeParameters(), x$0 -> new AnnotatedType[x$0], GenericTypeReflector::annotate);
/* 1189 */     return new AnnotatedParameterizedTypeImpl(inner, type.getAnnotations(), params);
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
/*      */   public static Annotation[] merge(Annotation[]... annotations) {
/* 1201 */     Set<Annotation> result = new LinkedHashSet<>();
/* 1202 */     for (Annotation[] annos : annotations) {
/* 1203 */       for (Annotation anno : annos) {
/* 1204 */         result.add(anno);
/*      */       }
/*      */     } 
/* 1207 */     return result.<Annotation>toArray(new Annotation[0]);
/*      */   }
/*      */   
/*      */   static boolean typeArraysEqual(AnnotatedType[] t1, AnnotatedType[] t2) {
/* 1211 */     if (t1 == t2) return true; 
/* 1212 */     if (t1 == null) return false; 
/* 1213 */     if (t2 == null) return false; 
/* 1214 */     if (t1.length != t2.length) return false;
/*      */     
/* 1216 */     for (int i = 0; i < t1.length; i++) {
/* 1217 */       if (!t1[i].getType().equals(t2[i].getType()) || !Arrays.equals((Object[])t1[i].getAnnotations(), (Object[])t2[i].getAnnotations())) {
/* 1218 */         return false;
/*      */       }
/*      */     } 
/* 1221 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static int hashCode(AnnotatedType... types) {
/* 1227 */     int typeHash = Arrays.<AnnotatedType>stream(types).mapToInt(t -> t.getType().hashCode()).reduce(0, (x, y) -> 127 * x ^ y);
/* 1228 */     int annotationHash = hashCode(Arrays.<AnnotatedType>stream(types)
/* 1229 */         .flatMap(t -> Arrays.stream(t.getAnnotations())));
/* 1230 */     return 31 * typeHash ^ annotationHash;
/*      */   }
/*      */   
/*      */   static int hashCode(Stream<Annotation> annotations) {
/* 1234 */     return annotations
/* 1235 */       .mapToInt(a -> 31 * a.annotationType().hashCode() ^ a.hashCode())
/* 1236 */       .reduce(0, (x, y) -> 127 * x ^ y);
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
/*      */   public static boolean equals(AnnotatedType t1, AnnotatedType t2) {
/* 1248 */     Objects.requireNonNull(t1);
/* 1249 */     Objects.requireNonNull(t2);
/* 1250 */     t1 = toCanonical(t1);
/* 1251 */     t2 = toCanonical(t2);
/*      */     
/* 1253 */     return t1.equals(t2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void buildUpperBoundClassAndInterfaces(Type type, Set<Class<?>> result) {
/* 1260 */     if (type instanceof ParameterizedType || type instanceof Class) {
/* 1261 */       result.add(erase(type));
/*      */       
/*      */       return;
/*      */     } 
/* 1265 */     for (AnnotatedType superType : getExactDirectSuperTypes(annotate(type))) {
/* 1266 */       buildUpperBoundClassAndInterfaces(superType.getType(), result);
/*      */     }
/*      */   }
/*      */   
/*      */   private static <I, O> O[] mapArray(I[] array, IntFunction<O[]> resultCtor, Function<I, O> mapper) {
/* 1271 */     O[] result = resultCtor.apply(array.length);
/* 1272 */     for (int i = 0; i < array.length; i++) {
/* 1273 */       result[i] = mapper.apply(array[i]);
/*      */     }
/* 1275 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class CaptureCacheKey
/*      */   {
/*      */     CaptureType capture;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     CaptureCacheKey(CaptureType capture) {
/* 1289 */       this.capture = capture;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 1294 */       return 127 * this.capture.getWildcardType().hashCode() ^ this.capture.getTypeVariable().hashCode();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 1299 */       if (this == obj) return true; 
/* 1300 */       if (!(obj instanceof CaptureCacheKey)) return false;
/*      */       
/* 1302 */       CaptureType that = ((CaptureCacheKey)obj).capture;
/* 1303 */       return (this.capture == that || (this.capture
/* 1304 */         .getWildcardType().equals(that.getWildcardType()) && this.capture
/* 1305 */         .getTypeVariable().equals(that.getTypeVariable()) && 
/* 1306 */         Arrays.equals((Object[])this.capture.getUpperBounds(), (Object[])that.getUpperBounds())));
/*      */     }
/*      */   }
/*      */   
/*      */   private static class AnnotatedCaptureCacheKey {
/*      */     AnnotatedCaptureType capture;
/*      */     CaptureType raw;
/*      */     
/*      */     AnnotatedCaptureCacheKey(AnnotatedCaptureType capture) {
/* 1315 */       this.capture = capture;
/* 1316 */       this.raw = (CaptureType)capture.getType();
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 1321 */       return 127 * this.raw.getWildcardType().hashCode() ^ this.raw.getTypeVariable().hashCode() ^ GenericTypeReflector.hashCode(Arrays.stream(this.capture.getAnnotations()));
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 1326 */       if (this == obj) return true; 
/* 1327 */       if (!(obj instanceof AnnotatedCaptureCacheKey)) return false;
/*      */       
/* 1329 */       AnnotatedCaptureCacheKey that = (AnnotatedCaptureCacheKey)obj;
/* 1330 */       return (this.capture == that.capture || ((new GenericTypeReflector.CaptureCacheKey(this.raw))
/* 1331 */         .equals(new GenericTypeReflector.CaptureCacheKey(that.raw)) && 
/* 1332 */         Arrays.equals((Object[])this.capture.getAnnotations(), (Object[])that.capture.getAnnotations())));
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\geantyref\GenericTypeReflector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */