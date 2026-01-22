/*    */ package ac.grim.grimac.utils.webhook;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import com.google.gson.JsonElement;
/*    */ import com.google.gson.JsonObject;
/*    */ import java.util.function.Function;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public class WebhookMessage implements JsonSerializable {
/*    */   public static final int MAX_CONTENT_LENGTH = 2000;
/*    */   public static final int MAX_EMBEDS = 10;
/*    */   @Nullable
/*    */   private String content;
/*    */   
/*    */   @Generated
/* 18 */   public WebhookMessage username(@Nullable String username) { this.username = username; return this; } @Nullable private String username; @Nullable private String avatar; @Nullable private Boolean tts; @NotNull private Embed[] embeds; @Generated public WebhookMessage avatar(@Nullable String avatar) { this.avatar = avatar; return this; } @Generated public WebhookMessage tts(@Nullable Boolean tts) { this.tts = tts; return this; }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   @Generated
/*    */   public String content() {
/* 24 */     return this.content; } @Nullable @Generated
/* 25 */   public String username() { return this.username; } @Nullable @Generated
/* 26 */   public String avatar() { return this.avatar; } @Nullable @Generated
/* 27 */   public Boolean tts() { return this.tts; } @NotNull @Generated
/* 28 */   public Embed[] embeds() { return this.embeds; }
/*    */   
/*    */   public WebhookMessage() {}
/*    */   
/*    */   public WebhookMessage(@NotNull JsonObject json) {
/*    */     JsonElement element;
/* 34 */     if ((element = json.get("content")) != null) content(element.getAsString()); 
/* 35 */     if ((element = json.get("username")) != null) username(element.getAsString()); 
/* 36 */     if ((element = json.get("avatar_url")) != null) avatar(element.getAsString()); 
/* 37 */     if ((element = json.get("tts")) != null) tts(Boolean.valueOf(element.getAsBoolean())); 
/* 38 */     if ((element = json.get("embeds")) != null) embeds(JsonSerializable.<Embed>deserializeArray(element.getAsJsonArray(), x$0 -> new Embed[x$0], Embed::new)); 
/*    */   }
/*    */   @Contract(value = "_ -> this", mutates = "this")
/*    */   @NotNull
/*    */   public WebhookMessage content(@Nullable String content) {
/* 43 */     if (content != null && content.length() > 2000) {
/* 44 */       throw new IllegalArgumentException("Webhook content too long, " + content.length() + " > 2000");
/*    */     }
/*    */     
/* 47 */     this.content = content;
/* 48 */     return this;
/*    */   }
/*    */   @Contract(value = "_ -> this", mutates = "this")
/*    */   @NotNull
/*    */   public WebhookMessage embeds(@NotNull Embed[] embeds) {
/* 53 */     if (embeds != null) {
/* 54 */       if (embeds.length > 10) {
/* 55 */         throw new IllegalArgumentException("Too many embeds, " + embeds.length + " > 10");
/*    */       }
/*    */       
/* 58 */       for (Embed embed : embeds) {
/* 59 */         Objects.requireNonNull(embed);
/*    */       }
/*    */     } 
/*    */     
/* 63 */     this.embeds = embeds;
/* 64 */     return this;
/*    */   }
/*    */   @Contract(value = "_ -> this", mutates = "this")
/*    */   @NotNull
/*    */   public WebhookMessage addEmbeds(@NotNull Embed... embeds) {
/* 69 */     if (embeds.length == 0) return this; 
/* 70 */     if (embeds() == null) return embeds(embeds);
/*    */     
/* 72 */     Embed[] newEmbeds = new Embed[(embeds()).length + embeds.length];
/*    */     
/* 74 */     System.arraycopy(embeds(), 0, newEmbeds, 0, (embeds()).length);
/* 75 */     System.arraycopy(embeds, (embeds()).length, newEmbeds, (embeds()).length, embeds.length);
/*    */     
/* 77 */     return embeds(newEmbeds);
/*    */   }
/*    */   @NotNull
/*    */   public JsonObject toJson() {
/* 81 */     JsonObject json = new JsonObject();
/* 82 */     if (content() != null) json.addProperty("content", content()); 
/* 83 */     if (username() != null) json.addProperty("username", username()); 
/* 84 */     if (avatar() != null) json.addProperty("avatar_url", avatar()); 
/* 85 */     if (tts() != null) json.addProperty("tts", tts()); 
/* 86 */     if (embeds() != null) json.add("embeds", (JsonElement)JsonSerializable.serializeArray((JsonSerializable[])embeds())); 
/* 87 */     return json;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\webhook\WebhookMessage.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */