/*     */ package ac.grim.grimac.shaded.geantyref;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
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
/*     */ class AnnotationInvocationHandler
/*     */   implements Annotation, InvocationHandler, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 8615044376674805680L;
/*  43 */   private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<>();
/*     */   
/*     */   static {
/*  46 */     primitiveWrapperMap.put(boolean.class, Boolean.class);
/*  47 */     primitiveWrapperMap.put(byte.class, Byte.class);
/*  48 */     primitiveWrapperMap.put(char.class, Character.class);
/*  49 */     primitiveWrapperMap.put(short.class, Short.class);
/*  50 */     primitiveWrapperMap.put(int.class, Integer.class);
/*  51 */     primitiveWrapperMap.put(long.class, Long.class);
/*  52 */     primitiveWrapperMap.put(double.class, Double.class);
/*  53 */     primitiveWrapperMap.put(float.class, Float.class);
/*     */   }
/*     */   
/*     */   private final Class<? extends Annotation> annotationType;
/*     */   private final Map<String, Object> values;
/*     */   private final int hashCode;
/*     */   
/*     */   AnnotationInvocationHandler(Class<? extends Annotation> annotationType, Map<String, Object> values) throws AnnotationFormatException {
/*  61 */     Class<?>[] interfaces = annotationType.getInterfaces();
/*  62 */     if (annotationType.isAnnotation() && interfaces.length == 1 && interfaces[0] == Annotation.class) {
/*  63 */       this.annotationType = annotationType;
/*  64 */       this.values = Collections.unmodifiableMap(normalize(annotationType, values));
/*  65 */       this.hashCode = calculateHashCode();
/*     */     } else {
/*  67 */       throw new AnnotationFormatException(annotationType.getName() + " is not an annotation type");
/*     */     } 
/*     */   }
/*     */   
/*     */   static Map<String, Object> normalize(Class<? extends Annotation> annotationType, Map<String, Object> values) throws AnnotationFormatException {
/*  72 */     Set<String> missing = new HashSet<>();
/*  73 */     Set<String> invalid = new HashSet<>();
/*  74 */     Map<String, Object> valid = new HashMap<>();
/*  75 */     for (Method element : annotationType.getDeclaredMethods()) {
/*  76 */       String elementName = element.getName();
/*  77 */       if (values.containsKey(elementName)) {
/*  78 */         Class<?> returnType = element.getReturnType();
/*  79 */         if (returnType.isPrimitive()) {
/*  80 */           returnType = primitiveWrapperMap.get(returnType);
/*     */         }
/*     */         
/*  83 */         if (returnType.isInstance(values.get(elementName))) {
/*  84 */           valid.put(elementName, values.get(elementName));
/*     */         } else {
/*  86 */           invalid.add(elementName);
/*     */         }
/*     */       
/*  89 */       } else if (element.getDefaultValue() != null) {
/*  90 */         valid.put(elementName, element.getDefaultValue());
/*     */       } else {
/*  92 */         missing.add(elementName);
/*     */       } 
/*     */     } 
/*     */     
/*  96 */     if (!missing.isEmpty()) {
/*  97 */       throw new AnnotationFormatException("Missing value(s) for " + String.join(",", missing));
/*     */     }
/*  99 */     if (!invalid.isEmpty()) {
/* 100 */       throw new AnnotationFormatException("Incompatible type(s) provided for " + String.join(",", invalid));
/*     */     }
/* 102 */     return valid;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/* 107 */     if (this.values.containsKey(method.getName())) {
/* 108 */       return this.values.get(method.getName());
/*     */     }
/* 110 */     return method.invoke(this, args);
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<? extends Annotation> annotationType() {
/* 115 */     return this.annotationType;
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
/*     */   public boolean equals(Object other) {
/* 127 */     if (this == other) {
/* 128 */       return true;
/*     */     }
/* 130 */     if (other == null) {
/* 131 */       return false;
/*     */     }
/* 133 */     if (!this.annotationType.isInstance(other)) {
/* 134 */       return false;
/*     */     }
/*     */     
/* 137 */     Annotation that = this.annotationType.cast(other);
/*     */ 
/*     */     
/* 140 */     for (Map.Entry<String, Object> element : this.values.entrySet()) {
/* 141 */       Object otherValue, value = element.getValue();
/*     */       
/*     */       try {
/* 144 */         otherValue = that.annotationType().getMethod(element.getKey(), new Class[0]).invoke(that, new Object[0]);
/* 145 */       } catch (ReflectiveOperationException e) {
/* 146 */         throw new RuntimeException(e);
/*     */       } 
/*     */       
/* 149 */       if (!Objects.deepEquals(value, otherValue)) {
/* 150 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 154 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 165 */     return this.hashCode;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 170 */     StringBuilder result = new StringBuilder();
/* 171 */     result.append('@').append(this.annotationType.getName()).append('(');
/* 172 */     Set<String> sorted = new TreeSet<>(this.values.keySet());
/* 173 */     for (String elementName : sorted) {
/*     */       String value;
/* 175 */       if (this.values.get(elementName).getClass().isArray()) {
/*     */ 
/*     */         
/* 178 */         value = Arrays.deepToString(new Object[] { this.values.get(elementName) }).replaceAll("^\\[\\[", "[").replaceAll("]]$", "]");
/*     */       } else {
/* 180 */         value = this.values.get(elementName).toString();
/*     */       } 
/* 182 */       result.append(elementName).append('=').append(value).append(", ");
/*     */     } 
/*     */     
/* 185 */     if (this.values.size() > 0) {
/* 186 */       result.delete(result.length() - 2, result.length());
/*     */     }
/* 188 */     result.append(")");
/*     */     
/* 190 */     return result.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   private int calculateHashCode() {
/* 195 */     int hashCode = 0;
/*     */     
/* 197 */     for (Map.Entry<String, Object> element : this.values.entrySet()) {
/* 198 */       hashCode += 127 * ((String)element.getKey()).hashCode() ^ calculateHashCode(element.getValue());
/*     */     }
/*     */     
/* 201 */     return hashCode;
/*     */   }
/*     */   
/*     */   private int calculateHashCode(Object element) {
/* 205 */     if (!element.getClass().isArray()) {
/* 206 */       return element.hashCode();
/*     */     }
/* 208 */     if (element instanceof Object[]) {
/* 209 */       return Arrays.hashCode((Object[])element);
/*     */     }
/* 211 */     if (element instanceof byte[]) {
/* 212 */       return Arrays.hashCode((byte[])element);
/*     */     }
/* 214 */     if (element instanceof short[]) {
/* 215 */       return Arrays.hashCode((short[])element);
/*     */     }
/* 217 */     if (element instanceof int[]) {
/* 218 */       return Arrays.hashCode((int[])element);
/*     */     }
/* 220 */     if (element instanceof long[]) {
/* 221 */       return Arrays.hashCode((long[])element);
/*     */     }
/* 223 */     if (element instanceof char[]) {
/* 224 */       return Arrays.hashCode((char[])element);
/*     */     }
/* 226 */     if (element instanceof float[]) {
/* 227 */       return Arrays.hashCode((float[])element);
/*     */     }
/* 229 */     if (element instanceof double[]) {
/* 230 */       return Arrays.hashCode((double[])element);
/*     */     }
/* 232 */     if (element instanceof boolean[]) {
/* 233 */       return Arrays.hashCode((boolean[])element);
/*     */     }
/*     */     
/* 236 */     return Objects.hashCode(element);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\geantyref\AnnotationInvocationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */