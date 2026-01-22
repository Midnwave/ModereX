/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Keyed;
/*     */ import java.util.Objects;
/*     */ import org.jspecify.annotations.NullMarked;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @NullMarked
/*     */ public class ResourceLocation
/*     */   implements Keyed
/*     */ {
/*     */   public static final String VANILLA_NAMESPACE = "minecraft";
/*     */   protected final String namespace;
/*     */   protected final String key;
/*     */   
/*     */   public ResourceLocation(Key key) {
/*  42 */     this(key.namespace(), key.value());
/*     */   }
/*     */   
/*     */   public ResourceLocation(String namespace, String key) {
/*  46 */     this.namespace = namespace;
/*  47 */     this.key = key;
/*     */   }
/*     */   
/*     */   public ResourceLocation(String location) {
/*  51 */     String[] array = { "minecraft", location };
/*  52 */     int index = location.indexOf(":");
/*  53 */     if (index != -1) {
/*  54 */       array[1] = location.substring(index + 1);
/*  55 */       if (index >= 1) {
/*  56 */         array[0] = location.substring(0, index);
/*     */       }
/*     */     } 
/*  59 */     this.namespace = array[0];
/*  60 */     this.key = array[1];
/*     */   }
/*     */   
/*     */   public static ResourceLocation read(PacketWrapper<?> wrapper) {
/*  64 */     return wrapper.readIdentifier();
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, ResourceLocation resourceLocation) {
/*  68 */     wrapper.writeIdentifier(resourceLocation);
/*     */   }
/*     */   
/*     */   public static ResourceLocation decode(NBT nbt, PacketWrapper<?> wrapper) {
/*  72 */     return new ResourceLocation(((NBTString)nbt).getValue());
/*     */   }
/*     */   
/*     */   public static NBT encode(PacketWrapper<?> wrapper, ResourceLocation resourceLocation) {
/*  76 */     return (NBT)new NBTString(resourceLocation.toString());
/*     */   }
/*     */   
/*     */   public static String getNamespace(String location) {
/*  80 */     int namespaceIdx = location.indexOf(':');
/*  81 */     if (namespaceIdx > 0) {
/*  82 */       return location.substring(0, namespaceIdx);
/*     */     }
/*  84 */     return "minecraft";
/*     */   }
/*     */   
/*     */   public static String getPath(String location) {
/*  88 */     int namespaceIdx = location.indexOf(':');
/*  89 */     if (namespaceIdx != -1) {
/*  90 */       return location.substring(namespaceIdx + 1);
/*     */     }
/*  92 */     return location;
/*     */   }
/*     */   @Contract("null -> null; !null -> !null")
/*     */   @Nullable
/*     */   public static String normString(@Nullable String location) {
/*  97 */     if (location == null) {
/*  98 */       return null;
/*     */     }
/* 100 */     int index = location.indexOf(':');
/* 101 */     if (index > 0)
/* 102 */       return location; 
/* 103 */     if (index == -1)
/*     */     {
/* 105 */       return "minecraft:" + location;
/*     */     }
/*     */     
/* 108 */     return "minecraft" + location;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Key key() {
/* 114 */     return Key.key(this.namespace, this.key);
/*     */   }
/*     */   
/*     */   public String getNamespace() {
/* 118 */     return this.namespace;
/*     */   }
/*     */   
/*     */   public String getKey() {
/* 122 */     return this.key;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 127 */     return Objects.hash(new Object[] { this.namespace, this.key });
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 132 */     if (obj instanceof ResourceLocation) {
/* 133 */       ResourceLocation other = (ResourceLocation)obj;
/* 134 */       return (other.namespace.equals(this.namespace) && other.key.equals(this.key));
/*     */     } 
/* 136 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 141 */     return this.namespace + ":" + this.key;
/*     */   }
/*     */   
/*     */   public static ResourceLocation minecraft(String key) {
/* 145 */     return new ResourceLocation("minecraft", key);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\resources\ResourceLocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */