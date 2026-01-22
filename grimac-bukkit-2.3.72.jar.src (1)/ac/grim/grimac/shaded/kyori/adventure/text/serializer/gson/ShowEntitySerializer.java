/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.gson;
/*     */ 
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.InvalidKeyException;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.json.JSONOptions;
/*     */ import ac.grim.grimac.shaded.kyori.option.OptionState;
/*     */ import com.google.gson.Gson;
/*     */ import com.google.gson.JsonParseException;
/*     */ import com.google.gson.TypeAdapter;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonToken;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class ShowEntitySerializer
/*     */   extends TypeAdapter<HoverEvent.ShowEntity>
/*     */ {
/*     */   private final Gson gson;
/*     */   private final boolean emitKeyAsTypeAndUuidAsId;
/*     */   
/*     */   static TypeAdapter<HoverEvent.ShowEntity> create(Gson gson, OptionState opt) {
/*  49 */     return (new ShowEntitySerializer(gson, ((Boolean)opt.value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_KEY_AS_TYPE_AND_UUID_AS_ID)).booleanValue())).nullSafe();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ShowEntitySerializer(Gson gson, boolean emitKeyAsTypeAndUuidAsId) {
/*  56 */     this.gson = gson;
/*  57 */     this.emitKeyAsTypeAndUuidAsId = emitKeyAsTypeAndUuidAsId;
/*     */   }
/*     */ 
/*     */   
/*     */   public HoverEvent.ShowEntity read(JsonReader in) throws IOException {
/*  62 */     in.beginObject();
/*     */     
/*  64 */     Key type = null;
/*  65 */     UUID id = null;
/*  66 */     Component name = null;
/*     */     
/*  68 */     while (in.hasNext()) {
/*  69 */       String string, fieldName = in.nextName();
/*     */       
/*  71 */       switch (fieldName) {
/*     */         case "id":
/*  73 */           if (in.peek() == JsonToken.BEGIN_ARRAY) {
/*     */             
/*  75 */             id = (UUID)this.gson.fromJson(in, UUID.class);
/*     */             continue;
/*     */           } 
/*  78 */           string = in.nextString();
/*     */ 
/*     */           
/*  81 */           if (string.contains(":")) {
/*  82 */             type = Key.key(string);
/*     */           }
/*     */ 
/*     */           
/*     */           try {
/*  87 */             id = UUID.fromString(string);
/*  88 */           } catch (IllegalArgumentException ignored) {
/*     */             try {
/*  90 */               type = Key.key(string);
/*  91 */             } catch (InvalidKeyException invalidKeyException) {}
/*     */           } 
/*     */           continue;
/*     */ 
/*     */ 
/*     */         
/*     */         case "type":
/*  98 */           type = (Key)this.gson.fromJson(in, Key.class);
/*     */           continue;
/*     */         case "uuid":
/* 101 */           id = (UUID)this.gson.fromJson(in, UUID.class);
/*     */           continue;
/*     */         case "name":
/* 104 */           name = (Component)this.gson.fromJson(in, SerializerFactory.COMPONENT_TYPE);
/*     */           continue;
/*     */       } 
/* 107 */       in.skipValue();
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 112 */     if (type == null || id == null) {
/* 113 */       throw new JsonParseException("A show entity hover event needs type and id fields to be deserialized");
/*     */     }
/* 115 */     in.endObject();
/*     */     
/* 117 */     return HoverEvent.ShowEntity.showEntity(type, id, name);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(JsonWriter out, HoverEvent.ShowEntity value) throws IOException {
/* 122 */     out.beginObject();
/*     */     
/* 124 */     out.name(this.emitKeyAsTypeAndUuidAsId ? "type" : "id");
/* 125 */     this.gson.toJson(value.type(), SerializerFactory.KEY_TYPE, out);
/*     */     
/* 127 */     out.name(this.emitKeyAsTypeAndUuidAsId ? "id" : "uuid");
/* 128 */     this.gson.toJson(value.id(), SerializerFactory.UUID_TYPE, out);
/*     */     
/* 130 */     Component name = value.name();
/* 131 */     if (name != null) {
/* 132 */       out.name("name");
/* 133 */       this.gson.toJson(name, SerializerFactory.COMPONENT_TYPE, out);
/*     */     } 
/*     */     
/* 136 */     out.endObject();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\gson\ShowEntitySerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */