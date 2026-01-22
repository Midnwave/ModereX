/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.event;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.exception.InvalidHandshakeException;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.CopyOnWriteArraySet;
/*     */ import java.util.logging.Level;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class EventManager
/*     */ {
/*  51 */   private final Map<PacketListenerPriority, Set<PacketListenerCommon>> listenersMap = new ConcurrentHashMap<>();
/*     */ 
/*     */   
/*  54 */   private volatile PacketListenerCommon[] listeners = new PacketListenerCommon[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void callEvent(PacketEvent event) {
/*  67 */     callEvent(event, null);
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
/*     */ 
/*     */   
/*     */   public void callEvent(PacketEvent event, @Nullable Runnable postCallListenerAction, boolean preVia) {
/*  82 */     for (PacketListenerCommon listener : this.listeners) {
/*     */       try {
/*  84 */         if (listener.isPreVia() == preVia)
/*  85 */           event.call(listener); 
/*  86 */       } catch (Exception t) {
/*     */         
/*  88 */         if (t.getClass() != InvalidHandshakeException.class && (t.getCause() == null || t.getCause().getClass() != InvalidHandshakeException.class)) {
/*  89 */           PacketEvents.getAPI().getLogger().log(Level.WARNING, "PacketEvents caught an unhandled exception while calling your listener.", t);
/*     */         }
/*     */       } 
/*  92 */       if (postCallListenerAction != null) {
/*  93 */         postCallListenerAction.run();
/*     */       }
/*     */     } 
/*     */     
/*  97 */     if (event instanceof ProtocolPacketEvent && !((ProtocolPacketEvent)event).needsReEncode()) {
/*  98 */       ((ProtocolPacketEvent)event).setLastUsedWrapper(null);
/*     */     }
/*     */   }
/*     */   
/*     */   public void callEvent(PacketEvent event, @Nullable Runnable postCallListenerAction) {
/* 103 */     callEvent(event, postCallListenerAction, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PacketListenerCommon registerListener(PacketListener listener, PacketListenerPriority priority) {
/* 113 */     PacketListenerCommon packetListenerAbstract = listener.asAbstract(priority);
/* 114 */     return registerListener(packetListenerAbstract);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PacketListenerCommon registerListener(PacketListenerCommon listener) {
/* 123 */     registerListenerNoRecalculation(listener);
/* 124 */     recalculateListeners();
/* 125 */     return listener;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PacketListenerCommon[] registerListeners(PacketListenerCommon... listeners) {
/* 134 */     for (PacketListenerCommon listener : listeners) {
/* 135 */       registerListenerNoRecalculation(listener);
/*     */     }
/* 137 */     recalculateListeners();
/* 138 */     return listeners;
/*     */   }
/*     */   
/*     */   public void unregisterListener(PacketListenerCommon listener) {
/* 142 */     if (unregisterListenerNoRecalculation(listener)) recalculateListeners(); 
/*     */   }
/*     */   
/*     */   public void unregisterListeners(PacketListenerCommon... listeners) {
/* 146 */     boolean modified = false;
/* 147 */     for (PacketListenerCommon listener : listeners) {
/* 148 */       modified |= unregisterListenerNoRecalculation(listener);
/*     */     }
/* 150 */     if (modified) recalculateListeners();
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void unregisterAllListeners() {
/* 158 */     this.listenersMap.clear();
/* 159 */     synchronized (this) {
/* 160 */       this.listeners = new PacketListenerCommon[0];
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void recalculateListeners() {
/* 167 */     synchronized (this) {
/* 168 */       List<PacketListenerCommon> list = new ArrayList<>();
/*     */       
/* 170 */       for (PacketListenerPriority priority : PacketListenerPriority.values()) {
/* 171 */         Set<PacketListenerCommon> set = this.listenersMap.get(priority);
/* 172 */         if (set != null) list.addAll(set); 
/*     */       } 
/* 174 */       this.listeners = list.<PacketListenerCommon>toArray(new PacketListenerCommon[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void registerListenerNoRecalculation(PacketListenerCommon listener) {
/* 181 */     Set<PacketListenerCommon> listenerSet = this.listenersMap.computeIfAbsent(listener.getPriority(), p -> new CopyOnWriteArraySet());
/* 182 */     listenerSet.add(listener);
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean unregisterListenerNoRecalculation(PacketListenerCommon listener) {
/* 187 */     Set<PacketListenerCommon> listenerSet = this.listenersMap.get(listener.getPriority());
/* 188 */     return (listenerSet != null && listenerSet.remove(listener));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\event\EventManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */