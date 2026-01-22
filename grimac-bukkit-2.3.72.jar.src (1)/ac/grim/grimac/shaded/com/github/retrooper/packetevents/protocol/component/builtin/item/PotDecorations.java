/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
/*     */ import java.util.Queue;
/*     */ import java.util.function.IntFunction;
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
/*     */ public class PotDecorations
/*     */ {
/*     */   @Nullable
/*     */   private ItemType back;
/*     */   @Nullable
/*     */   private ItemType left;
/*     */   @Nullable
/*     */   private ItemType right;
/*     */   @Nullable
/*     */   private ItemType front;
/*     */   
/*     */   private PotDecorations(Queue<Optional<ItemType>> items) {
/*  41 */     this(
/*  42 */         items.isEmpty() ? null : ((Optional<ItemType>)items.remove()).orElse(null), 
/*  43 */         items.isEmpty() ? null : ((Optional<ItemType>)items.remove()).orElse(null), 
/*  44 */         items.isEmpty() ? null : ((Optional<ItemType>)items.remove()).orElse(null), 
/*  45 */         items.isEmpty() ? null : ((Optional<ItemType>)items.remove()).orElse(null));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PotDecorations(@Nullable ItemType back, @Nullable ItemType left, @Nullable ItemType right, @Nullable ItemType front) {
/*  55 */     this.back = back;
/*  56 */     this.left = left;
/*  57 */     this.right = right;
/*  58 */     this.front = front;
/*     */   }
/*     */   
/*     */   private List<Optional<ItemType>> asList() {
/*  62 */     return Arrays.asList((Optional<ItemType>[])new Optional[] {
/*  63 */           Optional.ofNullable(this.back), 
/*  64 */           Optional.ofNullable(this.left), 
/*  65 */           Optional.ofNullable(this.right), 
/*  66 */           Optional.ofNullable(this.front)
/*     */         });
/*     */   }
/*     */   
/*     */   private static Optional<ItemType> readItem(PacketWrapper<?> wrapper) {
/*  71 */     ItemType type = (ItemType)wrapper.readMappedEntity(ItemTypes::getById);
/*  72 */     return (type == ItemTypes.BRICK) ? Optional.<ItemType>empty() : Optional.<ItemType>of(type);
/*     */   }
/*     */ 
/*     */   
/*     */   public static PotDecorations read(PacketWrapper<?> wrapper) {
/*  77 */     Queue<Optional<ItemType>> items = (Queue<Optional<ItemType>>)wrapper.readCollection(java.util.ArrayDeque::new, PotDecorations::readItem);
/*  78 */     return new PotDecorations(items);
/*     */   }
/*     */   
/*     */   private static void writeItem(PacketWrapper<?> wrapper, Optional<ItemType> type) {
/*  82 */     wrapper.writeMappedEntity((MappedEntity)type.orElse(ItemTypes.BRICK));
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, PotDecorations decorations) {
/*  86 */     wrapper.writeList(decorations.asList(), PotDecorations::writeItem);
/*     */   }
/*     */   @Nullable
/*     */   public ItemType getBack() {
/*  90 */     return this.back;
/*     */   }
/*     */   
/*     */   public void setBack(@Nullable ItemType back) {
/*  94 */     this.back = back;
/*     */   }
/*     */   @Nullable
/*     */   public ItemType getLeft() {
/*  98 */     return this.left;
/*     */   }
/*     */   
/*     */   public void setLeft(@Nullable ItemType left) {
/* 102 */     this.left = left;
/*     */   }
/*     */   @Nullable
/*     */   public ItemType getRight() {
/* 106 */     return this.right;
/*     */   }
/*     */   
/*     */   public void setRight(@Nullable ItemType right) {
/* 110 */     this.right = right;
/*     */   }
/*     */   @Nullable
/*     */   public ItemType getFront() {
/* 114 */     return this.front;
/*     */   }
/*     */   
/*     */   public void setFront(@Nullable ItemType front) {
/* 118 */     this.front = front;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 123 */     if (this == obj) return true; 
/* 124 */     if (!(obj instanceof PotDecorations)) return false; 
/* 125 */     PotDecorations that = (PotDecorations)obj;
/* 126 */     if (!Objects.equals(this.back, that.back)) return false; 
/* 127 */     if (!Objects.equals(this.left, that.left)) return false; 
/* 128 */     if (!Objects.equals(this.right, that.right)) return false; 
/* 129 */     return Objects.equals(this.front, that.front);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 134 */     return Objects.hash(new Object[] { this.back, this.left, this.right, this.front });
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\PotDecorations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */