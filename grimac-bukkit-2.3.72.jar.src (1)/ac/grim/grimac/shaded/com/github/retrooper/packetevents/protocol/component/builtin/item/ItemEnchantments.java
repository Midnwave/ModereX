/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
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
/*     */ public class ItemEnchantments
/*     */   implements Iterable<Map.Entry<EnchantmentType, Integer>>
/*     */ {
/*  35 */   public static final ItemEnchantments EMPTY = new ItemEnchantments(
/*  36 */       Collections.emptyMap(), true)
/*     */     {
/*     */       public void setEnchantments(Map<EnchantmentType, Integer> enchantments) {
/*  39 */         throw new UnsupportedOperationException();
/*     */       }
/*     */ 
/*     */       
/*     */       public void setShowInTooltip(boolean showInTooltip) {
/*  44 */         throw new UnsupportedOperationException();
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   private Map<EnchantmentType, Integer> enchantments;
/*     */   
/*     */   @Obsolete
/*     */   private boolean showInTooltip;
/*     */ 
/*     */   
/*     */   public ItemEnchantments(Map<EnchantmentType, Integer> enchantments) {
/*  56 */     this(enchantments, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public ItemEnchantments(Map<EnchantmentType, Integer> enchantments, boolean showInTooltip) {
/*  64 */     this.enchantments = Collections.unmodifiableMap(enchantments);
/*  65 */     this.showInTooltip = showInTooltip;
/*     */   }
/*     */   
/*     */   public static ItemEnchantments read(PacketWrapper<?> wrapper) {
/*  69 */     Map<EnchantmentType, Integer> enchantments = wrapper.readMap(ew -> (EnchantmentType)wrapper.readMappedEntity((IRegistry)EnchantmentTypes.getRegistry()), PacketWrapper::readVarInt);
/*     */ 
/*     */ 
/*     */     
/*  73 */     boolean showInTooltip = (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5) || wrapper.readBoolean());
/*  74 */     return new ItemEnchantments(enchantments, showInTooltip);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, ItemEnchantments enchantments) {
/*  78 */     ClientVersion version = wrapper.getServerVersion().toClientVersion();
/*  79 */     wrapper.writeMap(enchantments.getEnchantments(), (ew, enchantment) -> ew.writeVarInt(enchantment.getId(version)), PacketWrapper::writeVarInt);
/*     */ 
/*     */ 
/*     */     
/*  83 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_21_5)) {
/*  84 */       wrapper.writeBoolean(enchantments.isShowInTooltip());
/*     */     }
/*     */   }
/*     */   
/*     */   public int getEnchantmentLevel(EnchantmentType enchantment) {
/*  89 */     return ((Integer)this.enchantments.getOrDefault(enchantment, Integer.valueOf(0))).intValue();
/*     */   }
/*     */   
/*     */   public void setEnchantmentLevel(EnchantmentType enchantment, int level) {
/*  93 */     if (level == 0) {
/*  94 */       this.enchantments.remove(enchantment);
/*     */     } else {
/*  96 */       this.enchantments.put(enchantment, Integer.valueOf(level));
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/* 101 */     return (getEnchantmentCount() < 1);
/*     */   }
/*     */   
/*     */   public int getEnchantmentCount() {
/* 105 */     return this.enchantments.size();
/*     */   }
/*     */   
/*     */   public Map<EnchantmentType, Integer> getEnchantments() {
/* 109 */     return this.enchantments;
/*     */   }
/*     */   
/*     */   public void setEnchantments(Map<EnchantmentType, Integer> enchantments) {
/* 113 */     this.enchantments = enchantments;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public boolean isShowInTooltip() {
/* 121 */     return this.showInTooltip;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public void setShowInTooltip(boolean showInTooltip) {
/* 129 */     this.showInTooltip = showInTooltip;
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Map.Entry<EnchantmentType, Integer>> iterator() {
/* 134 */     return this.enchantments.entrySet().iterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 139 */     if (this == obj) return true; 
/* 140 */     if (!(obj instanceof ItemEnchantments)) return false; 
/* 141 */     ItemEnchantments that = (ItemEnchantments)obj;
/* 142 */     if (this.showInTooltip != that.showInTooltip) return false; 
/* 143 */     return this.enchantments.equals(that.enchantments);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 148 */     return Objects.hash(new Object[] { this.enchantments, Boolean.valueOf(this.showInTooltip) });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 153 */     return "ItemEnchantments{enchantments=" + this.enchantments + ", showInTooltip=" + this.showInTooltip + '}';
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemEnchantments.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */