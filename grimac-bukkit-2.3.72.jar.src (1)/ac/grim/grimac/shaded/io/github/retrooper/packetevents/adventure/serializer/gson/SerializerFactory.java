/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.serializer.gson;
/*     */ 
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.option.OptionState;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.serializer.json.JSONOptions;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.serializer.json.LegacyHoverEventSerializer;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.BlockNBTComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TranslationArgument;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.ClickEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextDecoration;
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
/*     */ final class SerializerFactory
/*     */   implements TypeAdapterFactory
/*     */ {
/*  45 */   static final Class<Key> KEY_TYPE = Key.class;
/*  46 */   static final Class<Component> COMPONENT_TYPE = Component.class;
/*  47 */   static final Class<Style> STYLE_TYPE = Style.class;
/*  48 */   static final Class<ClickEvent.Action> CLICK_ACTION_TYPE = ClickEvent.Action.class;
/*  49 */   static final Class<HoverEvent.Action> HOVER_ACTION_TYPE = HoverEvent.Action.class;
/*  50 */   static final Class<HoverEvent.ShowItem> SHOW_ITEM_TYPE = HoverEvent.ShowItem.class;
/*  51 */   static final Class<HoverEvent.ShowEntity> SHOW_ENTITY_TYPE = HoverEvent.ShowEntity.class;
/*  52 */   static final Class<String> STRING_TYPE = String.class;
/*  53 */   static final Class<TextColorWrapper> COLOR_WRAPPER_TYPE = TextColorWrapper.class;
/*  54 */   static final Class<TextColor> COLOR_TYPE = TextColor.class;
/*  55 */   static final Class<TextDecoration> TEXT_DECORATION_TYPE = TextDecoration.class;
/*  56 */   static final Class<BlockNBTComponent.Pos> BLOCK_NBT_POS_TYPE = BlockNBTComponent.Pos.class;
/*  57 */   static final Class<UUID> UUID_TYPE = UUID.class;
/*     */   static final Class<?> TRANSLATION_ARGUMENT_TYPE;
/*     */   private final OptionState features;
/*     */   
/*     */   static {
/*  62 */     if (BackwardCompatUtil.IS_4_15_0_OR_NEWER) {
/*  63 */       TRANSLATION_ARGUMENT_TYPE = TranslationArgument.class;
/*     */     } else {
/*  65 */       TRANSLATION_ARGUMENT_TYPE = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final LegacyHoverEventSerializer legacyHoverSerializer;
/*     */ 
/*     */ 
/*     */   
/*     */   private final BackwardCompatUtil.ShowAchievementToComponent compatShowAchievement;
/*     */ 
/*     */ 
/*     */   
/*     */   SerializerFactory(OptionState features, LegacyHoverEventSerializer legacyHoverSerializer, @Nullable BackwardCompatUtil.ShowAchievementToComponent compatShowAchievement) {
/*  81 */     this.features = features;
/*  82 */     this.legacyHoverSerializer = legacyHoverSerializer;
/*  83 */     this.compatShowAchievement = compatShowAchievement;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
/*  90 */     Class<? super T> rawType = type.getRawType();
/*  91 */     if (COMPONENT_TYPE.isAssignableFrom(rawType))
/*  92 */       return (TypeAdapter)ComponentSerializerImpl.create(this.features, gson); 
/*  93 */     if (KEY_TYPE.isAssignableFrom(rawType))
/*  94 */       return (TypeAdapter)KeySerializer.INSTANCE; 
/*  95 */     if (STYLE_TYPE.isAssignableFrom(rawType))
/*     */     {
/*  97 */       return (TypeAdapter)StyleSerializer.create(this.legacyHoverSerializer, this.compatShowAchievement, this.features, gson);
/*     */     }
/*  99 */     if (CLICK_ACTION_TYPE.isAssignableFrom(rawType))
/* 100 */       return (TypeAdapter)ClickEventActionSerializer.INSTANCE; 
/* 101 */     if (HOVER_ACTION_TYPE.isAssignableFrom(rawType))
/* 102 */       return (TypeAdapter)HoverEventActionSerializer.INSTANCE; 
/* 103 */     if (SHOW_ITEM_TYPE.isAssignableFrom(rawType))
/* 104 */       return (TypeAdapter)ShowItemSerializer.create(gson, this.features); 
/* 105 */     if (SHOW_ENTITY_TYPE.isAssignableFrom(rawType))
/* 106 */       return (TypeAdapter)ShowEntitySerializer.create(gson); 
/* 107 */     if (COLOR_WRAPPER_TYPE.isAssignableFrom(rawType))
/* 108 */       return TextColorWrapper.Serializer.INSTANCE; 
/* 109 */     if (COLOR_TYPE.isAssignableFrom(rawType))
/* 110 */       return ((Boolean)this.features.value(JSONOptions.EMIT_RGB)).booleanValue() ? (TypeAdapter)TextColorSerializer.INSTANCE : (TypeAdapter)TextColorSerializer.DOWNSAMPLE_COLOR; 
/* 111 */     if (TEXT_DECORATION_TYPE.isAssignableFrom(rawType))
/* 112 */       return (TypeAdapter)TextDecorationSerializer.INSTANCE; 
/* 113 */     if (BLOCK_NBT_POS_TYPE.isAssignableFrom(rawType)) {
/* 114 */       return (TypeAdapter)BlockNBTComponentPosSerializer.INSTANCE;
/*     */     }
/*     */     
/* 117 */     if (BackwardCompatUtil.IS_4_15_0_OR_NEWER) {
/* 118 */       if (UUID_TYPE.isAssignableFrom(rawType))
/* 119 */         return (TypeAdapter)UUIDSerializer.uuidSerializer(this.features); 
/* 120 */       if (TRANSLATION_ARGUMENT_TYPE.isAssignableFrom(rawType)) {
/* 121 */         return (TypeAdapter)TranslationArgumentSerializer.create(gson);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 126 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\adventure\serializer\gson\SerializerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */