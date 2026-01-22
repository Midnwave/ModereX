/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.bungeecord;
/*    */ 
/*    */ import com.google.gson.Gson;
/*    */ import com.google.gson.TypeAdapter;
/*    */ import com.google.gson.TypeAdapterFactory;
/*    */ import com.google.gson.reflect.TypeToken;
/*    */ import com.google.gson.stream.JsonReader;
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
/*    */ interface SelfSerializable
/*    */ {
/*    */   void write(JsonWriter paramJsonWriter) throws IOException;
/*    */   
/*    */   public static class AdapterFactory
/*    */     implements TypeAdapterFactory
/*    */   {
/*    */     static {
/* 53 */       SelfSerializableTypeAdapter.class.getName();
/*    */     }
/*    */ 
/*    */     
/*    */     public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
/* 58 */       if (!SelfSerializable.class.isAssignableFrom(type.getRawType())) {
/* 59 */         return null;
/*    */       }
/*    */       
/* 62 */       return new SelfSerializableTypeAdapter<>(type);
/*    */     }
/*    */     
/*    */     static class SelfSerializableTypeAdapter<T> extends TypeAdapter<T> {
/*    */       private final TypeToken<T> type;
/*    */       
/*    */       SelfSerializableTypeAdapter(TypeToken<T> type) {
/* 69 */         this.type = type;
/*    */       }
/*    */ 
/*    */       
/*    */       public void write(JsonWriter out, T value) throws IOException {
/* 74 */         ((SelfSerializable)value).write(out);
/*    */       }
/*    */ 
/*    */       
/*    */       public T read(JsonReader in) {
/* 79 */         throw new UnsupportedOperationException("Cannot load values of type " + this.type.getType().getTypeName());
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\bungeecord\SelfSerializable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */