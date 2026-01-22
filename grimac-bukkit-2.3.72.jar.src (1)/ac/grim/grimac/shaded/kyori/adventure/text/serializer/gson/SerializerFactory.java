/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.gson;
/*     */ 
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.BlockNBTComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TranslationArgument;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.ClickEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.ShadowColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextDecoration;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.json.JSONOptions;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.json.LegacyHoverEventSerializer;
/*     */ import ac.grim.grimac.shaded.kyori.option.OptionState;
/*     */ import com.google.gson.Gson;
/*     */ import com.google.gson.TypeAdapter;
/*     */ import com.google.gson.TypeAdapterFactory;
/*     */ import com.google.gson.reflect.TypeToken;
/*     */ import java.util.UUID;
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
/*     */ final class SerializerFactory
/*     */   implements TypeAdapterFactory
/*     */ {
/*  46 */   static final Class<Key> KEY_TYPE = Key.class;
/*  47 */   static final Class<Component> COMPONENT_TYPE = Component.class;
/*  48 */   static final Class<Style> STYLE_TYPE = Style.class;
/*  49 */   static final Class<ClickEvent.Action> CLICK_ACTION_TYPE = ClickEvent.Action.class;
/*  50 */   static final Class<HoverEvent.Action> HOVER_ACTION_TYPE = HoverEvent.Action.class;
/*  51 */   static final Class<HoverEvent.ShowItem> SHOW_ITEM_TYPE = HoverEvent.ShowItem.class;
/*  52 */   static final Class<HoverEvent.ShowEntity> SHOW_ENTITY_TYPE = HoverEvent.ShowEntity.class;
/*  53 */   static final Class<String> STRING_TYPE = String.class;
/*  54 */   static final Class<TextColorWrapper> COLOR_WRAPPER_TYPE = TextColorWrapper.class;
/*  55 */   static final Class<TextColor> COLOR_TYPE = TextColor.class;
/*  56 */   static final Class<ShadowColor> SHADOW_COLOR_TYPE = ShadowColor.class;
/*  57 */   static final Class<TextDecoration> TEXT_DECORATION_TYPE = TextDecoration.class;
/*  58 */   static final Class<BlockNBTComponent.Pos> BLOCK_NBT_POS_TYPE = BlockNBTComponent.Pos.class;
/*  59 */   static final Class<UUID> UUID_TYPE = UUID.class;
/*  60 */   static final Class<TranslationArgument> TRANSLATION_ARGUMENT_TYPE = TranslationArgument.class;
/*     */   
/*     */   private final OptionState features;
/*     */   private final LegacyHoverEventSerializer legacyHoverSerializer;
/*     */   
/*     */   SerializerFactory(OptionState features, LegacyHoverEventSerializer legacyHoverSerializer) {
/*  66 */     this.features = features;
/*  67 */     this.legacyHoverSerializer = legacyHoverSerializer;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
/*  73 */     Class<? super T> rawType = type.getRawType();
/*  74 */     if (COMPONENT_TYPE.isAssignableFrom(rawType))
/*  75 */       return (TypeAdapter)ComponentSerializerImpl.create(this.features, gson); 
/*  76 */     if (KEY_TYPE.isAssignableFrom(rawType))
/*  77 */       return (TypeAdapter)KeySerializer.INSTANCE; 
/*  78 */     if (STYLE_TYPE.isAssignableFrom(rawType))
/*  79 */       return (TypeAdapter)StyleSerializer.create(this.legacyHoverSerializer, this.features, gson); 
/*  80 */     if (CLICK_ACTION_TYPE.isAssignableFrom(rawType))
/*  81 */       return (TypeAdapter)ClickEventActionSerializer.INSTANCE; 
/*  82 */     if (HOVER_ACTION_TYPE.isAssignableFrom(rawType))
/*  83 */       return (TypeAdapter)HoverEventActionSerializer.INSTANCE; 
/*  84 */     if (SHOW_ITEM_TYPE.isAssignableFrom(rawType))
/*  85 */       return (TypeAdapter)ShowItemSerializer.create(gson, this.features); 
/*  86 */     if (SHOW_ENTITY_TYPE.isAssignableFrom(rawType))
/*  87 */       return (TypeAdapter)ShowEntitySerializer.create(gson, this.features); 
/*  88 */     if (COLOR_WRAPPER_TYPE.isAssignableFrom(rawType))
/*  89 */       return TextColorWrapper.Serializer.INSTANCE; 
/*  90 */     if (COLOR_TYPE.isAssignableFrom(rawType))
/*  91 */       return ((Boolean)this.features.value(JSONOptions.EMIT_RGB)).booleanValue() ? (TypeAdapter)TextColorSerializer.INSTANCE : (TypeAdapter)TextColorSerializer.DOWNSAMPLE_COLOR; 
/*  92 */     if (SHADOW_COLOR_TYPE.isAssignableFrom(rawType))
/*  93 */       return (TypeAdapter)ShadowColorSerializer.create(this.features); 
/*  94 */     if (TEXT_DECORATION_TYPE.isAssignableFrom(rawType))
/*  95 */       return (TypeAdapter)TextDecorationSerializer.INSTANCE; 
/*  96 */     if (BLOCK_NBT_POS_TYPE.isAssignableFrom(rawType))
/*  97 */       return (TypeAdapter)BlockNBTComponentPosSerializer.INSTANCE; 
/*  98 */     if (UUID_TYPE.isAssignableFrom(rawType))
/*  99 */       return (TypeAdapter)UUIDSerializer.uuidSerializer(this.features); 
/* 100 */     if (TRANSLATION_ARGUMENT_TYPE.isAssignableFrom(rawType)) {
/* 101 */       return (TypeAdapter)TranslationArgumentSerializer.create(gson);
/*     */     }
/* 103 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\gson\SerializerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */