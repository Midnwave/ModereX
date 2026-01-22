/*     */ package ac.grim.grimac.shaded.snakeyaml.introspector;
/*     */ 
/*     */ import ac.grim.grimac.shaded.snakeyaml.error.YAMLException;
/*     */ import ac.grim.grimac.shaded.snakeyaml.util.PlatformFeatureDetector;
/*     */ import java.beans.FeatureDescriptor;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
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
/*     */ public class PropertyUtils
/*     */ {
/*  34 */   private final Map<Class<?>, Map<String, Property>> propertiesCache = new HashMap<>();
/*     */   
/*  36 */   private final Map<Class<?>, Set<Property>> readableProperties = new HashMap<>();
/*     */   
/*  38 */   private BeanAccess beanAccess = BeanAccess.DEFAULT;
/*     */   private boolean allowReadOnlyProperties = false;
/*     */   private boolean skipMissingProperties = false;
/*     */   private final PlatformFeatureDetector platformFeatureDetector;
/*     */   private static final String TRANSIENT = "transient";
/*     */   
/*     */   public PropertyUtils() {
/*  45 */     this(new PlatformFeatureDetector());
/*     */   }
/*     */   
/*     */   PropertyUtils(PlatformFeatureDetector platformFeatureDetector) {
/*  49 */     this.platformFeatureDetector = platformFeatureDetector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  57 */     if (platformFeatureDetector.isRunningOnAndroid()) {
/*  58 */       this.beanAccess = BeanAccess.FIELD;
/*     */     }
/*     */   }
/*     */   
/*     */   protected Map<String, Property> getPropertiesMap(Class<?> type, BeanAccess bAccess) {
/*  63 */     if (this.propertiesCache.containsKey(type)) {
/*  64 */       return this.propertiesCache.get(type);
/*     */     }
/*     */     
/*  67 */     Map<String, Property> properties = new LinkedHashMap<>();
/*  68 */     boolean inaccessableFieldsExist = false;
/*  69 */     if (bAccess == BeanAccess.FIELD) {
/*  70 */       for (Class<?> c = type; c != null; c = c.getSuperclass()) {
/*  71 */         for (Field field : c.getDeclaredFields()) {
/*  72 */           int modifiers = field.getModifiers();
/*  73 */           if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers) && 
/*  74 */             !properties.containsKey(field.getName())) {
/*  75 */             properties.put(field.getName(), new FieldProperty(field));
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } else {
/*     */       try {
/*  81 */         for (PropertyDescriptor property : Introspector.getBeanInfo(type)
/*  82 */           .getPropertyDescriptors()) {
/*  83 */           Method readMethod = property.getReadMethod();
/*  84 */           if ((readMethod == null || !readMethod.getName().equals("getClass")) && 
/*  85 */             !isTransient(property)) {
/*  86 */             properties.put(property.getName(), new MethodProperty(property));
/*     */           }
/*     */         } 
/*  89 */       } catch (IntrospectionException e) {
/*  90 */         throw new YAMLException(e);
/*     */       } 
/*     */ 
/*     */       
/*  94 */       for (Class<?> c = type; c != null; c = c.getSuperclass()) {
/*  95 */         for (Field field : c.getDeclaredFields()) {
/*  96 */           int modifiers = field.getModifiers();
/*  97 */           if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)) {
/*  98 */             if (Modifier.isPublic(modifiers)) {
/*  99 */               properties.put(field.getName(), new FieldProperty(field));
/*     */             } else {
/* 101 */               inaccessableFieldsExist = true;
/*     */             } 
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 107 */     if (properties.isEmpty() && inaccessableFieldsExist) {
/* 108 */       throw new YAMLException("No JavaBean properties found in " + type.getName());
/*     */     }
/* 110 */     this.propertiesCache.put(type, properties);
/* 111 */     return properties;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isTransient(FeatureDescriptor fd) {
/* 117 */     return Boolean.TRUE.equals(fd.getValue("transient"));
/*     */   }
/*     */   
/*     */   public Set<Property> getProperties(Class<? extends Object> type) {
/* 121 */     return getProperties(type, this.beanAccess);
/*     */   }
/*     */   
/*     */   public Set<Property> getProperties(Class<? extends Object> type, BeanAccess bAccess) {
/* 125 */     if (this.readableProperties.containsKey(type)) {
/* 126 */       return this.readableProperties.get(type);
/*     */     }
/* 128 */     Set<Property> properties = createPropertySet(type, bAccess);
/* 129 */     this.readableProperties.put(type, properties);
/* 130 */     return properties;
/*     */   }
/*     */   
/*     */   protected Set<Property> createPropertySet(Class<? extends Object> type, BeanAccess bAccess) {
/* 134 */     Set<Property> properties = new TreeSet<>();
/* 135 */     Collection<Property> props = getPropertiesMap(type, bAccess).values();
/* 136 */     for (Property property : props) {
/* 137 */       if (property.isReadable() && (this.allowReadOnlyProperties || property.isWritable())) {
/* 138 */         properties.add(property);
/*     */       }
/*     */     } 
/* 141 */     return properties;
/*     */   }
/*     */   
/*     */   public Property getProperty(Class<? extends Object> type, String name) {
/* 145 */     return getProperty(type, name, this.beanAccess);
/*     */   }
/*     */   
/*     */   public Property getProperty(Class<? extends Object> type, String name, BeanAccess bAccess) {
/* 149 */     Map<String, Property> properties = getPropertiesMap(type, bAccess);
/* 150 */     Property property = properties.get(name);
/* 151 */     if (property == null && this.skipMissingProperties) {
/* 152 */       property = new MissingProperty(name);
/*     */     }
/* 154 */     if (property == null) {
/* 155 */       throw new YAMLException("Unable to find property '" + name + "' on class: " + type.getName());
/*     */     }
/* 157 */     return property;
/*     */   }
/*     */   
/*     */   public void setBeanAccess(BeanAccess beanAccess) {
/* 161 */     if (this.platformFeatureDetector.isRunningOnAndroid() && beanAccess != BeanAccess.FIELD) {
/* 162 */       throw new IllegalArgumentException("JVM is Android - only BeanAccess.FIELD is available");
/*     */     }
/*     */     
/* 165 */     if (this.beanAccess != beanAccess) {
/* 166 */       this.beanAccess = beanAccess;
/* 167 */       this.propertiesCache.clear();
/* 168 */       this.readableProperties.clear();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setAllowReadOnlyProperties(boolean allowReadOnlyProperties) {
/* 173 */     if (this.allowReadOnlyProperties != allowReadOnlyProperties) {
/* 174 */       this.allowReadOnlyProperties = allowReadOnlyProperties;
/* 175 */       this.readableProperties.clear();
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isAllowReadOnlyProperties() {
/* 180 */     return this.allowReadOnlyProperties;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSkipMissingProperties(boolean skipMissingProperties) {
/* 190 */     if (this.skipMissingProperties != skipMissingProperties) {
/* 191 */       this.skipMissingProperties = skipMissingProperties;
/* 192 */       this.readableProperties.clear();
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isSkipMissingProperties() {
/* 197 */     return this.skipMissingProperties;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\introspector\PropertyUtils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */