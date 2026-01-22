/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.flattener;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.KeybindComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.ScoreComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.SelectorComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TextComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TranslatableComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Buildable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.InheritanceAwareMap;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Deque;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Function;
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
/*     */ final class ComponentFlattenerImpl
/*     */   implements ComponentFlattener
/*     */ {
/*     */   static final ComponentFlattener BASIC;
/*     */   
/*     */   static {
/*  62 */     BASIC = (ComponentFlattener)(new BuilderImpl()).<KeybindComponent>mapper(KeybindComponent.class, component -> component.keybind()).<ScoreComponent>mapper(ScoreComponent.class, component -> { String value = component.value(); return (Function)((value != null) ? value : ""); }).<SelectorComponent>mapper(SelectorComponent.class, SelectorComponent::pattern).<TextComponent>mapper(TextComponent.class, TextComponent::content).<TranslatableComponent>mapper(TranslatableComponent.class, component -> { String fallback = component.fallback(); return (Function)((fallback != null) ? fallback : component.key()); }).build();
/*  63 */   } static final ComponentFlattener TEXT_ONLY = (ComponentFlattener)(new BuilderImpl())
/*  64 */     .<TextComponent>mapper(TextComponent.class, TextComponent::content)
/*  65 */     .build();
/*     */   
/*     */   private static final int MAX_DEPTH = 512;
/*     */   
/*     */   private final InheritanceAwareMap<Component, Handler> flatteners;
/*     */   private final Function<Component, String> unknownHandler;
/*     */   private final int maxNestedDepth;
/*     */   
/*     */   ComponentFlattenerImpl(InheritanceAwareMap<Component, Handler> flatteners, @Nullable Function<Component, String> unknownHandler, int maxNestedDepth) {
/*  74 */     this.flatteners = flatteners;
/*  75 */     this.unknownHandler = unknownHandler;
/*  76 */     this.maxNestedDepth = maxNestedDepth;
/*     */   }
/*     */   
/*     */   private static final class StackEntry {
/*     */     final Component component;
/*     */     final int depth;
/*     */     
/*     */     StackEntry(Component component, int depth) {
/*  84 */       this.component = component;
/*  85 */       this.depth = depth;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void flatten(@NotNull Component input, @NotNull FlattenerListener listener) {
/*  91 */     flatten0(input, listener, 0, 0);
/*     */   }
/*     */   
/*     */   private void flatten0(@NotNull Component input, @NotNull FlattenerListener listener, int depth, int nestedDepth) {
/*  95 */     Objects.requireNonNull(input, "input");
/*  96 */     Objects.requireNonNull(listener, "listener");
/*  97 */     if (input == Component.empty())
/*     */       return; 
/*  99 */     if (this.maxNestedDepth != -1 && nestedDepth > this.maxNestedDepth) {
/* 100 */       throw new IllegalStateException("Exceeded maximum nesting depth of " + this.maxNestedDepth + " while attempting to flatten components!");
/*     */     }
/*     */     
/* 103 */     Deque<StackEntry> componentStack = new ArrayDeque<>();
/* 104 */     Deque<Style> styleStack = new ArrayDeque<>();
/*     */ 
/*     */     
/* 107 */     componentStack.push(new StackEntry(input, depth));
/*     */     
/* 109 */     while (!componentStack.isEmpty()) {
/* 110 */       StackEntry entry = componentStack.pop();
/* 111 */       int currentDepth = entry.depth;
/*     */       
/* 113 */       if (currentDepth > 512) {
/* 114 */         throw new IllegalStateException("Exceeded maximum depth of 512 while attempting to flatten components!");
/*     */       }
/*     */       
/* 117 */       Component component = entry.component;
/* 118 */       Handler flattener = flattener(component);
/* 119 */       Style componentStyle = component.style();
/*     */ 
/*     */       
/* 122 */       listener.pushStyle(componentStyle);
/* 123 */       styleStack.push(componentStyle);
/*     */       
/* 125 */       if (flattener != null) {
/* 126 */         flattener.handle(this, component, listener, currentDepth, nestedDepth);
/*     */       }
/*     */       
/* 129 */       if (!component.children().isEmpty() && listener.shouldContinue()) {
/*     */         
/* 131 */         List<Component> children = component.children();
/* 132 */         for (int i = children.size() - 1; i >= 0; i--) {
/* 133 */           componentStack.push(new StackEntry(children.get(i), currentDepth + 1));
/*     */         }
/*     */         continue;
/*     */       } 
/* 137 */       Style style = styleStack.pop();
/* 138 */       listener.popStyle(style);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 143 */     while (!styleStack.isEmpty()) {
/* 144 */       Style style = styleStack.pop();
/* 145 */       listener.popStyle(style);
/*     */     } 
/*     */   }
/*     */   @Nullable
/*     */   private <T extends Component> Handler flattener(T test) {
/* 150 */     Handler flattener = (Handler)this.flatteners.get(test.getClass());
/*     */     
/* 152 */     if (flattener == null && this.unknownHandler != null) {
/* 153 */       return (self, component, listener, depth, nestedDepth) -> listener.component(this.unknownHandler.apply(component));
/*     */     }
/* 155 */     return flattener;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ComponentFlattener.Builder toBuilder() {
/* 161 */     return new BuilderImpl(this.flatteners, this.unknownHandler, this.maxNestedDepth);
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   static interface Handler {
/*     */     void handle(ComponentFlattenerImpl param1ComponentFlattenerImpl, Component param1Component, FlattenerListener param1FlattenerListener, int param1Int1, int param1Int2);
/*     */   }
/*     */   
/*     */   static final class BuilderImpl implements ComponentFlattener.Builder {
/*     */     private final InheritanceAwareMap.Builder<Component, ComponentFlattenerImpl.Handler> flatteners;
/*     */     @Nullable
/*     */     private Function<Component, String> unknownHandler;
/* 173 */     private int maxNestedDepth = -1;
/*     */     
/*     */     BuilderImpl() {
/* 176 */       this.flatteners = InheritanceAwareMap.builder().strict(true);
/*     */     }
/*     */     
/*     */     BuilderImpl(InheritanceAwareMap<Component, ComponentFlattenerImpl.Handler> flatteners, @Nullable Function<Component, String> unknownHandler, int maxNestedDepth) {
/* 180 */       this.flatteners = InheritanceAwareMap.builder(flatteners).strict(true);
/* 181 */       this.unknownHandler = unknownHandler;
/* 182 */       this.maxNestedDepth = maxNestedDepth;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public ComponentFlattener build() {
/* 187 */       return new ComponentFlattenerImpl((InheritanceAwareMap<Component, ComponentFlattenerImpl.Handler>)this.flatteners.build(), this.unknownHandler, this.maxNestedDepth);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public <T extends Component> ComponentFlattener.Builder mapper(@NotNull Class<T> type, @NotNull Function<T, String> converter) {
/* 193 */       this.flatteners.put(type, (self, component, listener, depth, nestedDepth) -> listener.component(converter.apply(component)));
/* 194 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public <T extends Component> ComponentFlattener.Builder complexMapper(@NotNull Class<T> type, @NotNull BiConsumer<T, Consumer<Component>> converter) {
/* 200 */       this.flatteners.put(type, (self, component, listener, depth, nestedDepth) -> converter.accept(component, ()));
/* 201 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public ComponentFlattener.Builder unknownMapper(@Nullable Function<Component, String> converter) {
/* 206 */       this.unknownHandler = converter;
/* 207 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public ComponentFlattener.Builder nestingLimit(int limit) {
/* 213 */       if (limit != -1 && limit < 1) throw new IllegalArgumentException("limit must be positive or ComponentFlattener.NO_NESTING_LIMIT"); 
/* 214 */       this.maxNestedDepth = limit;
/* 215 */       return this;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\flattener\ComponentFlattenerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */