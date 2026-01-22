/*     */ package ac.grim.grimac.shaded.kyori.adventure.platform.facet;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.OverrideOnly;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.audience.Audience;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.audience.MessageType;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.chat.ChatType;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.chat.SignedMessage;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.identity.Identity;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.inventory.Book;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.pointer.Pointers;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.sound.Sound;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.sound.SoundStop;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.ComponentLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.flattener.ComponentFlattener;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.title.Title;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.title.TitlePart;
/*     */ import java.io.Closeable;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CopyOnWriteArraySet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public class FacetAudience<V>
/*     */   implements Audience, Closeable
/*     */ {
/*     */   @NotNull
/*     */   protected final FacetAudienceProvider<V, FacetAudience<V>> provider;
/*     */   @NotNull
/*     */   private final Set<V> viewers;
/*     */   @Nullable
/*     */   private V viewer;
/*     */   private volatile Pointers pointers;
/*     */   private final Facet.Chat<V, Object> chat;
/*     */   private final Facet.ActionBar<V, Object> actionBar;
/*     */   private final Facet.Title<V, Object, Object, Object> title;
/*     */   private final Facet.Sound<V, Object> sound;
/*     */   private final Facet.EntitySound<V, Object> entitySound;
/*     */   private final Facet.Book<V, Object, Object> book;
/*     */   private final Facet.BossBar.Builder<V, Facet.BossBar<V>> bossBar;
/*     */   @Nullable
/*     */   private final Map<BossBar, Facet.BossBar<V>> bossBars;
/*     */   private final Facet.TabList<V, Object> tabList;
/*     */   @NotNull
/*     */   private final Collection<? extends Facet.Pointers<V>> pointerProviders;
/*     */   
/*     */   public FacetAudience(@NotNull FacetAudienceProvider<V, FacetAudience<V>> provider, @NotNull Collection<? extends V> viewers, @Nullable Collection<? extends Facet.Chat> chat, @Nullable Collection<? extends Facet.ActionBar> actionBar, @Nullable Collection<? extends Facet.Title> title, @Nullable Collection<? extends Facet.Sound> sound, @Nullable Collection<? extends Facet.EntitySound> entitySound, @Nullable Collection<? extends Facet.Book> book, @Nullable Collection<? extends Facet.BossBar.Builder> bossBar, @Nullable Collection<? extends Facet.TabList> tabList, @Nullable Collection<? extends Facet.Pointers> pointerProviders) {
/* 118 */     this.provider = Objects.<FacetAudienceProvider<V, FacetAudience<V>>>requireNonNull(provider, "audience provider");
/* 119 */     this.viewers = new CopyOnWriteArraySet<>();
/* 120 */     for (V viewer : Objects.<Collection>requireNonNull(viewers, "viewers")) {
/* 121 */       addViewer(viewer);
/*     */     }
/* 123 */     refresh();
/* 124 */     this.chat = Facet.<V, Facet.Chat<V, Object>>of((Collection)chat, this.viewer);
/* 125 */     this.actionBar = Facet.<V, Facet.ActionBar<V, Object>>of((Collection)actionBar, this.viewer);
/* 126 */     this.title = Facet.<V, Facet.Title<V, Object, Object, Object>>of((Collection)title, this.viewer);
/* 127 */     this.sound = Facet.<V, Facet.Sound<V, Object>>of((Collection)sound, this.viewer);
/* 128 */     this.entitySound = Facet.<V, Facet.EntitySound<V, Object>>of((Collection)entitySound, this.viewer);
/* 129 */     this.book = Facet.<V, Facet.Book<V, Object, Object>>of((Collection)book, this.viewer);
/* 130 */     this.bossBar = Facet.<V, Facet.BossBar.Builder<V, Facet.BossBar<V>>>of((Collection)bossBar, this.viewer);
/* 131 */     this
/* 132 */       .bossBars = (this.bossBar == null) ? null : Collections.<BossBar, Facet.BossBar<V>>synchronizedMap(new IdentityHashMap<>(4));
/* 133 */     this.tabList = Facet.<V, Facet.TabList<V, Object>>of((Collection)tabList, this.viewer);
/* 134 */     this.pointerProviders = (pointerProviders == null) ? Collections.<Facet.Pointers<V>>emptyList() : (Collection)pointerProviders;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addViewer(@NotNull V viewer) {
/* 144 */     if (this.viewers.add(viewer) && this.viewer == null) {
/* 145 */       this.viewer = viewer;
/* 146 */       refresh();
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
/* 157 */     if (this.viewers.remove(viewer) && this.viewer == viewer) {
/* 158 */       this.viewer = this.viewers.isEmpty() ? null : this.viewers.iterator().next();
/* 159 */       refresh();
/*     */     } 
/*     */     
/* 162 */     if (this.bossBars == null)
/* 163 */       return;  for (Facet.BossBar<V> listener : this.bossBars.values()) {
/* 164 */       listener.removeViewer(viewer);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void refresh() {
/* 174 */     synchronized (this) {
/* 175 */       this.pointers = null;
/*     */     } 
/*     */     
/* 178 */     if (this.bossBars == null)
/* 179 */       return;  for (Map.Entry<BossBar, Facet.BossBar<V>> entry : this.bossBars.entrySet()) {
/* 180 */       BossBar bar = entry.getKey();
/* 181 */       Facet.BossBar<V> listener = entry.getValue();
/*     */       
/* 183 */       listener.bossBarNameChanged(bar, bar.name(), bar.name());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendMessage(@NotNull Identity source, @NotNull Component original, @NotNull MessageType type) {
/* 189 */     if (this.chat == null)
/*     */       return; 
/* 191 */     Object message = createMessage(original, this.chat);
/* 192 */     if (message == null)
/*     */       return; 
/* 194 */     for (V viewer : this.viewers) {
/* 195 */       this.chat.sendMessage(viewer, source, message, type);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendMessage(@NotNull Component original, ChatType.Bound boundChatType) {
/* 201 */     if (this.chat == null)
/* 202 */       return;  Object message = createMessage(original, this.chat);
/* 203 */     if (message == null)
/*     */       return; 
/* 205 */     Component name = this.provider.componentRenderer.render(boundChatType.name(), this);
/* 206 */     Component target = null;
/* 207 */     if (boundChatType.target() != null) {
/* 208 */       target = this.provider.componentRenderer.render(boundChatType.target(), this);
/*     */     }
/* 210 */     Object renderedType = boundChatType.type().bind((ComponentLike)name, (ComponentLike)target);
/*     */     
/* 212 */     for (V viewer : this.viewers) {
/* 213 */       this.chat.sendMessage(viewer, Identity.nil(), message, renderedType);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendMessage(@NotNull SignedMessage signedMessage, ChatType.Bound boundChatType) {
/* 219 */     if (signedMessage.isSystem()) {
/* 220 */       Component content = (signedMessage.unsignedContent() != null) ? signedMessage.unsignedContent() : (Component)Component.text(signedMessage.message());
/* 221 */       sendMessage(content, boundChatType);
/*     */     } else {
/* 223 */       super.sendMessage(signedMessage, boundChatType);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendActionBar(@NotNull Component original) {
/* 229 */     if (this.actionBar == null)
/*     */       return; 
/* 231 */     Object message = createMessage(original, this.actionBar);
/* 232 */     if (message == null)
/*     */       return; 
/* 234 */     for (V viewer : this.viewers) {
/* 235 */       this.actionBar.sendMessage(viewer, message);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void playSound(Sound original) {
/* 241 */     if (this.sound == null)
/*     */       return; 
/* 243 */     for (V viewer : this.viewers) {
/* 244 */       Object position = this.sound.createPosition(viewer);
/* 245 */       if (position == null)
/*     */         continue; 
/* 247 */       this.sound.playSound(viewer, original, position);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void playSound(@NotNull Sound sound, Sound.Emitter emitter) {
/* 253 */     if (this.entitySound == null)
/* 254 */       return;  if (emitter == Sound.Emitter.self()) {
/* 255 */       for (V viewer : this.viewers) {
/* 256 */         Object message = this.entitySound.createForSelf(viewer, sound);
/* 257 */         if (message == null)
/* 258 */           continue;  this.entitySound.playSound(viewer, message);
/*     */       } 
/*     */     } else {
/*     */       
/* 262 */       Object message = this.entitySound.createForEmitter(sound, emitter);
/* 263 */       if (message == null)
/* 264 */         return;  for (V viewer : this.viewers) {
/* 265 */         this.entitySound.playSound(viewer, message);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void playSound(Sound original, double x, double y, double z) {
/* 272 */     if (this.sound == null)
/*     */       return; 
/* 274 */     Object position = this.sound.createPosition(x, y, z);
/* 275 */     for (V viewer : this.viewers) {
/* 276 */       this.sound.playSound(viewer, original, position);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void stopSound(@NotNull SoundStop original) {
/* 282 */     if (this.sound == null)
/*     */       return; 
/* 284 */     for (V viewer : this.viewers) {
/* 285 */       this.sound.stopSound(viewer, original);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void openBook(Book original) {
/* 291 */     if (this.book == null)
/*     */       return; 
/* 293 */     String title = toPlain(original.title());
/* 294 */     String author = toPlain(original.author());
/* 295 */     List<Object> pages = new LinkedList();
/* 296 */     for (Component originalPage : original.pages()) {
/* 297 */       Object page = createMessage(originalPage, this.book);
/* 298 */       if (page != null) {
/* 299 */         pages.add(page);
/*     */       }
/*     */     } 
/* 302 */     if (title == null || author == null || pages.isEmpty())
/*     */       return; 
/* 304 */     Object book = this.book.createBook(title, author, pages);
/* 305 */     if (book == null)
/*     */       return; 
/* 307 */     for (V viewer : this.viewers) {
/* 308 */       this.book.openBook(viewer, book);
/*     */     }
/*     */   }
/*     */   
/*     */   private String toPlain(Component comp) {
/* 313 */     if (comp == null) {
/* 314 */       return null;
/*     */     }
/* 316 */     StringBuilder builder = new StringBuilder();
/* 317 */     Objects.requireNonNull(builder); ComponentFlattener.basic().flatten(this.provider.componentRenderer.render(comp, this), builder::append);
/* 318 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public void showTitle(Title original) {
/* 323 */     if (this.title == null)
/*     */       return; 
/* 325 */     Object mainTitle = createMessage(original.title(), this.title);
/* 326 */     Object subTitle = createMessage(original.subtitle(), this.title);
/* 327 */     Title.Times times = original.times();
/* 328 */     int inTicks = (times == null) ? -1 : this.title.toTicks(times.fadeIn());
/* 329 */     int stayTicks = (times == null) ? -1 : this.title.toTicks(times.stay());
/* 330 */     int outTicks = (times == null) ? -1 : this.title.toTicks(times.fadeOut());
/*     */     
/* 332 */     Object collection = this.title.createTitleCollection();
/* 333 */     if (inTicks != -1 || stayTicks != -1 || outTicks != -1) {
/* 334 */       this.title.contributeTimes(collection, inTicks, stayTicks, outTicks);
/*     */     }
/* 336 */     this.title.contributeSubtitle(collection, subTitle);
/* 337 */     this.title.contributeTitle(collection, mainTitle);
/* 338 */     Object title = this.title.completeTitle(collection);
/* 339 */     if (title == null)
/*     */       return; 
/* 341 */     for (V viewer : this.viewers) {
/* 342 */       this.title.showTitle(viewer, title);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
/* 348 */     if (this.title == null)
/*     */       return; 
/* 350 */     Objects.requireNonNull(value, "value");
/* 351 */     Object collection = this.title.createTitleCollection();
/* 352 */     if (part == TitlePart.TITLE) {
/* 353 */       Object message = createMessage((Component)value, this.title);
/* 354 */       if (message != null) this.title.contributeTitle(collection, message); 
/* 355 */     } else if (part == TitlePart.SUBTITLE) {
/* 356 */       Object message = createMessage((Component)value, this.title);
/* 357 */       if (message != null) this.title.contributeSubtitle(collection, message); 
/* 358 */     } else if (part == TitlePart.TIMES) {
/* 359 */       Title.Times times = (Title.Times)value;
/* 360 */       int inTicks = this.title.toTicks(times.fadeIn());
/* 361 */       int stayTicks = this.title.toTicks(times.stay());
/* 362 */       int outTicks = this.title.toTicks(times.fadeOut());
/* 363 */       if (inTicks != -1 || stayTicks != -1 || outTicks != -1) {
/* 364 */         this.title.contributeTimes(collection, inTicks, stayTicks, outTicks);
/*     */       }
/*     */     } else {
/* 367 */       throw new IllegalArgumentException("Unknown TitlePart '" + part + "'");
/*     */     } 
/*     */     
/* 370 */     Object title = this.title.completeTitle(collection);
/* 371 */     if (title == null)
/*     */       return; 
/* 373 */     for (V viewer : this.viewers) {
/* 374 */       this.title.showTitle(viewer, title);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearTitle() {
/* 380 */     if (this.title == null)
/*     */       return; 
/* 382 */     for (V viewer : this.viewers) {
/* 383 */       this.title.clearTitle(viewer);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void resetTitle() {
/* 389 */     if (this.title == null)
/*     */       return; 
/* 391 */     for (V viewer : this.viewers) {
/* 392 */       this.title.resetTitle(viewer);
/*     */     }
/*     */   }
/*     */   
/*     */   public void showBossBar(@NotNull BossBar bar) {
/*     */     Facet.BossBar<V> listener;
/* 398 */     if (this.bossBar == null || this.bossBars == null) {
/*     */       return;
/*     */     }
/* 401 */     synchronized (this.bossBars) {
/* 402 */       listener = this.bossBars.get(bar);
/* 403 */       if (listener == null) {
/*     */ 
/*     */         
/* 406 */         listener = new FacetBossBarListener<>(this.bossBar.createBossBar(this.viewers), message -> this.provider.componentRenderer.render(message, this));
/*     */         
/* 408 */         this.bossBars.put(bar, listener);
/*     */       } 
/*     */     } 
/*     */     
/* 412 */     if (listener.isEmpty()) {
/* 413 */       listener.bossBarInitialized(bar);
/* 414 */       bar.addListener(listener);
/*     */     } 
/*     */     
/* 417 */     for (V viewer : this.viewers) {
/* 418 */       listener.addViewer(viewer);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void hideBossBar(@NotNull BossBar bar) {
/* 424 */     if (this.bossBars == null)
/*     */       return; 
/* 426 */     Facet.BossBar<V> listener = this.bossBars.get(bar);
/* 427 */     if (listener == null)
/*     */       return; 
/* 429 */     for (V viewer : this.viewers) {
/* 430 */       listener.removeViewer(viewer);
/*     */     }
/*     */     
/* 433 */     if (listener.isEmpty() && this.bossBars.remove(bar) != null) {
/* 434 */       bar.removeListener(listener);
/* 435 */       listener.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendPlayerListHeader(@NotNull Component header) {
/* 441 */     if (this.tabList != null) {
/* 442 */       Object headerFormatted = createMessage(header, this.tabList);
/* 443 */       if (headerFormatted == null)
/* 444 */         return;  for (V viewer : this.viewers) {
/* 445 */         this.tabList.send(viewer, headerFormatted, null);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendPlayerListFooter(@NotNull Component footer) {
/* 452 */     if (this.tabList != null) {
/* 453 */       Object footerFormatted = createMessage(footer, this.tabList);
/* 454 */       if (footerFormatted == null)
/* 455 */         return;  for (V viewer : this.viewers) {
/* 456 */         this.tabList.send(viewer, null, footerFormatted);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
/* 463 */     if (this.tabList != null) {
/* 464 */       Object headerFormatted = createMessage(header, this.tabList);
/* 465 */       Object footerFormatted = createMessage(footer, this.tabList);
/* 466 */       if (headerFormatted == null || footerFormatted == null)
/*     */         return; 
/* 468 */       for (V viewer : this.viewers) {
/* 469 */         this.tabList.send(viewer, headerFormatted, footerFormatted);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Pointers pointers() {
/* 476 */     if (this.pointers == null) {
/* 477 */       synchronized (this) {
/* 478 */         if (this.pointers == null) {
/* 479 */           V viewer = this.viewer;
/* 480 */           if (viewer == null) return Pointers.empty(); 
/* 481 */           Pointers.Builder builder = Pointers.builder();
/*     */           
/* 483 */           contributePointers(builder);
/*     */ 
/*     */           
/* 486 */           for (Facet.Pointers<V> provider : this.pointerProviders) {
/* 487 */             if (provider.isApplicable(viewer)) {
/* 488 */               provider.contributePointers(viewer, builder);
/*     */             }
/*     */           } 
/* 491 */           return this.pointers = (Pointers)builder.build();
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 496 */     return this.pointers;
/*     */   }
/*     */ 
/*     */   
/*     */   @OverrideOnly
/*     */   protected void contributePointers(Pointers.Builder builder) {}
/*     */ 
/*     */   
/*     */   public void close() {
/* 505 */     if (this.bossBars != null) {
/* 506 */       for (BossBar bar : new LinkedList(this.bossBars.keySet())) {
/* 507 */         hideBossBar(bar);
/*     */       }
/* 509 */       this.bossBars.clear();
/*     */     } 
/*     */     
/* 512 */     for (V viewer : this.viewers) {
/* 513 */       removeViewer(viewer);
/*     */     }
/* 515 */     this.viewers.clear();
/*     */   }
/*     */   @Nullable
/*     */   private Object createMessage(@NotNull Component original, Facet.Message<V, Object> facet) {
/* 519 */     Component message = this.provider.componentRenderer.render(original, this);
/* 520 */     V viewer = this.viewer;
/* 521 */     return (viewer == null) ? null : facet.createMessage(viewer, message);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\platform\facet\FacetAudience.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */