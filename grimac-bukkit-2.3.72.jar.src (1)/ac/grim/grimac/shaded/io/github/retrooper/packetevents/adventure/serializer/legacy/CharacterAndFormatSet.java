/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.serializer.legacy;
/*    */ 
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextFormat;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
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
/*    */ final class CharacterAndFormatSet
/*    */ {
/* 33 */   static final CharacterAndFormatSet DEFAULT = of(CharacterAndFormat.defaults());
/*    */   final List<TextFormat> formats;
/*    */   final List<TextColor> colors;
/*    */   final String characters;
/*    */   
/*    */   static CharacterAndFormatSet of(List<CharacterAndFormat> pairs) {
/* 39 */     int size = pairs.size();
/* 40 */     List<TextColor> colors = new ArrayList<>();
/* 41 */     List<TextFormat> formats = new ArrayList<>(size);
/* 42 */     StringBuilder characters = new StringBuilder(size);
/* 43 */     for (int i = 0; i < size; i++) {
/* 44 */       CharacterAndFormat pair = pairs.get(i);
/* 45 */       char character = pair.character();
/* 46 */       TextFormat format = pair.format();
/* 47 */       boolean formatIsTextColor = format instanceof TextColor;
/*    */ 
/*    */       
/* 50 */       characters.append(character);
/* 51 */       formats.add(format);
/* 52 */       if (formatIsTextColor) {
/* 53 */         colors.add((TextColor)format);
/*    */       }
/*    */ 
/*    */       
/* 57 */       if (pair.caseInsensitive()) {
/* 58 */         boolean added = false;
/*    */         
/* 60 */         if (Character.isUpperCase(character)) {
/* 61 */           characters.append(Character.toLowerCase(character));
/* 62 */           added = true;
/* 63 */         } else if (Character.isLowerCase(character)) {
/* 64 */           characters.append(Character.toUpperCase(character));
/* 65 */           added = true;
/*    */         } 
/*    */         
/* 68 */         if (added) {
/* 69 */           formats.add(format);
/* 70 */           if (formatIsTextColor) {
/* 71 */             colors.add((TextColor)format);
/*    */           }
/*    */         } 
/*    */       } 
/*    */     } 
/* 76 */     if (formats.size() != characters.length()) {
/* 77 */       throw new IllegalStateException("formats length differs from characters length");
/*    */     }
/* 79 */     return new CharacterAndFormatSet(Collections.unmodifiableList(formats), Collections.unmodifiableList(colors), characters.toString());
/*    */   }
/*    */   
/*    */   CharacterAndFormatSet(List<TextFormat> formats, List<TextColor> colors, String characters) {
/* 83 */     this.formats = formats;
/* 84 */     this.colors = colors;
/* 85 */     this.characters = characters;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\adventure\serializer\legacy\CharacterAndFormatSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */