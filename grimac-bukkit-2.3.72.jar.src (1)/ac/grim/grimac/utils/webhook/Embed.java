/*     */ package ac.grim.grimac.utils.webhook;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonObject;
/*     */ import lombok.Generated;
/*     */ 
/*     */ public class Embed implements JsonSerializable {
/*     */   public static final int MAX_TITLE_LENGTH = 256;
/*     */   public static final int MAX_DESCRIPTION_LENGTH = 4096;
/*     */   public static final int MAX_FIELDS = 25;
/*     */   @Nullable
/*     */   private String title;
/*     */   @NotNull
/*     */   private String description;
/*     */   @Nullable
/*     */   private String titleURL;
/*     */   
/*     */   @Generated
/*  20 */   public Embed titleURL(@Nullable String titleURL) { this.titleURL = titleURL; return this; } @Nullable private Instant timestamp; @Nullable private Integer color; @Nullable private EmbedFooter footer; @Nullable private String imageURL; @Nullable private String thumbnailURL; @Nullable private EmbedAuthor author; @NotNull private EmbedField[] fields; @Generated public Embed timestamp(@Nullable Instant timestamp) { this.timestamp = timestamp; return this; } @Generated public Embed color(@Nullable Integer color) { this.color = color; return this; } @Generated public Embed footer(@Nullable EmbedFooter footer) { this.footer = footer; return this; } @Generated public Embed imageURL(@Nullable String imageURL) { this.imageURL = imageURL; return this; } @Generated public Embed thumbnailURL(@Nullable String thumbnailURL) { this.thumbnailURL = thumbnailURL; return this; } @Generated public Embed author(@Nullable EmbedAuthor author) { this.author = author; return this; }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   @Generated
/*     */   public String title()
/*     */   {
/*  27 */     return this.title; } @NotNull @Generated
/*  28 */   public String description() { return this.description; } @Nullable @Generated
/*  29 */   public String titleURL() { return this.titleURL; } @Nullable @Generated
/*  30 */   public Instant timestamp() { return this.timestamp; } @Nullable @Generated
/*  31 */   public Integer color() { return this.color; } @Nullable @Generated
/*  32 */   public EmbedFooter footer() { return this.footer; } @Nullable @Generated
/*  33 */   public String imageURL() { return this.imageURL; } @Nullable @Generated
/*  34 */   public String thumbnailURL() { return this.thumbnailURL; } @Nullable @Generated
/*  35 */   public EmbedAuthor author() { return this.author; } @NotNull @Generated
/*  36 */   public EmbedField[] fields() { return this.fields; }
/*     */   
/*     */   public Embed(@NotNull String description) {
/*  39 */     description(description);
/*     */   }
/*     */   
/*     */   public Embed(@NotNull JsonElement jsonElement) {
/*  43 */     JsonObject json = jsonElement.getAsJsonObject();
/*  44 */     description(json.get("description").getAsString());
/*     */     
/*     */     JsonElement element;
/*  47 */     if ((element = json.get("title")) != null) title(element.getAsString()); 
/*  48 */     if ((element = json.get("url")) != null) titleURL(element.getAsString()); 
/*  49 */     if ((element = json.get("timestamp")) != null) timestamp(Instant.parse(element.getAsString())); 
/*  50 */     if ((element = json.get("color")) != null) color(Integer.valueOf(element.getAsInt())); 
/*  51 */     if ((element = json.get("footer")) != null) footer(new EmbedFooter(element)); 
/*  52 */     if ((element = json.get("image")) != null) imageURL(element.getAsJsonObject().get("url").getAsString()); 
/*  53 */     if ((element = json.get("thumbnail")) != null) imageURL(element.getAsJsonObject().get("url").getAsString()); 
/*  54 */     if ((element = json.get("author")) != null) author(new EmbedAuthor(element)); 
/*  55 */     if ((element = json.get("fields")) != null) fields(JsonSerializable.<EmbedField>deserializeArray(element.getAsJsonArray(), x$0 -> new EmbedField[x$0], EmbedField::new)); 
/*     */   }
/*     */   @Contract(value = "_ -> this", mutates = "this")
/*     */   @NotNull
/*     */   public Embed description(@NotNull String description) {
/*  60 */     Objects.requireNonNull(description, "Embed description cannot be null!");
/*  61 */     if (description.length() > 4096) {
/*  62 */       throw new IllegalArgumentException("Embed description too long, " + description.length() + " > 4096");
/*     */     }
/*     */     
/*  65 */     this.description = description;
/*  66 */     return this;
/*     */   }
/*     */   @Contract(value = "_ -> this", mutates = "this")
/*     */   @NotNull
/*     */   public Embed title(@Nullable String title) {
/*  71 */     if (title != null && title.length() > 256) {
/*  72 */       throw new IllegalArgumentException("Embed title too long, " + title.length() + " > 256");
/*     */     }
/*     */     
/*  75 */     this.title = title;
/*  76 */     return this;
/*     */   }
/*     */   @Contract(value = "_ -> this", mutates = "this")
/*     */   @NotNull
/*     */   public Embed fields(@NotNull EmbedField[] fields) {
/*  81 */     if (fields != null) {
/*  82 */       if (fields.length > 25) {
/*  83 */         throw new IllegalArgumentException("Too many fields, " + fields.length + " > 25");
/*     */       }
/*     */       
/*  86 */       for (EmbedField field : fields) {
/*  87 */         Objects.requireNonNull(field);
/*     */       }
/*     */     } 
/*     */     
/*  91 */     this.fields = fields;
/*  92 */     return this;
/*     */   }
/*     */   @Contract(value = "_ -> this", mutates = "this")
/*     */   @NotNull
/*     */   public Embed addFields(@NotNull EmbedField... fields) {
/*  97 */     if (fields.length == 0) return this; 
/*  98 */     if (fields() == null) return fields(fields);
/*     */     
/* 100 */     EmbedField[] newFields = new EmbedField[(fields()).length + fields.length];
/*     */     
/* 102 */     System.arraycopy(fields(), 0, newFields, 0, (fields()).length);
/* 103 */     System.arraycopy(fields, (fields()).length, newFields, (fields()).length, fields.length);
/*     */     
/* 105 */     return fields(newFields);
/*     */   }
/*     */   @NotNull
/*     */   public JsonObject toJson() {
/* 109 */     JsonObject json = new JsonObject();
/* 110 */     json.addProperty("description", description());
/* 111 */     if (title() != null) json.addProperty("title", title()); 
/* 112 */     if (color() != null) json.addProperty("color", Integer.valueOf(color().intValue() & 0xFFFFFF)); 
/* 113 */     if (titleURL() != null) json.addProperty("url", titleURL()); 
/* 114 */     if (timestamp() != null) json.addProperty("timestamp", timestamp().toString()); 
/* 115 */     if (footer() != null) json.add("footer", footer().toJson()); 
/* 116 */     if (imageURL() != null) {
/* 117 */       JsonObject image = new JsonObject();
/* 118 */       image.addProperty("url", imageURL());
/* 119 */       json.add("image", (JsonElement)image);
/*     */     } 
/* 121 */     if (thumbnailURL() != null) {
/* 122 */       JsonObject thumbnail = new JsonObject();
/* 123 */       thumbnail.addProperty("url", thumbnailURL());
/* 124 */       json.add("thumbnail", (JsonElement)thumbnail);
/*     */     } 
/* 126 */     if (author() != null) json.add("author", author().toJson()); 
/* 127 */     if (fields() != null) json.add("fields", (JsonElement)JsonSerializable.serializeArray((JsonSerializable[])fields())); 
/* 128 */     return json;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\webhook\Embed.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */