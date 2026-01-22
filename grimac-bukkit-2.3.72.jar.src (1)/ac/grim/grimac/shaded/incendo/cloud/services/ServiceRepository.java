/*     */ package ac.grim.grimac.shaded.incendo.cloud.services;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.services.annotation.Order;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.services.type.Service;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.function.Predicate;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ServiceRepository<Context, Response>
/*     */ {
/*  45 */   private final Object lock = new Object();
/*     */   
/*     */   private final TypeToken<? extends Service<Context, Response>> serviceType;
/*     */   private final List<ServiceWrapper<? extends Service<Context, Response>>> implementations;
/*  49 */   private int registrationOrder = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ServiceRepository(TypeToken<? extends Service<Context, Response>> serviceType) {
/*  57 */     this.serviceType = serviceType;
/*  58 */     this.implementations = new LinkedList<>();
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
/*     */   <T extends Service<Context, Response>> void registerImplementation(T service, Collection<Predicate<Context>> filters) {
/*  72 */     synchronized (this.lock) {
/*  73 */       this.implementations.add(new ServiceWrapper<>((Service)service, filters));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   LinkedList<ServiceWrapper<? extends Service<Context, Response>>> queue() {
/*  84 */     synchronized (this.lock) {
/*  85 */       return new LinkedList<>(this.implementations);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   final class ServiceWrapper<T extends Service<Context, Response>>
/*     */     implements Comparable<ServiceWrapper<T>>
/*     */   {
/*     */     private final boolean defaultImplementation;
/*     */ 
/*     */     
/*     */     private final T implementation;
/*     */ 
/*     */     
/*     */     private final Collection<Predicate<Context>> filters;
/*     */     
/* 102 */     private final int registrationOrder = ServiceRepository.this.registrationOrder++;
/*     */ 
/*     */     
/*     */     private final ExecutionOrder executionOrder;
/*     */ 
/*     */     
/*     */     private ServiceWrapper(T implementation, Collection<Predicate<Context>> filters) {
/* 109 */       this.defaultImplementation = ServiceRepository.access$200(ServiceRepository.this).isEmpty();
/* 110 */       this.implementation = implementation;
/* 111 */       this.filters = filters;
/* 112 */       ExecutionOrder executionOrder = implementation.order();
/* 113 */       if (executionOrder == null) {
/* 114 */         Order order = (Order)implementation.getClass().getAnnotation(Order.class);
/* 115 */         if (order != null) {
/* 116 */           executionOrder = order.value();
/*     */         } else {
/* 118 */           executionOrder = ExecutionOrder.SOON;
/*     */         } 
/*     */       } 
/* 121 */       this.executionOrder = executionOrder;
/*     */     }
/*     */     
/*     */     T implementation() {
/* 125 */       return this.implementation;
/*     */     }
/*     */     
/*     */     Collection<Predicate<Context>> filters() {
/* 129 */       return Collections.unmodifiableCollection(this.filters);
/*     */     }
/*     */     
/*     */     boolean isDefaultImplementation() {
/* 133 */       return this.defaultImplementation;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 138 */       return String.format("ServiceWrapper{type=%s,implementation=%s}", new Object[] {
/*     */             
/* 140 */             ServiceRepository.access$300(this.this$0).getType().getTypeName(), 
/* 141 */             TypeToken.get(this.implementation.getClass()).getType().getTypeName()
/*     */           });
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public int compareTo(ServiceWrapper<T> other) {
/* 148 */       return Comparator.<ServiceWrapper<T>>comparingInt(wrapper -> wrapper.isDefaultImplementation() ? Integer.MIN_VALUE : Integer.MAX_VALUE)
/*     */ 
/*     */         
/* 151 */         .thenComparingInt(wrapper -> wrapper.executionOrder.ordinal())
/* 152 */         .thenComparingInt(wrapper -> wrapper.registrationOrder).compare(this, other);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\services\ServiceRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */