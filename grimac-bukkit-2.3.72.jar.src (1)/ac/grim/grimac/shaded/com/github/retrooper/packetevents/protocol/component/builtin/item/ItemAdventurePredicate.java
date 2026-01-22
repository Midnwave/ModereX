/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.predicates.ComponentMatchers;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntitySet;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ItemAdventurePredicate
/*     */ {
/*     */   private List<BlockPredicate> predicates;
/*     */   @Obsolete
/*     */   private boolean showInTooltip;
/*     */   
/*     */   public ItemAdventurePredicate(List<BlockPredicate> predicates) {
/*  45 */     this(predicates, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public ItemAdventurePredicate(List<BlockPredicate> predicates, boolean showInTooltip) {
/*  53 */     this.predicates = predicates;
/*  54 */     this.showInTooltip = showInTooltip;
/*     */   }
/*     */   
/*     */   public static ItemAdventurePredicate read(PacketWrapper<?> wrapper) {
/*  58 */     List<BlockPredicate> predicates = wrapper.readList(BlockPredicate::read);
/*  59 */     boolean showInTooltip = (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5) || wrapper.readBoolean());
/*  60 */     return new ItemAdventurePredicate(predicates, showInTooltip);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, ItemAdventurePredicate predicate) {
/*  64 */     wrapper.writeList(predicate.predicates, BlockPredicate::write);
/*  65 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_21_5)) {
/*  66 */       wrapper.writeBoolean(predicate.showInTooltip);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addPredicate(BlockPredicate predicate) {
/*  71 */     this.predicates.add(predicate);
/*     */   }
/*     */   
/*     */   public List<BlockPredicate> getPredicates() {
/*  75 */     return this.predicates;
/*     */   }
/*     */   
/*     */   public void setPredicates(List<BlockPredicate> predicates) {
/*  79 */     this.predicates = predicates;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public boolean isShowInTooltip() {
/*  87 */     return this.showInTooltip;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/*     */   public void setShowInTooltip(boolean showInTooltip) {
/*  95 */     this.showInTooltip = showInTooltip;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 100 */     if (this == obj) return true; 
/* 101 */     if (!(obj instanceof ItemAdventurePredicate)) return false; 
/* 102 */     ItemAdventurePredicate that = (ItemAdventurePredicate)obj;
/* 103 */     if (this.showInTooltip != that.showInTooltip) return false; 
/* 104 */     return this.predicates.equals(that.predicates);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 109 */     return Objects.hash(new Object[] { this.predicates, Boolean.valueOf(this.showInTooltip) });
/*     */   }
/*     */ 
/*     */   
/*     */   public static class BlockPredicate
/*     */   {
/*     */     @Nullable
/*     */     private MappedEntitySet<StateType.Mapped> blocks;
/*     */     
/*     */     @Nullable
/*     */     private List<ItemAdventurePredicate.PropertyMatcher> properties;
/*     */     
/*     */     @Nullable
/*     */     private NBTCompound nbt;
/*     */     
/*     */     private ComponentMatchers matchers;
/*     */     
/*     */     public BlockPredicate(@Nullable MappedEntitySet<StateType.Mapped> blocks, @Nullable List<ItemAdventurePredicate.PropertyMatcher> properties, @Nullable NBTCompound nbt) {
/* 127 */       this(blocks, properties, nbt, new ComponentMatchers());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public BlockPredicate(@Nullable MappedEntitySet<StateType.Mapped> blocks, @Nullable List<ItemAdventurePredicate.PropertyMatcher> properties, @Nullable NBTCompound nbt, ComponentMatchers matchers) {
/* 136 */       this.blocks = blocks;
/* 137 */       this.properties = properties;
/* 138 */       this.nbt = nbt;
/* 139 */       this.matchers = matchers;
/*     */     }
/*     */     
/*     */     public static BlockPredicate read(PacketWrapper<?> wrapper) {
/* 143 */       MappedEntitySet<StateType.Mapped> blocks = (MappedEntitySet<StateType.Mapped>)wrapper.readOptional(ew -> MappedEntitySet.read(ew, StateTypes::getMappedById));
/*     */       
/* 145 */       List<ItemAdventurePredicate.PropertyMatcher> properties = (List<ItemAdventurePredicate.PropertyMatcher>)wrapper.readOptional(ew -> wrapper.readList(ItemAdventurePredicate.PropertyMatcher::read));
/*     */       
/* 147 */       NBTCompound nbt = (NBTCompound)wrapper.readOptional(PacketWrapper::readNBT);
/*     */       
/* 149 */       ComponentMatchers matchers = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5) ? ComponentMatchers.read(wrapper) : new ComponentMatchers();
/* 150 */       return new BlockPredicate(blocks, properties, nbt, matchers);
/*     */     }
/*     */     
/*     */     public static void write(PacketWrapper<?> wrapper, BlockPredicate predicate) {
/* 154 */       wrapper.writeOptional(predicate.blocks, MappedEntitySet::write);
/* 155 */       wrapper.writeOptional(predicate.properties, (ew, val) -> ew.writeList(val, ItemAdventurePredicate.PropertyMatcher::write));
/*     */       
/* 157 */       wrapper.writeOptional(predicate.nbt, PacketWrapper::writeNBT);
/* 158 */       if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5))
/* 159 */         ComponentMatchers.write(wrapper, predicate.matchers); 
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     public MappedEntitySet<StateType.Mapped> getBlocks() {
/* 164 */       return this.blocks;
/*     */     }
/*     */     
/*     */     public void setBlocks(@Nullable MappedEntitySet<StateType.Mapped> blocks) {
/* 168 */       this.blocks = blocks;
/*     */     }
/*     */     
/*     */     public void addProperty(ItemAdventurePredicate.PropertyMatcher propertyMatcher) {
/* 172 */       if (this.properties == null) {
/* 173 */         this.properties = new ArrayList<>(4);
/*     */       }
/* 175 */       this.properties.add(propertyMatcher);
/*     */     }
/*     */     @Nullable
/*     */     public List<ItemAdventurePredicate.PropertyMatcher> getProperties() {
/* 179 */       return this.properties;
/*     */     }
/*     */     
/*     */     public void setProperties(@Nullable List<ItemAdventurePredicate.PropertyMatcher> properties) {
/* 183 */       this.properties = properties;
/*     */     }
/*     */     @Nullable
/*     */     public NBTCompound getNbt() {
/* 187 */       return this.nbt;
/*     */     }
/*     */     
/*     */     public void setNbt(@Nullable NBTCompound nbt) {
/* 191 */       this.nbt = nbt;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ComponentMatchers getMatchers() {
/* 198 */       return this.matchers;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void setMatchers(ComponentMatchers matchers) {
/* 205 */       this.matchers = matchers;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 210 */       if (this == obj) return true; 
/* 211 */       if (!(obj instanceof BlockPredicate)) return false; 
/* 212 */       BlockPredicate that = (BlockPredicate)obj;
/* 213 */       if (!Objects.equals(this.blocks, that.blocks)) return false; 
/* 214 */       if (!Objects.equals(this.properties, that.properties)) return false; 
/* 215 */       if (!Objects.equals(this.nbt, that.nbt)) return false; 
/* 216 */       return this.matchers.equals(that.matchers);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 221 */       return Objects.hash(new Object[] { this.blocks, this.properties, this.nbt, this.matchers });
/*     */     }
/*     */   }
/*     */   
/*     */   public static class PropertyMatcher
/*     */   {
/*     */     private String name;
/*     */     private ItemAdventurePredicate.ValueMatcher matcher;
/*     */     
/*     */     public PropertyMatcher(String name, ItemAdventurePredicate.ValueMatcher matcher) {
/* 231 */       this.name = name;
/* 232 */       this.matcher = matcher;
/*     */     }
/*     */     
/*     */     public static PropertyMatcher read(PacketWrapper<?> wrapper) {
/* 236 */       String name = wrapper.readString();
/* 237 */       ItemAdventurePredicate.ValueMatcher matcher = ItemAdventurePredicate.ValueMatcher.read(wrapper);
/* 238 */       return new PropertyMatcher(name, matcher);
/*     */     }
/*     */     
/*     */     public static void write(PacketWrapper<?> wrapper, PropertyMatcher matcher) {
/* 242 */       wrapper.writeString(matcher.name);
/* 243 */       ItemAdventurePredicate.ValueMatcher.write(wrapper, matcher.matcher);
/*     */     }
/*     */     
/*     */     public String getName() {
/* 247 */       return this.name;
/*     */     }
/*     */     
/*     */     public void setName(String name) {
/* 251 */       this.name = name;
/*     */     }
/*     */     
/*     */     public ItemAdventurePredicate.ValueMatcher getMatcher() {
/* 255 */       return this.matcher;
/*     */     }
/*     */     
/*     */     public void setMatcher(ItemAdventurePredicate.ValueMatcher matcher) {
/* 259 */       this.matcher = matcher;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 264 */       if (this == obj) return true; 
/* 265 */       if (!(obj instanceof PropertyMatcher)) return false; 
/* 266 */       PropertyMatcher that = (PropertyMatcher)obj;
/* 267 */       if (!this.name.equals(that.name)) return false; 
/* 268 */       return this.matcher.equals(that.matcher);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 273 */       return Objects.hash(new Object[] { this.name, this.matcher });
/*     */     }
/*     */   }
/*     */   
/*     */   public static interface ValueMatcher
/*     */   {
/*     */     static ValueMatcher read(PacketWrapper<?> wrapper) {
/* 280 */       if (wrapper.readBoolean()) {
/* 281 */         return ItemAdventurePredicate.ExactValueMatcher.read(wrapper);
/*     */       }
/* 283 */       return ItemAdventurePredicate.RangedValueMatcher.read(wrapper);
/*     */     }
/*     */     
/*     */     static void write(PacketWrapper<?> wrapper, ValueMatcher matcher) {
/* 287 */       if (matcher instanceof ItemAdventurePredicate.ExactValueMatcher) {
/* 288 */         wrapper.writeBoolean(true);
/* 289 */         ItemAdventurePredicate.ExactValueMatcher.write(wrapper, (ItemAdventurePredicate.ExactValueMatcher)matcher);
/* 290 */       } else if (matcher instanceof ItemAdventurePredicate.RangedValueMatcher) {
/* 291 */         wrapper.writeBoolean(false);
/* 292 */         ItemAdventurePredicate.RangedValueMatcher.write(wrapper, (ItemAdventurePredicate.RangedValueMatcher)matcher);
/*     */       } else {
/* 294 */         throw new IllegalArgumentException("Illegal matcher implementation: " + matcher);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public static class ExactValueMatcher
/*     */     implements ValueMatcher {
/*     */     private String value;
/*     */     
/*     */     public ExactValueMatcher(String value) {
/* 304 */       this.value = value;
/*     */     }
/*     */     
/*     */     public static ExactValueMatcher read(PacketWrapper<?> wrapper) {
/* 308 */       return new ExactValueMatcher(wrapper.readString());
/*     */     }
/*     */     
/*     */     public static void write(PacketWrapper<?> wrapper, ExactValueMatcher matcher) {
/* 312 */       wrapper.writeString(matcher.value);
/*     */     }
/*     */     
/*     */     public String getValue() {
/* 316 */       return this.value;
/*     */     }
/*     */     
/*     */     public void setValue(String value) {
/* 320 */       this.value = value;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 325 */       if (this == obj) return true; 
/* 326 */       if (!(obj instanceof ExactValueMatcher)) return false; 
/* 327 */       ExactValueMatcher that = (ExactValueMatcher)obj;
/* 328 */       return this.value.equals(that.value);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 333 */       return Objects.hashCode(this.value);
/*     */     } }
/*     */   
/*     */   public static class RangedValueMatcher implements ValueMatcher {
/*     */     @Nullable
/*     */     private String minValue;
/*     */     @Nullable
/*     */     private String maxValue;
/*     */     
/*     */     public RangedValueMatcher(@Nullable String minValue, @Nullable String maxValue) {
/* 343 */       this.minValue = minValue;
/* 344 */       this.maxValue = maxValue;
/*     */     }
/*     */     
/*     */     public static RangedValueMatcher read(PacketWrapper<?> wrapper) {
/* 348 */       String minValue = (String)wrapper.readOptional(PacketWrapper::readString);
/* 349 */       String maxValue = (String)wrapper.readOptional(PacketWrapper::readString);
/* 350 */       return new RangedValueMatcher(minValue, maxValue);
/*     */     }
/*     */     
/*     */     public static void write(PacketWrapper<?> wrapper, RangedValueMatcher matcher) {
/* 354 */       wrapper.writeOptional(matcher.minValue, PacketWrapper::writeString);
/* 355 */       wrapper.writeOptional(matcher.maxValue, PacketWrapper::writeString);
/*     */     }
/*     */     @Nullable
/*     */     public String getMinValue() {
/* 359 */       return this.minValue;
/*     */     }
/*     */     
/*     */     public void setMinValue(@Nullable String minValue) {
/* 363 */       this.minValue = minValue;
/*     */     }
/*     */     @Nullable
/*     */     public String getMaxValue() {
/* 367 */       return this.maxValue;
/*     */     }
/*     */     
/*     */     public void setMaxValue(@Nullable String maxValue) {
/* 371 */       this.maxValue = maxValue;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 376 */       if (this == obj) return true; 
/* 377 */       if (!(obj instanceof RangedValueMatcher)) return false; 
/* 378 */       RangedValueMatcher that = (RangedValueMatcher)obj;
/* 379 */       if (!Objects.equals(this.minValue, that.minValue)) return false; 
/* 380 */       return Objects.equals(this.maxValue, that.maxValue);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 385 */       return Objects.hash(new Object[] { this.minValue, this.maxValue });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemAdventurePredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */