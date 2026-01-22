/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.gson;
/*    */ 
/*    */ import ac.grim.grimac.shaded.kyori.adventure.util.Index;
/*    */ import com.google.gson.JsonParseException;
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
/*    */ final class IndexedSerializer<E>
/*    */   extends TypeAdapter<E>
/*    */ {
/*    */   private final String name;
/*    */   private final Index<String, E> map;
/*    */   private final boolean throwOnUnknownKey;
/*    */   
/*    */   public static <E> TypeAdapter<E> strict(String name, Index<String, E> map) {
/* 39 */     return (new IndexedSerializer(name, map, true)).nullSafe();
/*    */   }
/*    */   
/*    */   public static <E> TypeAdapter<E> lenient(String name, Index<String, E> map) {
/* 43 */     return (new IndexedSerializer(name, map, false)).nullSafe();
/*    */   }
/*    */   
/*    */   private IndexedSerializer(String name, Index<String, E> map, boolean throwOnUnknownKey) {
/* 47 */     this.name = name;
/* 48 */     this.map = map;
/* 49 */     this.throwOnUnknownKey = throwOnUnknownKey;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(JsonWriter out, E value) throws IOException {
/* 54 */     out.value((String)this.map.key(value));
/*    */   }
/*    */ 
/*    */   
/*    */   public E read(JsonReader in) throws IOException {
/* 59 */     String string = in.nextString();
/* 60 */     E value = (E)this.map.value(string);
/* 61 */     if (value != null)
/* 62 */       return value; 
/* 63 */     if (this.throwOnUnknownKey) {
/* 64 */       throw new JsonParseException("invalid " + this.name + ":  " + string);
/*    */     }
/* 66 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\gson\IndexedSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */