/*     */ package ac.grim.grimac.shaded.snakeyaml.nodes;
/*     */ 
/*     */ import ac.grim.grimac.shaded.snakeyaml.error.YAMLException;
/*     */ import ac.grim.grimac.shaded.snakeyaml.util.UriEncoder;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
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
/*     */ public final class Tag
/*     */ {
/*     */   public static final String PREFIX = "tag:yaml.org,2002:";
/*  29 */   public static final Tag YAML = new Tag("tag:yaml.org,2002:yaml");
/*  30 */   public static final Tag MERGE = new Tag("tag:yaml.org,2002:merge");
/*  31 */   public static final Tag SET = new Tag("tag:yaml.org,2002:set");
/*  32 */   public static final Tag PAIRS = new Tag("tag:yaml.org,2002:pairs");
/*  33 */   public static final Tag OMAP = new Tag("tag:yaml.org,2002:omap");
/*  34 */   public static final Tag BINARY = new Tag("tag:yaml.org,2002:binary");
/*  35 */   public static final Tag INT = new Tag("tag:yaml.org,2002:int");
/*  36 */   public static final Tag FLOAT = new Tag("tag:yaml.org,2002:float");
/*  37 */   public static final Tag TIMESTAMP = new Tag("tag:yaml.org,2002:timestamp");
/*  38 */   public static final Tag BOOL = new Tag("tag:yaml.org,2002:bool");
/*  39 */   public static final Tag NULL = new Tag("tag:yaml.org,2002:null");
/*  40 */   public static final Tag STR = new Tag("tag:yaml.org,2002:str");
/*  41 */   public static final Tag SEQ = new Tag("tag:yaml.org,2002:seq");
/*  42 */   public static final Tag MAP = new Tag("tag:yaml.org,2002:map");
/*     */ 
/*     */   
/*  45 */   public static final Set<Tag> standardTags = new HashSet<>(15);
/*     */   
/*     */   static {
/*  48 */     standardTags.add(YAML);
/*  49 */     standardTags.add(MERGE);
/*  50 */     standardTags.add(SET);
/*  51 */     standardTags.add(PAIRS);
/*  52 */     standardTags.add(OMAP);
/*  53 */     standardTags.add(BINARY);
/*  54 */     standardTags.add(INT);
/*  55 */     standardTags.add(FLOAT);
/*  56 */     standardTags.add(TIMESTAMP);
/*  57 */     standardTags.add(BOOL);
/*  58 */     standardTags.add(NULL);
/*  59 */     standardTags.add(STR);
/*  60 */     standardTags.add(SEQ);
/*  61 */     standardTags.add(MAP);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  66 */   public static final Tag COMMENT = new Tag("tag:yaml.org,2002:comment");
/*     */ 
/*     */ 
/*     */   
/*  70 */   private static final Map<Tag, Set<Class<?>>> COMPATIBILITY_MAP = new HashMap<>(); static {
/*  71 */     Set<Class<?>> floatSet = new HashSet<>();
/*  72 */     floatSet.add(Double.class);
/*  73 */     floatSet.add(Float.class);
/*  74 */     floatSet.add(BigDecimal.class);
/*  75 */     COMPATIBILITY_MAP.put(FLOAT, floatSet);
/*     */     
/*  77 */     Set<Class<?>> intSet = new HashSet<>();
/*  78 */     intSet.add(Integer.class);
/*  79 */     intSet.add(Long.class);
/*  80 */     intSet.add(BigInteger.class);
/*  81 */     COMPATIBILITY_MAP.put(INT, intSet);
/*     */     
/*  83 */     Set<Class<?>> timestampSet = new HashSet<>();
/*  84 */     timestampSet.add(Date.class);
/*     */ 
/*     */     
/*     */     try {
/*  88 */       timestampSet.add(Class.forName("java.sql.Date"));
/*  89 */       timestampSet.add(Class.forName("java.sql.Timestamp"));
/*  90 */     } catch (ClassNotFoundException classNotFoundException) {}
/*     */ 
/*     */ 
/*     */     
/*  94 */     COMPATIBILITY_MAP.put(TIMESTAMP, timestampSet);
/*     */   }
/*     */   
/*     */   private final String value;
/*     */   private boolean secondary = false;
/*     */   
/*     */   public Tag(String tag) {
/* 101 */     if (tag == null)
/* 102 */       throw new NullPointerException("Tag must be provided."); 
/* 103 */     if (tag.length() == 0)
/* 104 */       throw new IllegalArgumentException("Tag must not be empty."); 
/* 105 */     if (tag.trim().length() != tag.length()) {
/* 106 */       throw new IllegalArgumentException("Tag must not contain leading or trailing spaces.");
/*     */     }
/* 108 */     this.value = UriEncoder.encode(tag);
/* 109 */     this.secondary = !tag.startsWith("tag:yaml.org,2002:");
/*     */   }
/*     */   
/*     */   public Tag(Class<? extends Object> clazz) {
/* 113 */     if (clazz == null) {
/* 114 */       throw new NullPointerException("Class for tag must be provided.");
/*     */     }
/* 116 */     this.value = "tag:yaml.org,2002:" + UriEncoder.encode(clazz.getName());
/*     */   }
/*     */   
/*     */   public boolean isSecondary() {
/* 120 */     return this.secondary;
/*     */   }
/*     */   
/*     */   public String getValue() {
/* 124 */     return this.value;
/*     */   }
/*     */   
/*     */   public boolean startsWith(String prefix) {
/* 128 */     return this.value.startsWith(prefix);
/*     */   }
/*     */   
/*     */   public String getClassName() {
/* 132 */     if (this.secondary) {
/* 133 */       throw new YAMLException("Invalid tag: " + this.value);
/*     */     }
/* 135 */     return UriEncoder.decode(this.value.substring("tag:yaml.org,2002:".length()));
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 140 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 145 */     if (obj instanceof Tag) {
/* 146 */       return this.value.equals(((Tag)obj).getValue());
/*     */     }
/* 148 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 154 */     return this.value.hashCode();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCompatible(Class<?> clazz) {
/* 165 */     Set<Class<?>> set = COMPATIBILITY_MAP.get(this);
/* 166 */     if (set != null) {
/* 167 */       return set.contains(clazz);
/*     */     }
/* 169 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matches(Class<? extends Object> clazz) {
/* 180 */     return this.value.equals("tag:yaml.org,2002:" + clazz.getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCustomGlobal() {
/* 189 */     return (!this.secondary && !standardTags.contains(this));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\nodes\Tag.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */