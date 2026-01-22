/*     */ package ac.grim.grimac.shaded.kyori.option;
/*     */ 
/*     */ import ac.grim.grimac.shaded.kyori.option.value.ValueSource;
/*     */ import java.util.Collections;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TreeMap;
/*     */ import java.util.function.Consumer;
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
/*     */ final class OptionStateImpl
/*     */   implements OptionState
/*     */ {
/*     */   private final OptionSchema schema;
/*     */   private final IdentityHashMap<Option<?>, Object> values;
/*     */   
/*     */   OptionStateImpl(OptionSchema schema, IdentityHashMap<Option<?>, Object> values) {
/*  43 */     this.schema = schema;
/*  44 */     this.values = new IdentityHashMap<>(values);
/*     */   }
/*     */ 
/*     */   
/*     */   public OptionSchema schema() {
/*  49 */     return this.schema;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean has(Option<?> option) {
/*  54 */     return this.values.containsKey(Objects.requireNonNull(option, "flag"));
/*     */   }
/*     */ 
/*     */   
/*     */   public <V> V value(Option<V> option) {
/*  59 */     V value = option.valueType().type().cast(this.values.get(Objects.requireNonNull(option, "flag")));
/*  60 */     return (value == null) ? option.defaultValue() : value;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/*  65 */     if (this == other) return true; 
/*  66 */     if (other == null || getClass() != other.getClass()) return false; 
/*  67 */     OptionStateImpl that = (OptionStateImpl)other;
/*  68 */     return Objects.equals(this.values, that.values);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  73 */     return Objects.hash(new Object[] { this.values });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  78 */     return getClass().getSimpleName() + "{values=" + this.values + '}';
/*     */   }
/*     */   
/*     */   static final class VersionedImpl
/*     */     implements OptionState.Versioned
/*     */   {
/*     */     private final OptionSchema schema;
/*     */     private final SortedMap<Integer, OptionState> sets;
/*     */     private final int targetVersion;
/*     */     private final OptionState filtered;
/*     */     
/*     */     VersionedImpl(OptionSchema schema, SortedMap<Integer, OptionState> sets, int targetVersion, OptionState filtered) {
/*  90 */       this.schema = schema;
/*  91 */       this.sets = sets;
/*  92 */       this.targetVersion = targetVersion;
/*  93 */       this.filtered = filtered;
/*     */     }
/*     */ 
/*     */     
/*     */     public OptionSchema schema() {
/*  98 */       return this.schema;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean has(Option<?> option) {
/* 103 */       return this.filtered.has(option);
/*     */     }
/*     */ 
/*     */     
/*     */     public <V> V value(Option<V> option) {
/* 108 */       return this.filtered.value(option);
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<Integer, OptionState> childStates() {
/* 113 */       return Collections.unmodifiableSortedMap(this.sets.headMap(Integer.valueOf(this.targetVersion + 1)));
/*     */     }
/*     */ 
/*     */     
/*     */     public OptionState.Versioned at(int version) {
/* 118 */       return new VersionedImpl(this.schema, this.sets, version, flattened(this.schema, this.sets, version));
/*     */     }
/*     */     
/*     */     public static OptionState flattened(OptionSchema schema, SortedMap<Integer, OptionState> versions, int targetVersion) {
/* 122 */       Map<Integer, OptionState> applicable = versions.headMap(Integer.valueOf(targetVersion + 1));
/* 123 */       OptionState.Builder builder = schema.stateBuilder();
/* 124 */       for (OptionState child : applicable.values()) {
/* 125 */         builder.values(child);
/*     */       }
/*     */       
/* 128 */       return builder.build();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object other) {
/* 133 */       if (this == other) return true; 
/* 134 */       if (other == null || getClass() != other.getClass()) return false; 
/* 135 */       VersionedImpl that = (VersionedImpl)other;
/* 136 */       return (this.targetVersion == that.targetVersion && 
/* 137 */         Objects.equals(this.schema, that.schema) && 
/* 138 */         Objects.equals(this.sets, that.sets) && 
/* 139 */         Objects.equals(this.filtered, that.filtered));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 144 */       return Objects.hash(new Object[] { this.schema, this.sets, 
/*     */ 
/*     */             
/* 147 */             Integer.valueOf(this.targetVersion), this.filtered });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String toString() {
/* 154 */       return getClass().getSimpleName() + "{schema=" + this.schema + ", sets=" + this.sets + ", targetVersion=" + this.targetVersion + ", filtered=" + this.filtered + '}';
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static final class BuilderImpl
/*     */     implements OptionState.Builder
/*     */   {
/*     */     private final OptionSchema schema;
/*     */     
/* 165 */     private final IdentityHashMap<Option<?>, Object> values = new IdentityHashMap<>();
/*     */     
/*     */     BuilderImpl(OptionSchema schema) {
/* 168 */       this.schema = schema;
/*     */     }
/*     */ 
/*     */     
/*     */     public OptionState build() {
/* 173 */       if (this.values.isEmpty()) return this.schema.emptyState();
/*     */       
/* 175 */       return new OptionStateImpl(this.schema, this.values);
/*     */     }
/*     */ 
/*     */     
/*     */     public <V> OptionState.Builder value(Option<V> option, V value) {
/* 180 */       if (!this.schema.has(Objects.<Option>requireNonNull(option, "option"))) {
/* 181 */         throw new IllegalStateException("Option '" + option.id() + "' was not present in active schema");
/*     */       }
/*     */       
/* 184 */       if (value == null) {
/* 185 */         this.values.remove(option);
/*     */       } else {
/* 187 */         this.values.put(option, value);
/*     */       } 
/* 189 */       return this;
/*     */     }
/*     */     
/*     */     private void putAll(Map<Option<?>, Object> values) {
/* 193 */       for (Map.Entry<Option<?>, Object> entry : values.entrySet()) {
/* 194 */         if (!this.schema.has(entry.getKey())) {
/* 195 */           throw new IllegalStateException("Option '" + ((Option)entry.getKey()).id() + "' was not present in active schema");
/*     */         }
/*     */         
/* 198 */         this.values.put(entry.getKey(), entry.getValue());
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public OptionState.Builder values(OptionState existing) {
/* 204 */       if (existing instanceof OptionStateImpl) {
/* 205 */         putAll(((OptionStateImpl)existing).values);
/* 206 */       } else if (existing instanceof OptionStateImpl.VersionedImpl) {
/* 207 */         putAll(((OptionStateImpl)((OptionStateImpl.VersionedImpl)existing).filtered).values);
/*     */       } else {
/* 209 */         throw new IllegalArgumentException("existing set " + existing + " is of an unknown implementation type");
/*     */       } 
/* 211 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public OptionState.Builder values(ValueSource source) {
/* 216 */       for (Option<?> opt : this.schema.knownOptions()) {
/* 217 */         Object value = source.value(opt);
/* 218 */         if (value != null) {
/* 219 */           this.values.put(opt, value);
/*     */         }
/*     */       } 
/*     */       
/* 223 */       return this;
/*     */     }
/*     */   }
/*     */   
/*     */   static final class VersionedBuilderImpl implements OptionState.VersionedBuilder {
/*     */     private final OptionSchema schema;
/* 229 */     private final Map<Integer, OptionStateImpl.BuilderImpl> builders = new TreeMap<>();
/*     */     
/*     */     VersionedBuilderImpl(OptionSchema schema) {
/* 232 */       this.schema = schema;
/*     */     }
/*     */ 
/*     */     
/*     */     public OptionState.Versioned build() {
/* 237 */       if (this.builders.isEmpty()) {
/* 238 */         return new OptionStateImpl.VersionedImpl(this.schema, Collections.emptySortedMap(), 0, this.schema.emptyState());
/*     */       }
/*     */       
/* 241 */       SortedMap<Integer, OptionState> built = new TreeMap<>();
/* 242 */       for (Map.Entry<Integer, OptionStateImpl.BuilderImpl> entry : this.builders.entrySet()) {
/* 243 */         built.put(entry.getKey(), ((OptionStateImpl.BuilderImpl)entry.getValue()).build());
/*     */       }
/*     */       
/* 246 */       return new OptionStateImpl.VersionedImpl(this.schema, built, ((Integer)built.lastKey()).intValue(), OptionStateImpl.VersionedImpl.flattened(this.schema, built, ((Integer)built.lastKey()).intValue()));
/*     */     }
/*     */ 
/*     */     
/*     */     public OptionState.VersionedBuilder version(int version, Consumer<OptionState.Builder> versionBuilder) {
/* 251 */       ((Consumer<OptionState.Builder>)Objects.<Consumer<OptionState.Builder>>requireNonNull(versionBuilder, "versionBuilder"))
/* 252 */         .accept(this.builders.computeIfAbsent(Integer.valueOf(version), $ -> new OptionStateImpl.BuilderImpl(this.schema)));
/* 253 */       return this;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\option\OptionStateImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */