/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.LastSeenMessages;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.filter.FilterMask;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import java.time.Instant;
/*     */ import java.util.Optional;
/*     */ import java.util.UUID;
/*     */ 
/*     */ public class ChatMessage_v1_19_3 extends ChatMessage_v1_16 {
/*     */   int index;
/*     */   byte[] signature;
/*     */   String plainContent;
/*     */   Instant timestamp;
/*     */   long salt;
/*     */   LastSeenMessages.Packed lastSeenMessagesPacked;
/*     */   @Nullable
/*     */   Component unsignedChatContent;
/*     */   FilterMask filterMask;
/*     */   ChatType.Bound chatFormatting;
/*     */   
/*     */   public ChatMessage_v1_19_3(UUID senderUUID, int index, byte[] signature, String plainContent, Instant timestamp, long salt, LastSeenMessages.Packed lastSeenMessagesPacked, @Nullable Component unsignedChatContent, FilterMask filterMask, ChatType.Bound chatFormatting) {
/*  25 */     super((Component)Component.text(plainContent), chatFormatting.getType(), senderUUID);
/*  26 */     this.index = index;
/*  27 */     this.signature = signature;
/*  28 */     this.plainContent = plainContent;
/*  29 */     this.timestamp = timestamp;
/*  30 */     this.salt = salt;
/*  31 */     this.lastSeenMessagesPacked = lastSeenMessagesPacked;
/*  32 */     this.unsignedChatContent = unsignedChatContent;
/*  33 */     this.filterMask = filterMask;
/*  34 */     this.chatFormatting = chatFormatting;
/*     */   }
/*     */   
/*     */   public int getIndex() {
/*  38 */     return this.index;
/*     */   }
/*     */   
/*     */   public void setIndex(int index) {
/*  42 */     this.index = index;
/*     */   }
/*     */   
/*     */   public byte[] getSignature() {
/*  46 */     return this.signature;
/*     */   }
/*     */   
/*     */   public void setSignature(byte[] signature) {
/*  50 */     this.signature = signature;
/*     */   }
/*     */ 
/*     */   
/*     */   public Component getChatContent() {
/*  55 */     return (Component)Component.text(this.plainContent);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setChatContent(Component chatContent) {
/*  61 */     throw new UnsupportedOperationException("PacketEvents is not able to serialize components to plain-text. Please use the #setPlainContent instead to update the content.");
/*     */   }
/*     */   
/*     */   public String getPlainContent() {
/*  65 */     return this.plainContent;
/*     */   }
/*     */   
/*     */   public void setPlainContent(String plainContent) {
/*  69 */     this.plainContent = plainContent;
/*     */   }
/*     */   
/*     */   public Instant getTimestamp() {
/*  73 */     return this.timestamp;
/*     */   }
/*     */   
/*     */   public void setTimestamp(Instant timestamp) {
/*  77 */     this.timestamp = timestamp;
/*     */   }
/*     */   
/*     */   public long getSalt() {
/*  81 */     return this.salt;
/*     */   }
/*     */   
/*     */   public void setSalt(long salt) {
/*  85 */     this.salt = salt;
/*     */   }
/*     */   
/*     */   public LastSeenMessages.Packed getLastSeenMessagesPacked() {
/*  89 */     return this.lastSeenMessagesPacked;
/*     */   }
/*     */   
/*     */   public void setLastSeenMessagesPacked(LastSeenMessages.Packed lastSeenMessagesPacked) {
/*  93 */     this.lastSeenMessagesPacked = lastSeenMessagesPacked;
/*     */   }
/*     */   
/*     */   public Optional<Component> getUnsignedChatContent() {
/*  97 */     return Optional.ofNullable(this.unsignedChatContent);
/*     */   }
/*     */   
/*     */   public void setUnsignedChatContent(@Nullable Component unsignedChatContent) {
/* 101 */     this.unsignedChatContent = unsignedChatContent;
/*     */   }
/*     */   
/*     */   public FilterMask getFilterMask() {
/* 105 */     return this.filterMask;
/*     */   }
/*     */   
/*     */   public void setFilterMask(FilterMask filterMask) {
/* 109 */     this.filterMask = filterMask;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChatType.Bound getChatFormatting() {
/* 114 */     return this.chatFormatting;
/*     */   }
/*     */   
/*     */   public void setChatFormatting(ChatType.Bound chatFormatting) {
/* 118 */     this.chatFormatting = chatFormatting;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ChatType.Bound getChatType() {
/* 127 */     return this.chatFormatting;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setChatType(ChatType.Bound chatFormatting) {
/* 136 */     this.chatFormatting = chatFormatting;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\message\ChatMessage_v1_19_3.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */