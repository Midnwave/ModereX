/*     */ package ac.grim.grimac.api.event;
/*     */ 
/*     */ import ac.grim.grimac.api.plugin.GrimPlugin;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
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
/*     */ public interface EventBus
/*     */ {
/*     */   void registerAnnotatedListeners(GrimPlugin paramGrimPlugin, @NotNull Object paramObject);
/*     */   
/*     */   void registerStaticAnnotatedListeners(GrimPlugin paramGrimPlugin, @NotNull Class<?> paramClass);
/*     */   
/*     */   void unregisterListeners(GrimPlugin paramGrimPlugin, Object paramObject);
/*     */   
/*     */   void unregisterStaticListeners(GrimPlugin paramGrimPlugin, Class<?> paramClass);
/*     */   
/*     */   void unregisterAllListeners(GrimPlugin paramGrimPlugin);
/*     */   
/*     */   void unregisterListener(GrimPlugin paramGrimPlugin, GrimEventListener<?> paramGrimEventListener);
/*     */   
/*     */   void post(@NotNull GrimEvent paramGrimEvent);
/*     */   
/*     */   <T extends GrimEvent> void subscribe(GrimPlugin paramGrimPlugin, @NotNull Class<T> paramClass, @NotNull GrimEventListener<T> paramGrimEventListener, int paramInt, boolean paramBoolean, @NotNull Class<?> paramClass1);
/*     */   
/*     */   default <T extends GrimEvent> void subscribe(GrimPlugin plugin, @NotNull Class<T> eventType, @NotNull GrimEventListener<T> listener, int priority, boolean ignoreCancelled) {
/* 128 */     subscribe(plugin, eventType, listener, priority, ignoreCancelled, plugin.getClass());
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
/*     */   default <T extends GrimEvent> void subscribe(GrimPlugin plugin, @NotNull Class<T> eventType, @NotNull GrimEventListener<T> listener) {
/* 140 */     subscribe(plugin, eventType, listener, 0, false);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\api\event\EventBus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */