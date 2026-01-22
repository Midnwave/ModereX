/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.standard;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.InvalidKeyException;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Keyed;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.nbt.api.BinaryTagHolder;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.DataComponentValue;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.StyleBuilderApplicable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.Context;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.ParsingException;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.StyleClaim;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class HoverTag
/*     */ {
/*     */   private static final String HOVER = "hover";
/*  55 */   static final TagResolver RESOLVER = SerializableResolver.claimingStyle("hover", HoverTag::create, 
/*     */ 
/*     */       
/*  58 */       StyleClaim.claim("hover", Style::hoverEvent, HoverTag::emit));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Tag create(ArgumentQueue args, Context ctx) throws ParsingException {
/*  66 */     String actionName = args.popOr("Hover event requires an action as its first argument").value();
/*  67 */     HoverEvent.Action<Object> action = (HoverEvent.Action<Object>)HoverEvent.Action.NAMES.value(actionName);
/*  68 */     ActionHandler<Object> value = actionHandler(action);
/*  69 */     if (value == null) {
/*  70 */       throw ctx.newException("Don't know how to turn '" + args + "' into a hover event", args);
/*     */     }
/*     */     
/*  73 */     return Tag.styling(new StyleBuilderApplicable[] { (StyleBuilderApplicable)HoverEvent.hoverEvent(action, value.parse(args, ctx)) });
/*     */   }
/*     */ 
/*     */   
/*     */   static void emit(HoverEvent<?> event, TokenEmitter emitter) {
/*  78 */     ActionHandler<Object> handler = actionHandler(event.action());
/*  79 */     emitter.tag("hover").argument((String)HoverEvent.Action.NAMES.key(event.action()));
/*  80 */     handler.emit(event.value(), emitter);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   static <V> ActionHandler<V> actionHandler(HoverEvent.Action<V> action) {
/*  85 */     ActionHandler<?> ret = null;
/*  86 */     if (action == HoverEvent.Action.SHOW_TEXT) {
/*  87 */       ret = ShowText.INSTANCE;
/*  88 */     } else if (action == HoverEvent.Action.SHOW_ITEM) {
/*  89 */       ret = ShowItem.INSTANCE;
/*  90 */     } else if (action == HoverEvent.Action.SHOW_ENTITY) {
/*  91 */       ret = ShowEntity.INSTANCE;
/*     */     } 
/*     */     
/*  94 */     return (ActionHandler)ret;
/*     */   }
/*     */   
/*     */   static interface ActionHandler<V> {
/*     */     @NotNull
/*     */     V parse(@NotNull ArgumentQueue param1ArgumentQueue, @NotNull Context param1Context) throws ParsingException;
/*     */     
/*     */     void emit(V param1V, TokenEmitter param1TokenEmitter); }
/*     */   
/*     */   static final class ShowText implements ActionHandler<Component> {
/* 104 */     private static final ShowText INSTANCE = new ShowText();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public Component parse(@NotNull ArgumentQueue args, @NotNull Context ctx) throws ParsingException {
/* 111 */       return ctx.deserialize(args.popOr("show_text action requires a message").value());
/*     */     }
/*     */ 
/*     */     
/*     */     public void emit(Component event, TokenEmitter emit) {
/* 116 */       emit.argument(event);
/*     */     }
/*     */   }
/*     */   
/*     */   static final class ShowItem implements ActionHandler<HoverEvent.ShowItem> {
/* 121 */     private static final ShowItem INSTANCE = new ShowItem();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public HoverEvent.ShowItem parse(@NotNull ArgumentQueue args, @NotNull Context ctx) throws ParsingException {
/*     */       try {
/* 130 */         Key key = Key.key(args.popOr("Show item hover needs at least an item ID").value());
/* 131 */         int count = args.hasNext() ? args.pop().asInt().<Throwable>orElseThrow(() -> ctx.newException("The count argument was not a valid integer")) : 1;
/* 132 */         if (args.hasNext()) {
/*     */ 
/*     */ 
/*     */           
/* 136 */           String value = args.peek().value();
/* 137 */           if (value.startsWith("{")) {
/* 138 */             args.pop();
/* 139 */             return legacyShowItem(key, count, value);
/*     */           } 
/*     */           
/* 142 */           Map<Key, DataComponentValue> datas = new HashMap<>();
/* 143 */           while (args.hasNext()) {
/*     */             
/* 145 */             Key dataKey = Key.key(args.pop().value());
/* 146 */             String dataVal = args.popOr("a value was expected for key " + dataKey).value();
/* 147 */             datas.put(dataKey, BinaryTagHolder.binaryTagHolder(dataVal));
/*     */           } 
/* 149 */           return HoverEvent.ShowItem.showItem((Keyed)key, count, datas);
/*     */         } 
/* 151 */         return HoverEvent.ShowItem.showItem(key, count);
/*     */       }
/* 153 */       catch (InvalidKeyException|NumberFormatException ex) {
/* 154 */         throw ctx.newException("Exception parsing show_item hover", ex, args);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     private static HoverEvent.ShowItem legacyShowItem(Key id, int count, String value) {
/* 160 */       return HoverEvent.ShowItem.showItem(id, count, BinaryTagHolder.binaryTagHolder(value));
/*     */     }
/*     */ 
/*     */     
/*     */     public void emit(HoverEvent.ShowItem event, TokenEmitter emit) {
/* 165 */       emit.argument(HoverTag.compactAsString(event.item()));
/*     */       
/* 167 */       if (event.count() != 1 || hasLegacy(event) || !event.dataComponents().isEmpty()) {
/* 168 */         emit.argument(Integer.toString(event.count()));
/*     */         
/* 170 */         if (hasLegacy(event)) {
/* 171 */           emitLegacyHover(event, emit);
/*     */         } else {
/* 173 */           for (Map.Entry<Key, DataComponentValue.TagSerializable> entry : (Iterable<Map.Entry<Key, DataComponentValue.TagSerializable>>)event.dataComponentsAs(DataComponentValue.TagSerializable.class).entrySet()) {
/* 174 */             emit.argument(((Key)entry.getKey()).asMinimalString());
/* 175 */             emit.argument(((DataComponentValue.TagSerializable)entry.getValue()).asBinaryTag().string());
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     static boolean hasLegacy(HoverEvent.ShowItem event) {
/* 183 */       return (event.nbt() != null);
/*     */     }
/*     */ 
/*     */     
/*     */     static void emitLegacyHover(HoverEvent.ShowItem event, TokenEmitter emit) {
/* 188 */       if (event.nbt() != null)
/* 189 */         emit.argument(event.nbt().string()); 
/*     */     }
/*     */   }
/*     */   
/*     */   static final class ShowEntity
/*     */     implements ActionHandler<HoverEvent.ShowEntity> {
/* 195 */     static final ShowEntity INSTANCE = new ShowEntity();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public HoverEvent.ShowEntity parse(@NotNull ArgumentQueue args, @NotNull Context ctx) throws ParsingException {
/*     */       try {
/* 203 */         Key key = Key.key(args.popOr("Show entity needs a type argument").value());
/* 204 */         UUID id = UUID.fromString(args.popOr("Show entity needs an entity UUID").value());
/* 205 */         if (args.hasNext()) {
/* 206 */           Component name = ctx.deserialize(args.pop().value());
/* 207 */           return HoverEvent.ShowEntity.showEntity(key, id, name);
/*     */         } 
/* 209 */         return HoverEvent.ShowEntity.showEntity(key, id);
/* 210 */       } catch (IllegalArgumentException|InvalidKeyException ex) {
/* 211 */         throw ctx.newException("Exception parsing show_entity hover", ex, args);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void emit(HoverEvent.ShowEntity event, TokenEmitter emit) {
/* 217 */       emit.argument(HoverTag.compactAsString(event.type()))
/* 218 */         .argument(event.id().toString());
/*     */       
/* 220 */       if (event.name() != null)
/* 221 */         emit.argument(event.name()); 
/*     */     }
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   static String compactAsString(@NotNull Key key) {
/* 227 */     if (key.namespace().equals("minecraft")) {
/* 228 */       return key.value();
/*     */     }
/* 230 */     return key.asString();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\standard\HoverTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */