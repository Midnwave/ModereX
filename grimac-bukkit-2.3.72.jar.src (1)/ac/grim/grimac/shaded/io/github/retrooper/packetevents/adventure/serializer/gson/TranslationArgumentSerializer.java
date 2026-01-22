/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.serializer.gson;
/*    */ 
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.ComponentLike;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.TranslationArgument;
/*    */ import com.google.gson.Gson;
/*    */ import com.google.gson.TypeAdapter;
/*    */ import com.google.gson.stream.JsonReader;
/*    */ import com.google.gson.stream.JsonToken;
/*    */ import com.google.gson.stream.JsonWriter;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class TranslationArgumentSerializer
/*    */   extends TypeAdapter<TranslationArgument>
/*    */ {
/*    */   private final Gson gson;
/*    */   
/*    */   static TypeAdapter<TranslationArgument> create(Gson gson) {
/* 38 */     return (new TranslationArgumentSerializer(gson)).nullSafe();
/*    */   }
/*    */   
/*    */   private TranslationArgumentSerializer(Gson gson) {
/* 42 */     this.gson = gson;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(JsonWriter out, TranslationArgument value) throws IOException {
/* 47 */     Object raw = value.value();
/* 48 */     if (raw instanceof Boolean) {
/* 49 */       out.value((Boolean)raw);
/* 50 */     } else if (raw instanceof Number) {
/* 51 */       out.value((Number)raw);
/* 52 */     } else if (raw instanceof ac.grim.grimac.shaded.kyori.adventure.text.Component) {
/* 53 */       this.gson.toJson(raw, SerializerFactory.COMPONENT_TYPE, out);
/*    */     } else {
/* 55 */       throw new IllegalStateException("Unable to serialize translatable argument of type " + raw.getClass() + ": " + raw);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public TranslationArgument read(JsonReader in) throws IOException {
/* 61 */     switch (in.peek()) { case BOOLEAN:
/* 62 */         return TranslationArgument.bool(in.nextBoolean());
/* 63 */       case NUMBER: return TranslationArgument.numeric((Number)this.gson.fromJson(in, Number.class)); }
/* 64 */      return TranslationArgument.component((ComponentLike)this.gson.fromJson(in, SerializerFactory.COMPONENT_TYPE));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\adventure\serializer\gson\TranslationArgumentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */