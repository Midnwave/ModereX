/*     */ package ac.grim.grimac.shaded.kyori.adventure.audience;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.OverrideOnly;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.chat.ChatType;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.chat.SignedMessage;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.dialog.DialogLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.identity.Identified;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.identity.Identity;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.inventory.Book;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.pointer.Pointer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.pointer.Pointers;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.resource.ResourcePackRequest;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.sound.Sound;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.sound.SoundStop;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.title.TitlePart;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Optional;
/*     */ import java.util.UUID;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Predicate;
/*     */ import java.util.function.Supplier;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @FunctionalInterface
/*     */ public interface ForwardingAudience
/*     */   extends Audience
/*     */ {
/*     */   @OverrideOnly
/*     */   @NotNull
/*     */   Iterable<? extends Audience> audiences();
/*     */   
/*     */   @NotNull
/*     */   default Pointers pointers() {
/*  76 */     return Pointers.empty();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   default Audience filterAudience(@NotNull Predicate<? super Audience> filter) {
/*  81 */     List<Audience> audiences = null;
/*  82 */     for (Audience audience : audiences()) {
/*  83 */       if (filter.test(audience)) {
/*  84 */         Audience filtered = audience.filterAudience(filter);
/*  85 */         if (filtered != Audience.empty()) {
/*  86 */           if (audiences == null) {
/*  87 */             audiences = new ArrayList<>();
/*     */           }
/*  89 */           audiences.add(filtered);
/*     */         } 
/*     */       } 
/*     */     } 
/*  93 */     return (audiences != null) ? 
/*  94 */       Audience.audience(audiences) : 
/*  95 */       Audience.empty();
/*     */   }
/*     */ 
/*     */   
/*     */   default void forEachAudience(@NotNull Consumer<? super Audience> action) {
/* 100 */     for (Audience audience : audiences()) audience.forEachAudience(action);
/*     */   
/*     */   }
/*     */   
/*     */   default void sendMessage(@NotNull Component message) {
/* 105 */     for (Audience audience : audiences()) audience.sendMessage(message);
/*     */   
/*     */   }
/*     */   
/*     */   default void sendMessage(@NotNull Component message, ChatType.Bound boundChatType) {
/* 110 */     for (Audience audience : audiences()) audience.sendMessage(message, boundChatType);
/*     */   
/*     */   }
/*     */   
/*     */   default void sendMessage(@NotNull SignedMessage signedMessage, ChatType.Bound boundChatType) {
/* 115 */     for (Audience audience : audiences()) audience.sendMessage(signedMessage, boundChatType);
/*     */   
/*     */   }
/*     */   
/*     */   default void deleteMessage(SignedMessage.Signature signature) {
/* 120 */     for (Audience audience : audiences()) audience.deleteMessage(signature);
/*     */   
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   default void sendMessage(@NotNull Identified source, @NotNull Component message, @NotNull MessageType type) {
/* 126 */     for (Audience audience : audiences()) audience.sendMessage(source, message, type);
/*     */   
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   default void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
/* 132 */     for (Audience audience : audiences()) audience.sendMessage(source, message, type);
/*     */   
/*     */   }
/*     */   
/*     */   default void sendActionBar(@NotNull Component message) {
/* 137 */     for (Audience audience : audiences()) audience.sendActionBar(message);
/*     */   
/*     */   }
/*     */   
/*     */   default void sendPlayerListHeader(@NotNull Component header) {
/* 142 */     for (Audience audience : audiences()) audience.sendPlayerListHeader(header);
/*     */   
/*     */   }
/*     */   
/*     */   default void sendPlayerListFooter(@NotNull Component footer) {
/* 147 */     for (Audience audience : audiences()) audience.sendPlayerListFooter(footer);
/*     */   
/*     */   }
/*     */   
/*     */   default void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
/* 152 */     for (Audience audience : audiences()) audience.sendPlayerListHeaderAndFooter(header, footer);
/*     */   
/*     */   }
/*     */   
/*     */   default <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
/* 157 */     for (Audience audience : audiences()) audience.sendTitlePart(part, value);
/*     */   
/*     */   }
/*     */   
/*     */   default void clearTitle() {
/* 162 */     for (Audience audience : audiences()) audience.clearTitle();
/*     */   
/*     */   }
/*     */   
/*     */   default void resetTitle() {
/* 167 */     for (Audience audience : audiences()) audience.resetTitle();
/*     */   
/*     */   }
/*     */   
/*     */   default void showBossBar(@NotNull BossBar bar) {
/* 172 */     for (Audience audience : audiences()) audience.showBossBar(bar);
/*     */   
/*     */   }
/*     */   
/*     */   default void hideBossBar(@NotNull BossBar bar) {
/* 177 */     for (Audience audience : audiences()) audience.hideBossBar(bar);
/*     */   
/*     */   }
/*     */   
/*     */   default void playSound(@NotNull Sound sound) {
/* 182 */     for (Audience audience : audiences()) audience.playSound(sound);
/*     */   
/*     */   }
/*     */   
/*     */   default void playSound(@NotNull Sound sound, double x, double y, double z) {
/* 187 */     for (Audience audience : audiences()) audience.playSound(sound, x, y, z);
/*     */   
/*     */   }
/*     */   
/*     */   default void playSound(@NotNull Sound sound, Sound.Emitter emitter) {
/* 192 */     for (Audience audience : audiences()) audience.playSound(sound, emitter);
/*     */   
/*     */   }
/*     */   
/*     */   default void stopSound(@NotNull SoundStop stop) {
/* 197 */     for (Audience audience : audiences()) audience.stopSound(stop);
/*     */   
/*     */   }
/*     */   
/*     */   default void openBook(@NotNull Book book) {
/* 202 */     for (Audience audience : audiences()) audience.openBook(book);
/*     */   
/*     */   }
/*     */   
/*     */   default void sendResourcePacks(@NotNull ResourcePackRequest request) {
/* 207 */     for (Audience audience : audiences()) audience.sendResourcePacks(request);
/*     */   
/*     */   }
/*     */   
/*     */   default void removeResourcePacks(@NotNull Iterable<UUID> ids) {
/* 212 */     for (Audience audience : audiences()) audience.removeResourcePacks(ids);
/*     */   
/*     */   }
/*     */   
/*     */   void removeResourcePacks(@NotNull UUID id, @NotNull UUID... others) {
/* 217 */     for (Audience audience : audiences()) audience.removeResourcePacks(id, others);
/*     */   
/*     */   }
/*     */   
/*     */   default void clearResourcePacks() {
/* 222 */     for (Audience audience : audiences()) audience.clearResourcePacks();
/*     */   
/*     */   }
/*     */   
/*     */   default void showDialog(@NotNull DialogLike dialog) {
/* 227 */     for (Audience audience : audiences()) audience.showDialog(dialog);
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface Single
/*     */     extends ForwardingAudience
/*     */   {
/*     */     @OverrideOnly
/*     */     @NotNull
/*     */     Audience audience();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     @NotNull
/*     */     default Iterable<? extends Audience> audiences() {
/* 254 */       return Collections.singleton(audience());
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     default <T> Optional<T> get(@NotNull Pointer<T> pointer) {
/* 259 */       return audience().get(pointer);
/*     */     }
/*     */     
/*     */     @Contract("_, null -> null; _, !null -> !null")
/*     */     @Nullable
/*     */     default <T> T getOrDefault(@NotNull Pointer<T> pointer, @Nullable T defaultValue) {
/* 265 */       return (T)audience().getOrDefault(pointer, defaultValue);
/*     */     }
/*     */ 
/*     */     
/*     */     default <T> T getOrDefaultFrom(@NotNull Pointer<T> pointer, @NotNull Supplier<? extends T> defaultValue) {
/* 270 */       return (T)audience().getOrDefaultFrom(pointer, defaultValue);
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     default Audience filterAudience(@NotNull Predicate<? super Audience> filter) {
/* 275 */       Audience audience = audience();
/* 276 */       return filter.test(audience) ? 
/* 277 */         this : 
/* 278 */         Audience.empty();
/*     */     }
/*     */ 
/*     */     
/*     */     default void forEachAudience(@NotNull Consumer<? super Audience> action) {
/* 283 */       audience().forEachAudience(action);
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     default Pointers pointers() {
/* 288 */       return audience().pointers();
/*     */     }
/*     */ 
/*     */     
/*     */     default void sendMessage(@NotNull Component message) {
/* 293 */       audience().sendMessage(message);
/*     */     }
/*     */ 
/*     */     
/*     */     default void sendMessage(@NotNull Component message, ChatType.Bound boundChatType) {
/* 298 */       audience().sendMessage(message, boundChatType);
/*     */     }
/*     */ 
/*     */     
/*     */     default void sendMessage(@NotNull SignedMessage signedMessage, ChatType.Bound boundChatType) {
/* 303 */       audience().sendMessage(signedMessage, boundChatType);
/*     */     }
/*     */ 
/*     */     
/*     */     default void deleteMessage(SignedMessage.Signature signature) {
/* 308 */       audience().deleteMessage(signature);
/*     */     }
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     default void sendMessage(@NotNull Identified source, @NotNull Component message, @NotNull MessageType type) {
/* 314 */       audience().sendMessage(source, message, type);
/*     */     }
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     default void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
/* 320 */       audience().sendMessage(source, message, type);
/*     */     }
/*     */ 
/*     */     
/*     */     default void sendActionBar(@NotNull Component message) {
/* 325 */       audience().sendActionBar(message);
/*     */     }
/*     */ 
/*     */     
/*     */     default void sendPlayerListHeader(@NotNull Component header) {
/* 330 */       audience().sendPlayerListHeader(header);
/*     */     }
/*     */ 
/*     */     
/*     */     default void sendPlayerListFooter(@NotNull Component footer) {
/* 335 */       audience().sendPlayerListFooter(footer);
/*     */     }
/*     */ 
/*     */     
/*     */     default void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
/* 340 */       audience().sendPlayerListHeaderAndFooter(header, footer);
/*     */     }
/*     */ 
/*     */     
/*     */     default <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
/* 345 */       audience().sendTitlePart(part, value);
/*     */     }
/*     */ 
/*     */     
/*     */     default void clearTitle() {
/* 350 */       audience().clearTitle();
/*     */     }
/*     */ 
/*     */     
/*     */     default void resetTitle() {
/* 355 */       audience().resetTitle();
/*     */     }
/*     */ 
/*     */     
/*     */     default void showBossBar(@NotNull BossBar bar) {
/* 360 */       audience().showBossBar(bar);
/*     */     }
/*     */ 
/*     */     
/*     */     default void hideBossBar(@NotNull BossBar bar) {
/* 365 */       audience().hideBossBar(bar);
/*     */     }
/*     */ 
/*     */     
/*     */     default void playSound(@NotNull Sound sound) {
/* 370 */       audience().playSound(sound);
/*     */     }
/*     */ 
/*     */     
/*     */     default void playSound(@NotNull Sound sound, double x, double y, double z) {
/* 375 */       audience().playSound(sound, x, y, z);
/*     */     }
/*     */ 
/*     */     
/*     */     default void playSound(@NotNull Sound sound, Sound.Emitter emitter) {
/* 380 */       audience().playSound(sound, emitter);
/*     */     }
/*     */ 
/*     */     
/*     */     default void stopSound(@NotNull SoundStop stop) {
/* 385 */       audience().stopSound(stop);
/*     */     }
/*     */ 
/*     */     
/*     */     default void openBook(@NotNull Book book) {
/* 390 */       audience().openBook(book);
/*     */     }
/*     */ 
/*     */     
/*     */     default void sendResourcePacks(@NotNull ResourcePackRequest request) {
/* 395 */       audience().sendResourcePacks(request.callback(Audiences.unwrapCallback(this, audience(), request.callback())));
/*     */     }
/*     */ 
/*     */     
/*     */     default void removeResourcePacks(@NotNull Iterable<UUID> ids) {
/* 400 */       audience().removeResourcePacks(ids);
/*     */     }
/*     */ 
/*     */     
/*     */     void removeResourcePacks(@NotNull UUID id, @NotNull UUID... others) {
/* 405 */       audience().removeResourcePacks(id, others);
/*     */     }
/*     */ 
/*     */     
/*     */     default void clearResourcePacks() {
/* 410 */       audience().clearResourcePacks();
/*     */     }
/*     */ 
/*     */     
/*     */     default void showDialog(@NotNull DialogLike dialog) {
/* 415 */       audience().showDialog(dialog);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\audience\ForwardingAudience.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */