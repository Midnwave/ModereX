/*    */ package ac.grim.grimac.utils.webhook;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import com.google.gson.JsonElement;
/*    */ import com.google.gson.JsonObject;
/*    */ import java.util.Objects;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public class EmbedFooter implements JsonSerializable {
/*    */   public static final int MAX_TEXT_LENGTH = 2048;
/*    */   
/*    */   @Generated
/* 15 */   public EmbedFooter icon(@Nullable String icon) { this.icon = icon; return this; } @NotNull
/*    */   private String text; @Nullable
/*    */   private String icon; @NotNull
/*    */   @Generated
/*    */   public String text() {
/* 20 */     return this.text; } @Nullable @Generated
/* 21 */   public String icon() { return this.icon; }
/*    */   
/*    */   public EmbedFooter(@NotNull String text) {
/* 24 */     this(text, null);
/*    */   }
/*    */   
/*    */   public EmbedFooter(@NotNull String text, @Nullable String icon) {
/* 28 */     text(text);
/* 29 */     icon(icon);
/*    */   }
/*    */   
/*    */   public EmbedFooter(@NotNull JsonElement jsonElement) {
/* 33 */     JsonObject json = jsonElement.getAsJsonObject();
/* 34 */     text(json.get("text").getAsString());
/* 35 */     JsonElement icon_url = json.get("icon_url");
/* 36 */     if (icon_url != null) icon(icon_url.getAsString()); 
/*    */   }
/*    */   @Contract(value = "_ -> this", mutates = "this")
/*    */   @NotNull
/*    */   public EmbedFooter text(@NotNull String text) {
/* 41 */     Objects.requireNonNull(text, "Embed footer text cannot be null!");
/* 42 */     if (text.length() > 2048) {
/* 43 */       throw new IllegalArgumentException("Embed footer text too long, " + text.length() + " > 2048");
/*    */     }
/*    */     
/* 46 */     this.text = text;
/* 47 */     return this;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public JsonElement toJson() {
/* 52 */     JsonObject json = new JsonObject();
/* 53 */     json.addProperty("text", text());
/* 54 */     if (icon() != null) json.addProperty("icon_url", icon()); 
/* 55 */     return (JsonElement)json;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\webhook\EmbedFooter.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */