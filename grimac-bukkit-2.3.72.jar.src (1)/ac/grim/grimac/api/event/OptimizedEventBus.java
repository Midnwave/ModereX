/*     */ package ac.grim.grimac.api.event;
/*     */ import ac.grim.grimac.api.event.events.CommandExecuteEvent;
/*     */ import ac.grim.grimac.api.event.events.CompletePredictionEvent;
/*     */ import ac.grim.grimac.api.event.events.FlagEvent;
/*     */ import ac.grim.grimac.api.event.events.GrimJoinEvent;
/*     */ import ac.grim.grimac.api.event.events.GrimReloadEvent;
/*     */ import ac.grim.grimac.api.plugin.GrimPlugin;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import java.lang.invoke.MethodHandle;
/*     */ import java.lang.invoke.MethodHandles;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.Arrays;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import java.util.function.Predicate;
/*     */ 
/*     */ public class OptimizedEventBus implements EventBus {
/*  19 */   private final MethodHandles.Lookup lookup = MethodHandles.lookup();
/*     */   
/*  21 */   private final Map<Class<? extends GrimEvent>, AtomicReference<OptimizedListener[]>> listenerMap = new ConcurrentHashMap<>();
/*     */   
/*     */   public OptimizedEventBus() {
/*  24 */     prefillKnownEventTypes(this.listenerMap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void prefillKnownEventTypes(Map<Class<? extends GrimEvent>, AtomicReference<OptimizedListener[]>> map) {
/*  34 */     map.put(GrimReloadEvent.class, (AtomicReference)new AtomicReference<>(new OptimizedListener[0]));
/*  35 */     map.put(GrimQuitEvent.class, (AtomicReference)new AtomicReference<>(new OptimizedListener[0]));
/*  36 */     map.put(GrimJoinEvent.class, (AtomicReference)new AtomicReference<>(new OptimizedListener[0]));
/*     */     
/*  38 */     map.put(FlagEvent.class, (AtomicReference)new AtomicReference<>(new OptimizedListener[0]));
/*  39 */     map.put(CommandExecuteEvent.class, (AtomicReference)new AtomicReference<>(new OptimizedListener[0]));
/*  40 */     map.put(CompletePredictionEvent.class, (AtomicReference)new AtomicReference<>(new OptimizedListener[0]));
/*     */ 
/*     */     
/*  43 */     map.put(GrimEvent.class, (AtomicReference)new AtomicReference<>(new OptimizedListener[0]));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerAnnotatedListeners(GrimPlugin plugin, @NotNull Object listener) {
/*  49 */     registerMethods(plugin, listener, listener.getClass());
/*     */   }
/*     */ 
/*     */   
/*     */   public void registerStaticAnnotatedListeners(GrimPlugin plugin, @NotNull Class<?> clazz) {
/*  54 */     registerMethods(plugin, null, clazz);
/*     */   }
/*     */   
/*     */   private void registerMethods(GrimPlugin plugin, @Nullable Object instance, @NotNull Class<?> clazz) {
/*  58 */     for (Method method : clazz.getDeclaredMethods()) {
/*  59 */       GrimEventHandler annotation = method.<GrimEventHandler>getAnnotation(GrimEventHandler.class);
/*  60 */       if (annotation != null && method.getParameterCount() == 1) {
/*  61 */         Class<?> eventType = method.getParameterTypes()[0];
/*  62 */         if (GrimEvent.class.isAssignableFrom(eventType))
/*     */           try {
/*  64 */             if (instance != null || Modifier.isStatic(method.getModifiers())) {
/*     */ 
/*     */ 
/*     */               
/*  68 */               method.setAccessible(true);
/*  69 */               MethodHandle handle = this.lookup.unreflect(method);
/*     */               
/*  71 */               GrimEventListener<GrimEvent> listener = event -> {
/*     */                   try {
/*     */                     if (instance != null) {
/*     */                       handle.invoke(instance, event);
/*     */                     } else {
/*     */                       handle.invoke(event);
/*     */                     } 
/*  78 */                   } catch (Throwable throwable) {
/*     */                     throw new RuntimeException("Failed to invoke listener for " + eventType.getName(), throwable);
/*     */                   } 
/*     */                 };
/*     */               
/*  83 */               Class<?> currentEventType = eventType;
/*  84 */               while (GrimEvent.class.isAssignableFrom(currentEventType))
/*     */               
/*     */               { 
/*  87 */                 OptimizedListener optimizedListener = new OptimizedListener(plugin, listener, annotation.priority(), annotation.ignoreCancelled(), method.getDeclaringClass(), instance);
/*     */ 
/*     */                 
/*  90 */                 addListener((Class)currentEventType, optimizedListener);
/*  91 */                 currentEventType = currentEventType.getSuperclass(); } 
/*     */             } 
/*  93 */           } catch (IllegalAccessException e) {
/*  94 */             System.err.println("Failed to register listener for " + eventType.getName() + ": " + e.getMessage());
/*  95 */             e.printStackTrace();
/*     */           }  
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addListener(Class<? extends GrimEvent> eventType, OptimizedListener newListener) {
/*     */     OptimizedListener[] oldArray, newArray;
/* 103 */     AtomicReference<OptimizedListener[]> ref = this.listenerMap.computeIfAbsent(eventType, k -> new AtomicReference<>(new OptimizedListener[0]));
/*     */ 
/*     */ 
/*     */     
/*     */     do {
/* 108 */       oldArray = ref.get();
/*     */ 
/*     */ 
/*     */       
/* 112 */       int insertionPoint = Arrays.binarySearch(oldArray, newListener, (a, b) -> Integer.compare(b.priority, a.priority));
/*     */ 
/*     */ 
/*     */       
/* 116 */       if (insertionPoint < 0) {
/* 117 */         insertionPoint = -(insertionPoint + 1);
/*     */       }
/*     */       else {
/*     */         
/* 121 */         while (insertionPoint < oldArray.length - 1 && (oldArray[insertionPoint + 1]).priority == newListener.priority)
/*     */         {
/* 123 */           insertionPoint++;
/*     */         }
/* 125 */         insertionPoint++;
/*     */       } 
/*     */       
/* 128 */       newArray = new OptimizedListener[oldArray.length + 1];
/*     */ 
/*     */       
/* 131 */       System.arraycopy(oldArray, 0, newArray, 0, insertionPoint);
/*     */ 
/*     */       
/* 134 */       newArray[insertionPoint] = newListener;
/*     */ 
/*     */       
/* 137 */       System.arraycopy(oldArray, insertionPoint, newArray, insertionPoint + 1, oldArray.length - insertionPoint);
/*     */     
/*     */     }
/* 140 */     while (!ref.compareAndSet(oldArray, newArray));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void unregisterListeners(GrimPlugin plugin, Object instance) {
/* 149 */     for (Map.Entry<Class<? extends GrimEvent>, AtomicReference<OptimizedListener[]>> entry : this.listenerMap.entrySet()) {
/* 150 */       removeListeners(entry.getKey(), entry.getValue(), listener -> 
/* 151 */           (listener.plugin.equals(plugin) && listener.instance != null && listener.instance.equals(instance)));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void unregisterStaticListeners(GrimPlugin plugin, Class<?> clazz) {
/* 159 */     for (Map.Entry<Class<? extends GrimEvent>, AtomicReference<OptimizedListener[]>> entry : this.listenerMap.entrySet()) {
/* 160 */       removeListeners(entry.getKey(), entry.getValue(), listener -> 
/* 161 */           (listener.plugin.equals(plugin) && listener.instance == null && listener.declaringClass.equals(clazz)));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void unregisterAllListeners(GrimPlugin plugin) {
/* 169 */     for (Map.Entry<Class<? extends GrimEvent>, AtomicReference<OptimizedListener[]>> entry : this.listenerMap.entrySet()) {
/* 170 */       removeListeners(entry.getKey(), entry.getValue(), listener -> listener.plugin.equals(plugin));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void unregisterListener(GrimPlugin plugin, GrimEventListener<?> eventListener) {
/* 177 */     for (Map.Entry<Class<? extends GrimEvent>, AtomicReference<OptimizedListener[]>> entry : this.listenerMap.entrySet()) {
/* 178 */       removeListeners(entry.getKey(), entry.getValue(), listener -> 
/* 179 */           (listener.plugin.equals(plugin) && listener.listener.equals(eventListener)));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void removeListeners(Class<? extends GrimEvent> eventType, AtomicReference<OptimizedListener[]> ref, Predicate<OptimizedListener> filter) {
/*     */     OptimizedListener[] oldArray;
/*     */     OptimizedListener[] newArray;
/*     */     do {
/* 188 */       oldArray = ref.get();
/*     */ 
/*     */       
/* 191 */       int remaining = 0;
/* 192 */       for (OptimizedListener listener : oldArray) {
/* 193 */         if (!filter.test(listener)) {
/* 194 */           remaining++;
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 199 */       if (remaining == oldArray.length) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/* 204 */       newArray = new OptimizedListener[remaining];
/* 205 */       int index = 0;
/* 206 */       for (OptimizedListener listener : oldArray) {
/* 207 */         if (!filter.test(listener)) {
/* 208 */           newArray[index++] = listener;
/*     */         }
/*     */       }
/*     */     
/*     */     }
/* 213 */     while (!ref.compareAndSet(oldArray, newArray));
/*     */     
/* 215 */     if (newArray.length == 0) {
/* 216 */       this.listenerMap.remove(eventType);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void post(@NotNull GrimEvent event) {
/* 226 */     Class<?> currentEventType = event.getClass();
/* 227 */     while (GrimEvent.class.isAssignableFrom(currentEventType)) {
/* 228 */       AtomicReference<OptimizedListener[]> ref = this.listenerMap.get(currentEventType);
/* 229 */       if (ref != null) {
/*     */         
/* 231 */         OptimizedListener[] listeners = ref.get();
/* 232 */         for (OptimizedListener listener : listeners) {
/*     */           try {
/* 234 */             if (!event.isCancelled() || listener.ignoreCancelled)
/*     */             {
/*     */               
/* 237 */               listener.listener.handle(event); } 
/* 238 */           } catch (Throwable throwable) {
/* 239 */             System.err.println("Error handling event " + event.getEventName() + ": " + throwable.getMessage());
/* 240 */             throwable.printStackTrace();
/*     */           } 
/*     */         } 
/*     */       } 
/* 244 */       currentEventType = currentEventType.getSuperclass();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends GrimEvent> void subscribe(GrimPlugin plugin, @NotNull Class<T> eventType, @NotNull GrimEventListener<T> listener, int priority, boolean ignoreCancelled, @NotNull Class<?> declaringClass) {
/* 253 */     OptimizedListener optimizedListener = new OptimizedListener(plugin, listener, priority, ignoreCancelled, declaringClass, null);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 258 */     addListener(eventType, optimizedListener);
/*     */   }
/*     */ 
/*     */   
/*     */   private static class OptimizedListener
/*     */   {
/*     */     final GrimPlugin plugin;
/*     */     final GrimEventListener<GrimEvent> listener;
/*     */     final int priority;
/*     */     final boolean ignoreCancelled;
/*     */     final Class<?> declaringClass;
/*     */     final Object instance;
/*     */     
/*     */     OptimizedListener(GrimPlugin plugin, GrimEventListener<GrimEvent> listener, int priority, boolean ignoreCancelled, Class<?> declaringClass, Object instance) {
/* 272 */       this.plugin = plugin;
/* 273 */       this.listener = listener;
/* 274 */       this.priority = priority;
/* 275 */       this.ignoreCancelled = ignoreCancelled;
/* 276 */       this.declaringClass = declaringClass;
/* 277 */       this.instance = instance;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\event\OptimizedEventBus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */