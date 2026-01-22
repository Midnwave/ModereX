/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.gson;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.NamedTextColor;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*    */ import com.google.gson.TypeAdapter;
/*    */ import com.google.gson.stream.JsonReader;
/*    */ import com.google.gson.stream.JsonWriter;
/*    */ import java.io.IOException;
/*    */ import java.util.Locale;
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
/*    */ final class TextColorSerializer
/*    */   extends TypeAdapter<TextColor>
/*    */ {
/* 37 */   static final TypeAdapter<TextColor> INSTANCE = (new TextColorSerializer(false)).nullSafe();
/* 38 */   static final TypeAdapter<TextColor> DOWNSAMPLE_COLOR = (new TextColorSerializer(true)).nullSafe();
/*    */   
/*    */   private final boolean downsampleColor;
/*    */   
/*    */   private TextColorSerializer(boolean downsampleColor) {
/* 43 */     this.downsampleColor = downsampleColor;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(JsonWriter out, TextColor value) throws IOException {
/* 48 */     if (value instanceof NamedTextColor) {
/* 49 */       out.value((String)NamedTextColor.NAMES.key(value));
/* 50 */     } else if (this.downsampleColor) {
/* 51 */       out.value((String)NamedTextColor.NAMES.key(NamedTextColor.nearestTo(value)));
/*    */     } else {
/* 53 */       out.value(asUpperCaseHexString(value));
/*    */     } 
/*    */   }
/*    */   
/*    */   private static String asUpperCaseHexString(TextColor color) {
/* 58 */     return String.format(Locale.ROOT, "%c%06X", new Object[] { Character.valueOf('#'), Integer.valueOf(color.value()) });
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public TextColor read(JsonReader in) throws IOException {
/* 63 */     TextColor color = fromString(in.nextString());
/* 64 */     if (color == null) return null;
/*    */     
/* 66 */     return this.downsampleColor ? (TextColor)NamedTextColor.nearestTo(color) : color;
/*    */   }
/*    */   @Nullable
/*    */   static TextColor fromString(@NotNull String value) {
/* 70 */     if (value.startsWith("#")) {
/* 71 */       return TextColor.fromHexString(value);
/*    */     }
/* 73 */     return (TextColor)NamedTextColor.NAMES.value(value);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\gson\TextColorSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */