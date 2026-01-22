/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.color.Color;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ItemCustomModelData
/*     */ {
/*     */   private List<Float> floats;
/*     */   private List<Boolean> flags;
/*     */   private List<String> strings;
/*     */   private List<Color> colors;
/*     */   
/*     */   public ItemCustomModelData(List<Float> floats, List<Boolean> flags, List<String> strings, List<Color> colors) {
/*  49 */     this.floats = floats;
/*  50 */     this.flags = flags;
/*  51 */     this.strings = strings;
/*  52 */     this.colors = colors;
/*     */   }
/*     */   
/*     */   public ItemCustomModelData(int legacyId) {
/*  56 */     this.floats = new ArrayList<>(1);
/*  57 */     this.flags = new ArrayList<>(0);
/*  58 */     this.strings = new ArrayList<>(0);
/*  59 */     this.colors = new ArrayList<>(0);
/*  60 */     setLegacyId(legacyId);
/*     */   }
/*     */   
/*     */   public static ItemCustomModelData read(PacketWrapper<?> wrapper) {
/*  64 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_4)) {
/*  65 */       return new ItemCustomModelData(wrapper
/*  66 */           .readList(PacketWrapper::readFloat), wrapper
/*  67 */           .readList(PacketWrapper::readBoolean), wrapper
/*  68 */           .readList(PacketWrapper::readString), wrapper
/*  69 */           .readList(Color::read));
/*     */     }
/*  71 */     return new ItemCustomModelData(wrapper.readVarInt());
/*     */   }
/*     */ 
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, ItemCustomModelData data) {
/*  76 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_4)) {
/*  77 */       wrapper.writeList(data.floats, PacketWrapper::writeFloat);
/*  78 */       wrapper.writeList(data.flags, PacketWrapper::writeBoolean);
/*  79 */       wrapper.writeList(data.strings, PacketWrapper::writeString);
/*  80 */       wrapper.writeList(data.colors, Color::write);
/*     */     } else {
/*  82 */       wrapper.writeVarInt(data.getLegacyId());
/*     */     } 
/*     */   }
/*     */   
/*     */   public List<Float> getFloats() {
/*  87 */     return this.floats;
/*     */   }
/*     */   
/*     */   public void setFloats(List<Float> floats) {
/*  91 */     this.floats = floats;
/*     */   }
/*     */   
/*     */   public List<Boolean> getFlags() {
/*  95 */     return this.flags;
/*     */   }
/*     */   
/*     */   public void setFlags(List<Boolean> flags) {
/*  99 */     this.flags = flags;
/*     */   }
/*     */   
/*     */   public List<String> getStrings() {
/* 103 */     return this.strings;
/*     */   }
/*     */   
/*     */   public void setStrings(List<String> strings) {
/* 107 */     this.strings = strings;
/*     */   }
/*     */   
/*     */   public List<Color> getColors() {
/* 111 */     return this.colors;
/*     */   }
/*     */   
/*     */   public void setColors(List<Color> colors) {
/* 115 */     this.colors = colors;
/*     */   }
/*     */   
/*     */   @Obsolete
/*     */   public int getLegacyId() {
/* 120 */     if (!this.floats.isEmpty()) {
/* 121 */       return ((Float)this.floats.get(0)).intValue();
/*     */     }
/* 123 */     return 0;
/*     */   }
/*     */   
/*     */   @Obsolete
/*     */   public void setLegacyId(int legacyId) {
/* 128 */     if (this.flags.isEmpty()) {
/* 129 */       this.floats.add(Float.valueOf(legacyId));
/*     */     } else {
/* 131 */       this.floats.set(0, Float.valueOf(legacyId));
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemCustomModelData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */