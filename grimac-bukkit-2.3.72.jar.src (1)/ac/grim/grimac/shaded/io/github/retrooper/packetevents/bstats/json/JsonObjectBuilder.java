/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.bstats.json;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.stream.Collectors;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JsonObjectBuilder
/*     */ {
/*  14 */   private StringBuilder builder = new StringBuilder();
/*     */   private boolean hasAtLeastOneField = false;
/*     */   
/*     */   public JsonObjectBuilder() {
/*  18 */     this.builder.append("{");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonObjectBuilder appendNull(String key) {
/*  28 */     appendFieldUnescaped(key, "null");
/*  29 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonObjectBuilder appendField(String key, String value) {
/*  40 */     if (value == null) {
/*  41 */       throw new IllegalArgumentException("JSON value must not be null");
/*     */     }
/*  43 */     appendFieldUnescaped(key, "\"" + escape(value) + "\"");
/*  44 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonObjectBuilder appendField(String key, int value) {
/*  55 */     appendFieldUnescaped(key, String.valueOf(value));
/*  56 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonObjectBuilder appendField(String key, JsonObject object) {
/*  67 */     if (object == null) {
/*  68 */       throw new IllegalArgumentException("JSON object must not be null");
/*     */     }
/*  70 */     appendFieldUnescaped(key, object.toString());
/*  71 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonObjectBuilder appendField(String key, String[] values) {
/*  82 */     if (values == null) {
/*  83 */       throw new IllegalArgumentException("JSON values must not be null");
/*     */     }
/*     */ 
/*     */     
/*  87 */     String escapedValues = Arrays.<String>stream(values).map(value -> "\"" + escape(value) + "\"").collect(Collectors.joining(","));
/*  88 */     appendFieldUnescaped(key, "[" + escapedValues + "]");
/*  89 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonObjectBuilder appendField(String key, int[] values) {
/* 100 */     if (values == null) {
/* 101 */       throw new IllegalArgumentException("JSON values must not be null");
/*     */     }
/*     */ 
/*     */     
/* 105 */     String escapedValues = Arrays.stream(values).<CharSequence>mapToObj(String::valueOf).collect(Collectors.joining(","));
/* 106 */     appendFieldUnescaped(key, "[" + escapedValues + "]");
/* 107 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonObjectBuilder appendField(String key, JsonObject[] values) {
/* 118 */     if (values == null) {
/* 119 */       throw new IllegalArgumentException("JSON values must not be null");
/*     */     }
/*     */ 
/*     */     
/* 123 */     String escapedValues = Arrays.<JsonObject>stream(values).map(JsonObject::toString).collect(Collectors.joining(","));
/* 124 */     appendFieldUnescaped(key, "[" + escapedValues + "]");
/* 125 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void appendFieldUnescaped(String key, String escapedValue) {
/* 135 */     if (this.builder == null) {
/* 136 */       throw new IllegalStateException("JSON has already been built");
/*     */     }
/* 138 */     if (key == null) {
/* 139 */       throw new IllegalArgumentException("JSON key must not be null");
/*     */     }
/* 141 */     if (this.hasAtLeastOneField) {
/* 142 */       this.builder.append(",");
/*     */     }
/* 144 */     this.builder.append("\"").append(escape(key)).append("\":").append(escapedValue);
/*     */     
/* 146 */     this.hasAtLeastOneField = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonObject build() {
/* 155 */     if (this.builder == null) {
/* 156 */       throw new IllegalStateException("JSON has already been built");
/*     */     }
/* 158 */     JsonObject object = new JsonObject(this.builder.append("}").toString());
/* 159 */     this.builder = null;
/* 160 */     return object;
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
/*     */   private static String escape(String value) {
/* 173 */     StringBuilder builder = new StringBuilder();
/* 174 */     for (int i = 0; i < value.length(); i++) {
/* 175 */       char c = value.charAt(i);
/* 176 */       if (c == '"') {
/* 177 */         builder.append("\\\"");
/* 178 */       } else if (c == '\\') {
/* 179 */         builder.append("\\\\");
/* 180 */       } else if (c <= '\017') {
/* 181 */         builder.append("\\u000").append(Integer.toHexString(c));
/* 182 */       } else if (c <= '\037') {
/* 183 */         builder.append("\\u00").append(Integer.toHexString(c));
/*     */       } else {
/* 185 */         builder.append(c);
/*     */       } 
/*     */     } 
/* 188 */     return builder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class JsonObject
/*     */   {
/*     */     private final String value;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private JsonObject(String value) {
/* 202 */       this.value = value;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 207 */       return this.value;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\bstats\json\JsonObjectBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */