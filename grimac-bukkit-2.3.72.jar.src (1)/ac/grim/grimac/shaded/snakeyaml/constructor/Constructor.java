/*     */ package ac.grim.grimac.shaded.snakeyaml.constructor;
/*     */ 
/*     */ import ac.grim.grimac.shaded.snakeyaml.LoaderOptions;
/*     */ import ac.grim.grimac.shaded.snakeyaml.TypeDescription;
/*     */ import ac.grim.grimac.shaded.snakeyaml.error.YAMLException;
/*     */ import ac.grim.grimac.shaded.snakeyaml.introspector.Property;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.CollectionNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.MappingNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.Node;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.NodeId;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.NodeTuple;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.ScalarNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.SequenceNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.Tag;
/*     */ import ac.grim.grimac.shaded.snakeyaml.util.EnumUtils;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
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
/*     */ public class Constructor
/*     */   extends SafeConstructor
/*     */ {
/*     */   public Constructor(LoaderOptions loadingConfig) {
/*  50 */     this(Object.class, loadingConfig);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Constructor(Class<? extends Object> theRoot, LoaderOptions loadingConfig) {
/*  60 */     this(new TypeDescription(checkRoot(theRoot)), (Collection<TypeDescription>)null, loadingConfig);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Class<? extends Object> checkRoot(Class<? extends Object> theRoot) {
/*  67 */     if (theRoot == null) {
/*  68 */       throw new NullPointerException("Root class must be provided.");
/*     */     }
/*  70 */     return theRoot;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Constructor(TypeDescription theRoot, LoaderOptions loadingConfig) {
/*  81 */     this(theRoot, (Collection<TypeDescription>)null, loadingConfig);
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
/*     */   public Constructor(TypeDescription theRoot, Collection<TypeDescription> moreTDs, LoaderOptions loadingConfig) {
/*  93 */     super(loadingConfig);
/*  94 */     if (theRoot == null) {
/*  95 */       throw new NullPointerException("Root type must be provided.");
/*     */     }
/*     */     
/*  98 */     this.yamlConstructors.put(null, new ConstructYamlObject());
/*     */ 
/*     */     
/* 101 */     if (!Object.class.equals(theRoot.getType())) {
/* 102 */       this.rootTag = new Tag(theRoot.getType());
/*     */     }
/* 104 */     this.yamlClassConstructors.put(NodeId.scalar, new ConstructScalar());
/* 105 */     this.yamlClassConstructors.put(NodeId.mapping, new ConstructMapping());
/* 106 */     this.yamlClassConstructors.put(NodeId.sequence, new ConstructSequence());
/* 107 */     addTypeDescription(theRoot);
/* 108 */     if (moreTDs != null) {
/* 109 */       for (TypeDescription td : moreTDs) {
/* 110 */         addTypeDescription(td);
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
/*     */   public Constructor(String theRoot, LoaderOptions loadingConfig) throws ClassNotFoundException {
/* 123 */     this((Class)Class.forName(check(theRoot)), loadingConfig);
/*     */   }
/*     */   
/*     */   private static String check(String s) {
/* 127 */     if (s == null) {
/* 128 */       throw new NullPointerException("Root type must be provided.");
/*     */     }
/* 130 */     if (s.trim().length() == 0) {
/* 131 */       throw new YAMLException("Root type must be provided.");
/*     */     }
/* 133 */     return s;
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
/*     */   protected class ConstructMapping
/*     */     implements Construct
/*     */   {
/*     */     public Object construct(Node node) {
/* 150 */       MappingNode mnode = (MappingNode)node;
/* 151 */       if (Map.class.isAssignableFrom(node.getType())) {
/* 152 */         if (node.isTwoStepsConstruction()) {
/* 153 */           return Constructor.this.newMap(mnode);
/*     */         }
/* 155 */         return Constructor.this.constructMapping(mnode);
/*     */       } 
/* 157 */       if (Collection.class.isAssignableFrom(node.getType())) {
/* 158 */         if (node.isTwoStepsConstruction()) {
/* 159 */           return Constructor.this.newSet((CollectionNode<?>)mnode);
/*     */         }
/* 161 */         return Constructor.this.constructSet(mnode);
/*     */       } 
/*     */       
/* 164 */       Object obj = Constructor.this.newInstance((Node)mnode);
/* 165 */       if (obj != BaseConstructor.NOT_INSTANTIATED_OBJECT) {
/* 166 */         if (node.isTwoStepsConstruction()) {
/* 167 */           return obj;
/*     */         }
/* 169 */         return constructJavaBean2ndStep(mnode, obj);
/*     */       } 
/*     */       
/* 172 */       throw new ConstructorException(null, null, "Can't create an instance for " + mnode
/* 173 */           .getTag(), node.getStartMark());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void construct2ndStep(Node node, Object object) {
/* 180 */       if (Map.class.isAssignableFrom(node.getType())) {
/* 181 */         Constructor.this.constructMapping2ndStep((MappingNode)node, (Map<Object, Object>)object);
/* 182 */       } else if (Set.class.isAssignableFrom(node.getType())) {
/* 183 */         Constructor.this.constructSet2ndStep((MappingNode)node, (Set<Object>)object);
/*     */       } else {
/* 185 */         constructJavaBean2ndStep((MappingNode)node, object);
/*     */       } 
/*     */     }
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
/*     */     protected Object constructJavaBean2ndStep(MappingNode node, Object object) {
/* 214 */       Constructor.this.flattenMapping(node, true);
/* 215 */       Class<? extends Object> beanType = node.getType();
/* 216 */       List<NodeTuple> nodeValue = node.getValue();
/* 217 */       for (NodeTuple tuple : nodeValue) {
/* 218 */         Node valueNode = tuple.getValueNode();
/*     */         
/* 220 */         String key = (String)Constructor.this.constructObject(tuple.getKeyNode());
/*     */         try {
/* 222 */           TypeDescription memberDescription = Constructor.this.typeDefinitions.get(beanType);
/*     */           
/* 224 */           Property property = (memberDescription == null) ? getProperty(beanType, key) : memberDescription.getProperty(key);
/*     */           
/* 226 */           if (!property.isWritable()) {
/* 227 */             throw new YAMLException("No writable property '" + key + "' on class: " + beanType
/* 228 */                 .getName());
/*     */           }
/*     */           
/* 231 */           valueNode.setType(property.getType());
/*     */           
/* 233 */           boolean typeDetected = (memberDescription != null && memberDescription.setupPropertyType(key, valueNode));
/* 234 */           if (!typeDetected && valueNode.getNodeId() != NodeId.scalar) {
/*     */             
/* 236 */             Class<?>[] arguments = property.getActualTypeArguments();
/* 237 */             if (arguments != null && arguments.length > 0)
/*     */             {
/*     */               
/* 240 */               if (valueNode.getNodeId() == NodeId.sequence) {
/* 241 */                 Class<?> t = arguments[0];
/* 242 */                 SequenceNode snode = (SequenceNode)valueNode;
/* 243 */                 snode.setListType(t);
/* 244 */               } else if (Map.class.isAssignableFrom(valueNode.getType())) {
/* 245 */                 Class<?> keyType = arguments[0];
/* 246 */                 Class<?> valueType = arguments[1];
/* 247 */                 MappingNode mnode = (MappingNode)valueNode;
/* 248 */                 mnode.setTypes(keyType, valueType);
/* 249 */                 mnode.setUseClassConstructor(Boolean.valueOf(true));
/* 250 */               } else if (Collection.class.isAssignableFrom(valueNode.getType())) {
/* 251 */                 Class<?> t = arguments[0];
/* 252 */                 MappingNode mnode = (MappingNode)valueNode;
/* 253 */                 mnode.setOnlyKeyType(t);
/* 254 */                 mnode.setUseClassConstructor(Boolean.valueOf(true));
/*     */               } 
/*     */             }
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 261 */           Object value = (memberDescription != null) ? newInstance(memberDescription, key, valueNode) : Constructor.this.constructObject(valueNode);
/*     */ 
/*     */           
/* 264 */           if ((property.getType() == float.class || property.getType() == Float.class) && value instanceof Double)
/*     */           {
/* 266 */             value = Float.valueOf(((Double)value).floatValue());
/*     */           }
/*     */ 
/*     */           
/* 270 */           if (property.getType() == String.class && Tag.BINARY.equals(valueNode.getTag()) && value instanceof byte[])
/*     */           {
/* 272 */             value = new String((byte[])value);
/*     */           }
/*     */           
/* 275 */           if (memberDescription == null || !memberDescription.setProperty(object, key, value)) {
/* 276 */             property.set(object, value);
/*     */           }
/* 278 */         } catch (DuplicateKeyException e) {
/* 279 */           throw e;
/* 280 */         } catch (Exception e) {
/* 281 */           throw new ConstructorException("Cannot create property=" + key + " for JavaBean=" + object, node
/* 282 */               .getStartMark(), e
/* 283 */               .getMessage(), valueNode.getStartMark(), e);
/*     */         } 
/*     */       } 
/* 286 */       return object;
/*     */     }
/*     */     
/*     */     private Object newInstance(TypeDescription memberDescription, String propertyName, Node node) {
/* 290 */       Object newInstance = memberDescription.newInstance(propertyName, node);
/* 291 */       if (newInstance != null) {
/* 292 */         Constructor.this.constructedObjects.put(node, newInstance);
/* 293 */         return Constructor.this.constructObjectNoCheck(node);
/*     */       } 
/* 295 */       return Constructor.this.constructObject(node);
/*     */     }
/*     */     
/*     */     protected Property getProperty(Class<? extends Object> type, String name) {
/* 299 */       return Constructor.this.getPropertyUtils().getProperty(type, name);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected class ConstructYamlObject
/*     */     implements Construct
/*     */   {
/*     */     private Construct getConstructor(Node node) {
/* 311 */       Class<?> cl = Constructor.this.getClassForNode(node);
/* 312 */       node.setType(cl);
/*     */       
/* 314 */       Construct constructor = Constructor.this.yamlClassConstructors.get(node.getNodeId());
/* 315 */       return constructor;
/*     */     }
/*     */     
/*     */     public Object construct(Node node) {
/*     */       try {
/* 320 */         return getConstructor(node).construct(node);
/* 321 */       } catch (ConstructorException e) {
/* 322 */         throw e;
/* 323 */       } catch (Exception e) {
/* 324 */         throw new ConstructorException(null, null, "Can't construct a java object for " + node
/* 325 */             .getTag() + "; exception=" + e.getMessage(), node
/* 326 */             .getStartMark(), e);
/*     */       } 
/*     */     }
/*     */     
/*     */     public void construct2ndStep(Node node, Object object) {
/*     */       try {
/* 332 */         getConstructor(node).construct2ndStep(node, object);
/* 333 */       } catch (Exception e) {
/* 334 */         throw new ConstructorException(null, null, "Can't construct a second step for a java object for " + node
/* 335 */             .getTag() + "; exception=" + e
/* 336 */             .getMessage(), node
/* 337 */             .getStartMark(), e);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected class ConstructScalar
/*     */     extends AbstractConstruct
/*     */   {
/*     */     public Object construct(Node nnode) {
/*     */       Object result;
/* 349 */       ScalarNode node = (ScalarNode)nnode;
/* 350 */       Class<?> type = node.getType();
/*     */ 
/*     */       
/* 353 */       Object instance = Constructor.this.newInstance(type, (Node)node, false);
/* 354 */       if (instance != BaseConstructor.NOT_INSTANTIATED_OBJECT) {
/* 355 */         return instance;
/*     */       }
/*     */ 
/*     */       
/* 359 */       if (type.isPrimitive() || type == String.class || Number.class.isAssignableFrom(type) || type == Boolean.class || Date.class
/* 360 */         .isAssignableFrom(type) || type == Character.class || type == BigInteger.class || type == BigDecimal.class || Enum.class
/*     */         
/* 362 */         .isAssignableFrom(type) || Tag.BINARY.equals(node.getTag()) || Calendar.class
/* 363 */         .isAssignableFrom(type) || type == UUID.class) {
/*     */         
/* 365 */         result = constructStandardJavaInstance(type, node);
/*     */       } else {
/*     */         Object argument;
/* 368 */         Constructor[] arrayOfConstructor = (Constructor[])type.getDeclaredConstructors();
/* 369 */         int oneArgCount = 0;
/* 370 */         Constructor<?> javaConstructor = null;
/* 371 */         for (Constructor<?> c : arrayOfConstructor) {
/* 372 */           if ((c.getParameterTypes()).length == 1) {
/* 373 */             oneArgCount++;
/* 374 */             javaConstructor = c;
/*     */           } 
/*     */         } 
/*     */         
/* 378 */         if (javaConstructor == null)
/* 379 */           throw new YAMLException("No single argument constructor found for " + type); 
/* 380 */         if (oneArgCount == 1) {
/* 381 */           argument = constructStandardJavaInstance(javaConstructor.getParameterTypes()[0], node);
/*     */ 
/*     */         
/*     */         }
/*     */         else {
/*     */ 
/*     */ 
/*     */           
/* 389 */           argument = Constructor.this.constructScalar(node);
/*     */           try {
/* 391 */             javaConstructor = type.getDeclaredConstructor(new Class[] { String.class });
/* 392 */           } catch (Exception e) {
/* 393 */             throw new YAMLException("Can't construct a java object for scalar " + node.getTag() + "; No String constructor found. Exception=" + e
/* 394 */                 .getMessage(), e);
/*     */           } 
/*     */         } 
/*     */         try {
/* 398 */           javaConstructor.setAccessible(true);
/* 399 */           result = javaConstructor.newInstance(new Object[] { argument });
/* 400 */         } catch (Exception e) {
/* 401 */           throw new ConstructorException(null, null, "Can't construct a java object for scalar " + node
/* 402 */               .getTag() + "; exception=" + e.getMessage(), node.getStartMark(), e);
/*     */         } 
/*     */       } 
/* 405 */       return result;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private Object constructStandardJavaInstance(Class<String> type, ScalarNode node) {
/*     */       Object result;
/* 412 */       if (type == String.class) {
/* 413 */         Construct stringConstructor = Constructor.this.yamlConstructors.get(Tag.STR);
/* 414 */         result = stringConstructor.construct((Node)node);
/* 415 */       } else if (type == Boolean.class || type == boolean.class) {
/* 416 */         Construct boolConstructor = Constructor.this.yamlConstructors.get(Tag.BOOL);
/* 417 */         result = boolConstructor.construct((Node)node);
/* 418 */       } else if (type == Character.class || type == char.class) {
/* 419 */         Construct charConstructor = Constructor.this.yamlConstructors.get(Tag.STR);
/* 420 */         String ch = (String)charConstructor.construct((Node)node);
/* 421 */         if (ch.length() == 0)
/* 422 */         { result = null; }
/* 423 */         else { if (ch.length() != 1) {
/* 424 */             throw new YAMLException("Invalid node Character: '" + ch + "'; length: " + ch.length());
/*     */           }
/* 426 */           result = Character.valueOf(ch.charAt(0)); }
/*     */       
/* 428 */       } else if (Date.class.isAssignableFrom(type)) {
/* 429 */         Construct dateConstructor = Constructor.this.yamlConstructors.get(Tag.TIMESTAMP);
/* 430 */         Date date = (Date)dateConstructor.construct((Node)node);
/* 431 */         if (type == Date.class) {
/* 432 */           result = date;
/*     */         } else {
/*     */           try {
/* 435 */             Constructor<?> constr = type.getConstructor(new Class[] { long.class });
/* 436 */             result = constr.newInstance(new Object[] { Long.valueOf(date.getTime()) });
/* 437 */           } catch (RuntimeException e) {
/* 438 */             throw e;
/* 439 */           } catch (Exception e) {
/* 440 */             throw new YAMLException("Cannot construct: '" + type + "'");
/*     */           } 
/*     */         } 
/* 443 */       } else if (type == Float.class || type == Double.class || type == float.class || type == double.class || type == BigDecimal.class) {
/*     */         
/* 445 */         if (type == BigDecimal.class) {
/* 446 */           result = new BigDecimal(node.getValue());
/*     */         } else {
/* 448 */           Construct doubleConstructor = Constructor.this.yamlConstructors.get(Tag.FLOAT);
/* 449 */           result = doubleConstructor.construct((Node)node);
/* 450 */           if (type == Float.class || type == float.class) {
/* 451 */             result = Float.valueOf(((Double)result).floatValue());
/*     */           }
/*     */         } 
/* 454 */       } else if (type == Byte.class || type == Short.class || type == Integer.class || type == Long.class || type == BigInteger.class || type == byte.class || type == short.class || type == int.class || type == long.class) {
/*     */ 
/*     */         
/* 457 */         Construct intConstructor = Constructor.this.yamlConstructors.get(Tag.INT);
/* 458 */         result = intConstructor.construct((Node)node);
/* 459 */         if (type == Byte.class || type == byte.class) {
/* 460 */           result = Byte.valueOf(Integer.valueOf(result.toString()).byteValue());
/* 461 */         } else if (type == Short.class || type == short.class) {
/* 462 */           result = Short.valueOf(Integer.valueOf(result.toString()).shortValue());
/* 463 */         } else if (type == Integer.class || type == int.class) {
/* 464 */           result = Integer.valueOf(Integer.parseInt(result.toString()));
/* 465 */         } else if (type == Long.class || type == long.class) {
/* 466 */           result = Long.valueOf(result.toString());
/*     */         } else {
/*     */           
/* 469 */           result = new BigInteger(result.toString());
/*     */         } 
/* 471 */       } else if (Enum.class.isAssignableFrom(type)) {
/* 472 */         String enumValueName = node.getValue();
/*     */         try {
/* 474 */           if (Constructor.this.loadingConfig.isEnumCaseSensitive()) {
/* 475 */             result = Enum.valueOf(type, enumValueName);
/*     */           } else {
/* 477 */             result = EnumUtils.findEnumInsensitiveCase(type, enumValueName);
/*     */           } 
/* 479 */         } catch (Exception ex) {
/* 480 */           throw new YAMLException("Unable to find enum value '" + enumValueName + "' for enum class: " + type
/* 481 */               .getName());
/*     */         } 
/* 483 */       } else if (Calendar.class.isAssignableFrom(type)) {
/* 484 */         SafeConstructor.ConstructYamlTimestamp contr = new SafeConstructor.ConstructYamlTimestamp();
/* 485 */         contr.construct((Node)node);
/* 486 */         result = contr.getCalendar();
/* 487 */       } else if (Number.class.isAssignableFrom(type)) {
/*     */         
/* 489 */         SafeConstructor.ConstructYamlFloat contr = new SafeConstructor.ConstructYamlFloat(Constructor.this);
/* 490 */         result = contr.construct((Node)node);
/* 491 */       } else if (UUID.class == type) {
/* 492 */         result = UUID.fromString(node.getValue());
/*     */       }
/* 494 */       else if (Constructor.this.yamlConstructors.containsKey(node.getTag())) {
/* 495 */         result = ((Construct)Constructor.this.yamlConstructors.get(node.getTag())).construct((Node)node);
/*     */       } else {
/* 497 */         throw new YAMLException("Unsupported class: " + type);
/*     */       } 
/*     */       
/* 500 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected class ConstructSequence
/*     */     implements Construct
/*     */   {
/*     */     public Object construct(Node node) {
/* 511 */       SequenceNode snode = (SequenceNode)node;
/* 512 */       if (Set.class.isAssignableFrom(node.getType())) {
/* 513 */         if (node.isTwoStepsConstruction()) {
/* 514 */           throw new YAMLException("Set cannot be recursive.");
/*     */         }
/* 516 */         return Constructor.this.constructSet(snode);
/*     */       } 
/* 518 */       if (Collection.class.isAssignableFrom(node.getType())) {
/* 519 */         if (node.isTwoStepsConstruction()) {
/* 520 */           return Constructor.this.newList(snode);
/*     */         }
/* 522 */         return Constructor.this.constructSequence(snode);
/*     */       } 
/* 524 */       if (node.getType().isArray()) {
/* 525 */         if (node.isTwoStepsConstruction()) {
/* 526 */           return Constructor.this.createArray(node.getType(), snode.getValue().size());
/*     */         }
/* 528 */         return Constructor.this.constructArray(snode);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 533 */       List<Constructor<?>> possibleConstructors = new ArrayList<>(snode.getValue().size());
/* 534 */       for (Constructor<?> constructor : node.getType()
/* 535 */         .getDeclaredConstructors()) {
/* 536 */         if (snode.getValue().size() == (constructor.getParameterTypes()).length) {
/* 537 */           possibleConstructors.add(constructor);
/*     */         }
/*     */       } 
/* 540 */       if (!possibleConstructors.isEmpty()) {
/* 541 */         if (possibleConstructors.size() == 1) {
/* 542 */           Object[] arrayOfObject = new Object[snode.getValue().size()];
/* 543 */           Constructor<?> c = possibleConstructors.get(0);
/* 544 */           int i = 0;
/* 545 */           for (Node argumentNode : snode.getValue()) {
/* 546 */             Class<?> type = c.getParameterTypes()[i];
/*     */             
/* 548 */             argumentNode.setType(type);
/* 549 */             arrayOfObject[i++] = Constructor.this.constructObject(argumentNode);
/*     */           } 
/*     */           
/*     */           try {
/* 553 */             c.setAccessible(true);
/* 554 */             return c.newInstance(arrayOfObject);
/* 555 */           } catch (Exception e) {
/* 556 */             throw new YAMLException(e);
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 561 */         List<Object> argumentList = (List)Constructor.this.constructSequence(snode);
/* 562 */         Class<?>[] parameterTypes = new Class[argumentList.size()];
/* 563 */         int index = 0;
/* 564 */         for (Object parameter : argumentList) {
/* 565 */           parameterTypes[index] = parameter.getClass();
/* 566 */           index++;
/*     */         } 
/*     */         
/* 569 */         for (Constructor<?> c : possibleConstructors) {
/* 570 */           Class<?>[] argTypes = c.getParameterTypes();
/* 571 */           boolean foundConstructor = true;
/* 572 */           for (int i = 0; i < argTypes.length; i++) {
/* 573 */             if (!wrapIfPrimitive(argTypes[i]).isAssignableFrom(parameterTypes[i])) {
/* 574 */               foundConstructor = false;
/*     */               break;
/*     */             } 
/*     */           } 
/* 578 */           if (foundConstructor) {
/*     */             try {
/* 580 */               c.setAccessible(true);
/* 581 */               return c.newInstance(argumentList.toArray());
/* 582 */             } catch (Exception e) {
/* 583 */               throw new YAMLException(e);
/*     */             } 
/*     */           }
/*     */         } 
/*     */       } 
/* 588 */       throw new YAMLException("No suitable constructor with " + snode.getValue().size() + " arguments found for " + node
/* 589 */           .getType());
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private Class<? extends Object> wrapIfPrimitive(Class<?> clazz) {
/* 595 */       if (!clazz.isPrimitive()) {
/* 596 */         return (Class)clazz;
/*     */       }
/* 598 */       if (clazz == int.class) {
/* 599 */         return (Class)Integer.class;
/*     */       }
/* 601 */       if (clazz == float.class) {
/* 602 */         return (Class)Float.class;
/*     */       }
/* 604 */       if (clazz == double.class) {
/* 605 */         return (Class)Double.class;
/*     */       }
/* 607 */       if (clazz == boolean.class) {
/* 608 */         return (Class)Boolean.class;
/*     */       }
/* 610 */       if (clazz == long.class) {
/* 611 */         return (Class)Long.class;
/*     */       }
/* 613 */       if (clazz == char.class) {
/* 614 */         return (Class)Character.class;
/*     */       }
/* 616 */       if (clazz == short.class) {
/* 617 */         return (Class)Short.class;
/*     */       }
/* 619 */       if (clazz == byte.class) {
/* 620 */         return (Class)Byte.class;
/*     */       }
/* 622 */       throw new YAMLException("Unexpected primitive " + clazz);
/*     */     }
/*     */ 
/*     */     
/*     */     public void construct2ndStep(Node node, Object object) {
/* 627 */       SequenceNode snode = (SequenceNode)node;
/* 628 */       if (List.class.isAssignableFrom(node.getType())) {
/* 629 */         List<Object> list = (List<Object>)object;
/* 630 */         Constructor.this.constructSequenceStep2(snode, list);
/* 631 */       } else if (node.getType().isArray()) {
/* 632 */         Constructor.this.constructArrayStep2(snode, object);
/*     */       } else {
/* 634 */         throw new YAMLException("Immutable objects cannot be recursive.");
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   protected Class<?> getClassForNode(Node node) {
/* 640 */     Class<? extends Object> classForTag = this.typeTags.get(node.getTag());
/* 641 */     if (classForTag == null) {
/* 642 */       Class<?> cl; String name = node.getTag().getClassName();
/*     */       
/*     */       try {
/* 645 */         cl = getClassForName(name);
/* 646 */       } catch (ClassNotFoundException e) {
/* 647 */         throw new YAMLException("Class not found: " + name);
/*     */       } 
/* 649 */       this.typeTags.put(node.getTag(), cl);
/* 650 */       return cl;
/*     */     } 
/* 652 */     return classForTag;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Class<?> getClassForName(String name) throws ClassNotFoundException {
/*     */     try {
/* 658 */       return Class.forName(name, true, Thread.currentThread().getContextClassLoader());
/* 659 */     } catch (ClassNotFoundException e) {
/* 660 */       return Class.forName(name);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\constructor\Constructor.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */