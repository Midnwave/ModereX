/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.gson;
/*    */ 
/*    */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*    */ import com.google.gson.TypeAdapter;
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
/*    */ final class KeySerializer
/*    */   extends TypeAdapter<Key>
/*    */ {
/* 33 */   static final TypeAdapter<Key> INSTANCE = (new KeySerializer()).nullSafe();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void write(JsonWriter out, Key value) throws IOException {
/* 40 */     out.value(value.asString());
/*    */   }
/*    */ 
/*    */   
/*    */   public Key read(JsonReader in) throws IOException {
/* 45 */     return Key.key(in.nextString());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\gson\KeySerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */