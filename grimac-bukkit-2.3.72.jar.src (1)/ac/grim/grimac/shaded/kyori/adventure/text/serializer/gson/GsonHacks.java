/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.gson;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import com.google.gson.JsonElement;
/*    */ import com.google.gson.JsonParseException;
/*    */ import com.google.gson.stream.JsonReader;
/*    */ import com.google.gson.stream.JsonToken;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class GsonHacks
/*    */ {
/*    */   static boolean isNullOrEmpty(@Nullable JsonElement element) {
/* 38 */     return (element == null || element
/* 39 */       .isJsonNull() || (element
/* 40 */       .isJsonArray() && element.getAsJsonArray().size() == 0) || (element
/* 41 */       .isJsonObject() && element.getAsJsonObject().entrySet().isEmpty()));
/*    */   }
/*    */   
/*    */   static boolean readBoolean(JsonReader in) throws IOException {
/* 45 */     JsonToken peek = in.peek();
/* 46 */     if (peek == JsonToken.BOOLEAN)
/* 47 */       return in.nextBoolean(); 
/* 48 */     if (peek == JsonToken.STRING)
/* 49 */       return Boolean.parseBoolean(in.nextString()); 
/* 50 */     if (peek == JsonToken.NUMBER) {
/* 51 */       return in.nextString().equals("1");
/*    */     }
/* 53 */     throw new JsonParseException("Token of type " + peek + " cannot be interpreted as a boolean");
/*    */   }
/*    */ 
/*    */   
/*    */   static String readString(JsonReader in) throws IOException {
/* 58 */     JsonToken peek = in.peek();
/* 59 */     if (peek == JsonToken.STRING || peek == JsonToken.NUMBER)
/* 60 */       return in.nextString(); 
/* 61 */     if (peek == JsonToken.BOOLEAN) {
/* 62 */       return String.valueOf(in.nextBoolean());
/*    */     }
/* 64 */     throw new JsonParseException("Token of type " + peek + " cannot be interpreted as a string");
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\gson\GsonHacks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */