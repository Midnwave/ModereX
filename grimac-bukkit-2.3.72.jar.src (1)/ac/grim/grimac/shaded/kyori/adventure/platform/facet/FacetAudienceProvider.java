/*     */ package ac.grim.grimac.shaded.kyori.adventure.platform.facet;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.audience.Audience;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.audience.ForwardingAudience;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.identity.Identity;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.permission.PermissionChecker;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.AudienceProvider;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.pointer.Pointered;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.pointer.Pointers;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.renderer.ComponentRenderer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.TriState;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.CopyOnWriteArraySet;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Function;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ @Internal
/*     */ public abstract class FacetAudienceProvider<V, A extends FacetAudience<V>>
/*     */   implements AudienceProvider, ForwardingAudience
/*     */ {
/*  67 */   protected static final Locale DEFAULT_LOCALE = Locale.US;
/*     */   
/*     */   protected final ComponentRenderer<Pointered> componentRenderer;
/*     */   private final Audience console;
/*     */   private final Audience player;
/*     */   protected final Map<V, A> viewers;
/*     */   private final Map<UUID, A> players;
/*     */   private final Set<A> consoles;
/*     */   private A empty;
/*     */   private volatile boolean closed;
/*     */   
/*     */   protected FacetAudienceProvider(@NotNull ComponentRenderer<Pointered> componentRenderer) {
/*  79 */     this.componentRenderer = Objects.<ComponentRenderer<Pointered>>requireNonNull(componentRenderer, "component renderer");
/*  80 */     this.viewers = new ConcurrentHashMap<>();
/*  81 */     this.players = new ConcurrentHashMap<>();
/*  82 */     this.consoles = new CopyOnWriteArraySet<>();
/*  83 */     this.console = (Audience)new ForwardingAudience() {
/*     */         @NotNull
/*     */         public Iterable<? extends Audience> audiences() {
/*  86 */           return FacetAudienceProvider.this.consoles;
/*     */         }
/*     */         
/*     */         @NotNull
/*     */         public Pointers pointers() {
/*  91 */           if (FacetAudienceProvider.this.consoles.size() == 1) {
/*  92 */             return ((FacetAudience)FacetAudienceProvider.this.consoles.iterator().next()).pointers();
/*     */           }
/*  94 */           return Pointers.empty();
/*     */         }
/*     */       };
/*     */     
/*  98 */     this.player = (Audience)Audience.audience(this.players.values());
/*  99 */     this.closed = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addViewer(@NotNull V viewer) {
/* 109 */     if (this.closed)
/* 110 */       return;  FacetAudience facetAudience = (FacetAudience)this.viewers.computeIfAbsent(
/* 111 */         Objects.requireNonNull(viewer, "viewer"), v -> createAudience(Collections.singletonList((V)v)));
/*     */     
/* 113 */     FacetPointers.Type type = (FacetPointers.Type)facetAudience.getOrDefault(FacetPointers.TYPE, FacetPointers.Type.OTHER);
/* 114 */     if (type == FacetPointers.Type.PLAYER) {
/* 115 */       UUID id = (UUID)facetAudience.getOrDefault(Identity.UUID, null);
/* 116 */       if (id != null) this.players.putIfAbsent(id, (A)facetAudience); 
/* 117 */     } else if (type == FacetPointers.Type.CONSOLE) {
/* 118 */       this.consoles.add((A)facetAudience);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeViewer(@NotNull V viewer) {
/* 129 */     FacetAudience facetAudience = (FacetAudience)this.viewers.remove(viewer);
/* 130 */     if (facetAudience == null)
/* 131 */       return;  FacetPointers.Type type = (FacetPointers.Type)facetAudience.getOrDefault(FacetPointers.TYPE, FacetPointers.Type.OTHER);
/* 132 */     if (type == FacetPointers.Type.PLAYER) {
/* 133 */       UUID id = (UUID)facetAudience.getOrDefault(Identity.UUID, null);
/* 134 */       if (id != null) this.players.remove(id); 
/* 135 */     } else if (type == FacetPointers.Type.CONSOLE) {
/* 136 */       this.consoles.remove(facetAudience);
/*     */     } 
/* 138 */     facetAudience.close();
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
/*     */   public void refreshViewer(@NotNull V viewer) {
/* 150 */     FacetAudience facetAudience = (FacetAudience)this.viewers.get(viewer);
/* 151 */     if (facetAudience != null) {
/* 152 */       facetAudience.refresh();
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
/*     */   @NotNull
/*     */   public Iterable<? extends Audience> audiences() {
/* 166 */     return (Iterable)this.viewers.values();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Audience all() {
/* 171 */     return (Audience)this;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Audience console() {
/* 176 */     return this.console;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Audience players() {
/* 181 */     return this.player;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Audience player(@NotNull UUID playerId) {
/* 186 */     return (Audience)this.players.getOrDefault(playerId, empty());
/*     */   }
/*     */   @NotNull
/*     */   private A empty() {
/* 190 */     if (this.empty == null) {
/* 191 */       this.empty = createAudience(Collections.emptyList());
/*     */     }
/* 193 */     return this.empty;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public Audience filter(@NotNull Predicate<V> predicate) {
/* 204 */     return (Audience)Audience.audience(
/* 205 */         filter(this.viewers
/* 206 */           .entrySet(), entry -> predicate.test(entry.getKey()), Map.Entry::getValue));
/*     */   }
/*     */   @NotNull
/*     */   private Audience filterPointers(@NotNull Predicate<Pointered> predicate) {
/* 210 */     return (Audience)Audience.audience(
/* 211 */         filter(this.viewers
/* 212 */           .entrySet(), entry -> predicate.test((Pointered)entry.getValue()), Map.Entry::getValue));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public Audience permission(@NotNull String permission) {
/* 219 */     return filterPointers(pointers -> ((PermissionChecker)pointers.get(PermissionChecker.POINTER).orElse(PermissionChecker.always(TriState.FALSE))).test(permission));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Audience world(@NotNull Key world) {
/* 224 */     return filterPointers(pointers -> world.equals(pointers.getOrDefault(FacetPointers.WORLD, null)));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Audience server(@NotNull String serverName) {
/* 229 */     return filterPointers(pointers -> serverName.equals(pointers.getOrDefault(FacetPointers.SERVER, null)));
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 234 */     this.closed = true;
/* 235 */     for (V viewer : this.viewers.keySet()) {
/* 236 */       removeViewer(viewer);
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
/*     */   @NotNull
/*     */   private static <T, V> Iterable<V> filter(@NotNull final Iterable<T> input, @NotNull final Predicate<T> filter, @NotNull final Function<T, V> transformer) {
/* 257 */     return new Iterable<V>()
/*     */       {
/*     */ 
/*     */         
/*     */         @NotNull
/*     */         public Iterator<V> iterator()
/*     */         {
/* 264 */           return new Iterator<V>() {
/*     */               private final Iterator<T> parent;
/*     */               private V next;
/*     */               
/*     */               private void populate() {
/* 269 */                 this.next = null;
/* 270 */                 while (this.parent.hasNext()) {
/* 271 */                   T next = this.parent.next();
/* 272 */                   if (filter.test(next)) {
/* 273 */                     this.next = transformer.apply(next);
/*     */                     return;
/*     */                   } 
/*     */                 } 
/*     */               }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */               
/*     */               public boolean hasNext() {
/* 286 */                 return (this.next != null);
/*     */               }
/*     */ 
/*     */               
/*     */               public V next() {
/* 291 */                 if (this.next == null) {
/* 292 */                   throw new NoSuchElementException();
/*     */                 }
/* 294 */                 V next = this.next;
/* 295 */                 populate();
/* 296 */                 return next;
/*     */               }
/*     */             };
/*     */         }
/*     */ 
/*     */         
/*     */         public void forEach(Consumer<? super V> action) {
/* 303 */           for (T each : input) {
/* 304 */             if (filter.test(each))
/* 305 */               action.accept(transformer.apply(each)); 
/*     */           } 
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected abstract A createAudience(@NotNull Collection<V> paramCollection);
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\platform\facet\FacetAudienceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */