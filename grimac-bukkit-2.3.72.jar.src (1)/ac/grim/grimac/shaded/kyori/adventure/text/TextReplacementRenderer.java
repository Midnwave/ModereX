/*     */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEventSource;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.renderer.ComponentRenderer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.function.BiFunction;
/*     */ import java.util.regex.MatchResult;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ final class TextReplacementRenderer
/*     */   implements ComponentRenderer<TextReplacementRenderer.State>
/*     */ {
/*  42 */   static final TextReplacementRenderer INSTANCE = new TextReplacementRenderer();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public Component render(@NotNull Component component, @NotNull State state) {
/*  49 */     if (!state.running) return component; 
/*  50 */     boolean prevFirstMatch = state.firstMatch;
/*  51 */     state.firstMatch = true;
/*     */     
/*  53 */     List<Component> oldChildren = component.children();
/*  54 */     int oldChildrenSize = oldChildren.size();
/*  55 */     Style oldStyle = component.style();
/*  56 */     List<Component> children = null;
/*  57 */     Component modified = component;
/*     */     
/*  59 */     if (component instanceof TextComponent) {
/*  60 */       String content = ((TextComponent)component).content();
/*  61 */       Matcher matcher = state.pattern.matcher(content);
/*  62 */       int replacedUntil = 0;
/*  63 */       while (matcher.find()) {
/*  64 */         PatternReplacementResult result = state.continuer.shouldReplace(matcher, ++state.matchCount, state.replaceCount);
/*  65 */         if (result == PatternReplacementResult.CONTINUE) {
/*     */           continue;
/*     */         }
/*  68 */         if (result == PatternReplacementResult.STOP) {
/*     */           
/*  70 */           state.running = false;
/*     */           
/*     */           break;
/*     */         } 
/*  74 */         if (matcher.start() == 0) {
/*     */           
/*  76 */           if (matcher.end() == content.length()) {
/*  77 */             ComponentLike replacement = state.replacement.apply(matcher, Component.text().content(matcher.group())
/*  78 */                 .style(component.style()));
/*     */             
/*  80 */             modified = (replacement == null) ? Component.empty() : replacement.asComponent();
/*     */             
/*  82 */             if (modified.style().hoverEvent() != null) {
/*  83 */               oldStyle = oldStyle.hoverEvent(null);
/*     */             }
/*     */ 
/*     */             
/*  87 */             modified = modified.style(modified.style().merge(component.style(), Style.Merge.Strategy.IF_ABSENT_ON_TARGET));
/*     */             
/*  89 */             if (children == null) {
/*  90 */               children = new ArrayList<>(oldChildrenSize + modified.children().size());
/*  91 */               children.addAll(modified.children());
/*     */             } 
/*     */           } else {
/*     */             
/*  95 */             modified = Component.text("", component.style());
/*  96 */             ComponentLike child = state.replacement.apply(matcher, Component.text().content(matcher.group()));
/*  97 */             if (child != null) {
/*  98 */               if (children == null) {
/*  99 */                 children = new ArrayList<>(oldChildrenSize + 1);
/*     */               }
/* 101 */               children.add(child.asComponent());
/*     */             } 
/*     */           } 
/*     */         } else {
/* 105 */           if (children == null) {
/* 106 */             children = new ArrayList<>(oldChildrenSize + 2);
/*     */           }
/* 108 */           if (state.firstMatch) {
/*     */             
/* 110 */             modified = ((TextComponent)component).content(content.substring(0, matcher.start()));
/* 111 */           } else if (replacedUntil < matcher.start()) {
/* 112 */             children.add(Component.text(content.substring(replacedUntil, matcher.start())));
/*     */           } 
/* 114 */           ComponentLike builder = state.replacement.apply(matcher, Component.text().content(matcher.group()));
/* 115 */           if (builder != null) {
/* 116 */             children.add(builder.asComponent());
/*     */           }
/*     */         } 
/* 119 */         state.replaceCount++;
/* 120 */         state.firstMatch = false;
/* 121 */         replacedUntil = matcher.end();
/*     */       } 
/* 123 */       if (replacedUntil < content.length())
/*     */       {
/* 125 */         if (replacedUntil > 0) {
/* 126 */           if (children == null) {
/* 127 */             children = new ArrayList<>(oldChildrenSize);
/*     */           }
/* 129 */           children.add(Component.text(content.substring(replacedUntil)));
/*     */         }
/*     */       
/*     */       }
/* 133 */     } else if (modified instanceof TranslatableComponent) {
/* 134 */       List<TranslationArgument> args = ((TranslatableComponent)modified).arguments();
/* 135 */       List<TranslationArgument> newArgs = null;
/* 136 */       for (int i = 0, size = args.size(); i < size; i++) {
/* 137 */         TranslationArgument original = args.get(i);
/* 138 */         TranslationArgument replaced = (original.value() instanceof Component) ? TranslationArgument.component(render((Component)original.value(), state)) : original;
/* 139 */         if (replaced != original && 
/* 140 */           newArgs == null) {
/* 141 */           newArgs = new ArrayList<>(size);
/* 142 */           if (i > 0) {
/* 143 */             newArgs.addAll(args.subList(0, i));
/*     */           }
/*     */         } 
/*     */         
/* 147 */         if (newArgs != null) {
/* 148 */           newArgs.add(replaced);
/*     */         }
/*     */       } 
/* 151 */       if (newArgs != null) {
/* 152 */         modified = ((TranslatableComponent)modified).arguments((List)newArgs);
/*     */       }
/*     */     } 
/*     */     
/* 156 */     if (state.running) {
/*     */       
/* 158 */       if (state.replaceInsideHoverEvents) {
/* 159 */         HoverEvent<?> event = oldStyle.hoverEvent();
/* 160 */         if (event != null) {
/* 161 */           HoverEvent<?> rendered = event.withRenderedValue(this, state);
/* 162 */           if (event != rendered) {
/* 163 */             modified = modified.style(s -> s.hoverEvent((HoverEventSource)rendered));
/*     */           }
/*     */         } 
/*     */       } 
/*     */       
/* 168 */       boolean first = true;
/* 169 */       for (int i = 0; i < oldChildrenSize; i++) {
/* 170 */         Component child = oldChildren.get(i);
/* 171 */         Component replaced = render(child, state);
/* 172 */         if (replaced != child) {
/* 173 */           if (children == null) {
/* 174 */             children = new ArrayList<>(oldChildrenSize);
/*     */           }
/* 176 */           if (first) {
/* 177 */             children.addAll(oldChildren.subList(0, i));
/*     */           }
/* 179 */           first = false;
/*     */         } 
/* 181 */         if (children != null) {
/* 182 */           children.add(replaced);
/* 183 */           first = false;
/*     */         }
/*     */       
/*     */       }
/*     */     
/* 188 */     } else if (children != null) {
/* 189 */       children.addAll(oldChildren);
/*     */     } 
/*     */ 
/*     */     
/* 193 */     state.firstMatch = prevFirstMatch;
/*     */     
/* 195 */     if (children != null) {
/* 196 */       return modified.children((List)children);
/*     */     }
/* 198 */     return modified;
/*     */   }
/*     */   
/*     */   static final class State {
/*     */     final Pattern pattern;
/*     */     final BiFunction<MatchResult, TextComponent.Builder, ComponentLike> replacement;
/*     */     final TextReplacementConfig.Condition continuer;
/*     */     final boolean replaceInsideHoverEvents;
/*     */     boolean running = true;
/* 207 */     int matchCount = 0;
/* 208 */     int replaceCount = 0;
/*     */     boolean firstMatch = true;
/*     */     
/*     */     State(@NotNull Pattern pattern, @NotNull BiFunction<MatchResult, TextComponent.Builder, ComponentLike> replacement, TextReplacementConfig.Condition continuer, boolean replaceInsideHoverEvents) {
/* 212 */       this.pattern = pattern;
/* 213 */       this.replacement = replacement;
/* 214 */       this.continuer = continuer;
/* 215 */       this.replaceInsideHoverEvents = replaceInsideHoverEvents;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\TextReplacementRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */