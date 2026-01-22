/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemAttributeModifiers;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemEnchantments;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemRarity;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
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
/*     */ public class StaticComponentMap
/*     */   implements IComponentMap
/*     */ {
/*  36 */   public static final StaticComponentMap EMPTY = new StaticComponentMap(Collections.emptyMap());
/*     */   
/*     */   @Obsolete
/*  39 */   public static final StaticComponentMap SHARED_ITEM_COMPONENTS = builder()
/*  40 */     .<Integer>set(ComponentTypes.MAX_STACK_SIZE, Integer.valueOf(64))
/*  41 */     .<ItemLore>set(ComponentTypes.LORE, ItemLore.EMPTY)
/*  42 */     .<ItemEnchantments>set(ComponentTypes.ENCHANTMENTS, ItemEnchantments.EMPTY)
/*  43 */     .<Integer>set(ComponentTypes.REPAIR_COST, Integer.valueOf(0))
/*  44 */     .<ItemAttributeModifiers>set(ComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY)
/*  45 */     .<ItemRarity>set(ComponentTypes.RARITY, ItemRarity.COMMON)
/*  46 */     .build();
/*     */   
/*     */   private final boolean empty;
/*     */   private final Map<ComponentType<?>, ?> delegate;
/*     */   
/*     */   public StaticComponentMap(Map<ComponentType<?>, ?> delegate) {
/*  52 */     this.empty = delegate.isEmpty();
/*  53 */     this
/*  54 */       .delegate = this.empty ? Collections.emptyMap() : Collections.unmodifiableMap(new HashMap<>(delegate));
/*     */   }
/*     */   
/*     */   public static Builder builder() {
/*  58 */     return new Builder();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean has(ComponentType<?> type) {
/*  63 */     return (!this.empty && this.delegate.containsKey(type));
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public <T> T get(ComponentType<T> type) {
/*  69 */     return this.empty ? null : (T)this.delegate.get(type);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> void set(ComponentType<T> type, Optional<T> value) {
/*  74 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   public StaticComponentMap merge(StaticComponentMap prioritizedMap) {
/*  78 */     return builder().setAll(this).setAll(prioritizedMap).build();
/*     */   }
/*     */   
/*     */   public Map<ComponentType<?>, ?> getDelegate() {
/*  82 */     return this.delegate;
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/*  86 */     return this.empty;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  91 */     if (this == obj) return true; 
/*  92 */     if (!(obj instanceof StaticComponentMap)) return false; 
/*  93 */     StaticComponentMap that = (StaticComponentMap)obj;
/*  94 */     return this.delegate.equals(that.delegate);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  99 */     return Objects.hashCode(this.delegate);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 104 */     return "Components" + this.delegate;
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */   {
/* 109 */     private final Map<ComponentType<?>, Object> map = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public StaticComponentMap build() {
/* 115 */       return new StaticComponentMap(this.map);
/*     */     }
/*     */     
/*     */     public Builder setAll(Builder map) {
/* 119 */       return setAll(map.map);
/*     */     }
/*     */     
/*     */     public Builder setAll(StaticComponentMap map) {
/* 123 */       return setAll(map.getDelegate());
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder setAll(Map<ComponentType<?>, ?> map) {
/* 128 */       for (Map.Entry<ComponentType<?>, ?> entry : map.entrySet()) {
/* 129 */         set(entry.getKey(), entry.getValue());
/*     */       }
/* 131 */       return this;
/*     */     }
/*     */     
/*     */     public <T> Builder set(ComponentType<T> type, Optional<T> value) {
/* 135 */       return set(type, value.orElse(null));
/*     */     }
/*     */     
/*     */     public <T> Builder set(ComponentType<T> type, @Nullable T value) {
/* 139 */       if (value == null) {
/* 140 */         this.map.remove(type);
/*     */       } else {
/* 142 */         this.map.put(type, value);
/*     */       } 
/* 144 */       return this;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\StaticComponentMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */