/*    */ package ac.grim.grimac.utils.webhook;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import com.google.gson.JsonElement;
/*    */ import com.google.gson.JsonObject;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public class EmbedAuthor implements JsonSerializable {
/*    */   public static final int MAX_NAME_LENGTH = 256;
/*    */   @NotNull
/*    */   private String name;
/*    */   
/*    */   @Generated
/* 15 */   public EmbedAuthor url(@Nullable String url) { this.url = url; return this; } @Nullable private String url; @Nullable private String icon; @Generated public EmbedAuthor icon(@Nullable String icon) { this.icon = icon; return this; }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   @Generated
/* 20 */   public String name() { return this.name; } @Nullable @Generated
/* 21 */   public String url() { return this.url; } @Nullable @Generated
/* 22 */   public String icon() { return this.icon; }
/*    */   
/*    */   public EmbedAuthor(@NotNull String name) {
/* 25 */     this(name, null, null);
/*    */   }
/*    */   
/*    */   public EmbedAuthor(@NotNull String name, @Nullable String url, @Nullable String icon) {
/* 29 */     name(name);
/* 30 */     url(url);
/* 31 */     icon(icon);
/*    */   }
/*    */   
/*    */   public EmbedAuthor(@NotNull JsonElement jsonElement) {
/* 35 */     JsonObject json = jsonElement.getAsJsonObject();
/* 36 */     name(json.get("name").getAsString());
/*    */     
/*    */     JsonElement element;
/* 39 */     if ((element = json.get("url")) != null) url(element.getAsString()); 
/* 40 */     if ((element = json.get("icon_url")) != null) icon(element.getAsString()); 
/*    */   }
/*    */   @Contract(value = "_ -> this", mutates = "this")
/*    */   @NotNull
/*    */   public EmbedAuthor name(@NotNull String name) {
/* 45 */     Objects.requireNonNull(name, "Embed author name cannot be null!");
/* 46 */     if (name.length() > 256) {
/* 47 */       throw new IllegalArgumentException("Embed author name too long, " + name.length() + " > 256");
/*    */     }
/*    */     
/* 50 */     this.name = name;
/* 51 */     return this;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public JsonElement toJson() {
/* 56 */     JsonObject json = new JsonObject();
/* 57 */     json.addProperty("name", name());
/* 58 */     if (url() != null) json.addProperty("url", url()); 
/* 59 */     if (icon() != null) json.addProperty("icon_url", icon()); 
/* 60 */     return (JsonElement)json;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\webhook\EmbedAuthor.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */