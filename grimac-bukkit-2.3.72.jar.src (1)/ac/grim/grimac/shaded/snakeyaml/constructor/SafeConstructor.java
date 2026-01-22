/*     */ package ac.grim.grimac.shaded.snakeyaml.constructor;
/*     */ 
/*     */ import ac.grim.grimac.shaded.snakeyaml.LoaderOptions;
/*     */ import ac.grim.grimac.shaded.snakeyaml.error.YAMLException;
/*     */ import ac.grim.grimac.shaded.snakeyaml.external.biz.base64Coder.Base64Coder;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.MappingNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.Node;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.NodeId;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.NodeTuple;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.ScalarNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.SequenceNode;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.Tag;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ import java.util.TreeSet;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ public class SafeConstructor
/*     */   extends BaseConstructor
/*     */ {
/*  45 */   public static final ConstructUndefined undefinedConstructor = new ConstructUndefined();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SafeConstructor(LoaderOptions loaderOptions) {
/*  53 */     super(loaderOptions);
/*  54 */     this.yamlConstructors.put(Tag.NULL, new ConstructYamlNull());
/*  55 */     this.yamlConstructors.put(Tag.BOOL, new ConstructYamlBool());
/*  56 */     this.yamlConstructors.put(Tag.INT, new ConstructYamlInt());
/*  57 */     this.yamlConstructors.put(Tag.FLOAT, new ConstructYamlFloat());
/*  58 */     this.yamlConstructors.put(Tag.BINARY, new ConstructYamlBinary());
/*  59 */     this.yamlConstructors.put(Tag.TIMESTAMP, new ConstructYamlTimestamp());
/*  60 */     this.yamlConstructors.put(Tag.OMAP, new ConstructYamlOmap());
/*  61 */     this.yamlConstructors.put(Tag.PAIRS, new ConstructYamlPairs());
/*  62 */     this.yamlConstructors.put(Tag.SET, new ConstructYamlSet());
/*  63 */     this.yamlConstructors.put(Tag.STR, new ConstructYamlStr());
/*  64 */     this.yamlConstructors.put(Tag.SEQ, new ConstructYamlSeq());
/*  65 */     this.yamlConstructors.put(Tag.MAP, new ConstructYamlMap());
/*  66 */     this.yamlConstructors.put(null, undefinedConstructor);
/*  67 */     this.yamlClassConstructors.put(NodeId.scalar, undefinedConstructor);
/*  68 */     this.yamlClassConstructors.put(NodeId.sequence, undefinedConstructor);
/*  69 */     this.yamlClassConstructors.put(NodeId.mapping, undefinedConstructor);
/*     */   }
/*     */   
/*     */   protected void flattenMapping(MappingNode node) {
/*  73 */     flattenMapping(node, false);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void flattenMapping(MappingNode node, boolean forceStringKeys) {
/*  78 */     processDuplicateKeys(node, forceStringKeys);
/*  79 */     if (node.isMerged()) {
/*  80 */       node.setValue(mergeNode(node, true, new HashMap<>(), new ArrayList<>(), forceStringKeys));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void processDuplicateKeys(MappingNode node) {
/*  86 */     processDuplicateKeys(node, false);
/*     */   }
/*     */   
/*     */   protected void processDuplicateKeys(MappingNode node, boolean forceStringKeys) {
/*  90 */     List<NodeTuple> nodeValue = node.getValue();
/*  91 */     Map<Object, Integer> keys = new HashMap<>(nodeValue.size());
/*  92 */     TreeSet<Integer> toRemove = new TreeSet<>();
/*  93 */     int i = 0;
/*  94 */     for (NodeTuple tuple : nodeValue) {
/*  95 */       Node keyNode = tuple.getKeyNode();
/*  96 */       if (!keyNode.getTag().equals(Tag.MERGE)) {
/*  97 */         if (forceStringKeys) {
/*  98 */           if (keyNode instanceof ScalarNode) {
/*  99 */             keyNode.setType(String.class);
/* 100 */             keyNode.setTag(Tag.STR);
/*     */           } else {
/* 102 */             throw new YAMLException("Keys must be scalars but found: " + keyNode);
/*     */           } 
/*     */         }
/* 105 */         Object key = constructObject(keyNode);
/* 106 */         if (key != null && !forceStringKeys && 
/* 107 */           keyNode.isTwoStepsConstruction()) {
/* 108 */           if (!this.loadingConfig.getAllowRecursiveKeys()) {
/* 109 */             throw new YAMLException("Recursive key for mapping is detected but it is not configured to be allowed.");
/*     */           }
/*     */           
/*     */           try {
/* 113 */             key.hashCode();
/* 114 */           } catch (Exception e) {
/* 115 */             throw new ConstructorException("while constructing a mapping", node.getStartMark(), "found unacceptable key " + key, tuple
/* 116 */                 .getKeyNode().getStartMark(), e);
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 122 */         Integer prevIndex = keys.put(key, Integer.valueOf(i));
/* 123 */         if (prevIndex != null) {
/* 124 */           if (!isAllowDuplicateKeys()) {
/* 125 */             throw new DuplicateKeyException(node.getStartMark(), key, tuple
/* 126 */                 .getKeyNode().getStartMark());
/*     */           }
/* 128 */           toRemove.add(prevIndex);
/*     */         } 
/*     */       } 
/* 131 */       i++;
/*     */     } 
/*     */     
/* 134 */     Iterator<Integer> indices2remove = toRemove.descendingIterator();
/* 135 */     while (indices2remove.hasNext()) {
/* 136 */       nodeValue.remove(((Integer)indices2remove.next()).intValue());
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
/*     */ 
/*     */   
/*     */   private List<NodeTuple> mergeNode(MappingNode node, boolean isPreffered, Map<Object, Integer> key2index, List<NodeTuple> values, boolean forceStringKeys) {
/* 151 */     Iterator<NodeTuple> iter = node.getValue().iterator();
/* 152 */     while (iter.hasNext()) {
/* 153 */       NodeTuple nodeTuple = iter.next();
/* 154 */       Node keyNode = nodeTuple.getKeyNode();
/* 155 */       Node valueNode = nodeTuple.getValueNode();
/* 156 */       if (keyNode.getTag().equals(Tag.MERGE)) {
/* 157 */         MappingNode mn; SequenceNode sn; List<Node> vals; iter.remove();
/* 158 */         switch (valueNode.getNodeId()) {
/*     */           case mapping:
/* 160 */             mn = (MappingNode)valueNode;
/* 161 */             mergeNode(mn, false, key2index, values, forceStringKeys);
/*     */             continue;
/*     */           case sequence:
/* 164 */             sn = (SequenceNode)valueNode;
/* 165 */             vals = sn.getValue();
/* 166 */             for (Node subnode : vals) {
/* 167 */               if (!(subnode instanceof MappingNode)) {
/* 168 */                 throw new ConstructorException("while constructing a mapping", node.getStartMark(), "expected a mapping for merging, but found " + subnode
/* 169 */                     .getNodeId(), subnode
/* 170 */                     .getStartMark());
/*     */               }
/* 172 */               MappingNode mnode = (MappingNode)subnode;
/* 173 */               mergeNode(mnode, false, key2index, values, forceStringKeys);
/*     */             } 
/*     */             continue;
/*     */         } 
/* 177 */         throw new ConstructorException("while constructing a mapping", node.getStartMark(), "expected a mapping or list of mappings for merging, but found " + valueNode
/*     */             
/* 179 */             .getNodeId(), valueNode
/* 180 */             .getStartMark());
/*     */       } 
/*     */ 
/*     */       
/* 184 */       if (forceStringKeys) {
/* 185 */         if (keyNode instanceof ScalarNode) {
/* 186 */           keyNode.setType(String.class);
/* 187 */           keyNode.setTag(Tag.STR);
/*     */         } else {
/* 189 */           throw new YAMLException("Keys must be scalars but found: " + keyNode);
/*     */         } 
/*     */       }
/* 192 */       Object key = constructObject(keyNode);
/* 193 */       if (!key2index.containsKey(key)) {
/* 194 */         values.add(nodeTuple);
/*     */         
/* 196 */         key2index.put(key, Integer.valueOf(values.size() - 1)); continue;
/* 197 */       }  if (isPreffered)
/*     */       {
/*     */         
/* 200 */         values.set(((Integer)key2index.get(key)).intValue(), nodeTuple);
/*     */       }
/*     */     } 
/*     */     
/* 204 */     return values;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void constructMapping2ndStep(MappingNode node, Map<Object, Object> mapping) {
/* 209 */     flattenMapping(node);
/* 210 */     super.constructMapping2ndStep(node, mapping);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void constructSet2ndStep(MappingNode node, Set<Object> set) {
/* 215 */     flattenMapping(node);
/* 216 */     super.constructSet2ndStep(node, set);
/*     */   }
/*     */   
/*     */   public class ConstructYamlNull
/*     */     extends AbstractConstruct
/*     */   {
/*     */     public Object construct(Node node) {
/* 223 */       if (node != null) {
/* 224 */         SafeConstructor.this.constructScalar((ScalarNode)node);
/*     */       }
/* 226 */       return null;
/*     */     }
/*     */   }
/*     */   
/* 230 */   private static final Map<String, Boolean> BOOL_VALUES = new HashMap<>();
/*     */   
/*     */   static {
/* 233 */     BOOL_VALUES.put("yes", Boolean.TRUE);
/* 234 */     BOOL_VALUES.put("no", Boolean.FALSE);
/* 235 */     BOOL_VALUES.put("true", Boolean.TRUE);
/* 236 */     BOOL_VALUES.put("false", Boolean.FALSE);
/* 237 */     BOOL_VALUES.put("on", Boolean.TRUE);
/* 238 */     BOOL_VALUES.put("off", Boolean.FALSE);
/*     */   }
/*     */   
/*     */   public class ConstructYamlBool
/*     */     extends AbstractConstruct
/*     */   {
/*     */     public Object construct(Node node) {
/* 245 */       String val = SafeConstructor.this.constructScalar((ScalarNode)node);
/* 246 */       return SafeConstructor.BOOL_VALUES.get(val.toLowerCase());
/*     */     }
/*     */   }
/*     */   
/*     */   public class ConstructYamlInt
/*     */     extends AbstractConstruct
/*     */   {
/*     */     public Object construct(Node node) {
/* 254 */       String value = SafeConstructor.this.constructScalar((ScalarNode)node).replaceAll("_", "");
/* 255 */       if (value.isEmpty()) {
/* 256 */         throw new ConstructorException("while constructing an int", node.getStartMark(), "found empty value", node
/* 257 */             .getStartMark());
/*     */       }
/* 259 */       int sign = 1;
/* 260 */       char first = value.charAt(0);
/* 261 */       if (first == '-') {
/* 262 */         sign = -1;
/* 263 */         value = value.substring(1);
/* 264 */       } else if (first == '+') {
/* 265 */         value = value.substring(1);
/*     */       } 
/* 267 */       int base = 10;
/* 268 */       if ("0".equals(value))
/* 269 */         return Integer.valueOf(0); 
/* 270 */       if (value.startsWith("0b"))
/* 271 */       { value = value.substring(2);
/* 272 */         base = 2; }
/* 273 */       else if (value.startsWith("0x"))
/* 274 */       { value = value.substring(2);
/* 275 */         base = 16; }
/* 276 */       else if (value.startsWith("0"))
/* 277 */       { value = value.substring(1);
/* 278 */         base = 8; }
/* 279 */       else { if (value.indexOf(':') != -1) {
/* 280 */           String[] digits = value.split(":");
/* 281 */           int bes = 1;
/* 282 */           int val = 0;
/* 283 */           for (int i = 0, j = digits.length; i < j; i++) {
/* 284 */             val = (int)(val + Long.parseLong(digits[j - i - 1]) * bes);
/* 285 */             bes *= 60;
/*     */           } 
/* 287 */           return SafeConstructor.this.createNumber(sign, String.valueOf(val), 10);
/*     */         } 
/* 289 */         return SafeConstructor.this.createNumber(sign, value, 10); }
/*     */       
/* 291 */       return SafeConstructor.this.createNumber(sign, value, base);
/*     */     }
/*     */   }
/*     */   
/* 295 */   private static final int[][] RADIX_MAX = new int[17][2];
/*     */   
/*     */   static {
/* 298 */     int[] radixList = { 2, 8, 10, 16 };
/* 299 */     for (int radix : radixList) {
/* 300 */       (new int[2])[0] = 
/* 301 */         maxLen(2147483647, radix); (new int[2])[1] = maxLen(Long.MAX_VALUE, radix);
/*     */       RADIX_MAX[radix] = new int[2];
/*     */     } 
/*     */   }
/*     */   private static int maxLen(int max, int radix) {
/* 306 */     return Integer.toString(max, radix).length();
/*     */   }
/*     */   
/*     */   private static int maxLen(long max, int radix) {
/* 310 */     return Long.toString(max, radix).length();
/*     */   }
/*     */   private Number createNumber(int sign, String number, int radix) {
/*     */     Number result;
/* 314 */     int len = (number != null) ? number.length() : 0;
/* 315 */     if (sign < 0) {
/* 316 */       number = "-" + number;
/*     */     }
/* 318 */     int[] maxArr = (radix < RADIX_MAX.length) ? RADIX_MAX[radix] : null;
/* 319 */     if (maxArr != null) {
/* 320 */       boolean gtInt = (len > maxArr[0]);
/* 321 */       if (gtInt) {
/* 322 */         if (len > maxArr[1]) {
/* 323 */           return new BigInteger(number, radix);
/*     */         }
/* 325 */         return createLongOrBigInteger(number, radix);
/*     */       } 
/*     */     } 
/*     */     
/*     */     try {
/* 330 */       result = Integer.valueOf(number, radix);
/* 331 */     } catch (NumberFormatException e) {
/* 332 */       result = createLongOrBigInteger(number, radix);
/*     */     } 
/* 334 */     return result;
/*     */   }
/*     */   
/*     */   protected static Number createLongOrBigInteger(String number, int radix) {
/*     */     try {
/* 339 */       return Long.valueOf(number, radix);
/* 340 */     } catch (NumberFormatException e1) {
/* 341 */       return new BigInteger(number, radix);
/*     */     } 
/*     */   }
/*     */   
/*     */   public class ConstructYamlFloat
/*     */     extends AbstractConstruct
/*     */   {
/*     */     public Object construct(Node node) {
/* 349 */       String value = SafeConstructor.this.constructScalar((ScalarNode)node).replaceAll("_", "");
/* 350 */       if (value.isEmpty()) {
/* 351 */         throw new ConstructorException("while constructing a float", node.getStartMark(), "found empty value", node
/* 352 */             .getStartMark());
/*     */       }
/* 354 */       int sign = 1;
/* 355 */       char first = value.charAt(0);
/* 356 */       if (first == '-') {
/* 357 */         sign = -1;
/* 358 */         value = value.substring(1);
/* 359 */       } else if (first == '+') {
/* 360 */         value = value.substring(1);
/*     */       } 
/* 362 */       String valLower = value.toLowerCase();
/* 363 */       if (".inf".equals(valLower))
/* 364 */         return Double.valueOf((sign == -1) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY); 
/* 365 */       if (".nan".equals(valLower))
/* 366 */         return Double.valueOf(Double.NaN); 
/* 367 */       if (value.indexOf(':') != -1) {
/* 368 */         String[] digits = value.split(":");
/* 369 */         int bes = 1;
/* 370 */         double val = 0.0D;
/* 371 */         for (int i = 0, j = digits.length; i < j; i++) {
/* 372 */           val += Double.parseDouble(digits[j - i - 1]) * bes;
/* 373 */           bes *= 60;
/*     */         } 
/* 375 */         return Double.valueOf(sign * val);
/*     */       } 
/* 377 */       Double d = Double.valueOf(value);
/* 378 */       return Double.valueOf(d.doubleValue() * sign);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public class ConstructYamlBinary
/*     */     extends AbstractConstruct
/*     */   {
/*     */     public Object construct(Node node) {
/* 388 */       String noWhiteSpaces = SafeConstructor.this.constructScalar((ScalarNode)node).replaceAll("\\s", "");
/* 389 */       byte[] decoded = Base64Coder.decode(noWhiteSpaces.toCharArray());
/* 390 */       return decoded;
/*     */     }
/*     */   }
/*     */   
/* 394 */   private static final Pattern TIMESTAMP_REGEXP = Pattern.compile("^([0-9][0-9][0-9][0-9])-([0-9][0-9]?)-([0-9][0-9]?)(?:(?:[Tt]|[ \t]+)([0-9][0-9]?):([0-9][0-9]):([0-9][0-9])(?:\\.([0-9]*))?(?:[ \t]*(?:Z|([-+][0-9][0-9]?)(?::([0-9][0-9])?)?))?)?$");
/*     */ 
/*     */   
/* 397 */   private static final Pattern YMD_REGEXP = Pattern.compile("^([0-9][0-9][0-9][0-9])-([0-9][0-9]?)-([0-9][0-9]?)$");
/*     */   
/*     */   public static class ConstructYamlTimestamp
/*     */     extends AbstractConstruct {
/*     */     private Calendar calendar;
/*     */     
/*     */     public Calendar getCalendar() {
/* 404 */       return this.calendar;
/*     */     }
/*     */     
/*     */     public Object construct(Node node) {
/*     */       TimeZone timeZone;
/* 409 */       ScalarNode scalar = (ScalarNode)node;
/* 410 */       String nodeValue = scalar.getValue();
/* 411 */       Matcher match = SafeConstructor.YMD_REGEXP.matcher(nodeValue);
/* 412 */       if (match.matches()) {
/* 413 */         String str1 = match.group(1);
/* 414 */         String str2 = match.group(2);
/* 415 */         String str3 = match.group(3);
/* 416 */         this.calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
/* 417 */         this.calendar.clear();
/* 418 */         this.calendar.set(1, Integer.parseInt(str1));
/*     */         
/* 420 */         this.calendar.set(2, Integer.parseInt(str2) - 1);
/* 421 */         this.calendar.set(5, Integer.parseInt(str3));
/* 422 */         return this.calendar.getTime();
/*     */       } 
/* 424 */       match = SafeConstructor.TIMESTAMP_REGEXP.matcher(nodeValue);
/* 425 */       if (!match.matches()) {
/* 426 */         throw new YAMLException("Unexpected timestamp: " + nodeValue);
/*     */       }
/* 428 */       String year_s = match.group(1);
/* 429 */       String month_s = match.group(2);
/* 430 */       String day_s = match.group(3);
/* 431 */       String hour_s = match.group(4);
/* 432 */       String min_s = match.group(5);
/*     */       
/* 434 */       String seconds = match.group(6);
/* 435 */       String millis = match.group(7);
/* 436 */       if (millis != null) {
/* 437 */         seconds = seconds + "." + millis;
/*     */       }
/* 439 */       double fractions = Double.parseDouble(seconds);
/* 440 */       int sec_s = (int)Math.round(Math.floor(fractions));
/* 441 */       int usec = (int)Math.round((fractions - sec_s) * 1000.0D);
/*     */       
/* 443 */       String timezoneh_s = match.group(8);
/* 444 */       String timezonem_s = match.group(9);
/*     */       
/* 446 */       if (timezoneh_s != null) {
/* 447 */         String time = (timezonem_s != null) ? (":" + timezonem_s) : "00";
/* 448 */         timeZone = TimeZone.getTimeZone("GMT" + timezoneh_s + time);
/*     */       } else {
/*     */         
/* 451 */         timeZone = TimeZone.getTimeZone("UTC");
/*     */       } 
/* 453 */       this.calendar = Calendar.getInstance(timeZone);
/* 454 */       this.calendar.set(1, Integer.parseInt(year_s));
/*     */       
/* 456 */       this.calendar.set(2, Integer.parseInt(month_s) - 1);
/* 457 */       this.calendar.set(5, Integer.parseInt(day_s));
/* 458 */       this.calendar.set(11, Integer.parseInt(hour_s));
/* 459 */       this.calendar.set(12, Integer.parseInt(min_s));
/* 460 */       this.calendar.set(13, sec_s);
/* 461 */       this.calendar.set(14, usec);
/* 462 */       return this.calendar.getTime();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public class ConstructYamlOmap
/*     */     extends AbstractConstruct
/*     */   {
/*     */     public Object construct(Node node) {
/* 473 */       Map<Object, Object> omap = new LinkedHashMap<>();
/* 474 */       if (!(node instanceof SequenceNode)) {
/* 475 */         throw new ConstructorException("while constructing an ordered map", node.getStartMark(), "expected a sequence, but found " + node
/* 476 */             .getNodeId(), node.getStartMark());
/*     */       }
/* 478 */       SequenceNode snode = (SequenceNode)node;
/* 479 */       for (Node subnode : snode.getValue()) {
/* 480 */         if (!(subnode instanceof MappingNode)) {
/* 481 */           throw new ConstructorException("while constructing an ordered map", node.getStartMark(), "expected a mapping of length 1, but found " + subnode
/* 482 */               .getNodeId(), subnode
/* 483 */               .getStartMark());
/*     */         }
/* 485 */         MappingNode mnode = (MappingNode)subnode;
/* 486 */         if (mnode.getValue().size() != 1) {
/* 487 */           throw new ConstructorException("while constructing an ordered map", node.getStartMark(), "expected a single mapping item, but found " + mnode
/* 488 */               .getValue().size() + " items", mnode
/* 489 */               .getStartMark());
/*     */         }
/* 491 */         Node keyNode = ((NodeTuple)mnode.getValue().get(0)).getKeyNode();
/* 492 */         Node valueNode = ((NodeTuple)mnode.getValue().get(0)).getValueNode();
/* 493 */         Object key = SafeConstructor.this.constructObject(keyNode);
/* 494 */         Object value = SafeConstructor.this.constructObject(valueNode);
/* 495 */         omap.put(key, value);
/*     */       } 
/* 497 */       return omap;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public class ConstructYamlPairs
/*     */     extends AbstractConstruct
/*     */   {
/*     */     public Object construct(Node node) {
/* 507 */       if (!(node instanceof SequenceNode)) {
/* 508 */         throw new ConstructorException("while constructing pairs", node.getStartMark(), "expected a sequence, but found " + node
/* 509 */             .getNodeId(), node.getStartMark());
/*     */       }
/* 511 */       SequenceNode snode = (SequenceNode)node;
/* 512 */       List<Object[]> pairs = new ArrayList(snode.getValue().size());
/* 513 */       for (Node subnode : snode.getValue()) {
/* 514 */         if (!(subnode instanceof MappingNode)) {
/* 515 */           throw new ConstructorException("while constructingpairs", node.getStartMark(), "expected a mapping of length 1, but found " + subnode
/* 516 */               .getNodeId(), subnode
/* 517 */               .getStartMark());
/*     */         }
/* 519 */         MappingNode mnode = (MappingNode)subnode;
/* 520 */         if (mnode.getValue().size() != 1) {
/* 521 */           throw new ConstructorException("while constructing pairs", node.getStartMark(), "expected a single mapping item, but found " + mnode
/* 522 */               .getValue().size() + " items", mnode
/* 523 */               .getStartMark());
/*     */         }
/* 525 */         Node keyNode = ((NodeTuple)mnode.getValue().get(0)).getKeyNode();
/* 526 */         Node valueNode = ((NodeTuple)mnode.getValue().get(0)).getValueNode();
/* 527 */         Object key = SafeConstructor.this.constructObject(keyNode);
/* 528 */         Object value = SafeConstructor.this.constructObject(valueNode);
/* 529 */         pairs.add(new Object[] { key, value });
/*     */       } 
/* 531 */       return pairs;
/*     */     }
/*     */   }
/*     */   
/*     */   public class ConstructYamlSet
/*     */     implements Construct
/*     */   {
/*     */     public Object construct(Node node) {
/* 539 */       if (node.isTwoStepsConstruction()) {
/* 540 */         return SafeConstructor.this.constructedObjects.containsKey(node) ? SafeConstructor.this.constructedObjects.get(node) : 
/* 541 */           SafeConstructor.this.createDefaultSet(((MappingNode)node).getValue().size());
/*     */       }
/* 543 */       return SafeConstructor.this.constructSet((MappingNode)node);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void construct2ndStep(Node node, Object object) {
/* 550 */       if (node.isTwoStepsConstruction()) {
/* 551 */         SafeConstructor.this.constructSet2ndStep((MappingNode)node, (Set<Object>)object);
/*     */       } else {
/* 553 */         throw new YAMLException("Unexpected recursive set structure. Node: " + node);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public class ConstructYamlStr
/*     */     extends AbstractConstruct
/*     */   {
/*     */     public Object construct(Node node) {
/* 562 */       return SafeConstructor.this.constructScalar((ScalarNode)node);
/*     */     }
/*     */   }
/*     */   
/*     */   public class ConstructYamlSeq
/*     */     implements Construct
/*     */   {
/*     */     public Object construct(Node node) {
/* 570 */       SequenceNode seqNode = (SequenceNode)node;
/* 571 */       if (node.isTwoStepsConstruction()) {
/* 572 */         return SafeConstructor.this.newList(seqNode);
/*     */       }
/* 574 */       return SafeConstructor.this.constructSequence(seqNode);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void construct2ndStep(Node node, Object data) {
/* 581 */       if (node.isTwoStepsConstruction()) {
/* 582 */         SafeConstructor.this.constructSequenceStep2((SequenceNode)node, (List)data);
/*     */       } else {
/* 584 */         throw new YAMLException("Unexpected recursive sequence structure. Node: " + node);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public class ConstructYamlMap
/*     */     implements Construct
/*     */   {
/*     */     public Object construct(Node node) {
/* 593 */       MappingNode mnode = (MappingNode)node;
/* 594 */       if (node.isTwoStepsConstruction()) {
/* 595 */         return SafeConstructor.this.createDefaultMap(mnode.getValue().size());
/*     */       }
/* 597 */       return SafeConstructor.this.constructMapping(mnode);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void construct2ndStep(Node node, Object object) {
/* 604 */       if (node.isTwoStepsConstruction()) {
/* 605 */         SafeConstructor.this.constructMapping2ndStep((MappingNode)node, (Map<Object, Object>)object);
/*     */       } else {
/* 607 */         throw new YAMLException("Unexpected recursive mapping structure. Node: " + node);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class ConstructUndefined
/*     */     extends AbstractConstruct
/*     */   {
/*     */     public Object construct(Node node) {
/* 616 */       throw new ConstructorException(null, null, "could not determine a constructor for the tag " + node
/* 617 */           .getTag(), node.getStartMark());
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\constructor\SafeConstructor.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */