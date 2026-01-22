/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.serializer;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTByte;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTByteArray;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTDouble;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTEnd;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTFloat;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTIntArray;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTLimiter;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTList;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTLong;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTLongArray;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTShort;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTType;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataOutput;
/*     */ import java.io.IOException;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultNBTSerializer
/*     */   extends NBTSerializer<DataInput, DataOutput>
/*     */ {
/*     */   @Internal
/*     */   public static final int OBJECT_HEADER_BYTES = 8;
/*     */   @Internal
/*     */   public static final int ARRAY_HEADER_BYTES = 12;
/*     */   @Internal
/*     */   public static final int OBJECT_REF_BYTES = 4;
/*     */   @Internal
/*     */   public static final int STRING_SIZE_BYTES = 28;
/*  56 */   public static final DefaultNBTSerializer INSTANCE = new DefaultNBTSerializer();
/*     */ 
/*     */   
/*     */   public DefaultNBTSerializer() {
/*  60 */     super((limiter, dataInput) -> dataInput.readByte(), DataOutput::writeByte, (limiter, dataInput) -> { dataInput.skipBytes(dataInput.readUnsignedShort()); return ""; }DataOutput::writeUTF);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  70 */     registerType(NBTType.END, 0, (limiter, stream) -> {
/*     */           limiter.increment(8); return NBTEnd.INSTANCE;
/*     */         }(stream, tag) -> {
/*     */         
/*     */         });
/*  75 */     registerType(NBTType.BYTE, 1, (limiter, stream) -> {
/*     */           limiter.increment(9);
/*     */           return new NBTByte(stream.readByte());
/*     */         }(stream, tag) -> stream.writeByte(tag.getAsByte()));
/*  79 */     registerType(NBTType.SHORT, 2, (limiter, stream) -> {
/*     */           limiter.increment(24);
/*     */           return new NBTShort(stream.readShort());
/*     */         }(stream, tag) -> stream.writeShort(tag.getAsShort()));
/*  83 */     registerType(NBTType.INT, 3, (limiter, stream) -> {
/*     */           limiter.increment(12);
/*     */           return new NBTInt(stream.readInt());
/*     */         }(stream, tag) -> stream.writeInt(tag.getAsInt()));
/*  87 */     registerType(NBTType.LONG, 4, (limiter, stream) -> {
/*     */           limiter.increment(16);
/*     */           return new NBTLong(stream.readLong());
/*     */         }(stream, tag) -> stream.writeLong(tag.getAsLong()));
/*  91 */     registerType(NBTType.FLOAT, 5, (limiter, stream) -> {
/*     */           limiter.increment(12);
/*     */           return new NBTFloat(stream.readFloat());
/*     */         }(stream, tag) -> stream.writeFloat(tag.getAsFloat()));
/*  95 */     registerType(NBTType.DOUBLE, 6, (limiter, stream) -> {
/*     */           limiter.increment(16);
/*     */           return new NBTDouble(stream.readDouble());
/*     */         }(stream, tag) -> stream.writeDouble(tag.getAsDouble()));
/*  99 */     registerType(NBTType.BYTE_ARRAY, 7, (limiter, stream) -> {
/*     */           limiter.increment(24);
/*     */           
/*     */           int length = stream.readInt();
/*     */           
/*     */           if (length >= 16777216) {
/*     */             throw new IllegalArgumentException("Byte array length is too large: " + length);
/*     */           }
/*     */           
/*     */           limiter.increment(1 * length);
/*     */           
/*     */           limiter.checkReadability(1 * length);
/*     */           
/*     */           byte[] array = new byte[length];
/*     */           stream.readFully(array);
/*     */           return new NBTByteArray(array);
/*     */         }(stream, tag) -> {
/*     */           byte[] array = tag.getValue();
/*     */           stream.writeInt(array.length);
/*     */           stream.write(array);
/*     */         });
/* 120 */     registerType(NBTType.STRING, 8, (limiter, stream) -> {
/*     */           limiter.increment(36);
/*     */           String string = stream.readUTF();
/*     */           limiter.increment(string.length() * 2);
/*     */           return new NBTString(string);
/*     */         }(stream, tag) -> stream.writeUTF(tag.getValue()));
/* 126 */     registerType(NBTType.LIST, 9, (limiter, stream) -> {
/*     */           limiter.enterDepth();
/*     */           
/*     */           try {
/*     */             limiter.increment(36);
/*     */             
/*     */             NBTType<? extends NBT> valueType = (NBTType)readTagType(limiter, stream);
/*     */             
/*     */             int size = stream.readInt();
/*     */             
/*     */             if (valueType == NBTType.END && size > 0) {
/*     */               throw new IllegalStateException("Missing nbt list values tag type");
/*     */             }
/*     */             
/*     */             limiter.increment(4 * size);
/*     */             NBTList<NBT> list = new NBTList(valueType, size);
/*     */             for (int i = 0; i < size; i++) {
/*     */               list.addTag(readTag(limiter, stream, valueType));
/*     */             }
/*     */             return list;
/*     */           } finally {
/*     */             limiter.exitDepth();
/*     */           } 
/*     */         }(stream, tag) -> {
/*     */           writeTagType(stream, tag.getTagsType());
/*     */           stream.writeInt(tag.size());
/*     */           for (NBT value : tag.getTags()) {
/*     */             writeTag(stream, value);
/*     */           }
/*     */         });
/* 156 */     registerType(NBTType.COMPOUND, 10, (limiter, stream) -> {
/*     */           limiter.enterDepth();
/*     */           
/*     */           try {
/*     */             limiter.increment(48);
/*     */             
/*     */             NBTCompound compound = new NBTCompound();
/*     */             
/*     */             NBTType<?> valueType;
/*     */             
/*     */             while ((valueType = readTagType(limiter, stream)) != NBTType.END) {
/*     */               String name = readString(limiter, stream);
/*     */               
/*     */               NBT tag = readTag(limiter, stream, valueType);
/*     */               if (!compound.getTags().containsKey(name)) {
/*     */                 limiter.increment(36);
/*     */               }
/*     */               compound.setTag(name, tag);
/*     */             } 
/*     */             return compound;
/*     */           } finally {
/*     */             limiter.exitDepth();
/*     */           } 
/*     */         }(stream, tag) -> {
/*     */           for (Map.Entry<String, NBT> entry : (Iterable<Map.Entry<String, NBT>>)tag.getTags().entrySet()) {
/*     */             NBT value = entry.getValue();
/*     */             writeTagType(stream, value.getType());
/*     */             writeTagName(stream, entry.getKey());
/*     */             writeTag(stream, value);
/*     */           } 
/*     */           writeTagType(stream, NBTType.END);
/*     */         });
/* 188 */     registerType(NBTType.INT_ARRAY, 11, (limiter, stream) -> {
/*     */           limiter.increment(24);
/*     */           
/*     */           int length = stream.readInt();
/*     */           
/*     */           if (length >= 16777216) {
/*     */             throw new IllegalArgumentException("Int array length is too large: " + length);
/*     */           }
/*     */           
/*     */           limiter.increment(length * 4);
/*     */           
/*     */           limiter.checkReadability(length * 4);
/*     */           
/*     */           int[] array = new int[length];
/*     */           for (int i = 0; i < array.length; i++) {
/*     */             array[i] = stream.readInt();
/*     */           }
/*     */           return new NBTIntArray(array);
/*     */         }(stream, tag) -> {
/*     */           int[] array = tag.getValue();
/*     */           stream.writeInt(array.length);
/*     */           for (int i : array) {
/*     */             stream.writeInt(i);
/*     */           }
/*     */         });
/* 213 */     registerType(NBTType.LONG_ARRAY, 12, (limiter, stream) -> {
/*     */           limiter.increment(24);
/*     */           int length = stream.readInt();
/*     */           if (length >= 16777216) {
/*     */             throw new IllegalArgumentException("Long array length is too large: " + length);
/*     */           }
/*     */           limiter.increment(length * 8);
/*     */           limiter.checkReadability(length * 8);
/*     */           long[] array = new long[length];
/*     */           for (int i = 0; i < array.length; i++) {
/*     */             array[i] = stream.readLong();
/*     */           }
/*     */           return new NBTLongArray(array);
/*     */         }(stream, tag) -> {
/*     */           long[] array = tag.getValue();
/*     */           stream.writeInt(array.length);
/*     */           for (long i : array) {
/*     */             stream.writeLong(i);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static String readString(NBTLimiter limiter, DataInput input) throws IOException {
/* 242 */     String string = input.readUTF();
/* 243 */     limiter.increment(28 + 2 * string.length());
/* 244 */     return string;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\nbt\serializer\DefaultNBTSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */