/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.message;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.ChatType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.LastSeenMessages;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.filter.FilterMask;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import java.time.Instant;
/*     */ import java.util.UUID;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ChatMessage_v1_19_1
/*     */   extends ChatMessage_v1_16
/*     */ {
/*     */   private String plainContent;
/*     */   @Nullable
/*     */   private Component unsignedChatContent;
/*     */   private ChatType.Bound chatFormatting;
/*     */   private byte[] previousSignature;
/*     */   private byte[] signature;
/*     */   private Instant timestamp;
/*     */   private long salt;
/*     */   private LastSeenMessages lastSeenMessages;
/*     */   private FilterMask filterMask;
/*     */   
/*     */   public ChatMessage_v1_19_1(String plainContent, Component decoratedChatContent, @Nullable Component unsignedChatContent, UUID senderUUID, ChatType.Bound chatFormatting, byte[] previousSignature, byte[] signature, Instant timestamp, long salt, LastSeenMessages lastSeenMessages, FilterMask filterMask) {
/*  48 */     super(decoratedChatContent, chatFormatting.getType(), senderUUID);
/*  49 */     this.plainContent = plainContent;
/*  50 */     this.unsignedChatContent = unsignedChatContent;
/*  51 */     this.chatFormatting = chatFormatting;
/*  52 */     this.previousSignature = previousSignature;
/*  53 */     this.signature = signature;
/*  54 */     this.timestamp = timestamp;
/*  55 */     this.salt = salt;
/*  56 */     this.lastSeenMessages = lastSeenMessages;
/*  57 */     this.filterMask = filterMask;
/*     */   }
/*     */   
/*     */   public String getPlainContent() {
/*  61 */     return this.plainContent;
/*     */   }
/*     */   
/*     */   public void setPlainContent(String plainContent) {
/*  65 */     this.plainContent = plainContent;
/*     */   }
/*     */   
/*     */   public boolean isChatContentDecorated() {
/*  69 */     return !getChatContent().equals(Component.text(this.plainContent));
/*     */   }
/*     */   @Nullable
/*     */   public Component getUnsignedChatContent() {
/*  73 */     return this.unsignedChatContent;
/*     */   }
/*     */   
/*     */   public void setUnsignedChatContent(@Nullable Component unsignedChatContent) {
/*  77 */     this.unsignedChatContent = unsignedChatContent;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChatType getType() {
/*  82 */     return this.chatFormatting.getType();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setType(ChatType type) {
/*  87 */     this.chatFormatting.setType(type);
/*     */   }
/*     */   
/*     */   public ChatType.Bound getChatFormatting() {
/*  91 */     return this.chatFormatting;
/*     */   }
/*     */   
/*     */   public void setChatFormatting(ChatType.Bound chatFormatting) {
/*  95 */     this.chatFormatting = chatFormatting;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ChatType.Bound getChatType() {
/* 104 */     return this.chatFormatting;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setChatType(ChatType.Bound chatFormatting) {
/* 113 */     this.chatFormatting = chatFormatting;
/*     */   }
/*     */   
/*     */   public byte[] getPreviousSignature() {
/* 117 */     return this.previousSignature;
/*     */   }
/*     */   
/*     */   public void setPreviousSignature(byte[] previousSignature) {
/* 121 */     this.previousSignature = previousSignature;
/*     */   }
/*     */   
/*     */   public byte[] getSignature() {
/* 125 */     return this.signature;
/*     */   }
/*     */   
/*     */   public void setSignature(byte[] signature) {
/* 129 */     this.signature = signature;
/*     */   }
/*     */   
/*     */   public Instant getTimestamp() {
/* 133 */     return this.timestamp;
/*     */   }
/*     */   
/*     */   public void setTimestamp(Instant timestamp) {
/* 137 */     this.timestamp = timestamp;
/*     */   }
/*     */   
/*     */   public long getSalt() {
/* 141 */     return this.salt;
/*     */   }
/*     */   
/*     */   public void setSalt(long salt) {
/* 145 */     this.salt = salt;
/*     */   }
/*     */   
/*     */   public LastSeenMessages getLastSeenMessages() {
/* 149 */     return this.lastSeenMessages;
/*     */   }
/*     */   
/*     */   public void setLastSeenMessages(LastSeenMessages lastSeenMessages) {
/* 153 */     this.lastSeenMessages = lastSeenMessages;
/*     */   }
/*     */   
/*     */   public FilterMask getFilterMask() {
/* 157 */     return this.filterMask;
/*     */   }
/*     */   
/*     */   public void setFilterMask(FilterMask filterMask) {
/* 161 */     this.filterMask = filterMask;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static class ChatTypeBoundNetwork
/*     */   {
/*     */     private ChatType type;
/*     */     private Component name;
/*     */     @Nullable
/*     */     private Component targetName;
/*     */     
/*     */     public ChatTypeBoundNetwork(ChatType type, Component name, @Nullable Component targetName) {
/* 174 */       this.type = type;
/* 175 */       this.name = name;
/* 176 */       this.targetName = targetName;
/*     */     }
/*     */     
/*     */     public ChatType getType() {
/* 180 */       return this.type;
/*     */     }
/*     */     
/*     */     public void setType(ChatType type) {
/* 184 */       this.type = type;
/*     */     }
/*     */     
/*     */     public Component getName() {
/* 188 */       return this.name;
/*     */     }
/*     */     
/*     */     public void setName(Component name) {
/* 192 */       this.name = name;
/*     */     }
/*     */     @Nullable
/*     */     public Component getTargetName() {
/* 196 */       return this.targetName;
/*     */     }
/*     */     
/*     */     public void setTargetName(@Nullable Component targetName) {
/* 200 */       this.targetName = targetName;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\message\ChatMessage_v1_19_1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */