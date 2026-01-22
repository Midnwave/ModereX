/*     */ package ac.grim.grimac.shaded.kyori.adventure.audience;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.bossbar.BossBar;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.chat.ChatType;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.chat.SignedMessage;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.dialog.DialogLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.identity.Identified;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.identity.Identity;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.inventory.Book;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.pointer.Pointered;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.resource.ResourcePackInfo;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.resource.ResourcePackInfoLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.resource.ResourcePackRequest;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.resource.ResourcePackRequestLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.sound.Sound;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.sound.SoundStop;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.ComponentLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.title.Title;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.title.TitlePart;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.UUID;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Predicate;
/*     */ import java.util.stream.Collector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface Audience
/*     */   extends Pointered
/*     */ {
/*     */   @NotNull
/*     */   static Audience empty() {
/* 113 */     return EmptyAudience.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Audience audience(@NotNull Audience... audiences) {
/* 125 */     int length = audiences.length;
/* 126 */     if (length == 0)
/* 127 */       return empty(); 
/* 128 */     if (length == 1) {
/* 129 */       return audiences[0];
/*     */     }
/* 131 */     return audience(Arrays.asList(audiences));
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
/*     */   @NotNull
/*     */   static ForwardingAudience audience(@NotNull Iterable<? extends Audience> audiences) {
/* 146 */     return () -> audiences;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Collector<? super Audience, ?, ForwardingAudience> toAudience() {
/* 158 */     return Audiences.COLLECTOR;
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
/*     */   @NotNull
/*     */   default Audience filterAudience(@NotNull Predicate<? super Audience> filter) {
/* 175 */     return filter.test(this) ? 
/* 176 */       this : 
/* 177 */       empty();
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
/*     */   default void forEachAudience(@NotNull Consumer<? super Audience> action) {
/* 194 */     action.accept(this);
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
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   default void sendMessage(@NotNull ComponentLike message) {
/* 209 */     sendMessage(message.asComponent());
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
/*     */   default void sendMessage(@NotNull Component message) {
/* 223 */     sendMessage(message, MessageType.SYSTEM);
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
/*     */   @Deprecated
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   @ScheduledForRemoval(inVersion = "5.0.0")
/*     */   default void sendMessage(@NotNull ComponentLike message, @NotNull MessageType type) {
/* 241 */     sendMessage(message.asComponent(), type);
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
/*     */   @Deprecated
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   @ScheduledForRemoval(inVersion = "5.0.0")
/*     */   default void sendMessage(@NotNull Component message, @NotNull MessageType type) {
/* 259 */     sendMessage(Identity.nil(), message, type);
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
/*     */   @Deprecated
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   default void sendMessage(@NotNull Identified source, @NotNull ComponentLike message) {
/* 276 */     sendMessage(source, message.asComponent());
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
/*     */   @Deprecated
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   default void sendMessage(@NotNull Identity source, @NotNull ComponentLike message) {
/* 291 */     sendMessage(source, message.asComponent());
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
/*     */   @Deprecated
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   default void sendMessage(@NotNull Identified source, @NotNull Component message) {
/* 306 */     sendMessage(source, message, MessageType.CHAT);
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
/*     */   @Deprecated
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   default void sendMessage(@NotNull Identity source, @NotNull Component message) {
/* 321 */     sendMessage(source, message, MessageType.CHAT);
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
/*     */   @Deprecated
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   @ScheduledForRemoval(inVersion = "5.0.0")
/*     */   default void sendMessage(@NotNull Identified source, @NotNull ComponentLike message, @NotNull MessageType type) {
/* 338 */     sendMessage(source, message.asComponent(), type);
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
/*     */   @Deprecated
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   @ScheduledForRemoval(inVersion = "5.0.0")
/*     */   default void sendMessage(@NotNull Identity source, @NotNull ComponentLike message, @NotNull MessageType type) {
/* 355 */     sendMessage(source, message.asComponent(), type);
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
/*     */   @Deprecated
/*     */   @ScheduledForRemoval(inVersion = "5.0.0")
/*     */   default void sendMessage(@NotNull Identified source, @NotNull Component message, @NotNull MessageType type) {
/* 371 */     sendMessage(source.identity(), message, type);
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
/*     */   @Deprecated
/*     */   @ScheduledForRemoval(inVersion = "5.0.0")
/*     */   default void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default void sendMessage(@NotNull Component message, ChatType.Bound boundChatType) {
/* 402 */     sendMessage(message, MessageType.CHAT);
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
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   default void sendMessage(@NotNull ComponentLike message, ChatType.Bound boundChatType) {
/* 415 */     sendMessage(message.asComponent(), boundChatType);
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
/*     */   default void sendMessage(@NotNull SignedMessage signedMessage, ChatType.Bound boundChatType) {
/* 430 */     Component content = (signedMessage.unsignedContent() != null) ? signedMessage.unsignedContent() : (Component)Component.text(signedMessage.message());
/* 431 */     if (signedMessage.isSystem()) {
/* 432 */       sendMessage(content);
/*     */     } else {
/* 434 */       sendMessage(signedMessage.identity(), content, MessageType.CHAT);
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
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   default void deleteMessage(@NotNull SignedMessage signedMessage) {
/* 448 */     if (signedMessage.canDelete()) {
/* 449 */       deleteMessage(Objects.<SignedMessage.Signature>requireNonNull(signedMessage.signature()));
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
/*     */   default void deleteMessage(SignedMessage.Signature signature) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   default void sendActionBar(@NotNull ComponentLike message) {
/* 473 */     sendActionBar(message.asComponent());
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
/*     */   default void sendActionBar(@NotNull Component message) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   default void sendPlayerListHeader(@NotNull ComponentLike header) {
/* 497 */     sendPlayerListHeader(header.asComponent());
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
/*     */   default void sendPlayerListHeader(@NotNull Component header) {
/* 510 */     sendPlayerListHeaderAndFooter(header, (Component)Component.empty());
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
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   default void sendPlayerListFooter(@NotNull ComponentLike footer) {
/* 524 */     sendPlayerListFooter(footer.asComponent());
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
/*     */   default void sendPlayerListFooter(@NotNull Component footer) {
/* 537 */     sendPlayerListHeaderAndFooter((Component)Component.empty(), footer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   default void sendPlayerListHeaderAndFooter(@NotNull ComponentLike header, @NotNull ComponentLike footer) {
/* 549 */     sendPlayerListHeaderAndFooter(header.asComponent(), footer.asComponent());
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
/*     */   default void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   default void showTitle(@NotNull Title title) {
/* 571 */     Title.Times times = title.times();
/* 572 */     if (times != null) sendTitlePart(TitlePart.TIMES, times);
/*     */     
/* 574 */     sendTitlePart(TitlePart.SUBTITLE, title.subtitle());
/* 575 */     sendTitlePart(TitlePart.TITLE, title.title());
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
/*     */   default <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default void clearTitle() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default void resetTitle() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default void showBossBar(@NotNull BossBar bar) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default void hideBossBar(@NotNull BossBar bar) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default void playSound(@NotNull Sound sound) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default void playSound(@NotNull Sound sound, double x, double y, double z) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default void playSound(@NotNull Sound sound, Sound.Emitter emitter) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   default void stopSound(@NotNull Sound sound) {
/* 678 */     stopSound(((Sound)Objects.<Sound>requireNonNull(sound, "sound")).asStop());
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
/*     */   default void stopSound(@NotNull SoundStop stop) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   default void openBook(Book.Builder book) {
/* 702 */     openBook(book.build());
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
/*     */   default void openBook(@NotNull Book book) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   void sendResourcePacks(@NotNull ResourcePackInfoLike first, @NotNull ResourcePackInfoLike... others) {
/* 733 */     sendResourcePacks(ResourcePackRequest.addingRequest(first, others));
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
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   default void sendResourcePacks(@NotNull ResourcePackRequestLike request) {
/* 747 */     sendResourcePacks(request.asResourcePackRequest());
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
/*     */   default void sendResourcePacks(@NotNull ResourcePackRequest request) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   default void removeResourcePacks(@NotNull ResourcePackRequestLike request) {
/* 771 */     removeResourcePacks(request.asResourcePackRequest());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   default void removeResourcePacks(@NotNull ResourcePackRequest request) {
/* 783 */     List<ResourcePackInfo> infos = request.packs();
/* 784 */     if (infos.size() == 1) {
/* 785 */       removeResourcePacks(((ResourcePackInfo)infos.get(0)).id(), new UUID[0]);
/* 786 */     } else if (infos.isEmpty()) {
/*     */       return;
/*     */     } 
/*     */     
/* 790 */     UUID[] otherReqs = new UUID[infos.size() - 1];
/* 791 */     for (int i = 0; i < otherReqs.length; i++) {
/* 792 */       otherReqs[i] = ((ResourcePackInfo)infos.get(i + 1)).id();
/*     */     }
/* 794 */     removeResourcePacks(((ResourcePackInfo)infos.get(0)).id(), otherReqs);
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
/*     */   @ForwardingAudienceOverrideNotRequired
/*     */   void removeResourcePacks(@NotNull ResourcePackInfoLike request, @NotNull ResourcePackInfoLike... others) {
/* 807 */     UUID[] otherReqs = new UUID[others.length];
/* 808 */     for (int i = 0; i < others.length; i++) {
/* 809 */       otherReqs[i] = others[i].asResourcePackInfo().id();
/*     */     }
/* 811 */     removeResourcePacks(request.asResourcePackInfo().id(), otherReqs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default void removeResourcePacks(@NotNull Iterable<UUID> ids) {
/*     */     UUID[] others;
/* 823 */     Iterator<UUID> it = ids.iterator();
/* 824 */     if (!it.hasNext())
/*     */       return; 
/* 826 */     UUID id = it.next();
/*     */     
/* 828 */     if (!it.hasNext()) {
/* 829 */       others = new UUID[0];
/* 830 */     } else if (ids instanceof Collection) {
/* 831 */       others = new UUID[((Collection)ids).size() - 1];
/* 832 */       for (int i = 0; i < others.length; i++) {
/* 833 */         others[i] = it.next();
/*     */       }
/*     */     } else {
/* 836 */       List<UUID> othersList = new ArrayList<>();
/* 837 */       while (it.hasNext()) {
/* 838 */         othersList.add(it.next());
/*     */       }
/* 840 */       others = othersList.<UUID>toArray(new UUID[0]);
/*     */     } 
/*     */     
/* 843 */     removeResourcePacks(id, others);
/*     */   }
/*     */   
/*     */   void removeResourcePacks(@NotNull UUID id, @NotNull UUID... others) {}
/*     */   
/*     */   default void clearResourcePacks() {}
/*     */   
/*     */   default void showDialog(@NotNull DialogLike dialog) {}
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\audience\Audience.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */