/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.json;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.option.Option;
/*     */ import ac.grim.grimac.shaded.kyori.option.OptionSchema;
/*     */ import ac.grim.grimac.shaded.kyori.option.OptionState;
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
/*     */ public final class JSONOptions
/*     */ {
/*     */   private static final int VERSION_INITIAL = 0;
/*     */   private static final int VERSION_1_16 = 2526;
/*     */   private static final int VERSION_1_20_3 = 3679;
/*     */   private static final int VERSION_1_20_5 = 3819;
/*     */   private static final int VERSION_1_21_4 = 4174;
/*     */   private static final int VERSION_1_21_5 = 4298;
/*     */   private static final int VERSION_1_21_6 = 4422;
/*  51 */   private static final OptionSchema.Mutable UNSAFE_SCHEMA = OptionSchema.globalSchema();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  61 */   public static final Option<Boolean> EMIT_RGB = Option.booleanOption(key("emit/rgb"), true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  68 */   public static final Option<HoverEventValueMode> EMIT_HOVER_EVENT_TYPE = UNSAFE_SCHEMA.enumOption(key("emit/hover_value_mode"), HoverEventValueMode.class, HoverEventValueMode.SNAKE_CASE);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  75 */   public static final Option<ClickEventValueMode> EMIT_CLICK_EVENT_TYPE = Option.enumOption(key("emit/click_value_mode"), ClickEventValueMode.class, ClickEventValueMode.SNAKE_CASE);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  83 */   public static final Option<Boolean> EMIT_COMPACT_TEXT_COMPONENT = UNSAFE_SCHEMA.booleanOption(key("emit/compact_text_component"), true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  91 */   public static final Option<Boolean> EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY = UNSAFE_SCHEMA.booleanOption(key("emit/hover_show_entity_id_as_int_array"), true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  98 */   public static final Option<Boolean> EMIT_HOVER_SHOW_ENTITY_KEY_AS_TYPE_AND_UUID_AS_ID = UNSAFE_SCHEMA.booleanOption(key("emit/hover_show_entity_key_as_type_and_uuid_as_id"), false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 107 */   public static final Option<Boolean> VALIDATE_STRICT_EVENTS = UNSAFE_SCHEMA.booleanOption(key("validate/strict_events"), true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 115 */   public static final Option<Boolean> EMIT_DEFAULT_ITEM_HOVER_QUANTITY = UNSAFE_SCHEMA.booleanOption(key("emit/default_item_hover_quantity"), true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 122 */   public static final Option<ShowItemHoverDataMode> SHOW_ITEM_HOVER_DATA_MODE = UNSAFE_SCHEMA.enumOption(key("emit/show_item_hover_data"), ShowItemHoverDataMode.class, ShowItemHoverDataMode.EMIT_EITHER);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 129 */   public static final Option<ShadowColorEmitMode> SHADOW_COLOR_MODE = UNSAFE_SCHEMA.enumOption(key("emit/shadow_color"), ShadowColorEmitMode.class, ShadowColorEmitMode.EMIT_INTEGER);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 136 */   public static final Option<Boolean> EMIT_CHANGE_PAGE_CLICK_EVENT_PAGE_AS_STRING = UNSAFE_SCHEMA.booleanOption(key("emit/change_page_click_event_page_as_string"), false);
/*     */ 
/*     */   
/* 139 */   private static final OptionSchema SCHEMA = OptionSchema.childSchema((OptionSchema)UNSAFE_SCHEMA).frozenView();
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
/*     */   private static final OptionState.Versioned BY_DATA_VERSION;
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
/*     */   static {
/* 188 */     BY_DATA_VERSION = SCHEMA.versionedStateBuilder().version(0, b -> b.value(EMIT_HOVER_EVENT_TYPE, HoverEventValueMode.VALUE_FIELD).value(EMIT_CLICK_EVENT_TYPE, ClickEventValueMode.CAMEL_CASE).value(EMIT_RGB, Boolean.valueOf(false)).value(EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, Boolean.valueOf(false)).value(EMIT_HOVER_SHOW_ENTITY_KEY_AS_TYPE_AND_UUID_AS_ID, Boolean.valueOf(true)).value(VALIDATE_STRICT_EVENTS, Boolean.valueOf(false)).value(EMIT_DEFAULT_ITEM_HOVER_QUANTITY, Boolean.valueOf(false)).value(SHOW_ITEM_HOVER_DATA_MODE, ShowItemHoverDataMode.EMIT_LEGACY_NBT).value(SHADOW_COLOR_MODE, ShadowColorEmitMode.NONE).value(EMIT_CHANGE_PAGE_CLICK_EVENT_PAGE_AS_STRING, Boolean.valueOf(true))).version(2526, b -> b.value(EMIT_HOVER_EVENT_TYPE, HoverEventValueMode.CAMEL_CASE).value(EMIT_RGB, Boolean.valueOf(true))).version(3679, b -> b.value(EMIT_COMPACT_TEXT_COMPONENT, Boolean.valueOf(true)).value(EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, Boolean.valueOf(true)).value(VALIDATE_STRICT_EVENTS, Boolean.valueOf(true))).version(3819, b -> b.value(EMIT_DEFAULT_ITEM_HOVER_QUANTITY, Boolean.valueOf(true)).value(SHOW_ITEM_HOVER_DATA_MODE, ShowItemHoverDataMode.EMIT_DATA_COMPONENTS)).version(4174, b -> b.value(SHADOW_COLOR_MODE, ShadowColorEmitMode.EMIT_INTEGER)).version(4298, b -> b.value(EMIT_HOVER_EVENT_TYPE, HoverEventValueMode.SNAKE_CASE).value(EMIT_CLICK_EVENT_TYPE, ClickEventValueMode.SNAKE_CASE).value(EMIT_HOVER_SHOW_ENTITY_KEY_AS_TYPE_AND_UUID_AS_ID, Boolean.valueOf(false))).version(4422, b -> b.value(EMIT_CHANGE_PAGE_CLICK_EVENT_PAGE_AS_STRING, Boolean.valueOf(false))).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 195 */   private static final OptionState MOST_COMPATIBLE = SCHEMA.stateBuilder()
/* 196 */     .value(EMIT_HOVER_EVENT_TYPE, HoverEventValueMode.ALL)
/* 197 */     .value(EMIT_CLICK_EVENT_TYPE, ClickEventValueMode.BOTH)
/* 198 */     .value(EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, Boolean.valueOf(false))
/* 199 */     .value(EMIT_COMPACT_TEXT_COMPONENT, Boolean.valueOf(false))
/* 200 */     .value(VALIDATE_STRICT_EVENTS, Boolean.valueOf(false))
/* 201 */     .value(SHOW_ITEM_HOVER_DATA_MODE, ShowItemHoverDataMode.EMIT_EITHER)
/* 202 */     .value(SHADOW_COLOR_MODE, ShadowColorEmitMode.EMIT_INTEGER)
/* 203 */     .build();
/*     */   
/*     */   private static String key(String value) {
/* 206 */     return "adventure:json/" + value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static OptionSchema schema() {
/* 216 */     return SCHEMA;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static OptionState.Versioned byDataVersion() {
/* 226 */     return BY_DATA_VERSION;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static OptionState compatibility() {
/* 238 */     return MOST_COMPATIBLE;
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
/*     */   public enum HoverEventValueMode
/*     */   {
/* 252 */     SNAKE_CASE,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 258 */     CAMEL_CASE,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 264 */     VALUE_FIELD,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 270 */     ALL;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/* 277 */     public static final HoverEventValueMode MODERN_ONLY = CAMEL_CASE;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/* 283 */     public static final HoverEventValueMode LEGACY_ONLY = VALUE_FIELD;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/* 289 */     public static final HoverEventValueMode BOTH = ALL;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     static {
/*     */     
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public enum ClickEventValueMode
/*     */   {
/* 303 */     SNAKE_CASE,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 309 */     CAMEL_CASE,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 315 */     BOTH;
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
/*     */   public enum ShowItemHoverDataMode
/*     */   {
/* 329 */     EMIT_LEGACY_NBT,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 335 */     EMIT_DATA_COMPONENTS,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 341 */     EMIT_EITHER;
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
/*     */   public enum ShadowColorEmitMode
/*     */   {
/* 354 */     NONE,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 360 */     EMIT_INTEGER,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 366 */     EMIT_ARRAY;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\json\JSONOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */