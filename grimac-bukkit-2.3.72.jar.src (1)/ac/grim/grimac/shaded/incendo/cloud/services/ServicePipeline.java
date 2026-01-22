/*     */ package ac.grim.grimac.shaded.incendo.cloud.services;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.services.type.Service;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.Executor;
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
/*     */ public final class ServicePipeline
/*     */ {
/*  44 */   private final Object lock = new Object();
/*     */   private final Map<Type, ServiceRepository<?, ?>> repositories;
/*     */   private final Executor executor;
/*     */   
/*     */   ServicePipeline(Executor executor) {
/*  49 */     this.repositories = new HashMap<>();
/*  50 */     this.executor = executor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ServicePipelineBuilder builder() {
/*  59 */     return new ServicePipelineBuilder();
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
/*     */ 
/*     */ 
/*     */   
/*     */   public <Context, Result> ServicePipeline registerServiceType(TypeToken<? extends Service<Context, Result>> type, Service<Context, Result> defaultImplementation) {
/*  77 */     synchronized (this.lock) {
/*  78 */       if (this.repositories.containsKey(type.getType())) {
/*  79 */         throw new IllegalArgumentException(
/*  80 */             String.format("Service of type '%s' has already been registered", new Object[] {
/*     */                 
/*  82 */                 type.getType().getTypeName()
/*     */               }));
/*     */       }
/*  85 */       ServiceRepository<Context, Result> repository = new ServiceRepository<>(type);
/*  86 */       repository.registerImplementation(defaultImplementation, Collections.emptyList());
/*  87 */       this.repositories.put(type.getType(), repository);
/*  88 */       return this;
/*     */     } 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> ServicePipeline registerMethods(T instance) throws Exception {
/* 114 */     synchronized (this.lock) {
/*     */       
/* 116 */       Map<? extends Service<?, ?>, TypeToken<? extends Service<?, ?>>> services = AnnotatedMethodServiceFactory.INSTANCE.lookupServices(instance);
/* 117 */       for (Map.Entry<? extends Service<?, ?>, TypeToken<? extends Service<?, ?>>> serviceEntry : services
/* 118 */         .entrySet()) {
/* 119 */         TypeToken<? extends Service<?, ?>> type = serviceEntry.getValue();
/* 120 */         ServiceRepository<?, ?> repository = this.repositories.get(type.getType());
/* 121 */         if (repository == null) {
/* 122 */           throw new IllegalArgumentException(
/* 123 */               String.format("No service registered for type '%s'", new Object[] { type.getType().getTypeName() }));
/*     */         }
/* 125 */         repository.registerImplementation(serviceEntry
/* 126 */             .getKey(), 
/* 127 */             Collections.emptyList());
/*     */       } 
/*     */     } 
/*     */     
/* 131 */     return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <Context, Result> ServicePipeline registerServiceImplementation(TypeToken<? extends Service<Context, Result>> type, Service<Context, Result> implementation, Collection<Predicate<Context>> filters) {
/* 152 */     synchronized (this.lock) {
/* 153 */       ServiceRepository<Context, Result> repository = getRepository(type);
/* 154 */       repository.registerImplementation(implementation, filters);
/*     */     } 
/* 156 */     return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <Context, Result> ServicePipeline registerServiceImplementation(Class<? extends Service<Context, Result>> type, Service<Context, Result> implementation, Collection<Predicate<Context>> filters) {
/* 177 */     return registerServiceImplementation(TypeToken.get(type), implementation, filters);
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
/*     */   public <Context> ServicePump<Context> pump(Context context) {
/* 189 */     return new ServicePump<>(this, context);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   <Context, Result> ServiceRepository<Context, Result> getRepository(TypeToken<? extends Service<Context, Result>> type) {
/* 197 */     ServiceRepository<Context, Result> repository = (ServiceRepository<Context, Result>)this.repositories.get(type.getType());
/* 198 */     if (repository == null) {
/* 199 */       throw new IllegalArgumentException(
/* 200 */           String.format("No service registered for type '%s'", new Object[] { type.getType().getTypeName() }));
/*     */     }
/* 202 */     return repository;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<Type> recognizedTypes() {
/* 211 */     return Collections.unmodifiableCollection(this.repositories.keySet());
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
/*     */ 
/*     */   
/*     */   public <Context, Result, S extends Service<Context, Result>> Collection<TypeToken<? extends S>> getImplementations(TypeToken<S> type) {
/* 228 */     ServiceRepository<Context, Result> repository = getRepository(type);
/* 229 */     List<TypeToken<? extends S>> collection = new LinkedList<>();
/*     */     
/* 231 */     LinkedList<? extends ServiceRepository<Context, Result>.ServiceWrapper<? extends Service<Context, Result>>> queue = repository.queue();
/* 232 */     queue.sort(null);
/* 233 */     Collections.reverse(queue);
/* 234 */     for (ServiceRepository<Context, Result>.ServiceWrapper<? extends Service<Context, Result>> wrapper : queue) {
/* 235 */       collection
/* 236 */         .add(TypeToken.get(wrapper.implementation().getClass()));
/*     */     }
/* 238 */     return Collections.unmodifiableList(collection);
/*     */   }
/*     */   
/*     */   Executor executor() {
/* 242 */     return this.executor;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\services\ServicePipeline.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */