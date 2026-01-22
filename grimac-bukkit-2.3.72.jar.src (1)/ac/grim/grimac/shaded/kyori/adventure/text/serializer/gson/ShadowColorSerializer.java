/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.gson;
/*    */ 
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.ShadowColor;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.json.JSONOptions;
/*    */ import ac.grim.grimac.shaded.kyori.option.OptionState;
/*    */ import com.google.gson.JsonParseException;
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
/*    */ final class ShadowColorSerializer
/*    */   extends TypeAdapter<ShadowColor>
/*    */ {
/*    */   private final boolean emitArray;
/*    */   
/*    */   static TypeAdapter<ShadowColor> create(OptionState options) {
/* 38 */     return (new ShadowColorSerializer((options.value(JSONOptions.SHADOW_COLOR_MODE) == JSONOptions.ShadowColorEmitMode.EMIT_ARRAY))).nullSafe();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private ShadowColorSerializer(boolean emitArray) {
/* 44 */     this.emitArray = emitArray;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(JsonWriter out, ShadowColor value) throws IOException {
/* 49 */     if (this.emitArray) {
/* 50 */       out.beginArray()
/* 51 */         .value(componentAsFloat(value.red()))
/* 52 */         .value(componentAsFloat(value.green()))
/* 53 */         .value(componentAsFloat(value.blue()))
/* 54 */         .value(componentAsFloat(value.alpha()))
/* 55 */         .endArray();
/*    */     } else {
/*    */       
/* 58 */       out.value(value.value());
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public ShadowColor read(JsonReader in) throws IOException {
/* 64 */     if (in.peek() == JsonToken.BEGIN_ARRAY) {
/* 65 */       in.beginArray();
/* 66 */       double r = in.nextDouble();
/* 67 */       double g = in.nextDouble();
/* 68 */       double b = in.nextDouble();
/* 69 */       double a = in.nextDouble();
/* 70 */       if (in.peek() != JsonToken.END_ARRAY) {
/* 71 */         throw new JsonParseException("Failed to parse shadow colour at " + in.getPath() + ": expected end of 4-element array but got " + in.peek() + " instead.");
/*    */       }
/* 73 */       in.endArray();
/*    */       
/* 75 */       return ShadowColor.shadowColor(
/* 76 */           componentFromFloat(r), 
/* 77 */           componentFromFloat(g), 
/* 78 */           componentFromFloat(b), 
/* 79 */           componentFromFloat(a));
/*    */     } 
/*    */ 
/*    */     
/* 83 */     return ShadowColor.shadowColor(in.nextInt());
/*    */   }
/*    */   
/*    */   static float componentAsFloat(int element) {
/* 87 */     return element / 255.0F;
/*    */   }
/*    */   
/*    */   static int componentFromFloat(double element) {
/* 91 */     return (int)((float)element * 255.0F);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\gson\ShadowColorSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */