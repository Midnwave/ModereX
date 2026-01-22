/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.gson;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.BlockNBTComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.BuildableComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.ComponentBuilder;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.ComponentLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.EntityNBTComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.KeybindComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.NBTComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.ScoreComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.SelectorComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.StorageNBTComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TextComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TranslatableComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TranslationArgument;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.json.JSONOptions;
/*     */ import ac.grim.grimac.shaded.kyori.option.OptionState;
/*     */ import com.google.gson.Gson;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.google.gson.JsonParseException;
/*     */ import com.google.gson.TypeAdapter;
/*     */ import com.google.gson.reflect.TypeToken;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonToken;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class ComponentSerializerImpl
/*     */   extends TypeAdapter<Component>
/*     */ {
/*  78 */   static final Type COMPONENT_LIST_TYPE = (new TypeToken<List<Component>>() {  }).getType();
/*  79 */   static final Type TRANSLATABLE_ARGUMENT_LIST_TYPE = (new TypeToken<List<TranslationArgument>>() {  }).getType(); private final boolean emitCompactTextComponent; private final Gson gson;
/*     */   
/*     */   static TypeAdapter<Component> create(OptionState features, Gson gson) {
/*  82 */     return (new ComponentSerializerImpl(((Boolean)features.value(JSONOptions.EMIT_COMPACT_TEXT_COMPONENT)).booleanValue(), gson)).nullSafe();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ComponentSerializerImpl(boolean emitCompactTextComponent, Gson gson) {
/*  89 */     this.emitCompactTextComponent = emitCompactTextComponent;
/*  90 */     this.gson = gson;
/*     */   }
/*     */   public BuildableComponent<?, ?> read(JsonReader in) throws IOException {
/*     */     BuildableComponent<?, ?> buildableComponent;
/*     */     StorageNBTComponent.Builder builder;
/*  95 */     JsonToken token = in.peek();
/*  96 */     if (token == JsonToken.STRING || token == JsonToken.NUMBER || token == JsonToken.BOOLEAN)
/*  97 */       return (BuildableComponent<?, ?>)Component.text(GsonHacks.readString(in)); 
/*  98 */     if (token == JsonToken.BEGIN_ARRAY) {
/*  99 */       ComponentBuilder<?, ?> parent = null;
/* 100 */       in.beginArray();
/* 101 */       while (in.hasNext()) {
/* 102 */         BuildableComponent<?, ?> child = read(in);
/* 103 */         if (parent == null) {
/* 104 */           parent = child.toBuilder(); continue;
/*     */         } 
/* 106 */         parent.append((Component)child);
/*     */       } 
/*     */       
/* 109 */       if (parent == null) {
/* 110 */         throw notSureHowToDeserialize(in.getPath());
/*     */       }
/* 112 */       in.endArray();
/* 113 */       return parent.build();
/* 114 */     }  if (token != JsonToken.BEGIN_OBJECT) {
/* 115 */       throw notSureHowToDeserialize(in.getPath());
/*     */     }
/*     */ 
/*     */     
/* 119 */     JsonObject style = new JsonObject();
/* 120 */     List<Component> extra = Collections.emptyList();
/*     */ 
/*     */     
/* 123 */     String text = null;
/* 124 */     String translate = null;
/* 125 */     String translateFallback = null;
/* 126 */     List<TranslationArgument> translateWith = null;
/* 127 */     String scoreName = null;
/* 128 */     String scoreObjective = null;
/* 129 */     String scoreValue = null;
/* 130 */     String selector = null;
/* 131 */     String keybind = null;
/* 132 */     String nbt = null;
/* 133 */     boolean nbtInterpret = false;
/* 134 */     BlockNBTComponent.Pos nbtBlock = null;
/* 135 */     String nbtEntity = null;
/* 136 */     Key nbtStorage = null;
/* 137 */     Component separator = null;
/*     */     
/* 139 */     in.beginObject();
/* 140 */     while (in.hasNext()) {
/* 141 */       String fieldName = in.nextName();
/* 142 */       if (fieldName.equals("text")) {
/* 143 */         text = GsonHacks.readString(in); continue;
/* 144 */       }  if (fieldName.equals("translate")) {
/* 145 */         translate = in.nextString(); continue;
/* 146 */       }  if (fieldName.equals("fallback")) {
/* 147 */         translateFallback = in.nextString(); continue;
/* 148 */       }  if (fieldName.equals("with")) {
/* 149 */         translateWith = (List<TranslationArgument>)this.gson.fromJson(in, TRANSLATABLE_ARGUMENT_LIST_TYPE); continue;
/* 150 */       }  if (fieldName.equals("score")) {
/* 151 */         in.beginObject();
/* 152 */         while (in.hasNext()) {
/* 153 */           String scoreFieldName = in.nextName();
/* 154 */           if (scoreFieldName.equals("name")) {
/* 155 */             scoreName = in.nextString(); continue;
/* 156 */           }  if (scoreFieldName.equals("objective")) {
/* 157 */             scoreObjective = in.nextString(); continue;
/* 158 */           }  if (scoreFieldName.equals("value")) {
/* 159 */             scoreValue = in.nextString(); continue;
/*     */           } 
/* 161 */           in.skipValue();
/*     */         } 
/*     */         
/* 164 */         if (scoreName == null || scoreObjective == null) {
/* 165 */           throw new JsonParseException("A score component requires a name and objective");
/*     */         }
/* 167 */         in.endObject(); continue;
/* 168 */       }  if (fieldName.equals("selector")) {
/* 169 */         selector = in.nextString(); continue;
/* 170 */       }  if (fieldName.equals("keybind")) {
/* 171 */         keybind = in.nextString(); continue;
/* 172 */       }  if (fieldName.equals("nbt")) {
/* 173 */         nbt = in.nextString(); continue;
/* 174 */       }  if (fieldName.equals("interpret")) {
/* 175 */         nbtInterpret = in.nextBoolean(); continue;
/* 176 */       }  if (fieldName.equals("block")) {
/* 177 */         nbtBlock = (BlockNBTComponent.Pos)this.gson.fromJson(in, SerializerFactory.BLOCK_NBT_POS_TYPE); continue;
/* 178 */       }  if (fieldName.equals("entity")) {
/* 179 */         nbtEntity = in.nextString(); continue;
/* 180 */       }  if (fieldName.equals("storage")) {
/* 181 */         nbtStorage = (Key)this.gson.fromJson(in, SerializerFactory.KEY_TYPE); continue;
/* 182 */       }  if (fieldName.equals("extra")) {
/* 183 */         extra = (List<Component>)this.gson.fromJson(in, COMPONENT_LIST_TYPE); continue;
/* 184 */       }  if (fieldName.equals("separator")) {
/* 185 */         buildableComponent = read(in); continue;
/*     */       } 
/* 187 */       style.add(fieldName, (JsonElement)this.gson.fromJson(in, JsonElement.class));
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 192 */     if (text != null) {
/* 193 */       TextComponent.Builder builder1 = Component.text().content(text);
/* 194 */     } else if (translate != null) {
/* 195 */       if (translateWith != null) {
/* 196 */         TranslatableComponent.Builder builder1 = Component.translatable().key(translate).fallback(translateFallback).arguments(translateWith);
/*     */       } else {
/* 198 */         TranslatableComponent.Builder builder1 = Component.translatable().key(translate).fallback(translateFallback);
/*     */       } 
/* 200 */     } else if (scoreName != null && scoreObjective != null) {
/* 201 */       if (scoreValue == null) {
/* 202 */         ScoreComponent.Builder builder1 = Component.score().name(scoreName).objective(scoreObjective);
/*     */       } else {
/* 204 */         ScoreComponent.Builder builder1 = Component.score().name(scoreName).objective(scoreObjective).value(scoreValue);
/*     */       } 
/* 206 */     } else if (selector != null) {
/* 207 */       SelectorComponent.Builder builder1 = Component.selector().pattern(selector).separator((ComponentLike)buildableComponent);
/* 208 */     } else if (keybind != null) {
/* 209 */       KeybindComponent.Builder builder1 = Component.keybind().keybind(keybind);
/* 210 */     } else if (nbt != null) {
/* 211 */       if (nbtBlock != null) {
/* 212 */         BlockNBTComponent.Builder builder1 = ((BlockNBTComponent.Builder)nbt(Component.blockNBT(), nbt, nbtInterpret, (Component)buildableComponent)).pos(nbtBlock);
/* 213 */       } else if (nbtEntity != null) {
/* 214 */         EntityNBTComponent.Builder builder1 = ((EntityNBTComponent.Builder)nbt(Component.entityNBT(), nbt, nbtInterpret, (Component)buildableComponent)).selector(nbtEntity);
/* 215 */       } else if (nbtStorage != null) {
/* 216 */         builder = ((StorageNBTComponent.Builder)nbt(Component.storageNBT(), nbt, nbtInterpret, (Component)buildableComponent)).storage(nbtStorage);
/*     */       } else {
/* 218 */         throw notSureHowToDeserialize(in.getPath());
/*     */       } 
/*     */     } else {
/* 221 */       throw notSureHowToDeserialize(in.getPath());
/*     */     } 
/*     */     
/* 224 */     builder.style((Style)this.gson.fromJson((JsonElement)style, SerializerFactory.STYLE_TYPE))
/* 225 */       .append(extra);
/* 226 */     in.endObject();
/* 227 */     return builder.build();
/*     */   }
/*     */   
/*     */   private static <C extends NBTComponent<C, B>, B extends ac.grim.grimac.shaded.kyori.adventure.text.NBTComponentBuilder<C, B>> B nbt(B builder, String nbt, boolean interpret, @Nullable Component separator) {
/* 231 */     return (B)builder
/* 232 */       .nbtPath(nbt)
/* 233 */       .interpret(interpret)
/* 234 */       .separator((ComponentLike)separator);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(JsonWriter out, Component value) throws IOException {
/* 239 */     if (value instanceof TextComponent && value
/*     */       
/* 241 */       .children().isEmpty() && 
/* 242 */       !value.hasStyling() && this.emitCompactTextComponent) {
/*     */ 
/*     */       
/* 245 */       out.value(((TextComponent)value).content());
/*     */       
/*     */       return;
/*     */     } 
/* 249 */     out.beginObject();
/*     */     
/* 251 */     if (value.hasStyling()) {
/* 252 */       JsonElement style = this.gson.toJsonTree(value.style(), SerializerFactory.STYLE_TYPE);
/* 253 */       if (style.isJsonObject()) {
/* 254 */         for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)style.getAsJsonObject().entrySet()) {
/* 255 */           out.name(entry.getKey());
/* 256 */           this.gson.toJson(entry.getValue(), out);
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 261 */     if (!value.children().isEmpty()) {
/* 262 */       out.name("extra");
/* 263 */       this.gson.toJson(value.children(), COMPONENT_LIST_TYPE, out);
/*     */     } 
/*     */     
/* 266 */     if (value instanceof TextComponent) {
/* 267 */       out.name("text");
/* 268 */       out.value(((TextComponent)value).content());
/* 269 */     } else if (value instanceof TranslatableComponent) {
/* 270 */       TranslatableComponent translatable = (TranslatableComponent)value;
/* 271 */       out.name("translate");
/* 272 */       out.value(translatable.key());
/* 273 */       String fallback = translatable.fallback();
/* 274 */       if (fallback != null) {
/* 275 */         out.name("fallback");
/* 276 */         out.value(fallback);
/*     */       } 
/* 278 */       if (!translatable.arguments().isEmpty()) {
/* 279 */         out.name("with");
/* 280 */         this.gson.toJson(translatable.arguments(), TRANSLATABLE_ARGUMENT_LIST_TYPE, out);
/*     */       } 
/* 282 */     } else if (value instanceof ScoreComponent) {
/* 283 */       ScoreComponent score = (ScoreComponent)value;
/* 284 */       out.name("score");
/* 285 */       out.beginObject();
/* 286 */       out.name("name");
/* 287 */       out.value(score.name());
/* 288 */       out.name("objective");
/* 289 */       out.value(score.objective());
/* 290 */       if (score.value() != null) {
/* 291 */         out.name("value");
/* 292 */         out.value(score.value());
/*     */       } 
/* 294 */       out.endObject();
/* 295 */     } else if (value instanceof SelectorComponent) {
/* 296 */       SelectorComponent selector = (SelectorComponent)value;
/* 297 */       out.name("selector");
/* 298 */       out.value(selector.pattern());
/* 299 */       serializeSeparator(out, selector.separator());
/* 300 */     } else if (value instanceof KeybindComponent) {
/* 301 */       out.name("keybind");
/* 302 */       out.value(((KeybindComponent)value).keybind());
/* 303 */     } else if (value instanceof NBTComponent) {
/* 304 */       NBTComponent<?, ?> nbt = (NBTComponent<?, ?>)value;
/* 305 */       out.name("nbt");
/* 306 */       out.value(nbt.nbtPath());
/* 307 */       out.name("interpret");
/* 308 */       out.value(nbt.interpret());
/* 309 */       serializeSeparator(out, nbt.separator());
/* 310 */       if (value instanceof BlockNBTComponent) {
/* 311 */         out.name("block");
/* 312 */         this.gson.toJson(((BlockNBTComponent)value).pos(), SerializerFactory.BLOCK_NBT_POS_TYPE, out);
/* 313 */       } else if (value instanceof EntityNBTComponent) {
/* 314 */         out.name("entity");
/* 315 */         out.value(((EntityNBTComponent)value).selector());
/* 316 */       } else if (value instanceof StorageNBTComponent) {
/* 317 */         out.name("storage");
/* 318 */         this.gson.toJson(((StorageNBTComponent)value).storage(), SerializerFactory.KEY_TYPE, out);
/*     */       } else {
/* 320 */         throw notSureHowToSerialize(value);
/*     */       } 
/*     */     } else {
/* 323 */       throw notSureHowToSerialize(value);
/*     */     } 
/*     */     
/* 326 */     out.endObject();
/*     */   }
/*     */   
/*     */   private void serializeSeparator(JsonWriter out, @Nullable Component separator) throws IOException {
/* 330 */     if (separator != null) {
/* 331 */       out.name("separator");
/* 332 */       write(out, separator);
/*     */     } 
/*     */   }
/*     */   
/*     */   static JsonParseException notSureHowToDeserialize(Object element) {
/* 337 */     return new JsonParseException("Don't know how to turn " + element + " into a Component");
/*     */   }
/*     */   
/*     */   private static IllegalArgumentException notSureHowToSerialize(Component component) {
/* 341 */     return new IllegalArgumentException("Don't know how to serialize " + component + " as a Component");
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\gson\ComponentSerializerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */