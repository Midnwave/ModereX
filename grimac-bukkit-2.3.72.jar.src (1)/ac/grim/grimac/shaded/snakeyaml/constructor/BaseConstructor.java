/*     */ package ac.grim.grimac.shaded.snakeyaml.constructor;
/*     */ 
/*     */ import ac.grim.grimac.shaded.snakeyaml.LoaderOptions;
/*     */ import ac.grim.grimac.shaded.snakeyaml.TypeDescription;
/*     */ import ac.grim.grimac.shaded.snakeyaml.composer.Composer;
/*     */ import ac.grim.grimac.shaded.snakeyaml.error.YAMLException;
/*     */ import ac.grim.grimac.shaded.snakeyaml.introspector.PropertyUtils;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.CollectionNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.MappingNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.Node;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.NodeId;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.NodeTuple;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.ScalarNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.SequenceNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.Tag;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeMap;
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
/*     */ public abstract class BaseConstructor
/*     */ {
/*  56 */   protected static final Object NOT_INSTANTIATED_OBJECT = new Object();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  62 */   protected final Map<NodeId, Construct> yamlClassConstructors = new EnumMap<>(NodeId.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  70 */   protected final Map<Tag, Construct> yamlConstructors = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  75 */   protected final Map<String, Construct> yamlMultiConstructors = new HashMap<>();
/*     */ 
/*     */   
/*     */   protected Composer composer;
/*     */ 
/*     */   
/*     */   final Map<Node, Object> constructedObjects;
/*     */ 
/*     */   
/*     */   private final Set<Node> recursiveObjects;
/*     */ 
/*     */   
/*     */   private final ArrayList<RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>>> maps2fill;
/*     */ 
/*     */   
/*     */   private final ArrayList<RecursiveTuple<Set<Object>, Object>> sets2fill;
/*     */ 
/*     */   
/*     */   protected Tag rootTag;
/*     */ 
/*     */   
/*     */   private PropertyUtils propertyUtils;
/*     */ 
/*     */   
/*     */   private boolean explicitPropertyUtils;
/*     */ 
/*     */   
/*     */   private boolean allowDuplicateKeys = true;
/*     */ 
/*     */   
/*     */   private boolean wrappedToRootException = false;
/*     */   
/*     */   private boolean enumCaseSensitive = false;
/*     */   
/*     */   protected final Map<Class<? extends Object>, TypeDescription> typeDefinitions;
/*     */   
/*     */   protected final Map<Tag, Class<? extends Object>> typeTags;
/*     */   
/*     */   protected LoaderOptions loadingConfig;
/*     */ 
/*     */   
/*     */   public BaseConstructor(LoaderOptions loadingConfig) {
/* 117 */     if (loadingConfig == null) {
/* 118 */       throw new NullPointerException("LoaderOptions must be provided.");
/*     */     }
/* 120 */     this.constructedObjects = new HashMap<>();
/* 121 */     this.recursiveObjects = new HashSet<>();
/* 122 */     this.maps2fill = new ArrayList<>();
/*     */     
/* 124 */     this.sets2fill = new ArrayList<>();
/* 125 */     this.typeDefinitions = new HashMap<>();
/* 126 */     this.typeTags = new HashMap<>();
/*     */     
/* 128 */     this.rootTag = null;
/* 129 */     this.explicitPropertyUtils = false;
/*     */     
/* 131 */     this.typeDefinitions.put(SortedMap.class, new TypeDescription(SortedMap.class, Tag.OMAP, TreeMap.class));
/*     */     
/* 133 */     this.typeDefinitions.put(SortedSet.class, new TypeDescription(SortedSet.class, Tag.SET, TreeSet.class));
/*     */     
/* 135 */     this.loadingConfig = loadingConfig;
/*     */   }
/*     */   
/*     */   public void setComposer(Composer composer) {
/* 139 */     this.composer = composer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean checkData() {
/* 149 */     return this.composer.checkNode();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getData() throws NoSuchElementException {
/* 159 */     if (!this.composer.checkNode()) {
/* 160 */       throw new NoSuchElementException("No document is available.");
/*     */     }
/* 162 */     Node node = this.composer.getNode();
/* 163 */     if (this.rootTag != null) {
/* 164 */       node.setTag(this.rootTag);
/*     */     }
/* 166 */     return constructDocument(node);
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
/*     */   public Object getSingleData(Class<?> type) {
/* 178 */     Node node = this.composer.getSingleNode();
/* 179 */     if (node != null && !Tag.NULL.equals(node.getTag())) {
/* 180 */       if (Object.class != type) {
/* 181 */         node.setTag(new Tag(type));
/* 182 */       } else if (this.rootTag != null) {
/* 183 */         node.setTag(this.rootTag);
/*     */       } 
/* 185 */       return constructDocument(node);
/*     */     } 
/* 187 */     Construct construct = this.yamlConstructors.get(Tag.NULL);
/* 188 */     return construct.construct(node);
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
/*     */   protected final Object constructDocument(Node node) {
/*     */     try {
/* 201 */       Object data = constructObject(node);
/* 202 */       fillRecursive();
/* 203 */       return data;
/* 204 */     } catch (RuntimeException e) {
/* 205 */       if (this.wrappedToRootException && !(e instanceof YAMLException)) {
/* 206 */         throw new YAMLException(e);
/*     */       }
/* 208 */       throw e;
/*     */     }
/*     */     finally {
/*     */       
/* 212 */       this.constructedObjects.clear();
/* 213 */       this.recursiveObjects.clear();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void fillRecursive() {
/* 221 */     if (!this.maps2fill.isEmpty()) {
/* 222 */       for (RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>> entry : this.maps2fill) {
/* 223 */         RecursiveTuple<Object, Object> key_value = entry._2();
/* 224 */         ((Map)entry._1()).put(key_value._1(), key_value._2());
/*     */       } 
/* 226 */       this.maps2fill.clear();
/*     */     } 
/* 228 */     if (!this.sets2fill.isEmpty()) {
/* 229 */       for (RecursiveTuple<Set<Object>, Object> value : this.sets2fill) {
/* 230 */         ((Set)value._1()).add(value._2());
/*     */       }
/* 232 */       this.sets2fill.clear();
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
/*     */   protected Object constructObject(Node node) {
/* 244 */     if (this.constructedObjects.containsKey(node)) {
/* 245 */       return this.constructedObjects.get(node);
/*     */     }
/* 247 */     return constructObjectNoCheck(node);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object constructObjectNoCheck(Node node) {
/* 257 */     if (this.recursiveObjects.contains(node)) {
/* 258 */       throw new ConstructorException(null, null, "found unconstructable recursive node", node
/* 259 */           .getStartMark());
/*     */     }
/* 261 */     this.recursiveObjects.add(node);
/* 262 */     Construct constructor = getConstructor(node);
/*     */     
/* 264 */     Object data = this.constructedObjects.containsKey(node) ? this.constructedObjects.get(node) : constructor.construct(node);
/*     */     
/* 266 */     finalizeConstruction(node, data);
/* 267 */     this.constructedObjects.put(node, data);
/* 268 */     this.recursiveObjects.remove(node);
/* 269 */     if (node.isTwoStepsConstruction()) {
/* 270 */       constructor.construct2ndStep(node, data);
/*     */     }
/* 272 */     return data;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Construct getConstructor(Node node) {
/* 283 */     if (node.useClassConstructor()) {
/* 284 */       return this.yamlClassConstructors.get(node.getNodeId());
/*     */     }
/* 286 */     Tag tag = node.getTag();
/* 287 */     Construct constructor = this.yamlConstructors.get(tag);
/* 288 */     if (constructor == null) {
/* 289 */       for (String prefix : this.yamlMultiConstructors.keySet()) {
/* 290 */         if (tag.startsWith(prefix)) {
/* 291 */           return this.yamlMultiConstructors.get(prefix);
/*     */         }
/*     */       } 
/* 294 */       return this.yamlConstructors.get(null);
/*     */     } 
/* 296 */     return constructor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String constructScalar(ScalarNode node) {
/* 307 */     return node.getValue();
/*     */   }
/*     */ 
/*     */   
/*     */   protected List<Object> createDefaultList(int initSize) {
/* 312 */     return new ArrayList(initSize);
/*     */   }
/*     */   
/*     */   protected Set<Object> createDefaultSet(int initSize) {
/* 316 */     return new LinkedHashSet(initSize);
/*     */   }
/*     */ 
/*     */   
/*     */   protected Map<Object, Object> createDefaultMap(int initSize) {
/* 321 */     return new LinkedHashMap<>(initSize);
/*     */   }
/*     */   
/*     */   protected Object createArray(Class<?> type, int size) {
/* 325 */     return Array.newInstance(type.getComponentType(), size);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object finalizeConstruction(Node node, Object data) {
/* 331 */     Class<? extends Object> type = node.getType();
/* 332 */     if (this.typeDefinitions.containsKey(type)) {
/* 333 */       return ((TypeDescription)this.typeDefinitions.get(type)).finalizeConstruction(data);
/*     */     }
/* 335 */     return data;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Object newInstance(Node node) {
/* 340 */     return newInstance(Object.class, node);
/*     */   }
/*     */   
/*     */   protected final Object newInstance(Class<?> ancestor, Node node) {
/* 344 */     return newInstance(ancestor, node, true);
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
/*     */   protected Object newInstance(Class<?> ancestor, Node node, boolean tryDefault) {
/*     */     try {
/* 362 */       Class<? extends Object> type = node.getType();
/* 363 */       if (this.typeDefinitions.containsKey(type)) {
/* 364 */         TypeDescription td = this.typeDefinitions.get(type);
/* 365 */         Object instance = td.newInstance(node);
/* 366 */         if (instance != null) {
/* 367 */           return instance;
/*     */         }
/*     */       } 
/*     */       
/* 371 */       if (tryDefault)
/*     */       {
/*     */ 
/*     */         
/* 375 */         if (ancestor.isAssignableFrom(type) && !Modifier.isAbstract(type.getModifiers())) {
/* 376 */           Constructor<?> c = type.getDeclaredConstructor(new Class[0]);
/* 377 */           c.setAccessible(true);
/* 378 */           return c.newInstance(new Object[0]);
/*     */         } 
/*     */       }
/* 381 */     } catch (Exception e) {
/* 382 */       throw new YAMLException(e);
/*     */     } 
/*     */     
/* 385 */     return NOT_INSTANTIATED_OBJECT;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Set<Object> newSet(CollectionNode<?> node) {
/* 390 */     Object instance = newInstance(Set.class, (Node)node);
/* 391 */     if (instance != NOT_INSTANTIATED_OBJECT) {
/* 392 */       return (Set<Object>)instance;
/*     */     }
/* 394 */     return createDefaultSet(node.getValue().size());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected List<Object> newList(SequenceNode node) {
/* 400 */     Object instance = newInstance(List.class, (Node)node);
/* 401 */     if (instance != NOT_INSTANTIATED_OBJECT) {
/* 402 */       return (List<Object>)instance;
/*     */     }
/* 404 */     return createDefaultList(node.getValue().size());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Map<Object, Object> newMap(MappingNode node) {
/* 410 */     Object instance = newInstance(Map.class, (Node)node);
/* 411 */     if (instance != NOT_INSTANTIATED_OBJECT) {
/* 412 */       return (Map<Object, Object>)instance;
/*     */     }
/* 414 */     return createDefaultMap(node.getValue().size());
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
/*     */   protected List<? extends Object> constructSequence(SequenceNode node) {
/* 429 */     List<Object> result = newList(node);
/* 430 */     constructSequenceStep2(node, result);
/* 431 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Set<? extends Object> constructSet(SequenceNode node) {
/* 441 */     Set<Object> result = newSet((CollectionNode<?>)node);
/* 442 */     constructSequenceStep2(node, result);
/* 443 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object constructArray(SequenceNode node) {
/* 453 */     return constructArrayStep2(node, createArray(node.getType(), node.getValue().size()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void constructSequenceStep2(SequenceNode node, Collection<Object> collection) {
/* 463 */     for (Node child : node.getValue()) {
/* 464 */       collection.add(constructObject(child));
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
/*     */   protected Object constructArrayStep2(SequenceNode node, Object array) {
/* 476 */     Class<?> componentType = node.getType().getComponentType();
/*     */     
/* 478 */     int index = 0;
/* 479 */     for (Node child : node.getValue()) {
/*     */       
/* 481 */       if (child.getType() == Object.class) {
/* 482 */         child.setType(componentType);
/*     */       }
/*     */       
/* 485 */       Object value = constructObject(child);
/*     */       
/* 487 */       if (componentType.isPrimitive()) {
/*     */         
/* 489 */         if (value == null) {
/* 490 */           throw new NullPointerException("Unable to construct element value for " + child);
/*     */         }
/*     */ 
/*     */         
/* 494 */         if (byte.class.equals(componentType)) {
/* 495 */           Array.setByte(array, index, ((Number)value).byteValue());
/*     */         }
/* 497 */         else if (short.class.equals(componentType)) {
/* 498 */           Array.setShort(array, index, ((Number)value).shortValue());
/*     */         }
/* 500 */         else if (int.class.equals(componentType)) {
/* 501 */           Array.setInt(array, index, ((Number)value).intValue());
/*     */         }
/* 503 */         else if (long.class.equals(componentType)) {
/* 504 */           Array.setLong(array, index, ((Number)value).longValue());
/*     */         }
/* 506 */         else if (float.class.equals(componentType)) {
/* 507 */           Array.setFloat(array, index, ((Number)value).floatValue());
/*     */         }
/* 509 */         else if (double.class.equals(componentType)) {
/* 510 */           Array.setDouble(array, index, ((Number)value).doubleValue());
/*     */         }
/* 512 */         else if (char.class.equals(componentType)) {
/* 513 */           Array.setChar(array, index, ((Character)value).charValue());
/*     */         }
/* 515 */         else if (boolean.class.equals(componentType)) {
/* 516 */           Array.setBoolean(array, index, ((Boolean)value).booleanValue());
/*     */         } else {
/*     */           
/* 519 */           throw new YAMLException("unexpected primitive type");
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 524 */         Array.set(array, index, value);
/*     */       } 
/*     */       
/* 527 */       index++;
/*     */     } 
/* 529 */     return array;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Set<Object> constructSet(MappingNode node) {
/* 539 */     Set<Object> set = newSet((CollectionNode<?>)node);
/* 540 */     constructSet2ndStep(node, set);
/* 541 */     return set;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Map<Object, Object> constructMapping(MappingNode node) {
/* 551 */     Map<Object, Object> mapping = newMap(node);
/* 552 */     constructMapping2ndStep(node, mapping);
/* 553 */     return mapping;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void constructMapping2ndStep(MappingNode node, Map<Object, Object> mapping) {
/* 563 */     List<NodeTuple> nodeValue = node.getValue();
/* 564 */     for (NodeTuple tuple : nodeValue) {
/* 565 */       Node keyNode = tuple.getKeyNode();
/* 566 */       Node valueNode = tuple.getValueNode();
/* 567 */       Object key = constructObject(keyNode);
/* 568 */       if (key != null) {
/*     */         try {
/* 570 */           key.hashCode();
/* 571 */         } catch (Exception e) {
/* 572 */           throw new ConstructorException("while constructing a mapping", node.getStartMark(), "found unacceptable key " + key, tuple
/* 573 */               .getKeyNode().getStartMark(), e);
/*     */         } 
/*     */       }
/* 576 */       Object value = constructObject(valueNode);
/* 577 */       if (keyNode.isTwoStepsConstruction()) {
/* 578 */         if (this.loadingConfig.getAllowRecursiveKeys()) {
/* 579 */           postponeMapFilling(mapping, key, value); continue;
/*     */         } 
/* 581 */         throw new YAMLException("Recursive key for mapping is detected but it is not configured to be allowed.");
/*     */       } 
/*     */ 
/*     */       
/* 585 */       mapping.put(key, value);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void postponeMapFilling(Map<Object, Object> mapping, Object key, Object value) {
/* 596 */     this.maps2fill.add(0, new RecursiveTuple<>(mapping, new RecursiveTuple<>(key, value)));
/*     */   }
/*     */   
/*     */   protected void constructSet2ndStep(MappingNode node, Set<Object> set) {
/* 600 */     List<NodeTuple> nodeValue = node.getValue();
/* 601 */     for (NodeTuple tuple : nodeValue) {
/* 602 */       Node keyNode = tuple.getKeyNode();
/* 603 */       Object key = constructObject(keyNode);
/* 604 */       if (key != null) {
/*     */         try {
/* 606 */           key.hashCode();
/* 607 */         } catch (Exception e) {
/* 608 */           throw new ConstructorException("while constructing a Set", node.getStartMark(), "found unacceptable key " + key, tuple
/* 609 */               .getKeyNode().getStartMark(), e);
/*     */         } 
/*     */       }
/* 612 */       if (keyNode.isTwoStepsConstruction()) {
/* 613 */         postponeSetFilling(set, key); continue;
/*     */       } 
/* 615 */       set.add(key);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void postponeSetFilling(Set<Object> set, Object key) {
/* 626 */     this.sets2fill.add(0, new RecursiveTuple<>(set, key));
/*     */   }
/*     */   
/*     */   public void setPropertyUtils(PropertyUtils propertyUtils) {
/* 630 */     this.propertyUtils = propertyUtils;
/* 631 */     this.explicitPropertyUtils = true;
/* 632 */     Collection<TypeDescription> tds = this.typeDefinitions.values();
/* 633 */     for (TypeDescription typeDescription : tds) {
/* 634 */       typeDescription.setPropertyUtils(propertyUtils);
/*     */     }
/*     */   }
/*     */   
/*     */   public final PropertyUtils getPropertyUtils() {
/* 639 */     if (this.propertyUtils == null) {
/* 640 */       this.propertyUtils = new PropertyUtils();
/*     */     }
/* 642 */     return this.propertyUtils;
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
/*     */   public TypeDescription addTypeDescription(TypeDescription definition) {
/* 654 */     if (definition == null) {
/* 655 */       throw new NullPointerException("TypeDescription is required.");
/*     */     }
/* 657 */     Tag tag = definition.getTag();
/* 658 */     this.typeTags.put(tag, definition.getType());
/* 659 */     definition.setPropertyUtils(getPropertyUtils());
/* 660 */     return this.typeDefinitions.put(definition.getType(), definition);
/*     */   }
/*     */   
/*     */   private static class RecursiveTuple<T, K>
/*     */   {
/*     */     private final T _1;
/*     */     private final K _2;
/*     */     
/*     */     public RecursiveTuple(T _1, K _2) {
/* 669 */       this._1 = _1;
/* 670 */       this._2 = _2;
/*     */     }
/*     */     
/*     */     public K _2() {
/* 674 */       return this._2;
/*     */     }
/*     */     
/*     */     public T _1() {
/* 678 */       return this._1;
/*     */     }
/*     */   }
/*     */   
/*     */   public final boolean isExplicitPropertyUtils() {
/* 683 */     return this.explicitPropertyUtils;
/*     */   }
/*     */   
/*     */   public boolean isAllowDuplicateKeys() {
/* 687 */     return this.allowDuplicateKeys;
/*     */   }
/*     */   
/*     */   public void setAllowDuplicateKeys(boolean allowDuplicateKeys) {
/* 691 */     this.allowDuplicateKeys = allowDuplicateKeys;
/*     */   }
/*     */   
/*     */   public boolean isWrappedToRootException() {
/* 695 */     return this.wrappedToRootException;
/*     */   }
/*     */   
/*     */   public void setWrappedToRootException(boolean wrappedToRootException) {
/* 699 */     this.wrappedToRootException = wrappedToRootException;
/*     */   }
/*     */   
/*     */   public boolean isEnumCaseSensitive() {
/* 703 */     return this.enumCaseSensitive;
/*     */   }
/*     */   
/*     */   public void setEnumCaseSensitive(boolean enumCaseSensitive) {
/* 707 */     this.enumCaseSensitive = enumCaseSensitive;
/*     */   }
/*     */   
/*     */   public LoaderOptions getLoadingConfig() {
/* 711 */     return this.loadingConfig;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\constructor\BaseConstructor.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */