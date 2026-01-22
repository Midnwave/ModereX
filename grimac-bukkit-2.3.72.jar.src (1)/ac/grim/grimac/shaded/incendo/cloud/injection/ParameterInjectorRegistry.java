/*     */ package ac.grim.grimac.shaded.incendo.cloud.injection;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.GenericTypeReflector;
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.InjectionException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.services.ServicePipeline;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.tuple.Pair;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.util.annotation.AnnotationAccessor;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Optional;
/*     */ import java.util.function.Predicate;
/*     */ import java.util.stream.Collectors;
/*     */ import org.apiguardian.api.API;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.STABLE)
/*     */ public final class ParameterInjectorRegistry<C>
/*     */   implements InjectionService<C>
/*     */ {
/*  57 */   private final List<Pair<Predicate<TypeToken<?>>, ParameterInjector<C, ?>>> injectors = new ArrayList<>();
/*  58 */   private final ServicePipeline servicePipeline = ServicePipeline.builder().build();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParameterInjectorRegistry() {
/*  64 */     this.servicePipeline.registerServiceType(new TypeToken<InjectionService<C>>() {  }, this);
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
/*     */   public synchronized <T> ParameterInjectorRegistry<C> registerInjector(Class<T> clazz, ParameterInjector<C, T> injector) {
/*  80 */     return registerInjector(TypeToken.get(clazz), injector);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public synchronized <T> ParameterInjectorRegistry<C> registerInjector(TypeToken<T> type, ParameterInjector<C, T> injector) {
/*  96 */     return registerInjector(cl -> GenericTypeReflector.isSuperType(cl.getType(), type.getType()), injector);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public synchronized <T> ParameterInjectorRegistry<C> registerInjector(Predicate<TypeToken<?>> predicate, ParameterInjector<C, T> injector) {
/* 118 */     this.injectors.add(Pair.of(predicate, injector));
/* 119 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object handle(InjectionRequest<C> request) {
/* 124 */     for (ParameterInjector<C, ?> injector : injectors(request.injectedType())) {
/* 125 */       Object value = injector.create(request.commandContext(), request.annotationAccessor());
/* 126 */       if (value != null) {
/* 127 */         return value;
/*     */       }
/*     */     } 
/* 130 */     return null;
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public <T> Optional<T> getInjectable(Class<T> clazz, CommandContext<C> context, AnnotationAccessor annotationAccessor) {
/* 152 */     return getInjectable(TypeToken.get(clazz), context, annotationAccessor);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public <T> Optional<T> getInjectable(TypeToken<T> type, CommandContext<C> context, AnnotationAccessor annotationAccessor) {
/* 175 */     InjectionRequest<C> request = InjectionRequest.of(context, type, annotationAccessor);
/*     */     
/*     */     try {
/* 178 */       Object rawResult = this.servicePipeline.pump(request).through(new TypeToken<InjectionService<C>>() {  }).complete();
/*     */       
/* 180 */       if (!request.injectedClass().isInstance(rawResult)) {
/* 181 */         throw new IllegalStateException(String.format("Injector returned type %s which is not an instance of %s", new Object[] { rawResult
/*     */                 
/* 183 */                 .getClass().getName(), request
/* 184 */                 .injectedClass().getName() }));
/*     */       }
/*     */ 
/*     */       
/* 188 */       T result = (T)rawResult;
/*     */       
/* 190 */       return Optional.of(result);
/* 191 */     } catch (IllegalStateException ignored) {
/* 192 */       return Optional.empty();
/* 193 */     } catch (InjectionException injectionException) {
/* 194 */       throw injectionException;
/* 195 */     } catch (Exception e) {
/* 196 */       throw new InjectionException(
/* 197 */           String.format("Failed to inject type %s", new Object[] { type.getType().getTypeName() }), e);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public ParameterInjectorRegistry<C> registerInjectionService(InjectionService<C> service) {
/* 212 */     this.servicePipeline.registerServiceImplementation(new TypeToken<InjectionService<C>>() {  }, service, 
/* 213 */         Collections.emptyList());
/* 214 */     return this;
/*     */   }
/*     */   
/*     */   private synchronized <T> Collection<ParameterInjector<C, ?>> injectors(TypeToken<T> type) {
/* 218 */     return Collections.unmodifiableCollection((Collection<? extends ParameterInjector<C, ?>>)this.injectors.stream()
/* 219 */         .filter(pair -> ((Predicate<TypeToken>)pair.first()).test(type))
/* 220 */         .map(Pair::second)
/* 221 */         .collect(Collectors.toList()));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\injection\ParameterInjectorRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */