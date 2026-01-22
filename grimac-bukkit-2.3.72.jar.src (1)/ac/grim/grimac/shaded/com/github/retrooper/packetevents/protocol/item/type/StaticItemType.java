/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.StaticComponentMap;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.AbstractMappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.EnumMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class StaticItemType
/*     */   extends AbstractMappedEntity
/*     */   implements ItemType
/*     */ {
/*     */   private final int maxAmount;
/*     */   private final int maxDurability;
/*     */   private final ItemType craftRemainder;
/*     */   @Nullable
/*     */   private final StateType placedType;
/*     */   private final Set<ItemTypes.ItemAttribute> attributes;
/*     */   private final Map<ClientVersion, StaticComponentMap> components;
/*     */   
/*     */   @Internal
/*     */   public StaticItemType(@Nullable TypesBuilderData data, int maxAmount, int maxDurability, ItemType craftRemainder, @Nullable StateType placedType, Set<ItemTypes.ItemAttribute> attributes) {
/*  52 */     super(data);
/*  53 */     this.maxAmount = maxAmount;
/*  54 */     this.maxDurability = maxDurability;
/*  55 */     this.craftRemainder = craftRemainder;
/*  56 */     this.placedType = placedType;
/*  57 */     this.attributes = attributes;
/*  58 */     this.components = new EnumMap<>(ClientVersion.class);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxAmount() {
/*  63 */     return this.maxAmount;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxDurability() {
/*  68 */     return this.maxDurability;
/*     */   }
/*     */ 
/*     */   
/*     */   public ItemType getCraftRemainder() {
/*  73 */     return this.craftRemainder;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public StateType getPlacedType() {
/*  78 */     return this.placedType;
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<ItemTypes.ItemAttribute> getAttributes() {
/*  83 */     return this.attributes;
/*     */   }
/*     */ 
/*     */   
/*     */   public StaticComponentMap getComponents(ClientVersion version) {
/*  88 */     if (!version.isRelease()) {
/*  89 */       throw new IllegalArgumentException("Unsupported version for getting components of " + 
/*  90 */           getName() + ": " + version);
/*     */     }
/*  92 */     return this.components.getOrDefault(version, StaticComponentMap.SHARED_ITEM_COMPONENTS);
/*     */   }
/*     */   
/*     */   void setComponents(ClientVersion version, StaticComponentMap components) {
/*  96 */     if (this.components.containsKey(version))
/*  97 */       throw new IllegalStateException("Components are already defined for " + 
/*  98 */           getName() + " in version " + version); 
/*  99 */     if (!version.isRelease()) {
/* 100 */       throw new IllegalArgumentException("Unsupported version for setting components of " + 
/* 101 */           getName() + ": " + version);
/*     */     }
/* 103 */     this.components.put(version, components);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean hasComponents(ClientVersion version) {
/* 109 */     return this.components.containsKey(version);
/*     */   }
/*     */ 
/*     */   
/*     */   void fillComponents() {
/* 114 */     StaticComponentMap lastComponents = null;
/* 115 */     for (ClientVersion version : ClientVersion.values()) {
/* 116 */       if (version.isRelease()) {
/*     */ 
/*     */         
/* 119 */         StaticComponentMap components = this.components.get(version);
/* 120 */         if (components == null) {
/* 121 */           if (lastComponents != null) {
/* 122 */             this.components.put(version, lastComponents);
/*     */           }
/*     */         } else {
/*     */           
/* 126 */           if (lastComponents == null)
/*     */           {
/* 128 */             for (ClientVersion beforeVersion : ClientVersion.values()) {
/* 129 */               if (beforeVersion == version) {
/*     */                 break;
/*     */               }
/* 132 */               this.components.put(beforeVersion, components);
/*     */             } 
/*     */           }
/* 135 */           lastComponents = components;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\type\StaticItemType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */