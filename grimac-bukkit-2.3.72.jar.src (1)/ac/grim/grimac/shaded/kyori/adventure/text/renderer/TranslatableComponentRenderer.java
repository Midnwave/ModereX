/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.renderer;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.BlockNBTComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.ComponentBuilder;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.ComponentLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.EntityNBTComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.KeybindComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.ScoreComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.SelectorComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.StorageNBTComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TextComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TranslatableComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TranslationArgument;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEventSource;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.translation.Translator;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.TriState;
/*     */ import java.text.AttributedCharacterIterator;
/*     */ import java.text.FieldPosition;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumSet;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Objects;
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
/*     */ public abstract class TranslatableComponentRenderer<C>
/*     */   extends AbstractComponentRenderer<C>
/*     */ {
/*     */   private static final Set<Style.Merge> MERGES;
/*     */   
/*     */   static {
/*  68 */     Set<Style.Merge> merges = EnumSet.allOf(Style.Merge.class);
/*  69 */     merges.remove(Style.Merge.EVENTS);
/*  70 */     MERGES = Collections.unmodifiableSet(merges);
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
/*     */   @NotNull
/*     */   public static TranslatableComponentRenderer<Locale> usingTranslationSource(@NotNull final Translator source) {
/*  84 */     Objects.requireNonNull(source, "source");
/*  85 */     return new TranslatableComponentRenderer<Locale>() {
/*     */         @Nullable
/*     */         protected MessageFormat translate(@NotNull String key, @NotNull Locale context) {
/*  88 */           return source.translate(key, context);
/*     */         }
/*     */         @NotNull
/*     */         protected Component renderTranslatableInner(@NotNull TranslatableComponent component, @NotNull Locale context) {
/*     */           Component translated;
/*  93 */           TriState anyTranslations = source.hasAnyTranslations();
/*  94 */           if (anyTranslations == TriState.FALSE) return (Component)component;
/*     */ 
/*     */           
/*  97 */           if (source.canTranslate(component.key(), context)) {
/*  98 */             translated = source.translate(component, context);
/*     */           } else {
/* 100 */             translated = null;
/*     */           } 
/* 102 */           return (translated != null) ? render(translated, context) : super.renderTranslatableInner(component, context);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected MessageFormat translate(@NotNull String key, @NotNull C context) {
/* 115 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected MessageFormat translate(@NotNull String key, @Nullable String fallback, @NotNull C context) {
/* 127 */     return translate(key, context);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected Component renderBlockNbt(@NotNull BlockNBTComponent component, @NotNull C context) {
/* 133 */     BlockNBTComponent.Builder builder = ((BlockNBTComponent.Builder)nbt(context, Component.blockNBT(), component)).pos(component.pos());
/* 134 */     return mergeStyleAndOptionallyDeepRender((Component)component, builder, context);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected Component renderEntityNbt(@NotNull EntityNBTComponent component, @NotNull C context) {
/* 140 */     EntityNBTComponent.Builder builder = ((EntityNBTComponent.Builder)nbt(context, Component.entityNBT(), component)).selector(component.selector());
/* 141 */     return mergeStyleAndOptionallyDeepRender((Component)component, builder, context);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected Component renderStorageNbt(@NotNull StorageNBTComponent component, @NotNull C context) {
/* 147 */     StorageNBTComponent.Builder builder = ((StorageNBTComponent.Builder)nbt(context, Component.storageNBT(), component)).storage(component.storage());
/* 148 */     return mergeStyleAndOptionallyDeepRender((Component)component, builder, context);
/*     */   }
/*     */   
/*     */   protected <O extends ac.grim.grimac.shaded.kyori.adventure.text.NBTComponent<O, B>, B extends ac.grim.grimac.shaded.kyori.adventure.text.NBTComponentBuilder<O, B>> B nbt(@NotNull C context, B builder, O oldComponent) {
/* 152 */     builder
/* 153 */       .nbtPath(oldComponent.nbtPath())
/* 154 */       .interpret(oldComponent.interpret());
/* 155 */     Component separator = oldComponent.separator();
/* 156 */     if (separator != null) {
/* 157 */       builder.separator((ComponentLike)render(separator, context));
/*     */     }
/* 159 */     return builder;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected Component renderKeybind(@NotNull KeybindComponent component, @NotNull C context) {
/* 164 */     KeybindComponent.Builder builder = Component.keybind().keybind(component.keybind());
/* 165 */     return mergeStyleAndOptionallyDeepRender((Component)component, builder, context);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected Component renderScore(@NotNull ScoreComponent component, @NotNull C context) {
/* 174 */     ScoreComponent.Builder builder = Component.score().name(component.name()).objective(component.objective()).value(component.value());
/* 175 */     return mergeStyleAndOptionallyDeepRender((Component)component, builder, context);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected Component renderSelector(@NotNull SelectorComponent component, @NotNull C context) {
/* 180 */     SelectorComponent.Builder builder = Component.selector().pattern(component.pattern());
/* 181 */     return mergeStyleAndOptionallyDeepRender((Component)component, builder, context);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected Component renderText(@NotNull TextComponent component, @NotNull C context) {
/* 186 */     TextComponent.Builder builder = Component.text().content(component.content());
/* 187 */     return mergeStyleAndOptionallyDeepRender((Component)component, builder, context);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected Component renderTranslatable(@NotNull TranslatableComponent component, @NotNull C context) {
/* 192 */     List<TranslationArgument> arguments = component.arguments();
/* 193 */     List<Component> children = component.children();
/*     */     
/* 195 */     if (!arguments.isEmpty() || !children.isEmpty()) {
/* 196 */       TranslatableComponent.Builder builder = (TranslatableComponent.Builder)component.toBuilder();
/*     */       
/* 198 */       if (!arguments.isEmpty()) {
/* 199 */         List<TranslationArgument> translatedArguments = new ArrayList<>(arguments);
/* 200 */         for (int i = 0; i < translatedArguments.size(); i++) {
/* 201 */           TranslationArgument arg = translatedArguments.get(i);
/* 202 */           if (arg.value() instanceof Component && !(arg.value() instanceof ac.grim.grimac.shaded.kyori.adventure.text.VirtualComponent)) {
/* 203 */             translatedArguments.set(i, TranslationArgument.component((ComponentLike)render((Component)arg.value(), context)));
/*     */           }
/*     */         } 
/*     */         
/* 207 */         builder.arguments(translatedArguments);
/*     */       } 
/*     */       
/* 210 */       component = (TranslatableComponent)builder.build();
/*     */     } 
/*     */     
/* 213 */     return renderTranslatableInner(component, context);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected Component renderTranslatableInner(@NotNull TranslatableComponent component, @NotNull C context) {
/* 218 */     MessageFormat format = translate(component.key(), component.fallback(), context);
/* 219 */     if (format == null) return optionallyRenderChildrenAndStyle((Component)component, context);
/*     */     
/* 221 */     List<TranslationArgument> args = component.arguments();
/*     */     
/* 223 */     TextComponent.Builder builder = Component.text();
/* 224 */     mergeStyle((Component)component, builder, context);
/*     */ 
/*     */     
/* 227 */     if (args.isEmpty()) {
/* 228 */       builder.content(format.format((Object[])null, new StringBuffer(), (FieldPosition)null).toString());
/* 229 */       return optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
/*     */     } 
/*     */     
/* 232 */     Object[] nulls = new Object[args.size()];
/* 233 */     StringBuffer sb = format.format(nulls, new StringBuffer(), (FieldPosition)null);
/* 234 */     AttributedCharacterIterator it = format.formatToCharacterIterator(nulls);
/*     */     
/* 236 */     while (it.getIndex() < it.getEndIndex()) {
/* 237 */       int end = it.getRunLimit();
/* 238 */       Integer index = (Integer)it.getAttribute(MessageFormat.Field.ARGUMENT);
/* 239 */       if (index != null) {
/* 240 */         TranslationArgument arg = args.get(index.intValue());
/* 241 */         builder.append(arg.asComponent());
/*     */       } else {
/* 243 */         builder.append((Component)Component.text(sb.substring(it.getIndex(), end)));
/*     */       } 
/* 245 */       it.setIndex(end);
/*     */     } 
/*     */     
/* 248 */     return optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
/*     */   }
/*     */   
/*     */   protected Component optionallyRenderChildrenAndStyle(Component component, C context) {
/* 252 */     HoverEvent<?> hoverEvent = component.hoverEvent();
/* 253 */     if (hoverEvent != null) {
/* 254 */       component = component.hoverEvent((HoverEventSource)hoverEvent.withRenderedValue(this, context));
/*     */     }
/*     */     
/* 257 */     List<Component> children = component.children();
/* 258 */     if (children.isEmpty()) return component;
/*     */     
/* 260 */     List<Component> rendered = new ArrayList<>(children.size());
/* 261 */     children.forEach(child -> rendered.add(render(child, (C)context)));
/*     */     
/* 263 */     return component.children(rendered);
/*     */   }
/*     */   
/*     */   protected <O extends ac.grim.grimac.shaded.kyori.adventure.text.BuildableComponent<O, B>, B extends ComponentBuilder<O, B>> O mergeStyleAndOptionallyDeepRender(Component component, B builder, C context) {
/* 267 */     mergeStyle(component, (ComponentBuilder<?, ?>)builder, context);
/* 268 */     return optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
/*     */   }
/*     */   
/*     */   protected <O extends ac.grim.grimac.shaded.kyori.adventure.text.BuildableComponent<O, B>, B extends ComponentBuilder<O, B>> O optionallyRenderChildrenAppendAndBuild(List<Component> children, B builder, C context) {
/* 272 */     if (!children.isEmpty()) {
/* 273 */       children.forEach(child -> builder.append(render(child, (C)context)));
/*     */     }
/* 275 */     return (O)builder.build();
/*     */   }
/*     */   
/*     */   protected <B extends ComponentBuilder<?, ?>> void mergeStyle(Component component, B builder, C context) {
/* 279 */     builder.mergeStyle(component, MERGES);
/* 280 */     builder.clickEvent(component.clickEvent());
/* 281 */     HoverEvent<?> hoverEvent = component.hoverEvent();
/* 282 */     if (hoverEvent != null)
/* 283 */       builder.hoverEvent((HoverEventSource)hoverEvent.withRenderedValue(this, context)); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\renderer\TranslatableComponentRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */