/*     */ package ac.grim.grimac.shaded.snakeyaml;
/*     */ 
/*     */ import ac.grim.grimac.shaded.snakeyaml.error.YAMLException;
/*     */ import ac.grim.grimac.shaded.snakeyaml.internal.Logger;
/*     */ import ac.grim.grimac.shaded.snakeyaml.introspector.BeanAccess;
/*     */ import ac.grim.grimac.shaded.snakeyaml.introspector.Property;
/*     */ import ac.grim.grimac.shaded.snakeyaml.introspector.PropertySubstitute;
/*     */ import ac.grim.grimac.shaded.snakeyaml.introspector.PropertyUtils;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.Node;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.Tag;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class TypeDescription
/*     */ {
/*  43 */   private static final Logger log = Logger.getLogger(TypeDescription.class.getPackage().getName());
/*     */   
/*     */   private final Class<? extends Object> type;
/*     */   
/*     */   private Class<?> impl;
/*     */   
/*     */   private final Tag tag;
/*     */   
/*     */   private transient Set<Property> dumpProperties;
/*     */   
/*     */   private transient PropertyUtils propertyUtils;
/*     */   
/*     */   private transient boolean delegatesChecked;
/*     */   
/*  57 */   private Map<String, PropertySubstitute> properties = Collections.emptyMap();
/*     */   
/*  59 */   protected Set<String> excludes = Collections.emptySet();
/*  60 */   protected String[] includes = null;
/*     */   protected BeanAccess beanAccess;
/*     */   
/*     */   public TypeDescription(Class<? extends Object> clazz, Tag tag) {
/*  64 */     this(clazz, tag, null);
/*     */   }
/*     */   
/*     */   public TypeDescription(Class<? extends Object> clazz, Tag tag, Class<?> impl) {
/*  68 */     this.type = clazz;
/*  69 */     this.tag = tag;
/*  70 */     this.impl = impl;
/*  71 */     this.beanAccess = null;
/*     */   }
/*     */   
/*     */   public TypeDescription(Class<? extends Object> clazz, String tag) {
/*  75 */     this(clazz, new Tag(tag), null);
/*     */   }
/*     */   
/*     */   public TypeDescription(Class<? extends Object> clazz) {
/*  79 */     this(clazz, new Tag(clazz), null);
/*     */   }
/*     */   
/*     */   public TypeDescription(Class<? extends Object> clazz, Class<?> impl) {
/*  83 */     this(clazz, new Tag(clazz), impl);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Tag getTag() {
/*  93 */     return this.tag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<? extends Object> getType() {
/* 102 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void putListPropertyType(String property, Class<? extends Object> type) {
/* 113 */     addPropertyParameters(property, new Class[] { type });
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
/*     */   @Deprecated
/*     */   public void putMapPropertyType(String property, Class<? extends Object> key, Class<? extends Object> value) {
/* 126 */     addPropertyParameters(property, new Class[] { key, value });
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
/*     */   public void addPropertyParameters(String pName, Class<?>... classes) {
/* 138 */     if (!this.properties.containsKey(pName)) {
/* 139 */       substituteProperty(pName, null, null, null, classes);
/*     */     } else {
/* 141 */       PropertySubstitute pr = this.properties.get(pName);
/* 142 */       pr.setActualTypeArguments(classes);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 149 */     return "TypeDescription for " + getType() + " (tag='" + getTag() + "')";
/*     */   }
/*     */   
/*     */   private void checkDelegates() {
/* 153 */     Collection<PropertySubstitute> values = this.properties.values();
/* 154 */     for (PropertySubstitute p : values) {
/*     */       try {
/* 156 */         p.setDelegate(discoverProperty(p.getName()));
/* 157 */       } catch (YAMLException yAMLException) {}
/*     */     } 
/*     */     
/* 160 */     this.delegatesChecked = true;
/*     */   }
/*     */   
/*     */   private Property discoverProperty(String name) {
/* 164 */     if (this.propertyUtils != null) {
/* 165 */       if (this.beanAccess == null) {
/* 166 */         return this.propertyUtils.getProperty(this.type, name);
/*     */       }
/* 168 */       return this.propertyUtils.getProperty(this.type, name, this.beanAccess);
/*     */     } 
/* 170 */     return null;
/*     */   }
/*     */   
/*     */   public Property getProperty(String name) {
/* 174 */     if (!this.delegatesChecked) {
/* 175 */       checkDelegates();
/*     */     }
/* 177 */     return this.properties.containsKey(name) ? (Property)this.properties.get(name) : discoverProperty(name);
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
/*     */   public void substituteProperty(String pName, Class<?> pType, String getter, String setter, Class<?>... argParams) {
/* 191 */     substituteProperty(new PropertySubstitute(pName, pType, getter, setter, argParams));
/*     */   }
/*     */   
/*     */   public void substituteProperty(PropertySubstitute substitute) {
/* 195 */     if (Collections.EMPTY_MAP == this.properties) {
/* 196 */       this.properties = new LinkedHashMap<>();
/*     */     }
/* 198 */     substitute.setTargetType(this.type);
/* 199 */     this.properties.put(substitute.getName(), substitute);
/*     */   }
/*     */   
/*     */   public void setPropertyUtils(PropertyUtils propertyUtils) {
/* 203 */     this.propertyUtils = propertyUtils;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setIncludes(String... propNames) {
/* 208 */     this.includes = (propNames != null && propNames.length > 0) ? propNames : null;
/*     */   }
/*     */   
/*     */   public void setExcludes(String... propNames) {
/* 212 */     if (propNames != null && propNames.length > 0) {
/* 213 */       this.excludes = new HashSet<>();
/* 214 */       Collections.addAll(this.excludes, propNames);
/*     */     } else {
/* 216 */       this.excludes = Collections.emptySet();
/*     */     } 
/*     */   }
/*     */   
/*     */   public Set<Property> getProperties() {
/* 221 */     if (this.dumpProperties != null) {
/* 222 */       return this.dumpProperties;
/*     */     }
/*     */     
/* 225 */     if (this.propertyUtils != null) {
/* 226 */       if (this.includes != null) {
/* 227 */         this.dumpProperties = new LinkedHashSet<>();
/* 228 */         for (String propertyName : this.includes) {
/* 229 */           if (!this.excludes.contains(propertyName)) {
/* 230 */             this.dumpProperties.add(getProperty(propertyName));
/*     */           }
/*     */         } 
/* 233 */         return this.dumpProperties;
/*     */       } 
/*     */ 
/*     */       
/* 237 */       Set<Property> readableProps = (this.beanAccess == null) ? this.propertyUtils.getProperties(this.type) : this.propertyUtils.getProperties(this.type, this.beanAccess);
/*     */       
/* 239 */       if (this.properties.isEmpty()) {
/* 240 */         if (this.excludes.isEmpty()) {
/* 241 */           return this.dumpProperties = readableProps;
/*     */         }
/* 243 */         this.dumpProperties = new LinkedHashSet<>();
/* 244 */         for (Property property : readableProps) {
/* 245 */           if (!this.excludes.contains(property.getName())) {
/* 246 */             this.dumpProperties.add(property);
/*     */           }
/*     */         } 
/* 249 */         return this.dumpProperties;
/*     */       } 
/*     */       
/* 252 */       if (!this.delegatesChecked) {
/* 253 */         checkDelegates();
/*     */       }
/*     */       
/* 256 */       this.dumpProperties = new LinkedHashSet<>();
/*     */       
/* 258 */       for (Property property : this.properties.values()) {
/* 259 */         if (!this.excludes.contains(property.getName()) && property.isReadable()) {
/* 260 */           this.dumpProperties.add(property);
/*     */         }
/*     */       } 
/*     */       
/* 264 */       for (Property property : readableProps) {
/* 265 */         if (!this.excludes.contains(property.getName())) {
/* 266 */           this.dumpProperties.add(property);
/*     */         }
/*     */       } 
/*     */       
/* 270 */       return this.dumpProperties;
/*     */     } 
/* 272 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean setupPropertyType(String key, Node valueNode) {
/* 280 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean setProperty(Object targetBean, String propertyName, Object value) throws Exception {
/* 285 */     return false;
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
/*     */   public Object newInstance(Node node) {
/* 298 */     if (this.impl != null) {
/*     */       try {
/* 300 */         Constructor<?> c = this.impl.getDeclaredConstructor(new Class[0]);
/* 301 */         c.setAccessible(true);
/* 302 */         return c.newInstance(new Object[0]);
/* 303 */       } catch (Exception e) {
/* 304 */         log.warn(e.getLocalizedMessage());
/* 305 */         this.impl = null;
/*     */       } 
/*     */     }
/* 308 */     return null;
/*     */   }
/*     */   
/*     */   public Object newInstance(String propertyName, Node node) {
/* 312 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object finalizeConstruction(Object obj) {
/* 322 */     return obj;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\TypeDescription.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */