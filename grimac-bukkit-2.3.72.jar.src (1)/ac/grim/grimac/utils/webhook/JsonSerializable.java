/*    */ package ac.grim.grimac.utils.webhook;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import com.google.gson.JsonArray;
/*    */ import com.google.gson.JsonElement;
/*    */ import com.google.gson.JsonNull;
/*    */ import java.util.function.Function;
/*    */ import java.util.function.IntFunction;
/*    */ 
/*    */ 
/*    */ public interface JsonSerializable
/*    */ {
/*    */   @NotNull
/*    */   static JsonArray serializeArray(@Nullable JsonSerializable[] serializableArray) {
/* 16 */     JsonArray array = new JsonArray();
/*    */     
/* 18 */     for (JsonSerializable serializable : serializableArray) {
/* 19 */       array.add((serializable == null) ? (JsonElement)JsonNull.INSTANCE : serializable.toJson());
/*    */     }
/*    */     
/* 22 */     return array;
/*    */   } @NotNull
/*    */   JsonElement toJson();
/*    */   static <T extends JsonSerializable> T[] deserializeArray(JsonArray jsonArray, IntFunction<T[]> newArray, Function<JsonElement, T> constructor) {
/* 26 */     JsonSerializable[] arrayOfJsonSerializable = (JsonSerializable[])newArray.apply(jsonArray.size());
/*    */     
/* 28 */     for (int i = 0; i < jsonArray.size(); i++) {
/* 29 */       arrayOfJsonSerializable[i] = (JsonSerializable)constructor.apply(jsonArray.get(i));
/*    */     }
/*    */     
/* 32 */     return (T[])arrayOfJsonSerializable;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\webhook\JsonSerializable.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */