/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.serializer.gson;
/*    */ 
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.option.OptionState;
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.serializer.json.JSONOptions;
/*    */ import com.google.gson.TypeAdapter;
/*    */ import com.google.gson.stream.JsonReader;
/*    */ import com.google.gson.stream.JsonToken;
/*    */ import com.google.gson.stream.JsonWriter;
/*    */ import java.io.IOException;
/*    */ import java.util.UUID;
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
/*    */ final class UUIDSerializer
/*    */   extends TypeAdapter<UUID>
/*    */ {
/*    */   private final boolean emitIntArray;
/*    */   
/*    */   static TypeAdapter<UUID> uuidSerializer(OptionState features) {
/* 39 */     return (new UUIDSerializer(((Boolean)features.value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY)).booleanValue())).nullSafe();
/*    */   }
/*    */   
/*    */   private UUIDSerializer(boolean emitIntArray) {
/* 43 */     this.emitIntArray = emitIntArray;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(JsonWriter out, UUID value) throws IOException {
/* 48 */     if (this.emitIntArray) {
/* 49 */       int msb0 = (int)(value.getMostSignificantBits() >> 32L);
/* 50 */       int msb1 = (int)(value.getMostSignificantBits() & 0xFFFFFFFFL);
/* 51 */       int lsb0 = (int)(value.getLeastSignificantBits() >> 32L);
/* 52 */       int lsb1 = (int)(value.getLeastSignificantBits() & 0xFFFFFFFFL);
/*    */       
/* 54 */       out.beginArray()
/* 55 */         .value(msb0)
/* 56 */         .value(msb1)
/* 57 */         .value(lsb0)
/* 58 */         .value(lsb1)
/* 59 */         .endArray();
/*    */     } else {
/* 61 */       out.value(value.toString());
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public UUID read(JsonReader in) throws IOException {
/* 68 */     if (in.peek() == JsonToken.BEGIN_ARRAY) {
/* 69 */       in.beginArray();
/* 70 */       int msb0 = in.nextInt();
/* 71 */       int msb1 = in.nextInt();
/* 72 */       int lsb0 = in.nextInt();
/* 73 */       int lsb1 = in.nextInt();
/* 74 */       in.endArray();
/* 75 */       return new UUID(msb0 << 32L | msb1 & 0xFFFFFFFFL, lsb0 << 32L | lsb1 & 0xFFFFFFFFL);
/*    */     } 
/*    */     
/* 78 */     return UUID.fromString(in.nextString());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\adventure\serializer\gson\UUIDSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */