/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.codec;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufInputStream;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufOutputStream;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTByte;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTDouble;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTEnd;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTFloat;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTLimiter;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTList;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTLong;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTNumber;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTShort;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.serializer.DefaultNBTSerializer;
/*     */ import com.google.gson.JsonArray;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.google.gson.JsonPrimitive;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.zip.GZIPInputStream;
/*     */ import java.util.zip.GZIPOutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NBTCodec
/*     */ {
/*     */   @Deprecated
/*     */   public static NBT jsonToNBT(JsonElement element) {
/*  45 */     if (element instanceof JsonPrimitive) {
/*  46 */       if (((JsonPrimitive)element).isBoolean()) {
/*  47 */         return (NBT)new NBTByte(element.getAsBoolean());
/*     */       }
/*  49 */       if (((JsonPrimitive)element).isString()) {
/*  50 */         return (NBT)new NBTString(element.getAsString());
/*     */       }
/*  52 */       if (((JsonPrimitive)element).isNumber()) {
/*  53 */         Number num = element.getAsNumber();
/*  54 */         if (num instanceof Float) {
/*  55 */           return (NBT)new NBTFloat(num.floatValue());
/*     */         }
/*  57 */         if (num instanceof Double) {
/*  58 */           return (NBT)new NBTDouble(num.doubleValue());
/*     */         }
/*  60 */         if (num instanceof Byte) {
/*  61 */           return (NBT)new NBTByte(num.byteValue());
/*     */         }
/*  63 */         if (num instanceof Short) {
/*  64 */           return (NBT)new NBTShort(num.shortValue());
/*     */         }
/*  66 */         if (num instanceof Integer || num instanceof com.google.gson.internal.LazilyParsedNumber) {
/*  67 */           return (NBT)new NBTInt(num.intValue());
/*     */         }
/*  69 */         if (num instanceof Long) {
/*  70 */           return (NBT)new NBTLong(num.longValue());
/*     */         }
/*     */       } 
/*     */     } else {
/*     */       
/*  75 */       if (element instanceof JsonArray) {
/*  76 */         List<NBT> list = new ArrayList<>();
/*  77 */         for (JsonElement var : element) {
/*  78 */           list.add(jsonToNBT(var));
/*     */         }
/*  80 */         if (list.isEmpty()) {
/*  81 */           return (NBT)new NBTList(NBTType.COMPOUND);
/*     */         }
/*  83 */         NBTList<? extends NBT> l = new NBTList(((NBT)list.get(0)).getType());
/*  84 */         for (NBT nbt : list) {
/*  85 */           l.addTagUnsafe(nbt);
/*     */         }
/*  87 */         return (NBT)l;
/*     */       } 
/*     */       
/*  90 */       if (element instanceof JsonObject) {
/*  91 */         JsonObject obj = (JsonObject)element;
/*  92 */         NBTCompound compound = new NBTCompound();
/*  93 */         for (Map.Entry<String, JsonElement> jsonEntry : (Iterable<Map.Entry<String, JsonElement>>)obj.entrySet()) {
/*  94 */           compound.setTag(jsonEntry.getKey(), jsonToNBT(jsonEntry.getValue()));
/*     */         }
/*  96 */         return (NBT)compound;
/*     */       } 
/*  98 */       if (element instanceof com.google.gson.JsonNull || element == null)
/*  99 */         return (NBT)new NBTCompound(); 
/*     */     } 
/* 101 */     throw new IllegalStateException("Failed to convert JSON to NBT " + element.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static JsonElement nbtToJson(NBT nbt, boolean parseByteAsBool) {
/* 109 */     if (nbt instanceof NBTNumber) {
/* 110 */       if (nbt instanceof NBTByte && parseByteAsBool) {
/* 111 */         byte val = ((NBTByte)nbt).getAsByte();
/* 112 */         if (val == 0) {
/* 113 */           return (JsonElement)new JsonPrimitive(Boolean.valueOf(false));
/*     */         }
/* 115 */         if (val == 1) {
/* 116 */           return (JsonElement)new JsonPrimitive(Boolean.valueOf(true));
/*     */         }
/*     */       } 
/* 119 */       return (JsonElement)new JsonPrimitive(((NBTNumber)nbt).getAsNumber());
/*     */     } 
/* 121 */     if (nbt instanceof NBTString) {
/* 122 */       return (JsonElement)new JsonPrimitive(((NBTString)nbt).getValue());
/*     */     }
/* 124 */     if (nbt instanceof NBTList) {
/* 125 */       NBTList<? extends NBT> list = (NBTList<? extends NBT>)nbt;
/* 126 */       JsonArray jsonArray = new JsonArray();
/*     */       
/* 128 */       list.getTags().forEach(tag -> jsonArray.add(nbtToJson(tag, parseByteAsBool)));
/*     */ 
/*     */       
/* 131 */       return (JsonElement)jsonArray;
/*     */     } 
/* 133 */     if (nbt instanceof NBTEnd) {
/* 134 */       throw new IllegalStateException("Encountered the NBTEnd tag during the NBT to JSON conversion: " + nbt.toString());
/*     */     }
/* 136 */     if (nbt instanceof NBTCompound) {
/* 137 */       JsonObject jsonObject = new JsonObject();
/* 138 */       Map<String, NBT> compoundTags = ((NBTCompound)nbt).getTags();
/* 139 */       for (String tagName : compoundTags.keySet()) {
/* 140 */         NBT tag = compoundTags.get(tagName);
/* 141 */         JsonElement jsonValue = nbtToJson(tag, parseByteAsBool);
/* 142 */         jsonObject.add(tagName, jsonValue);
/*     */       } 
/* 144 */       return (JsonElement)jsonObject;
/*     */     } 
/*     */     
/* 147 */     throw new IllegalStateException("Failed to convert NBT to JSON.");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static NBT readNBTFromBuffer(Object byteBuf, ServerVersion serverVersion) {
/* 153 */     NBTLimiter limiter = NBTLimiter.forBuffer(byteBuf);
/* 154 */     return readNBTFromBuffer(byteBuf, serverVersion, limiter);
/*     */   }
/*     */   
/*     */   public static NBT readNBTFromBuffer(Object byteBuf, ServerVersion serverVersion, NBTLimiter limiter) {
/* 158 */     if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_8)) {
/*     */       try {
/* 160 */         boolean named = serverVersion.isOlderThan(ServerVersion.V_1_20_2);
/* 161 */         return DefaultNBTSerializer.INSTANCE.deserializeTag(limiter, new ByteBufInputStream(byteBuf), named);
/*     */       }
/* 163 */       catch (IOException ex) {
/* 164 */         throw new IllegalStateException(ex);
/*     */       } 
/*     */     }
/*     */     
/*     */     try {
/* 169 */       short length = ByteBufHelper.readShort(byteBuf);
/* 170 */       if (length < 0) {
/* 171 */         return null;
/*     */       }
/* 173 */       Object slicedBuffer = ByteBufHelper.readSlice(byteBuf, length);
/* 174 */       DataInputStream stream = new DataInputStream(new GZIPInputStream((InputStream)new ByteBufInputStream(slicedBuffer)));
/*     */       
/* 176 */       try { NBT nBT = DefaultNBTSerializer.INSTANCE.deserializeTag(limiter, stream);
/* 177 */         stream.close(); return nBT; } catch (Throwable throwable) { try { stream.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }
/*     */     
/* 179 */     } catch (IOException ex) {
/* 180 */       throw new IllegalStateException(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static void writeNBTToBuffer(Object byteBuf, ServerVersion serverVersion, NBTCompound tag) {
/* 186 */     writeNBTToBuffer(byteBuf, serverVersion, (NBT)tag);
/*     */   }
/*     */   
/*     */   public static void writeNBTToBuffer(Object byteBuf, ServerVersion serverVersion, NBT tag) {
/* 190 */     if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_8)) { 
/* 191 */       try { ByteBufOutputStream outputStream = new ByteBufOutputStream(byteBuf); 
/* 192 */         try { if (tag != null) {
/* 193 */             boolean named = serverVersion.isOlderThan(ServerVersion.V_1_20_2);
/* 194 */             DefaultNBTSerializer.INSTANCE.serializeTag(outputStream, tag, named);
/*     */           } else {
/* 196 */             DefaultNBTSerializer.INSTANCE.serializeTag(outputStream, (NBT)NBTEnd.INSTANCE);
/*     */           } 
/* 198 */           outputStream.close(); } catch (Throwable throwable) { try { outputStream.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  } catch (IOException e)
/* 199 */       { throw new IllegalStateException(e); }
/*     */       
/*     */        }
/*     */     
/* 203 */     else if (tag == null)
/* 204 */     { ByteBufHelper.writeShort(byteBuf, -1); }
/*     */     else
/* 206 */     { int lengthWriterIndex = ByteBufHelper.writerIndex(byteBuf);
/* 207 */       ByteBufHelper.writeShort(byteBuf, 0);
/* 208 */       int writerIndexDataStart = ByteBufHelper.writerIndex(byteBuf); 
/* 209 */       try { DataOutputStream outputstream = new DataOutputStream(new GZIPOutputStream((OutputStream)new ByteBufOutputStream(byteBuf))); 
/* 210 */         try { DefaultNBTSerializer.INSTANCE.serializeTag(outputstream, tag);
/* 211 */           outputstream.close(); } catch (Throwable throwable) { try { outputstream.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  } catch (Exception e)
/* 212 */       { throw new IllegalStateException(e); }
/*     */       
/* 214 */       int writerIndexDataEnd = ByteBufHelper.writerIndex(byteBuf);
/* 215 */       ByteBufHelper.writerIndex(byteBuf, lengthWriterIndex);
/* 216 */       ByteBufHelper.writeShort(byteBuf, writerIndexDataEnd - writerIndexDataStart);
/* 217 */       ByteBufHelper.writerIndex(byteBuf, writerIndexDataEnd); }
/*     */   
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\nbt\codec\NBTCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */