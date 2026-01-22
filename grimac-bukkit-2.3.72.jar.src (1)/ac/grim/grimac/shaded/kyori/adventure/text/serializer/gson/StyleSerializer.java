/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.gson;
/*     */ 
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.ClickEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEventSource;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.ShadowColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextDecoration;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.json.JSONOptions;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.json.LegacyHoverEventSerializer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.ARGBLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Codec;
/*     */ import ac.grim.grimac.shaded.kyori.option.OptionState;
/*     */ import com.google.gson.Gson;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.google.gson.JsonParseException;
/*     */ import com.google.gson.JsonPrimitive;
/*     */ import com.google.gson.JsonSyntaxException;
/*     */ import com.google.gson.TypeAdapter;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonToken;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.util.EnumSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ 
/*     */ 
/*     */ final class StyleSerializer
/*     */   extends TypeAdapter<Style>
/*     */ {
/*  75 */   private static final TextDecoration[] DECORATIONS = new TextDecoration[] { TextDecoration.BOLD, TextDecoration.ITALIC, TextDecoration.UNDERLINED, TextDecoration.STRIKETHROUGH, TextDecoration.OBFUSCATED }; private final LegacyHoverEventSerializer legacyHover;
/*     */   private final boolean emitValueFieldHover;
/*     */   private final boolean emitCamelCaseHover;
/*     */   private final boolean emitSnakeCaseHover;
/*     */   private final boolean emitCamelCaseClick;
/*     */   private final boolean emitSnakeCaseClick;
/*     */   private final boolean strictEventValues;
/*     */   private final boolean emitShadowColor;
/*     */   private final boolean emitStringPage;
/*     */   private final Gson gson;
/*     */   
/*     */   static {
/*  87 */     Set<TextDecoration> knownDecorations = EnumSet.allOf(TextDecoration.class);
/*  88 */     for (TextDecoration decoration : DECORATIONS) {
/*  89 */       knownDecorations.remove(decoration);
/*     */     }
/*  91 */     if (!knownDecorations.isEmpty()) {
/*  92 */       throw new IllegalStateException("Gson serializer is missing some text decorations: " + knownDecorations);
/*     */     }
/*     */   }
/*     */   
/*     */   static TypeAdapter<Style> create(LegacyHoverEventSerializer legacyHover, OptionState features, Gson gson) {
/*  97 */     JSONOptions.HoverEventValueMode hoverMode = (JSONOptions.HoverEventValueMode)features.value(JSONOptions.EMIT_HOVER_EVENT_TYPE);
/*  98 */     JSONOptions.ClickEventValueMode clickMode = (JSONOptions.ClickEventValueMode)features.value(JSONOptions.EMIT_CLICK_EVENT_TYPE);
/*  99 */     return (new StyleSerializer(legacyHover, (hoverMode == JSONOptions.HoverEventValueMode.VALUE_FIELD || hoverMode == JSONOptions.HoverEventValueMode.ALL), (hoverMode == JSONOptions.HoverEventValueMode.CAMEL_CASE || hoverMode == JSONOptions.HoverEventValueMode.ALL), (hoverMode == JSONOptions.HoverEventValueMode.SNAKE_CASE || hoverMode == JSONOptions.HoverEventValueMode.ALL), (clickMode == JSONOptions.ClickEventValueMode.CAMEL_CASE || clickMode == JSONOptions.ClickEventValueMode.BOTH), (clickMode == JSONOptions.ClickEventValueMode.SNAKE_CASE || clickMode == JSONOptions.ClickEventValueMode.BOTH), ((Boolean)features
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 106 */         .value(JSONOptions.VALIDATE_STRICT_EVENTS)).booleanValue(), 
/* 107 */         (features.value(JSONOptions.SHADOW_COLOR_MODE) != JSONOptions.ShadowColorEmitMode.NONE), ((Boolean)features
/* 108 */         .value(JSONOptions.EMIT_CHANGE_PAGE_CLICK_EVENT_PAGE_AS_STRING)).booleanValue(), gson))
/*     */       
/* 110 */       .nullSafe();
/*     */   }
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
/*     */   private StyleSerializer(LegacyHoverEventSerializer legacyHover, boolean emitValueFieldHover, boolean emitCamelCaseHover, boolean emitSnakeCaseHover, boolean emitCamelCaseClick, boolean emitSnakeCaseClick, boolean strictEventValues, boolean emitShadowColor, boolean emitStringPage, Gson gson) {
/* 136 */     this.legacyHover = legacyHover;
/* 137 */     this.emitValueFieldHover = emitValueFieldHover;
/* 138 */     this.emitCamelCaseHover = emitCamelCaseHover;
/* 139 */     this.emitSnakeCaseHover = emitSnakeCaseHover;
/* 140 */     this.emitCamelCaseClick = emitCamelCaseClick;
/* 141 */     this.emitSnakeCaseClick = emitSnakeCaseClick;
/* 142 */     this.strictEventValues = strictEventValues;
/* 143 */     this.emitShadowColor = emitShadowColor;
/* 144 */     this.emitStringPage = emitStringPage;
/* 145 */     this.gson = gson;
/*     */   }
/*     */ 
/*     */   
/*     */   public Style read(JsonReader in) throws IOException {
/* 150 */     in.beginObject();
/* 151 */     Style.Builder style = Style.style();
/*     */     
/* 153 */     while (in.hasNext()) {
/* 154 */       String fieldName = in.nextName();
/* 155 */       if (fieldName.equals("font")) {
/* 156 */         style.font((Key)this.gson.fromJson(in, SerializerFactory.KEY_TYPE)); continue;
/* 157 */       }  if (fieldName.equals("color")) {
/* 158 */         TextColorWrapper color = (TextColorWrapper)this.gson.fromJson(in, SerializerFactory.COLOR_WRAPPER_TYPE);
/* 159 */         if (color.color != null) {
/* 160 */           style.color(color.color); continue;
/* 161 */         }  if (color.decoration != null)
/* 162 */           style.decoration(color.decoration, TextDecoration.State.TRUE);  continue;
/*     */       } 
/* 164 */       if (fieldName.equals("shadow_color")) {
/* 165 */         style.shadowColor((ARGBLike)this.gson.fromJson(in, SerializerFactory.SHADOW_COLOR_TYPE)); continue;
/* 166 */       }  if (TextDecoration.NAMES.keys().contains(fieldName)) {
/* 167 */         style.decoration((TextDecoration)TextDecoration.NAMES.value(fieldName), GsonHacks.readBoolean(in)); continue;
/* 168 */       }  if (fieldName.equals("insertion")) {
/* 169 */         style.insertion(in.nextString()); continue;
/* 170 */       }  if (fieldName.equals("click_event") || fieldName.equals("clickEvent")) {
/* 171 */         in.beginObject();
/* 172 */         ClickEvent.Action action = null;
/* 173 */         String value = null;
/* 174 */         Key key = null;
/* 175 */         Integer page = null;
/* 176 */         while (in.hasNext()) {
/* 177 */           String clickEventField = in.nextName();
/* 178 */           if (clickEventField.equals("action")) {
/* 179 */             action = (ClickEvent.Action)this.gson.fromJson(in, SerializerFactory.CLICK_ACTION_TYPE); continue;
/* 180 */           }  if (clickEventField.equals("page")) {
/* 181 */             if (in.peek() == JsonToken.NUMBER) {
/* 182 */               page = Integer.valueOf(in.nextInt()); continue;
/* 183 */             }  if (in.peek() == JsonToken.STRING) {
/* 184 */               page = Integer.valueOf(Integer.parseInt(in.nextString())); continue;
/* 185 */             }  if (in.peek() == JsonToken.NULL) {
/* 186 */               throw ComponentSerializerImpl.notSureHowToDeserialize(clickEventField);
/*     */             }
/* 188 */             in.skipValue(); continue;
/*     */           } 
/* 190 */           if (clickEventField.equals("value") || clickEventField.equals("url") || clickEventField.equals("path") || clickEventField.equals("command") || clickEventField.equals("payload")) {
/* 191 */             if (in.peek() == JsonToken.NULL) {
/* 192 */               if (this.strictEventValues) {
/* 193 */                 throw ComponentSerializerImpl.notSureHowToDeserialize(clickEventField);
/*     */               }
/* 195 */               in.nextNull(); continue;
/*     */             } 
/* 197 */             value = in.nextString(); continue;
/*     */           } 
/* 199 */           if (clickEventField.equals("id")) {
/* 200 */             key = Key.key(in.nextString()); continue;
/*     */           } 
/* 202 */           in.skipValue();
/*     */         } 
/*     */         
/* 205 */         if (action != null && action.readable()) {
/* 206 */           switch (action) {
/*     */             case OPEN_URL:
/* 208 */               if (value != null) style.clickEvent(ClickEvent.openUrl(value)); 
/*     */               break;
/*     */             case RUN_COMMAND:
/* 211 */               if (value != null) style.clickEvent(ClickEvent.runCommand(value)); 
/*     */               break;
/*     */             case SUGGEST_COMMAND:
/* 214 */               if (value != null) style.clickEvent(ClickEvent.suggestCommand(value)); 
/*     */               break;
/*     */             case CHANGE_PAGE:
/* 217 */               if (page != null) style.clickEvent(ClickEvent.changePage(page.intValue())); 
/*     */               break;
/*     */             case COPY_TO_CLIPBOARD:
/* 220 */               if (value != null) style.clickEvent(ClickEvent.copyToClipboard(value)); 
/*     */               break;
/*     */             case CUSTOM:
/* 223 */               if (key != null && value != null) style.clickEvent(ClickEvent.custom(key, value));
/*     */               
/*     */               break;
/*     */           } 
/*     */ 
/*     */ 
/*     */         
/*     */         }
/* 231 */         in.endObject(); continue;
/* 232 */       }  if (fieldName.equals("hover_event") || fieldName.equals("hoverEvent")) {
/* 233 */         JsonObject hoverEventObject = (JsonObject)this.gson.fromJson(in, JsonObject.class);
/* 234 */         if (hoverEventObject != null) {
/* 235 */           JsonPrimitive serializedAction = hoverEventObject.getAsJsonPrimitive("action");
/* 236 */           if (serializedAction == null) {
/*     */             continue;
/*     */           }
/*     */ 
/*     */           
/* 241 */           HoverEvent.Action<Object> action = (HoverEvent.Action<Object>)this.gson.fromJson((JsonElement)serializedAction, SerializerFactory.HOVER_ACTION_TYPE);
/* 242 */           if (action.readable()) {
/*     */             Object value;
/* 244 */             Class<?> actionType = action.type();
/* 245 */             if (hoverEventObject.has("contents")) {
/* 246 */               JsonElement rawValue = hoverEventObject.get("contents");
/* 247 */               if (GsonHacks.isNullOrEmpty(rawValue)) {
/* 248 */                 if (this.strictEventValues) {
/* 249 */                   throw ComponentSerializerImpl.notSureHowToDeserialize(rawValue);
/*     */                 }
/* 251 */                 value = null;
/* 252 */               } else if (SerializerFactory.COMPONENT_TYPE.isAssignableFrom(actionType)) {
/* 253 */                 value = this.gson.fromJson(rawValue, SerializerFactory.COMPONENT_TYPE);
/* 254 */               } else if (SerializerFactory.SHOW_ITEM_TYPE.isAssignableFrom(actionType)) {
/* 255 */                 value = this.gson.fromJson(rawValue, SerializerFactory.SHOW_ITEM_TYPE);
/* 256 */               } else if (SerializerFactory.SHOW_ENTITY_TYPE.isAssignableFrom(actionType)) {
/* 257 */                 value = this.gson.fromJson(rawValue, SerializerFactory.SHOW_ENTITY_TYPE);
/*     */               } else {
/* 259 */                 value = null;
/*     */               } 
/* 261 */             } else if (hoverEventObject.has("value")) {
/* 262 */               JsonElement element = hoverEventObject.get("value");
/* 263 */               if (GsonHacks.isNullOrEmpty(element)) {
/* 264 */                 if (this.strictEventValues) {
/* 265 */                   throw ComponentSerializerImpl.notSureHowToDeserialize(element);
/*     */                 }
/* 267 */                 value = null;
/* 268 */               } else if (SerializerFactory.COMPONENT_TYPE.isAssignableFrom(actionType)) {
/* 269 */                 Component rawValue = (Component)this.gson.fromJson(element, SerializerFactory.COMPONENT_TYPE);
/* 270 */                 value = legacyHoverEventContents(action, rawValue);
/* 271 */               } else if (SerializerFactory.STRING_TYPE.isAssignableFrom(actionType)) {
/* 272 */                 value = this.gson.fromJson(element, SerializerFactory.STRING_TYPE);
/*     */               } else {
/* 274 */                 value = null;
/*     */               } 
/* 276 */             } else if (SerializerFactory.SHOW_ITEM_TYPE.isAssignableFrom(actionType)) {
/* 277 */               value = this.gson.fromJson((JsonElement)hoverEventObject, SerializerFactory.SHOW_ITEM_TYPE);
/* 278 */             } else if (SerializerFactory.SHOW_ENTITY_TYPE.isAssignableFrom(actionType)) {
/* 279 */               value = this.gson.fromJson((JsonElement)hoverEventObject, SerializerFactory.SHOW_ENTITY_TYPE);
/*     */             } else {
/* 281 */               if (this.strictEventValues) {
/* 282 */                 throw ComponentSerializerImpl.notSureHowToDeserialize(hoverEventObject);
/*     */               }
/* 284 */               value = null;
/*     */             } 
/*     */             
/* 287 */             if (value != null)
/* 288 */               style.hoverEvent((HoverEventSource)HoverEvent.hoverEvent(action, value)); 
/*     */           } 
/*     */         } 
/*     */         continue;
/*     */       } 
/* 293 */       in.skipValue();
/*     */     } 
/*     */ 
/*     */     
/* 297 */     in.endObject();
/* 298 */     return style.build();
/*     */   }
/*     */   
/*     */   private Object legacyHoverEventContents(HoverEvent.Action<?> action, Component rawValue) {
/* 302 */     if (action == HoverEvent.Action.SHOW_TEXT)
/* 303 */       return rawValue; 
/* 304 */     if (this.legacyHover != null) {
/*     */       try {
/* 306 */         if (action == HoverEvent.Action.SHOW_ENTITY)
/* 307 */           return this.legacyHover.deserializeShowEntity(rawValue, decoder()); 
/* 308 */         if (action == HoverEvent.Action.SHOW_ITEM) {
/* 309 */           return this.legacyHover.deserializeShowItem(rawValue);
/*     */         }
/* 311 */       } catch (IOException ex) {
/* 312 */         throw new JsonParseException(ex);
/*     */       } 
/*     */     }
/*     */     
/* 316 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   private Codec.Decoder<Component, String, JsonParseException> decoder() {
/* 320 */     return string -> (Component)this.gson.fromJson(string, SerializerFactory.COMPONENT_TYPE);
/*     */   }
/*     */   
/*     */   private Codec.Encoder<Component, String, JsonParseException> encoder() {
/* 324 */     return component -> this.gson.toJson(component, SerializerFactory.COMPONENT_TYPE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(JsonWriter out, Style value) throws IOException {
/* 329 */     out.beginObject();
/*     */     
/* 331 */     for (int i = 0, length = DECORATIONS.length; i < length; i++) {
/* 332 */       TextDecoration decoration = DECORATIONS[i];
/* 333 */       TextDecoration.State state = value.decoration(decoration);
/* 334 */       if (state != TextDecoration.State.NOT_SET) {
/* 335 */         String name = (String)TextDecoration.NAMES.key(decoration);
/* 336 */         assert name != null;
/* 337 */         out.name(name);
/* 338 */         out.value((state == TextDecoration.State.TRUE));
/*     */       } 
/*     */     } 
/*     */     
/* 342 */     TextColor color = value.color();
/* 343 */     if (color != null) {
/* 344 */       out.name("color");
/* 345 */       this.gson.toJson(color, SerializerFactory.COLOR_TYPE, out);
/*     */     } 
/*     */     
/* 348 */     ShadowColor shadowColor = value.shadowColor();
/* 349 */     if (shadowColor != null && this.emitShadowColor) {
/* 350 */       out.name("shadow_color");
/* 351 */       this.gson.toJson(shadowColor, SerializerFactory.SHADOW_COLOR_TYPE, out);
/*     */     } 
/*     */     
/* 354 */     String insertion = value.insertion();
/* 355 */     if (insertion != null) {
/* 356 */       out.name("insertion");
/* 357 */       out.value(insertion);
/*     */     } 
/*     */     
/* 360 */     ClickEvent clickEvent = value.clickEvent();
/* 361 */     if (clickEvent != null) {
/* 362 */       ClickEvent.Action action = clickEvent.action();
/*     */       
/* 364 */       if (this.emitSnakeCaseClick) {
/* 365 */         out.name("click_event");
/* 366 */         out.beginObject();
/* 367 */         out.name("action");
/* 368 */         this.gson.toJson(action, SerializerFactory.CLICK_ACTION_TYPE, out);
/*     */         
/* 370 */         if (action.readable()) {
/* 371 */           ClickEvent.Payload payload = clickEvent.payload();
/*     */           
/* 373 */           if (payload instanceof ClickEvent.Payload.Text) {
/* 374 */             switch (action) {
/*     */               case OPEN_URL:
/* 376 */                 out.name("url");
/*     */                 break;
/*     */               case RUN_COMMAND:
/*     */               case SUGGEST_COMMAND:
/* 380 */                 out.name("command");
/*     */                 break;
/*     */               case COPY_TO_CLIPBOARD:
/* 383 */                 out.name("value");
/*     */                 break;
/*     */             } 
/* 386 */             out.value(((ClickEvent.Payload.Text)payload).value());
/* 387 */           } else if (payload instanceof ClickEvent.Payload.Custom) {
/* 388 */             ClickEvent.Payload.Custom customPayload = (ClickEvent.Payload.Custom)payload;
/* 389 */             out.name("id");
/* 390 */             this.gson.toJson(customPayload.key(), SerializerFactory.KEY_TYPE, out);
/* 391 */             out.name("payload");
/* 392 */             out.value(customPayload.data());
/* 393 */           } else if (payload instanceof ClickEvent.Payload.Int) {
/* 394 */             ClickEvent.Payload.Int intPayload = (ClickEvent.Payload.Int)payload;
/* 395 */             out.name("page");
/* 396 */             if (this.emitStringPage) {
/* 397 */               out.value(String.valueOf(intPayload.integer()));
/*     */             } else {
/* 399 */               out.value(intPayload.integer());
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         
/* 404 */         out.endObject();
/*     */       } 
/*     */       
/* 407 */       if (this.emitCamelCaseClick && action.payloadType() == ClickEvent.Payload.Text.class) {
/* 408 */         out.name("clickEvent");
/* 409 */         out.beginObject();
/* 410 */         out.name("action");
/* 411 */         this.gson.toJson(action, SerializerFactory.CLICK_ACTION_TYPE, out);
/* 412 */         out.name("value");
/* 413 */         out.value(clickEvent.value());
/* 414 */         out.endObject();
/*     */       } 
/*     */     } 
/*     */     
/* 418 */     HoverEvent<?> hoverEvent = value.hoverEvent();
/* 419 */     if (hoverEvent != null && (((this.emitSnakeCaseHover || this.emitCamelCaseHover) && hoverEvent.action() != HoverEvent.Action.SHOW_ACHIEVEMENT) || this.emitValueFieldHover)) {
/* 420 */       HoverEvent.Action<?> action = hoverEvent.action();
/*     */       
/* 422 */       if (this.emitSnakeCaseHover && action != HoverEvent.Action.SHOW_ACHIEVEMENT) {
/* 423 */         out.name("hover_event");
/* 424 */         out.beginObject();
/*     */         
/* 426 */         out.name("action");
/* 427 */         this.gson.toJson(action, SerializerFactory.HOVER_ACTION_TYPE, out);
/*     */         
/* 429 */         if (action == HoverEvent.Action.SHOW_ITEM) {
/* 430 */           for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)this.gson.toJsonTree(hoverEvent.value(), SerializerFactory.SHOW_ITEM_TYPE).getAsJsonObject().entrySet()) {
/* 431 */             out.name(entry.getKey());
/* 432 */             this.gson.toJson(entry.getValue(), out);
/*     */           } 
/* 434 */         } else if (action == HoverEvent.Action.SHOW_ENTITY) {
/* 435 */           for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)this.gson.toJsonTree(hoverEvent.value(), SerializerFactory.SHOW_ENTITY_TYPE).getAsJsonObject().entrySet()) {
/* 436 */             out.name(entry.getKey());
/* 437 */             this.gson.toJson(entry.getValue(), out);
/*     */           } 
/* 439 */         } else if (action == HoverEvent.Action.SHOW_TEXT) {
/* 440 */           out.name("value");
/* 441 */           this.gson.toJson(hoverEvent.value(), SerializerFactory.COMPONENT_TYPE, out);
/*     */         } else {
/* 443 */           throw new JsonParseException("Don't know how to serialize " + hoverEvent.value());
/*     */         } 
/*     */         
/* 446 */         out.endObject();
/*     */       } 
/*     */       
/* 449 */       if (this.emitCamelCaseHover || this.emitValueFieldHover) {
/* 450 */         out.name("hoverEvent");
/* 451 */         out.beginObject();
/*     */         
/* 453 */         out.name("action");
/* 454 */         this.gson.toJson(action, SerializerFactory.HOVER_ACTION_TYPE, out);
/*     */         
/* 456 */         if (this.emitCamelCaseHover && action != HoverEvent.Action.SHOW_ACHIEVEMENT) {
/* 457 */           out.name("contents");
/* 458 */           if (action == HoverEvent.Action.SHOW_ITEM) {
/* 459 */             this.gson.toJson(hoverEvent.value(), SerializerFactory.SHOW_ITEM_TYPE, out);
/* 460 */           } else if (action == HoverEvent.Action.SHOW_ENTITY) {
/* 461 */             this.gson.toJson(hoverEvent.value(), SerializerFactory.SHOW_ENTITY_TYPE, out);
/* 462 */           } else if (action == HoverEvent.Action.SHOW_TEXT) {
/* 463 */             this.gson.toJson(hoverEvent.value(), SerializerFactory.COMPONENT_TYPE, out);
/*     */           } else {
/* 465 */             throw new JsonParseException("Don't know how to serialize " + hoverEvent.value());
/*     */           } 
/*     */         } 
/*     */         
/* 469 */         if (this.emitValueFieldHover) {
/* 470 */           out.name("value");
/* 471 */           serializeLegacyHoverEvent(hoverEvent, out);
/*     */         } 
/*     */         
/* 474 */         out.endObject();
/*     */       } 
/*     */     } 
/*     */     
/* 478 */     Key font = value.font();
/* 479 */     if (font != null) {
/* 480 */       out.name("font");
/* 481 */       this.gson.toJson(font, SerializerFactory.KEY_TYPE, out);
/*     */     } 
/*     */     
/* 484 */     out.endObject();
/*     */   }
/*     */   
/*     */   private void serializeLegacyHoverEvent(HoverEvent<?> hoverEvent, JsonWriter out) throws IOException {
/* 488 */     if (hoverEvent.action() == HoverEvent.Action.SHOW_TEXT) {
/* 489 */       this.gson.toJson(hoverEvent.value(), SerializerFactory.COMPONENT_TYPE, out);
/* 490 */     } else if (hoverEvent.action() == HoverEvent.Action.SHOW_ACHIEVEMENT) {
/* 491 */       this.gson.toJson(hoverEvent.value(), String.class, out);
/* 492 */     } else if (this.legacyHover != null) {
/* 493 */       Component serialized = null;
/*     */       try {
/* 495 */         if (hoverEvent.action() == HoverEvent.Action.SHOW_ENTITY) {
/* 496 */           serialized = this.legacyHover.serializeShowEntity((HoverEvent.ShowEntity)hoverEvent.value(), encoder());
/* 497 */         } else if (hoverEvent.action() == HoverEvent.Action.SHOW_ITEM) {
/* 498 */           serialized = this.legacyHover.serializeShowItem((HoverEvent.ShowItem)hoverEvent.value());
/*     */         } 
/* 500 */       } catch (IOException ex) {
/* 501 */         throw new JsonSyntaxException(ex);
/*     */       } 
/* 503 */       if (serialized != null) {
/* 504 */         this.gson.toJson(serialized, SerializerFactory.COMPONENT_TYPE, out);
/*     */       } else {
/* 506 */         out.nullValue();
/*     */       } 
/*     */     } else {
/* 509 */       out.nullValue();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\gson\StyleSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */