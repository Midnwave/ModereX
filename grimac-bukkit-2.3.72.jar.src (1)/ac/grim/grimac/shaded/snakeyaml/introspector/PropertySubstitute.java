/*     */ package ac.grim.grimac.shaded.snakeyaml.introspector;
/*     */ 
/*     */ import ac.grim.grimac.shaded.snakeyaml.error.YAMLException;
/*     */ import ac.grim.grimac.shaded.snakeyaml.internal.Logger;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PropertySubstitute
/*     */   extends Property
/*     */ {
/*  36 */   private static final Logger log = Logger.getLogger(PropertySubstitute.class.getPackage().getName());
/*     */   
/*     */   protected Class<?> targetType;
/*     */   
/*     */   private final String readMethod;
/*     */   private final String writeMethod;
/*     */   private transient Method read;
/*     */   private transient Method write;
/*     */   private Field field;
/*     */   protected Class<?>[] parameters;
/*     */   private Property delegate;
/*     */   private boolean filler;
/*     */   
/*     */   public PropertySubstitute(String name, Class<?> type, String readMethod, String writeMethod, Class<?>... params) {
/*  50 */     super(name, type);
/*  51 */     this.readMethod = readMethod;
/*  52 */     this.writeMethod = writeMethod;
/*  53 */     setActualTypeArguments(params);
/*  54 */     this.filler = false;
/*     */   }
/*     */   
/*     */   public PropertySubstitute(String name, Class<?> type, Class<?>... params) {
/*  58 */     this(name, type, null, null, params);
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<?>[] getActualTypeArguments() {
/*  63 */     if (this.parameters == null && this.delegate != null) {
/*  64 */       return this.delegate.getActualTypeArguments();
/*     */     }
/*  66 */     return this.parameters;
/*     */   }
/*     */   
/*     */   public void setActualTypeArguments(Class<?>... args) {
/*  70 */     if (args != null && args.length > 0) {
/*  71 */       this.parameters = args;
/*     */     } else {
/*  73 */       this.parameters = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(Object object, Object value) throws Exception {
/*  79 */     if (this.write != null) {
/*  80 */       if (!this.filler) {
/*  81 */         this.write.invoke(object, new Object[] { value });
/*  82 */       } else if (value != null) {
/*  83 */         if (value instanceof Collection) {
/*  84 */           Collection<?> collection = (Collection)value;
/*  85 */           for (Object val : collection) {
/*  86 */             this.write.invoke(object, new Object[] { val });
/*     */           } 
/*  88 */         } else if (value instanceof Map) {
/*  89 */           Map<?, ?> map = (Map<?, ?>)value;
/*  90 */           for (Map.Entry<?, ?> entry : map.entrySet()) {
/*  91 */             this.write.invoke(object, new Object[] { entry.getKey(), entry.getValue() });
/*     */           } 
/*  93 */         } else if (value.getClass().isArray()) {
/*     */           
/*  95 */           int len = Array.getLength(value);
/*  96 */           for (int i = 0; i < len; i++) {
/*  97 */             this.write.invoke(object, new Object[] { Array.get(value, i) });
/*     */           } 
/*     */         } 
/*     */       } 
/* 101 */     } else if (this.field != null) {
/* 102 */       this.field.set(object, value);
/* 103 */     } else if (this.delegate != null) {
/* 104 */       this.delegate.set(object, value);
/*     */     } else {
/* 106 */       log.warn("No setter/delegate for '" + getName() + "' on object " + object);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object get(Object object) {
/*     */     try {
/* 114 */       if (this.read != null)
/* 115 */         return this.read.invoke(object, new Object[0]); 
/* 116 */       if (this.field != null) {
/* 117 */         return this.field.get(object);
/*     */       }
/* 119 */     } catch (Exception e) {
/* 120 */       throw new YAMLException("Unable to find getter for property '" + 
/* 121 */           getName() + "' on object " + object + ":" + e);
/*     */     } 
/*     */     
/* 124 */     if (this.delegate != null) {
/* 125 */       return this.delegate.get(object);
/*     */     }
/* 127 */     throw new YAMLException("No getter or delegate for property '" + 
/* 128 */         getName() + "' on object " + object);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Annotation> getAnnotations() {
/* 133 */     Annotation[] annotations = null;
/* 134 */     if (this.read != null) {
/* 135 */       annotations = this.read.getAnnotations();
/* 136 */     } else if (this.field != null) {
/* 137 */       annotations = this.field.getAnnotations();
/*     */     } 
/* 139 */     return (annotations != null) ? Arrays.<Annotation>asList(annotations) : this.delegate.getAnnotations();
/*     */   }
/*     */ 
/*     */   
/*     */   public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
/*     */     A annotation;
/* 145 */     if (this.read != null) {
/* 146 */       annotation = this.read.getAnnotation(annotationType);
/* 147 */     } else if (this.field != null) {
/* 148 */       annotation = this.field.getAnnotation(annotationType);
/*     */     } else {
/* 150 */       annotation = this.delegate.getAnnotation(annotationType);
/*     */     } 
/* 152 */     return annotation;
/*     */   }
/*     */   
/*     */   public void setTargetType(Class<?> targetType) {
/* 156 */     if (this.targetType != targetType) {
/* 157 */       this.targetType = targetType;
/*     */       
/* 159 */       String name = getName();
/* 160 */       for (Class<?> c = targetType; c != null; c = c.getSuperclass()) {
/* 161 */         for (Field f : c.getDeclaredFields()) {
/* 162 */           if (f.getName().equals(name)) {
/* 163 */             int modifiers = f.getModifiers();
/* 164 */             if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)) {
/* 165 */               f.setAccessible(true);
/* 166 */               this.field = f;
/*     */             } 
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       } 
/* 172 */       if (this.field == null && log.isLoggable(Logger.Level.WARNING)) {
/* 173 */         log.warn(String.format("Failed to find field for %s.%s", new Object[] { targetType.getName(), getName() }));
/*     */       }
/*     */ 
/*     */       
/* 177 */       if (this.readMethod != null) {
/* 178 */         this.read = discoverMethod(targetType, this.readMethod, new Class[0]);
/*     */       }
/* 180 */       if (this.writeMethod != null) {
/* 181 */         this.filler = false;
/* 182 */         this.write = discoverMethod(targetType, this.writeMethod, new Class[] { getType() });
/* 183 */         if (this.write == null && this.parameters != null) {
/* 184 */           this.filler = true;
/* 185 */           this.write = discoverMethod(targetType, this.writeMethod, this.parameters);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private Method discoverMethod(Class<?> type, String name, Class<?>... params) {
/* 192 */     for (Class<?> c = type; c != null; c = c.getSuperclass()) {
/* 193 */       for (Method method : c.getDeclaredMethods()) {
/* 194 */         if (name.equals(method.getName())) {
/* 195 */           Class<?>[] parameterTypes = method.getParameterTypes();
/* 196 */           if (parameterTypes.length == params.length) {
/*     */ 
/*     */             
/* 199 */             boolean found = true;
/* 200 */             for (int i = 0; i < parameterTypes.length; i++) {
/* 201 */               if (!parameterTypes[i].isAssignableFrom(params[i])) {
/* 202 */                 found = false;
/*     */               }
/*     */             } 
/* 205 */             if (found) {
/* 206 */               method.setAccessible(true);
/* 207 */               return method;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 212 */     }  if (log.isLoggable(Logger.Level.WARNING)) {
/* 213 */       log.warn(String.format("Failed to find [%s(%d args)] for %s.%s", new Object[] { name, Integer.valueOf(params.length), this.targetType
/* 214 */               .getName(), getName() }));
/*     */     }
/* 216 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 221 */     String n = super.getName();
/* 222 */     if (n != null) {
/* 223 */       return n;
/*     */     }
/* 225 */     return (this.delegate != null) ? this.delegate.getName() : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<?> getType() {
/* 230 */     Class<?> t = super.getType();
/* 231 */     if (t != null) {
/* 232 */       return t;
/*     */     }
/* 234 */     return (this.delegate != null) ? this.delegate.getType() : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReadable() {
/* 239 */     return (this.read != null || this.field != null || (this.delegate != null && this.delegate.isReadable()));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWritable() {
/* 244 */     return (this.write != null || this.field != null || (this.delegate != null && this.delegate.isWritable()));
/*     */   }
/*     */   
/*     */   public void setDelegate(Property delegate) {
/* 248 */     this.delegate = delegate;
/* 249 */     if (this.writeMethod != null && this.write == null && !this.filler) {
/* 250 */       this.filler = true;
/* 251 */       this.write = discoverMethod(this.targetType, this.writeMethod, getActualTypeArguments());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\introspector\PropertySubstitute.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */