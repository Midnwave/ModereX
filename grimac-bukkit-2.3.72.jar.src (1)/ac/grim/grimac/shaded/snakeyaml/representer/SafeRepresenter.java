/*     */ package ac.grim.grimac.shaded.snakeyaml.representer;
/*     */ 
/*     */ import ac.grim.grimac.shaded.snakeyaml.DumperOptions;
/*     */ import ac.grim.grimac.shaded.snakeyaml.error.YAMLException;
/*     */ import ac.grim.grimac.shaded.snakeyaml.external.biz.base64Coder.Base64Coder;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.Node;
/*     */ import ac.grim.grimac.shaded.snakeyaml.nodes.Tag;
/*     */ import ac.grim.grimac.shaded.snakeyaml.reader.StreamReader;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ import java.util.UUID;
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
/*     */ 
/*     */ class SafeRepresenter
/*     */   extends BaseRepresenter
/*     */ {
/*     */   protected Map<Class<? extends Object>, Tag> classTags;
/*  44 */   protected TimeZone timeZone = null;
/*     */   protected DumperOptions.NonPrintableStyle nonPrintableStyle;
/*     */   
/*     */   public SafeRepresenter(DumperOptions options) {
/*  48 */     if (options == null) {
/*  49 */       throw new NullPointerException("DumperOptions must be provided.");
/*     */     }
/*  51 */     this.nullRepresenter = new RepresentNull();
/*  52 */     this.representers.put(String.class, new RepresentString());
/*  53 */     this.representers.put(Boolean.class, new RepresentBoolean());
/*  54 */     this.representers.put(Character.class, new RepresentString());
/*  55 */     this.representers.put(UUID.class, new RepresentUuid());
/*  56 */     this.representers.put(byte[].class, new RepresentByteArray());
/*     */     
/*  58 */     Represent primitiveArray = new RepresentPrimitiveArray();
/*  59 */     this.representers.put(short[].class, primitiveArray);
/*  60 */     this.representers.put(int[].class, primitiveArray);
/*  61 */     this.representers.put(long[].class, primitiveArray);
/*  62 */     this.representers.put(float[].class, primitiveArray);
/*  63 */     this.representers.put(double[].class, primitiveArray);
/*  64 */     this.representers.put(char[].class, primitiveArray);
/*  65 */     this.representers.put(boolean[].class, primitiveArray);
/*     */     
/*  67 */     this.multiRepresenters.put(Number.class, new RepresentNumber());
/*  68 */     this.multiRepresenters.put(List.class, new RepresentList());
/*  69 */     this.multiRepresenters.put(Map.class, new RepresentMap());
/*  70 */     this.multiRepresenters.put(Set.class, new RepresentSet());
/*  71 */     this.multiRepresenters.put(Iterator.class, new RepresentIterator());
/*  72 */     this.multiRepresenters.put((new Object[0]).getClass(), new RepresentArray());
/*  73 */     this.multiRepresenters.put(Date.class, new RepresentDate());
/*  74 */     this.multiRepresenters.put(Enum.class, new RepresentEnum());
/*  75 */     this.multiRepresenters.put(Calendar.class, new RepresentDate());
/*  76 */     this.classTags = new HashMap<>();
/*  77 */     this.nonPrintableStyle = options.getNonPrintableStyle();
/*  78 */     setDefaultScalarStyle(options.getDefaultScalarStyle());
/*  79 */     setDefaultFlowStyle(options.getDefaultFlowStyle());
/*     */   }
/*     */   
/*     */   protected Tag getTag(Class<?> clazz, Tag defaultTag) {
/*  83 */     if (this.classTags.containsKey(clazz)) {
/*  84 */       return this.classTags.get(clazz);
/*     */     }
/*  86 */     return defaultTag;
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
/*     */   public Tag addClassTag(Class<? extends Object> clazz, Tag tag) {
/*  98 */     if (tag == null) {
/*  99 */       throw new NullPointerException("Tag must be provided.");
/*     */     }
/* 101 */     return this.classTags.put(clazz, tag);
/*     */   }
/*     */   
/*     */   protected class RepresentNull
/*     */     implements Represent {
/*     */     public Node representData(Object data) {
/* 107 */       return SafeRepresenter.this.representScalar(Tag.NULL, "null");
/*     */     }
/*     */   }
/*     */   
/* 111 */   private static final Pattern MULTILINE_PATTERN = Pattern.compile("\n|| | ");
/*     */   
/*     */   protected class RepresentString
/*     */     implements Represent {
/*     */     public Node representData(Object data) {
/* 116 */       Tag tag = Tag.STR;
/* 117 */       DumperOptions.ScalarStyle style = SafeRepresenter.this.defaultScalarStyle;
/* 118 */       String value = data.toString();
/* 119 */       if (SafeRepresenter.this.nonPrintableStyle == DumperOptions.NonPrintableStyle.BINARY && 
/* 120 */         !StreamReader.isPrintable(value)) {
/* 121 */         tag = Tag.BINARY;
/* 122 */         byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
/*     */ 
/*     */ 
/*     */         
/* 126 */         String checkValue = new String(bytes, StandardCharsets.UTF_8);
/* 127 */         if (!checkValue.equals(value)) {
/* 128 */           throw new YAMLException("invalid string value has occurred");
/*     */         }
/* 130 */         char[] binary = Base64Coder.encode(bytes);
/* 131 */         value = String.valueOf(binary);
/* 132 */         style = DumperOptions.ScalarStyle.LITERAL;
/*     */       } 
/*     */ 
/*     */       
/* 136 */       if (SafeRepresenter.this.defaultScalarStyle == DumperOptions.ScalarStyle.PLAIN && SafeRepresenter
/* 137 */         .MULTILINE_PATTERN.matcher(value).find()) {
/* 138 */         style = DumperOptions.ScalarStyle.LITERAL;
/*     */       }
/* 140 */       return SafeRepresenter.this.representScalar(tag, value, style);
/*     */     }
/*     */   }
/*     */   
/*     */   protected class RepresentBoolean
/*     */     implements Represent {
/*     */     public Node representData(Object data) {
/*     */       String value;
/* 148 */       if (Boolean.TRUE.equals(data)) {
/* 149 */         value = "true";
/*     */       } else {
/* 151 */         value = "false";
/*     */       } 
/* 153 */       return SafeRepresenter.this.representScalar(Tag.BOOL, value);
/*     */     }
/*     */   }
/*     */   
/*     */   protected class RepresentNumber
/*     */     implements Represent {
/*     */     public Node representData(Object data) {
/*     */       Tag tag;
/*     */       String value;
/* 162 */       if (data instanceof Byte || data instanceof Short || data instanceof Integer || data instanceof Long || data instanceof java.math.BigInteger) {
/*     */         
/* 164 */         tag = Tag.INT;
/* 165 */         value = data.toString();
/*     */       } else {
/* 167 */         Number number = (Number)data;
/* 168 */         tag = Tag.FLOAT;
/* 169 */         if (number.equals(Double.valueOf(Double.NaN))) {
/* 170 */           value = ".NaN";
/* 171 */         } else if (number.equals(Double.valueOf(Double.POSITIVE_INFINITY))) {
/* 172 */           value = ".inf";
/* 173 */         } else if (number.equals(Double.valueOf(Double.NEGATIVE_INFINITY))) {
/* 174 */           value = "-.inf";
/*     */         } else {
/* 176 */           value = number.toString();
/*     */         } 
/*     */       } 
/* 179 */       return SafeRepresenter.this.representScalar(SafeRepresenter.this.getTag(data.getClass(), tag), value);
/*     */     }
/*     */   }
/*     */   
/*     */   protected class RepresentList
/*     */     implements Represent
/*     */   {
/*     */     public Node representData(Object data) {
/* 187 */       return SafeRepresenter.this.representSequence(SafeRepresenter.this.getTag(data.getClass(), Tag.SEQ), (List)data, DumperOptions.FlowStyle.AUTO);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected class RepresentIterator
/*     */     implements Represent
/*     */   {
/*     */     public Node representData(Object data) {
/* 196 */       Iterator<Object> iter = (Iterator<Object>)data;
/* 197 */       return SafeRepresenter.this.representSequence(SafeRepresenter.this.getTag(data.getClass(), Tag.SEQ), new SafeRepresenter.IteratorWrapper(iter), DumperOptions.FlowStyle.AUTO);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class IteratorWrapper
/*     */     implements Iterable<Object>
/*     */   {
/*     */     private final Iterator<Object> iter;
/*     */     
/*     */     public IteratorWrapper(Iterator<Object> iter) {
/* 207 */       this.iter = iter;
/*     */     }
/*     */     
/*     */     public Iterator<Object> iterator() {
/* 211 */       return this.iter;
/*     */     }
/*     */   }
/*     */   
/*     */   protected class RepresentArray
/*     */     implements Represent {
/*     */     public Node representData(Object data) {
/* 218 */       Object[] array = (Object[])data;
/* 219 */       List<Object> list = Arrays.asList(array);
/* 220 */       return SafeRepresenter.this.representSequence(Tag.SEQ, list, DumperOptions.FlowStyle.AUTO);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected class RepresentPrimitiveArray
/*     */     implements Represent
/*     */   {
/*     */     public Node representData(Object data) {
/* 231 */       Class<?> type = data.getClass().getComponentType();
/*     */       
/* 233 */       if (byte.class == type)
/* 234 */         return SafeRepresenter.this.representSequence(Tag.SEQ, asByteList(data), DumperOptions.FlowStyle.AUTO); 
/* 235 */       if (short.class == type)
/* 236 */         return SafeRepresenter.this.representSequence(Tag.SEQ, asShortList(data), DumperOptions.FlowStyle.AUTO); 
/* 237 */       if (int.class == type)
/* 238 */         return SafeRepresenter.this.representSequence(Tag.SEQ, asIntList(data), DumperOptions.FlowStyle.AUTO); 
/* 239 */       if (long.class == type)
/* 240 */         return SafeRepresenter.this.representSequence(Tag.SEQ, asLongList(data), DumperOptions.FlowStyle.AUTO); 
/* 241 */       if (float.class == type)
/* 242 */         return SafeRepresenter.this.representSequence(Tag.SEQ, asFloatList(data), DumperOptions.FlowStyle.AUTO); 
/* 243 */       if (double.class == type)
/* 244 */         return SafeRepresenter.this.representSequence(Tag.SEQ, asDoubleList(data), DumperOptions.FlowStyle.AUTO); 
/* 245 */       if (char.class == type)
/* 246 */         return SafeRepresenter.this.representSequence(Tag.SEQ, asCharList(data), DumperOptions.FlowStyle.AUTO); 
/* 247 */       if (boolean.class == type) {
/* 248 */         return SafeRepresenter.this.representSequence(Tag.SEQ, asBooleanList(data), DumperOptions.FlowStyle.AUTO);
/*     */       }
/*     */       
/* 251 */       throw new YAMLException("Unexpected primitive '" + type.getCanonicalName() + "'");
/*     */     }
/*     */     
/*     */     private List<Byte> asByteList(Object in) {
/* 255 */       byte[] array = (byte[])in;
/* 256 */       List<Byte> list = new ArrayList<>(array.length);
/* 257 */       for (int i = 0; i < array.length; i++) {
/* 258 */         list.add(Byte.valueOf(array[i]));
/*     */       }
/* 260 */       return list;
/*     */     }
/*     */     
/*     */     private List<Short> asShortList(Object in) {
/* 264 */       short[] array = (short[])in;
/* 265 */       List<Short> list = new ArrayList<>(array.length);
/* 266 */       for (int i = 0; i < array.length; i++) {
/* 267 */         list.add(Short.valueOf(array[i]));
/*     */       }
/* 269 */       return list;
/*     */     }
/*     */     
/*     */     private List<Integer> asIntList(Object in) {
/* 273 */       int[] array = (int[])in;
/* 274 */       List<Integer> list = new ArrayList<>(array.length);
/* 275 */       for (int i = 0; i < array.length; i++) {
/* 276 */         list.add(Integer.valueOf(array[i]));
/*     */       }
/* 278 */       return list;
/*     */     }
/*     */     
/*     */     private List<Long> asLongList(Object in) {
/* 282 */       long[] array = (long[])in;
/* 283 */       List<Long> list = new ArrayList<>(array.length);
/* 284 */       for (int i = 0; i < array.length; i++) {
/* 285 */         list.add(Long.valueOf(array[i]));
/*     */       }
/* 287 */       return list;
/*     */     }
/*     */     
/*     */     private List<Float> asFloatList(Object in) {
/* 291 */       float[] array = (float[])in;
/* 292 */       List<Float> list = new ArrayList<>(array.length);
/* 293 */       for (int i = 0; i < array.length; i++) {
/* 294 */         list.add(Float.valueOf(array[i]));
/*     */       }
/* 296 */       return list;
/*     */     }
/*     */     
/*     */     private List<Double> asDoubleList(Object in) {
/* 300 */       double[] array = (double[])in;
/* 301 */       List<Double> list = new ArrayList<>(array.length);
/* 302 */       for (int i = 0; i < array.length; i++) {
/* 303 */         list.add(Double.valueOf(array[i]));
/*     */       }
/* 305 */       return list;
/*     */     }
/*     */     
/*     */     private List<Character> asCharList(Object in) {
/* 309 */       char[] array = (char[])in;
/* 310 */       List<Character> list = new ArrayList<>(array.length);
/* 311 */       for (int i = 0; i < array.length; i++) {
/* 312 */         list.add(Character.valueOf(array[i]));
/*     */       }
/* 314 */       return list;
/*     */     }
/*     */     
/*     */     private List<Boolean> asBooleanList(Object in) {
/* 318 */       boolean[] array = (boolean[])in;
/* 319 */       List<Boolean> list = new ArrayList<>(array.length);
/* 320 */       for (int i = 0; i < array.length; i++) {
/* 321 */         list.add(Boolean.valueOf(array[i]));
/*     */       }
/* 323 */       return list;
/*     */     }
/*     */   }
/*     */   
/*     */   protected class RepresentMap
/*     */     implements Represent
/*     */   {
/*     */     public Node representData(Object data) {
/* 331 */       return SafeRepresenter.this.representMapping(SafeRepresenter.this.getTag(data.getClass(), Tag.MAP), (Map<?, ?>)data, DumperOptions.FlowStyle.AUTO);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected class RepresentSet
/*     */     implements Represent
/*     */   {
/*     */     public Node representData(Object data) {
/* 340 */       Map<Object, Object> value = new LinkedHashMap<>();
/* 341 */       Set<Object> set = (Set<Object>)data;
/* 342 */       for (Object key : set) {
/* 343 */         value.put(key, null);
/*     */       }
/* 345 */       return SafeRepresenter.this.representMapping(SafeRepresenter.this.getTag(data.getClass(), Tag.SET), value, DumperOptions.FlowStyle.AUTO);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected class RepresentDate
/*     */     implements Represent
/*     */   {
/*     */     public Node representData(Object data) {
/*     */       Calendar calendar;
/* 355 */       if (data instanceof Calendar) {
/* 356 */         calendar = (Calendar)data;
/*     */       } else {
/*     */         
/* 359 */         calendar = Calendar.getInstance((SafeRepresenter.this.getTimeZone() == null) ? TimeZone.getTimeZone("UTC") : SafeRepresenter.this.timeZone);
/* 360 */         calendar.setTime((Date)data);
/*     */       } 
/* 362 */       int years = calendar.get(1);
/* 363 */       int months = calendar.get(2) + 1;
/* 364 */       int days = calendar.get(5);
/* 365 */       int hour24 = calendar.get(11);
/* 366 */       int minutes = calendar.get(12);
/* 367 */       int seconds = calendar.get(13);
/* 368 */       int millis = calendar.get(14);
/* 369 */       StringBuilder buffer = new StringBuilder(String.valueOf(years));
/* 370 */       while (buffer.length() < 4)
/*     */       {
/* 372 */         buffer.insert(0, "0");
/*     */       }
/* 374 */       buffer.append("-");
/* 375 */       if (months < 10) {
/* 376 */         buffer.append("0");
/*     */       }
/* 378 */       buffer.append(months);
/* 379 */       buffer.append("-");
/* 380 */       if (days < 10) {
/* 381 */         buffer.append("0");
/*     */       }
/* 383 */       buffer.append(days);
/* 384 */       buffer.append("T");
/* 385 */       if (hour24 < 10) {
/* 386 */         buffer.append("0");
/*     */       }
/* 388 */       buffer.append(hour24);
/* 389 */       buffer.append(":");
/* 390 */       if (minutes < 10) {
/* 391 */         buffer.append("0");
/*     */       }
/* 393 */       buffer.append(minutes);
/* 394 */       buffer.append(":");
/* 395 */       if (seconds < 10) {
/* 396 */         buffer.append("0");
/*     */       }
/* 398 */       buffer.append(seconds);
/* 399 */       if (millis > 0) {
/* 400 */         if (millis < 10) {
/* 401 */           buffer.append(".00");
/* 402 */         } else if (millis < 100) {
/* 403 */           buffer.append(".0");
/*     */         } else {
/* 405 */           buffer.append(".");
/*     */         } 
/* 407 */         buffer.append(millis);
/*     */       } 
/*     */ 
/*     */       
/* 411 */       int gmtOffset = calendar.getTimeZone().getOffset(calendar.getTime().getTime());
/* 412 */       if (gmtOffset == 0) {
/* 413 */         buffer.append('Z');
/*     */       } else {
/* 415 */         if (gmtOffset < 0) {
/* 416 */           buffer.append('-');
/* 417 */           gmtOffset *= -1;
/*     */         } else {
/* 419 */           buffer.append('+');
/*     */         } 
/* 421 */         int minutesOffset = gmtOffset / 60000;
/* 422 */         int hoursOffset = minutesOffset / 60;
/* 423 */         int partOfHour = minutesOffset % 60;
/*     */         
/* 425 */         if (hoursOffset < 10) {
/* 426 */           buffer.append('0');
/*     */         }
/* 428 */         buffer.append(hoursOffset);
/* 429 */         buffer.append(':');
/* 430 */         if (partOfHour < 10) {
/* 431 */           buffer.append('0');
/*     */         }
/* 433 */         buffer.append(partOfHour);
/*     */       } 
/*     */       
/* 436 */       return SafeRepresenter.this.representScalar(SafeRepresenter.this.getTag(data.getClass(), Tag.TIMESTAMP), buffer.toString(), DumperOptions.ScalarStyle.PLAIN);
/*     */     }
/*     */   }
/*     */   
/*     */   protected class RepresentEnum
/*     */     implements Represent
/*     */   {
/*     */     public Node representData(Object data) {
/* 444 */       Tag tag = new Tag(data.getClass());
/* 445 */       return SafeRepresenter.this.representScalar(SafeRepresenter.this.getTag(data.getClass(), tag), ((Enum)data).name());
/*     */     }
/*     */   }
/*     */   
/*     */   protected class RepresentByteArray
/*     */     implements Represent {
/*     */     public Node representData(Object data) {
/* 452 */       char[] binary = Base64Coder.encode((byte[])data);
/* 453 */       return SafeRepresenter.this.representScalar(Tag.BINARY, String.valueOf(binary), DumperOptions.ScalarStyle.LITERAL);
/*     */     }
/*     */   }
/*     */   
/*     */   public TimeZone getTimeZone() {
/* 458 */     return this.timeZone;
/*     */   }
/*     */   
/*     */   public void setTimeZone(TimeZone timeZone) {
/* 462 */     this.timeZone = timeZone;
/*     */   }
/*     */   
/*     */   protected class RepresentUuid
/*     */     implements Represent {
/*     */     public Node representData(Object data) {
/* 468 */       return SafeRepresenter.this.representScalar(SafeRepresenter.this.getTag(data.getClass(), new Tag(UUID.class)), data.toString());
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\representer\SafeRepresenter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */