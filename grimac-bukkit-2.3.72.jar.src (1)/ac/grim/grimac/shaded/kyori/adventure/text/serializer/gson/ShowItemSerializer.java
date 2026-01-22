/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.gson;
/*     */ 
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Keyed;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.nbt.api.BinaryTagHolder;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.DataComponentValue;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.json.JSONOptions;
/*     */ import ac.grim.grimac.shaded.kyori.option.OptionState;
/*     */ import com.google.gson.Gson;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonParseException;
/*     */ import com.google.gson.TypeAdapter;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonToken;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
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
/*     */ final class ShowItemSerializer
/*     */   extends TypeAdapter<HoverEvent.ShowItem>
/*     */ {
/*     */   private static final String LEGACY_SHOW_ITEM_TAG = "tag";
/*     */   private static final String DATA_COMPONENT_REMOVAL_PREFIX = "!";
/*     */   private final Gson gson;
/*     */   private final boolean emitDefaultQuantity;
/*     */   private final JSONOptions.ShowItemHoverDataMode itemDataMode;
/*     */   
/*     */   static TypeAdapter<HoverEvent.ShowItem> create(Gson gson, OptionState opt) {
/*  60 */     return (new ShowItemSerializer(gson, ((Boolean)opt.value(JSONOptions.EMIT_DEFAULT_ITEM_HOVER_QUANTITY)).booleanValue(), (JSONOptions.ShowItemHoverDataMode)opt.value(JSONOptions.SHOW_ITEM_HOVER_DATA_MODE))).nullSafe();
/*     */   }
/*     */   
/*     */   private ShowItemSerializer(Gson gson, boolean emitDefaultQuantity, JSONOptions.ShowItemHoverDataMode itemDataMode) {
/*  64 */     this.gson = gson;
/*  65 */     this.emitDefaultQuantity = emitDefaultQuantity;
/*  66 */     this.itemDataMode = itemDataMode;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HoverEvent.ShowItem read(JsonReader in) throws IOException {
/*  72 */     in.beginObject();
/*     */     
/*  74 */     Key key = null;
/*  75 */     int count = 1;
/*  76 */     BinaryTagHolder nbt = null;
/*  77 */     Map<Key, DataComponentValue> dataComponents = null;
/*     */     
/*  79 */     while (in.hasNext()) {
/*  80 */       String fieldName = in.nextName();
/*  81 */       if (fieldName.equals("id")) {
/*  82 */         key = (Key)this.gson.fromJson(in, SerializerFactory.KEY_TYPE); continue;
/*  83 */       }  if (fieldName.equals("count")) {
/*  84 */         count = in.nextInt(); continue;
/*  85 */       }  if (fieldName.equals("tag")) {
/*  86 */         JsonToken token = in.peek();
/*  87 */         if (token == JsonToken.STRING || token == JsonToken.NUMBER) {
/*  88 */           nbt = BinaryTagHolder.binaryTagHolder(in.nextString()); continue;
/*  89 */         }  if (token == JsonToken.BOOLEAN) {
/*  90 */           nbt = BinaryTagHolder.binaryTagHolder(String.valueOf(in.nextBoolean())); continue;
/*  91 */         }  if (token == JsonToken.NULL) {
/*  92 */           in.nextNull(); continue;
/*     */         } 
/*  94 */         throw new JsonParseException("Expected tag to be a string");
/*     */       } 
/*  96 */       if (fieldName.equals("components")) {
/*  97 */         in.beginObject();
/*  98 */         while (in.peek() != JsonToken.END_OBJECT) {
/*  99 */           Key id; boolean removed; String name = in.nextName();
/*     */ 
/*     */           
/* 102 */           if (name.startsWith("!")) {
/* 103 */             id = Key.key(name.substring(1));
/* 104 */             removed = true;
/*     */           } else {
/* 106 */             id = Key.key(name);
/* 107 */             removed = false;
/*     */           } 
/*     */           
/* 110 */           JsonElement tree = (JsonElement)this.gson.fromJson(in, JsonElement.class);
/* 111 */           if (dataComponents == null) {
/* 112 */             dataComponents = new HashMap<>();
/*     */           }
/* 114 */           dataComponents.put(id, removed ? (DataComponentValue)DataComponentValue.removed() : GsonDataComponentValue.gsonDataComponentValue(tree));
/*     */         } 
/* 116 */         in.endObject(); continue;
/*     */       } 
/* 118 */       in.skipValue();
/*     */     } 
/*     */ 
/*     */     
/* 122 */     if (key == null) {
/* 123 */       throw new JsonParseException("Not sure how to deserialize show_item hover event");
/*     */     }
/* 125 */     in.endObject();
/*     */     
/* 127 */     if (dataComponents != null) {
/* 128 */       return HoverEvent.ShowItem.showItem((Keyed)key, count, dataComponents);
/*     */     }
/* 130 */     return HoverEvent.ShowItem.showItem(key, count, nbt);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(JsonWriter out, HoverEvent.ShowItem value) throws IOException {
/* 136 */     out.beginObject();
/*     */     
/* 138 */     out.name("id");
/* 139 */     this.gson.toJson(value.item(), SerializerFactory.KEY_TYPE, out);
/*     */     
/* 141 */     int count = value.count();
/* 142 */     if (count != 1 || this.emitDefaultQuantity) {
/* 143 */       out.name("count");
/* 144 */       out.value(count);
/*     */     } 
/*     */     
/* 147 */     Map<Key, DataComponentValue> dataComponents = value.dataComponents();
/* 148 */     if (!dataComponents.isEmpty() && this.itemDataMode != JSONOptions.ShowItemHoverDataMode.EMIT_LEGACY_NBT) {
/* 149 */       out.name("components");
/* 150 */       out.beginObject();
/* 151 */       for (Map.Entry<Key, GsonDataComponentValue> entry : (Iterable<Map.Entry<Key, GsonDataComponentValue>>)value.dataComponentsAs(GsonDataComponentValue.class).entrySet()) {
/* 152 */         JsonElement el = ((GsonDataComponentValue)entry.getValue()).element();
/* 153 */         if (el instanceof com.google.gson.JsonNull) {
/* 154 */           out.name("!" + ((Key)entry.getKey()).asString());
/* 155 */           out.beginObject().endObject(); continue;
/*     */         } 
/* 157 */         out.name(((Key)entry.getKey()).asString());
/* 158 */         this.gson.toJson(el, out);
/*     */       } 
/*     */       
/* 161 */       out.endObject();
/* 162 */     } else if (this.itemDataMode != JSONOptions.ShowItemHoverDataMode.EMIT_DATA_COMPONENTS) {
/* 163 */       maybeWriteLegacy(out, value);
/*     */     } 
/*     */     
/* 166 */     out.endObject();
/*     */   }
/*     */ 
/*     */   
/*     */   private static void maybeWriteLegacy(JsonWriter out, HoverEvent.ShowItem value) throws IOException {
/* 171 */     BinaryTagHolder nbt = value.nbt();
/* 172 */     if (nbt != null) {
/* 173 */       out.name("tag");
/* 174 */       out.value(nbt.string());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\gson\ShowItemSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */