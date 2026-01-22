/*    */ package ac.grim.grimac.utils.webhook;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import com.google.gson.JsonElement;
/*    */ import com.google.gson.JsonObject;
/*    */ import java.util.Objects;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public class EmbedField implements JsonSerializable {
/*    */   public static final int MAX_NAME_LENGTH = 256;
/*    */   public static final int MAX_VALUE_LENGTH = 1024;
/*    */   
/*    */   @Generated
/*    */   public EmbedField inline(boolean inline) {
/* 14 */     this.inline = inline; return this;
/*    */   } @NotNull
/*    */   private String name; @NotNull
/*    */   private String value; private boolean inline; @NotNull
/*    */   @Generated
/*    */   public String name() {
/* 20 */     return this.name; } @NotNull @Generated
/* 21 */   public String value() { return this.value; } @Generated
/* 22 */   public boolean inline() { return this.inline; }
/*    */   
/*    */   public EmbedField(@NotNull String name, @NotNull String value) {
/* 25 */     this(name, value, false);
/*    */   }
/*    */   
/*    */   public EmbedField(@NotNull String name, @NotNull String value, boolean inline) {
/* 29 */     name(name);
/* 30 */     value(value);
/* 31 */     inline(inline);
/*    */   }
/*    */   
/*    */   public EmbedField(@NotNull JsonElement jsonElement) {
/* 35 */     JsonObject json = jsonElement.getAsJsonObject();
/* 36 */     name(json.get("name").getAsString());
/* 37 */     value(json.get("value").getAsString());
/* 38 */     JsonElement inline = json.get("inline");
/* 39 */     if (inline != null) inline(inline.getAsBoolean()); 
/*    */   }
/*    */   @Contract(value = "_ -> this", mutates = "this")
/*    */   @NotNull
/*    */   public EmbedField name(@NotNull String name) {
/* 44 */     Objects.requireNonNull(name, "Embed field name cannot be null!");
/* 45 */     if (name.length() > 256) {
/* 46 */       throw new IllegalArgumentException("Embed field name too long, " + name.length() + " > 256");
/*    */     }
/*    */     
/* 49 */     this.name = name;
/* 50 */     return this;
/*    */   }
/*    */   @Contract(value = "_ -> this", mutates = "this")
/*    */   @NotNull
/*    */   public EmbedField value(@NotNull String value) {
/* 55 */     Objects.requireNonNull(value, "Embed field value cannot be null!");
/* 56 */     if (value.length() > 1024) {
/* 57 */       throw new IllegalArgumentException("Embed field value too long, " + value.length() + " > 1024");
/*    */     }
/*    */     
/* 60 */     this.value = value;
/* 61 */     return this;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public JsonElement toJson() {
/* 66 */     JsonObject json = new JsonObject();
/* 67 */     json.addProperty("name", name());
/* 68 */     json.addProperty("value", value());
/* 69 */     json.addProperty("inline", Boolean.valueOf(inline()));
/* 70 */     return (JsonElement)json;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\webhook\EmbedField.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */