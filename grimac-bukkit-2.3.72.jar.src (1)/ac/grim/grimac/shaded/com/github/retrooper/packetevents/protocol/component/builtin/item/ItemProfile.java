/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
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
/*     */ public class ItemProfile
/*     */ {
/*     */   @Nullable
/*     */   private String name;
/*     */   @Nullable
/*     */   private UUID id;
/*     */   private List<Property> properties;
/*     */   
/*     */   public ItemProfile(@Nullable String name, @Nullable UUID id, List<Property> properties) {
/*  39 */     this.name = name;
/*  40 */     this.id = id;
/*  41 */     this.properties = properties;
/*     */   }
/*     */   
/*     */   public static ItemProfile read(PacketWrapper<?> wrapper) {
/*  45 */     String name = (String)wrapper.readOptional(ew -> ew.readString(16));
/*  46 */     UUID id = (UUID)wrapper.readOptional(PacketWrapper::readUUID);
/*  47 */     List<Property> properties = wrapper.readList(Property::read);
/*  48 */     return new ItemProfile(name, id, properties);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, ItemProfile profile) {
/*  52 */     wrapper.writeOptional(profile.name, (ew, name) -> ew.writeString(name, 16));
/*  53 */     wrapper.writeOptional(profile.id, PacketWrapper::writeUUID);
/*  54 */     wrapper.writeList(profile.properties, Property::write);
/*     */   }
/*     */   @Nullable
/*     */   public String getName() {
/*  58 */     return this.name;
/*     */   }
/*     */   
/*     */   public void setName(@Nullable String name) {
/*  62 */     this.name = name;
/*     */   }
/*     */   @Nullable
/*     */   public UUID getId() {
/*  66 */     return this.id;
/*     */   }
/*     */   
/*     */   public void setId(@Nullable UUID id) {
/*  70 */     this.id = id;
/*     */   }
/*     */   
/*     */   public void addProperty(Property property) {
/*  74 */     this.properties.add(property);
/*     */   }
/*     */   
/*     */   public List<Property> getProperties() {
/*  78 */     return this.properties;
/*     */   }
/*     */   
/*     */   public void setProperties(List<Property> properties) {
/*  82 */     this.properties = properties;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  87 */     if (this == obj) return true; 
/*  88 */     if (!(obj instanceof ItemProfile)) return false; 
/*  89 */     ItemProfile that = (ItemProfile)obj;
/*  90 */     if (!Objects.equals(this.name, that.name)) return false; 
/*  91 */     if (!Objects.equals(this.id, that.id)) return false; 
/*  92 */     return this.properties.equals(that.properties);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  97 */     return Objects.hash(new Object[] { this.name, this.id, this.properties });
/*     */   }
/*     */   
/*     */   public static class Property {
/*     */     private String name;
/*     */     private String value;
/*     */     @Nullable
/*     */     private String signature;
/*     */     
/*     */     public Property(String name, String value, @Nullable String signature) {
/* 107 */       this.name = name;
/* 108 */       this.value = value;
/* 109 */       this.signature = signature;
/*     */     }
/*     */     
/*     */     public static Property read(PacketWrapper<?> wrapper) {
/* 113 */       String name = wrapper.readString(64);
/* 114 */       String value = wrapper.readString(32767);
/* 115 */       String signature = (String)wrapper.readOptional(ew -> ew.readString(1024));
/* 116 */       return new Property(name, value, signature);
/*     */     }
/*     */     
/*     */     public static void write(PacketWrapper<?> wrapper, Property property) {
/* 120 */       wrapper.writeString(property.name, 64);
/* 121 */       wrapper.writeString(property.value, 32767);
/* 122 */       wrapper.writeOptional(property.signature, (ew, signature) -> ew.writeString(signature, 1024));
/*     */     }
/*     */ 
/*     */     
/*     */     public String getName() {
/* 127 */       return this.name;
/*     */     }
/*     */     
/*     */     public void setName(String name) {
/* 131 */       this.name = name;
/*     */     }
/*     */     
/*     */     public String getValue() {
/* 135 */       return this.value;
/*     */     }
/*     */     
/*     */     public void setValue(String value) {
/* 139 */       this.value = value;
/*     */     }
/*     */     @Nullable
/*     */     public String getSignature() {
/* 143 */       return this.signature;
/*     */     }
/*     */     
/*     */     public void setSignature(@Nullable String signature) {
/* 147 */       this.signature = signature;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 152 */       if (this == obj) return true; 
/* 153 */       if (!(obj instanceof Property)) return false; 
/* 154 */       Property property = (Property)obj;
/* 155 */       if (!this.name.equals(property.name)) return false; 
/* 156 */       if (!this.value.equals(property.value)) return false; 
/* 157 */       return Objects.equals(this.signature, property.signature);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 162 */       return Objects.hash(new Object[] { this.name, this.value, this.signature });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemProfile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */