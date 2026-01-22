/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.io.DataOutput;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.BitSet;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ 
/*     */ public class LastSeenMessages
/*     */ {
/*  14 */   public static final LastSeenMessages EMPTY = new LastSeenMessages(new ArrayList<>());
/*     */   
/*     */   private final List<Entry> entries;
/*     */   
/*     */   public LastSeenMessages(List<Entry> entries) {
/*  19 */     this.entries = entries;
/*     */   }
/*     */   
/*     */   public void updateHash(DataOutput output) throws IOException {
/*  23 */     for (Entry entry : this.entries) {
/*  24 */       UUID uuid = entry.getUUID();
/*  25 */       byte[] lastVerifier = entry.getLastVerifier();
/*  26 */       output.writeByte(70);
/*  27 */       output.writeLong(uuid.getMostSignificantBits());
/*  28 */       output.writeLong(uuid.getLeastSignificantBits());
/*  29 */       output.write(lastVerifier);
/*     */     } 
/*     */   }
/*     */   
/*     */   public List<Entry> getEntries() {
/*  34 */     return this.entries;
/*     */   }
/*     */   
/*     */   public static class Packed {
/*     */     private List<MessageSignature.Packed> packedMessageSignatures;
/*     */     
/*     */     public Packed(List<MessageSignature.Packed> packedMessageSignatures) {
/*  41 */       this.packedMessageSignatures = packedMessageSignatures;
/*     */     }
/*     */     
/*     */     public List<MessageSignature.Packed> getPackedMessageSignatures() {
/*  45 */       return this.packedMessageSignatures;
/*     */     }
/*     */     
/*     */     public void setPackedMessageSignatures(List<MessageSignature.Packed> packedMessageSignatures) {
/*  49 */       this.packedMessageSignatures = packedMessageSignatures;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class Entry {
/*     */     private final UUID uuid;
/*     */     private final byte[] signature;
/*     */     
/*     */     public Entry(UUID uuid, byte[] lastVerifier) {
/*  58 */       this.uuid = uuid;
/*  59 */       this.signature = lastVerifier;
/*     */     }
/*     */     
/*     */     public UUID getUUID() {
/*  63 */       return this.uuid;
/*     */     }
/*     */     
/*     */     public byte[] getLastVerifier() {
/*  67 */       return this.signature;
/*     */     } }
/*     */   
/*     */   public static class LegacyUpdate {
/*     */     private final LastSeenMessages lastSeenMessages;
/*     */     @Nullable
/*     */     private final LastSeenMessages.Entry lastReceived;
/*     */     
/*     */     public LegacyUpdate(LastSeenMessages lastSeenMessages, @Nullable LastSeenMessages.Entry lastReceived) {
/*  76 */       this.lastSeenMessages = lastSeenMessages;
/*  77 */       this.lastReceived = lastReceived;
/*     */     }
/*     */     
/*     */     public LastSeenMessages getLastSeenMessages() {
/*  81 */       return this.lastSeenMessages;
/*     */     }
/*     */     @Nullable
/*     */     public LastSeenMessages.Entry getLastReceived() {
/*  85 */       return this.lastReceived;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Update
/*     */   {
/*     */     private final int offset;
/*     */     
/*     */     private final BitSet acknowledged;
/*     */     
/*     */     private final byte checksum;
/*     */     
/*     */     @Obsolete
/*     */     public Update(int offset, BitSet acknowledged) {
/* 100 */       this(offset, acknowledged, (byte)0);
/*     */     }
/*     */     
/*     */     public Update(int offset, BitSet acknowledged, byte checksum) {
/* 104 */       this.offset = offset;
/* 105 */       this.acknowledged = acknowledged;
/* 106 */       this.checksum = checksum;
/*     */     }
/*     */     
/*     */     public int getOffset() {
/* 110 */       return this.offset;
/*     */     }
/*     */     
/*     */     public BitSet getAcknowledged() {
/* 114 */       return this.acknowledged;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public byte getChecksum() {
/* 121 */       return this.checksum;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\LastSeenMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */